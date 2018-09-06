/**
 * 
 */
package com.aizhixin.cloud.studentpractice.task.domain;



import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;




@ApiModel(description="附件信息")
@Data
@EqualsAndHashCode(callSuper=false)
public class FileDomain {
	
	@ApiModelProperty(value = "附件id", required = false)
	protected String id;

	@ApiModelProperty(value = "任务名称", required = true, position=1)
	@Size(min = 1, max = 80)
	private String fileName;
	
	@ApiModelProperty(value = "任务名称", required = true, position=2)
	@Size(min = 1, max = 300)
	private String srcUrl;
	
	@ApiModelProperty(value = "来源表id", required = false)
	protected String sourceId;
	
}
