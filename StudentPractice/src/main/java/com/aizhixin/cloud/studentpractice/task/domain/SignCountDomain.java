/**
 * 
 */
package com.aizhixin.cloud.studentpractice.task.domain;






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




@ApiModel(description="实践考勤统计")
@Data
@EqualsAndHashCode(callSuper=false)
public class SignCountDomain {
	
	@ApiModelProperty(value = "学号")
	private String jobNum;
	
	@ApiModelProperty(value = "学生id")
	private Long studentId;
	
	@ApiModelProperty(value = "学生名称")
	private String studentName;
	
	@ApiModelProperty(value = "辅导员名称", required = false)
	private String counselorName;
	
	@ApiModelProperty(value = "班级名称")
	private String className;
	
	@ApiModelProperty(value = "专业名称")
	private String professionalName;
	
	@ApiModelProperty(value = "学院名称")
	private String collegeName;
	
	@ApiModelProperty(value = "参与计划id")
	private Long groupId;
	
	@ApiModelProperty(value = "参与计划名称")
	private String groupName;
	
	@ApiModelProperty(value = "年级")
	private String grade;
	
	@ApiModelProperty(value = "实践签到总数")
	private Integer signInTotalNum;
	
	@ApiModelProperty(value = "正常签到数")
	private Integer signInNormalNum;
	
	@ApiModelProperty(value = "请假数")
	private Integer leaveNum;
}
