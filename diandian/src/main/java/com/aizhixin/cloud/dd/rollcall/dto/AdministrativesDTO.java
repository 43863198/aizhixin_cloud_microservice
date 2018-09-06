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
public class AdministrativesDTO {
    private String collegeName;
    private String className;
    private Long participationCount;
    private Long practical;
    private String proportion;
    private Long classId;
    private Long collegeId;
    private Long professionId;
    private String professionalName;
    private Long semesterId;
    private String semesterName;
}
