package com.aizhixin.cloud.orgmanager.classschedule.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by zhen.pan on 2017/6/27.
 */
@ApiModel(description="教学班排课信息")
@ToString
public class SchoolTimeTableDomain {
    @ApiModelProperty(value = "教学班名称")
    @Getter @Setter private String teachingClassName;
    @ApiModelProperty(value = "选课课号(教学班编码)")
    @Getter @Setter private String teachingClassCode;
    @ApiModelProperty(value = "选课人数")
    @Getter @Setter private Long studentsNum;
    @ApiModelProperty(value = "选修还是必修")
    @Getter @Setter private String optionOrMust;
    @ApiModelProperty(value = "星期几上课（周日1，周六7）")
    @Getter @Setter private Integer dayOfWeek;
    @ApiModelProperty(value = "从第几节开始，序号")
    @Getter @Setter private Integer periodMo;
    @ApiModelProperty(value = "总共持续几个小节")
    @Getter @Setter private Integer periodNum;
    @ApiModelProperty(value = "起始周，第几周序号")
    @Getter @Setter private Integer startWeekNo;
    @ApiModelProperty(value = "结束周，第几周序号")
    @Getter @Setter private Integer endWeekNo;
    @ApiModelProperty(value = "单周或双周(可以填写汉字单双或者空着:[单][双])")
    @Getter @Setter private String singleOrDouble;
    @ApiModelProperty(value = "教室")
    @Getter @Setter private String classroom;
    @ApiModelProperty(value = "上课班级编码（多个班级之间使用英文逗号分隔）")
    @Getter @Setter private String classesCodes;
    @ApiModelProperty(value = "上课班级名称（多个班级之间使用英文逗号分隔）")
    @Getter @Setter private String classesNames;
    @ApiModelProperty(value = "上课教师工号")
    @Getter @Setter private String teacherJobNumber;
    @ApiModelProperty(value = "上课教师名称")
    @Getter @Setter private String teacherName;
    @ApiModelProperty(value = "开课学院编码")
    @Getter @Setter private String collegeCode;
    @ApiModelProperty(value = "开课学院名称")
    @Getter @Setter private String collegeName;
    @ApiModelProperty(value = "学期编码")
    @Getter @Setter private String semesterCode;
    @ApiModelProperty(value = "学期名称")
    @Getter @Setter private String semesterName;
    @ApiModelProperty(value = "学年")
    @Getter @Setter private String year;
    @ApiModelProperty(value = "课程编码")
    @Getter @Setter private String courseCode;
    @ApiModelProperty(value = "课程名称")
    @Getter @Setter private String courseName;
    @ApiModelProperty(value = "考核方式(考试、考察)")
    @Getter @Setter private String checkType;
    @ApiModelProperty(value = "学分")
    @Getter @Setter private Float credit;
    @ApiModelProperty(value = "总学时")
    @Getter @Setter private Integer totalStudyTime;
    @ApiModelProperty(value = "上课学时")
    @Getter @Setter private Integer courseTime;
    @ApiModelProperty(value = "实验学时")
    @Getter @Setter private Integer experimentTime;
    @ApiModelProperty(value = "上机学时")
    @Getter @Setter private Integer operatorTime;
    @ApiModelProperty(value = "组织机构ID")
    @Getter @Setter private Long orgId;
    @ApiModelProperty(value = "操作人")
    @Getter @Setter private Long userId;
}
