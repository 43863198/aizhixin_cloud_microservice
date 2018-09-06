
package com.aizhixin.cloud.school.schoolinfo.domain;

import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/** 
 * @ClassName: SchoolHotSpecialty 
 * @Description: 热门专业信息domain
 * @author xiagen
 * @date 2017年5月12日 下午5:31:49 
 *  
 */
@ApiModel(description="热门专业信息")
public class SchoolHotSpecialtyDomain {
	
	@ApiModelProperty("热门专业id")
	@Getter@Setter
	private Long id;
	
	@ApiModelProperty("专业名称specialtyName")
	@Getter@Setter
	private String specialtyName;
	
	@ApiModelProperty("操作者id")
	@Getter@Setter
	private Long userId;
	
	@ApiModelProperty("学校id")
	@Getter@Setter
	private Long orgId;
	

	@ApiModelProperty("学院id")
	@Getter@Setter
	private Long collegeId;
	
	
	@ApiModelProperty("学院名称collegeName")
	@Getter@Setter
	private String collegeName;
	
	@ApiModelProperty("专业id")
	@Getter@Setter
	private Long specialtyId;
	
	@ApiModelProperty("专业介绍")
	@Getter@Setter
	private String introduction;
	
	@ApiModelProperty("专业介绍图片")
	@Getter@Setter
	private String inUrl;

	@ApiModelProperty("模版展示")
	@Getter@Setter
	private Integer templateShow;
	
	@ApiModelProperty("创建时间")
	@Getter@Setter
	private Date createDate;
	
	public SchoolHotSpecialtyDomain(){}
	public SchoolHotSpecialtyDomain(Long id,Date createDate,Long specialtyId,String introduction,String inUrl,Integer templateShow,Long collegeId){
		this.createDate=createDate;
		this.id=id;
		this.specialtyId=specialtyId;
		this.introduction=introduction;
		this.inUrl=inUrl;
		this.templateShow=templateShow;
		this.collegeId= collegeId;
	}
}
