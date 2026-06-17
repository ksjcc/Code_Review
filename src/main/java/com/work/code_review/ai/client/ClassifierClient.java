package com.work.code_review.ai.client;

import com.work.code_review.ai.entity.ClassificationAttemptResult;
import com.work.code_review.ai.entity.ClassificationCategory;

public interface ClassifierClient {

    ClassificationCategory classifyQuestion(String question);

    ClassificationAttemptResult classifyQuestionSafely(String question);
}
