package com.aizhixin.cloud.dd.communication.service;

import com.aizhixin.cloud.dd.common.core.ApiReturn;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.communication.dto.ReportDTO;
import com.aizhixin.cloud.dd.communication.dto.RollCallEverDTO;
import com.aizhixin.cloud.dd.communication.dto.RollCallReportDTO;
import com.aizhixin.cloud.dd.communication.dto.RollCallReportStudentDTO;
import com.aizhixin.cloud.dd.communication.entity.RollCallEver;
import com.aizhixin.cloud.dd.communication.entity.RollCallReport;
import com.aizhixin.cloud.dd.communication.jdbcTemplate.RollCallEverQuery;
import com.aizhixin.cloud.dd.communication.repository.RollCallEverRepository;
import com.aizhixin.cloud.dd.communication.repository.RollCallReportRepository;
import com.aizhixin.cloud.dd.communication.utils.HttpSimpleUtils;
import com.aizhixin.cloud.dd.constant.PushMessageConstants;
import com.aizhixin.cloud.dd.constant.RollCallConstants;
import com.aizhixin.cloud.dd.counsellorollcall.repository.StudentSignInRepository;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.PeriodDTO;
import com.aizhixin.cloud.dd.rollcall.dto.StudentDomain;
import com.aizhixin.cloud.dd.rollcall.entity.Leave;
import com.aizhixin.cloud.dd.messege.entity.PushMessage;
import com.aizhixin.cloud.dd.rollcall.repository.LeaveRepository;
import com.aizhixin.cloud.dd.rollcall.repository.PushMessageRepository;
import com.aizhixin.cloud.dd.rollcall.service.PeriodService;
import com.aizhixin.cloud.dd.messege.service.PushService;
import com.aizhixin.cloud.dd.rollcall.utils.RedisUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
// 无用
@Component
@Transactional
public class RollCallEverService {

    private final Logger log = LoggerFactory.getLogger(RollCallEverService.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private StudentSignInRepository studentSignInRepository;

    @Autowired
    private RollCallEverRepository rollCallEverRepository;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;
    @Autowired
    private RollCallReportRepository rollCallReportRepository;
    @Autowired
    private PushMessageRepository pushMessageRepository;
    @Autowired
    private PushService pushService;
    @Autowired
    private RollCallEverQuery rollCallEverQuery;
    @Autowired
    private HttpSimpleUtils httpSimpleUtils;
    @Autowired
    private LeaveRepository leaveRepository;
    @Autowired
    private PeriodService periodService;

    public boolean openRollCallEver(String accessToken, Long teacherId, List<Long> classIds) {
        if (teacherId == null || classIds == null) {
            return false;
        }
        RollCallEver rollCallEver = new RollCallEver();
        rollCallEver.setTeacherId(teacherId);
        rollCallEver.setClassIds(httpSimpleUtils.ArrayToStringIds(classIds));
        rollCallEver.setOpenTime(new Timestamp(System.currentTimeMillis()));
        rollCallEver.setStatus(true);
        rollCallEverRepository.save(rollCallEver);
        initStudentRollCallEver(accessToken, rollCallEver.getId(), classIds);
        return true;
    }

    public void initStudentRollCallEver(String accessToken, Long rollCallEverId, List<Long> classIds) {
        Set<Long> cids = new HashSet<>();
        if (null != classIds && classIds.size() > 0) {
            cids.addAll(classIds);
        }
        for (Long cid : cids) {
            List<Long> studentEver = orgManagerRemoteService.getstudentidsbyclassesid(cid);
            if (null != studentEver && studentEver.size() > 0) {
                List<RollCallReport> list = new ArrayList<RollCallReport>();
                RollCallReport rcr = null;

                List<PushMessage> messages = new ArrayList<PushMessage>();
                List<Long> userIds = new ArrayList<Long>();
                for (Long studentId : studentEver) {
                    rcr = new RollCallReport();
                    rcr.setRollCallEverId(rollCallEverId);
                    rcr.setStudentId(studentId);
                    rcr.setHaveReport(false);
                    rcr.setClassId(cid);
                    list.add(rcr);
                    PushMessage message = new PushMessage();
                    message.setContent("您有新的签到提醒！");
                    message.setFunction(PushMessageConstants.FUNCITON_STUDENT_NOTICE);
                    message.setModule(PushMessageConstants.MODULE_RollCallEVER);
                    message.setHaveRead(Boolean.FALSE);
                    message.setPushTime(new Date());
                    message.setDeleteFlag(DataValidity.VALID.getState());
                    message.setTitle("辅导员点名通知");
                    message.setUserId(studentId);
                    String businessContent = "";
                    message.setBusinessContent(businessContent);
                    messages.add(message);
                    userIds.add(studentId);
                }
                rollCallReportRepository.save(list);
                //TODO 消息 保存 需要更新缓存
                pushMessageRepository.save(messages);
                pushService.listPush(accessToken, "您有一个新的签到提醒", "辅导员点名通知", "辅导员点名通知", userIds);
            }
        }
    }

    public boolean openRollCallEvers(String accessToken, Long teacherId, List<Long> classIds, Long organId) throws ParseException {
        if (teacherId == null || classIds == null) {
            return false;
        }
        RollCallEver rollCallEver = new RollCallEver();
        rollCallEver.setTeacherId(teacherId);
        rollCallEver.setClassIds(httpSimpleUtils.ArrayToStringIds(classIds));
        rollCallEver.setOpenTime(new Timestamp(System.currentTimeMillis()));
        rollCallEver.setStatus(true);
        rollCallEverRepository.save(rollCallEver);
        initStudentRollCallEvers(accessToken, rollCallEver.getId(), classIds, organId);
        return true;
    }

    public void initStudentRollCallEvers(String accessToken, Long rollCallEverId, List<Long> classIds, Long organId) throws ParseException {
        Set<Long> cids = new HashSet<>();
        if (null != classIds && classIds.size() > 0) {
            cids.addAll(classIds);
        }
        // List <Long> studentEver = orgManagerRemoteService.getstudentidsbyclassesids(cids);
        List<StudentDomain> studentEver = orgManagerRemoteService.findStudentDomainByClassesIds(cids);
        if (null != studentEver && studentEver.size() > 0) {
            List<RollCallReport> list = new ArrayList<RollCallReport>();
            RollCallReport rcr = null;
            List<PushMessage> messages = new ArrayList<PushMessage>();
            List<Long> userIds = new ArrayList<Long>();

            Set<Long> ids = new HashSet<>();
            for (StudentDomain studentDomain : studentEver) {
                ids.add(studentDomain.getId());
            }
            List<Leave> allLeave = leaveRepository.findAllByStudentIdWithCurrentDate(ids);
            Map<Long, List> leaveMap = new HashedMap();
            if (null != allLeave && allLeave.size() > 0) {
                List leavelist = null;
                for (Leave leave : allLeave) {
                    if (!leaveMap.containsKey(leave.getStudentId())) {
                        leavelist = new ArrayList();
                        leaveMap.put(leave.getStudentId(), leavelist);
                    }
                    leavelist.add(leave);
                }
            }

            for (StudentDomain studentDomain : studentEver) {
                rcr = new RollCallReport();
                rcr.setRollCallEverId(rollCallEverId);
                rcr.setStudentId(studentDomain.getId());
                rcr.setLookStatus(0);
                rcr.setClassId(studentDomain.getClassesId());
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
                SimpleDateFormat edf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
                Date currentDate = new Date();
                String start = null, end = null;

                List<Leave> leaveList = leaveMap.get(studentDomain.getId());
                if (null != leaveList && leaveList.size() > 0) {
                    for (Leave leave : leaveList) {
                        String requestType = leave.getRequestType();
                        Date startDate = leave.getStartDate();
                        if (startDate != null) {
                            start = sdf.format(startDate);
                        }
                        Date endDate = leave.getEndDate();
                        if (endDate != null) {
                            end = edf.format(endDate);
                        }
                        if ("day".equals(requestType)) {
                            Date startDate1 = null;
                            Date endDate1 = null;
                            if (start != null) {
                                startDate1 = s.parse(start);
                            }
                            if (end != null) {
                                endDate1 = s.parse(end);
                            }
                            if (currentDate.getTime() >= startDate1.getTime() && currentDate.getTime() <= endDate1.getTime()) {
                                rcr.setLeaveStatus(true);
                            }
                        } else if ("period".equals(requestType)) {
                            /*取课程节开始 结束信息*/
                            Long startPeriodId = leave.getStartPeriodId();
                            Long endPeriodId = leave.getEndPeriodId();
                            String startTime, endTime;
                            Date startDate1 = null, endDate1 = null;
                            List<PeriodDTO> periods = periodService.listPeriod(organId);
                            if (periods != null && periods.size() > 0) {
                                for (PeriodDTO period : periods) {
                                    if (startPeriodId == period.getId()) {
                                        startTime = DateFormatUtil.format(new Date(), DateFormatUtil.FORMAT_SHORT) + " " + period.getStartTime() + ":00";
                                        startDate1 = DateFormatUtil.parse(startTime, DateFormatUtil.FORMAT_LONG);
                                    }
                                    if (endPeriodId == period.getId()) {
                                        endTime = DateFormatUtil.format(new Date(), DateFormatUtil.FORMAT_SHORT) + " " + period.getEndTime() + ":00";
                                        endDate1 = DateFormatUtil.parse(endTime, DateFormatUtil.FORMAT_LONG);
                                    }
                                }
                                /* 比较 */
                                if (currentDate.getTime() >= startDate1.getTime() && currentDate.getTime() <= endDate1.getTime()) {
                                    rcr.setLeaveStatus(true);
                                }
                            }
                        }
                    }
                } else {
                    rcr.setLeaveStatus(Boolean.FALSE);
                }
                rcr.setHaveReport(false);
                list.add(rcr);
                PushMessage message = new PushMessage();
                message.setContent("您有新的签到提醒！");
                message.setFunction(PushMessageConstants.FUNCITON_STUDENT_NOTICE);
                message.setModule(PushMessageConstants.MODULE_RollCallEVER);
                message.setHaveRead(Boolean.FALSE);
                message.setPushTime(new Date());
                message.setDeleteFlag(DataValidity.VALID.getState());
                message.setTitle("辅导员点名通知");
                message.setUserId(studentDomain.getId());
                String businessContent = "";
                message.setBusinessContent(businessContent);
                messages.add(message);
                userIds.add(studentDomain.getId());
            }
            rollCallReportRepository.save(list);
            //TODO 消息 保存 需要更新缓存
            pushMessageRepository.save(messages);
            pushService.listPush(accessToken, "您有一个新的签到提醒", "辅导员点名通知", "辅导员点名通知", userIds);
        }
    }

    public void closeRollCallEver(Long rollCallEverId) {
        rollCallEverRepository.updateRollCallEver(rollCallEverId);
    }

    public List<RollCallEverDTO> queryEver(Long teacherId, Long rollCallEverId) {
        return rollCallEverQuery.queryEver(teacherId, rollCallEverId);
    }

    public List<RollCallReportDTO> querEverDetail(Long rollCallEverId, Boolean haveReport) {
        return rollCallEverQuery.queryEverDetail(rollCallEverId, haveReport);

    }

    public List<RollCallReportDTO> querEverDetails(Long rollCallEverId, Boolean haveReport, Boolean leaveStatus) {
        return rollCallEverQuery.queryEverDetails(rollCallEverId, haveReport, leaveStatus);

    }

    public Map<String, Object> reprotEver(AccountDTO accountDTO, ReportDTO reportDto) {
        RollCallReport rollCallReport = rollCallReportRepository.findOne(reportDto.getId());
        if (null == rollCallReport) {
            return null;
        }
        RollCallEver rollCallEver = rollCallEverRepository.findOne(rollCallReport.getRollCallEverId());
        if (rollCallEver == null) {
            return null;
        }
        if (!rollCallEver.isStatus()) {
            return null;
        }

        if (accountDTO.getAntiCheating()) {
            if (StringUtils.isNotBlank(reportDto.getDeviceToken())) {
                if (!signAntiCheating(rollCallReport.getRollCallEverId(), accountDTO.getId(), reportDto.getDeviceToken())) {
                    return ApiReturn.message(Boolean.FALSE, RollCallConstants.ROLL_CALL_WARNING_MESSAGE, null);
                }
            }
        }

        rollCallReport.setGpsDetail(reportDto.getGpsDetail());
        rollCallReport.setGpsLocation(reportDto.getGpsLocaltion());
        rollCallReport.setGpsType(reportDto.getGpsType());
        rollCallReport.setSignTime(new Timestamp(System.currentTimeMillis()));
        rollCallReport.setDeviceToken(reportDto.getDeviceToken());
        rollCallReport.setHaveReport(Boolean.TRUE);
        rollCallReportRepository.save(rollCallReport);
        return ApiReturn.message(Boolean.TRUE, null, null);
    }

    public boolean signAntiCheating(Long counsellorId, Long studentId, String deviceToken) {
        boolean isCan = true;
        deviceToken = StringUtils.isNotBlank(deviceToken) ? deviceToken.trim() : null;
        try {
            Object stuId = stringRedisTemplate.opsForHash().get(RedisUtil.getCounslorAntiCheatingKey(counsellorId), deviceToken);
            if (log.isDebugEnabled()) {
                log.info("辅导员点名:antiCheating--> counsellorId:" + counsellorId + ",stuId:" + stuId + ",studentId:" + studentId + ",deviceToken:" + deviceToken);
            }

            if (null == stuId) {
                stringRedisTemplate.opsForHash().put(RedisUtil.getCounslorAntiCheatingKey(counsellorId), deviceToken, String.valueOf(studentId));
            } else {
                Long stuIdL = Long.valueOf((String) stuId);
                if (log.isDebugEnabled()) {
                    log.info("辅导员点名:studentId:" + studentId + ",studIdL" + stuIdL);
                }
                if (!stuIdL.equals(studentId)) {
                    return false;
                }
            }
        } catch (Exception e) {
            log.warn("Exception", e);
        }
        return isCan;
    }

    public List<RollCallReportStudentDTO> querEverStudentDetail(Long studentId) {
        List<RollCallReportStudentDTO> list = rollCallEverQuery.queryEverStudentDetail(studentId);

        return list;
    }

    public boolean ModifyLeave(Long reportId) {
        int result = rollCallReportRepository.findreportIdModifyLeave(reportId);
        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }
    // 无用
    public void scheduleCloseRollCallEver() {
        rollCallEverRepository.closeRollCallEver();
    }

    public List<Long> getRollcalleverIdByTeacherId(List<Long> teacherIds, Date startDate, Date endDate) {
        if (teacherIds.size() > 0) {
            return rollCallEverRepository.findByTeacherIdIn(teacherIds, startDate, endDate);
        } else {
            return null;
        }

    }

    public List<Long> getIsHaveTeacherId(List<Long> teacherIds, Date startDate, Date endDate) {
        if (teacherIds.size() > 0) {
            return rollCallEverRepository.getIsHaveTeacherId(teacherIds, startDate, endDate);
        } else {
            return null;
        }

    }

    public List<RollCallReport> getRollCallReports(Long classId, Long rid) {
        return rollCallReportRepository.findAllByRollCallEverIdAndClassId(rid, classId);
    }

}
