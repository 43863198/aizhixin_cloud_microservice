
package com.aizhixin.cloud.school.schoolinfo.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: SchoolIntrductionDomain
 * @Description: 学校简介domain
 * @author xiagen
 * @date 2017年5月12日 上午11:32:46
 * 
 */
@ApiModel(description="学校简介")
public class SchoolIntrductionDomain {
	@ApiModelProperty("学校id")
	@Getter
	@Setter
	private Long orgId;
	
	@ApiModelProperty("学校简介")
	@Getter
	@Setter
	private String introduction;
	
	@ApiModelProperty("当前操作者id")
	@Getter
	@Setter
	private Long userId;
	
}
