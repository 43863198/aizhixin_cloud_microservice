package com.aizhixin.cloud.studentpractice.common.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Created by jianwei.wu on ${date} ${time}
 * @E-mail wujianwei@aizhixin.com
 */
@ApiModel
@Data
@AllArgsConstructor
public class StudentDTO {
    @ApiModelProperty(value = "学生id")
    private Long id;
    @ApiModelProperty(value = "学生账号id")
    private Long accountId;
    @ApiModelProperty(value = "学生名字")
    private String name;
    @ApiModelProperty(value = "学号")
    private String jobNumber;
    @ApiModelProperty(value = "班级")
    private String classesName;
    @ApiModelProperty(value = "班级id")
    private Long classesId;
    @ApiModelProperty(value = "专业名称")
    private String professionalName;
    @ApiModelProperty(value = "专业id")
    private Long professionalId;
    @ApiModelProperty(value = "学院名称")
    private String collegeName;
    @ApiModelProperty(value = "学院")
    private Long collegeId;
    @ApiModelProperty(value = "手机号")
    private String phone;
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "性别")
    private String sex;
    
    public StudentDTO(Long id, Long accountId, String name, String jobNumber, String classesName, Long classesId, String professionalName, Long professionalId, String collegeName, Long collegeId){
    	this.id = id;
    	this.accountId = accountId;
    	this.name = name;
    	this.jobNumber = jobNumber;
    	this.classesName = classesName;
    	this.classesId = classesId;
    	this.professionalName = professionalName;
    	this.professionalId = professionalId;
    	this.collegeName = collegeName;
    	this.collegeId = collegeId;
    }
}
