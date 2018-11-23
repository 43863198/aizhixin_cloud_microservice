package com.aizhixin.cloud.dd.questionnaire.thread;

import java.util.*;
import java.util.concurrent.TimeUnit;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.messege.dto.AudienceDTO;
import com.aizhixin.cloud.dd.messege.service.MessageService;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.utils.RedisUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.dd.common.core.ClassType;
import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.constant.PushMessageConstants;
import com.aizhixin.cloud.dd.questionnaire.domain.QuestionnaireAssginStudentsDomain;
import com.aizhixin.cloud.dd.questionnaire.dto.QuestionnaireAssignDTO;
import com.aizhixin.cloud.dd.rollcall.dto.TeachingClassesDTO;
import com.aizhixin.cloud.dd.messege.entity.PushMessage;
import com.aizhixin.cloud.dd.questionnaire.entity.Questionnaire;
import com.aizhixin.cloud.dd.questionnaire.entity.QuestionnaireAssgin;
import com.aizhixin.cloud.dd.rollcall.repository.PushMessageRepository;
import com.aizhixin.cloud.dd.messege.service.PushService;
import com.aizhixin.cloud.dd.questionnaire.serviceV2.QuestionnaireServiceV2;
import com.aizhixin.cloud.dd.rollcall.utils.JsonUtil;

public class QuestionnairStudentTeachingClassInsertThread extends Thread {
    private QuestionnaireServiceV2 qs;
    private OrgManagerRemoteClient tc;
    private List<TeachingClassesDTO> teachingClasses;
    private Questionnaire questionnaire;
    private IdNameDomain semester;
    private Long userId;
    private PushMessageRepository pushMessageRepository;
    private PushService pushService;
    private MessageService messageService;
    private String accessToken;
    private RedisTemplate redisTemplate;

    public QuestionnairStudentTeachingClassInsertThread(QuestionnaireServiceV2 qs, OrgManagerRemoteClient tc,
                                                        QuestionnaireAssignDTO questionnaireAssignDTO, Questionnaire questionnaire, IdNameDomain semester,
                                                        Long userId, PushMessageRepository pushMessageRepository, PushService pushService, String accessToken, RedisTemplate redisTemplate, MessageService messageService) {
        this.qs = qs;
        this.tc = tc;
        this.teachingClasses = questionnaireAssignDTO.getTeachingClasses();
        this.questionnaire = questionnaire;
        this.semester = semester;
        this.userId = userId;
        this.pushMessageRepository = pushMessageRepository;
        this.pushService = pushService;
        this.accessToken = accessToken;
        this.redisTemplate = redisTemplate;
        this.messageService = messageService;
    }

    @Override
    public void run() {
        Map<String, Object> redisData = new HashMap<>();
        redisData.put(ApiReturnConstants.RESULT, "20");
        redisData.put(ApiReturnConstants.DATA, "完成");

        List<PushMessage> messages = new ArrayList<>();
        List<QuestionnaireAssginStudentsDomain> qasdl = new ArrayList<>();
        List<Long> userIds = new ArrayList<Long>();
        List<QuestionnaireAssgin> questionnaireAssgins = new ArrayList<>();
        for (TeachingClassesDTO teachingClassesDTO : teachingClasses) {
            List<Long> userIdAll = new ArrayList<Long>();
            QuestionnaireAssgin questionnaireAssgin = qs.findByQuestionnaireAssgin(10, teachingClassesDTO.getTeachingClassesId(), questionnaire.getId());
            Long questionnaireAssginId = 0l;
            if (null == questionnaireAssgin) {
                questionnaireAssgin = new QuestionnaireAssgin();
                questionnaireAssgin.setQuestionnaire(questionnaire);
                questionnaireAssgin.setCollegeId(teachingClassesDTO.getCollegeId());
                questionnaireAssgin.setCollegeName(teachingClassesDTO.getCollegeName());
                questionnaireAssgin.setTeacherId(teachingClassesDTO.getTeacherId());
                questionnaireAssgin.setTeacherName(teachingClassesDTO.getTeacherName());
                questionnaireAssgin.setCourseName(teachingClassesDTO.getCourseName());
                questionnaireAssgin.setCourseId(teachingClassesDTO.getCourseId());
                questionnaireAssgin.setTeachingClassId(teachingClassesDTO.getTeachingClassesId());
                questionnaireAssgin.setTeachingClassCode(teachingClassesDTO.getTeachingClassCode());
                questionnaireAssgin.setTeachingClassName(teachingClassesDTO.getTeachingClassName());
                questionnaireAssgin.setSemesterId(semester.getId());
                questionnaireAssgin.setCreatedBy(userId);
                questionnaireAssgin.setLastModifiedBy(userId);
                questionnaireAssgin.setStatus("10");// 分配
                questionnaireAssgin.setClassType(ClassType.TeachingClass.getClassTypeI());
                questionnaireAssginId = qs.saveQuestionnaireAssgin(questionnaireAssgin);
            } else {
                questionnaireAssginId = questionnaireAssgin.getId();
                List<Long> userIdss = qs.findQuestionnaireAssginStudent(questionnaireAssginId);
                if (null != userIdss && 0 < userIdss.size()) {
                    userIdAll.addAll(userIdss);
                }
            }
            questionnaireAssgins.add(questionnaireAssgin);
            String json = tc.findTeachingClassListStudent(teachingClassesDTO.getTeachingClassesId(), 1, Integer.MAX_VALUE);
            if (!StringUtils.isEmpty(json)) {
                try {
                    Map<String, Object> result = JsonUtil.Json2Object(json);
                    if (null != result.get("data")) {
                        List<Map<String, Object>> rl = (List<Map<String, Object>>) result.get("data");
                        if (null != rl && 0 < rl.size()) {
                            for (Map<String, Object> map : rl) {
                                QuestionnaireAssginStudentsDomain qd = new QuestionnaireAssginStudentsDomain();
                                if (null != map.get("classesId")) {
                                    qd.setClassesId(Long.valueOf(map.get("classesId").toString()));
                                }
                                if (null != map.get("classesName")) {
                                    qd.setClassesName(map.get("classesName").toString());
                                }
                                if (null != map.get("id")) {
                                    if (userIdAll.contains(Long.valueOf(map.get("id").toString()))) {
                                        continue;
                                    }
                                    userIdAll.add(Long.valueOf(map.get("id").toString()));
                                    qd.setStuId(Long.valueOf(map.get("id").toString()));
                                    userIds.add(Long.valueOf(map.get("id").toString()));
                                    PushMessage message = new PushMessage();
                                    message.setContent(questionnaire.getName());
                                    message.setFunction(PushMessageConstants.FUNCITON_QUESTIONNAIRE_NOTICE);
                                    message.setModule(PushMessageConstants.MODULE_QUESTIONNAIRE);
                                    message.setHaveRead(Boolean.FALSE);
                                    message.setPushTime(new Date());
                                    message.setTitle("问卷调查通知");
                                    message.setUserId(Long.valueOf(map.get("id").toString()));
                                    String businessContent = "";
                                    message.setBusinessContent(businessContent);
                                    messages.add(message);
                                }
                                if (null != map.get("name")) {
                                    qd.setStuName(map.get("name").toString());
                                }
                                qd.setQuestionnaireAssginId(questionnaireAssginId);
                                qd.setStatus(10);
                                qasdl.add(qd);
                            }
                        }
                    }
                } catch (Exception e) {
                    redisData.put(ApiReturnConstants.RESULT, "30");
                    redisData.put(ApiReturnConstants.DATA, e);
                }
            }
        }
        if (!qasdl.isEmpty()) {
            qs.save(qasdl);
            pushMessageRepository.save(messages);
            pushService.listPush(accessToken, questionnaire.getName(), "问卷调查通知", "问卷调查通知", userIds);

            //----新消息服务----start
            List<AudienceDTO> audienceList = new ArrayList<>();
            for (QuestionnaireAssginStudentsDomain item : qasdl) {
                audienceList.add(new AudienceDTO(item.getStuId(), item));
            }
            messageService.push("问卷调查通知", "您有新的调查问卷。", PushMessageConstants.FUNCITON_QUESTIONNAIRE_NOTICE, audienceList);
            //----新消息服务----end
        }
        redisTemplate.opsForValue().set(RedisUtil.getQuesAssignResultKey(questionnaire.getId()), redisData, 1, TimeUnit.DAYS);
    }
}
