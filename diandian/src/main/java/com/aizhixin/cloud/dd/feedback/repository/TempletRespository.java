package com.aizhixin.cloud.dd.feedback.repository;

import com.aizhixin.cloud.dd.feedback.dto.FeedbackTempletDTO;
import com.aizhixin.cloud.dd.feedback.entity.FeedbackTemplet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TempletRespository extends JpaRepository<FeedbackTemplet, Long> {

    public List<FeedbackTemplet> findByOrgId(Long orgId, Integer deleteFlag);

    public FeedbackTemplet findById(Long id);

    @Query(value = "select new com.aizhixin.cloud.dd.feedback.dto.FeedbackTempletDTO(ft.id, ft.orgId, ft.quesType, ft.totalScore) from com.aizhixin.cloud.dd.feedback.entity.FeedbackTemplet ft where ft.type=:type and ft.orgId=:orgId and ft.deleteFlag=:deleteFlag")
    public List<FeedbackTempletDTO> findByTypeAndOrgId(@Param("type") Integer type, @Param("orgId") Long orgId, @Param("deleteFlag") Integer deleteFlag);

    @Modifying
    @Query("update  #{#entityName} ac set ac.deleteFlag = 1 where ac.orgId=:orgId and ac.type=:type")
    public void deleteByOrgId(@Param("type") Integer type, @Param("orgId") Long orgId);
}
