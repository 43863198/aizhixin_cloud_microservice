package com.aizhixin.cloud.dd.constant;

import io.swagger.models.auth.In;

public interface LeaveConstants {

    String STATUS_PROCESSED = "processed";
    /**
     * 状态：申请
     */
    String STATUS_REQUEST = "request";
    /**
     * 状态：驳回
     */
    String STATUS_REJECT = "reject";
    /**
     * 状态：删除
     */
    String STATUS_DELETE = "delete";
    /**
     * 状态：通过
     */
    String STATUS_PASS = "pass";
    /**
     * 状态：撤回
     */
    String STATUS_CANCLE = "cancle";
    /**
     * 类型：课程节
     */
    String TYPE_PERIOD = "period";
    /**
     * 类型：天
     */
    String TYPE_DAY = "day";

    /**
     * 类型：公假
     */
    Integer TYPE_PU = 1;
    Integer TYPE_PU_SX = 10;
    Integer TYPE_PU_ZS = 20;
    Integer TYPE_PU_GS = 30;
    /**
     * 类型：私假
     */
    Integer TYPE_PR = 0;
    Integer TYPE_PR_BJ = 40;
    Integer TYPE_PR_SJ = 50;

}
