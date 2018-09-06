package com.aizhixin.cloud.dd.rollcall.dto;

import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by zhen.pan on 2017/5/10.
 */
@Data
@ApiModel(description = "点点学生老师周课表")
public class DianDianWeekSchoolTimeTableDomain {
    @ApiModelProperty(value = "教学班ID")
    private Long teachingClassId;
    @ApiModelProperty(value = "教学班名称")
    private String teachingClassName;
    private String teachingClassCode;
    @ApiModelProperty(value = "课程名称")
    private String courseName;
    @ApiModelProperty(value = "第几周ID")
    private Long weekId;
    @ApiModelProperty(value = "周几")
    private Integer dayOfWeek;
    @ApiModelProperty(value = "从第几节开始ID")
    private Long periodId;
    @ApiModelProperty(value = "从第几节开始，序号")
    private Integer periodNo;
    @ApiModelProperty(value = "总共持续几个小节")
    private Integer periodNum;
    @ApiModelProperty(value = "开始时间")
    private String periodStarttime;
    @ApiModelProperty(value = "结束时间")
    private String periodEndtime;
    @ApiModelProperty(value = "上课日期")
    private Date teachDate;
    @ApiModelProperty(value = "教室")
    private String classroom;
    @ApiModelProperty(value = "教师ID")
    private Long teacherId;
    @ApiModelProperty(value = "教师名称")
    private String teacherName;

    @ApiModelProperty(value = "老师列表")
    private List<IdNameDomain> teachers;

    public DianDianWeekSchoolTimeTableDomain() {}

    public DianDianWeekSchoolTimeTableDomain(Long teachingClassId, Long weekId, Long periodId, Integer dayOfWeek, Integer periodNum, String classroom) {
        this.teachingClassId = teachingClassId;
        this.weekId = weekId;
        this.periodId = periodId;
        this.dayOfWeek = dayOfWeek;
        this.periodNum = periodNum;
        this.classroom = classroom;
    }
}
