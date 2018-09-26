package com.aizhixin.cloud.dd.feedback.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author hsh
 */
@Data
@ApiModel(description = "反馈模板")
public class FeedbackTempletDTO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "orgId")
    private Long orgId;

    @ApiModelProperty(value = "10:信息员反馈 20:督导反馈")
    private Integer type;

    @ApiModelProperty(value = "试题类型 10:打分 20:选项 30:简答")
    private Integer quesType;

    @ApiModelProperty(value = "打分题总分")
    private Integer totalScore;

    @ApiModelProperty(value = "打分题总分")
    private Float totalScore2;

    @ApiModelProperty(value = "教师评价列表")
    private List<FeedbackTempletQuesDTO> teacherQuesList;

    @ApiModelProperty(value = "学风评价列表")
    private List<FeedbackTempletQuesDTO> styleQuesList;

    @ApiModelProperty(value = "题目列表")
    private List<FeedbackTempletQuesDTO> quesList;

    public FeedbackTempletDTO() {

    }

    public FeedbackTempletDTO(Long id, Long orgId, Integer quesType, Float totalScore) {
        this.id = id;
        this.orgId = orgId;
        this.quesType = quesType;
        this.totalScore2 = totalScore;
    }
}
