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
    void shouldReturnCorrectVoiceForKnownLanguages() {
        // Test for various languages
        assertVoice("us", "en-US", "Joanna");
        assertVoice("de", "de-DE", "Vicki");
        assertVoice("fr", "fr-FR", "Lea");
        assertVoice("es", "es-ES", "Lucia");
        assertVoice("sv", "sv-SE", "Elin");
        assertVoice("zh", "cmn-CN", "Zhiyu");
        assertVoice("ar", "arb", "Hala");
        assertVoice("hi", "hi-IN", "Kajal");
        assertVoice("pt", "pt-BR", "Vitoria");
    }

    @Test
    void shouldFallbackToDefaultForUnknownLanguage() {
        // Test for an unknown language
        VoiceSelection result = supportedVoicesService.selectVoice("xx");
        assertEquals("en-US", result.getPollyLocaleCode());
        assertEquals("Joanna", result.getPollyVoiceId());
    }

    @Test
    void shouldReturnAllSupportedLanguages() {
        // Get all supported languages
        List<SupportedLangauge> supportedLanguages = supportedVoicesService.getSupportedLanguages();

        // Verify the number of supported languages
        assertEquals(9, supportedLanguages.size(), "Unexpected number of supported languages");

        // Verify the properties of each supported language
        assertLanguage(supportedLanguages.get(0), "ar", "Arabic", "arb", "Hala");
        assertLanguage(supportedLanguages.get(1), "de", "German", "de-DE", "Vicki");
        assertLanguage(supportedLanguages.get(2), "en", "English (US)", "en-US", "Joanna");
        assertLanguage(supportedLanguages.get(3), "es", "Spanish (Castilian)", "es-ES", "Lucia");
        assertLanguage(supportedLanguages.get(4), "fr", "France", "fr-FR", "Lea");
        assertLanguage(supportedLanguages.get(5), "hi", "Hindi", "hi-IN", "Kajal");
        assertLanguage(supportedLanguages.get(6), "pt", "Portuguese (Brazil)", "pt-BR", "Vitoria");
        assertLanguage(supportedLanguages.get(7), "sv", "Swedish", "sv-SE", "Elin");
        assertLanguage(supportedLanguages.get(8), "zh", "Mandarin Chinese", "cmn-CN", "Zhiyu");
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
