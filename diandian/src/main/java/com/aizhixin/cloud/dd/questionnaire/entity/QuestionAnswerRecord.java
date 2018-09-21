/**
 *
 */
package com.aizhixin.cloud.dd.questionnaire.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.aizhixin.cloud.dd.common.entity.AbstractEntitytwo;

import lombok.Getter;
import lombok.Setter;

/**
 * 答题记录
 *
 * @author zhen.pan
 */
@Entity
@Table(name = "DD_QUESTION_ANSWER_RECORD")
public class QuestionAnswerRecord extends AbstractEntitytwo implements Serializable {

    private static final long serialVersionUID = -4412554043983769571L;
    /**
     * 具体分配给确定 学生记录
     */
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "QUESTIONNAIRE_ASSGIN_STUDENTS_ID")
    @Getter
    @Setter
    private QuestionnaireAssginStudents questionnaireAssginStudents;
    /**
     * 题目
     */
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "QUESTIONS_ID")
    @Getter
    @Setter
    private Questions questions;
    /**
     * 评分
     */
    @Column(name = "SCORE")
    @Getter
    @Setter
    private Float score;

    /**
     * 权重评分
     */
    @Column(name = "weight_score")
    @Getter
    @Setter
    private Float weightScore;

    /**
     * 答题结果
     */
    @Column(name = "answer")
    @Getter
    @Setter
    private String answer;

}
