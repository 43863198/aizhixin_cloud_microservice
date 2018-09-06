package com.aizhixin.cloud.dd.rollcall.dto.counsellorAttendance;

import lombok.Data;

import java.util.List;

/**
 * Created by LIMH on 2017/10/17.
 */
@Data
public class ClassesInfoDTO {
    private Long classId;
    private String className;
    private int courseNum;
    private int totalStudent;
    private int commitStudent;
    private int uncommitStudent;
    private List <ClassAttendanceDetailDTO> classAttendanceDetail;
}
