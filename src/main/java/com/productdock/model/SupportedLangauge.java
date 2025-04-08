package com.productdock.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing supported languages for text-to-speech conversion.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportedLangauge {
    private String languageCode;
    private String languageName;
    private String locale;
    private String voice;
}
