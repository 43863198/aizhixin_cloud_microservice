package com.aizhixin.cloud.dd.credit.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author hsh
 */
@Data
@ApiModel(description = "学分模板")
public class CreditTempletDTO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "orgId")
    private Long orgId;

    @ApiModelProperty(value = "name")
    private String name;

    @ApiModelProperty(value = "题目列表")
    private List<CreditTempletQuesDTO> quesList;

}
