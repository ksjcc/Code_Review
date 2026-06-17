package com.work.code_review.ai.client;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import dev.langchain4j.model.chat.ChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class LlmChat {

  private static final int MAX_RETRY = 2;

  private final ChatModel chatModel;

  public String streamChat(
      String requestId,
      String message,
      StreamingChunkHandler handler,
      Supplier<String> fallbackSupplier) {

    long start = System.currentTimeMillis();
    for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
      try {
        String result = chatModel.chat(message);
        long latency = System.currentTimeMillis() - start;
        log.info("LLM stream success requestId={} attempt={} latency={}ms", requestId, attempt, latency);
        PromptRuntimeTracker.recordLlmCall(message, result, attempt, true, false);
        handler.onChunk(result);
        handler.onComplete(result);
        return result;
      } catch (Exception e) {
        log.warn("LLM stream failed requestId={} attempt={}", requestId, attempt, e);
      }
    }

    long latency = System.currentTimeMillis() - start;
    log.error("LLM stream fallback requestId={} latency={}ms", requestId, latency);
    String fallback = fallbackSupplier.get();
    PromptRuntimeTracker.recordLlmCall(message, fallback, MAX_RETRY, false, true);
    handler.onChunk(fallback);
    handler.onComplete(fallback);
    return fallback;
  }
}
