package com.aizhixin.cloud.dd.questionnaire.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aizhixin.cloud.dd.questionnaire.dto.StandardDTO;
import com.aizhixin.cloud.dd.questionnaire.entity.Standard;

public interface StandardRepository extends JpaRepository<Standard, Long> {

    List<StandardDTO> findAllByQuestionnaireId(Long questionnaireId);

    List<Standard> findByQuestionnaireId(Long questionnaireId);

}
