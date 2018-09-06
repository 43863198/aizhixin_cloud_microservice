package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(description = "学周列表信息")
@Data
@EqualsAndHashCode(callSuper = false)
public class WeekListDTO {

	private List weekList;

}
