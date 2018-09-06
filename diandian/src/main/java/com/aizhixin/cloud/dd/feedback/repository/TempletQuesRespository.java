package com.aizhixin.cloud.dd.feedback.repository;

import com.aizhixin.cloud.dd.feedback.dto.FeedbackTempletQuesDTO;
import com.aizhixin.cloud.dd.feedback.entity.FeedbackTempletQues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TempletQuesRespository extends JpaRepository<FeedbackTempletQues, Long> {

    @Query(value = "select new com.aizhixin.cloud.dd.feedback.dto.FeedbackTempletQuesDTO(ftq.id, ftq.subject, ftq.content, ftq.score) from com.aizhixin.cloud.dd.feedback.entity.FeedbackTempletQues ftq where ftq.templet.id=:templetId and ftq.group=:group and ftq.deleteFlag=:deleteFlag")
    public List<FeedbackTempletQuesDTO> findByTempletId(@Param("templetId") Long templetId, @Param("group") Integer group, @Param("deleteFlag") Integer deleteFlag);

    @Modifying
    @Query("update  #{#entityName} ac set ac.deleteFlag = 1 where ac.templet.id=:templetId")
    public void deleteByTempletId(@Param("templetId") Long templetId);
}
