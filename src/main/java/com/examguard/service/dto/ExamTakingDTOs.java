package com.examguard.service.dto;

import java.time.Instant;
import java.util.List;

/**
 * These are plain data-carrier classes (Java "records") — not JPA entities.
 * They exist so we NEVER accidentally send correctOptionIndex to a student
 * before they submit. The entity has that field; these response shapes
 * simply don't include it.
 */
public class ExamTakingDTOs {

    // ---- what a student sees when starting an exam ----
    public record QuestionForStudent(Long questionId, String text, List<OptionForStudent> options) {}
    public record OptionForStudent(Long optionId, String text, int orderIndex) {}

    public record StartAttemptResponse(Long attemptId, Instant endedAt, List<QuestionForStudent> questions) {}

    // ---- saving one answer ----
    public record SaveAnswerRequest(Long questionId, int selectedOptionIndex) {}
    public record SaveAnswerResponse(boolean saved, Instant savedAt) {}

    // ---- submitting ----
    public record SubmitResponse(int score, int total) {}

    // ---- review after submission (safe to show correct answers now) ----
    public record ReviewItem(
        String text,
        List<String> options,
        int correctOptionIndex,
        Integer selectedOptionIndex
    ) {}

    // ---- proctoring ----
    public record ProctorEventRequest(String type) {} // e.g. "TAB_SWITCH"
    public record ProctorEventResponse(boolean recorded) {}

    // ---- teacher live dashboard ----
    public record LiveStudentStatus(
        String studentName,
        String status,          // "IN_PROGRESS" or "SUBMITTED"
        int answeredCount,
        int totalQuestions,
        int tabSwitchCount,
        Integer score
    ) {}
}