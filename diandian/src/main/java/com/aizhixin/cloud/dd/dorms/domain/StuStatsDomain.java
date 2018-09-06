package com.aizhixin.cloud.dd.dorms.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class StuStatsDomain {
    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "学生姓名")
    private String stuName;
    @ApiModelProperty(value = "学生id")
    private Long stuId;
    @ApiModelProperty(value = "性别")
    private String gender;
    @ApiModelProperty(value = "专业名称")
    private String profName;
    @ApiModelProperty(value = "专业id")
    private Long profId;
    @ApiModelProperty(value = "房间id")
    private Long roomId;
    @ApiModelProperty(value = "房间号")
    private String roomNo;
    @ApiModelProperty(value = "床位id")
    private Long bedId;
    @ApiModelProperty(value = "床位名称")
    private String bedName;
    @ApiModelProperty(value = "手机号")
    private String phone;
    @ApiModelProperty(value = "选择时间")
    private Date createdDate;
}
