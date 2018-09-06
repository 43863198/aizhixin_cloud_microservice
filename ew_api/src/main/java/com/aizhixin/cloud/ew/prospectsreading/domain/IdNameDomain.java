package com.aizhixin.cloud.ew.prospectsreading.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description="id、name信息")
@Data
public class IdNameDomain {
    @ApiModelProperty(value = "ID", position=1)
    private Long id;
    @ApiModelProperty(value = "name 名称", position=2)
    private String name;

    public IdNameDomain () {}

    public IdNameDomain(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
