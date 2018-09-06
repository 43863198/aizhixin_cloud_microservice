package com.aizhixin.cloud.dd.approve.domain;

import lombok.Data;

@Data
public class AdjustCourseScheduleRecordDomain {
    /**
     * 调课类型
     */
    private String type;
    /**
     * 原始上课时间
     */
    private String agoAttendClassTime;
    /**
     * 原始上课地址
     */
    private String agoAttendClassAddress;
    /**
     * 新的上课时间
     */
    private String newAttendClassTime;
    /**
     * 新的上课地址
     */
    private String newAttendClassAddress;

    /**
     * 教学班id
     */
    private Long teachingClassId;
}
