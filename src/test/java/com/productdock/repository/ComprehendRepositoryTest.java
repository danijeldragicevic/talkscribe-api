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
    void shouldReturnDetectedLanguageCode() throws ComprehendRepositoryException {
        // Given: Mock AWS Comprehend response
        DominantLanguage language = DominantLanguage.builder()
                .languageCode("fr")
                .score(0.98f)
                .build();

        DetectDominantLanguageResponse response = DetectDominantLanguageResponse.builder()
                .languages(language)
                .build();

        when(comprehendClient.detectDominantLanguage(any(DetectDominantLanguageRequest.class)))
                .thenReturn(response);

        // When: Calling the repository method
        String result = comprehendRepository.detectLanguage("Bonjour, comment ça va ?");

        // Then: Verify correct behaviour
        assertEquals("fr", result);

        // Ensure that the comprehendClient was called once
        verify(comprehendClient, times(1)).detectDominantLanguage(any(DetectDominantLanguageRequest.class));
    }

    @Test
    void shouldReturnDefaultLanguageCode() throws ComprehendRepositoryException {
        // Given: Mock AWS Comprehend response
        DetectDominantLanguageResponse response = DetectDominantLanguageResponse.builder()
                .languages(List.of())
                .build();

        when(comprehendClient.detectDominantLanguage(any(DetectDominantLanguageRequest.class)))
                .thenReturn(response);

        // When: Calling the repository method
        String result = comprehendRepository.detectLanguage("Unintelligible text");

        // Then: Verify correct behaviour
        assertEquals("en", result);

        // Ensure that the comprehendClient was called once
        verify(comprehendClient, times(1)).detectDominantLanguage(any(DetectDominantLanguageRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenComprehendFails() {
        // Given: Simulate AWS Comprehend failure
        when(comprehendClient.detectDominantLanguage(any(DetectDominantLanguageRequest.class)))
                .thenThrow(ComprehendException.builder().message("AWS Comprehend error").build());

        // When: Calling the repository method
        ComprehendRepositoryException exception = assertThrows(ComprehendRepositoryException.class,
                () -> comprehendRepository.detectLanguage("Some text"));

        // Then: Verify exception handling
        assertEquals("AWS Comprehend error", exception.getMessage());
    }
}
