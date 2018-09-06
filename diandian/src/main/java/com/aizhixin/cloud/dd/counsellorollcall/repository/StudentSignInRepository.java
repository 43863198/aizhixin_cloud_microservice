package com.aizhixin.cloud.dd.counsellorollcall.repository;

import com.aizhixin.cloud.dd.counsellorollcall.domain.CouRollCallDomain;
import com.aizhixin.cloud.dd.counsellorollcall.domain.RollcallReportDomain;
import com.aizhixin.cloud.dd.counsellorollcall.domain.StuRollcallReportDomainV2;
import com.aizhixin.cloud.dd.counsellorollcall.domain.StudentSignInDomain;
import com.aizhixin.cloud.dd.counsellorollcall.entity.CounsellorRollcall;
import com.aizhixin.cloud.dd.counsellorollcall.entity.StudentSignIn;
import com.aizhixin.cloud.dd.counsellorollcall.entity.TempGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by LIMH on 2017/11/30.
 */
public interface StudentSignInRepository extends JpaRepository<StudentSignIn, Long> {

    @Query("select new com.aizhixin.cloud.dd.counsellorollcall.domain.CouRollCallDomain(n.id,n.openTime,sum(c.haveReport),count(1),n.status) from #{#entityName} AS c left join c.counsellorRollcall AS n where n.tempGroup=?1 and c.semesterId=?2 and c.counsellorRollcall.deleteFlag=?3 group by n.id order by n.openTime desc")
    List<CouRollCallDomain> findConRollCallList(TempGroup tempGroup, Long semesterId, Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.dd.counsellorollcall.domain.CouRollCallDomain(n.id,n.openTime,sum(c.haveReport),count(1),n.status) from #{#entityName} AS c left join c.counsellorRollcall AS n where n.tempGroup=?1  and c.counsellorRollcall.deleteFlag=?2 group by n.id order by n.openTime desc")
    List<CouRollCallDomain> findConRollCallListNoSemesterId(TempGroup tempGroup, Integer deleteFlag);

    // @Query(value = "select new com.aizhixin.cloud.dd.counsellorollcall.domain.CouRollCallDomain(n.id,n.openTime,sum(c.haveReport),count(1)) from #{#entityName} AS c left join
    // c.counsellorRollcall AS n where n.tempGroup=:tempGroup group by n.id order by n.openTime desc \n-- #pageable\n")
    // Page <CouRollCallDomain> findConRollCallList(@Param(value = "tempGroup") TempGroup tempGroup, Pageable pageable);

    // @Query(value = "select new com.aizhixin.cloud.dd.counsellorollcall.domain.CouRollCallDomain(n.id,n.openTime,sum(c.haveReport),count(1)) from #{#entityName} AS c left join
    // c.counsellorRollcall AS n where n.tempGroup=:tempGroup group by n.id order by n.openTime desc")
    // Page <CouRollCallDomain> findConRollCallList(@Param(value = "tempGroup") TempGroup tempGroup, Pageable pageable);

    @Query("select new com.aizhixin.cloud.dd.counsellorollcall.domain.CouRollCallDomain(n.id,n.openTime,sum(c.haveReport),count(1),n.status) from #{#entityName} c left join c.counsellorRollcall n where c.counsellorRollcall=:counsellorRollcall and c.deleteFlag = :deleteFlag")
    CouRollCallDomain findConRollCall(@Param(value = "counsellorRollcall") CounsellorRollcall counsellorRollcall, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.dd.counsellorollcall.domain.StudentSignInDomain(c.id,c.counsellorRollcall.id,c.studentId,c.studentName,c.classId,c.className,c.haveRead,c.gpsLocation,c.gpsDetail,c.signTime,c.haveReport,c.status) from #{#entityName} c where c.counsellorRollcall=:counsellorRollcall and c.deleteFlag = :deleteFlag and c.status =:status")
    List<StudentSignInDomain> findAllByCounsellorRollcallAndStautsAndDeleteFlagOrderByStudentNum(@Param(value = "counsellorRollcall") CounsellorRollcall counsellorRollcall,
                                                                                                 @Param(value = "status") String status, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.dd.counsellorollcall.domain.StudentSignInDomain(c.id,c.counsellorRollcall.id,c.studentId,c.studentName,c.classId,c.className,c.haveRead,c.gpsLocation,c.gpsDetail,c.signTime,c.haveReport,c.status) from #{#entityName} c where c.counsellorRollcall=:counsellorRollcall and c.deleteFlag = :deleteFlag ")
    List<StudentSignInDomain> findAllByCounsellorRollcallAndStautsAndDeleteFlagOrderByStudentNum(@Param(value = "counsellorRollcall") CounsellorRollcall counsellorRollcall,
                                                                                                 @Param(value = "deleteFlag") Integer deleteFlag);
    @Transactional
    @Modifying
    @Query("update #{#entityName} c set c.status=:status where c.id in :ids")
    void updateStatusByIds(@Param(value = "ids") Set<Long> ids, @Param(value = "status") String status);

    @Query("select t from #{#entityName} t where t.id in :ids")
    public List<StudentSignIn> findByIds(@Param(value = "ids") Set<Long> ids);

    @Query("select new com.aizhixin.cloud.dd.counsellorollcall.domain.RollcallReportDomain(c.id,n.teacherId,n.teacherName,c.studentName,c.studentNum,n.id,c.gpsLocation,c.gpsDetail,c.signTime,n.openTime,c.haveReport,c.status,c.haveRead,n.status) from #{#entityName} c left join c.counsellorRollcall n where c.studentId=:studentId and c.deleteFlag = :deleteFlag and (n.tempGroup.rollcallNum is null or n.tempGroup.rollcallNum < 2) order by c.createdDate desc")
    List<RollcallReportDomain> listStudentSignIn(@Param(value = "studentId") Long studentId, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.dd.counsellorollcall.domain.StuRollcallReportDomainV2(c.id,n.id,c.gpsLocation,c.gpsDetail,c.signTime,n.openTime,c.haveReport,c.status,c.haveRead,c.gpsLocation2,c.gpsDetail2,c.signTime2,c.haveReport2,c.status2,n.status) from #{#entityName} c left join c.counsellorRollcall n where c.studentId=:studentId and c.deleteFlag = :deleteFlag and n.tempGroup.id=:groupId and n.openTime<=:endDate and n.openTime>=:startDate order by c.createdDate asc")
    List<StuRollcallReportDomainV2> findByStudentIdAndGroupIdAndDateAndDeleteFlag(@Param(value = "studentId") Long studentId, @Param(value = "groupId") Long groupId, @Param(value = "startDate") Date startDate, @Param(value = "endDate") Date endDate, @Param(value = "deleteFlag") Integer deleteFlag);

    @Modifying
    @Query("update #{#entityName} c set c.haveRead = true where c.id =:id")
    void updateHaveRead(@Param(value = "id") Long id);

    List<StudentSignIn> findAllByCounsellorRollcallOrderByStudentNum(CounsellorRollcall counsellorRollcall);

    List<StudentSignIn> findAllByCounsellorRollcallAndStatusOrderByStudentNum(CounsellorRollcall counsellorRollcall, Integer status);

    List<StudentSignIn> findAllByCounsellorRollcallAndHaveReadOrderByStudentNum(CounsellorRollcall counsellorRollcall, Boolean haveRead);

    List<StudentSignIn> findAllByCounsellorRollcallAndStatusAndHaveReadOrderByStudentNum(CounsellorRollcall counsellorRollcall, Integer status, Boolean haveRead);

    StudentSignIn findFirstByStudentIdAndDeleteFlagOrderByCreatedDateDesc(Long stuId, Integer deleteFlag);

}
