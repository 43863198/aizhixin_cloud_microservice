package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.constant.PushMessageConstants;
import com.aizhixin.cloud.dd.constant.ReturnConstants;
import com.aizhixin.cloud.dd.constant.RollCallConstants;
import com.aizhixin.cloud.dd.messege.dto.AudienceDTO;
import com.aizhixin.cloud.dd.messege.dto.MessageDTO;
import com.aizhixin.cloud.dd.messege.entity.PushMessage;
import com.aizhixin.cloud.dd.messege.service.MessageService;
import com.aizhixin.cloud.dd.messege.service.PushService;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;
import com.aizhixin.cloud.dd.rollcall.domain.RollCallAppealDomain;
import com.aizhixin.cloud.dd.rollcall.dto.RollCallAppealDTO;
import com.aizhixin.cloud.dd.rollcall.entity.*;
import com.aizhixin.cloud.dd.rollcall.repository.*;
import com.aizhixin.cloud.dd.rollcall.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class RollcallAppealService {
    @Autowired
    private RollCallRepository rollCallRepository;
    @Autowired
    private ScheduleRollCallRepository scheduleRollCallRepository;
    @Autowired
    private RollCallAppealRepository appealRepository;
    @Autowired
    private RollCallAppealFileRepository fileRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private PushMessageRepository pushMessageRepository;
    @Autowired
    private PushService pushService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MessageService messageService;

    public Map<String, Object> create(String accessToken, Long userId, RollCallAppealDTO appealDTO) {
        Map<String, Object> result = new HashMap<>();
        if (appealDTO.getScheduleId() == null) {
            result.put(ReturnConstants.RETURN_SUCCESS, Boolean.FALSE);
            result.put(ReturnConstants.RETURN_MESSAGE, "排课ID不能为空！");
            return result;
        }
        ScheduleRollCall scheduleRollCall = scheduleRollCallRepository.findBySchedule_Id(appealDTO.getScheduleId());
        if (scheduleRollCall == null) {
            result.put(ReturnConstants.RETURN_SUCCESS, Boolean.FALSE);
            result.put(ReturnConstants.RETURN_MESSAGE, "无排课信息！");
            return result;
        }
        List<RollCall> rollCalls = rollCallRepository.findByScheduleRollcallIdInAndStudentId(scheduleRollCall.getId(), userId);
        RollCall rollCall = null;
        if (rollCalls == null || rollCalls.size() == 0) {
            //从redis中获取
            rollCall = (RollCall) redisTemplate.opsForHash().get(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()), userId);
            if (rollCall != null) {
                rollCall = rollCallRepository.save(rollCall);
                redisTemplate.opsForHash().put(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()), userId, rollCall);
            }
        } else {
            rollCall = rollCalls.get(0);
        }
        if (rollCall == null) {
            result.put(ReturnConstants.RETURN_SUCCESS, Boolean.FALSE);
            result.put(ReturnConstants.RETURN_MESSAGE, "无考勤信息！");
            return result;
        }
        RollCallAppeal appeal = new RollCallAppeal();
        appeal.setRollCall(rollCall);
        appeal.setScheduleRollCall(scheduleRollCall);
        appeal.setStuId(userId);
        appeal.setContent(appealDTO.getContent());
        appeal.setAppealStatus(10);
        appeal.setTeacherId(scheduleRollCall.getSchedule().getTeacherId());
        appeal.setTeacherName(scheduleRollCall.getSchedule().getTeacherNname());
        appeal = appealRepository.save(appeal);
        //保存文件
        if (appealDTO.getAppealFiles() != null && appealDTO.getAppealFiles().size() > 0) {
            List<RollCallAppealFile> list = appealDTO.getAppealFiles();
            for (RollCallAppealFile item : list) {
                item.setAppealId(appeal.getId());
            }
            list = fileRepository.save(list);
            appeal.setAppealFiles(list);
        }
        List<PushMessage> msgList = new ArrayList<>();
        PushMessage stuMsg = new PushMessage(userId, "您有新的考勤申诉信息！", "考勤申诉通知", PushMessageConstants.ROLLCALL_APPEAL_MODULE, PushMessageConstants.ROLLCALL_APPEAL_STUDENT_NOTICE, Boolean.FALSE, new Date(), "");
        msgList.add(stuMsg);
        PushMessage teacherMsg = new PushMessage(scheduleRollCall.getSchedule().getTeacherId(), "您有新的考勤申诉信息！", "考勤申诉通知", PushMessageConstants.ROLLCALL_APPEAL_MODULE, PushMessageConstants.ROLLCALL_APPEAL_TEACHER_NOTICE, Boolean.FALSE, new Date(), "");
        msgList.add(teacherMsg);
        pushMessageRepository.save(msgList);
        List<Long> userIds = new ArrayList<>();
        userIds.add(userId);
        userIds.add(scheduleRollCall.getSchedule().getTeacherId());
        pushService.listPush(accessToken, "您有一条考勤申诉信息", "考勤申诉通知", "考勤申诉通知", userIds);

        //----新消息服务----start
        List<RollCallAppeal> appeals = new ArrayList<>();
        appeals.add(appeal);
        List<RollCallAppealDomain> list = typeAppealDomains(appeals);

        List<MessageDTO> messageDTOs = new ArrayList<>();
        MessageDTO messageDTO1 = new MessageDTO();
        messageDTO1.setTitle("考勤申诉通知");
        messageDTO1.setContent("您有新的考勤申诉信息！");
        messageDTO1.setFunction(PushMessageConstants.ROLLCALL_APPEAL_STUDENT_NOTICE);
        List<AudienceDTO> audienceList1 = new ArrayList<>();
        audienceList1.add(new AudienceDTO(userId, list.get(0)));
        messageDTO1.setAudience(audienceList1);
        messageDTOs.add(messageDTO1);

        MessageDTO messageDTO2 = new MessageDTO();
        messageDTO2.setTitle("补卡申请通知");
        messageDTO2.setContent("您有新的补卡申请信息！");
        messageDTO2.setFunction(PushMessageConstants.ROLLCALL_APPEAL_TEACHER_NOTICE);
        List<AudienceDTO> audienceList2 = new ArrayList<>();
        audienceList2.add(new AudienceDTO(appeal.getTeacherId(), list.get(0)));
        messageDTO2.setAudience(audienceList2);
        messageDTOs.add(messageDTO2);

        messageService.pushList(messageDTOs);
        //----新消息服务----end


        result.put(ReturnConstants.RETURN_SUCCESS, Boolean.TRUE);
        return result;
    }

    public PageData<RollCallAppealDomain> getListByStudent(Long userId, Pageable pageable, Integer appealStatus) {
        Page<RollCallAppeal> page = null;
        if (appealStatus != null && appealStatus > 0) {
            page = appealRepository.findByStuIdAndAppealStatusAndDeleteFlag(pageable, userId, appealStatus, DataValidity.VALID.getState());
        } else {
            page = appealRepository.findByStuIdAndDeleteFlag(pageable, userId, DataValidity.VALID.getState());
        }
        List<RollCallAppealDomain> list = new ArrayList<>();
        if (page != null && page.getContent() != null) {
            list = typeAppealDomains(page.getContent());
        }
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageSize(page.getSize());
        pageDomain.setPageNumber(pageable.getPageNumber() + 1);
        pageDomain.setTotalElements(page.getTotalElements());
        pageDomain.setTotalPages(page.getTotalPages());
        PageData<RollCallAppealDomain> pageData = new PageData<>();
        pageData.setData(list);
        pageData.setPage(pageDomain);
        return pageData;
    }

    public PageData<RollCallAppealDomain> getListByTeacher(Long userId, Pageable pageable, Integer appealStatus) {
        Page<RollCallAppeal> page = null;
        if (appealStatus != null && appealStatus > 0) {
            page = appealRepository.findByTeacherIdAndAppealStatusAndDeleteFlag(pageable, userId, appealStatus, DataValidity.VALID.getState());
        } else {
            page = appealRepository.findByTeacherIdAndDeleteFlag(pageable, userId, DataValidity.VALID.getState());
        }
        List<RollCallAppealDomain> list = new ArrayList<>();
        if (page != null && page.getContent() != null) {
            list = typeAppealDomains(page.getContent());
        }
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageSize(page.getSize());
        pageDomain.setPageNumber(pageable.getPageNumber() + 1);
        pageDomain.setTotalElements(page.getTotalElements());
        pageDomain.setTotalPages(page.getTotalPages());
        PageData<RollCallAppealDomain> pageData = new PageData<>();
        pageData.setData(list);
        pageData.setPage(pageDomain);
        return pageData;
    }

    private List<RollCallAppealDomain> typeAppealDomains(List<RollCallAppeal> appeals) {
        if (appeals == null || appeals.size() == 0) {
            return null;
        }
        List<RollCallAppealDomain> list = new ArrayList<>();
        Set<Long> stuIds = new HashSet<>();
        for (RollCallAppeal item : appeals) {
            stuIds.add(item.getStuId());
        }
        Set<UserInfo> users = userInfoRepository.findByUserIdIn(stuIds);
        Map<Long, UserInfo> stuMap = new HashMap<>();
        for (UserInfo item : users) {
            stuMap.put(item.getUserId(), item);
        }
        for (RollCallAppeal item : appeals) {
            if (item.getRollCall() != null && item.getScheduleRollCall() != null && item.getScheduleRollCall().getSchedule() != null) {
                RollCallAppealDomain d = new RollCallAppealDomain();
                d.setId(item.getId());
                d.setStuId(item.getStuId());
                UserInfo stu = stuMap.get(item.getStuId());
                if (stu != null) {
                    d.setAvatar(stu.getAvatar());
                    d.setStuName(stu.getName());
                }
                d.setTeacherId(item.getTeacherId());
                d.setContent(item.getContent());
                d.setAppealFiles(item.getAppealFiles());
                d.setAppealStatus(item.getAppealStatus());
                d.setAppealDate(item.getAppealDate());
                d.setCreatedDate(item.getCreatedDate());
                if (item.getRollCall() != null) {
                    RollCall rollCall = item.getRollCall();
                    d.setType(rollCall.getType());
                    d.setSignTime(rollCall.getSignTime());
                    d.setClassId(rollCall.getClassId());
                    d.setClassName(rollCall.getClassName());
                }

                Schedule schedule = item.getScheduleRollCall().getSchedule();
                d.setTeacherName(schedule.getTeacherNname());
                d.setCourseId(schedule.getCourseId());
                d.setCourseName(schedule.getCourseName());
                d.setTeachingClassId(schedule.getTeachingclassId());
                d.setTeachingClassName(schedule.getTeachingclassName());
                d.setWeekName(schedule.getWeekName());
                d.setDayOfWeek(schedule.getDayOfWeek());
                d.setPeriodNo(schedule.getPeriodNo());
                d.setPeriodNum(schedule.getPeriodNum());
                d.setTeachDate(schedule.getTeachDate());

                list.add(d);
            }
        }
        return list;
    }

    public void approvalByTeacher(String accessToken, Long appealId, Integer appealStatus) {
        RollCallAppeal appeal = appealRepository.findOne(appealId);
        appeal.setAppealStatus(appealStatus);
        appeal.setAppealDate(new Date());
        if (appealStatus == 20) {
            RollCall rollCall = appeal.getRollCall();
            rollCall.setType(RollCallConstants.TYPE_NORMA);
        }
        appealRepository.save(appeal);
        if (appealStatus == 20) {
            RollCall rollCall = (RollCall) redisTemplate.opsForHash().get(RedisUtil.getScheduleRollCallKey(appeal.getScheduleRollCall().getId()), appeal.getStuId());
            if (rollCall != null) {
                rollCall.setType(RollCallConstants.TYPE_NORMA);
                rollCall.setCanRollCall(false);
                redisTemplate.opsForHash().put(RedisUtil.getScheduleRollCallKey(appeal.getScheduleRollCall().getId()), appeal.getStuId(), rollCall);
            }
        }
        PushMessage stuMsg = new PushMessage(appeal.getStuId(), "您有新的考勤申诉信息！", "考勤申诉通知", PushMessageConstants.ROLLCALL_APPEAL_MODULE, PushMessageConstants.ROLLCALL_APPEAL_STUDENT_NOTICE, Boolean.FALSE, new Date(), "");
        pushMessageRepository.save(stuMsg);
        List<Long> userIds = new ArrayList<>();
        userIds.add(appeal.getStuId());
        pushService.listPush(accessToken, "您有一条考勤申诉信息", "考勤申诉通知", "考勤申诉通知", userIds);

        //----新消息服务----start
        List<RollCallAppeal> appeals = new ArrayList<>();
        appeals.add(appeal);
        List<RollCallAppealDomain> list = typeAppealDomains(appeals);

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setTitle("考勤申诉通知");
        messageDTO.setContent("您有新的考勤申诉信息！");
        messageDTO.setFunction(PushMessageConstants.ROLLCALL_APPEAL_STUDENT_NOTICE);
        List<AudienceDTO> audienceList = new ArrayList<>();
        audienceList.add(new AudienceDTO(appeal.getStuId(), list.get(0)));
        messageDTO.setAudience(audienceList);
        messageService.push(messageDTO);
        //----新消息服务----end
    }
}
