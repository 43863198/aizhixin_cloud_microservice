package com.aizhixin.cloud.orgmanager.training.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import java.util.Date;
import java.util.List;

/**
 * @author Created by jianwei.wu on ${date} ${time}
 * @E-mail wujianwei@aizhixin.com
 */
@ApiModel
@Data
public class TrainingGropDTO implements java.io.Serializable{
    @ApiModelProperty(value = "实训小组id")
    private Long id;
    @ApiModelProperty(value = "实训小组名称")
    private String groupName;
    @ApiModelProperty(value = "实训小开始束时间")
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
    @ApiModelProperty(value = "企业导师id")
    private Long corporateMentorsId;
    @ApiModelProperty(value = "学校id")
    private Long orgId;
    @ApiModelProperty(value = "学校老师id")
    private Long teacherId;
    @ApiModelProperty(value = "学生id")
    private List<Long> studentIds;
    @ApiModelProperty(value = "实践参与计划设置")
    private TrainingGropSetDTO setDTO = new TrainingGropSetDTO();

    public TrainingGropDTO(){
    }
    
    public TrainingGropDTO(Long id,String groupName){
    	this.id = id;
    	this.groupName = groupName;
    }
    
    public TrainingGropDTO(Long id,String groupName,Date startDate,Date endDate,Long corporateMentorsId,Long orgId,Long teacherId,List<Long> studentIds){
    	this.id =id;
    	this.groupName = groupName;
    	this.startDate = startDate;
    	this.endDate = endDate;
    	this.corporateMentorsId = corporateMentorsId;
    	this.orgId = orgId;
    	this.teacherId = teacherId;
    	this.studentIds = studentIds;
    }

}
