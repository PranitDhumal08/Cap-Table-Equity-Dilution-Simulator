package com.captablex.exception;

/**
 * Exception thrown when a requested domain entity (Company, Stakeholder) cannot be found.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
