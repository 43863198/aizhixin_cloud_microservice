package com.aizhixin.cloud.dd.questionnaire.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aizhixin.cloud.dd.questionnaire.utils.QuestionnaireType;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.utils.PageDomainUtil;
import com.aizhixin.cloud.dd.questionnaire.JdbcTemplate.QuestionnaireExportJdbc;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.questionnaire.dto.QuestionnaireDataDTO;
import com.aizhixin.cloud.dd.questionnaire.dto.QuestionnaireQuestionChoiceDTO;
import com.aizhixin.cloud.dd.questionnaire.dto.QuestionnaireQuestionDataDTO;
import com.aizhixin.cloud.dd.questionnaire.dto.QuestionnaireStudentCommentDTO;
import com.aizhixin.cloud.dd.rollcall.dto.StudentInfoDTOV2;
import com.aizhixin.cloud.dd.questionnaire.entity.Questionnaire;
import com.aizhixin.cloud.dd.questionnaire.entity.QuestionnaireAssgin;
import com.aizhixin.cloud.dd.questionnaire.entity.Questions;
import com.aizhixin.cloud.dd.questionnaire.entity.QuestionsChoice;
import com.aizhixin.cloud.dd.questionnaire.repository.QuestionnaireAssginRepository;
import com.aizhixin.cloud.dd.questionnaire.repository.QuestionnaireRepository;
import com.aizhixin.cloud.dd.questionnaire.repository.QuestionsRepository;
import com.aizhixin.cloud.dd.rollcall.utils.JsonUtil;
import com.aizhixin.cloud.dd.rollcall.utils.MathUtil;

@Slf4j
@Service
@Transactional
public class QuestionnaireExport {
    @Autowired
    private QuestionnaireExportJdbc questionnaireExportJdbc;
    @Autowired
    private QuestionnaireRepository questionnaireRepository;
    @Autowired
    private QuestionsRepository questionsRepository;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteClient;
    @Autowired
    private QuestionnaireAssginRepository questionnaireAssginRepository;

    /**
     * @param questionnaireId
     * @Title: getQuestionaireData
     * @Description: 问卷信息及答题信息统计
     * @return: QuestionnaireDataDTO
     */
    public QuestionnaireDataDTO getQuestionaireData(Long questionnaireId) {
        QuestionnaireDataDTO qdd = new QuestionnaireDataDTO();
        Questionnaire q = questionnaireRepository.findOne(questionnaireId);
        if (null != q) {
            qdd.setQuesType(q.getQuesType());
            qdd.setQuestionnaireName(q.getName());
            qdd.setEndDate(q.getEndDate());
            if (q.isQuantification()) {
                qdd.setQuestionnaireTotalScore(q.getTotalScore() + "");
                // 获取问卷提交人数平均分
                Double qAvg = null;
                if (q.getQuesType().intValue() == QuestionnaireType.STUDENT.getType()) {
                    qAvg = questionnaireExportJdbc.avgScoreStu(questionnaireId);
                } else if (q.getQuesType().intValue() == QuestionnaireType.TEACHER.getType()) {
                    qAvg = questionnaireExportJdbc.avgScoreUser(questionnaireId);
                } else if (q.getQuesType().intValue() == QuestionnaireType.PEER.getType()) {
                    qAvg = questionnaireExportJdbc.avgScorePeer(questionnaireId);
                }
                if (null != qAvg) {
                    qdd.setAvgScore(String.valueOf(qAvg));
                }
            } else {
                qdd.setQuestionnaireTotalScore("-");
                qdd.setAvgScore("-");
            }
        }
        if (q.getQuesType().intValue() == QuestionnaireType.STUDENT.getType()) {
            // 获取问卷调查人数
            Integer totalPeple = questionnaireExportJdbc.countTotalNumStu(questionnaireId);
            qdd.setTotalPeple(totalPeple);
            // 提交人数
            Integer commitPeple = questionnaireExportJdbc.countCommitTotalStu(questionnaireId);
            qdd.setCommitNum(commitPeple);
            // 未提交人数
            Integer noCommitPeple = totalPeple - commitPeple;
            qdd.setNoCommitNum(noCommitPeple);
        } else {
            // 获取问卷调查人数
            Integer totalPeple = questionnaireExportJdbc.countTotalNumUser(questionnaireId);
            qdd.setTotalPeple(totalPeple);
            // 提交人数
            Integer commitPeple = questionnaireExportJdbc.countCommitTotalUser(questionnaireId);
            qdd.setCommitNum(commitPeple);
            // 未提交人数
            Integer noCommitPeple = totalPeple - commitPeple;
            qdd.setNoCommitNum(noCommitPeple);
        }

        if (qdd.getTotalPeple() == 0) {
            qdd.setCommitZb(MathUtil.doubleToBFBString(0.00));
            qdd.setNoCommitZb(MathUtil.doubleToBFBString(0.00));
        } else {
            // 提交人数占比
            Double cd = Double.parseDouble(qdd.getCommitNum() + "") / Double.parseDouble(qdd.getTotalPeple() + "");
            String commitPepleZb = MathUtil.doubleToBFBString(cd);
            qdd.setCommitZb(commitPepleZb);
            // 未提交人数占比
            Double nd = 1 - cd;
            String noCommitPepleZb = MathUtil.doubleToBFBString(nd);
            qdd.setNoCommitZb(noCommitPepleZb);
        }
        List<QuestionnaireQuestionDataDTO> qqddl = totalQuestionInfo(q);
        qdd.setQuestionnaireQuestionDataDTOs(qqddl);
        return qdd;
    }

    public List<QuestionnaireQuestionDataDTO> totalQuestionInfo(Questionnaire q) {
        List<Questions> ql = questionsRepository.findByQuestionnaireIdOrderByNoAsc(q.getId());
        Map<Long, String> map = null;
        if (q.isQuantification()) {
            if (q.getQuesType().intValue() == QuestionnaireType.STUDENT.getType()) {
                map = questionnaireExportJdbc.countQuestionAvgScoreStu(q.getId());
            } else if (q.getQuesType().intValue() == QuestionnaireType.TEACHER.getType()) {
                map = questionnaireExportJdbc.countQuestionAvgScoreUser(q.getId());
            } else if (q.getQuesType().intValue() == QuestionnaireType.PEER.getType()) {
                map = questionnaireExportJdbc.countQuestionAvgScorePeer(q.getId());
            }
        }
        List<QuestionnaireQuestionDataDTO> qqddl = new ArrayList<>();
        if (null != ql && 0 < ql.size()) {
            for (Questions questions : ql) {
                QuestionnaireQuestionDataDTO qqdd = new QuestionnaireQuestionDataDTO();
                // 试题内容
                qqdd.setContent(questions.getName());
                qqdd.setQA(questions.isQA());
                // 试题序号
                qqdd.setNo(questions.getNo());
                if (q.isQuantification()) {
                    // 试题总分
                    qqdd.setScore(questions.getScore() + "");
                } else {
                    // 试题总分
                    qqdd.setScore("-");
                }
                // 这道题的平均分
                if (null != map) {
                    if (!StringUtils.isEmpty(map.get(questions.getId()))) {
                        qqdd.setAvgScore(map.get(questions.getId()));
                    }
                } else {
                    qqdd.setAvgScore("-");
                }
                if (q.isChoiceQuestion()) {
                    if (questions.isRadio()) {
                        // 单选
                        qqdd.setQuestionType(10);
                    } else {
                        // 多选
                        qqdd.setQuestionType(20);
                    }
                    Map<String, Object> zbStuMap = new HashMap<>();
                    if (q.getQuesType().intValue() == QuestionnaireType.STUDENT.getType()) {
                        zbStuMap = questionnaireExportJdbc.getQuestionChoiceZbStu(questions);
                    } else {
                        zbStuMap = questionnaireExportJdbc.getQuestionChoiceZbUser(questions);
                    }
                    List<QuestionnaireQuestionChoiceDTO> qqcdl = new ArrayList<>();
                    for (QuestionsChoice questionsChoice : questions.getQuestionsChoice()) {
                        QuestionnaireQuestionChoiceDTO qqcd = new QuestionnaireQuestionChoiceDTO();
                        qqcd.setChoice(questionsChoice.getChoice());
                        if (q.isQuantification()) {
                            qqcd.setScore(questionsChoice.getScore());
                        } else {
                            qqcd.setScore("-");
                        }
                        Double choiceZb = 0.00;
                        if (zbStuMap.get("total") != null && zbStuMap.get(questionsChoice.getChoice()) != null) {
                            Double a = Double.parseDouble(zbStuMap.get("total").toString());
                            Double b = Double.parseDouble(zbStuMap.get(questionsChoice.getChoice()).toString());
                            if (a == 0) {
                                choiceZb = 0.00;
                            } else {
                                choiceZb = b / a;
                            }
                        }
                        qqcd.setChoiceZb(MathUtil.doubleToBFBString(choiceZb));
                        qqcdl.add(qqcd);
                    }
                    // 试题选项占比
                    qqdd.setChoices(qqcdl);
                } else {
                    // 非选择题
                    qqdd.setQuestionType(30);
                }
                qqddl.add(qqdd);
            }
        }
        return qqddl;
    }

    /**
     * @param questionnaireId
     * @param pageNumber
     * @param pageSize
     * @Title: findNoCommitPeple
     * @Description: 获取未提交人员名单
     * @return: List<StudentInfoDTOV2>
     */
    public List<StudentInfoDTOV2> findNoCommitPeple(Long questionnaireId, Integer pageNumber, Integer pageSize) {
        Integer pageStart = (pageNumber - 1) * pageSize;
        List<Long> noCommit = questionnaireExportJdbc.findByNoCommitPelpe(questionnaireId, pageStart, pageSize);
        List<StudentInfoDTOV2> sidvl = new ArrayList<>();
        Map<Long, String> teacherInfo = new HashMap<>();
        if (null != noCommit && 0 < noCommit.size()) {
            List<Map<String, Object>> mapList = orgManagerRemoteClient.findByIds(noCommit);
            int i = 1;
            if (null != mapList && 0 < mapList.size()) {
                for (Long stuId : noCommit) {
                    for (Map<String, Object> map : mapList) {
                        if (stuId.longValue() == Long.valueOf(map.get("id").toString()).longValue()) {
                            StudentInfoDTOV2 sid = new StudentInfoDTOV2();
                            sid.setNo(pageStart + i);
                            sid.setStuId(stuId);
                            if (null != map.get("name")) {
                                sid.setStuName(map.get("name").toString());
                            }
                            if (null != map.get("classesName")) {
                                sid.setClassesName(map.get("classesName").toString());
                            }
                            if (null != map.get("professionalName")) {
                                sid.setProfName(map.get("professionalName").toString());
                            }
                            if (null != map.get("collegeName")) {
                                sid.setCollegeName(map.get("collegeName").toString());
                            }
                            if (null != map.get("classesId")) {
                                Long classesId = Long.valueOf(map.get("classesId").toString());
                                if (null != teacherInfo.get(classesId)) {
                                    sid.setTeacherName(teacherInfo.get(classesId));
                                } else {
                                    String json = orgManagerRemoteClient.classesTeacherList(classesId);
                                    if (!StringUtils.isEmpty(json)) {
                                        try {
                                            Map<String, Object> mapData = JsonUtil.Json2Object(json);
                                            if (null != mapData && null != mapData.get("data")) {
                                                List<Map<String, Object>> dataList = (List<Map<String, Object>>) mapData
                                                        .get("data");
                                                if (null != dataList && 0 < dataList.size()) {
                                                    Map<String, Object> d = dataList.get(0);
                                                    if (null != d.get("name")) {
                                                        sid.setTeacherName(d.get("name").toString());
                                                        teacherInfo.put(classesId, d.get("name").toString());
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {
                                            log.warn("Exception", e);
                                        }
                                    }
                                }
                            }
                            sidvl.add(sid);
                            i++;
                        }
                    }
                }
            }
        }
        return sidvl;
    }

    public List<StudentInfoDTOV2> findNoCommitUser(Long questionnaireId, Integer pageNumber, Integer pageSize) {
        Integer startPage = (pageNumber - 1) * pageSize;
        List<StudentInfoDTOV2> qscdl = questionnaireExportJdbc.findByNoCommitUserComment(questionnaireId,
                startPage, pageSize);
        return qscdl;
    }

    /**
     * @param questionnaireId
     * @param pageNumber
     * @param pageSize
     * @param result
     * @return
     */
    public Map<String, Object> findNoCommitPeplePage(Long questionnaireId, Integer pageNumber, Integer pageSize,
                                                     Map<String, Object> result) {
        Questionnaire questionnaire = questionnaireRepository.findOne(questionnaireId);
        List<StudentInfoDTOV2> stuInfo;
        Integer total;
        if (questionnaire.getQuesType().intValue() == QuestionnaireType.STUDENT.getType()) {
            stuInfo = findNoCommitPeple(questionnaireId, pageNumber, pageSize);
            total = questionnaireExportJdbc.countByNoCommitPelpe(questionnaireId);
        } else {
            stuInfo = findNoCommitUser(questionnaireId, pageNumber, pageSize);
            total = questionnaireExportJdbc.countByNoCommitUser(questionnaireId);
        }
        PageDomainUtil pd = PageDomainUtil.getPage(total, pageNumber, pageSize);
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, stuInfo);
        result.put(ApiReturnConstants.PAGE, pd);
        return result;
    }

    public void getStuInfo(List<QuestionnaireStudentCommentDTO> qscdl) {
        if (qscdl != null && 0 < qscdl.size()) {
            List<Long> ids = new ArrayList<>();
            Map<Long, AccountDTO> map = new HashMap<>();
            for (QuestionnaireStudentCommentDTO questionnaireStudentCommentDTO : qscdl) {
                ids.add(questionnaireStudentCommentDTO.getStuId());
            }
            if (!ids.isEmpty()) {
                List<Map<String, Object>> dataList = orgManagerRemoteClient.findByIds(ids);
                if (null != dataList && 0 < dataList.size()) {
                    for (Map<String, Object> map2 : dataList) {
                        AccountDTO a = new AccountDTO();
                        if (null != map2.get("id")) {
                            a.setId(Long.valueOf(map2.get("id").toString()));
                        }
                        if (null != map2.get("name")) {
                            a.setName(map2.get("name").toString());
                        }
                        if (null != map2.get("classesName")) {
                            a.setClassesName(map2.get("classesName").toString());
                        }
                        if (null != map2.get("professionalName")) {
                            a.setProfessionalName(map2.get("professionalName").toString());
                        }
                        if (null != map2.get("collegeName")) {
                            a.setCollegeName(map2.get("collegeName").toString());
                        }
                        map.put(a.getId(), a);
                    }
                }
                for (QuestionnaireStudentCommentDTO questionnaireStudentCommentDTO : qscdl) {
                    AccountDTO a = map.get(questionnaireStudentCommentDTO.getStuId());
                    if (null != a) {
                        if (!StringUtils.isEmpty(a.getName())) {
                            questionnaireStudentCommentDTO.setStuName(a.getName());
                        }
                        if (!StringUtils.isEmpty(a.getClassesName())) {
                            questionnaireStudentCommentDTO.setClassesName(a.getClassesName());
                        }
                        if (!StringUtils.isEmpty(a.getProfessionalName())) {
                            questionnaireStudentCommentDTO.setProfName(a.getProfessionalName());
                        }
                        if (!StringUtils.isEmpty(a.getCollegeName())) {
                            questionnaireStudentCommentDTO.setCollegeName(a.getCollegeName());
                        }
                    }
                }
            }
        }
    }

    /**
     * @param questionnaireId
     * @param pageNumber
     * @param pageSize
     * @Title: findByQuestionnaireComment
     * @Description: 获取问卷评语
     * @return: List<QuestionnaireStudentCommentDTO>
     */
    public List<QuestionnaireStudentCommentDTO> findByQuestionnaireComment(Long questionnaireId, Integer pageNumber, Integer pageSize) {
        Integer startPage = (pageNumber - 1) * pageSize;
        List<QuestionnaireStudentCommentDTO> qscdl = questionnaireExportJdbc.findByCommitPelpeComment(questionnaireId,
                startPage, pageSize);
        getStuInfo(qscdl);
        return qscdl;
    }

    public List<QuestionnaireStudentCommentDTO> findByQuestionnaireUserComment(Long questionnaireId, Integer pageNumber, Integer pageSize) {
        Integer startPage = (pageNumber - 1) * pageSize;
        List<QuestionnaireStudentCommentDTO> qscdl = questionnaireExportJdbc.findByCommitUserComment(questionnaireId,
                startPage, pageSize);
        return qscdl;
    }

    public Map<String, Object> findByQuestionnaireCommentInfo(Long questionnaireId, Integer pageNumber,
                                                              Integer pageSize, Map<String, Object> result) {
        Questionnaire questionnaire = questionnaireRepository.findOne(questionnaireId);
        List<QuestionnaireStudentCommentDTO> qscdl;
        Integer total = 0;
        if (questionnaire.getQuesType().intValue() == QuestionnaireType.STUDENT.getType()) {
            qscdl = findByQuestionnaireComment(questionnaireId, pageNumber, pageSize);
            total = questionnaireExportJdbc.countByCommitPelpeComment(questionnaireId);
        } else {
            qscdl = findByQuestionnaireUserComment(questionnaireId, pageNumber, pageSize);
            total = questionnaireExportJdbc.countByCommitUserComment(questionnaireId);
        }
        PageDomainUtil pd = PageDomainUtil.getPage(total, pageNumber, pageSize);
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, qscdl);
        result.put(ApiReturnConstants.PAGE, pd);
        return result;
    }

    /**
     * --------------------------------------------------------------------------------------按班统计----------------------------------------------------------------------------------------------------------------
     **/
    public QuestionnaireDataDTO findClassInfoQuestionnaire(Long questionnaireAssginId) {
        QuestionnaireAssgin qa = questionnaireAssginRepository.findOne(questionnaireAssginId);
        QuestionnaireDataDTO qdd = new QuestionnaireDataDTO();
        if (null != qa) {
            qdd.setQuestionnaireName(qa.getQuestionnaire().getName());
            qdd.setEndDate(qa.getQuestionnaire().getEndDate());
            if (qa.getQuestionnaire().isQuantification()) {
                qdd.setQuestionnaireTotalScore(qa.getQuestionnaire().getTotalScore() + "");
                Double avgScore = questionnaireExportJdbc.countByClassAvgScore(questionnaireAssginId);
                if (null != avgScore) {
                    qdd.setAvgScore(String.valueOf(avgScore));
                }
            } else {
                qdd.setQuestionnaireTotalScore("-");
                qdd.setAvgScore("-");
            }
            // 总人数
            Integer totalPeple = questionnaireExportJdbc.countByClassTotalNum(questionnaireAssginId);
            qdd.setTotalPeple(totalPeple);
            // 提交人数
            Integer commitNum = questionnaireExportJdbc.countByClassCommitPeple(questionnaireAssginId);
            qdd.setCommitNum(commitNum);
            // 未提交人数
            Integer noCommitNum = totalPeple - commitNum;
            qdd.setNoCommitNum(noCommitNum);
            if (totalPeple == 0) {
                qdd.setCommitZb(MathUtil.doubleToBFBString(0.00));
                qdd.setNoCommitZb(MathUtil.doubleToBFBString(0.00));
            } else {
                // 提交人数占比
                Double commitZb = Double.parseDouble(commitNum + "") / Double.parseDouble(totalPeple + "");
                qdd.setCommitZb(MathUtil.doubleToBFBString(commitZb));
                // 未提交人数占比
                Double noCommitZb = 1 - commitZb;
                qdd.setNoCommitZb(MathUtil.doubleToBFBString(noCommitZb));
            }
            List<QuestionnaireQuestionDataDTO> qqddl = totalClassQuestionInfo(qa.getQuestionnaire(),
                    questionnaireAssginId);
            qdd.setQuestionnaireQuestionDataDTOs(qqddl);
        }
        return qdd;
    }

    public List<QuestionnaireQuestionDataDTO> totalClassQuestionInfo(Questionnaire q, Long questionnaireAssginId) {
        List<Questions> ql = questionsRepository.findByQuestionnaireIdOrderByNoAsc(q.getId());
        Map<Long, String> map = null;
        if (q.isQuantification()) {
            map = questionnaireExportJdbc.countClassQuestionAvgScore(q.getId(), questionnaireAssginId);
        }
        List<QuestionnaireQuestionDataDTO> qqddl = new ArrayList<>();
        if (null != ql && 0 < ql.size()) {
            for (Questions questions : ql) {
                QuestionnaireQuestionDataDTO qqdd = new QuestionnaireQuestionDataDTO();
                // 试题内容
                qqdd.setContent(questions.getName());
                // 试题序号
                qqdd.setNo(questions.getNo());
                if (q.isQuantification()) {
                    // 试题总分
                    qqdd.setScore(questions.getScore() + "");
                } else {
                    // 试题总分
                    qqdd.setScore("-");
                }
                // 这道题的平均分
                if (null != map) {
                    if (!StringUtils.isEmpty(map.get(questions.getId()))) {
                        qqdd.setAvgScore(map.get(questions.getId()));
                    }
                } else {
                    qqdd.setAvgScore("-");
                }
                if (q.isChoiceQuestion()) {
                    if (questions.isRadio()) {
                        // 单选
                        qqdd.setQuestionType(10);
                    } else {
                        // 多选
                        qqdd.setQuestionType(20);
                    }
                    List<QuestionnaireQuestionChoiceDTO> qqcdl = new ArrayList<>();
                    for (QuestionsChoice questionsChoice : questions.getQuestionsChoice()) {
                        QuestionnaireQuestionChoiceDTO qqcd = new QuestionnaireQuestionChoiceDTO();
                        qqcd.setChoice(questionsChoice.getChoice());
                        if (q.isQuantification()) {
                            qqcd.setScore(questionsChoice.getScore());
                        } else {
                            qqcd.setScore("-");
                        }
                        Double choiceZb = questionnaireExportJdbc.getClassChoiceZb(questions.getId(),
                                questionsChoice.getChoice(), questionnaireAssginId);
                        qqcd.setChoiceZb(MathUtil.doubleToBFBString(choiceZb));
                        qqcdl.add(qqcd);
                    }
                    // 试题选项占比
                    qqdd.setChoices(qqcdl);
                } else {
                    // 非选择题
                    qqdd.setQuestionType(30);
                }
                qqddl.add(qqdd);
            }
        }
        return qqddl;
    }

    public List<StudentInfoDTOV2> findClassNoCommitPeple(Long questionnaireAssginId, Integer pageNumber,
                                                         Integer pageSize) {
        Integer pageStart = (pageNumber - 1) * pageSize;
        List<Long> noCommit = questionnaireExportJdbc.findByClassNoCommitPepleInfo(questionnaireAssginId, pageStart,
                pageSize);
        List<StudentInfoDTOV2> sidvl = new ArrayList<>();
        Map<Long, String> teacherInfo = new HashMap<>();
        if (null != noCommit && 0 < noCommit.size()) {
            List<Map<String, Object>> mapList = orgManagerRemoteClient.findByIds(noCommit);
            int i = 1;
            if (null != mapList && 0 < mapList.size()) {
                for (Long stuId : noCommit) {
                    for (Map<String, Object> map : mapList) {
                        if (stuId.longValue() == Long.valueOf(map.get("id").toString()).longValue()) {
                            StudentInfoDTOV2 sid = new StudentInfoDTOV2();
                            sid.setNo(pageStart + i);
                            sid.setStuId(stuId);
                            if (null != map.get("name")) {
                                sid.setStuName(map.get("name").toString());
                            }
                            if (null != map.get("classesName")) {
                                sid.setClassesName(map.get("classesName").toString());
                            }
                            if (null != map.get("professionalName")) {
                                sid.setProfName(map.get("professionalName").toString());
                            }
                            if (null != map.get("collegeName")) {
                                sid.setCollegeName(map.get("collegeName").toString());
                            }
                            if (null != map.get("classesId")) {
                                Long classesId = Long.valueOf(map.get("classesId").toString());
                                if (null != teacherInfo.get(classesId)) {
                                    sid.setTeacherName(teacherInfo.get(classesId));
                                } else {
                                    String json = orgManagerRemoteClient.classesTeacherList(classesId);
                                    if (!StringUtils.isEmpty(json)) {
                                        try {
                                            Map<String, Object> mapData = JsonUtil.Json2Object(json);
                                            if (null != mapData && null != mapData.get("data")) {
                                                List<Map<String, Object>> dataList = (List<Map<String, Object>>) mapData
                                                        .get("data");
                                                if (null != dataList && 0 < dataList.size()) {
                                                    Map<String, Object> d = dataList.get(0);
                                                    if (null != d.get("name")) {
                                                        sid.setTeacherName(d.get("name").toString());
                                                        teacherInfo.put(classesId, d.get("name").toString());
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {
                                            log.warn("Exception", e);
                                        }
                                    }
                                }
                            }
                            sidvl.add(sid);
                            i++;
                        }
                    }
                }
            }
        }
        return sidvl;
    }


    public Map<String, Object> findClassNoCommitPeplePage(Long questionnaireAssginId, Integer pageNumber,
                                                          Integer pageSize, Map<String, Object> result) {
        List<StudentInfoDTOV2> stuInfo = findClassNoCommitPeple(questionnaireAssginId, pageNumber, pageSize);
        Integer total = questionnaireExportJdbc.countByClassNoCommitPepleInfo(questionnaireAssginId);
        PageDomainUtil pd = PageDomainUtil.getPage(total, pageNumber, pageSize);
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, stuInfo);
        result.put(ApiReturnConstants.PAGE, pd);
        return result;
    }

    /**
     * @param questionnaireAssginId
     * @param pageNumber
     * @param pageSize
     * @Title: findClassByQuestionnaireComment
     * @Description: 按班统计评语
     * @return: List<QuestionnaireStudentCommentDTO>
     */
    public List<QuestionnaireStudentCommentDTO> findClassByQuestionnaireComment(Long questionnaireAssginId,
                                                                                Integer pageNumber, Integer pageSize) {
        Integer startPage = (pageNumber - 1) * pageSize;
        QuestionnaireAssgin qa = questionnaireAssginRepository.findOne(questionnaireAssginId);
        List<QuestionnaireStudentCommentDTO> qscdl = questionnaireExportJdbc.findByClassCommitPelpeComment(questionnaireAssginId, startPage, pageSize);
        if (null != qscdl && 0 < qscdl.size()) {
            for (QuestionnaireStudentCommentDTO questionnaireStudentCommentDTO : qscdl) {
                questionnaireStudentCommentDTO.setClassType(qa.getClassType());
                questionnaireStudentCommentDTO.setClassesName(qa.getClassesName());
                questionnaireStudentCommentDTO.setTeachingClassName(qa.getTeachingClassName());
            }
        }
        getStuInfo(qscdl);
        return qscdl;
    }

    public Map<String, Object> findClassByQuestionnaireCommentInfo(Long questionnaireAssginId, Integer pageNumber,
                                                                   Integer pageSize, Map<String, Object> result) {
        List<QuestionnaireStudentCommentDTO> qscdl = findClassByQuestionnaireComment(questionnaireAssginId, pageNumber,
                pageSize);
        Integer total = questionnaireExportJdbc.countByClassCommitPelpeComment(questionnaireAssginId);
        PageDomainUtil pd = PageDomainUtil.getPage(total, pageNumber, pageSize);
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, qscdl);
        result.put(ApiReturnConstants.PAGE, pd);
        return result;
    }
}
