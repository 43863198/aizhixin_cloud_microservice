package com.aizhixin.cloud.dd.counsellorollcall.utils;

/**
 * Created by LIMH on 2017/12/1.
 */
public enum CounsellorRollCallEnum {
    // 导员点名学生状态
    UnCommit("未提交", "10"), HavaTo("已到", "20"), NonArrival("未到", "30"), AskForLeave("请假", "40"), Late("迟到","50");

    private String type;
    private String name;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    CounsellorRollCallEnum(String name, String type) {
        this.type = type;
        this.name = name;
    }

    public static String getNameByType(String type) {
        CounsellorRollCallEnum[] values = CounsellorRollCallEnum.values();
        if (values != null && values.length > 0) {
            for (CounsellorRollCallEnum value : values) {
                if (value.getType().equals(type)) {
                    return value.getName();
                }
            }
        }
        return null;
    }
}
