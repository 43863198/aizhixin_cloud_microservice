
package com.aizhixin.cloud.school.schoolinfo.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/** 
 * @ClassName: SchoolExcellentTeacher 
 * @Description: 优秀教师
 * @author xiagen
 * @date 2017年5月15日 上午11:32:34 
 *  
 */

@ApiModel(description="优秀教师domain")
public class SchoolExcellentTeacherDomain  {
    @ApiModelProperty("优秀教师的id")
	@Getter@Setter
	private Long id;
	
    @ApiModelProperty("当前用户id")
	@Getter@Setter
	private Long userId;
    
    @ApiModelProperty("学校id")
	@Getter@Setter
	private Long orgId;
	
    @ApiModelProperty("教师id")
	@Getter@Setter
	private Long teacherId;

    @ApiModelProperty("教师介绍")
	@Getter@Setter
	private String introduction;
	
    @ApiModelProperty("教师介绍图片地址")
	@Getter@Setter
	private String inUrl;
	
    @ApiModelProperty("模版展示类型")
	@Getter@Setter
	private Integer templateShow;
    
    @ApiModelProperty("教师姓名")
	@Getter@Setter
	private String teacherName;
    
    @ApiModelProperty("手机号")
	@Getter@Setter
	private String phone;
    
    @ApiModelProperty("工号")
	@Getter@Setter
	private String jobNumber;
    
    @ApiModelProperty("性别")
	@Getter@Setter
	private String sex;
    
    @ApiModelProperty("邮件")
	@Getter@Setter
	private String email;
    
    public SchoolExcellentTeacherDomain(){}

	public SchoolExcellentTeacherDomain(Long id, Long teacherId,String introduction, String inUrl, Integer templateShow) {
		this.id = id;
		this.teacherId=teacherId;
		this.introduction = introduction;
		this.inUrl = inUrl;
		this.templateShow = templateShow;
	}
    
}
