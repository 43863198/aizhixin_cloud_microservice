package com.aizhixin.cloud.orgmanager.training.entity;

import com.aizhixin.cloud.orgmanager.company.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

import java.util.Date;

/**
 * 实训参与计划设置
 */
@Entity
@Table(name = "T_TRAINING_GROUP_SET")
@ToString
public class TrainingGroupSet {

	@ApiModelProperty(value = "ID")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	@Getter
	@Setter
	private Long id;

	@ApiModelProperty(value = "实践参与计划id")
	@Column(name = "GROUP_ID")
	@Getter
	@Setter
	private Long groupId;

	@ApiModelProperty(value = "是否需要签到")
	@Column(name = "IS_NEED_SIGN")
	@Getter
	@Setter
	private Boolean isNeedSign = Boolean.FALSE;

	@ApiModelProperty(value = "需要签到次数")
	@Column(name = "NEED_SIGN_NUM")
	@Getter
	@Setter
	private Integer needSignNum;

	@ApiModelProperty(value = "是否需要日志周志")
	@Column(name = "IS_NEED_SUMMARY")
	@Getter
	@Setter
	private Boolean isNeedSummary = Boolean.FALSE;

	@ApiModelProperty(value = "需要日报数量")
	@Column(name = "NEED_DAILY_NUM")
	@Getter
	@Setter
	private Integer needDailyNum;

	@ApiModelProperty(value = "需要周报数量")
	@Column(name = "NEED_WEEKLY_NUM")
	@Getter
	@Setter
	private Integer needWeeklyNum;

	@ApiModelProperty(value = "需要月总结数量")
	@Column(name = "NEED_MONTHLY_NUM")
	@Getter
	@Setter
	private Integer needMonthlyNum;

	@ApiModelProperty(value = "是否需要实践报告")
	@Column(name = "IS_NEED_REPORT")
	@Getter
	@Setter
	private Boolean isNeedReport = Boolean.FALSE;

	@ApiModelProperty(value = "签到所占权重")
	@Column(name = "SIGN_WEIGHT")
	@Getter
	@Setter
	private Double signWeight;

	@ApiModelProperty(value = "周日志所占权重")
	@Column(name = "SUMMARY_WEIGHT")
	@Getter
	@Setter
	private Double summaryWeight;

	@ApiModelProperty(value = "实践报告所占权重")
	@Column(name = "REPORT_WEIGHT")
	@Getter
	@Setter
	private Double reportWeight;

	@ApiModelProperty(value = "实践任务所占权重")
	@Column(name = "TASK_WEIGHT")
	@Getter
	@Setter
	private Double taskWeight;

	@ApiModelProperty(value = "创建人")
	@CreatedBy
	@Column(name = "CREATED_BY")
	@Getter
	@Setter
	private Long createdBy;

	@ApiModelProperty(value = "创建时间")
	@CreatedDate
	@Column(name = "CREATED_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	@Getter
	@Setter
	private Date createdDate = new Date();

	@ApiModelProperty(value = "最后一次修改人")
	@LastModifiedBy
	@Column(name = "LAST_MODIFIED_BY")
	@Getter
	@Setter
	private Long lastModifiedBy;

	@ApiModelProperty(value = "最后一次修改时间")
	@LastModifiedDate
	@Column(name = "LAST_MODIFIED_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	@Getter
	@Setter
	private Date lastModifiedDate = new Date();

	@ApiModelProperty(value = "成绩生成日期")
	@Column(name = "SCORE_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	@Getter
	@Setter
	private Date scoreDate;
	
	
//	@ApiModelProperty(value = "是否日志评分")
//	@Column(name = "SUMMARY_IS_GRADE")
//	@Getter
//	@Setter
//	private Boolean summaryIsGrade = Boolean.FALSE;

}