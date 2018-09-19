package com.aizhixin.cloud.dd.counsellorollcall.v1.service;

import com.aizhixin.cloud.dd.common.core.ApiReturn;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.communication.dto.ReportDTO;
import com.aizhixin.cloud.dd.communication.utils.RedisKeyUtil;
import com.aizhixin.cloud.dd.constant.PushMessageConstants;
import com.aizhixin.cloud.dd.constant.RollCallConstants;
import com.aizhixin.cloud.dd.counsellorollcall.domain.*;
import com.aizhixin.cloud.dd.counsellorollcall.dto.StudentSignInDTO;
import com.aizhixin.cloud.dd.counsellorollcall.entity.*;
import com.aizhixin.cloud.dd.counsellorollcall.repository.AlarmClockRepository;
import com.aizhixin.cloud.dd.counsellorollcall.repository.StudentSignInRepository;
import com.aizhixin.cloud.dd.counsellorollcall.utils.CounsellorRollCallEnum;
import com.aizhixin.cloud.dd.messege.dto.AudienceDTO;
import com.aizhixin.cloud.dd.messege.dto.MessageDTO;
import com.aizhixin.cloud.dd.messege.service.MessageService;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.StudentDTO;
import com.aizhixin.cloud.dd.messege.entity.PushMessage;
import com.aizhixin.cloud.dd.rollcall.repository.PushMessageRepository;
import com.aizhixin.cloud.dd.rollcall.service.AttendanceStatisticsService;
import com.aizhixin.cloud.dd.messege.service.PushService;
import com.aizhixin.cloud.dd.rollcall.service.SemesterService;
import com.aizhixin.cloud.dd.rollcall.service.StudentService;
import com.aizhixin.cloud.dd.rollcall.utils.RedisUtil;
import com.alibaba.fastjson.JSON;
import com.netflix.discovery.util.StringUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by LIMH on 2017/11/30.
 */
@Service
@Transactional
public class StudentSignInService {

    private final Logger log = LoggerFactory.getLogger(StudentSignInService.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private StudentSignInRepository studentSignInRepository;

    @Autowired
    private CounsellorRollcallService counsellorRollcallService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private PushMessageRepository pushMessageRepository;

    @Autowired
    private PushService pushService;

    @Autowired
    private CounsellorRedisService counsellorRedisService;

    @Autowired
    private AlarmClockRepository clockRepository;

    @Autowired
    private MessageService messageService;

    /**
     * 查询StudentSignIn
     *
     * @param counsellorRollcall
     * @return
     */
    public List<StudentSignIn> findStudentSignInByCounsellorRollcall(CounsellorRollcall counsellorRollcall) {
        return studentSignInRepository.findAllByCounsellorRollcallOrderByStudentNum(counsellorRollcall);
    }

    /**
     * 保存
     *
     * @param conunsellorRollcall
     * @param studentIds
     * @param orgId
     */
//    @Async
    public void save(String accessToken, CounsellorRollcall conunsellorRollcall, Set<Long> studentIds, Long orgId, Integer rollcallNum, Map<Long, Boolean> leaveMap) {
        Long beginTime = System.currentTimeMillis();
        List<StudentDTO> studentDTOS = studentService.getStudentByIdsV2(studentIds);
        if (studentDTOS == null || studentDTOS.isEmpty()) {
            return;
        }
        boolean isV2 = false;
        if (rollcallNum != null && rollcallNum > 1) {
            isV2 = true;
        }
        Long semesterId = semesterService.getSemesterId(orgId);
        List<StudentSignIn> studentSignIns = new ArrayList<>();
        List<PushMessage> messages = new ArrayList<PushMessage>();
        List<Long> userIds = new ArrayList<Long>();
        for (StudentDTO st : studentDTOS) {
            String type = CounsellorRollCallEnum.UnCommit.getType();
            if(leaveMap != null && leaveMap.get(st.getStudentId())){
                type = CounsellorRollCallEnum.AskForLeave.getType();
            }
            if (isV2) {
                studentSignIns.add(new StudentSignIn(conunsellorRollcall, st.getStudentId(), st.getStudentName(), st.getSutdentNum(), st.getClassesId(), st.getClassesName(), st.getProfessionalId(), st.getCollegeId(), orgId, semesterId, type, type, st.getTeachingYear()));
            } else {
                studentSignIns.add(new StudentSignIn(conunsellorRollcall, st.getStudentId(), st.getStudentName(), st.getSutdentNum(), st.getClassesId(), st.getClassesName(), st.getProfessionalId(), st.getCollegeId(), orgId, semesterId, type, st.getTeachingYear()));
            }
            messages.add(new PushMessage(st.getStudentId(), "您有新的签到提醒！", "辅导员点名通知", PushMessageConstants.MODULE_RollCallEVER, PushMessageConstants.FUNCITON_STUDENT_NOTICE, Boolean.FALSE, new Date(), ""));
            userIds.add(st.getStudentId());
        }
        if (studentSignIns.size() > 0) {
            try {
                studentSignIns = studentSignInRepository.save(studentSignIns);
                //缓存到redis
                if (conunsellorRollcall.getTempGroup().getRollcallNum() == null || conunsellorRollcall.getTempGroup().getRollcallNum().intValue() < 2) {
                    counsellorRedisService.putCache(conunsellorRollcall, studentSignIns);
                }
                AlarmClock alarmClock = null;
                List<AlarmClock> clockList = clockRepository.findByTempGroupAndDeleteFlag(conunsellorRollcall.getTempGroup().getId(), DataValidity.VALID.getState());
                if (clockList != null && clockList.size() > 0) {
                    alarmClock = clockList.get(0);
                }
                List<StuTempGroupDomainV2> stuTempGroupDomainV2s = counsellorRedisService.putCacheV2(conunsellorRollcall, studentSignIns, isV2, alarmClock);
                pushMessageRepository.save(messages);
                pushService.listPush(accessToken, "您有一个新的签到提醒", "辅导员点名通知", "辅导员点名通知", userIds);
                //----新消息服务----start
                MessageDTO messageDTO = new MessageDTO();
                messageDTO.setTitle("辅导员点名通知");
                messageDTO.setContent("您有新的签到提醒！");
                messageDTO.setFunction(PushMessageConstants.FUNCITON_STUDENT_NOTICE);
                List<AudienceDTO> audienceList = new ArrayList<>();
                for(StuTempGroupDomainV2 item: stuTempGroupDomainV2s){
                    audienceList.add(new AudienceDTO(item.getStudentId(), item.getMessageId(), item));
                }
                messageDTO.setAudience(audienceList);
                messageService.push(messageDTO);
                //----新消息服务----end
                log.info("导员点名执行保存信息执行时间:" + (System.currentTimeMillis() - beginTime));
            } catch (Exception e) {
                log.warn("savestudentsigninerror:" + studentSignIns.toString());
                log.warn("savestudentsignin", e);
                throw e;
            }
        } else {
            log.warn("导员点名无保存信息");
        }
    }

    /**
     * 查询导员点名列表
     *
     * @param tempGroup
     * @return
     */
    public List<CouRollCallDomain> findConRollCallList(TempGroup tempGroup) {
        return studentSignInRepository.findConRollCallListNoSemesterId(tempGroup, DataValidity.VALID.getState());
    }

    public CouRollCallDomain findConRollcall(Long counsellorRollcallId) {
        CounsellorRollcall counsellorRollcall = counsellorRollcallService.findOne(counsellorRollcallId);
        if (null == counsellorRollcall) {
            return null;
        }
        return studentSignInRepository.findConRollCall(counsellorRollcall, DataValidity.VALID.getState());
    }

    /**
     * 查询导员点名明细
     *
     * @param couRollcallId
     * @param status
     */
    public Map<String, Object> findCouRollcallDetail(Long couRollcallId, String status) {
        CounsellorRollcall counsellorRollcall = counsellorRollcallService.findOne(couRollcallId);
        if (null == counsellorRollcall) {
            return ApiReturn.message(Boolean.FALSE, "不存在该点名信息。", null);
        }

        List list = new ArrayList();
        CouRollCallDomain couRollCallDomain = studentSignInRepository.findConRollCall(counsellorRollcall, DataValidity.VALID.getState());
        if (null == couRollCallDomain) {
            return ApiReturn.message(Boolean.FALSE, "不存在该点名信息。", null);
        }
        list.add(couRollCallDomain);

        List<StudentSignInDomain> studentSignInDomains = null;
        if (StringUtils.isBlank(status)) {
            studentSignInDomains = studentSignInRepository.findAllByCounsellorRollcallAndStautsAndDeleteFlagOrderByStudentNum(counsellorRollcall, DataValidity.VALID.getState());
        } else {
            studentSignInDomains
                    = studentSignInRepository.findAllByCounsellorRollcallAndStautsAndDeleteFlagOrderByStudentNum(counsellorRollcall, status, DataValidity.VALID.getState());
        }
        studentSignInDomains = counsellorRedisService.updateReadFromCache(studentSignInDomains);
        // 组装返回数据
        Map<String, List<StudentSignInDomain>> classMap = new TreeMap();
        Map<Long, List<StudentSignInDomain>> classIdMap = new TreeMap();
        for (StudentSignInDomain domain : studentSignInDomains) {
            if (null != domain.getClassId() && null != classIdMap.get(domain.getClassId())) {
                classIdMap.get(domain.getClassId()).add(domain);
            } else {
                List studentList = new ArrayList();
                studentList.add(domain);
                if (null != domain.getClassId()) {
                    classMap.put(domain.getClassName(), studentList);
                    classIdMap.put(domain.getClassId(), studentList);
                }
            }
        }

        for (Map.Entry<String, List<StudentSignInDomain>> entry : classMap.entrySet()) {
            Map map = new HashMap();
            map.put("className", entry.getKey());
            map.put("classList", entry.getValue());
            list.add(map);

        }
        return ApiReturn.message(Boolean.TRUE, null, list);
    }

    /**
     * 修改学生状态
     *
     * @param ids
     * @param status
     */
    public void updateStudetnStatus(Set<Long> ids, String status) {
        studentSignInRepository.updateStatusByIds(ids, status);
        //更新缓存
        try {
            if (ids != null && ids.size() > 0) {
                for (Long id : ids) {
                    if (id != null && id.longValue() > 0) {
                        StudentSignIn signIn = studentSignInRepository.findOne(id);
                        if (signIn != null && signIn.getCounsellorRollcall() != null && signIn.getCounsellorRollcall().getTempGroup() != null) {
                            counsellorRedisService.updateRollcallStatus(signIn.getStudentId(), signIn);
                            counsellorRedisService.updateRollcallStatusV2(signIn.getCounsellorRollcall().getTempGroup().getId(), signIn.getStudentId(), signIn);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Exception", e);
        }

    }

    /**
     * 学生签到列表
     *
     * @param studentId
     * @return
     */
    public List<RollcallReportDomain> listStudentSignIn(Long studentId) {
        List<RollcallReportDomain> rollcallReportDomains = studentSignInRepository.listStudentSignIn(studentId, DataValidity.VALID.getState());
        if (rollcallReportDomains == null || rollcallReportDomains.isEmpty()) {
            return new ArrayList<>();
        }
        RollcallReportDomain rollcallReportDomain = rollcallReportDomains.get(0);
        if (rollcallReportDomain != null) {
            if (!rollcallReportDomain.getHaveRead()) {
                studentSignInRepository.updateHaveRead(rollcallReportDomain.getId());
            }
        }
        return rollcallReportDomains;
    }

    /**
     * 学生签到列表
     *
     * @param studentId
     * @return
     */
    public List<RollcallReportDomain> getStudentSignInList(Long studentId) {
        List<RollcallReportDomain> rollcallReportDomains = studentSignInRepository.listStudentSignIn(studentId, DataValidity.VALID.getState());
        if (rollcallReportDomains == null || rollcallReportDomains.isEmpty()) {
            return new ArrayList<>();
        }
        return rollcallReportDomains;
    }

    /**
     * 从缓存读取，没有数据再从数据库读取
     *
     * @param studentId
     * @return
     */
    public List<RollcallReportDomain> listStudentSignInCache(Long studentId) {
        List<RollcallReportDomain> rollcallReportDomains = counsellorRedisService.getRollcallReportsByStuId(studentId);
        if (rollcallReportDomains == null || rollcallReportDomains.isEmpty()) {
            //没有数据，读取数据库
            rollcallReportDomains = listStudentSignIn(studentId);
            if (rollcallReportDomains != null && rollcallReportDomains.size() > 0) {
                for (RollcallReportDomain item : rollcallReportDomains) {
                    if (!item.getHaveRead()) {
                        item.setHaveRead(true);
                    } else {
                        break;
                    }
                }
                counsellorRedisService.pushCache(studentId, rollcallReportDomains);
            }
        }
        return rollcallReportDomains;
    }

    /**
     * 辅导员点名学生签到
     *
     * @param reportDTO
     * @return
     */
    public Map<String, Object> signIn(AccountDTO accountDTO, ReportDTO reportDTO) {
        try {
            Map<String, Object> result = new HashMap<>();
            Long counsellorRollCallId = counsellorRedisService.readLongReidsData(reportDTO.getId());
            if (null == counsellorRollCallId || counsellorRollCallId == 0L) {
                log.info("缓存为空，从数据库获取签到信息再刷新缓存----------");
                StudentSignIn studentSignIn = studentSignInRepository.findOne(reportDTO.getId());
                if (studentSignIn == null || studentSignIn.getCounsellorRollcall() == null) {
                   return ApiReturn.message(Boolean.FALSE, "获取信息异常", null);
                }
                counsellorRollCallId = studentSignIn.getCounsellorRollcall().getId();
            }
            if (accountDTO.getAntiCheating()) {
                if (StringUtils.isNotBlank(reportDTO.getDeviceToken())) {
                    if (!signAntiCheating(counsellorRollCallId, accountDTO.getId(), reportDTO.getDeviceToken())) {
                        return ApiReturn.message(Boolean.FALSE, RollCallConstants.ROLL_CALL_WARNING_MESSAGE, null);
                    }
                }
            }
            StudentSignIn studentSignIn = new StudentSignIn();
            //判断几次签到
            Long groupId = counsellorRedisService.getGroupId(counsellorRollCallId);
            if (groupId == null) {
                CounsellorRollcall rollcall = counsellorRollcallService.findOne(counsellorRollCallId);
                if (rollcall != null) {
                    groupId = rollcall.getTempGroup().getId();
                }
            }
            log.debug("groupIdV2:" + groupId);

            CounsellorRollcallRule rule = null;
            if (groupId != null && groupId.longValue() > 0) {
                rule = counsellorRedisService.getRollcallRule(groupId);
            } else {
                return ApiReturn.message(Boolean.FALSE, "无点名信息", null);
            }
            if (rule != null) {
                //判断签到时间是几次签到
                String siginTimeStr = formatTime(new Date());
                long siginTime = getTime(siginTimeStr).getTime();

                Date startTime = getTime(rule.getStartTime());
                String lateStr = "0";
                if (rule.getLateTime() != null) {
                    lateStr = rule.getLateTime().toString();
                }
                long lateTime = Long.parseLong(lateStr);
                lateTime = startTime.getTime() + lateTime * 60 * 1000;

                Date endTime = getTime(rule.getEndTime());
                String endFlexStr = "0";
                if (rule.getEndFlexTime() != null) {
                    endFlexStr = rule.getEndFlexTime().toString();
                }
                long secondTime = Long.parseLong(endFlexStr);
                secondTime = endTime.getTime() - secondTime * 60 * 1000;

                String stopStr = "0";
                if (rule.getStopTime() != null) {
                    stopStr = rule.getStopTime().toString();
                }
                long stopTime = Long.parseLong(stopStr);
                stopTime = endTime.getTime() + stopTime * 60 * 1000;

                if (siginTime > lateTime && siginTime < secondTime) {
                    //第一次迟到
                    List<StuTempGroupDomainV2> rollcallReportDomains = counsellorRedisService.getRollcallReportsByStuIdV2(accountDTO.getId());
                    boolean isSigned = false;
                    if (rollcallReportDomains != null && rollcallReportDomains.size() > 0) {
                        for (StuTempGroupDomainV2 group : rollcallReportDomains) {
                            if (group.getGroupId().longValue() == groupId.longValue()) {
                                List<StuRollcallReportDomainV2> rl = group.getReportList();
                                if (rl != null && rl.size() > 0) {
                                    for (StuRollcallReportDomainV2 ri : rl) {
                                        if (ri != null && ri.getCounsellorId() != null && ri.getCounsellorId().longValue() == counsellorRollCallId.longValue()) {
                                            if (StringUtils.isNotEmpty(ri.getStatus()) && (ri.getStatus().equals(CounsellorRollCallEnum.HavaTo.getType()) || ri.getStatus().equals(CounsellorRollCallEnum.Late.getType()) || ri.getStatus().equals(CounsellorRollCallEnum.AskForLeave.getType()))) {
                                                isSigned = true;
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!isSigned) {
                        setStudentSignIn(1, CounsellorRollCallEnum.Late.getType(), studentSignIn, reportDTO);
                        result.put("type", 1);
                        result.put("status", CounsellorRollCallEnum.Late.getType());
                        result.put("gpsDetail", studentSignIn.getGpsDetail());
                        result.put("gpsLocation", studentSignIn.getGpsLocation());
                        result.put("signTime", DateFormatUtil.format(studentSignIn.getSignTime(), DateFormatUtil.FORMAT_SHORT_MINUTE));
                    } else {
                        System.out.println("第一次已经签到");
                        studentSignIn = null;
                    }
                } else if (siginTime >= secondTime && siginTime <= stopTime) {
                    //第二次已到
                    List<StuTempGroupDomainV2> rollcallReportDomains = counsellorRedisService.getRollcallReportsByStuIdV2(accountDTO.getId());
                    boolean isSigned = false;
                    if (rollcallReportDomains != null && rollcallReportDomains.size() > 0) {
                        for (StuTempGroupDomainV2 group : rollcallReportDomains) {
                            if (group.getGroupId().longValue() == groupId.longValue()) {
                                List<StuRollcallReportDomainV2> rl = group.getReportList();
                                if (rl != null && rl.size() > 0) {
                                    for (StuRollcallReportDomainV2 ri : rl) {
                                        if (ri != null && ri.getCounsellorId() != null && ri.getCounsellorId().longValue() == counsellorRollCallId.longValue()) {
                                            if (StringUtils.isNotEmpty(ri.getStatus2()) && (ri.getStatus2().equals(CounsellorRollCallEnum.HavaTo.getType()) || ri.getStatus2().equals(CounsellorRollCallEnum.Late.getType()) || ri.getStatus2().equals(CounsellorRollCallEnum.AskForLeave.getType()))) {
                                                isSigned = true;
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!isSigned) {
                        setStudentSignIn(2, CounsellorRollCallEnum.HavaTo.getType(), studentSignIn, reportDTO);
                        result.put("type", 2);
                        result.put("status", CounsellorRollCallEnum.HavaTo.getType());
                        result.put("gpsDetail", studentSignIn.getGpsDetail2());
                        result.put("gpsLocation", studentSignIn.getGpsLocation2());
                        result.put("signTime", DateFormatUtil.format(studentSignIn.getSignTime2(), DateFormatUtil.FORMAT_SHORT_MINUTE));
                    } else {
                        System.out.println("第二次已经签到");
                        studentSignIn = null;
                    }
                } else if (siginTime > stopTime) {
                    System.out.println("已停止考勤");
                    studentSignIn = null;
                } else {
                    //第一次已到
                    List<StuTempGroupDomainV2> rollcallReportDomains = counsellorRedisService.getRollcallReportsByStuIdV2(accountDTO.getId());
                    boolean isSigned = false;
                    if (rollcallReportDomains != null && rollcallReportDomains.size() > 0) {
                        for (StuTempGroupDomainV2 group : rollcallReportDomains) {
                            if (group.getGroupId().longValue() == groupId.longValue()) {
                                List<StuRollcallReportDomainV2> rl = group.getReportList();
                                if (rl != null && rl.size() > 0) {
                                    for (StuRollcallReportDomainV2 ri : rl) {
                                        if (ri != null && ri.getCounsellorId() != null && ri.getCounsellorId().longValue() == counsellorRollCallId.longValue()) {
                                            if (StringUtils.isNotEmpty(ri.getStatus()) && (ri.getStatus().equals(CounsellorRollCallEnum.HavaTo.getType()) || ri.getStatus().equals(CounsellorRollCallEnum.Late.getType()) || ri.getStatus().equals(CounsellorRollCallEnum.AskForLeave.getType()))) {
                                                isSigned = true;
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!isSigned) {
                        setStudentSignIn(1, CounsellorRollCallEnum.HavaTo.getType(), studentSignIn, reportDTO);
                        result.put("type", 1);
                        result.put("status", CounsellorRollCallEnum.HavaTo.getType());
                        result.put("gpsDetail", studentSignIn.getGpsDetail());
                        result.put("gpsLocation", studentSignIn.getGpsLocation());
                        result.put("signTime", DateFormatUtil.format(studentSignIn.getSignTime(), DateFormatUtil.FORMAT_SHORT_MINUTE));
                    } else {
                        System.out.println("第一次已经签到");
                        studentSignIn = null;
                    }
                }
            } else {
                //第一次已到
                setStudentSignIn(1, CounsellorRollCallEnum.HavaTo.getType(), studentSignIn, reportDTO);
                result.put("type", 1);
                result.put("status", CounsellorRollCallEnum.HavaTo.getType());
                result.put("gpsDetail", studentSignIn.getGpsDetail());
                result.put("gpsLocation", studentSignIn.getGpsLocation());
                result.put("signTime", DateFormatUtil.format(studentSignIn.getSignTime(), DateFormatUtil.FORMAT_SHORT_MINUTE));
            }
            if (studentSignIn != null) {
                String rollCallData = JSON.toJSONString(studentSignIn);
                counsellorRedisService.updateRollcallStatus(accountDTO.getId(), studentSignIn);
                counsellorRedisService.updateRollcallStatusV2(groupId, accountDTO.getId(), studentSignIn);
                stringRedisTemplate.opsForList().rightPush(RedisKeyUtil.STUSIGNINKEY, rollCallData);
            }
            return ApiReturn.message(Boolean.TRUE, null, result);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn(reportDTO.toString());
            log.warn("siginException" + e.getMessage(), e);
            return ApiReturn.message(Boolean.FALSE, null, null);
        }
    }

    private void setStudentSignIn(Integer type, String result, StudentSignIn studentSignIn, ReportDTO reportDTO) {
        studentSignIn.setId(reportDTO.getId());
        if (type == 2) {
            studentSignIn.setGpsDetail2(reportDTO.getGpsDetail());
            studentSignIn.setGpsLocation2(reportDTO.getGpsLocaltion());
            studentSignIn.setGpsType2(reportDTO.getGpsType());
            studentSignIn.setHaveReport2(1);
            studentSignIn.setHaveRead2(true);
            studentSignIn.setDeviceToken2(reportDTO.getDeviceToken());
            studentSignIn.setSignTime2(new Timestamp(System.currentTimeMillis()));
            studentSignIn.setStatus2(result);
        } else {
            studentSignIn.setGpsDetail(reportDTO.getGpsDetail());
            studentSignIn.setGpsLocation(reportDTO.getGpsLocaltion());
            studentSignIn.setGpsType(reportDTO.getGpsType());
            studentSignIn.setHaveReport(1);
            studentSignIn.setHaveRead(true);
            studentSignIn.setDeviceToken(reportDTO.getDeviceToken());
            studentSignIn.setSignTime(new Timestamp(System.currentTimeMillis()));
            studentSignIn.setStatus(result);
        }
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

    public Map<String, Object> findAllByCounsellorRollcallAndStatusAndHaveRead(CounsellorRollcall counsellorRollcall, Integer status, Boolean haveRead) {
        List<StudentSignIn> signInList = null;
        Double longitude = 0.0;// 经度
        Double latitude = 0.0;// 纬度
        int count = 0;
        if (status != null && haveRead != null) {
            signInList = studentSignInRepository.findAllByCounsellorRollcallAndStatusAndHaveReadOrderByStudentNum(counsellorRollcall, status, haveRead);
        } else if (status == null && haveRead != null) {
            signInList = studentSignInRepository.findAllByCounsellorRollcallAndHaveReadOrderByStudentNum(counsellorRollcall, haveRead);
        } else if (status != null && haveRead == null) {
            signInList = studentSignInRepository.findAllByCounsellorRollcallAndStatusOrderByStudentNum(counsellorRollcall, status);
        } else {
            signInList = studentSignInRepository.findAllByCounsellorRollcallOrderByStudentNum(counsellorRollcall);
        }
        List<StudentSignInDTO> list = new ArrayList<>();
        try {
            if (signInList != null) {
                StudentSignInDTO dto = null;
                for (StudentSignIn studentSignIn : signInList) {
                    dto = new StudentSignInDTO();
                    try {
                        dto.setRid(studentSignIn.getCounsellorRollcall().getId());
                        BeanUtils.copyProperties(dto, studentSignIn);
                        dto.setSignTime(DateFormatUtil.format(studentSignIn.getSignTime()));
                        dto.setStatus(CounsellorRollCallEnum.getNameByType(studentSignIn.getStatus()));

                        if (null != studentSignIn.getGpsLocation()) {
                            String[] no = studentSignIn.getGpsLocation().split("-");
                            if (no.length == 2) {
                                List<Double> ll = new ArrayList<>();
                                ll.add(Double.valueOf(no[1]));
                                ll.add(Double.valueOf(no[0]));
                                dto.setLltudes(ll);
                                longitude = longitude + Double.valueOf(no[1]);
                                latitude = latitude + Double.valueOf(no[0]);
                                count++;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Object> message = ApiReturn.message(Boolean.TRUE, null, list);
        List<Double> center = new ArrayList<>();
        if (count > 0) {
            center.add(longitude / count);
            center.add(latitude / count);
        }
        message.put("center", center);
        return message;
    }

    /**
     * 学生导员点名签到防作弊校验
     *
     * @param counsellorId
     * @param studentId
     * @param deviceToken
     * @return
     */
    public boolean signAntiCheating(Long counsellorId, Long studentId, String deviceToken) {
        boolean isCan = true;
        deviceToken = StringUtils.isNotBlank(deviceToken) ? deviceToken.trim() : null;
        try {
            Object stuId = stringRedisTemplate.opsForHash().get(RedisUtil.getCounslorAntiCheatingKey(counsellorId), deviceToken);
            if (log.isDebugEnabled()) {
                log.debug("辅导员点名:antiCheating--> counsellorId:" + counsellorId + ",stuId:" + stuId + ",studentId:" + studentId + ",deviceToken:" + deviceToken);
            }

            if (null == stuId) {
                stringRedisTemplate.opsForHash().put(RedisUtil.getCounslorAntiCheatingKey(counsellorId), deviceToken, String.valueOf(studentId));
            } else {
                Long stuIdL = Long.valueOf((String) stuId);
                if (log.isDebugEnabled()) {
                    log.debug("辅导员点名:studentId:" + studentId + ",studIdL" + stuIdL);
                }
                if (!stuIdL.equals(studentId)) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isCan;
    }
}
