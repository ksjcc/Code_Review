package com.work.code_review.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

@Configuration
public class AiConfig {
  @Bean
  public ChatModel chatModel(
      @Value("${langchain4j.open-ai.chat-model.api-key}") String apiKey,
      @Value("${langchain4j.open-ai.chat-model.base-url}") String baseUrl,
      @Value("${langchain4j.open-ai.chat-model.model-name}") String modelName,
      @Value("${langchain4j.open-ai.chat-model.temperature:0.7}") Double temperature) {
    return OpenAiChatModel.builder()
        .apiKey(apiKey)
        .baseUrl(baseUrl)
        .modelName(modelName)
        .temperature(temperature)
        .build();
  }

  @Bean
  public StreamingChatModel streamingChatModel(
      @Value("${langchain4j.open-ai.chat-model.api-key}") String apiKey,
      @Value("${langchain4j.open-ai.chat-model.base-url}") String baseUrl,
      @Value("${langchain4j.open-ai.chat-model.model-name}") String modelName,
      @Value("${langchain4j.open-ai.chat-model.temperature:0.7}") Double temperature) {
    return OpenAiStreamingChatModel.builder()
        .apiKey(apiKey)
        .baseUrl(baseUrl)
        .modelName(modelName)
        .temperature(temperature)
        .build();
  }
}
