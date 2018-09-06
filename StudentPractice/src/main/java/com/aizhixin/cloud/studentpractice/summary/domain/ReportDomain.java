/**
 * 
 */
package com.aizhixin.cloud.studentpractice.summary.domain;




import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import com.aizhixin.cloud.studentpractice.task.domain.FileDomain;
import com.aizhixin.cloud.studentpractice.task.entity.File;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;




@ApiModel(description="日报，周报，月报")
@Data
@EqualsAndHashCode(callSuper=false)
public class ReportDomain {
	
	
	@ApiModelProperty(value = "ID")
	private String id;

	@ApiModelProperty(value = "创建人ID")
	private Long createdBy;
	
	@ApiModelProperty(value = "创建人ID")
	private String creatorName;
	
	@ApiModelProperty(value = "学生学号")
	private String jobNum;
	
	@ApiModelProperty(value = "审核建议")
	private String advice;
	
	@ApiModelProperty(value = "创建人头像")
	private String creatorAvatar;

	@ApiModelProperty(value = "创建时间")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	
	@ApiModelProperty(value = "机构id", required = false)
	private Long orgId;
	
	@ApiModelProperty(value = "报告标题", required = false)
	private String reportTitle;
	
	@ApiModelProperty(value = "报告描述", required = false)
	private String description;
	
	@ApiModelProperty(value = "辅导员id", required = false)
	private Long counselorId;
	
	@ApiModelProperty(value = "辅导员名称", required = false)
	private String counselorName;
	
	@ApiModelProperty(value = "实践报告数据状态:未提交[uncommit],待审核[checkPending],已通过[pass],未通过[notPass],被打回[backTo]", required = false)
	private String status;
	
	@ApiModelProperty(value = "提交状态save保存,commit提交", required = false)
	private String commitStatus;
	
	@ApiModelProperty(value = "附件信息", required = false)
	private List<File> fileList = new ArrayList<File>();
	
}
