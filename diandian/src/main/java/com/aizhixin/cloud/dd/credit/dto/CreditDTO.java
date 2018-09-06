package com.aizhixin.cloud.dd.credit.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author hsh
 */
@Data
@ApiModel(description = "学分")
public class CreditDTO {
    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "name")
    private String name;
    @ApiModelProperty(value = "templetId")
    private Long templetId;
    @ApiModelProperty(value = "templetName")
    private String templetName;
    @ApiModelProperty(value = "学分评分人")
    List<Long> ratingStus;
}
