package com.aizhixin.cloud.orgmanager.electrict.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Created by jianwei.wu
 * @E-mail wujianwei@aizhixin.com
 */
@ApiModel
@Data
public class ElectricFenceStatisticsDTO {
    @ApiModelProperty(value = "学生ID")
    private Long id;
    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "工号(学号)")
    private String jobNumber;
    @ApiModelProperty(value = "班级名称")
    private String classesName;
    @ApiModelProperty(value = "专业名称")
    private String professionalName;
    @ApiModelProperty(value = "学院名称")
    private String collegeName;
    @ApiModelProperty(value = "被检测到次数")
    private Long checkCount;
    @ApiModelProperty(value = "日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date checkdate;
    @ApiModelProperty(value = "曾离开")
    private String leave;
    @ApiModelProperty(value = "当前位置")
    private String address;
    @ApiModelProperty(value = "离线状态")
    private String onlinStatus;
    @ApiModelProperty(value = "备注")
    private String remark;
}
