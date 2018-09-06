
package com.aizhixin.cloud.dd.questionnaire.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.aizhixin.cloud.dd.common.entity.AbstractEntitytwo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author meihua.li
 */
@Entity(name = "DD_QUESTIONS")
public class Questions extends AbstractEntitytwo {
    private static final long serialVersionUID = 1073324416002687849L;
    /**
     * 题号
     */
    @Column(name = "NO")
    @Getter
    @Setter
    private Integer no;
    /**
     * 问卷
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "QUESTIONNAIRE_ID")
    @Getter
    @Setter
    private Questionnaire questionnaire;
    /**
     * 题目名称
     */
    @Column(name = "NAME")
    @Getter
    @Setter
    private String name;
    /**
     * 题目分值
     */
    @Column(name = "SCORE")
    @Getter
    @Setter
    private Integer score;

    /**
     * 答题选择限制
     */
    @Column(name = "answer_limit")
    @Getter
    @Setter
    private Integer answerLimit = 0;

    /**
     * 是否是单选
     */
    @Column(name = "radio")
    @Getter
    @Setter
    private boolean radio;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "questions")
    @Getter
    @Setter
    private List<QuestionsChoice> questionsChoice;
}
