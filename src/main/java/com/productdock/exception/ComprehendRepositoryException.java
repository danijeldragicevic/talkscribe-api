package com.productdock.exception;

public class ComprehendRepositoryException extends RuntimeException {

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
