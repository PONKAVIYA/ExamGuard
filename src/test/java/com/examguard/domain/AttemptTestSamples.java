package com.examguard.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AttemptTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static Attempt getAttemptSample1() {
        return new Attempt().id(1L).score(1);
    }

    public static Attempt getAttemptSample2() {
        return new Attempt().id(2L).score(2);
    }

    public static Attempt getAttemptRandomSampleGenerator() {
        return new Attempt().id(longCount.incrementAndGet()).score(intCount.incrementAndGet());
    }
}
