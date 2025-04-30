package com.productdock.exception;

public class TooManyRequestsException extends RuntimeException {

    /**
     * Constructs a new TooManyRequestsException with the specified detail message.
     *
     * @param message the detail message
     */
    public TooManyRequestsException(String message) {
        super(message);
    }
}
