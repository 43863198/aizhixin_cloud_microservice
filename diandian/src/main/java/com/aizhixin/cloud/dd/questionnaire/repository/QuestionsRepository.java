package com.aizhixin.cloud.dd.questionnaire.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aizhixin.cloud.dd.questionnaire.entity.Questions;

public interface QuestionsRepository extends JpaRepository<Questions, Long> {
    
    List<Questions> findAllByQuestionnaireId(Long questionnaireId);
    
    List<Questions> findByQuestionnaireIdOrderByNoAsc(Long questionnaireId);
}
