package com.aizhixin.cloud.dd.counsellorollcall.v1.service;

import java.util.*;

import com.aizhixin.cloud.dd.counsellorollcall.entity.*;
import com.aizhixin.cloud.dd.counsellorollcall.repository.CounsellorRollcallRepository;
import com.aizhixin.cloud.dd.counsellorollcall.repository.CounsellorRollcallRuleRepository;
import com.aizhixin.cloud.dd.counsellorollcall.utils.CounsellorRollCallType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.dd.common.core.ApiReturn;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.counsellorollcall.domain.TempGroupDomain;
import com.aizhixin.cloud.dd.counsellorollcall.dto.CounRollcallGroupDTO;
import com.aizhixin.cloud.dd.counsellorollcall.repository.TempGroupRepository;
import com.aizhixin.cloud.dd.orgStructure.service.UserInfoService;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.service.ClassesService;

@Service
@Transactional
public class TempGroupService {

    private final Logger LOG = LoggerFactory.getLogger(TempGroupService.class);

    @Autowired
    private TempGroupRepository tempGroupRepository;

    @Autowired
    private StudentSubGroupService studentSubGroupService;

    @Autowired
    private CounsellorRollcallService conunsellorRollcallService;

    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;

    @Autowired
    private ClassesService classesService;

    @Autowired
    private AlarmClockService alarmClockService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private CounsellorRedisService counsellorRedisService;

    @Autowired
    private CounsellorRollcallRuleRepository ruleRepository;

    @Autowired
    private CounsellorRollcallRepository rollcallRepository;

    /**
     * 获取所有点名组
     *
     * @param teacherId
     * @return
     */
    // @Transactional(readOnly = true)
    public Map<String, Object> listCounsellorGroup(Long organId, Long teacherId, String teacherName) {
        List<TempGroupDomain> tempGroupDomains = tempGroupRepository.findAllByTeacherIdAndDeleteFlag(teacherId, DataValidity.VALID.getState());
        if (tempGroupDomains == null || tempGroupDomains.isEmpty()) {
            List<TempGroupDomain> deleteTempGroupDomains = tempGroupRepository.findAllByTeacherId(teacherId);
            if (deleteTempGroupDomains == null || deleteTempGroupDomains.isEmpty()) {
                // 第一次使用辅导员点名功能，默认创建（N+1）个点名组
                tempGroupDomains = createDefaultCounsellorGroup(organId, teacherId, teacherName);
            }
        } else {
            // 检查是否有新添加的行政班
            tempGroupDomains = checkoutNewClass(tempGroupDomains, teacherId, teacherName, organId);
        }
        alarmClockService.complementAlarmInfo(tempGroupDomains);
        return ApiReturn.message(Boolean.TRUE, null, tempGroupDomains);
    }

    List<TempGroupDomain> checkoutNewClass(List<TempGroupDomain> tempGroupDomains, Long teacherId, String teacherName, Long organId) {
        if (tempGroupDomains == null) {
            return null;
        }
        for (TempGroupDomain tempGroupDomain : tempGroupDomains) {
            if (null == tempGroupDomain) {
                continue;
            }
            if ("所有行政班".equals(tempGroupDomain.getName())) {
                // 查询
                List<IdNameDomain> classesInfo = orgManagerRemoteService.getClassesByTeacher(teacherId);
                if (classesInfo != null && !classesInfo.isEmpty()) {
                    // 创建默认分组
                    Set<Long> allStudentIds = new HashSet<>();
                    for (IdNameDomain idNameDomain : classesInfo) {
                        Set<Long> studentIds = classesService.getStudentIdsNotIncludeException(idNameDomain.getId());
                        if (studentIds == null || studentIds.isEmpty()) {
                            continue;
                        }
                        allStudentIds.addAll(studentIds);
                    }
                    if (tempGroupDomain.getSubGroupNum().intValue() == allStudentIds.size()) {
                        break;
                    }
                    if (!tempGroupDomain.getStatus()) {
                        TempGroup tempGroup = tempGroupRepository.findOne(tempGroupDomain.getId());
                        tempGroup.setSubGroupNum(allStudentIds.size());
                        tempGroupRepository.save(tempGroup);
                        updateSubStudent(allStudentIds, tempGroup);
                        tempGroupDomains = tempGroupRepository.findAllByTeacherIdAndDeleteFlag(teacherId, DataValidity.VALID.getState());
                    }
                }
            }
        }
        return tempGroupDomains;
    }

    /**
     * 删除点名组
     *
     * @param tempGroupIds
     */
    public Map<String, Object> deleteTempGroup(Set<Long> tempGroupIds) {
        if (tempGroupIds != null) {
            for (Long tempGroupId : tempGroupIds) {
                TempGroup tempGroup = tempGroupRepository.findOne(tempGroupId);
                if (null == tempGroup) {
                    throw new NullPointerException();
                }
                tempGroup.setDeleteFlag(DataValidity.INVALID.getState());
                tempGroupRepository.save(tempGroup);
                alarmClockService.deleteByTempGroupId(tempGroup);
            }
            try {
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("--------------------------------------------------");
                        counsellorRedisService.delCacheByDelTempGroup(tempGroupIds);
                    }
                };
                Thread thread = new Thread(myRunnable);
                thread.start();
            } catch (Exception e) {
                LOG.info("delCacheByDelTempGroup-1", e);
            }
        }
        return ApiReturn.message(Boolean.TRUE, null, null);
    }

    /**
     * 校验时间格式
     *
     * @param alarmTime
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> checkTime(String alarmTime) {
        try {
            if (StringUtils.isBlank(alarmTime)) {
                return null;
            }
            String date = DateFormatUtil.formatShort(new Date()) + " " + alarmTime;
            DateFormatUtil.parse(date, "yyyy-MM-dd HH:mm");
        } catch (Exception e) {
            return ApiReturn.message(Boolean.FALSE, "时间格式校验错误", null);
        }
        return null;
    }

    /**
     * 保存导员分组信息 ````````````````````````````````````````````
     *
     * @param teacherId
     * @param teacherName
     * @param counRollcallGroupDTO
     * @return
     */
    public Map<String, Object> saveTempGroup(Long orgId, Long teacherId, String teacherName, CounRollcallGroupDTO counRollcallGroupDTO) {

        Map<String, Object> map = checkTime(counRollcallGroupDTO.getAlarmTime());
        if (null != map) {
            return map;
        }

        Set<Long> studentList = getChooseStudents(counRollcallGroupDTO);

        if (studentList == null || studentList.isEmpty()) {
            return ApiReturn.message(Boolean.FALSE, "点名组必须包含学生!", null);
        }

        Map result = checkCounRollcallGroup(teacherId, counRollcallGroupDTO, studentList);
        if (null != result) {
            return result;
        }
        try {

            TempGroup tempGroup = new TempGroup(counRollcallGroupDTO.getTempGroupName(), teacherId, teacherName, studentList.size(), Boolean.FALSE, orgId, UUID.randomUUID().toString());
            tempGroup.setRollcallType(CounsellorRollCallType.Other.getType());
            tempGroup.setRollcallNum(1);
            tempGroupRepository.save(tempGroup);

            // 保存定时点名信息
            saveOrUpdateAlarmClock(tempGroup, counRollcallGroupDTO.getAlarmTime(), counRollcallGroupDTO.getAlarmModel(), counRollcallGroupDTO.getAlarmOnOff());

            List<StudentSubGroup> studentSubGroupList = new ArrayList<>();
            for (Long studentId : studentList) {
                studentSubGroupList.add(new StudentSubGroup(tempGroup, studentId));
            }
            studentSubGroupService.save(studentSubGroupList);

        } catch (Exception e) {
            e.printStackTrace();
            LOG.warn("开启点名失败2:" + e.getMessage(), e.getMessage());
            return ApiReturn.message(Boolean.FALSE, "保存异常,联系管理员", null);
        }
        return ApiReturn.message(Boolean.TRUE, null, null);
    }

    public Map<String, Object> updateTempGroupAlarm(Long tempGroupId, Boolean alarmOnOff) {
        TempGroup tempGroup = findOne(tempGroupId);
        if (null == tempGroup) {
            return ApiReturn.message(Boolean.FALSE, "tmepGroup is not found", null);
        }
        AlarmClock alarmClock = alarmClockService.getAlarmClock(tempGroup);
        if (null == alarmClock) {
            return ApiReturn.message(Boolean.FALSE, "请先设置定时任务信息。", null);
        }
        return saveOrUpdateAlarmClock(tempGroup, alarmClock.getClockTime(), alarmClock.getClockMode(), alarmOnOff);
    }

    /**
     * 修改导员点名组
     *
     * @param teacherId
     * @param teacherName
     * @param counRollcallGroupDTO
     * @return
     */
    public Map<String, Object> updateTempGroup(Long teacherId, String teacherName, CounRollcallGroupDTO counRollcallGroupDTO) {

        Map<String, Object> map = checkTime(counRollcallGroupDTO.getAlarmTime());
        if (null != map) {
            return map;
        }

        Set<Long> studentList = getChooseStudents(counRollcallGroupDTO);

        Map result = checkCounRollcallGroup(teacherId, counRollcallGroupDTO, studentList);
        if (null != result) {
            return result;
        }

        TempGroup tempGroup = tempGroupRepository.findOne(counRollcallGroupDTO.getTempGroupId());
        if (null == tempGroup) {
            throw new NullPointerException();
        }

        tempGroup.setName(counRollcallGroupDTO.getTempGroupName());
        if (!studentList.isEmpty()) {
            tempGroup.setSubGroupNum(studentList.size());
        }
        tempGroupRepository.save(tempGroup);

        saveOrUpdateAlarmClock(tempGroup, counRollcallGroupDTO.getAlarmTime(), counRollcallGroupDTO.getAlarmModel(), counRollcallGroupDTO.getAlarmOnOff());

        updateSubStudent(studentList, tempGroup);

        return ApiReturn.message(Boolean.TRUE, null, null);
    }

    public void updateSubStudent(Set<Long> studentList, TempGroup tempGroup) {
        if (!studentList.isEmpty()) {
            List<StudentSubGroup> studentSubGroups = studentSubGroupService.findAllByTempGroupAndDeleteFlag(tempGroup, DataValidity.VALID.getState());
            if (null == studentSubGroups || studentSubGroups.isEmpty()) {
                throw new NullPointerException();
            }

            // 计算新增的学生
            List newList = new ArrayList();
            for (Long studentId : studentList) {
                newList.add(studentId);
            }

            List oldList = new ArrayList();
            for (StudentSubGroup studentSubGroup : studentSubGroups) {
                oldList.add(studentSubGroup.getStudentId());
            }

            newList.retainAll(oldList);

            List<StudentSubGroup> addList = new ArrayList<>();
            for (Long studentId : studentList) {
                if (newList.contains(studentId)) {
                    continue;
                }
                addList.add(new StudentSubGroup(tempGroup, studentId));
            }

            for (StudentSubGroup studentSubGroup : studentSubGroups) {
                if (newList.contains(studentSubGroup.getStudentId())) {
                    continue;
                }
                studentSubGroup.setDeleteFlag(DataValidity.INVALID.getState());
                addList.add(studentSubGroup);
            }

            studentSubGroupService.save(addList);
        }
    }

    @Async
    public void openTempGroupSchedule(String accessToken, Long tempGroupId) {
        Random r = new Random(150);
        try {
            Thread.sleep(r.nextInt());
        } catch (Exception e) {
        }
        openTempGroup(accessToken, tempGroupId);
    }

    /**
     * 开启分组点名
     *
     * @param tempGroupId
     * @return
     */
    public Map<String, Object> openTempGroup(String accessToken, Long tempGroupId) {
        if (tempGroupId == null) {
            LOG.warn("开启点名失败-0-为空");
            throw new NullPointerException();
        }
        TempGroup tempGroup = tempGroupRepository.findOne(tempGroupId);
        if (null == tempGroup) {
            LOG.warn("开启点名失败-1-为空:" + tempGroupId);
            throw new NullPointerException();
        }
        if (tempGroup.getStatus()) {
            return ApiReturn.message(Boolean.FALSE, "该分组有点名正在进行中,请关闭点名后再进行操作。", null);
        }
        try {
            tempGroup.setStatus(Boolean.TRUE);
            tempGroupRepository.save(tempGroup);
            //redis缓存点名规则
            if (tempGroup.getRollcallNum() != null && tempGroup.getRollcallNum().intValue() > 1 && tempGroup.getRuleId() != null) {
                try {
                    Date startDate = new Date();
                    Calendar sCalendar = Calendar.getInstance();
                    sCalendar.setTime(startDate);
                    sCalendar.set(Calendar.HOUR_OF_DAY, 0);
                    sCalendar.set(Calendar.MINUTE, 0);
                    sCalendar.set(Calendar.SECOND, 0);
                    startDate = sCalendar.getTime();
                    Date endDate = new Date();
                    Calendar eCalendar = Calendar.getInstance();
                    eCalendar.setTime(endDate);
                    eCalendar.set(Calendar.HOUR_OF_DAY, 23);
                    eCalendar.set(Calendar.MINUTE, 59);
                    eCalendar.set(Calendar.SECOND, 59);
                    endDate = eCalendar.getTime();
                    List<TempGroup> groups = new ArrayList<>();
                    groups.add(tempGroup);
                    List<Long> rollcallList = rollcallRepository.findByTempGroupAndOpenTime(groups, startDate, endDate);
                    if (rollcallList != null && rollcallList.size() > 0) {
                        return ApiReturn.message(Boolean.FALSE, "点名今日已经开启过!", null);
                    }
                } catch (Exception e) {
                    LOG.warn("Exception", e);
                }
                CounsellorRollcallRule rule = ruleRepository.findOne(tempGroup.getRuleId());
                counsellorRedisService.putRollcallRule(tempGroup.getId(), rule);
            }
            //开启点名
            conunsellorRollcallService.openConunsellorRollcall(accessToken, tempGroup);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.warn("开启点名失败1:" + tempGroupId, e);
            return ApiReturn.message(Boolean.FALSE, "开启点名失败!联系管理员", null);
        }
        return ApiReturn.message(Boolean.TRUE, null, null);
    }

    /**
     * 关闭点名组
     *
     * @param tempGroupId
     * @return
     */
    public Map<String, Object> closeTempGroup(Long tempGroupId) {
        LOG.warn("closeTempGroup:" + tempGroupId);
        TempGroup tempGroup = tempGroupRepository.findOne(tempGroupId);
        if (null == tempGroup) {
            throw new NullPointerException();
        }
        if (!tempGroup.getStatus()) {
            return ApiReturn.message(Boolean.FALSE, "没有正在进行的点名组，请确认该操作。", null);
        }
        try {
            tempGroup.setStatus(Boolean.FALSE);
            tempGroupRepository.save(tempGroup);
            conunsellorRollcallService.closeConunsellorRollcallAsyn(tempGroup, false, false);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.warn("关闭点名组失败:" + e.getMessage(), e.getMessage());
            LOG.warn("Exception", e);
            return ApiReturn.message(Boolean.FALSE, "关闭点名组失败!联系管理员", null);
        }
        return ApiReturn.message(Boolean.TRUE, null, null);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void closeAllTempGroup() {
        try {
            List<TempGroup> tempGroups = tempGroupRepository.findAllByStatusAndDeleteFlag(Boolean.TRUE, DataValidity.VALID.getState());
            if (tempGroups == null || tempGroups.isEmpty()) {
                return;
            }
            for (TempGroup tempGroup : tempGroups) {
                LOG.info("开始关闭导员点名:" + tempGroup.getId());
                conunsellorRollcallService.closeConunsellorRollcallV2(tempGroup, false, true, true);
                LOG.info("完成关闭导员点名:" + tempGroup.getId());
            }
            LOG.info("定时关闭点名完成");
        } catch (Exception e) {
            e.printStackTrace();
            LOG.warn("定时任务，关闭点名异常", e.getMessage());
        }
    }

    /**
     * 校验 CounRollcallGroupDTO
     *
     * @param teacherId
     * @param counRollcallGroupDTO
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> checkCounRollcallGroup(Long teacherId, CounRollcallGroupDTO counRollcallGroupDTO, Set<Long> studentList) {
        if (verufucationName(teacherId, counRollcallGroupDTO.getTempGroupName())) {
            return ApiReturn.message(Boolean.FALSE, "组名称重复!", null);
        }
        return null;
    }

    /**
     * 校验分组名称
     *
     * @param teacherId
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    public boolean verufucationName(Long teacherId, String name) {
        List<TempGroup> tempGroups = tempGroupRepository.findAllByTeacherIdAndNameAndDeleteFlag(teacherId, name, DataValidity.VALID.getState());
        if (tempGroups == null || tempGroups.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * 创建默认分组
     */
    public List<TempGroupDomain> createDefaultCounsellorGroup(Long organId, Long teacherId, String teacherName) {
        List<TempGroupDomain> tempGroupDomains = null;
        List<IdNameDomain> classesInfo = orgManagerRemoteService.getClassesByTeacher(teacherId);
        if (classesInfo != null && !classesInfo.isEmpty()) {
            // 创建默认分组
            Set<Long> allStudentIds = new HashSet<>();
            for (IdNameDomain idNameDomain : classesInfo) {
                Set<Long> studentIds = classesService.getStudentIdsNotIncludeException(idNameDomain.getId());
                if (studentIds == null || studentIds.isEmpty()) {
                    continue;
                }
                allStudentIds.addAll(studentIds);
                saveTempGroup(organId, teacherId, teacherName, new CounRollcallGroupDTO(idNameDomain.getName(), studentIds));
            }
            // 创建所有行政班集合分组
            saveTempGroup(organId, teacherId, teacherName, new CounRollcallGroupDTO("所有行政班", allStudentIds));
            tempGroupDomains = tempGroupRepository.findAllByTeacherIdAndDeleteFlag(teacherId, DataValidity.VALID.getState());
        } else {
            tempGroupDomains = new ArrayList<>();
        }
        return tempGroupDomains;
    }

    /**
     * 定时任务保存
     */
    public Map<String, Object> saveOrUpdateAlarmClock(TempGroup tempGroup, String alarmTime, String alarmModel, Boolean alarmOnOff) {
        if (null == tempGroup) {
            return ApiReturn.message(Boolean.FALSE, "tmepGroup is not found", null);
        }
        if (StringUtils.isBlank(alarmTime) || StringUtils.isBlank(alarmModel)) {
            return ApiReturn.message(Boolean.FALSE, "请先设置定时任务信息。", null);
        }
        AlarmClock alarmClock = alarmClockService.getAlarmClock(tempGroup);
        if (null == alarmClock) {
            alarmClock = new AlarmClock();
            alarmClock.setTempGroup(tempGroup);
        }

        if (StringUtils.isBlank(alarmTime) || StringUtils.isBlank(alarmModel)) {
            alarmClock.setStatus(Boolean.FALSE);
        } else {
            if (null != alarmOnOff) {
                alarmClock.setStatus(alarmOnOff);
            } else {
                alarmClock.setStatus(Boolean.TRUE);
            }
            alarmClock.setClockTime(alarmTime);
            alarmClock.setClockMode(alarmModel);
        }
        alarmClockService.save(alarmClock);

        try {
            Date now = new Date();
            Integer weekOfDate = DateFormatUtil.getWeekOfDate(DateFormatUtil.formatShort(now));

            if (-1 == alarmClock.getClockMode().indexOf(String.valueOf(weekOfDate))) {
                return ApiReturn.message(Boolean.TRUE, null, null);
            }

            String str = DateFormatUtil.formatShort(now) + " " + alarmClock.getClockTime();
            Date doTime = DateFormatUtil.parse(str, DateFormatUtil.FORMAT_MINUTE);
            if (tempGroup.getRollcallNum() != null && tempGroup.getRollcallNum().intValue() > 1) {
                String endStr = DateFormatUtil.formatShort(now) + " " + alarmClock.getEndTime();
                Date endTime = DateFormatUtil.parse(endStr, DateFormatUtil.FORMAT_MINUTE);
                alarmClockService.putAlarmMap(doTime.getTime(), endTime.getTime(), tempGroup.getId());
            } else {
                alarmClockService.putAlarmMap(doTime.getTime(), tempGroup.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOG.warn("添加定时任务异常,组id:()", tempGroup.getId(), e.getMessage());
        }
        return ApiReturn.message(Boolean.TRUE, null, null);
    }

    @Transactional(readOnly = true)
    public TempGroup findOne(Long tempGroupId) {
        return tempGroupRepository.findOne(tempGroupId);
    }

    /**
     * 获取选中的学生
     *
     * @param counRollcallGroupDTO
     * @return
     */
    @Transactional(readOnly = true)
    public Set<Long> getChooseStudents(CounRollcallGroupDTO counRollcallGroupDTO) {
        Set<Long> studentIds = counRollcallGroupDTO.getStudentList();
        if (studentIds == null) {
            studentIds = new HashSet<>();
        }
        studentIds.addAll(userInfoService.findUserIds(counRollcallGroupDTO.getOrgId(), counRollcallGroupDTO.getCollegeIds(), counRollcallGroupDTO.getProIds(),
                counRollcallGroupDTO.getClassIds(), counRollcallGroupDTO.getTeachingClassIds()));
        return studentIds;
    }

    /**
     * 凌晨关闭导员点名
     */
    public void closeCounsellorRollcall() {

    }
}
