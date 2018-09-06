/**
 *
 */
package com.aizhixin.cloud.paycallback.core;

import lombok.Getter;

/**
 * 缴费状态(10未缴费，20已欠费, 30已结清, 70已过期)
 *
 * @author zhen.pan
 */
public enum PaymentState {
    NOPAY(10),//未缴费
    OWED(20),//已欠费
    COMPLETE(30);//已结清

    @Getter
    private Integer state;
    @Getter
    private String stateDesc;

    PaymentState(Integer state) {
        this.state = state;
        switch (state) {
            case 10:
                this.stateDesc = "未缴费";
                break;
            case 20:
                this.stateDesc = "已欠费";
                break;
            default:
                this.state = 30;
                this.stateDesc = "已结清";
        }
    }
}