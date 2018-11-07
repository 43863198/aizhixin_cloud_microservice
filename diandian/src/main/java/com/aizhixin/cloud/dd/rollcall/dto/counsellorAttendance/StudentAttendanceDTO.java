package com.aizhixin.cloud.dd.rollcall.dto.counsellorAttendance;

import lombok.Data;

/**
 * Created by LIMH on 2017/10/17.
 */
@Data
public class StudentAttendanceDTO implements Comparable<StudentAttendanceDTO> {
    private String studentName;
    private String jobNumber;
    private String type;
    private String signTime;
    private String distance;
    private Boolean isPublicLeave = false;

    public int compareTo(StudentAttendanceDTO o) {
        if (null != jobNumber) {
            if (null == o || null == o.getJobNumber()) {
                return 1;
            }
            return  jobNumber.compareTo(o.getJobNumber());
        }
        return -1;
    }
}
