package com.aizhixin.cloud.dd.credit.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@ApiModel(description = "学生详情")
@ToString
@Data
public class CreditStudentDetailsDomain {
    @ApiModelProperty(value = "quesId")
    private Long quesId;
    @ApiModelProperty(value = "content")
    private String content;
    @ApiModelProperty(value = "minScore")
    private Float minScore;
    @ApiModelProperty(value = "maxScore")
    private Float maxScore;
    @ApiModelProperty(value = "scores")
    private String scores;
    @ApiModelProperty(value = "avgScore")
    private Float avgScore;
}
