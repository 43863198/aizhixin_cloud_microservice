package com.aizhixin.cloud.dd.rollcall.serviceV2;

import com.aizhixin.cloud.dd.appeal.entity.AppealFile;
import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.constant.LeaveConstants;
import com.aizhixin.cloud.dd.constant.PushMessageConstants;
import com.aizhixin.cloud.dd.messege.dto.AudienceDTO;
import com.aizhixin.cloud.dd.messege.service.MessageService;
import com.aizhixin.cloud.dd.messege.service.PushMessageService;
import com.aizhixin.cloud.dd.messege.service.PushService;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.domain.LeaveDomain;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.DianDianDaySchoolTimeTableDomain;
import com.aizhixin.cloud.dd.rollcall.dto.PeriodDTO;
import com.aizhixin.cloud.dd.rollcall.entity.Leave;
import com.aizhixin.cloud.dd.rollcall.repository.LeaveRepository;
import com.aizhixin.cloud.dd.rollcall.service.InitScheduleService;
import com.aizhixin.cloud.dd.rollcall.service.PeriodService;
import com.aizhixin.cloud.dd.rollcall.utils.IOUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LeaveServiceV2 {
    private final Logger log = LoggerFactory.getLogger(LeaveServiceV2.class);
    @Autowired
    private PushService pushService;
    @Autowired
    private IOUtil ioUtil = new IOUtil();
    @Autowired
    private MessageService messageService;
    @Autowired
    private PushMessageService pushMessageService;
    @Autowired
    private LeaveRepository leaveRepository;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteClient;
    @Autowired
    private PeriodService periodService;
    @Autowired
    private UserInfoRepository userInfoRepository;

    @Async
    public void initLeaveData() {
        List<Leave> leaves = leaveRepository.findByDeleteFlagAndStartTimeIsNullOrEndTimeIsNullOrOrgIdIsNull(DataValidity.VALID.getState());
        Map<Long, UserInfo> userInfoMap = new HashMap<>();
        Map<Long, Map<String, Object>> periodMap = new HashMap<>();
        for (Leave leave : leaves) {
            if (leave.getStartTime() != null) {
                UserInfo stu = getUserInfo(userInfoMap, leave.getStudentId());
                if (stu != null) {
                    leave.setOrgId(stu.getOrgId());
                }
                if(leave.getEndTime() == null && leave.getRequestType().equals(LeaveConstants.TYPE_PERIOD)){
                    Map<String, Object> endp = getPeriod(periodMap, leave.getEndPeriodId());
                    if (endp != null) {
                        leave.setEndTime(DateFormatUtil.parse2(DateFormatUtil.formatShort(leave.getStartDate()) + " " + endp.get("endTime"), DateFormatUtil.FORMAT_MINUTE));
                    }
                }
            } else {
                if (leave.getRequestType().equals(LeaveConstants.TYPE_DAY)) {
                    leave.setStartTime(leave.getStartDate());
                    leave.setEndTime(DateFormatUtil.parse2(DateFormatUtil.formatShort(leave.getEndDate()) + " 23:59:59", DateFormatUtil.FORMAT_MINUTE));
                } else {
                    Map<String, Object> startp = getPeriod(periodMap, leave.getStartPeriodId());
                    if (startp != null) {
                        leave.setStartTime(DateFormatUtil.parse2(DateFormatUtil.formatShort(leave.getStartDate()) + " " + startp.get("startTime"), DateFormatUtil.FORMAT_MINUTE));
                    }
                    Map<String, Object> endp = getPeriod(periodMap, leave.getEndPeriodId());
                    if (endp != null) {
                        leave.setEndTime(DateFormatUtil.parse2(DateFormatUtil.formatShort(leave.getStartDate()) + " " + endp.get("endTime"), DateFormatUtil.FORMAT_MINUTE));
                    }
                }
                UserInfo stu = getUserInfo(userInfoMap, leave.getStudentId());
                if (stu != null) {
                    leave.setStudentName(stu.getName());
                    leave.setStudentJobNum(stu.getJobNum());
                    leave.setClassName(stu.getClassesName());
                    leave.setOrgId(stu.getOrgId());
                }
                UserInfo teacher = getUserInfo(userInfoMap, leave.getHeadTeacherId());
                if (teacher != null) {
                    leave.setTeacherJobNum(teacher.getJobNum());
                }
            }
            if (leave.getStartTime() != null && leave.getEndTime() != null) {
                String duration = getDuration(leave.getEndTime(), leave.getStartTime());
                leave.setDuration(duration);
            }
            leaveRepository.save(leave);
        }
    }

    private Map<String, Object> getPeriod(Map<Long, Map<String, Object>> periodMap, Long periodId) {
        Map<String, Object> period = periodMap.get(periodId);
        if (period == null) {
            Map<String, Object> map = orgManagerRemoteClient.getPeriod(periodId);
            if (map != null && map.get("startTime") != null && map.get("endTime") != null) {
                periodMap.put(periodId, map);
                period = map;
            }
        }
        return period;
    }

    private UserInfo getUserInfo(Map<Long, UserInfo> userInfoMap, Long userId) {
        UserInfo user = userInfoMap.get(userId);
        if (user == null) {
            user = userInfoRepository.findByUserId(userId);
            if (user != null) {
                userInfoMap.put(user.getUserId(), user);
            }
        }
        return user;
    }

    public PageData<LeaveDomain> getLeaveList(Pageable pageable, Long orgId, String stuName, String teacherName, Integer status, String className, Integer leavePublic, Integer leaveType) {
        String statusStr = "reject";
        if (status == 1) {
            statusStr = "pass";
        }
        if (StringUtils.isEmpty(stuName)) {
            stuName = "";
        }
        if (StringUtils.isEmpty(teacherName)) {
            teacherName = "";
        }
        if (StringUtils.isEmpty(className)) {
            className = "";
        }
        Page<LeaveDomain> page = null;
        if (leavePublic != null && leaveType != null) {
            page = leaveRepository.findByStatusAndLeavePublicAndLeaveTypeAndDeleteFlagAndNameLike(pageable, orgId, statusStr, leavePublic, leaveType, DataValidity.VALID.getState(), stuName, teacherName, className);
        } else if (leavePublic != null) {
            page = leaveRepository.findByStatusAndLeavePublicAndDeleteFlagAndNameLike(pageable, orgId, statusStr, leavePublic, DataValidity.VALID.getState(), stuName, teacherName, className);
        } else {
            page = leaveRepository.findByStatusAndDeleteFlagAndNameLike(pageable, orgId, statusStr, DataValidity.VALID.getState(), stuName, teacherName, className);
        }
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageSize(page.getSize());
        pageDomain.setPageNumber(page.getNumber());
        pageDomain.setTotalElements(page.getTotalElements());
        pageDomain.setTotalPages(page.getTotalPages());
        List<LeaveDomain> list = page.getContent();
        PageData<LeaveDomain> pageData = new PageData<>();
        pageData.setData(list);
        pageData.setPage(pageDomain);
        return pageData;
    }

    public Map<String, Object> getLeaveType() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> puList = new ArrayList<>();
        Map<String, Object> put1 = new HashMap<>();
        put1.put("type", LeaveConstants.TYPE_PU_SX);
        put1.put("name", "实习");
        puList.add(put1);
        Map<String, Object> put2 = new HashMap<>();
        put2.put("type", LeaveConstants.TYPE_PU_ZS);
        put2.put("name", "招生");
        puList.add(put2);
        Map<String, Object> put3 = new HashMap<>();
        put3.put("type", LeaveConstants.TYPE_PU_GS);
        put3.put("name", "公事");
        puList.add(put3);
        Map<String, Object> put35 = new HashMap<>();
        put35.put("type", LeaveConstants.TYPE_PU_QT);
        put35.put("name", "其它");
        puList.add(put35);
        result.put("公假", puList);
        List<Map<String, Object>> prList = new ArrayList<>();
        Map<String, Object> put4 = new HashMap<>();
        put4.put("type", LeaveConstants.TYPE_PR_BJ);
        put4.put("name", "病假");
        prList.add(put4);
        Map<String, Object> put5 = new HashMap<>();
        put5.put("type", LeaveConstants.TYPE_PR_SJ);
        put5.put("name", "事假");
        prList.add(put5);
        Map<String, Object> put55 = new HashMap<>();
        put55.put("type", LeaveConstants.TYPE_PR_QT);
        put55.put("name", "其它");
        puList.add(put55);
        result.put("私假", prList);
        return result;
    }

    public Object requestLeave(AccountDTO account, Integer laveType, Date startTime, Date endTime, String content,
                               Long headTeacherId, String headTeacherName, String accessToken, List<AppealFile> files) {
        String leavePictureUrls = "";
        String requestType = LeaveConstants.TYPE_DAY;
        if (endTime.getTime() - startTime.getTime() < 1000 * 60 * 60 * 24) {
            requestType = LeaveConstants.TYPE_PERIOD;
        }
        Integer leavePublic = LeaveConstants.TYPE_PR;
        if (laveType < 40) {
            leavePublic = LeaveConstants.TYPE_PU;
        }
        String duration = getDuration(endTime, startTime);
        Date startDay = DateFormatUtil.parseDay(DateFormatUtil.formatShort(startTime));
        Date endDay = DateFormatUtil.parseDay(DateFormatUtil.formatShort(endTime));
        try {
            if (files != null && files.size() > 0) {
                for (int i = 0; i < files.size(); i++) {
                    AppealFile file = files.get(i);
                    leavePictureUrls += file.getFileSrc();
                    if (i != files.size() - 1) {
                        leavePictureUrls += ";";
                    }
                }
            }
        } catch (Exception e) {
            log.warn("请假图片上传失败...。");
            e.printStackTrace();
        }
        if (leavePictureUrls == null) {
            leavePictureUrls = "";
        }
        List<Long> ids = new ArrayList<Long>();
        List<AudienceDTO> audiences = new ArrayList<>();
        UserInfo stu = userInfoRepository.findByUserId(account.getId());
        List<PeriodDTO> startPeriods =  getStartPeriodList(account.getId(), account.getOrganId(), startTime);
        List<PeriodDTO> endPeriods =  getEndPeriodList(account.getId(), account.getOrganId(), endTime);
        Long startPeriodId = getStartPeriodId(startTime, startPeriods);
        Long endPeriodId = getEndPeriodId(endTime, endPeriods);
        // 由班主任照常请
        if (headTeacherId != null) {
            UserInfo teacher = userInfoRepository.findByUserId(headTeacherId);
            if (requestType.equals(LeaveConstants.TYPE_DAY)) {
                Leave l = new Leave();
                l.setHeadTeacherId(headTeacherId);
                l.setTeacherName(headTeacherName);
                l.setRequestContent(content);
                l.setRequestType(requestType);
                l.setStartPeriodId(startPeriodId);
                l.setEndPeriodId(endPeriodId);
                l.setStartDate(startDay);
                l.setEndDate(endDay);
                l.setStartTime(startTime);
                l.setEndTime(endTime);
                l.setDuration(duration);
                l.setLeaveType(laveType);
                l.setLeavePublic(leavePublic);
                l.setStatus(LeaveConstants.STATUS_REQUEST);
                l.setStudentId(account.getId());
                l.setStudentName(account.getName());
                l.setStudentJobNum(stu.getJobNum());
                l.setClassName(stu.getClassesName());
                l.setOrgId(stu.getOrgId());
                if (teacher != null) {
                    l.setTeacherJobNum(teacher.getJobNum());
                }
                l.setLeaveSchool(true);
                l.setLeavePictureUrls(leavePictureUrls);
                ids.add(headTeacherId);
                l = leaveRepository.save(l);
                pushMessageService.createPushMessage("请假审批通知", "请假审批通知", PushMessageConstants.FUNCTION_TEACHER_APPROVAL, PushMessageConstants.MODULE_LEAVE, "请假审批通知", headTeacherId);
                AudienceDTO dto = new AudienceDTO();
                dto.setUserId(headTeacherId);
                dto.setData(l);
                audiences.add(dto);
            } else if (requestType.equals(LeaveConstants.TYPE_PERIOD)) {
                Leave l = new Leave();
                l.setHeadTeacherId(headTeacherId);
                l.setTeacherName(headTeacherName);
                l.setRequestContent(content);
                l.setRequestType(requestType);
                l.setStartPeriodId(startPeriodId);
                l.setEndPeriodId(endPeriodId);
                l.setStartDate(startDay);
                l.setStartTime(startTime);
                l.setEndTime(endTime);
                l.setDuration(duration);
                l.setLeaveType(laveType);
                l.setLeavePublic(leavePublic);
                l.setStatus(LeaveConstants.STATUS_REQUEST);
                l.setLeaveSchool(true);
                l.setLeavePictureUrls(leavePictureUrls);
                l.setStudentId(account.getId());
                l.setStudentName(account.getName());
                l.setStudentJobNum(stu.getJobNum());
                l.setClassName(stu.getClassesName());
                l.setOrgId(stu.getOrgId());
                if (teacher != null) {
                    l.setTeacherJobNum(teacher.getJobNum());
                }
                ids.add(headTeacherId);
                l = leaveRepository.save(l);
                pushMessageService.createPushMessage("请假审批通知", "请假审批通知", PushMessageConstants.FUNCTION_TEACHER_APPROVAL, PushMessageConstants.MODULE_LEAVE, "请假审批通知", headTeacherId);
                AudienceDTO dto = new AudienceDTO();
                dto.setUserId(headTeacherId);
                dto.setData(l);
                audiences.add(dto);
            }
        } else {
            // 查看类型
            if (requestType.equals(LeaveConstants.TYPE_DAY)) {
                // 按天请的话需要处理成按节请假
                String beginTeachTime = DateFormatUtil.format(startDay, DateFormatUtil.FORMAT_SHORT);
                String endTeachTime = DateFormatUtil.format(endDay, DateFormatUtil.FORMAT_SHORT);

                List<Date> manyDate = DateFormatUtil.getMonthBetweenDate(startDay, endDay);
                for (Date schoolDay : manyDate) {
                    // 根据日期从平台获取排课数据
                    List<DianDianDaySchoolTimeTableDomain> ddt = orgManagerRemoteClient.getStudentDaySchoolTimeTable(account.getId(), DateFormatUtil.formatShort(schoolDay));
                    Date date = new Date();

                    for (DianDianDaySchoolTimeTableDomain ddds : ddt) {
                        List<IdNameDomain> idNameDomains = InitScheduleService.parseTeacherList(ddds.getTeachers());
                        if (null == idNameDomains || idNameDomains.size() == 0) {
                            log.warn("请假模块获取老师信息失败...,该节课无法进行请假。" + ddds.toString());
                            continue;
                        }
                        for (IdNameDomain idNameDomain : idNameDomains) {
                            Leave l = new Leave();
                            Long teacherId = idNameDomain.getId();
                            UserInfo user = userInfoRepository.findByUserId(teacherId);
                            l.setHeadTeacherId(teacherId);
                            l.setTeacherName(idNameDomain.getName());
                            l.setStartDate(ddds.getTeachDate());
                            l.setRequestContent(content);
                            l.setRequestType(LeaveConstants.TYPE_PERIOD);
                            l.setStatus(LeaveConstants.STATUS_REQUEST);
                            l.setLeaveSchool(true);
                            l.setStudentId(account.getId());
                            l.setStudentName(account.getName());
                            l.setStudentJobNum(stu.getJobNum());
                            l.setClassName(stu.getClassesName());
                            l.setOrgId(stu.getOrgId());
                            if (user != null) {
                                l.setTeacherJobNum(user.getJobNum());
                            }
                            l.setStartPeriodId(ddds.getPeriodId());
                            l.setStartTime(startTime);
                            l.setEndTime(endTime);
                            l.setDuration(duration);
                            l.setLeaveType(laveType);
                            l.setLeavePublic(leavePublic);
                            // 暂未计算结束课程节(未完成LMH)
                            l.setEndPeriodId(ddds.getPeriodId());
                            l.setLeavePictureUrls(leavePictureUrls);
                            l.setCreatedDate(date);
                            l = leaveRepository.save(l);
                            ids.add(teacherId);
                            pushMessageService.createPushMessage("请假审批通知", "请假审批通知", PushMessageConstants.FUNCTION_TEACHER_APPROVAL, PushMessageConstants.MODULE_LEAVE, "请假审批通知", teacherId);
                            AudienceDTO dto = new AudienceDTO();
                            dto.setUserId(teacherId);
                            dto.setData(l);
                            audiences.add(dto);
                        }
                    }
                }
            } else {
                Map map = getBetweenStartAndEndPeriodId(account.getOrganId(), startPeriodId, endPeriodId);
                List<DianDianDaySchoolTimeTableDomain> ddt = orgManagerRemoteClient.getStudentDaySchoolTimeTable(account.getId(), null);
                Date date = new Date();
                for (DianDianDaySchoolTimeTableDomain ddds : ddt) {
                    if (map.containsKey(ddds.getPeriodId())) {
                        List<IdNameDomain> idNameDomains = InitScheduleService.parseTeacherList(ddds.getTeachers());
                        if (null == idNameDomains || idNameDomains.size() == 0) {
                            log.warn("请假模块获取老师信息失败...,该节课无法进行请假。" + ddds.toString());
                            continue;
                        }
                        for (IdNameDomain idNameDomain : idNameDomains) {
                            Leave l = new Leave();
                            Long teacherId = idNameDomain.getId();
                            UserInfo user = userInfoRepository.findByUserId(teacherId);
                            l.setHeadTeacherId(teacherId);
                            l.setTeacherName(idNameDomain.getName());
                            l.setRequestContent(content);
                            l.setRequestType(LeaveConstants.TYPE_PERIOD);
                            l.setStartPeriodId(ddds.getPeriodId());
                            l.setEndPeriodId(ddds.getPeriodId());
                            l.setStatus(LeaveConstants.STATUS_REQUEST);
                            l.setStudentId(account.getId());
                            l.setStudentName(account.getName());
                            l.setStudentJobNum(stu.getJobNum());
                            l.setClassName(stu.getClassesName());
                            l.setOrgId(stu.getOrgId());
                            if (user != null) {
                                l.setTeacherJobNum(user.getJobNum());
                            }
                            l.setLeaveSchool(true);
                            l.setStartDate(ddds.getTeachDate());
                            l.setStartTime(startTime);
                            l.setEndTime(endTime);
                            l.setDuration(duration);
                            l.setLeaveType(laveType);
                            l.setLeavePublic(leavePublic);
                            l.setLeavePictureUrls(leavePictureUrls);
                            l.setCreatedDate(date);
                            l = leaveRepository.save(l);
                            ids.add(teacherId);
                            pushMessageService.createPushMessage("请假审批通知", "请假审批通知", PushMessageConstants.FUNCTION_TEACHER_APPROVAL, PushMessageConstants.MODULE_LEAVE, "请假审批通知", teacherId);
                            AudienceDTO dto = new AudienceDTO();
                            dto.setUserId(teacherId);
                            dto.setData(l);
                            audiences.add(dto);
                        }
                    }
                }
            }
        }
        pushService.listPush(accessToken, "您有新的请假申请未处理", "请假申请", "请假申请", ids);
        //----新消息服务----start
        messageService.push("请假审批通知", "请假审批通知", PushMessageConstants.FUNCTION_TEACHER_APPROVAL, audiences);
        //----新消息服务----end
        Map<String, Object> res = new HashMap<>();
        res.put(ApiReturnConstants.MESSAGE, ApiReturnConstants.SUCCESS);
        res.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        return res;
    }

    private String getDuration(Date endDate, Date startDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
//        long nm = 1000 * 60;
        long diff = endDate.getTime() - startDate.getTime();
        long day = diff / nd;
        long hour = diff % nd / nh;
//        long min = diff % nd % nh / nm;
        return day + "天" + hour + "小时";// + min + "分钟";
    }


    public Map getBetweenStartAndEndPeriodId(long organId, Long startId, Long endId) {
        Map<Long, Object> map = new HashMap();
        // 计算两个课程节之间的课程节id。
        List<PeriodDTO> periodList = periodService.listPeriod(organId);
        if (null != periodList && periodList.size() > 0) {
            PeriodDTO startPeriod = null;
            PeriodDTO endPeiod = null;
            for (PeriodDTO p : periodList) {
                if (startId.longValue() == p.getId().longValue()) {
                    startPeriod = p;
                }
                if (endId.longValue() == p.getId().longValue()) {
                    endPeiod = p;
                }
            }
            if (null != startPeriod && null != endPeiod) {
                if (startPeriod.getNo().longValue() == endPeiod.getNo().longValue()) {
                    map.put(startPeriod.getId(), startPeriod);
                    return map;
                }
                // 待复查
                for (PeriodDTO p : periodList) {
                    if (p.getNo().longValue() >= startPeriod.getNo().longValue() && p.getNo().longValue() <= endPeiod.getNo().longValue()) {
                        map.put(p.getId(), p);
                    }
                }
            }
        }
        return map;
    }

    private List<PeriodDTO> getPeriodList(Long stuId, Long orgId, Date date) {
        List<PeriodDTO> list = periodService.findAllByOrganIdAndStatusV2(stuId, orgId, date);
        return list;
    }

    private List<PeriodDTO> getStartPeriodList(Long stuId, Long orgId, Date date) {
        return periodService.listPeriod(orgId);
    }

    private List<PeriodDTO> getEndPeriodList(Long stuId, Long orgId, Date date) {
        List<PeriodDTO> list = periodService.listPeriod(orgId);
        Collections.reverse(list);
        return list;
    }

    private Long getStartPeriodId(Date startTime, List<PeriodDTO> list) {
        if (list != null && list.size() > 0) {
            long start = startTime.getTime();
            String dayStr = DateFormatUtil.formatShort(startTime);
            for (PeriodDTO p : list) {
                Date date = DateFormatUtil.parse2(dayStr + " " + p.getStartTime(), "yyyy-MM-dd HH:mm");
                if (date != null) {
                    if (date.getTime() >= start) {
                        return p.getId();
                    } else {
                        Date edate = DateFormatUtil.parse2(dayStr + " " + p.getEndTime(), "yyyy-MM-dd HH:mm");
                        if (edate != null) {
                            if (edate.getTime() >= start) {
                                return p.getId();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private Long getEndPeriodId(Date endTime, List<PeriodDTO> list) {
        if (list != null && list.size() > 0) {
            long end = endTime.getTime();
            String dayStr = DateFormatUtil.formatShort(endTime);
            for (PeriodDTO p : list) {
                Date date = DateFormatUtil.parse2(dayStr + " " + p.getStartTime(), "yyyy-MM-dd HH:mm");
                if (date != null) {
                    if (end >= date.getTime()) {
                        return p.getId();
                    } else {
                        Date edate = DateFormatUtil.parse2(dayStr + " " + p.getEndTime(), "yyyy-MM-dd HH:mm");
                        if (edate != null) {
                            if (end >= edate.getTime()) {
                                return p.getId();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
