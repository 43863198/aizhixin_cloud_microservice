/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author zhen.pan
 *
 */
@ApiModel(description="用户信息")
@Data
public class TeacherDomain {
	@ApiModelProperty(value = "ID", allowableValues = "range[1,infinity]", position=1)
	private Long id;
	@ApiModelProperty(value = "账号ID", allowableValues = "range[1,infinity]", position=2)
	private Long accountId;
	@NotNull
	@ApiModelProperty(value = "姓名", required = true, position=4)
	@Size(min = 0, max = 50)
	private String name;
	@ApiModelProperty(value = "姓名电话", position=5)
	@Size(min = 0, max = 20)
	private String phone;
	@Email
	@ApiModelProperty(value = "邮箱", position=6)
	@Size(min = 0, max = 65)
	private String email;
	@ApiModelProperty(value = "工号", position=7)
	@Size(min = 0, max = 50)
	private String jobNumber;
	@ApiModelProperty(value = "性别(男性male|女性female)", allowableValues = "male,female", position=8)
	@Size(min = 0, max = 10)
	private String sex;
	@ApiModelProperty(value = "是否班主任", allowableValues = "true,false", position=9)
	private Boolean classesManager;
	@ApiModelProperty(value = "学院ID", allowableValues = "range[1,infinity)", position=13)
	@Digits(fraction = 0, integer = 18)
	private Long collegeId;
	@ApiModelProperty(value = "学院名称", position=14)
	private String collegeName;
	@ApiModelProperty(value = "学院编码", position=14)
	private String collegeCode;
	@ApiModelProperty(value = "学校ID", allowableValues = "range[1,infinity)", position=15)
	@Digits(fraction = 0, integer = 18)
	private Long orgId;
	@ApiModelProperty(value = "创建日期", position=16)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createdDate;
	@ApiModelProperty(value = "操作用户ID", allowableValues = "range[1,infinity)", position=13)
	@Digits(fraction = 0, integer = 30)
	private Long userId;

	public TeacherDomain() {}

	public TeacherDomain(Long id, String name , String phone, String email, String jobNumber, String sex) {
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.jobNumber = jobNumber;
		this.sex = sex;
	}

	public TeacherDomain(Long id, String name , String phone, String email, String jobNumber, String sex, Long collegeId, String collegeName) {
		this(id, name, phone, email, jobNumber, sex);
		this.collegeId = collegeId;
		this.collegeName = collegeName;
	}
}
