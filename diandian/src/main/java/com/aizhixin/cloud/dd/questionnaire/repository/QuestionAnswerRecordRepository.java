package com.aizhixin.cloud.dd.questionnaire.repository;

import com.aizhixin.cloud.dd.questionnaire.entity.QuestionAnswerRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionAnswerRecordRepository extends JpaRepository<QuestionAnswerRecord, Long> {
    List<QuestionAnswerRecord> findAllByQuestionnaireAssginStudentsId(Long id);
}
