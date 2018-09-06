package com.aizhixin.cloud.dd.questionnaire.utils;

public enum QuestionnaireType {

    STUDENT("学生评教", 10), TEACHER("教师评学", 20), PEER("同行评教", 30);

    private String name;
    private Integer value;

    QuestionnaireType(String name, Integer value) {
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
