package com.aizhixin.cloud.dd.counsellorollcall.utils;

public enum CounsellorRollCallType {

    MorningExercise("早操", 20), SelfStudy("晚自习", 20), Check("晚查寝", 30), Internship("外出实习", 40), Other("其它", 50);

    private Integer type;
    private String name;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    CounsellorRollCallType(String name, Integer type) {
        this.type = type;
        this.name = name;
    }

    public static String getNameByType(String type) {
        CounsellorRollCallType[] values = CounsellorRollCallType.values();
        if (values != null && values.length > 0) {
            for (CounsellorRollCallType value : values) {
                if (value.getType().equals(type)) {
                    return value.getName();
                }
            }
        }
        return null;
    }
}
