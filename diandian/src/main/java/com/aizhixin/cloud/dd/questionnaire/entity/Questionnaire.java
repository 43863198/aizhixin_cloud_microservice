package com.aizhixin.cloud.dd.questionnaire.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;

import org.springframework.format.annotation.DateTimeFormat;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

/**
 * @author meihua.li
 */
@Entity
@Table(name = "DD_QUESTIONNAIRE")
public class Questionnaire extends AbstractEntity {
    private static final long serialVersionUID = -72356318415851577L;

    /**
     * 问卷名称
     */
    @Column(name = "NAME")
    @Getter
    @Setter
    private String name;

    /**
     * 总分
     */
    @Column(name = "TOTAL_SCORE")
    @Getter
    @Setter
    private Float totalScore;

    /**
     * 题目数量
     */
    @Column(name = "TOTAL_QUESTIONS")
    @Getter
    @Setter
    private Integer totalQuestions;

    /**
     * 状态（初始化、分配）
     */
    @Column(name = "STATUS")
    @Getter
    @Setter
    private Integer status;

    /**
     * 学校ID
     */
    @Column(name = "ORGAN_ID")
    @Getter
    @Setter
    private Long organId;

    /**
     * 截止日期
     */
    @Column(name = "END_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date endDate;

    // 是否量化
    @Column(name = "quantification")
    @Getter
    @Setter
    private boolean quantification;

    // 是否选择题
    @Column(name = "choice_question")
    @Getter
    @Setter
    private boolean choiceQuestion;

    /**
     * 选项
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "questionnaire")
    @Getter
    @Setter
    private List<Questions> questions;

    //是否评语
    @Column(name = "q_comment")
    @Getter
    @Setter
    private boolean qComment;

    @Column(name = "ques_type")
    @Getter
    @Setter
    private Integer quesType;

    @Column(name = "allocation_num")
    @Getter
    @Setter
    private Integer allocationNum;
}
