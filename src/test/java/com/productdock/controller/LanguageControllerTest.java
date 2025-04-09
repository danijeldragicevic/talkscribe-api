package com.productdock.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.productdock.model.SupportedLangauge;
import com.productdock.service.SupportedVoicesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LanguageControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private SupportedVoicesService supportedVoicesService;

    @InjectMocks
    private LanguageController languageController;

    @BeforeEach
    void setUp() {
        Mockito.reset(supportedVoicesService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(languageController)
                .build();
    }

    @Test
    void shouldReturnSupportedLanguages() throws Exception {
        List<SupportedLangauge> mockLanguages = List.of(
                new SupportedLangauge("en", "English (US)", "en-US", "Joanna"),
                new SupportedLangauge("de", "German", "de-DE", "Vicki")
        );

        when(supportedVoicesService.getSupportedLanguages()).thenReturn(mockLanguages);

        mockMvc.perform(get("/api/languages")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].languageCode").value("en"))
                .andExpect(jsonPath("$[0].languageName").value("English (US)"))
                .andExpect(jsonPath("$[0].locale").value("en-US"))
                .andExpect(jsonPath("$[0].voice").value("Joanna"))
                .andExpect(jsonPath("$[1].languageCode").value("de"))
                .andExpect(jsonPath("$[1].languageName").value("German"))
                .andExpect(jsonPath("$[1].locale").value("de-DE"))
                .andExpect(jsonPath("$[1].voice").value("Vicki"));
    }
}