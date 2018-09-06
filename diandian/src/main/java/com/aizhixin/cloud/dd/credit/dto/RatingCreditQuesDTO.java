package com.aizhixin.cloud.dd.credit.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hsh
 */
@Data
@ApiModel(description = "打分试题")
public class RatingCreditQuesDTO {

    @ApiModelProperty(value = "quesId")
    private Long quesId;

    @ApiModelProperty(value = "score")
    private Float score;
}
