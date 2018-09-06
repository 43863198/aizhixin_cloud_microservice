package com.aizhixin.cloud.dd.rollcall.repository;

import com.aizhixin.cloud.dd.rollcall.entity.RollCallAppeal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RollCallAppealRepository extends JpaRepository<RollCallAppeal, Long> {
    Page<RollCallAppeal> findByStuIdAndAppealStatusAndDeleteFlag(Pageable pageable, Long stuId, Integer applealStatus, Integer deleteFlag);

    Page<RollCallAppeal> findByStuIdAndDeleteFlag(Pageable pageable, Long stuId, Integer deleteFlag);

    Page<RollCallAppeal> findByTeacherIdAndAppealStatusAndDeleteFlag(Pageable pageable, Long teacherId, Integer applealStatus, Integer deleteFlag);

    Page<RollCallAppeal> findByTeacherIdAndDeleteFlag(Pageable pageable, Long teacherId, Integer deleteFlag);
}
