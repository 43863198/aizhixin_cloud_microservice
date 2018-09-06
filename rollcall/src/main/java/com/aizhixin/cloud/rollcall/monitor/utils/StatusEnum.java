package com.aizhixin.cloud.rollcall.monitor.utils;

import lombok.Getter;
import lombok.Setter;

/**
 * @author LIMH
 * @date 2017/12/11
 */

public enum StatusEnum {
    /**
     * 标志
     */
    Success(1, "成功"), Fail(0, "失败");
    @Setter
    @Getter
    private int status;
    @Setter
    @Getter
    private String name;

    StatusEnum(int status, String name) {
        this.status = status;
        this.name = name;
    }
}
