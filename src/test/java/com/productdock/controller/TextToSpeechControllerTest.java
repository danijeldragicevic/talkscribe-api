package com.productdock.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.productdock.exception.GlobalExceptionHandler;
import com.productdock.model.TextToSpeechRequest;
import com.productdock.service.TextToSpeechService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.ByteArrayInputStream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for TextToSpeechController.
 */
@ExtendWith(MockitoExtension.class)
class TextToSpeechControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TextToSpeechService textToSpeechService;

    @InjectMocks
    private TextToSpeechController textToSpeechController;

    @BeforeEach
    void setUp() {
        Mockito.reset(textToSpeechService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(textToSpeechController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldConvertTextToSpeech() throws Exception {
        String text = "Hello, World!";
        TextToSpeechRequest request = new TextToSpeechRequest(text);

        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream("audio data".getBytes()));
        when(textToSpeechService.convertTextToSpeech(text)).thenReturn(resource);

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/text-to-speech")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Test
    void shouldReturnBadRequestWhenTextIsBlank() throws Exception {
        TextToSpeechRequest request = new TextToSpeechRequest("");
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/text-to-speech")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                        .andExpect(status().isBadRequest());
    }
}
