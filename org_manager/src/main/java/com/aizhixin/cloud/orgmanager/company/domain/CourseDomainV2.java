/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author zhen.pan
 *
 */
@Data
public class CourseDomainV2{
	private Long id;
	@ApiModelProperty(value = "课程编码")
	private String code;
	@ApiModelProperty(value = "学分")
	private Float credit;
	@ApiModelProperty(value = "课程描述", position=4)
	@Getter
	@Setter
	protected String courseDesc;
	@ApiModelProperty(value = "课程附加属性，扩展字段", position=5)
	@Getter
	@Setter
	protected String courseProp;
	@ApiModelProperty(value = "学校ID", required = true, position=8)
	@NotNull
	@Digits(fraction = 0, integer = 18)
	private Long orgId;
	private String name;
}
