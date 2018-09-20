/**
 *
 */
package com.aizhixin.cloud.orgmanager.company.service;


import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.PageDomain;
import com.aizhixin.cloud.orgmanager.common.core.*;
import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.company.domain.ClassesDomain;
import com.aizhixin.cloud.orgmanager.company.domain.ClassesIdNameCollegeNameDomain;
import com.aizhixin.cloud.orgmanager.company.domain.excel.ClassesExcelDomain;
import com.aizhixin.cloud.orgmanager.company.entity.Classes;
import com.aizhixin.cloud.orgmanager.company.entity.College;
import com.aizhixin.cloud.orgmanager.company.entity.Professional;
import com.aizhixin.cloud.orgmanager.company.repository.ClassesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class ClassesService {
    @Autowired
    private EntityManager em;
    @Autowired
    private ClassesRepository classesRepository;
    @Autowired
    private ProfessionalService professionalService;
    @Autowired
    private CollegeService collegeService;
    @Autowired
    private UserService userService;
    @Autowired
    private ExcelBasedataHelper excelBasedataHelper;
    @Autowired
    private ClassesTeacherService classesTeacherService;
    @Autowired
    private BaseDataCacheService baseDataCacheService;
    @Autowired
    private SemesterService semesterService;

    /**
     * 保存实体
     *
     * @param classes
     * @return
     */
    public Classes save(Classes classes) {
        classes = classesRepository.save(classes);
        baseDataCacheService.cacheClasses(new ClassesDomain(classes));
        return classes;
    }

    public List<Classes> saveList(List<Classes> classeses) {
        classeses = classesRepository.save(classeses);
        List<ClassesDomain> cacheList = new ArrayList<>();
        for (Classes c : classeses) {
            cacheList.add(new ClassesDomain(c));
        }
        if (cacheList.size() > 0) {
            baseDataCacheService.cacheClasses(cacheList);
        }
        return classeses;
    }

    /**
     * 根据实体ID查询实体
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public Classes findById(Long id) {
        return classesRepository.findOne(id);
    }

    /**
     * count特定专业name值的正常数据
     *
     * @param professional
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    public Long countByName(Professional professional, String name) {
        return classesRepository.countByProfessionalAndNameAndDeleteFlag(professional, name, DataValidity.VALID.getState());
    }

    /**
     * count特定专业班级name值的正常数据，但是排除特定id（修改时用来排除自己）
     *
     * @param professional
     * @param name
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public Long countByNameAndIdNot(Professional professional, String name, Long id) {
        return classesRepository.countByProfessionalAndNameAndIdNotAndDeleteFlag(professional, name, id, DataValidity.VALID.getState());
    }

    /**
     * count特定学校code值的正常数据
     *
     * @param orgId
     * @param code
     * @return
     */
    @Transactional(readOnly = true)
    public Long countByCode(Long orgId, String code) {
        return classesRepository.countByOrgIdAndCodeAndDeleteFlag(orgId, code, DataValidity.VALID.getState());
    }

    /**
     * count特定学校班级code值的正常数据，但是排除特定id（修改时用来排除自己）
     *
     * @param orgId
     * @param code
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public Long countByCodeAndIdNot(Long orgId, String code, Long id) {
        return classesRepository.countByOrgIdAndCodeAndIdNotAndDeleteFlag(orgId, code, id, DataValidity.VALID.getState());
    }

    /**
     * 分页查询特定专业班级的ID和name
     *
     * @param pageable
     * @param professional
     * @return
     */
    @Transactional(readOnly = true)
    public Page<IdNameDomain> findIdName(Pageable pageable, Professional professional) {
        return classesRepository.findIdName(pageable, professional, DataValidity.VALID.getState());
    }

    /**
     * 分页查询特定专业班级的ID和name
     *
     * @param pageable
     * @param professional
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    public Page<IdNameDomain> findIdName(Pageable pageable, Professional professional, String name) {
        return classesRepository.findIdName(pageable, professional, name, DataValidity.VALID.getState());
    }

    /**
     * 分页查询特定专业学院班级的ID和name
     *
     * @param pageable
     * @param college
     * @return
     */
    @Transactional(readOnly = true)
    public Page<IdNameDomain> findIdName(Pageable pageable, College college) {
        return classesRepository.findIdName(pageable, college, DataValidity.VALID.getState());
    }

    /**
     * 分页查询特定专业学院班级的ID和name
     *
     * @param pageable
     * @param college
     * @return
     */
    @Transactional(readOnly = true)
    public Page<IdNameDomain> findIdName(Pageable pageable, College college, String name) {
        return classesRepository.findIdName(pageable, college, name, DataValidity.VALID.getState());
    }

    /**
     * 分页查询特定学校班级的ID和name
     *
     * @param pageable
     * @param orgId
     * @return
     */
    @Transactional(readOnly = true)
    public Page<IdNameDomain> findIdName(Pageable pageable, Long orgId) {
        return classesRepository.findIdName(pageable, orgId, DataValidity.VALID.getState());
    }

    /**
     * 分页查询特定学校班级的ID和name
     *
     * @param pageable
     * @param orgId
     * @return
     */
    @Transactional(readOnly = true)
    public Page<IdNameDomain> findIdName(Pageable pageable, Long orgId, String name) {
        return classesRepository.findIdName(pageable, orgId, name, DataValidity.VALID.getState());
    }
//	/**
//	 * 根据专业、班级name查询专业列表
//	 * @param pageable
//	 * @param professional
//	 * @param name
//	 * @return
//	 */
//	@Transactional(readOnly = true)
//	public Page<ClassesDomain> findName(Pageable pageable, Professional professional, String name) {
//		return classesRepository.findByProfessionalAndName(pageable, professional, DataValidity.VALID.getState(), name);
//	}
//	/**
//	 * 根据学校、班级name查询专业列表
//	 * @param pageable
//	 * @param orgId
//	 * @param name
//	 * @return
//	 */
//	@Transactional(readOnly = true)
//	public Page<ClassesDomain> findName(Pageable pageable, Long orgId, String name) {
//		return classesRepository.findByOrgIdAndName(pageable, orgId, DataValidity.VALID.getState(), name);
//	}
//	/**
//	 * 根据学院、班级name查询专业列表
//	 * @param pageable
//	 * @param college
//	 * @param name
//	 * @return
//	 */
//	@Transactional(readOnly = true)
//	public Page<ClassesDomain> findNameByCollege(Pageable pageable, College college, String name) {
//		return classesRepository.findByColleageAndName(pageable, college, DataValidity.VALID.getState(), name);
//	}
//	/**
//	 * 根据学院、班级name查询专业列表
//	 * @param pageable
//	 * @param orgId
//	 * @param name
//	 * @return
//	 */
//	@Transactional(readOnly = true)
//	public Page<ClassesDomain> findNameByOrgId(Pageable pageable, Long orgId, String name) {
//		return classesRepository.findByOrgIdAndName(pageable, orgId, DataValidity.VALID.getState(), name);
//	}
//	/**
//	 * 根据专业查询班级列表
//	 * @param pageable
//	 * @param professional
//	 * @return
//	 */
//	@Transactional(readOnly = true)
//	public Page<ClassesDomain> find(Pageable pageable, Professional professional) {
//		return classesRepository.findByProfessional(pageable, professional, DataValidity.VALID.getState());
//	}
//	/**
//	 * 根据学校查询班级列表
//	 * @param pageable
//	 * @param orgId
//	 * @return
//	 */
//	@Transactional(readOnly = true)
//	public Page<ClassesDomain> find(Pageable pageable, Long orgId) {
//		return classesRepository.findByOrgId(pageable, orgId, DataValidity.VALID.getState());
//	}
//	/**
//	 * 根据学院查询班级列表
//	 * @param pageable
//	 * @param college
//	 * @return
//	 */
//	@Transactional(readOnly = true)
//	public Page<ClassesDomain> find(Pageable pageable, College college) {
//		return classesRepository.findByColleage(pageable, college, DataValidity.VALID.getState());
//	}

    @Transactional(readOnly = true)
    public Long countByProfessional(Professional professional) {
        return classesRepository.countByProfessionalAndDeleteFlag(professional, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<Classes> findByIds(Set<Long> ids) {
        return classesRepository.findByIdIn(ids);
    }

    public List<Classes> saveAll(List<Classes> classes) {
        return classesRepository.save(classes);
    }

    @Transactional(readOnly = true)
    public List<Classes> findByCodes(Set<String> codes) {
        return classesRepository.findByCodeIn(codes);
    }

    @Transactional(readOnly = true)
    public List<ClassesIdNameCollegeNameDomain> findNameAndCollegeNameByIdIn(Set<Long> ids) {
        return classesRepository.findNameAndCollegeNameByIdIn(ids);
    }

    @Transactional(readOnly = true)
    public List<String> findCodesByOrgIdAndCodes(Long orgId, Set<String> codes) {
        return classesRepository.findCodesByOrgIdAndCodeInAndDeleteFlag(orgId, codes, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<String> findNamesByOrgIdAndCodes(Long orgId, Set<String> names) {
        return classesRepository.findNamesByOrgIdAndCodeInAndDeleteFlag(orgId, names, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<Classes> findByOrgIdAndCodes(Long orgId, Set<String> codes) {
        return classesRepository.findByOrgIdAndCodeInAndDeleteFlag(orgId, codes, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<Classes> findByOrgIdAndNames(Long orgId, Set<String> names) {
        return classesRepository.findByOrgIdAndNameInAndDeleteFlag(orgId, names, DataValidity.VALID.getState());
    }

    public void updateNewCollegeByProfesional(College newCollege, Professional professional) {
        classesRepository.updateNewCollegeByProfesional(professional, newCollege);
    }

    @Transactional(readOnly = true)
    public List<Classes> findByOrgId(Long orgId) {
        return classesRepository.findByOrgIdAndDeleteFlag(orgId, DataValidity.VALID.getState());
    }
    //*************************************************************以下部分处理页面调用逻辑**********************************************************************//

    /**
     * 班级信息保存
     *
     * @param cd
     * @return
     */
    public Classes save(ClassesDomain cd) {
        Classes classes = new Classes();
        Professional professional = professionalService.findById(cd.getProfessionalId());
        if (null == professional) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据专业ID[" + cd.getProfessionalId() + "]没有查找到对应的专业信息");
        }
        if (StringUtils.isEmpty(cd.getTeachingYear())) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "学年是必须的");
        }
        classes.setInSchoolDate(cd.getInSchoolDate());
        classes.setOutSchoolDate(cd.getOutSchoolDate());
        if (null == cd.getSchoolStatus() || 0 == cd.getSchoolStatus() || SchoolStatus.IN_SCHOOL.getState().intValue() == cd.getSchoolStatus()) {
            classes.setSchoolStatus(SchoolStatus.IN_SCHOOL.getState());
        } else {
            classes.setSchoolStatus(SchoolStatus.OUT_SCHOOL.getState());
        }
        classes.setTeachingYear(cd.getTeachingYear());
        classes.setProfessional(professional);
        classes.setCollege(professional.getCollege());
        classes.setOrgId(professional.getOrgId());
        Long c = countByName(professional, cd.getName());
        if (c > 0) {
            throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "班级名称[" + cd.getName() + "]已经存在");
        }
        if (!StringUtils.isEmpty(cd.getCode())) {
            c = countByCode(professional.getOrgId(), cd.getCode());
            if (c > 0) {
                throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "班级编码[" + cd.getCode() + "]已经存在");
            }
            classes.setCode(cd.getCode());
        }
        classes.setName(cd.getName());
        classes.setSchoolStatus(SchoolStatus.IN_SCHOOL.getState());

        //学制
        classes.setSchoolingLength(cd.getSchoolingLength());

        classes.setCreatedBy(cd.getUserId());
        classes.setLastModifiedBy(cd.getUserId());
        return save(classes);
    }

    /**
     * 班级信息修改
     *
     * @param cd
     * @return
     */
    public Classes update(ClassesDomain cd) {
        if (null == cd.getId() || cd.getId() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "ID是必须的");
        }
        Classes classes = findById(cd.getId());
        if (null == classes) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "ID[" + cd.getId() + "]对应的班级信息不存在");
        }
        Professional professional = professionalService.findById(cd.getProfessionalId());
        if (null == professional) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据专业ID[" + cd.getProfessionalId() + "]没有查找到对应的专业信息");
        }
        if (professional.getId().longValue() != classes.getProfessional().getId().longValue()) {
            classes.setProfessional(professional);
            classes.setCollege(professional.getCollege());
            classes.setOrgId(professional.getOrgId());

            userService.updateProfesionalByClasses(classes, professional);
            if (null != professional.getCollege()) {
                userService.updateCollgeByClasses(classes, professional.getCollege());
            }
        }
        classes.setInSchoolDate(cd.getInSchoolDate());
        classes.setOutSchoolDate(cd.getOutSchoolDate());
        if (null == cd.getSchoolStatus() || 0 == cd.getSchoolStatus() || SchoolStatus.IN_SCHOOL.getState().intValue() == cd.getSchoolStatus()) {
            classes.setSchoolStatus(SchoolStatus.IN_SCHOOL.getState());
        } else {
            classes.setSchoolStatus(SchoolStatus.OUT_SCHOOL.getState());
        }
        classes.setTeachingYear(cd.getTeachingYear());
        //验证name
        Long c = countByNameAndIdNot(professional, cd.getName(), cd.getId());
        if (c > 0) {
            throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "班级名称[" + cd.getName() + "]已经存在");
        }
        if (!StringUtils.isEmpty(cd.getCode())) {
            c = countByCodeAndIdNot(professional.getOrgId(), cd.getCode(), cd.getId());
            if (c > 0) {
                throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "班级编码[" + cd.getCode() + "]已经存在");
            }
            classes.setCode(cd.getCode());
        }
        classes.setName(cd.getName());
        //学制
        classes.setSchoolingLength(cd.getSchoolingLength());

        classes.setLastModifiedBy(cd.getUserId());
        classes.setLastModifiedDate(new Date());
        save(classes);
        return classes;
    }

    /**
     * 班级信息删除
     *
     * @param userId
     * @param id
     */
    public void delete(Long userId, Long id) {
        if (null == id || id <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "ID是必须的");
        }
        Classes classes = findById(id);
        if (null == classes) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "ID[" + id + "]对应的班级信息不存在");
        }
        long uc = userService.countByClasses(classes);
        if (uc > 0) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "班级下有用户数据存在");
        }
        classes.setDeleteFlag(DataValidity.INVALID.getState());
        classes.setLastModifiedBy(userId);
        classes.setLastModifiedDate(new Date());
        save(classes);
    }

    public void deleteAll(Long userId, List<Long> ids) {
        if (null != ids && !ids.isEmpty()) {
            for (Long id : ids) {
                delete(userId, id);
            }
        }
    }

    /**
     * 获取班级详细信息
     *
     * @param id
     * @return
     */
    public ClassesDomain get(Long id) {
        ClassesDomain d = null;
        d = baseDataCacheService.readClasses(id);
        if (null != d) {
            return d;
        } else {
            d = new ClassesDomain();
        }
        Classes classes = findById(id);
        if (null != classes) {
            d.setId(classes.getId());
            d.setName(classes.getName());
            d.setCode(classes.getCode());
            d.setInSchoolDate(classes.getInSchoolDate());
            d.setOutSchoolDate(classes.getOutSchoolDate());
            d.setCreatedDate(classes.getCreatedDate());
            d.setSchoolStatus(classes.getSchoolStatus());
            d.setTeachingYear(classes.getTeachingYear());
            d.setSchoolingLength(classes.getSchoolingLength());
            if (null != classes.getProfessional()) {
                d.setProfessionalId(classes.getProfessional().getId());
                d.setProfessionalName(classes.getProfessional().getName());
            }
            if (null != classes.getCollege()) {
                d.setCollegeId(classes.getCollege().getId());
                d.setCollegeName(classes.getCollege().getName());
            }
        }
        return d;
    }

    /**
     * 班级查询界面修改
     *
     * @param pageable
     * @param orgId
     * @param collegeId
     * @param professionalId
     * @param teachingYear
     * @param name
     * @param teacherName
     * @return
     */
    @Transactional(readOnly = true)
    public PageData<ClassesDomain> queryList(Pageable pageable, Long orgId, Long collegeId, Long professionalId, String teachingYear, String name, String teacherName) {
        PageData<ClassesDomain> pageData = new PageData<>();
        pageData.getPage().setPageSize(pageable.getPageSize());
        pageData.getPage().setPageNumber(pageable.getPageNumber() + 1);
        if (null == orgId || orgId <= 0) {
            return pageData;
        }
        Professional professional = null;
        if (null != professionalId && professionalId > 0) {
            professional = professionalService.findById(professionalId);
        }
        College college = null;
        if (null == professional) {
            if (null != collegeId && collegeId > 0) {
                college = collegeService.findById(collegeId);
            }
        }
        Map<String, Object> condition = new HashMap<>();
        condition.put("deleteFlag", DataValidity.VALID.getState());
        StringBuilder chql;
        StringBuilder hql;
        if (!StringUtils.isEmpty(teacherName)) {
            chql = new StringBuilder("select count(distinct c.classes.id) from com.aizhixin.cloud.orgmanager.company.entity.ClassesTeacher c where c.classes.deleteFlag = :deleteFlag and c.teacher.name like :teacher");
            hql = new StringBuilder("select distinct new com.aizhixin.cloud.orgmanager.company.domain.ClassesDomain(c.classes.id, c.classes.name, c.classes.code, c.classes.teachingYear, c.classes.college.id, c.classes.college.name, c.classes.professional.id, c.classes.professional.name, c.classes.createdDate, c.classes.schoolStatus, c.classes.inSchoolDate, c.classes.outSchoolDate, c.classes.schoolingLength) from com.aizhixin.cloud.orgmanager.company.entity.ClassesTeacher c where c.classes.deleteFlag = :deleteFlag and c.teacher.name like :teacher");

            condition.put("teacher", "%" + teacherName + "%");

            if (!StringUtils.isEmpty(name)) {
                hql.append(" and (c.classes.name like :name or c.classes.code like :name)");
                chql.append(" and (c.classes.name like :name or c.classes.code like :name)");
                condition.put("name", "%" + name + "%");
            }
            if (!StringUtils.isEmpty(teachingYear)) {
                hql.append(" and c.classes.teachingYear = :teachingYear");
                chql.append(" and c.classes.teachingYear = :teachingYear");
                condition.put("teachingYear", teachingYear);
            }
            if (null != professional) {
                hql.append(" and c.classes.professional = :professional");
                chql.append(" and c.classes.professional = :professional");
                condition.put("professional", professional);
            } else if (null != college) {
                hql.append(" and c.classes.college = :college");
                chql.append(" and c.classes.college = :college");
                condition.put("college", college);
            } else {
                hql.append(" and c.classes.orgId = :orgId");
                chql.append(" and c.classes.orgId = :orgId");
                condition.put("orgId", orgId);
            }
            hql.append(" order by c.classes.id DESC");
        } else {
            chql = new StringBuilder("select count(c.id) from com.aizhixin.cloud.orgmanager.company.entity.Classes c inner join c.professional inner join c.college where c.deleteFlag = :deleteFlag");
            hql = new StringBuilder("select new com.aizhixin.cloud.orgmanager.company.domain.ClassesDomain(c.id, c.name, c.code, c.teachingYear, c.college.id, c.college.name, c.professional.id, c.professional.name, c.createdDate, c.schoolStatus, c.inSchoolDate, c.outSchoolDate, c.schoolingLength) from com.aizhixin.cloud.orgmanager.company.entity.Classes c where c.deleteFlag = :deleteFlag");

            if (!StringUtils.isEmpty(name)) {
                hql.append(" and (c.name like :name or c.code like :name)");
                chql.append(" and (c.name like :name or c.code like :name)");
                condition.put("name", "%" + name + "%");
            }
            if (!StringUtils.isEmpty(teachingYear)) {
                hql.append(" and c.teachingYear = :teachingYear");
                chql.append(" and c.teachingYear = :teachingYear");
                condition.put("teachingYear", teachingYear);
            }
            if (null != professional) {
                hql.append(" and c.professional = :professional");
                chql.append(" and c.professional = :professional");
                condition.put("professional", professional);
            } else if (null != college) {
                hql.append(" and c.college = :college");
                chql.append(" and c.college = :college");
                condition.put("college", college);
            } else {
                hql.append(" and c.orgId = :orgId");
                chql.append(" and c.orgId = :orgId");
                condition.put("orgId", orgId);
            }
            hql.append(" order by c.id DESC");
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
        TypedQuery<ClassesDomain> tq = em.createQuery(hql.toString(), ClassesDomain.class);
        for (Map.Entry<String, Object> e : condition.entrySet()) {
            tq.setParameter(e.getKey(), e.getValue());
        }
        tq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        tq.setMaxResults(pageable.getPageSize());
        pageData.setData(tq.getResultList());
        pageData.getPage().setTotalPages(PageUtil.cacalatePagesize(count, pageData.getPage().getPageSize()));

        Set<Long> classesIds = new HashSet<>();
        Map<Long, ClassesDomain> classesDomainMap = new HashMap<>();
        for (ClassesDomain d : pageData.getData()) {
            classesIds.add(d.getId());
            classesDomainMap.put(d.getId(), d);
        }
        if (classesIds.size() > 0) {
            List<IdNameDomain> list = classesTeacherService.findTeacherNameByClassesIds(classesIds);
            for (IdNameDomain d : list) {
                ClassesDomain c = classesDomainMap.get(d.getId());
                if (null != c) {
                    if (null == c.getTeachers()) {
                        c.setTeachers(d.getName());
                    } else {
                        c.setTeachers(c.getTeachers() + "," + d.getName());
                    }
                }
            }
        }
        return pageData;
    }

    /**
     * 根据专业、班级name查询专业信息
     *
     * @param pageable
     * @param orgId
     * @param professionalId
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    public PageData<ClassesDomain> queryList(Pageable pageable, Long orgId, Long professionalId, String name, String teachingYear) {
        return queryList(pageable, orgId, null, professionalId, teachingYear, name, null);
    }

    /**
     * 根据专业、班级name查询专业信息
     *
     * @param pageable
     * @param orgId
     * @param collegeId
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    public PageData<ClassesDomain> queryListCollection(Pageable pageable, Long orgId, Long collegeId, String name) {
        return queryList(pageable, orgId, collegeId, null, null, name, null);
    }

    /**
     * 根据专业查询班级列表，仅查询ID、Name字段，可用于下拉列表
     *
     * @param r
     * @param professionalId
     * @param name
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> dropList(Map<String, Object> r, Long professionalId, String name, Pageable pageable) {
        PageDomain p = new PageDomain();
        r.put(ApiReturnConstants.PAGE, p);
        p.setPageNumber(pageable.getPageNumber());
        p.setPageSize(pageable.getPageSize());

        Professional professionnal = professionalService.findById(professionalId);
        if (null == professionnal) {
            return r;
        }

        Page<IdNameDomain> page = null;
        if (org.springframework.util.StringUtils.isEmpty(name)) {
            page = findIdName(pageable, professionnal);
        } else {
            page = findIdName(pageable, professionnal, name);
        }

        p.setTotalElements(page.getTotalElements());
        p.setTotalPages(page.getTotalPages());

        r.put(ApiReturnConstants.DATA, page.getContent());
        return r;
    }

    /**
     * 根据学院查询班级列表，仅查询ID、Name字段，可用于下拉列表
     *
     * @param r
     * @param collegeId
     * @param name      班级名称
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> dropListCollege(Map<String, Object> r, Long collegeId, String name, Pageable pageable) {
        PageDomain p = new PageDomain();
        r.put(ApiReturnConstants.PAGE, p);
        p.setPageNumber(pageable.getPageNumber());
        p.setPageSize(pageable.getPageSize());

        College college = collegeService.findById(collegeId);
        if (null == college) {
            return r;
        }

        Page<IdNameDomain> page = null;
        if (org.springframework.util.StringUtils.isEmpty(name)) {
            page = findIdName(pageable, college);
        } else {
            page = findIdName(pageable, college, name);
        }

        p.setTotalElements(page.getTotalElements());
        p.setTotalPages(page.getTotalPages());

        r.put(ApiReturnConstants.DATA, page.getContent());
        return r;
    }

    /**
     * 根据学院查询班级列表，仅查询ID、Name字段，可用于下拉列表
     *
     * @param r
     * @param orgId
     * @param name     班级名称
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> dropListOrgId(Map<String, Object> r, Long orgId, String name, Pageable pageable) {
        PageDomain p = new PageDomain();
        r.put(ApiReturnConstants.PAGE, p);
        p.setPageNumber(pageable.getPageNumber());
        p.setPageSize(pageable.getPageSize());

        Page<IdNameDomain> page = null;
        if (org.springframework.util.StringUtils.isEmpty(name)) {
            page = findIdName(pageable, orgId);
        } else {
            page = findIdName(pageable, orgId, name);
        }
        p.setTotalElements(page.getTotalElements());
        p.setTotalPages(page.getTotalPages());
        r.put(ApiReturnConstants.DATA, page.getContent());
        return r;
    }


//	public List<ClassesDomain> save(List<ClassesDomain> cds) {
//		Set<String> codes = new HashSet<>();
//		List<Classes> list = new ArrayList<>();
//		for (ClassesDomain d : cds) {
//			if (null != d.getProfessionalCode()) {
//				codes.add(d.getProfessionalCode());
//			}
//		}
//		Map<String, Professional> cache = new HashMap<>();
//		if (codes.size() > 0) {
//			List<Professional> plist = professionalService.findByCodeIn(codes);
//			for (Professional p : plist) {
//				cache.put(p.getCode(), p);
//			}
//			for(ClassesDomain d : cds) {
//				Classes c = new Classes();
//				c.setCode(d.getCode());
//				c.setName(d.getName());
//				c.setSchoolStatus(SchoolStatus.IN_SCHOOL.getState());
//				Professional p = cache.get(d.getProfessionalCode());
//				c.setProfessional(p);
//				if (null != p) {
//					c.setCollege(p.getCollege());
//					c.setOrgId(p.getOrgId());
//				}
//				c.setCreatedBy(d.getUserId());
//				c.setLastModifiedBy(d.getUserId());
//				list.add(c);
//			}
//		} else {
//			for(ClassesDomain d : cds) {
//				Classes c = new Classes();
//				c.setCode(d.getCode());
//				c.setName(d.getName());
//				c.setCreatedBy(d.getUserId());
//				c.setLastModifiedBy(d.getUserId());
//				list.add(c);
//			}
//		}
//		List<ClassesDomain> outList = new ArrayList<>();
//		setOutClasssList(outList, list);
//		return outList;
//	}
//
//	private void setOutClasssList(List<ClassesDomain> outList, List<Classes> list) {
//		if (list.size() > 0) {
//			list = saveAll(list);
//			for (Classes c : list) {
//				ClassesDomain d = new ClassesDomain();
//				d.setId(c.getId());
//				d.setCode(c.getCode());
//				d.setName(c.getName());
//				if (null != c.getProfessional()) {
//					d.setProfessionalId(c.getProfessional().getId());
//					d.setProfessionalCode(c.getProfessional().getCode());
//				}
//				outList.add(d);
//			}
//		}
//	}
//
//	public List<ClassesDomain> update(List<ClassesDomain> classesDomain) {
//		Set<String> codes = new HashSet<>();
//		Set<Long> ids = new HashSet<>();
//		List<Classes> list = null;
//		Map<Long, ClassesDomain> ccs = new HashMap<>();
//		for (ClassesDomain d : classesDomain) {
//			if (null != d.getProfessionalCode()) {
//				codes.add(d.getProfessionalCode());
//				ids.add(d.getId());
//				ccs.put(d.getId(), d);
//			}
//		}
//		Map<String, Professional> cache = new HashMap<>();
//		List<Professional> plist = professionalService.findByCodeIn(codes);
//		for (Professional p : plist) {
//			cache.put(p.getCode(), p);
//		}
//		list = findByIds(ids);
//		for (Classes c : list) {
//			ClassesDomain d = ccs.get(c.getId());
//			if (null != d) {
//				c.setCode(d.getCode());
//				c.setName(d.getName());
//				Professional p = cache.get(d.getProfessionalCode());
//				c.setProfessional(p);
//				if (null != p) {
//					c.setCollege(p.getCollege());
//					c.setOrgId(p.getOrgId());
//				}
//			}
//			c.setLastModifiedBy(d.getUserId());
//		}
//		list = saveAll(list);
//		List<ClassesDomain> outList = new ArrayList<>();
//		setOutClasssList(outList, list);
//		return outList;
//	}
//
//	public List<ClassesDomain> delete(List<ClassesDomain> pds) {
//		List<Classes> ds = null;
//		Set<Long> ids = new HashSet<>();
//		Long userId = 0L;
//		for (ClassesDomain d : pds) {
//			ids.add(d.getId());
//			if(userId <= 0) {
//				userId = d.getUserId();
//			}
//		}
//		if(ids.size() > 0) {
//			ds = findByIds(ids);
//			for (Classes c : ds) {
//				c.setDeleteFlag(DataValidity.INVALID.getState());
//				c.setLastModifiedBy(userId);
//				c.setSchoolStatus(SchoolStatus.OUT_SCHOOL.getState());
//			}
//			if (ds.size() > 0) {
//				ds = saveAll(ds);
//			}
//		}
//		List<ClassesDomain> outList = new ArrayList<>();
//		setOutClasssList(outList, ds);
//		return outList;
//	}

    public List<ClassesExcelDomain> importClassesData(Set<Integer> failSign, Long orgId, MultipartFile file, Long userId) {
        if (null == orgId || orgId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "orgId是必须的");
        }
        if (null == userId || userId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "userId是必须的");
        }
        List<ClassesExcelDomain> excelDatas = excelBasedataHelper.readClassesFromInputStream(file);
        if (null == excelDatas || excelDatas.size() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "没有读取到任何数据");
        }
        Set<String> codeSet = new HashSet<>();
        Set<String> nameSet = new HashSet<>();
        Set<String> professionalCodeSet = new HashSet<>();
        Set<String> professionalNameSet = new HashSet<>();

        boolean hasError = false;
        String msg = null;
        for (ClassesExcelDomain d : excelDatas) {
            if (!StringUtils.isEmpty(d.getCode())) {
                if (codeSet.contains(d.getCode())) {
                    msg = "班级编码在此Excel中已经存在";
                    d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                    hasError = true;
                }
                codeSet.add(d.getCode());
            }
            if (!StringUtils.isEmpty(d.getName())) {
                if (nameSet.contains(d.getName())) {
                    msg = "班级名称在此Excel中已经存在";
                    d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                    hasError = true;
                }
                nameSet.add(d.getName());
            }
            if (StringUtils.isEmpty(d.getCode()) && StringUtils.isEmpty(d.getName())) {
                msg = "班级信息是必须的";
                d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                hasError = true;
            }
            if (!StringUtils.isEmpty(d.getProfessionalCode())) {
                professionalCodeSet.add(d.getProfessionalCode());
            }
            if (!StringUtils.isEmpty(d.getProfessionalName())) {
                professionalNameSet.add(d.getProfessionalName());
            }
            if (StringUtils.isEmpty(d.getProfessionalCode()) && StringUtils.isEmpty(d.getProfessionalName())) {
                msg = "专业是必须的";
                d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                hasError = true;
            }
            if (StringUtils.isEmpty(d.getGrade()) && StringUtils.isEmpty(d.getGrade())) {
                msg = "年级是必须的";
                d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                hasError = true;
            }
        }
        Map<String, Professional> professionalNameMap = new HashMap<>();
        Map<String, Professional> professionalCodeMap = new HashMap<>();
        //如果专业的名称有有效值的数量多于编码的数量，则采用名称验证，否则采用学院编码来验证数据
        if (professionalNameSet.size() > 0) {
            List<Professional> professionalList = professionalService.findByOrgIdAndNameIn(orgId, professionalNameSet);
            for (Professional p : professionalList) {
                professionalNameMap.put(p.getName(), p);
            }
        }
        if (professionalCodeSet.size() > 0) {
            List<Professional> professionalList = professionalService.findByOrgIdAndCodeIn(orgId, professionalCodeSet);
            for (Professional p : professionalList) {
                professionalCodeMap.put(p.getCode(), p);
            }
        }

        Map<String, Classes> codeClassesSaveMap = new HashMap<>();
        if (codeSet.size() > 0) {
            List<Classes> list = findByOrgIdAndCodes(orgId, codeSet);
            for (Classes classes : list) {
                codeClassesSaveMap.put(classes.getCode(), classes);
            }
        }
        List<String> names = null;
        if (nameSet.size() > 0) {
            names = findNamesByOrgIdAndCodes(orgId, nameSet);
        }
        //二次验证
        List<Classes> data = new ArrayList<>();
        Map<String, ClassesExcelDomain> cache = new HashMap<>();//因为code不是必须的，所以按照名称匹配保存后的数据的ID
        for (ClassesExcelDomain d : excelDatas) {
            Classes cs = null;
            if (!StringUtils.isEmpty(d.getCode())) {
                cs = codeClassesSaveMap.get(d.getCode());
            }
            if (StringUtils.isEmpty(d.getName())) {
                msg = "班级名称是必须的";
                d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                hasError = true;
            } else {
                cache.put(d.getName(), d);
                if (null != names && names.contains(d.getName())) {
                    if (null == cs) {
                        msg = "班级名称已经存在";
                        d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                        hasError = true;
                    } else {
                        if (!d.getName().equals(cs.getName()) && null != names && names.contains(d.getName())) {
                            msg = "班级名称已经存在";
                            d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                            hasError = true;
                        }
                    }
                }
            }
            Classes c = new Classes();
            c.setCode(d.getCode());
            c.setName(d.getName());
            c.setTeachingYear(d.getGrade());
            if (professionalCodeMap.size() > 0 && !StringUtils.isEmpty(d.getProfessionalCode())) {//优先采用编码来关联数据
                Professional p = professionalCodeMap.get(d.getProfessionalCode());
                if (null != p) {
                    c.setProfessional(p);
                    c.setCollege(p.getCollege());
                }
            }
            if (null == c.getProfessional()) {
                if (professionalNameMap.size() > 0 && !StringUtils.isEmpty(d.getProfessionalName())) {
                    Professional p = professionalNameMap.get(d.getProfessionalName());
                    if (null != p) {
                        c.setProfessional(p);
                        c.setCollege(p.getCollege());
                    }
                }
            }
            if (null == c.getProfessional()) {
                msg = "根据专业名称和编码没有找到对应的专业信息";
                d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                hasError = true;
            }

            c.setOrgId(orgId);
            c.setCreatedBy(userId);
            c.setLastModifiedBy(userId);
            data.add(c);
        }

        if (hasError) {
            failSign.add(1);
            return excelDatas;
        }
        List<Classes> dataSave = new ArrayList<>();
        for (Classes c : data) {
            Classes classes = codeClassesSaveMap.get(c.getCode());
            if (null != classes) {
                classes.setCollege(c.getCollege());
                classes.setProfessional(c.getProfessional());
                classes.setName(c.getName());

                dataSave.add(classes);
            } else {
                dataSave.add(c);
            }
        }
        dataSave = saveList(dataSave);
        for (Classes c : dataSave) {//回填已经保存的数据的ID
            ClassesExcelDomain d = cache.get(c.getName());
            if (null != d) {
                d.setId(c.getId());
            }
        }
        return excelDatas;
    }
}
