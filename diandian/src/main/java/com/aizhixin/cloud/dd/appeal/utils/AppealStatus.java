package com.aizhixin.cloud.dd.appeal.utils;

public enum AppealStatus {
    PENDING("待处理", 10), PASS("通过", 20), UNPASS("未通过", 30);

    private String name;
    private int status;

    AppealStatus(String name, int status) {
        this.name = name;
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }
}
