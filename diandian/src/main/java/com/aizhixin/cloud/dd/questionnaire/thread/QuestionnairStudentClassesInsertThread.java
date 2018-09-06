package com.aizhixin.cloud.dd.questionnaire.thread;

import java.util.List;

import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.questionnaire.entity.Questionnaire;
import com.aizhixin.cloud.dd.questionnaire.serviceV2.QuestionnaireServiceV2;

public class QuestionnairStudentClassesInsertThread extends Thread {
	private List<Long> classesIds;
	private Questionnaire questionnaire;
	private String accessToken;
	private Long userId;
	private QuestionnaireServiceV2 qs;
	private IdNameDomain semester;

	
	public QuestionnairStudentClassesInsertThread(List<Long> classesIds, Questionnaire questionnaire,
			String accessToken, Long userId, QuestionnaireServiceV2 qs, IdNameDomain semester) {
		super();
		this.classesIds = classesIds;
		this.questionnaire = questionnaire;
		this.accessToken = accessToken;
		this.userId = userId;
		this.qs = qs;
		this.semester = semester;
	}

	@Override
	public void run() {
	     qs.insertClassesStudent(classesIds, accessToken, questionnaire, userId, semester);
	}
}
