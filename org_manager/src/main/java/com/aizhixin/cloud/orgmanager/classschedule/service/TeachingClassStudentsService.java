/**
 *
 */
package com.aizhixin.cloud.orgmanager.classschedule.service;


import com.aizhixin.cloud.orgmanager.classschedule.domain.*;
import com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClass;
import com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClassStudents;
import com.aizhixin.cloud.orgmanager.classschedule.msg.dto.TeachingClassStudentMsgDTO;
import com.aizhixin.cloud.orgmanager.classschedule.repository.TeachingClassStudentsRepository;
import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.PageDomain;
import com.aizhixin.cloud.orgmanager.common.core.ApiReturnConstants;
import com.aizhixin.cloud.orgmanager.common.core.DataValidity;
import com.aizhixin.cloud.orgmanager.common.core.ErrorCode;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.common.domain.CountDomain;
import com.aizhixin.cloud.orgmanager.common.domain.IdIdNameDomain;
import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.common.service.DataSynService;
import com.aizhixin.cloud.orgmanager.company.core.RollCallType;
import com.aizhixin.cloud.orgmanager.company.core.UserType;
import com.aizhixin.cloud.orgmanager.company.domain.CourseDomain;
import com.aizhixin.cloud.orgmanager.company.domain.StudentDomain;
import com.aizhixin.cloud.orgmanager.company.domain.StudentSimpleDomain;
import com.aizhixin.cloud.orgmanager.company.entity.*;
import com.aizhixin.cloud.orgmanager.company.service.ClassesService;
import com.aizhixin.cloud.orgmanager.company.service.CourseService;
import com.aizhixin.cloud.orgmanager.company.service.SemesterService;
import com.aizhixin.cloud.orgmanager.company.service.UserService;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;

/**
 * 班级相关操作业务逻辑处理
 *
 * @author zhen.pan
 */
@Component
@Transactional
public class TeachingClassStudentsService {
    @Autowired
    private EntityManager em;
    @Autowired
    private TeachingClassStudentsRepository teachingClassStudentsRepository;
    @Autowired
    private TeachingClassService teachingClassService;
    @Autowired
    private UserService userService;
    @Autowired
    private ClassesService classesService;
    @Autowired
    private SemesterService semesterService;
    @Autowired
    private TeachingClassClassesService teachingClassClassesService;
    @Autowired
    private TeachingClassTeacherService teachingClassTeacherService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private DataSynService dataSynService;


    /**
     * 保存实体
     *
     * @param teachingClassStudents
     * @return
     */
    public TeachingClassStudents save(TeachingClassStudents teachingClassStudents) {
        return teachingClassStudentsRepository.save(teachingClassStudents);
    }

    public List<TeachingClassStudents> save(List<TeachingClassStudents> teachingClassStudentses) {
        return teachingClassStudentsRepository.save(teachingClassStudentses);
    }

    @Transactional(readOnly = true)
    public Page<StudentDomain> findSimpleStudentsByTeachingClass(TeachingClass teachingClass, Pageable pageable) {
        return teachingClassStudentsRepository.findSimpleStudentByTeachingClass(pageable, teachingClass);
    }

    @Transactional(readOnly = true)
    public Page<StudentDomain> findSimpleStudentByTeachingClassAndName(TeachingClass teachingClass, String name, Pageable pageable) {
        return teachingClassStudentsRepository.findSimpleStudentByTeachingClassAndName(pageable, teachingClass, name);
    }

    @Transactional(readOnly = true)
    public Page<StudentDomain> findSimpleStudentsByTeachingClassNotIncludeException(TeachingClass teachingClass, Pageable pageable) {
        return teachingClassStudentsRepository.findSimpleStudentByTeachingClassNotIncludeException(pageable, teachingClass, RollCallType.NORMAL.getState());
    }

    @Transactional(readOnly = true)
    public Long countByTeachingClassAndTeachers(TeachingClass teachingClass, List<User> students) {
        return teachingClassStudentsRepository.countByTeachingClassAndStudentIn(teachingClass, students);
    }

    @Transactional(readOnly = true)
    public List<TeachingClassStudents> findByTeachingClassAndStudent(TeachingClass teachingClass, User student) {
        return teachingClassStudentsRepository.findByTeachingClassAndStudent(teachingClass, student);
    }

    @Transactional(readOnly = true)
    public List<TeachingClassStudents> findByTeachingClassAndStudents(TeachingClass teachingClass, List<User> students) {
        return teachingClassStudentsRepository.findByTeachingClassAndStudentIn(teachingClass, students);
    }

    public void delete(List<TeachingClassStudents> ts) {
        for (TeachingClassStudents t : ts) {
            teachingClassStudentsRepository.delete(t);
        }
    }

    @Transactional(readOnly = true)
    public Long countByTeachingClass(TeachingClass teachingClass) {
        return teachingClassStudentsRepository.countByTeachingClass(teachingClass);
    }

    @Transactional(readOnly = true)
    public Long countByTeachingClassId(Long teachingClassId) {
        return teachingClassStudentsRepository.countByTeachingClass_id(teachingClassId);
    }

    @Transactional(readOnly = true)
    public Set<Long> findTeachingClassIdsByStudent(Semester semester, User student) {
        return teachingClassStudentsRepository.findTeachingClassByStudent(student, semester);
    }

    @Transactional(readOnly = true)
    public List<IdNameDomain> findSimpleClassesByTeachingClass(TeachingClass teachingClass) {
        return teachingClassStudentsRepository.findSimpleClassByTeachingClass(teachingClass);
    }

    @Transactional(readOnly = true)
    public List<ClassesCollegeDomain> findSimpleClassesByTeachingClassIds(Set teachingClassIds) {
        return teachingClassStudentsRepository.findSimpleClassByTeachingClassIds(teachingClassIds);
    }

    @Transactional(readOnly = true)
    public List<TeachStudentDomain> findByTeachStudents(TeachingClass teachingClass) {
        return teachingClassStudentsRepository.findTeachStudentByTeachingClass(teachingClass);
    }

    @Transactional(readOnly = true)
    public List<TeachStudentDomain> findByTeachStudents(List<TeachingClass> teachingClasses) {
        return teachingClassStudentsRepository.findTeachStudentByTeachingClass(teachingClasses);
    }

    @Transactional(readOnly = true)
    public List<StudentDomain> findTeachStudentByTeachingClassInAndClassesNotIn(List<TeachingClass> teachingClasses, List<Classes> classeses, String name) {
        return teachingClassStudentsRepository.findTeachStudentByTeachingClassInAndClassesNotIn(teachingClasses, classeses, name);
    }

    @Transactional(readOnly = true)
    public List<StudentDomain> findTeachStudentByTeachingClassIn(List<TeachingClass> teachingClasses, String name) {
        return teachingClassStudentsRepository.findTeachStudentByTeachingClassIn(teachingClasses, name);
    }

    @Transactional(readOnly = true)
    public List<StudentSimpleDomain> findSimpleStudent(TeachingClass teachingClass, Classes classes, String name, Pageable pageable) {
        StringBuilder hql = new StringBuilder("SELECT new com.aizhixin.cloud.orgmanager.company.domain.StudentSimpleDomain(t.student.id, t.student.name, t.student.jobNumber, t.student.phone, t.student.email, t.student.sex, t.student.classes.id, t.student.classes.name) FROM com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClassStudents t WHERE t.teachingClass=:teachingClass");
        if (null != classes) {
            hql.append(" AND t.student.classes = :classes");
        }
        if (!StringUtils.isEmpty(name)) {
            hql.append(" AND (t.student.name like :name OR t.student.jobNumber like :name )");
        }
        TypedQuery<StudentSimpleDomain> tq = em.createQuery(hql.toString(), StudentSimpleDomain.class);
        tq.setParameter("teachingClass", teachingClass);
        if (null != classes) {
            tq.setParameter("classes", classes);
        }
        if (!StringUtils.isEmpty(name)) {
            tq.setParameter("name", "%" + name + "%");
        }
        tq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        tq.setMaxResults(pageable.getPageSize());
        return tq.getResultList();
    }

    @Transactional(readOnly = true)
    public PageData<StudentSimpleDomain> findSimpleStudentPage(TeachingClass teachingClass, Classes classes, String name, Pageable pageable) {
        PageData<StudentSimpleDomain> pageData = new PageData<>();
        StringBuilder hql = new StringBuilder("SELECT new com.aizhixin.cloud.orgmanager.company.domain.StudentSimpleDomain(t.student.id, t.student.name, t.student.jobNumber, t.student.phone, t.student.email, t.student.sex, t.student.classes.id, t.student.classes.name) FROM com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClassStudents t WHERE t.teachingClass=:teachingClass");
        StringBuilder chql = new StringBuilder("SELECT count(t.student.id) FROM com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClassStudents t WHERE t.teachingClass=:teachingClass");
        if (null != classes) {
            hql.append(" AND t.student.classes = :classes");
            chql.append(" AND t.student.classes = :classes");
        }
        if (!StringUtils.isEmpty(name)) {
            hql.append(" AND (t.student.name like :name OR t.student.jobNumber like :name )");
            chql.append(" AND (t.student.name like :name OR t.student.jobNumber like :name )");
        }
        Query q = em.createQuery(chql.toString());
        q.setParameter("teachingClass", teachingClass);
        if (null != classes) {
            q.setParameter("classes", classes);
        }
        if (!StringUtils.isEmpty(name)) {
            q.setParameter("name", "%" + name + "%");
        }

        Long count = (Long) q.getSingleResult();
        pageData.getPage().setTotalElements(count);
        pageData.getPage().setPageNumber(pageable.getPageNumber() + 1);
        pageData.getPage().setTotalPages(PageUtil.cacalatePagesize(count, pageable.getPageSize()));
        if (count > 0) {
            TypedQuery<StudentSimpleDomain> tq = em.createQuery(hql.toString(), StudentSimpleDomain.class);
            tq.setParameter("teachingClass", teachingClass);
            if (null != classes) {
                tq.setParameter("classes", classes);
            }
            if (!StringUtils.isEmpty(name)) {
                tq.setParameter("name", "%" + name + "%");
            }
            tq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
            tq.setMaxResults(pageable.getPageSize());
            pageData.setData(tq.getResultList());
        }
        return pageData;
    }

    @Transactional(readOnly = true)
    public PageData<StudentSimpleDomain> findSimpleStudentByClassesPage(List<Classes> classes, String name, Pageable pageable) {
//		Map<String, Object> r = new HashMap<>();
        PageData<StudentSimpleDomain> pagedata = new PageData<>();
        StringBuilder hql = new StringBuilder("SELECT new com.aizhixin.cloud.orgmanager.company.domain.StudentSimpleDomain(t.id, t.name, t.jobNumber, t.phone, t.email, t.sex, t.classes.id, t.classes.name) FROM com.aizhixin.cloud.orgmanager.company.entity.User t WHERE t.deleteFlag=:deleteFlag and t.classes in (:classes)");
        StringBuilder chql = new StringBuilder("SELECT count(t.id) FROM com.aizhixin.cloud.orgmanager.company.entity.User t WHERE t.deleteFlag=:deleteFlag and t.classes in(:classes)");
        if (!StringUtils.isEmpty(name)) {
            hql.append(" AND (t.name like :name OR t.jobNumber like :name )");
            chql.append(" AND (t.name like :name OR t.jobNumber like :name )");
        }
        Query q = em.createQuery(chql.toString());
        q.setParameter("classes", classes);
        q.setParameter("deleteFlag", DataValidity.VALID.getState());

        if (!StringUtils.isEmpty(name)) {
            q.setParameter("name", "%" + name + "%");
        }

        Long count = (Long) q.getSingleResult();
        pagedata.getPage().setTotalElements(count);
        pagedata.getPage().setPageNumber(pageable.getPageNumber() + 1);
        pagedata.getPage().setPageSize(pageable.getPageSize());
        pagedata.getPage().setTotalPages(PageUtil.cacalatePagesize(count, pageable.getPageSize()));
        if (count > 0) {
            TypedQuery<StudentSimpleDomain> tq = em.createQuery(hql.toString(), StudentSimpleDomain.class);
            tq.setParameter("classes", classes);
            tq.setParameter("deleteFlag", DataValidity.VALID.getState());
            if (!StringUtils.isEmpty(name)) {
                tq.setParameter("name", "%" + name + "%");
            }
            tq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
            tq.setMaxResults(pageable.getPageSize());
            pagedata.setData(tq.getResultList());
        }
        return pagedata;
    }

    @Transactional(readOnly = true)
    public List<TeachingClassStudents> findByTeachingClass(TeachingClass teachingClasses) {
        return teachingClassStudentsRepository.findByTeachingClass(teachingClasses);
    }

    @Transactional(readOnly = true)
    public List<TeachingClass> findTeachingClassByStudentAndCourses(User student, List<Course> courses, Semester semester) {
        return teachingClassStudentsRepository.findByTeachingClassByStudentAndCourses(student, courses, semester);
    }

    @Transactional(readOnly = true)
    public Page<StudentDomain> findPageTeachStudentByTeachingClasses(Pageable pageable, TeachingClass teachingClasses, String name) {
        return teachingClassStudentsRepository.findPageTeachStudentByTeachingClassesAndName(pageable, teachingClasses, name, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public Page<StudentDomain> findPageTeachStudentByTeachingClasses(Pageable pageable, TeachingClass teachingClasses) {
        return teachingClassStudentsRepository.findPageTeachStudentByTeachingClasses(pageable, teachingClasses, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<TeachingClassStudents> findByIdIn(Set<Long> ids) {
        return teachingClassStudentsRepository.findByIdIn(ids);
    }

    public void deleteByTeachingClassList(List<TeachingClass> teachingClassList) {
        if (teachingClassList != null && teachingClassList.size() > 0) {
            teachingClassStudentsRepository.deleteByTeachingClassIn(teachingClassList);
        }
    }

    @Transactional(readOnly = true)
    public List<TeachingClassDomain> findTeachingClassByStudentAndSemester(User student, Semester semester) {
        return teachingClassStudentsRepository.findTeachingClassByStudentAndSemester(student, semester);
    }


    @Transactional(readOnly = true)
    public List<StudentDomain> findPageTeachStudentByTeachingClasses(TeachingClass teachingClasses) {
        return teachingClassStudentsRepository.findPageTeachStudentInfoByTeachingClasses(teachingClasses, UserType.B_STUDENT.getState(), DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<TeachingClass> findAllTeachingClass(Long orgId, Semester semester) {
        return teachingClassStudentsRepository.findAllTeachingClass(orgId, semester);
    }

    public void deleteByStudentAndSemster(User student, Semester semester) {
        teachingClassStudentsRepository.deleteByStudentAndSemester(student, semester);
    }

    public void deleteByStudent(User student) {
        //2018-03-22添加学期限制，只能删除当前学期的数据
        Semester semester = semesterService.getSemesterByDate(student.getOrgId(), new Date());
        if (null != semester) {
            deleteByStudentAndSemster(student, semester);
        }
    }

    @Transactional(readOnly = true)
    public List<CountDomain> countByTeachingClass(List<TeachingClass> teachingClassList) {
        return teachingClassStudentsRepository.countByTeachingClass(teachingClassList);
    }


    @Transactional(readOnly = true)
    public Set<Long> findTeachingClassIdsByStudent(User student) {
        return teachingClassStudentsRepository.findTeachingClassIdByStudent(student);
    }


    @Transactional(readOnly = true)
    public Set<Long> findTeachingClassIdsByStudentAndSemester(User student, Semester semester) {
        return teachingClassStudentsRepository.findTeachingClassIdByStudentAndSemester(student, semester);
    }

    @Transactional(readOnly = true)
    public List<TeachingclassAndClasses> findTeachingClassIdNameByClassesAndSemester(Set<Long> classesIds, Semester semester) {
        return teachingClassStudentsRepository.findTeachingClassIdNameByClassesAndSemester(classesIds, semester);
    }

    @Transactional(readOnly = true)
    public List<TeachingClassStudents> findByTeachingClassAndStudentClassesIn(TeachingClass teachingClass, Set<Classes> classes) {
        return teachingClassStudentsRepository.findByTeachingClassAndStudent_ClassesIn(teachingClass, classes);
    }
    //*************************************************************以下部分处理页面调用逻辑**********************************************************************//

    private void initTeachingClassByStudent(TeachingClass t, List<User> students, List<TeachingClassStudents> tcss) {
        for (User s : students) {
            TeachingClassStudents tcs = new TeachingClassStudents();
            tcs.setTeachingClass(t);
            tcs.setSemester(t.getSemester());
            tcs.setOrgId(t.getOrgId());
            tcs.setStudent(s);
            tcss.add(tcs);
        }
    }

//    @Transactional
    private TeachingClassStudentMsgDTO initStudent(TeachingClass t, User s) {
        TeachingClassStudentMsgDTO dto = new TeachingClassStudentMsgDTO();
        if (null != t) {
            dto.setOrgId(t.getOrgId());
            dto.setTeachingClassId(t.getId());
        }
        User u=  userService.findByUserId(s.getId());
        if (null != u) {
            dto.setStudentId(u.getId());
            dto.setStudentName(u.getName());
            dto.setStudentJobNumber(u.getJobNumber());
            dto.setSex(u.getSex());
            Classes classes = u.getClasses();
            if (null!=classes) {
                dto.setClassesId(classes.getId());
                dto.setClassesName(classes.getName());
            }
            College college = u.getCollege();
            if (null!=college){
                dto.setCollegeId(college.getId());
                dto.setCollegeName(college.getName());
            }
            Professional professional = u.getProfessional();
            if (null!=professional){
                dto.setProfId(professional.getId());
                dto.setProfName(professional.getName());
            }
        }
        return dto;
    }

    private TeachingClassStudentMsgDTO initStudent(TeachingClass t, Long studentId) {
        TeachingClassStudentMsgDTO dto = new TeachingClassStudentMsgDTO();
        dto.setOrgId(t.getOrgId());
        dto.setTeachingClassId(t.getId());
        dto.setStudentId(studentId);
        return dto;
    }

    public void save(TeachingClass t, Set<Long> studentIds) {
        if (null == studentIds || studentIds.size() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "添加学生时，学生ID列表是必须的");
        }
        List<User> students = userService.findByIds(studentIds);//没有验证学生
        if (null == students || students.size() != studentIds.size()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据学生的ID列表和实际查询到的信息不匹配");
        }
        long tsc = countByTeachingClassAndTeachers(t, students);
        if (tsc > 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "部分学生已经存在此教学班");
        }
        List<TeachingClassStudents> tcss = new ArrayList<>();
        initTeachingClassByStudent(t, students, tcss);
        if (tcss.size() > 0) {
            save(tcss);
            sendAddMsg(tcss);
        }
    }

    public void sendAddMsg(List<TeachingClassStudents> tcss) {
        List<TeachingClassStudentMsgDTO> msgList = new ArrayList<>();
        for (TeachingClassStudents t : tcss) {
            msgList.add(initStudent(t.getTeachingClass(), t.getStudent()));
        }
        if (!msgList.isEmpty()) {
            if (msgList.size()<=100){
                dataSynService.sendTeachingStudentAddMsg(msgList);
            }else {
                List<TeachingClassStudentMsgDTO> teachingClassStudentMsgDTOS = new ArrayList<>();
                for (int i=0;i<msgList.size();i++) {
                    if (i%100==0&&i!=0){
                        dataSynService.sendTeachingStudentAddMsg(teachingClassStudentMsgDTOS);
                        teachingClassStudentMsgDTOS.clear();
                    }
                    teachingClassStudentMsgDTOS.add(msgList.get(i));
                    if (i==msgList.size()-1&&!teachingClassStudentMsgDTOS.isEmpty()){
                        dataSynService.sendTeachingStudentAddMsg(teachingClassStudentMsgDTOS);
                    }
                }
            }

        }
    }




    public void save(Long teachingClassId, Set<Long> studentIds) {
        if (null == teachingClassId || teachingClassId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教学班ID是必须的");
        }
        TeachingClass t = teachingClassService.findById(teachingClassId);
        if (null != t) {
            save(t, studentIds);
        }
    }

    @Transactional(readOnly = true)
    public PageData<StudentDomain> list(Long teachingClassId, String name, Pageable pageable) {
        PageData<StudentDomain> r = new PageData<>();
        r.getPage().setPageNumber(pageable.getPageNumber() + 1);
        r.getPage().setPageSize(pageable.getPageSize());
        TeachingClass t = teachingClassService.findById(teachingClassId);
        if (null != t) {
            Page<StudentDomain> page = null;
            if (StringUtils.isEmpty(name)) {
                page = findSimpleStudentsByTeachingClass(t, pageable);
            } else {
                page = findSimpleStudentByTeachingClassAndName(t, name, pageable);
            }
            if (null != page) {
                r.setData(page.getContent());
                r.getPage().setTotalElements(page.getTotalElements());
                r.getPage().setTotalPages(page.getTotalPages());
            }
        }
        return r;
    }

    @Transactional(readOnly = true)
    public PageData<StudentDomain> listNotIncludeException(Long teachingClassId, Pageable pageable) {
        PageData<StudentDomain> r = new PageData<>();
        r.getPage().setPageNumber(pageable.getPageNumber() + 1);
        r.getPage().setPageSize(pageable.getPageSize());
        TeachingClass t = teachingClassService.findById(teachingClassId);
        if (null != t) {
            Page<StudentDomain> page = findSimpleStudentsByTeachingClassNotIncludeException(t, pageable);
            if (null != page) {
                r.setData(page.getContent());
                r.getPage().setTotalElements(page.getTotalElements());
                r.getPage().setTotalPages(page.getTotalPages());
            }
        }
        return r;
    }

    public void delete(Long teachingClassId, Long studentId) {
        TeachingClass t = teachingClassService.findById(teachingClassId);
        if (null != t) {
            User student = userService.findById(studentId);
            if (null != student) {
                List<TeachingClassStudentMsgDTO> msgList = new ArrayList<>();
                List<TeachingClassStudents> ts = findByTeachingClassAndStudent(t, student);
                if (ts.size() > 0) {
                    delete(ts);
                    msgList.add(initStudent(t, student));
                    dataSynService.sendTeachingStudentDeleteMsg(msgList);
                }
            } else {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据学生的ID[" + studentId + "]查找不到对应的学生信息");
            }
        } else {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教学班ID[" + teachingClassId + "]没有查找到对应的数据");
        }
    }

    public void delete(Long teachingClassId, Set<Long> studentIds) {
        TeachingClass t = teachingClassService.findById(teachingClassId);
        if (null != t) {
            List<User> students = userService.findByIds(studentIds);
            if (null != students && students.size() == studentIds.size()) {
                List<TeachingClassStudents> ts = findByTeachingClassAndStudents(t, students);
                if (ts.size() > 0) {
                    delete(ts);
                    List<TeachingClassStudentMsgDTO> msgList = new ArrayList<>();
                    StringBuilder sb = new StringBuilder();
                    for (Long sid : studentIds) {
                        sb.append(",").append(sid);
                        msgList.add(initStudent(t, sid));
                    }
                    dataSynService.sendTeachingStudentDeleteMsg(msgList);
                }
            } else {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据学生的ID列表和实际查询到的信息不匹配");
            }
        } else {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教学班ID[" + teachingClassId + "]没有查找到对应的数据");
        }
    }

    public void delete(Long teachingClassId) {
        TeachingClass t = teachingClassService.findById(teachingClassId);
        if (null != t) {
                List<TeachingClassStudents> ts = findByTeachingClass(t);
                if (ts.size() > 0) {
                    delete(ts);
                    List<TeachingClassStudentMsgDTO> msgList = new ArrayList<>();
                    for (TeachingClassStudents sid : ts) {
                        msgList.add(initStudent(t, sid.getStudent().getId()));
                    }
                    dataSynService.sendTeachingStudentDeleteMsg(msgList);
                }
        } else {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教学班ID[" + teachingClassId + "]没有查找到对应的数据");
        }
    }

    @Transactional(readOnly = true)
    public Set<Long> getStudentTeachingClassIds(Semester semester, User student) {
        Set<Long> teachingClassIds = new HashSet<>();
        Set<Long> sts = findTeachingClassIdsByStudent(semester, student);
        if (null != sts & sts.size() > 0) {
            teachingClassIds.addAll(sts);
        }
        //去掉学生通过行政班关联教学班
//        if (null != student.getClasses()) {
//            sts = teachingClassClassesService.findTeachingClassIdsByClasses(semester, student.getClasses());
//            if (null != sts & sts.size() > 0) {
//                teachingClassIds.addAll(sts);
//            }
//        }
        return teachingClassIds;
    }

    @Transactional(readOnly = true)
    public List<CourseDomain> findSemesterStudentCourse(Long studentId, Long semesterId) {
        List<CourseDomain> r = new ArrayList<>();
        User student = userService.findById(studentId);
        if (null == student) {
            return r;
        }
        Semester semester = semesterService.getSemesterByOrgIdAndIdAndDate(student.getOrgId(), semesterId, null);
        if (null == semester) {
            return r;
        }

        Set<Long> teachingClassIds = getStudentTeachingClassIds(semester, student);
        if (teachingClassIds.size() > 0) {
            r = teachingClassService.findOnlyCourseIdNameByIds(teachingClassIds);
        }
        return r;
    }

    @Transactional(readOnly = true)
    public PageData<CourseDomain> findSemesterStudentCoursePage(Long studentId, Long semesterId, Pageable pageable) {
        PageData<CourseDomain> r = new PageData<>();
        User student = userService.findById(studentId);
        if (null == student) {
            return r;
        }
        Semester semester = semesterService.getSemesterByOrgIdAndIdAndDate(student.getOrgId(), semesterId, null);
        if (null == semester) {
            return r;
        }

        Set<Long> teachingClassIds = getStudentTeachingClassIds(semester, student);
        if (teachingClassIds.size() > 0) {
            Page page = teachingClassService.findOnlyCourseIdNameByIds(pageable, teachingClassIds);
            r.setData(page.getContent());
            r.getPage().setTotalElements(page.getTotalElements());
            r.getPage().setTotalPages(page.getTotalPages());
            r.getPage().setPageNumber(pageable.getPageNumber() + 1);
            r.getPage().setPageSize(pageable.getPageSize());
        }
        return r;
    }

    @Transactional(readOnly = true)
    public List<StudentSimpleDomain> findSimpleStudent(Long teachingClassId, Long classesId, String name, Pageable pageable) {
        List<StudentSimpleDomain> data = new ArrayList<>();
        if (null == teachingClassId || teachingClassId < 0) {
            return data;
        }
        TeachingClass teachingClass = teachingClassService.findById(teachingClassId);
        if (null == teachingClass) {
            return data;
        }
        Classes classes = null;
        if (null != classesId && classesId > 0) {
            classes = classesService.findById(classesId);
        }
        data = findSimpleStudent(teachingClass, classes, name, pageable);
        return data;
    }


    @Transactional(readOnly = true)
    public List<StudentDomain> findTeachingclassStudent(Long teachingClassId) {
        List<StudentDomain> data = new ArrayList<>();
        if (null == teachingClassId || teachingClassId < 0) {
            return data;
        }
        TeachingClass teachingClass = teachingClassService.findById(teachingClassId);
        if (null == teachingClass) {
            return data;
        }
        return findPageTeachStudentByTeachingClasses(teachingClass);
    }

    @Transactional(readOnly = true)
    public PageData<StudentSimpleDomain> findSimpleStudentPage(Long teachingClassId, Long classesId, String name, Pageable pageable) {
        if (null == teachingClassId || teachingClassId < 0) {
            return new PageData<>();
        }
        TeachingClass teachingClass = teachingClassService.findById(teachingClassId);
        if (null == teachingClass) {
            return new PageData<>();
        }
        Classes classes = null;
        if (null != classesId && classesId > 0) {
            classes = classesService.findById(classesId);
        }
        return findSimpleStudentPage(teachingClass, classes, name, pageable);
    }

    public List<IdIdNameDomain> findTeacherByStudentAndCourse(TeacherOfStudentDomain d) {
        if (null != d) {
            User student = null;
            if (null != d.getStudentId() && d.getStudentId() > 0) {
                student = userService.findById(d.getStudentId());
            }
            if (null == student) return null;
            List<Course> cs = null;
            if (null != d.getCourseIds() && d.getCourseIds().size() > 0) {
                cs = courseService.findByIds(d.getCourseIds());
            }
            Semester semester = semesterService.getSemesterByOrgIdAndIdAndDate(student.getOrgId(), d.getSemersterId(), null);
            if (null != student && null != cs && cs.size() > 0 && null != semester) {
                List<TeachingClass> ts = findTeachingClassByStudentAndCourses(student, cs, semester);
                Set<Long> tids = new HashSet<>();
                Map<Long, TeachingClass> ttc = new HashMap<>();
                for (TeachingClass t : ts) {
                    tids.add(t.getId());
                    ttc.put(t.getId(), t);
                }
                if (null != student.getClasses()) {
                    List<TeachingClass> ts2 = teachingClassClassesService.findByTeachingClassByClassesAndCourses(student.getClasses(), cs, semester);
                    for (TeachingClass t : ts2) {
                        if (!tids.contains(t.getId())) {
                            ts.add(t);
                            ttc.put(t.getId(), t);
                        }
                    }
                }
                if (ts.size() > 0) {
                    List<IdIdNameDomain> r = teachingClassTeacherService.findTeacherByTeachingClassesIn(ts);
                    for (IdIdNameDomain c : r) {//可优化为一次查询
                        TeachingClass t = ttc.get(c.getLogicId());
                        if (null != t && null != t.getCourse()) {
                            c.setLogicId(t.getCourse().getId());
                        }
                    }
                    return r;
                }
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> queryStudentByTeachingClass(Pageable pageable, Long teachingclassId, String name) {
        Map<String, Object> r = new HashedMap();
        Page<StudentDomain> page = null;
        PageDomain p = new PageDomain();
        p.setPageNumber(pageable.getPageNumber());
        p.setPageSize(pageable.getPageSize());
        r.put(ApiReturnConstants.PAGE, p);
        TeachingClass teachingClass = null;
        if (null != teachingclassId && teachingclassId > 0) {
            teachingClass = teachingClassService.findById(teachingclassId);
        }
        if (null != teachingClass) {
            if (StringUtils.isEmpty(name)) {
                page = findPageTeachStudentByTeachingClasses(pageable, teachingClass);
            } else {
                page = findPageTeachStudentByTeachingClasses(pageable, teachingClass, name);
            }
            if (null != page) {
                p.setTotalElements(page.getTotalElements());
                p.setTotalPages(page.getTotalPages());
                r.put(ApiReturnConstants.DATA, page.getContent());
            }
        }
        return r;
    }

    @Transactional(readOnly = true)
    public List<TeachingclassCourseTeacherListDomain> findTeachingclassByStudentAndSemester(Long studentId, Long semesterId) {
        List<TeachingClassDomain> r = new ArrayList<>();
        List<TeachingclassCourseTeacherListDomain> list = new ArrayList<>();
        User student = userService.findById(studentId);
        if (null == student) {
            return list;
        }
        Semester semester = semesterService.getSemesterByOrgIdAndIdAndDate(student.getOrgId(), semesterId, null);
        if (null == semester) {
            return list;
        }

        Set<Long> tids = new HashSet<>();

        List<TeachingClassDomain> optTeachingClassList = findTeachingClassByStudentAndSemester(student, semester);
        if (null != optTeachingClassList && optTeachingClassList.size() > 0) {
            r.addAll(optTeachingClassList);
            for (TeachingClassDomain tcd : optTeachingClassList) {
                tids.add(tcd.getId());
            }
        }
        if (null != student.getClasses()) {
            List<TeachingClassDomain> mustTeachingClassList = teachingClassClassesService.findByTeachingClassByClassesAndSemester(student.getClasses(), semester);
            for (TeachingClassDomain tcd : mustTeachingClassList) {
                if (!tids.contains(tcd.getId())) {
                    tids.add(tcd.getId());
                    r.add(tcd);
                }
            }
        }
        if (tids.size() > 0) {
            List<IdIdNameDomain> teachingclassTeacherList = teachingClassTeacherService.findTeacherByTeachingClassIds(tids);
            Map<Long, List<IdNameDomain>> teacherMap = new HashMap<>();
            for (IdIdNameDomain d : teachingclassTeacherList) {
                List<IdNameDomain> teachers = teacherMap.get(d.getLogicId());
                if (null == teachers) {
                    teachers = new ArrayList<>();
                    teacherMap.put(d.getLogicId(), teachers);
                }
                teachers.add(new IdNameDomain(d.getId(), d.getName()));
            }
            for (TeachingClassDomain t : r) {
                TeachingclassCourseTeacherListDomain d = new TeachingclassCourseTeacherListDomain(t.getId(), t.getName(), t.getCode(), t.getCourseId(), t.getCourseName());
                list.add(d);
                d.setTeachers(teacherMap.get(t.getId()));
            }
        }
        return list;
    }

    /**
     * 按照行政班保存教学班的学生信息
     *
     * @param t       教学班
     * @param classes 行政班
     */
    public void saveStudentByClasses(TeachingClass t, Set<Classes> classes) {
        if (null == t) {
            return;
        }
        if (null == classes || classes.size() <= 0) {
            return;
        }
        List<User> students = userService.findByClassesInAndUserType(classes, UserType.B_STUDENT.getState());
        List<TeachingClassStudents> tcss = new ArrayList<>();
        initTeachingClassByStudent(t, students, tcss);
        if (tcss.size() > 0) {
            save(tcss);
            sendAddMsg(tcss);
        }
    }
}