/**
 *
 */
package com.aizhixin.cloud.orgmanager.classschedule.service;


import com.aizhixin.cloud.orgmanager.classschedule.core.ClassesOrStudents;
import com.aizhixin.cloud.orgmanager.classschedule.core.SingleOrDouble;
import com.aizhixin.cloud.orgmanager.classschedule.domain.*;
import com.aizhixin.cloud.orgmanager.classschedule.domain.excel.*;
import com.aizhixin.cloud.orgmanager.classschedule.entity.*;
import com.aizhixin.cloud.orgmanager.classschedule.msg.dto.TeachingClassMsgDTO;
import com.aizhixin.cloud.orgmanager.classschedule.repository.TeachingClassRepository;
import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.async.AsyncTaskBase;
import com.aizhixin.cloud.orgmanager.common.core.ErrorCode;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.common.domain.CountDomain;
import com.aizhixin.cloud.orgmanager.common.domain.IdIdNameDomain;
import com.aizhixin.cloud.orgmanager.common.domain.IdNameCountDomain;
import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.common.provider.store.redis.RedisTokenStore;
import com.aizhixin.cloud.orgmanager.common.service.DataSynService;
import com.aizhixin.cloud.orgmanager.company.core.UserType;
import com.aizhixin.cloud.orgmanager.company.domain.CourseDomain;
import com.aizhixin.cloud.orgmanager.company.entity.*;
import com.aizhixin.cloud.orgmanager.company.service.*;
import com.aizhixin.cloud.orgmanager.remote.DDClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

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
public class TeachingClassService {
    @Autowired
    private EntityManager em;
    @Autowired
    private TeachingClassRepository teachingClassRepository;
    @Autowired
    private SemesterService semesterService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private TeachingClassTeacherService teachingClassTeacherService;
    @Autowired
    private TeachingClassClassesService teachingClassClassesService;
    @Autowired
    private TeachingClassStudentsService teachingClassStudentsService;
    @Autowired
    private UserService userService;
    @Autowired
    private SchoolTimeTableService schoolTimeTableService;
    @Autowired
    private ExcelCourseScheduleDataService excelCourseScheduleDataService;
    @Autowired
    private ClassesService classesService;
    @Autowired
    private WeekService weekService;
    @Autowired
    private PeriodService periodService;
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    @Autowired
    private AsyncTaskBase asyncTaskBase;
    @Autowired
    private TempCourseScheduleService tempCourseScheduleService;
    @Autowired
    private DataSynService dataSynService;
    @Autowired
    private DDClient ddClient;

    /**
     * 保存实体
     *
     * @param teachingClass
     * @return
     */
    public TeachingClass save(TeachingClass teachingClass) {
        return teachingClassRepository.save(teachingClass);
    }

    @Transactional(readOnly = true)
    public TeachingClass findById(Long id) {
        return teachingClassRepository.findOne(id);
    }

//    @Transactional(readOnly = true)
//    public Page <TeachingClassDomain> findOrgIdAndName(Pageable pageable, Long orgId, String name) {
//        return teachingClassRepository.findByOrgIdAndName(pageable, orgId, name);
//    }
//
//    @Transactional(readOnly = true)
//    public Page <TeachingClassDomain> findOrgId(Pageable pageable, Long orgId) {
//        return teachingClassRepository.findByOrgId(pageable, orgId);
//    }

    public void delete(TeachingClass t) {
        teachingClassRepository.delete(t);
    }

    @Transactional(readOnly = true)
    public List<TeachingClassDomain> findByIds(Set<Long> ids) {
        return teachingClassRepository.findByIds(ids);
    }

    @Transactional(readOnly = true)
    public List<IdNameDomain> findCourseNameByIds(Set<Long> ids) {
        return teachingClassRepository.findCouseNameByIds(ids);
    }

    @Transactional(readOnly = true)
    public List<IdIdNameDomain> findCourseIdNameByIds(Set<Long> ids) {
        return teachingClassRepository.findCouseIdNameByIds(ids);
    }

    @Transactional(readOnly = true)
    public Page<CourseDomain> findOnlyCourseIdNameByIds(Pageable pageable, Set<Long> ids) {
        return teachingClassRepository.findOnlyCouseIdNameByIds(pageable, ids);
    }

    @Transactional(readOnly = true)
    public List<CourseDomain> findOnlyCourseIdNameByIds(Set<Long> ids) {
        return teachingClassRepository.findOnlyCouseIdNameByIds(ids);
    }

    @Transactional(readOnly = true)
    public List<IdNameDomain> findTeachingClassIdNameByIdsAndCourseAndSemester(Set<Long> ids, Course course, Semester semester) {
        return teachingClassRepository.findTeachingClassIdNameByIdsAndCourseAndSemester(ids, course, semester);
    }

    @Transactional(readOnly = true)
    public Long countByCode(Long orgId, String code) {
        return teachingClassRepository.countByOrgIdAndCode(orgId, code);
    }

    @Transactional(readOnly = true)
    public Long countByCodeAndIdNot(Long orgId, String code, Long id) {
        return teachingClassRepository.countByOrgIdAndCodeAndIdNot(orgId, code, id);
    }

    @Transactional(readOnly = true)
    public List<TeachingClassSimpleDomain> findIdNameByIds(Set<Long> ids) {
        return teachingClassRepository.findIdNameByIds(ids);
    }

    public List<TeachingClass> save(List<TeachingClass> teachingClasses) {
        return teachingClassRepository.save(teachingClasses);
    }

    @Transactional(readOnly = true)
    public List<TeachingClass> findByIdIn(Set<Long> ids) {
        return teachingClassRepository.findByIdIn(ids);
    }

    @Transactional(readOnly = true)
    public List<TeachingClass> findByCodeIn(Long orgId, Set<String> codes) {
        return teachingClassRepository.findByOrgIdAndCodeIn(orgId, codes);
    }

    @Transactional
    public List<TeachingClass> findByOrgIdAndCodeIn(Long orgId, Set<String> codes) {
        List <TeachingClass> list = teachingClassRepository.findByOrgIdAndCodeIn(orgId, codes);
        if(list != null && list.size() > 0){
            for(TeachingClass item : list){
                Semester s = item.getSemester();
                s.getId();
                Course c = item.getCourse();
                c.getId();
            }
        }
        return list;
    }

    public PageData findCourseAndTeacherPage(TeachingClassQueryDomain teachingClassQueryDomain, Semester semester, College colleg) {
        Pageable pageable = PageUtil.createNoErrorPageRequest(teachingClassQueryDomain.getPageNumber(), teachingClassQueryDomain.getPageSize());
        PageData<TeachingClassCourseTeacherDomain> p = new PageData<>();
        Map<String, Object> qryParam = new HashMap<>();
        qryParam.put("orgId", teachingClassQueryDomain.getOrgId());
        if (StringUtils.isEmpty(teachingClassQueryDomain.getTeacherName())) {
            StringBuilder hql = new StringBuilder("SELECT  new com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingClassCourseTeacherDomain(t.id, t.name, t.code, t.course.name) FROM com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClass t WHERE t.orgId=:orgId");
            StringBuilder chql = new StringBuilder("SELECT COUNT(t.course.id) FROM com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClass t WHERE t.orgId=:orgId");
            if (null != semester) {
                hql.append(" AND t.semester = :semester");
                chql.append(" AND t.semester = :semester");
                qryParam.put("semester", semester);
            }
            if (!StringUtils.isEmpty(teachingClassQueryDomain.getCourseName())) {
                hql.append(" AND t.course.name like :courseName");
                chql.append(" AND t.course.name like :courseName");
                qryParam.put("courseName", "%" + teachingClassQueryDomain.getCourseName() + "%");
            }
            if (null != teachingClassQueryDomain.getTeachingClassIds() && teachingClassQueryDomain.getTeachingClassIds().size() > 0) {
                hql.append(" AND t.id not in (:tids)");
                chql.append(" AND t.id not in (:tids)");
                qryParam.put("tids", teachingClassQueryDomain.getTeachingClassIds());
            }
            Query q = em.createQuery(chql.toString());
            for (Map.Entry<String, Object> e : qryParam.entrySet()) {
                q.setParameter(e.getKey(), e.getValue());
            }
            Long count = (Long) q.getSingleResult();
            p.getPage().setTotalElements(count);
            p.getPage().setPageNumber(pageable.getPageNumber());
            p.getPage().setPageSize(pageable.getPageSize());
            p.getPage().setTotalPages(PageUtil.cacalatePagesize(count, pageable.getPageSize()));
            if (count > 0) {
                TypedQuery<TeachingClassCourseTeacherDomain> tq = em.createQuery(hql.toString(), TeachingClassCourseTeacherDomain.class);
                for (Map.Entry<String, Object> e : qryParam.entrySet()) {
                    tq.setParameter(e.getKey(), e.getValue());
                }
                tq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
                tq.setMaxResults(pageable.getPageSize());
                p.setData(tq.getResultList());

                //填充老师
                Set<Long> teachingClassIds = new HashSet<>();
                for (TeachingClassCourseTeacherDomain d : p.getData()) {
                    teachingClassIds.add(d.getTeachingClassId());
                }
//                Map<Long, IdIdNameDomain> cache1 = new HashMap<>();
//                List<IdIdNameDomain> teachers = teachingClassTeacherService.findTeacherByTeachingClassIds(teachingClassIds);
//                for (IdIdNameDomain d : teachers) {
//                    cache.put(d.getLogicId(), d);
//                }
//                for (TeachingClassCourseTeacherDomain d : p.getData()) {
//                    IdIdNameDomain td = cache.get(d.getTeachingClassId());
//                    if (null != td) {
//                        d.setTeacherName(td.getName());
//                        d.setTeacherId(td.getId());
//                    }
//                }

                List<TeachingClassTeacherInfoDomain> teachers = teachingClassTeacherService.findTeacherInfoByTeachingClassIds(teachingClassIds);
                Map<Long, TeachingClassTeacherInfoDomain> cache = new HashMap<>();
                for (TeachingClassTeacherInfoDomain d : teachers) {
                    cache.put(d.getLogicId(), d);
                }
                for (TeachingClassCourseTeacherDomain d : p.getData()) {
                    TeachingClassTeacherInfoDomain td = cache.get(d.getTeachingClassId());
                    if (null != td) {
                        d.setTeacherName(td.getName());
                        d.setTeacherId(td.getId());
                        d.setCollegeName(td.getCollegeName());
                    }
                }
            }
        } else {
            qryParam.put("teacherName", "%" + teachingClassQueryDomain.getTeacherName() + "%");
            StringBuilder hql = new StringBuilder("SELECT  new com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingClassCourseTeacherDomain(t.teachingClass.id, t.teachingClass.name, t.teachingClass.code, t.teacher.college.name, t.teachingClass.course.name, t.teacher.id, t.teacher.name) FROM com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClassTeacher t WHERE t.orgId=:orgId and t.teacher.name like :teacherName");
            StringBuilder chql = new StringBuilder("SELECT COUNT(t.id) FROM com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClassTeacher t WHERE t.teachingClass.orgId=:orgId and t.teacher.name like :teacherName");
            if (null != semester) {
                hql.append(" AND t.teachingClass.semester = :semester");
                chql.append(" AND t.teachingClass.semester = :semester");
                qryParam.put("semester", semester);
            }
            if (!StringUtils.isEmpty(teachingClassQueryDomain.getCourseName())) {
                hql.append(" AND t.teachingClass.course.name like :courseName");
                chql.append(" AND t.teachingClass.course.name like :courseName");
                qryParam.put("courseName", "%" + teachingClassQueryDomain.getCourseName() + "%");
            }

            if (null != teachingClassQueryDomain.getTeachingClassIds() && teachingClassQueryDomain.getTeachingClassIds().size() > 0) {
                hql.append(" AND t.teachingClass.id not in (:tids)");
                chql.append(" AND t.teachingClass.id not in (:tids)");
                qryParam.put("tids", teachingClassQueryDomain.getTeachingClassIds());
            }
            Query q = em.createQuery(chql.toString());
            for (Map.Entry<String, Object> e : qryParam.entrySet()) {
                q.setParameter(e.getKey(), e.getValue());
            }
            Long count = (Long) q.getSingleResult();
            p.getPage().setTotalElements(count);
            p.getPage().setPageNumber(pageable.getPageNumber());
            p.getPage().setPageSize(pageable.getPageSize());
            p.getPage().setTotalPages(PageUtil.cacalatePagesize(count, pageable.getPageSize()));
            if (count > 0) {
                TypedQuery<TeachingClassCourseTeacherDomain> tq = em.createQuery(hql.toString(), TeachingClassCourseTeacherDomain.class);
                for (Map.Entry<String, Object> e : qryParam.entrySet()) {
                    tq.setParameter(e.getKey(), e.getValue());
                }
                tq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
                tq.setMaxResults(pageable.getPageSize());

                p.setData(tq.getResultList());
            }
        }
        return p;
    }


    @Transactional(readOnly = true)
    public Page<CourseDomain> findOnlyCourseIdNameByIdsSelfHql(Pageable pageable, Set<Long> ids) {
        StringBuilder hql = new StringBuilder("select distinct new com.aizhixin.cloud.orgmanager.company.domain.CourseDomain(t.course.id, t.course.code, t.course.name, t.course.orgId) from com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClass t where t.id in (:ids) ORDER BY t.id DESC");
        StringBuilder chql = new StringBuilder("select count(distinct t.course.id) from com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClass t, com.aizhixin.cloud.orgmanager.company.entity.Course c where t.course.id = c.id and t.id in (:ids) ORDER BY t.id DESC");

        Query q = em.createQuery(chql.toString());
        q.setParameter("ids", ids);
        Long count = (Long) q.getSingleResult();
        if (count > 0) {
            TypedQuery<CourseDomain> tq = em.createQuery(hql.toString(), CourseDomain.class);
            tq.setParameter("ids", ids);
            tq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
            tq.setMaxResults(pageable.getPageSize());

            return new PageImpl(tq.getResultList(), pageable, count);
        } else {
            return new PageImpl(new ArrayList<>(), pageable, count);
        }
    }

    @Transactional(readOnly = true)
    public List<String> findCodeByCodeIn(Long orgId, Set<String> codes) {
        return teachingClassRepository.findCodeByOrgIdAndCodeIn(orgId, codes);
    }


    @Transactional(readOnly = true)
    public List<TeachingClassDomain> findTeachingClasssAndCourseByIds(Set<Long> ids) {
        return teachingClassRepository.findTeachingClasssAndCourseByIds(ids);
    }

    private void batchDelete(List<TeachingClass> delList) {
        if (delList.size() > 0) {
            schoolTimeTableService.deleteByTeachingClassList(delList);
            teachingClassStudentsService.deleteByTeachingClassList(delList);
            teachingClassClassesService.deleteByTeachingClassList(delList);
            teachingClassTeacherService.deleteByTeachingClassList(delList);
            tempCourseScheduleService.deleteByTeacherClass(delList);
//            RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
            List<TeachingClassMsgDTO> msgList = new ArrayList<>();
            Set<Long> ids = new HashSet<>();
            Long orgId = delList.get(0).getOrgId();
            for (TeachingClass t : delList) {
                delete(t);
                msgList.add(initTeachingClassMsgDTO(t, t.getCourse(), t.getSemester()));
                ids.add(t.getId());
            }
            //发送消息
            dataSynService.sendTeachingDeleteMsg(msgList);
            ddClient.initStuRollCallStatsByTeachingClass(orgId, ids);
        }
    }

    public long countBySemester(Semester semester) {
        return teachingClassRepository.countBySemester(semester);
    }

//    /**
//     * @param type 10 add, 20 update, 30 delete
//     * @param str
//     */
//    public void sendEventAdice(int type, String str) {
//        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
//        switch (type) {
//            case 10:
//                redisTokenStore.setPrefix("org_api:add");
//                break;
//            case 20:
//                redisTokenStore.setPrefix("org_api:update");
//                break;
//            case 30:
//                redisTokenStore.setPrefix("org_api:delete");
//                break;
//            default:
//                redisTokenStore.setPrefix("org_api:deletestudent");
//        }
//
//        redisTokenStore.storeTeachingclassId(str);
//
//        switch (type) {
//            case 10:
//                redisTokenStore.pushAddTeachingclassEvent();
//                break;
//            case 20:
//                redisTokenStore.pushUpdateTeachingclassEvent();
//                break;
//            case 30:
//                redisTokenStore.pushDeleteTeachingclassEvent();
//                break;
//            default:
//                redisTokenStore.pushDeleteTeachingclassStudentEvent();
//        }
//    }
    //*************************************************************以下部分处理页面调用逻辑**********************************************************************//

    public TeachingClass save(TeachingClassDomain tc) {
        if (null == tc.getSemesterId() || tc.getSemesterId() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "学期ID是必须的");
        }
        if (null == tc.getCourseId() || tc.getCourseId() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "课程ID是必须的");
        }
        if (StringUtils.isEmpty(tc.getName())) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教学班名称是必须的");
        }
        if (null == tc.getClassOrStudents() || (tc.getClassOrStudents() != ClassesOrStudents.CLASSES.getState() && tc.getClassOrStudents() != ClassesOrStudents.STUDENTS.getState())) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "按照行政班(10)或者学生(20)添加是必须的，并且不能是其它值");
        }
        if (null == tc.getTeacherIds() || tc.getTeacherIds().size() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教师ID列表是必须的");
        }
        Semester semester = semesterService.findById(tc.getSemesterId());
        if (null == semester) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据学期Id[" + tc.getSemesterId() + "]查找不到对应的学期信息");
        }
        Course course = courseService.findById(tc.getCourseId());
        if (null == course) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据课程Id[" + tc.getSemesterId() + "]查找不到对应的课程信息");
        }
        TeachingClass t = new TeachingClass();
        t.setName(tc.getName());
        t.setCode(tc.getCode());
        t.setSemester(semester);
        t.setOrgId(semester.getOrgId());
        t.setSource(tc.getSource());
        t.setLastModifiedBy(tc.getUserId());
        t.setCreatedBy(tc.getUserId());
        t.setCourse(course);
        t.setClassOrStudents(tc.getClassOrStudents());
        if (!StringUtils.isEmpty(tc.getCode())) {
            long c = countByCode(t.getOrgId(), tc.getCode());
            if (c > 0) {
                throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "教学班编码[" + tc.getCode() + "]已经存在");
            }
            t.setCode(tc.getCode());
        }
        t = save(t);
        //消息推送
        List<TeachingClassMsgDTO> msgList = new ArrayList<>();
        msgList.add(initTeachingClassMsgDTO(t, course, semester));
        if (!msgList.isEmpty()) {
            dataSynService.sendTeachingAddMsg(msgList);
        }
        Set<Long> teacherIds = new HashSet<>();
        teacherIds.addAll(tc.getTeacherIds());
        teachingClassTeacherService.save(t, teacherIds);

        if (tc.getClassOrStudents() == ClassesOrStudents.CLASSES.getState()) {//按照行政班添加学生
            teachingClassClassesService.save(t, tc.getClassesIds());
        } else {//直接添加学生
            if (null != tc.getStudentIds() && tc.getStudentIds().size() > 0) {
                teachingClassStudentsService.save(t, tc.getStudentIds());
            }
        }
        //sendEventAdice(10, t.getId().toString());

        return t;
    }

    private TeachingClassMsgDTO initTeachingClassMsgDTO(TeachingClass t, Course course, Semester semester) {
        TeachingClassMsgDTO dto = new TeachingClassMsgDTO ();
        dto.setOrgId(t.getOrgId());
        dto.setId(t.getId());
        dto.setCode(t.getCode());
        dto.setName(t.getName());
        dto.setClassOrStudents(t.getClassOrStudents());
        dto.setCourseId(course.getId());
        dto.setCourseName(course.getName());
        dto.setCourseCode(course.getCode());

        dto.setSemesterId(semester.getId());
        dto.setSemesterName(semester.getName());
        return dto;
    }


    public TeachingClass saveByKj(TeachingClassDomain tc) {
        if (null == tc.getSemesterId() || tc.getSemesterId() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "学期ID是必须的");
        }
        if (null == tc.getCourseId() || tc.getCourseId() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "课程ID是必须的");
        }
        if (StringUtils.isEmpty(tc.getName())) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教学班名称是必须的");
        }
        if (null == tc.getClassOrStudents() || (tc.getClassOrStudents() != ClassesOrStudents.CLASSES.getState() && tc.getClassOrStudents() != ClassesOrStudents.STUDENTS.getState())) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "按照行政班(10)或者学生(20)添加是必须的，并且不能是其它值");
        }
        if (null == tc.getTeacherIds() || tc.getTeacherIds().size() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教师ID列表是必须的");
        }
        Semester semester = semesterService.findById(tc.getSemesterId());
        if (null == semester) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据学期Id[" + tc.getSemesterId() + "]查找不到对应的学期信息");
        }
        Course course = courseService.findById(tc.getCourseId());
        if (null == course) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据课程Id[" + tc.getSemesterId() + "]查找不到对应的课程信息");
        }
        TeachingClass t = new TeachingClass();
        t.setName(tc.getName());
        t.setCode(tc.getCode());
        t.setSemester(semester);
        t.setOrgId(semester.getOrgId());
        t.setCourse(course);
        t.setCreatedBy(tc.getUserId());
        t.setLastModifiedBy(tc.getUserId());
        //添加教学班来源
        t.setSource(tc.getSource());
        t.setClassOrStudents(tc.getClassOrStudents());
        if (!StringUtils.isEmpty(tc.getCode())) {
            long c = countByCode(t.getOrgId(), tc.getCode());
            if (c > 0) {
                throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "教学班编码[" + tc.getCode() + "]已经存在");
            }
            t.setCode(tc.getCode());
        }
        t = save(t);
        Set<Long> teacherids = new HashSet<>();
        teacherids.addAll(tc.getTeacherIds());
        teachingClassTeacherService.save(t, teacherids);

        if (tc.getClassOrStudents() == ClassesOrStudents.CLASSES.getState()) {//按照行政班添加学生
            teachingClassClassesService.save(t, tc.getClassesIds());
        } else {//直接添加学生
            if (null != tc.getStudentIds() && tc.getStudentIds().size() > 0) {
                teachingClassStudentsService.save(t, tc.getStudentIds());
            }
        }
        return t;
    }

    public TeachingClass update(TeachingClassUpdateDomain tc) {
        if (null == tc.getId() || tc.getId() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "ID是必须的");
        }
        if (null == tc.getSemesterId() || tc.getSemesterId() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "学期ID是必须的");
        }
        if (null == tc.getCourseId() || tc.getCourseId() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "课程ID是必须的");
        }
        if (StringUtils.isEmpty(tc.getName())) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教学班名称是必须的");
        }
        TeachingClass t = findById(tc.getId());
        if (null == t) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据教学班ID[" + tc.getId() + "]查找不到对应的数据");
        }
        Semester semester = semesterService.findById(tc.getSemesterId());
        if (null == semester) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据学期Id[" + tc.getSemesterId() + "]查找不到对应的学期信息");
        }
        Course course = courseService.findById(tc.getCourseId());
        if (null == course) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据课程Id[" + tc.getSemesterId() + "]查找不到对应的课程信息");
        }

        if (null != t.getSemester() && t.getSemester().getId().longValue() != tc.getSemesterId()) {    //学期发送改变，修改修改对应学生和班级的学期信息//老师也要做相应的学期的修改
            if (t.getClassOrStudents() == ClassesOrStudents.CLASSES.getState()) {
                List<TeachingClassClasses> tccs = teachingClassClassesService.findByTeachingClass(t);
                for (TeachingClassClasses c : tccs) {
                    c.setSemester(semester);
                }
                teachingClassClassesService.save(tccs);
            } else {
                List<TeachingClassStudents> ss = teachingClassStudentsService.findByTeachingClass(t);
                for (TeachingClassStudents s : ss) {
                    s.setSemester(semester);
                }
                teachingClassStudentsService.save(ss);
            }
            List<TeachingClass> teachingClassList = new ArrayList<>();
            teachingClassList.add(t);
            List<TeachingClassTeacher> teachingClassTeacherList = teachingClassTeacherService.findByTeachingClassesIn(teachingClassList);
            if (null != teachingClassTeacherList && teachingClassTeacherList.size() > 0) {
                for (TeachingClassTeacher teacher : teachingClassTeacherList) {
                    teacher.setSemester(semester);
                }
                teachingClassTeacherService.save(teachingClassTeacherList);
            }
        }
        t.setSemester(semester);
        t.setCode(tc.getCode());
        t.setCourse(course);
        t.setName(tc.getName());
        t.setLastModifiedBy(tc.getUserId());
        t.setLastModifiedDate(new Date());
        t = save(t);

//        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
//        redisTokenStore.setPrefix("org_api:update");
//        redisTokenStore.storeTeachingclassId(t.getId().toString());
//
//        redisTokenStore.pushUpdateTeachingclassEvent();
//        sendEventAdice(20, t.getId().toString());
        //消息推送
        List<TeachingClassMsgDTO> msgList = new ArrayList<>();
        msgList.add(initTeachingClassMsgDTO(t, course, semester));
        if (!msgList.isEmpty()) {
            dataSynService.sendTeachingUpdateMsg(msgList);
        }
        return t;
    }

    public void updateAllCourse(TeachingclassBatchUpdateCourseDomain tc) {
        if (null == tc) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "课程id、教学班id列表、orgid、userid是必须的");
        }
        if (null == tc.getCourseId() || tc.getCourseId() <= 0 || null == tc.getTeachingclassIds() || tc.getTeachingclassIds().size() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "课程id、教学班id列表、orgid、userid是必须的");
        }
        Course c = courseService.findById(tc.getCourseId());
        if (null == c) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据课程id[" + tc.getCourseId() + "]没有找到对应的课程信息");
        }
        List<TeachingClass> list = findByIdIn(tc.getTeachingclassIds());
        if (null == list || list.size() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "没有找到相应的教学班信息");
        }
        if (list.size() != tc.getTeachingclassIds().size()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据教学班id列表查找的教学班数量和id列表数量不一致");
        }
        for (TeachingClass t : list) {
            t.setCourse(c);
        }
        save(list);
    }

    public void deleteBatch(TeachingclassIdsAndOrgAndUserDomain tc) {
        if (null == tc) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教学班id列表、orgid、userid是必须的");
        }
        if (null == tc.getTeachingclassIds() || tc.getTeachingclassIds().size() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "课程id、教学班id列表、orgid、userid是必须的");
        }
        List<TeachingClass> list = findByIdIn(tc.getTeachingclassIds());
        if (null == list || list.size() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "没有找到相应的教学班信息");
        }
        if (list.size() != tc.getTeachingclassIds().size()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据教学班id列表查找的教学班数量和id列表数量不一致");
        }
        batchDelete(list);
    }

    @Transactional(readOnly = true)
    public TeachingClassDomain get(Long id) {
        TeachingClassDomain td = new TeachingClassDomain();
        TeachingClass t = findById(id);
        if (null != t) {
            td.setId(t.getId());
            td.setName(t.getName());
//            if (ClassesOrStudents.CLASSES.getState() == t.getClassOrStudents()) {
//                List<Classes> classes = teachingClassClassesService.findClassesByTeachingClass(t);
//                if (null != classes && classes.size() > 0) {
//                    td.setStudentsCount(userService.countByClasses(classes));
//                }
//            } else {
//                td.setStudentsCount(t.getStudentsCount());
//            }
            td.setStudentsCount(teachingClassStudentsService.countByTeachingClass(t));
            td.setTeacherNames(t.getTeacherNames());
            td.setClassOrStudents(t.getClassOrStudents());
            td.setCode(t.getCode());
            if (null != t.getSemester()) {
                td.setSemesterId(t.getSemester().getId());
                td.setSemesterName(t.getSemester().getName());
                td.setSemesterStart(t.getSemester().getStartDate());
                td.setSemesterEnd(t.getSemester().getEndDate());
            }
            if (null != t.getCourse()) {
                td.setCourseId(t.getCourse().getId());
                td.setCourseName(t.getCourse().getName());
            }
        }
        return td;
    }

    @Transactional(readOnly = true)
    public PageData<TeachingClassDomain> queryList(Pageable pageable, Long orgId, Integer mustOrOption, Long semesterId, String name, String courseName, String teacherName) {
        PageData<TeachingClassDomain> pageData = new PageData<>();
        pageData.getPage().setPageSize(pageable.getPageSize());
        pageData.getPage().setPageNumber(pageable.getPageNumber() + 1);
        if (null == orgId || orgId <= 0) {
            return pageData;
        }
        Semester semester = null;
        if (null != semesterId && semesterId > 0) {
            semester = semesterService.findById(semesterId);
        }
        Map<String, Object> condition = new HashMap<>();
        condition.put("orgId", orgId);
        StringBuilder chql;
        StringBuilder hql;
        if (!StringUtils.isEmpty(teacherName)) {
            chql = new StringBuilder("select count(distinct t.teachingClass.id) from com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClassTeacher t where t.teachingClass.orgId=:orgId  ");
            hql = new StringBuilder("select distinct new com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingClassDomain(t.teachingClass.id, t.teachingClass.name, t.teachingClass.code, t.teachingClass.semester.id, t.teachingClass.semester.name, t.teachingClass.course.id, t.teachingClass.course.name, t.teachingClass.classOrStudents, t.teachingClass.teacherNames, t.teachingClass.classesNames, t.teachingClass.studentsCount) from com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClassTeacher t where t.teachingClass.orgId=:orgId ");

            hql.append(" and t.teacher.name like :teacherName ");
            chql.append(" and t.teacher.name like :teacherName");
            condition.put("teacherName", "%" + teacherName + "%");

            if (!StringUtils.isEmpty(name)) {
                hql.append(" and (t.teachingClass.name like :name or t.teachingClass.code like :name)");
                chql.append(" and (t.teachingClass.name like :name or t.teachingClass.code like :name)");
                condition.put("name", "%" + name + "%");
            }
            if (!StringUtils.isEmpty(courseName)) {
                hql.append(" and t.teachingClass.course.name like :courseName");
                chql.append(" and t.teachingClass.course.name like :courseName");
                condition.put("courseName", "%" + courseName + "%");
            }
            if (null != semester) {
                hql.append(" and t.teachingClass.semester = :semester");
                chql.append(" and t.teachingClass.semester = :semester");
                condition.put("semester", semester);
            }
            if (null != mustOrOption && (ClassesOrStudents.CLASSES.getState().intValue() == mustOrOption || ClassesOrStudents.STUDENTS.getState().intValue() == mustOrOption)) {
                hql.append(" and t.teachingClass.classOrStudents = :classOrStudents");
                chql.append(" and t.teachingClass.classOrStudents = :classOrStudents");
                condition.put("classOrStudents", mustOrOption);
            }

            hql.append(" order by t.teachingClass.id DESC");
        } else {
            chql = new StringBuilder("select count(t.id) from com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClass t where t.orgId=:orgId  ");
            hql = new StringBuilder("select new com.aizhixin.cloud.orgmanager.classschedule.domain.TeachingClassDomain(t.id, t.name, t.code, t.semester.id, t.semester.name, t.course.id, t.course.name, t.classOrStudents, t.teacherNames, t.classesNames, t.studentsCount) from com.aizhixin.cloud.orgmanager.classschedule.entity.TeachingClass t where t.orgId=:orgId ");

            if (!StringUtils.isEmpty(name)) {
                hql.append(" and (t.name like :name or t.code like :name)");
                chql.append(" and (t.name like :name or t.code like :name)");
                condition.put("name", "%" + name + "%");
            }
            if (!StringUtils.isEmpty(courseName)) {
                hql.append(" and t.course.name like :courseName");
                chql.append(" and t.course.name like :courseName");
                condition.put("courseName", "%" + courseName + "%");
            }
            if (null != semester) {
                hql.append(" and t.semester = :semester");
                chql.append(" and t.semester = :semester");
                condition.put("semester", semester);
            }

            if (null != mustOrOption && (ClassesOrStudents.CLASSES.getState().intValue() == mustOrOption || ClassesOrStudents.STUDENTS.getState().intValue() == mustOrOption)) {
                hql.append(" and t.classOrStudents = :classOrStudents");
                chql.append(" and t.classOrStudents = :classOrStudents");
                condition.put("classOrStudents", mustOrOption);
            }
            hql.append(" order by t.id DESC");
        }

        Query q = em.createQuery(chql.toString());
        for (Map.Entry<String, Object> e : condition.entrySet()) {
            q.setParameter(e.getKey(), e.getValue());
        }
        Long count = (Long) q.getSingleResult();
        pageData.getPage().setTotalElements(count);
        if (count <= 0) {
            pageData.getPage().setTotalPages(1);
            return pageData;
        }
        TypedQuery<TeachingClassDomain> tq = em.createQuery(hql.toString(), TeachingClassDomain.class);
        for (Map.Entry<String, Object> e : condition.entrySet()) {
            tq.setParameter(e.getKey(), e.getValue());
        }
        tq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        tq.setMaxResults(pageable.getPageSize());
        pageData.setData(tq.getResultList());
        pageData.getPage().setTotalPages(PageUtil.cacalatePagesize(count, pageData.getPage().getPageSize()));

        Set<Long> teachingClassIdSet = new HashSet<>();
        Map<Long, TeachingClassDomain> teachingClassDomainMap = new HashMap<>();
        for (TeachingClassDomain d : pageData.getData()) {
//            if (ClassesOrStudents.CLASSES.getState() == d.getClassOrStudents()) {
//                d.setStudentsCount(0L);
//                List <Classes> classes = teachingClassClassesService.findClassesByTeachingClassId(d.getId());
//                if (null != classes && classes.size() > 0) {
//                    d.setStudentsCount(userService.countByClasses(classes));
//                }
//            } else if (ClassesOrStudents.STUDENTS.getState() == d.getClassOrStudents()) {
            d.setStudentsCount(teachingClassStudentsService.countByTeachingClassId(d.getId()));
//            }

            teachingClassIdSet.add(d.getId());
            teachingClassDomainMap.put(d.getId(), d);
        }
        if (teachingClassIdSet.size() > 0) {
            List<IdIdNameDomain> teachingclassTeacherList = teachingClassTeacherService.findTeacherByTeachingClassIds(teachingClassIdSet);
            for (IdIdNameDomain d : teachingclassTeacherList) {
                TeachingClassDomain teachingClassDomain = teachingClassDomainMap.get(d.getLogicId());
                if (null != teachingClassDomain) {
                    if (null == teachingClassDomain.getTeacherIds()) {
                        teachingClassDomain.setTeacherIds(new ArrayList<>());
                    }
                    teachingClassDomain.getTeacherIds().add(d.getId());

                    if (StringUtils.isEmpty(teachingClassDomain.getTeacherNames())) {
                        teachingClassDomain.setTeacherNames(d.getName());
                    } else {
                        teachingClassDomain.setTeacherNames(teachingClassDomain.getTeacherNames() + "," + d.getName());
                    }
                }
            }
        }

        return pageData;
    }

//    @Transactional(readOnly = true)
//    public Map <String, Object> queryList(Map <String, Object> r, Pageable pageable, Long orgId, String name) {
//        Page <TeachingClassDomain> page = null;
//        PageDomain p = new PageDomain();
//        p.setPageNumber(pageable.getPageNumber());
//        p.setPageSize(pageable.getPageSize());
//        r.put(ApiReturnConstants.PAGE, p);
//
//        if (!StringUtils.isEmpty(name)) {
//            page = findOrgIdAndName(pageable, orgId, name);
//        } else {
//            page = findOrgId(pageable, orgId);
//        }
//        p.setTotalElements(page.getTotalElements());
//        p.setTotalPages(page.getTotalPages());
//
//        Set<Long> teachingClassIdSet = new HashSet<>();
//        Map<Long, TeachingClassDomain> teachingClassDomainMap = new HashMap<>();
//        for (TeachingClassDomain d : page.getContent()) {
//            if (ClassesOrStudents.CLASSES.getState() == d.getClassOrStudents()) {
//                d.setStudentsCount(0L);
//                List <Classes> classes = teachingClassClassesService.findClassesByTeachingClassId(d.getId());
//                if (null != classes && classes.size() > 0) {
//                    d.setStudentsCount(userService.countByClasses(classes));
//                }
//            } else if (ClassesOrStudents.STUDENTS.getState() == d.getClassOrStudents()) {
//                d.setStudentsCount(teachingClassStudentsService.countByTeachingClassId(d.getId()));
//            }
//
//            teachingClassIdSet.add(d.getId());
//            teachingClassDomainMap.put(d.getId(), d);
//        }
//        if (teachingClassIdSet.size() > 0) {
//            List<IdIdNameDomain> teachingclassTeacherList = teachingClassTeacherService.findTeacherByTeachingClassIds(teachingClassIdSet);
//            for (IdIdNameDomain d : teachingclassTeacherList) {
//                TeachingClassDomain teachingClassDomain = teachingClassDomainMap.get(d.getLogicId());
//                if (null != teachingClassDomain) {
//                    if (null == teachingClassDomain.getTeacherIds()) {
//                        teachingClassDomain.setTeacherIds(new ArrayList<>());
//                    }
//                    teachingClassDomain.getTeacherIds().add(d.getId());
//                }
//            }
//        }
//        r.put(ApiReturnConstants.DATA, page.getContent());
//        return r;
//    }

    public void delete(Long id) {
        if (null == id || id <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "ID是必须的");
        }
        TeachingClass t = findById(id);
        if (null == t) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据教学班ID[" + id + "]查找不到对应的数据");
        }
        List<TeachingClass> list = new ArrayList<>();
        list.add(t);
        batchDelete(list);

        //消息推送
        List<TeachingClassMsgDTO> msgList = new ArrayList<>();
        msgList.add(initTeachingClassMsgDTO(t, t.getCourse(), t.getSemester()));
        if (!msgList.isEmpty()) {
            dataSynService.sendTeachingDeleteMsg(msgList);
        }

//        long c = teachingClassTeacherService.countByTeachingClass(t);
//        if (c > 0) {
//            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教学班ID[" + id + "]存在教师信息不能被删除");
//        }
//        c = schoolTimeTableService.countByTeachingClass(t);
//        if (c > 0) {
//            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教学班ID[" + id + "]已经用于课表，不能被删除");
//        }
//        if (null != t.getClassOrStudents()) {
//            if (ClassesOrStudents.CLASSES.getState() == t.getClassOrStudents()) {
//                c = teachingClassClassesService.countByTeachingClass(t);
//                if (c > 0) {
//                    throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教学班ID[" + id + "]存在行政班信息不能被删除");
//                }
//            } else if (ClassesOrStudents.STUDENTS.getState() == t.getClassOrStudents()) {
//                c = teachingClassStudentsService.countByTeachingClass(t);
//                if (c > 0) {
//                    throw new CommonException(ErrorCode.ID_IS_REQUIRED, "教学班ID[" + id + "]存在学生信息不能被删除");
//                }
//            }
//        }
//        delete(t);
    }

    @Transactional(readOnly = true)
    public Map<Long, String> getCourseNameByIds(Set<Long> ids) {
        List<IdNameDomain> list = findCourseNameByIds(ids);
        Map<Long, String> r = new HashMap<>();
        for (IdNameDomain d : list) {
            r.put(d.getId(), d.getName());
        }
        return r;
    }

    @Transactional(readOnly = true)
    public Map<Long, IdNameDomain> getCourseIdNameByIds(Set<Long> ids) {
        List<IdIdNameDomain> list = findCourseIdNameByIds(ids);
        Map<Long, IdNameDomain> r = new HashMap<>();
        for (IdIdNameDomain d : list) {
            r.put(d.getLogicId(), new IdNameDomain(d.getId(), d.getName()));
        }
        return r;
    }

    @Transactional(readOnly = true)
    public List<IdNameDomain> findClassesById(Long id) {
        if (null != id && id > 0) {
            TeachingClass teachingClass = findById(id);
            if (null != teachingClass) {
                if (ClassesOrStudents.CLASSES.getState().intValue() == teachingClass.getClassOrStudents()) {
                    return teachingClassClassesService.findSimpleClassesByTeachingClass(teachingClass);
                } else {
                    return teachingClassStudentsService.findSimpleClassesByTeachingClass(teachingClass);
                }
            }
        }
        return new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public List<ClassesCollegeDomain> findClassesByIds(Set ids) {
        List<ClassesCollegeDomain> resultList = new ArrayList<>();
        if (null != ids && ids.size() > 0) {
            List<TeachingClass> classes = findByIdIn(ids);

            Set classClassesSet = new HashSet();
            Set studentClassesSet = new HashSet();
            if (null != classes && classes.size() > 0) {
                for (TeachingClass teachingClass : classes) {
                    if (ClassesOrStudents.CLASSES.getState().intValue() == teachingClass.getClassOrStudents()) {
                        classClassesSet.add(teachingClass.getId());
                    } else {
                        studentClassesSet.add(teachingClass.getId());
                    }
                }
                if (classClassesSet.size() > 0) {
                    resultList.addAll(teachingClassClassesService.findSimpleClassesByTeachingClass(classClassesSet));
                }
                if (studentClassesSet.size() > 0) {
                    resultList.addAll(teachingClassStudentsService.findSimpleClassesByTeachingClassIds(studentClassesSet));
                }
            }
        }
        return resultList;
    }

    @Transactional(readOnly = true)
    public Long countStudents(Long id) {
        Long count = 0L;
        if (null != id && id > 0) {
            TeachingClass teachingClass = findById(id);
            if (null != teachingClass) {
//                if (ClassesOrStudents.CLASSES.getState().intValue() == teachingClass.getClassOrStudents()) {
//                    List<Classes> classes = teachingClassClassesService.findClassesByTeachingClass(teachingClass);
//                    if (null != classes && classes.size() > 0) {
//                        count = userService.countByClasses(classes);
//                    }
//                } else {
                    count = teachingClassStudentsService.countByTeachingClass(teachingClass);
//                }
            }
        }
        return count;
    }

    @Transactional(readOnly = true)
    public List<TeachStudentDomain> getStudents(Long id) {
        List<TeachStudentDomain> r = new ArrayList<>();
        if (null != id && id > 0) {
            TeachingClass teachingClass = findById(id);
            if (null != teachingClass) {
//                if (ClassesOrStudents.CLASSES.getState().intValue() == teachingClass.getClassOrStudents()) {
//                    List<Classes> classes = teachingClassClassesService.findClassesByTeachingClass(teachingClass);
//                    if (null != classes && classes.size() > 0) {
//                        r = userService.findByClasseses(classes);
//                    }
//                } else {
                    r = teachingClassStudentsService.findByTeachStudents(teachingClass);
//                }
            }
        }
        return r;
    }


    @Transactional(readOnly = true)
    public List<TeachStudentDomain> getStudents(Set<Long> ids) {
        List<TeachStudentDomain> r = null;
        if (null != ids && ids.size() > 0) {
            List<TeachingClass> teachingClasses = findByIdIn(ids);
//            List<TeachingClass> stcs = new ArrayList<>();
//            List<Classes> ces = new ArrayList<>();
//            Set<Long> cs = new HashSet<>();
//            Map<Long, Long> cts = new HashMap<>();
            if (null != teachingClasses && teachingClasses.size() > 0) {
//                for (TeachingClass t : teachingClasses) {
//                    if (ClassesOrStudents.CLASSES.getState().intValue() == t.getClassOrStudents()) {
//                        List<Classes> classes = teachingClassClassesService.findClassesByTeachingClass(t);
//                        for (Classes c : classes) {
//                            if (!cs.contains(c.getId())) {
//                                cs.add(c.getId());
//                                ces.add(c);
//                                cts.put(c.getId(), t.getId());
//                            }
//                        }
//                    } else {
//                        stcs.add(t);
//                    }
//                }
//                if (null != ces && ces.size() > 0) {
//                    r = userService.findByClasseses(ces);
//                    //将班级ID替换为教学班ID
//                    for (TeachStudentDomain d : r) {
//                        d.setTeachingClassId(cts.get(d.getClassesId()));
//                    }
//                }
//                List<TeachStudentDomain> r2 = null;
//                if (stcs.size() > 0) {
//                    r2 = teachingClassStudentsService.findByTeachStudents(stcs);
//                }
//                if (null != r) {
//                    if (null != r2) {
//                        r.addAll(r2);
//                    }
//                } else {
//                    r = r2;
//                }
                r = teachingClassStudentsService.findByTeachStudents(teachingClasses);
            }
        }
        return r;
    }

    @Transactional(readOnly = true)
    public List<IdNameCountDomain> findIdNameByTeacherCouseIdAndSemester(Long teacherId, Long courseId, Long semesterId) {
        List<IdNameCountDomain> r = new ArrayList<>();
        User teacher = userService.findById(teacherId);
        if (null == teacher || UserType.B_TEACHER.getState().intValue() != teacher.getUserType()) {
            return r;
        }
        Semester semester = semesterService.getSemesterByOrgIdAndIdAndDate(teacher.getOrgId(), semesterId, null);
        if (null == semester) {
            return r;
        }
        Course course = courseService.findById(courseId);
        if (null == course) {
            return r;
        }
//        Set <Long> teachingClassIds = teachingClassTeacherService.findTeachingClassIdsByTeacher(semester, teacher);
//        if (teachingClassIds.size() > 0) {
//            r = findTeachingClassIdNameByIdsAndCourseAndSemester(teachingClassIds, course, semester);
//        }
        Set<Long> teachingClassIds = new HashSet<>();
        List<TeachingClass> studentTeachignclass = new ArrayList<>();
//        List<TeachingClass> classesTeachignclass = new ArrayList<>();
        Set<TeachingClass> teachingclass = teachingClassTeacherService.findTeachingClassByTeacher(semester, teacher);
        for (TeachingClass t : teachingclass) {
            teachingClassIds.add(t.getId());
//            if (ClassesOrStudents.STUDENTS.getState().intValue() == t.getClassOrStudents().intValue()) {
                studentTeachignclass.add(t);
//            } else {
//                classesTeachignclass.add(t);
//            }
        }

        if (teachingClassIds.size() > 0) {
            List<IdNameDomain> rlist = findTeachingClassIdNameByIdsAndCourseAndSemester(teachingClassIds, course, semester);
            Map<Long, CountDomain> cmap = new HashMap<>();
            if (studentTeachignclass.size() > 0) {
                List<CountDomain> countDomainList = teachingClassStudentsService.countByTeachingClass(studentTeachignclass);
                for (CountDomain c : countDomainList) {
                    cmap.put(c.getId(), c);
                }
            }
//            if (classesTeachignclass.size() > 0) {
//                for (TeachingClass t : classesTeachignclass) {
//                    List<Classes> classesList = teachingClassClassesService.findClassesByTeachingClass(t);
//                    if (classesList.size() > 0) {
//                        long c = userService.countStudentByClassesIn(classesList);
//                        cmap.put(t.getId(), new CountDomain(t.getId(), c));
//                    }
//                }
//            }
            for (IdNameDomain d : rlist) {
                IdNameCountDomain rd = new IdNameCountDomain(d.getId(), d.getName());
                r.add(rd);
                CountDomain c = cmap.get(rd.getId());
                if (null != c) {
                    rd.setCount(c.getCount());
                }
            }
        }
        return r;
    }

    @Transactional(readOnly = true)
    public List<IdNameDomain> findIdNameByStudentCouseIdAndSemester(Long studentId, Long courseId, Long semesterId) {
        List<IdNameDomain> r = new ArrayList<>();
        User student = userService.findById(studentId);
        if (null == student || UserType.B_STUDENT.getState().intValue() != student.getUserType()) {
            return r;
        }
        Semester semester = semesterService.getSemesterByOrgIdAndIdAndDate(student.getOrgId(), semesterId, null);

        Course course = courseService.findById(courseId);
        if (null == course) {
            return r;
        }
        Set<Long> teachingClassIds = teachingClassStudentsService.getStudentTeachingClassIds(semester, student);
        if (teachingClassIds.size() > 0) {
            r = findTeachingClassIdNameByIdsAndCourseAndSemester(teachingClassIds, course, semester);
        }
        return r;
    }

//    private void fillOutData(List <TeachingClassDomain> outData, List <TeachingClass> list) {
//        if (list.size() > 0) {
//            for (TeachingClass teachingClass : list) {
//                TeachingClassDomain d = new TeachingClassDomain();
//                d.setId(teachingClass.getId());
//                d.setName(teachingClass.getName());
//                d.setCode(teachingClass.getCode());
//                outData.add(d);
//                if (null != teachingClass.getSemester()) {
//                    d.setSemesterId(teachingClass.getSemester().getId());
//                    d.setSemesterCode(teachingClass.getSemester().getCode());
//                    d.setSemesterName(teachingClass.getSemester().getName());
//                }
//            }
//        }
//    }

//    public List <TeachingClassDomain> saveAll(List <TeachingClassDomain> ds) {
//        Set <String> scodes = new HashSet <>();
//        for (TeachingClassDomain d : ds) {
//            scodes.add(d.getSemesterCode());
//        }
//        Map <String, Semester> sms = new HashMap <>();
//        List <Semester> ss = semesterService.findByCodes(scodes);
//        for (Semester s : ss) {
//            sms.put(s.getCode(), s);
//        }
//        List <TeachingClass> list = new ArrayList <>();
//        for (TeachingClassDomain d : ds) {
//            TeachingClass teachingClass = new TeachingClass();
//            teachingClass.setName(d.getName());
//            teachingClass.setCode(d.getCode());
//            teachingClass.setClassOrStudents(ClassesOrStudents.STUDENTS.getState());
//            if (!StringUtils.isEmpty(d.getSemesterCode())) {
//                Semester s = sms.get(d.getSemesterCode());
//                if (null != s) {
//                    teachingClass.setSemester(s);
//                    teachingClass.setOrgId(s.getOrgId());
//                }
//            }
//            list.add(teachingClass);
//        }
//        List <TeachingClassDomain> outData = new ArrayList <>();
//        if (list.size() > 0) {
//            list = save(list);
//            fillOutData(outData, list);
//        }
//        return outData;
//    }
//
//
//    public List <TeachingClassDomain> updateAll(List <TeachingClassDomain> ds) {
//        Set <String> scodes = new HashSet <>();
//        Set <Long> ids = new HashSet <>();
//        Map <Long, TeachingClassDomain> wds = new HashMap <>();
//        for (TeachingClassDomain d : ds) {
//            scodes.add(d.getSemesterCode());
//            ids.add(d.getId());
//            wds.put(d.getId(), d);
//        }
//        Map <String, Semester> sms = new HashMap <>();
//        List <Semester> ss = semesterService.findByCodes(scodes);
//        for (Semester s : ss) {
//            sms.put(s.getCode(), s);
//        }
//        List <TeachingClass> list = findByIdIn(ids);
//        for (TeachingClass teachingClass : list) {
//            TeachingClassDomain d = wds.get(teachingClass.getId());
//            if (null != d) {
//                teachingClass.setName(d.getName());
//                teachingClass.setCode(d.getCode());
//                if (!StringUtils.isEmpty(d.getSemesterCode())) {
//                    Semester s = sms.get(d.getSemesterCode());
//                    if (null != s) {
//                        teachingClass.setSemester(s);
//                        teachingClass.setOrgId(s.getOrgId());
//                    }
//                }
//            }
//        }
//        List <TeachingClassDomain> outData = new ArrayList <>();
//        if (list.size() > 0) {
//            list = save(list);
//            fillOutData(outData, list);
//        }
//        return outData;
//    }
//
//    public List <TeachingClassDomain> deleteAll(List <TeachingClassDomain> ds) {
//        List <TeachingClassDomain> outData = new ArrayList <>();
//        Set <Long> ids = new HashSet <>();
//        Long userId = 0L;
//        for (TeachingClassDomain d : ds) {
//            ids.add(d.getId());
//            if (userId <= 0) {
//                userId = d.getUserId();
//            }
//        }
//        List <TeachingClass> list = findByIdIn(ids);
//        for (TeachingClass teachingClass : list) {
//            delete(teachingClass);
//        }
//        fillOutData(outData, list);
//        return outData;
//    }

    public PageData<TeachingClassCourseTeacherDomain> findCourseTeacher(TeachingClassQueryDomain teachingClassQueryDomain) {
        if (null == teachingClassQueryDomain.getOrgId() || teachingClassQueryDomain.getOrgId() <= 0) {
            return new PageData<>();
        }
        Semester semester = null;
        if (null != teachingClassQueryDomain.getSemesterId() && teachingClassQueryDomain.getSemesterId() > 0) {
            semester = semesterService.findById(teachingClassQueryDomain.getSemesterId());
        }
        return findCourseAndTeacherPage(teachingClassQueryDomain, semester, null);
    }

    private boolean validateTeachingClassCode(Set<String> teachingClassCodeSet, String teachingClassCode, StringBuilder sb) {
        boolean validator = true;
        if (StringUtils.isEmpty(teachingClassCode)) {
            sb.append(",教学班编码是必须的");
            validator = false;
        } else {
            if (teachingClassCodeSet.contains(teachingClassCode)) {
                sb.append(",教学班编码已经在此Excel中存在");
            } else {
                teachingClassCodeSet.add(teachingClassCode);
            }
        }
        return validator;
    }

    private boolean validateTeachingClassCode2(Set<String> teachingClassCodeSet, String teachingClassCode, StringBuilder sb) {
        boolean validator = true;
        if (StringUtils.isEmpty(teachingClassCode)) {
            sb.append(",教学班编码是必须的");
            validator = false;
        } else {
            if (!teachingClassCodeSet.contains(teachingClassCode)) {
                sb.append(",教学班编码在[教学任务]标签页中不存在");
                validator = false;
            }
        }
        return validator;
    }

    private boolean firstValidateTeachingClassExcelData(List<TeachingClassExcelDomain> teachingClassExcelDomainList, Set<String> teachingClassCodeSet, Set<String> courseCodeSet, Set<String> semesterCodeSet) {
        boolean validator = true;
        if (null != teachingClassExcelDomainList && teachingClassExcelDomainList.size() > 0) {
            //必填项验证及进一步验证条件的获取
            StringBuilder sb = new StringBuilder();
            for (TeachingClassExcelDomain teachingClassExcelDomain : teachingClassExcelDomainList) {
                boolean v1 = validateTeachingClassCode(teachingClassCodeSet, teachingClassExcelDomain.getTeachingClassCode(), sb);
                if (!v1) {
                    validator = v1;
                }
                if (StringUtils.isEmpty(teachingClassExcelDomain.getCourseCode())) {
                    sb.append(",课程编码是必须的");
                    validator = false;
                } else {
                    courseCodeSet.add(teachingClassExcelDomain.getCourseCode());
                }
                if (StringUtils.isEmpty(teachingClassExcelDomain.getSchoolYear())) {
                    sb.append(",学年是必须的");
                    validator = false;
                }
                if (StringUtils.isEmpty(teachingClassExcelDomain.getSemester())) {
                    sb.append(",学期是必须的");
                    validator = false;
                }
                if (!StringUtils.isEmpty(teachingClassExcelDomain.getSchoolYear()) && !StringUtils.isEmpty(teachingClassExcelDomain.getSemester())) {
                    semesterCodeSet.add(teachingClassExcelDomain.getSchoolYear() + "-" + teachingClassExcelDomain.getSemester());
                }
                if (sb.length() > 0) {
                    teachingClassExcelDomain.setMsg(sb.substring(1));
                    sb.delete(0, sb.length());
                }
            }
        }
        return validator;
    }

    public boolean firstValidateTeacherExcelData(List<TeacherExcelDomain> teacherExcelDomainList, Set<String> teachingClassCodeSet, Set<String> teacherCodeSet) {
        boolean validator = true;
        if (null != teacherExcelDomainList && teacherExcelDomainList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (TeacherExcelDomain teacherExcelDomain : teacherExcelDomainList) {
                boolean v1 = validateTeachingClassCode2(teachingClassCodeSet, teacherExcelDomain.getTeachingClassCode(), sb);
                if (!v1) {
                    validator = v1;
                }
                if (StringUtils.isEmpty(teacherExcelDomain.getTeacherCode())) {
                    sb.append(",教师工号是必须的");
                    validator = false;
                } else {
                    teacherCodeSet.add(teacherExcelDomain.getTeacherCode());
                }

                if (sb.length() > 0) {
                    teacherExcelDomain.setMsg(sb.substring(1));
                    sb.delete(0, sb.length());
                }
            }
        }
        return validator;
    }

    public boolean firstValidateStudentExcelData(List<StudentExcelDomain> studentExcelDomainList, Set<String> teachingClassCodeSet, Set<String> studentCodeSet) {
        boolean validator = true;
        if (null != studentExcelDomainList && studentExcelDomainList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (StudentExcelDomain studentExcelDomain : studentExcelDomainList) {
                boolean v1 = validateTeachingClassCode2(teachingClassCodeSet, studentExcelDomain.getTeachingClassCode(), sb);
                if (!v1) {
                    validator = v1;
                }
                if (StringUtils.isEmpty(studentExcelDomain.getStudentCode())) {
                    sb.append(",学生学号是必须的");
                    validator = false;
                } else {
                    studentCodeSet.add(studentExcelDomain.getStudentCode());
                }
                if (sb.length() > 0) {
                    studentExcelDomain.setMsg(sb.substring(1));
                    sb.delete(0, sb.length());
                }
            }
        }
        return validator;
    }

    public boolean firstValidateClassesExcelData(List<ClassesExcelDomain> classesExcelDomainList, Set<String> teachingClassCodeSet, Set<String> classesCodeSet, Set<String> classesNameSet) {
        boolean validator = true;
        if (null != classesExcelDomainList && classesExcelDomainList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (ClassesExcelDomain classesExcelDomain : classesExcelDomainList) {
                boolean v1 = validateTeachingClassCode2(teachingClassCodeSet, classesExcelDomain.getTeachingClassCode(), sb);
                if (!v1) {
                    validator = v1;
                }
                if (StringUtils.isEmpty(classesExcelDomain.getClassesCode()) && StringUtils.isEmpty(classesExcelDomain.getClassesName())) {
                    sb.append(",班级信息是必须的");
                    validator = false;
                } else {
                    if (!StringUtils.isEmpty(classesExcelDomain.getClassesCode())) {
                        classesCodeSet.add(classesExcelDomain.getClassesCode());
                    }
                    if (!StringUtils.isEmpty(classesExcelDomain.getClassesName())) {
                        classesNameSet.add(classesExcelDomain.getClassesName());
                    }
                }
                if (sb.length() > 0) {
                    classesExcelDomain.setMsg(sb.substring(1));
                    sb.delete(0, sb.length());
                }
            }
        }
        return validator;
    }

    private boolean firstValidateCourseScheduleExcelData(List<CourseScheduleExcelDomain> courseScheduleExcelDomainList, Set<String> teachingClassCodeSet) {
        boolean validator = true;
        if (null != courseScheduleExcelDomainList && courseScheduleExcelDomainList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (CourseScheduleExcelDomain courseScheduleExcelDomain : courseScheduleExcelDomainList) {
                boolean v1 = validateTeachingClassCode2(teachingClassCodeSet, courseScheduleExcelDomain.getTeachingClassCode(), sb);
                if (!v1) {
                    validator = v1;
                }
                if (null == courseScheduleExcelDomain.getStartWeek() || Integer.MIN_VALUE == courseScheduleExcelDomain.getStartWeek()) {
                    sb.append(",起始周是必须的，并且必须是一个大于0的整数");
                    validator = false;
                }

                if (null == courseScheduleExcelDomain.getEndWeek() || Integer.MIN_VALUE == courseScheduleExcelDomain.getEndWeek()) {
                    sb.append(",结束周是必须的，并且必须是一个大于0的整数");
                    validator = false;
                }
                if (!StringUtils.isEmpty(courseScheduleExcelDomain.getOneOrDouble())) {
                    if (!SingleOrDouble.SINGLE.getStateDesc().equals(courseScheduleExcelDomain.getOneOrDouble()) && !SingleOrDouble.DOUBLE.getStateDesc().equals(courseScheduleExcelDomain.getOneOrDouble())) {
                        sb.append(",单双周只能填写[单]、[双]或者空着");
                        validator = false;
                    }
                }
                if (null == courseScheduleExcelDomain.getWeek() || Integer.MIN_VALUE == courseScheduleExcelDomain.getEndWeek()) {
                    sb.append(",星期几是必须的，并且必须是一个1-7的数字");
                    validator = false;
                } else {
                    if (courseScheduleExcelDomain.getWeek() < 1 || courseScheduleExcelDomain.getWeek() > 7) {
                        sb.append(",星期几必须是一个1-7的数字");
                        validator = false;
                    }
                }

                if (null == courseScheduleExcelDomain.getStartPoriod() || Integer.MIN_VALUE == courseScheduleExcelDomain.getStartPoriod()) {
                    sb.append(",起始节是必须的，并且必须是一个大于0的整数");
                    validator = false;
                }

                if (null == courseScheduleExcelDomain.getPeriodNum() || Integer.MIN_VALUE == courseScheduleExcelDomain.getPeriodNum()) {
                    sb.append(",持续节是必须的，并且必须是一个大于0的整数");
                    validator = false;
                }

                if (sb.length() > 0) {
                    courseScheduleExcelDomain.setMsg(sb.substring(1));
                    sb.delete(0, sb.length());
                }
            }
        }
        return validator;
    }

    private String clearCacheAndContactMsg(StringBuilder sb, String msg) {
        String r = null;
        if (sb.length() > 0) {
            if (null != msg) {
                r = msg + sb.toString();
            } else {
                r = sb.substring(1);
            }
            sb.delete(0, sb.length());
        } else {
            r = msg;
        }
        return r;
    }

    private TeachingClass getTeachingClass(StringBuilder sb, String teachingClassCode, Map<String, TeachingClass> teachingClassMapForSave) {
        if (!StringUtils.isEmpty(teachingClassCode)) {
            TeachingClass teachingClass = teachingClassMapForSave.get(teachingClassCode);
            if (null == teachingClass) {
                sb.append(",该教学班编码在excel的教学任务标签中不存在");
            }
            return teachingClass;
        }
        return null;
    }

    private void initWeek(Map<String, Semester> codeAndSemesterMap, Map<Long, Map<Integer, Week>> weekMap) {
        if (null != codeAndSemesterMap && codeAndSemesterMap.size() > 0) {
            for (Semester semester : codeAndSemesterMap.values()) {
                List<Week> weeks = weekService.findAllWeekBySemester(semester);
                Map<Integer, Week> weekMap1 = new HashMap<>();
                weekMap.put(semester.getId(), weekMap1);
                for (Week week : weeks) {
                    weekMap1.put(week.getNo(), week);
                }
            }
        }
    }

    private void initWeek2(Semester semester, Map<Integer, Week> weekMap) {
        if (null != semester) {
            List<Week> weeks = weekService.findAllWeekBySemester(semester);
            for (Week week : weeks) {
                weekMap.put(week.getNo(), week);
            }
        }
    }

    public void importMustCourseScheduleDataNew(Long orgId, MultipartFile file, Long userId) {
        if (null == orgId || orgId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "orgId是必须的");
        }
        if (null == userId || userId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "userId是必须的");
        }

        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
        redisTokenStore.setPrefix("org_api:excel");
        MustCourseScheduleExcelDomain mustCourseScheduleExcelDomain = redisTokenStore.readMustCourseScheduleExcelDomain(orgId.toString());
        if (null != mustCourseScheduleExcelDomain && 10 == mustCourseScheduleExcelDomain.getState()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "上次导入任务还在进行中，请稍等一会再试");
        }

        MustCourseScheduleExcelDomain r = excelCourseScheduleDataService.readMustCourseScheduleFromInputStream(file);
        StringBuilder sb = new StringBuilder();
        if (null == r.getClassesExcelDomainList() || r.getClassesExcelDomainList().size() <= 0) {
            sb.append(",没有读取到任何班级数据");
        }
        if (null == r.getTeacherExcelDomainList() || r.getTeacherExcelDomainList().size() <= 0) {
            sb.append(",没有读取到任何老师数据");
        }
        if (null == r.getTeachingClassExcelDomainList() || r.getTeachingClassExcelDomainList().size() <= 0) {
            sb.append(",没有读取到任何教学任务数据");
        }
        if (null == r.getCourseScheduleExcelDomainList() || r.getCourseScheduleExcelDomainList().size() <= 0) {
            sb.append(",没有读取到任何课程表数据");
        }
        if (sb.length() > 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, sb.substring(1));
        }
        mustCourseScheduleExcelDomain = new MustCourseScheduleExcelDomain();
        mustCourseScheduleExcelDomain.setState(10);//处理中
        redisTokenStore.storeMustCourseScheduleExcelDomain(orgId.toString(), mustCourseScheduleExcelDomain);
        asyncTaskBase.importMustCourseSchedules(this, orgId, userId, r, redisTokenStore);

    }

    public MustCourseScheduleExcelDomain importMustCourseScheduleMsg(Long orgId, Long userId) {
        if (null == orgId || orgId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "orgId是必须的");
        }
        if (null == userId || userId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "userId是必须的");
        }
        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
        redisTokenStore.setPrefix("org_api:excel");
        return redisTokenStore.readMustCourseScheduleExcelDomain(orgId.toString());
    }

    /**
     * 带批量更新功能的版本
     *
     * @param orgId
     * @param userId
     * @return
     */
    public void processMustCourseScheduleData(Long orgId, Long userId, MustCourseScheduleExcelDomain r) {
        boolean validator = true;
        Set<String> teachingClassCodeSet = new HashSet<>();
        Set<String> courseCodeSet = new HashSet<>();
        Set<String> semesterCodeSet = new HashSet<>();
        Set<String> teacherCodeSet = new HashSet<>();
        Set<String> classesCodeSet = new HashSet<>();
        Set<String> classesNameSet = new HashSet<>();
        boolean v1 = firstValidateTeachingClassExcelData(r.getTeachingClassExcelDomainList(), teachingClassCodeSet, courseCodeSet, semesterCodeSet);
        if (!v1) {
            validator = v1;
        }
        boolean v2 = firstValidateTeacherExcelData(r.getTeacherExcelDomainList(), teachingClassCodeSet, teacherCodeSet);
        if (!v2) {
            validator = v2;
        }
        boolean v3 = firstValidateClassesExcelData(r.getClassesExcelDomainList(), teachingClassCodeSet, classesCodeSet, classesNameSet);
        if (!v3) {
            validator = v3;
        }
        boolean v4 = firstValidateCourseScheduleExcelData(r.getCourseScheduleExcelDomainList(), teachingClassCodeSet);
        if (!v4) {
            validator = v4;
        }

        List<TeachingClass> teachingClassList = null;
        if (teachingClassCodeSet.size() > 0) {
            teachingClassList = findByCodeIn(orgId, teachingClassCodeSet);
        }
        List<Course> courseList = null;
        if (courseCodeSet.size() > 0) {
            courseList = courseService.findByOrgIdAndCodes(orgId, courseCodeSet);
        }
        List<Semester> semesterList = null;
        if (semesterCodeSet.size() > 0) {
            semesterList = semesterService.findByOrgIdAndCodeIn(orgId, semesterCodeSet);
        }
        List<User> teacherList = null;
        if (teacherCodeSet.size() > 0) {
            teacherList = userService.findTeachersByCodeInAndOrg(orgId, teacherCodeSet);
        }
        List<Classes> classesByCodeList = null;
        if (classesCodeSet.size() > 0) {
            classesByCodeList = classesService.findByOrgIdAndCodes(orgId, classesCodeSet);
        }
        List<Classes> classesByNameList = null;
        if (classesNameSet.size() > 0) {
            classesByNameList = classesService.findByOrgIdAndNames(orgId, classesNameSet);
        }
        Map<String, Course> codeAndCourseMap = new HashMap<>();
        if (null != courseList) {
            for (Course course : courseList) {
                codeAndCourseMap.put(course.getCode(), course);
            }
        }
        Semester currentSemester = null;
        Map<String, Semester> codeAndSemesterMap = new HashMap<>();
        if (null != semesterList) {
            for (Semester semester : semesterList) {
                codeAndSemesterMap.put(semester.getCode(), semester);
                currentSemester = semester;
            }
        }
        Map<String, List<TeachingClassTeacher>> teachingClassForTeacherMap = new HashMap<>();
        Map<String, List<TeachingClassClasses>> teachingClassForClassesMap = new HashMap<>();
        Map<String, List<SchoolTimeTable>> teachingClassForSchoolTimeTablesMap = new HashMap<>();

        Map<Integer, Week> weekMap = new HashMap<>();
        //初始化所以使用到学期的学周数据
        initWeek2(currentSemester, weekMap);
        //初始化课程节
        Map<Integer, Period> periodMap = new HashMap<>();
        List<Period> periodList = periodService.findByOrgId(orgId);
        for (Period period : periodList) {
            periodMap.put(period.getNo(), period);
        }

        Map<String, TeachingClass> codeAndTeachingClassMap = new HashMap<>();
        if (null != teachingClassList && teachingClassList.size() > 0) {
            for (TeachingClass teachingClass : teachingClassList) {
                codeAndTeachingClassMap.put(teachingClass.getCode(), teachingClass);
            }
        }

        Map<String, User> codeAndTeacherMap = new HashMap<>();
        if (null != teacherList && teacherList.size() > 0) {
            for (User teacher : teacherList) {
                codeAndTeacherMap.put(teacher.getJobNumber(), teacher);
            }
        }
        Map<String, Classes> codeAndClassesByCodeMap = new HashMap<>();
        if (null != classesByCodeList && classesByCodeList.size() > 0) {
            for (Classes classes : classesByCodeList) {
                codeAndClassesByCodeMap.put(classes.getCode(), classes);
            }
        }
        Map<String, Classes> codeAndClassesByNameMap = new HashMap<>();
        if (null != classesByNameList && classesByNameList.size() > 0) {
            for (Classes classes : classesByNameList) {
                codeAndClassesByNameMap.put(classes.getName(), classes);
            }
        }

        //--------------------------------------------最终保存结果构造及验证----------------------------------------------//
        List<TeachingClass> teachingClassListSave = new ArrayList<>();
        List<TeachingClassTeacher> teachingClassTeacherList = new ArrayList<>();
        List<TeachingClassClasses> teachingClassClassesList = new ArrayList<>();
        List<SchoolTimeTable> schoolTimeTableList = new ArrayList<>();

        Map<String, TeachingClass> teachingClassMapForSave = new HashMap<>();

        //二次验证教学班
        boolean v5 = secondValidatorAndCreateTeachingClass2(orgId, ClassesOrStudents.CLASSES.getState(), r.getTeachingClassExcelDomainList(), codeAndTeachingClassMap, codeAndCourseMap, currentSemester, teachingClassListSave);
        if (!v5) {
            validator = v5;
        }
        //初始化教学班
        initTeachingClassingMap(teachingClassListSave, teachingClassMapForSave);
        //二次验证教学班老师
        boolean v6 = secondValidatorAndCreateTeacher2(r.getTeacherExcelDomainList(), teachingClassTeacherList, teachingClassForTeacherMap, teachingClassMapForSave, codeAndTeacherMap);
        if (!v6) {
            validator = v6;
        }
        //二次验证班级信息
        boolean v7 = secondValidatorAndCreateClasses2(r.getClassesExcelDomainList(), teachingClassClassesList, teachingClassForClassesMap, teachingClassMapForSave, codeAndClassesByCodeMap, codeAndClassesByNameMap);
        if (!v7) {
            validator = v7;
        }
        //验证并构造课表数据
        boolean v8 = secondValidatorAndCreateCourseSchedule2(r.getCourseScheduleExcelDomainList(), schoolTimeTableList, teachingClassForSchoolTimeTablesMap, teachingClassMapForSave, weekMap, periodMap);
        if (!v8) {
            validator = v8;
        }

        boolean v9 = secondValidatorTeachingClassProperty(r.getTeachingClassExcelDomainList(), teachingClassMapForSave, teachingClassForTeacherMap, teachingClassForClassesMap, teachingClassForSchoolTimeTablesMap);
        if (!v9) {
            validator = v9;
        }
        //暂不支持多个学期课表数据的导入，方便支持删除操作
        if (codeAndSemesterMap.size() > 1) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "当前教学班课程表存在多个学期数据，暂不支持");
        }

        if (!validator) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "验证失败");
        }

        //先删除已经导入教学班的老师、班级、学生、课表信息，然后保存新数据进去(只有教学班自己是修改操作，子项全是覆盖操作)
        List<Long> teachingClassIdList = new ArrayList<>();
        if (null != teachingClassList && teachingClassList.size() > 0) {
            schoolTimeTableService.deleteByTeachingClassList(teachingClassList);
            teachingClassClassesService.deleteByTeachingClassList(teachingClassList);
            teachingClassTeacherService.deleteByTeachingClassList(teachingClassList);
            teachingClassStudentsService.deleteByTeachingClassList(teachingClassList);

            save(teachingClassList);
            for (TeachingClass t : teachingClassList) {
                teachingClassIdList.add(t.getId());
            }
        }
        //发送消息
        if (!teachingClassIdList.isEmpty()) {
            if (teachingClassIdList.size()<=100) {
                dataSynService.sendTeachingClassAllUserDeleteMsg(teachingClassIdList);
            }else {
                List<Long> ids = new ArrayList<>();
                for (int i=0;i<teachingClassIdList.size();i++) {
                    if (i%100==0&&i!=0){
                        dataSynService.sendTeachingClassAllUserDeleteMsg(ids);
                        ids.clear();
                    }
                    ids.add(teachingClassIdList.get(i));
                    if (!teachingClassIdList.isEmpty()&&i==ids.size()-1){
                        dataSynService.sendTeachingClassAllUserDeleteMsg(ids);
                    }
                }
            }
        }
        //验证的时候不修改已保存教学班的信息，在实际保存之前做修改操作
        List<TeachingClassMsgDTO> msgList = new ArrayList<>();
        setTeachingclassProperty(r.getTeachingClassExcelDomainList(), teachingClassListSave, codeAndCourseMap, currentSemester);
        if (!teachingClassListSave.isEmpty()) {
            save(teachingClassListSave);
            for (TeachingClass t : teachingClassListSave) {
                msgList.add(initTeachingClassMsgDTO(t, t.getCourse(), t.getSemester()));
            }
        }
        if (!msgList.isEmpty()) {
            if (msgList.size()<=100) {
                dataSynService.sendTeachingAddMsg(msgList);
            }else {
                List<TeachingClassMsgDTO> teachingClassMsgDTOS = new ArrayList<>();
                for (int i=0;i<msgList.size();i++){
                    if (i%100==0&&i!=0){
                        dataSynService.sendTeachingAddMsg(teachingClassMsgDTOS);
                        teachingClassMsgDTOS.clear();
                    }
                    teachingClassMsgDTOS.add(msgList.get(i));
                    if (i==msgList.size()-1&&!teachingClassMsgDTOS.isEmpty()){
                        dataSynService.sendTeachingAddMsg(teachingClassMsgDTOS);
                    }
                }
            }
        }
        teachingClassTeacherService.save(teachingClassTeacherList);
        teachingClassClassesService.save(teachingClassClassesList);

        for (TeachingClassClasses t : teachingClassClassesList) {
            Set<Classes> classesSet = new HashSet<>();
            classesSet.add(t.getClasses());
            teachingClassStudentsService.saveStudentByClasses(t.getTeachingClass(), classesSet);
        }

        schoolTimeTableService.save(schoolTimeTableList);
        teachingClassTeacherService.sendAddMsg(teachingClassTeacherList);
    }


    private void initTeachingClassingMap(List<TeachingClass> teachingClassListSave, Map<String, TeachingClass> teachingClassMapForSave) {
        for (TeachingClass teachingClass : teachingClassListSave) {
            teachingClassMapForSave.put(teachingClass.getCode(), teachingClass);
        }
    }


    public void importOptionCourseScheduleDataNew(Long orgId, MultipartFile file, Long userId) {
        if (null == orgId || orgId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "orgId是必须的");
        }
        if (null == userId || userId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "userId是必须的");
        }

        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
        redisTokenStore.setPrefix("org_api:excel");
        OptionCourseScheduleExcelDomain optionCourseScheduleExcelDomain = redisTokenStore.readOptionCourseScheduleExcelDomain(orgId.toString());
        if (null != optionCourseScheduleExcelDomain && 10 == optionCourseScheduleExcelDomain.getState()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "上次导入任务还在进行中，请稍等一会再试");
        }

        OptionCourseScheduleExcelDomain r = excelCourseScheduleDataService.readOptionCourseScheduleFromInputStream(file);
        StringBuilder sb = new StringBuilder();
        if (null == r.getStudentExcelDomainList() || r.getStudentExcelDomainList().size() <= 0) {
            sb.append(",没有读取到任何学生数据");
        }
        if (null == r.getTeacherExcelDomainList() || r.getTeacherExcelDomainList().size() <= 0) {
            sb.append(",没有读取到任何老师数据");
        }
        if (null == r.getTeachingClassExcelDomainList() || r.getTeachingClassExcelDomainList().size() <= 0) {
            sb.append(",没有读取到任何教学任务数据");
        }
        if (null == r.getCourseScheduleExcelDomainList() || r.getCourseScheduleExcelDomainList().size() <= 0) {
            sb.append(",没有读取到任何课程表数据");
        }
        if (sb.length() > 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, sb.substring(1));
        }
        optionCourseScheduleExcelDomain = new OptionCourseScheduleExcelDomain();
        optionCourseScheduleExcelDomain.setState(10);//处理中
        redisTokenStore.storeOptionCourseScheduleExcelDomain(orgId.toString(), optionCourseScheduleExcelDomain);
        asyncTaskBase.importOptionCourseSchedules(this, orgId, userId, r, redisTokenStore);
    }

    public OptionCourseScheduleExcelDomain importOptionCourseScheduleMsg(Long orgId, Long userId) {
        if (null == orgId || orgId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "orgId是必须的");
        }
        if (null == userId || userId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "userId是必须的");
        }
        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
        redisTokenStore.setPrefix("org_api:excel");
        return redisTokenStore.readOptionCourseScheduleExcelDomain(orgId.toString());
    }

    public void processOptionCourseScheduleData(Long orgId, Long userId, OptionCourseScheduleExcelDomain r) {
        boolean validator = true;
        Set<String> teachingClassCodeSet = new HashSet<>();
        Set<String> courseCodeSet = new HashSet<>();
        Set<String> semesterCodeSet = new HashSet<>();
        Set<String> teacherCodeSet = new HashSet<>();
        Set<String> studentsCodeSet = new HashSet<>();
        boolean v1 = firstValidateTeachingClassExcelData(r.getTeachingClassExcelDomainList(), teachingClassCodeSet, courseCodeSet, semesterCodeSet);
        if (!v1) {
            validator = v1;
        }
        boolean v2 = firstValidateTeacherExcelData(r.getTeacherExcelDomainList(), teachingClassCodeSet, teacherCodeSet);
        if (!v2) {
            validator = v2;
        }
        boolean v3 = firstValidateStudentExcelData(r.getStudentExcelDomainList(), teachingClassCodeSet, studentsCodeSet);
        if (!v3) {
            validator = v3;
        }
        boolean v4 = firstValidateCourseScheduleExcelData(r.getCourseScheduleExcelDomainList(), teachingClassCodeSet);
        if (!v4) {
            validator = v4;
        }

        List<TeachingClass> teachingClassList = null;
        if (teachingClassCodeSet.size() > 0) {
            teachingClassList = findByCodeIn(orgId, teachingClassCodeSet);
        }
        List<Course> courseList = null;
        if (courseCodeSet.size() > 0) {
            courseList = courseService.findByOrgIdAndCodes(orgId, courseCodeSet);
        }
        List<Semester> semesterList = null;
        if (semesterCodeSet.size() > 0) {
            semesterList = semesterService.findByOrgIdAndCodeIn(orgId, semesterCodeSet);
        }
        List<User> teacherList = null;
        if (teacherCodeSet.size() > 0) {
            teacherList = userService.findTeachersByCodeInAndOrg(orgId, teacherCodeSet);
        }
        List<User> studentsByCodeList = null;
        if (studentsCodeSet.size() > 0) {
            studentsByCodeList = userService.findStudentsByCodeInAndOrg(orgId, studentsCodeSet);
        }
        Map<String, Course> codeAndCourseMap = new HashMap<>();
        if (null != courseList) {
            for (Course course : courseList) {
                codeAndCourseMap.put(course.getCode(), course);
            }
        }
        Semester currentSemester = null;
        Map<String, Semester> codeAndSemesterMap = new HashMap<>();
        if (null != semesterList) {
            for (Semester semester : semesterList) {
                currentSemester = semester;
                codeAndSemesterMap.put(semester.getCode(), semester);
            }
        }

        Map<String, List<TeachingClassTeacher>> teachingClassForTeacherMap = new HashMap<>();
        Map<String, List<TeachingClassStudents>> teachingClassStudentsMap = new HashMap<>();
        Map<String, List<SchoolTimeTable>> teachingClassForSchoolTimeTablesMap = new HashMap<>();

        //初始化所以使用到学期的学周数据
//        Map <Long, Map <Integer, Week>> weekMap = new HashMap <>();
        Map<Integer, Week> weekMap = new HashMap<>();
        initWeek2(currentSemester, weekMap);

        //初始化课程节
        Map<Integer, Period> periodMap = new HashMap<>();
        List<Period> periodList = periodService.findByOrgId(orgId);
        for (Period period : periodList) {
            periodMap.put(period.getNo(), period);
        }

        Map<String, TeachingClass> codeAndTeachingClassMap = new HashMap<>();
        if (null != teachingClassList && teachingClassList.size() > 0) {
            for (TeachingClass teachingClass : teachingClassList) {
                codeAndTeachingClassMap.put(teachingClass.getCode(), teachingClass);
            }
        }

        Map<String, User> codeAndTeacherMap = new HashMap<>();
        if (null != teacherList && teacherList.size() > 0) {
            for (User teacher : teacherList) {
                codeAndTeacherMap.put(teacher.getJobNumber(), teacher);
            }
        }
        Map<String, User> codeAndStudentMap = new HashMap<>();
        if (null != studentsByCodeList) {
            for (User student : studentsByCodeList) {
                codeAndStudentMap.put(student.getJobNumber(), student);
            }
        }

        //--------------------------------------------最终保存结果构造及验证----------------------------------------------//
        List<TeachingClass> teachingClassListSave = new ArrayList<>();
        List<TeachingClassTeacher> teachingClassTeacherList = new ArrayList<>();
        List<TeachingClassStudents> teachingClassStudentList = new ArrayList<>();
        List<SchoolTimeTable> schoolTimeTableList = new ArrayList<>();
        Map<String, TeachingClass> teachingClassMapForSave = new HashMap<>();


        //二次验证教学班
        boolean v5 = secondValidatorAndCreateTeachingClass2(orgId, ClassesOrStudents.STUDENTS.getState(), r.getTeachingClassExcelDomainList(), codeAndTeachingClassMap, codeAndCourseMap, currentSemester, teachingClassListSave);
        if (!v5) {
            validator = v5;
        }
        //初始化教学班
        initTeachingClassingMap(teachingClassListSave, teachingClassMapForSave);

        //二次验证教学班老师
        boolean v6 = secondValidatorAndCreateTeacher2(r.getTeacherExcelDomainList(), teachingClassTeacherList, teachingClassForTeacherMap, teachingClassMapForSave, codeAndTeacherMap);
        if (!v6) {
            validator = v6;
        }
        //二次验证学生信息
        boolean v7 = secondValidatorAndCreateStudent2(r.getStudentExcelDomainList(), teachingClassStudentList, teachingClassStudentsMap, teachingClassMapForSave, codeAndStudentMap);
        if (!v7) {
            validator = v7;
        }
        //验证并构造课表数据
        boolean v8 = secondValidatorAndCreateCourseSchedule2(r.getCourseScheduleExcelDomainList(), schoolTimeTableList, teachingClassForSchoolTimeTablesMap, teachingClassMapForSave, weekMap, periodMap);
        if (!v8) {
            validator = v8;
        }
        boolean v9 = secondValidatorTeachingClassPropertyOpt(r.getTeachingClassExcelDomainList(), teachingClassMapForSave, teachingClassForTeacherMap, teachingClassStudentsMap, teachingClassForSchoolTimeTablesMap);
        if (!v9) {
            validator = v9;
        }
        //暂不支持多个学期课表数据的导入，方便支持删除操作
        if (codeAndSemesterMap.size() > 1) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "当前教学班课程表存在多个学期数据，暂不支持");
        }

        if (!validator) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "验证失败");
        }

        //先删除已经导入教学班的老师、学生、课表信息，然后保存新数据进去(只有教学班自己是修改操作，子项全是覆盖操作)
        List<Long> teachingClassIdList = new ArrayList<>();
        if (null != teachingClassList && teachingClassList.size() > 0) {
            schoolTimeTableService.deleteByTeachingClassList(teachingClassList);
            teachingClassStudentsService.deleteByTeachingClassList(teachingClassList);
            teachingClassTeacherService.deleteByTeachingClassList(teachingClassList);
            for (TeachingClass t : teachingClassList) {
                teachingClassIdList.add(t.getId());
            }
            save(teachingClassList);
        }
        //验证的时候不修改已保存教学班的信息，在实际保存之前做修改操作
        setTeachingclassProperty(r.getTeachingClassExcelDomainList(), teachingClassListSave, codeAndCourseMap, currentSemester);
        List<TeachingClassMsgDTO> msgList = new ArrayList<>();
        if (!teachingClassListSave.isEmpty()) {
            save(teachingClassListSave);
            for (TeachingClass t : teachingClassListSave) {
                msgList.add(initTeachingClassMsgDTO(t, t.getCourse(), t.getSemester()));
            }
        }

        if (!teachingClassIdList.isEmpty()) {
            if (teachingClassIdList.size()<=100) {
                dataSynService.sendTeachingClassAllUserDeleteMsg(teachingClassIdList);
            }else {
                List<Long> ids = new ArrayList<>();
                for (int i=0;i<teachingClassIdList.size();i++) {
                    if (i%100==0&&i!=0){
                        dataSynService.sendTeachingClassAllUserDeleteMsg(ids);
                        ids.clear();
                    }
                    ids.add(teachingClassIdList.get(i));
                    if (i==teachingClassIdList.size()-1&&!ids.isEmpty()){
                        dataSynService.sendTeachingClassAllUserDeleteMsg(ids);
                    }
                }
            }
        }

        teachingClassTeacherService.save(teachingClassTeacherList);
        teachingClassStudentsService.save(teachingClassStudentList);
        schoolTimeTableService.save(schoolTimeTableList);

        //发送消息
        if (!msgList.isEmpty()) {
            if (msgList.size()<=100) {
                dataSynService.sendTeachingAddMsg(msgList);
            }else {
                List<TeachingClassMsgDTO> teachingClassMsgDTOS = new ArrayList<>();
                for (int i=0;i<msgList.size();i++){
                    if (i%100==0&&i!=0){
                        dataSynService.sendTeachingAddMsg(teachingClassMsgDTOS);
                        teachingClassMsgDTOS.clear();
                    }
                    teachingClassMsgDTOS.add(msgList.get(i));
                    if (i==msgList.size()-1&&!teachingClassMsgDTOS.isEmpty()){
                        dataSynService.sendTeachingAddMsg(teachingClassMsgDTOS);
                    }
                }
            }
        }
        teachingClassStudentsService.sendAddMsg(teachingClassStudentList);
        teachingClassTeacherService.sendAddMsg(teachingClassTeacherList);
    }

    /**
     * 验证的时候不修改已保存教学班的信息，在实际保存之前做修改操作
     *
     * @param teachingClassExcelDomainList
     * @param teachingClassListSave
     * @param codeAndCourseMap
     * @param semester
     */
    private void setTeachingclassProperty(List<TeachingClassExcelDomain> teachingClassExcelDomainList, List<TeachingClass> teachingClassListSave, Map<String, Course> codeAndCourseMap, Semester semester) {
        Map<String, TeachingClassExcelDomain> teachingClassExcelDomainMap = new HashMap<>();
        for (TeachingClassExcelDomain d : teachingClassExcelDomainList) {
            teachingClassExcelDomainMap.put(d.getTeachingClassCode(), d);
        }
        for (TeachingClass t : teachingClassListSave) {
            TeachingClassExcelDomain d = teachingClassExcelDomainMap.get(t.getCode());
            if (null != d) {
                t.setName(d.getTeachingClassName());
            }
            Course course = codeAndCourseMap.get(d.getCourseCode());
            if (null != course) {
                t.setCourse(course);
            }

            t.setSemester(semester);
        }
    }

    private boolean secondValidatorTeachingClassPropertyOpt(List<TeachingClassExcelDomain> teachingClassExcelDomainList, Map<String, TeachingClass> teachingClassMapForSave, Map<String, List<TeachingClassTeacher>> teachingClassForTeacherMap, Map<String, List<TeachingClassStudents>> teachingClassStudentsMap, Map<String, List<SchoolTimeTable>> teachingClassForSchoolTimeTablesMap) {
        boolean validator = true;
        if (null != teachingClassExcelDomainList && teachingClassExcelDomainList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (TeachingClassExcelDomain teachingClassExcelDomain : teachingClassExcelDomainList) {
                if (!StringUtils.isEmpty(teachingClassExcelDomain.getTeachingClassCode())) {
                    TeachingClass teachingClass = teachingClassMapForSave.get(teachingClassExcelDomain.getTeachingClassCode());
                    if (null != teachingClass) {
                        if (teachingClass.getClassOrStudents() == ClassesOrStudents.CLASSES.getState().intValue()) {
                            sb.append(",选修课不能导入必修数据");
                            validator = false;
                        }
                    }
                    List<TeachingClassTeacher> teachingClassTeacherList = teachingClassForTeacherMap.get(teachingClassExcelDomain.getTeachingClassCode());
                    if (null == teachingClassTeacherList || teachingClassTeacherList.size() <= 0) {
                        sb.append(",该教学班没有对应的老师数据");
                        validator = false;
                    }
                    List<TeachingClassStudents> teachingClassStudentsList = teachingClassStudentsMap.get(teachingClassExcelDomain.getTeachingClassCode());
                    if (null == teachingClassStudentsList || teachingClassStudentsList.size() <= 0) {
                        sb.append(",该教学班没有对应的学生数据");
                        validator = false;
                    }
                    List<SchoolTimeTable> schoolTimeTableList = teachingClassForSchoolTimeTablesMap.get(teachingClassExcelDomain.getTeachingClassCode());
                    if (null == schoolTimeTableList || schoolTimeTableList.size() <= 0) {
                        sb.append(",该教学班没有对应的排课数据");
                        validator = false;
                    }
                }
                teachingClassExcelDomain.setMsg(clearCacheAndContactMsg(sb, teachingClassExcelDomain.getMsg()));
            }
        }
        return validator;
    }

    private boolean secondValidatorTeachingClassProperty(List<TeachingClassExcelDomain> teachingClassExcelDomainList, Map<String, TeachingClass> teachingClassMapForSave, Map<String, List<TeachingClassTeacher>> teachingClassForTeacherMap, Map<String, List<TeachingClassClasses>> teachingClassForClassesMap, Map<String, List<SchoolTimeTable>> teachingClassForSchoolTimeTablesMap) {
        boolean validator = true;
        if (null != teachingClassExcelDomainList && teachingClassExcelDomainList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (TeachingClassExcelDomain teachingClassExcelDomain : teachingClassExcelDomainList) {
                if (!StringUtils.isEmpty(teachingClassExcelDomain.getTeachingClassCode())) {
                    TeachingClass teachingClass = teachingClassMapForSave.get(teachingClassExcelDomain.getTeachingClassCode());
                    if (null != teachingClass) {
                        if (teachingClass.getClassOrStudents() == ClassesOrStudents.STUDENTS.getState().intValue()) {
                            sb.append(",必修课不能导入选修数据");
                            validator = false;
                        }
                    }
                    List<TeachingClassTeacher> teachingClassTeacherList = teachingClassForTeacherMap.get(teachingClassExcelDomain.getTeachingClassCode());
                    if (null == teachingClassTeacherList || teachingClassTeacherList.size() <= 0) {
                        sb.append(",该教学班没有对应的老师数据");
                        validator = false;
                    }
                    List<TeachingClassClasses> teachingClassClassesList = teachingClassForClassesMap.get(teachingClassExcelDomain.getTeachingClassCode());
                    if (null == teachingClassClassesList || teachingClassClassesList.size() <= 0) {
                        sb.append(",该教学班没有对应的班级数据");
                        validator = false;
                    }
                    List<SchoolTimeTable> schoolTimeTableList = teachingClassForSchoolTimeTablesMap.get(teachingClassExcelDomain.getTeachingClassCode());
                    if (null == schoolTimeTableList || schoolTimeTableList.size() <= 0) {
                        sb.append(",该教学班没有对应的排课数据");
                        validator = false;
                    }
                }
                teachingClassExcelDomain.setMsg(clearCacheAndContactMsg(sb, teachingClassExcelDomain.getMsg()));
            }
        }
        return validator;
    }

    private boolean secondValidatorAndCreateTeachingClass2(Long orgId, Integer classesOrStudent, List<TeachingClassExcelDomain> teachingClassExcelDomainList, Map<String, TeachingClass> codeAndTeachingClassMap, Map<String, Course> codeAndcourseMap, Semester semester, List<TeachingClass> teachingClassList) {
        boolean validator = true;
        if (null != teachingClassExcelDomainList && teachingClassExcelDomainList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (TeachingClassExcelDomain teachingClassExcelDomain : teachingClassExcelDomainList) {
                TeachingClass teachingClass = null;
                if (null != codeAndTeachingClassMap && !StringUtils.isEmpty(teachingClassExcelDomain.getTeachingClassCode())) {
                    teachingClass = codeAndTeachingClassMap.get(teachingClassExcelDomain.getTeachingClassCode());
                }
                if (null == teachingClass) {//新增
                    teachingClass = new TeachingClass();
                    teachingClass.setClassOrStudents(classesOrStudent);
                    teachingClass.setCode(teachingClassExcelDomain.getTeachingClassCode());
                    teachingClass.setOrgId(orgId);
                    teachingClass.setName(teachingClassExcelDomain.getTeachingClassName());
                    teachingClass.setSemester(semester);
                }
                teachingClassList.add(teachingClass);
//                teachingClass.setName(teachingClassExcelDomain.getTeachingClassName());
                Course course = null;
                if (StringUtils.isEmpty(teachingClassExcelDomain.getCourseCode()) && !codeAndcourseMap.keySet().contains(teachingClassExcelDomain.getCourseCode())) {
                    sb.append(",该课程编码不存在");
                    validator = false;
                } else {
                    course = codeAndcourseMap.get(teachingClassExcelDomain.getCourseCode());
                    if (null == course) {
                        sb.append(",根据课程编码没有找到对应的课程数据");
                        validator = false;
                    }
                }
                if (null == course) {
                    sb.append(",课程数据是必须的");
                    validator = false;
                }
                if (null == semester) {
                    sb.append(",根据学年学期信息没有找到对应的学期数据");
                    validator = false;
                }

                teachingClassExcelDomain.setMsg(clearCacheAndContactMsg(sb, teachingClassExcelDomain.getMsg()));
            }
        }
        return validator;
    }

    private boolean secondValidatorAndCreateTeacher2(List<TeacherExcelDomain> teacherExcelDomainList, List<TeachingClassTeacher> teachingClassTeacherList, Map<String, List<TeachingClassTeacher>> teachingClassForTeacherMap, Map<String, TeachingClass> teachingClassMapForSave, Map<String, User> codeAndTeacherMap) {
        boolean validator = true;
        if (null != teacherExcelDomainList && teacherExcelDomainList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (TeacherExcelDomain teacherExcelDomain : teacherExcelDomainList) {
                TeachingClassTeacher teachingClassTeacher = new TeachingClassTeacher();
                teachingClassTeacherList.add(teachingClassTeacher);
                TeachingClass teachingClass = getTeachingClass(sb, teacherExcelDomain.getTeachingClassCode(), teachingClassMapForSave);
                if (null == teachingClass) {
                    validator = false;
                } else {
                    teachingClassTeacher.setTeachingClass(teachingClass);
                    teachingClassTeacher.setOrgId(teachingClass.getOrgId());
                    teachingClassTeacher.setSemester(teachingClass.getSemester());
                }
                if (!StringUtils.isEmpty(teacherExcelDomain.getTeachingClassCode())) {
                    List<TeachingClassTeacher> teachingClassTeacherList1 = teachingClassForTeacherMap.get(teacherExcelDomain.getTeachingClassCode());
                    if (null == teachingClassTeacherList1) {
                        teachingClassTeacherList1 = new ArrayList<>();
                        teachingClassForTeacherMap.put(teacherExcelDomain.getTeachingClassCode(), teachingClassTeacherList1);
                    }
                    teachingClassTeacherList1.add(teachingClassTeacher);
                }

                if (!StringUtils.isEmpty(teacherExcelDomain.getTeacherCode())) {
                    User teacher = codeAndTeacherMap.get(teacherExcelDomain.getTeacherCode());
                    if (null != teacher) {
                        teachingClassTeacher.setTeacher(teacher);
                    } else {
                        sb.append(",根据教师工号没有找到对应的教师信息");
                        validator = false;
                    }
                }

                if (null == teachingClassTeacher.getTeachingClass()) {
                    sb.append(",没有合适的教学班信息匹配");
                    validator = false;
                }
                if (null == teachingClassTeacher.getTeacher()) {
                    sb.append(",没有合适的老师信息匹配");
                    validator = false;
                }
                teacherExcelDomain.setMsg(clearCacheAndContactMsg(sb, teacherExcelDomain.getMsg()));
            }
        }
        return validator;
    }

    private boolean secondValidatorAndCreateClasses2(List<ClassesExcelDomain> classesExcelDomainList, List<TeachingClassClasses> teachingClassClassesList, Map<String, List<TeachingClassClasses>> teachingClassForClassesMap, Map<String, TeachingClass> teachingClassMapForSave, Map<String, Classes> codeAndClassesByCodeMap, Map<String, Classes> codeAndClassesByNameMap) {
        boolean validator = true;
        if (null != classesExcelDomainList && classesExcelDomainList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (ClassesExcelDomain classesExcelDomain : classesExcelDomainList) {
                TeachingClassClasses teachingClassClasses = new TeachingClassClasses();
                teachingClassClassesList.add(teachingClassClasses);
                TeachingClass teachingClass = getTeachingClass(sb, classesExcelDomain.getTeachingClassCode(), teachingClassMapForSave);
                if (null == teachingClass) {
                    validator = false;
                } else {
                    teachingClassClasses.setTeachingClass(teachingClass);
                    teachingClassClasses.setOrgId(teachingClass.getOrgId());
                    teachingClassClasses.setSemester(teachingClass.getSemester());
                }
                if (!StringUtils.isEmpty(classesExcelDomain.getClassesCode())) {
                    Classes classes = codeAndClassesByCodeMap.get(classesExcelDomain.getClassesCode());
                    if (null != classes) {
                        teachingClassClasses.setClasses(classes);
                    }
                }
                if (null == teachingClassClasses.getClasses()) {
                    if (!StringUtils.isEmpty(classesExcelDomain.getClassesName())) {
                        Classes classes = codeAndClassesByNameMap.get(classesExcelDomain.getClassesName());
                        if (null != classes) {
                            teachingClassClasses.setClasses(classes);
                        }
                    }
                }

                if (!StringUtils.isEmpty(classesExcelDomain.getTeachingClassCode())) {
                    List<TeachingClassClasses> teachingClassClasses1 = teachingClassForClassesMap.get(classesExcelDomain.getTeachingClassCode());
                    if (null == teachingClassClasses1) {
                        teachingClassClasses1 = new ArrayList<>();
                        teachingClassForClassesMap.put(classesExcelDomain.getTeachingClassCode(), teachingClassClasses1);
                    }
                    teachingClassClasses1.add(teachingClassClasses);
                }
                if (null == teachingClassClasses.getTeachingClass()) {
                    sb.append(",没有匹配到合适的教学班信息");
                    validator = false;
                }
                if (null == teachingClassClasses.getClasses()) {
                    sb.append(",没有匹配到合适的班级信息");
                    validator = false;
                }

                classesExcelDomain.setMsg(clearCacheAndContactMsg(sb, classesExcelDomain.getMsg()));
            }
        }
        return validator;
    }

    private boolean secondValidatorAndCreateStudent2(List<StudentExcelDomain> studentExcelDomains, List<TeachingClassStudents> teachingClassStudents, Map<String, List<TeachingClassStudents>> teachingClassStudentsMap, Map<String, TeachingClass> teachingClassMapForSave, Map<String, User> codeAndStudentMap) {
        boolean validator = true;
        if (null != studentExcelDomains && studentExcelDomains.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (StudentExcelDomain studentExcelDomain : studentExcelDomains) {
                TeachingClassStudents teachingClassStudent = new TeachingClassStudents();
                teachingClassStudents.add(teachingClassStudent);
                TeachingClass teachingClass = getTeachingClass(sb, studentExcelDomain.getTeachingClassCode(), teachingClassMapForSave);
                if (null == teachingClass) {
                    validator = false;
                } else {
                    teachingClassStudent.setTeachingClass(teachingClass);
                    teachingClassStudent.setOrgId(teachingClass.getOrgId());
                    teachingClassStudent.setSemester(teachingClass.getSemester());
                }
                if (!StringUtils.isEmpty(studentExcelDomain.getStudentCode())) {
                    User student = codeAndStudentMap.get(studentExcelDomain.getStudentCode());
                    if (null != student) {
                        teachingClassStudent.setStudent(student);
                    } else {
                        sb.append(",根据学生学号没有找到对应的学生信息");
                        validator = false;
                    }
                }

                if (!StringUtils.isEmpty(studentExcelDomain.getTeachingClassCode())) {
                    List<TeachingClassStudents> teachingClassStudents1 = teachingClassStudentsMap.get(studentExcelDomain.getTeachingClassCode());
                    if (null == teachingClassStudents1) {
                        teachingClassStudents1 = new ArrayList<>();
                        teachingClassStudentsMap.put(studentExcelDomain.getTeachingClassCode(), teachingClassStudents1);
                    }
                    teachingClassStudents1.add(teachingClassStudent);
                }
                if (null == teachingClassStudent.getTeachingClass()) {
                    sb.append(",没有匹配到合适的教学班信息");
                    validator = false;
                }
                if (null == teachingClassStudent.getStudent()) {
                    sb.append(",没有匹配到合适的学生信息");
                    validator = false;
                }

                studentExcelDomain.setMsg(clearCacheAndContactMsg(sb, studentExcelDomain.getMsg()));
            }
        }
        return validator;
    }

    private boolean secondValidatorAndCreateCourseSchedule2(List<CourseScheduleExcelDomain> courseScheduleExcelDomainList, List<SchoolTimeTable> schoolTimeTableList, Map<String, List<SchoolTimeTable>> teachingClassForSchoolTimeTablesMap, Map<String, TeachingClass> teachingClassMapForSave, Map<Integer, Week> weekMap, Map<Integer, Period> periodMap) {
        boolean validator = true;
//        Map <Integer, Week> weekMap1 = null;
        if (null != courseScheduleExcelDomainList && courseScheduleExcelDomainList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (CourseScheduleExcelDomain courseScheduleExcelDomain : courseScheduleExcelDomainList) {
                SchoolTimeTable schoolTimeTable = new SchoolTimeTable();
                schoolTimeTableList.add(schoolTimeTable);
                TeachingClass teachingClass = getTeachingClass(sb, courseScheduleExcelDomain.getTeachingClassCode(), teachingClassMapForSave);
                if (null == teachingClass) {
                    validator = false;
                } else {
                    schoolTimeTable.setTeachingClass(teachingClass);
                    schoolTimeTable.setOrgId(teachingClass.getOrgId());
                    schoolTimeTable.setSemester(teachingClass.getSemester());
                }
                if (null != teachingClass) {
                    if (null != teachingClass.getSemester()) {
//                        weekMap1 = weekMap.get(teachingClass.getSemester().getId());
                    } else {
                        sb.append(",该教学班没有找到对应的学期信息");
                        validator = false;
                    }
                    if (null != weekMap) {
                        Week startWeek = weekMap.get(courseScheduleExcelDomain.getStartWeek());
                        if (null == startWeek) {
                            sb.append(",没有找到对应的起始周信息");
                            validator = false;
                        } else {
                            schoolTimeTable.setStartWeek(startWeek);
                            schoolTimeTable.setStartWeekNo(startWeek.getNo());
                        }
                        Week endWeek = weekMap.get(courseScheduleExcelDomain.getEndWeek());
                        if (null == endWeek) {
                            sb.append(",没有找到对应的结束周信息");
                            validator = false;
                        } else {
                            schoolTimeTable.setEndWeek(endWeek);
                            schoolTimeTable.setEndWeekNo(endWeek.getNo());
                        }
                    } else {
                        sb.append(",没有找到对应的学周信息");
                        validator = false;
                    }
                }
                if (null != courseScheduleExcelDomain.getWeek() && Integer.MIN_VALUE != courseScheduleExcelDomain.getWeek()) {
                    if (courseScheduleExcelDomain.getWeek() < 1 || courseScheduleExcelDomain.getWeek() > 7) {
                        sb.append(",星期几应该是1-7的整数");
                        validator = false;
                    } else {
                        schoolTimeTable.setDayOfWeek(courseScheduleExcelDomain.getWeek());
                    }
                }
                if (null != courseScheduleExcelDomain.getStartPoriod() && Integer.MIN_VALUE != courseScheduleExcelDomain.getStartPoriod()) {
                    Period period = periodMap.get(courseScheduleExcelDomain.getStartPoriod());
                    if (null == period) {
                        sb.append(",没有找到对应的起始节信息");
                        validator = false;
                    } else {
                        schoolTimeTable.setPeriod(period);
                        schoolTimeTable.setPeriodNo(period.getNo());
                    }
                }
                if (null != courseScheduleExcelDomain.getPeriodNum() && Integer.MIN_VALUE != courseScheduleExcelDomain.getPeriodNum()) {
                    if (courseScheduleExcelDomain.getPeriodNum() <= 0 || courseScheduleExcelDomain.getPeriodNum() >= 20) {
                        sb.append(",持续节数应该是1-20的整数");
                        validator = false;
                    } else {
                        schoolTimeTable.setPeriodNum(courseScheduleExcelDomain.getPeriodNum());
                    }
                }
                if (!StringUtils.isEmpty(courseScheduleExcelDomain.getOneOrDouble())) {
                    if (SingleOrDouble.SINGLE.getStateDesc().equals(courseScheduleExcelDomain.getOneOrDouble())) {
                        schoolTimeTable.setSingleOrDouble(SingleOrDouble.SINGLE.getState());
                    } else if (SingleOrDouble.DOUBLE.getStateDesc().equals(courseScheduleExcelDomain.getOneOrDouble())) {
                        schoolTimeTable.setSingleOrDouble(SingleOrDouble.DOUBLE.getState());
                    }
                } else {
                    schoolTimeTable.setSingleOrDouble(SingleOrDouble.ALL.getState());
                }
                if (!StringUtils.isEmpty(courseScheduleExcelDomain.getClassesRoom())) {
                    schoolTimeTable.setClassroom(courseScheduleExcelDomain.getClassesRoom());
                }

                if (!StringUtils.isEmpty(courseScheduleExcelDomain.getTeachingClassCode())) {
                    List<SchoolTimeTable> schoolTimeTableList1 = teachingClassForSchoolTimeTablesMap.get(courseScheduleExcelDomain.getTeachingClassCode());
                    if (null == schoolTimeTableList1) {
                        schoolTimeTableList1 = new ArrayList<>();
                        teachingClassForSchoolTimeTablesMap.put(courseScheduleExcelDomain.getTeachingClassCode(), schoolTimeTableList1);
                    }
                    schoolTimeTableList1.add(schoolTimeTable);
                }
                if (null == schoolTimeTable.getTeachingClass()) {
                    sb.append(",没有匹配到合适的教学班信息");
                    validator = false;
                }

                if (null == schoolTimeTable.getStartWeek()) {
                    sb.append(",没有匹配到合适的起始周信息");
                    validator = false;
                }

                if (null == schoolTimeTable.getEndWeek()) {
                    sb.append(",没有匹配到合适的结束周信息");
                    validator = false;
                }

                if (null == schoolTimeTable.getPeriod()) {
                    sb.append(",没有匹配到合适的起始课程节信息");
                    validator = false;
                }
                if (null == schoolTimeTable.getPeriodNum()) {
                    sb.append(",持续节是必须录入的信息");
                    validator = false;
                }
                courseScheduleExcelDomain.setMsg(clearCacheAndContactMsg(sb, courseScheduleExcelDomain.getMsg()));
            }
        }
        return validator;
    }


    @Transactional(readOnly = true)
    public List<TeachingClassDomain> findTeachingClasssAndTeacherByIds(Set<Long> ids) {
        if (null != ids && ids.size() > 0) {
            List<TeachingClassDomain> list = findTeachingClasssAndCourseByIds(ids);
            Map<Long, TeachingClassDomain> teachingClassDomainMap = new HashMap<>();
            for (TeachingClassDomain d : list) {
                teachingClassDomainMap.put(d.getId(), d);
            }

            List<IdIdNameDomain> teachingclassTeacherList = teachingClassTeacherService.findTeacherByTeachingClassIds(ids);
            for (IdIdNameDomain d : teachingclassTeacherList) {
                TeachingClassDomain teachingClassDomain = teachingClassDomainMap.get(d.getLogicId());
                if (null != teachingClassDomain) {
                    if (null == teachingClassDomain.getTeacherIds()) {
                        teachingClassDomain.setTeacherIds(new ArrayList<>());
                    }
                    teachingClassDomain.getTeacherIds().add(d.getId());
                }
            }
            return list;
        }
        return new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public List<TeachingclassAndClasses> getAllTeachingClassesAndClasses(Long semesterId, Set<Long> classesIds) {
        List<TeachingclassAndClasses> list = new ArrayList<>();
        if (null == classesIds || classesIds.size() <= 0) {
            return list;
        }
        Semester semester = null;
        if (null != semesterId && semesterId > 0) {
            semester = semesterService.findById(semesterId);
        }
        if (null == semester) {
            return list;
        }
        List<TeachingclassAndClasses> list1 = teachingClassClassesService.findAllTeachingClass(classesIds, semester);
        if (null != list1 && list1.size() > 0) {
            list.addAll(list1);
        }
        list1 = teachingClassStudentsService.findTeachingClassIdNameByClassesAndSemester(classesIds, semester);
        if (null != list1 && list1.size() > 0) {
            list.addAll(list1);
        }
        return list;
    }


    public List<ClassDomain> getClassListByTeachingClassId(Long teachingClassId) {
        StringBuilder sql = new StringBuilder("select DISTINCT(u.CLASSES_ID) as id,(select `NAME` from `t_classes` c where DELETE_FLAG =0 and c.ID =u.CLASSES_ID) as name,tcs.SEMESTER_ID,tcs.ORG_ID from `t_user` u LEFT JOIN `t_teaching_class_students` tcs ON u.id = tcs.STUDENT_ID  where u.DELETE_FLAG =0 and tcs.TEACHING_CLASS_ID =" + teachingClassId);
        Query sq = em.createNativeQuery(sql.toString());
        List<Object> resultList = sq.getResultList();
        List<ClassDomain> list = new ArrayList<ClassDomain>();
        if (null != resultList) {
            for (Object obj : resultList) {
                Object[] d = (Object[]) obj;
                ClassDomain dto = new ClassDomain();
                if (null != d[0]) {
                    dto.setId(Long.valueOf(String.valueOf(d[0])));
                }
                if (null != d[1]) {
                    dto.setName(String.valueOf(d[1]));
                }
                if (null != d[2]) {
                    dto.setSemesterId(Long.valueOf(String.valueOf(d[2])));
                }
                if (null != d[3]) {
                    dto.setOrgId(Long.valueOf(String.valueOf(d[3])));
                }
                list.add(dto);
            }
        }
        return list;
    }
}