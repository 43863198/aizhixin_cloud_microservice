package com.aizhixin.cloud.orgmanager.company.service;

import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.ApiReturnConstants;
import com.aizhixin.cloud.orgmanager.common.core.DataValidity;
import com.aizhixin.cloud.orgmanager.common.core.ErrorCode;
import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.common.util.RoleConfig;
import com.aizhixin.cloud.orgmanager.company.domain.ClassTeacherDomain;
import com.aizhixin.cloud.orgmanager.company.domain.TeacherDomain;
import com.aizhixin.cloud.orgmanager.company.domain.UserDomain;
import com.aizhixin.cloud.orgmanager.company.domain.message.ClassesTeacherBO;
import com.aizhixin.cloud.orgmanager.company.entity.Classes;
import com.aizhixin.cloud.orgmanager.company.entity.ClassesTeacher;
import com.aizhixin.cloud.orgmanager.company.entity.User;
import com.aizhixin.cloud.orgmanager.company.repository.ClassesTeacherRepository;
import com.aizhixin.cloud.orgmanager.training.core.GroupStatusConstants;
import com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO;
import com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupListInfoDTO;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.util.*;

/**
 * 班主任相关操作 Created by zhen.pan on 2017/4/18.
 */
@Component
@Transactional
public class ClassesTeacherService {
    @Autowired
    private ClassesTeacherRepository classesTeacherRepository;
    @Autowired
    private ClassesService classesService;
    @Autowired
    private UserService userService;
    @Autowired
    private BaseDataCacheService baseDataCacheService;
    @Autowired
    private EntityManager em;
//    @Autowired
//    private MessageSerivice messageSerivice;

    /**
     * 保存实体
     * 
     * @param classesTeacher
     * @return
     */
    public ClassesTeacher save(ClassesTeacher classesTeacher) {
        return classesTeacherRepository.save(classesTeacher);
    }

    public void save(List<ClassesTeacher> datas) {
        classesTeacherRepository.save(datas);
    }

    /**
     * 根据班级和教师ID列表查询老师是否重复指定为班主任
     * 
     * @param classes
     * @param teacherIds
     * @return
     */
    @Transactional(readOnly = true)
    public Long countByClassesAndTeacherIds(Classes classes, Set<Long> teacherIds) {
        return classesTeacherRepository.countByClassesAndTeacher_idIn(classes, teacherIds);
    }

    public void delete(ClassesTeacher classesTeacher) {
        classesTeacherRepository.delete(classesTeacher);
    }

    @Transactional(readOnly = true)
    public List<TeacherDomain> findTeacherByClass(Classes classes) {
        return classesTeacherRepository.findTeacherByClasses(classes, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<ClassesTeacher> findByClass(Classes classes) {
        return classesTeacherRepository.findByClasses(classes);
    }

    @Transactional(readOnly = true)
    public List<IdNameDomain> findClassesIdNameByTeacher(User teacher) {
        return classesTeacherRepository.findClassesIdNameByTeacher(teacher, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<Classes> findClassesByTeacher(User teacher) {
        return classesTeacherRepository.findClassesByTeacher(teacher, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public long countByTeacher(User teacher) {
        return classesTeacherRepository.countByTeacher(teacher);
    }

    @Transactional(readOnly = true)
    public List<IdNameDomain> findTeacherNameByClassesIds(Set<Long> classesIds) {
        return classesTeacherRepository.findTeacherNameByClassesIds(classesIds, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<TeacherDomain> findTeacherByClassesId(Long classesId) {
        return classesTeacherRepository.findTeacherByClassesId(classesId, DataValidity.VALID.getState());
    }

    public int deleteByClasses(Classes classes){
        return classesTeacherRepository.deleteByClasses(classes);
    }
    
    @Transactional(readOnly = true)
    public List<ClassesTeacher> findByOrgIdIsNull() {
        return classesTeacherRepository.findByOrgIdIsNull();
    }
    
    @Transactional(readOnly = true)
    public Page<ClassTeacherDomain> findTeacherPage(Pageable pageable,Long orgId) {
        return classesTeacherRepository.findTeacherPage(pageable,orgId);
    }
    
    @Transactional(readOnly = true)
    public Page<ClassTeacherDomain> findTeacherPageByKeywords(Pageable pageable,String name,String jobNumber,Long orgId) {
        return classesTeacherRepository.findTeacherPageByKeywords(pageable, name, jobNumber,orgId);
    }

    // *************************************************************以下部分处理页面调用逻辑**********************************************************************//

    /**
     * 初始化辅导员所在机构id(用于实践统计)
     */
    public void initOrgId(){
    	List<ClassesTeacher> list = findByOrgIdIsNull();
    	if(null != list && !list.isEmpty()){
    		for(ClassesTeacher cTeacher : list){
    			if(null != cTeacher.getClasses()){
	    			Classes classes = classesService.findById(cTeacher.getClasses().getId());
	    			if(null != classes){
	    				cTeacher.setOrgId(classes.getOrgId());
	    			}
    			}
    		}
    		save(list);
    	}
    }
    
    
    /**
     * 给班级添加任意多个老师作为班主任
     * 
     * @param classesId
     * @param teacherIds
     * @return
     */
    public void save(Long classesId, Set<Long> teacherIds) {
        if (null == teacherIds || teacherIds.size() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "老师ID是必须的");
        }
        Classes classes = classesService.findById(classesId);
        if (null == classes) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据班级Id[" + classesId + "]查找不到对应的班级信息");
        }
        long cts = countByClassesAndTeacherIds(classes, teacherIds);
        if (cts > 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "有部分老师已经为该班的班主任了");
        }
        List<User> teachers = userService.findByIds(teacherIds);
        if (null == teachers || teachers.size() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据老师Id[" + teacherIds + "]查找不到对应的老师信息");
        }
        List<ClassesTeacher> datas = new ArrayList<>();
        List<ClassesTeacherBO> msgs = new ArrayList<>();
        for (User teacher : teachers) {
            ClassesTeacher classesTeacher = new ClassesTeacher();
            classesTeacher.setClasses(classes);
            classesTeacher.setTeacher(teacher);
            classesTeacher.setOrgId(classes.getOrgId());
            datas.add(classesTeacher);
            ClassesTeacherBO classesTeacherBO = new ClassesTeacherBO();
            classesTeacherBO.setOperator("add");
            classesTeacherBO.setClassId(classes.getId());
            classesTeacherBO.setTeacherId(teacher.getId());
            msgs.add(classesTeacherBO);
        }
//        if (!msgs.isEmpty()) {
//            messageSerivice.sendClassTeacherChangeMsg(msgs);
//        }
        save(datas);

        // 更新cache
        for (User teacher : teachers) {
            UserDomain d = baseDataCacheService.readUser(teacher.getId());
            if (null != d) {
                d.addRole(RoleConfig.CLASSES_MASTER);
                baseDataCacheService.cacheUser(d);
            }
        }
    }

    public void delete(Long classesId, Set<Long> teacherIds) {
        if (null == teacherIds || teacherIds.size() <= 0) {
            throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "老师ID是必须的");
        }
        Classes classes = classesService.findById(classesId);
        List<ClassesTeacher> datas = findByClass(classes);
        List<ClassesTeacherBO> msgs = new ArrayList<>();
        for (ClassesTeacher classesTeacher : datas) {
            if (null != classesTeacher.getTeacher()) {
                if (teacherIds.contains(classesTeacher.getTeacher().getId())) {
                    delete(classesTeacher);

                    ClassesTeacherBO classesTeacherBO = new ClassesTeacherBO();
                    classesTeacherBO.setOperator("delete");
                    classesTeacherBO.setClassId(classes.getId());
                    classesTeacherBO.setTeacherId(classesTeacher.getTeacher().getId());
                    msgs.add(classesTeacherBO);
                }
            }
        }

//        if (!msgs.isEmpty()) {
//            messageSerivice.sendClassTeacherChangeMsg(msgs);
//        }

        // 更新cache
        for (ClassesTeacher classesTeacher : datas) {
            UserDomain d = baseDataCacheService.readUser(classesTeacher.getTeacher().getId());
            if (null != d) {
                long cs = countByTeacher(classesTeacher.getTeacher().getId());
                if (cs <= 0) {
                    d.deleteRole(RoleConfig.CLASSES_MASTER);
                    baseDataCacheService.cacheUser(d);
                }
            }
        }
    }

    public Map<String, List<TeacherDomain>> findClassesTeachers(Long classesId) {
        Classes classes = classesService.findById(classesId);
        Map<String, List<TeacherDomain>> res = new HashedMap();
        if (null != classes) {
            List<TeacherDomain> data = findTeacherByClass(classes);
            res.put(ApiReturnConstants.DATA, data);
        }
        return res;
    }

    public List<IdNameDomain> findClassesByTeacher(Long teacherId) {
        List<IdNameDomain> r = new ArrayList<>();
        User teacher = userService.findById(teacherId);
        if (null != teacher) {
            r = findClassesIdNameByTeacher(teacher);
        }
        return r;
    }

    public long countByTeacher(Long teacherId) {
        User teacher = userService.findById(teacherId);
        if (null != teacher) {
            return countByTeacher(teacher);
        }
        return 0;
    }

    public List<Long> getClassTeacherIds(Long orgId, Long collegeId, String nj) {
        List<Long> d = new ArrayList<>();
        Map<String, Object> condition = new HashMap<>();
        StringBuilder sql = new StringBuilder(
            "SELECT DISTINCT ct.TEACHER_ID FROM t_classes_teacher ct INNER JOIN t_user u ON ct.TEACHER_ID = u.ID LEFT JOIN t_college cg ON u.COLLEGE_ID = cg.ID WHERE 1 =1");
        if (null != orgId) {
            sql.append(" AND u.ORG_ID = :orgId");
            condition.put("orgId", orgId);
        }
        if (null != collegeId) {
            sql.append(" AND u.COLLEGE_ID = :collegeId");
            condition.put("collegeId", collegeId);
        }
        if (null != nj) {
            sql.append(" AND (u.NAME like :name OR u.JOB_NUMBER like :job)");
            condition.put("name", "%" + nj + "%");
            condition.put("job", "%" + nj + "%");
        }
        Query q = em.createNativeQuery(sql.toString());
        for (Map.Entry<String, Object> e : condition.entrySet()) {
            q.setParameter(e.getKey(), e.getValue());
        }
        List<Object> res = q.getResultList();
        for (Object obj : res) {
            if (null != obj) {
                d.add(Long.valueOf(String.valueOf(obj)));
            }
        }
        return d;
    }
    
    @Transactional(readOnly = true)
    public PageData<ClassTeacherDomain> queryClassTeacherPage(Pageable pageable, String keywords,Long orgId){
        PageData<ClassTeacherDomain> reslultData = new PageData<>();
        Page<ClassTeacherDomain> page = null;
        if(!StringUtils.isEmpty(keywords)) {
        	 page = findTeacherPageByKeywords(pageable, keywords, keywords,orgId);
        }else{
        	 page = findTeacherPage(pageable,orgId);
        }
        if(null != page) {
             reslultData.setData(page.getContent());
        }
        reslultData.getPage().setTotalElements(page.getTotalElements());
        reslultData.getPage().setTotalPages(page.getTotalPages());
        reslultData.getPage().setPageNumber(pageable.getPageNumber());
        reslultData.getPage().setPageSize(pageable.getPageSize());
        return reslultData;
    }
}
