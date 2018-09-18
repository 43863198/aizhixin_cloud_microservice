package com.aizhixin.cloud.dd.counsellorollcall.v2.service;

import com.aizhixin.cloud.dd.common.core.ApiReturn;
import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.common.provider.store.redis.RedisTokenStore;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.counsellorollcall.domain.*;
import com.aizhixin.cloud.dd.counsellorollcall.dto.CounRollcallGroupDTOV2;
import com.aizhixin.cloud.dd.counsellorollcall.dto.CounRollcallGroupPracticeDTO;
import com.aizhixin.cloud.dd.counsellorollcall.dto.CounRollcallRuleDTO;
import com.aizhixin.cloud.dd.counsellorollcall.entity.*;
import com.aizhixin.cloud.dd.counsellorollcall.repository.*;
import com.aizhixin.cloud.dd.counsellorollcall.utils.CounsellorRollCallEnum;
import com.aizhixin.cloud.dd.counsellorollcall.utils.CounsellorRollCallType;
import com.aizhixin.cloud.dd.counsellorollcall.v1.service.AlarmClockService;
import com.aizhixin.cloud.dd.counsellorollcall.v1.service.CounsellorRedisService;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;
import com.aizhixin.cloud.dd.orgStructure.service.UserInfoService;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.ClassesService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class CounselorRollcallTeacherService {
    private final Logger LOG = LoggerFactory.getLogger(CounselorRollcallTeacherService.class);
    @Autowired
    private CounsellorRollcallRuleRepository ruleRepository;
    @Autowired
    private TempGroupRepository groupRepository;
    @Autowired
    private StudentSubGroupRepository studentSubGroupRepository;
    @Autowired
    private AlarmClockRepository clockRepository;
    @Autowired
    private AlarmClockService clockService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteClient;
    @Autowired
    private ClassesService classesService;
    @Autowired
    private CounsellorRedisService counsellorRedisService;
    @Autowired
    private CounsellorRollcallRepository rollcallRepository;
    @Autowired
    private CounsellorRollcallRuleTempRepository ruleTempRepository;
    @Autowired
    private CounselorRollcallStudentService studentService;
    @Autowired
    private RedisTokenStore redisTokenStore;
    @Autowired
    private StudentSignInRepository studentSignInRepository;
    @Autowired
    private CounsellorRollcallQuery rollcallQuery;
    @Autowired
    private TempGroupRuleTempRepository tempGroupRuleTempRepository;

    //10:迟到 20:开始缺卡 30:结束缺卡 40:请假 50:已到 60:两次都缺卡
    public Map<String, Object> getDailyStatistics(Long groupId, Integer status, String date) {
        List<DailyStatisticsStuDomain> list = rollcallQuery.findDailyStatistics(groupId, date, status);
        Map<String, List<DailyStatisticsStuDomain>> map = new HashMap<>();
        List<StuTempGroupDomainV2> groups = rollcallQuery.findGroupById(groupId);
        StuTempGroupDomainV2 group = groups.get(0);
        if (list != null && list.size() > 0) {
            for (DailyStatisticsStuDomain item : list) {
                UserInfo user = userInfoRepository.findByUserId(item.getStudentId());
                if (user != null) {
                    item.setAvatar(user.getAvatar());
                }
                List<DailyStatisticsStuDomain> subList = map.get(item.getClassName());
                if (subList == null) {
                    subList = new ArrayList<>();
                }
                item.setFirstTime(group.getFirstTime());
                item.setLateTime(group.getLateTime());
                item.setSecondTime(group.getSecondTime());
                item.setEndTime(group.getEndTime());
                subList.add(item);
                map.put(item.getClassName(), subList);
            }
        }
        List<DailyStatisticsDomain> result = new ArrayList<>();
        for (String key : map.keySet()) {
            DailyStatisticsDomain d = new DailyStatisticsDomain();
            d.setClassName(key);
            d.setStudents(map.get(key));
            result.add(d);
        }
        return ApiReturn.message(Boolean.TRUE, null, result);
    }

    public Map<String, Object> getStuRollCall(Long stuId, Long groupId, String date) {
        List<StuTempGroupDomainV2> groups = rollcallQuery.findGroupById(groupId);
        StuTempGroupDomainV2 group = groups.get(0);
        UserInfo user = userInfoRepository.findByUserId(stuId);
        List<DailyStatisticsStuDomain> list = rollcallQuery.findDailyStatisticsStu(stuId, groupId, date);
        if (list != null && list.size() > 0) {
            for (DailyStatisticsStuDomain item : list) {
                if (user != null) {
                    item.setAvatar(user.getAvatar());
                }
                item.setFirstTime(group.getFirstTime());
                item.setLateTime(group.getLateTime());
                item.setSecondTime(group.getSecondTime());
                item.setEndTime(group.getEndTime());
            }
        }
        return ApiReturn.message(Boolean.TRUE, null, list);
    }

    //10:迟到 20:开始缺卡 30:结束缺卡 40:开始请假 50:结束请假 60:开始已到 70:结束已到
    public Map<String, Object> updateRollcallStatus(Long id, Integer status) {
        StudentSignIn signIn = studentSignInRepository.findOne(id);
        if (signIn != null) {
            switch (status.intValue()) {
                case 10:
                    signIn.setStatus(CounsellorRollCallEnum.Late.getType());
                    break;
                case 20:
                    signIn.setStatus(CounsellorRollCallEnum.UnCommit.getType());
                    break;
                case 30:
                    signIn.setStatus2(CounsellorRollCallEnum.UnCommit.getType());
                    break;
                case 40:
                    signIn.setStatus(CounsellorRollCallEnum.AskForLeave.getType());
                    break;
                case 50:
                    signIn.setStatus2(CounsellorRollCallEnum.AskForLeave.getType());
                    break;
                case 60:
                    signIn.setStatus(CounsellorRollCallEnum.HavaTo.getType());
                    break;
                case 70:
                    signIn.setStatus2(CounsellorRollCallEnum.HavaTo.getType());
                    break;
            }
            studentSignInRepository.save(signIn);
            //如果是当天，更新缓存
            String cDate = DateFormatUtil.format(signIn.getCreatedDate(), DateFormatUtil.FORMAT_SHORT);
            String currentDate = DateFormatUtil.format(new Date(), DateFormatUtil.FORMAT_SHORT);
            if (cDate.equals(currentDate)) {
                studentService.updateStuCache(signIn);
                if (signIn.getCounsellorRollcall() != null && signIn.getCounsellorRollcall().getTempGroup() != null) {
                    if (signIn.getCounsellorRollcall().getTempGroup().getRollcallNum() == null || signIn.getCounsellorRollcall().getTempGroup().getRollcallNum().intValue() < 2) {
                        counsellorRedisService.refreshStuCache(signIn.getStudentId(), signIn.getCounsellorRollcall().getId(), signIn.getStatus());
                    }
                }
            }
            return ApiReturn.message(Boolean.TRUE, null, null);
        } else {
            return ApiReturn.message(Boolean.FALSE, "无点名记录", null);
        }
    }

    public Map<String, Object> updateRollcallStatusBatch(Set<Long> ids, Integer status) {
        List<StudentSignIn> signIns = studentSignInRepository.findByIds(ids);
        if (signIns != null && signIns.size() > 0) {
            String value = "";
            if (status.intValue() == 10) {
                value = CounsellorRollCallEnum.HavaTo.getType();
            } else if (status.intValue() == 20) {
                value = CounsellorRollCallEnum.AskForLeave.getType();
            } else if (status.intValue() == 30) {
                value = CounsellorRollCallEnum.UnCommit.getType();
            }
            for (StudentSignIn item : signIns) {
                item.setStatus(value);
                item.setStatus2(value);
            }
            studentSignInRepository.save(signIns);

            //如果是当天，更新缓存
            StudentSignIn signIn = signIns.get(0);
            String cDate = DateFormatUtil.format(signIn.getCreatedDate(), DateFormatUtil.FORMAT_SHORT);
            String currentDate = DateFormatUtil.format(new Date(), DateFormatUtil.FORMAT_SHORT);
            if (cDate.equals(currentDate)) {
                for (StudentSignIn item : signIns) {
                    studentService.updateStuCache(item);
                    if (item.getCounsellorRollcall() != null && item.getCounsellorRollcall().getTempGroup() != null) {
                        if (item.getCounsellorRollcall().getTempGroup().getRollcallNum() == null || item.getCounsellorRollcall().getTempGroup().getRollcallNum().intValue() < 2) {
                            counsellorRedisService.refreshStuCache(item.getStudentId(), item.getCounsellorRollcall().getId(), item.getStatus());
                        }
                    }
                }
            }
            return ApiReturn.message(Boolean.TRUE, null, null);
        } else {
            return ApiReturn.message(Boolean.FALSE, "无点名记录", null);
        }
    }

    public Map<String, Object> getMonthlyStatistics(Long groupId, Integer status, String date) {
        List<MonthlyStatisticsStuDomain> list = rollcallQuery.findMonthlyStatistics(groupId, date, status);
        Map<String, List<MonthlyStatisticsStuDomain>> map = new HashMap<>();
        if (list != null && list.size() > 0) {
            for (MonthlyStatisticsStuDomain item : list) {
                UserInfo user = userInfoRepository.findByUserId(item.getStudentId());
                String className = item.getClassName();
                if (user != null) {
                    item.setAvatar(user.getAvatar());
                    className = user.getClassesName();
                }
                List<MonthlyStatisticsStuDomain> subList = map.get(className);
                if (subList == null) {
                    subList = new ArrayList<>();
                }
                subList.add(item);
                map.put(className, subList);
            }
        }
        List<MonthlyStatisticsDomain> result = new ArrayList();
        Comparator<MonthlyStatisticsStuDomain> comparator = (h1, h2) -> {
            return h2.getCount().compareTo(h1.getCount());
        };
        for (String key : map.keySet()) {
            MonthlyStatisticsDomain d = new MonthlyStatisticsDomain();
            d.setClassName(key);
            d.setStudents(map.get(key));
            d.getStudents().sort(comparator);
            result.add(d);
        }
        return ApiReturn.message(Boolean.TRUE, null, result);
    }

    public Map<String, Object> getMonthlyStatisticsStudentDetails(Long groupId, Long stuId, Integer status, String date) {
        List<MonthlyStatsRecordDomain> list = rollcallQuery.findMonthlyStatisticsStuRecord(groupId, stuId, date, status);
        return ApiReturn.message(Boolean.TRUE, null, list);
    }

    public Map<String, Object> getTempGroup(AccountDTO accountDTO) {
        List<TempGroupDomainV2> tempGroupDomains = groupRepository.findByTeacherIdAndDeleteFlag(accountDTO.getId(), DataValidity.VALID.getState());
        if (tempGroupDomains == null || tempGroupDomains.isEmpty()) {
            List<TempGroupDomain> deleteTempGroupDomains = groupRepository.findAllByTeacherId(accountDTO.getId());
            if (deleteTempGroupDomains == null || deleteTempGroupDomains.isEmpty()) {
                // 第一次使用辅导员点名功能，默认创建（N+1）个点名组
                tempGroupDomains = createDefaultCounsellorGroup(accountDTO);
            }
        } else {
            // 检查是否有新添加的行政班
            tempGroupDomains = checkoutNewClass(tempGroupDomains, accountDTO.getId());
        }
        List<CounsellorRollcallRuleTemp> ruleTemps = ruleTempRepository.findAll();
        Map<Long, CounsellorRollcallRuleTemp> ruleMap = new HashMap<>();
        if (ruleTemps != null && ruleTemps.size() > 0) {
            for (CounsellorRollcallRuleTemp temp : ruleTemps) {
                ruleMap.put(temp.getRuleId(), temp);
            }
        }
        List<TempGroupRuleTemp> groupTemps = tempGroupRuleTempRepository.findAll();
        Map<Long, TempGroupRuleTemp> groupMap = new HashMap<>();
        if (groupTemps != null && groupTemps.size() > 0) {
            for (TempGroupRuleTemp temp : groupTemps) {
                groupMap.put(temp.getGroupId(), temp);
            }
        }
        for (TempGroupDomainV2 tempGroupDomain : tempGroupDomains) {
            TempGroup t = new TempGroup();
            t.setId(tempGroupDomain.getId());
            t.setMessageId(tempGroupDomain.getMessageId());
            AlarmClock a = clockRepository.findByTempGroupAndDeleteFlag(t, DataValidity.VALID.getState());
            if (a != null) {
                tempGroupDomain.setAlarmTime(a.getClockTime());
                tempGroupDomain.setAlarmModel(a.getClockMode());
                tempGroupDomain.setStatus(a.getStatus());
            }
            if (tempGroupDomain.getRollcallNum() != null && tempGroupDomain.getRollcallNum().intValue() > 1 && tempGroupDomain.getRuleId() != null) {
                TempGroupRuleTemp groupTemp = groupMap.get(tempGroupDomain.getId());
                if (groupTemp != null) {
                    tempGroupDomain.setRuleId(groupTemp.getRuleId());
                }
                CounsellorRollcallRule rule = ruleRepository.findOne(tempGroupDomain.getRuleId());
                if (rule == null) {
                    rule = new CounsellorRollcallRule();
                    rule.setId(tempGroupDomain.getRuleId());
                }
                CounsellorRollcallRuleTemp temp = ruleMap.get(rule.getId());
                if (temp != null) {
                    rule.setStartTime(temp.getStartTime());
                    rule.setStartFlexTime(temp.getStartFlexTime());
                    rule.setLateTime(temp.getLateTime());
                    rule.setEndTime(temp.getEndTime());
                    rule.setEndFlexTime(temp.getEndFlexTime());
                    rule.setStopTime(temp.getStopTime());
                    rule.setDays(temp.getDays());
                }
                tempGroupDomain.setAlarmTime(rule.getStartTime() + "--" + rule.getEndTime());
                tempGroupDomain.setRule(rule);
            }
        }
        return ApiReturn.message(Boolean.TRUE, null, tempGroupDomains);
    }

    public Map<String, Object> getTempGroupByPractice(Long practiceId) {
        List<TempGroupDomainV2> tempGroupDomains = groupRepository.findByPracticeIdAndDeleteFlag2(practiceId, DataValidity.VALID.getState());
        List<CounsellorRollcallRuleTemp> ruleTemps = ruleTempRepository.findAll();
        Map<Long, CounsellorRollcallRuleTemp> ruleMap = new HashMap<>();
        if (ruleTemps != null && ruleTemps.size() > 0) {
            for (CounsellorRollcallRuleTemp temp : ruleTemps) {
                ruleMap.put(temp.getRuleId(), temp);
            }
        }
        List<TempGroupRuleTemp> groupTemps = tempGroupRuleTempRepository.findAll();
        Map<Long, TempGroupRuleTemp> groupMap = new HashMap<>();
        if (groupTemps != null && groupTemps.size() > 0) {
            for (TempGroupRuleTemp temp : groupTemps) {
                groupMap.put(temp.getGroupId(), temp);
            }
        }
        for (TempGroupDomainV2 tempGroupDomain : tempGroupDomains) {
            TempGroup t = new TempGroup();
            t.setId(tempGroupDomain.getId());
            t.setMessageId(tempGroupDomain.getMessageId());
            AlarmClock a = clockRepository.findByTempGroupAndDeleteFlag(t, DataValidity.VALID.getState());
            if (a != null) {
                tempGroupDomain.setAlarmTime(a.getClockTime());
                tempGroupDomain.setAlarmModel(a.getClockMode());
                tempGroupDomain.setStatus(a.getStatus());
            }
            if (tempGroupDomain.getRollcallNum() != null && tempGroupDomain.getRollcallNum().intValue() > 1 && tempGroupDomain.getRuleId() != null) {
                TempGroupRuleTemp groupTemp = groupMap.get(tempGroupDomain.getId());
                if (groupTemp != null) {
                    tempGroupDomain.setRuleId(groupTemp.getRuleId());
                }
                CounsellorRollcallRule rule = ruleRepository.findOne(tempGroupDomain.getRuleId());
                if (rule == null) {
                    rule = new CounsellorRollcallRule();
                    rule.setId(tempGroupDomain.getRuleId());
                }
                CounsellorRollcallRuleTemp temp = ruleMap.get(rule.getId());
                if (temp != null) {
                    rule.setStartTime(temp.getStartTime());
                    rule.setStartFlexTime(temp.getStartFlexTime());
                    rule.setLateTime(temp.getLateTime());
                    rule.setEndTime(temp.getEndTime());
                    rule.setEndFlexTime(temp.getEndFlexTime());
                    rule.setStopTime(temp.getStopTime());
                    rule.setDays(temp.getDays());
                }
                tempGroupDomain.setAlarmTime(rule.getStartTime() + "--" + rule.getEndTime());
                tempGroupDomain.setRule(rule);
            }
        }
        return ApiReturn.message(Boolean.TRUE, null, tempGroupDomains);
    }

    private List<TempGroupDomainV2> checkoutNewClass(List<TempGroupDomainV2> tempGroupDomains, Long teacherId) {
        if (tempGroupDomains == null) {
            return null;
        }
        for (TempGroupDomainV2 tempGroupDomain : tempGroupDomains) {
            if (null == tempGroupDomain) {
                continue;
            }
            if ("所有行政班".equals(tempGroupDomain.getName())) {
                // 查询
                List<IdNameDomain> classesInfo = orgManagerRemoteClient.getClassesByTeacher(teacherId);
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
                        TempGroup tempGroup = groupRepository.findOne(tempGroupDomain.getId());
                        tempGroup.setSubGroupNum(allStudentIds.size());
                        groupRepository.save(tempGroup);
                        updateSubStudent(allStudentIds, tempGroup);
                        tempGroupDomains = groupRepository.findByTeacherIdAndDeleteFlag(teacherId, DataValidity.VALID.getState());
                    }
                }
            }
        }
        return tempGroupDomains;
    }

    private List<TempGroupDomainV2> createDefaultCounsellorGroup(AccountDTO accountDTO) {
        List<TempGroupDomainV2> tempGroupDomains = null;
        List<IdNameDomain> classesInfo = orgManagerRemoteClient.getClassesByTeacher(accountDTO.getId());
        if (classesInfo != null && !classesInfo.isEmpty()) {
            // 创建默认分组
            Set<Long> allStudentIds = new HashSet<>();
            for (IdNameDomain idNameDomain : classesInfo) {
                Set<Long> studentIds = classesService.getStudentIdsNotIncludeException(idNameDomain.getId());
                if (studentIds == null || studentIds.isEmpty()) {
                    continue;
                }
                allStudentIds.addAll(studentIds);
                saveTempGroup(accountDTO, new CounRollcallGroupDTOV2(idNameDomain.getName(), studentIds));
            }
            // 创建所有行政班集合分组
            saveTempGroup(accountDTO, new CounRollcallGroupDTOV2("所有行政班", allStudentIds));
            tempGroupDomains = groupRepository.findByTeacherIdAndDeleteFlag(accountDTO.getId(), DataValidity.VALID.getState());
        } else {
            tempGroupDomains = new ArrayList<>();
        }
        return tempGroupDomains;
    }

    public Map<String, Object> saveTempGroup(AccountDTO accountDTO, CounRollcallGroupDTOV2 dto) {
        Set<Long> studentList = getChooseStudents(dto);
        if (studentList == null || studentList.isEmpty()) {
            return ApiReturn.message(Boolean.FALSE, "点名组必须包含学生!", null);
        }
        Map result = checkCounRollcallGroup(accountDTO.getId(), dto);
        if (null != result) {
            return result;
        }
        try {
            TempGroup tempGroup = new TempGroup(dto.getTempGroupName(), accountDTO.getId(), accountDTO.getName(), studentList.size(), Boolean.FALSE, accountDTO.getOrganId(), UUID.randomUUID().toString());
            tempGroup.setRuleId(dto.getRuleId());
            tempGroup.setPracticeId(dto.getPracticeId());
            if (dto.getRollcallNum() != null && dto.getRollcallNum().intValue() > 0) {
                tempGroup.setRollcallNum(dto.getRollcallNum());
            } else {
                tempGroup.setRollcallNum(1);
            }
            if (dto.getRollcallType() != null && dto.getRollcallType().intValue() > 0) {
                tempGroup.setRollcallType(dto.getRollcallType());
            } else {
                tempGroup.setRollcallType(CounsellorRollCallType.Other.getType());
            }
            tempGroup = groupRepository.save(tempGroup);
            // 保存学生信息
            List<StudentSubGroup> studentSubGroupList = new ArrayList<>();
            for (Long studentId : studentList) {
                studentSubGroupList.add(new StudentSubGroup(tempGroup, studentId));
            }
            studentSubGroupRepository.save(studentSubGroupList);
            // 保存定时点名信息
            saveOrUpdateAlarmClock(tempGroup, dto);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.warn("开启点名失败:" + e.toString(), e.getMessage());
            return ApiReturn.message(Boolean.FALSE, "保存异常,联系管理员", null);
        }
        return ApiReturn.message(Boolean.TRUE, null, null);
    }

    public Map<String, Object> updateTempGroup(AccountDTO accountDTO, CounRollcallGroupDTOV2 dto) {
        LOG.warn("==================");
        LOG.warn(dto.toString());
        Set<Long> studentList = getChooseStudents(dto);
        Map result = checkCounRollcallGroup(accountDTO.getId(), dto);
        if (null != result) {
            return result;
        }
        result = new HashMap();
        TempGroup tempGroup = groupRepository.findOne(dto.getTempGroupId());
        if (null == tempGroup) {
            throw new NullPointerException();
        }
        tempGroup.setName(dto.getTempGroupName());
        if (!studentList.isEmpty()) {
            tempGroup.setSubGroupNum(studentList.size());
        }
        if (dto.getRollcallType() != null && dto.getRollcallType() > 0) {
            tempGroup.setRollcallType(dto.getRollcallType());
        } else {
            tempGroup.setRollcallType(CounsellorRollCallType.Other.getType());
        }
//        tempGroup.setPracticeId(dto.getPracticeId());
        boolean isRuleChange = false;
        if (tempGroup.getRollcallNum() != null && tempGroup.getRollcallNum().intValue() > 1 && tempGroup.getRuleId().longValue() != dto.getRuleId().longValue()) {
            List<TempGroup> groups = new ArrayList<>();
            groups.add(tempGroup);
            Date startDate = new Date();
            startDate.setHours(0);
            startDate.setMinutes(0);
            startDate.setSeconds(0);
            Date endDate = new Date();
            endDate.setHours(23);
            endDate.setMinutes(59);
            endDate.setSeconds(59);
            List<Long> rollcallList = rollcallRepository.findByTempGroupAndOpenTime(groups, startDate, endDate);
            if (rollcallList != null && rollcallList.size() > 0) {
                //添加到队列中，第二天生效
                isRuleChange = true;
                TempGroupRuleTemp ruleTemp = null;
                try {
                    ruleTemp = tempGroupRuleTempRepository.findByGroupId(tempGroup.getId());
                } catch (Exception e) {

                }
                if (ruleTemp == null) {
                    ruleTemp = new TempGroupRuleTemp();
                }
                ruleTemp.setGroupId(tempGroup.getId());
                ruleTemp.setRuleId(dto.getRuleId());
                tempGroupRuleTempRepository.save(ruleTemp);
            }
        }
        if (!isRuleChange) {
            tempGroup.setRuleId(dto.getRuleId());
            groupRepository.save(tempGroup);
            saveOrUpdateAlarmClock(tempGroup, dto);
        }
        updateSubStudent(studentList, tempGroup);
        result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        result.put("isAlert", isRuleChange);
        return result;
    }

    //更新学生
    public Map<String, Object> updateTempGroupByPracticeId(CounRollcallGroupPracticeDTO dto) {
        Map result = new HashMap();
        List<TempGroup> groups = groupRepository.findByPracticeIdAndDeleteFlag(dto.getPracticeId(), DataValidity.VALID.getState());
        if (groups != null && groups.size() > 0) {
            Set<Long> studentList = getChooseStudents(dto);
            for (TempGroup tempGroup : groups) {
                updateSubStudent(studentList, tempGroup);
            }
            result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        } else {
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            result.put(ApiReturnConstants.MESSAGE, "没有点名组数据");
        }
        return result;
    }

    public void updateSubStudent(Set<Long> studentList, TempGroup tempGroup) {
        if (!studentList.isEmpty()) {
            List<StudentSubGroup> studentSubGroups = studentSubGroupRepository.findAllByTempGroupAndDeleteFlag(tempGroup, DataValidity.VALID.getState());
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
            studentSubGroupRepository.save(addList);
        }
    }

    /**
     * 定时任务保存
     */
    private Map<String, Object> saveOrUpdateAlarmClock(TempGroup tempGroup, CounRollcallGroupDTOV2 dto) {
        if (null == tempGroup) {
            return ApiReturn.message(Boolean.FALSE, "tmepGroup is not found", null);
        }
        String alarmTime;
        String alarmModel;
        Boolean alarmOnOff;
        String lateTime = null;
        String secondTime = null;
        String endTime = null;
        String startEndTime = null;
        if (tempGroup.getRollcallNum() != null && tempGroup.getRollcallNum().intValue() > 1) {
            CounsellorRollcallRule rule = ruleRepository.findOne(dto.getRuleId());
            if (rule != null) {
                int sft = 0;
                if (rule.getStartFlexTime() != null && rule.getStartFlexTime().intValue() > 0) {
                    sft = rule.getStartFlexTime().intValue() * 60 * 1000;
                }
                Date startTime = getTime(rule.getStartTime());
                startTime = new Date(startTime.getTime() - sft);
                alarmTime = formatTime(startTime);

                int lft = 0;
                if (rule.getLateTime() != null && rule.getLateTime().intValue() > 0) {
                    lft = rule.getLateTime().intValue() * 60 * 1000;
                }
                Date lTime = getTime(rule.getStartTime());
                lTime = new Date(lTime.getTime() + lft);
                lateTime = formatTime(lTime);

                int eft = 0;
                if (rule.getEndFlexTime() != null && rule.getEndFlexTime().intValue() > 0) {
                    eft = rule.getEndFlexTime().intValue() * 60 * 1000;
                }
                Date eTime = getTime(rule.getEndTime());
                Date sTime = new Date(eTime.getTime() - eft);
                secondTime = formatTime(sTime);

                eft = 0;
                if (rule.getStopTime() != null && rule.getStopTime().intValue() > 0) {
                    eft = rule.getStopTime().intValue() * 60 * 1000;
                }
                sTime = new Date(eTime.getTime() + eft);
                endTime = formatTime(sTime);
                startEndTime = rule.getStartTime() + "--" + rule.getEndTime();
                alarmModel = rule.getDays();

                LOG.warn("==================1");
                LOG.warn(alarmTime);
            } else {
                return ApiReturn.message(Boolean.FALSE, "rule is not found", null);
            }
        } else {
            alarmTime = dto.getAlarmTime();
            alarmModel = dto.getAlarmModel();
            LOG.warn("==================2");
            LOG.warn(alarmTime);
        }
        alarmOnOff = dto.getAlarmOnOff();
        if (StringUtils.isBlank(alarmTime) || StringUtils.isBlank(alarmModel)) {
            return ApiReturn.message(Boolean.FALSE, "请先设置定时任务信息。", null);
        }
        AlarmClock alarmClock = clockRepository.findByTempGroupAndDeleteFlag(tempGroup, DataValidity.VALID.getState());
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
        alarmClock.setLateTime(lateTime);
        alarmClock.setSecondTime(secondTime);
        alarmClock.setEndTime(endTime);
        alarmClock.setStartEndTime(startEndTime);
        LOG.warn("==================3");
        LOG.warn(alarmClock.toString());
        clockRepository.save(alarmClock);
        try {
            Date now = new Date();
            Integer weekOfDate = DateFormatUtil.getWeekOfDate(DateFormatUtil.formatShort(now));
            if (-1 == alarmClock.getClockMode().indexOf(String.valueOf(weekOfDate))) {
                return ApiReturn.message(Boolean.TRUE, null, null);
            }

            String str = DateFormatUtil.formatShort(now) + " " + alarmClock.getClockTime();
            Date doTime = DateFormatUtil.parse(str, DateFormatUtil.FORMAT_MINUTE);
            if (tempGroup.getRollcallNum().intValue() > 1 && StringUtils.isNotEmpty(endTime)) {
                String stopStr = DateFormatUtil.formatShort(now) + " " + endTime;
                Date stoTime = DateFormatUtil.parse(stopStr, DateFormatUtil.FORMAT_MINUTE);
                clockService.putAlarmMap(doTime.getTime(), stoTime.getTime(), tempGroup.getId());
            } else {
                clockService.putAlarmMap(doTime.getTime(), tempGroup.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOG.warn("添加定时任务异常,组id:()", tempGroup.getId(), e.getMessage());
        }
        return ApiReturn.message(Boolean.TRUE, null, null);
    }

    /**
     * 定时任务保存
     */
    private Map<String, Object> updateAlarmClock(TempGroup tempGroup, CounsellorRollcallRule rule) {
        int sft = 0;
        if (rule.getStartFlexTime() != null && rule.getStartFlexTime().intValue() > 0) {
            sft = rule.getStartFlexTime().intValue() * 60 * 1000;
        }
        Date startTime = getTime(rule.getStartTime());
        startTime = new Date(startTime.getTime() - sft);
        String alarmTime = formatTime(startTime);

        int lft = 0;
        if (rule.getLateTime() != null && rule.getLateTime().intValue() > 0) {
            lft = rule.getLateTime().intValue() * 60 * 1000;
        }
        startTime = getTime(rule.getStartTime());
        Date lTime = new Date(startTime.getTime() + lft);
        String lateTime = formatTime(lTime);

        int eft = 0;
        if (rule.getEndFlexTime() != null && rule.getEndFlexTime().intValue() > 0) {
            eft = rule.getEndFlexTime().intValue() * 60 * 1000;
        }
        Date eTime = getTime(rule.getEndTime());
        Date sTime = new Date(eTime.getTime() - eft);
        String secondTime = formatTime(sTime);

        eft = 0;
        if (rule.getStopTime() != null && rule.getStopTime().intValue() > 0) {
            eft = rule.getStopTime().intValue() * 60 * 1000;
        }
        sTime = new Date(eTime.getTime() + eft);
        String endTime = formatTime(sTime);
        String startEndTime = rule.getStartTime() + "--" + rule.getEndTime();
        String alarmModel = rule.getDays();

        AlarmClock alarmClock = clockRepository.findByTempGroupAndDeleteFlag(tempGroup, DataValidity.VALID.getState());
        if(alarmClock == null){
            alarmClock = new AlarmClock();
            alarmClock.setTempGroup(tempGroup);
        }
        alarmClock.setClockTime(alarmTime);
        alarmClock.setClockMode(alarmModel);
        alarmClock.setLateTime(lateTime);
        alarmClock.setSecondTime(secondTime);
        alarmClock.setEndTime(endTime);
        alarmClock.setStartEndTime(startEndTime);
        clockRepository.save(alarmClock);
        try {
            Date now = new Date();
            Integer weekOfDate = DateFormatUtil.getWeekOfDate(DateFormatUtil.formatShort(now));
            if (-1 == alarmClock.getClockMode().indexOf(String.valueOf(weekOfDate))) {
                return ApiReturn.message(Boolean.TRUE, null, null);
            }

            String str = DateFormatUtil.formatShort(now) + " " + alarmClock.getClockTime();
            Date doTime = DateFormatUtil.parse(str, DateFormatUtil.FORMAT_MINUTE);
            clockService.putAlarmMap(doTime.getTime(), tempGroup.getId());
        } catch (Exception e) {
            e.printStackTrace();
            LOG.warn("添加定时任务异常,组id:()", tempGroup.getId(), e.getMessage());
        }
        return ApiReturn.message(Boolean.TRUE, null, null);
    }

    private Date getTime(String timeStr) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        try {
            return format.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String formatTime(Date time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(time);
    }

    private Set<Long> getChooseStudents(CounRollcallGroupDTOV2 counRollcallGroupDTO) {
        Set<Long> studentIds = counRollcallGroupDTO.getStudentList();
        if (studentIds == null) {
            studentIds = new HashSet<>();
        }
        studentIds.addAll(userInfoService.findUserIds(counRollcallGroupDTO.getOrgId(), counRollcallGroupDTO.getCollegeIds(), counRollcallGroupDTO.getProIds(),
                counRollcallGroupDTO.getClassIds(), counRollcallGroupDTO.getTeachingClassIds()));
        return studentIds;
    }

    private Set<Long> getChooseStudents(CounRollcallGroupPracticeDTO counRollcallGroupDTO) {
        Set<Long> studentIds = counRollcallGroupDTO.getStudentList();
        if (studentIds == null) {
            studentIds = new HashSet<>();
        }
        studentIds.addAll(userInfoService.findUserIds(counRollcallGroupDTO.getOrgId(), counRollcallGroupDTO.getCollegeIds(), counRollcallGroupDTO.getProIds(),
                counRollcallGroupDTO.getClassIds(), counRollcallGroupDTO.getTeachingClassIds()));
        return studentIds;
    }

    private Map<String, Object> checkCounRollcallGroup(Long teacherId, CounRollcallGroupDTOV2 counRollcallGroupDTO) {
        if (verufucationName(counRollcallGroupDTO.getTempGroupId(), teacherId, counRollcallGroupDTO.getTempGroupName())) {
            return ApiReturn.message(Boolean.FALSE, "组名称重复!", null);
        }
        return null;
    }

    private boolean verufucationName(Long id, Long teacherId, String name) {
        List<TempGroup> tempGroups = groupRepository.findAllByTeacherIdAndNameAndDeleteFlag(teacherId, name, DataValidity.VALID.getState());
        if (tempGroups != null && tempGroups.size() > 0) {
            if (id != null && id.longValue() > 0) {
                TempGroup group = tempGroups.get(0);
                if (group != null && group.getId().longValue() == id.longValue()) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }


    public Map<String, Object> delTempGroup(Long userId, Set<Long> groupIds) {
        Map<String, Object> result = new HashMap<>();
        if (groupIds != null) {
            List<TempGroup> groups = new ArrayList<>();
            for (Long id : groupIds) {
                TempGroup group = groupRepository.findOne(id);
                if (group != null && group.getDeleteFlag().equals(DataValidity.VALID.getState())) {
                    group.setDeleteFlag(DataValidity.INVALID.getState());
                    groupRepository.save(group);
                    AlarmClock clock = clockRepository.findByTempGroupAndDeleteFlag(group, DataValidity.VALID.getState());
                    if (clock != null) {
                        clock.setDeleteFlag(DataValidity.INVALID.getState());
                        clockRepository.save(clock);
                    }
                    groups.add(group);
                }
            }
            if (groups.size() > 0) {
                counsellorRedisService.delCacheByDelTempGroupV2(groups);
            }
            result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        } else {
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            result.put(ApiReturnConstants.MESSAGE, "无点名组信息");
        }
        return result;
    }

    public Map<String, Object> delTempGroupByPracticeId(Long practiceId) {
        Map<String, Object> result = new HashMap<>();
        List<TempGroup> groups = groupRepository.findByPracticeIdAndDeleteFlag(practiceId, DataValidity.VALID.getState());
        if (groups != null && groups.size() > 0) {
            for (TempGroup group : groups) {
                group.setDeleteFlag(DataValidity.INVALID.getState());
                AlarmClock clock = clockRepository.findByTempGroupAndDeleteFlag(group, DataValidity.VALID.getState());
                if (clock != null) {
                    clock.setDeleteFlag(DataValidity.INVALID.getState());
                    clockRepository.save(clock);
                }
            }
            groupRepository.save(groups);
            counsellorRedisService.delCacheByDelTempGroupV2(groups);
            result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        } else {
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            result.put(ApiReturnConstants.MESSAGE, "无点名组信息");
        }
        return result;
    }

    public List<CounsellorRollcallRule> getRuleList(Long userId) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        List<CounsellorRollcallRule> list = ruleRepository.findByUserIdAndDeleteFlag(userId, DataValidity.VALID.getState(), sort);
        List<CounsellorRollcallRuleTemp> temps = ruleTempRepository.findAll();
        if (temps != null && temps.size() > 0) {
            Map<Long, CounsellorRollcallRuleTemp> map = new HashMap<>();
            for (CounsellorRollcallRuleTemp temp : temps) {
                map.put(temp.getRuleId(), temp);
            }
            for (CounsellorRollcallRule item : list) {
                CounsellorRollcallRuleTemp temp = map.get(item.getId());
                if (temp != null) {
                    item.setStartTime(temp.getStartTime());
                    item.setStartFlexTime(temp.getStartFlexTime());
                    item.setLateTime(temp.getLateTime());
                    item.setEndTime(temp.getEndTime());
                    item.setEndFlexTime(temp.getEndFlexTime());
                    item.setStopTime(temp.getStopTime());
                    item.setDays(temp.getDays());
                }
            }
        }
        return list;
    }

    public Map<String, Object> addRule(Long userId, CounRollcallRuleDTO ruleDTO) {
        Map<String, Object> result = new HashMap<>();
        CounsellorRollcallRule rule = typeCounsellorRollcallRule(userId, ruleDTO);
        rule = ruleRepository.save(rule);
        result.put(ApiReturnConstants.DATA, rule);
        result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        return result;
    }

    public Map<String, Object> updateRule(Long userId, CounRollcallRuleDTO ruleDTO) {
        Map<String, Object> result = new HashMap<>();
        CounsellorRollcallRule rule = ruleRepository.findOne(ruleDTO.getId());
        if (rule != null) {
            //检查是否开启
            Date startDate = new Date();
            startDate.setHours(0);
            startDate.setMinutes(0);
            startDate.setSeconds(0);
            Date endDate = new Date();
            endDate.setHours(23);
            endDate.setMinutes(59);
            endDate.setSeconds(59);
            List<TempGroup> groups = groupRepository.findByRuleIdAndDeleteFlag(rule.getId(), DataValidity.VALID.getState());
            List<Long> rollcallList = null;
            if (groups != null && groups.size() > 0) {
                rollcallList = rollcallRepository.findByTempGroupAndOpenTime(groups, startDate, endDate);
            }
            if (rollcallList != null && rollcallList.size() > 0) {
                //添加到队列中，第二天生效
                CounsellorRollcallRuleTemp temp = null;
                try {
                    temp = ruleTempRepository.findByRuleId(rule.getId());
                } catch (Exception e) {

                }
                if (temp == null) {
                    temp = new CounsellorRollcallRuleTemp();
                }
                temp.setRuleId(rule.getId());
                temp.setUserId(userId);
                temp.setStartTime(ruleDTO.getStartTime());
                temp.setStartFlexTime(ruleDTO.getStartFlexTime());
                temp.setLateTime(ruleDTO.getLateTime());
                temp.setEndTime(ruleDTO.getEndTime());
                temp.setEndFlexTime(ruleDTO.getEndFlexTime());
                temp.setStopTime(ruleDTO.getStopTime());
                temp.setDays(ruleDTO.getDays());
                ruleTempRepository.save(temp);
                rule = typeCounsellorRollcallRule(userId, ruleDTO);
                rule.setId(temp.getRuleId());
                result.put(ApiReturnConstants.DATA, rule);
                result.put("isAlert", Boolean.TRUE);
            } else {
                rule = typeCounsellorRollcallRule(userId, ruleDTO);
                rule.setId(ruleDTO.getId());
                ruleRepository.save(rule);
                List<TempGroup> list = groupRepository.findByRuleIdAndDeleteFlag(rule.getId(), DataValidity.VALID.getState());
                if (list != null && list.size() > 0) {
                    for (TempGroup item : list) {
                        counsellorRedisService.putRollcallRule(item.getId(), rule);
                        updateAlarmClock(item, rule);
                    }
                }
                result.put(ApiReturnConstants.DATA, rule);
                result.put("isAlert", Boolean.FALSE);
            }
            result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        } else {
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            result.put(ApiReturnConstants.MESSAGE, "无规则信息");
        }
        return result;
    }

    public void applyCounsellorRollcallRule() {
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        List<CounsellorRollcallRuleTemp> temps = ruleTempRepository.findAll(sort);
        if (temps != null && temps.size() > 0) {
            for (CounsellorRollcallRuleTemp temp : temps) {
                CounsellorRollcallRule rule = ruleRepository.findOne(temp.getRuleId());
                if (rule != null) {
                    rule.setUserId(temp.getUserId());
                    rule.setStartTime(temp.getStartTime());
                    rule.setStartFlexTime(temp.getStartFlexTime());
                    rule.setLateTime(temp.getLateTime());
                    rule.setEndTime(temp.getEndTime());
                    rule.setEndFlexTime(temp.getEndFlexTime());
                    rule.setStopTime(temp.getStopTime());
                    rule.setDays(temp.getDays());
                    ruleRepository.save(rule);
                    List<TempGroup> list = groupRepository.findByRuleIdAndDeleteFlag(rule.getId(), DataValidity.VALID.getState());
                    if (list != null && list.size() > 0) {
                        for (TempGroup item : list) {
                            counsellorRedisService.putRollcallRule(item.getId(), rule);
                            updateAlarmClock(item, rule);
                        }
                    }
                }
                ruleTempRepository.delete(temp);
            }
        }
        List<TempGroupRuleTemp> ruleTemps = tempGroupRuleTempRepository.findAll(sort);
        if (ruleTemps != null && ruleTemps.size() > 0) {
            for (TempGroupRuleTemp item : ruleTemps) {
                TempGroup group = groupRepository.findOne(item.getGroupId());
                if (group != null) {
                    group.setRuleId(item.getRuleId());
                    groupRepository.save(group);
                    CounsellorRollcallRule rule = ruleRepository.findOne(group.getRuleId());
                    counsellorRedisService.putRollcallRule(group.getId(), rule);
                    updateAlarmClock(group, rule);
                }
                tempGroupRuleTempRepository.delete(item);
            }
        }
    }

    private CounsellorRollcallRule typeCounsellorRollcallRule(Long userId, CounRollcallRuleDTO ruleDTO) {
        CounsellorRollcallRule rule = new CounsellorRollcallRule();
        rule.setUserId(userId);
        rule.setStartTime(ruleDTO.getStartTime());
        rule.setStartFlexTime(ruleDTO.getStartFlexTime());
        rule.setLateTime(ruleDTO.getLateTime());
        rule.setEndTime(ruleDTO.getEndTime());
        rule.setEndFlexTime(ruleDTO.getEndFlexTime());
        rule.setStopTime(ruleDTO.getStopTime());
        rule.setDays(ruleDTO.getDays());
        rule.setDeleteFlag(DataValidity.VALID.getState());
        return rule;
    }

    public Map<String, Object> delRule(Long userId, Set<Long> ruleIds) {
        Map<String, Object> result = new HashMap<>();
        if (ruleIds != null && ruleIds.size() > 0) {
            for (Long ruleId : ruleIds) {
                CounsellorRollcallRule rule = ruleRepository.findByIdAndUserIdAndDeleteFlag(ruleId, userId, DataValidity.VALID.getState());
                if (rule != null) {
                    rule.setDeleteFlag(DataValidity.INVALID.getState());
                    ruleRepository.save(rule);
                }
            }
            result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        } else {
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            result.put(ApiReturnConstants.MESSAGE, "无规则信息");
        }
        return result;
    }
}
