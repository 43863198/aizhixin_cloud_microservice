package com.aizhixin.cloud.dd.rollcall.dto.counsellorAttendance;

import lombok.Data;

import java.util.List;

/**
 * Created by LIMH on 2017/10/17.
 */
@Data
public class CounsellorAttendanceDTO {
    private String currentPeriod;
    private String currentDate;
    private String dayOfWeek;
    private List <ClassesInfoDTO> classInfo;
}
