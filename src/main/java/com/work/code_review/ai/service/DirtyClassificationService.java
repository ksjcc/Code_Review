package com.work.code_review.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.work.code_review.ai.entity.DirtyClassificationRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DirtyClassificationService {

    private final ObjectMapper objectMapper;
    private final String dirtyOutputFile;

    public DirtyClassificationService(
            ObjectMapper objectMapper,
            @Value("${classifier.dirty.output-file:src/main/resources/static/task1_dirty_records.json}") String dirtyOutputFile) {
        this.objectMapper = objectMapper;
        this.dirtyOutputFile = dirtyOutputFile;
    }

    public synchronized void save(Long id, String question, String rawResponse, String errorMessage) {
        File file = new File(dirtyOutputFile);
        List<DirtyClassificationRecord> records = readExisting(file);

        DirtyClassificationRecord record = new DirtyClassificationRecord();
        record.setId(id);
        record.setQuestion(question);
        record.setRawResponse(rawResponse);
        record.setErrorMessage(errorMessage);
        record.setCreatedAt(LocalDateTime.now());
        records.add(record);

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, records);
        } catch (IOException e) {
            throw new RuntimeException("写入脏表失败", e);
        }
    }

    private List<DirtyClassificationRecord> readExisting(File file) {
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(file, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException("读取脏表失败", e);
        }
    }
}
