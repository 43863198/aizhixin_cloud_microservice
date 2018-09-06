package com.aizhixin.cloud.dd.feedback.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hsh
 */
@Data
@ApiModel(description = "反馈题目")
public class FeedbackRecordAnswerDTO {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "题目id")
    private Long quesId;

    @ApiModelProperty(value = "填写答案")
    private String answer;

    public FeedbackRecordAnswerDTO() {

    }

    public FeedbackRecordAnswerDTO(Long id, Long quesId, String answer) {
        this.id = id;
        this.quesId = quesId;
        this.answer = answer;
    }

    public FeedbackRecordAnswerDTO(Long quesId, String answer) {
        this.quesId = quesId;
        this.answer = answer;
    }
}
