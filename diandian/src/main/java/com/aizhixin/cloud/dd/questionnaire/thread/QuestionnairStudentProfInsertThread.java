package com.aizhixin.cloud.dd.questionnaire.thread;

import java.util.List;

import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.questionnaire.entity.Questionnaire;
import com.aizhixin.cloud.dd.questionnaire.serviceV2.QuestionnaireServiceV2;

public class QuestionnairStudentProfInsertThread extends Thread{
	private List<Long> profIds;
	private Questionnaire questionnaire;
	private String accessToken;
	private Long userId;
	private QuestionnaireServiceV2 qs;
	private IdNameDomain semester;
	

	public QuestionnairStudentProfInsertThread(List<Long> profIds, Questionnaire questionnaire, String accessToken,
			Long userId, QuestionnaireServiceV2 qs, IdNameDomain semester) {
		super();
		this.profIds = profIds;
		this.questionnaire = questionnaire;
		this.accessToken = accessToken;
		this.userId = userId;
		this.qs = qs;
		this.semester = semester;
	}






	@Override
	public void run() {

		qs.insertProfStudent(profIds, accessToken, questionnaire, userId, semester);
	}
}
