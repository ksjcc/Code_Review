package com.work.code_review.ai.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ClassificationResult {

    private Long id;
    private String question;

    @JsonProperty("predicted_category")
    private String predictedCategory;

}
