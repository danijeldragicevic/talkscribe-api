package com.productdock.exception;

/**
 * Custom exception for handling errors in the text-to-speech repository.
 */
public class TextToSpeechRepositoryException extends RuntimeException {

    /**
     * Constructs a new TextToSpeechRepositoryException with the specified detail message and cause.
     *
     *  @param message the detail message
     *  @param cause the cause of the exception
     */
    public TextToSpeechRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
