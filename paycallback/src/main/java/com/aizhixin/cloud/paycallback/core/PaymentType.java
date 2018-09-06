/**
 *
 */
package com.aizhixin.cloud.paycallback.core;

import lombok.Getter;

/**
 * 缴费方式(10全款，20分期)
 *
 * @author zhen.pan
 */
public enum PaymentType {
    ALL(10),//全款
    INSTALLMENT(20);//分期

    @Getter
    private Integer state;
    @Getter
    private String stateDesc;

    PaymentType(Integer state) {
        this.state = state;
        switch (state) {
            case 10:
                this.stateDesc = "全款";
                break;
            default:
                this.state = 20;
                this.stateDesc = "分期";
        }
    }
}