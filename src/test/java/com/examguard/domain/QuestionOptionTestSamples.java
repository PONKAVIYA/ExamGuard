package com.examguard.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class QuestionOptionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static QuestionOption getQuestionOptionSample1() {
        return new QuestionOption().id(1L).text("text1").orderIndex(1);
    }

    public static QuestionOption getQuestionOptionSample2() {
        return new QuestionOption().id(2L).text("text2").orderIndex(2);
    }

    public static QuestionOption getQuestionOptionRandomSampleGenerator() {
        return new QuestionOption()
            .id(longCount.incrementAndGet())
            .text(UUID.randomUUID().toString())
            .orderIndex(intCount.incrementAndGet());
    }
}
