package com.example.megacity_back.sync.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Scheduled 활성화.
 * 기본 단일 스레드 스케줄러를 사용하므로 동일 테이블의 싱크 잡은 중첩 실행되지 않는다.
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
    @org.springframework.context.annotation.Bean("syncScheduler")
    public org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler syncScheduler() {
        return scheduler("sync-");
    }

    @org.springframework.context.annotation.Bean("modbusScheduler")
    public org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler modbusScheduler() {
        return scheduler("modbus-refresh-");
    }

    private org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler scheduler(String prefix) {
        var scheduler = new org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix(prefix);
        scheduler.setWaitForTasksToCompleteOnShutdown(false);
        return scheduler;
    }
}
