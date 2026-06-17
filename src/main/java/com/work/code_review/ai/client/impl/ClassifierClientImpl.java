package com.work.code_review.ai.client.impl;

import org.springframework.stereotype.Component;

import com.work.code_review.ai.client.ClassifierClient;
import com.work.code_review.ai.entity.ClassificationAttemptResult;
import com.work.code_review.ai.entity.ClassificationCategory;

import dev.langchain4j.model.chat.ChatModel;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClassifierClientImpl implements ClassifierClient {

    private static final int MAX_RETRY = 3;

    private final ChatModel chatModel;

    @Override
    public ClassificationCategory classifyQuestion(String question) {
        ClassificationAttemptResult result = classifyQuestionSafely(question);
        if (!result.isSuccess()) {
            throw new IllegalStateException(result.getErrorMessage());
        }
        return result.getCategory();
    }

    @Override
    public ClassificationAttemptResult classifyQuestionSafely(String question) {
        String prompt = """
                你是一个客服FAQ分类助手。你的任务是根据用户问题，从给定的6个分类标签中选择且只能选择1个最合适的类别。

                分类标签与定义如下：
                1. 退款退货：用户要求退款、退货、换货，或咨询退款进度。
                2. 物流查询：用户询问包裹位置、配送状态、快递信息。
                3. 账号问题：用户遇到登录、密码、账号安全、绑定信息修改等问题。
                4. 商品咨询：用户询问商品信息、规格、材质、库存、价格、适用场景等内容。
                5. 投诉建议：用户对服务、流程、商品质量等表达不满，或提出改进建议。
                6. 其他：不属于以上任何类别的问题，例如闲聊、无明确意图、无法归类的表述。

                分类规则：
                1. 如果一条问题同时涉及多个类别，以用户的主要诉求为准。
                2. 输出必须严格使用以下标签之一：退款退货、物流查询、账号问题、商品咨询、投诉建议、其他。
                3. “退款进度查询”必须归入“退款退货”，不能归入“物流查询”。
                4. 如果表述中包含辱骂、抱怨、不满，但同时包含明确投诉内容或建议，归入“投诉建议”。
                5. 如果只是纯辱骂、纯情绪表达、无明确业务诉求，归入“其他”。
                6. 如果用户主要在问商品是否有某种属性、规格、材质、库存或用途，归入“商品咨询”。
                7. 如果用户主要在问包裹在哪、什么时候到、派送是否异常、地址能否修改，归入“物流查询”。

                用户问题：%s

                请直接输出最终分类结果。
                不要解释。
                不要输出多余文字。
                只能输出一个标签名称。""".formatted(question);

        String lastRawResponse = null;
        String lastErrorMessage = null;

        for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
            try {
                String response = chatModel.chat(prompt).trim();
                lastRawResponse = response;
                ClassificationCategory category = ClassificationCategory.fromLabel(response);
                return ClassificationAttemptResult.success(category, response);
            } catch (Exception e) {
                lastErrorMessage = "第 " + attempt + " 次分类失败: " + e.getMessage();
            }
        }

        return ClassificationAttemptResult.failed(lastRawResponse, lastErrorMessage);
    }
}
