package com.aizhixin.cloud.dd.appeal.service;

import com.aizhixin.cloud.dd.appeal.domain.AppealDomain;
import com.aizhixin.cloud.dd.appeal.domain.CounsellorDomain;
import com.aizhixin.cloud.dd.appeal.domain.DataDomain;
import com.aizhixin.cloud.dd.appeal.dto.AppealDTO;
import com.aizhixin.cloud.dd.appeal.entity.Appeal;
import com.aizhixin.cloud.dd.appeal.entity.AppealFile;
import com.aizhixin.cloud.dd.appeal.repository.AppealFileRepository;
import com.aizhixin.cloud.dd.appeal.repository.AppealRepository;
import com.aizhixin.cloud.dd.appeal.utils.AppealStatus;
import com.aizhixin.cloud.dd.appeal.utils.AppealType;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.constant.PushMessageConstants;
import com.aizhixin.cloud.dd.constant.ReturnConstants;
import com.aizhixin.cloud.dd.counsellorollcall.entity.AlarmClock;
import com.aizhixin.cloud.dd.counsellorollcall.entity.CounsellorRollcall;
import com.aizhixin.cloud.dd.counsellorollcall.entity.StudentSignIn;
import com.aizhixin.cloud.dd.counsellorollcall.entity.TempGroup;
import com.aizhixin.cloud.dd.counsellorollcall.repository.AlarmClockRepository;
import com.aizhixin.cloud.dd.counsellorollcall.repository.StudentSignInRepository;
import com.aizhixin.cloud.dd.counsellorollcall.utils.CounsellorRollCallEnum;
import com.aizhixin.cloud.dd.counsellorollcall.v1.service.CounsellorRedisService;
import com.aizhixin.cloud.dd.counsellorollcall.v2.service.CounselorRollcallStudentService;
import com.aizhixin.cloud.dd.messege.dto.AudienceDTO;
import com.aizhixin.cloud.dd.messege.dto.MessageDTO;
import com.aizhixin.cloud.dd.messege.entity.PushMessage;
import com.aizhixin.cloud.dd.messege.service.MessageService;
import com.aizhixin.cloud.dd.messege.service.PushService;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.repository.PushMessageRepository;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class AppealService {
    private final Logger logger = LoggerFactory.getLogger(AppealService.class);
    @Autowired
    private AppealRepository appealRepository;
    @Autowired
    private AppealFileRepository fileRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private PushMessageRepository pushMessageRepository;
    @Autowired
    private PushService pushService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StudentSignInRepository studentSignInRepository;
    @Autowired
    private AlarmClockRepository clockRepository;
    @Autowired
    private CounselorRollcallStudentService studentService;
    @Autowired
    private CounsellorRedisService counsellorRedisService;
    @Autowired
    private MessageService messageService;

    public Map<String, Object> create(String accessToken, AccountDTO account, AppealDTO appealDTO) {
        Map<String, Object> result = new HashMap<>();
        Long userId = account.getId();
        String userName = account.getName();
        Appeal appeal = new Appeal();
        appeal.setType(appealDTO.getType());
        if (appealDTO.getType().intValue() == AppealType.COUNSELLOR.getType()) {
            if (appealDTO.getCounsellorDTO().getSignInId() == null) {
                result.put(ReturnConstants.RETURN_SUCCESS, Boolean.FALSE);
                result.put(ReturnConstants.RETURN_MESSAGE, "签到ID不能为空！");
                return result;
            }
            if (appealDTO.getCounsellorDTO().getTimes() == null) {
                appealDTO.getCounsellorDTO().setTimes(1);
            }
            StudentSignIn signIn = studentSignInRepository.findOne(appealDTO.getCounsellorDTO().getSignInId());
            if (signIn == null || signIn.getCounsellorRollcall() == null || signIn.getCounsellorRollcall().getTempGroup() == null) {
                result.put(ReturnConstants.RETURN_SUCCESS, Boolean.FALSE);
                result.put(ReturnConstants.RETURN_MESSAGE, "无签到数据！");
                return result;
            }
            CounsellorRollcall rollcall = signIn.getCounsellorRollcall();
            TempGroup group = rollcall.getTempGroup();
            appeal.setInspectorId(group.getTeacherId());
            appeal.setInspectorName(group.getTeacherName());

            CounsellorDomain data = new CounsellorDomain();
            data.setTimes(appealDTO.getCounsellorDTO().getTimes());
            data.setSignInId(signIn.getId());
            data.setGroupId(group.getId());
            data.setGroupName(group.getName());
            data.setRollcallType(group.getRollcallType());
            data.setOpenTime(rollcall.getOpenTime());
            AlarmClock alarmClock = clockRepository.findByTempGroupAndDeleteFlag(group, DataValidity.VALID.getState());
            if (alarmClock == null) {
                data.setStatus(signIn.getStatus());
                data.setStartEndTime(formatTime(data.getOpenTime()));
            } else {
                if (group.getRollcallNum() != null && group.getRollcallNum().intValue() > 1) {
                    if (appealDTO.getCounsellorDTO().getTimes() > 1) {
                        data.setStatus(signIn.getStatus2());
                        data.setStartEndTime(alarmClock.getSecondTime() + "--" + alarmClock.getEndTime());
                    } else {
                        data.setStatus(signIn.getStatus());
                        data.setStartEndTime(alarmClock.getClockTime() + "--" + alarmClock.getLateTime());
                    }
                } else {
                    data.setStatus(signIn.getStatus());
                    data.setStartEndTime(alarmClock.getClockTime());
                }
            }
            String sourceData = JSON.toJSONString(data);
            appeal.setSourceData(sourceData);
        }
        appeal.setApplicantId(userId);
        appeal.setApplicantName(userName);
        appeal.setContent(appealDTO.getContent());
        appeal.setAppealStatus(AppealStatus.PENDING.getStatus());
        appeal = appealRepository.save(appeal);
        //保存文件
        List<AppealFile> files = new ArrayList<>();
        if (appealDTO.getAppealFiles() != null && appealDTO.getAppealFiles().size() > 0) {
            files = appealDTO.getAppealFiles();
            for (AppealFile item : files) {
                item.setAppealId(appeal.getId());
            }
            files = fileRepository.save(files);
        }
        List<PushMessage> msgList = new ArrayList<>();
        PushMessage stuMsg = new PushMessage(userId, "您有新的补卡申请信息！", "补卡申请通知", PushMessageConstants.APPEAL_MODULE, PushMessageConstants.APPEAL_STUDENT_NOTICE, Boolean.FALSE, new Date(), "");
        msgList.add(stuMsg);
        PushMessage teacherMsg = new PushMessage(appeal.getInspectorId(), "您有新的补卡申请信息！", "补卡申请通知", PushMessageConstants.APPEAL_MODULE, PushMessageConstants.APPEAL_TEACHER_NOTICE, Boolean.FALSE, new Date(), "");
        msgList.add(teacherMsg);
        pushMessageRepository.save(msgList);
        List<Long> userIds = new ArrayList<>();
        userIds.add(userId);
        userIds.add(appeal.getInspectorId());
        pushService.listPush(accessToken, "您有一条补卡申请信息", "补卡申请通知", "补卡申请通知", userIds);

        //----新消息服务----start
        List<Appeal> appeals = new ArrayList<>();
        appeal.setAppealFiles(files);
        appeals.add(appeal);
        List<AppealDomain> list = typeAppealDomains(appeals);

        List<MessageDTO> messageDTOs = new ArrayList<>();
        MessageDTO messageDTO1 = new MessageDTO();
        messageDTO1.setTitle("补卡申请通知");
        messageDTO1.setContent("您有新的补卡申请信息！");
        messageDTO1.setFunction(PushMessageConstants.APPEAL_STUDENT_NOTICE);
        List<AudienceDTO> audienceList1 = new ArrayList<>();
        audienceList1.add(new AudienceDTO(userId, list.get(0)));
        messageDTO1.setAudience(audienceList1);
        messageDTOs.add(messageDTO1);

        MessageDTO messageDTO2 = new MessageDTO();
        messageDTO2.setTitle("补卡申请通知");
        messageDTO2.setContent("您有新的补卡申请信息！");
        messageDTO2.setFunction(PushMessageConstants.APPEAL_TEACHER_NOTICE);
        List<AudienceDTO> audienceList2 = new ArrayList<>();
        audienceList2.add(new AudienceDTO(appeal.getInspectorId(), list.get(0)));
        messageDTO2.setAudience(audienceList2);
        messageDTOs.add(messageDTO2);

        messageService.pushList(messageDTOs);
        //----新消息服务----end

        result.put(ReturnConstants.RETURN_SUCCESS, Boolean.TRUE);
        return result;
    }

    public PageData<AppealDomain> getListByStudent(Long userId, Pageable pageable, Integer appealStatus) {
        Page<Appeal> page = null;
        if (appealStatus != null && appealStatus > 0) {
            page = appealRepository.findByApplicantIdAndAppealStatusAndDeleteFlag(pageable, userId, appealStatus, DataValidity.VALID.getState());
        } else {
            page = appealRepository.findByApplicantIdAndDeleteFlag(pageable, userId, DataValidity.VALID.getState());
        }
        List<AppealDomain> list = typeAppealDomains(page.getContent());
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageSize(page.getSize());
        pageDomain.setPageNumber(pageable.getPageNumber() + 1);
        pageDomain.setTotalElements(page.getTotalElements());
        pageDomain.setTotalPages(page.getTotalPages());
        PageData<AppealDomain> pageData = new PageData<>();
        pageData.setData(list);
        pageData.setPage(pageDomain);
        return pageData;
    }

    public PageData<AppealDomain> getListByTeacher(Long userId, Pageable pageable, Integer appealStatus) {
        Page<Appeal> page = null;
        if (appealStatus != null && appealStatus > 0) {
            page = appealRepository.findByInspectorIdAndAppealStatusAndDeleteFlag(pageable, userId, appealStatus, DataValidity.VALID.getState());
        } else {
            page = appealRepository.findByInspectorIdAndDeleteFlag(pageable, userId, DataValidity.VALID.getState());
        }
        List<AppealDomain> list = typeAppealDomains(page.getContent());
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageSize(page.getSize());
        pageDomain.setPageNumber(pageable.getPageNumber() + 1);
        pageDomain.setTotalElements(page.getTotalElements());
        pageDomain.setTotalPages(page.getTotalPages());
        PageData<AppealDomain> pageData = new PageData<>();
        pageData.setData(list);
        pageData.setPage(pageDomain);
        return pageData;
    }

    private List<AppealDomain> typeAppealDomains(List<Appeal> list) {
        List<AppealDomain> reuslt = new ArrayList<>();
        if (list != null && list.size() > 0) {
            Set<Long> userIds = new HashSet<>();
            for (Appeal item : list) {
                userIds.add(item.getApplicantId());
            }
            Set<UserInfo> users = userInfoRepository.findByUserIdIn(userIds);
            Map<Long, UserInfo> userMap = new HashMap<>();
            for (UserInfo item : users) {
                userMap.put(item.getUserId(), item);
            }
            for (Appeal item : list) {
                AppealDomain d = new AppealDomain();
                BeanUtils.copyProperties(item, d);
                DataDomain data = null;
                if (item.getType().intValue() == AppealType.COUNSELLOR.getType()) {
                    data = JSON.parseObject(item.getSourceData(), CounsellorDomain.class);
                }
                d.setData(data);
                UserInfo user = userMap.get(item.getApplicantId());
                if (user != null) {
                    d.setAvatar(user.getAvatar());
                    d.setClassName(user.getClassesName());
                }
                reuslt.add(d);
            }
        }
        return reuslt;
    }

    public void approvalByTeacher(String accessToken, Long appealId, Integer appealStatus) {
        Appeal appeal = appealRepository.findOne(appealId);
        appeal.setAppealStatus(appealStatus);
        appeal.setAppealDate(new Date());
        if (appealStatus == AppealStatus.PASS.getStatus()) {
            if (appeal.getType().intValue() == AppealType.COUNSELLOR.getType()) {
                processCounsellor(appeal);
            }
        }
        appealRepository.save(appeal);
        PushMessage stuMsg = new PushMessage(appeal.getApplicantId(), "您有新的补卡申请信息！", "补卡申请通知", PushMessageConstants.APPEAL_MODULE, PushMessageConstants.APPEAL_STUDENT_NOTICE, Boolean.FALSE, new Date(), "");
        pushMessageRepository.save(stuMsg);
        List<Long> userIds = new ArrayList<>();
        userIds.add(appeal.getApplicantId());
        pushService.listPush(accessToken, "您有一条补卡申请信息", "补卡申请通知", "补卡申请通知", userIds);

        //----新消息服务----start
        List<Appeal> appeals = new ArrayList<>();
        appeals.add(appeal);
        List<AppealDomain> list = typeAppealDomains(appeals);

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setTitle("补卡申请通知");
        messageDTO.setContent("您有新的补卡申请信息！");
        messageDTO.setFunction(PushMessageConstants.APPEAL_STUDENT_NOTICE);
        List<AudienceDTO> audienceList = new ArrayList<>();
        audienceList.add(new AudienceDTO(appeal.getApplicantId(), list.get(0)));
        messageDTO.setAudience(audienceList);
        messageService.push(messageDTO);
        //----新消息服务----end
    }

    /**
     * 点名申诉
     *
     * @param appeal
     */
    private void processCounsellor(Appeal appeal) {
        CounsellorDomain data = JSON.parseObject(appeal.getSourceData(), CounsellorDomain.class);
        data.setStatus(CounsellorRollCallEnum.HavaTo.getType());
        String sourceData = JSON.toJSONString(data);
        appeal.setSourceData(sourceData);
        StudentSignIn signIn = studentSignInRepository.findOne(data.getSignInId());
        if (signIn != null) {
            if (data.getTimes() > 1) {
                signIn.setStatus2(CounsellorRollCallEnum.HavaTo.getType());
            } else {
                signIn.setStatus(CounsellorRollCallEnum.HavaTo.getType());
            }
            studentSignInRepository.save(signIn);
            //更新缓存
            studentService.updateStuCache(signIn);
            if (signIn.getCounsellorRollcall() != null && signIn.getCounsellorRollcall().getTempGroup() != null) {
                if (signIn.getCounsellorRollcall().getTempGroup().getRollcallNum() == null || signIn.getCounsellorRollcall().getTempGroup().getRollcallNum().intValue() < 2) {
                    counsellorRedisService.refreshStuCache(signIn.getStudentId(), signIn.getCounsellorRollcall().getId(), signIn.getStatus());
                }
            }
        }
    }

    private String formatTime(Date time) {
        if (time == null) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(time);
    }
}
