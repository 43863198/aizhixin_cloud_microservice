package com.aizhixin.cloud.dd.questionnaire.thread;

import java.util.List;

import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.questionnaire.entity.Questionnaire;
import com.aizhixin.cloud.dd.questionnaire.serviceV2.QuestionnaireServiceV2;

public class QuestionnairStudentCollegeInsertThread extends Thread{
	private List<Long> collegeIds;
	private Questionnaire questionnaire;
	private String accessToken;
	private Long userId;
	private QuestionnaireServiceV2 qs;
	private IdNameDomain semester;
	

	public QuestionnairStudentCollegeInsertThread(List<Long> collegeIds, Questionnaire questionnaire, String accessToken,
			Long userId, QuestionnaireServiceV2 qs, IdNameDomain semester) {
		super();
		this.collegeIds = collegeIds;
		this.questionnaire = questionnaire;
		this.accessToken = accessToken;
		this.userId = userId;
		this.qs = qs;
		this.semester = semester;
	}






	@Override
	public void run() {

		qs.insertCollegeStudent(collegeIds, accessToken, questionnaire, userId, semester);
	}
}
