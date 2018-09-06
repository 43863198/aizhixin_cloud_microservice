package com.aizhixin.cloud.orgmanager.classschedule.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@ApiModel(description="临时调停课基本信息")
@NoArgsConstructor
@ToString
public class TempAdjustCourseListDomain {
    @ApiModelProperty(value = "临时调课ID", position=1)
    @Getter @Setter private Long id;
    @ApiModelProperty(value = "教师工号", position=2)
    @Getter @Setter private String teacherJobNumber;
    @ApiModelProperty(value = "教师姓名", position=3)
    @Getter @Setter private String teacherName;
    @ApiModelProperty(value = "操作日期", position=4)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Getter @Setter private Date createDate;
    @ApiModelProperty(value = "教学班ID", position=5)
    @Getter @Setter private Long teachingClassId;
    @ApiModelProperty(value = "教学班编码", position=6)
    @Getter @Setter private String teachingClassCode;
    @ApiModelProperty(value = "教学班名称", position=7)
    @Getter @Setter private String teachingClassName;
    @ApiModelProperty(value = "调课类型:10增加,20停止,30调整", position=8)
    @Getter @Setter private Integer adjustType;
    @ApiModelProperty(value = "源停（加）课日期(yyyy-MM-dd)", position=9)
    @Getter @Setter private String eventDate;
    @ApiModelProperty(value = "源第几节", position=10)
    @Getter @Setter private Integer periodNo;
    @ApiModelProperty(value = "源持续节", position=11)
    @Getter @Setter private Integer periodNum;
    @ApiModelProperty(value = "源教室", position=12)
    @Getter @Setter private String classroom;
//    @ApiModelProperty(value = "目标加课日期(yyyy-MM-dd)", position=13)
//    @Getter @Setter private String destEventDate;
//    @ApiModelProperty(value = "目标第几节", position=14)
//    @Getter @Setter private Integer destPeriodNo;
//    @ApiModelProperty(value = "目标持续节", position=15)
//    @Getter @Setter private Integer destPeriodNum;
//    @ApiModelProperty(value = "目标教室", position=16)
//    @Getter @Setter private String destClassroom;

    public TempAdjustCourseListDomain (Long id, String teacherJobNumber, String teacherName, Long teachingClassId, String teachingClassCode, String teachingClassName, Integer adjustType, Date createDate, String eventDate, Integer periodNo, Integer periodNum, String classroom) {
        this.id = id;
        this.teacherJobNumber = teacherJobNumber;
        this.teacherName = teacherName;
        this.teachingClassId = teachingClassId;
        this.teachingClassCode = teachingClassCode;
        this.teachingClassName = teachingClassName;
        this.adjustType = adjustType;
        this.createDate = createDate;
        this.eventDate = eventDate;
        this.periodNo = periodNo;
        this.periodNum = periodNum;
        this.classroom = classroom;
//        this.destEventDate = destEventDate;
//        this.destPeriodNo = destPeriodNo;
//        this.destPeriodNum = destPeriodNum;
//        this.destClassroom = destClassroom;
    }
}
