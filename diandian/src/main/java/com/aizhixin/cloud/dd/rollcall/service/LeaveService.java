package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.constant.LeaveConstants;
import com.aizhixin.cloud.dd.constant.PushMessageConstants;
import com.aizhixin.cloud.dd.constant.RollCallConstants;
import com.aizhixin.cloud.dd.messege.dto.AudienceDTO;
import com.aizhixin.cloud.dd.messege.service.MessageService;
import com.aizhixin.cloud.dd.messege.service.PushMessageService;
import com.aizhixin.cloud.dd.messege.service.PushService;
import com.aizhixin.cloud.dd.orgStructure.entity.TeachingClassStudent;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.repository.TeachingClassStudentRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.*;
import com.aizhixin.cloud.dd.rollcall.entity.*;
import com.aizhixin.cloud.dd.rollcall.repository.*;
import com.aizhixin.cloud.dd.rollcall.utils.CourseUtils;
import com.aizhixin.cloud.dd.rollcall.utils.IOUtil;
import com.aizhixin.cloud.dd.rollcall.utils.RedisUtil;
import com.google.common.collect.Sets;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.*;

@Service
@Transactional
public class LeaveService {
    private final Logger log = LoggerFactory.getLogger(LeaveService.class);

    @Autowired
    private LeaveRepository leaveRepository;
    @Autowired
    private PushMessageService pushMessageService;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;
    @Autowired
    private PushService pushService;
    @Autowired
    private StudentLeaveScheduleService studentLeaveScheduleService;
    @Autowired
    private StudentLeaveScheduleRepository studentLeaveScheduleRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private ScheduleRollCallRepository scheduleRollCallRepository;
    @Autowired
    private ScheduleRollCallService scheduleRollCallService;
    @Autowired
    private RollCallRepository rollCallRepository;
    @Autowired
    private LeaveRequestQuery leaveRequestQuery;
    @Autowired
    private UserService userService;
    @Autowired
    private InitScheduleService initScheduleService;
    @Autowired
    private PeriodService periodService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IOUtil ioUtil = new IOUtil();
    @Autowired
    private MessageService messageService;
    @Autowired
    private RollCallStatsService rollCallStatsService;
    @Autowired
    private TeachingClassStudentRepository teachingClassStudentRepository;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteClient;
    @Autowired
    private UserInfoRepository userInfoRepository;

    public Object requestLeave(AccountDTO account, Boolean isLeaveSchoole, String requestType, Long startPeriodId, Long endPeriodId, Date startDay, Date endDay, String content,
                               Long headTeacherId, String headTeacherName, String accessToken, MultipartFile[] files) {
        String leavePictureUrls = "";
        try {
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    MultipartFile file = files[i];
                    IODTO ioDTO = ioUtil.upload(file.getOriginalFilename(), (byte[]) file.getBytes());
                    leavePictureUrls += ioDTO.getFileUrl();
                    if (i != files.length - 1) {
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
        Map<String, Object> periodMap = orgManagerRemoteClient.listPeriod(account.getOrganId(), 0, 1000);
        Date startTime = DateFormatUtil.parse2(DateFormatUtil.formatShort(startDay) + getStartPeriodTime((List<Map<String, Object>>) periodMap.get("data")), "yyyy-MM-dd HH:mm");
        Date endTime = null;
        if (requestType.equals(LeaveConstants.TYPE_DAY)) {
            endTime = DateFormatUtil.parse2(DateFormatUtil.formatShort(endDay) + getEndPeriodTime((List<Map<String, Object>>) periodMap.get("data")), "yyyy-MM-dd HH:mm");
        } else {
            endTime = DateFormatUtil.parse2(DateFormatUtil.formatShort(startDay) + getEndPeriodTime((List<Map<String, Object>>) periodMap.get("data")), "yyyy-MM-dd HH:mm");
        }
        String duration = getDuration(endTime, startTime);
        UserInfo stu = userInfoRepository.findByUserId(account.getId());
        // 由班主任照常请
        if (headTeacherId != null) {
            UserInfo user = userInfoRepository.findByUserId(headTeacherId);
            if (requestType.equals(LeaveConstants.TYPE_DAY)) {
                Leave l = new Leave();
                l.setHeadTeacherId(headTeacherId);
                l.setTeacherName(headTeacherName);
                l.setRequestContent(content);
                l.setRequestType(requestType);
                l.setStartDate(startDay);
                l.setEndDate(endDay);
                l.setStartTime(startTime);
                l.setEndTime(endTime);
                l.setDuration(duration);
                l.setLeaveType(LeaveConstants.TYPE_PR_SJ);
                l.setLeavePublic(LeaveConstants.TYPE_PR);
                l.setStatus(LeaveConstants.STATUS_REQUEST);
                l.setStudentId(account.getId());
                l.setStudentName(account.getName());
                l.setStudentJobNum(stu.getJobNum());
                l.setOrgId(stu.getOrgId());
                if (user != null) {
                    l.setTeacherJobNum(user.getJobNum());
                }
                l.setClassName(stu.getClassesName());
                l.setLeaveSchool(isLeaveSchoole);
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
                l.setLeaveType(LeaveConstants.TYPE_PR_SJ);
                l.setLeavePublic(LeaveConstants.TYPE_PR);
                l.setStatus(LeaveConstants.STATUS_REQUEST);
                l.setLeaveSchool(isLeaveSchoole);
                l.setLeavePictureUrls(leavePictureUrls);
                l.setStudentId(account.getId());
                l.setStudentName(account.getName());
                l.setStudentJobNum(stu.getJobNum());
                l.setOrgId(stu.getOrgId());
                if (user != null) {
                    l.setTeacherJobNum(user.getJobNum());
                }
                l.setClassName(stu.getClassesName());
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
                    List<DianDianDaySchoolTimeTableDomain> ddt = orgManagerRemoteService.getStudentDaySchoolTimeTable(account.getId(), DateFormatUtil.formatShort(schoolDay));
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
                            l.setLeaveSchool(isLeaveSchoole);
                            l.setStudentId(account.getId());
                            l.setStudentName(account.getName());
                            l.setStudentJobNum(stu.getJobNum());
                            l.setOrgId(stu.getOrgId());
                            if (user != null) {
                                l.setTeacherJobNum(user.getJobNum());
                            }
                            l.setClassName(stu.getClassesName());
                            l.setStartPeriodId(ddds.getPeriodId());
                            l.setStartTime(startTime);
                            l.setEndTime(endTime);
                            l.setDuration(duration);
                            l.setLeaveType(LeaveConstants.TYPE_PR_SJ);
                            l.setLeavePublic(LeaveConstants.TYPE_PR);
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
                String teachTime = DateFormatUtil.format(startDay);
                // 课程节id;
                Map map = getBetweenStartAndEndPeriodId(account.getOrganId(), startPeriodId, endPeriodId);
                List<DianDianDaySchoolTimeTableDomain> ddt = orgManagerRemoteService.getStudentDaySchoolTimeTable(account.getId(), null);
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
                            l.setOrgId(stu.getOrgId());
                            if (user != null) {
                                l.setTeacherJobNum(user.getJobNum());
                            }
                            l.setClassName(stu.getClassesName());
                            l.setLeaveSchool(isLeaveSchoole);
                            l.setStartDate(ddds.getTeachDate());
                            l.setStartTime(startTime);
                            l.setEndTime(endTime);
                            l.setDuration(duration);
                            l.setLeaveType(LeaveConstants.TYPE_PR_SJ);
                            l.setLeavePublic(LeaveConstants.TYPE_PR);
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
        if (endDate != null && startDate != null) {
            long nd = 1000 * 24 * 60 * 60;
            long nh = 1000 * 60 * 60;
//        long nm = 1000 * 60;
            long diff = endDate.getTime() - startDate.getTime();
            long day = diff / nd;
            long hour = diff % nd / nh;
//        long min = diff % nd % nh / nm;
            return day + "天" + hour + "小时";// + min + "分钟";
        }
        return null;
    }

    private String getStartPeriodTime(List<Map<String, Object>> list) {
        if (list != null && list.size() > 0) {
            return list.get(0).get("startTime").toString();
        }
        return null;
    }

    private String getEndPeriodTime(List<Map<String, Object>> list) {
        if (list != null && list.size() > 0) {
            Collections.reverse(list);
            return list.get(0).get("endTime").toString();
        }
        return null;
    }

    /**
     * 取消请假
     *
     * @param leaveId
     * @param studentId
     * @return
     */

    public Object cancleLeaveRequest(Long leaveId, Long studentId) {
        Leave leave = leaveRepository.findOne(leaveId);
        if (leave == null) {
            Map<String, Object> res = new HashMap<>();
            res.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            res.put(ApiReturnConstants.MESSAGE, "leave request not exist!");
            return res;
        }
        if (!leave.getStudentId().equals(studentId)) {
            Map<String, Object> res = new HashMap<>();
            res.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            res.put(ApiReturnConstants.MESSAGE, "this one is other perple request!");
            return res;
        }
        if (!leave.getStatus().equals(LeaveConstants.STATUS_REQUEST)) {
            Map<String, Object> res = new HashMap<>();
            res.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            res.put(ApiReturnConstants.MESSAGE, "this request is " + leave.getStatus());
            return res;
        }

        List<Leave> leavList = leaveRepository.findAllByStudentIdAndStartDateAndEndDateAndStartPeriodIdAndEndPeriodIdAndRequestContentAndRequestType(leave.getStudentId(),
                leave.getStartDate(), leave.getEndDate(), leave.getStartPeriodId(), leave.getEndPeriodId(), leave.getRequestContent(), leave.getRequestType());
        if (leavList != null && leavList.size() > 0) {
            for (Leave leave1 : leavList) {
                leave1.setStatus(LeaveConstants.STATUS_CANCLE);
            }
        } else {
            leave.setStatus(LeaveConstants.STATUS_PASS);
            leaveRepository.save(leave);
        }
        leaveRepository.save(leavList);

        Map<String, Object> res = new HashMap<>();
        res.put(ApiReturnConstants.MESSAGE, ApiReturnConstants.SUCCESS);
        res.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        return res;
    }

    /**
     * 获取请假列表
     *
     * @param studentId
     * @param status
     * @param organId
     * @param orderByKey
     * @param orderBy
     * @return
     */
    public Object getLeaveRequsetByStudentAndStatus(Long studentId, String status, Long organId, String orderByKey, String orderBy) {
        List<StudentForLeaveDTO> leaveList = new ArrayList();

        List<Leave> leaves = null;
        if (LeaveConstants.STATUS_PROCESSED.equals(status)) {
            leaves = leaveRepository.findAllByStudentIdAndDeleteFlagAndStatusInOrderByLastModifiedDateDesc(studentId, DataValidity.VALID.getState(), Sets.newHashSet(LeaveConstants.STATUS_REJECT, LeaveConstants.STATUS_PASS));
        } else {
            leaves = leaveRepository.findAllByStudentIdAndStatusAndDeleteFlag(new Sort(new Order(Direction.DESC, "lastModifiedDate")), studentId, status, DataValidity.VALID.getState());
        }

        if (null != leaves && leaves.size() > 0) {
            List<Leave> comparteList = new ArrayList<>();
            boolean flag = false;
            for (Leave l : leaves) {
                for (Leave leave : comparteList) {
                    flag = false;
                    if (DateFormatUtil.format(l.getCreatedDate(), "yyyy-MM-dd HH").equals(DateFormatUtil.format(leave.getCreatedDate(), "yyyy-MM-dd HH"))
                            && DateFormatUtil.formatShort(l.getStartDate()).equals(DateFormatUtil.formatShort(leave.getStartDate()))
                            && l.getRequestType().equals(leave.getRequestType())) {
                        if ("period".equals(l.getRequestType()) && l.getStartPeriodId().equals(leave.getStartPeriodId()) && l.getEndPeriodId().equals(leave.getEndPeriodId())) {
                            flag = true;
                            break;
                        } else if ("day".equals(l.getRequestType()) && DateFormatUtil.formatShort(l.getEndDate()).equals(DateFormatUtil.formatShort(leave.getEndDate()))) {
                            flag = true;
                            break;
                        }
                    }
                }
                if (flag) {
                    continue;
                }
                StudentForLeaveDTO sfl = new StudentForLeaveDTO();
                sfl.setLeaveId(l.getId());
                sfl.setCreatedDate(DateFormatUtil.format(l.getCreatedDate(), DateFormatUtil.FORMAT_MINUTE));
                sfl.setLastModifyDate(DateFormatUtil.format(l.getLastModifiedDate()));

                String startDate = DateFormatUtil.formatShort(l.getStartDate());
                String endDate = DateFormatUtil.formatShort(l.getEndDate());
                sfl.setStartDate(startDate);
                if (startDate.equals(endDate)) {
                    endDate = "";
                }
                sfl.setEndDate(endDate);
                sfl.setStartTime(DateFormatUtil.format(l.getStartTime(), DateFormatUtil.FORMAT_MINUTE));
                sfl.setEndTime(DateFormatUtil.format(l.getEndTime(), DateFormatUtil.FORMAT_MINUTE));
                sfl.setLeavePublic(l.getLeavePublic());
                sfl.setLeaveType(l.getLeaveType());
                sfl.setDuration(l.getDuration());
                sfl.setStatus(l.getStatus());
                sfl.setLeaveSchool(l.getLeaveSchool());
                sfl.setRequestContent(null == l.getRequestContent() ? "" : l.getRequestContent());
                sfl.setRejectContent(l.getRejectContent());
                sfl.setRequestType(l.getRequestType());
                sfl.setTeacherId(l.getHeadTeacherId());
                sfl.setTeacherName(l.getTeacherName());
                sfl.setStartPeriodId(null == l.getStartPeriodId() ? 0L : l.getStartPeriodId());
                sfl.setEndPeriodId(null == l.getEndPeriodId() ? 0L : l.getEndPeriodId());
                sfl.setLeavePictureUrls(l.getLeavePictureUrls());
                comparteList.add(l);
                leaveList.add(sfl);
            }
        }
        pushMessageService.readMessage(PushMessageConstants.MODULE_LEAVE, PushMessageConstants.FUNCTION_STUDENT_NOTICE, studentId);
        return leaveList;
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

    /**
     * 批准学生请假
     *
     * @param leaveId
     * @param account
     * @param accessToken
     * @return
     */
    public Object passLeaveRequest(Long leaveId, AccountDTO account, String accessToken) throws Exception {
        try {
            Leave leave = leaveRepository.findOne(leaveId);
            if (leave == null) {
                Map<String, Object> res = new HashMap<>();
                res.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                res.put(ApiReturnConstants.MESSAGE, "leave request not exist!");
                return res;
            }
            if (!leave.getHeadTeacherId().equals(account.getId())) {
                Map<String, Object> res = new HashMap<>();
                res.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                res.put(ApiReturnConstants.MESSAGE, "error creator!");
                return res;
            }
            if (!leave.getStatus().equals(LeaveConstants.STATUS_REQUEST)) {
                Map<String, Object> res = new HashMap<>();
                res.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                res.put(ApiReturnConstants.MESSAGE, "this request is " + leave.getStatus());
                return res;
            }

            List<Leave> leavList = leaveRepository.findAllByStudentIdAndStartDateAndEndDateAndStartPeriodIdAndEndPeriodIdAndRequestContentAndRequestType(leave.getStudentId(),
                    leave.getStartDate(), leave.getEndDate(), leave.getStartPeriodId(), leave.getEndPeriodId(), leave.getRequestContent(), leave.getRequestType());
            if (leavList != null && leavList.size() > 0) {
                for (Leave leave1 : leavList) {
                    leave1.setStatus(LeaveConstants.STATUS_PASS);
                    if (leave1.getHeadTeacherId().longValue() != account.getId().longValue()) {
                        leave1.setDeleteFlag(DataValidity.INVALID.getState());
                    }
                }
                /**
                 * 处理学生排课请假信息
                 */
                leaveRepository.save(leavList);
            } else {
                leave.setStatus(LeaveConstants.STATUS_PASS);
                leaveRepository.save(leave);
            }

            Set<Long> ids = new HashSet<>();
            if (leave.getRequestType().equals(LeaveConstants.TYPE_DAY)) {
                List<Date> manyDate = DateFormatUtil.getMonthBetweenDate(leave.getStartTime(), leave.getEndTime());
                for (Date schoolDay : manyDate) {
                    if (!DateFormatUtil.compareDate(schoolDay, DateFormatUtil.parse((DateFormatUtil.formatShort(new Date()) + " 23:59:59"), DateFormatUtil.FORMAT_LONG))) {
                        // 从平台获取排课
                        // 根据日期从平台获取排课数据
                        List<DianDianDaySchoolTimeTableDomain> ddt = orgManagerRemoteService.getStudentDaySchoolTimeTable(leave.getStudentId(), DateFormatUtil.format(schoolDay, DateFormatUtil.FORMAT_SHORT));
                        for (DianDianDaySchoolTimeTableDomain dto : ddt) {
                            Long tempTeacherId = send(leave.getStudentId(), InitScheduleService.parseTeacher(dto.getTeachers()).getId(), dto.getCourseId(), dto.getTeachDate(), dto.getPeriodId(), null, leave, account.getId());
                            if (null != tempTeacherId) {
                                ids.add(tempTeacherId);
                            }
                        }
                    } else {
                        // 直接修改本地数据库排课信息
                        Set<Long> teachingclassIds = orgManagerRemoteService.getStudentDayTeachingClassId(leave.getStudentId(), DateFormatUtil.format(schoolDay, DateFormatUtil.FORMAT_SHORT));
                        // 获取当天该学生的排课信息
                        List<Schedule> scheduleList = scheduleRepository.findByTeachDateAndDeleteFlagAndTeachingclassIdIn(DateFormatUtil.format(schoolDay, DateFormatUtil.FORMAT_SHORT), DataValidity.VALID.getState(), teachingclassIds);
                        // 获取该学生的某一天的排课，修改其签到记录。 对于未点名的课程，需要实时获取请假列表查看。
                        Set<Long> scheduleRollCallIds = new HashSet();
                        for (Schedule schedule : scheduleList) {
                            ScheduleRollCall scheduleRollCall = scheduleRollCallService.findBySchedule(schedule.getId());
                            if (null != scheduleRollCall && scheduleRollCall.getIsOpenRollcall()) {
                                scheduleRollCallIds.add(scheduleRollCall.getId());
                                boolean inClass = scheduleRollCall.getIsInClassroom();
                                if (inClass) {
                                    // 课堂内，需要去redis库中修改签到状态。
                                    RollCall rollCall = (RollCall) redisTemplate.opsForHash().get(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()), leave.getStudentId());
                                    if (null != rollCall) {
                                        rollCall.setLastType(rollCall.getType());
                                        if (leave.getLeavePublic() == LeaveConstants.TYPE_PU) {
                                            rollCall.setDeleteFlag(DataValidity.INVALID.getState());
                                            redisTemplate.opsForHash().delete(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()), rollCall.getStudentId());
                                        } else {
                                            rollCall.setType(RollCallConstants.TYPE_ASK_FOR_LEAVE);
                                            redisTemplate.opsForHash().put(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()), rollCall.getStudentId(), rollCall);
                                        }
                                    }
                                }
                            }
                            Long tempTeacherId = null;
                            try {
                                tempTeacherId = send(leave.getStudentId(), schedule.getTeacherId(), schedule.getCourseId(), DateFormatUtil.parse(schedule.getTeachDate(), DateFormatUtil.FORMAT_SHORT), schedule.getPeriodId(), schedule, leave, account.getId());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (null != tempTeacherId) {
                                ids.add(tempTeacherId);
                            }
                        }
                        // 修改学生的签到状态
                        if (null != scheduleRollCallIds && scheduleRollCallIds.size() > 0) {
                            if (leave.getLeavePublic() == LeaveConstants.TYPE_PU) {
                                rollCallRepository.deleteByStudentIdAndScheduleRollcallIdIn(leave.getStudentId(), scheduleRollCallIds);
                            } else {
                                rollCallRepository.updateRollCallByStudentIdAndScheduleRollCall(RollCallConstants.TYPE_ASK_FOR_LEAVE, leave.getStudentId(), scheduleRollCallIds);
                            }
                        }
                    }
                }
            } else if (leave.getRequestType().equals(LeaveConstants.TYPE_PERIOD)) {
                Map map = getBetweenStartAndEndPeriodId(account.getOrganId(), leave.getStartPeriodId(), leave.getEndPeriodId());
                // 当天以后的请假
                if (!DateFormatUtil.compareDate(leave.getStartDate(), new Date())) {
                    // 根据日期从平台获取排课数据
                    List<DianDianDaySchoolTimeTableDomain> ddt = orgManagerRemoteService.getStudentDaySchoolTimeTable(leave.getStudentId(), DateFormatUtil.format(leave.getStartDate(), DateFormatUtil.FORMAT_SHORT));
                    for (DianDianDaySchoolTimeTableDomain dto : ddt) {
                        if (map.containsKey(dto.getPeriodId())) {
                            Long tempTeacherId = send(leave.getStudentId(), InitScheduleService.parseTeacher(dto.getTeachers()).getId(), dto.getCourseId(), dto.getTeachDate(), dto.getPeriodId(), null, leave, account.getId());
                            if (null != tempTeacherId) {
                                ids.add(tempTeacherId);
                            }
                        }
                    }
                } else {
                    // 直接修改本地数据库排课信息
                    Set<Long> teachingclassIds = orgManagerRemoteService.getStudentDayTeachingClassId(leave.getStudentId(), DateFormatUtil.format(leave.getStartDate(), DateFormatUtil.FORMAT_SHORT));
                    // 获取当天该学生的排课信息
                    List<Schedule> scheduleList = scheduleRepository.findByTeachDateAndDeleteFlagAndTeachingclassIdIn(DateFormatUtil.format(leave.getStartDate(), DateFormatUtil.FORMAT_SHORT), DataValidity.VALID.getState(), teachingclassIds);
                    // 获取该学生的某一天的排课，修改其签到记录。 对于未点名的课程，需要实时获取请假列表查看。
                    Set<Long> scheduleRollCallIds = new HashSet();
                    for (Schedule schedule : scheduleList) {
                        if (map.containsKey(schedule.getPeriodId())) {
                            ScheduleRollCall scheduleRollCall = scheduleRollCallService.findBySchedule(schedule);
                            if (null != scheduleRollCall && scheduleRollCall.getIsOpenRollcall()) {
                                scheduleRollCallIds.add(scheduleRollCall.getId());
                                boolean inClass = scheduleRollCall.getIsInClassroom();
                                if (inClass) {
                                    // 课堂内，需要去redis库中修改签到状态。
                                    RollCall rollCall = (RollCall) redisTemplate.opsForHash().get(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()), leave.getStudentId());
                                    if (null != rollCall) {
                                        rollCall.setLastType(rollCall.getType());
                                        if (leave.getLeavePublic() == LeaveConstants.TYPE_PU) {
                                            rollCall.setDeleteFlag(DataValidity.INVALID.getState());
                                            redisTemplate.opsForHash().delete(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()), rollCall.getStudentId());
                                        } else {
                                            rollCall.setType(RollCallConstants.TYPE_ASK_FOR_LEAVE);
                                            redisTemplate.opsForHash().put(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()), rollCall.getStudentId(), rollCall);
                                        }
                                    }
                                }
                            }
                            Long tempTeacherId = null;
                            try {
                                tempTeacherId = send(leave.getStudentId(), schedule.getTeacherId(), schedule.getCourseId(), DateFormatUtil.parse(schedule.getTeachDate(), DateFormatUtil.FORMAT_SHORT), schedule.getPeriodId(), schedule, leave, account.getId());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (null != tempTeacherId) {
                                ids.add(tempTeacherId);
                            }
                        }
                    }
                    // 修改学生的签到状态
                    if (null != scheduleRollCallIds && scheduleRollCallIds.size() > 0) {
                        if (leave.getLeavePublic() == LeaveConstants.TYPE_PU) {
                            rollCallRepository.deleteByStudentIdAndScheduleRollcallIdIn(leave.getStudentId(), scheduleRollCallIds);
                        } else {
                            rollCallRepository.updateRollCallByStudentIdAndScheduleRollCall(RollCallConstants.TYPE_ASK_FOR_LEAVE, leave.getStudentId(), scheduleRollCallIds);
                        }
                    }
                }
            }
            pushMessageService.createPushMessage("请假审批结果通知", "请假审批结果通知", PushMessageConstants.FUNCTION_STUDENT_NOTICE, PushMessageConstants.MODULE_LEAVE, "请假审批结果通知", leave.getStudentId());
            ids.add(leave.getStudentId());
            pushService.listPush(accessToken, "您有一条未读的请假审批结果通知", "请假审批", "请假审批", ids);
            //----新消息服务----start
            List<AudienceDTO> audiences = new ArrayList<>();
            AudienceDTO dto = new AudienceDTO();
            dto.setUserId(leave.getStudentId());
            dto.setData(leave);
            audiences.add(dto);
            messageService.push("请假审批结果通知", "您有一条未读的请假审批结果通知", PushMessageConstants.FUNCTION_STUDENT_NOTICE, audiences);
            //----新消息服务----end
            //更新统计
            Map<String, Object> semesterMap = orgManagerRemoteClient.getorgsemester(account.getOrganId(), null);
            if (semesterMap != null && semesterMap.get("id") != null) {
                Long semesterId = Long.parseLong(semesterMap.get("id").toString());
                rollCallStatsService.statsStuAllByStuId(account.getOrganId(), semesterId, leave.getStudentId());
                List<TeachingClassStudent> teachingClassList = teachingClassStudentRepository.findByStuId(leave.getStudentId());
                if (teachingClassList != null && teachingClassList.size() > 0) {
                    for (TeachingClassStudent tcs : teachingClassList) {
                        rollCallStatsService.statsStuByTeachingClass(account.getOrganId(), semesterId, tcs.getTeachingClassId());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Object> res = new HashMap<>();
        res.put(ApiReturnConstants.MESSAGE, ApiReturnConstants.SUCCESS);
        res.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        return res;
    }

    public void updateRollCallAndSendMessage() {

    }

    /**
     * @param studentId
     * @param teacherId
     * @param courseId
     * @param teachDate
     * @param periodId
     * @param schedule
     * @param leave
     * @param accountId
     * @return
     */
    public Long send(Long studentId, Long teacherId, Long courseId, Date teachDate, Long periodId, Schedule schedule, Leave leave, Long accountId) {
        StudentLeaveSchedule sls = new StudentLeaveSchedule();
        sls.setStudentId(studentId);
        sls.setTeacherId(teacherId);
        sls.setCourseId(courseId);
        sls.setRequesDate(teachDate);
        sls.setRequestPeriodId(periodId);
        sls.setScheduleId(null == schedule ? null : schedule.getId());
        sls.setLeaveId(leave.getId());
        studentLeaveScheduleRepository.save(sls);

        if (!sls.getTeacherId().equals(accountId)) {
            pushMessageService.createPushMessage("请假通知", "请假通知", PushMessageConstants.FUNCTION_TEACHER_NOTICE, PushMessageConstants.MODULE_LEAVE, "请假通知", teacherId);
            //----新消息服务----start
            List<AudienceDTO> audiences = new ArrayList<>();
            AudienceDTO dto = new AudienceDTO();
            dto.setUserId(teacherId);
            dto.setData(leave);
            audiences.add(dto);
            messageService.push("请假通知", "请假通知", PushMessageConstants.FUNCTION_TEACHER_NOTICE, audiences);
            //----新消息服务----end
            return teacherId;
        }
        return null;
    }

    public Object rejectLeaveRequest(Long leaveId, String content, Long teacherId, String accessToken) {
        Leave leave = leaveRepository.findOne(leaveId);
        if (leave == null) {
            Map<String, Object> res = new HashMap<>();
            res.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            res.put(ApiReturnConstants.MESSAGE, "leave request not exist!");
            return res;
        }
        if (!leave.getHeadTeacherId().equals(teacherId)) {
            Map<String, Object> res = new HashMap<>();
            res.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            res.put(ApiReturnConstants.MESSAGE, "error creator!");
            return res;
        }
        if (!leave.getStatus().equals(LeaveConstants.STATUS_REQUEST)) {
            Map<String, Object> res = new HashMap<>();
            res.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            res.put(ApiReturnConstants.MESSAGE, "this request is " + leave.getStatus());
            return res;
        }

        List<Leave> leavList = leaveRepository.findAllByStudentIdAndStartDateAndEndDateAndStartPeriodIdAndEndPeriodIdAndRequestContentAndRequestType(leave.getStudentId(),
                leave.getStartDate(), leave.getEndDate(), leave.getStartPeriodId(), leave.getEndPeriodId(), leave.getRequestContent(), leave.getRequestType());
        if (leavList != null && leavList.size() > 0) {
            for (Leave leave1 : leavList) {
                leave1.setRejectContent(content);
                leave1.setStatus(LeaveConstants.STATUS_REJECT);
                if (leave1.getHeadTeacherId().longValue() != teacherId.longValue()) {
                    leave1.setDeleteFlag(DataValidity.INVALID.getState());
                }
            }
        } else {
            leave.setRejectContent(content);
            leave.setStatus(LeaveConstants.STATUS_REJECT);
            leaveRepository.save(leave);
        }
        leaveRepository.save(leavList);

        List<Long> ids = new ArrayList<Long>();
        ids.add(leave.getStudentId());
        pushService.listPush(accessToken, "您有一条未读的请假审批结果通知", "请假审批", "请假审批", ids);
        pushMessageService.createPushMessage("请假审批结果通知", "请假审批结果通知", PushMessageConstants.FUNCTION_STUDENT_NOTICE, PushMessageConstants.MODULE_LEAVE, "请假审批结果通知", leave.getStudentId());
        //----新消息服务----start
        List<AudienceDTO> audiences = new ArrayList<>();
        AudienceDTO dto = new AudienceDTO();
        dto.setUserId(leave.getStudentId());
        dto.setData(leave);
        audiences.add(dto);
        messageService.push("请假审批结果通知", "您有一条未读的请假审批结果通知", PushMessageConstants.FUNCTION_STUDENT_NOTICE, audiences);
        //----新消息服务----end
        Map<String, Object> res = new HashMap<>();
        res.put(ApiReturnConstants.MESSAGE, ApiReturnConstants.SUCCESS);
        res.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        return res;
    }

    /**
     * 老师根据请假类型获取请假列表
     *
     * @param headTeacherId
     * @param status
     * @param organId
     * @param orderByKey
     * @param orderBy
     * @return
     */
    public Object getLeaveRequsetByTeacherAndStatus(Long headTeacherId, String status, Long organId, String orderByKey, String orderBy) {
        List<LeaveForTeacherDTO> ltList = new ArrayList();

        List<Leave> leaves = new ArrayList();
        if (LeaveConstants.STATUS_PROCESSED.equals(status)) {
            leaves = leaveRepository.findAllByHeadTeacherIdAndDeleteFlagAndStatusInOrderByLastModifiedDateDesc(headTeacherId, DataValidity.VALID.getState(), Sets.newHashSet(LeaveConstants.STATUS_REJECT, LeaveConstants.STATUS_PASS));
        } else {
            leaves = leaveRepository.findAllByheadTeacherIdAndStatusAndDeleteFlag(new Sort(new Order(Direction.DESC, "lastModifiedDate")), headTeacherId, status, DataValidity.VALID.getState());
        }

        Map<Long, AccountDTO> map = new HashMap();
        AccountDTO account = new AccountDTO();
        LeaveForTeacherDTO dto = null;
        for (Leave leave : leaves) {
            if (!map.containsKey(leave.getStudentId())) {
                userService.getUserInfo(account, leave.getStudentId());
                map.put(leave.getStudentId(), account);
            } else {
                account = map.get(leave.getStudentId());
            }
            dto = new LeaveForTeacherDTO();
            dto.setLeaveId(leave.getId());
            dto.setCreatedDate(DateFormatUtil.format(leave.getCreatedDate(), DateFormatUtil.FORMAT_MINUTE));
            dto.setLastModifyDate(DateFormatUtil.format(leave.getLastModifiedDate()));
            dto.setClassName(account.getClassesName());
            dto.setMajorName(account.getProfessionalName());
            dto.setCollegeName(account.getCollegeName());

            String startDate = DateFormatUtil.formatShort(leave.getStartDate());
            String endDate = DateFormatUtil.formatShort(leave.getEndDate());
            dto.setStartDate(startDate);
            if (startDate.equals(endDate)) {
                endDate = "";
            }
            dto.setEndDate(endDate);
            dto.setLeaveSchool(leave.getLeaveSchool());
            dto.setStatus(leave.getStatus());
            dto.setRejectContent(leave.getRejectContent());
            dto.setRequestContent(leave.getRequestContent());
            dto.setRequestType(leave.getRequestType());
            dto.setStudentId(leave.getStudentId());
            dto.setStudentName(account.getName());
            dto.setStartPeriodId(leave.getStartPeriodId() == null ? 0 : leave.getStartPeriodId());
            dto.setEndPeriodId(leave.getEndPeriodId() == null ? 0 : leave.getEndPeriodId());
            dto.setLeavePictureUrls(leave.getLeavePictureUrls());
            dto.setTeacherId(leave.getHeadTeacherId());
            dto.setTeacherName(leave.getTeacherName());

            dto.setStartTime(DateFormatUtil.format(leave.getStartTime(), DateFormatUtil.FORMAT_MINUTE));
            dto.setEndTime(DateFormatUtil.format(leave.getEndTime(), DateFormatUtil.FORMAT_MINUTE));
            dto.setLeavePublic(leave.getLeavePublic());
            dto.setLeaveType(leave.getLeaveType());
            dto.setDuration(leave.getDuration());

            ltList.add(dto);
        }
        pushMessageService.readMessage(PushMessageConstants.MODULE_LEAVE, PushMessageConstants.FUNCTION_TEACHER_APPROVAL, headTeacherId);
        return ltList;
    }

    public Object getLeaveRequsetForTeachByTeacherAndStatus(AccountDTO teacher, String status, Long organId, String orderByKey, String orderBy) {
        List<LeaveForTeacherDTO> page = null;
        List<StudentLeaveSchedule> leaveStudents = studentLeaveScheduleRepository.findByTeacherIdAndDeleteFlag(teacher.getId(), DataValidity.VALID.getState());

        Set<Long> leaveSet = new HashSet<>();
        leaveStudents.forEach((st) -> {
            leaveSet.add(st.getLeaveId());
        });

        Map<Long, Leave> leaveMap = new HashedMap();
        List<Leave> leaves = leaveRepository.findAllByIdIn(leaveSet);
        for (Leave leave : leaves) {
            leaveMap.put(leave.getId(), leave);
        }

        List<LeaveForTeacherDTO> ltList = new ArrayList();
        Map<Long, AccountDTO> studentMap = new HashMap();

        LeaveForTeacherDTO dto = null;

        Set<Long> studentSet = new HashSet<>();
        for (Leave leave : leaves) {
            studentSet.add(leave.getStudentId());
        }
        if (studentSet.size() > 0) {
            String json = orgManagerRemoteService.getStudentByIds(studentSet);
            JSONArray arr = JSONArray.fromObject(json);
            if (null != arr && arr.length() > 0) {
                AccountDTO accDto = null;
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    accDto = new AccountDTO();
                    Long id = obj.getLong("id");
                    accDto.setId(id);
                    accDto.setName(obj.getString("name"));
                    accDto.setClassesId(obj.getLong("classesId"));
                    accDto.setClassesName(obj.getString("classesName"));
                    accDto.setProfessionalName(obj.getString("professionalName"));
                    accDto.setCollegeName(obj.getString("collegeName"));
                    studentMap.put(id, accDto);
                }
            }
        }

        AccountDTO account = null;
        Leave stleave = null;
        for (StudentLeaveSchedule studentLeaveSchedule : leaveStudents) {
            account = studentMap.get(studentLeaveSchedule.getStudentId());
            if (null == account) {
                continue;
            }
            stleave = leaveMap.get(studentLeaveSchedule.getLeaveId());
            if (null == stleave) {
                continue;
            }
            dto = new LeaveForTeacherDTO();
            dto.setLeaveId(stleave.getId());
            dto.setCreatedDate(DateFormatUtil.format(stleave.getCreatedDate()));
            dto.setLastModifyDate(DateFormatUtil.format(stleave.getLastModifiedDate()));
            dto.setClassName(account.getClassesName());
            dto.setMajorName(account.getProfessionalName());
            dto.setCollegeName(account.getCollegeName());
            dto.setStartDate(DateFormatUtil.formatShort(stleave.getStartDate()));
            dto.setEndDate(DateFormatUtil.formatShort(stleave.getEndDate()));
            dto.setLeaveSchool(stleave.getLeaveSchool());
            dto.setStatus(stleave.getStatus());
            dto.setRejectContent(stleave.getRejectContent());
            dto.setRequestContent(stleave.getRequestContent());
            dto.setRequestType(stleave.getRequestType());
            dto.setStudentId(stleave.getStudentId());
            dto.setStudentName(account.getName());
            dto.setStartPeriodId(stleave.getStartPeriodId() == null ? 0 : stleave.getStartPeriodId());
            dto.setEndPeriodId(stleave.getEndPeriodId() == null ? 0 : stleave.getEndPeriodId());
            dto.setLeavePictureUrls(stleave.getLeavePictureUrls());
            dto.setTeacherId(stleave.getHeadTeacherId());
            dto.setTeacherName(stleave.getTeacherName());

            dto.setStartTime(DateFormatUtil.format(stleave.getStartTime(), DateFormatUtil.FORMAT_MINUTE));
            dto.setEndTime(DateFormatUtil.format(stleave.getEndTime(), DateFormatUtil.FORMAT_MINUTE));
            dto.setLeavePublic(stleave.getLeavePublic());
            dto.setLeaveType(stleave.getLeaveType());
            dto.setDuration(stleave.getDuration());

            ltList.add(dto);
        }

        pushMessageService.readMessage(PushMessageConstants.MODULE_LEAVE, PushMessageConstants.FUNCTION_TEACHER_NOTICE, teacher.getId());
        return ltList;
    }
}
