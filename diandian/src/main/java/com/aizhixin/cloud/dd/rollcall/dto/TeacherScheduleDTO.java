package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel(description = "教师课程列表")
@Data
public class TeacherScheduleDTO {

    /**
     * 排课ID
     */
    private Long id;
    private String weekName;
    private String dayOfWeek;
    private Long coureseId;
    private String courseName;
    private String teacher;
    private String classRoom;
    private String classBeginTime;
    private String classEndTime;
    private String whichLesson;

    private String teach_time;
    private String rollcallType;
    private Boolean rollCall;

    /**
     * 迟到时间
     */
    private Integer lateTime = 0;
    /**
     * 旷课时间
     */
    private Integer absenteeismTime = 0;
    /**
     * 考勤率
     */
    private String attendance = "0";

    /**
     * 是否在课堂内
     */
    private Boolean classrommRollCall = false;

    private Integer lessonOrderNum = 0;

    private String classNames;

    /**
     * 学生数
     */
    private Long totalStu = 0L;

    /**
     * 签到学生数
     */
    private Integer rollcallStu = 0;
}
