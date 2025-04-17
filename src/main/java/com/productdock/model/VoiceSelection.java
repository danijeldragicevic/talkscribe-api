package com.productdock.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoiceSelection {
    private String pollyLocaleCode;
    private String pollyVoiceId;
}
