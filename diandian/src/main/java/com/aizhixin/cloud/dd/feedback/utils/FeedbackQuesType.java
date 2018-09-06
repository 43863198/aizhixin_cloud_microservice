package com.aizhixin.cloud.dd.feedback.utils;

/**
 * @author hsh
 */
public enum FeedbackQuesType {
    DAFEN("打分题", 10), XUANXIANG("选项题", 20), WENDA("问答题", 30);

    private String name;
    private Integer value;

    FeedbackQuesType(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Integer getType() {
        return value;
    }
}
