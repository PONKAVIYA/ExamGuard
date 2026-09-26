package com.examguard.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProctorEventTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static ProctorEvent getProctorEventSample1() {
        return new ProctorEvent().id(1L).eventType("eventType1");
    }

    public static ProctorEvent getProctorEventSample2() {
        return new ProctorEvent().id(2L).eventType("eventType2");
    }

    public static ProctorEvent getProctorEventRandomSampleGenerator() {
        return new ProctorEvent().id(longCount.incrementAndGet()).eventType(UUID.randomUUID().toString());
    }
}
