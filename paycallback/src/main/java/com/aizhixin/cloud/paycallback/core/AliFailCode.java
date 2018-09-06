/**
 *
 */
package com.aizhixin.cloud.paycallback.core;

import lombok.Getter;

/**
 * 支付宝学生缴费大厅:0正常 -1通用异常 -2参数异常
 *
 * @author zhen.pan
 */
public enum AliFailCode {
    NORMAL(0),//正常
    OTHER_FAIL(-1),//通用异常
    PARAM_FAIL(-2);//参数异常

    @Getter
    private Integer state;
    @Getter
    private String stateDesc;

    AliFailCode(Integer state) {
        this.state = state;
        switch (state) {
            case 0:
                this.stateDesc = "0";//正常
                break;
            case -1:
                this.stateDesc = "-1";//通用异常
                break;
            default:
                this.state = -2;
                this.stateDesc = "-2";//参数异常
        }
    }
}