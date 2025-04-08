package com.productdock.service;

import com.productdock.model.VoiceSelection;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class VoiceSelectorService {

    // Map Comprehend language codes → Polly locale codes
    private static final Map<String, String> LANGUAGE_TO_LOCALE_MAP = Map.of(
            "en", "en-US",
            "de", "de-DE",
            "fr", "fr-FR",
            "es", "es-ES",
            "sv", "sv-SE",
            "zh", "cmn-CN",
            "ar", "arb",
            "hi", "hi-IN",
            "pt", "pt-BR"
    );

    // Map Polly locale codes → preferred Polly voice IDs
    private static final Map<String, String> LOCALE_TO_VOICE_MAP = Map.of(
            "en-US", "Joanna",
            "de-DE", "Vicki",
            "fr-FR", "Lea",
            "es-ES", "Lucia",
            "sv-SE", "Elin",
            "cmn-CN", "Zhiyu",
            "arb", "Hala",
            "hi-IN", "Kajal",
            "pt-BR", "Vitoria"
    );

    /**
     * Selects the appropriate voice for text-to-speech conversion based on the detected language code.
     * @param languageCode the detected language code
     * @return a VoiceSelection object containing the language locale and voice ID
     * */
    public VoiceSelection selectVoice(String languageCode) {
        String pollyLocaleCode = LANGUAGE_TO_LOCALE_MAP.getOrDefault(languageCode, "en-US");
        String pollyVoiceId = LOCALE_TO_VOICE_MAP.getOrDefault(pollyLocaleCode, "Joanna");
        return new VoiceSelection(pollyLocaleCode, pollyVoiceId);
    }
}
