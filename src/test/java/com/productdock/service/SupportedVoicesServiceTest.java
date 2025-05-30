package com.productdock.service;

import com.productdock.model.SupportedLangauge;
import com.productdock.model.VoiceSelection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SupportedVoicesServiceTest {

    private SupportedVoicesService supportedVoicesService;

    @BeforeEach
    void setUp() {
        supportedVoicesService = new SupportedVoicesService();
    }

    @Test
    void shouldReturnCorrectVoiceSuccessfully() {
        assertVoice("us", "en-US", "Joanna");
        assertVoice("de", "de-DE", "Vicki");
        assertVoice("fr", "fr-FR", "Lea");
        assertVoice("es", "es-ES", "Lucia");
        assertVoice("sv", "sv-SE", "Elin");
        assertVoice("pt", "pt-PT", "Ines");
    }

    @Test
    void shouldFallbackToDefaultLanguageSuccessfully() {
        // When
        VoiceSelection result = supportedVoicesService.selectVoice("xx");

        // Then
        assertEquals("en-US", result.getPollyLocaleCode());
        assertEquals("Joanna", result.getPollyVoiceId());
    }

    @Test
    void shouldReturnAllSupportedLanguagesSuccessfully() {
        // Given
        List<SupportedLangauge> supportedLanguages = supportedVoicesService.getSupportedLanguages();

        // When/Then
        assertEquals(6, supportedLanguages.size(), "Unexpected number of supported languages");

        assertLanguage(supportedLanguages.get(0), "en", "English", "en-US", "Joanna");
        assertLanguage(supportedLanguages.get(1), "fr", "French", "fr-FR", "Lea");
        assertLanguage(supportedLanguages.get(2), "de", "German", "de-DE", "Vicki");
        assertLanguage(supportedLanguages.get(3), "pt", "Portuguese", "pt-PT", "Ines");
        assertLanguage(supportedLanguages.get(4), "es", "Spanish", "es-ES", "Lucia");
        assertLanguage(supportedLanguages.get(5), "sv", "Swedish", "sv-SE", "Elin");
    }

    // Helper method to verify voice selection
    private void assertVoice(String inputLanguage, String expectedLocale, String expectedVoice) {
        VoiceSelection result = supportedVoicesService.selectVoice(inputLanguage);
        assertEquals(expectedLocale, result.getPollyLocaleCode(), "Locale mismatch for language: " + inputLanguage);
        assertEquals(expectedVoice, result.getPollyVoiceId(), "Voice mismatch for language: " + inputLanguage);
    }

    // Helper method to verify language properties
    private void assertLanguage(SupportedLangauge language, String expectedCode, String expectedName, String expectedLocale, String expectedVoice) {
        assertEquals(expectedCode, language.getLanguageCode(), "Language code mismatch");
        assertEquals(expectedName, language.getLanguageName(), "Language name mismatch");
        assertEquals(expectedLocale, language.getLocale(), "Locale mismatch");
        assertEquals(expectedVoice, language.getVoice(), "Voice mismatch");
    }
}
