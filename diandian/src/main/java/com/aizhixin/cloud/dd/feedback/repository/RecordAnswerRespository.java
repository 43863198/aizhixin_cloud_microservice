package com.aizhixin.cloud.dd.feedback.repository;

import com.aizhixin.cloud.dd.feedback.entity.FeedbackRecordAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecordAnswerRespository extends JpaRepository<FeedbackRecordAnswer, String> {

    @Query(value = "select fra from com.aizhixin.cloud.dd.feedback.entity.FeedbackRecordAnswer fra where fra.record.id=:recordId and fra.templetQues.group=:group")
    public List<FeedbackRecordAnswer> findByRecordIdAndGroup(@Param("recordId") Long recordId, @Param("group") Integer group);
}
