package com.aizhixin.cloud.dd.feedback.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@ApiModel(description = "反馈记录题目选项")
@ToString
@Data
public class FeedbackTempletOptionsDomain {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "选项")
    private String option;

    @ApiModelProperty(value = "内容")
    private String content;
}
