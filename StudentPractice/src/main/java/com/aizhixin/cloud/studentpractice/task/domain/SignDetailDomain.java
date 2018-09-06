/**
 * 
 */
package com.aizhixin.cloud.studentpractice.task.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@ApiModel(description = "当前有效实践参与计划信息")
@Data
@EqualsAndHashCode(callSuper = false)
public class SignDetailDomain {


	@ApiModelProperty(value = "学生id")
	private Long studentId;
	
	@ApiModelProperty(value = "签到地图经纬度")
	private String gpsLocation;
	
	@ApiModelProperty(value = "签到所在具体位置")
	private String gpsDetail;
	
	@ApiModelProperty(value = "签到网络类型")
	private String gpsType;
	
	@ApiModelProperty(value = "签到时间")
	private String signTime;
	
	@ApiModelProperty(value = "签到状态[未提交:10,已到:20,未到:30,请假:40,迟到:50]")
	private String signStatus;
	
	@ApiModelProperty(value = "应签到时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
	private Date createdDate;
}
