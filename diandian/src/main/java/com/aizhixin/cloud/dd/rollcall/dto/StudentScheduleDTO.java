package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel(description = "学生课程列表")
@Data
public class StudentScheduleDTO {

    private Long id = 0L;
    private String weekName;
    private String dayOfWeek;
    private String courseName;
    private String teacher;
    private String avatar;
    private String classRoom;
    private String classBeginTime;
    private String classEndTime;
    private String whichLesson;
    private Long scheduleId = 0L;
    private Long scheduleRollCallId = 0l;
    private Long teachingClassId;
    private String teachingClassName;
    private String teach_time;
    private String type;
    private Boolean haveReport = true;
    private String rollcallType;
    private String signTime = "";
    private Integer lateTime = 0;
    private Integer absenteeismTime = 0;
    private Boolean canReport = true;
    private String address = "";
    private Integer lessonOrderNum = 0;
    private String assessScore = "0";
    private Boolean rollCall = false;
    private String localtion = "";
    private Boolean inClass = false;
    private Integer deviation = 0;
    private Boolean inAssess = false;

}
