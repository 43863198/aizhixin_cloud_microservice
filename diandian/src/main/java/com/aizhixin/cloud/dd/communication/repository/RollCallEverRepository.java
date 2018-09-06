package com.aizhixin.cloud.dd.communication.repository;

import com.aizhixin.cloud.dd.communication.entity.RollCallEver;
import com.aizhixin.cloud.dd.communication.entity.RollCallReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;


public interface RollCallEverRepository extends JpaRepository<RollCallEver, Long>, JpaSpecificationExecutor<RollCallEver> {

    @Modifying
    @Query(value = "update com.aizhixin.cloud.dd.communication.entity.RollCallEver rc set rc.status = false where rc.id = :rollCallEverId")
    void updateRollCallEver(@Param("rollCallEverId") Long z);

    @Modifying
    @Query(value = "update com.aizhixin.cloud.dd.communication.entity.RollCallEver rc set rc.status = false where rc.status = true")
    void closeRollCallEver();

    @Query(value = "select distinct rc.id from com.aizhixin.cloud.dd.communication.entity.RollCallEver rc where rc.openTime between :startDate and :endDate and rc.teacherId in (:teacherIds)")
    List<Long> findByTeacherIdIn(@Param("teacherIds") List<Long> teacherIds, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "select distinct rc.teacherId from com.aizhixin.cloud.dd.communication.entity.RollCallEver rc where rc.openTime between :startDate and :endDate and rc.teacherId in (:teacherIds)")
    List<Long> getIsHaveTeacherId(@Param("teacherIds") List<Long> teacherIds, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    RollCallEver findFirstByDeleteFlagAndClassIdsLikeOrderByCreatedDateDesc(Integer deleteFlag,String classIds);
    
}
