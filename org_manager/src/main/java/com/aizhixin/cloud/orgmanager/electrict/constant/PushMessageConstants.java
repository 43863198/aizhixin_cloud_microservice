package com.aizhixin.cloud.orgmanager.electrict.constant;

import com.aizhixin.cloud.orgmanager.electrict.constant.BaseConstants;

/*
 * 推送消息
 */
public interface PushMessageConstants extends BaseConstants {

    /**
     * 请假模块
     */
    String MODULE_LEAVE                  = "leave";
    /**
     * 点名模块
     */
    String MODULE_ROLLCALL               = "rollcall";

    /**
    * 辅导员随时点模块
    */
    String MODULE_RollCallEVER           = "rollcallever";

    /**
     * 问卷模块
     */
    String MODULE_QUESTIONNAIRE          = "questionnaire";
    /**
     * 电子围栏模块
     */
    String MODULE_ELECTRICFENCE          = "electricFence";
    /**
     * 点名模块 学生提醒功能
     */
    String FUNCTION_STUDENT_REMIND       = "student_remind";

    String FUNCITON_STUDENT_NOTICE       = "rollCallEver_student_notice";

    String FUNCITON_QUESTIONNAIRE_NOTICE = "que_student_notice";
    /**
     * 请假模块 老师通知
     */
    String FUNCTION_TEACHER_NOTICE       = "teacher_notice";
    /**
     * 请假模块 审批人 审批提醒
     */
    String FUNCTION_TEACHER_APPROVAL     = "teacher_approval";
    /**
     * 请假模块 审批结果通知
     */
    String FUNCTION_STUDENT_NOTICE       = "student_notice";

    /**
     * 离校学生通知班主任
     */
    String FUNCTION_LEAVETEACHER_NOTICE  = "leaveTeacher_notice";

}
