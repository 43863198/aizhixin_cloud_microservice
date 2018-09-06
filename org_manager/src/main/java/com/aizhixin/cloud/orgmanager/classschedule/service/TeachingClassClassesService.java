/**
 *
 */
package com.aizhixin.cloud.orgmanager.classschedule.service;


import com.aizhixin.cloud.orgmanager.classschedule.core.ClassesOrStudents;
import com.aizhixin.cloud.orgmanager.classschedule.domain.ClassesCollegeDomain;
import com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingClassDomain;
import com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingclassAndClasses;
import com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClass;
import com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClassClasses;
import com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClassStudents;
import com.aizhixin.cloud.orgmanager.classschedule.repository.TeachingClassClassesRepository;
import com.aizhixin.cloud.orgmanager.common.core.ApiReturnConstants;
import com.aizhixin.cloud.orgmanager.common.core.ErrorCode;
import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.company.entity.Classes;
import com.aizhixin.cloud.orgmanager.company.entity.Course;
import com.aizhixin.cloud.orgmanager.company.entity.Semester;
import com.aizhixin.cloud.orgmanager.company.service.ClassesService;
import com.aizhixin.cloud.orgmanager.company.service.SemesterService;
import com.aizhixin.cloud.orgmanager.company.service.UserService;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 班级相关操作业务逻辑处理
 *
 * @author zhen.pan
 */
@Component
@Transactional
public class TeachingClassClassesService {
    @Autowired
    private TeachingClassClassesRepository teachingClassClassesRepository;
    @Autowired
    private TeachingClassService teachingClassService;
    @Autowired
    private TeachingClassStudentsService teachingClassStudentsService;
    @Autowired
    private ClassesService classesService;
    @Autowired
    private UserService userService;
    @Autowired
    private SemesterService semesterService;

    /**
     * 保存实体
     *
     * @param teachingClassClasses
     * @return
     */
    public TeachingClassClasses save(TeachingClassClasses teachingClassClasses) {
        return teachingClassClassesRepository.save(teachingClassClasses);
    }

    public void save(List <TeachingClassClasses> teachingClassClassess) {
        teachingClassClassesRepository.save(teachingClassClassess);
    }

    @Transactional(readOnly = true)
    public List <IdNameDomain> findSimpleClassesByTeachingClass(TeachingClass teachingClass) {
        return teachingClassClassesRepository.findSimpleClassesByTeachingClass(teachingClass);
    }

    @Transactional(readOnly = true)
    public List <ClassesCollegeDomain> findSimpleClassesByTeachingClass(Set teachingClassIds) {
        return teachingClassClassesRepository.findSimpleClassesByTeachingClassIds(teachingClassIds);
    }

    @Transactional(readOnly = true)
    public Long countByTeachingClassAndClasseses(TeachingClass teachingClass, List <Classes> classeses) {
        return teachingClassClassesRepository.countByTeachingClassAndClassesIn(teachingClass, classeses);
    }

    @Transactional(readOnly = true)
    public List <TeachingClassClasses> findByTeachingClassAndClass(TeachingClass teachingClass, Classes classes) {
        return teachingClassClassesRepository.findByTeachingClassAndClasses(teachingClass, classes);
    }

    public void delete(List <TeachingClassClasses> ts) {
        for (TeachingClassClasses t : ts) {
            teachingClassClassesRepository.delete(t);
        }
    }

    @Transactional(readOnly = true)
    public Long countByTeachingClass(TeachingClass teachingClass) {
        return teachingClassClassesRepository.countByTeachingClass(teachingClass);
    }

    @Transactional(readOnly = true)
    public List <Classes> findClassesByTeachingClass(TeachingClass teachingClass) {
        return teachingClassClassesRepository.findClassesByTeachingClass(teachingClass);
    }

    @Transactional(readOnly = true)
    public List <Classes> findClassesByTeachingClassId(Long teachingClassId) {
        return teachingClassClassesRepository.findClassesByTeachingClassId(teachingClassId);
    }

    @Transactional(readOnly = true)
    public Set <Long> findTeachingClassIdsByClasses(Semester semester, Classes classes) {
        return teachingClassClassesRepository.findTeachingClassByClasses(classes, semester);
    }

    @Transactional(readOnly = true)
    public List <TeachingClassClasses> findByTeachingClass(TeachingClass teachingClass) {
        return teachingClassClassesRepository.findByTeachingClass(teachingClass);
    }

    @Transactional(readOnly = true)
    public List <TeachingClass> findByTeachingClassByClassesAndCourses(Classes classes, List <Course> courses, Semester semester) {
        return teachingClassClassesRepository.findByTeachingClassByClassesAndCourses(classes, courses, semester);
    }

    public void deleteByTeachingClassList(List <TeachingClass> teachingClassList) {
        teachingClassClassesRepository.deleteByTeachingClassIn(teachingClassList);
    }

    @Transactional(readOnly = true)
    public List <TeachingClassDomain> findByTeachingClassByClassesAndSemester(Classes classes, Semester semester) {
        return teachingClassClassesRepository.findTeachingClassByClassesAndSemester(classes, semester);
    }

    @Transactional(readOnly = true)
    public List <TeachingClass> findAllTeachingClass(Long orgId, Semester semester) {
        return teachingClassClassesRepository.findAllTeachingClass(orgId, semester);
    }


    @Transactional(readOnly = true)
    public List <TeachingclassAndClasses> findAllTeachingClass(Set<Long> classesIds, Semester semester) {
        return teachingClassClassesRepository.findTeachingClassIdNameByClassesAndSemester(classesIds, semester);
    }
    //*************************************************************以下部分处理页面调用逻辑**********************************************************************//

    public void save(TeachingClass t, Set <Long> classesIds) {
        if (ClassesOrStudents.CLASSES.getState() != t.getClassOrStudents()) {//按照行政班添加学校
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教学班必须是按照行政班添加的才能使用");
        }
        if (null == classesIds || classesIds.size() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "按照行政班添加学生时，行政班ID列表是必须的");
        }
        List <Classes> classeses = classesService.findByIds(classesIds);
        if (null == classeses || classeses.size() != classesIds.size()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据行政班级的ID列表和实际查询到的信息不匹配");
        }
        long cc = countByTeachingClassAndClasseses(t, classeses);
        if (cc > 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "部分行政班已经存在于此教学班");
        }
        List <TeachingClassClasses> teachingClassClassess = new ArrayList <>();
        Set<Classes> classesSet = new HashSet<>();
        for (Classes c : classeses) {
            TeachingClassClasses tcc = new TeachingClassClasses();
            tcc.setTeachingClass(t);
            tcc.setClasses(c);
            tcc.setSemester(t.getSemester());
            tcc.setOrgId(t.getOrgId());
            teachingClassClassess.add(tcc);
            classesSet.add(c);
        }
        if (teachingClassClassess.size() > 0) {
            save(teachingClassClassess);
            teachingClassStudentsService.saveStudentByClasses(t, classesSet);
//			List<Classes> cs = findClassesByTeachingClass(t);
//			if (cs.size() > 0) {
//				t.setClassesNames(getClassesNames(cs));
//				t.setStudentsCount(countStudentsByClasses(cs));
//				teachingClassService.save(t);
//			}
        }
    }

    public void save(Long teachingClassId, Set <Long> classesIds) {
        if (null == teachingClassId || teachingClassId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教学班ID是必须的");
        }
        TeachingClass t = teachingClassService.findById(teachingClassId);
        if (null != t) {
            save(t, classesIds);
        } else {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教学班ID[" + teachingClassId + "]没有查找到对应的数据");
        }
    }

    @Transactional(readOnly = true)
    public Map <String, Object> list(Long teachingClassId) {
        Map <String, Object> res = new HashedMap();
        TeachingClass t = teachingClassService.findById(teachingClassId);
        if (null != t) {
            List <IdNameDomain> data = findSimpleClassesByTeachingClass(t);
            res.put(ApiReturnConstants.DATA, data);
        }
        return res;
    }

    public void delete(Long teachingClassId, Long classesId) {
        TeachingClass t = teachingClassService.findById(teachingClassId);
        if (null != t) {
            if (ClassesOrStudents.CLASSES.getState() == t.getClassOrStudents()) {
                Classes classes = classesService.findById(classesId);
                if (null != classes) {
                    List <TeachingClassClasses> ts = findByTeachingClassAndClass(t, classes);
                    if (ts.size() > 0) {
                        delete(ts);
//                        List <Classes> cs = findClassesByTeachingClass(t);
//                        if (cs.size() > 0) {
//                            t.setClassesNames(getClassesNames(cs));
//                            t.setStudentsCount(countStudentsByClasses(cs));
//                            teachingClassService.save(t);
//                        }
                    }
                    Set<Classes> classesSet = new HashSet<>();
                    classesSet.add(classes);
                    List<TeachingClassStudents> students = teachingClassStudentsService.findByTeachingClassAndStudentClassesIn(t, classesSet);
                    if (null != students && !students.isEmpty()) {
                        teachingClassStudentsService.delete(students);
                    }
                } else {
                    throw new CommonException(ErrorCode.ID_IS_REQUIRED, "行政班ID[" + classesId + "]没有查找到对应的数据");
                }
            } else {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教学班ID[" + teachingClassId + "]不是按照行政班导入学生的");
            }
        } else {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教学班ID[" + teachingClassId + "]没有查找到对应的数据");
        }
    }

    public String getClassesNames(List <Classes> list) {
        StringBuilder cms = new StringBuilder();
        for (Classes c : list) {
            cms.append(c.getName()).append(",");
        }
        if (cms.length() > 0) {
            return cms.substring(0, cms.length() - 1);
        }
        return null;
    }

    public long countStudentsByClasses(List <Classes> list) {
        return userService.countByClasses(list);
    }

    @Transactional(readOnly = true)
    public List<TeachingclassAndClasses> queryTeachingclassAndClassesByClassesIds(Long semesterId, Set<Long> classesIds) {
        Semester semester = null;
        if (null != semesterId && semesterId > 0) {
            semester = semesterService.findById(semesterId);
        }
        if (null == semester || null == classesIds || classesIds.size() <= 0) {
            return new ArrayList<>();
        }

        return findAllTeachingClass(classesIds, semester);
    }
}