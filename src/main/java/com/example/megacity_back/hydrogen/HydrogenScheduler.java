package com.example.megacity_back.hydrogen;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.dao.DataIntegrityViolationException;
import lombok.extern.slf4j.Slf4j;
@Component @Slf4j
@ConditionalOnProperty(name="hydrogen.scheduling-enabled",havingValue="true",matchIfMissing=true)
public class HydrogenScheduler {
    private final HydrogenContinuousService service;
    public HydrogenScheduler(HydrogenContinuousService service) {this.service=service;}
    @EventListener(ApplicationReadyEvent.class) public void startup() { tick(); }
    // Every minute retries failures; only completed five-minute intervals are persisted.
    @Scheduled(cron="0 * * * * *",zone="Asia/Seoul",scheduler="hydrogenTaskScheduler")
    public void tick() {
        try {
            try {service.initialize();} catch(DataIntegrityViolationException concurrentInitialization) {
                log.debug("다른 인스턴스에서 수소 상태 초기화 완료",concurrentInitialization);
            }
            service.tick();
        } catch(RuntimeException e) {log.error("수소 5분별 자동 저장 실패. 다음 주기에 재시도합니다.",e);}
    }
}
