/**
 * 
 */
package com.aizhixin.cloud.studentpractice.score.domain;




import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;




@ApiModel(description="实践成绩查询")
@Data
@EqualsAndHashCode(callSuper=false)
public class QueryScoreDomain {
	
	@ApiModelProperty(value = "实践计划id", required = false)
	private Long groupId;
	
	@ApiModelProperty(value = "学生id", required = false)
	private Long stuId;
	
	@ApiModelProperty(value = "辅导员id", required = false)
	private Long counselorId;
	
	@ApiModelProperty(value = "院系id", required = false)
	private Long collegeId;
	
	@ApiModelProperty(value = "专业id", required = false)
	private Long professionalId;
	
	@ApiModelProperty(value = "班级id", required = false)
	private Long classId;

	@ApiModelProperty(value = "第几页", required = false)
	private Integer pageNumber;
	
	@ApiModelProperty(value = "每页数据的数目", required = false)
	private Integer pageSize;
	
	@ApiModelProperty(value = "学号/姓名/参与计划名称", required = false)
	private String keyWords;
	
	@ApiModelProperty(value = "排序标识(asc:升序,desc:倒序)", required = false)
	private String sortFlag;
	
	@ApiModelProperty(value = "排序字段(SIGN_SCORE:签到成绩,SUMMARY_SCORE:日志成绩,REPORT_SCORE:实践报告成绩,TASK_SCORE:任务成绩,TOTAL_SCORE:总成绩)", required = false)
	private String sortField;
	
}
