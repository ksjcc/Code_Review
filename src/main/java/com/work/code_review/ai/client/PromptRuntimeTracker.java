package com.work.code_review.ai.client;

public final class PromptRuntimeTracker {

    private PromptRuntimeTracker() {
    }

    public static void recordLlmCall(
            String message,
            String result,
            int attempt,
            boolean success,
            boolean fallback) {
    }
}
