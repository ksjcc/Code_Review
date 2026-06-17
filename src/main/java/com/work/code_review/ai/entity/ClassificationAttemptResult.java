package com.work.code_review.ai.entity;

import lombok.Data;

@Data
public class ClassificationAttemptResult {

    private boolean success;
    private ClassificationCategory category;
    private String rawResponse;
    private String errorMessage;

    public static ClassificationAttemptResult success(ClassificationCategory category, String rawResponse) {
        ClassificationAttemptResult result = new ClassificationAttemptResult();
        result.setSuccess(true);
        result.setCategory(category);
        result.setRawResponse(rawResponse);
        return result;
    }

    public static ClassificationAttemptResult failed(String rawResponse, String errorMessage) {
        ClassificationAttemptResult result = new ClassificationAttemptResult();
        result.setSuccess(false);
        result.setRawResponse(rawResponse);
        result.setErrorMessage(errorMessage);
        return result;
    }
}
