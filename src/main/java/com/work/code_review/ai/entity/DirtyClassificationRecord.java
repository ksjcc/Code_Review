package com.work.code_review.ai.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DirtyClassificationRecord {

    private Long id;
    private String question;
    private String rawResponse;
    private String errorMessage;
    private LocalDateTime createdAt;
}
