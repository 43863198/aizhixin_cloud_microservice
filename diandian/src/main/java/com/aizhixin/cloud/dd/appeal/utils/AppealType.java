package com.aizhixin.cloud.dd.appeal.utils;

public enum AppealType {

    COUNSELLOR("点名申诉", 10);

    private String name;
    private int type;

    AppealType(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
