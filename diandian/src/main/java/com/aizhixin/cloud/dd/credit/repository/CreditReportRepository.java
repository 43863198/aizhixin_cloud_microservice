package com.aizhixin.cloud.dd.credit.repository;

import com.aizhixin.cloud.dd.credit.entity.CreditReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CreditReportRepository extends JpaRepository<CreditReport, Long> {

    @Query(value = "select t from #{#entityName} t where t.deleteFlag=0 and t.orgId=:orgId and t.className like %:className% and t.teacherName like %:teacherName%")
    public Page<CreditReport> findByOrgIdAndClassNameAndTeacherName(Pageable pageable, @Param("orgId") Long orgId, @Param("className") String className, @Param("teacherName") String teacherName);

    @Query(value = "select t from #{#entityName} t where t.deleteFlag=0 and t.orgId=:orgId and t.className like %:className% and t.teacherName like %:teacherName% and t.templetId=:templetId")
    public Page<CreditReport> findByOrgIdAndClassNameAndTeacherNameAndTempletId(Pageable pageable, @Param("orgId") Long orgId, @Param("className") String className, @Param("teacherName") String teacherName, @Param("templetId") Long templetId);

    public List<CreditReport> findByCreditIdAndDeleteFlag(Long creditId, Integer deleteFlag);

    public List<CreditReport> findByCreditIdAndClassIdAndDeleteFlag(Long creditId, Long classId, Integer deleteFlag);

    @Query(value = "select t from #{#entityName} t where t.deleteFlag=0 and t.orgId=:orgId and t.className like %:className% and t.teacherName like %:teacherName% and t.templetId=:templetId")
    public List<CreditReport> findByOrgIdAndClassNameAndTeacherNameAndTempletId(@Param("orgId") Long orgId, @Param("className") String className, @Param("teacherName") String teacherName, @Param("templetId") Long templetId);

}
