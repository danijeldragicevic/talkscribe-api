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
    private WebRequest mockRequest;

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Test
    void shouldHandleTextToSpeechServiceException() {
        TextToSpeechServiceException exception = new TextToSpeechServiceException("Failed to convert text to speech", new RuntimeException("Mock error"));

        ServletWebRequest servletWebRequest = Mockito.mock(ServletWebRequest.class);
        HttpServletRequest mockHttpRequest = Mockito.mock(HttpServletRequest.class);
        when(servletWebRequest.getRequest()).thenReturn(mockHttpRequest);
        when(mockHttpRequest.getRequestURI()).thenReturn("/api/text-to-speech");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTextToSpeechServiceException(exception, servletWebRequest);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Failed to convert text to speech", response.getBody().getError());
        assertEquals("/api/text-to-speech", response.getBody().getPath());
    }
}