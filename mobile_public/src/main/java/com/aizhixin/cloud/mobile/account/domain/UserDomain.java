/**
 * 
 */
package com.aizhixin.cloud.mobile.account.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Set;

/**
 * @author zhen.pan
 *
 */
@ApiModel(description="用户信息")
@Data
@NoArgsConstructor
public class UserDomain implements java.io.Serializable {
	@ApiModelProperty(value = "ID", position=1)
	private Long id;
	@ApiModelProperty(value = "账号ID", position=2)
	private Long accountId;
	@ApiModelProperty(value = "用户类型(10学校管理员，20学院管理员，40班级管理员，60老师，70学生)", position=3)
	private Integer userType;
	@ApiModelProperty(value = "姓名", position=4)
	private String name;
	@ApiModelProperty(value = "姓名电话", position=5)
	private String phone;
	@ApiModelProperty(value = "邮箱", position=6)
	private String email;
	@ApiModelProperty(value = "工号(学号)", position=7)
	private String jobNumber;
	@ApiModelProperty(value = "性别(男性male|女性female)", position=8)
	private String sex;
	@ApiModelProperty(value = "班级ID", position=9)
	private Long classesId;
	@ApiModelProperty(value = "班级名称", position=10)
	private String classesName;
	@ApiModelProperty(value = "专业ID", position=11)
	private Long professionalId;
	@ApiModelProperty(value = "专业名称", position=12)
	private String professionalName;
	@ApiModelProperty(value = "学院ID", position=13)
	private Long collegeId;
	@ApiModelProperty(value = "学院名称", position=14)
	private String collegeName;
	@ApiModelProperty(value = "学院Code", position=14)
	private String collegeCode;
	@ApiModelProperty(value = "学校ID", position=15)
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
	@ApiModelProperty(value = "操作用户ID", position=20)
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

	public String createRoleString() {
		if (null != roles && roles.size() > 0) {
			int i = 0;
			StringBuilder t = new StringBuilder();
			for (String r : roles) {
				if (i > 0) {
					t.append(",");
				}
				t.append(r);
				i++;
			}
			return t.toString();
		}
		return null;
	}
}
