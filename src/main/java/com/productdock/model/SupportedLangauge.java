package com.productdock.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportedLangauge {
    private String languageCode;
    private String languageName;
    private String locale;
    private String voice;
}
