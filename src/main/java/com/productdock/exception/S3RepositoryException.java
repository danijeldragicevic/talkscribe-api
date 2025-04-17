package com.productdock.exception;

public class S3RepositoryException extends Exception {

    /**
     * Constructs a new S3RepositoryException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public S3RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
