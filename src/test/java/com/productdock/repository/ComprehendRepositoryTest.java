package com.productdock.repository;

import com.productdock.exception.ComprehendRepositoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.comprehend.model.ComprehendException;
import software.amazon.awssdk.services.comprehend.model.DetectDominantLanguageRequest;
import software.amazon.awssdk.services.comprehend.model.DetectDominantLanguageResponse;
import software.amazon.awssdk.services.comprehend.model.DominantLanguage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComprehendRepositoryTest {

    @Mock
    private ComprehendClient comprehendClient;

    @InjectMocks
    private ComprehendRepository comprehendRepository;

    @BeforeEach
    void setUp() {
        Mockito.reset(comprehendClient);
    }

    @Test
    void shouldReturnDetectedLanguageCodeSuccessfully() {
        // Given
        DominantLanguage language = DominantLanguage.builder()
                .languageCode("fr")
                .score(0.98f)
                .build();

        DetectDominantLanguageResponse response = DetectDominantLanguageResponse.builder()
                .languages(language)
                .build();

        // When
        when(comprehendClient.detectDominantLanguage(any(DetectDominantLanguageRequest.class)))
                .thenReturn(response);

        // Then
        assertEquals("fr", comprehendRepository.detectLanguage("Bonjour, comment ça va ?"));
        verify(comprehendClient, times(1)).detectDominantLanguage(any(DetectDominantLanguageRequest.class));
    }

    @Test
    void shouldReturnDefaultLanguageCodeSuccessfully() {
        // Given
        DetectDominantLanguageResponse response = DetectDominantLanguageResponse.builder()
                .languages(List.of())
                .build();

        // When
        when(comprehendClient.detectDominantLanguage(any(DetectDominantLanguageRequest.class)))
                .thenReturn(response);

        // Then
        assertEquals("en", comprehendRepository.detectLanguage("Unintelligible text"));
        verify(comprehendClient, times(1)).detectDominantLanguage(any(DetectDominantLanguageRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenComprehendFails() {
        // When
        when(comprehendClient.detectDominantLanguage(any(DetectDominantLanguageRequest.class)))
                .thenThrow(ComprehendException.builder().message("error").build());

        ComprehendRepositoryException exception = assertThrows(ComprehendRepositoryException.class,
                () -> comprehendRepository.detectLanguage("Some text"));

        // Then
        assertEquals("AWS Comprehend error", exception.getMessage());
    }
}
