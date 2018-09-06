package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

@ApiModel(description = "教师端课程详情信息")
@Data
@EqualsAndHashCode(callSuper = false)
public class CourseInforDTO {

    private Long id;

    private String dayOfWeek;

    private String classRoom;

    private String courseName;

    private String classBeginTime;

    private String classEndTime;

    private String teach_time;

    private String weekName;

    private String whichLesson;

    private Integer assessPeopelNum;

    private Integer totalPeopelNum;

    private Boolean isRollCall;
    private Boolean rollCall = false;

    private Integer lateTime;

    private Long teachingClassId;

    private Long periodId;

    private Integer lessonOrderNum;

    private String periodType;

    private List classInfor = new ArrayList();

    private String classNames;

}
