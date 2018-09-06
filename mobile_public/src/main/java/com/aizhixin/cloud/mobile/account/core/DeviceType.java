/**
 *
 */
package com.aizhixin.cloud.mobile.account.core;

import lombok.Getter;

/**
 * 设备类型(10 IOS老师，20 IOS学生，30 Android老师，40 Android学生)
 *
 * @author zhen.pan
 */
public enum DeviceType {
    IOS_TEACHER(10),//IOS老师
    IOS_STUDENT(20),//IOS学生
    ANDROID_TEACHER(30),//Android老师
    ANDROID_STUDENT(40);//Android学生

    @Getter
    private Integer state;
    @Getter
    private String stateDesc;

    DeviceType(Integer state) {
        this.state = state;
        switch (state) {
            case 10:
                this.stateDesc = "IOS老师";
                break;
            case 20:
                this.stateDesc = "IOS学生";
                break;
            case 30:
                this.stateDesc = "Android老师";
                break;
            default:
                this.state = 40;
                this.stateDesc = "Android学生";
        }
    }
}