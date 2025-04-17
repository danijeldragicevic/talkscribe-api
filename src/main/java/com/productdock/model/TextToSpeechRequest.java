package com.productdock.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextToSpeechRequest {
    @NotBlank(message = "Text cannot be blank")
    private String text;
}
