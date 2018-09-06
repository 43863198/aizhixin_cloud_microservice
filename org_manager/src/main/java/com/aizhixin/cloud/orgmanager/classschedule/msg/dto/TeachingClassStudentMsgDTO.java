package com.aizhixin.cloud.orgmanager.classschedule.msg.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="教学班学生消息对象")
@ToString
@NoArgsConstructor
public class TeachingClassStudentMsgDTO {
    @ApiModelProperty(value = "学校ID")
    @Getter @Setter private Long orgId;
    @ApiModelProperty(value = "教学班Id")
    @Getter @Setter private Long teachingClassId;
    @ApiModelProperty(value = "学生ID")
    @Getter @Setter private Long studentId;
    @ApiModelProperty(value = "学生名称")
    @Getter @Setter private String studentName;
    @ApiModelProperty(value = "学生学号")
    @Getter @Setter private String studentJobNumber;
    @ApiModelProperty(value = "性别")
    @Getter @Setter private String sex;
    @ApiModelProperty(value = "行政班id")
    @Getter @Setter private Long classesId;
    @ApiModelProperty(value = "行政班名称")
    @Getter @Setter private String classesName;
    @ApiModelProperty(value = "学院id")
    @Getter @Setter private Long collegeId;
    @ApiModelProperty(value = "学院名称")
    @Getter @Setter private String collegeName;
    @ApiModelProperty(value = "专业id")
    @Getter @Setter private Long profId;
    @ApiModelProperty(value = "专业名称")
    @Getter @Setter private String profName;

}
