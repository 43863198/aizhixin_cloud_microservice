package com.aizhixin.cloud.dd.counsellorollcall.repository;

import com.aizhixin.cloud.dd.counsellorollcall.entity.CounsellorRollcall;
import com.aizhixin.cloud.dd.counsellorollcall.entity.TempGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by LIMH on 2017/11/30.
 */
public interface CounsellorRollcallRepository extends JpaRepository<CounsellorRollcall, Long> {
    List<CounsellorRollcall> findAllByTempGroupAndStatusAndDeleteFlag(TempGroup tempGroup, Boolean status, Integer deleteFlag);

    List<CounsellorRollcall> findByTempGroup(TempGroup tempGroup);

    @Query(value = "select distinct rc.id from com.aizhixin.cloud.dd.counsellorollcall.entity.CounsellorRollcall rc where rc.openTime between :startDate and :endDate and rc.teacherId in (:teacherIds)")
    List<Long> findByTeacherIdIn(@Param("teacherIds") List<Long> teacherIds, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "select distinct rc.teacherId from com.aizhixin.cloud.dd.counsellorollcall.entity.CounsellorRollcall rc where rc.openTime between :startDate and :endDate and rc.teacherId in (:teacherIds)")
    List<Long> getIsHaveTeacherId(@Param("teacherIds") List<Long> teacherIds, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "select distinct rc.id from com.aizhixin.cloud.dd.counsellorollcall.entity.CounsellorRollcall rc where rc.openTime between :startDate and :endDate and rc.tempGroup in (:groups)")
    List<Long> findByTempGroupAndOpenTime(@Param("groups") List<TempGroup> groups, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
