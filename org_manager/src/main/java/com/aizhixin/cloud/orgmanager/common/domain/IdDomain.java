package com.aizhixin.cloud.orgmanager.common.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="id信息")
@ToString
@NoArgsConstructor
public class IdDomain {
    @ApiModelProperty(value = "ID")
    @Getter @Setter private Long id;
}
