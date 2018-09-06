package com.aizhixin.cloud.orgmanager.training.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Created by jianwei.wu
 * @E-mail wujianwei@aizhixin.com
 */
@ApiModel
@Data
public class CorporateMentorsInfoDTO {
    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "工号")
    private String jobNumber;
   @ApiModelProperty(value = "姓名")
   private String name;
    @ApiModelProperty(value = "学校id")
    private Long orgId;
    @ApiModelProperty(value = "企业id")
    private Long enterpriseId;
    @ApiModelProperty(value = "企业名称")
    private String enterpriseName;
    @ApiModelProperty(value = "企业地址")
    private String companyAddress;
    @ApiModelProperty(value = "部门")
    private String department;
    @ApiModelProperty(value = "职位")
    private String position;
    @ApiModelProperty(value = "邮箱")
    private String mailbox;
    @ApiModelProperty(value = "手机")
    private  String phone;
    @ApiModelProperty(value = "省")
    private String province;
    @ApiModelProperty(value = "市")
    private String city;
}
