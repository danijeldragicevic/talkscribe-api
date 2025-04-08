package com.productdock.service;

import com.productdock.model.SupportedLangauge;
import com.productdock.model.VoiceSelection;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class VoiceSelectorService {

    // Map Comprehend language codes → Polly locale codes
    private static final Map<String, String> LANG_CODE_TO_LOCALE_MAP = Map.of(
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

    // Map Polly locale codes → Language names
    private static final Map<String, String> LOCALE_TO_LANG_NAME_MAP = Map.of(
            "en-US", "English (US)",
            "de-DE", "German",
            "fr-FR", "France",
            "es-ES", "Spanish (Castilian)",
            "sv-SE", "Swedish",
            "cmn-CN", "Mandarin Chinese",
            "arb", "Arabic",
            "hi-IN", "Hindi",
            "pt-BR", "Portuguese (Brazil)"
    );

    /**
     * Selects the appropriate voice for text-to-speech conversion based on the detected language code.
     * @param languageCode the detected language code
     * @return a VoiceSelection object containing the language locale and voice ID
     * */
    public VoiceSelection selectVoice(String languageCode) {
        String pollyLocaleCode = LANG_CODE_TO_LOCALE_MAP.getOrDefault(languageCode, "en-US");
        String pollyVoiceId = LOCALE_TO_VOICE_MAP.getOrDefault(pollyLocaleCode, "Joanna");
        return new VoiceSelection(pollyLocaleCode, pollyVoiceId);
    }

    @Cacheable("supportedLanguages")
    public List<SupportedLangauge> getSupportedLanguages() {
        List<SupportedLangauge> supportedLangauges = new ArrayList<>();

        for (Map.Entry<String, String> entry: LANG_CODE_TO_LOCALE_MAP.entrySet()) {
            String languageCode = entry.getKey();
            String locale = entry.getValue();
            String languageName  = LOCALE_TO_LANG_NAME_MAP.get(locale);
            String voice = LOCALE_TO_VOICE_MAP.get(locale);

            SupportedLangauge supportedLangauge = new SupportedLangauge(languageCode,  languageName ,locale, voice);
            supportedLangauges.add(supportedLangauge);
        }

        // Sort the list by the language code
        supportedLangauges.sort(new Comparator<SupportedLangauge>() {
            @Override
            public int compare(SupportedLangauge o1, SupportedLangauge o2) {
                return o1.getLanguageCode().compareTo(o2.getLanguageCode());
            }
        });

        return  supportedLangauges;
    }
}
