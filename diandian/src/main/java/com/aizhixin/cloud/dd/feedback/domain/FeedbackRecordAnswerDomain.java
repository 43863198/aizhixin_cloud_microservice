package com.aizhixin.cloud.dd.feedback.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@ApiModel(description = "反馈记录题目")
@ToString
@Data
public class FeedbackRecordAnswerDomain {
    @ApiModelProperty(value = "项目")
    private String subject;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "分数")
    private Integer score;

    @ApiModelProperty(value = "分数")
    private Float score2;

    @ApiModelProperty(value = "填写答案")
    private String answer;

    @ApiModelProperty(value = "填写答案")
    private String answer2;

    @ApiModelProperty(value = "选项列表")
    private List<FeedbackTempletOptionsDomain> optionList;
}
