package com.aizhixin.cloud.dd.feedback.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;
import com.aizhixin.cloud.dd.common.entity.AbstractOnlyIdAndCreatedDateEntity;
import com.aizhixin.cloud.dd.common.entity.AbstractOnlyIdEntity;
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
@Entity(name = "DD_FEEDBACK_TEMPLET_OPTIONS")
@ToString
public class FeedbackTempletOptions extends AbstractOnlyIdAndCreatedDateEntity {

    @Column(name = "ques_option")
    @Getter
    @Setter
    private String option;

    @Column(name = "ques_content")
    @Getter
    @Setter
    private String content;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "templet_ques_id")
    @Getter
    @Setter
    private FeedbackTempletQues templetQues;

    public FeedbackTempletOptions() {

    }

    public FeedbackTempletOptions(String option, String content, FeedbackTempletQues templetQues) {
        this.option = option;
        this.content = content;
        this.templetQues = templetQues;
    }
}
