package com.aizhixin.cloud.dd.questionnaire.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class QuestionnaireDataDTO {
	//问卷名称
    private String questionnaireName;
    //截止时间
    private Date   endDate;
    //问卷分数
    private String questionnaireTotalScore;
    //问卷平均分
    private String avgScore;
    //问卷调查人数
    private Integer totalPeple;
    //问卷提交人数
    private Integer commitNum;
    //问卷占比
    private String commitZb;
    //问卷未提交人数
    private Integer noCommitNum;
    //问卷未提交人数占比
    private String noCommitZb;
    //问卷试题信息统计
    private List<QuestionnaireQuestionDataDTO> questionnaireQuestionDataDTOs=new ArrayList<>();

    //问卷类型
    private Integer quesType;
}
