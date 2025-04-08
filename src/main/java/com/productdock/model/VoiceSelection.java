package com.productdock.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing a voice selection for text-to-speech conversion.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoiceSelection {
    private String pollyLocaleCode;
    private String pollyVoiceId;
}
