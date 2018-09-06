package com.aizhixin.cloud.dd.rollcall.dto.Attendance;

import lombok.Data;

/**
 * 教学班考勤 按老师
 * <p>
 * Created by LIMH on 2017/9/16.
 */
@Data
public class TeachingClassesAttendanceGroupbyTeacherDto {
    /**
     * 年级
     */
    String grade;
    String collegeName;
    String teacherJob;
    String teacherName;
    private Integer total;
    private Integer normal;
    private Integer later;
    private Integer askForLeave;
    private Integer truancy;
    private Integer leave;
    private String attendance;
}
