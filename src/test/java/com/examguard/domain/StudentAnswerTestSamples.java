package com.examguard.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class StudentAnswerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static StudentAnswer getStudentAnswerSample1() {
        return new StudentAnswer().id(1L).selectedOptionIndex(1);
    }

    public static StudentAnswer getStudentAnswerSample2() {
        return new StudentAnswer().id(2L).selectedOptionIndex(2);
    }

    public static StudentAnswer getStudentAnswerRandomSampleGenerator() {
        return new StudentAnswer().id(longCount.incrementAndGet()).selectedOptionIndex(intCount.incrementAndGet());
    }
}
