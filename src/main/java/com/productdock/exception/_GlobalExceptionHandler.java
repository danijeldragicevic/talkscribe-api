package com.productdock.exception;

import com.productdock.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class _GlobalExceptionHandler {

    /**
     * Handles MaxUploadSizeExceededException and returns a 413 Payload Too Large.
     *
     * @param exception the exception thrown
     * @param request the request during which the exception was thrown
     * @return ResponseEntity with error details and HTTP status code
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException exception, WebRequest request) {
        log.error("Handling MaxUploadSizeExceededException: {}", exception.getMessage());
        return buildErrorResponse(exception.getMessage(), HttpStatus.PAYLOAD_TOO_LARGE, request);
    }

    /**
     * Handles TooManyRequestsException and returns a 429 Too Many Requests response.
     *
     * @param exception the exception thrown
     * @param request the request during which the exception was thrown
     * @return ResponseEntity with error details and HTTP status code
     */
    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ErrorResponse> handleTooManyRequestsException(TooManyRequestsException exception, WebRequest request) {
        log.error("Handling TooManyRequestsException: {}", exception.getMessage());
        return buildErrorResponse(exception.getMessage(), HttpStatus.TOO_MANY_REQUESTS, request);
    }

    /**
     * Handles TextToSpeechServiceException and returns a 503 Service Unavailable response.
     *
     * @param exception the exception thrown
     * @param request the request during which the exception was thrown
     * @return ResponseEntity with error details and HTTP status code
     */
    @ExceptionHandler(TextToSpeechServiceException.class)
    public ResponseEntity<ErrorResponse> handleTextToSpeechServiceException(TextToSpeechServiceException exception, WebRequest request) {
        log.error("Handling TextToSpeechServiceException: {}", exception.getMessage());
        return buildErrorResponse(exception.getMessage(), HttpStatus.SERVICE_UNAVAILABLE, request);
    }

    /**
     * Handles ComprehendRepositoryException and returns a 503 Service Unavailable response.
     *
     * @param exception the exception thrown
     * @param request the request during which the exception was thrown
     * @return ResponseEntity with error details and HTTP status code
     */
    @ExceptionHandler(SpeechToTextServiceException.class)
    public ResponseEntity<ErrorResponse> handleSpeechToTextServiceException(SpeechToTextServiceException exception, WebRequest request) {
        log.error("Handling SpeechToTextServiceException: {}", exception.getMessage());
        return buildErrorResponse(exception.getMessage(), HttpStatus.SERVICE_UNAVAILABLE, request);
    }

    /**
     * Builds an error response with the given message, HTTP status, and request details.
     *
     * @param message the error message
     * @param httpStatus the HTTP status code
     * @param request the web request during which the error occurred
     * @return ResponseEntity with error details and HTTP status code
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus httpStatus, WebRequest request) {
        String requestURI = (((ServletWebRequest) request).getRequest().getRequestURI());
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                httpStatus.value(),
                message,
                requestURI
        );
        return new ResponseEntity<>(errorResponse, httpStatus);
    }
}
