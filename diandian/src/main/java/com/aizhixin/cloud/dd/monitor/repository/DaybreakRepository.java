package com.aizhixin.cloud.dd.monitor.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import com.aizhixin.cloud.dd.monitor.entity.DayBreak;

/**
 * @author LIMH
 * @date 2017/12/11
 */
public interface DaybreakRepository extends MongoRepository<DayBreak, String> {

    /**
     * 刪除当天重复的排课信息
     * 
     * @param orgId
     * @param date
     */
    void deleteByOrgIdAndTeachDate(Long orgId, String date);

    List<DayBreak> findAllByTeachDateAndOrgNameLike(String teachDate, String orgName);

    List<DayBreak> findAllByTeachDate(String teachDate);

    List<DayBreak> findAllByOrgIdAndTeachDateAndSuccessFlag(Long orgId, String teachDate, Integer successFlag);

    List<DayBreak> findAllByOrgIdAndTeachDate(Long orgId, String teachDate);

    void deleteByScheduleId(Long scheduleId);

    void deleteByTeachingclassIdAndTeachDateAndPeriodNoAndPerioidNum(Long teachingclassId, String teachDate, Integer periodNo, Integer periodNum);

    List<DayBreak> findAllByTeachingclassIdAndTeachDateAndPeriodNoAndPerioidNum(Long teachingclassId, String teachDate, Integer periodNo, Integer periodNum);
}
