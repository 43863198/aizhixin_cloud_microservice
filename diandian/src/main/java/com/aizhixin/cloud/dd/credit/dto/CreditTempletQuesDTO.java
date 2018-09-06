package com.aizhixin.cloud.dd.credit.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@ApiModel(description = "学分模板试题")
@ToString
@Data
public class CreditTempletQuesDTO {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "templetId")
    private Long templetId;

    @ApiModelProperty(value = "content")
    private String content;

    @ApiModelProperty(value = "minScore")
    private Float minScore;

    @ApiModelProperty(value = "maxScore")
    private Float maxScore;
}
