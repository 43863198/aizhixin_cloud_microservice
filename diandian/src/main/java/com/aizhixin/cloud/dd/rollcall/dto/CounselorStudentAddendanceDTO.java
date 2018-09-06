package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by Administrator on 2017/6/15.
 */
@Data
@ApiModel(description = "辅导员查下看学生考勤")
@EqualsAndHashCode(callSuper = false)
public class CounselorStudentAddendanceDTO {
    Long studentId;
    String studentNum;
    String studentName;
    String avatar;
    Integer normalCount;
    Integer exceptionCount;


}
