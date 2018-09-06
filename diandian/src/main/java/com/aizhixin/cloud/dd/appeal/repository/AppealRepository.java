package com.aizhixin.cloud.dd.appeal.repository;

import com.aizhixin.cloud.dd.appeal.entity.Appeal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppealRepository extends JpaRepository<Appeal, Long> {
    Page<Appeal> findByApplicantIdAndAppealStatusAndDeleteFlag(Pageable pageable, Long applicantId, Integer applealStatus, Integer deleteFlag);

    Page<Appeal> findByApplicantIdAndDeleteFlag(Pageable pageable, Long applicantId, Integer deleteFlag);

    Page<Appeal> findByInspectorIdAndAppealStatusAndDeleteFlag(Pageable pageable, Long inspectorId, Integer applealStatus, Integer deleteFlag);

    Page<Appeal> findByInspectorIdAndDeleteFlag(Pageable pageable, Long inspectorId, Integer deleteFlag);
}
