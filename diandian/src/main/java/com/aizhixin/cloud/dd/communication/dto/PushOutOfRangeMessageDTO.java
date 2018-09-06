package com.aizhixin.cloud.dd.communication.dto;

import com.aizhixin.cloud.dd.rollcall.dto.BaseDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
@ApiModel
@Data
@AllArgsConstructor
public class PushOutOfRangeMessageDTO extends BaseDTO {
	
	@NotNull
    @ApiModelProperty(value = "学生id", required = false)
    protected Long            id;
	
	@ApiModelProperty(value = "姓名", required = false)
	protected String            name;
	@ApiModelProperty(value = "学号", required = false)
	protected String            jobNumber;
	
	@ApiModelProperty(value = "地址", required = false)
	protected String            address;
	
	@ApiModelProperty(value = "离校时间", required = false)
	@CreatedDate
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	protected Date            time;
	
	@ApiModelProperty(value = "推送时间", required = false)
	@CreatedDate
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	protected Date            pushTime;

}
