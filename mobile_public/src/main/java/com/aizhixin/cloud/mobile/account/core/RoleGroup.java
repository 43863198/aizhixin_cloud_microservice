/**
 *
 */
package com.aizhixin.cloud.mobile.account.core;

import lombok.Getter;

/**
 * 角色组(10 M，20 B，30 C，40 COM)
 *
 * @author zhen.pan
 */
public enum RoleGroup {
    M(10),//管理端
    B(20),//B端
    C(30),//C端
    COM(40);//企业端

    @Getter
    private Integer state;
    @Getter
    private String stateDesc;

    RoleGroup(Integer state) {
        this.state = state;
        switch (state) {
            case 10:
                this.stateDesc = "M";
                break;
            case 20:
                this.stateDesc = "B";
                break;
            case 30:
                this.stateDesc = "C";
                break;
            default:
                this.state = 40;
                this.stateDesc = "COM";
        }
    }
}