package com.aizhixin.cloud.dd.rollcall.dto.Attendance;

import lombok.Data;

/**
 * 行政班考勤 按学院
 * <p>
 * Created by LIMH on 2017/9/16.
 */
@Data
public class ClassesAttendanceByProfessDto {
    /**
     * 年级
     */
    String grade;
    String collegeName;
    String proName;
    private Integer total;
    private Integer normal;
    private Integer later;
    private Integer askForLeave;
    private Integer truancy;
    private Integer leave;
    private String attendance;
}
