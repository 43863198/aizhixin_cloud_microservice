package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@ApiModel(description = "学生获取评教")
@EqualsAndHashCode(callSuper = false)
public class AssessPageInfo<T> extends PageInfo<T> {

	private Double scour;
}
