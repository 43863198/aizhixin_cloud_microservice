/**
 * 
 */
package com.aizhixin.cloud.studentpractice.task.domain;




import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;




@ApiModel(description="查询任务分页请求实体")
@Data
@EqualsAndHashCode(callSuper=false)
public class QueryTaskPageDomain {
	
	@ApiModelProperty(value = "任务名称", required = false)
	private String taskName;
	
	@ApiModelProperty(value = "任务名称或实践小组名称", required = false)
	private String KeyWord;

	@ApiModelProperty(value = "任务进度(0:未完成[未达到100%],100:已完成[已达到100%])", required = false)
	private String progress;
	
	@ApiModelProperty(value = "任务状态(uncommit:未提交,checkPending:待审核,pass:已通过,notPass:未通过,backTo:被打回)[查询多个状态以英文,分割]", required = false)
	private String taskStatus;
	
	@ApiModelProperty(value = "实践计划id", required = false)
	private Long groupId;
	
	@ApiModelProperty(value = "第几页", required = false)
	private Integer pageNumber;
	
	@ApiModelProperty(value = "每页数据的数目", required = false)
	private Integer pageSize;
	
	@ApiModelProperty(value = "排序标识(asc:创建时间升序,desc:创建时间倒序)", required = false)
	private String sortFlag;
	
	@ApiModelProperty(value = "实践课程(周任务)id", required = false)
	private String weekTaskId;
	
}
