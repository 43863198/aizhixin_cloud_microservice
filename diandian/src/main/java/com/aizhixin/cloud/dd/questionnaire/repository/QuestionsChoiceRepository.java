package com.aizhixin.cloud.dd.questionnaire.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aizhixin.cloud.dd.questionnaire.dto.QuestionsChoiceDTO;
import com.aizhixin.cloud.dd.questionnaire.entity.QuestionsChoice;

public interface QuestionsChoiceRepository extends JpaRepository<QuestionsChoice, Long>{
	
	@Query("select new com.aizhixin.cloud.dd.questionnaire.dto.QuestionsChoiceDTO(qc.id,qc.choice,qc.content,qc.score,qc.questions.id)  from #{#entityName} qc where qc.questions.id in :questionIds and qc.deleteFlag=:deleteFlag")
	public List<QuestionsChoiceDTO> findByQuestions_IdAndDeleteFlag(@Param("questionIds")List<Long>questionIds,@Param("deleteFlag")Integer deleteFlag);
}
