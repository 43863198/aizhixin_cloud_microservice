package com.aizhixin.cloud.orgmanager.classschedule.msg.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="教学班消息对象")
@ToString
@NoArgsConstructor
public class TeachingClassMsgDTO {
    @ApiModelProperty(value = "学校")
    @Getter @Setter private Long orgId;
    @ApiModelProperty(value = "学期")
    @Getter @Setter private Long semesterId;
    @ApiModelProperty(value = "学期")
    @Getter @Setter private String semesterName;

    @ApiModelProperty(value = "ID")
    @Getter @Setter private Long id;
    @ApiModelProperty(value = "教学班编码")
    @Getter @Setter private String code;
    @ApiModelProperty(value = "教学班名称")
    @Getter @Setter private String name;
    @ApiModelProperty(value = "关联行政班还是直接关联学生")
    @Getter @Setter private Integer classOrStudents;

    @ApiModelProperty(value = "课程ID")
    @Getter @Setter private Long courseId;
    @ApiModelProperty(value = "课程名称")
    @Getter @Setter private String courseName;
    @ApiModelProperty(value = "课程编码")
    @Getter @Setter private String courseCode;
}
