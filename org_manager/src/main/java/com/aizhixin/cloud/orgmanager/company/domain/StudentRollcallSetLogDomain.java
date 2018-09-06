package com.aizhixin.cloud.orgmanager.company.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import java.util.Date;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-10-18
 */
@Data
@ApiModel(description="暂停考勤设置日志")
@AllArgsConstructor
public class StudentRollcallSetLogDomain {
    @ApiModelProperty(value = "操作时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date operationTime;
    @ApiModelProperty(value = "操作人")
    private String operator;
    @ApiModelProperty(value = "操作类型 10暂停考勤，20恢复考勤")
    private Integer opt;
    @ApiModelProperty(value = "备注理由")
    private String msg;
    @ApiModelProperty(value = "被操作人学号")
    private String stuJobNumber;
    @ApiModelProperty(value = "被操作人姓名")
    private String stuName;
    @ApiModelProperty(value = "班级")
    private String stuClassesName;
    @ApiModelProperty(value = "年级")
    private String stuClassesYear;
    @ApiModelProperty(value = "专业")
    private String stuProfessionalName;
    @ApiModelProperty(value = "学院")
    private String stuCollegeName;

}
