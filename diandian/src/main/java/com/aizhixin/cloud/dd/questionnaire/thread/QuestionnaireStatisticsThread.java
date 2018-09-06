package com.aizhixin.cloud.dd.questionnaire.thread;

import com.aizhixin.cloud.dd.questionnaire.entity.Questionnaire;
import com.aizhixin.cloud.dd.questionnaire.serviceV2.QuestionnaireStatisticsService;

public class QuestionnaireStatisticsThread extends Thread {
    private QuestionnaireStatisticsService statisticsService;
    private String key;
    private Questionnaire questionnaire;

    public QuestionnaireStatisticsThread(QuestionnaireStatisticsService statisticsService, String key, Questionnaire questionnaire) {
        this.statisticsService = statisticsService;
        this.key = key;
        this.questionnaire = questionnaire;
    }

    @Override
    public void run() {
        statisticsService.generateStuExcel(key, questionnaire);
    }
}
