package com.productdock.exception;

import com.productdock.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private _GlobalExceptionHandler exceptionHandler;

    private ServletWebRequest mockWebRequestWithUri(String uri) {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURI()).thenReturn(uri);

        ServletWebRequest webRequest = mock(ServletWebRequest.class);
        when(webRequest.getRequest()).thenReturn(mockRequest);
        return webRequest;
    }

    @Test
    void shouldHandleMaxUploadSizeExceededException() {
        // Given
        MaxUploadSizeExceededException exception = new MaxUploadSizeExceededException(10 * 1024 * 1024); // 10 MB

        // When
        ServletWebRequest request = mockWebRequestWithUri("/api/any-endpoint");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMaxUploadSizeExceededException(exception, request);

        // Then
        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());
        assertTrue(response.getBody().getError().contains("Maximum upload size"));
        assertEquals("/api/any-endpoint", response.getBody().getPath());
    }

    @Test
    void shouldHandleTooManyRequestsException() {
        // Given
        TooManyRequestsException exception = new TooManyRequestsException("Too many requests");

        // When
        ServletWebRequest request = mockWebRequestWithUri("/api/any-endpoint");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTooManyRequestsException(exception, request);

        // Then
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        Assertions.assertNotNull(response.getBody()); // Ensure response body is not null
        assertEquals("Too many requests", response.getBody().getError());
        assertEquals("/api/any-endpoint", response.getBody().getPath());
    }

    @Test
    void shouldHandleTextToSpeechServiceException() {
        // Given
        TextToSpeechServiceException exception = new TextToSpeechServiceException("Failed to convert text to speech", new RuntimeException("Mock"));

        // When
        ServletWebRequest request = mockWebRequestWithUri("/api/text-to-speech");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTextToSpeechServiceException(exception, request);

        // Then
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        Assertions.assertNotNull(response.getBody()); // Ensure response body is not null
        assertEquals("Failed to convert text to speech", response.getBody().getError());
        assertEquals("/api/text-to-speech", response.getBody().getPath());
    }

    @Test
    void shouldHandleSpeechToTextServiceException() {
        // Given
        SpeechToTextServiceException exception = new SpeechToTextServiceException("Failed to convert speech to text", new RuntimeException("Mock"));

        // When
        ServletWebRequest request = mockWebRequestWithUri("/api/speech-to-text");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleSpeechToTextServiceException(exception, request);

        // Then
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        Assertions.assertNotNull(response.getBody()); // Ensure response body is not null
        assertEquals("Failed to convert speech to text", response.getBody().getError());
        assertEquals("/api/speech-to-text", response.getBody().getPath());
    }
}
