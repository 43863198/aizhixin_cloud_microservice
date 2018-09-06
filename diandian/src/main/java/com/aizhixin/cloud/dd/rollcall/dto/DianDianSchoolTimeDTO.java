package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by zhen.pan on 2017/5/10.
 */
@Data
@ApiModel(description="点点教学班对应的课表")
public class DianDianSchoolTimeDTO {
    @ApiModelProperty(value = "教学班ID")
    private Long teachingClassId;
    @ApiModelProperty(value = "教学班名称")
    private String teachingClassName;
    @ApiModelProperty(value = "课程ID")
    private Long courseId;
    @ApiModelProperty(value = "课程名称")
    private String courseName;
    @ApiModelProperty(value = "教师列表: 格式[id,name;*]")
    private String teachers;
    @ApiModelProperty(value = "学期ID")
    private Long semesterId;
    @ApiModelProperty(value = "学期名称")
    private String semesterName;
    @ApiModelProperty(value = "班级关联(10)，还是学生关联(20)")
    private Integer classOrStudents;
    @ApiModelProperty(value = "第几周ID")
    private Long weekId;
    @ApiModelProperty(value = "第几周名称")
    private String weekName;
    @ApiModelProperty(value = "第几周数字")
    private Integer weekNo;
    @ApiModelProperty(value = "周几")
    private Integer dayOfWeek;
    @ApiModelProperty(value = "从第几节开始ID")
    private Long periodId;
    @ApiModelProperty(value = "从第几节开始，序号")
    private Integer periodNo;
    @ApiModelProperty(value = "总共持续几个小节")
    private Integer periodNum;
    @ApiModelProperty(value = "教室")
    private String classroom;

    public DianDianSchoolTimeDTO() {}

    public DianDianSchoolTimeDTO(Long teachingClassId, Long periodId, Integer periodNo, Integer periodNum, String classroom) {
        this.teachingClassId = teachingClassId;
        this.periodId = periodId;
        this.periodNo = periodNo;
        this.periodNum = periodNum;
        this.classroom = classroom;
    }
}
