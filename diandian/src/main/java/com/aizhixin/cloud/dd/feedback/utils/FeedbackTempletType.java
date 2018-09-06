package com.aizhixin.cloud.dd.feedback.utils;

/**
 * @author hsh
 */
public enum FeedbackTempletType {
    TEACHING("教学反馈", 10), STEERING("督导反馈", 20);

    private String name;
    private Integer value;

    FeedbackTempletType(String name, Integer value) {
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
