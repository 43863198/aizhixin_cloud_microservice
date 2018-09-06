package com.aizhixin.cloud.dd.rollcall.repository;

import com.aizhixin.cloud.dd.rollcall.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import com.aizhixin.cloud.dd.rollcall.entity.ScheduleRollCall;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ScheduleRollCallRepository extends JpaRepository<ScheduleRollCall, Long> {

    ScheduleRollCall findBySchedule_Id(Long scheduleId);

    ScheduleRollCall findBySchedule(Schedule schedule);

    @Transactional
    @Modifying
    @Query("update com.aizhixin.cloud.dd.rollcall.entity.ScheduleRollCall sc set sc.localtion = ?2 where sc.id = ?1")
    void updateScheduleVerify(Long Id, String verify);

    @Transactional
    @Modifying
    @Query("delete from  com.aizhixin.cloud.dd.rollcall.entity.ScheduleRollCall sc  where sc.schedule.id = ?1")
    void deleteByScheduleId(Long scheduleId);
}
