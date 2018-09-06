package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(description = "获取课程")
@Data
@EqualsAndHashCode(callSuper = false)
public class CoursePullDownListDTO {
    Long courseId;
    String courseName;
    String courseCode;
}
