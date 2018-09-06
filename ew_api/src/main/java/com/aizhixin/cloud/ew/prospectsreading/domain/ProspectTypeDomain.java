package com.aizhixin.cloud.ew.prospectsreading.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(description="职位、专业分类信息")
@Data
public class ProspectTypeDomain {
    @ApiModelProperty(value = "typeName 分类名称", position=1)
    private String typeName;
    @ApiModelProperty(value = "subNodes 子节点", position=2)
    private List<IdNameDomain> subNodes;
}
