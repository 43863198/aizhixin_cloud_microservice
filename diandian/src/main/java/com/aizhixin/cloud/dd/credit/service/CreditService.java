package com.aizhixin.cloud.dd.credit.service;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.credit.domain.CreditDomain;
import com.aizhixin.cloud.dd.credit.domain.CreditRatingPersonDomain;
import com.aizhixin.cloud.dd.credit.domain.CreditStudentDetailsDomain;
import com.aizhixin.cloud.dd.credit.dto.CreditDTO;
import com.aizhixin.cloud.dd.credit.dto.RatingCreditDTO;
import com.aizhixin.cloud.dd.credit.dto.RatingCreditQuesDTO;
import com.aizhixin.cloud.dd.credit.entity.*;
import com.aizhixin.cloud.dd.credit.repository.*;
import com.aizhixin.cloud.dd.messege.dto.AudienceDTO;
import com.aizhixin.cloud.dd.messege.service.MessageService;
import com.aizhixin.cloud.dd.orgStructure.entity.Classes;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.repository.ClassesRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;
import com.aizhixin.cloud.dd.orgStructure.utils.UserType;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class CreditService {

    @Autowired
    private CreditRepository creditRepository;
    @Autowired
    private CreditRatingPersonRepository personRepository;
    @Autowired
    private CreditClassRepository classRepository;
    @Autowired
    private CreditStudentRepository studentRepository;
    @Autowired
    private CreditStudentRecordRepository studentRecordRepository;
    @Autowired
    private CreditTempletRepository templetRepository;
    @Autowired
    private CreditTempletQuesRepository quesRepository;
    @Autowired
    private CreditReportRepository reportRepository;
    @Autowired
    private CreditReportRecordRepository reportRecordRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private ClassesRepository classesRepository;
    @Autowired
    private MessageService messageService;

    @Transactional
    public void saveCredit(AccountDTO account, CreditDTO dto) {
        Credit credit = new Credit();
        credit.setName(dto.getName());
        credit.setOrgId(account.getOrganId());
        credit.setTeacherId(account.getId());
        credit.setTeacherName(account.getName());
        credit.setTempletId(dto.getTempletId());
        credit.setTempletName(dto.getTempletName());
        if (dto.getRatingStus() != null && dto.getRatingStus().size() > 0) {
            credit.setRatingStuCount(dto.getRatingStus().size());
            credit = creditRepository.save(credit);
            List<UserInfo> userInfos = userInfoRepository.findByUserIdIn(dto.getRatingStus());
            if (userInfos != null && userInfos.size() > 0) {
                Map<Long, CreditClass> classMap = new HashMap<>();
                Set<CreditRatingPerson> personSet = new HashSet<>();
                for (UserInfo stu : userInfos) {
                    if (stu.getClassesId() != null && stu.getClassesId() > 0) {
                        CreditRatingPerson p = new CreditRatingPerson();
                        p.setStuId(stu.getUserId());
                        p.setStuName(stu.getName());
                        p.setAvatar(stu.getAvatar());
                        p.setClassId(stu.getClassesId());
                        p.setCreditId(credit.getId());
                        personSet.add(p);

                        CreditClass c = new CreditClass();
                        c.setClassId(stu.getClassesId());
                        c.setClassName(stu.getClassesName());
                        c.setCredit(credit);
                        c.setCommitCount(0);
                        classMap.put(c.getClassId(), c);
                    }
                }

                Set<CreditClass> classSet = new HashSet<>();
                for (Long key : classMap.keySet()) {
                    classSet.add(classMap.get(key));
                }

                List<CreditStudent> studentList = new ArrayList<>();
                for (CreditClass cc : classSet) {
                    List<UserInfo> userInfoList = userInfoRepository.findByClassesIdAndUserType(cc.getClassId(), UserType.B_STUDENT.getState());
                    if (userInfoList != null && userInfoList.size() > 0) {
                        for (UserInfo u : userInfoList) {
                            CreditStudent cs = new CreditStudent();
                            cs.setCreditId(credit.getId());
                            cs.setStuId(u.getUserId());
                            cs.setStuName(u.getName());
                            cs.setJobNum(u.getJobNum());
                            cs.setAvatar(u.getAvatar());
                            cs.setClassId(u.getClassesId());
                            cs.setAvgScore(0f);
                            cs.setRatingCount(0);
                            studentList.add(cs);
                        }
                    }
                }
                studentRepository.save(studentList);
                personRepository.save(personSet);
                classRepository.save(classSet);
                credit.setClassCount(classSet.size());
                creditRepository.save(credit);
                sendMessage(credit.getTeacherName(), new ArrayList<>(personSet));
            }
        } else {
            credit.setRatingStuCount(0);
            credit.setClassCount(0);
            credit = creditRepository.save(credit);
        }
        updateTempletCount(credit.getTempletId());
    }

    private void updateTempletCount(Long templetId) {
        CreditTemplet templet = templetRepository.findOne(templetId);
        if (templet != null) {
            Long count = creditRepository.countByTempletIdAndDeleteFlag(templetId, DataValidity.VALID.getState());
            templet.setUsageCount(count.intValue());
            templetRepository.save(templet);
        }
    }

    private void updateClassCount(Credit credit) {
        Long count = classRepository.countByCreditAndDeleteFlag(credit, DataValidity.VALID.getState());
        if (count == null) {
            count = 0L;
        }
        credit.setClassCount(count.intValue());
        creditRepository.save(credit);
    }

    private void updateRatingPersonCount(Credit credit) {
        Long count = personRepository.countByCreditIdAndDeleteFlag(credit.getId(), DataValidity.VALID.getState());
        if (count == null) {
            count = 0L;
        }
        credit.setRatingStuCount(count.intValue());
        creditRepository.save(credit);
    }

    @Transactional
    public void updateCredit(AccountDTO account, CreditDTO dto) {
        Credit credit = creditRepository.findOne(dto.getId());
        if (credit == null) {
            return;
        }
        credit.setName(dto.getName());
        credit.setOrgId(account.getOrganId());
        credit.setTeacherId(account.getId());
        credit.setTeacherName(account.getName());
        credit.setTempletId(dto.getTempletId());
        credit.setTempletName(dto.getTempletName());

        if (dto.getRatingStus() != null && dto.getRatingStus().size() > 0) {
            //已有
            List<CreditRatingPerson> ratingPersonList = personRepository.findByCreditIdAndDeleteFlag(credit.getId(), DataValidity.VALID.getState());
            Map<Long, CreditRatingPerson> personMap = new HashMap<>();
            for (CreditRatingPerson p : ratingPersonList) {
                personMap.put(p.getStuId(), p);
            }
            List<CreditClass> creditClassList = classRepository.findByCreditAndDeleteFlag(credit, DataValidity.VALID.getState());
            Map<Long, CreditClass> classMap = new HashMap<>();
            for (CreditClass c : creditClassList) {
                classMap.put(c.getClassId(), c);
            }

            List<CreditRatingPerson> addRatingPersonList = new ArrayList<>();
            List<CreditRatingPerson> delRatingPersonList = new ArrayList<>();
            List<CreditClass> addClassList = new ArrayList<>();
            List<CreditClass> delClassList = new ArrayList<>();

            List<UserInfo> userInfos = userInfoRepository.findByUserIdIn(dto.getRatingStus());
            Map<Long, UserInfo> stuMap = new HashMap<>();
            if (userInfos != null && userInfos.size() > 0) {
                for (UserInfo stu : userInfos) {
                    if (stu.getClassesId() != null && personMap.get(stu.getUserId()) == null) {
                        CreditRatingPerson p = new CreditRatingPerson();
                        p.setStuId(stu.getUserId());
                        p.setStuName(stu.getName());
                        p.setAvatar(stu.getAvatar());
                        p.setClassId(stu.getClassesId());
                        p.setCreditId(credit.getId());
                        addRatingPersonList.add(p);
                        if (classMap.get(p.getClassId()) == null) {
                            CreditClass c = new CreditClass();
                            c.setClassId(stu.getClassesId());
                            c.setClassName(stu.getClassesName());
                            c.setCredit(credit);
                            c.setCommitCount(0);
                            addClassList.add(c);
                            classMap.put(p.getClassId(), c);
                        }
                    }
                    stuMap.put(stu.getUserId(), stu);
                }
                for (CreditRatingPerson rp : ratingPersonList) {
                    if (stuMap.get(rp.getStuId()) == null) {
                        rp.setDeleteFlag(DataValidity.INVALID.getState());
                        delRatingPersonList.add(rp);
                        Long classId = rp.getClassId();
                        boolean flag = false;
                        for (CreditRatingPerson rrp : ratingPersonList) {
                            if (rrp.getClassId().intValue() == classId.intValue() && rrp.getDeleteFlag().intValue() == DataValidity.VALID.getState().intValue()) {
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            CreditClass c = classMap.get(classId);
                            if (c.getId() != null && c.getId() > 0) {
                                c.setDeleteFlag(DataValidity.INVALID.getState());
                                delClassList.add(c);
                            }
                        }
                    }
                }
                if (addRatingPersonList.size() > 0) {
                    personRepository.save(addRatingPersonList);
                    sendMessage(credit.getTeacherName(), addRatingPersonList);
                }
                if (delRatingPersonList.size() > 0) {
                    personRepository.save(delRatingPersonList);
                }
                if (addClassList.size() > 0) {
                    classRepository.save(addClassList);
                    //新增
                    addCreditStudent(credit, addClassList);
                }
                if (delClassList.size() > 0) {
                    classRepository.save(delClassList);
                    //删除
                    deleteCreditStudent(credit, delClassList);
                }
                //更新学生数量
                updateRatingPersonCount(credit);
            }
        } else {
            credit.setRatingStuCount(0);
            deleteAll(credit);
        }
        credit = creditRepository.save(credit);
        updateTempletCount(credit.getTempletId());
        updateClassCount(credit);
    }

    private void addCreditStudent(Credit credit, List<CreditClass> list) {
        if (list != null) {
            List<CreditStudent> studentList = new ArrayList<>();
            for (CreditClass cc : list) {
                List<UserInfo> userInfoList = userInfoRepository.findByClassesIdAndUserType(cc.getClassId(), UserType.B_STUDENT.getState());
                if (userInfoList != null && userInfoList.size() > 0) {
                    for (UserInfo u : userInfoList) {
                        CreditStudent cs = new CreditStudent();
                        cs.setCreditId(credit.getId());
                        cs.setStuId(u.getUserId());
                        cs.setStuName(u.getName());
                        cs.setJobNum(u.getJobNum());
                        cs.setAvatar(u.getAvatar());
                        cs.setClassId(u.getClassesId());
                        cs.setAvgScore(0f);
                        cs.setRatingCount(0);
                        studentList.add(cs);
                    }
                }
            }
            studentRepository.save(studentList);
        }
    }

    private void deleteCreditStudent(Credit credit, List<CreditClass> list) {
        if (list != null) {
            for (CreditClass cc : list) {
                //删除学生
                List<CreditStudent> studentList = studentRepository.findByCreditIdAndClassIdAndDeleteFlag(credit.getId(), cc.getClassId(), DataValidity.VALID.getState());
                if (studentList != null && studentList.size() > 0) {
                    Set<Long> stuIds = new HashSet<>();
                    for (CreditStudent cs : studentList) {
                        cs.setDeleteFlag(DataValidity.INVALID.getState());
                        stuIds.add(cs.getStuId());
                    }
                    studentRepository.save(studentList);
                }
            }
        }

    }

    private void deleteAll(Credit credit) {
        //删除评分学生
        List<CreditRatingPerson> ratingPersonList = personRepository.findByCreditIdAndDeleteFlag(credit.getId(), DataValidity.VALID.getState());
        if (ratingPersonList != null && ratingPersonList.size() > 0) {
            for (CreditRatingPerson cr : ratingPersonList) {
                cr.setDeleteFlag(DataValidity.INVALID.getState());
            }
            personRepository.save(ratingPersonList);
        }
        //删除班级
        List<CreditClass> classList = classRepository.findByCreditAndDeleteFlag(credit, DataValidity.VALID.getState());
        if (classList != null && classList.size() > 0) {
            for (CreditClass ci : classList) {
                ci.setDeleteFlag(DataValidity.INVALID.getState());
            }
            classRepository.save(classList);
        }
        //删除学生
        List<CreditStudent> studentList = studentRepository.findByCreditIdAndDeleteFlag(credit.getId(), DataValidity.VALID.getState());
        if (studentList != null && studentList.size() > 0) {
            for (CreditStudent cs : studentList) {
                cs.setDeleteFlag(DataValidity.INVALID.getState());
            }
            studentRepository.save(studentList);
        }
        //删除学生记录
        List<CreditStudentRecord> studentRecordList = studentRecordRepository.findByCreditIdAndDeleteFlag(credit.getId(), DataValidity.VALID.getState());
        if (studentRecordList != null && studentRecordList.size() > 0) {
            for (CreditStudentRecord cs : studentRecordList) {
                cs.setDeleteFlag(DataValidity.INVALID.getState());
            }
            studentRecordRepository.save(studentRecordList);
        }
    }

    @Transactional
    public void deleteCredit(Long creditId) {
        Credit credit = creditRepository.findOne(creditId);
        if (credit != null) {
            credit.setDeleteFlag(DataValidity.INVALID.getState());
            creditRepository.save(credit);
            deleteAll(credit);
            updateTempletCount(credit.getTempletId());
        }
    }

    public List getCreditList(Long teacherId) {
        List<Credit> list = creditRepository.findByTeacherIdAndDeleteFlagOrderByIdDesc(teacherId, DataValidity.VALID.getState());
        if (list != null && list.size() > 0) {
            List<CreditDomain> result = new ArrayList<>();
            for (Credit c : list) {
                CreditDomain d = new CreditDomain();
                BeanUtils.copyProperties(c, d);
                if (c.getRatingPersonList() != null && c.getRatingPersonList().size() > 0) {
                    List<CreditRatingPersonDomain> personDomains = new ArrayList<>();
                    Set<Long> stuIds = new HashSet<>();
                    for (CreditRatingPerson p : c.getRatingPersonList()) {
                        stuIds.add(p.getStuId());
                    }
                    Set<UserInfo> userInfoList = userInfoRepository.findByUserIdIn(stuIds);
                    Map<Long, UserInfo> userInfoMap = new HashMap<>();
                    for (UserInfo u : userInfoList) {
                        userInfoMap.put(u.getUserId(), u);
                    }
                    for (CreditRatingPerson p : c.getRatingPersonList()) {
                        if (p.getDeleteFlag().intValue() == DataValidity.VALID.getState().intValue()) {
                            CreditRatingPersonDomain pd = new CreditRatingPersonDomain();
                            BeanUtils.copyProperties(p, pd);
                            UserInfo u = userInfoMap.get(p.getStuId());
                            BeanUtils.copyProperties(u, pd);
                            pd.setUserId(p.getStuId());
                            pd.setName(p.getStuName());
                            personDomains.add(pd);
                        }
                    }
                    d.setRatingPersonList(personDomains);
                }
                result.add(d);
            }
            return result;
        }
        return new ArrayList();
    }

    public List getCreditListByStuId(Long stuId) {
        UserInfo userInfo = userInfoRepository.findByUserId(stuId);
        if (userInfo != null && userInfo.getClassesId() != null) {
            List<CreditClass> list = classRepository.findByClassIdAndDeleteFlagOrderByIdDesc(userInfo.getClassesId(), DataValidity.VALID.getState());
            return list;
        }
        return new ArrayList();
    }

    public List getCreditClassList(Long creditId) {
        Credit credit = creditRepository.findOne(creditId);
        if (credit != null) {
            List<CreditClass> list = classRepository.findByCreditAndDeleteFlag(credit, DataValidity.VALID.getState());
            if (list != null && list.size() > 0) {
                Map<Long, List<CreditRatingPerson>> personMap = new HashMap<>();
                for (CreditClass creditClass : list) {
                    List<CreditRatingPerson> personList = new ArrayList<>();
                    if (creditClass.getCredit() != null && creditClass.getCredit().getRatingPersonList() != null) {
                        for (CreditRatingPerson p : creditClass.getCredit().getRatingPersonList()) {
                            if (p.getClassId() != null && creditClass.getClassId() != null && p.getDeleteFlag().intValue() == DataValidity.VALID.getState().intValue() && p.getClassId().intValue() == creditClass.getClassId().intValue()) {
                                personList.add(p);
                            }
                        }
                        personMap.put(creditClass.getClassId(), personList);
                    }
                }
                for (CreditClass creditClass : list) {
                    List<CreditRatingPerson> personList = personMap.get(creditClass.getClassId());
                    Credit credit1 = new Credit();
                    BeanUtils.copyProperties(credit, credit1);
                    credit1.setRatingPersonList(personList);
                    creditClass.setCredit(credit1);
                }
            }
            return list;
        }
        return new ArrayList();
    }

    public List getCreditStudentList(Long creditId, Long classId) {
        List<CreditStudent> list = studentRepository.findByCreditIdAndClassIdAndDeleteFlag(creditId, classId, DataValidity.VALID.getState());
        return list;
    }

    public List<CreditStudentDetailsDomain> getCreditStudentDetails(Long creditId, Long stuId) {
        Credit credit = creditRepository.findOne(creditId);
        List<CreditStudentDetailsDomain> result = new ArrayList<>();
        if (credit != null) {
            List<CreditStudentRecord> recordList = studentRecordRepository.findByCreditIdAndStuIdAndDeleteFlag(creditId, stuId, DataValidity.VALID.getState());
            Map<Long, CreditStudentRecord> recordMap = new HashMap<>();
            if (recordList != null && recordList.size() > 0) {
                for (CreditStudentRecord r : recordList) {
                    recordMap.put(r.getQuesId(), r);
                }
            }
            CreditTemplet templet = templetRepository.findOne(credit.getTempletId());
            List<CreditTempletQues> quesList = quesRepository.findByTempletIdAndDeleteFlag(templet.getId(), DataValidity.VALID.getState());
            if (quesList != null && quesList.size() > 0) {
                for (CreditTempletQues ques : quesList) {
                    CreditStudentDetailsDomain d = new CreditStudentDetailsDomain();
                    d.setQuesId(ques.getId());
                    d.setContent(ques.getContent());
                    d.setMinScore(ques.getMinScore());
                    d.setMaxScore(ques.getMaxScore());
                    CreditStudentRecord r = recordMap.get(ques.getId());
                    if (r != null) {
                        d.setScores(r.getScores());
                        d.setAvgScore(r.getAvgScore());
                    }
                    result.add(d);
                }
            }
        }
        return result;
    }

    @Transactional
    public void updateCreditStudentScore(Long creditId, Long stuId, Float score) {
        List<CreditStudent> list = studentRepository.findByCreditIdAndStuIdAndDeleteFlag(creditId, stuId, DataValidity.VALID.getState());
        if (list != null && list.size() > 0) {
            CreditStudent cs = list.get(0);
            cs.setAvgScore(score);
            studentRepository.save(cs);
        }
    }

    @Transactional
    public void commitCredit(Long creditId, Long classId) {
        Credit credit = creditRepository.findOne(creditId);
        if (credit != null) {
            List<CreditStudent> studentList = studentRepository.findByCreditIdAndClassIdAndDeleteFlag(creditId, classId, DataValidity.VALID.getState());
            if (studentList != null && studentList.size() > 0) {
                Classes classes = classesRepository.findByClassesId(classId);
                List<CreditReport> reports = reportRepository.findByCreditIdAndClassIdAndDeleteFlag(creditId, classId, DataValidity.VALID.getState());
                CreditReport report = null;
                if (reports != null && reports.size() > 0) {
                    report = reports.get(0);
                } else {
                    report = new CreditReport();
                    report.setName(credit.getName());
                    report.setOrgId(credit.getOrgId());
                    report.setCreditId(credit.getId());
                    report.setTeacherId(credit.getTeacherId());
                    report.setTeacherName(credit.getTeacherName());
                    report.setTempletId(credit.getTempletId());
                    report.setTempletName(credit.getTempletName());
                    report.setClassId(classId);
                    report.setClassName(classes.getClassesName());
                    report.setCommitCount(0);
                    report = reportRepository.save(report);
                }
                BigDecimal count = new BigDecimal(report.getCommitCount());
                BigDecimal newCount = new BigDecimal(report.getCommitCount() + 1);
                BigDecimal stuCount = new BigDecimal(studentList.size());
                BigDecimal totalScore = new BigDecimal(0);
                for (CreditStudent item : studentList) {
                    List<CreditStudentRecord> studentRecords = studentRecordRepository.findByCreditIdAndStuIdAndDeleteFlag(creditId, item.getStuId(), DataValidity.VALID.getState());
                    List<CreditReportRecord> records = reportRecordRepository.findByReportIdAndStuId(report.getId(), item.getStuId());
                    Map<Long, CreditReportRecord> recordMap = new HashMap();
                    if (records != null && records.size() > 0) {
                        for (CreditReportRecord crr : records) {
                            recordMap.put(crr.getQuesId(), crr);
                        }
                    }
                    List<CreditReportRecord> reportRecords = new ArrayList<>();
                    if (studentRecords != null && studentRecords.size() > 0) {
                        for (CreditStudentRecord csr : studentRecords) {
                            CreditReportRecord record = recordMap.get(csr.getQuesId());
                            if (record == null) {
                                record = new CreditReportRecord();
                                record.setReportId(report.getId());
                                record.setStuId(item.getStuId());
                                record.setStuName(item.getStuName());
                                record.setJobNum(item.getJobNum());
                                record.setQuesId(csr.getQuesId());
                                record.setAvgScore(csr.getAvgScore());
                            } else {
                                BigDecimal avg = new BigDecimal(record.getAvgScore());
                                avg = avg.multiply(count);
                                avg = avg.add(new BigDecimal(csr.getAvgScore()));
                                avg = avg.divide(newCount, 1, BigDecimal.ROUND_HALF_UP);
                                record.setAvgScore(avg.floatValue());
                            }
                            record.setCreatedDate(new Date());
                            reportRecords.add(record);
                        }
                        reportRecordRepository.save(reportRecords);
                    }
                    totalScore = totalScore.add(new BigDecimal(item.getAvgScore()));
                }
                //提交次数
                report.setCommitCount(newCount.intValue());
                totalScore = totalScore.divide(stuCount, 1, BigDecimal.ROUND_HALF_UP);
                report.setAvgScore(totalScore.floatValue());
                //平均分
                reportRepository.save(report);
                List<CreditClass> creditClasses = classRepository.findByCreditAndClassIdAndDeleteFlag(credit, classId, DataValidity.VALID.getState());
                if (creditClasses != null && creditClasses.size() > 0) {
                    CreditClass creditClass = creditClasses.get(0);
                    Integer cCount = creditClass.getCommitCount();
                    cCount++;
                    creditClass.setCommitCount(cCount);
                    classRepository.save(creditClass);
                }
            }
        }
    }

    @Transactional
    public void ratingCredit(RatingCreditDTO dto) {
        if (dto.getCreditId() == null || dto.getStuId() == null || dto.getQuesList() == null) {
            return;
        }
        Credit credit = creditRepository.findOne(dto.getCreditId());
        if (credit != null) {
            List<CreditStudent> studentList = studentRepository.findByCreditIdAndStuIdAndDeleteFlag(dto.getCreditId(), dto.getStuId(), DataValidity.VALID.getState());
            if (studentList != null && studentList.size() > 0) {
                CreditStudent student = studentList.get(0);
                //添加评分记录
                Map<Long, RatingCreditQuesDTO> quesDTOMap = new HashMap();
                for (RatingCreditQuesDTO qd : dto.getQuesList()) {
                    quesDTOMap.put(qd.getQuesId(), qd);
                }

                BigDecimal totalScore = new BigDecimal(0);
                List<CreditStudentRecord> recordList = studentRecordRepository.findByCreditIdAndStuIdAndDeleteFlag(credit.getId(), dto.getStuId(), DataValidity.VALID.getState());
                if (recordList == null || recordList.size() == 0) {
                    List<CreditTempletQues> quesList = quesRepository.findByTempletIdAndDeleteFlag(credit.getTempletId(), DataValidity.VALID.getState());
                    for (CreditStudent s : studentList) {
                        for (CreditTempletQues tq : quesList) {
                            CreditStudentRecord r = new CreditStudentRecord();
                            r.setStuId(s.getStuId());
                            r.setQuesId(tq.getId());
                            r.setCreditId(credit.getId());
                            r.setScores("");
                            r.setAvgScore(0f);
                            recordList.add(r);
                        }
                    }
                    recordList = studentRecordRepository.save(recordList);
                }
                for (CreditStudentRecord r : recordList) {
                    RatingCreditQuesDTO quesDTO = quesDTOMap.get(r.getQuesId());
                    totalScore = totalScore.add(new BigDecimal(quesDTO.getScore()));
                    if (StringUtils.isEmpty(r.getScores())) {
                        r.setScores(quesDTO.getScore() + "分");
                        r.setAvgScore(quesDTO.getScore());
                    } else {
                        String[] scores = r.getScores().toString().replaceAll("分", "").split(",");
                        BigDecimal ts = new BigDecimal(0);
                        BigDecimal tc = new BigDecimal(scores.length);

                        for (String score : scores) {
                            ts = ts.add(new BigDecimal(Float.parseFloat(score)));
                        }
                        ts = ts.add(new BigDecimal(quesDTO.getScore()));
                        tc = tc.add(new BigDecimal(1));
                        BigDecimal ta = ts.divide(tc, 1, BigDecimal.ROUND_HALF_UP);
                        r.setScores(r.getScores() + "," + quesDTO.getScore() + "分");
                        r.setAvgScore(ta.floatValue());
                    }
                }
                studentRecordRepository.save(recordList);
                //学生平均分
                BigDecimal avgScore = new BigDecimal(student.getAvgScore());
                BigDecimal count = new BigDecimal(student.getRatingCount());
                avgScore = avgScore.multiply(count);
                avgScore = avgScore.add(totalScore);
                count = count.add(new BigDecimal(1));
                avgScore = avgScore.divide(count, 1, BigDecimal.ROUND_HALF_UP);
                student.setAvgScore(avgScore.floatValue());
                student.setRatingCount(count.intValue());
                studentRepository.save(student);
            }
        }
    }

    private void sendMessage(String teacherName, List<CreditRatingPerson> stus) {
        if (stus != null && stus.size() > 0) {
            List<AudienceDTO> audience = new ArrayList<>();
            for (CreditRatingPerson stu : stus) {
                AudienceDTO d = new AudienceDTO();
                d.setUserId(stu.getStuId());
                audience.add(d);
            }
            messageService.push("素质学分", teacherName + "老师选择了你作为素质学分评分人员", "student_szxf_notice", audience);

        }

    }
}
