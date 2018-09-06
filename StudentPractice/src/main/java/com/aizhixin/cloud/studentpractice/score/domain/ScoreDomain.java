/**
 * 
 */
package com.aizhixin.cloud.studentpractice.score.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;




@ApiModel(description="实践成绩")
@Data
@EqualsAndHashCode(callSuper=false)
public class ScoreDomain implements java.io.Serializable{
	
	@ApiModelProperty(value = "成绩id")
	private String id;
	
	@ApiModelProperty(value = "学号")
	private String jobNum;
	
	@ApiModelProperty(value = "学生id")
	private Long studentId;
	
	@ApiModelProperty(value = "学生名称")
	private String studentName;
	
	@ApiModelProperty(value = "学生头像")
	private String studentAvatar;
	
	@ApiModelProperty(value = "机构id")
	private Long orgId;
	
	@ApiModelProperty(value = "企业导师id")
	private Long mentorId;
	
	@ApiModelProperty(value = "企业导师名称")
	private String mentorName;
	
	@ApiModelProperty(value = "辅导员id")
	private Long counselorId;
	
	@ApiModelProperty(value = "辅导员名称")
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
	
	@ApiModelProperty(value = "参与计划id")
	private Long groupId;
	
	@ApiModelProperty(value = "参与计划名称")
	private String groupName;
	
	@ApiModelProperty(value = "企业id")
	private Long enterpriseId;
	
	@ApiModelProperty(value = "企业名称")
	private String enterpriseName;
	
	@ApiModelProperty(value = "签到成绩")
	private Double signScore;
	
	@ApiModelProperty(value = "日志成绩")
	private Double summaryScore;
	
	@ApiModelProperty(value = "实践报告成绩")
	private Double reportScore;
	
	@ApiModelProperty(value = "任务成绩")
	private Double taskScore;
	
	@ApiModelProperty(value = "总成绩")
	private Double totalScore;
	
}
