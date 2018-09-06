package com.aizhixin.cloud.ew.common.dto;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AccountDTO implements java.io.Serializable{
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -1683528815261114626L;

	/**
	 * 修改：用户账号信息后台获取，前台传人参数可以为空
	 */
	@ApiModelProperty(value = "id", required = false)
	private Long id;

	private String name;

	@ApiModelProperty(value = "账号", required = false)
	private String login;

	@Size(max = 50)
	@ApiModelProperty(value = "电话号码", required = false)
	private String phoneNumber;

	@Email
	@ApiModelProperty(value = "邮箱", required = false)
	@Size(min = 5, max = 100)
	private String email;

	@ApiModelProperty(value = "学校ID", required = false)
	private Long organId;

	@ApiModelProperty(value = "权限名", required = false)
	private String role;

	@ApiModelProperty(value = "学校名", required = false)
	private String organName;
	
	@ApiModelProperty(value = "学校logo", required = false)
	private String organLogo;
	
	@ApiModelProperty(value = "头像", required = false)
	private String avatar;
	
	@ApiModelProperty(value = "知新名", required = false)
	private String shortName;

	@ApiModelProperty(value = "来源自B or C", required = false)
	private String groupType;
	
	@ApiModelProperty(value = "密码", required = false)
	private String password;
	
	@ApiModelProperty(value = "学号/工号", required = false)
    private String personId;
	
	@ApiModelProperty(value = "正方形logo")
	private String ptLogo;
    
	@ApiModelProperty(value = "长方形logo")
    private String lptLogo;
	
	private boolean antiCheating = true;
	
	private String currentTime;
	
	@ApiModelProperty(value = "性别", required = false)
	private String gender;
	
	@ApiModelProperty(value = "班级", required = false)
	private String className;
	
	@ApiModelProperty(value = "学院", required = false)
	private String collegeName;

	
}