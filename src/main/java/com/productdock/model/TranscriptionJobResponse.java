package com.productdock.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranscriptionJobResponse {
    private String jobName;
    private String jobStatus;
    private String transcript;
}
