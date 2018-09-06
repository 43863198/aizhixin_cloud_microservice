/**
 * 
 */
package com.aizhixin.cloud.studentpractice.task.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@ApiModel(description="学生信息")
@Data
@ToString
public class StuInforDomain {
	
	@ApiModelProperty(value = "学生ID", required = false)
	public Long id;

	@ApiModelProperty(value = "学生名称", required = false)
	public String name;
	
	@ApiModelProperty(value = "学生学号", required = false)
	public String jobNum;
	
	@ApiModelProperty(value = "学生头像", required = false)
	public String avatar;
	
	@ApiModelProperty(value = "学生性别", required = false)
	public String sex;
	
	@ApiModelProperty(value = "学生所属院系", required = false)
	public String stuCollege;
	
	@ApiModelProperty(value = "学生所属专业", required = false)
	public String stuProfession;
	
	@ApiModelProperty(value = "学生所在行政班名称", required = false)
	public String stuClassName;
	
	@ApiModelProperty(value = "学生手机号", required = false)
	public String stuPhone;
	
	@ApiModelProperty(value = "导师所在公司名称", required = false)
	public String mentorCompanyName;
	
	@ApiModelProperty(value = "导师所在公司地址", required = false)
	public String mentorCompanyAddr;
	
	@ApiModelProperty(value = "导师id", required = false)
	public Long mentorId;
	
	@ApiModelProperty(value = "导师名称", required = false)
	public String mentorName;
	
	@ApiModelProperty(value = "导师手机号", required = false)
	public String mentorPhone;
	
	@ApiModelProperty(value = "辅导员id", required = false)
	private Long counselorId;
	
	@ApiModelProperty(value = "辅导员名称", required = false)
	private String counselorName;
	
	@ApiModelProperty(value = "辅导员工号", required = false)
	private String counselorJobNum;
	
	@ApiModelProperty(value = "班级id", required = false)
	private Long classId;
	
	@ApiModelProperty(value = "院系id", required = false)
	private Long collegeId;
	
	@ApiModelProperty(value = "专业id", required = false)
	private Long professionalId;
	
	@ApiModelProperty(value = "机构id", required = false)
	protected Long orgId;
	
	@ApiModelProperty(value = "实践小组id", required = false)
	protected Long trainingGroupId;
	
	@ApiModelProperty(value = "所属实践小组名称名称", required = false)
	private String trainingGroupName;
}
