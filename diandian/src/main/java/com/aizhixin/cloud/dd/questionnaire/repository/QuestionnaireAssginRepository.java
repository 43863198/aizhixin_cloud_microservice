package com.aizhixin.cloud.dd.questionnaire.repository;

import com.aizhixin.cloud.dd.rollcall.dto.IdCountDTO;
import com.aizhixin.cloud.dd.questionnaire.entity.Questionnaire;
import com.aizhixin.cloud.dd.questionnaire.entity.QuestionnaireAssgin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface QuestionnaireAssginRepository extends JpaRepository<QuestionnaireAssgin, Long> {

    @Query(value = "SELECT count(1) from #{#entityName} qu where qu.questionnaire = :questionnaire and qu.deleteFlag = '0' and qu.status=:status")
    public int queryCountByQuqestionIdAndstatus(@Param("questionnaire") Questionnaire questionnaire, @Param("status") String status);

    Page<QuestionnaireAssgin> findAllByDeleteFlagAndQuestionnaire(Pageable pageable, Questionnaire questionnaire);

    @Query(value = "select new com.aizhixin.cloud.dd.rollcall.dto.IdCountDTO(qu.questionnaire.id,count(qu.questionnaire.id)) from #{#entityName} qu where qu.questionnaire.id in :questionnaireIds and qu.status='10' group by qu.questionnaire.id ")
    List<IdCountDTO> findAllByQuestionnaires(@Param("questionnaireIds") Set<Long> questionnaireIds);

    @Query(value = "select teachingClassId from #{#entityName} qu where qu.questionnaire.id = :questionnaireId and qu.status=10 and qu.classType=10")
    Set<Long> findTeachingClassIdsByQuestionnaireId(@Param("questionnaireId") Long questionnaireId);


    List<QuestionnaireAssgin> findByQuestionnaire_IdInAndDeleteFlag(Long questionnaireId, Integer deleteFlag);

    List<QuestionnaireAssgin> findByIdInAndDeleteFlag(List<Long> ids, Integer deleteFlag);

    QuestionnaireAssgin findByTeachingClassIdAndClassTypeAndStatusAndQuestionnaire_Id(Long teachingClassId, Integer classType, String status, Long questionnaireId);

    QuestionnaireAssgin findByClassesIdAndClassTypeAndStatusAndQuestionnaire_Id(Long classesId, Integer classType, String status, Long questionnaireId);

    @Modifying
    @Query("update #{#entityName} q set q.score = ?2 , q.weightScore = ?6, q.commitStatus= ?3 , q.commitDate = ?4 ,q.comment= ?5 where q.id = ?1")
    public int updateScore(Long id, int totalActualScore, int status, Date commitDate, String comment, float totalWeight);

    @Modifying
    @Query("update #{#entityName} q set q.score = ?2 , q.weightScore = ?5, q.commitStatus= ?3 , q.commitDate = ?4 where q.id = ?1")
    public int updateScore(Long id, int totalActualScore, int status, Date commitDate, float totalWeight);

    @Modifying
    @Query("update #{#entityName} q set q.deleteFlag=1  where q.questionnaire.id = ?1 and q.createdBy = ?2")
    public int deleteByCreatedBy(Long quesId, Long userId);

    @Query(value = "SELECT t from #{#entityName} t where (t.collegeName is null or t.collegeName = 'null') and t.deleteFlag=0 and t.questionnaire.id in (select q.id from com.aizhixin.cloud.dd.questionnaire.entity.Questionnaire q)")
    public List<QuestionnaireAssgin> findByCollegeNull();
}