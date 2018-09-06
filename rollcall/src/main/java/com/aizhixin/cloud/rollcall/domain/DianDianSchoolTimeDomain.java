package com.aizhixin.cloud.rollcall.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description = "点点学校的天课表")
@NoArgsConstructor
@ToString
public class DianDianSchoolTimeDomain {
    @ApiModelProperty(value = "教学班ID")
    @Getter @Setter private Long teachingClassId;
    @ApiModelProperty(value = "教学班名称")
    @Getter @Setter private String teachingClassName;
    @ApiModelProperty(value = "教学班编号")
    @Getter @Setter private String teachingClassCode;
    @ApiModelProperty(value = "课程ID")
    @Getter @Setter private Long courseId;
    @ApiModelProperty(value = "课程名称")
    @Getter @Setter private String courseName;
    @ApiModelProperty(value = "教师列表: 格式[id,name;*]")
    @Getter @Setter private String teachers;
    @ApiModelProperty(value = "学期ID")
    @Getter @Setter private Long semesterId;
    @ApiModelProperty(value = "学期名称")
    @Getter @Setter private String semesterName;
    @ApiModelProperty(value = "班级关联(10)，还是学生关联(20)")
    @Getter @Setter private Integer classOrStudents;
    @ApiModelProperty(value = "第几周ID")
    @Getter @Setter private Long weekId;
    @ApiModelProperty(value = "第几周名称")
    @Getter @Setter private String weekName;
    @ApiModelProperty(value = "第几周数字")
    @Getter @Setter private Integer weekNo;
    @ApiModelProperty(value = "周几")
    @Getter @Setter private Integer dayOfWeek;
    @ApiModelProperty(value = "从第几节开始ID")
    @Getter @Setter private Long periodId;
    @ApiModelProperty(value = "从第几节开始，序号")
    @Getter @Setter private Integer periodNo;
    @ApiModelProperty(value = "总共持续几个小节")
    @Getter @Setter private Integer periodNum;
    @ApiModelProperty(value = "教室")
    @Getter @Setter private String classroom;
}
