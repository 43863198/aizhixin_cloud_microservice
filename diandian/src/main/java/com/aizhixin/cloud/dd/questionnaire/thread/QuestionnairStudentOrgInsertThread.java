package com.aizhixin.cloud.dd.questionnaire.thread;

import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.questionnaire.entity.Questionnaire;
import com.aizhixin.cloud.dd.questionnaire.serviceV2.QuestionnaireServiceV2;

public class QuestionnairStudentOrgInsertThread extends Thread{
	private Long orgId;
	private Questionnaire questionnaire;
	private String accessToken;
	private Long userId;
	private QuestionnaireServiceV2 qs;
	private IdNameDomain semester;
	

	public QuestionnairStudentOrgInsertThread(Long orgId, Questionnaire questionnaire, String accessToken,
			Long userId, QuestionnaireServiceV2 qs, IdNameDomain semester) {
		super();
		this.orgId = orgId;
		this.questionnaire = questionnaire;
		this.accessToken = accessToken;
		this.userId = userId;
		this.qs = qs;
		this.semester = semester;
	}




	@Override
	public void run() {

		qs.insertOrgStudent(orgId, accessToken, questionnaire, userId, semester);
	}
}
