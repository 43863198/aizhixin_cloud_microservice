/**
 *
 */
package com.aizhixin.cloud.paycallback.core;

import lombok.Getter;

/**
 * 分期额度(10首次，20每次)
 *
 * @author zhen.pan
 */
public enum InstallmentAmount {
    FIRST(10),//首次
    EVERY(20);//每次

    @Getter
    private Integer state;
    @Getter
    private String stateDesc;

    InstallmentAmount(Integer state) {
        this.state = state;
        switch (state) {
            case 10:
                this.stateDesc = "首次";
                break;
            default:
                this.state = 20;
                this.stateDesc = "每次";
        }
    }
}