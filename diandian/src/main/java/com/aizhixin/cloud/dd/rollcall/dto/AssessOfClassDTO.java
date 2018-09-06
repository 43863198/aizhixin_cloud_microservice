package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel(description = "评分")
@Data
public class AssessOfClassDTO {

	private Long classId;

	private Double avgScore = 0.0;

	private String className;

	private Integer count = 0;

}
