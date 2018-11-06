package com.aizhixin.cloud.dd.classperf.service;

import com.aizhixin.cloud.dd.classperf.domain.ClassPerfTeacherDomain;
import com.aizhixin.cloud.dd.classperf.dto.ClassPerfBatchDTO;
import com.aizhixin.cloud.dd.classperf.dto.ClassPerfDTO;
import com.aizhixin.cloud.dd.classperf.entity.ClassPerfLog;
import com.aizhixin.cloud.dd.classperf.entity.ClassPerfStudent;
import com.aizhixin.cloud.dd.classperf.entity.ClassPerfTeacher;
import com.aizhixin.cloud.dd.classperf.repository.ClassPerfLogRepository;
import com.aizhixin.cloud.dd.classperf.repository.ClassPerfStudentRepository;
import com.aizhixin.cloud.dd.classperf.repository.ClassPerfTeacherRepository;
import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.orgStructure.entity.OrgInfo;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.repository.OrgInfoRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;
import com.aizhixin.cloud.dd.orgStructure.utils.UserType;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class ClassPerfService {
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private ClassPerfTeacherRepository classPerfTeacherRepository;
    @Autowired
    private ClassPerfStudentRepository classPerfStudentRepository;
    @Autowired
    private ClassPerfLogRepository classPerfLogRepository;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteClient;
    @Autowired
    private OrgInfoRepository orgInfoRepository;

    public Map<String, Object> updateLimitScore(Long orgId, Long teacherId, Integer score) {
        Map<String, Object> result = new HashMap<>();
        ClassPerfTeacher teacher = classPerfTeacherRepository.findByTeacherId(teacherId);
        if (teacher == null) {
            teacher = new ClassPerfTeacher();
            teacher.setOrgId(orgId);
            teacher.setTeacherId(teacherId);
            teacher.setResidualScore(50);
            teacher.setRatingLimit(50);
        }
        Integer residualScore = score - (teacher.getRatingLimit() - teacher.getResidualScore());
        if (residualScore < 0) {
            residualScore = 0;
        }
        teacher.setResidualScore(residualScore);
        teacher.setRatingLimit(score);
        classPerfTeacherRepository.save(teacher);
        result.put(ApiReturnConstants.SUCCESS, true);
        return result;
    }

    public Map<String, Object> updateAllTeacherLimitScore(Long orgId, Integer score) {
        Map<String, Object> result = new HashMap<>();
        List<UserInfo> teachers = userInfoRepository.findByOrgIdAndUserType(orgId, UserType.B_TEACHER.getState());
        if (teachers != null && teachers.size() > 0) {
            for (UserInfo item : teachers) {
                updateLimitScore(orgId, item.getUserId(), score);
            }
            result.put(ApiReturnConstants.SUCCESS, true);
        } else {
            result.put(ApiReturnConstants.SUCCESS, false);
        }
        return result;
    }

    public Map<String, Object> batchRateStudent(ClassPerfBatchDTO dto){
        Map<String, Object> result = new HashMap<>();
        //check
        ClassPerfTeacher teacher = classPerfTeacherRepository.findByTeacherId(dto.getTeacherId());
        int score = dto.getScore().intValue();
        if (score < 0) {
            score = 0 - score;
        }
        if (score > teacher.getResidualScore()) {
            result.put(ApiReturnConstants.SUCCESS, false);
            result.put(ApiReturnConstants.ERROR, "打分大于教师剩余分数");
            return result;
        }

        //update student
        UserInfo userInfo = userInfoRepository.findByUserId(dto.getStudentId());
        Long semesterId = getSemesterId(userInfo.getOrgId());
        ClassPerfStudent student = classPerfStudentRepository.findByStudentIdAndSemesterId(dto.getStudentId(), semesterId);
        if (student == null) {
            student = new ClassPerfStudent();
            BeanUtils.copyProperties(userInfo, student);
            student.setStudentId(userInfo.getUserId());
            student.setSemesterId(semesterId);
            student.setUpdateDate(DateFormatUtil.formatShort(new Date()));
            student.setTotalScore(0);
        }
        if (dto.getType() == 10) {
            student.setTotalScore(student.getTotalScore() + dto.getScore());
        } else {
            student.setTotalScore(student.getTotalScore() - dto.getScore());
        }
        student = classPerfStudentRepository.save(student);

        //insert log
        UserInfo teacherInfo = userInfoRepository.findByUserId(dto.getTeacherId());
        ClassPerfLog log = new ClassPerfLog();
        log.setStudentId(dto.getStudentId());
        log.setTeacherId(dto.getTeacherId());
        log.setScore(dto.getScore());
        log.setType(dto.getType());
        log.setComment(dto.getComment());
        log.setFiles(dto.getFiles());
        log.setClassPerfId(student.getId());
        log.setTeacherName(teacherInfo.getName());
        log.setTeacherJobnum(teacherInfo.getJobNum());
        log.setTeacherGender(teacherInfo.getSex());
        log.setAvatar(teacherInfo.getAvatar());
        classPerfLogRepository.save(log);

        //update teacher
        teacher.setResidualScore(teacher.getResidualScore() - score);
        classPerfTeacherRepository.save(teacher);
        result.put(ApiReturnConstants.SUCCESS, true);
        return result;
    }

    public Map<String, Object> rateStudent(ClassPerfDTO dto) {
        Map<String, Object> result = new HashMap<>();
        //check
        ClassPerfTeacher teacher = classPerfTeacherRepository.findByTeacherId(dto.getTeacherId());
        int score = dto.getScore().intValue();
        if (score < 0) {
            score = 0 - score;
        }
        if (score > teacher.getResidualScore()) {
            result.put(ApiReturnConstants.SUCCESS, false);
            result.put(ApiReturnConstants.ERROR, "打分大于教师剩余分数");
            return result;
        }

        //update student
        UserInfo userInfo = userInfoRepository.findByUserId(dto.getStudentId());
        Long semesterId = getSemesterId(userInfo.getOrgId());
        ClassPerfStudent student = classPerfStudentRepository.findByStudentIdAndSemesterId(dto.getStudentId(), semesterId);
        if (student == null) {
            student = new ClassPerfStudent();
            BeanUtils.copyProperties(userInfo, student);
            student.setStudentId(userInfo.getUserId());
            student.setSemesterId(semesterId);
            student.setUpdateDate(DateFormatUtil.formatShort(new Date()));
            student.setTotalScore(0);
        }
        if (dto.getType() == 10) {
            student.setTotalScore(student.getTotalScore() + dto.getScore());
        } else {
            student.setTotalScore(student.getTotalScore() - dto.getScore());
        }
        student = classPerfStudentRepository.save(student);

        //insert log
        UserInfo teacherInfo = userInfoRepository.findByUserId(dto.getTeacherId());
        ClassPerfLog log = new ClassPerfLog();
        log.setStudentId(dto.getStudentId());
        log.setTeacherId(dto.getTeacherId());
        log.setScore(dto.getScore());
        log.setType(dto.getType());
        log.setComment(dto.getComment());
        log.setFiles(dto.getFiles());
        log.setClassPerfId(student.getId());
        log.setTeacherName(teacherInfo.getName());
        log.setTeacherJobnum(teacherInfo.getJobNum());
        log.setTeacherGender(teacherInfo.getSex());
        log.setAvatar(teacherInfo.getAvatar());
        classPerfLogRepository.save(log);

        //update teacher
        teacher.setResidualScore(teacher.getResidualScore() - score);
        classPerfTeacherRepository.save(teacher);
        result.put(ApiReturnConstants.SUCCESS, true);
        return result;
    }

    public ClassPerfTeacher getTeacherInfo(Long orgId, Long teacherId) {
        ClassPerfTeacher teacher = classPerfTeacherRepository.findByTeacherId(teacherId);
        if (teacher == null) {
            teacher = new ClassPerfTeacher();
            teacher.setOrgId(orgId);
            teacher.setTeacherId(teacherId);
            teacher.setRatingLimit(50);
            teacher.setResidualScore(50);
            teacher = classPerfTeacherRepository.save(teacher);
        }
        return teacher;
    }

    public PageData<ClassPerfTeacherDomain> getTeacherList(Pageable pageable, Long orgId, Long collegeId, String name) {
        Page<UserInfo> page = null;
        PageData<ClassPerfTeacherDomain> pageData = new PageData<>();
        if (collegeId != null && collegeId > 0l && !StringUtils.isEmpty(name)) {
            page = userInfoRepository.findByOrgIdAndCollegeIdAndUserTypeAndNameLikeOrOrgIdAndCollegeIdAndUserTypeAndJobNumLike(pageable, orgId, collegeId, UserType.B_TEACHER.getState(), name, orgId, collegeId, UserType.B_TEACHER.getState(), name);
        } else if (collegeId != null && collegeId > 0l) {
            page = userInfoRepository.findByOrgIdAndCollegeIdAndUserType(pageable, orgId, collegeId, UserType.B_TEACHER.getState());
        } else if (!StringUtils.isEmpty(name)) {
            page = userInfoRepository.findByOrgIdAndUserTypeAndNameLikeOrOrgIdAndUserTypeAndJobNumLike(pageable, orgId, UserType.B_TEACHER.getState(), name, orgId, UserType.B_TEACHER.getState(), name);
        } else {
            page = userInfoRepository.findByOrgIdAndUserType(pageable, orgId, UserType.B_TEACHER.getState());
        }
        if (page.getContent() != null && page.getContent().size() > 0) {
            Set<Long> teacherIds = new HashSet<>();
            for (UserInfo u : page.getContent()) {
                teacherIds.add(u.getUserId());
            }
            List<ClassPerfTeacher> classPerfTeachers = classPerfTeacherRepository.findByTeacherIdIn(teacherIds);
            Map<Long, ClassPerfTeacher> teacherMap = new HashMap<>();
            if (classPerfTeachers != null && classPerfTeachers.size() > 0) {
                for (ClassPerfTeacher item : classPerfTeachers) {
                    teacherMap.put(item.getTeacherId(), item);
                }
            }
            List<ClassPerfTeacherDomain> list = new ArrayList<>();
            for (UserInfo info : page.getContent()) {
                ClassPerfTeacherDomain d = new ClassPerfTeacherDomain();
                BeanUtils.copyProperties(info, d);
                ClassPerfTeacher ct = teacherMap.get(d.getUserId());
                if (ct != null) {
                    d.setRatingLimit(ct.getRatingLimit());
                    d.setResidualScore(ct.getResidualScore());
                } else {
                    d.setRatingLimit(50);
                    d.setResidualScore(50);
                }
                list.add(d);
            }
            pageData.setData(list);
        }

        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageSize(page.getSize());
        pageDomain.setPageNumber(page.getNumber());
        pageDomain.setTotalElements(page.getTotalElements());
        pageDomain.setTotalPages(page.getTotalPages());
        pageData.setPage(pageDomain);
        return pageData;
    }

    public PageData<ClassPerfStudent> getStudentList(Pageable pageable, Long orgId, Long collegeId, Long profId, Long classesId, String name) {
        Long semesterId = getSemesterId(orgId);
        Page<ClassPerfStudent> page = null;
        if (collegeId != null && collegeId > 0) {
            if (profId != null && profId > 0) {
                if (classesId != null && classesId > 0) {
                    if (!StringUtils.isEmpty(name)) {
                        page = classPerfStudentRepository.findByOrgIdAndSemesterIdAndCollegeIdAndProfIdAndClassesIdAndNameLikeOrOrgIdAndSemesterIdAndCollegeIdAndProfIdAndClassesIdAndJobNumLike(pageable, orgId, semesterId, collegeId, profId, classesId, name, orgId, semesterId, collegeId, profId, classesId, name);
                    } else {
                        page = classPerfStudentRepository.findByOrgIdAndSemesterIdAndCollegeIdAndProfIdAndClassesId(pageable, orgId, semesterId, collegeId, profId, classesId);
                    }
                } else {
                    if (!StringUtils.isEmpty(name)) {
                        page = classPerfStudentRepository.findByOrgIdAndSemesterIdAndCollegeIdAndProfIdAndNameLikeOrOrgIdAndSemesterIdAndCollegeIdAndProfIdAndJobNumLike(pageable, orgId, semesterId, collegeId, profId, name, orgId, semesterId, collegeId, profId, name);
                    } else {
                        page = classPerfStudentRepository.findByOrgIdAndSemesterIdAndCollegeIdAndProfId(pageable, orgId, semesterId, collegeId, profId);
                    }
                }
            } else {
                if (classesId != null && classesId > 0) {
                    if (!StringUtils.isEmpty(name)) {
                        page = classPerfStudentRepository.findByOrgIdAndSemesterIdAndCollegeIdAndClassesIdAndNameLikeOrOrgIdAndSemesterIdAndCollegeIdAndClassesIdAndJobNumLike(pageable, orgId, semesterId, collegeId, classesId, name, orgId, semesterId, collegeId, classesId, name);
                    } else {
                        page = classPerfStudentRepository.findByOrgIdAndSemesterIdAndCollegeIdAndClassesId(pageable, orgId, semesterId, collegeId, classesId);
                    }
                } else {
                    if (!StringUtils.isEmpty(name)) {
                        page = classPerfStudentRepository.findByOrgIdAndSemesterIdAndCollegeIdAndNameLikeOrOrgIdAndSemesterIdAndCollegeIdAndJobNumLike(pageable, orgId, semesterId, collegeId, name, orgId, semesterId, collegeId, name);
                    } else {
                        page = classPerfStudentRepository.findByOrgIdAndSemesterIdAndCollegeId(pageable, orgId, semesterId, collegeId);
                    }
                }
            }
        } else if (StringUtils.isEmpty(name)) {
            page = classPerfStudentRepository.findByOrgIdAndSemesterId(pageable, orgId, semesterId);
        } else {
            page = classPerfStudentRepository.findByOrgIdAndSemesterIdAndNameLikeOrOrgIdAndSemesterIdAndJobNumLike(pageable, orgId, semesterId, name, orgId, semesterId, name);
        }
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageSize(page.getSize());
        pageDomain.setPageNumber(page.getNumber());
        pageDomain.setTotalElements(page.getTotalElements());
        pageDomain.setTotalPages(page.getTotalPages());
        PageData<ClassPerfStudent> pageData = new PageData<>();
        pageData.setData(page.getContent());
        pageData.setPage(pageDomain);
        return pageData;
    }

    public PageData<ClassPerfStudent> getStudentList(Pageable pageable, Long orgId, String name) {
        Long semesterId = getSemesterId(orgId);
        Page<ClassPerfStudent> page = null;
        if (StringUtils.isEmpty(name)) {
            page = classPerfStudentRepository.findByOrgIdAndSemesterId(pageable, orgId, semesterId);
        } else {
            page = classPerfStudentRepository.findByOrgIdAndSemesterIdAndNameLikeOrOrgIdAndSemesterIdAndJobNumLike(pageable, orgId, semesterId, name, orgId, semesterId, name);
        }
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageSize(page.getSize());
        pageDomain.setPageNumber(page.getNumber());
        pageDomain.setTotalElements(page.getTotalElements());
        pageDomain.setTotalPages(page.getTotalPages());
        PageData<ClassPerfStudent> pageData = new PageData<>();
        pageData.setData(page.getContent());
        pageData.setPage(pageDomain);
        return pageData;
    }

    public ClassPerfStudent getStudent(Long orgId, Long stuId){
        Long semesterId = getSemesterId(orgId);
        return classPerfStudentRepository.findByStudentIdAndSemesterId(stuId, semesterId);
    }

    public List<ClassPerfLog> getStudentLogList(String classPerfId) {
        return classPerfLogRepository.findByClassPerfIdOrderByCreatedDateDesc(classPerfId);
    }

    public PageData<ClassPerfLog> getStudentLogList(Pageable pageable, String classPerfId, String teacherName) {
        Page<ClassPerfLog> page = null;
        if (StringUtils.isEmpty(teacherName)) {
            page = classPerfLogRepository.findByClassPerfIdOrderByCreatedDateDesc(pageable, classPerfId);
        } else {
            page = classPerfLogRepository.findByClassPerfIdAndTeacherNameLikeOrClassPerfIdAndTeacherJobnumLikeOrderByCreatedDateDesc(pageable, classPerfId, teacherName, classPerfId, teacherName);
        }
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageSize(page.getSize());
        pageDomain.setPageNumber(page.getNumber());
        pageDomain.setTotalElements(page.getTotalElements());
        pageDomain.setTotalPages(page.getTotalPages());
        PageData<ClassPerfLog> pageData = new PageData<>();
        pageData.setData(page.getContent());
        pageData.setPage(pageDomain);
        return pageData;
    }

    private Long getSemesterId(Long orgId) {
        Map<String, Object> datas = orgManagerRemoteClient.getSemester(orgId, null);
        if (datas != null && datas.get("data") != null) {
            Map<String, Object> data = (Map<String, Object>) datas.get("data");
            if (data != null && data.get("id") != null) {
                return Long.parseLong(data.get("id").toString());
            }
        }
        return null;
    }

    public void initAllTeacherLimitScore() {
        List<OrgInfo> list = orgInfoRepository.findAll();
        if (list != null && list.size() > 0) {
            for (OrgInfo item : list) {
                initTeacherLimitScore(item.getOrgId());
            }
        }
    }

    @Async("threadPool1")
    private void initTeacherLimitScore(Long orgId) {
        List<ClassPerfTeacher> teachers = classPerfTeacherRepository.findByOrgId(orgId);
        if (teachers != null && teachers.size() > 0) {
            for (ClassPerfTeacher item : teachers) {
                item.setResidualScore(item.getRatingLimit());
            }
            classPerfTeacherRepository.save(teachers);
        }
    }
}
