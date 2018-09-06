package com.aizhixin.cloud.dd.rollcall.dto;


import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@ApiModel(description = "个人中心学生端考勤汇总")
@EqualsAndHashCode(callSuper = false)
public class StudentAttendanceGatherDTO {
    String personId;
    String studentName;
    Long courseId;
    String className;
    String courseName;
    String teacherName;
    int normal = 0;
    int askforlevae = 0;
    int leave = 0;
    int truancy = 0;
    int late = 0;
}
