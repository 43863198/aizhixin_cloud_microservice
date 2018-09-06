package com.aizhixin.cloud.rollcall.monitor.utils;

import lombok.Getter;
import lombok.Setter;

/**
 * @author LIMH
 * @date 2017/12/11
 */
public enum MonitorTypeEnum {
    /**
     * 监控类别
     */
    Daybreak("daybreak", "凌晨"), BeforeClass("beforeclass", "课前"), OutClass("outclass", "课后");

    @Setter
    @Getter
    private String type;
    @Setter
    @Getter
    private String name;

    MonitorTypeEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }
}
