package com.captablex.exception;

/**
 * Exception thrown when business rules or relational constraints are violated.
 */
public class BusinessValidationException extends RuntimeException {
    public BusinessValidationException(String message) {
        super(message);
    }
}
