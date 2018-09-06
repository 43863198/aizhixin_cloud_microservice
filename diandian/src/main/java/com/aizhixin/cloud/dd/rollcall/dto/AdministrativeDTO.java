package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by Administrator on 2017/6/15.
 */
@Data
@ApiModel(description = "行政班考勤详细")
@EqualsAndHashCode(callSuper = false)
public class AdministrativeDTO {
    private String JobNumber;
    private String studentName;
    private Long arrvied; //已到 1
    private Long beg; //请假 4
    private Long crunk;//旷到 2
    private Long late;//迟到 3
    private Long early;//早退 5
    private String className;
    private Long studentId;
    private Long semesterId;
    private String semesterName;
}
