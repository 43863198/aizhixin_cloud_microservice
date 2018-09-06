package com.aizhixin.cloud.orgmanager.classschedule.domain.excel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@ApiModel(description="选修课课程表excel信息")
@NoArgsConstructor
@ToString
public class OptionCourseScheduleExcelDomain implements Serializable {
    @ApiModelProperty(value = "state 处理中10，成功20，失败30", position=1)
    @Getter @Setter private Integer state;
    @ApiModelProperty(value = "cause 原因描述", position=2)
    @Getter @Setter private String message;
    @ApiModelProperty(value = "teachingClassExcelDomainList 教学班列表", position=3)
    @Getter @Setter private List<TeachingClassExcelDomain> teachingClassExcelDomainList;
    @ApiModelProperty(value = "teacherExcelDomainList 教学班老师列表", position=4)
    @Getter @Setter private List<TeacherExcelDomain> teacherExcelDomainList;
    @ApiModelProperty(value = "classesExcelDomainList 教学班学生列表", position=5)
    @Getter @Setter private List<StudentExcelDomain> studentExcelDomainList;
    @ApiModelProperty(value = "courseScheduleExcelDomainList 教学班课程具体安排列表", position=6)
    @Getter @Setter private List<CourseScheduleExcelDomain> courseScheduleExcelDomainList;
}
