package com.productdock.exception;

/**
 * Custom exception for handling errors in the text-to-speech repository.
 */
public class PollyRepositoryException extends RuntimeException {

    /**
     * Constructs a new PollyRepositoryException with the specified detail message and cause.
     *
     *  @param message the detail message
     *  @param cause the cause of the exception
     */
    public PollyRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
