package com.aizhixin.cloud.dd.rollcall.dto.Attendance;

import lombok.Data;

/**
 * 教学班考勤 按课程节
 * <p>
 * Created by LIMH on 2017/9/16.
 */
@Data
public class TeachingClassesAttendanceGroupbyScheduleDto {
    /**
     * 年级
     */
    String teachingClassesCode;
    String courseName;
    String teacherName;
    String className;
    String classRoom;
    String teachDate;
    String dayOfWeek;
    String period;
    private Integer total;
    private Integer normal;
    private Integer later;
    private Integer askForLeave;
    private Integer truancy;
    private Integer leave;
    private String attendance;

}
