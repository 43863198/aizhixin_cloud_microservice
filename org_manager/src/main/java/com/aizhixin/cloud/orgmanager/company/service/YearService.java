/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.service;

/**
 * 学年相关操作业务逻辑处理
 * 
 * @author 郑宁
 *
 */
//@Component
//@Transactional
//public class YearService {
//	@Autowired
//	private YearRepository yearRepository;
//	@Autowired
//	private SemesterService semesterService;
//	@Autowired
//	private OrganizationService organizationService;
//	@Autowired
//	private WeekService weekService;
//
//	/**
//	 * 保存实体
//	 *
//	 * @param week
//	 * @return
//	 */
//	public Year save(Year year) {
//		return yearRepository.save(year);
//	}
//
//	/**
//	 * 根据ID查询实体
//	 *
//	 * @param id
//	 * @return
//	 */
//	@Transactional(readOnly = true)
//	public Year findById(String id) {
//		return yearRepository.findOne(id);
//	}
//
//	/**
//	 * 按名称统计学年数量
//	 *
//	 * @param orgId
//	 * @param name
//	 * @return
//	 */
//	@Transactional(readOnly = true)
//	public Long countByName(Long orgId, String name) {
//		return yearRepository.countByOrgIdAndNameAndDeleteFlag(orgId, name,
//				DataValidity.VALID.getState());
//	}
//
//	/**
//	 * count指定学校的所有name的值的正常数据，但是排除特定id（修改时用来排除自己）
//	 *
//	 * @param name
//	 * @param id
//	 * @return
//	 */
//	@Transactional(readOnly = true)
//	public Long countByNameAndIdNot(Long orgId, String name, String id) {
//		return yearRepository.countByOrgIdAndNameAndIdNotAndDeleteFlag(orgId,
//				name, id, DataValidity.VALID.getState());
//	}
//
//	/**
//	 * 按name分页查询
//	 *
//	 * @param pageable
//	 * @param orgId
//	 * @param name
//	 * @return
//	 */
//	@Transactional(readOnly = true)
//	public Page<YearDomain> findName(Pageable pageable, Long orgId, String name) {
//		return yearRepository.findByOrgIdAndName(pageable, orgId,
//				DataValidity.VALID.getState(), name);
//	}
//
//	/**
//	 * 根据指定学校查询学年列表
//	 *
//	 * @param pageable
//	 * @param orgId
//	 * @return
//	 */
//	@Transactional(readOnly = true)
//	public Page<YearDomain> find(Pageable pageable, Long orgId) {
//		return yearRepository.findByPage(pageable,
//				DataValidity.VALID.getState(), orgId);
//	}
//
//	// *************************************************************以下部分处理页面调用逻辑**********************************************************************//
//
//	/**
//	 * 保存学年和学期信息
//	 *
//	 * @param yd
//	 * @return
//	 */
//	public Year save(YearDomain yd) {
//
//		// Organization org = organizationService.findById(yd.getOrgId());
//		// if (null == org) {
//		// throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT,
//		// "根据组织ID没有查找到对应的组织机构信息");
//		// }
//		Long c = countByName(yd.getOrgId(), yd.getName());
//		if (null != c && c.longValue() > 0) {
//			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION,
//					"该【学年名称】值已经存在");
//		}
//		Year year = new Year();
//		UUID uuid = UUID.randomUUID();
//		year.setId(uuid.toString());
//		year.setName(yd.getName());
//		year.setOrgId(yd.getOrgId());
//		year.setCreatedBy(yd.getUserId());
//		year.setLastModifiedBy(yd.getUserId());
//		year = save(year);
//		if (null != yd.getSemesterList()) {
//			for (SemesterDomain sd : yd.getSemesterList()) {
//				if (null != sd) {
//					sd.setOrgId(yd.getOrgId());
//					sd.setUserId(yd.getUserId());
//					sd.setYearId(uuid.toString());
//					Semester semeter = semesterService.save(sd);
//					weekService.createAllWeek(semeter);
//				}
//			}
//		}
//
//		return year;
//	}
//
//	/**
//	 * 修改学年和学期信息
//	 *
//	 * @param yd
//	 * @return
//	 */
//	public Year update(YearDomain yd) {
//
//		// Organization org = organizationService.findById(yd.getOrgId());
//		// if (null == org) {
//		// throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT,
//		// "根据组织ID没有查找到对应的组织机构信息");
//		// }
//		if (StringUtils.isBlank(yd.getId())) {
//			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "ID是必须的");
//		}
//		Year year = this.findById(yd.getId());
//		if (null == year) {
//			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT,
//					"对应ID的对象不存在");
//		}
//		if (null != yd.getSemesterList()) {
//			for (SemesterDomain sd : yd.getSemesterList()) {
//				if (null != sd) {
//					if (null == sd.getId() || sd.getId() <= 0) {
//						sd.setOrgId(yd.getOrgId());
//						sd.setUserId(yd.getUserId());
//						sd.setYearId(yd.getId());
//						Semester semeter = semesterService.save(sd);
//						weekService.createAllWeek(semeter);
//					}
//				}
//			}
//		}
//
//		return year;
//	}
//
//	/**
//	 * 删除学年
//	 *
//	 * @param userId
//	 * @param id
//	 */
//	public void delete(Long userId, String id) {
//		if (StringUtils.isBlank(id)) {
//			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "ID是必须的");
//		}
//		System.out.println("id: " + id);
//		Year year = findById(id);
//		if (null == year) {
//			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT,
//					"对应ID的对象不存在");
//		}
//		Long result = semesterService.countByYear(year);
//		if (null != result && result.longValue() > 0L) {
//			throw new CommonException(ErrorCode.DELETE_CONFLICT, "该学年下存在学期不能删除");
//		}
//		year.setDeleteFlag(DataValidity.INVALID.getState());
//		year.setLastModifiedBy(userId);
//		year.setLastModifiedDate(new Date());
//		save(year);
//	}
//
//	@Transactional(readOnly = true)
//	public Map<String, Object> queryList(Map<String, Object> r,
//			Pageable pageable, Long orgId, String name) {
//		Page<YearDomain> page = null;
//		// Organization org = organizationService.findById(orgId);
//		PageDomain p = new PageDomain();
//		p.setPageNumber(pageable.getPageNumber());
//		p.setPageSize(pageable.getPageSize());
//		r.put(ApiReturnConstants.PAGE, p);
//
//		// if (null == org) {
//		// r.put(ApiReturnConstants.DATA, new ArrayList<SemesterDomain>());
//		// return r;
//		// }
//		if (!StringUtils.isEmpty(name)) {
//			page = findName(pageable, orgId, name);
//		} else {
//			page = find(pageable, orgId);
//		}
//
//		List<YearDomain> yearList = page.getContent();
//		if (null != yearList && yearList.size() > 0) {
//			for (YearDomain yd : yearList) {
//				if (null != yd) {
//					Year year = new Year();
//					year.setId(yd.getId());
//					yd.setSemesterList(semesterService.findByYear(year));
//				}
//			}
//		}
//		p.setTotalElements(page.getTotalElements());
//		p.setTotalPages(page.getTotalPages());
//
//		r.put(ApiReturnConstants.DATA, page.getContent());
//		return r;
//	}
//
//	@Transactional(readOnly = true)
//	public Map<String, Object> dropList(Map<String, Object> r,
//			Pageable pageable, Long orgId, String name) {
//		Page<YearDomain> page = null;
//		// Organization org = organizationService.findById(orgId);
//		PageDomain p = new PageDomain();
//		p.setPageNumber(pageable.getPageNumber());
//		p.setPageSize(pageable.getPageSize());
//		r.put(ApiReturnConstants.PAGE, p);
//
//		// if (null == org) {
//		// r.put(ApiReturnConstants.DATA, new ArrayList<SemesterDomain>());
//		// return r;
//		// }
//		if (!StringUtils.isEmpty(name)) {
//			page = findName(pageable, orgId, name);
//		} else {
//			page = find(pageable, orgId);
//		}
//
//		List<YearDomain> yearList = page.getContent();
//		if (null != yearList && yearList.size() > 0) {
//			for (YearDomain yd : yearList) {
//				if (null != yd) {
//					Year year = new Year();
//					year.setId(yd.getId());
//					yd.setSemesterIdNameList(semesterService.findIdNameByYear(year));
//				}
//			}
//		}
//		p.setTotalElements(page.getTotalElements());
//		p.setTotalPages(page.getTotalPages());
//
//		r.put(ApiReturnConstants.DATA, page.getContent());
//		return r;
//	}
//
//
//	@Transactional(readOnly = true)
//	public YearDomain get(String id) {
//
//		YearDomain yd = new YearDomain();
//		Year year = findById(id);
//		if(null != year){
//			yd.setId(year.getId());
//			yd.setName(year.getName());
//			yd.setOrgId(year.getOrgId());
//			yd.setSemesterList(semesterService.findByYear(year));
//		}
//		return yd;
//	}
//}
