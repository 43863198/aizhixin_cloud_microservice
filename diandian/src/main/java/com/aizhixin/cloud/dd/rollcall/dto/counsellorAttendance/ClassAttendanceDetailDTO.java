package com.aizhixin.cloud.dd.rollcall.dto.counsellorAttendance;

import lombok.Data;

import java.util.List;

/**
 * Created by LIMH on 2017/10/17.
 */
@Data
public class ClassAttendanceDetailDTO {
    private String teachingClassName;
    private String teacher;
    private List <StudentAttendanceDTO> studentAttendance;
}
