package com.aizhixin.cloud.dd.feedback.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hsh
 */
@Data
@ApiModel(description = "反馈模板题目选项")
public class FeedbackTempletOptionsDTO {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "选项")
    private String option;

    @ApiModelProperty(value = "内容")
    private String content;

    public FeedbackTempletOptionsDTO() {

    }

    public FeedbackTempletOptionsDTO(Long id, String option, String content) {
        this.id = id;
        this.option = option;
        this.content = content;
    }
}
