/**
 * 
 */
package com.aizhixin.cloud.dd.rollcall.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@ApiModel(description = "班级信息")
@Data
@EqualsAndHashCode(callSuper = false)
public class ClassesDTO {
	@ApiModelProperty(value = "ID", position = 1)
	protected Long id;
	@ApiModelProperty(value = "名称", position = 2)
	protected String name;
	@ApiModelProperty(value = "学院ID")
	private Long collegeId;
	@ApiModelProperty(value = "学院名称")
	private String collegeName;
	@ApiModelProperty(value = "专业ID")
	private Long professionalId;
	@ApiModelProperty(value = "专业名称")
	private String professionalName;
	@ApiModelProperty(value = "创建日期")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createdDate;
	@ApiModelProperty(value = "用户ID", required = true, position = 30)
	protected Long userId;
}
