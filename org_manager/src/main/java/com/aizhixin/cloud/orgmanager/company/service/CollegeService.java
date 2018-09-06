/**
 *
 */
package com.aizhixin.cloud.orgmanager.company.service;


import com.aizhixin.cloud.orgmanager.common.PageDomain;
import com.aizhixin.cloud.orgmanager.common.core.ApiReturnConstants;
import com.aizhixin.cloud.orgmanager.common.core.DataValidity;
import com.aizhixin.cloud.orgmanager.common.core.ErrorCode;
import com.aizhixin.cloud.orgmanager.common.domain.IdIdNameDomain;
import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.company.domain.CollegeDomain;
import com.aizhixin.cloud.orgmanager.company.domain.excel.LineCodeNameBaseDomain;
import com.aizhixin.cloud.orgmanager.company.entity.College;
import com.aizhixin.cloud.orgmanager.company.repository.CollegeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;


/**
 * 学院相关操作业务逻辑处理
 *
 * @author zhen.pan
 */
@Component
@Transactional
public class CollegeService {
    private static Logger LOG = LoggerFactory.getLogger(CollegeService.class);
    @Autowired
    private ProfessionalService professionalService;
    @Autowired
    private CollegeRepository collectRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ExcelBasedataHelper excelBasedataHelper;
    @Autowired
    private BaseDataCacheService baseDataCacheService;

    /**
     * 实体保存
     *
     * @param college
     * @return
     */
    public College save(College college) {
        college = collectRepository.save(college);
        baseDataCacheService.cacheCollege(new CollegeDomain(college));
        return college;
    }

    public List<College> saveAll(List<College> colleges) {
        colleges = collectRepository.save(colleges);
        List<CollegeDomain> cacheList = new ArrayList<>();
        for (College c : colleges) {
            if (null != c.getId()) {
                cacheList.add(new CollegeDomain(c));
            }
        }
        if (cacheList.size() > 0) {
            baseDataCacheService.cacheCollege(cacheList);
        }
        return colleges;
    }

    /**
     * 根据实体ID查询实体
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public College findById(Long id) {
        return collectRepository.findOne(id);
    }

    /**
     * count特定组织机构ID的code值的正常数据
     *
     * @param orgId
     * @param code
     * @return
     */
    @Transactional(readOnly = true)
    public Long countByCode(Long orgId, String code) {
        return collectRepository.countByOrgIdAndCodeAndDeleteFlag(orgId, code, DataValidity.VALID.getState());
    }

    /**
     * count特定组织机构ID的name值正常数据
     *
     * @param orgId
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    public Long countByName(Long orgId, String name) {
        return collectRepository.countByOrgIdAndNameAndDeleteFlag(orgId, name, DataValidity.VALID.getState());
    }

    /**
     * count特定组织机构ID的code值正常数据，但是排除特定id（修改时用来排除自己）
     *
     * @param orgId
     * @param code
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public Long countByCodeAndIdNot(Long orgId, String code, Long id) {
        return collectRepository.countByOrgIdAndCodeAndIdNotAndDeleteFlag(orgId, code, id, DataValidity.VALID.getState());
    }

    /**
     * count特定组织机构ID所有name的值的正常数据，但是排除特定id（修改时用来排除自己）
     *
     * @param orgId
     * @param name
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public Long countByNameAndIdNot(Long orgId, String name, Long id) {
        return collectRepository.countByOrgIdAndNameAndIdNotAndDeleteFlag(orgId, name, id, DataValidity.VALID.getState());
    }

    /**
     * 分页查询特定组织机构ID的学院ID和name
     *
     * @param pageable
     * @param orgId
     * @return
     */
    @Transactional(readOnly = true)
    public Page<IdNameDomain> findIdName(Pageable pageable, Long orgId) {
        return collectRepository.findIdName(pageable, orgId, DataValidity.VALID.getState());
    }

    /**
     * 分页查询特定组织机构ID的学院ID和name
     *
     * @param pageable
     * @param orgId
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    public Page<IdNameDomain> findIdName(Pageable pageable, Long orgId, String name) {
        return collectRepository.findIdName(pageable, orgId, name, DataValidity.VALID.getState());
    }

    /**
     * 根据组织机构ID、name和code查询学院列表
     * @param pageable
     * @param orgId
     * @param name
     * @param code
     * @return
     */
//	@Transactional(readOnly = true)
//	public Page<CollegeDomain> findCodeName(Pageable pageable, Long orgId, String name, String code) {
//		return collectRepository.findByOrgIdAndNameCode(pageable, orgId, DataValidity.VALID.getState(), name, code);
//	}
    /**
     * 根据组织ID和code查询学院列表
     * @param pageable
     * @param orgId
     * @param code
     * @return
     */
//	@Transactional(readOnly = true)
//	public Page<CollegeDomain> findCode(Pageable pageable, Long orgId, String code) {
//		return collectRepository.findByOrgIdAndCode(pageable, orgId, DataValidity.VALID.getState(), code);
//	}

    /**
     * 根据组织ID、name查询学院列表
     *
     * @param pageable
     * @param orgId
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    public Page<CollegeDomain> findName(Pageable pageable, Long orgId, String name) {
        return collectRepository.findByOrgIdAndName(pageable, orgId, DataValidity.VALID.getState(), name);
    }

    /**
     * 根据组织ID查询学院列表
     *
     * @param pageable
     * @param orgId
     * @return
     */
    @Transactional(readOnly = true)
    public Page<CollegeDomain> find(Pageable pageable, Long orgId) {
        return collectRepository.findByOrgId(pageable, orgId, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public Long countByOrgId(Long orgId) {
        return collectRepository.countByOrgIdAndDeleteFlag(orgId, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<College> findByIdIn(Set<Long> ids) {
        return collectRepository.findByIdIn(ids);
    }

    @Transactional(readOnly = true)
    public List<College> findByCodeIn(Long orgId, Set<String> codes) {
        return collectRepository.findByOrgIdAndCodeInAndDeleteFlag(orgId, codes, DataValidity.VALID.getState());
    }
    @Transactional(readOnly = true)
    public List<College> findByNameIn(Long orgId, Set<String> names) {
        return collectRepository.findByOrgIdAndNameInAndDeleteFlag(orgId, names, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<College> findAllByOrgIdAndDeleteFlag(Long orgId, Integer flag) {
        return collectRepository.findAllByOrgIdAndDeleteFlag(orgId, flag);
    }

    @Transactional(readOnly = true)
    public List<IdIdNameDomain> findByOrgIds(Set<Long> orgIds) {
        return collectRepository.findByorgIds(orgIds, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<String> findCodesByOrgIdAndCodes(Long orgId, Set<String> codes) {
        return collectRepository.findCodesByOrgIdAndCodeInAndDeleteFlag(orgId, codes, DataValidity.VALID.getState());
    }

    @Transactional(readOnly = true)
    public List<String> findNamesByOrgIdAndCodes(Long orgId, Set<String> names) {
        return collectRepository.findNamesByOrgIdAndCodeInAndDeleteFlag(orgId, names, DataValidity.VALID.getState());
    }
//*************************************************************以下部分处理页面调用逻辑**********************************************************************//	

    /**
     * 创建学院
     *
     * @param cd
     * @return
     */
    public College save(CollegeDomain cd) {
        College college = new College();
        college.setOrgId(cd.getOrgId());
        Long c = countByName(cd.getOrgId(), cd.getName());
        if (c > 0) {
            throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "学院名称[" + cd.getName() + "]已经存在");
        }
        if (!StringUtils.isEmpty(cd.getCode())) {
            c = countByCode(cd.getOrgId(), cd.getCode());
            if (c > 0) {
                throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "学院编码[" + cd.getCode() + "]已经存在");
            }
            college.setCode(cd.getCode());
        }
        college.setName(cd.getName());

        college.setCreatedBy(cd.getUserId());
        college.setLastModifiedBy(cd.getUserId());
        return save(college);
    }

    /**
     * 修改学院
     *
     * @param cd
     * @return
     */
    public College update(CollegeDomain cd) {
        if (null == cd.getId() || cd.getId() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "ID是必须的");
        }
        College college = findById(cd.getId());
        if (null == college) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "ID[" + cd.getId() + "]对应的学院不存在");
        }
        college.setOrgId(cd.getOrgId());
        //验证name、code
        Long c = countByNameAndIdNot(cd.getOrgId(), cd.getName(), cd.getId());
        if (c > 0) {
            throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "学院名称[" + cd.getName() + "]已经存在");
        }
        if (!StringUtils.isEmpty(cd.getCode())) {
            c = countByCodeAndIdNot(cd.getOrgId(), cd.getCode(), cd.getId());
            if (c > 0) {
                throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "学院编码[" + cd.getCode() + "]已经存在");
            }
            college.setCode(cd.getCode());
        }
        college.setName(cd.getName());
        college.setLastModifiedBy(cd.getUserId());
        college.setLastModifiedDate(new Date());
        save(college);
        return college;
    }

    /**
     * 删除学院
     *
     * @param userId
     * @param id
     */
    public void delete(Long userId, Long id) {
        if (null == id || id <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "ID是必须的");
        }
        College college = findById(id);
        if (null == college) {
            throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "ID[" + id + "]对应的学院不存在");
        }
        long pc = professionalService.countByCollege(college);
        if (pc > 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "对应学院下还有专业数据");
        } else {
            pc = userService.countByCollege(college);
            if (pc > 0) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "对应学院下还有人员数据");
            }
        }
        college.setDeleteFlag(DataValidity.INVALID.getState());
        college.setLastModifiedBy(userId);
        college.setLastModifiedDate(new Date());
        save(college);
        //还需要删除对应的管理员
    }

    public CollegeDomain get(Long id) {
        CollegeDomain d = null;
        d = baseDataCacheService.readCollege(id);
        if (null != d) {
            return d;
        } else {
            d = new CollegeDomain();
        }
        College c = findById(id);
        if (null != c) {
            d.setOrgId(c.getOrgId());
            d.setCreatedDate(c.getCreatedDate());
            d.setId(c.getId());
            d.setName(c.getName());
            c.setCode(c.getCode());
        }
        return d;
    }

    /**
     * 根据查询条件查询学院信息列表
     *
     * @param r
     * @param pageable
     * @param orgId
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> queryList(Map<String, Object> r, Pageable pageable, Long orgId, String name) {
        Page<CollegeDomain> page = null;
//		if(!StringUtils.isEmpty(name) && !StringUtils.isEmpty(code)) {
//			page = findCodeName(pageable, orgId, name, code);
//		} else if(!StringUtils.isEmpty(name)) {
//			page = findName(pageable, orgId, name);
//		} else if(!StringUtils.isEmpty(code)) {
//			page = findCode(pageable, orgId, code);
//		} else {
//			page = find(pageable, orgId);
//		}
        if (!StringUtils.isEmpty(name)) {
            page = findName(pageable, orgId, name);
        } else {
            page = find(pageable, orgId);
        }

        PageDomain p = new PageDomain();
        p.setTotalElements(page.getTotalElements());
        p.setPageNumber(pageable.getPageNumber());
        p.setPageSize(pageable.getPageSize());
        p.setTotalPages(page.getTotalPages());

        r.put(ApiReturnConstants.PAGE, p);
        r.put(ApiReturnConstants.DATA, page.getContent());
        return r;
    }

    /**
     * 分页查询学院ID、Name字段，可用于下来列表
     *
     * @param r        查询结果容器
     * @param orgId    学校ID
     * @param name     学院名称
     * @param pageable 分页数据
     * @return 查询结果
     */
    @Transactional(readOnly = true)
    public Map<String, Object> dropList(Map<String, Object> r, Long orgId, String name, Pageable pageable) {
        Page<IdNameDomain> page = null;
        if (StringUtils.isEmpty(name)) {
            page = findIdName(pageable, orgId);
        } else {
            page = findIdName(pageable, orgId, name);
        }
        PageDomain p = new PageDomain();
        p.setTotalElements(page.getTotalElements());
        p.setPageNumber(pageable.getPageNumber());
        p.setPageSize(pageable.getPageSize());
        p.setTotalPages(page.getTotalPages());

        r.put(ApiReturnConstants.PAGE, p);
        r.put(ApiReturnConstants.DATA, page.getContent());
        return r;
    }

//    public List<College> save(List<CollegeDomain> cds) {
//        List<College> cs = new ArrayList<>();
//        Date dt = new Date();
//        for (CollegeDomain d : cds) {
//            College college = new College();
//            college.setLastModifiedBy(d.getUserId());
//            college.setOrgId(d.getOrgId());
//            college.setCreatedBy(d.getUserId());
//            college.setName(d.getName());
//            college.setCode(d.getCode());
//            college.setCreatedDate(dt);
//            college.setLastModifiedDate(dt);
//            cs.add(college);
//        }
//        return saveAll(cs);
//    }
//
//
//    public List<College> update(List<CollegeDomain> cds) {
//        Set<Long> ids = new HashSet<>();
//        Long userId = 0L;
//        Map<Long, CollegeDomain> cache = new HashMap<>();
//        for (CollegeDomain d : cds) {
//            if (null != d.getId() && d.getId() > 0) {
//                ids.add(d.getId());
//                cache.put(d.getId(), d);
//                if (0 == userId) {
//                    userId = d.getUserId();
//                }
//            }
//        }
//        List<College> cs = findByIdIn(ids);
//        if (cs.size() != cds.size()) {
//            LOG.warn("Batch update college data in size:{} find size:{}", cds.size(), cs.size());
//        }
//        Date dt = new Date();
//        for (College college : cs) {
//            CollegeDomain d = cache.get(college.getId());
//            if (null != d) {
//                college.setName(d.getName());
//                college.setCode(d.getCode());
//                college.setLastModifiedBy(userId);
//                college.setLastModifiedDate(dt);
//            }
//        }
//        return saveAll(cs);
//    }
//
//    public List<College> delete(List<CollegeDomain> cds) {
//        Set<Long> ids = new HashSet<>();
//        Long userId = 0L;
//        for (CollegeDomain d : cds) {
//            if (null != d.getId() && d.getId() > 0) {
//                ids.add(d.getId());
//                if (0 == userId) {
//                    userId = d.getUserId();
//                }
//            }
//        }
//        List<College> cs = findByIdIn(ids);
//        if (cs.size() != cds.size()) {
//            LOG.warn("Batch delete college data in size:{} find size:{}", cds.size(), cs.size());
//        }
//        Date d = new Date();
//        for (College c : cs) {
//            c.setDeleteFlag(DataValidity.INVALID.getState());
//            c.setLastModifiedBy(userId);
//            c.setLastModifiedDate(d);
//        }
//        return saveAll(cs);
//    }
    /**
     * 批量导入学院信息
     *
     * @param failSign  验证失败标识
     * @param orgId     学校
     * @param file      excel文件
     * @param userId    操作人
     * @return
     */
    public List<LineCodeNameBaseDomain> importCollegeData(Set<Integer> failSign, Long orgId, MultipartFile file, Long userId) {
        if (null == orgId || orgId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "orgId是必须的");
        }
        if (null == userId || userId <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "userId是必须的");
        }
        List<LineCodeNameBaseDomain> excelDatas = excelBasedataHelper.readCollegesFromInputStream(file);
        if (null == excelDatas || excelDatas.size() <= 0) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "没有读取到任何数据");
        }
        
        Set<String> codeSet = new HashSet<>();// 使用Set是为了进行contains，判断值的否唯一
        Set<String> nameSet = new HashSet<>();

        boolean hasError = false;
        String msg = null;
        for (LineCodeNameBaseDomain d : excelDatas) {
            if (!StringUtils.isEmpty(d.getCode())) {
                if (codeSet.contains(d.getCode())) {
                    msg = "学院编码在此Excel中已经存在";
                    d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);// 不为null时拼接前后的错误信息
                    hasError = true;
                } else {
                    codeSet.add(d.getCode());
                }
            }
            if (StringUtils.isEmpty(d.getCode()) && StringUtils.isEmpty(d.getName())) {
                msg = "学院编码、名称是必须的";
                d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                hasError = true;
            }
            if (StringUtils.isEmpty(d.getName())) {
                msg = "学院名称是必须的";
                d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                hasError = true;
            } else {
                if (nameSet.contains(d.getName())) {
                    msg = "学院名称在此Excel中已经存在";
                    d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                    hasError = true;
                } else {
                    nameSet.add(d.getName());
                }
            }
        }

        Map<String, College> codeCollegeMap = new HashMap<>();
        List<College> list = null;
        if (codeSet.size() > 0) {
            list = findByCodeIn(orgId, codeSet);
            for (College college : list) {
                codeCollegeMap.put(college.getCode(), college);
            }
        }
        List<String> names = null;
        if (nameSet.size() > 0) {
            names = findNamesByOrgIdAndCodes(orgId, nameSet);
        }
        //错误判断及空行数据提示
        List<College> data = new ArrayList<>();
        Map<String, LineCodeNameBaseDomain> cache = new HashMap<>();//因为code不是必须的，所以按照名称匹配保存后的数据的ID
        for (LineCodeNameBaseDomain d : excelDatas) {
            College cs = null;
            if (!StringUtils.isEmpty(d.getCode()) && codeCollegeMap.keySet().contains(d.getCode())) {
                cs = codeCollegeMap.get(d.getCode());
            }

            if (!StringUtils.isEmpty(d.getName())) {
                cache.put(d.getName(), d);
                if (null == cs) {//新增
                    if (null != names && names.contains(d.getName())) {
                        msg = "学院名称已经存在";
                        d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                        hasError = true;
                    }
                } else {
                    if (!d.getName().equals(cs.getName()) && null != names && names.contains(d.getName())) {
                        msg = "学院名称已经存在";
                        d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                        hasError = true;
                    }
                }
            } else {
                msg = "学院名称是必须的";
                d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                hasError = true;
            }

            College c = new College();
            c.setName(d.getName());
            c.setCreatedBy(userId);
            c.setOrgId(orgId);
            c.setCode(d.getCode());
            data.add(c);
        }
        if (hasError) {
            failSign.add(1);
            return excelDatas;
        }
        List<College> dataSave = new ArrayList<>();
        for (College c : data) {
            College sc = codeCollegeMap.get(c.getCode());
            if (null != sc) {
                sc.setName(c.getName());
                dataSave.add(sc);
            } else {
                dataSave.add(c);
            }
        }
        dataSave = saveAll(dataSave);
        for (College c : dataSave) {//回填已经保存的数据的ID
            LineCodeNameBaseDomain d = cache.get(c.getName());
            if (null != d) {
                d.setId(c.getId());
            }
        }
        return excelDatas;
    }
}
