package com.aizhixin.cloud.ew.prospectsreading.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description="职位信息")
@Data
public class PositionQueryListDomain {
    @ApiModelProperty(value = "ID", position=1)
    private Long id;
    @ApiModelProperty(value = "name 职位名称", position=2)
    private String name;
    @ApiModelProperty(value = "type 职位类型", position=3)
    private String type;
    @ApiModelProperty(value = "publishStatus 发布状态(0未发布，1已发布)", position=9)
    private Integer publishStatus;

    public PositionQueryListDomain(Long id, String name, String type, Integer publishStatus) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.publishStatus = publishStatus;
    }
}
