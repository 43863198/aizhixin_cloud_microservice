package com.aizhixin.cloud.dd.feedback.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author hsh
 */
@Data
@ApiModel(description = "反馈模板题目")
public class FeedbackTempletQuesDTO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "项目")
    private String subject;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "分数")
    private Integer score;

    @ApiModelProperty(value = "分数")
    private Float score2;

    @ApiModelProperty(value = "选项列表")
    private List<FeedbackTempletOptionsDTO> optionList;

    public FeedbackTempletQuesDTO(){

    }

    public FeedbackTempletQuesDTO(Long id, String subject, String content, Float score) {
        this.id = id;
        this.subject = subject;
        this.content = content;
        this.score2 = score;
    }
}
