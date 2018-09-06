package com.aizhixin.cloud.rollcall.repository;

import com.aizhixin.cloud.rollcall.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleRepository extends JpaRepository <Schedule, Long> {

    @Query("select distinct t.organId from #{#entityName} t where t.teachDate = :teachDate")
    List<Long> findOrgIdByTeachDate(@Param(value = "teachDate") String teachDate);
}
