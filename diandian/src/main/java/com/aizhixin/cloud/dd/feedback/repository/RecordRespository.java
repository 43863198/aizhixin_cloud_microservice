package com.aizhixin.cloud.dd.feedback.repository;

import com.aizhixin.cloud.dd.feedback.entity.FeedbackRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecordRespository extends JpaRepository<FeedbackRecord, Long> {

    public Page<FeedbackRecord> findByOrgIdAndJobNumAndTypeAndDeleteFlagOrderByCreatedDateDesc(Pageable pageable, Long orgId, String jobNum, Integer type, Integer deleteFlag);

    @Query(value = "select t from #{#entityName} t where t.orgId=:orgId and t.userName like %:userName% and t.teachingClassTeacher like %:teachingClassTeacher% and t.courseName like %:courseName% and t.type=:type and t.deleteFlag=:deleteFlag")
    public Page<FeedbackRecord> findList(Pageable pageable, @Param("orgId") Long orgId, @Param("userName") String userName, @Param("teachingClassTeacher") String teachingClassTeacher, @Param("courseName") String courseName, @Param("type") Integer type, @Param("deleteFlag") Integer deleteFlag);

    @Query(value = "select t from #{#entityName} t where t.orgId=:orgId and t.type=:type and (t.userName LIKE CONCAT('%',:userName,'%') or t.jobNum LIKE CONCAT('%',:userName,'%')) and (t.teachingClassTeacher LIKE CONCAT('%',:teachingClassTeacher,'%') or t.teacherJobNum LIKE CONCAT('%',:teachingClassTeacher,'%')) and t.courseName LIKE CONCAT('%',:courseName,'%') and t.deleteFlag=:deleteFlag order by t.createdDate desc")
    public Page<FeedbackRecord> findList(Pageable pageable, @Param("orgId") Long orgId, @Param("type") Integer type, @Param("userName") String userName, @Param("teachingClassTeacher") String teachingClassTeacher, @Param("courseName") String courseName, @Param("deleteFlag") Integer deleteFlag);

    @Query(value = "select t from #{#entityName} t where t.orgId=:orgId and t.type=:type and t.deleteFlag=:deleteFlag order by t.createdDate desc")
    public Page<FeedbackRecord> findList(Pageable pageable, @Param("orgId") Long orgId, @Param("type") Integer type, @Param("deleteFlag") Integer deleteFlag);


    public FeedbackRecord findById(Long id);
}
