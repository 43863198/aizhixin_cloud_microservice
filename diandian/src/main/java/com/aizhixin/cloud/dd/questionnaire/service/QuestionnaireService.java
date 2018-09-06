package com.aizhixin.cloud.dd.questionnaire.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aizhixin.cloud.dd.appeal.domain.AppealDomain;
import com.aizhixin.cloud.dd.appeal.entity.Appeal;
import com.aizhixin.cloud.dd.common.core.ClassType;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.messege.dto.AudienceDTO;
import com.aizhixin.cloud.dd.messege.dto.MessageDTO;
import com.aizhixin.cloud.dd.messege.service.MessageService;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;
import com.aizhixin.cloud.dd.orgStructure.utils.TeacherType;
import com.aizhixin.cloud.dd.orgStructure.utils.UserType;
import com.aizhixin.cloud.dd.questionnaire.JdbcTemplate.*;
import com.aizhixin.cloud.dd.questionnaire.dto.*;
import com.aizhixin.cloud.dd.questionnaire.entity.*;
import com.aizhixin.cloud.dd.questionnaire.repository.*;
import com.aizhixin.cloud.dd.questionnaire.utils.QuestionnaireType;
import com.aizhixin.cloud.dd.remote.*;
import com.aizhixin.cloud.dd.messege.service.PushService;
import com.aizhixin.cloud.dd.rollcall.service.SemesterService;
import com.aizhixin.cloud.dd.rollcall.service.StudentService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.exception.DlEduException;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.constant.PushMessageConstants;
import com.aizhixin.cloud.dd.constant.QuestionnaireStatus;
import com.aizhixin.cloud.dd.rollcall.JdbcTemplate.QuestionnaireCensusDetailScoreQueryPaginationSQL;
import com.aizhixin.cloud.dd.rollcall.JdbcTemplate.QuestionnaireCensusQueryPaginationSQL;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.IdCountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.PageInfo;
import com.aizhixin.cloud.dd.rollcall.dto.StudentDTO;
import com.aizhixin.cloud.dd.rollcall.dto.TeachingClassesDTO;
import com.aizhixin.cloud.dd.messege.entity.PushMessage;
import com.aizhixin.cloud.dd.rollcall.repository.PushMessageRepository;
import com.aizhixin.cloud.dd.rollcall.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author xiagen
 * @ClassName: QuestionnaireService
 * @Description:
 * @date 2017年5月26日 下午1:14:06
 */
@Service
@Transactional
public class QuestionnaireService {
    @Autowired
    private QuestionnaireRepository questionnaireRepository;
    @Autowired
    private QuestionsRepository questionsRepository;
    @Autowired
    private QuestionnaireQueryJdbcTemplate questionnaireQueryJdbcTemplate;
    @Autowired
    private QuestionnaireAssginRepository assginRepository;
    @Autowired
    private QuestionnaireAssginJdbc assignJdbc;
    @Autowired
    private StandardRepository standardRepository;
    @Autowired
    private QuestionnaireAssginStudentsRepository assginStudentsRepository;
    @Autowired
    private QuestionAnswerRecordRepository questionAnswerRecordRepository;
    @Autowired
    private StudentClient studentClient;
    @Autowired
    private CourseClient courseClient;
    @Autowired
    private TeacherClient teacherClient;
    @Autowired
    private ClassesClient classesClient;
    @Autowired
    private PushMessageRepository pushMessageRepository;
    @Autowired
    private PushService pushService;
    @Autowired
    private QuestionnaireCensusDetailQueryJdbcTemplate questionnaireCensusDetailQueryJdbcTemplate;
    @Autowired
    private QuestionnaireCensusQueryJdbcTemplate questionnaireCensusQueryJdbcTemplate;
    @Autowired
    private SemesterService semesterService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private QuestionnairAssignQuery questionnairAssignQuery;
    @Autowired
    private QuestionsChoiceRepository questionsChoiceRepository;
    @Autowired
    private QuestionnaireAssignUserRepository assignUserRepository;
    @Autowired
    private QuestionnaireAssginUserJdbc assginUserJdbc;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private QuestionAnswerRecordJDBC recordJDBC;
    @Autowired
    private MessageService messageService;

    public Object saveAssignUserWeight(List<QuestionnaireAssignUserWeightDTO> list) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (list != null && list.size() > 0) {
            List<QuestionnaireAssignUser> userList = new ArrayList<>();
            for (QuestionnaireAssignUserWeightDTO item : list) {
                QuestionnaireAssignUser user = assignUserRepository.findOne(item.getId());
                if (user != null && user.getDeleteFlag() == 0) {
                    user.setWeight(item.getWeight());
                    userList.add(user);
                } else {
                    result.put(ApiReturnConstants.MESSAGE, "无权重信息");
                    result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
                    return result;
                }
            }
            assignUserRepository.save(userList);
            result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        } else {
            result.put(ApiReturnConstants.MESSAGE, "无权重信息");
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
        }
        return result;
    }

    /**
     * 分配到人
     *
     * @param dto
     * @param orgId
     * @return
     */
    @Transactional
    public Object assignUser(QuestionnaireAssignUserListDTO dto, Long orgId) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (dto.getQuestionId() != null && dto.getQuestionId() > 0) {
            Questionnaire ques = questionnaireRepository.findOne(dto.getQuestionId());
            if (ques != null && ques.getDeleteFlag() == 0) {
                if (ques.getQuesType().intValue() == QuestionnaireType.TEACHER.getType()) {
                    result = assginUserQuesType20(dto, orgId, ques);
                } else if (ques.getQuesType().intValue() == QuestionnaireType.PEER.getType()) {
                    result = assginUserQuesType30(dto, orgId, ques);
                } else {
                    result.put(ApiReturnConstants.MESSAGE, "问卷类型错误");
                    result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
                }
            } else {
                result.put(ApiReturnConstants.MESSAGE, "无问卷信息");
                result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            }
        } else {
            result.put(ApiReturnConstants.MESSAGE, "问卷id必填");
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
        }
        return result;
    }

    private Map<String, Object> assginUserQuesType20(QuestionnaireAssignUserListDTO dto, Long orgId, Questionnaire ques) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (dto.isAll()) {
            Pageable pageable = PageUtil.createNoErrorPageRequest(1, 50000);
            PageData<UserInfo> users = getUser(pageable, orgId, dto.getCollegeId(), UserType.B_TEACHER.getState(), dto.getTeacherType(), dto.getTeacherName());
            if (users != null && users.getData() != null && users.getData().size() > 0) {
                List<QuestionnaireAssignUser> newUsers = new ArrayList<>();
                List<QuestionnaireAssgin> assgins = new ArrayList<>();
                for (UserInfo item : users.getData()) {
                    QuestionnaireAssignUser d = new QuestionnaireAssignUser();
                    d.setQuesId(dto.getQuestionId());
                    d.setUserId(item.getUserId());
                    d.setUserName(item.getName());
                    d.setJobNum(item.getJobNum());
                    d.setCollegeId(item.getCollegeId());
                    d.setCollegeName(item.getCollegeName());
                    d.setUserType(UserType.B_TEACHER.getState());
                    d.setTeacherType(item.getTeacherType());
                    d.setStatus(10);
                    newUsers.add(d);
                    if (item.getTeacherType() != null && item.getTeacherType().intValue() == TeacherType.TEACHING_TEACHER.getType()) {
                        setAssgin(assgins, ques, d, orgId);
                    } else {
                        setClassAssgin(assgins, ques, d, orgId);
                    }
                }
                assignUserRepository.save(newUsers);
                assginRepository.save(assgins);
                questionnaireRepository.updateAssignNum(dto.getQuestionId());
                result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
            } else {
                result.put(ApiReturnConstants.MESSAGE, "无分配信息");
                result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            }
        } else if (dto.getUsers() != null && dto.getUsers().size() > 0) {
            List<QuestionnaireAssignUser> newUsers = new ArrayList<>();
            List<QuestionnaireAssgin> assgins = new ArrayList<>();
            for (QuestionnaireAssignUserDTO item : dto.getUsers()) {
                QuestionnaireAssignUser d = new QuestionnaireAssignUser();
                d.setQuesId(dto.getQuestionId());
                d.setUserId(item.getUserId());
                d.setUserName(item.getUserName());
                d.setJobNum(item.getJobNum());
                d.setCollegeId(item.getCollegeId());
                d.setCollegeName(item.getCollegeName());
                d.setUserType(item.getUserType());
                d.setTeacherType(item.getTeacherType());
                d.setWeight(item.getWeight());
                d.setStatus(10);
                newUsers.add(d);
                if (item.getTeacherType() != null && item.getTeacherType().intValue() == TeacherType.TEACHING_TEACHER.getType()) {
                    setAssgin(assgins, ques, d, orgId);
                } else {
                    setClassAssgin(assgins, ques, d, orgId);
                }
            }
            assignUserRepository.save(newUsers);
            assginRepository.save(assgins);
            questionnaireRepository.updateAssignNum(dto.getQuestionId());
            result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        } else {
            result.put(ApiReturnConstants.MESSAGE, "无分配信息");
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
        }
        return result;
    }

    private Map<String, Object> assginUserQuesType30(QuestionnaireAssignUserListDTO dto, Long orgId, Questionnaire ques) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (dto.isAll()) {
            Pageable pageable = PageUtil.createNoErrorPageRequest(1, 50000);
            PageData<UserInfo> users = getUserAllType(pageable, orgId, dto.getCollegeId(), null, dto.getTeacherName());
            if (users != null && users.getData() != null && users.getData().size() > 0) {
                List<QuestionnaireAssignUser> newUsers = new ArrayList<>();
                List<QuestionnaireAssgin> assgins = new ArrayList<>();
                for (UserInfo item : users.getData()) {
                    QuestionnaireAssignUser d = new QuestionnaireAssignUser();
                    d.setQuesId(dto.getQuestionId());
                    d.setUserId(item.getUserId());
                    d.setUserName(item.getName());
                    d.setJobNum(item.getJobNum());
                    d.setCollegeId(item.getCollegeId());
                    d.setCollegeName(item.getCollegeName());
                    d.setUserType(item.getUserType());
                    d.setTeacherType(item.getTeacherType());
                    d.setWeight(0L);
                    d.setStatus(10);
                    newUsers.add(d);

                    QuestionnaireAssgin qa = new QuestionnaireAssgin();
                    qa.setQuestionnaire(ques);
                    qa.setCollegeId(item.getCollegeId());
                    qa.setCollegeName(item.getCollegeName());
                    qa.setClassType(ClassType.Classes.getClassTypeI());
                    qa.setCreatedBy(item.getUserId());
                    qa.setLastModifiedBy(item.getUserId());
                    qa.setStatus("10");// 分配
                    qa.setCommitStatus(QuestionnaireStatus.DD_QUESTIONNAIRE_ASSGIN_STUDENTS_INIT);
                    assgins.add(qa);
                }
                assignUserRepository.save(newUsers);
                assginRepository.save(assgins);
                questionnaireRepository.updateAssignNum(dto.getQuestionId());
                result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
            }
        } else if (dto.getUsers() != null && dto.getUsers().size() > 0) {
            List<QuestionnaireAssignUser> newUsers = new ArrayList<>();
            List<QuestionnaireAssgin> assgins = new ArrayList<>();
            for (QuestionnaireAssignUserDTO item : dto.getUsers()) {
                QuestionnaireAssignUser d = new QuestionnaireAssignUser();
                d.setQuesId(dto.getQuestionId());
                d.setUserId(item.getUserId());
                d.setUserName(item.getUserName());
                d.setJobNum(item.getJobNum());
                d.setCollegeId(item.getCollegeId());
                d.setCollegeName(item.getCollegeName());
                d.setUserType(item.getUserType());
                d.setTeacherType(item.getTeacherType());
                d.setWeight(item.getWeight());
                d.setStatus(10);
                newUsers.add(d);

                QuestionnaireAssgin qa = new QuestionnaireAssgin();
                qa.setQuestionnaire(ques);
                qa.setCollegeId(item.getCollegeId());
                qa.setCollegeName(item.getCollegeName());
                qa.setClassType(ClassType.Classes.getClassTypeI());
                qa.setCreatedBy(item.getUserId());
                qa.setLastModifiedBy(item.getUserId());
                qa.setStatus("10");// 分配
                qa.setCommitStatus(QuestionnaireStatus.DD_QUESTIONNAIRE_ASSGIN_STUDENTS_INIT);
                assgins.add(qa);
            }
            assignUserRepository.save(newUsers);
            assginRepository.save(assgins);
            questionnaireRepository.updateAssignNum(dto.getQuestionId());
            result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        } else {
            result.put(ApiReturnConstants.MESSAGE, "无分配信息");
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
        }
        return result;
    }

    private void setAssgin(List<QuestionnaireAssgin> list, Questionnaire ques, QuestionnaireAssignUser qau, Long orgId) {
        String str = orgManagerRemoteService.getTeachingclassIdBy(qau.getUserId(), null);
        if (!StringUtils.isEmpty(str)) {
            JSONArray array = JSONArray.fromString(str);
            if (array != null && array.length() > 0) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject item = array.getJSONObject(i);
                    if (item != null) {
                        String cstr = orgManagerRemoteService.getteachingclassclassesByTeachingId(item.getLong("id"));
                        if (!StringUtils.isEmpty(cstr)) {
                            JSONObject cobj = JSONObject.fromString(cstr);
                            if (cobj != null && cobj.getJSONArray("data") != null && cobj.getJSONArray("data").length() > 0) {
                                JSONArray carray = cobj.getJSONArray("data");
                                for (int j = 0; j < carray.length(); j++) {
                                    JSONObject cb = carray.getJSONObject(j);
                                    QuestionnaireAssgin qa = new QuestionnaireAssgin();
                                    qa.setQuestionnaire(ques);
                                    if (cb.get("id") != null && !cb.get("id").toString().equals("null")) {
                                        qa.setClassesId(Long.parseLong(cb.get("id").toString()));
                                    }
                                    if (cb.get("name") != null && !cb.get("name").toString().equals("null")) {
                                        qa.setClassesName(cb.get("name").toString());
                                    }
                                    if (item.get("id") != null && !item.get("id").toString().equals("null")) {
                                        qa.setTeachingClassId(Long.parseLong(item.get("id").toString()));
                                    }
                                    if (item.get("name") != null && !item.get("name").toString().equals("null")) {
                                        qa.setTeachingClassName(item.get("name").toString());
                                    }
                                    if (item.get("courseId") != null && !item.get("courseId").toString().equals("null")) {
                                        qa.setCourseId(Long.parseLong(item.get("courseId").toString()));
                                    }
                                    if (item.get("courseName") != null && !item.get("courseName").toString().equals("null")) {
                                        qa.setCourseName(item.get("courseName").toString());
                                    }
                                    qa.setCollegeId(qau.getCollegeId());
                                    qa.setCollegeName(qau.getCollegeName());
                                    qa.setClassType(ClassType.Classes.getClassTypeI());
                                    qa.setCreatedBy(qau.getUserId());
                                    qa.setLastModifiedBy(qau.getUserId());
                                    qa.setStatus("10");// 分配
                                    qa.setCommitStatus(QuestionnaireStatus.DD_QUESTIONNAIRE_ASSGIN_STUDENTS_INIT);
                                    list.add(qa);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void setClassAssgin(List<QuestionnaireAssgin> list, Questionnaire ques, QuestionnaireAssignUser qau, Long orgId) {
        List<IdNameDomain> clist = orgManagerRemoteService.getClassesByTeacher(qau.getUserId());
        if (clist != null && clist.size() > 0) {
            for (IdNameDomain item : clist) {
                if (item != null) {
                    QuestionnaireAssgin qa = new QuestionnaireAssgin();
                    qa.setQuestionnaire(ques);
                    qa.setClassesId(item.getId());
                    qa.setClassesName(item.getName());
                    qa.setCollegeId(qau.getCollegeId());
                    qa.setCollegeName(qau.getCollegeName());
                    qa.setClassType(ClassType.Classes.getClassTypeI());
                    qa.setCreatedBy(qau.getUserId());
                    qa.setLastModifiedBy(qau.getUserId());
                    qa.setStatus("10");// 分配
                    qa.setCommitStatus(QuestionnaireStatus.DD_QUESTIONNAIRE_ASSGIN_STUDENTS_INIT);
                    list.add(qa);
                }
            }
        }
    }

    /**
     * 查询分配
     *
     * @param quesId
     * @param teacherType
     * @param name
     * @return
     */
    public PageData<QuestionnaireAssignUser> getAssignUser(Pageable pageable, Long quesId, Integer teacherType, String name) {
        if (StringUtils.isEmpty(name)) {
            name = "";
        }
        Page<QuestionnaireAssignUser> page = null;
        if (teacherType != null && teacherType > 0) {
            page = assignUserRepository.findByQuesIdAndTeacherTypeAndUserName(pageable, quesId, teacherType, name);
        } else {
            page = assignUserRepository.findByQuesIdAndUserName(pageable, quesId, name);
        }
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageSize(page.getSize());
        pageDomain.setPageNumber(page.getNumber());
        pageDomain.setTotalElements(page.getTotalElements());
        pageDomain.setTotalPages(page.getTotalPages());
        PageData<QuestionnaireAssignUser> pageData = new PageData<>();
        pageData.setData(page.getContent());
        pageData.setPage(pageDomain);
        return pageData;
    }

    /**
     * 查询教师用户
     *
     * @param pageable
     * @param orgId
     * @param userType
     * @param teacherType
     * @param name
     * @return
     */
    public PageData<UserInfo> getUser(Pageable pageable, Long orgId, Long collegeId, Integer userType, Integer teacherType, String name) {
        if (org.springframework.util.StringUtils.isEmpty(name)) {
            name = "";
        }
        if (userType == null || userType < 1) {
            userType = 60;
        }
        Page<UserInfo> page = null;
        if (collegeId != null && collegeId > 0 && teacherType != null && teacherType > 0) {
            page = userInfoRepository.findByOrgIdAndCollegeIdAndUserTypeAndIsHTeacherAndTeacherTypeAndNameLikeOrOrgIdAndCollegeIdAndUserTypeAndIsHTeacherAndTeacherTypeAndJobNumLike(pageable, orgId, collegeId, userType, true, teacherType, name, orgId, collegeId, userType, true, teacherType, name);
        } else if (collegeId != null && collegeId > 0) {
            page = userInfoRepository.findByOrgIdAndCollegeIdAndUserTypeAndIsHTeacherAndNameLikeOrOrgIdAndCollegeIdAndUserTypeAndIsHTeacherAndJobNumLike(pageable, orgId, collegeId, userType, true, name, orgId, collegeId, userType, true, name);
        } else if (teacherType != null && teacherType > 0 && userType == 60) {
            page = userInfoRepository.findByOrgIdAndUserTypeAndIsHTeacherAndTeacherTypeAndNameLikeOrOrgIdAndUserTypeAndIsHTeacherAndTeacherTypeAndJobNumLike(pageable, orgId, userType, true, teacherType, name, orgId, userType, true, teacherType, name);
        } else {
            page = userInfoRepository.findByOrgIdAndUserTypeAndIsHTeacherAndNameLikeOrOrgIdAndUserTypeAndIsHTeacherAndJobNumLike(pageable, orgId, userType, true, name, orgId, userType, true, name);
        }
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageSize(page.getSize());
        pageDomain.setPageNumber(page.getNumber());
        pageDomain.setTotalElements(page.getTotalElements());
        pageDomain.setTotalPages(page.getTotalPages());
        PageData<UserInfo> pageData = new PageData<>();
        pageData.setData(page.getContent());
        pageData.setPage(pageDomain);
        return pageData;
    }

    /**
     * 查询用户
     *
     * @param pageable
     * @param orgId
     * @param userType
     * @param name
     * @return
     */
    public PageData<UserInfo> getUserAllType(Pageable pageable, Long orgId, Long collegeId, Integer userType, String name) {
        if (org.springframework.util.StringUtils.isEmpty(name)) {
            name = "";
        }
        Page<UserInfo> page = null;
        if (collegeId != null && collegeId > 0 && userType != null && userType > 0) {
            page = userInfoRepository.findByOrgIdAndCollegeIdAndUserTypeAndNameLikeOrOrgIdAndCollegeIdAndUserTypeAndJobNumLike(pageable, orgId, collegeId, userType, name, orgId, collegeId, userType, name);
        } else if (collegeId != null && collegeId > 0) {
            page = userInfoRepository.findByOrgIdAndCollegeIdAndNameLikeOrOrgIdAndCollegeIdAndJobNumLike(pageable, orgId, collegeId, name, orgId, collegeId, name);
        } else if (userType != null && userType > 0) {
            page = userInfoRepository.findByOrgIdAndUserTypeAndNameLikeOrOrgIdAndUserTypeAndJobNumLike(pageable, orgId, userType, name, orgId, userType, name);
        } else {
            page = userInfoRepository.findByOrgIdAndNameLikeOrOrgIdAndJobNumLike(pageable, orgId, name, orgId, name);
        }
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageSize(page.getSize());
        pageDomain.setPageNumber(page.getNumber());
        pageDomain.setTotalElements(page.getTotalElements());
        pageDomain.setTotalPages(page.getTotalPages());
        PageData<UserInfo> pageData = new PageData<>();
        pageData.setData(page.getContent());
        pageData.setPage(pageDomain);
        return pageData;
    }

    /**
     * 删除分配
     *
     * @param dto
     * @return
     */
    public Object delAssignUser(QuestionnaireAssignUserListDTO dto) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (dto.getQuestionId() != null && dto.getQuestionId() > 0) {
            if (dto.isAll()) {
                assginUserJdbc.deleteByNameAndTeacherType(dto.getQuestionId(), dto.getTeacherName(), dto.getTeacherType());
                assignJdbc.deleteByNameAndTeacherType(dto.getQuestionId(), dto.getTeacherName(), dto.getTeacherType());
                questionnaireRepository.updateAssignNum(dto.getQuestionId());
                result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
            } else if (dto.getUsers() != null && dto.getUsers().size() > 0) {
                for (QuestionnaireAssignUserDTO item : dto.getUsers()) {
                    if (item != null) {
                        assignUserRepository.deleteByUserId(dto.getQuestionId(), item.getUserId());
                        assginRepository.deleteByCreatedBy(dto.getQuestionId(), item.getUserId());
                    }
                }
                questionnaireRepository.updateAssignNum(dto.getQuestionId());
                result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
            } else {
                result.put(ApiReturnConstants.MESSAGE, "用户id必填");
                result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            }
        } else {
            result.put(ApiReturnConstants.MESSAGE, "问卷id必填");
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
        }
        return result;
    }

    /**
     * save[问卷保存] 创建人: MEIHUA.LI 创建时间: 2016年11月9日 上午9:40:54
     *
     * @param dto
     * @return
     * @Title: save
     * @since CodingExample Ver(编码范例查看) 1.1
     */
    public Object save(QuestionnaireDTO dto, Long organId) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            // 问卷信息
            Questionnaire questionnaire = new Questionnaire();
            questionnaire.setId(dto.getId());
            questionnaire.setName(dto.getName());
            questionnaire.setTotalQuestions(dto.getQuestions().size());
            questionnaire.setTotalScore(dto.getTotalScore());
            questionnaire.setStatus(QuestionnaireStatus.QUESTION_STATUS_INIT);
            questionnaire.setDeleteFlag(DataValidity.VALID.getState());
            questionnaire.setOrganId(organId);
            questionnaire.setQComment(dto.isQcomment());
            questionnaire.setChoiceQuestion(dto.isChoiceQuestion());
            questionnaire.setQuantification(dto.isQuantification());
            questionnaire.setEndDate(DateFormatUtil.parse((DateFormatUtil.formatShort(dto.getEndDate()) + " 23:59:59"),
                    DateFormatUtil.FORMAT_LONG));
            //默认学生评教
            if (dto.getQuesType() == null || dto.getQuesType() < 1) {
                dto.setQuesType(QuestionnaireType.STUDENT.getType());
            }
            questionnaire.setQuesType(dto.getQuesType());
            questionnaireRepository.save(questionnaire);
            // 问卷题目
            List<QuestionDTO> questionDtos = dto.getQuestions();
            if (questionnaire.isChoiceQuestion()) {
                List<QuestionsChoice> qcl = new ArrayList<>();
                for (QuestionDTO questionDto : questionDtos) {
                    Questions question = new Questions();
                    BeanUtils.copyProperties(questionDto, question);
                    question.setQuestionnaire(questionnaire);
                    question.setQuestionsChoice(null);
                    question.setId(null);
                    if (!questionDto.getQuestionChioce().isEmpty()) {
                        for (QuestionsChoiceDTO questionsChoiceDTO : questionDto.getQuestionChioce()) {
                            QuestionsChoice qc = new QuestionsChoice();
                            qc.setId(null);
                            BeanUtils.copyProperties(questionsChoiceDTO, qc);
                            qc.setId(null);
                            qc.setQuestions(question);
                            qcl.add(qc);
                        }
                    }
                }
                if (!qcl.isEmpty()) {
                    questionsChoiceRepository.save(qcl);
                }
            } else {
                List<Questions> ql = new ArrayList<>();
                for (QuestionDTO questionDto : questionDtos) {
                    Questions question = new Questions();
                    BeanUtils.copyProperties(questionDto, question);
                    question.setQuestionnaire(questionnaire);
                    ql.add(question);
                }
                questionsRepository.save(ql);
            }
            result.put("trueMSG", true);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("message", "保存失败" + e);
            result.put("falseMSG", false);
        }
        return result;
    }

    /**
     * deleteQuestionniare[现在是物理删除，待修改为逻辑删除] 创建人: MEIHUA.LI 创建时间: 2016年11月9日 上午9:53:22
     *
     * @param id
     * @return
     * @throws Exception
     * @Title: deleteQuestionniare
     * @since CodingExample Ver(编码范例查看) 1.1
     */
    public Object deleteQuestionniare(Long id) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            questionnaireRepository.delete(id);
            result.put("trueMSG", true);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("errorMSG", false);
            throw new Exception(e);
        }
        return result;
    }

    /**
     * 问卷列表查询
     *
     * @param pageable
     * @param organId
     * @return
     * @author meihua.li
     */
    @Transactional(readOnly = true)
    public PageData<QuestionnaireDTO> query(Pageable pageable, Long organId, Integer type) {
        PageData<QuestionnaireDTO> pageData = PageData.getPageData(pageable);
        try {
            Page<Questionnaire> page = questionnaireRepository.findByOrganIdAndQuesTypeAndDeleteFlag(pageable, organId, type, DataValidity.VALID.getState());
            List<Questionnaire> content = page.getContent();
            List<QuestionnaireDTO> qDtoList = null;
            if (content != null && content.size() > 0) {
                qDtoList = new ArrayList<>();
                Set<Long> qset = new HashSet<>();
                for (Questionnaire questionnaire : content) {
                    qset.add(questionnaire.getId());
                }
                // 待查询分配班级
                List<IdCountDTO> idList = assginRepository.findAllByQuestionnaires(qset);
                Map questionnaireMap = new HashMap();
                for (IdCountDTO idCountDTO : idList) {
                    questionnaireMap.put(idCountDTO.getId(), idCountDTO.getCount());
                }
                QuestionnaireDTO dto = null;
                for (Questionnaire questionnaire : content) {
                    dto = new QuestionnaireDTO();
                    BeanUtils.copyProperties(questionnaire, dto);
                    dto.setCreateDate(questionnaire.getCreatedDate());
                    dto.setQuestions(null);
                    dto.setTeachingNum(questionnaireMap.get(questionnaire.getId()) == null ? 0
                            : Integer.parseInt(questionnaireMap.get(questionnaire.getId()) + ""));
                    Date endDate = questionnaire.getEndDate();
                    if (null != endDate) {
                        if (endDate.after(new Date())) {
                            dto.setIsEnd("");
                        } else {
                            dto.setIsEnd("已截至");
                        }
                    }
                    qDtoList.add(dto);
                }
            }
            pageData.setData(qDtoList);
            PageData.setPageData(pageData, page.getTotalElements(), page.getTotalPages());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageData;
    }

    @Transactional(readOnly = true)
    public QuestionnaireDTO queryDetail(Long questionnaireID) {
        QuestionnaireDTO questionnaireDTO = new QuestionnaireDTO();
        Questionnaire questionnaire = questionnaireRepository.findOne(questionnaireID);
        if (questionnaire != null) {
            BeanUtils.copyProperties(questionnaire, questionnaireDTO);
            questionnaireDTO.setCreateDate(questionnaire.getCreatedDate());
            questionnaireDTO.setQcomment(questionnaire.isQComment());
            List<Questions> questions = questionsRepository.findAllByQuestionnaireId(questionnaireID);
            if (questions != null) {
                List<QuestionDTO> questionDtos = new ArrayList<QuestionDTO>();
                if (!questionnaire.isChoiceQuestion()) {
                    for (Questions question : questions) {
                        QuestionDTO questionDTO = new QuestionDTO();
                        BeanUtils.copyProperties(question, questionDTO);
                        questionDtos.add(questionDTO);
                    }
                } else {
                    for (Questions question : questions) {
                        QuestionDTO questionDTO = new QuestionDTO();
                        BeanUtils.copyProperties(question, questionDTO);
                        List<QuestionsChoiceDTO> qcdl = new ArrayList<>();
                        for (QuestionsChoice questionsChoice : question.getQuestionsChoice()) {
                            QuestionsChoiceDTO qcd = new QuestionsChoiceDTO();
                            qcd.setChoice(questionsChoice.getChoice());
                            qcd.setContent(questionsChoice.getContent());
                            qcd.setScore(questionsChoice.getScore());
                            qcd.setId(questionsChoice.getId());
                            qcdl.add(qcd);
                            // BeanUtils.copyProperties(questionsChoice, qcd);
                        }
                        questionDTO.setQuestionChioce(qcdl);
                        questionDtos.add(questionDTO);
                    }
                }
                questionnaireDTO.setQuestions(questionDtos);
            }
        }
        return questionnaireDTO;
    }

    public Map<String, Object> updateEndTime(Long quesId, String endDate) {
        Map<String, Object> result = new HashMap<String, Object>();
        Questionnaire questionnaire = questionnaireRepository.findOne(quesId);
        if (questionnaire == null || questionnaire.getDeleteFlag() == DataValidity.INVALID.getState()) {
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            result.put(ApiReturnConstants.MESSAGE, "没有问卷信息");
            return result;
        }
        try {
            questionnaire.setEndDate(DateFormatUtil.parse(endDate + " 23:59:59", DateFormatUtil.FORMAT_LONG));
        } catch (ParseException e) {
            e.printStackTrace();
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            result.put(ApiReturnConstants.MESSAGE, "结束日期格式错误");
            return result;
        }
        questionnaireRepository.save(questionnaire);
        result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        return result;
    }

    /**
     * update[修改，待优化] 创建人: MEIHUA.LI 创建时间: 2016年11月9日 上午9:40:29
     *
     * @param dto
     * @return
     * @throws Exception
     * @Title: update
     * @since CodingExample Ver(编码范例查看) 1.1
     */
    public Object update(QuestionnaireDTO dto, Long organId) throws Exception {
        questionnaireRepository.delete(dto.getId());
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Questionnaire questionnaire = new Questionnaire();
            questionnaire.setId(null);
            questionnaire.setName(dto.getName());
            questionnaire.setTotalQuestions(dto.getQuestions().size());
            questionnaire.setTotalScore(dto.getTotalScore());
            questionnaire.setStatus(QuestionnaireStatus.QUESTION_STATUS_INIT);
            questionnaire.setEndDate(dto.getEndDate());
            questionnaire.setDeleteFlag(QuestionnaireStatus.QUESTION_DELETEFLAG_NORMAL);
            questionnaire.setQuantification(dto.isQuantification());
            questionnaire.setChoiceQuestion(dto.isChoiceQuestion());
            questionnaire.setOrganId(organId);
            questionnaire.setQComment(dto.isQcomment());
            if (dto.getQuesType() == null || dto.getQuesType() < 1) {
                dto.setQuesType(QuestionnaireType.STUDENT.getType());
            }
            questionnaire.setQuesType(dto.getQuesType());

            questionnaire = questionnaireRepository.save(questionnaire);
            List<QuestionDTO> questionDtos = dto.getQuestions();
            if (questionnaire.isChoiceQuestion()) {
                List<QuestionsChoice> qcl = new ArrayList<>();
                for (QuestionDTO questionDto : questionDtos) {
                    Questions question = new Questions();
                    BeanUtils.copyProperties(questionDto, question);
                    question.setQuestionnaire(questionnaire);
                    question.setQuestionsChoice(null);
                    question.setId(null);
                    if (!questionDto.getQuestionChioce().isEmpty()) {
                        for (QuestionsChoiceDTO questionsChoiceDTO : questionDto.getQuestionChioce()) {
                            QuestionsChoice qc = new QuestionsChoice();
                            qc.setId(null);
                            BeanUtils.copyProperties(questionsChoiceDTO, qc);
                            qc.setId(null);
                            qc.setQuestions(question);
                            qcl.add(qc);
                        }
                    }
                }
                if (!qcl.isEmpty()) {
                    questionsChoiceRepository.save(qcl);
                }
            } else {
                List<Questions> ql = new ArrayList<>();
                for (QuestionDTO questionDto : questionDtos) {
                    Questions question = new Questions();
                    BeanUtils.copyProperties(questionDto, question);
                    question.setQuestionnaire(questionnaire);
                    ql.add(question);
                }
                questionsRepository.save(ql);
            }
            result.put("trueMSG", true);
        } catch (BeansException e) {
            e.printStackTrace();
            result.put("falseMSG", false);
        }
        return result;
    }

    public QuestionnaireDetailDTO findStudentInfo(
            QuestionnaireDetailDTO questionnaireDetailDTO,
            QuestionnaireAssgin questionnaireAssgin,
            QuestionnaireAssginStudents questionnaireAssginStudents)
            throws JsonParseException, JsonMappingException, IOException {
        String json = studentClient.findByStudentId(questionnaireAssginStudents.getStudentId());
        Map<String, Object> map = JsonUtil.Json2Object(json);
        if (map.get("name") != null) {
            questionnaireDetailDTO.setStudentName(map.get("name").toString());
        }
        if (map.get("classesName") != null) {
            questionnaireDetailDTO.setClassName(map.get("classesName").toString());
        }
        String jsonCourse = courseClient.findByCourseId(questionnaireAssgin.getCourseId());
        Map<String, Object> mapCourse = JsonUtil.Json2Object(jsonCourse);
        if (mapCourse.get("code") != null) {
            questionnaireDetailDTO.setCourseCode(mapCourse.get("code").toString());
        }
        if (mapCourse.get("name") != null) {
            questionnaireDetailDTO.setCourseName(mapCourse.get("name").toString());
        }
        String jsonTeacher = teacherClient.findByTeacherId(questionnaireAssgin.getTeacherId());
        Map<String, Object> mapTeacher = JsonUtil.Json2Object(jsonTeacher);
        if (mapTeacher.get("name") != null) {
            questionnaireDetailDTO.setTeacherName(mapTeacher.get("name").toString());
        }
        return questionnaireDetailDTO;
    }

    public QuestionnaireAssgin findQuestionnaireAssginById(Long questionnaireAssginId) {
        return assginRepository.findOne(questionnaireAssginId);
    }

    public QuestionnaireAssginStudents findQuestionnaireAssGinStudentById(Long questionnaireAssGinStudentId) {
        return assginStudentsRepository.findOne(questionnaireAssGinStudentId);
    }

    public QuestionnaireAssignUser findQuestionnaireAssignUserById(Long questionnaireAssGinStudentId) {
        return assignUserRepository.findOne(questionnaireAssGinStudentId);
    }

    public QuestionnaireAssginStudents findQuestionnaireAssGinStudentByAssginIdAndStudentId(Long studentId,
                                                                                            QuestionnaireAssgin questionnaireAssgin) {
        return assginStudentsRepository.findAllBystudentIdAndQuestionnaireAssgin(studentId,
                questionnaireAssgin);
    }

    public List<QuestionAnswerRecord> findQuestionAnswerRecordByQuestionnaireAssginStudentId(
            Long questionnaireAssginStudentId) {
        return questionAnswerRecordRepository.findAllByQuestionnaireAssginStudentsId(questionnaireAssginStudentId);
    }

    public Object assigned(QuestionnaireAssignDTO questionnaireAssignDTO, String accessToken, AccountDTO account) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            IdNameDomain semester = semesterService.getCurrentSemester(account.getOrganId());
            if (null == semester) {
                Map map = new HashMap();
                map.put(ApiReturnConstants.MESSAGE, "该时间不在学期内，不能分配问卷。");
                return map;
            }
            Long questionnaireId = questionnaireAssignDTO.getQuestionnaireId();
            List<TeachingClassesDTO> teachingClasses = questionnaireAssignDTO.getTeachingClasses();
            if (null != teachingClasses && teachingClasses.size() > 0) {
                // 根据id获取阅卷详细信息
                Questionnaire questionnaire = questionnaireRepository.findOne(questionnaireId);
                questionnaire.setStatus(QuestionnaireStatus.QUESTION_STATUS_ASSIGN);
                List<QuestionnaireAssginStudents> qasList = new ArrayList<QuestionnaireAssginStudents>();
                List<PushMessage> messages = new ArrayList<PushMessage>();
                List<Long> userIds = new ArrayList<Long>();
                for (TeachingClassesDTO teachingClass : teachingClasses) {
                    Long teachingClassesId = teachingClass.getTeachingClassesId();
                    String teachingClassName = teachingClass.getTeachingClassName();
                    String teachingClassCode = teachingClass.getTeachingClassCode();
                    Long collegeId = teachingClass.getCollegeId();
                    String collegeName = teachingClass.getCollegeName();
                    Long teacherId = teachingClass.getTeacherId();
                    String teacherName = teachingClass.getTeacherName();
                    Long courseId = teachingClass.getCourseId();
                    String courseName = teachingClass.getCourseName();
                    // 插入 dd_questionnaire_assgin
                    QuestionnaireAssgin questionnaireAssgin = new QuestionnaireAssgin();
                    questionnaireAssgin.setQuestionnaire(questionnaire);
                    questionnaireAssgin.setCollegeId(collegeId);
                    questionnaireAssgin.setCollegeName(collegeName);
                    questionnaireAssgin.setTeacherId(teacherId);
                    questionnaireAssgin.setTeacherName(teacherName);
                    questionnaireAssgin.setCourseName(courseName);
                    questionnaireAssgin.setCourseId(courseId);
                    questionnaireAssgin.setTeachingClassId(teachingClassesId);
                    questionnaireAssgin.setTeachingClassCode(teachingClassCode);
                    questionnaireAssgin.setTeachingClassName(teachingClassName);
                    questionnaireAssgin.setSemesterId(semester.getId());
                    questionnaireAssgin.setCreatedBy(account.getId());
                    questionnaireAssgin.setLastModifiedBy(account.getId());
                    questionnaireAssgin.setStatus("10");// 分配
                    assginRepository.save(questionnaireAssgin);

                    List<StudentDTO> listStudent = studentService.listStudents(teachingClassesId);
                    if (listStudent != null) {
                        QuestionnaireAssginStudents questionnaireAssginStudents = null;
                        for (StudentDTO studentDTO : listStudent) {
                            questionnaireAssginStudents = new QuestionnaireAssginStudents();
                            questionnaireAssginStudents.setQuestionnaireAssgin(questionnaireAssgin);
                            questionnaireAssginStudents.setStudentId(studentDTO.getStudentId());
                            questionnaireAssginStudents.setStudentName(studentDTO.getStudentName());
                            questionnaireAssginStudents.setClassesId(studentDTO.getClassesId());
                            questionnaireAssginStudents.setClassesName(studentDTO.getClassesName());
                            questionnaireAssginStudents
                                    .setStatus(QuestionnaireStatus.DD_QUESTIONNAIRE_ASSGIN_STUDENTS_INIT);
                            qasList.add(questionnaireAssginStudents);
                            PushMessage message = new PushMessage();
                            message.setContent("您有新的调查问卷。");
                            message.setFunction(PushMessageConstants.FUNCITON_QUESTIONNAIRE_NOTICE);
                            message.setModule(PushMessageConstants.MODULE_QUESTIONNAIRE);
                            message.setHaveRead(Boolean.FALSE);
                            message.setPushTime(new Date());
                            message.setTitle("问卷调查通知");
                            message.setUserId(studentDTO.getStudentId());
                            String businessContent = "";
                            message.setBusinessContent(businessContent);
                            messages.add(message);
                            userIds.add(studentDTO.getStudentId());
                        }
                    }
                }
                questionnaireRepository.save(questionnaire);
                //TODO 消息 保存 需要更新缓存
                pushMessageRepository.save(messages);
                pushService.listPush(accessToken, "您有一个新的问卷调查", "问卷调查通知", "问卷调查通知", userIds);
                qasList = assginStudentsRepository.save(qasList);

                //----新消息服务----start
                List<AudienceDTO> audienceList = new ArrayList<>();
                for (QuestionnaireAssginStudents item : qasList) {
                    QuestionnaireCensusDTO d = new QuestionnaireCensusDTO();
                    BeanUtils.copyProperties(item.getQuestionnaireAssgin().getQuestionnaire(), d);
                    d.setQuestionnaireAssignId(item.getQuestionnaireAssgin().getId());
                    d.setQuestionnaireAssignStudentId(item.getId());
                    d.setCollegeId(item.getQuestionnaireAssgin().getCollegeId());
                    d.setCollegeName(item.getQuestionnaireAssgin().getCollegeName());
                    d.setTeacherId(item.getQuestionnaireAssgin().getTeacherId());
                    d.setTeacherName(item.getQuestionnaireAssgin().getTeacherName());
                    d.setCourseId(item.getQuestionnaireAssgin().getCourseId());
                    d.setCourseCode(item.getQuestionnaireAssgin().getCourseCode());
                    d.setCourseName(item.getQuestionnaireAssgin().getCourseName());
                    d.setSystemDate(new Date());
                    d.setQuestionnaireStatus(item.getQuestionnaireAssgin().getStatus());
                    d.setClassId(item.getClassesId());
                    d.setClassName(item.getClassesName());
                    if (item.getQuestionnaireAssgin().getWeightScore() != null) {
                        d.setWeight(item.getQuestionnaireAssgin().getWeightScore().longValue());
                    }
                    audienceList.add(new AudienceDTO(item.getStudentId(), d));
                }
                messageService.push("问卷调查通知", "您有新的调查问卷。", PushMessageConstants.FUNCITON_QUESTIONNAIRE_NOTICE, audienceList);
                //----新消息服务----end
            }
            result.put("trueMSG", true);
        } catch (Exception e) {
            result.put("trueMSG", false);
            e.printStackTrace();
        }
        return result;
    }

    public void cancleAssigned(Long questionnaireAssignId) throws Exception {
        QuestionnaireAssgin questionnaireAssgin = assginRepository.findOne(questionnaireAssignId);
        long id = questionnaireAssgin.getQuestionnaire().getId();
        Questionnaire questionnaire = questionnaireRepository.findOne(id);
        int count = assginRepository.queryCountByQuqestionIdAndstatus(questionnaire, "10");
        if (count == 1 && questionnaire.getStatus() == 20) {
            questionnaire.setStatus(10);
        }
        questionnaireAssgin.setStatus("20");
        assginRepository.save(questionnaireAssgin);
        questionnaireRepository.save(questionnaire);
    }

    public void cancleAssignedList(List<Long> questionnaireAssignId) throws Exception {
        List<QuestionnaireAssgin> questionnaireAssgins = assginRepository
                .findByIdInAndDeleteFlag(questionnaireAssignId, DataValidity.VALID.getState());
        int i = 0;
        long id = 0l;
        if (null != questionnaireAssgins && 0 < questionnaireAssgins.size()) {
            for (QuestionnaireAssgin questionnaireAssgin : questionnaireAssgins) {
                if (i == 0) {
                    id = questionnaireAssgin.getQuestionnaire().getId();
                }
                questionnaireAssgin.setStatus("20");
                i++;
            }
            assginRepository.save(questionnaireAssgins);
            Questionnaire questionnaire = questionnaireRepository.findOne(id);
            int count = assginRepository.queryCountByQuqestionIdAndstatus(questionnaire, "10");
            if (count == 0 && questionnaire.getStatus() == 20) {
                questionnaire.setStatus(10);
            }
            questionnaireRepository.save(questionnaire);
        }
    }

    public void cancleAssignedAll(Long questionnaireId) throws Exception {
        List<QuestionnaireAssgin> questionnaireAssgins = assginRepository
                .findByQuestionnaire_IdInAndDeleteFlag(questionnaireId, DataValidity.VALID.getState());
        if (null != questionnaireAssgins && 0 < questionnaireAssgins.size()) {
            for (QuestionnaireAssgin questionnaireAssgin : questionnaireAssgins) {
                questionnaireAssgin.setStatus("20");
            }
            assginRepository.save(questionnaireAssgins);
            Questionnaire questionnaire = questionnaireRepository.findOne(questionnaireId);
            questionnaire.setStatus(10);
            questionnaireRepository.save(questionnaire);
        }
    }


    public QuestionnaireCensusDetailDTO queryCensusDetail(Integer offset, Integer limit, Long questionnaireAssginId,
                                                          Long classId) throws JsonParseException, JsonMappingException, IOException {
        QuestionnaireCensusDetailDTO questionnaireCensusDetailDTO = new QuestionnaireCensusDetailDTO();
        // 1.查询 问卷名称，老师名称，课堂名称（编号）
        QuestionnaireAssgin questionnaireAssgin = assginRepository.findOne(questionnaireAssginId);
        questionnaireCensusDetailDTO.setQuestionnaireAssginId(questionnaireAssginId);
        String jsonTeacher = teacherClient.findByTeacherId(questionnaireAssgin.getTeacherId());
        Map<String, Object> r = JsonUtil.Json2Object(jsonTeacher);
        if (r.get("name") != null) {
            questionnaireCensusDetailDTO.setTeacherName(r.get("name").toString());
        }
        String jsonCourse = courseClient.findByCourseId(questionnaireAssgin.getCourseId());
        Map<String, Object> rc = JsonUtil.Json2Object(jsonCourse);
        if (rc.get("name") != null) {
            questionnaireCensusDetailDTO.setCourseName(rc.get("name").toString());
        }
        if (rc.get("code") != null) {
            questionnaireCensusDetailDTO.setCouresCode(rc.get("code").toString());
        }
        questionnaireCensusDetailDTO.setQuestionnaireId(questionnaireAssgin.getQuestionnaire().getId());
        questionnaireCensusDetailDTO.setQuestionnaireName(questionnaireAssgin.getQuestionnaire().getName());
        try {
            // 查询班级, 班级人数s
            PageInfo page = questionnaireCensusDetailQueryJdbcTemplate.getPageInfo(Integer.MAX_VALUE, 1,
                    questionnaireCensusDetailQueryJdbcTemplate.classAndTotallPeoNum, null,
                    new QuestionnaireCensusDetailClassInfoQueryPaginationSQL(questionnaireAssginId, classId), null,
                    null);
            List<QuestionnaireCensusDetailDTO> classAndNums = page.getData();
            List<String> classNames = new ArrayList<String>();
            int totalCount = 0;
            for (QuestionnaireCensusDetailDTO questionnaireCensusDetailDTO2 : classAndNums) {
                String json = classesClient.getById(questionnaireCensusDetailDTO2.getClassId());
                Map<String, Object> c = JsonUtil.Json2Object(json);
                if (c.get("name") != null) {
                    classNames.add(c.get("name").toString());
                }
                totalCount += questionnaireCensusDetailDTO2.getTotalCount();
            }
            questionnaireCensusDetailDTO.setClassNames(classNames);
            questionnaireCensusDetailDTO.setTotalCount(totalCount);
            // 查询提交人数 以及总分
            page = questionnaireCensusDetailQueryJdbcTemplate.getPageInfo(Integer.MAX_VALUE, 1,
                    questionnaireCensusDetailQueryJdbcTemplate.commitCountAndTotalScore, null,
                    new QuestionnaireCensusDetailScoreQueryPaginationSQL(questionnaireAssginId, classId,
                            QuestionnaireStatus.DD_QUESTIONNAIRE_ASSGIN_STUDENTS_FINISH),
                    null, null);
            List<QuestionnaireCensusDetailDTO> commitCountAndTotalScore = page.getData();
            if (commitCountAndTotalScore != null) {
                questionnaireCensusDetailDTO.setCommitCount(commitCountAndTotalScore.get(0).getCommitCount());
                questionnaireCensusDetailDTO.setTotalScore(commitCountAndTotalScore.get(0).getTotalScore());
            }
            float averageScore = questionnaireCensusDetailDTO.getAverageScore();
            // 查询评级
            List<Standard> Standards = standardRepository
                    .findByQuestionnaireId(questionnaireAssgin.getQuestionnaire().getId());
            for (Standard standard : Standards) {
                int min = standard.getMixScore();
                int max = standard.getMaxScore();
                if (averageScore >= min && averageScore < max) {
                    questionnaireCensusDetailDTO.setLeaveName(standard.getLevelName());
                }
            }
            Page<QuestionnaireAssginStudents> p = null;
            Sort sort = new Sort(Direction.DESC, "commitDate", "studentName");
            if (StringUtils.isBlank(String.valueOf(classId == null ? "" : classId))) {
                p = assginStudentsRepository.findByQuestionnaireAssgin_Id(questionnaireAssginId,
                        PageUtil.createNoErrorPageRequestAndSort(offset, limit, sort));
            } else {
                p = assginStudentsRepository.findByClassesIdAndQuestionnaireAssgin_Id(classId,
                        questionnaireAssginId, PageUtil.createNoErrorPageRequestAndSort(offset, limit, sort));
            }
            for (int i = 0; i < p.getContent().size(); i++) {
                String json = classesClient.getById(p.getContent().get(i).getClassesId());
                Map<String, Object> c = JsonUtil.Json2Object(json);
                String jsonStudent = studentClient.findByStudentId(p.getContent().get(i).getStudentId());
                Map<String, Object> map = JsonUtil.Json2Object(jsonStudent);
                if (map.get("name") != null) {
                    p.getContent().get(i).setStudentName(map.get("name").toString());
                }
                if (c.get("name") != null) {
                    p.getContent().get(i).setClassesName(c.get("name").toString());
                }
            }
            questionnaireCensusDetailDTO.setPage(p);
        } catch (DlEduException e) {
            e.printStackTrace();
        }
        return questionnaireCensusDetailDTO;
    }

    @SuppressWarnings({"rawtypes", "static-access"})
    public PageInfo queryQuestionnaireCensus(int limit, int offset, String name, String teacherName, String courseName,
                                             Long organId)
            throws DlEduException, DataAccessException, JsonParseException, JsonMappingException, IOException {
        List<Long> teacherId = new ArrayList<>();
        List<Long> courseId = new ArrayList<>();
        PageInfo<QuestionnaireCensusDTO> page = new PageInfo<QuestionnaireCensusDTO>();
        List<Map<String, Object>> lt = null;
        List<Map<String, Object>> lc = null;
        if (teacherName != null && !"".equals(teacherName)) {
            String json = teacherClient.findByTeacherList(organId, teacherName, 1, 300);
            Map<String, Object> map = JsonUtil.Json2Object(json);
            lt = (List<Map<String, Object>>) map.get("data");
            for (int i = 0; i < lt.size(); i++) {
                teacherId.add(Long.valueOf(lt.get(i).get("id").toString()));
            }
            if (teacherId.isEmpty()) {
                page.setData(null);
                page.setLimit(limit);
                page.setOffset(offset);
                page.setPageCount(0);
                page.setTotalCount(Long.valueOf(0 + ""));
                return page;
            }
        }
        if (courseName != null && !"".equals(courseName)) {
            String json = courseClient.getExcellentCourseLikeName(organId, courseName, 1, 300);
            Map<String, Object> map = JsonUtil.Json2Object(json);
            lc = (List<Map<String, Object>>) map.get("data");
            for (int i = 0; i < lc.size(); i++) {
                courseId.add(Long.valueOf(lc.get(i).get("id").toString()));
            }
            if (courseId.isEmpty()) {
                page.setData(null);
                page.setLimit(limit);
                page.setOffset(offset);
                page.setPageCount(0);
                page.setTotalCount(Long.valueOf(0 + ""));
                return page;
            }
        }
        page = questionnaireCensusQueryJdbcTemplate.getPageInfo(limit, offset,
                questionnaireCensusQueryJdbcTemplate.questionnaireMapper, null,
                new QuestionnaireCensusQueryPaginationSQL(name, organId), teacherId, courseId);
        for (int i = 0; i < page.getData().size(); i++) {
            for (int j = 0; j < teacherId.size(); j++) {
                if (((QuestionnaireCensusDTO) page.getData().get(i)).getTeacherId().longValue() == teacherId.get(j)
                        .longValue()) {
                    if (lt.get(j).get("name") != null) {
                        ((QuestionnaireCensusDTO) page.getData().get(i))
                                .setTeacherName(lt.get(j).get("name").toString());
                    }
                }
            }
            for (int a = 0; a < courseId.size(); a++) {
                if (((QuestionnaireCensusDTO) page.getData().get(i)).getCourseId().longValue() == courseId.get(a)
                        .longValue()) {
                    if (lc.get(a).get("code") != null) {
                        ((QuestionnaireCensusDTO) page.getData().get(i))
                                .setCourseCode(lc.get(a).get("code").toString());
                    }
                    if (lc.get(a).get("name") != null) {
                        ((QuestionnaireCensusDTO) page.getData().get(i))
                                .setCollegeName(lc.get(a).get("name").toString());
                    }
                }
            }
        }
        return page;
    }

    public List<Map> queryQuestionnaireClass(Long questionnaireAssginId)
            throws DlEduException, DataAccessException, JsonParseException, JsonMappingException, IOException {
        // 查询班级, 班级人数
        List<Map> temp = new ArrayList<Map>();
        PageInfo page = questionnaireCensusDetailQueryJdbcTemplate.getPageInfo(Integer.MAX_VALUE, 1,
                questionnaireCensusDetailQueryJdbcTemplate.classAndTotallPeoNum, null,
                new QuestionnaireCensusDetailClassInfoQueryPaginationSQL(questionnaireAssginId, null), null, null);
        List<QuestionnaireCensusDetailDTO> classAndNums = page.getData();
        for (QuestionnaireCensusDetailDTO questionnaireCensusDetailDTO : classAndNums) {
            Map tempMap = new HashMap();
            String json = classesClient.getById(questionnaireCensusDetailDTO.getClassId());
            Map<String, Object> map = JsonUtil.Json2Object(json);
            if (map.get("name") != null) {
                tempMap.put("className", map.get("name").toString());
            } else {
                tempMap.put("className", null);
            }
            tempMap.put("classId", questionnaireCensusDetailDTO.getClassId());
            temp.add(tempMap);
        }
        return temp;
    }

    public QuestionnaireDetailDTO getQuestionnaireDetailDTO(Long questionnaireAssginId,
                                                            Long questionnaireAssginStudentId) {
        QuestionnaireDetailDTO result = new QuestionnaireDetailDTO();
        QuestionnaireAssgin questionnaireAssgin = findQuestionnaireAssginById(questionnaireAssginId);
        QuestionnaireDTO questionnaireDTO = queryDetail(questionnaireAssgin.getQuestionnaire().getId());
        Map map = new HashMap();
        if (questionnaireDTO.getQuesType().intValue() == QuestionnaireType.STUDENT.getType()) {
            QuestionnaireAssginStudents questionnaireAssginStudents = findQuestionnaireAssGinStudentById(questionnaireAssginStudentId);
            int status = questionnaireAssginStudents.getStatus();
            if (QuestionnaireStatus.DD_QUESTIONNAIRE_ASSGIN_STUDENTS_FINISH == status) {
                List<QuestionAnswerRecord> temp = findQuestionAnswerRecordByQuestionnaireAssginStudentId(questionnaireAssginStudentId);
                for (QuestionAnswerRecord questionAnswerRecord : temp) {
                    QuestionAnswerScoreDTO qasd = new QuestionAnswerScoreDTO();
                    qasd.setScore(questionAnswerRecord.getScore());
                    qasd.setAnswer(questionAnswerRecord.getAnswer());
                    map.put(questionAnswerRecord.getQuestions().getId(), qasd);
                }
                List<QuestionDTO> questions = questionnaireDTO.getQuestions();
                for (QuestionDTO questionDTO : questions) {
                    QuestionAnswerScoreDTO qasd = (QuestionAnswerScoreDTO) map.get(questionDTO.getId());
                    if (null != qasd) {
                        if (qasd.getScore() == null) {
                            questionDTO.setActualScore(0);
                        } else {
                            questionDTO.setActualScore(qasd.getScore());
                        }

                        questionDTO.setAnswer(qasd.getAnswer());
                    }
                }
                result.setQuestions(questions);
            }
            BeanUtils.copyProperties(questionnaireDTO, result);
            result.setQcomment(questionnaireDTO.isQcomment());
            result.setQuestionnaireAssginId(questionnaireAssginId);
            result.setQuestionnaireAssginStudentId(questionnaireAssginStudentId);
            result.setTotalActualScore(questionnaireAssginStudents.getScore() == null ? 0 : questionnaireAssginStudents.getScore());
            result.setCourseName(questionnaireAssgin.getCourseName());
            result.setTeacherName(questionnaireAssgin.getTeacherName());
            result.setComment(questionnaireAssginStudents.getComment());
            result.setClassType(questionnaireAssgin.getClassType());
            result.setCommit(questionnaireAssginStudents.getStatus());
        } else {
            int status = questionnaireAssgin.getCommitStatus();
            if (QuestionnaireStatus.DD_QUESTIONNAIRE_ASSGIN_STUDENTS_FINISH == status) {
                List<QuestionAnswerRecord> temp = recordJDBC.findByQuestionnaireAssginStudentsId(questionnaireAssginId);
                for (QuestionAnswerRecord questionAnswerRecord : temp) {
                    QuestionAnswerScoreDTO qasd = new QuestionAnswerScoreDTO();
                    qasd.setScore(questionAnswerRecord.getScore());
                    qasd.setAnswer(questionAnswerRecord.getAnswer());
                    map.put(questionAnswerRecord.getQuestions().getId(), qasd);
                }
                List<QuestionDTO> questions = questionnaireDTO.getQuestions();
                for (QuestionDTO questionDTO : questions) {
                    QuestionAnswerScoreDTO qasd = (QuestionAnswerScoreDTO) map.get(questionDTO.getId());
                    if (null != qasd) {
                        if (qasd.getScore() == null) {
                            questionDTO.setActualScore(0);
                        } else {
                            questionDTO.setActualScore(qasd.getScore());
                        }

                        questionDTO.setAnswer(qasd.getAnswer());
                    }
                }
                result.setQuestions(questions);
            }
            BeanUtils.copyProperties(questionnaireDTO, result);
            result.setQcomment(questionnaireDTO.isQcomment());
            result.setQuestionnaireAssginId(questionnaireAssginId);
            result.setQuestionnaireAssginStudentId(questionnaireAssginStudentId);
            result.setTotalActualScore(questionnaireAssgin.getScore() == null ? 0 : questionnaireAssgin.getScore());
            result.setCourseName(questionnaireAssgin.getCourseName());
            result.setTeacherName(questionnaireAssgin.getTeacherName());
            result.setComment(questionnaireAssgin.getComment());
            result.setClassType(questionnaireAssgin.getClassType());
            result.setCommit(questionnaireAssgin.getCommitStatus());
            result.setClassName(questionnaireAssgin.getClassesName());
        }

        return result;
    }

    public List findByQuestionnaireInfo(Long id, Integer type, Integer status) {
        List<QuestionnaireCensusDTO> page = questionnairAssignQuery.getQuestionnaireInfo(id, type, status);
        return page;
    }

    public List<QuestionnaireCensusDTO> findByQuestionnaireUser(Long userId, Integer status) {
        List<QuestionnaireCensusDTO> list = assginUserJdbc.findByUserIdAndStatus(userId, status);
        return list;
    }

    public Object saveQuestions(QuestionsDTO questionsDTO, Long userId) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Long id = questionsDTO.getId();
            if (id == null || id < 1) {
                id = questionsDTO.getQuestionnaireId();
            }
            Questionnaire questionnaire = questionnaireRepository.findOne(id);
            List<QuestionDTO> questionDTOs = questionsDTO.getQuestions();
            if (questionnaire.getQuesType().intValue() == QuestionnaireType.STUDENT.getType()) {
                for (QuestionDTO questionDTO : questionDTOs) {
                    QuestionAnswerRecord QuestionAnswerRecord = new QuestionAnswerRecord();
                    QuestionnaireAssginStudents questionnaireAssginStudents = new QuestionnaireAssginStudents();
                    questionnaireAssginStudents.setId(questionsDTO.getQuestionnaireAssginStudentId());
                    QuestionAnswerRecord.setQuestionnaireAssginStudents(questionnaireAssginStudents);
                    Questions questions = new Questions();
                    questions.setId(questionDTO.getId());
                    QuestionAnswerRecord.setQuestions(questions);
                    QuestionAnswerRecord.setScore(questionDTO.getActualScore().intValue());
                    QuestionAnswerRecord.setAnswer(questionDTO.getAnswer());
                    questionAnswerRecordRepository.save(QuestionAnswerRecord);
                }
                if (!org.springframework.util.StringUtils.isEmpty(questionsDTO.getComment())) {
                    assginStudentsRepository.updateScore(questionsDTO.getQuestionnaireAssginStudentId(),
                            questionsDTO.getTotalActualScore(), QuestionnaireStatus.DD_QUESTIONNAIRE_ASSGIN_STUDENTS_FINISH,
                            new Date(), questionsDTO.getComment());
                } else {
                    assginStudentsRepository.updateScore(questionsDTO.getQuestionnaireAssginStudentId(),
                            questionsDTO.getTotalActualScore(), QuestionnaireStatus.DD_QUESTIONNAIRE_ASSGIN_STUDENTS_FINISH,
                            new Date());
                }
            } else {
                float weight = 0;
                float totalWeight = 0;
                if (questionnaire.getQuesType().intValue() == QuestionnaireType.PEER.getType()) {
                    QuestionnaireAssignUser assignUser = assignUserRepository.findByQuesIdAndUserIdAndDeleteFlag(id, userId, DataValidity.VALID.getState());
                    if (assignUser != null && assignUser.getWeight() != null && assignUser.getWeight() > 0) {
                        weight = assignUser.getWeight().floatValue();
                    }
                }
                for (QuestionDTO questionDTO : questionDTOs) {
                    QuestionAnswerRecord record = new QuestionAnswerRecord();
                    QuestionnaireAssginStudents questionnaireAssginStudents = new QuestionnaireAssginStudents();
                    questionnaireAssginStudents.setId(questionsDTO.getQuestionnaireAssginId());//AssginId
                    record.setQuestionnaireAssginStudents(questionnaireAssginStudents);
                    Questions questions = new Questions();
                    questions.setId(questionDTO.getId());
                    record.setQuestions(questions);
                    record.setScore(questionDTO.getActualScore().intValue());
                    if (weight > 0) {
                        record.setWeightScore(record.getScore().intValue() * weight / 100);
                    } else {
                        record.setWeightScore(record.getScore().floatValue());
                    }
                    totalWeight += record.getWeightScore();
                    record.setAnswer(questionDTO.getAnswer());
                    questionAnswerRecordRepository.save(record);
                }
                if (!org.springframework.util.StringUtils.isEmpty(questionsDTO.getComment())) {
                    assginRepository.updateScore(questionsDTO.getQuestionnaireAssginId(),
                            questionsDTO.getTotalActualScore(), QuestionnaireStatus.DD_QUESTIONNAIRE_ASSGIN_STUDENTS_FINISH,
                            new Date(), questionsDTO.getComment(), totalWeight);
                } else {
                    assginRepository.updateScore(questionsDTO.getQuestionnaireAssginId(),
                            questionsDTO.getTotalActualScore(), QuestionnaireStatus.DD_QUESTIONNAIRE_ASSGIN_STUDENTS_FINISH,
                            new Date(), totalWeight);
                }
            }

            result.put("trueMSG", true);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("trueMSG", false);
        }
        return result;
    }

    public PageData<QuestionnareAssignQueryDTO> queryAssignQuestionare(Long id, String courseName, String teacherName, Integer offset, Integer limit, Long assginId) {
        return questionnairAssignQuery.queryAssign(offset, limit, courseName, teacherName, id, assginId, null);
    }

    public PageData<QuestionnareAssignQueryDTO> queryAssignQuestionareList(Long id, String courseName, String teacherName, Integer offset, Integer limit, Long assginId, Long collegeId) {
        return questionnairAssignQuery.queryAssign(offset, limit, courseName, teacherName, id, assginId, collegeId);
    }

    public PageData<QuestionnaireAssginStudents> regularStatistics(Long assginId, Integer pageNumber,
                                                                   Integer pageSize) {
        Pageable pageable = PageUtil.createNoErrorPageRequestAndSort(pageNumber, pageSize,
                new Sort(new Sort.Order(Sort.Direction.ASC, "studentId")));
        PageData<QuestionnaireAssginStudents> pageData = PageData.getPageData(pageable);
        Page<QuestionnaireAssginStudents> page = assginStudentsRepository
                .findByQuestionnaireAssgin_Id(assginId, pageable);
        pageData.setData(page.getContent());
        PageData.setPageData(pageData, page.getTotalElements(), page.getTotalPages());
        return pageData;
    }

    public List<PartStatisticsDTO> partStatistics(Long questionnaireAssginId) {
        return questionnairAssignQuery.partStatistics(questionnaireAssginId);
    }

    public Set<Long> queryTeachingClassIdByQuId(Long questionnaireId) {
        return assginRepository.findTeachingClassIdsByQuestionnaireId(questionnaireId);
    }

    public List<Questions> queryQuestions(Long questionnaireID) {
        return questionsRepository.findAllByQuestionnaireId(questionnaireID);
    }
}
