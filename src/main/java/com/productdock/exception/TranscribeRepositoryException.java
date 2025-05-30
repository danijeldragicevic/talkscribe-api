package com.productdock.exception;

public class TranscribeRepositoryException extends RuntimeException {

    /**
     * Constructs a new TranscribeRepositoryException with the specified detail message.
     *
     * @param message the detail message
     */
    public TranscribeRepositoryException(String message) {
        super(message);
    }

    /**
     * Constructs a new TranscribeRepositoryException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public TranscribeRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
