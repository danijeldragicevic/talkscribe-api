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
public class SupportedVoicesService {

    // Map Comprehend language codes → Polly locale codes
    private static final Map<String, String> LANG_CODE_TO_LOCALE_MAP = Map.of(
            "en", "en-US",
            "de", "de-DE",
            "fr", "fr-FR",
            "es", "es-ES",
            "sv", "sv-SE",
            "pt", "pt-PT"
    );

    // Map Polly locale codes → Polly voice IDs
    private static final Map<String, String> LOCALE_TO_VOICE_MAP = Map.of(
            "en-US", "Joanna",
            "de-DE", "Vicki",
            "fr-FR", "Lea",
            "es-ES", "Lucia",
            "sv-SE", "Elin",
            "pt-PT", "Ines"
    );

    // Map Polly locale codes → Language names
    private static final Map<String, String> LOCALE_TO_LANG_NAME_MAP = Map.of(
            "en-US", "English",
            "de-DE", "German",
            "fr-FR", "French",
            "es-ES", "Spanish",
            "sv-SE", "Swedish",
            "pt-PT", "Portuguese"
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

        // Sort the list by the language name
        supportedLangauges.sort(new Comparator<SupportedLangauge>() {
            @Override
            public int compare(SupportedLangauge o1, SupportedLangauge o2) {
                return o1.getLanguageName().compareTo(o2.getLanguageName());
            }
        });

        return  supportedLangauges;
    }
}
