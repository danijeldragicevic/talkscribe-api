package com.productdock.controller;

import com.productdock.exception._GlobalExceptionHandler;
import com.productdock.model.TranscriptionJobResponse;
import com.productdock.service.SpeechToTextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SpeechToTextControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SpeechToTextService speechToTextService;

    @InjectMocks
    private SpeechToTextController speechToTextController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(speechToTextController)
                .setControllerAdvice(new _GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldStartTranscriptionJobSuccessfully() throws Exception {
        // Given
        MockMultipartFile mockFile = new MockMultipartFile("audioFile", "test.mp3", "audio/mpeg", "audio-content".getBytes());
        TranscriptionJobResponse response = new TranscriptionJobResponse("job-123", "IN_PROGRESS", null);

        when(speechToTextService.startTranscriptionJob(any())).thenReturn(response);

        // When
        mockMvc.perform(multipart("/api/speech-to-text")
                        .file(mockFile))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Then
        verify(speechToTextService, times(1)).startTranscriptionJob(any());
    }

    @Test
    void shouldReturnBadRequestWhenFileIsEmpty() throws Exception {
        // Given
        MockMultipartFile emptyFile = new MockMultipartFile("audioFile", "", "audio/mpeg", new byte[0]);

        // When
        mockMvc.perform(multipart("/api/speech-to-text")
                        .file(emptyFile))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Then
        verify(speechToTextService, never()).startTranscriptionJob(any());
    }

    @Test
    void shouldGetTranscriptionJobStatusSuccessfully() throws Exception {
        // Given
        String jobName = "job-123";
        TranscriptionJobResponse response = new TranscriptionJobResponse(jobName, "COMPLETED", "This is my test voice recording.");

        // When
        when(speechToTextService.getTranscriptionJobStatus(jobName)).thenReturn(response);

        mockMvc.perform(get("/api/speech-to-text/status/{jobName}", jobName))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-store, must-revalidate"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.jobName").value(jobName))
                .andExpect(jsonPath("$.jobStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.transcript").value("This is my test voice recording."));

        // Then
        verify(speechToTextService, times(1)).getTranscriptionJobStatus(jobName);
    }
}
