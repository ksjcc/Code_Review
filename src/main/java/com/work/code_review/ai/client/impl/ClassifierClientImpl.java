package com.work.code_review.ai.client.impl;

import org.springframework.stereotype.Component;

import com.work.code_review.ai.client.ClassifierClient;

import dev.langchain4j.model.chat.ChatModel;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClassifierClientImpl implements ClassifierClient {

    private final ChatModel chatModel;

    @Override
    public String classifyQuestion(String question) {
        String prompt = """
                你是一个客服分类助手。请对以下用户问题进行分类。

                分类类别：退款退货、物流查询、账号问题、商品咨询、投诉建议、其他

                用户问题：%s

                请直接回复分类结果，只回复类别名称。""".formatted(question);

        return chatModel.chat(prompt);
    }
}
