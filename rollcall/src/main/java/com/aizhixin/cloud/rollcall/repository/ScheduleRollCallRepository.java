package com.aizhixin.cloud.rollcall.repository;

import com.aizhixin.cloud.rollcall.entity.ScheduleRollCall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ScheduleRollCallRepository extends JpaRepository <ScheduleRollCall, Long> {
    ScheduleRollCall findBySchedule_Id(Long scheduleId);

    List <ScheduleRollCall> findBySchedule_teachDateAndSchedule_organId(String teachDate, Long organId);

    List <ScheduleRollCall> findAllByIdIn(Set <Long> ids);
}
