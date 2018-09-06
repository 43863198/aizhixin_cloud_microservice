/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.service;


import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.DataValidity;
import com.aizhixin.cloud.orgmanager.common.core.ErrorCode;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.company.core.OrgCustomeOrNot;
import com.aizhixin.cloud.orgmanager.company.domain.OrgDomain;
import com.aizhixin.cloud.orgmanager.company.entity.Organization;
import com.aizhixin.cloud.orgmanager.company.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.Map.Entry;


/**
 * 组织机构相关操作业务逻辑处理
 * @author zhen.pan
 *
 */
@Component
@Transactional
public class OrganizationService {
	@Autowired
	private EntityManager em;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private BaseDataCacheService baseDataCacheService;

//	@Autowired
//	@Qualifier("diandianJdbcTemplate")
//	private JdbcTemplate jdbcTemplate;
	
	/**
	 * 保存实体
	 * @param org
	 * @return
	 */
	public Organization save(Organization org) {
		org = organizationRepository.save(org);
		baseDataCacheService.cacheOrg(new OrgDomain(org));
		return org;
	}
	/**
	 * 根据ID查询实体
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public Organization findById(Long id) {
		return organizationRepository.findOne(id);
	}
	/**
	 * count所有code的值的正常数据
	 * @param code
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countByCode(String code) {
		return organizationRepository.countByCodeAndDeleteFlag(code, DataValidity.VALID.getState());
	}
	/**
	 * count所有name的值的正常数据
	 * @param name
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countByName(String name) {
		return organizationRepository.countByNameAndDeleteFlag(name, DataValidity.VALID.getState());
	}
	/**
	 * count所有domainName的值的正常数据
	 * @param domainName
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countByDomainName(String domainName) {
		return organizationRepository.countByDomainNameAndDeleteFlag(domainName, DataValidity.VALID.getState());
	}
	/**
	 * count所有code的值的正常数据，但是排除特定id（修改时用来排除自己）
	 * @param code
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countByCodeAndIdNot(String code, Long id) {
		return organizationRepository.countByCodeAndIdNotAndDeleteFlag(code, id, DataValidity.VALID.getState());
	}
	/**
	 * count所有name的值的正常数据，但是排除特定id（修改时用来排除自己）
	 * @param name
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countByNameAndIdNot(String name, Long id) {
		return organizationRepository.countByNameAndIdNotAndDeleteFlag(name, id, DataValidity.VALID.getState());
	}
	/**
	 * count所有domainName的值的正常数据，但是排除特定id（修改时用来排除自己）
	 * @param domainName
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countByDomainNameAndIdNot(String domainName, Long id) {
		return organizationRepository.countByDomainNameAndIdNotAndDeleteFlag(domainName, id, DataValidity.VALID.getState());
	}
	/**
	 * 根据域名查询组织机构列表
	 * @param domainName
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Organization> findByDomainName(String domainName) {
		return organizationRepository.findByDomainNameAndDeleteFlag(domainName, DataValidity.VALID.getState());
	}
	/**
	 * 分页查询组织结构的ID和name
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<IdNameDomain> findIdName(Pageable pageable) {
		return organizationRepository.findIdName(pageable, DataValidity.VALID.getState());
	}

	@Transactional(readOnly = true)
	public List<IdNameDomain> findIdNameByIds(Set<Long> ids) {
		return organizationRepository.findIdNameByIdIn(ids);
	}

//	public Organization findDiandianById(Long id) {
//		String sql = "SELECT id, name,code FROM dd_organ WHERE id=?";
//		Organization org = new Organization();
//		jdbcTemplate.query(sql, new Long[]{id}, new int[]{Types.BIGINT}, new ResultSetExtractor<Organization>() {
//			public Organization extractData(ResultSet rs) throws SQLException, DataAccessException {
//				if(rs.next()) {
//					org.setId(rs.getLong(1));
//					org.setName(rs.getString(2));
//					org.setCode(rs.getString(3));
//				}
//				return null;
//			}
//		});
//		return org;
//	}
//	public List<IdNameDomain> findDiandianAllOrgIdName() {
//		String sql = "SELECT id, name FROM dd_organ WHERE status=?";
//		List<IdNameDomain> list = new ArrayList<>();
//		jdbcTemplate.query(sql, new String[]{"created"}, new int[]{Types.VARCHAR}, new ResultSetExtractor<IdNameDomain>() {
//			public IdNameDomain extractData(ResultSet rs) throws SQLException, DataAccessException {
//				while (rs.next()) {
//					IdNameDomain d = new IdNameDomain();
//					d.setId(rs.getLong(1));
//					d.setName(rs.getString(2));
//					list.add(d);
//				}
//				return null;
//			}
//		});
//		return list;
//	}

	public List<Organization> findByIdIn(Set<Long> ids) {
		return organizationRepository.findByIdIn(ids);
	}

	public List<Organization> findAll() {
		return organizationRepository.findByDeleteFlag(DataValidity.VALID.getState());
	}
	//*************************************************************以下部分处理页面调用逻辑**********************************************************************//	
	/**
	 * 属性copy
	 * @param org
	 * @param od
	 */
	private void orgPropertiesCopy(Organization org, OrgDomain od) {
		org.setName(od.getName());
		org.setCode(od.getCode());
		org.setDomainName(od.getDomainName());
		org.setLogo(od.getLogo());
		org.setCustomer(org.getCustomer());
		org.setProvince(od.getProvince());
		org.setRectangleLogo(od.getLptLogo());
		org.setSquareLogo(od.getPtLogo());
	}
	/**
	 * 保存组织机构信息
	 * @param userId
	 * @param od
	 * @return
	 */
	public Organization save(Long userId, OrgDomain od) {
		Organization org = new Organization();
		Long c = countByName(od.getName());
		if(c > 0) {
			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "该[" + od.getName() + "]值已经存在");
		}
		c = countByDomainName(od.getDomainName());
		if(c > 0) {
			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "该[" + od.getDomainName() + "]值已经存在");
		}
		c = countByCode(od.getCode());
		if(c > 0) {
			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "该[" + od.getCode() + "]值已经存在");
		}

		orgPropertiesCopy(org, od);
		if (null == od.getCustomer()) {
			org.setCustomer(OrgCustomeOrNot.CLOSE.getState());
		} else {
			if (OrgCustomeOrNot.CLOSE.getState().intValue() != od.getCustomer() &&  OrgCustomeOrNot.SETUP.getState().intValue() != od.getCustomer()) {
				throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "是否启动定制化首页，只能是启用10，停用20");
			}
		}
		org.setCreatedBy(userId);
		org.setLastModifiedBy(userId);
		return organizationRepository.save(org);
	}
	/**
	 * 修改组织机构信息
	 * @param userId
	 * @param od
	 * @return
	 */
	public Organization update(Long userId, OrgDomain od) {
		if(null == od.getId() || od.getId() <= 0) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "ID是必须的");
		}
		Organization org = findById(od.getId());
		if(null == org) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "对应ID的对象不存在");
		}
		//验证name、code、domainName
		Long c = countByNameAndIdNot(od.getName(), od.getId());
		if(c > 0) {
			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "该名称[" + od.getName() + "]值已经存在");
		}
		org.setName(od.getName());
		c = countByCodeAndIdNot(od.getCode(), od.getId());
		if(c > 0) {
			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "该编码[" + od.getCode() + "]值已经存在");
		}
		org.setCode(od.getCode());
		c = countByDomainNameAndIdNot(od.getDomainName(), od.getId());
		if(c > 0) {
			throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "该域名[" + od.getDomainName() + "]值已经存在");
		}
		if (null == od.getCustomer()) {
			org.setCustomer(OrgCustomeOrNot.CLOSE.getState());
		} else {
			if (OrgCustomeOrNot.CLOSE.getState().intValue() != od.getCustomer() &&  OrgCustomeOrNot.SETUP.getState().intValue() != od.getCustomer()) {
				throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "是否启动定制化首页，只能是启用10，停用20");
			}
		}
		org.setDomainName(od.getDomainName());
		if(!StringUtils.isEmpty(od.getProvince())) {
			org.setProvince(od.getProvince().trim());
		}
		if(!StringUtils.isEmpty(od.getLogo())) {
			org.setLogo(od.getLogo());
		}
		org.setLastModifiedBy(userId);
		org.setLastModifiedDate(new Date());
		organizationRepository.save(org);
		return org;
	}
	/**
	 * 删除组织机构信息
	 * @param userId
	 * @param id
	 */
	public void delete(Long userId, Long id) {
		if(null == id || id <= 0) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "ID是必须的");
		}
		Organization org = findById(id);
		if(null == org) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "对应ID的对象不存在");
		}
		org.setDeleteFlag(DataValidity.INVALID.getState());
		org.setLastModifiedBy(userId);
		org.setLastModifiedDate(new Date());
		organizationRepository.save(org);
		//还需要删除对应的管理员
	}

	public OrgDomain get(Long id) {
		OrgDomain d = null;
		d = baseDataCacheService.readOrg(id);
		if (null != d) {
			return d;
		} else {
			d = new OrgDomain ();
		}
		Organization org = findById(id);
		if (null != org) {
			d.setId(org.getId());
			d.setName(org.getName());
			d.setDomainName(org.getDomainName());
			d.setCode(org.getCode());
			d.setCustomer(org.getCustomer());
			d.setLogo(org.getLogo());
			d.setProvince(org.getProvince());
			d.setLptLogo(org.getRectangleLogo());
			d.setPtLogo(org.getSquareLogo());
			d.setCreatedDate(org.getCreatedDate());
		}
		return d;
	}

	/**
	 * 验证组织结构的code和domainName
	 * @param r
	 * @param od
	 * @return
	 */
	@Transactional(readOnly = true)
	public Map<String, Object> check(Map<String, Object> r, OrgDomain od) {
		long c = countByDomainName(od.getDomainName());
		if(c > 0) {
			r.put("code", Boolean.FALSE);
		} else {
			r.put("code", Boolean.TRUE);
		}
		c = countByCode(od.getCode());
		if(c > 0) {
			r.put("domainName", Boolean.FALSE);
		} else {
			r.put("domainName", Boolean.TRUE);
		}
		return r;
	}
	/**
	 * 按照域名查找组织机构
	 * @param domainname
	 * @return
	 */
	@Transactional(readOnly = true)
	public OrgDomain findByDomainname(String domainname) {
		List<Organization> list = findByDomainName(domainname);
		OrgDomain o = new OrgDomain();
		if(list.size() > 0) {
			Organization org = list.get(0);
			o.setId(org.getId());
			o.setCode(org.getCode());
			o.setDomainName(org.getDomainName());
			o.setName(org.getName());
			o.setProvince(org.getProvince());
			o.setLogo(org.getLogo());
			o.setPtLogo(org.getSquareLogo());
			o.setLptLogo(org.getRectangleLogo());
			o.setCreatedDate(org.getCreatedDate());
			o.setCustomer(org.getCustomer());
			if (null == org.getCustomer() || 0 == org.getCustomer()) {
				o.setCustomer(OrgCustomeOrNot.CLOSE.getState());
			}
		} else {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据域名(" + domainname + ")没有找到相关的组织结构信息");
		}
		return o;
	}
	/**
	 * 按照查询条件分页查询组织机构信息列表
	 * @param pageable
	 * @param name
	 * @param code
	 * @param province
	 * @return
	 */
	@Transactional(readOnly = true)
	public PageData<OrgDomain> queryList(Pageable pageable,
			String name, String code, String province) {
		PageData<OrgDomain> pd = new PageData<>();
		Map<String, Object> condition = new HashMap<String, Object>();
		StringBuilder chql = new StringBuilder(
				"select count(o.id) from com.aizhixin.cloud.orgmanager.company.entity.Organization o where o.deleteFlag = :deleteFlag");
		StringBuilder hql = new StringBuilder(
				"select new com.aizhixin.cloud.orgmanager.company.domain.OrgDomain(o.id, o.name, o.code, o.domainName, o.logo, o.province, o.createdDate, o.customer) from com.aizhixin.cloud.orgmanager.company.entity.Organization o where o.deleteFlag = :deleteFlag");

		condition.put("deleteFlag", DataValidity.VALID.getState());
		if (!StringUtils.isEmpty(province)) {
			hql.append(" and o.province like :province");
			chql.append(" and o.province like :province");
			condition.put("province", "%" + province + "%");
		}
		if (!StringUtils.isEmpty(code)) {
			hql.append(" and o.code like :code");
			chql.append(" and o.code like :code");
			condition.put("code", "%" + code + "%");
		}
		if (!StringUtils.isEmpty(name)) {
			hql.append(" and o.name like :name");
			chql.append(" and o.name like :name");
			condition.put("name", "%" + name + "%");
		}
		hql.append(" order by o.id DESC");

		Query q = em.createQuery(chql.toString());
		for (Entry<String, Object> e : condition.entrySet()) {
			q.setParameter(e.getKey(), e.getValue());
		}
		Long count = (Long) q.getSingleResult();
		if (count <= 0) {
			return pd;
		}
		TypedQuery<OrgDomain> tq = em.createQuery(hql.toString(), OrgDomain.class);
		for (Entry<String, Object> e : condition.entrySet()) {
			tq.setParameter(e.getKey(), e.getValue());
		}
		tq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
		tq.setMaxResults(pageable.getPageSize());

		pd.setData(tq.getResultList());
		pd.getPage().setTotalElements(count);
		pd.getPage().setPageNumber(pageable.getPageNumber());
		pd.getPage().setPageSize(pageable.getPageSize());
		pd.getPage().setTotalPages(PageUtil.cacalatePagesize(count, pd.getPage().getPageSize()));

		return pd;
	}
	@Transactional(readOnly = true)
	public PageData<OrgDomain> queryList2(Pageable pageable, String name, String code, String province) {
		PageData<OrgDomain> pd = new PageData<>();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<OrgDomain> criteriaQuery = criteriaBuilder.createQuery(OrgDomain.class);
		Root<Organization> root = criteriaQuery.from(Organization.class);
		CompoundSelection<OrgDomain> o2 = criteriaBuilder.construct(OrgDomain.class, root.get("id"), root.get("name"), root.get("code"), root.get("domainName"), root.get("logo"), root.get("province"), root.get("createdDate"));
		criteriaQuery.select(o2);
		Predicate predicate = criteriaBuilder.equal(root.get("deleteFlag"), DataValidity.VALID.getState());
		if (!StringUtils.isEmpty(province)) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("province"), "%" + province + "%"));
		}
		if (!StringUtils.isEmpty(code)) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("code"), "%" + code + "%"));
		}
		if (!StringUtils.isEmpty(name)) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("name"), "%" + name + "%"));
		}

//		Specification<OrgDomain> specification = new Specification<OrgDomain>() {
//			@Override
//			public Predicate toPredicate(Root<OrgDomain> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
//				Root<Organization> org = criteriaQuery.from(Organization.class);
//				CompoundSelection<OrgDomain> o = criteriaBuilder.construct(OrgDomain.class, org.get("id"), org.get("name"), org.get("code"), org.get("domainName"), org.get("logo"), org.get("province"), org.get("createdDate"));
//				criteriaQuery.select(o);
//				Predicate predicate = criteriaBuilder.equal(root.get("deleteFlag"), DataValidity.VALID.getState());
//				if (!StringUtils.isEmpty(province)) {
//					predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("province"), "%" + province + "%"));
//				}
//				if (!StringUtils.isEmpty(code)) {
//					predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("code"), "%" + code + "%"));
//				}
//				if (!StringUtils.isEmpty(name)) {
//					predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("name"), "%" + name + "%"));
//				}
//				return predicate;
//			}
//		};
//		Pageable pageRequest = new PageRequest(pageable.getPageNumber(),pageable.getPageSize(), Sort.Direction.DESC,"id");
		criteriaQuery.where(predicate);
		List<OrgDomain> list = em.createQuery(criteriaQuery).getResultList();
//		Page<OrgDomain> page = organizationRepository.findAll(specification,pageRequest);
//		System.out.println("-------------------------------------------------page.getTotalElements()" + page.getTotalElements());
		for(OrgDomain o : list) {
			System.out.println(o);
		}
		return pd;
	}
	/**
	 * 分页查询组织机构的id、name字段
	 * 主要用于下拉列表
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	public PageData<IdNameDomain> dropList(Pageable pageable) {
		PageData<IdNameDomain> pd = new PageData<>();
		Page<IdNameDomain> page = findIdName(pageable);
		pd.getPage().setTotalElements(page.getTotalElements());
		pd.getPage().setPageNumber(pageable.getPageNumber());
		pd.getPage().setPageSize(pageable.getPageSize());
		pd.getPage().setTotalPages(page.getTotalPages());

		pd.setData(page.getContent());
		return pd;
	}
}
