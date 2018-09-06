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
public class SummaryDomain implements java.io.Serializable{
	
	
	@ApiModelProperty(value = "ID")
	private String id;
	
	@ApiModelProperty(value = "学号")
	private String jobNum;

	@ApiModelProperty(value = "创建人ID")
	private Long createdBy;
	
	@ApiModelProperty(value = "创建人ID")
	private String creatorName;
	
	@ApiModelProperty(value = "创建人头像")
	private String creatorAvatar;

	@ApiModelProperty(value = "创建时间")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	
	@ApiModelProperty(value = "机构id", required = false)
	private Long orgId;
	
	@ApiModelProperty(value = "机构名称", required = false)
	private String orgName;
	
	@ApiModelProperty(value = "报告标题", required = false)
	private String summaryTitle;
	
	@ApiModelProperty(value = "类型:日报[daily],周报[weekly],月报[monthly]", required = false)
	private String summaryType;
	
	@ApiModelProperty(value = "报告描述", required = false)
	private String description;
	
	@ApiModelProperty(value = "辅导员id", required = false)
	private Long counselorId;
	
	@ApiModelProperty(value = "发布状态:公开[open],非公开[private],保存[save]", required = false)
	private String publishStatus;
	
	@ApiModelProperty(value = "评论次数", required = false)
	private Integer commentNum;
	
	@ApiModelProperty(value = "浏览次数", required = false)
	private Integer browseNum;
	
	@ApiModelProperty(value = "是否批阅")
	private Boolean isReview;
	
	@ApiModelProperty(value = "周日志分数")
	private String summaryScore;
	
	@ApiModelProperty(value = "附件信息", required = false)
	private List<File> fileList = new ArrayList<File>();
	
}
