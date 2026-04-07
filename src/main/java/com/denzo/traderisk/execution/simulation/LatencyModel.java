package com.denzo.traderisk.execution.simulation;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class LatencyModel {

    public void simulateLatency() {
        int latency = ThreadLocalRandom.current().nextInt(20, 120);
        try {
            Thread.sleep(latency);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
