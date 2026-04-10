package com.example.megacity_back.modbus.server;

import com.example.megacity_back.modbus.config.ModbusProperties;
import com.example.megacity_back.modbus.service.ModbusRegisterSnapshotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "modbus", name = "enabled", havingValue = "true")
public class ModbusTcpServer implements SmartLifecycle {

    private final ModbusProperties properties;
    private final ModbusTcpRequestHandler requestHandler;
    private final ModbusRegisterSnapshotService snapshotService;

    private final AtomicBoolean running = new AtomicBoolean(false);

    private ExecutorService acceptExecutor;
    private ExecutorService clientExecutor;
    private ServerSocket serverSocket;

    public ModbusTcpServer(
            ModbusProperties properties,
            ModbusTcpRequestHandler requestHandler,
            ModbusRegisterSnapshotService snapshotService
    ) {
        this.properties = properties;
        this.requestHandler = requestHandler;
        this.snapshotService = snapshotService;
    }

    @Override
    public synchronized void start() {
        if (running.get()) {
            return;
        }

        try {
            serverSocket = new ServerSocket(properties.getPort(), 50, InetAddress.getByName(properties.getHost()));
            acceptExecutor = Executors.newSingleThreadExecutor();
            clientExecutor = Executors.newCachedThreadPool();
            running.set(true);

            acceptExecutor.submit(this::acceptLoop);
            log.info("[Modbus] TCP slave started on {}:{}", properties.getHost(), properties.getPort());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to start Modbus TCP server", e);
        }
    }

    private void acceptLoop() {
        while (running.get()) {
            try {
                Socket client = serverSocket.accept();
                clientExecutor.submit(() -> handleClient(client));
            } catch (SocketException closedWhileStopping) {
                if (running.get()) {
                    log.warn("[Modbus] Accept loop interrupted: {}", closedWhileStopping.getMessage());
                }
            } catch (IOException e) {
                if (running.get()) {
                    log.error("[Modbus] Failed to accept client connection", e);
                }
            }
        }
    }

    private void handleClient(Socket client) {
        try (Socket socket = client;
             InputStream input = socket.getInputStream();
             OutputStream output = socket.getOutputStream()) {

            socket.setSoTimeout(properties.getSocketTimeoutMs());
            log.debug("[Modbus] Client connected: {}", socket.getRemoteSocketAddress());

            while (running.get() && !socket.isClosed()) {
                byte[] header = input.readNBytes(7);
                if (header.length == 0) {
                    return;
                }
                if (header.length < 7) {
                    log.debug("[Modbus] Incomplete MBAP header from {}", socket.getRemoteSocketAddress());
                    return;
                }

                int remainingLength = ((header[4] & 0xFF) << 8) | (header[5] & 0xFF);
                if (remainingLength < 2) {
                    log.debug("[Modbus] Invalid length field from {}", socket.getRemoteSocketAddress());
                    return;
                }

                byte[] pdu = input.readNBytes(remainingLength - 1);
                if (pdu.length < remainingLength - 1) {
                    log.debug("[Modbus] Incomplete PDU from {}", socket.getRemoteSocketAddress());
                    return;
                }

                byte[] requestAdu = new byte[header.length + pdu.length];
                System.arraycopy(header, 0, requestAdu, 0, header.length);
                System.arraycopy(pdu, 0, requestAdu, header.length, pdu.length);

                byte[] response = requestHandler.handle(requestAdu, snapshotService.currentSnapshot());
                if (response != null) {
                    output.write(response);
                    output.flush();
                }
            }
        } catch (SocketException e) {
            log.debug("[Modbus] Client disconnected: {}", e.getMessage());
        } catch (IOException e) {
            log.warn("[Modbus] Client I/O error: {}", e.getMessage());
        }
    }

    @Override
    public synchronized void stop() {
        if (!running.getAndSet(false)) {
            return;
        }

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            log.debug("[Modbus] Error while closing server socket", e);
        }

        if (acceptExecutor != null) {
            acceptExecutor.shutdownNow();
        }
        if (clientExecutor != null) {
            clientExecutor.shutdownNow();
        }

        log.info("[Modbus] TCP slave stopped");
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
