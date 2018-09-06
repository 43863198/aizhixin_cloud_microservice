package com.aizhixin.cloud.orgmanager.classschedule.msg.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="教学班老师消息对象")
@ToString
@NoArgsConstructor
public class TeachingClassTeacherMsgDTO {
    @ApiModelProperty(value = "学校ID")
    @Getter @Setter private Long orgId;
    @ApiModelProperty(value = "教学班Id")
    @Getter @Setter private Long teachingClassId;
    @ApiModelProperty(value = "教师ID")
    @Getter @Setter private Long teacherId;
    @ApiModelProperty(value = "教师名称")
    @Getter @Setter private String teacherName;
    @ApiModelProperty(value = "教师工号")
    @Getter @Setter private String teacherJobNumber;
}
