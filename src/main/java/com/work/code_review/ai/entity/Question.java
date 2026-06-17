package com.work.code_review.ai.entity;

import lombok.Data;

@Data
public class Question {

    private Long id;
    private String question;
    private String label;
}
