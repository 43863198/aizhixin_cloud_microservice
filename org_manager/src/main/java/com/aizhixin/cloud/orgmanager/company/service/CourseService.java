/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.service;


import com.aizhixin.cloud.orgmanager.common.PageDomain;
import com.aizhixin.cloud.orgmanager.common.async.AsyncTaskBase;
import com.aizhixin.cloud.orgmanager.common.core.ApiReturnConstants;
import com.aizhixin.cloud.orgmanager.common.core.DataValidity;
import com.aizhixin.cloud.orgmanager.common.core.ErrorCode;
import com.aizhixin.cloud.orgmanager.common.core.ExcelImportStatus;
import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.common.provider.store.redis.RedisTokenStore;
import com.aizhixin.cloud.orgmanager.common.service.DataSynService;
import com.aizhixin.cloud.orgmanager.company.domain.CourseDomain;
import com.aizhixin.cloud.orgmanager.company.domain.CourseDomainV2;
import com.aizhixin.cloud.orgmanager.company.domain.IdCodeNameBase;
import com.aizhixin.cloud.orgmanager.company.domain.excel.CourseExcelDomain;
import com.aizhixin.cloud.orgmanager.company.domain.excel.CourseRedisData;
import com.aizhixin.cloud.orgmanager.company.entity.Course;
import com.aizhixin.cloud.orgmanager.company.entity.Organization;
import com.aizhixin.cloud.orgmanager.company.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 课程相关操作业务逻辑处理
 * @author zhen.pan
 *
 */
@Component
@Transactional
public class CourseService {
	@Autowired
	private CourseRepository courseRepository;
	@Autowired
	private RedisConnectionFactory redisConnectionFactory;
	@Autowired
    private ExcelBasedataHelper excelBasedataHelper;
	@Autowired
	private AsyncTaskBase asyncTaskBase;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private DataSynService dataSynService;
	/**
	 * 保存实体
	 * @param course
	 * @return
	 */
	public Course save(Course course) {
		return courseRepository.save(course);
	}
	/**
	 * 根据ID查询实体
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public Course findById(Long id) {
		return courseRepository.findOne(id);
	}
	/**
	 * count指定学校特定name的值的正常数据
	 * @param orgId
	 * @param name
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countByName(Long orgId, String name) {
		return courseRepository.countByOrgIdAndNameAndDeleteFlag(orgId, name, DataValidity.VALID.getState());
	}
	/**
	 * count指定学校特定name的值的正常数据，但是排除特定id（修改时用来排除自己）
	 * @param orgId
	 * @param name
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countByNameAndIdNot(Long orgId, String name, Long id) {
		return courseRepository.countByOrgIdAndNameAndIdNotAndDeleteFlag(orgId, name, id, DataValidity.VALID.getState());
	}
	/**
	 * count指定学校特定code的值的正常数据
	 * @param orgId
	 * @param code
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countByCode(Long orgId, String code) {
		return courseRepository.countByOrgIdAndCodeAndDeleteFlag(orgId, code, DataValidity.VALID.getState());
	}
	/**
	 * count指定学校特定code的值的正常数据，但是排除特定id（修改时用来排除自己）
	 * @param orgId
	 * @param code
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countByCodeAndIdNot(Long orgId, String code, Long id) {
		return courseRepository.countByOrgIdAndCodeAndIdNotAndDeleteFlag(orgId, code, id, DataValidity.VALID.getState());
	}
	/**
	 * 分页查询指定学校的课程的ID和name
	 * @param pageable
	 * @param orgId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<IdNameDomain> findIdName(Pageable pageable, Long orgId) {
		return courseRepository.findIdName(pageable, orgId, DataValidity.VALID.getState());
	}

	/**
	 * 分页查询指定学校的课程的ID和name
	 * @param pageable
	 * @param orgId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<IdCodeNameBase> findIdNameCode(Pageable pageable, Long orgId) {
		return courseRepository.findIdNameCode(pageable, orgId, DataValidity.VALID.getState());
	}

	@Transactional(readOnly = true)
	public Page<IdCodeNameBase> findIdNameCode(Pageable pageable, Long orgId, String name) {
		return courseRepository.findIdNameCode(pageable, orgId, name, DataValidity.VALID.getState());
	}

	@Transactional(readOnly = true)
	public Page<IdNameDomain> findIdName(Pageable pageable, Long orgId, String name) {
		return courseRepository.findIdName(pageable, orgId, name, DataValidity.VALID.getState());
	}
	@Transactional(readOnly = true)
	public Page<CourseDomain> findByOrgId(Pageable pageable, Long orgId) {
		return courseRepository.findByOrgId(pageable, orgId, DataValidity.VALID.getState());
	}
	@Transactional(readOnly = true)
	public Page<CourseDomain> findByOrgIdAndName(Pageable pageable, Long orgId, String name) {
		return courseRepository.findByOrgIdAndName(pageable, orgId, name, DataValidity.VALID.getState());
	}
	@Transactional(readOnly = true)
	public List<Course> findByIds(Set<Long> ids) {
		return courseRepository.findByIdIn(ids);
	}

	public List<Course> save(List<Course> courses) {
		return courseRepository.save(courses);
	}
	@Transactional(readOnly = true)
	public List<Course> findByOrgIdAndCodes(Long orgId, Set<String> codes) {
		return courseRepository.findByOrgIdAndCodeIn(orgId, codes);
	}
	
	@Transactional(readOnly = true)
	public List<Course> findByCodeIn(Long orgId, Set<String> codes) {
		return courseRepository.findByOrgIdAndCodeInAndDeleteFlag(orgId, codes, DataValidity.VALID.getState());
	}
	
//	@Transactional(readOnly = true)
//    public List<String> findNamesByOrgIdAndCodes(Long orgId, Set<String> names) {
//        return courseRepository.findNamesByOrgIdAndCodeInAndDeleteFlag(orgId, names, DataValidity.VALID.getState());
//    }
	
	@Transactional(readOnly = true)
	public List<String> findCodesByOrgIdAndCodes(Long orgId, Set<String> codes) {
		return courseRepository.findCodesByOrgIdAndCodeInAndDeleteFlag(orgId, codes, DataValidity.VALID.getState());
	}
	//*************************************************************以下部分处理页面调用逻辑**********************************************************************//	
	/**
	 * 保存课程
	 * @param userId
	 * @param cd
	 * @return
	 */
	public Course save(Long userId, CourseDomain cd) {
		Course course = new Course();
		if (null == cd.getOrgId() || cd.getOrgId() <= 0) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "学校ID是必须的");
		}
//		Long c = countByName(cd.getOrgId(), cd.getName());
//		if(c > 0) {
//			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "课程名称[" + cd.getName() + "]已经存在");
//		}
		if (!StringUtils.isEmpty(cd.getCode())) {
			Long c = countByCode(cd.getOrgId(), cd.getCode());
			if(c > 0) {
				throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "课程编码[" + cd.getCode() + "]已经存在");
			}
			course.setCode(cd.getCode());
		}
		course.setName(cd.getName());
		course.setOrgId(cd.getOrgId());
		course.setCredit(cd.getCredit());
		course.setCourseDesc(cd.getCourseDesc());
		course.setCourseProp(cd.getCourseProp());
		course.setCreatedBy(userId);
		course.setLastModifiedBy(userId);
		course.setSource(cd.getSource());
		course.setCreatedBy(cd.getUserId());
		course.setLastModifiedBy(cd.getUserId());
		return save(course);
	}
	/**
	 * 修改课程信息
	 * @param userId
	 * @param cd
	 * @return
	 */
	public Course update(Long userId, CourseDomain cd) {
		if(null == cd.getId() || cd.getId() <= 0) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "ID是必须的");
		}
		Course course = findById(cd.getId());
		if(null == course) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据课程ID[" + cd.getId() + "]查找不到对应的课程数据");
		}
		
//		Long c = countByNameAndIdNot(cd.getOrgId(), cd.getName(), course.getId());
//		if(c > 0) {
//			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "课程名称[" + cd.getName() + "]已经存在");
//		}
		if (!StringUtils.isEmpty(cd.getCode())) {
			Long c = countByCodeAndIdNot(cd.getOrgId(), cd.getCode(), cd.getId());
			if(c > 0) {
				throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "课程编码[" + cd.getCode() + "]已经存在");
			}
			course.setCode(cd.getCode());
		}
		course.setName(cd.getName());
		course.setOrgId(cd.getOrgId());
		course.setCredit(cd.getCredit());
		course.setCourseDesc(cd.getCourseDesc());
		course.setCourseProp(cd.getCourseProp());
		course.setCreatedBy(userId);
		course.setLastModifiedBy(userId);
		course.setLastModifiedDate(new Date());
		CourseDomainV2 courseDomainV2 = new CourseDomainV2();
		course = save(course);
		courseDomainV2.setId(course.getId());
		courseDomainV2.setCode(course.getCode());
		courseDomainV2.setOrgId(course.getOrgId());
		courseDomainV2.setCourseDesc(course.getCourseDesc());
		courseDomainV2.setCourseProp(course.getCourseProp());
		courseDomainV2.setCredit(course.getCredit());
		courseDomainV2.setName(course.getName());
		List<CourseDomainV2> courseDomainV2List = new ArrayList<>();
		courseDomainV2List.add(courseDomainV2);
        dataSynService.sendUpdateCourse(courseDomainV2List);
		return course;
	}

	/**
	 * 获取课程详情信息
	 * @param id
	 * @return
	 */
	public CourseDomain get(Long id) {
		if(null == id || id <= 0) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "ID是必须的");
		}
		Course course = findById(id);
		if(null == course) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据课程ID[" + id + "]查找不到对应的课程数据");
		}
		CourseDomain c = new CourseDomain(course.getId(), course.getName(), course.getCode(), course.getCourseDesc(), course.getCreatedDate());
		c.setCredit(course.getCredit());
		c.setCourseProp(course.getCourseProp());
		return c;
	}

	/**
	 * 删除课程信息
	 * @param userId
	 * @param id
	 */
	public void delete(Long userId, Long id) {
		if(null == id || id <= 0) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "ID是必须的");
		}
		Course course = findById(id);
		if(null == course) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据课程ID[" + id + "]查找不到对应的课程数据");
		}
		course.setDeleteFlag(DataValidity.INVALID.getState());
		course.setLastModifiedBy(userId);
		course.setLastModifiedDate(new Date());
		save(course);
	}
	/**
	 * 按照查询条件分页查询指定学校的课程信息列表
	 * @param r
	 * @param pageable
	 * @param orgId 		required
	 * @param name
	 * @return
	 */
	@Transactional(readOnly = true)
	public Map<String, Object> queryList(Map<String, Object> r, Pageable pageable, Long orgId, String name) {
		PageDomain p = new PageDomain();
		p.setPageNumber(pageable.getPageNumber());
		p.setPageSize(pageable.getPageSize());
		r.put(ApiReturnConstants.PAGE, p);

		Page<CourseDomain> page;
		if(!liquibase.util.StringUtils.isEmpty(name)) {
			page = findByOrgIdAndName(pageable, orgId, name);
		} else {
			page = findByOrgId(pageable, orgId);
		}
		p.setTotalElements(page.getTotalElements());
		p.setTotalPages(page.getTotalPages());
		r.put(ApiReturnConstants.DATA, page.getContent());

		return r;
	}
	/**
	 * 分页查询指定学校课程id、name、code字段
	 * 主要用于下拉列表
	 * @param r
	 * @param pageable
	 * @param orgId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Map<String, Object> dropList(Map<String, Object> r, Pageable pageable, Long orgId, String name) {
		PageDomain p = new PageDomain();
		r.put(ApiReturnConstants.PAGE, p);
		p.setPageNumber(pageable.getPageNumber());
		p.setPageSize(pageable.getPageSize());
		
		Page<IdCodeNameBase> page;
		if (StringUtils.isEmpty(name)) {
			page = findIdNameCode(pageable, orgId);
		} else {
			page = findIdNameCode(pageable, orgId, name);
		}
		
		p.setTotalElements(page.getTotalElements());
		p.setTotalPages(page.getTotalPages());

		r.put(ApiReturnConstants.DATA, page.getContent());
		return r;
	}

	public List<CourseDomain> queryByIds(Set<Long> ids) {
		List<Course> data = findByIds(ids);
		List<CourseDomain> r = new ArrayList<>();
		for (Course c : data) {
			CourseDomain d = new CourseDomain(c.getId(), c.getCode(), c.getName(), c.getOrgId());
			d.setCredit(c.getCredit());
			d.setCourseProp(c.getCourseProp());
			d.setCourseDesc(c.getCourseDesc());
			r.add(d);
		}
		return r;
	}

	private void fillOutData(List<CourseDomain> outData, List<Course> list) {
		if (list.size() > 0) {
			for (Course course : list) {
				CourseDomain d = new CourseDomain(course.getId(), course.getCode(), course.getName(),course.getOrgId());
				d.setCredit(course.getCredit());
				outData.add(d);
			}
		}
	}


//	public List<CourseDomain> saveAll(List<CourseDomain> ds) {
//		List<Course> list = new ArrayList<>();
//		for(CourseDomain d : ds) {
//			Course course = new Course();
//			course.setName(d.getName());
//			course.setCode(d.getCode());
//			course.setCredit(d.getCredit());
//			course.setCreatedBy(d.getUserId());
//			course.setLastModifiedBy(d.getUserId());
//			course.setOrgId(d.getOrgId());
//			list.add(course);
//		}
//		List<CourseDomain> outData = new ArrayList<>();
//		if (list.size() > 0) {
//			list = save(list);
//			fillOutData(outData, list);
//		}
//		return outData;
//	}
//
//	public List<CourseDomain> updateAll(List<CourseDomain> ds) {
//		Set<Long> ids = new HashSet<>();
//		Map<Long, CourseDomain> wds = new HashMap<>();
//		for (CourseDomain d : ds) {
//			ids.add(d.getId());
//			wds.put(d.getId(), d);
//		}
//		List<Course> list = findByIds(ids);
//		for(Course course : list) {
//			CourseDomain d = wds.get(course.getId());
//			if (null != d) {
//				course.setName(d.getName());
//				course.setCode(d.getCode());
//				course.setCredit(d.getCredit());
//				course.setLastModifiedBy(d.getUserId());
//			}
//		}
//		List<CourseDomain> outData = new ArrayList<>();
//		if (list.size() > 0) {
//			list = save(list);
//			fillOutData(outData, list);
//		}
//		return outData;
//	}
//
//	public List<CourseDomain> deleteAll(List<CourseDomain> ds) {
//		List<CourseDomain> outData = new ArrayList<>();
//		Set<Long> ids = new HashSet<>();
//		Long userId = 0L;
//		for (CourseDomain d : ds) {
//			ids.add(d.getId());
//			if(userId <= 0) {
//				userId = d.getUserId();
//			}
//		}
//		List<Course> list = findByIds(ids);
//		for(Course course : list) {
//			course.setDeleteFlag(DataValidity.INVALID.getState());
//			course.setLastModifiedBy(userId);
//		}
//		list = save(list);
//		fillOutData(outData, list);
//		return outData;
//	}
	
	public CourseRedisData importCourseMsg(Long orgId, Long userId) {
		if (null == orgId || orgId <= 0) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "orgId是必须的");
		}
		if (null == userId || userId <= 0) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "userId是必须的");
		}
		RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
		redisTokenStore.setPrefix(RedisTokenStore.ORG_EXCEL_SIGN);
		return redisTokenStore.readCourseRedisData(orgId.toString());
	}
	
	public void importCourseData(Long orgId, MultipartFile file, Long userId) {
		if (null == orgId || orgId <= 0) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "orgId是必须的");
		}
		if (null == userId || userId <= 0) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "userId是必须的");
		}
		RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
		redisTokenStore.setPrefix(RedisTokenStore.ORG_EXCEL_SIGN);
		CourseRedisData courseRedisData = redisTokenStore.readCourseRedisData(orgId.toString());
		if (null != courseRedisData && ExcelImportStatus.DOING.getState() == courseRedisData.getState()) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "上一次课程的导入任务还在进行中，请稍等一会再试");
		}
		List<CourseExcelDomain> excelDatas = excelBasedataHelper.readCoursesFromInputStream(file);
		if (null == excelDatas || excelDatas.size() <= 0) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "没有读取到任何数据");
		}
		courseRedisData = new CourseRedisData();
		courseRedisData.setState(ExcelImportStatus.DOING.getState());//处理中
		redisTokenStore.storeCourseRedisData(orgId.toString(), courseRedisData);
		asyncTaskBase.importCourses(this, orgId, userId, excelDatas, redisTokenStore);
	}
	
	public void processCourseData(Long orgId, Long userId, List<CourseExcelDomain> excelDatas) {
		Organization org = organizationService.findById(orgId);
		if (null == org) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据学校ID[" + orgId + "]没有查找到对应的学校信息");
		}
        
        Set<String> codeSet = new HashSet<>();// 使用Set是为了进行contains，判断值的否唯一

        boolean hasError = false;
        String msg = null;
		Map<String, CourseExcelDomain> cache = new HashMap<>();
        for (CourseExcelDomain d : excelDatas) {
        	//错误判断提示--与Excel表中数据比对
            if (!StringUtils.isEmpty(d.getCode())) {//判断编号不为空、""时
                if (codeSet.contains(d.getCode())) {//比较编号的value
                    msg = "课程编码在此Excel中已经存在";
                    d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);// 不为空、""时拼接前后的错误信息
                    hasError = true;//返回错误信息判断标记为true
                } else {
                    codeSet.add(d.getCode());//set进Set集合方便比较唯一值
					cache.put(d.getCode(), d);
                }
            }
            if (StringUtils.isEmpty(d.getCode())) {
            	msg = "课程编码是必须的";
            	d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
            	hasError = true;
            } 
            if (StringUtils.isEmpty(d.getName())) {
                msg = "课程名称是必须的";
                d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
                hasError = true;
            }
        }
        Map<String, Course> codeCourseMap = new HashMap<>();
        List<Course> list = null;// find datas
        if (codeSet.size() > 0) {
            list = findByCodeIn(orgId, codeSet);
            for (Course course : list) {
            	codeCourseMap.put(course.getCode(), course);
            }
        }

        //错误判断提示--与数据中数据比对
//        List<Course> data = new ArrayList<>();
		List<Course> dataAdd = new ArrayList<>();
		List<Course> dataUpdate = new ArrayList<>();
        for (CourseExcelDomain d : excelDatas) {
			Course c = new Course();
			if (!StringUtils.isEmpty(d.getCode())) {
//				Course course = codeCourseMap.keySet().get(d.getCode());
				if (!codeCourseMap.keySet().contains(d.getCode())) {//新增
					dataAdd.add(c);
				} else {//修改
					dataUpdate.add(c);
				}
			} else {
				continue;
			}
            c.setName(d.getName());
            c.setCreatedBy(userId);
            c.setOrgId(orgId);
            c.setCode(d.getCode());
            if (!StringUtils.isEmpty(d.getCourseProp())) {
            	c.setCourseProp(d.getCourseProp());
            }

            if (!StringUtils.isEmpty(d.getCredit())) {
				Pattern pattern = Pattern.compile("[1-9]\\d*\\.?\\d*");
				Matcher isNum = pattern.matcher(d.getCredit());
				if (!isNum.matches()) {
					msg = "分数应是数字格式并且为正数";
					d.setMsg(null == d.getMsg() ? msg : d.getMsg() + "," + msg);
					hasError = true;
				}
				c.setCredit(Float.valueOf(d.getCredit()));
			}
        }
        if (hasError) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "验证失败");
        }
        List<Course> dataSave = new ArrayList<>();
        for (Course c : dataUpdate) {
			Course d = codeCourseMap.get(c.getCode());
            if (null != d) {
            	d.setName(c.getName());
            	d.setCourseProp(c.getCourseProp());
            	d.setCredit(c.getCredit());
				dataSave.add(d);
            } else {
				hasError = true;
			}
        }

		if (hasError) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "修改逻辑异常");
		}
		dataSave.addAll(dataAdd);

        save(dataSave);
        for (Course c : dataSave) {//回填已经保存的数据的ID
        	CourseExcelDomain d = cache.get(c.getCode());
            if (null != d) {
                d.setId(c.getId());
            }
        }
    }
}