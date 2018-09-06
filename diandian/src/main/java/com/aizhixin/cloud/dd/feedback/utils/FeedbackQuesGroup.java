package com.aizhixin.cloud.dd.feedback.utils;

/**
 * @author hsh
 */
public enum FeedbackQuesGroup {
    FEEDBACK("反馈评价", 10), TEACHER("教师评价", 20), STYLE("学风评价", 30);

    private String name;
    private Integer value;

    FeedbackQuesGroup(String name, Integer value) {
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
