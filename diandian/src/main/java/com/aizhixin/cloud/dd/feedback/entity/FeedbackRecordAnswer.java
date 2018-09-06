package com.aizhixin.cloud.dd.feedback.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractOnlyIdAndCreatedDateEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * @author hsh
 */
@Entity(name = "DD_FEEDBACK_RECORD_ANSWER")
@ToString
public class FeedbackRecordAnswer extends AbstractOnlyIdAndCreatedDateEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "record_id")
    @Getter
    @Setter
    private FeedbackRecord record;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "templet_ques_id")
    @Getter
    @Setter
    private FeedbackTempletQues templetQues;

    @Column(name = "a_answer")
    @Getter
    @Setter
    private String answer;

}
