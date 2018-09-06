package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;


@ApiModel(description = "班级信息")
@Data
public class AssessInfoWithTeacherDTO {
    private Integer score;
    private String content;
    private String assessTime;
}
