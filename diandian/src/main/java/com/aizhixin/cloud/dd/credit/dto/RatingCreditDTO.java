package com.aizhixin.cloud.dd.credit.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author hsh
 */
@Data
@ApiModel(description = "打分")
public class RatingCreditDTO {

    @ApiModelProperty(value = "creditId")
    private Long creditId;

    @ApiModelProperty(value = "stuId")
    private Long stuId;

    @ApiModelProperty(value = "quesList")
    private List<RatingCreditQuesDTO> quesList;

}
