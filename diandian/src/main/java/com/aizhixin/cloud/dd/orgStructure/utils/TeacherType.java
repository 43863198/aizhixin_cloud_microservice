package com.aizhixin.cloud.dd.orgStructure.utils;

public enum TeacherType {
    NO_TEACHING_TEACHER("非授课老师", 10), TEACHING_TEACHER("授课老师", 20);

    private String name;
    private Integer value;

    TeacherType(String name, Integer value) {
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
