package com.productdock.exception;

public class SpeechToTextServiceException extends RuntimeException {

    /**
     * Constructs a new SpeechToTextServiceException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public SpeechToTextServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
