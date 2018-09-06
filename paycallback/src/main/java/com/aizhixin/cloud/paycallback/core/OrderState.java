/**
 *
 */
package com.aizhixin.cloud.paycallback.core;

import lombok.Getter;

/**
 * 订单状态(10初始(待付款/待退款)，20已付款/已退款, 30已超时/已取消, 40已完成)
 *
 * @author zhen.pan
 */
public enum OrderState {
    INIT(10),//初始(待付款/待退款)
    EXCUTED(20),//已付款/已退款
    CANCEL(30),//已超时/已取消
    COMPLETE(40);//已完成

    @Getter
    private Integer state;
    @Getter
    private String stateDesc;

    OrderState(Integer state) {
        this.state = state;
        switch (state) {
            case 10:
                this.stateDesc = "初始化";
                break;
            case 20:
                this.stateDesc = "已付款";
                break;
            case 30:
                this.stateDesc = "已超时";
                break;
            default:
                this.state = 40;
                this.stateDesc = "已完成";
        }
    }
}