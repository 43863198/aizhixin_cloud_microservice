package com.aizhixin.cloud.dd.feedback.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;
import com.aizhixin.cloud.dd.common.entity.AbstractOnlyIdAndCreatedDateEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author hsh
 */
@Entity(name = "DD_FEEDBACK_TEMPLET_QUES")
@ToString
public class FeedbackTempletQues extends AbstractOnlyIdAndCreatedDateEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "templet_id")
    @Getter
    @Setter
    private FeedbackTemplet templet;

//    @Column(name = "templet_id")
//    @Getter
//    @Setter
//    private Long templet;

    @Column(name = "subject")
    @Getter
    @Setter
    private String subject;

    @Column(name = "content")
    @Getter
    @Setter
    private String content;

    @Column(name = "score")
    @Getter
    @Setter
    private Integer score;

    @Column(name = "tempgroup")
    @Getter
    @Setter
    private Integer group;

    public FeedbackTempletQues() {
    }

    public FeedbackTempletQues(FeedbackTemplet templet, String subject, String content, Integer score, Integer group) {
        this.templet = templet;
        this.subject = subject;
        this.content = content;
        this.score = score;
        this.group = group;
    }
}
