/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.Email;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.Date;

/**
 * @author zhengning
 *
 */
@ApiModel(description="辅导员信息")
@Data
public class ClassTeacherDomain {
	@ApiModelProperty(value = "辅导员账号ID")
	private Long accountId;
	@ApiModelProperty(value = "辅导员姓名")
	private String name;
	@ApiModelProperty(value = "辅导员工号")
	private String jobNumber;
	@ApiModelProperty(value = "辅导员性别(男性male|女性female)")
	private String sex;
	@ApiModelProperty(value = "学院名称")
	private String collegeName;
    @ApiModelProperty(value = "专业名称")
    private String professionalName;
    @ApiModelProperty(value = "班级ID")
    private Long classesId;
    @ApiModelProperty(value = "班级名称", position = 10)
    private String classesName;
    @ApiModelProperty(value = "学生学年")
    private String teachingYear;
    @ApiModelProperty(value = "学制")
    private String schoolingLength;
    
    public ClassTeacherDomain(){
    	
    }
    
    
    public ClassTeacherDomain(Long accountId,String name,String sex,String jobNumber,Long classId,String className,String collegeName,String professionalName,String teachingYear,String schoolingLength){
    	this.accountId = accountId;
    	this.name = name;
    	this.sex = sex;
    	this.jobNumber = jobNumber;
    	this.classesId = classId;
    	this.classesName = className;
    	this.collegeName = collegeName;
    	this.professionalName = professionalName;
    	this.teachingYear = teachingYear;
    	this.schoolingLength = schoolingLength;
    }
}
