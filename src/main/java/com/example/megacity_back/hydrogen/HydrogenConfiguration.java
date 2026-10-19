package com.example.megacity_back.hydrogen;

import org.springframework.context.annotation.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import java.time.Clock;

@Configuration
public class HydrogenConfiguration {
    @Bean("hydrogenClock") public Clock hydrogenClock() { return Clock.system(HydrogenTime.ZONE); }
    @Bean("hydrogenTaskScheduler") public ThreadPoolTaskScheduler hydrogenScheduler() {
        var scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("hydrogen-");
        return scheduler;
    }
}
