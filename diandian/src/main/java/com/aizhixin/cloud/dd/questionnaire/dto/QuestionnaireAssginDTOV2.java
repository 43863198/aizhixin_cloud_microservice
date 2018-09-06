package com.aizhixin.cloud.dd.questionnaire.dto;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QuestionnaireAssginDTOV2 {
	@ApiModelProperty(value="分配id")
	private Long id;
	@ApiModelProperty(value="序号")
	private Integer no;
	@ApiModelProperty(value="班级类型")
	private Integer classType;
	@ApiModelProperty(value="班级编码")
	private String code;
	@ApiModelProperty(value="班级名称")
	private String name;
	@ApiModelProperty(value="教师名称")
	private String teacherName;
	@ApiModelProperty(value="分配时间")
	private Date  assginDate;
}
