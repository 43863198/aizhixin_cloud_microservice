package com.aizhixin.cloud.dd.dorms.domain;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AssginDomain {
	@ApiModelProperty(value="学院id")
	private Long collegeId;
	@ApiModelProperty(value="学院名称")
	private String collegeName;
	@ApiModelProperty(value="专业集合")
	private List<profDomain> pl=new ArrayList<>();
}
