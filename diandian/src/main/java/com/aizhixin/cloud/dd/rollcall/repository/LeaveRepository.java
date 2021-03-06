package com.aizhixin.cloud.dd.rollcall.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.aizhixin.cloud.dd.rollcall.domain.LeaveDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.aizhixin.cloud.dd.rollcall.entity.Leave;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LeaveRepository extends JpaRepository<Leave, Long>, JpaSpecificationExecutor<Leave> {
    public List<Leave> findAllByIdIn(Set<Long> leaves);

    public List<Leave> findAllByheadTeacherIdAndStatusAndDeleteFlag(Sort sort, Long headTeacherId, String status, Integer deleteFlag);

    public List<Leave> findAllByHeadTeacherIdAndDeleteFlagAndStatusInOrderByLastModifiedDateDesc(Long headTeacherId, Integer deleteFlag, Set<String> statusSet);

    public List<Leave> findAllByStudentIdAndDeleteFlagAndStatusInOrderByLastModifiedDateDesc(Long studentId, Integer deleteFlag, Set<String> statusSet);

    public Leave findFirstByStudentIdAndDeleteFlagAndStatusInOrderByLastModifiedDateDesc(Long studentId, Integer deleteFlag, Set<String> statusSet);

    public List<Leave> findAllByStudentIdAndStatusAndDeleteFlag(Sort sort, Long studentId, String status, Integer deleteFlag);

    public List<Leave> findAllByStudentIdAndStartDateAndEndDateAndStartPeriodIdAndEndPeriodIdAndRequestContentAndRequestType(Long studetnId, Date startDate, Date endDate, Long startPeriodId, Long endPeriiodId, String requestCon, String requestType);

    @Query(value = "SELECT * FROM `dd_leave` WHERE student_id IN (?1) AND start_date =CURRENT_DATE", nativeQuery = true)
    public List<Leave> findAllByStudentIdWithCurrentDate(Set<Long> ids);

    public List<Leave> findByDeleteFlagAndStartTimeIsNullOrEndTimeIsNullOrOrgIdIsNull(Integer deleteFlage);

    @Query(value = "select new com.aizhixin.cloud.dd.rollcall.domain.LeaveDomain(id, studentId, studentName, headTeacherId, teacherName, startTime, endTime, className, requestContent, status, leavePublic, leaveType) FROM #{#entityName} t where t.orgId=:orgId and t.status=:status and t.leavePublic=:leavePublic and t.leaveType=:leaveType and t.deleteFlag=:deleteFlag and (t.studentName LIKE CONCAT('%',:studentName,'%') or t.studentJobNum LIKE CONCAT('%',:studentName,'%')) and (t.teacherName LIKE CONCAT('%',:teacherName,'%') or t.teacherJobNum LIKE CONCAT('%',:teacherName,'%')) and t.className LIKE CONCAT('%',:className,'%')")
    public Page<LeaveDomain> findByStatusAndLeavePublicAndLeaveTypeAndDeleteFlagAndNameLike(Pageable pageable, @Param("orgId") Long orgId, @Param("status") String status, @Param("leavePublic") Integer leavePublic, @Param("leaveType") Integer leaveType, @Param("deleteFlag") Integer deleteFlag, @Param("studentName") String studentName, @Param("teacherName") String teacherName, @Param("className") String className);

    @Query(value = "select new com.aizhixin.cloud.dd.rollcall.domain.LeaveDomain(id, studentId, studentName, headTeacherId, teacherName, startTime, endTime, className, requestContent, status, leavePublic, leaveType) FROM #{#entityName} t where t.orgId=:orgId and t.status=:status and t.leavePublic=:leavePublic and t.deleteFlag=:deleteFlag and (t.studentName LIKE CONCAT('%',:studentName,'%') or t.studentJobNum LIKE CONCAT('%',:studentName,'%')) and (t.teacherName LIKE CONCAT('%',:teacherName,'%') or t.teacherJobNum LIKE CONCAT('%',:teacherName,'%')) and t.className LIKE CONCAT('%',:className,'%')")
    public Page<LeaveDomain> findByStatusAndLeavePublicAndDeleteFlagAndNameLike(Pageable pageable, @Param("orgId") Long orgId, @Param("status") String status, @Param("leavePublic") Integer leavePublic, @Param("deleteFlag") Integer deleteFlag, @Param("studentName") String studentName, @Param("teacherName") String teacherName, @Param("className") String className);

    @Query(value = "select new com.aizhixin.cloud.dd.rollcall.domain.LeaveDomain(id, studentId, studentName, headTeacherId, teacherName, startTime, endTime, className, requestContent, status, leavePublic, leaveType) FROM #{#entityName} t where t.orgId=:orgId and t.status=:status and t.deleteFlag=:deleteFlag and (t.studentName LIKE CONCAT('%',:studentName,'%') or t.studentJobNum LIKE CONCAT('%',:studentName,'%')) and (t.teacherName LIKE CONCAT('%',:teacherName,'%') or t.teacherJobNum LIKE CONCAT('%',:teacherName,'%')) and t.className LIKE CONCAT('%',:className,'%')")
    public Page<LeaveDomain> findByStatusAndDeleteFlagAndNameLike(Pageable pageable, @Param("orgId") Long orgId, @Param("status") String status, @Param("deleteFlag") Integer deleteFlag, @Param("studentName") String studentName, @Param("teacherName") String teacherName, @Param("className") String className);

    @Query(value = "select t from #{#entityName} t where t.status=:status and t.deleteFlag=:deleteFlag and t.startTime<=:date and t.endTime>=:date and studentId in (:ids)")
    public List<Leave> findByStatusAndDeleteFlagAndStudentIdIn(@Param("status") String status, @Param("deleteFlag") Integer deleteFlag, @Param("date") Date date, @Param("ids") Set<Long> ids);

    @Query(value = "select t from #{#entityName} t where t.status=:status and t.deleteFlag=:deleteFlag and ((t.startTime<=:startDate and t.endTime>=:startDate) or (t.startTime<=:endDate and t.endTime>=:endDate)) and studentId in (:ids)")
    public List<Leave> findByStatusAndDeleteFlagAndStudentIdIn2(@Param("status") String status, @Param("deleteFlag") Integer deleteFlag, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("ids") Set<Long> ids);

    @Query(value = "select t from #{#entityName} t where t.status=:status and ((t.startTime<=:startDate and t.endTime>=:startDate) or (t.startTime<=:endDate and t.endTime>=:endDate)) and studentId in (:ids)")
    public List<Leave> findByStatusAndStudentIdIn2(@Param("status") String status, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("ids") Set<Long> ids);

    @Query(value = "select new com.aizhixin.cloud.dd.rollcall.domain.LeaveDomain(id, studentId, studentName, headTeacherId, teacherName, startTime, endTime, className, requestContent, status, leavePublic, leaveType) FROM #{#entityName} t where t.orgId=:orgId and t.status=:status and t.leavePublic=:leavePublic and t.leaveType=:leaveType and t.deleteFlag=:deleteFlag and (t.studentName LIKE CONCAT('%',:studentName,'%') or t.studentJobNum LIKE CONCAT('%',:studentName,'%')) and (t.teacherName LIKE CONCAT('%',:teacherName,'%') or t.teacherJobNum LIKE CONCAT('%',:teacherName,'%')) and t.className LIKE CONCAT('%',:className,'%') and (t.startTime >= :startDate and t.endTime <= :endDate) ")
    public Page<LeaveDomain> findByStatusAndLeavePublicAndLeaveTypeAndDeleteFlagAndStartDateAndEndDateAndNameLike(Pageable pageable, @Param("orgId") Long orgId, @Param("status") String status, @Param("leavePublic") Integer leavePublic, @Param("leaveType") Integer leaveType, @Param("deleteFlag") Integer deleteFlag, @Param("studentName") String studentName, @Param("teacherName") String teacherName, @Param("className") String className, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "select new com.aizhixin.cloud.dd.rollcall.domain.LeaveDomain(id, studentId, studentName, headTeacherId, teacherName, startTime, endTime, className, requestContent, status, leavePublic, leaveType) FROM #{#entityName} t where t.orgId=:orgId and t.status=:status and t.leavePublic=:leavePublic and t.leaveType=:leaveType and t.deleteFlag=:deleteFlag and (t.studentName LIKE CONCAT('%',:studentName,'%') or t.studentJobNum LIKE CONCAT('%',:studentName,'%')) and (t.teacherName LIKE CONCAT('%',:teacherName,'%') or t.teacherJobNum LIKE CONCAT('%',:teacherName,'%')) and t.className LIKE CONCAT('%',:className,'%') and t.startTime >= :startDate")
    public Page<LeaveDomain> findByStatusAndLeavePublicAndLeaveTypeAndDeleteFlagAndStartDateAndNameLike(Pageable pageable, @Param("orgId") Long orgId, @Param("status") String status, @Param("leavePublic") Integer leavePublic, @Param("leaveType") Integer leaveType, @Param("deleteFlag") Integer deleteFlag, @Param("studentName") String studentName, @Param("teacherName") String teacherName, @Param("className") String className, @Param("startDate") Date startDate);

    @Query(value = "select new com.aizhixin.cloud.dd.rollcall.domain.LeaveDomain(id, studentId, studentName, headTeacherId, teacherName, startTime, endTime, className, requestContent, status, leavePublic, leaveType) FROM #{#entityName} t where t.orgId=:orgId and t.status=:status and t.leavePublic=:leavePublic and t.leaveType=:leaveType and t.deleteFlag=:deleteFlag and (t.studentName LIKE CONCAT('%',:studentName,'%') or t.studentJobNum LIKE CONCAT('%',:studentName,'%')) and (t.teacherName LIKE CONCAT('%',:teacherName,'%') or t.teacherJobNum LIKE CONCAT('%',:teacherName,'%')) and t.className LIKE CONCAT('%',:className,'%') and t.endTime <= :endDate")
    public Page<LeaveDomain> findByStatusAndLeavePublicAndLeaveTypeAndDeleteFlagAndEndDateAndNameLike(Pageable pageable, @Param("orgId") Long orgId, @Param("status") String status, @Param("leavePublic") Integer leavePublic, @Param("leaveType") Integer leaveType, @Param("deleteFlag") Integer deleteFlag, @Param("studentName") String studentName, @Param("teacherName") String teacherName, @Param("className") String className, @Param("endDate") Date endDate);

    @Query(value = "select new com.aizhixin.cloud.dd.rollcall.domain.LeaveDomain(id, studentId, studentName, headTeacherId, teacherName, startTime, endTime, className, requestContent, status, leavePublic, leaveType) FROM #{#entityName} t where t.orgId=:orgId and t.status=:status and t.leavePublic=:leavePublic and t.deleteFlag=:deleteFlag and (t.studentName LIKE CONCAT('%',:studentName,'%') or t.studentJobNum LIKE CONCAT('%',:studentName,'%')) and (t.teacherName LIKE CONCAT('%',:teacherName,'%') or t.teacherJobNum LIKE CONCAT('%',:teacherName,'%')) and t.className LIKE CONCAT('%',:className,'%') and (t.startTime >= :startDate and t.endTime <= :endDate) ")
    public Page<LeaveDomain> findByStatusAndLeavePublicAndDeleteFlagAndStartDateAndEndDateAndNameLike(Pageable pageable, @Param("orgId") Long orgId, @Param("status") String status, @Param("leavePublic") Integer leavePublic, @Param("deleteFlag") Integer deleteFlag, @Param("studentName") String studentName, @Param("teacherName") String teacherName, @Param("className") String className, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "select new com.aizhixin.cloud.dd.rollcall.domain.LeaveDomain(id, studentId, studentName, headTeacherId, teacherName, startTime, endTime, className, requestContent, status, leavePublic, leaveType) FROM #{#entityName} t where t.orgId=:orgId and t.status=:status and t.leavePublic=:leavePublic and t.deleteFlag=:deleteFlag and (t.studentName LIKE CONCAT('%',:studentName,'%') or t.studentJobNum LIKE CONCAT('%',:studentName,'%')) and (t.teacherName LIKE CONCAT('%',:teacherName,'%') or t.teacherJobNum LIKE CONCAT('%',:teacherName,'%')) and t.className LIKE CONCAT('%',:className,'%') and t.startTime >= :startDate")
    public Page<LeaveDomain> findByStatusAndLeavePublicAndDeleteFlagAndStartDateAndNameLike(Pageable pageable, @Param("orgId") Long orgId, @Param("status") String status, @Param("leavePublic") Integer leavePublic, @Param("deleteFlag") Integer deleteFlag, @Param("studentName") String studentName, @Param("teacherName") String teacherName, @Param("className") String className, @Param("startDate") Date startDate);

    @Query(value = "select new com.aizhixin.cloud.dd.rollcall.domain.LeaveDomain(id, studentId, studentName, headTeacherId, teacherName, startTime, endTime, className, requestContent, status, leavePublic, leaveType) FROM #{#entityName} t where t.orgId=:orgId and t.status=:status and t.leavePublic=:leavePublic and t.deleteFlag=:deleteFlag and (t.studentName LIKE CONCAT('%',:studentName,'%') or t.studentJobNum LIKE CONCAT('%',:studentName,'%')) and (t.teacherName LIKE CONCAT('%',:teacherName,'%') or t.teacherJobNum LIKE CONCAT('%',:teacherName,'%')) and t.className LIKE CONCAT('%',:className,'%') and t.endTime <= :endDate")
    public Page<LeaveDomain> findByStatusAndLeavePublicAndDeleteFlagAndEndDateAndNameLike(Pageable pageable, @Param("orgId") Long orgId, @Param("status") String status, @Param("leavePublic") Integer leavePublic, @Param("deleteFlag") Integer deleteFlag, @Param("studentName") String studentName, @Param("teacherName") String teacherName, @Param("className") String className, @Param("endDate") Date endDate);

    @Query(value = "select new com.aizhixin.cloud.dd.rollcall.domain.LeaveDomain(id, studentId, studentName, headTeacherId, teacherName, startTime, endTime, className, requestContent, status, leavePublic, leaveType) FROM #{#entityName} t where t.orgId=:orgId and t.status=:status and t.deleteFlag=:deleteFlag and (t.studentName LIKE CONCAT('%',:studentName,'%') or t.studentJobNum LIKE CONCAT('%',:studentName,'%')) and (t.teacherName LIKE CONCAT('%',:teacherName,'%') or t.teacherJobNum LIKE CONCAT('%',:teacherName,'%')) and t.className LIKE CONCAT('%',:className,'%') and (t.startTime >= :startDate and t.endTime <= :endDate) ")
    public Page<LeaveDomain> findByStatusAndDeleteFlagAndStartDateAndEndDateAndNameLike(Pageable pageable, @Param("orgId") Long orgId, @Param("status") String status, @Param("deleteFlag") Integer deleteFlag, @Param("studentName") String studentName, @Param("teacherName") String teacherName, @Param("className") String className,@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "select new com.aizhixin.cloud.dd.rollcall.domain.LeaveDomain(id, studentId, studentName, headTeacherId, teacherName, startTime, endTime, className, requestContent, status, leavePublic, leaveType) FROM #{#entityName} t where t.orgId=:orgId and t.status=:status and t.deleteFlag=:deleteFlag and (t.studentName LIKE CONCAT('%',:studentName,'%') or t.studentJobNum LIKE CONCAT('%',:studentName,'%')) and (t.teacherName LIKE CONCAT('%',:teacherName,'%') or t.teacherJobNum LIKE CONCAT('%',:teacherName,'%')) and t.className LIKE CONCAT('%',:className,'%') and t.startTime >= :startDate")
    public Page<LeaveDomain> findByStatusAndDeleteFlagAndStartDateAndNameLike(Pageable pageable, @Param("orgId") Long orgId, @Param("status") String status, @Param("deleteFlag") Integer deleteFlag, @Param("studentName") String studentName, @Param("teacherName") String teacherName, @Param("className") String className,@Param("startDate") Date startDate);

    @Query(value = "select new com.aizhixin.cloud.dd.rollcall.domain.LeaveDomain(id, studentId, studentName, headTeacherId, teacherName, startTime, endTime, className, requestContent, status, leavePublic, leaveType) FROM #{#entityName} t where t.orgId=:orgId and t.status=:status and t.deleteFlag=:deleteFlag and (t.studentName LIKE CONCAT('%',:studentName,'%') or t.studentJobNum LIKE CONCAT('%',:studentName,'%')) and (t.teacherName LIKE CONCAT('%',:teacherName,'%') or t.teacherJobNum LIKE CONCAT('%',:teacherName,'%')) and t.className LIKE CONCAT('%',:className,'%') and t.endTime <= :endDate")
    public Page<LeaveDomain> findByStatusAndDeleteFlagAndEndDateAndNameLike(Pageable pageable, @Param("orgId") Long orgId, @Param("status") String status, @Param("deleteFlag") Integer deleteFlag, @Param("studentName") String studentName, @Param("teacherName") String teacherName, @Param("className") String className, @Param("endDate") Date endDate);
}
