package com.aizhixin.cloud.dd.common.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description = "")
@ToString
public class IdNameCode extends IdNameDomain {
    @ApiModelProperty(value = "课程编码", required = true, position = 2)
    @Getter
    @Setter
    private String code;

}
