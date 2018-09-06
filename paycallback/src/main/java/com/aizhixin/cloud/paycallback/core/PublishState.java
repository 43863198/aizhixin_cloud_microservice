/**
 *
 */
package com.aizhixin.cloud.paycallback.core;

import lombok.Getter;

/**
 * 缴费科目发布状态(10待发布，20已发布，30已取消, 70已过期)
 *
 * @author zhen.pan
 */
public enum PublishState {
    INIT(10),//待发布
    PUBLISH(20),//已发布
    CANCEL(30),//已取消
    EXPRIRED(70);//已过期

    @Getter
    private Integer state;
    @Getter
    private String stateDesc;

    PublishState(Integer state) {
        this.state = state;
        switch (state) {
            case 10:
                this.stateDesc = "待发布";
                break;
            case 20:
                this.stateDesc = "已发布";
                break;
            case 30:
                this.stateDesc = "已取消";
                break;
            default:
                this.state = 70;
                this.stateDesc = "已过期";
        }
    }
}