package com.aizhixin.cloud.dd.rollcall.domain;

import com.aizhixin.cloud.dd.rollcall.entity.RollCallAppealFile;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@ToString
public class RollCallAppealDomain {
    @ApiModelProperty(value="申诉ID")
    private Long id;

    @ApiModelProperty(value="学生ID")
    private Long stuId;

    @ApiModelProperty(value="学生姓名")
    private String stuName;

    @ApiModelProperty(value="学生头像")
    private String avatar;

    @ApiModelProperty(value="教师ID")
    private Long teacherId;

    @ApiModelProperty(value="教师姓名")
    private String teacherName;

    @ApiModelProperty(value="课程ID")
    private Long courseId;

    @ApiModelProperty(value="课程名")
    private String courseName;

    @ApiModelProperty(value="班级ID")
    private Long classId;

    @ApiModelProperty(value="班级名")
    private String className;

    @ApiModelProperty(value="教学班ID")
    private Long teachingClassId;

    @ApiModelProperty(value="教学班名")
    private String teachingClassName;

    @ApiModelProperty(value="签到结果")
    private String type;

    @ApiModelProperty(value="签到时间")
    private Date signTime;

    @ApiModelProperty(value="周")
    private String weekName;

    @ApiModelProperty(value="星期")
    private Integer dayOfWeek;

    @ApiModelProperty(value="开始节")
    private Integer periodNo;

    @ApiModelProperty(value="持续节")
    private Integer periodNum;

    @ApiModelProperty(value="上课日期")
    private String teachDate;

    @ApiModelProperty(value="申诉内容")
    private String content;

    @ApiModelProperty(value="图片")
    private List<RollCallAppealFile> appealFiles;

    @ApiModelProperty(value="审批状态 10:未审批 20:通过 30:不通过")
    private Integer appealStatus;

    @ApiModelProperty(value="审批日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date appealDate;

    @ApiModelProperty(value="创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdDate;

    public RollCallAppealDomain() {
    }

    public RollCallAppealDomain(Long id, Long stuId, Long teacherId, String teacherName, Long courseId, String courseName, Long classId, String className, Long teachingClassId, String teachingClassName, String type, Date signTime, String weekName, Integer dayOfWeek, Integer periodNo, Integer periodNum, String teachDate, String content, List<RollCallAppealFile> appealFiles, Integer appealStatus, Date appealDate, Date createdDate) {
        this.id = id;
        this.stuId = stuId;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.courseId = courseId;
        this.courseName = courseName;
        this.classId = classId;
        this.className = className;
        this.teachingClassId = teachingClassId;
        this.teachingClassName = teachingClassName;
        this.type = type;
        this.signTime = signTime;
        this.weekName = weekName;
        this.dayOfWeek = dayOfWeek;
        this.periodNo = periodNo;
        this.periodNum = periodNum;
        this.teachDate = teachDate;
        this.content = content;
        this.appealFiles = appealFiles;
        this.appealStatus = appealStatus;
        this.appealDate = appealDate;
        this.createdDate = createdDate;
    }
}
