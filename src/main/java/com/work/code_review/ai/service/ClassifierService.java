package com.work.code_review.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.work.code_review.ai.client.ClassifierClient;
import com.work.code_review.ai.entity.ClassificationResult;
import com.work.code_review.ai.entity.Question;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClassifierService {

    private final ObjectMapper objectMapper;
    private final ClassifierClient task1ClassifierClient;

    public ClassifierService(ObjectMapper objectMapper, ClassifierClient task1ClassifierClient) {
        this.objectMapper = objectMapper;
        this.task1ClassifierClient = task1ClassifierClient;
    }

    public void batchClassify(String inputFile, String outputFile) throws IOException {
        List<Question> questions = objectMapper.readValue(
                new File(inputFile),
                new TypeReference<>() {
                });

        List<ClassificationResult> results = new ArrayList<>();
        for (Question item : questions) {
            String category = task1ClassifierClient.classifyQuestion(item.getQuestion());

            ClassificationResult result = new ClassificationResult();
            result.setId(item.getId());
            result.setQuestion(item.getQuestion());
            result.setPredictedCategory(category);
            results.add(result);
        }

        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputFile), results);
        System.out.println("分类完成，共处理 " + results.size() + " 条问题");
    }
}
