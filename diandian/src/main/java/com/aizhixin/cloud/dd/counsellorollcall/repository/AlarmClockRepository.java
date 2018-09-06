package com.aizhixin.cloud.dd.counsellorollcall.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.aizhixin.cloud.dd.counsellorollcall.entity.AlarmClock;
import com.aizhixin.cloud.dd.counsellorollcall.entity.TempGroup;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by LIMH on 2017/11/29.
 */
public interface AlarmClockRepository extends JpaRepository<AlarmClock, Long> {
    /**
     * 
     * @param tempGroup
     * @param deleteFlag
     * @return
     */
    AlarmClock findByTempGroupAndDeleteFlag(TempGroup tempGroup, Integer deleteFlag);

    @Query("select t from #{#entityName} t where t.tempGroup.id=:groupId and t.deleteFlag=:deleteFlag")
    List<AlarmClock> findByTempGroupAndDeleteFlag(@Param("groupId")Long groupId, @Param("deleteFlag") Integer deleteFlag);

    /**
     * 
     * @param status
     * @param deleteFlag
     * @param clockMode
     * @return
     */
    @Query("select a from com.aizhixin.cloud.dd.counsellorollcall.entity.AlarmClock a where a.status = ?1 and a.deleteFlag = ?2 and a.clockMode like  %?3%")
    List<AlarmClock> findAllByStatusAndDeleteFlagAndAndClockModeLike(Boolean status, Integer deleteFlag, String clockMode);

    @Transactional
    @Modifying
    @Query("update  #{#entityName} ac set ac.deleteFlag = 1 where ac.tempGroup=:tempGroup")
    void deleteAlarmClockByTempGroup(@Param("tempGroup") TempGroup tempGroup);
}
