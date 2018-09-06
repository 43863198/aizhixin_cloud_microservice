/**
 * 
 */
package com.aizhixin.cloud.studentpractice.summary.domain;






import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import com.aizhixin.cloud.studentpractice.common.core.DataValidity;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;




@ApiModel(description="日报，周报，月报数量统计")
@Data
@EqualsAndHashCode(callSuper=false)
public class SummaryReplyCountDomain {
	
	@ApiModelProperty(value = "ID")
	@Getter @Setter private String id;

	@ApiModelProperty(value = "类型:日报[daily],周报[weekly],月报[monthly]", required = false)
	private String summaryType;
	
	@ApiModelProperty(value = "报告标题", required = false)
	private String summaryTitle;
	
	@ApiModelProperty(value = "回复数量", required = false)
	private Integer replyNum;
	
	@ApiModelProperty(value = "学号")
	private String jobNum;
	
	@ApiModelProperty(value = "学生id")
	private Long studentId;
	
	@ApiModelProperty(value = "学生名称")
	private String studentName;
	
	@ApiModelProperty(value = "机构id")
	private Long orgId;
	
	@ApiModelProperty(value = "学生id")
	private Long mentorId;
	
	@ApiModelProperty(value = "辅导员id", required = false)
	private Long counselorId;
	
	@ApiModelProperty(value = "辅导员名称", required = false)
	private String counselorName;
	
	@ApiModelProperty(value = "班级id")
	private Long classId;
	
	@ApiModelProperty(value = "院系id")
	private Long collegeId;
	
	@ApiModelProperty(value = "专业id")
	private Long professionalId;
	
	@ApiModelProperty(value = "班级名称")
	private String className;
	
	@ApiModelProperty(value = "专业名称")
	private String professionalName;
	
	@ApiModelProperty(value = "学院名称")
	private String collegeName;
	
	@ApiModelProperty(value = "年级")
	private String grade;
	
	@ApiModelProperty(value = "参与计划Id")
	private Long groupId;
	
	@ApiModelProperty(value = "参与计划名称")
	private String groupName;
	
	@ApiModelProperty(value = "需提交日志数量")
	private Integer needDailyNum;
	
	@ApiModelProperty(value = "需提交周志数量")
	private Integer needWeeklyNum;
	
	@ApiModelProperty(value = "需提交月志数量")
	private Integer needMonthlyNum;
	
	@ApiModelProperty(value = "已提交日志数量")
	private Integer dailyNum;
	
	@ApiModelProperty(value = "已提交周志数量")
	private Integer weeklyNum;
	
	@ApiModelProperty(value = "已提交月志数量")
	private Integer monthlyNum;
	
	@ApiModelProperty(value = "创建时间")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createdDate = new Date();

}
