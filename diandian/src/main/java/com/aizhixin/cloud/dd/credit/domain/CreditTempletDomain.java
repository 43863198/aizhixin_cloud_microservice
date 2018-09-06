package com.aizhixin.cloud.dd.credit.domain;

import com.aizhixin.cloud.dd.credit.entity.CreditTempletQues;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@ApiModel(description = "学分模板")
@ToString
@Data
public class CreditTempletDomain {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "orgId")
    private Long orgId;

    @ApiModelProperty(value = "name")
    private String name;

    @ApiModelProperty(value = "打分题总分")
    private Float totalScore;

    @ApiModelProperty(value = "题目列表")
    private List<CreditTempletQues> quesList;
}
