/**
 *
 */
package com.aizhixin.cloud.orgmanager.company.core;

import lombok.Getter;

/**
 * 用户类型(10学校管理员，20学院管理员，40班级管理员，60老师，70学生)
 *
 * @author zhen.pan
 */
public enum RollCallType {
    PASS(10),//暂停考勤
    NORMAL(20);//正常考勤

    @Getter
    private Integer state;
    @Getter
    private String stateDesc;

    RollCallType(Integer state) {
        this.state = state;
        switch (state) {
            case 10:
                this.stateDesc = "暂停考勤";
                break;
            default:
                this.state = 20;
                this.stateDesc = "正常考勤";
        }
    }
}