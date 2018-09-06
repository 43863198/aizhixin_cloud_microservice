
package com.aizhixin.cloud.school.schoolinfo.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName: SchoolLogoInfoDomain
 * @Description:学校logo信息domain
 * @author xiagen
 * @date 2017年5月11日 下午6:28:34
 * 
 */
@ApiModel(description = "学校logo信息")
public class SchoolLogoInfoDomain {
	
	@ApiModelProperty("学校logo信息id")
	@Getter@Setter
	private Long id=Long.valueOf("0");
	
	@ApiModelProperty("学校id")
	@Getter@Setter
	private Long orgId;
	
	@ApiModelProperty("学校logo地址")
	@Getter@Setter
	private String logoUrl;
	
	@ApiModelProperty("学校logo简介")
	@Getter@Setter
	private String description;
	
	@ApiModelProperty("用户id")
	@Getter@Setter
	private Long userId;
	
	@ApiModelProperty("logo尺寸")
	@Getter@Setter
	private Integer logoSize;
	
	@ApiModelProperty("logo顺序")
	@Getter@Setter
	private Integer logoSort;
	
	public SchoolLogoInfoDomain(){
		
	}

	public SchoolLogoInfoDomain(Long id, Long schoolId, String logoUrl, String description,Integer logoSize,Integer logoSort) {
		super();
		this.id = id;
		this.orgId = schoolId;
		this.logoUrl = logoUrl;
		this.description = description;
		this.logoSize=logoSize;
		this.logoSort=logoSort;
	}
	
	
}
