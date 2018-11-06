package com.aizhixin.cloud.dd.rollcall.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aizhixin.cloud.dd.rollcall.entity.RollCall;

public interface RollCallRepository extends JpaRepository<RollCall, Long> {

    List<RollCall> findByScheduleRollcallIdInAndStudentId(@Param("ids") Set<Long> ids, @Param("studentId") Long studentId);

    List<RollCall> findByScheduleRollcallIdInAndStudentId(Long scheduleRollcallId, Long studentId);
    //
    List<RollCall> findByStudentIdAndScheduleRollcallIdIn(Long studentId, Set<Long> ids);

    List<RollCall> findAllByScheduleRollcallIdAndStudentIdIn(Long scheduleRollcallId, Set<Long> studentIds);

    List<RollCall> findAllByScheduleRollcallIdAndDeleteFlag(Long scheduleRollCallId, Integer deleteFlag);

    List<RollCall> findByScheduleRollcallId(Long scheduleRollCallId);

    @Modifying
    @Query("update com.aizhixin.cloud.dd.rollcall.entity.RollCall rc set rc.type = :type,rc.signTime = :signTime ,rc.distance = '' where rc.id in (:rollCallIds)")
    void cancleRollCall(@Param("rollCallIds") Set<Long> rollCallIds, @Param("type") String rollCallType, @Param("signTime") Date signTime);

    @Modifying
    @Query("update com.aizhixin.cloud.dd.rollcall.entity.RollCall rc set rc.type = :type  where rc.studentId = :studentId and rc.scheduleRollcallId in (:scheduleRollCallIds)")
    void updateRollCallByStudentIdAndScheduleRollCall(@Param("type") String type, @Param("studentId") Long studentId, @Param("scheduleRollCallIds") Set<Long> scheduleRollCallIds);

    @Modifying
    @Query("update com.aizhixin.cloud.dd.rollcall.entity.RollCall rc set rc.type = :type, rc.isPublicLeave=:isPublicLeave  where rc.studentId = :studentId and rc.scheduleRollcallId in (:scheduleRollCallIds)")
    void updateRollCallByStudentIdAndScheduleRollCall(@Param("type") String type, @Param("isPublicLeave") Boolean isPublicLeave, @Param("studentId") Long studentId, @Param("scheduleRollCallIds") Set<Long> scheduleRollCallIds);

    @Modifying
    @Query("update com.aizhixin.cloud.dd.rollcall.entity.RollCall rc set rc.deleteFlag = 1  where rc.studentId = :studentId and rc.scheduleRollcallId in (:scheduleRollCallIds)")
    void deleteRollCallByStudentIdAndScheduleRollCall( @Param("studentId") Long studentId, @Param("scheduleRollCallIds") Set<Long> scheduleRollCallIds);


    public void deleteByStudentIdAndScheduleRollcallIdIn(Long studentId, Set<Long> scheduleRollCallIds);

    List<RollCall> findByIdIn(Set<Long> ids);

    // List<RollCall> findByScheduleIdAndStudentId(Long scheduleId, Long
    // studentId);

    // @Query(value =
    // "SELECT ss.id from com.dinglicom.dledu.domain.RollCall ss where ss.semesterId = :semesterId and ss.courseId = :courseId ")
    // List<Long> findAllIdsBySemesterIdAndCourseId(
    // @Param("semesterId") Long semesterId,
    // @Param("courseId") Long courseId);
    //
    // @Query(value =
    // "SELECT ss.id from com.dinglicom.dledu.domain.RollCall ss where ss.semesterId = :semesterId and ss.courseId = :courseId and ss.studentScheduleId in (:studentScheduleIds)")
    // List<Long> findAllIdsBySemesterIdAndCourseIdAndStudentScheduleId(
    // @Param("semesterId") Long semesterId,
    // @Param("courseId") Long courseId,
    // @Param("studentScheduleIds") Set<Long> studentScheduleIds);
    //
    // @Query(value =
    // "SELECT count(1) from com.dinglicom.dledu.domain.RollCall where studentId = :id and type = 1")
    // Long getNormerCount(@Param("id") Long id);
    //
    // @Query(value =
    // "SELECT count(1) from com.dinglicom.dledu.domain.RollCall where studentId = :id ")
    // Long getAllCount(@Param("id") Long id);
    //
    // @Query(value =
    // "SELECT ss.id from com.dinglicom.dledu.domain.RollCall ss where ss.scheduleId = :scheduleId")
    // List<Long> findRollCallIdsByScheduleId(@Param("scheduleId") Long
    // scheduleId);
    //
    // @Query(value =
    // "SELECT ss.id from com.dinglicom.dledu.domain.RollCall ss where ss.scheduleId = :scheduleId and ss.type =:type")
    // List<Long> findRollCallIdsByScheduleIdAndType(
    // @Param("scheduleId") Long scheduleId, @Param("type") String type);
    //
    // @Query(value =
    // "SELECT ss.id from com.dinglicom.dledu.domain.RollCall ss where ss.scheduleId = :scheduleId and ss.type !=:type")
    // List<Long> findRollCallIdsByScheduleIdAndNotType(
    // @Param("scheduleId") Long scheduleId, @Param("type") String type);
    //
    // @Query(value =
    // "SELECT ss.studentId from com.dinglicom.dledu.domain.RollCall ss where ss.scheduleId = :scheduleId and ss.type !=:type")
    // List<Long> findRollCallStudentIdsByScheduleIdAndNotType(
    // @Param("scheduleId") Long scheduleId, @Param("type") String type);
    //
    // List<RollCall> findAllByScheduleId(Long schedule_id);
    //
    // @Query(value =
    // "SELECT ss.id from com.dinglicom.dledu.domain.RollCall ss where ss.scheduleId = :scheduleId and ss.type != 2 and ss.type !=4")
    // List<Long> findAllByScheduleIdAndType(@Param("scheduleId") Long
    // scheduleId);
    //
    // RollCall findOneByScheduleIdAndStudentId(Long id, Long student_id);
    //

    //
    // List<RollCall> findAllByScheduleIdAndStudentId(Long scheduleId,
    // Long studentId);
    //
    // @Modifying
    // @Query("update RollCall rc set rc.type = :type,rc.signTime = :signTime ,rc.distance = '' where rc.id in :rollCallIds")
    // void cancleRollCall(@Param("rollCallIds") Set<Long> ids,
    // @Param("type") String rollCallType, @Param("signTime") Date signTime);
    //
    // @Modifying
    // @Transactional
    // @Query("delete from RollCall rc where rc.scheduleId = :scheduleId")
    // void deleteByScheduleId(@Param("scheduleId") long scheduleId);
    //
    @Modifying
    @Query("update com.aizhixin.cloud.dd.rollcall.entity.RollCall rc set rc.type = :type,rc.canRollCall =:canRollCall where rc.id in :rollCallIds")
    void updateRollCall(@Param("rollCallIds") Set<Long> rollCallIds, @Param("type") String type, @Param("canRollCall") boolean isCanRollCall);

    //
    // @Modifying
    // @Query("update RollCall rc set rc.canRollCall =:isCanRollCall where rc.scheduleId =:scheduleId")
    // void updateRollCallAndCan(@Param("scheduleId") Long scheduleId,
    // @Param("isCanRollCall") boolean isCanRollCall);
    //
    // @Modifying
    // @Query("update RollCall rc set rc.type = :type,rc.canRollCall =:isCanRollCall,rc.signTime = null,rc.distance = null where rc.id in :rollCallIds")
    // void updateRollCallAndResetSigintime(
    // @Param("rollCallIds") Set<Long> rollCallIds,
    // @Param("type") String type,
    // @Param("isCanRollCall") boolean isCanRollCall);
    // 签到总次数
    @Query("select count(1) from com.aizhixin.cloud.dd.rollcall.entity.RollCall rc  where rc.signTime between :startTime and :endTime and rc.deleteFlag =:deleteFlag and rc.scheduleRollcallId in (:scheduleRollcallIds)")
    Long findCountByClassIds(@Param("scheduleRollcallIds") Set<Long> scheduleRollcallIds, @Param("startTime") Date startTime, @Param("endTime") Date endTime,
        @Param("deleteFlag") Integer deleteFlag);

    // 旷课总次数
    @Query("select count(1) from com.aizhixin.cloud.dd.rollcall.entity.RollCall rc  where rc.signTime between :startTime and :endTime and rc.type = '4' and rc.deleteFlag =:deleteFlag and rc.scheduleRollcallId in (:scheduleRollcallIds)")
    Long findAbsenteeismCount(@Param("scheduleRollcallIds") Set<Long> scheduleRollcallIds, @Param("startTime") Date startTime, @Param("endTime") Date endTime,
        @Param("deleteFlag") Integer deleteFlag);

    // 统计代课老师的应签到学生数
    Long countByteacherIdAndDeleteFlagAndSignTimeAfter(@Param("teacherId") Long teacherId, @Param("deleteFlag") Integer deleteFlag, @Param("signTime") Date signTime);

    // 统计代课老师的签到数
    Long countByteacherIdAndDeleteFlagAndGpsLocationNotNullAndSignTimeAfter(@Param("teacherId") Long teacherId, @Param("deleteFlag") Integer deleteFlag,
        @Param("signTime") Date signTime);

    Page<RollCall> findAllByDeleteFlagAndOrgIdIsNotNull(Integer deleteFlag, Pageable pageable);

    Page<RollCall> findAllByDeleteFlagAndOrgId(Integer deleteFlag, Long orgId, Pageable pageable);
}
