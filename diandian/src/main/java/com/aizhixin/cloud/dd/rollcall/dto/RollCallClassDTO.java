package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;

import java.util.ArrayList;

import lombok.Data;

@ApiModel(description = "课程节信息")
@Data
public class RollCallClassDTO {

	private String authCode = "";

	private Boolean classroomRollcall = false;

	private String className;

	private ArrayList<RollCallDTO> rollCallList = null;

	private Integer classSize;

}
