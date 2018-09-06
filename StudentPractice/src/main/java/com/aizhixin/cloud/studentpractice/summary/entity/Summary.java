
package com.aizhixin.cloud.studentpractice.summary.entity;

import io.swagger.annotations.ApiModelProperty;

import com.aizhixin.cloud.studentpractice.common.entity.AbstractStringIdEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


/**
 * 日报周报月报表
 * @author zhengning
 *
 */
@Entity(name = "SP_SUMMARY")
@ToString
public class Summary extends AbstractStringIdEntity {
	private static final long serialVersionUID = 5532895497747016693L;
	

	@ApiModelProperty(value = "机构id", required = false)
	@Column(name = "ORG_ID")
	@Getter @Setter private Long orgId;
	
	@ApiModelProperty(value = "机构名称", required = false)
	@Column(name = "org_name")
	@Getter @Setter private String orgName;
	
	@NotNull
	@ApiModelProperty(value = "类型:日报[daily],周报[weekly],月报[monthly]", required = false)
	@Column(name = "SUMMARY_TYPE")
	@Getter @Setter private String summaryType;
	
	@ApiModelProperty(value = "报告标题", required = false)
	@Column(name = "SUMMARY_TITLE")
	@Getter @Setter private String summaryTitle;
	
	@ApiModelProperty(value = "报告描述", required = false)
	@Column(name = "DESCRIPTION")
	@Getter @Setter private String description;
	
	@ApiModelProperty(value = "辅导员id", required = false)
	@Column(name = "COUNSELOR_ID")
	@Getter @Setter private Long counselorId;
	
	@ApiModelProperty(value = "辅导员名称", required = false)
	@Column(name = "COUNSELOR_NAME")
	@Getter @Setter private String counselorName;
	
	@NotNull
	@ApiModelProperty(value = "发布状态:公开[open],非公开[private],保存[save]", required = false)
	@Column(name = "PUBLISH_STATUS")
	@Getter @Setter private String publishStatus;
	
	@ApiModelProperty(value = "评论次数", required = false)
	@Column(name = "COMMENT_NUM")
	@Getter @Setter private Integer commentNum;
	
	@ApiModelProperty(value = "浏览次数", required = false)
	@Column(name = "BROWSE_NUM")
	@Getter @Setter private Integer browseNum;
	
	@ApiModelProperty(value = "创建者名称", required = false)
	@Column(name = "creator_name")
	@Getter @Setter private String creatorName;
	
	@ApiModelProperty(value = "企业导师id", required = false)
	@Column(name = "MENTOR_ID")
	@Getter @Setter private Long mentorId;
	
	@ApiModelProperty(value = "导师名称", required = false)
	@Column(name = "MENTOR_NAME")
	@Getter @Setter private String mentorName;
	
	@ApiModelProperty(value = "实践参与计划id")
	@Column(name = "GROUP_ID")
	@Getter @Setter private Long groupId;
	
	@ApiModelProperty(value = "实践参与计划名称")
	@Column(name = "GROUP_NAME")
	@Getter @Setter private String groupName;
	
	@ApiModelProperty(value = "学号")
	@Column(name = "JOB_NUM")
	@Getter @Setter private String jobNum;
	
//	@ApiModelProperty(value = "是否批阅")
//	@Column(name = "IS_REVIEW")
//	@Getter @Setter private Boolean isReview;
//	
//	@ApiModelProperty(value = "周日志分数")
//	@Column(name = "SUMMARY_SCORE")
//	@Getter @Setter private String summaryScore;
	
}
