package com.productdock.exception;

/**
 * Custom exception for handling errors in the text-to-speech service.
 */
public class TextToSpeechServiceException extends RuntimeException {

    /**
     * Constructs a new TextToSpeechServiceException with the specified detail message and cause.
     *
     *  @param message the detail message
     *  @param cause the cause of the exception
     */
    public TextToSpeechServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
