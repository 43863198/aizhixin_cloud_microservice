package com.aizhixin.cloud.dd.monitor.utils;

/**
 * @author LIMH
 * @date 2017/12/20
 */
public enum ScheduelStatusEnum {
    //
    ScheduleStatusOpen("10", "打卡机开启"), ScheduleStatuClose("20", "打开机关闭");

    String status;
    String name;

    ScheduelStatusEnum(String status, String name) {
        this.status = status;
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
