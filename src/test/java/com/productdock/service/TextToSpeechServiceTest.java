package com.productdock.service;

import com.productdock.exception.TextToSpeechRepositoryException;
import com.productdock.exception.TextToSpeechServiceException;
import com.productdock.repository.TextToSpeechRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for TextToSpeechService.
 */
@ExtendWith(MockitoExtension.class)
class TextToSpeechServiceTest {

    @Mock
    private TextToSpeechRepository textToSpeechRepository;

    @InjectMocks
    private TextToSpeechService textToSpeechService;

    private static final String SAMPLE_TEXT = "Hello, this is a test";

    @BeforeEach
    void setUp() {
        Mockito.reset(textToSpeechRepository);
    }

    @Test
    void shouldConvertTextToSpeechSuccessfully() {
        // Given: Mock repository response
        InputStream mockAudioStream = new ByteArrayInputStream("mock audio data".getBytes());
        when(textToSpeechRepository.convertTextToSpeech(SAMPLE_TEXT)).thenReturn(mockAudioStream);

        // When: Calling the service method
        InputStreamResource result = textToSpeechService.convertTextToSpeech(SAMPLE_TEXT);

        // Then: Verify correct behavior
        assertNotNull(result, "The response should not be null");
        assertDoesNotThrow(() -> result.getInputStream(), "Should return a valid InputStream");

        // Ensure repository method was called once
        verify(textToSpeechRepository, times(1)).convertTextToSpeech(SAMPLE_TEXT);
    }

    @Test
    void shouldThrowServiceExceptionWhenRepositoryFails() {
        // Given: Simulate repository failure
        when(textToSpeechRepository.convertTextToSpeech(SAMPLE_TEXT))
                .thenThrow(new TextToSpeechRepositoryException("Repository error", new RuntimeException("Mock error")));

        // When: Calling the service method
        TextToSpeechServiceException exception = assertThrows(
                TextToSpeechServiceException.class,
                () -> textToSpeechService.convertTextToSpeech(SAMPLE_TEXT),
                "Should throw TextToSpeechServiceException"
        );

        // Then: Validate exception details
        assertEquals("Error processing text-to-speech request", exception.getMessage());
        assertInstanceOf(TextToSpeechRepositoryException.class, exception.getCause());

        // Ensure repository method was called once
        verify(textToSpeechRepository, times(1)).convertTextToSpeech(SAMPLE_TEXT);
    }
}
