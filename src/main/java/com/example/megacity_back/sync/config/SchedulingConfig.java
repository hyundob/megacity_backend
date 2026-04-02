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
}
