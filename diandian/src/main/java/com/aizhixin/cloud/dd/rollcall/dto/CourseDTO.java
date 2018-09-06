package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(description = "课表中课程")
@Data
@EqualsAndHashCode(callSuper = false)
public class CourseDTO {

    private Long id = 0L;

    private String dayOfWeek = "";

    private String classRoom = "";

    private String courseName = "";

    private Long periodId;
    private Integer periodNo;
    private Integer periodNum;
    private String classBeginTime = "";

    private String classEndTime = "";

    private String teach_time = "";

    private String weekName = "";

    private String whichLesson = "";
    private int lessonOrderNum;

    private String teacher = "";

    private Long teacherId = 0L;
    private Long studentId = 0L;
    private Long studentScheduleId = 0L;

    private Long classId = 0L;
    private Long semesterId = 0L;
    private Long courseId = 0L;
    private String classNames;
    private Long teachingClassId = 0L;
    private String teachingClassCode;
    /**
     * 该节课采用的点名方式
     */
    private String rollcallType = "";

    private String periodType = "";

    private boolean isRollCall = false;

    private String isAutomatic = "";

    private Boolean canReport = true;

    private String localtion = "";

    private String rollCallEndTime = "";

    private String type = "";

    private Boolean haveReport = false;

    /**
     * 是否开启点名
     */
    private String isOpen = "";

    /**
     * 迟到时间
     */
    private int lateTime = 0;

    private int reuser = 0;
    /**
     * 基础点名方式，点名设置
     */
    private String rollCallTypeOrigin = "";

    private int calCount = 0;

    private int deviation = 0;

    private int confiLevel = 0;

    private String signTime = "";

    private Integer course_later_time = -1;

    private String address = "";

    /**
     * 是否可以认领
     */
    private Boolean isCanClaim = false;

}
