package com.productdock.exception;

/**
 * Custom exception for handling errors in the Comprehend repository.
 */
public class ComprehendRepositoryException extends Exception {

    /**
     * Constructs a new ComprehendRepositoryException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public ComprehendRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
