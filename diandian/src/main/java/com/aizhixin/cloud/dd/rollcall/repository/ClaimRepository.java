package com.aizhixin.cloud.dd.rollcall.repository;

import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.rollcall.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimRepository extends JpaRepository <Claim, Long> {
    public List <Claim> findAllByTeachingclassIdAndTeachDateAndPeriodNoAndPeriodNumAndDeleteFlag(Long teachingclassId, String teachDate, Integer periodNo, Integer periodNum, Integer deleteFlag);


    public List <Claim> findAllByTeachingclassIdAndDeleteFlagAndTeachDateBeforeOrderByTeachDateDesc(Long teachingclassId, Integer deleteFlag, String teachDate);
}
