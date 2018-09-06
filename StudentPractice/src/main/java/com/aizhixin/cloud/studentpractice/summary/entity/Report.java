
package com.aizhixin.cloud.studentpractice.summary.entity;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;

import com.aizhixin.cloud.studentpractice.common.entity.AbstractStringIdEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;


/**
 * 实践报告
 * @author zhengning
 *
 */
@Entity(name = "SP_REPORT")
@ToString
public class Report extends AbstractStringIdEntity {
	private static final long serialVersionUID = 5532895497747016693L;
	

	@ApiModelProperty(value = "机构id", required = false)
	@Column(name = "ORG_ID")
	@Getter @Setter private Long orgId;
	
	@ApiModelProperty(value = "报告标题", required = false)
	@Column(name = "REPORT_TITLE")
	@Getter @Setter private String reportTitle;
	
	@ApiModelProperty(value = "报告描述", required = false)
	@Column(name = "DESCRIPTION")
	@Getter @Setter private String description;
	
	@ApiModelProperty(value = "审核建议", required = false)
	@Column(name = "ADVICE")
	@Getter @Setter private String advice;
	
	@ApiModelProperty(value = "辅导员id", required = false)
	@Column(name = "COUNSELOR_ID")
	@Getter @Setter private Long counselorId;
	
	@ApiModelProperty(value = "辅导员名称", required = false)
	@Column(name = "COUNSELOR_NAME")
	@Getter @Setter private String counselorName;
	
	@ApiModelProperty(value = "学生学号")
	@Column(name = "JOB_NUM")
	@Getter @Setter private String jobNum;
	
	@ApiModelProperty(value = "状态:未提交[uncommit],待审核[checkPending],已通过[pass],未通过[notPass],被打回[backTo]", required = false)
	@Column(name = "STATUS")
	@Getter @Setter private String status;
	
	@ApiModelProperty(value = "创建者名称", required = false)
	@Column(name = "creator_name")
	@Getter @Setter private String creatorName;
	
	@ApiModelProperty(value = "实践参与计划id")
	@Column(name = "GROUP_ID")
	@Getter @Setter private Long groupId;
	
	@ApiModelProperty(value = "实践参与计划名称")
	@Column(name = "GROUP_NAME")
	@Getter @Setter private String groupName;
	
	@ApiModelProperty(value = "批阅时间", required = false)
	@Column(name = "REVIEW_TIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	@Getter @Setter private Date reviewTime;
	
}
