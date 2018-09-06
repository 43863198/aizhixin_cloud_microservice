package com.aizhixin.cloud.studentpractice.common.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;

import java.util.Date;

/**
 * @author Created by jianwei.wu
 * @E-mail wujianwei@aizhixin.com
 */
@ApiModel
@Data
@AllArgsConstructor
public class TrainingGroupInfoDTO implements java.io.Serializable{
    /**
     * 实训组id
     */
    private Long id;
    /**
     * 实训组编码
     */
    private String gropCode;
    /**
     * 实训组名称
     */
    private String gropName;
    /**
     * 实训企业导师id
     */
    private Long corporateMentorsInfoId;
    /**
     * 实训企业导师姓名
     */
    private String corporateMentorsInfoName;
    /**
     * 学校老师id
     */
    private Long teacherId;
    /**
     * 学校老师id
     */
    private String teacherName;
    /**
     * 学校老师工号
     */
    private String jobNumber;
    /**
     * 学校老师学院名称
     */
    private String collegeName;
    
    @ApiModelProperty(value = "实践小组状态[end:结束,notOver:未结束]")
    private String status;
    
    @ApiModelProperty(value = "学生人数")
    private Long studentCount;
    
//	@ApiModelProperty(value = "是否日志评分")
//	private Boolean summaryIsGrade;

    @ApiModelProperty(value = "实训小组结束时间")
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date startDate;
    @ApiModelProperty(value = "结束时间")
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date endDate;

    
    public TrainingGroupInfoDTO(Long id,String gropCode,String gropName,Long corporateMentorsInfoId,String corporateMentorsInfoName,
    		Long teacherId,String teacherName,String jobNumber,String collegeName,Date startDate,Date endDate){
    	this.id =id;
    	this.gropCode=gropCode;
    	this.gropName=gropName;
    	this.corporateMentorsInfoId=corporateMentorsInfoId;
    	this.corporateMentorsInfoName=corporateMentorsInfoName;
    	this.teacherId=teacherId;
    	this.teacherName=teacherName;
    	this.jobNumber=jobNumber;
    	this.collegeName=collegeName;
    	this.startDate=startDate;
    	this.endDate=endDate;
    }
    
    public TrainingGroupInfoDTO(Long id,String gropCode,String gropName,Long corporateMentorsInfoId,String corporateMentorsInfoName,
    		Long teacherId,String teacherName,String jobNumber,Date startDate,Date endDate){
    	this.id =id;
    	this.gropCode=gropCode;
    	this.gropName=gropName;
    	this.corporateMentorsInfoId=corporateMentorsInfoId;
    	this.corporateMentorsInfoName=corporateMentorsInfoName;
    	this.teacherId=teacherId;
    	this.teacherName=teacherName;
    	this.jobNumber=jobNumber;
    	this.startDate=startDate;
    	this.endDate=endDate;
    }
    
    public TrainingGroupInfoDTO(Long id,String gropName,Date startDate,Date endDate){
    	this.id =id;
    	this.gropName=gropName;
    	this.startDate=startDate;
    	this.endDate=endDate;
    }
    
    public TrainingGroupInfoDTO(Long id,Long studentCount){
    	this.id =id;
    	this.studentCount=studentCount;
    }
}
