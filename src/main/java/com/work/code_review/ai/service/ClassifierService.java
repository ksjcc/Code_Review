package com.work.code_review.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.work.code_review.ai.client.ClassifierClient;
import com.work.code_review.ai.entity.ClassificationAttemptResult;
import com.work.code_review.ai.entity.ClassificationCategory;
import com.work.code_review.ai.entity.ClassificationResult;
import com.work.code_review.ai.entity.Question;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

@Service
public class ClassifierService {

    private final ObjectMapper objectMapper;
    private final ClassifierClient classifierClient;
    private final Executor classifierExecutor;
    private final DirtyClassificationService dirtyClassificationService;

    public ClassifierService(
            ObjectMapper objectMapper,
            ClassifierClient classifierClient,
            Executor classifierExecutor,
            DirtyClassificationService dirtyClassificationService) {
        this.objectMapper = objectMapper;
        this.classifierClient = classifierClient;
        this.classifierExecutor = classifierExecutor;
        this.dirtyClassificationService = dirtyClassificationService;
    }

    public void batchClassify(String inputFile, String outputFile) throws IOException {
        List<Question> questions = objectMapper.readValue(
                new File(inputFile),
                new TypeReference<>() {
                });

        List<CompletableFuture<ClassificationResult>> futures = new ArrayList<>();
        for (Question item : questions) {
            futures.add(CompletableFuture.supplyAsync(() -> classify(item), classifierExecutor));
        }

        List<ClassificationResult> results = futures.stream()
                .map(future -> {
                    try {
                        return future.join();
                    } catch (CompletionException e) {
                        throw new RuntimeException("批量分类失败", e.getCause());
                    }
                })
                .toList();

        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputFile), results);
        System.out.println("分类完成，共处理 " + results.size() + " 条问题");
    }

    private ClassificationResult classify(Question item) {
        ClassificationAttemptResult attemptResult = classifierClient.classifyQuestionSafely(item.getQuestion());

        if (!attemptResult.isSuccess()) {
            dirtyClassificationService.save(
                    item.getId(),
                    item.getQuestion(),
                    attemptResult.getRawResponse(),
                    attemptResult.getErrorMessage());
            return buildResult(item, "人工审核");
        }

        ClassificationCategory category = attemptResult.getCategory();
        return buildResult(item, category.getLabel());
    }

    private ClassificationResult buildResult(Question item, String predictedCategory) {
        ClassificationResult result = new ClassificationResult();
        result.setId(item.getId());
        result.setQuestion(item.getQuestion());
        result.setPredictedCategory(predictedCategory);
        return result;
    }
}
