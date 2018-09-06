package com.aizhixin.cloud.orgmanager.company.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@ApiModel(description="学校和电话学号列表查询条件封装")
public class StudentQueryByJobNumberOrPhones {
    @ApiModelProperty(value = "学校ID", required = true, position=1)
    @Getter @Setter private Long orgId;

    @ApiModelProperty(value = "电话号码或学号列表", required = true, position=7)
    @Getter @Setter private Set<String> phoneOrJobNumbers;
}
