package com.aizhixin.cloud.ew.prospectsreading.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description="职位能力分类信息")
@Data
public class PositionAbilityListDomain {
    @ApiModelProperty(value = "content 职位能力分类内容", position=1)
    private String content;
    @ApiModelProperty(value = "score 职位能力分类分值", position=2)
    private Double score;
}
