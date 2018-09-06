package com.aizhixin.cloud.ew.prospectsreading.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(description="职位信息")
@Data
public class PositionDomain {
    @ApiModelProperty(value = "ID", position=1)
    private Long id;
    @ApiModelProperty(value = "name 职位名称", position=2)
    private String name;
    @ApiModelProperty(value = "type 职位类型", position=3)
    private String type;
    @ApiModelProperty(value = "professionalQualitys 职素分项列表", position=4)
    private List<PositionAbilityListDomain> professionalQualitys;
    @ApiModelProperty(value = "technicalAbilitys 技能分项列表", position=5)
    private List<PositionAbilityListDomain> technicalAbilitys;
    @ApiModelProperty(value = "knowledges 知识分项列表", position=6)
    private List<PositionAbilityListDomain> knowledges;
    @ApiModelProperty(value = "desc 职位描述", position=8)
    private String desc;
    @ApiModelProperty(value = "publishStatus 发布状态(0未发布，1已发布)", position=9)
    private Integer publishStatus;
}
