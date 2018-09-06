package com.aizhixin.cloud.token.login.common;

import lombok.Getter;
import org.omg.CORBA.UNKNOWN;

/**
 * Created by wu on 2017/7/20.
 */
public enum StatusCodeEnum {
    UNKNOWN_SCAN(100),//未知的二维码扫描
    SCAN_SUCCESS(200),//扫码成功
    OVER_DUE(300),//二维码过期
    UNAUTHORIZED(401),//未授权
    LOGIN_SUCCESS(600),//登录成功
    LOGIN_FAIL(700),//登录失败
    POST_FAIL(20000),//数据向sokcetf服务发送失败
    POST_SUCCES(10000);//数据向sokcetf服务发送成功
    @Getter
    private Integer state;
    @Getter
    private String stateDesc;

    StatusCodeEnum(Integer state) {
        this.state = state;
        switch (state) {
            case 100:
                this.stateDesc = "未知的二维码扫描";
                break;
            case 200:
                this.stateDesc = "扫码成功";
                break;
            case 300:
                this.stateDesc = "二维码过期";
                break;
            case 401:
                this.stateDesc = "未授权";
                break;
            case 1000:
                this.stateDesc = "数据向sokcetf服务发送成功";
                break;
            case 2000:
                this.stateDesc = "数据向sokcetf服务发送失败";
                break;
            case 600:
                this.stateDesc = "登录成功";
                break;
            case 700:
                this.stateDesc = "登录失败";
                break;
            default:
               break;
        }
    }

}
