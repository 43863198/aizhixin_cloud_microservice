/**
 * 
 */
package com.aizhixin.cloud.studentpractice.task.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Temporal;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@ApiModel(description = "当前有效实践参与计划信息")
@Data
@EqualsAndHashCode(callSuper = false)
public class GroupStuDomain {

	@ApiModelProperty(value = "实践参与计划id", required = false)
	private Long groupId;

	@ApiModelProperty(value = "实践参与计划名称", required = false)
	private String groupName;

	@ApiModelProperty(value = "学生id", required = false)
	private Long stuId;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	private Date startDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	private Date endDate;

}
