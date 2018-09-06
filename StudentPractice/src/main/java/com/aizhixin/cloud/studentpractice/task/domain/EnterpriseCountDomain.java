/**
 * 
 */
package com.aizhixin.cloud.studentpractice.task.domain;





import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;




@ApiModel(description="实践公司人数统计")
@Data
@EqualsAndHashCode(callSuper=false)
public class EnterpriseCountDomain {
	
	
	@ApiModelProperty(value = "实践人数", required = false)
	protected long practiceNum;
	
	@ApiModelProperty(value = "企业名称", required = false)
	protected String enterpriseName;
	
	@ApiModelProperty(value = "实践公司所在省份")
	@Getter @Setter protected String province;
	
	@ApiModelProperty(value = "实践公司所在城市")
	@Getter @Setter protected String city;
	
}
