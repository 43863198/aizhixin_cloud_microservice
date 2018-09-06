/**
 * 
 */
package com.aizhixin.cloud.studentpractice.task.domain;




import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;




@ApiModel(description="是否参与实践学生人数统计")
@Data
@EqualsAndHashCode(callSuper=false)
public class JoinStuNumCountDomain {
	
	@ApiModelProperty(value = "班级id", required = false)
	private Long classId;
	
	@ApiModelProperty(value = "参与人数", required = false)
	private Integer JoinNum = 0;
	
	@ApiModelProperty(value = "未参与人数", required = false)
	private Integer notJoinNum = 0;
	
}
