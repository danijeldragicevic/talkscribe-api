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
    private VoiceSelectorService voiceSelectorService;

    private TextToSpeechService textToSpeechService;

    private static final String SAMPLE_TEXT = "Hello, this is a test";

    @BeforeEach
    void setUp() {
        textToSpeechService = new TextToSpeechService(comprehendRepository, pollyRepository, voiceSelectorService);
    }


    @Test
    void shouldConvertTextToSpeechSuccessfully() throws ComprehendRepositoryException {
        // Given: Mock repository response
        InputStream mockAudioStream = new ByteArrayInputStream("mock audio data".getBytes());

        when(comprehendRepository.detectLanguage(SAMPLE_TEXT)).thenReturn("en");

        VoiceSelection mockVoice = new VoiceSelection("en-US", "Joanna");
        when(voiceSelectorService.selectVoice("en")).thenReturn(mockVoice);

        when(pollyRepository.convertTextToSpeech(SAMPLE_TEXT, mockVoice.getPollyVoiceId(), mockVoice.getPollyLocaleCode())).thenReturn(mockAudioStream);

        // When: Calling the service method
        InputStreamResource result = textToSpeechService.convertTextToSpeech(SAMPLE_TEXT);

        // Then: Verify correct behavior
        assertNotNull(result, "The response should not be null");
        assertDoesNotThrow(() -> result.getInputStream(), "Should return a valid InputStream");
    }

    @Test
    void shouldThrowServiceExceptionWhenRepositoryFails() throws ComprehendRepositoryException {
        // Given: Simulate repository failure
        when(comprehendRepository.detectLanguage(SAMPLE_TEXT)).thenReturn("en");

        VoiceSelection mockVoice = new VoiceSelection("en-US", "Joanna");
        when(voiceSelectorService.selectVoice("en")).thenReturn(mockVoice);

        when(pollyRepository.convertTextToSpeech(SAMPLE_TEXT, mockVoice.getPollyVoiceId(), mockVoice.getPollyLocaleCode()))
                .thenThrow(new PollyRepositoryException("Repository error", new RuntimeException("Mock error")));

        // When: Calling the service method
        TextToSpeechServiceException exception = assertThrows(
                TextToSpeechServiceException.class,
                () -> textToSpeechService.convertTextToSpeech(SAMPLE_TEXT),
                "Should throw TextToSpeechServiceException"
        );

        // Then: Validate exception details
        assertEquals("Error processing text-to-speech request", exception.getMessage());
        assertInstanceOf(PollyRepositoryException.class, exception.getCause());
    }
}
