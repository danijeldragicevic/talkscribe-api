package com.productdock.service;

import com.productdock.model.VoiceSelection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VoiceSelectorServiceTest {

    private VoiceSelectorService voiceSelectorService;

    @BeforeEach
    void setUp() {
        voiceSelectorService = new VoiceSelectorService();
    }

    @Test
    void shouldReturnCorrectVoiceForKnownLanguages() {
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
        VoiceSelection result = voiceSelectorService.selectVoice("xx");
        assertEquals("en-US", result.getPollyLocaleCode());
        assertEquals("Joanna", result.getPollyVoiceId());
    }

    // Helper method to keep tests DRY
    private void assertVoice(String inputLanguage, String expectedLocale, String expectedVoice) {
        VoiceSelection result = voiceSelectorService.selectVoice(inputLanguage);
        assertEquals(expectedLocale, result.getPollyLocaleCode(), "Locale mismatch for language: " + inputLanguage);
        assertEquals(expectedVoice, result.getPollyVoiceId(), "Voice mismatch for language: " + inputLanguage);
    }
}
