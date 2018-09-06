/**
 *
 */
package com.aizhixin.cloud.paycallback.core;

import lombok.Getter;

/**
 * 订单类型(10付款，20退款)
 *
 * @author zhen.pan
 */
public enum OrderType {
    PAYMENT(10),//付款
    PAYBACK(20);//退款

    @Getter
    private Integer state;
    @Getter
    private String stateDesc;

    OrderType(Integer state) {
        this.state = state;
        switch (state) {
            case 10:
                this.stateDesc = "付款";
                break;
            default:
                this.state = 20;
                this.stateDesc = "退款";
        }
    }
}