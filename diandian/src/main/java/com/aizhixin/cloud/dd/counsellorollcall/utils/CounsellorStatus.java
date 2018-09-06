package com.aizhixin.cloud.dd.counsellorollcall.utils;

/**
 * @author LIMH
 * @date 2017/12/20
 */
public enum CounsellorStatus {
    //
    UnOpenCounsellorRollcall("0", "未点名"), OpenCounsellorRollcall("1", "已点名");

    String type;
    String name;

    CounsellorStatus(String type, String name) {
        this.type = type;
        this.name = name;
    }

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
}
