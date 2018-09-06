package com.aizhixin.cloud.dd.questionnaire.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "问卷分配到人权重")
@Data
public class QuestionnaireAssignUserWeightDTO {
    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "权重")
    private Long weight;
}
