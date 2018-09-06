package com.aizhixin.cloud.dd.feedback.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * @author hsh
 */
@Entity(name = "DD_FEEDBACK_TEMPLET")
@ToString
public class FeedbackTemplet extends AbstractEntity {

    @Column(name = "ORG_ID")
    @Getter
    @Setter
    private Long orgId;

    //教学反馈 督导反馈
    @Column(name = "TYPE")
    @Getter
    @Setter
    private Integer type;//

    //试题类型 打分 选项 简答
    @Column(name = "QUES_TYPE")
    @Getter
    @Setter
    private Integer quesType;

    //打分题总分
    @Column(name = "TOTAL_SCORE")
    @Getter
    @Setter
    private Integer totalScore;

    public FeedbackTemplet() {
    }

    public FeedbackTemplet(Long orgId, Integer type, Integer quesType, Integer totalScore) {
        this.orgId = orgId;
        this.type = type;
        this.quesType = quesType;
        this.totalScore = totalScore;
    }
}
