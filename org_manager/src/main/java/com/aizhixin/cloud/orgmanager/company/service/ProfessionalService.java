/**
 *
 */
package com.aizhixin.cloud.orgmanager.company.service;

import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.PageDomain;
import com.aizhixin.cloud.orgmanager.common.core.ApiReturnConstants;
import com.aizhixin.cloud.orgmanager.common.core.DataValidity;
import com.aizhixin.cloud.orgmanager.common.core.ErrorCode;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.common.domain.IdIdNameDomain;
import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.common.util.ExcelUtil;
import com.aizhixin.cloud.orgmanager.company.domain.ProfessionnalDomain;
import com.aizhixin.cloud.orgmanager.company.domain.excel.ProfessionalExcelDomain;
import com.aizhixin.cloud.orgmanager.company.entity.College;
import com.aizhixin.cloud.orgmanager.company.entity.Professional;
import com.aizhixin.cloud.orgmanager.company.repository.ProfessionalRepository;
import org.apache.poi.ss.usermodel.Sheet;
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
 * 专业相关操作业务逻辑处理
 *
 * @author zhen.pan
 */
@Component
@Transactional
public class ProfessionalService {
    @Autowired
    private EntityManager em;
    @Autowired
    private ProfessionalRepository professionalRepository;
    @Autowired
    private CollegeService collegeService;
    @Autowired
    private UserService userService;
    @Autowired
    private ClassesService classesService;
    @Autowired
    private ExcelBasedataHelper excelBasedataHelper;
    @Autowired
    private BaseDataCacheService baseDataCacheService;

    /**
     * 保存实体
     *
     * @param professional
     * @return
     */
    public Professional save(Professional professional) {
        professional =  professionalRepository.save(professional);
        baseDataCacheService.cacheProfessional(new ProfessionnalDomain(professional));
        return professional;
    }

    /**
     * 根据实体ID查询实体
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public Professional findById(Long id) {
        return professionalRepository.findOne(id);
    }

    /**
     * count特定学院code值的正常数据
     *
     * @param college
     * @param code
     * @return
     */
    @Transactional(readOnly = true)
    public Long countByCode(College college, String code) {
        return professionalRepository.countByCollegeAndCodeAndDeleteFlag(college, code,
                DataValidity.VALID.getState());
    }

    /**
     * count特定学院name值的正常数据
     *
     * @param college
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    public Long countByName(College college, String name) {
        return professionalRepository.countByCollegeAndNameAndDeleteFlag(college, name,
                DataValidity.VALID.getState());
    }

    /**
     * count特定学院code值的正常数据，但是排除特定id（修改时用来排除自己）
     *
     * @param college
     * @param code
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public Long countByCodeAndIdNot(College college, String code, Long id) {
        return professionalRepository.countByCollegeAndCodeAndIdNotAndDeleteFlag(college, code, id,
                DataValidity.VALID.getState());
    }

    /**
     * count特定学院name值的正常数据，但是排除特定id（修改时用来排除自己）
     *
     * @param college
     * @param name
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public Long countByNameAndIdNot(College college, String name, Long id) {
        return professionalRepository.countByCollegeAndNameAndIdNotAndDeleteFlag(college, name, id,
                DataValidity.VALID.getState());
    }

    /**
     * 分页查询特定学院专业的ID和name
     *
     * @param pageable
     * @param college
     * @return
     */
    @Transactional(readOnly = true)
    public Page<IdNameDomain> findIdName(Pageable pageable, College college) {
        return professionalRepository.findIdName(pageable, college, DataValidity.VALID.getState());
    }

    /**
     * 分页查询特定学院专业的ID和name
     *
     * @param pageable
     * @param college
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    public Page<IdNameDomain> findIdName(Pageable pageable, College college, String name) {
        return professionalRepository.findIdName(pageable, college, name, DataValidity.VALID.getState());
    }

//    /**
//     * 根据学院、name和code查询专业列表
//     *
//     * @param pageable
//     * @param name
//     * @param code
//     * @return
//     */
//	@Transactional(readOnly = true)
//	public Page<ProfessionnalDomain> findCodeName(Pageable pageable, College college, String name, String code) {
//		return professionalRepository.findByCollegeAndNameCode(pageable, college, DataValidity.VALID.getState(),
//				name, code);
//	}
//
//    /**
//     * 根据学院和code查询专业列表
//     *
//     * @param pageable
//     * @param college
//     * @param code
//     * @return
//     */
//	@Transactional(readOnly = true)
//	public Page<ProfessionnalDomain> findCode(Pageable pageable, College college, String code) {
//		return professionalRepository.findByCollegeAndCode(pageable, college, DataValidity.VALID.getState(), code);
//	}
//
//    /**
//     * 根据学院和name查询专业列表
//     *
//     * @param pageable
//     * @param college
//     * @param name
//     * @return
//     */
//    @Transactional(readOnly = true)
//    public Page<ProfessionnalDomain> findName(Pageable pageable, College college, String name) {
//        return professionalRepository.findByCollegeAndName(pageable, college, DataValidity.VALID.getState(), name);
//    }
//
//    /**
//     * 根据学校和name查询专业列表
//     *
//     * @param pageable
//     * @param orgId
//     * @param name
//     * @return
//     */
//    @Transactional(readOnly = true)
//    public Page<ProfessionnalDomain> findName(Pageable pageable, Long orgId, String name) {
//        return professionalRepository.findByOrgIdAndName(pageable, orgId, DataValidity.VALID.getState(), name);
//    }

//    /**
//     * 根据学院查询专业列表
//     *
//     * @param pageable
//     * @param college
//     * @return
//     */
//    @Transactional(readOnly = true)
//    public Page<ProfessionnalDomain> find(Pageable pageable, College college) {
//        return professionalRepository.findByCollege(pageable, college, DataValidity.VALID.getState());
//    }
//
//    /**
//     * 根据学校查询专业列表
//     *
//     * @param pageable
//     * @param orgId
//     * @return
//     */
//    @Transactional(readOnly = true)
//    public Page<ProfessionnalDomain> find(Pageable pageable, Long orgId) {
//        return professionalRepository.findByOrgId(pageable, orgId, DataValidity.VALID.getState());
//    }


    @Transactional(readOnly = true)
    public Long countByCollege(College college) {
        return professionalRepository.countByCollegeAndDeleteFlag(college, DataValidity.VALID.getState());
    }

    public List<Professional> saveAll(List<Professional> professionals) {
        professionals =  professionalRepository.save(professionals);
        List<ProfessionnalDomain> cacheList = new ArrayList<>();
        for (Professional p : professionals) {
            cacheList.add(new ProfessionnalDomain(p));
        }
        if (cacheList.size() > 0) {
            baseDataCacheService.cacheProfessional(cacheList);
        }
        return professionals;
    }

    @Transactional(readOnly = true)
    public List<Professional> findByIdIn(Set<Long> ids) {
        return professionalRepository.findByIdIn(ids);
    }

    @Transactional(readOnly = true)
    public List<Professional> findByCodeIn(Set<String> codes) {
        return professionalRepository.findByCodeIn(codes);
    }

    @Transactional(readOnly = true)
    public List<ProfessionnalDomain> findProfessionalAndCollege(Long orgId, String name) {
        return professionalRepository.findProfessionalAndCollege(orgId, name, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<IdIdNameDomain> findProfessionalAndCollege(Set<Long> collegeIds) {
        return professionalRepository.findIdNameByCollegeIdIn(collegeIds, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<String> findCodesByOrgIdAndCodes(Long orgId, Set<String> codes) {
        return professionalRepository.findCodesByOrgIdAndCodeInAndDeleteFlag(orgId, codes, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<String> findNamesByOrgIdAndCodes(Long orgId, Set<String> names) {
        return professionalRepository.findNamesByOrgIdAndCodeInAndDeleteFlag(orgId, names, DataValidity.VALID.getState());
    }
    @Transactional(readOnly = true)
    public List<Professional> findByOrgIdAndCodeIn(Long orgId, Set<String> codes) {
        return professionalRepository.findByOrgIdAndCodeInAndDeleteFlag(orgId, codes, DataValidity.VALID.getState());
    }
    @Transactional(readOnly = true)
    public List<Professional> findByOrgIdAndNameIn(Long orgId, Set<String> names) {
        return professionalRepository.findByOrgIdAndNameInAndDeleteFlag(orgId, names, DataValidity.VALID.getState());
    }
    @Transactional(readOnly = true)
    public List<Professional> findByOrgId(Long orgId) {
        return professionalRepository.findByOrgIdAndDeleteFlag(orgId, DataValidity.VALID.getState());
    }
    // *************************************************************以下部分处理页面调用逻辑**********************************************************************//

    /**
     * 专业信息保存
     *
     * @param pd
     * @return
     */
    public Professional save(ProfessionnalDomain pd) {
        Professional professional = new Professional();
        College college = collegeService.findById(pd.getCollegeId());
        if (null == college) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据学院ID[" + pd.getCollegeId() + "]没有查找到对应的学院信息");
        }
        professional.setCollege(college);
        professional.setOrgId(college.getOrgId());
        Long c = countByName(college, pd.getName());
        if (c > 0) {
            throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "专业名称[" + pd.getName() + "]已经存在");
        }
        professional.setName(pd.getName());
        if (!StringUtils.isEmpty(pd.getCode())) {
            c = countByCode(college, pd.getCode());
            if (c > 0) {
                throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "编码[" + pd.getCode() + "]已经存在");
            }
            professional.setCode(pd.getCode());
        }

        professional.setCreatedBy(pd.getUserId());
        professional.setLastModifiedBy(pd.getUserId());
        return save(professional);
    }

    /**
     * 专业信息修改
     *
     * @param pd
     * @param pd
     * @return
     */
    public Professional update(ProfessionnalDomain pd) {
        if (null == pd.getId() || pd.getId() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "ID是必须的");
        }
        Professional professional = findById(pd.getId());
        if (null == professional) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "ID[" + pd.getId() + "]对应的专业不存在");
        }
        College college = collegeService.findById(pd.getCollegeId());
        if (null == college) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据学院ID[" + pd.getCollegeId() + "]没有查找到对应的学院信息");
        }
        if (college.getId().longValue() != professional.getCollege().getId().longValue()) {
            professional.setCollege(college);
            professional.setOrgId(college.getOrgId());

            classesService.updateNewCollegeByProfesional(college, professional);
            userService.updateCollgeByProfesional(professional, college);
        }
        // 验证name、code
        Long c = countByNameAndIdNot(college, pd.getName(), pd.getId());
        if (c > 0) {
            throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "专业名称[" + pd.getName() + "]已经存在");
        }
        professional.setName(pd.getName());
        if (!StringUtils.isEmpty(pd.getCode())) {
            c = countByCodeAndIdNot(college, pd.getCode(), pd.getId());
            if (c > 0) {
                throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "编码[" + pd.getCode() + "]已经存在");
            }
            professional.setCode(pd.getCode());
        }

        professional.setLastModifiedBy(pd.getUserId());
        professional.setLastModifiedDate(new Date());
        save(professional);
        return professional;
    }

    public ProfessionnalDomain get(long id) {
        ProfessionnalDomain d = null;
        d = baseDataCacheService.readProfessional(id);
        if (null != d) {
            return d;
        } else {
            d = new ProfessionnalDomain();
        }
        Professional p = findById(id);
        if (null != d) {
            d.setId(p.getId());
            d.setName(p.getName());
            d.setCode(p.getCode());
            if (null != p.getCollege()) {
                d.setCollegeId(p.getCollege().getId());
                d.setCollegeName(p.getCollege().getName());
            }
            d.setCreatedDate(p.getCreatedDate());
        }
        return d;
    }

    /**
     * 专业信息删除
     *
     * @param userId
     * @param id
     */
    public void delete(Long userId, Long id) {
        if (null == id || id <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "ID是必须的");
        }
        Professional professional = findById(id);
        if (null == professional) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "ID[" + id + "]对应的专业不存在");
        }
        long cc = classesService.countByProfessional(professional);
        if (cc > 0) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "专业下存在班级数据");
        } else {
            cc = userService.countByProfessional(professional);
            if (cc > 0) {
                throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "专业下存在用户数据");
            }
        }
        professional.setDeleteFlag(DataValidity.INVALID.getState());
        professional.setLastModifiedBy(userId);
        professional.setLastModifiedDate(new Date());
        save(professional);
    }

    /**
     * 根据学院、name、code查询专业信息
     *
     * @param pageable
     * @param orgId
     * @param collegeId
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    public PageData<ProfessionnalDomain> queryList(Pageable pageable, Long orgId, Long collegeId, String name) {
        PageData<ProfessionnalDomain> pageData = new PageData<>();
        pageData.getPage().setPageSize(pageable.getPageSize());
        pageData.getPage().setPageNumber(pageable.getPageNumber() + 1);
        if (null == orgId || orgId <= 0) {
            return pageData;
        }
        College college = null;
        if (null != collegeId && collegeId > 0) {
            college = collegeService.findById(collegeId);
        }
        Map<String, Object> condition = new HashMap<>();
        condition.put("deleteFlag", DataValidity.VALID.getState());
        StringBuilder chql;
        StringBuilder hql;
        chql = new StringBuilder("select count(c.id) from com.aizhixin.cloud.orgmanager.company.entity.Professional c inner join c.college where c.deleteFlag = :deleteFlag");
        hql = new StringBuilder("select new com.aizhixin.cloud.orgmanager.company.domain.ProfessionnalDomain(c.id, c.code, c.name, c.college.id, c.college.name, c.createdDate) from com.aizhixin.cloud.orgmanager.company.entity.Professional c where c.deleteFlag = :deleteFlag");

        if (!StringUtils.isEmpty(name)) {
            hql.append(" and (c.name like :name or c.code like :name)");
            chql.append(" and (c.name like :name or c.code like :name)");
            condition.put("name", "%" + name + "%");
        }
        if(null != college) {
            hql.append(" and c.college = :college");
            chql.append(" and c.college = :college");
            condition.put("college", college);
        } else {
            hql.append(" and c.orgId = :orgId");
            chql.append(" and c.orgId = :orgId");
            condition.put("orgId", orgId);
        }
        hql.append(" order by c.id DESC");

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
        TypedQuery<ProfessionnalDomain> tq = em.createQuery(hql.toString(), ProfessionnalDomain.class);
        for (Map.Entry<String, Object> e : condition.entrySet()) {
            tq.setParameter(e.getKey(), e.getValue());
        }
        tq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        tq.setMaxResults(pageable.getPageSize());
        pageData.setData(tq.getResultList());
        pageData.getPage().setTotalPages(PageUtil.cacalatePagesize(count, pageData.getPage().getPageSize()));
        return pageData;
    }

    /**
     * 根据学院查询专业列表，仅查询ID、Name字段，可用于下拉列表
     *
     * @param r         结果容器
     * @param collegeId 学院ID
     * @param name      专业名称
     * @param pageable  分页信息
     * @return 查询结果
     */
    @Transactional(readOnly = true)
    public Map<String, Object> dropList(Map<String, Object> r, Long collegeId, String name, Pageable pageable) {
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

    @Transactional(readOnly = true)
    public List<Map<String, Object>> multDropList(Long orgId, String name) {
        List<Map<String, Object>> r = new ArrayList<>();
        Map<Long, Map<String, Object>> colleges = new HashMap<>();
        if (StringUtils.isEmpty(name)) {
            Page<IdNameDomain> page = collegeService.findIdName(PageUtil.createNoErrorPageRequest(1, Integer.MAX_VALUE), orgId);
            Set<Long> ids = new HashSet<>();
            for (IdNameDomain d : page.getContent()) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", d.getId());
                item.put("name", d.getName());

                r.add(item);
                colleges.put(d.getId(), item);
                ids.add(d.getId());
            }
            if (ids.size() > 0) {
                List<IdIdNameDomain> ps = findProfessionalAndCollege(ids);
                for (IdIdNameDomain d : ps) {
                    Map<String, Object> item = colleges.get(d.getLogicId());
                    if (null != item) {
                        List<IdNameDomain> pps = (List<IdNameDomain>) item.get("professionals");
                        if (null == pps) {
                            pps = new ArrayList<>();
                            item.put("professionals", pps);
                        }
                        IdNameDomain idn = new IdNameDomain(d.getId(), d.getName());
                        pps.add(idn);
                    }
                }
            }
        } else {
            List<ProfessionnalDomain> list = findProfessionalAndCollege(orgId, name);
            for (ProfessionnalDomain d : list) {
                if (!colleges.keySet().contains(d.getCollegeId())) {
                    Map<String, Object> item = new HashMap<>();
                    colleges.put(d.getCollegeId(), item);
                    item.put("id", d.getCollegeId());
                    item.put("name", d.getCollegeName());
                    List<IdNameDomain> ps = new ArrayList<>();
                    item.put("professionals", ps);
                    IdNameDomain idn = new IdNameDomain(d.getId(), d.getName());
                    ps.add(idn);
                    r.add(item);
                } else {
                    Map<String, Object> item = colleges.get(d.getCollegeId());
                    List<IdNameDomain> ps = (List<IdNameDomain>) item.get("professionals");
                    IdNameDomain idn = new IdNameDomain(d.getId(), d.getName());
                    ps.add(idn);
                }
            }
        }
        return r;
    }


//    public List<Professional> save(List<ProfessionnalDomain> pds) {
//        List<Professional> ds = new ArrayList<>();
//        Set<String> codes = new HashSet<>();
//        Date date = new Date();
//        Long orgId = null;
//        for (ProfessionnalDomain d : pds) {
//            codes.add(d.getCollegeCode());
//            if (null == orgId || orgId <= 0) {
//                orgId = d.getOrgId();
//            }
//        }
//        List<College> cs = null;
//        if (codes.size() > 0) {
//            cs = collegeService.findByCodeIn(orgId, codes);
//            Map<String, College> cache = new HashMap<>();
//            for (College c : cs) {
//                cache.put(c.getCode(), c);
//            }
//            for (ProfessionnalDomain d : pds) {
//                Professional professional = new Professional();
//                professional.setName(d.getName());
//                professional.setCode(d.getCode());
//                professional.setCreatedDate(date);
//                professional.setCreatedBy(d.getUserId());
//                professional.setLastModifiedDate(date);
//                professional.setLastModifiedBy(d.getUserId());
//                if (null != cache.get(d.getCollegeCode())) {
//                    professional.setCollege(cache.get(d.getCollegeCode()));
//                    professional.setOrgId(cache.get(d.getCollegeCode()).getOrgId());
//                }
//                ds.add(professional);
//            }
//        } else {
//            for (ProfessionnalDomain d : pds) {
//                Professional professional = new Professional();
//                professional.setName(d.getName());
//                professional.setCode(d.getCode());
//                professional.setCreatedDate(date);
//                professional.setCreatedBy(d.getUserId());
//                professional.setLastModifiedDate(date);
//                professional.setLastModifiedBy(d.getUserId());
//                ds.add(professional);
//            }
//        }
//
//        if (ds.size() > 0) {
//            return saveAll(ds);
//        }
//        return ds;
//    }
//
//    public List<Professional> update(List<ProfessionnalDomain> pds) {
//        List<Professional> ds = new ArrayList<>();
//        Set<Long> ids = new HashSet<>();
//        Set<String> codes = new HashSet<>();
//        Map<Long, ProfessionnalDomain> pvs = new HashMap<>();
//        Date date = new Date();
//        Long userId = 0L;
//        Long orgId = null;
//        for (ProfessionnalDomain d : pds) {
//            ids.add(d.getId());
//            codes.add(d.getCollegeCode());
//            pvs.put(d.getId(), d);
//            if (userId <= 0) {
//                userId = d.getUserId();
//            }
//            if (null == orgId || orgId <= 0) {
//                orgId = d.getOrgId();
//            }
//        }
//
//        if (ids.size() > 0) {
//            Map<String, College> cache = new HashMap<>();
//            if (codes.size() > 0) {
//                List<College> cs = collegeService.findByCodeIn(orgId, codes);
//                for (College c : cs) {
//                    cache.put(c.getCode(), c);
//                }
//            }
//            ds = findByIdIn(ids);
//            for (Professional professional : ds) {
//                ProfessionnalDomain d = pvs.get(professional.getId());
//                if (null != d) {
//                    professional.setName(d.getName());
//                    professional.setCode(d.getCode());
//                    if (null != cache.get(d.getCollegeCode())) {
//                        professional.setCollege(cache.get(d.getCollegeCode()));
//                        professional.setOrgId(cache.get(d.getCollegeCode()).getOrgId());
//                    }
//                    professional.setLastModifiedDate(date);
//                    professional.setLastModifiedBy(userId);
//                }
//            }
//        }
//
//        if (ds.size() > 0) {
//            return saveAll(ds);
//        }
//        return ds;
//    }
//
//    public List<Professional> delete(List<ProfessionnalDomain> pds) {
//        List<Professional> ds = new ArrayList<>();
//        Set<Long> ids = new HashSet<>();
//        Date date = new Date();
//        Long userId = 0L;
//        for (ProfessionnalDomain d : pds) {
//            ids.add(d.getId());
//            if (userId <= 0) {
//                userId = d.getUserId();
//            }
//        }
//        if (ids.size() > 0) {
//            ds = findByIdIn(ids);
//            for (Professional professional : ds) {
//                professional.setDeleteFlag(DataValidity.INVALID.getState());
//                professional.setLastModifiedBy(userId);
//                professional.setLastModifiedDate(date);
//            }
//            if (ds.size() > 0) {
//                return saveAll(ds);
//            }
//        }
//        return ds;
//    }

    /**
     * 导入学院信息
     *
     * @param organId
     * @param file
     */
    public List importProfessionalData(Long organId, MultipartFile file) {
        ExcelUtil execl = new ExcelUtil(file);
        if (null == execl) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "文件异常");
        }
        Sheet collegeSheet = execl.getSheet("学院");
        if (null == collegeSheet) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "未获取到学院sheet");
        }

//        return readProfessional(organId, collegeSheet);
        return null;
    }

    /**
     * 批量Excle导入专业数据
     *
     * @param orgId
     * @param file
     * @return
     */
    public List<ProfessionalExcelDomain> importProfessionalData(Set<Integer> failSign, Long orgId, MultipartFile file, Long userId) {
        if (null == orgId || orgId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "orgId是必须的");
        }
        if (null == userId || userId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "userId是必须的");
        }
        List<ProfessionalExcelDomain> excelDatas = excelBasedataHelper.readProfessionalsFromInputStream(file);
        if (null == excelDatas || excelDatas.size() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "没有读取到任何数据");
        }
        Set<String> codeSet = new HashSet<>();
        Set<String> nameSet = new HashSet<>();
        Set<String> collegeCodeSet = new HashSet<>();
        Set<String> collegeNameSet = new HashSet<>();

        boolean hasError = false;
        String msg = null;
        for (ProfessionalExcelDomain d : excelDatas) {
            if (!StringUtils.isEmpty(d.getCode())) {
                if (codeSet.contains(d.getCode())) {
                    msg = "专业编码在此Excel中已经存在";
                    d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                    hasError = true;
                }
                codeSet.add(d.getCode());
            }
            if (!StringUtils.isEmpty(d.getName())) {
                if (nameSet.contains(d.getName())) {
                    msg = "专业名称在此Excel中已经存在";
                    d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                    hasError = true;
                }
                nameSet.add(d.getName());
            }
            if (!StringUtils.isEmpty(d.getCollegeCode())) {
                collegeCodeSet.add(d.getCollegeCode());
            }
            if (!StringUtils.isEmpty(d.getCollegeName())) {
                collegeNameSet.add(d.getCollegeName());
            }
        }
        Map<String, College> collegeNameMap = new HashMap<>();
        Map<String, College> collegeCodeMap = new HashMap<>();
        //如果学院的名称有有效值的数量多于编码的数量，则采用名称验证，否则采用学院编码来验证数据
        if (collegeNameSet.size() > 0) {
            List<College> collegeList = collegeService.findByNameIn(orgId, collegeNameSet);
            for (College c : collegeList) {
                collegeNameMap.put(c.getName(), c);
            }
        }
        if (collegeCodeSet.size() > 0) {
            List<College> collegeList = collegeService.findByCodeIn(orgId, collegeCodeSet);
            for (College c : collegeList) {
                collegeCodeMap.put(c.getCode(), c);
            }
        }

//        List<String> codes = null;
        Map<String, Professional> codeProfessionalDBMap = new HashMap<>();
        if (codeSet.size() > 0) {
//            codes = findCodesByOrgIdAndCodes(orgId, codeSet);
            List<Professional> list = findByOrgIdAndCodeIn(orgId, codeSet);
            for (Professional p : list) {
                codeProfessionalDBMap.put(p.getCode(), p);
            }
        }
        List<String> names = null;
        if (nameSet.size() > 0) {
            names = findNamesByOrgIdAndCodes(orgId, nameSet);
        }
        //错误判断及空行数据提示
        List<Professional> data = new ArrayList<>();
        Map<String, ProfessionalExcelDomain> cache = new HashMap<>();//因为code不是必须的，所以按照名称匹配保存后的数据的ID
        for (ProfessionalExcelDomain d : excelDatas) {
            Professional ps = null;
            College c = null;
            if (StringUtils.isEmpty(d.getCode()) && StringUtils.isEmpty(d.getName())) {
                msg = "空数据";
                d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                hasError = true;
            }
            if (!StringUtils.isEmpty(d.getCode()) && codeProfessionalDBMap.keySet().size() > 0 && codeProfessionalDBMap.keySet().contains(d.getCode())) {
                ps = codeProfessionalDBMap.get(d.getCode());
            }
            if (StringUtils.isEmpty(d.getName())) {
                msg = "专业名称是必须的";
                d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                hasError = true;
            } else {
                if (null != names && names.contains(d.getName())) {
                    if (null == ps) {
                        msg = "专业名称已经存在";
                        d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                        hasError = true;
                    } else {
                        if (!d.getName().equals(ps.getName()) && null != names && names.contains(d.getName())) {
                            msg = "专业名称已经存在";
                            d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                            hasError = true;
                        }
                    }
                }
            }
            if (StringUtils.isEmpty(d.getCollegeCode()) && StringUtils.isEmpty(d.getCollegeName())) {
                msg = "学院信息是必须的";
                d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                hasError = true;
            }
            if (collegeCodeMap.size() > 0 && !StringUtils.isEmpty(d.getCollegeCode())) {//采用学院编码来关联数据
                c = collegeCodeMap.get(d.getCollegeCode());
                if (null == c) {
                    msg = "根据学院编码没有找到对应的学院信息";
                    d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                    hasError = true;
                }
            }
            if (null == c) {
                if (collegeNameMap.size() > 0 && !StringUtils.isEmpty(d.getCollegeName())) {
                    c = collegeNameMap.get(d.getCollegeName());
                    if (null == c) {
                        msg = "根据学院名称没有找到对应的学院信息";
                        d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                        hasError = true;
                    }
                }
            }
            if (null == c) {
                msg = "没有找到对应的学院信息";
                d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                hasError = true;
            }
            Professional p = new Professional();
            if (!StringUtils.isEmpty(d.getName())) {
                cache.put(d.getName(), d);
            }
            p.setCode(d.getCode());
            p.setName(d.getName());
            p.setCollege(c);
            p.setOrgId(orgId);
            p.setCreatedBy(userId);
            p.setLastModifiedBy(userId);
            data.add(p);
        }
        if (hasError) {
            failSign.add(1);
            return excelDatas;
        }
        List<Professional> saveData = new ArrayList<>();
        for (Professional p : data) {
            Professional ps = codeProfessionalDBMap.get(p.getCode());
            if (null != ps) {
                ps.setName(p.getName());
                ps.setCollege(p.getCollege());
                saveData.add(ps);
            } else {
                saveData.add(p);
            }
        }
        saveData = saveAll(saveData);
        for (Professional c : saveData) {//回填已经保存的数据的ID
            ProfessionalExcelDomain d = cache.get(c.getName());
            if (null != d) {
                d.setId(c.getId());
            }
        }
        return excelDatas;
    }
}
