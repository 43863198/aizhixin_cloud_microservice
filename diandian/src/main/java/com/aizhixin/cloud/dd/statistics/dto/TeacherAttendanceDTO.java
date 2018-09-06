package com.aizhixin.cloud.dd.statistics.dto;

import lombok.Data;

/**
 * Created by LIMH on 2017/8/22.
 */
@Data
public class TeacherAttendanceDTO {
    Long teacherId;
    String teacherName;
    String classRate;
    String assessRate;
    double avgRate;
}
