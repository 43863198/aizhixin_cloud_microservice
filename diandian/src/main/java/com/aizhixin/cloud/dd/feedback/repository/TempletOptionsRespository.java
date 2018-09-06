package com.aizhixin.cloud.dd.feedback.repository;

import com.aizhixin.cloud.dd.feedback.dto.FeedbackTempletOptionsDTO;
import com.aizhixin.cloud.dd.feedback.entity.FeedbackTempletOptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TempletOptionsRespository extends JpaRepository<FeedbackTempletOptions, String> {

    @Query(value = "select new com.aizhixin.cloud.dd.feedback.dto.FeedbackTempletOptionsDTO(fto.id, fto.option, fto.content) from com.aizhixin.cloud.dd.feedback.entity.FeedbackTempletOptions fto where fto.templetQues.id=:quesId and fto.deleteFlag=:deleteFlag")
    public List<FeedbackTempletOptionsDTO> findByQuesIdAndDeleteFlag(@Param("quesId") Long quesId, @Param("deleteFlag") Integer deleteFlag);

    @Query(value = "select new com.aizhixin.cloud.dd.feedback.dto.FeedbackTempletOptionsDTO(fto.id, fto.option, fto.content) from com.aizhixin.cloud.dd.feedback.entity.FeedbackTempletOptions fto where fto.templetQues.id=:quesId")
    public List<FeedbackTempletOptionsDTO> findByQuesId(@Param("quesId") Long quesId);
}
