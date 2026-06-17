package com.work.code_review.ai.client;

public interface StreamingChunkHandler {

    void onChunk(String chunk);

    void onComplete(String fullContent);
}
