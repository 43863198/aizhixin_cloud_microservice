package com.aizhixin.cloud.paycallback.common.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel(description="id、name、count信息")
@NoArgsConstructor
@ToString
public class IdNameCountDomain {

    @ApiModelProperty(value = "ID", position=1)
    @Getter @Setter private Long id;

    @ApiModelProperty(value = "名称", position=2)
    @Getter @Setter private String name;

    @ApiModelProperty(value = "COUNT", position=3)
    @Getter @Setter  private Long count;

    public IdNameCountDomain(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
