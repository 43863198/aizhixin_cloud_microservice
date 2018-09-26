package com.aizhixin.cloud.dd.questionnaire.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.aizhixin.cloud.dd.common.entity.AbstractOnlyIdAndCreatedDateEntity;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "DD_QUESTION_CHOICE")
public class QuestionsChoice extends AbstractOnlyIdAndCreatedDateEntity {

    /**
     * @fieldName: serialVersionUID
     * @fieldType: long
     */
    private static final long serialVersionUID = 1L;

    /**
     * 试题id
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "question_id")
    @Getter
    @Setter
    private Questions questions;

    /**
     * 试题选项
     */
    @Column(name = "choice")
    @Getter
    @Setter
    private String choice;

    /**
     * 选项内容
     */
    @Column(name = "content")
    @Getter
    @Setter
    private String content;

    /**
     * 选项分值
     */
    @Column(name = "score")
    @Getter
    @Setter
    private String score;
}
