package com.examguard.web.rest.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when an answer or submit request arrives after Attempt.endedAt.
 * Maps to HTTP 410 Gone — "the thing you're trying to act on no longer
 * accepts changes," which is the correct semantic for a closed exam.
 */
@ResponseStatus(HttpStatus.GONE)
public class ExamExpiredException extends RuntimeException {
    public ExamExpiredException(String message) {
        super(message);
    }
}