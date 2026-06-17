package com.work.code_review.ai.entity;

import java.util.Arrays;

public enum ClassificationCategory {

    REFUND_RETURN("退款退货"),
    LOGISTICS_QUERY("物流查询"),
    ACCOUNT_ISSUE("账号问题"),
    PRODUCT_CONSULTATION("商品咨询"),
    COMPLAINT_SUGGESTION("投诉建议"),
    OTHER("其他");

    private final String label;

    ClassificationCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static ClassificationCategory fromLabel(String label) {
        if (label == null) {
            throw new IllegalArgumentException("分类结果不能为空");
        }
        return Arrays.stream(values())
                .filter(category -> category.label.equals(label.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("非法分类结果: " + label));
    }
}
