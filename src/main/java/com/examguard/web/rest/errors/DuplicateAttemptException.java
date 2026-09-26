package com.examguard.web.rest.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Syllabus link (Unit I): a user-defined exception.
 *
 * Thrown when a student tries to start an exam they have already attempted.
 * @ResponseStatus tells Spring: "if this exception escapes a controller
 * method uncaught, turn it into an HTTP 409 Conflict automatically" —
 * no separate error-handling class needed for this one.
 *
 * Viva line: "I extend RuntimeException (unchecked) because this is a
 * business-rule violation, not a recoverable I/O error, so I don't want
 * to force every calling method to declare 'throws'."
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateAttemptException extends RuntimeException {
    public DuplicateAttemptException(String message) {
        super(message);
    }
}