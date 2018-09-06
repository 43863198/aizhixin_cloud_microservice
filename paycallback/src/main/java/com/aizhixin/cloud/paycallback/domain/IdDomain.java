package com.aizhixin.cloud.paycallback.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="id信息")
@NoArgsConstructor
@ToString
public class IdDomain {
    @ApiModelProperty(value = "ID", position=1)
    @Getter @Setter private String id;
}
