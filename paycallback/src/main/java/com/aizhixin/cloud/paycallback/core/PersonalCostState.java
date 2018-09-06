/**
 *
 */
package com.aizhixin.cloud.paycallback.core;

import lombok.Getter;

/**
 * 人员费用状态(10正常，20自愿放弃)
 *
 * @author zhen.pan
 */
public enum PersonalCostState {
    NORMAL(10),//正常
    CANCEL(20);//自愿放弃

    @Getter
    private Integer state;
    @Getter
    private String stateDesc;

    PersonalCostState(Integer state) {
        this.state = state;
        switch (state) {
            case 10:
                this.stateDesc = "正常";
                break;
            default:
                this.state = 20;
                this.stateDesc = "自愿放弃";
        }
    }
}