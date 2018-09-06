package com.aizhixin.cloud.dd.dorms.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProfStatsDomain {
    @ApiModelProperty(value = "专业名称")
    private String profName;
    @ApiModelProperty(value = "专业id")
    private Long profId;
    @ApiModelProperty(value = "专业学生数")
    private Long profStuNum;
    @ApiModelProperty(value = "床位数量")
    private Integer bedNum;
    @ApiModelProperty(value = "已选床位数量")
    private Integer selectedBedNum;
    @ApiModelProperty(value = "已选占比")
    private String selectedPct;
}
