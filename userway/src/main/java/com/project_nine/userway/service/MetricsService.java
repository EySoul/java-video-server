package com.project_nine.userway.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.time.Duration;
import org.springframework.stereotype.Service;

/**
 * MetricsService
 */
@Service
public class MetricsService {

    private final Counter requetVideosCounter;
    private final Timer requetVideosTimer;

    public MetricsService(MeterRegistry registry) {
        this.requetVideosCounter = Counter.builder(
            "proxy.video-get.request.count"
        )
            .description(
                "Total number of get requests to video-service via proxy"
            )
            .register(registry);
        this.requetVideosTimer = Timer.builder(
            "proxy.video-get.request.latency"
        )
            .description("Latency get to video-service proxy calls")
            .register(registry);
    }

    public void recordRequest(Duration duration) {
        requetVideosCounter.increment();
        requetVideosTimer.record(duration);
    }
}
