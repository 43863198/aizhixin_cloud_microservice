package com.aizhixin.cloud.dd.counsellorollcall.v2.service;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.counsellorollcall.domain.StuRollcallReportDomainV2;
import com.aizhixin.cloud.dd.counsellorollcall.domain.StuTempGroupDomainV2;
import com.aizhixin.cloud.dd.counsellorollcall.entity.AlarmClock;
import com.aizhixin.cloud.dd.counsellorollcall.entity.CounsellorRollcall;
import com.aizhixin.cloud.dd.counsellorollcall.entity.StudentSignIn;
import com.aizhixin.cloud.dd.counsellorollcall.repository.CounsellorRollcallQuery;
import com.aizhixin.cloud.dd.counsellorollcall.repository.CounsellorRollcallRepository;
import com.aizhixin.cloud.dd.counsellorollcall.repository.StudentSignInRepository;
import com.aizhixin.cloud.dd.counsellorollcall.repository.TempGroupRepository;
import com.aizhixin.cloud.dd.counsellorollcall.thread.UpdateRollcallMessageThread;
import com.aizhixin.cloud.dd.counsellorollcall.utils.CounsellorRollCallType;
import com.aizhixin.cloud.dd.counsellorollcall.v1.service.CounsellorRedisService;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class CounselorRollcallStudentService {
    @Autowired
    private CounsellorRedisService counsellorRedisService;
    @Autowired
    private StudentSignInRepository studentSignInRepository;
    @Autowired
    private TempGroupRepository groupRepository;
    @Autowired
    private CounsellorRollcallRepository rollcallRepository;
    @Autowired
    private CounsellorRollcallQuery rollcallQuery;
    @Autowired
    private UserInfoRepository userInfoRepository;

    //redis
    public List<StuTempGroupDomainV2> getRollcallGroupList(AccountDTO accountDTO) {
        List<StuTempGroupDomainV2> rollcallReportDomains = counsellorRedisService.getRollcallReportsByStuIdV2(accountDTO.getId());
        if (rollcallReportDomains == null || rollcallReportDomains.isEmpty()) {
            //没有数据，读取数据库
            rollcallReportDomains = listStudentSignIn(accountDTO.getId(), true);
            if (rollcallReportDomains != null && rollcallReportDomains.size() > 0) {
                counsellorRedisService.pushCacheV2(accountDTO.getId(), rollcallReportDomains);
            }
        }
        //更新1次签到缓存
        counsellorRedisService.getRollcallReportsByStuId(accountDTO.getId());
        return rollcallReportDomains;
    }

    //redis
    public List<StuTempGroupDomainV2> getRollcallGroupListByPracticeId(AccountDTO accountDTO, Long practiceId) {
        List<StuTempGroupDomainV2> rollcallReportDomains = getRollcallGroupList(accountDTO);
        List<StuTempGroupDomainV2> result = new ArrayList<>();
        if (rollcallReportDomains != null && rollcallReportDomains.size() > 0) {
            for(StuTempGroupDomainV2 item: rollcallReportDomains){
                if(item.getPracticeId() != null && item.getPracticeId().intValue() == practiceId.intValue()){
                    result.add(item);
                }
            }
        }
        return result;
    }

    public List<StuRollcallReportDomainV2> getRollcall(Long stuId, Long groupId, String date) {
        List<StuTempGroupDomainV2> groups = rollcallQuery.findGroupById(groupId);
        if(groups != null && groups.size()>0){
            StuTempGroupDomainV2 group = groups.get(0);
            if (StringUtils.isEmpty(date)) {
                date = DateFormatUtil.format(new Date(), DateFormatUtil.FORMAT_SHORT);
            }
            Date startDate = getTime(date + " 00:00:00");
            Date endDate = getTime(date + " 23:59:59");
            List<StuRollcallReportDomainV2> domain = studentSignInRepository.findByStudentIdAndGroupIdAndDateAndDeleteFlag(stuId, groupId, startDate, endDate, DataValidity.VALID.getState());
            if (domain != null && domain.size() > 0) {
                for (StuRollcallReportDomainV2 item : domain) {
                    item.setFirstTime(group.getFirstTime());
                    item.setLateTime(group.getLateTime());
                    item.setSecondTime(group.getSecondTime());
                    item.setEndTime(group.getEndTime());
                }
            }
            return domain;
        }
        return null;
    }

    public void refreshStuCache(Long stuId) {
        List<StuTempGroupDomainV2> rollcallReportDomains = listStudentSignIn(stuId, false);
        counsellorRedisService.pushCacheV2(stuId, rollcallReportDomains);
        UpdateRollcallMessageThread.addCounsellersNotCount(rollcallReportDomains);
    }

    public StuTempGroupDomainV2 refreshStuCache(CounsellorRollcall rollcall, StudentSignIn signIn, boolean isV2, AlarmClock alarmClock) {
        StuTempGroupDomainV2 result = null;
        List<StuTempGroupDomainV2> list = counsellorRedisService.getRollcallByStuIdV2(signIn.getStudentId());
        boolean isUpdate = false;
        if (list != null && list.size() > 0) {
            for (StuTempGroupDomainV2 item : list) {
                if (item.getGroupId().longValue() == signIn.getCounsellorRollcall().getTempGroup().getId().longValue()) {
                    item.setHaveRead(false);
                    item.setIsOpen(true);
                    item.setOpenTime(new Timestamp(System.currentTimeMillis()));
                    item.setMessageId(signIn.getCounsellorRollcall().getTempGroup().getMessageId());
                    item.setPracticeId(signIn.getCounsellorRollcall().getTempGroup().getPracticeId());
                    List<StuRollcallReportDomainV2> reportList = item.getReportList();
                    if (isV2) {
                        reportList = new ArrayList<>();
                        StuRollcallReportDomainV2 domainV2 = typeStuRollcallReportDomainV2(signIn, alarmClock, rollcall.getId());
                        reportList.add(domainV2);
                        if (alarmClock != null) {
                            item.setAlarmTime(alarmClock.getStartEndTime());
                            item.setAlarmModel(alarmClock.getClockMode());
                            item.setFirstTime(alarmClock.getClockTime());
                            item.setLateTime(alarmClock.getLateTime());
                            item.setSecondTime(alarmClock.getSecondTime());
                            item.setEndTime(alarmClock.getEndTime());
                        }
                    } else {
                        if (reportList == null) {
                            reportList = new ArrayList<>();
                        }
                        StuRollcallReportDomainV2 domainV2 = typeStuRollcallReportDomainV2(signIn, alarmClock, rollcall.getId());
                        reportList.add(domainV2);
                        if (alarmClock != null) {
                            item.setAlarmTime(alarmClock.getClockTime());
                            item.setAlarmModel(alarmClock.getClockMode());
                        }
                    }
                    item.setReportList(reportList);
                    result = item;
                    isUpdate = true;
                    break;
                }
            }
        }
        if (!isUpdate) {
            UserInfo user = userInfoRepository.findByUserId(signIn.getStudentId());
            StuTempGroupDomainV2 item = new StuTempGroupDomainV2();
            item.setGroupId(rollcall.getTempGroup().getId());
            item.setGroupName(rollcall.getTempGroup().getName());
            item.setRollcallNum(rollcall.getTempGroup().getRollcallNum());
            item.setRollcallType(rollcall.getTempGroup().getRollcallType());

            item.setTeacherName(rollcall.getTeacherName());
            item.setStudentId(signIn.getStudentId());
            item.setStudentName(signIn.getStudentName());
            item.setPsersonId(signIn.getStudentNum());
            if (user != null && StringUtils.isNotEmpty(user.getAvatar())) {
                item.setAvatar(user.getAvatar());
            }
            item.setClassName(signIn.getClassName());

            item.setHaveRead(false);
            item.setIsOpen(true);
            item.setOpenTime(new Timestamp(System.currentTimeMillis()));

            if (alarmClock != null) {
                if (isV2) {
                    item.setAlarmTime(alarmClock.getStartEndTime());
                } else {
                    item.setAlarmTime(alarmClock.getClockTime());
                }
                item.setAlarmModel(alarmClock.getClockMode());
                item.setFirstTime(alarmClock.getClockTime());
                item.setLateTime(alarmClock.getLateTime());
                item.setSecondTime(alarmClock.getSecondTime());
                item.setEndTime(alarmClock.getEndTime());
            }


            List<StuRollcallReportDomainV2> reportList = new ArrayList<>();
            StuRollcallReportDomainV2 domainV2 = typeStuRollcallReportDomainV2(signIn, alarmClock, rollcall.getId());
            reportList.add(domainV2);
            item.setReportList(reportList);
            item.setMessageId(signIn.getCounsellorRollcall().getTempGroup().getMessageId());
            item.setPracticeId(signIn.getCounsellorRollcall().getTempGroup().getPracticeId());

            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(item);
            result = item;
        }
        //排序
        Comparator<StuTempGroupDomainV2> comparator = (h1, h2) -> {
            long h1t = 0;
            if (h1.getOpenTime() != null) {
                h1t = h1.getOpenTime().getTime();
            }
            long h2t = 0;
            if (h2.getOpenTime() != null) {
                h2t = h2.getOpenTime().getTime();
            }
            return Long.compare(h1t, h2t);
        };
        list.sort(comparator.reversed());
        counsellorRedisService.pushCacheV2(signIn.getStudentId(), list);
        return result;
    }

    private StuRollcallReportDomainV2 typeStuRollcallReportDomainV2(StudentSignIn signIn, AlarmClock alarmClock, Long rollcallId) {
        StuRollcallReportDomainV2 dd = new StuRollcallReportDomainV2();
        dd.setId(signIn.getId());
        dd.setCounsellorId(rollcallId);
        dd.setIsOpen(true);
        dd.setOpenTime(DateFormatUtil.format(new Date(), DateFormatUtil.FORMAT_SHORT_MINUTE));
        dd.setHaveRead(signIn.isHaveRead());
        dd.setStatus(signIn.getStatus());
        dd.setGpsLocation(signIn.getGpsLocation());
        dd.setGpsDetail(signIn.getGpsDetail());
        if (signIn.getSignTime() != null) {
            dd.setSignTime(DateFormatUtil.format(signIn.getSignTime(), DateFormatUtil.FORMAT_SHORT_MINUTE));
        }
        dd.setHaveReport(signIn.getHaveReport());

        dd.setStatus2(signIn.getStatus2());
        dd.setGpsLocation2(signIn.getGpsLocation2());
        dd.setGpsDetail2(signIn.getGpsDetail2());
        if (signIn.getSignTime2() != null) {
            dd.setSignTime2(DateFormatUtil.format(signIn.getSignTime2(), DateFormatUtil.FORMAT_SHORT_MINUTE));
        }
        dd.setHaveReport2(signIn.getHaveReport2());
        if (alarmClock != null) {
            dd.setFirstTime(alarmClock.getClockTime());
            dd.setLateTime(alarmClock.getLateTime());
            dd.setSecondTime(alarmClock.getSecondTime());
            dd.setEndTime(alarmClock.getEndTime());
        }
        return dd;
    }

    public void updateStuCache(StudentSignIn signIn) {
        List<StuTempGroupDomainV2> list = counsellorRedisService.getRollcallByStuIdV2(signIn.getStudentId());
        if (list != null && list.size() > 0) {
            boolean isUpdate = false;
            for (StuTempGroupDomainV2 item : list) {
                if (item.getReportList() != null && item.getReportList().size() > 0) {
                    for (StuRollcallReportDomainV2 ss : item.getReportList()) {
                        if (ss.getId().longValue() == signIn.getId().longValue()) {
                            ss.setStatus(signIn.getStatus());
                            if (signIn.getSignTime() != null) {
                                ss.setSignTime(DateFormatUtil.format(signIn.getSignTime(), DateFormatUtil.FORMAT_SHORT_MINUTE));
                            }
                            ss.setHaveReport(signIn.getHaveReport());
                            ss.setGpsDetail(signIn.getGpsDetail());
                            ss.setGpsLocation(signIn.getGpsLocation());

                            ss.setStatus2(signIn.getStatus2());
                            if (signIn.getSignTime2() != null) {
                                ss.setSignTime2(DateFormatUtil.format(signIn.getSignTime2(), DateFormatUtil.FORMAT_SHORT_MINUTE));
                            }
                            ss.setHaveReport2(signIn.getHaveReport2());
                            ss.setGpsDetail2(signIn.getGpsDetail2());
                            ss.setGpsLocation2(signIn.getGpsLocation2());

                            isUpdate = true;
                            break;
                        }
                    }
                }
                if (isUpdate) {
                    break;
                }
            }
            if (isUpdate) {
                counsellorRedisService.pushCacheV2(signIn.getStudentId(), list);
            }
        }
    }

    private List<StuTempGroupDomainV2> listStudentSignIn(Long userId, boolean isRead) {
        UserInfo user = userInfoRepository.findByUserId(userId);
        List<StuTempGroupDomainV2> reuslt = new ArrayList<>();
        List<StuTempGroupDomainV2> list = rollcallQuery.findGroupByStudentId(userId);
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        for (StuTempGroupDomainV2 item : list) {
            if (item.getOpenTime() != null) {
                if (user != null) {
                    item.setStudentName(user.getName());
                    item.setPsersonId(user.getJobNum());
                    item.setStudentId(userId);
                    item.setAvatar(user.getAvatar());
                    item.setClassName(user.getClassesName());
                }
                if (item.getRollcallNum() == null || item.getRollcallNum().intValue() < 1) {
                    item.setRollcallNum(1);
                }
                if (item.getRollcallType() == null || item.getRollcallType().intValue() < 1) {
                    item.setRollcallType(CounsellorRollCallType.Other.getType());
                }
                Date startDate = new Date();
                startDate.setHours(0);
                startDate.setMinutes(0);
                startDate.setSeconds(0);
                Date endDate = new Date();
                endDate.setHours(23);
                endDate.setMinutes(59);
                endDate.setSeconds(59);
                List<StuRollcallReportDomainV2> signlist = studentSignInRepository.findByStudentIdAndGroupIdAndDateAndDeleteFlag(userId, item.getGroupId(), startDate, endDate, DataValidity.VALID.getState());
                if (signlist != null && signlist.size() > 0) {
                    StuRollcallReportDomainV2 sign = signlist.get(signlist.size() - 1);
                    item.setHaveRead(sign.getHaveRead());
                    item.setReportList(signlist);
                    item.setIsOpen(sign.getIsOpen());
                    for (StuRollcallReportDomainV2 sitem : signlist) {
                        sitem.setFirstTime(item.getFirstTime());
                        sitem.setLateTime(item.getLateTime());
                        sitem.setSecondTime(item.getSecondTime());
                        sitem.setEndTime(item.getEndTime());
                        if (isRead && !sitem.getHaveRead()) {
                            studentSignInRepository.updateHaveRead(sitem.getId());
                        }
                    }
                }
                reuslt.add(item);
            }
        }
        Comparator<StuTempGroupDomainV2> comparator = (h1, h2) -> {
            return Float.compare(h1.getOpenTime().getTime(), h2.getOpenTime().getTime());
        };
        reuslt.sort(comparator.reversed());
        return reuslt;
    }

    //队列
    public Map<String, Object> signin(Long stuId) {
        return null;
    }

    private String formatDate(Date time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(time);
    }

    private Date getTime(String timeStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return format.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
