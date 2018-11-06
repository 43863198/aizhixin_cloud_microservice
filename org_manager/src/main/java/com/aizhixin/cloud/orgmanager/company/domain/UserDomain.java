/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.domain;

import com.aizhixin.cloud.orgmanager.company.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zhen.pan
 *
 */
@ApiModel(description="用户信息")
@Data
@NoArgsConstructor
public class UserDomain implements java.io.Serializable {
	@ApiModelProperty(value = "ID", allowableValues = "range[1,infinity]", position=1)
	private Long id;
	@ApiModelProperty(value = "账号ID", allowableValues = "range[1,infinity]", position=2)
	private Long accountId;
	@ApiModelProperty(value = "用户类型(10学校管理员，20学院管理员，40班级管理员，60老师，70学生)", position=3, allowableValues = "10,20,40,60,70")
	private Integer userType;
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
	@ApiModelProperty(value = "工号(学号)", position=7)
	@Size(min = 0, max = 50)
	private String jobNumber;
	@ApiModelProperty(value = "性别(男性male|女性female)", allowableValues = "male,female", position=8)
	@Size(min = 0, max = 10)
	private String sex;
	@ApiModelProperty(value = "班级ID", allowableValues = "range[1,infinity)", position=9)
	@Digits(fraction = 0, integer = 18)
	private Long classesId;
	@ApiModelProperty(value = "班级名称", position=10)
	private String classesName;
	@ApiModelProperty(value = "专业ID", allowableValues = "range[1,infinity)", position=11)
	@Digits(fraction = 0, integer = 18)
	private Long professionalId;
	@ApiModelProperty(value = "专业名称", position=12)
	private String professionalName;
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
	@ApiModelProperty(value = "学校名称", position=16)
	private String orgName;
	@ApiModelProperty(value = "学校编码", position=16)
	private String orgCode;
	@ApiModelProperty(value = "角色组", position=17)
	private String roleGroup;
	@ApiModelProperty(value = "角色", position=18)
	private Set<String> roles;
	@ApiModelProperty(value = "创建日期", position=19)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createdDate;
	@ApiModelProperty(value = "操作用户ID", allowableValues = "range[1,infinity)", position=20)
	@Digits(fraction = 0, integer = 30)
	private Long userId;
	@ApiModelProperty(value = "年级", position=22)
	private String teachingYear;
	@ApiModelProperty(value = "入学日期", position=23)
	private Date inSchoolDate;
	@ApiModelProperty(value = "是否在校状态", position=24)
	private Integer schoolStatus;
	@ApiModelProperty(value = "身份证号", position=25)
	private String idNumber;
	@ApiModelProperty(value = "生源地", position=26)
	private String studentSource;
	@ApiModelProperty(value = "是否班长", position=27)
	private Boolean isMonitor;

	public UserDomain(User user, String roleGroup, String roleName) {
		this.id = user.getId();
		this.accountId = user.getId();
		this.userType = user.getUserType();
		this.name = user.getName();
		this.jobNumber = user.getJobNumber();
		this.idNumber = user.getIdNumber();
		this.studentSource = user.getStudentSource();
		this.email = user.getEmail();
		this.sex = user.getSex();
		this.orgId = user.getOrgId();
		this.createdDate = user.getCreatedDate();
		this.inSchoolDate = user.getInSchoolDate();
		this.schoolStatus = user.getSchoolStatus();
		if (!StringUtils.isEmpty(roleGroup) && !StringUtils.isEmpty(roleName)) {
			this.roleGroup = roleGroup;
			if (null == this.roles) {
				this.roles = new HashSet<>();
			}
			this.roles.add(roleName);
		}
		if (null != user.getClasses()) {
			this.teachingYear = user.getClasses().getTeachingYear();
			this.classesId = user.getClasses().getId();
			this.classesName = user.getClasses().getName();
		}
		if (null != user.getProfessional()) {
			this.professionalId = user.getProfessional().getId();
			this.professionalName = user.getProfessional().getName();
		}
		if (null != user.getCollege()) {
			this.collegeId = user.getCollege().getId();
			this.collegeName = user.getCollege().getName();
		}
	}

	public void addRole(String roleName) {
		if (null == this.roles) {
			this.roles = new HashSet<>();
		}
		this.roles.add(roleName);
	}

	public void deleteRole(String roleName) {
		if (null != this.roles) {
			this.roles.remove(roleName);
		}
	}
}
