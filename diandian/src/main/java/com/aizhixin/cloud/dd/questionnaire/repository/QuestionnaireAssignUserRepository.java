package com.aizhixin.cloud.dd.questionnaire.repository;

import com.aizhixin.cloud.dd.questionnaire.entity.QuestionnaireAssignUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface QuestionnaireAssignUserRepository extends JpaRepository<QuestionnaireAssignUser, Long> {

    @Query("select q from #{#entityName} q where q.deleteFlag=0 and q.quesId=:quesId and q.teacherType=:teacherType and (q.userName LIKE CONCAT('%',:userName,'%') or q.jobNum LIKE CONCAT('%',:userName,'%'))")
    public Page<QuestionnaireAssignUser> findByQuesIdAndTeacherTypeAndUserName(Pageable pageable, @Param("quesId") Long quesId, @Param("teacherType") Integer teacherType, @Param("userName") String userName);

    @Query("select q from #{#entityName} q where q.deleteFlag=0 and q.quesId=:quesId and (q.userName LIKE CONCAT('%',:userName,'%') or q.jobNum LIKE CONCAT('%',:userName,'%'))")
    public Page<QuestionnaireAssignUser> findByQuesIdAndUserName(Pageable pageable, @Param("quesId") Long quesId, @Param("userName") String userName);

    public QuestionnaireAssignUser findByQuesIdAndUserIdAndDeleteFlag(Long quesId, Long userId, Integer deleteFlag);

    @Modifying
    @Query("update #{#entityName} q set q.deleteFlag=1  where q.quesId = ?1 and q.userId = ?2")
    public int deleteByUserId(Long quesId, Long userId);

    @Modifying
    @Query("update #{#entityName} q set q.deleteFlag=1  where q.quesId = ?1")
    public int deleteByQuesId(Long quesId);

    @Modifying
    @Query("update #{#entityName} q set q.score = ?2 , q.status= ?3 , q.commitDate = ?4 ,q.comment= ?5  where q.id = ?1")
    public int updateScore(Long queAssginStudentId, int totalActualScore, int status, Date commitDate,String comment);

    @Modifying
    @Query("update #{#entityName} q set q.score = ?2 , q.status= ?3 , q.commitDate = ?4   where q.id = ?1")
    public int updateScore(Long queAssginStudentId, int totalActualScore, int status, Date commitDate);
}
