/**
 * 
 */
package com.aizhixin.cloud.studentpractice.task.domain;




import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;




@ApiModel(description="查询学生信息分页请求实体")
@Data
@EqualsAndHashCode(callSuper=false)
public class QueryStuPageDomain {
	
	@ApiModelProperty(value = "学生id", required = false)
	private Long stuId;
	
	@ApiModelProperty(value = "辅导员id", required = false)
	private Long counselorId;
	
	@ApiModelProperty(value = "学生名称", required = false)
	private String stuName;
	
	@ApiModelProperty(value = "班级名称", required = false)
	private String className;
	
	@ApiModelProperty(value = "起始时间(数据格式为:yyyy-MM-dd)", required = false)
	private String startTime;
	
	@ApiModelProperty(value = "截至实践(数据格式为:yyyy-MM-dd)", required = false)
	private String endTime;
	
	@ApiModelProperty(value = "参与计划id")
	private Long groupId;
	
	@ApiModelProperty(value = "院系id", required = false)
	private Long collegeId;
	
	@ApiModelProperty(value = "专业id", required = false)
	private Long professionalId;
	
	@ApiModelProperty(value = "班级id", required = false)
	private Long classId;

	@ApiModelProperty(value = "学号", required = false)
	private String jobNum;
	
	@ApiModelProperty(value = "实践企业名称", required = false)
	private String enterpriseName;
	
	@ApiModelProperty(value = "学生姓名或学生学号[汇总查询时为班级名称]", required = false)
	private String keyWords;
	
	@ApiModelProperty(value = "第几页", required = false)
	private Integer pageNumber;
	
	@ApiModelProperty(value = "每页数据的数目", required = false)
	private Integer pageSize;
	
	
	
}
