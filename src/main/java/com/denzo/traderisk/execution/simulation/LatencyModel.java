package com.denzo.traderisk.execution.simulation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class LatencyModel {

    @Value("${execution.latency.min:20}")
    private int minLatency;

    @Value("${execution.latency.max:120}")
    private int maxLatency;

    public void simulateLatency() {
        int latency = ThreadLocalRandom.current().nextInt(minLatency, maxLatency + 1);
        try {
            Thread.sleep(latency);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
