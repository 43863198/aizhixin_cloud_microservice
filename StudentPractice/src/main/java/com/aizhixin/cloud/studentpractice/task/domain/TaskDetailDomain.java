/**
 * 
 */
package com.aizhixin.cloud.studentpractice.task.domain;



import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;




@ApiModel(description="老师端任务完成详情信息")
@Data
@EqualsAndHashCode(callSuper=false)
public class TaskDetailDomain {
	
	
	@ApiModelProperty(value = "学生任务信息", required = false)
	private StuTaskDomain stuTask = new StuTaskDomain();
	
	@ApiModelProperty(value = "学生任务评审信息列表", required = false)
	private List<ReviewTaskDomain> reviewTaskList = new ArrayList<ReviewTaskDomain>();
	
}
