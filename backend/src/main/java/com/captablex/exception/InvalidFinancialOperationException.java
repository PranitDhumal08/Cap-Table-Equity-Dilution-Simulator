package com.captablex.exception;

/**
 * Exception thrown when a financial calculation rule is violated,
 * such as non-positive valuation, non-positive investment, or empty share capitalization.
 */
public class InvalidFinancialOperationException extends RuntimeException {
    public InvalidFinancialOperationException(String message) {
        super(message);
    }
}
