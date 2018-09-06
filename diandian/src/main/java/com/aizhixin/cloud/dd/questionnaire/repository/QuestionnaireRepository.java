package com.aizhixin.cloud.dd.questionnaire.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aizhixin.cloud.dd.questionnaire.entity.Questionnaire;

public interface QuestionnaireRepository extends JpaRepository<Questionnaire, Long> {


    /***
     * 查看同名的问卷是否存在
     * @return
     */
    @Query(value = "select count(1) from #{#entityName} q where q.name = :name")
    public int findQuestionnaireByName(@Param("name") String name);

    Page<Questionnaire> findByOrganId(Long organId, Pageable pageable);

    Page<Questionnaire> findByOrganIdAndQuesTypeAndDeleteFlag(Pageable pageable, Long organId, Integer type,Integer deleteFlag);
    
    public  List<Questionnaire> findByOrganIdAndDeleteFlag(Long organId,Integer deleteFlag);

    @Modifying
    @Query("update #{#entityName} q set q.allocationNum=(select count(t) from com.aizhixin.cloud.dd.questionnaire.entity.QuestionnaireAssignUser t where t.deleteFlag=0 and t.quesId=q.id)  where q.id = ?1")
    public int updateAssignNum(Long id);

}
