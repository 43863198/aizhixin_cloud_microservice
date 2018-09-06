package com.aizhixin.cloud.dd.counsellorollcall.v1.service;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.counsellorollcall.domain.TempGroupDomain;
import com.aizhixin.cloud.dd.counsellorollcall.entity.AlarmClock;
import com.aizhixin.cloud.dd.counsellorollcall.entity.TempGroup;
import com.aizhixin.cloud.dd.counsellorollcall.repository.AlarmClockRepository;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LIMH on 2017/12/5.
 */
@Service
@Transactional
public class AlarmClockService {

    private final Logger LOG = LoggerFactory.getLogger(AlarmClockService.class);

    @Autowired
    private AlarmClockRepository alarmClockRepository;

    @Lazy
    @Autowired
    private TempGroupService tempGroupService;

    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;

    private static Map<Long, List<Long>> alarmMap = new ConcurrentHashMap<>();
    private static Map<Long, List<Long>> closeAlarmMap = new ConcurrentHashMap<>();

    public AlarmClock getAlarmClock(TempGroup tempGroup) {
        return alarmClockRepository.findByTempGroupAndDeleteFlag(tempGroup, DataValidity.VALID.getState());
    }

    public void save(AlarmClock alarmClock) {
        alarmClockRepository.save(alarmClock);
    }

    /**
     * 执行导员点名定时任务
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Object needAlarmClockTempGroup() {
        Date now = new Date();
        Integer weekOfDate = DateFormatUtil.getWeekOfDate(DateFormatUtil.formatShort(now));

        List<AlarmClock> alarmClockList = alarmClockRepository.findAllByStatusAndDeleteFlagAndAndClockModeLike(Boolean.TRUE, DataValidity.VALID.getState(), String.valueOf(weekOfDate));
        if (null == alarmClockList || alarmClockList.isEmpty()) {
            return null;
        }
        LOG.info("执行定时任务的数据集合为({})", alarmClockList.size());
        for (AlarmClock alarmClock : alarmClockList) {
            try {
                //开启点名任务
                alarmClock(now, alarmClock, weekOfDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (alarmClock.getTempGroup().getRollcallNum() != null && alarmClock.getTempGroup().getRollcallNum().intValue() > 1 && alarmClock.getEndTime() != null) {
                    closeAlarmClock(now, alarmClock, weekOfDate);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return null;
    }

    /**
     * 处理定时任务 开启点名
     *
     * @param date
     * @param alarmClock
     * @param weekOfDate
     */
    public void alarmClock(Date date, AlarmClock alarmClock, Integer weekOfDate) {
        if (alarmClock == null) {
            return;
        }
        if (StringUtils.isBlank(alarmClock.getClockTime()) || StringUtils.isBlank(alarmClock.getClockMode())) {
            return;
        }
        if (alarmClock.getTempGroup().getStatus()) {
            return;
        }

        // 检查教师是否是辅导员
        List<IdNameDomain> classList = orgManagerRemoteService.getClassesByTeacher(alarmClock.getTempGroup().getTeacherId());
        if (classList == null || classList.size() == 0) {
            alarmClockRepository.deleteAlarmClockByTempGroup(alarmClock.getTempGroup());
            LOG.warn("教师[" + alarmClock.getTempGroup().getTeacherId() + "]不是辅导员，删除点名组[" + alarmClock.getTempGroup().getId() + "]定时任务");
            return;
        }

        Date alarmTime = null;
        String time = null;
        try {
            time = DateFormatUtil.formatShort(date) + " " + alarmClock.getClockTime();
            alarmTime = DateFormatUtil.parse(time, DateFormatUtil.FORMAT_MINUTE);
        } catch (Exception e) {
            LOG.warn("辅导员定时任务时间转换异常," + time, e.getMessage());
            return;
        }

        Long currentTimeMil = date.getTime();
        Long tenTimeMil = currentTimeMil + 5 * 60 * 1000;
        Long alarmTimeMil = alarmTime.getTime();

        // 只执行接下来5分钟的任务
        if (alarmTimeMil >= currentTimeMil && alarmTimeMil < tenTimeMil) {
            addAlarmMap(alarmClock.getTempGroup().getId(), alarmTimeMil);
        } else if (StringUtils.isNotEmpty(alarmClock.getEndTime()) && !alarmClock.getTempGroup().getStatus()) {
            //未到结束时间，立即开启
            Date endTime = null;
            try {
                String timeStr = DateFormatUtil.formatShort(date) + " " + alarmClock.getEndTime();
                endTime = DateFormatUtil.parse(timeStr, DateFormatUtil.FORMAT_MINUTE);
            } catch (Exception e) {
                LOG.warn("Exception", e);
                return;
            }
            if (endTime != null && endTime.getTime() > currentTimeMil && (currentTimeMil - alarmTimeMil) > 1 * 60 * 1000) {
                //立即开启
                LOG.warn("点名立即开启:"+alarmClock.getTempGroup().getId());
                openTempGroup(alarmClock.getTempGroup().getId());
            }
        }
    }

    /**
     * 处理定时任务 关闭点名
     *
     * @param date
     * @param alarmClock
     * @param weekOfDate
     */
    public void closeAlarmClock(Date date, AlarmClock alarmClock, Integer weekOfDate) {
        if (alarmClock == null) {
            return;
        }
        if (StringUtils.isBlank(alarmClock.getEndTime())) {
            return;
        }
        if (!alarmClock.getTempGroup().getStatus()) {
            return;
        }
        Date endTime = null;
        String time = null;
        try {
            time = DateFormatUtil.formatShort(date) + " " + alarmClock.getEndTime();
            endTime = DateFormatUtil.parse(time, DateFormatUtil.FORMAT_MINUTE);
        } catch (Exception e) {
            LOG.warn("辅导员定时任务时间转换异常," + time, e.getMessage());
            return;
        }

        Long currentTimeMil = date.getTime();
        Long tenTimeMil = currentTimeMil + 5 * 60 * 1000;
        Long endTimeMil = endTime.getTime();

        // 只执行接下来5分钟的任务
        if (endTimeMil >= currentTimeMil && endTimeMil < tenTimeMil) {
            addCloseAlarmMap(alarmClock.getTempGroup().getId(), endTimeMil);
        } else if ((currentTimeMil - endTimeMil) > 1 * 60 * 1000) {
            //判断当前时间已经大于截止时间1分钟，立即关闭
            LOG.warn("判断当前时间已经大于截止时间1分钟:" + alarmClock.getTempGroup().getId());
            closeTempGroup(alarmClock.getTempGroup().getId());
        }
    }

    public void addAlarmMap(Long tempGroupId, Long alarmTimeMil) {
        List<Long> longList = alarmMap.get(alarmTimeMil);
        if (null == longList) {
            longList = new ArrayList<>();
            alarmMap.put(alarmTimeMil, longList);
        }
        longList.add(tempGroupId);
    }

    public void addCloseAlarmMap(Long tempGroupId, Long alarmTimeMil) {
        List<Long> longList = closeAlarmMap.get(alarmTimeMil);
        if (null == longList) {
            longList = new ArrayList<>();
            closeAlarmMap.put(alarmTimeMil, longList);
        }
        longList.add(tempGroupId);
    }

    @Scheduled(cron = "50 * * * * ?")
    public void doOpenTempGroup() {
        if (alarmMap.isEmpty()) {
            return;
        }
        Long nowTime = System.currentTimeMillis();
        long oneMin = 60 * 1000;

        for (Map.Entry<Long, List<Long>> entry : alarmMap.entrySet()) {
            if (entry.getKey() - nowTime <= oneMin) {
                List<Long> tempGroups = entry.getValue();
                if (null != tempGroups && !tempGroups.isEmpty()) {
                    for (Long tempGroupId : tempGroups) {
                        TempGroup tempGroup = tempGroupService.findOne(tempGroupId);
                        if (null == tempGroup || DataValidity.INVALID.getState().equals(tempGroup.getDeleteFlag())) {
                            continue;
                        }
                        AlarmClock alarmClock = alarmClockRepository.findByTempGroupAndDeleteFlag(tempGroup, DataValidity.VALID.getState());
                        if (null != alarmClock && alarmClock.getStatus()) {
                            tempGroupService.openTempGroupSchedule(TokenUtil.accessToken, tempGroupId);
                            LOG.info("定时任务开启导员点名组,组id:" + tempGroupId);
                            alarmMap.remove(entry.getKey());
                        }
                    }
                }
            }
        }
    }

    private void openTempGroup(Long tempGroupId) {
        TempGroup tempGroup = tempGroupService.findOne(tempGroupId);
        if (tempGroup != null && DataValidity.VALID.getState().equals(tempGroup.getDeleteFlag())) {
            AlarmClock alarmClock = alarmClockRepository.findByTempGroupAndDeleteFlag(tempGroup, DataValidity.VALID.getState());
            if (null != alarmClock && alarmClock.getStatus()) {
                tempGroupService.openTempGroupSchedule(TokenUtil.accessToken, tempGroupId);
                LOG.info("立即开启导员点名组,组id:" + tempGroupId);
            }
        }
    }

    @Scheduled(cron = "59 * * * * ?")
    public void doCloseTempGroup() {
        if (closeAlarmMap.isEmpty()) {
            return;
        }
        Long nowTime = System.currentTimeMillis();
        long oneMin = 60 * 1000;

        for (Map.Entry<Long, List<Long>> entry : closeAlarmMap.entrySet()) {
            if (entry.getKey() - nowTime <= oneMin) {
                List<Long> tempGroups = entry.getValue();
                if (null != tempGroups && !tempGroups.isEmpty()) {
                    for (Long tempGroupId : tempGroups) {
                        TempGroup tempGroup = tempGroupService.findOne(tempGroupId);
                        if (null == tempGroup || DataValidity.INVALID.getState().equals(tempGroup.getDeleteFlag())) {
                            continue;
                        }
                        AlarmClock alarmClock = alarmClockRepository.findByTempGroupAndDeleteFlag(tempGroup, DataValidity.VALID.getState());
                        if (null != alarmClock && alarmClock.getStatus()) {
                            tempGroupService.closeTempGroup(tempGroupId);
                            LOG.info("定时任务关闭导员点名组,组id:" + tempGroupId);
                            closeAlarmMap.remove(entry.getKey());
                        }
                    }
                }
            }
        }
    }

    private void closeTempGroup(Long tempGroupId) {
        LOG.info("closeTempGroup,组id:" + tempGroupId);
        try {
            TempGroup tempGroup = tempGroupService.findOne(tempGroupId);
            if (tempGroup != null && DataValidity.VALID.getState().equals(tempGroup.getDeleteFlag())) {
                AlarmClock alarmClock = alarmClockRepository.findByTempGroupAndDeleteFlag(tempGroup, DataValidity.VALID.getState());
                if (null != alarmClock && alarmClock.getStatus()) {
                    tempGroupService.closeTempGroup(tempGroupId);
                    LOG.info("立即关闭导员点名组,组id:" + tempGroupId);
                }
            }
        } catch (Exception e) {
            LOG.info("Exception", e);
        }
    }

    /**
     * 临时添加任务组
     *
     * @param doTime
     * @param tempGroupId
     */
    public void putAlarmMap(Long doTime, Long tempGroupId) {
        long tenMin = 5 * 60 * 1000;
        long time = doTime - System.currentTimeMillis();
        if (time < tenMin && time > 0) {
            addAlarmMap(tempGroupId, doTime);
        }
    }

    public void putAlarmMap(Long doTime, Long stopTime, Long tempGroupId) {
        long tenMin = 5 * 60 * 1000;
        long time = doTime - System.currentTimeMillis();
        long stime = stopTime - System.currentTimeMillis();
        if (time < tenMin && stime > 0) {
            addAlarmMap(tempGroupId, doTime);
        }
    }

    public void complementAlarmInfo(List<TempGroupDomain> tempGroupDomains) {
        if (tempGroupDomains == null || tempGroupDomains.isEmpty()) {
            return;
        }
        for (TempGroupDomain tempGroupDomain : tempGroupDomains) {
            AlarmClock alarmClock = alarmClockRepository.findByTempGroupAndDeleteFlag(tempGroupService.findOne(tempGroupDomain.getId()), DataValidity.VALID.getState());
            if (alarmClock != null) {
                tempGroupDomain.setAlarmTime(alarmClock.getClockTime());
                tempGroupDomain.setAlarmModel(alarmClock.getClockMode());
                tempGroupDomain.setStatus(alarmClock.getStatus());
            }
        }
    }

    public void deleteByTempGroupId(TempGroup tempGroup) {
        alarmClockRepository.deleteAlarmClockByTempGroup(tempGroup);
    }

}
