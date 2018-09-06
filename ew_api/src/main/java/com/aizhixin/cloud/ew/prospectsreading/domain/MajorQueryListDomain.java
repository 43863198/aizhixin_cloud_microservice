package com.aizhixin.cloud.ew.prospectsreading.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description="专业信息")
@Data
public class MajorQueryListDomain {
    @ApiModelProperty(value = "ID", position=1)
    private Long id;
    @ApiModelProperty(value = "name 专业名称", position=2)
    private String name;
    @ApiModelProperty(value = "type 专业类型", position=3)
    private String type;
    @ApiModelProperty(value = "publishStatus 发布状态(0未发布，1已发布)", position=4)
    private Integer publishStatus;

    public MajorQueryListDomain(Long id, String name, String type, Integer publishStatus) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.publishStatus = publishStatus;
    }
}
