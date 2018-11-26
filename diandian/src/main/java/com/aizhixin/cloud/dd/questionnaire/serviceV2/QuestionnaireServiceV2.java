package com.aizhixin.cloud.dd.questionnaire.serviceV2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.messege.dto.AudienceDTO;
import com.aizhixin.cloud.dd.messege.service.MessageService;
import com.aizhixin.cloud.dd.questionnaire.dto.QuestionnaireCensusDTO;
import com.aizhixin.cloud.dd.questionnaire.entity.QuestionnaireAssginStudents;
import com.aizhixin.cloud.dd.remote.*;
import com.aizhixin.cloud.dd.rollcall.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.ClassType;
import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.common.utils.PageDomainUtil;
import com.aizhixin.cloud.dd.constant.PushMessageConstants;
import com.aizhixin.cloud.dd.constant.QuestionnaireStatus;
import com.aizhixin.cloud.dd.questionnaire.JdbcTemplate.QuestionnaireAssginJdbc;
import com.aizhixin.cloud.dd.questionnaire.JdbcTemplate.QuestionnaireAssginStudentsJdbc;
import com.aizhixin.cloud.dd.questionnaire.domain.QuestionnaireAssginStudentsDomain;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.questionnaire.dto.QuestionnaireAssginDTOV2;
import com.aizhixin.cloud.dd.questionnaire.dto.QuestionnaireAssignDTO;
import com.aizhixin.cloud.dd.messege.entity.PushMessage;
import com.aizhixin.cloud.dd.questionnaire.entity.Questionnaire;
import com.aizhixin.cloud.dd.questionnaire.entity.QuestionnaireAssgin;
import com.aizhixin.cloud.dd.rollcall.repository.PushMessageRepository;
import com.aizhixin.cloud.dd.questionnaire.repository.QuestionnaireAssginRepository;
import com.aizhixin.cloud.dd.questionnaire.repository.QuestionnaireRepository;
import com.aizhixin.cloud.dd.messege.service.PushService;
import com.aizhixin.cloud.dd.rollcall.service.SemesterService;
import com.aizhixin.cloud.dd.questionnaire.thread.QuestionnairStudentClassesInsertThread;
import com.aizhixin.cloud.dd.questionnaire.thread.QuestionnairStudentCollegeInsertThread;
import com.aizhixin.cloud.dd.questionnaire.thread.QuestionnairStudentOrgInsertThread;
import com.aizhixin.cloud.dd.questionnaire.thread.QuestionnairStudentProfInsertThread;
import com.aizhixin.cloud.dd.questionnaire.thread.QuestionnairStudentTeachingClassInsertThread;
import com.aizhixin.cloud.dd.rollcall.utils.JsonUtil;

@Slf4j
@Service
@Transactional
public class QuestionnaireServiceV2 {
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Autowired
    private QuestionnaireRepository qr;
    @Autowired
    private SemesterService semesterService;
    @Autowired
    private QuestionnaireAssginStudentsJdbc questionnaireAssginStudentsJdbc;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteClient;
    @Autowired
    private PushMessageRepository pushMessageRepository;
    @Autowired
    private PushService pushService;
    @Autowired
    private QuestionnaireRepository questionnaireRepository;
    @Autowired
    private QuestionnaireAssginJdbc questionnaireAssginJdbc;
    @Autowired
    private QuestionnaireAssginRepository questionnaireAssginRepository;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MessageService messageService;

    public Map<String, Object> isCommit(AccountDTO account, String role) {
        Map<String, Object> result = new HashMap<>();
        String date = DateFormatUtil.formatShort(new Date());
        Map<String, Object> map = questionnaireAssginStudentsJdbc.findAlertByNoCommitStudent(account.getId(), account.getOrganId(), date, role);
        if (map != null) {//&& map.get("iconUrl") != null && map.get("targetUrl") != null
            if (map.get("ONOFF").equals("on")) {
                result.put("isAlert", Boolean.TRUE);
                result.put("targetType", map.get("targetType"));
                result.put("title", map.get("title"));
                result.put("iconUrl", map.get("iconUrl"));
                result.put("targetUrl", map.get("targetUrl"));
            } else if (map.get("ONOFF").equals("many") && !StringUtils.isEmpty(map.get("ORGS"))) {
                String[] orgs = map.get("ORGS").toString().split(",");
                boolean flag = false;
                for (String org : orgs) {
                    if (org.equals(account.getOrganId().toString())) {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    result.put("isAlert", Boolean.TRUE);
                    result.put("targetType", map.get("targetType"));
                    result.put("title", map.get("title"));
                    result.put("iconUrl", map.get("iconUrl"));
                    result.put("targetUrl", map.get("targetUrl"));
                } else {
                    result.put("isAlert", Boolean.FALSE);
                }
            } else {
                result.put("isAlert", Boolean.FALSE);
            }
        } else {
            result.put("isAlert", Boolean.FALSE);
        }
        return result;
    }

    public void save(List<QuestionnaireAssginStudentsDomain> ql) {
        questionnaireAssginStudentsJdbc.insertQuestionnaireAssgin(ql);
    }

    public Long saveQuestionnaireAssgin(QuestionnaireAssgin questionnaireAssgin) {
        return questionnaireAssginJdbc.insertQuestionnaireAssgin(questionnaireAssgin);
    }

    public Map<String, Object> getAssignResult(Long quesId) {
        Map<String, Object> result = (Map<String, Object>) redisTemplate.opsForValue().get(RedisUtil.getQuesAssignResultKey(quesId));
        return result;
    }

    public Map<String, Object> saveQuestionnaireAssginV2(String accessToken, AccountDTO account, QuestionnaireAssignDTO questionnaireAssignDTO, Map<String, Object> result) {
        if (questionnaireAssignDTO.getAssignType() == 10) {
            result = saveTeachingClassQuestionnaireAssgin(accessToken, account, questionnaireAssignDTO, result);
        }
        if (questionnaireAssignDTO.getAssignType() == 20) {
            result = saveClassesQuestionnaireAssgin(accessToken, account, questionnaireAssignDTO, result);
        }
        if (questionnaireAssignDTO.getAssignType() == 30) {
            result = saveProfQuestionnaireAssgin(accessToken, account, questionnaireAssignDTO, result);
        }
        if (questionnaireAssignDTO.getAssignType() == 40) {
            result = saveCollegeQuestionnaireAssgin(accessToken, account, questionnaireAssignDTO, result);
        }
        if (questionnaireAssignDTO.getAssignType() == 50) {
            result = saveOrgQuestionnaireAssgin(accessToken, account, questionnaireAssignDTO, result);
        }
        return result;
    }

    public Map<String, Object> saveTeachingClassQuestionnaireAssgin(String accessToken, AccountDTO account, QuestionnaireAssignDTO questionnaireAssignDTO, Map<String, Object> result) {
        IdNameDomain semester = semesterService.getCurrentSemester(account.getOrganId());
        if (null == semester) {
            result.put(ApiReturnConstants.MESSAGE, "该时间不在学期内，不能分配问卷。");
            redisTemplate.opsForValue().set(RedisUtil.getQuesAssignResultKey(questionnaireAssignDTO.getQuestionnaireId()), null);
            return result;
        }
        Questionnaire q = qr.findOne(questionnaireAssignDTO.getQuestionnaireId());
        q.setStatus(QuestionnaireStatus.QUESTION_STATUS_ASSIGN);
        q = questionnaireRepository.save(q);
        Map<String, Object> redisData = new HashMap<>();
        redisData.put(ApiReturnConstants.RESULT, "10");
        redisData.put(ApiReturnConstants.DATA, "进行中");
        redisTemplate.opsForValue().set(RedisUtil.getQuesAssignResultKey(questionnaireAssignDTO.getQuestionnaireId()), redisData, 1, TimeUnit.DAYS);
        QuestionnairStudentTeachingClassInsertThread qt = new QuestionnairStudentTeachingClassInsertThread(this, orgManagerRemoteClient, questionnaireAssignDTO, q, semester, account.getId(), pushMessageRepository, pushService, accessToken, redisTemplate, messageService, log);
        qt.start();
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, q.getId());
        return result;
    }

    public Map<String, Object> saveClassesQuestionnaireAssgin(String accessToken, AccountDTO account, QuestionnaireAssignDTO questionnaireAssignDTO, Map<String, Object> result) {
        IdNameDomain semester = semesterService.getCurrentSemester(account.getOrganId());
        if (null == semester) {
            result.put(ApiReturnConstants.MESSAGE, "该时间不在学期内，不能分配问卷。");
            redisTemplate.opsForValue().set(RedisUtil.getQuesAssignResultKey(questionnaireAssignDTO.getQuestionnaireId()), null);
            return result;
        }
        Questionnaire q = qr.findOne(questionnaireAssignDTO.getQuestionnaireId());
        q.setStatus(QuestionnaireStatus.QUESTION_STATUS_ASSIGN);
        q = questionnaireRepository.save(q);
        Map<String, Object> redisData = new HashMap<>();
        redisData.put(ApiReturnConstants.RESULT, "10");
        redisData.put(ApiReturnConstants.DATA, "进行中");
        redisTemplate.opsForValue().set(RedisUtil.getQuesAssignResultKey(questionnaireAssignDTO.getQuestionnaireId()), redisData, 1, TimeUnit.DAYS);
        QuestionnairStudentClassesInsertThread qst
                = new QuestionnairStudentClassesInsertThread(questionnaireAssignDTO.getClassesIds(), q, accessToken, account.getId(), this, semester);
        qst.start();
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, q.getId());
        return result;
    }

    public Map<String, Object> saveProfQuestionnaireAssgin(String accessToken, AccountDTO account, QuestionnaireAssignDTO questionnaireAssignDTO, Map<String, Object> result) {
        IdNameDomain semester = semesterService.getCurrentSemester(account.getOrganId());
        if (null == semester) {
            result.put(ApiReturnConstants.MESSAGE, "该时间不在学期内，不能分配问卷。");
            redisTemplate.opsForValue().set(RedisUtil.getQuesAssignResultKey(questionnaireAssignDTO.getQuestionnaireId()), null);
            return result;
        }
        Questionnaire q = qr.findOne(questionnaireAssignDTO.getQuestionnaireId());
        q.setStatus(QuestionnaireStatus.QUESTION_STATUS_ASSIGN);
        q = questionnaireRepository.save(q);
        Map<String, Object> redisData = new HashMap<>();
        redisData.put(ApiReturnConstants.RESULT, "10");
        redisData.put(ApiReturnConstants.DATA, "进行中");
        redisTemplate.opsForValue().set(RedisUtil.getQuesAssignResultKey(questionnaireAssignDTO.getQuestionnaireId()), redisData, 1, TimeUnit.DAYS);
        QuestionnairStudentProfInsertThread qst = new QuestionnairStudentProfInsertThread(questionnaireAssignDTO.getProfIds(), q, accessToken, account.getId(), this, semester);
        qst.start();
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, q.getId());
        return result;
    }

    public Map<String, Object> saveCollegeQuestionnaireAssgin(String accessToken, AccountDTO account, QuestionnaireAssignDTO questionnaireAssignDTO, Map<String, Object> result) {
        IdNameDomain semester = semesterService.getCurrentSemester(account.getOrganId());
        if (null == semester) {
            result.put(ApiReturnConstants.MESSAGE, "该时间不在学期内，不能分配问卷。");
            redisTemplate.opsForValue().set(RedisUtil.getQuesAssignResultKey(questionnaireAssignDTO.getQuestionnaireId()), null);
            return result;
        }
        Questionnaire q = qr.findOne(questionnaireAssignDTO.getQuestionnaireId());
        q.setStatus(QuestionnaireStatus.QUESTION_STATUS_ASSIGN);
        q = questionnaireRepository.save(q);
        Map<String, Object> redisData = new HashMap<>();
        redisData.put(ApiReturnConstants.RESULT, "10");
        redisData.put(ApiReturnConstants.DATA, "进行中");
        redisTemplate.opsForValue().set(RedisUtil.getQuesAssignResultKey(questionnaireAssignDTO.getQuestionnaireId()), redisData, 1, TimeUnit.DAYS);
        QuestionnairStudentCollegeInsertThread qst = new QuestionnairStudentCollegeInsertThread(questionnaireAssignDTO.getCollegeIds(), q, accessToken, account.getId(), this, semester);
        qst.start();
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, q.getId());
        return result;
    }

    public Map<String, Object> saveOrgQuestionnaireAssgin(String accessToken, AccountDTO account, QuestionnaireAssignDTO questionnaireAssignDTO, Map<String, Object> result) {
        IdNameDomain semester = semesterService.getCurrentSemester(account.getOrganId());
        if (null == semester) {
            result.put(ApiReturnConstants.MESSAGE, "该时间不在学期内，不能分配问卷。");
            redisTemplate.opsForValue().set(RedisUtil.getQuesAssignResultKey(questionnaireAssignDTO.getQuestionnaireId()), null);
            return result;
        }
        Questionnaire q = qr.findOne(questionnaireAssignDTO.getQuestionnaireId());
        q.setStatus(QuestionnaireStatus.QUESTION_STATUS_ASSIGN);
        q = questionnaireRepository.save(q);
        Map<String, Object> redisData = new HashMap<>();
        redisData.put(ApiReturnConstants.RESULT, "10");
        redisData.put(ApiReturnConstants.DATA, "进行中");
        redisTemplate.opsForValue().set(RedisUtil.getQuesAssignResultKey(questionnaireAssignDTO.getQuestionnaireId()), redisData, 1, TimeUnit.DAYS);
        QuestionnairStudentOrgInsertThread qst = new QuestionnairStudentOrgInsertThread(questionnaireAssignDTO.getOrgId(), q, accessToken, account.getId(), this, semester);
        qst.start();
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, q.getId());
        return result;
    }

    public List<Long> findQuestionnaireAssginStudent(Long questionnaireAssginId) {
        return questionnaireAssginJdbc.findQuestionnaireAssginStudent(questionnaireAssginId);
    }

    public QuestionnaireAssgin findByQuestionnaireAssgin(Integer classType, Long classId, Long questionnaireId) {
        QuestionnaireAssgin q = null;
        if (classType == 10) {
            q = questionnaireAssginRepository.findByTeachingClassIdAndClassTypeAndStatusAndQuestionnaire_Id(classId, 10, "10", questionnaireId);
        } else {
            q = questionnaireAssginRepository.findByClassesIdAndClassTypeAndStatusAndQuestionnaire_Id(classId, 20, "10", questionnaireId);
        }
        return q;
    }

    /**
     * @param classesIds
     * @param accessToken
     * @param questionnaire
     * @param userId
     * @param semester
     * @Title: insertClassesStudent
     * @Description: 按照行政班分配问卷
     * @return: void
     */
    public void insertClassesStudent(List<Long> classesIds, String accessToken, Questionnaire questionnaire, Long userId, IdNameDomain semester) {
        Map<String, Object> redisData = new HashMap<>();
        redisData.put(ApiReturnConstants.RESULT, "20");
        redisData.put(ApiReturnConstants.DATA, "完成");

        List<PushMessage> messages = new ArrayList<>();
        List<QuestionnaireAssginStudentsDomain> qasdl = new ArrayList<>();
        List<Long> userIds = new ArrayList<Long>();
        List<Map<String, Object>> ml = orgManagerRemoteClient.findByClassesIds(classesIds);
        Map<Long, Long> mm = new HashMap<>();
        Map<Long, List<Long>> mml = new HashMap<>();
        if (null != ml && 0 < ml.size()) {
            for (int i = 0; i < classesIds.size(); i++) {
                List<Long> userIdAll = new ArrayList<Long>();
                try {
                    String json = orgManagerRemoteClient.get(classesIds.get(i));
                    if (!StringUtils.isEmpty(json)) {
                        Map<String, Object> map = JsonUtil.Json2Object(json);
                        QuestionnaireAssgin qa = findByQuestionnaireAssgin(20, classesIds.get(i), questionnaire.getId());
                        Long qaId = 0l;
                        if (null == qa) {
                            qa = new QuestionnaireAssgin();
                            qa.setQuestionnaire(questionnaire);
                            qa.setClassesId(classesIds.get(i));
                            if (null != map.get("code")) {
                                qa.setClassesCode(map.get("code").toString());
                            }
                            if (null != map.get("name")) {
                                qa.setClassesName(map.get("name").toString());
                            }
                            if (null != map.get("collegeId")) {
                                qa.setCollegeId(Long.valueOf(map.get("collegeId").toString()));
                            }
                            if (null != map.get("collegeName")) {
                                qa.setCollegeName(map.get("collegeName").toString());
                            }
                            if (null != map.get("professionalId")) {
                                qa.setProfId(Long.valueOf(map.get("professionalId").toString()));
                            }
                            if (null != map.get("professionalName")) {
                                qa.setProfName(map.get("professionalName").toString());
                            }
                            String teacherinfo = orgManagerRemoteClient.classesTeacherList(classesIds.get(i));
                            if (!StringUtils.isEmpty(teacherinfo)) {
                                Map<String, Object> teacherJson = JsonUtil.Json2Object(teacherinfo);
                                if (null != teacherJson.get("data")) {
                                    List<Map<String, Object>> mapdata = (List<Map<String, Object>>) teacherJson.get("data");
                                    if (null != mapdata && 0 < mapdata.size()) {
                                        if (null != mapdata.get(0).get("name")) {
                                            qa.setTeacherName(mapdata.get(0).get("name").toString());
                                        }
                                        if (null != mapdata.get(0).get("id")) {
                                            qa.setTeacherId(Long.valueOf(mapdata.get(0).get("id").toString()));
                                        }
                                    }
                                }
                            }
                            qa.setSemesterId(semester.getId());
                            qa.setCreatedBy(userId);
                            qa.setLastModifiedBy(userId);
                            qa.setClassType(ClassType.Classes.getClassTypeI());
                            qa.setStatus("10");// 分配
                            qaId = this.saveQuestionnaireAssgin(qa);
                            mm.put(qa.getClassesId(), qaId);
                        } else {
                            qaId = qa.getId();
                            mm.put(qa.getClassesId(), qaId);
                            List<Long> userIdss = findQuestionnaireAssginStudent(qaId);
                            if (null != userIdss && 0 < userIdss.size()) {
                                userIdAll.addAll(userIdss);
                            }
                        }
                        mml.put(qa.getClassesId(), userIdAll);
                    }
                } catch (Exception e) {
                    redisData.put(ApiReturnConstants.RESULT, "30");
                    redisData.put(ApiReturnConstants.DATA, e.getMessage());
                }
            }
        }
        for (Map<String, Object> map2 : ml) {
            QuestionnaireAssginStudentsDomain qd = new QuestionnaireAssginStudentsDomain();
            if (null != map2.get("classesId")) {
                qd.setClassesId(Long.valueOf(map2.get("classesId").toString()));
            }
            if (null != map2.get("classesName")) {
                qd.setClassesName(map2.get("classesName").toString());
            }
            if (null != map2.get("id")) {
                if (null != mml.get(qd.getClassesId())) {
                    if (mml.get(qd.getClassesId()).contains(Long.valueOf(map2.get("id").toString()))) {
                        continue;
                    }
                } else {
                    mml.put(qd.getClassesId(), new ArrayList<Long>());
                }
                mml.get(qd.getClassesId()).add(Long.valueOf(map2.get("id").toString()));
                qd.setStuId(Long.valueOf(map2.get("id").toString()));
                userIds.add(Long.valueOf(map2.get("id").toString()));

                PushMessage message = new PushMessage();
                message.setContent(questionnaire.getName());
                message.setFunction(PushMessageConstants.FUNCITON_QUESTIONNAIRE_NOTICE);
                message.setModule(PushMessageConstants.MODULE_QUESTIONNAIRE);
                message.setHaveRead(Boolean.FALSE);
                message.setPushTime(new Date());
                message.setTitle("问卷调查通知");
                message.setUserId(Long.valueOf(map2.get("id").toString()));
                String businessContent = "";
                message.setBusinessContent(businessContent);
                messages.add(message);
            }
            if (null != map2.get("name")) {
                qd.setStuName(map2.get("name").toString());
            }
            qd.setQuestionnaireAssginId(mm.get(qd.getClassesId()));
            qd.setStatus(10);
            qasdl.add(qd);
        }
        if (!qasdl.isEmpty()) {
            this.save(qasdl);
            //TODO 消息 保存 需要更新缓存
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

    public void insertProfStudent(List<Long> profIds, String accessToken, Questionnaire questionnaire, Long userId, IdNameDomain semester) {
        List<Long> classesIds = new ArrayList<>();
        for (Long professionalId : profIds) {
            Map<String, Object> map = orgManagerRemoteClient.droplist(professionalId, 1, Integer.MAX_VALUE);
            if (null != map && null != map.get("data")) {
                List<Map<String, Object>> data = (List<Map<String, Object>>) map.get("data");
                if (null != data && 0 < data.size()) {
                    for (Map<String, Object> map2 : data) {
                        if (null != map2.get("id")) {
                            classesIds.add(Long.valueOf(map2.get("id").toString()));
                        }
                    }
                }
            }
        }
        if (classesIds != null && classesIds.size() > 0) {
            insertClassesStudent(classesIds, accessToken, questionnaire, userId, semester);
        } else {
            Map<String, Object> redisData = new HashMap<>();
            redisData.put(ApiReturnConstants.RESULT, "20");
            redisData.put(ApiReturnConstants.DATA, "完成");
            redisTemplate.opsForValue().set(RedisUtil.getQuesAssignResultKey(questionnaire.getId()), redisData, 1, TimeUnit.DAYS);
        }
    }

    public void insertCollegeStudent(List<Long> collegeIds, String accessToken, Questionnaire questionnaire, Long userId, IdNameDomain semester) {
        List<Long> classesIds = new ArrayList<>();
        for (Long collegeId : collegeIds) {
            Map<String, Object> map = orgManagerRemoteClient.droplistcollege(collegeId, 1, Integer.MAX_VALUE);
            if (null != map && null != map.get("data")) {
                List<Map<String, Object>> data = (List<Map<String, Object>>) map.get("data");
                if (null != data && 0 < data.size()) {
                    for (Map<String, Object> map2 : data) {
                        if (null != map2.get("id")) {
                            classesIds.add(Long.valueOf(map2.get("id").toString()));
                        }
                    }
                }
            }
        }
        if (classesIds != null && classesIds.size() > 0) {
            insertClassesStudent(classesIds, accessToken, questionnaire, userId, semester);
        } else {
            Map<String, Object> redisData = new HashMap<>();
            redisData.put(ApiReturnConstants.RESULT, "20");
            redisData.put(ApiReturnConstants.DATA, "完成");
            redisTemplate.opsForValue().set(RedisUtil.getQuesAssignResultKey(questionnaire.getId()), redisData, 1, TimeUnit.DAYS);
        }
    }

    public void insertOrgStudent(Long orgId, String accessToken, Questionnaire questionnaire, Long userId, IdNameDomain semester) {
        List<Long> classesIds = new ArrayList<>();
        Map<String, Object> map = orgManagerRemoteClient.droplistorg(orgId, 1, Integer.MAX_VALUE);
        if (null != map && null != map.get("data")) {
            List<Map<String, Object>> data = (List<Map<String, Object>>) map.get("data");
            if (null != data && 0 < data.size()) {
                for (Map<String, Object> map2 : data) {
                    if (null != map2.get("id")) {
                        classesIds.add(Long.valueOf(map2.get("id").toString()));
                    }
                }
            }
        }
        if (classesIds != null && classesIds.size() > 0) {
            insertClassesStudent(classesIds, accessToken, questionnaire, userId, semester);
        } else {
            Map<String, Object> redisData = new HashMap<>();
            redisData.put(ApiReturnConstants.RESULT, "20");
            redisData.put(ApiReturnConstants.DATA, "完成");
            redisTemplate.opsForValue().set(RedisUtil.getQuesAssignResultKey(questionnaire.getId()), redisData, 1, TimeUnit.DAYS);
        }
    }

    public Map<String, Object> findQuestionnaireAssgin(Long questionnaireId, Integer classType, String name, String teacherName, Integer pageNumber, Integer pageSize,
                                                       Map<String, Object> reMap) {
        Integer startPage = (pageNumber - 1) * pageSize;
        List<QuestionnaireAssginDTOV2> qdl = questionnaireAssginJdbc.findByQuestionnaireAssginInfo(questionnaireId, classType, name, teacherName, startPage, pageSize);
        Integer total = questionnaireAssginJdbc.countByQuestionnaireAssginInfo(questionnaireId, classType, name, teacherName);
        PageDomainUtil pd = PageDomainUtil.getPage(total, pageNumber, pageSize);
        reMap.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        reMap.put(ApiReturnConstants.DATA, qdl);
        reMap.put(ApiReturnConstants.PAGE, pd);
        return reMap;
    }


}
