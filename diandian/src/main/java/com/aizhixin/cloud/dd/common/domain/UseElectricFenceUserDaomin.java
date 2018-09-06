package com.aizhixin.cloud.dd.common.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Created by jianwei.wu on ${date} ${time}
 * @E-mail wujianwei@aizhixin.com
 */
@ApiModel(description="使用电子围栏的用户信息")
@Data
public class UseElectricFenceUserDaomin {
    @ApiModelProperty(value = "ID", allowableValues = "range[1,infinity]", position=1)
    private Long id;
    @NotNull
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
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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
    @ApiModelProperty(value = "经纬度")
    private String lltude;
    @ApiModelProperty(value = "是否超出范围   1:超出范围   0:在范围内")
    private Integer outOfRange;

    public UseElectricFenceUserDaomin(){};

    public UseElectricFenceUserDaomin(Long id, String name, String jobNumber, String classesName, String professionalName, String collegeName, Long checkCount){
        this.id = id;
        this.name = name;
        this.jobNumber = jobNumber;
        this.classesName = classesName;
        this.professionalName = professionalName;
        this.collegeName = collegeName;
        this.checkCount = checkCount;
    };
    public UseElectricFenceUserDaomin(Long id, String name, String jobNumber, String classesName, String professionalName, String collegeName, Long checkCount, Date checkdate){
        this.id = id;
        this.name = name;
        this.jobNumber = jobNumber;
        this.classesName = classesName;
        this.professionalName = professionalName;
        this.collegeName = collegeName;
        this.checkCount = checkCount;
        this.checkdate = checkdate;
    };
    public UseElectricFenceUserDaomin(Date checkdate, String address, Integer outOfRange, String lltude){
        this.checkdate = checkdate;
        this.address = address;
        this.outOfRange = outOfRange;
        this.lltude = lltude;
    };




}
