package com.aizhixin.cloud.dd.questionnaire.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.aizhixin.cloud.dd.questionnaire.entity.QuestionnaireAssgin;
import com.aizhixin.cloud.dd.questionnaire.entity.QuestionnaireAssginStudents;

public interface QuestionnaireAssginStudentsRepository extends
        JpaRepository<QuestionnaireAssginStudents, Long> {

    List<QuestionnaireAssginStudents> findAllByQuestionnaireAssginId(Long questionnaireAssginId);

    Page<QuestionnaireAssginStudents> findByQuestionnaireAssgin_Id(Long questionnaireAssginId, Pageable pageable);

    Page<QuestionnaireAssginStudents> findByClassesIdAndQuestionnaireAssgin_Id(Long classesId,
                                                                               Long questionnaireAssginId, Pageable pageable);

    QuestionnaireAssginStudents findAllBystudentIdAndQuestionnaireAssgin(Long studentId, QuestionnaireAssgin questionnaireAssgin);

    QuestionnaireAssginStudents findFirstByStudentIdAndQuestionnaireAssgin_statusOrderByCreatedDateDesc(Long stuId,String status);
    @Modifying
    @Query("update #{#entityName} q set q.score = ?2 , q.status= ?3 , q.commitDate = ?4 ,q.comment= ?5  where q.id = ?1")
    public int updateScore(Long queAssginStudentId, float totalActualScore, int status, Date commitDate,String comment);
    
    @Modifying
    @Query("update #{#entityName} q set q.score = ?2 , q.status= ?3 , q.commitDate = ?4   where q.id = ?1")
    public int updateScore(Long queAssginStudentId, float totalActualScore, int status, Date commitDate);
    
    public Long countByStudentIdAndStatus(Long studentId,Integer status);
}
