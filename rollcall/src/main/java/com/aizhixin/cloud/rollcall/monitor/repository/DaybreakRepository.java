package com.aizhixin.cloud.rollcall.monitor.repository;

import com.aizhixin.cloud.rollcall.monitor.entity.DayBreak;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

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
