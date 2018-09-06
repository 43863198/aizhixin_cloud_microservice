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
public class QueryStatisticalPageDomain {
	
	@ApiModelProperty(value = "参与计划id")
	private Long groupId;
	
	@ApiModelProperty(value = "学生id")
	private Long stuId;
	
	@ApiModelProperty(value = "学生姓名/学号", required = false)
	private String keyWords;
	
	@ApiModelProperty(value = "第几页", required = false)
	private Integer pageNumber;
	
	@ApiModelProperty(value = "每页数据的数目", required = false)
	private Integer pageSize;
	
	@ApiModelProperty(value = "排序标识(asc:创建时间升序,desc:创建时间倒序)", required = false)
	private String sortFlag;
	
	@ApiModelProperty(value = "排序字段(PASS_NUM:任务完成数,SIGNIN_NORMAL_NUM:正常打卡数,SUMMARY_TOTAL_NUM:已提交周日志数)", required = false)
	private String sortField;
	
}
