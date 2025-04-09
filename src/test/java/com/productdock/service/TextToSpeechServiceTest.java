package com.productdock.service;

import com.productdock.exception.ComprehendRepositoryException;
import com.productdock.exception.PollyRepositoryException;
import com.productdock.exception.TextToSpeechServiceException;
import com.productdock.model.VoiceSelection;
import com.productdock.repository.ComprehendRepository;
import com.productdock.repository.PollyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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
    private ComprehendRepository comprehendRepository;

    @Mock
    private PollyRepository pollyRepository;

    @Mock
    private SupportedVoicesService supportedVoicesService;

    private TextToSpeechService textToSpeechService;

    private static final String SAMPLE_TEXT = "Hello, this is a test";

    @BeforeEach
    void setUp() {
        textToSpeechService = new TextToSpeechService(comprehendRepository, pollyRepository, supportedVoicesService);
    }

    @Test
    void shouldConvertTextToSpeechSuccessfully() throws ComprehendRepositoryException {
        // Given
        InputStream audio = new ByteArrayInputStream("audio data".getBytes());
        VoiceSelection mockVoice = new VoiceSelection("en-US", "Joanna");

        // When
        when(comprehendRepository.detectLanguage(SAMPLE_TEXT)).thenReturn("en");
        when(supportedVoicesService.selectVoice("en")).thenReturn(mockVoice);
        when(pollyRepository.convertTextToSpeech(SAMPLE_TEXT, mockVoice.getPollyVoiceId(), mockVoice.getPollyLocaleCode())).thenReturn(audio);

        // Then
        InputStreamResource result = textToSpeechService.convertTextToSpeech(SAMPLE_TEXT);
        assertNotNull(result, "The response should not be null");
        assertDoesNotThrow(() -> result.getInputStream(), "Should return a valid InputStream");

        // Verify
        verify(comprehendRepository, times(1)).detectLanguage(SAMPLE_TEXT);
        verify(supportedVoicesService, times(1)).selectVoice("en");
        verify(pollyRepository, times(1)).convertTextToSpeech(SAMPLE_TEXT, mockVoice.getPollyVoiceId(), mockVoice.getPollyLocaleCode());
    }

    @Test
    void shouldThrowServiceExceptionWhenRepositoryFails() throws ComprehendRepositoryException {
        // Given
        VoiceSelection mockVoice = new VoiceSelection("en-US", "Joanna");

        // When
        when(comprehendRepository.detectLanguage(SAMPLE_TEXT)).thenReturn("en");
        when(supportedVoicesService.selectVoice("en")).thenReturn(mockVoice);
        when(pollyRepository.convertTextToSpeech(SAMPLE_TEXT, mockVoice.getPollyVoiceId(), mockVoice.getPollyLocaleCode()))
                .thenThrow(new PollyRepositoryException("Repository error", new RuntimeException("Mock error")));

        // Then
        TextToSpeechServiceException exception = assertThrows(
                TextToSpeechServiceException.class,
                () -> textToSpeechService.convertTextToSpeech(SAMPLE_TEXT),
                "Should throw TextToSpeechServiceException"
        );

        assertEquals("Error processing text-to-speech request", exception.getMessage());
        assertInstanceOf(PollyRepositoryException.class, exception.getCause());
    }
}
