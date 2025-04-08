package com.productdock.exception;

import com.productdock.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Test class for GlobalExceptionHandler.
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private WebRequest mockRequest; // Mocking WebRequest

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler; // Automatically injects into the test

    @Test
    void shouldHandleTextToSpeechServiceException() {
        // Given: Simulate an exception
        TextToSpeechServiceException exception = new TextToSpeechServiceException("Failed to convert text to speech", new RuntimeException("Mock error"));

        // Simulate request path
        ServletWebRequest servletWebRequest = Mockito.mock(ServletWebRequest.class);
        HttpServletRequest mockHttpRequest = Mockito.mock(HttpServletRequest.class);
        when(servletWebRequest.getRequest()).thenReturn(mockHttpRequest);
        when(mockHttpRequest.getRequestURI()).thenReturn("/api/text-to-speech");

        // When: Call the exception handler
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTextToSpeechServiceException(exception, servletWebRequest);

        // Then: Validate response
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Failed to convert text to speech", response.getBody().getError());
        assertEquals("/api/text-to-speech", response.getBody().getPath());
    }
}