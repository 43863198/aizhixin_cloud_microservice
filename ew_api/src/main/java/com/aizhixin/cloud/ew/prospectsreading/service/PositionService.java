package com.aizhixin.cloud.ew.prospectsreading.service;

import com.aizhixin.cloud.ew.common.PageInfo;
import com.aizhixin.cloud.ew.common.RestResult;
import com.aizhixin.cloud.ew.common.core.DataValidity;
import com.aizhixin.cloud.ew.common.core.PageUtil;
import com.aizhixin.cloud.ew.prospectsreading.core.PositionAbilityType;
import com.aizhixin.cloud.ew.prospectsreading.domain.IdNameDomain;
import com.aizhixin.cloud.ew.prospectsreading.domain.PositionAbilityListDomain;
import com.aizhixin.cloud.ew.prospectsreading.domain.PositionDomain;
import com.aizhixin.cloud.ew.prospectsreading.domain.PositionQueryListDomain;
import com.aizhixin.cloud.ew.prospectsreading.domain.ProspectTypeDomain;
import com.aizhixin.cloud.ew.prospectsreading.entity.Position;
import com.aizhixin.cloud.ew.prospectsreading.entity.PositionAbilityList;
import com.aizhixin.cloud.ew.prospectsreading.repository.PositionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Component
@Transactional
public class PositionService {
	@Autowired
	private PositionRepository positionRepository;
	@Autowired
	private PositionAbilityListService positionAbilityListService;

	/**
	 * 保存实体
	 * 
	 * @param entity
	 * @return
	 */
	public Position save(Position entity) {
		return positionRepository.save(entity);
	}

	/**
	 * 根据实体ID查询实体
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public Position findById(Long id) {
		return positionRepository.findByIdAndDeleteFlag(id, DataValidity.VALID.getState());
	}

	@Transactional(readOnly = true)
	public long countByName(String name) {
		return positionRepository.countByNameAndDeleteFlag(name, DataValidity.VALID.getState());
	}

	@Transactional(readOnly = true)
	public long countByNameAndIdNot(String name, Long id) {
		return positionRepository.countByNameAndIdNotAndDeleteFlag(name, id, DataValidity.VALID.getState());
	}

	@Transactional(readOnly = true)
	public Page<PositionQueryListDomain> findAllPage(Pageable page) {
		return positionRepository.findAll(page, DataValidity.VALID.getState());
	}

	@Transactional(readOnly = true)
	public Page<PositionQueryListDomain> findByNamePage(Pageable page, String name) {
		return positionRepository.findByName(page, name, DataValidity.VALID.getState());
	}

	public Position findByName(String name) {
		List<Position> list = positionRepository.findByName(name);
		if (null != list && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Transactional(readOnly = true)
	public Page<PositionQueryListDomain> findPublishStatusPage(Pageable page) {
		return positionRepository.findByPublishStatus(page, 1, DataValidity.VALID.getState());
	}

	// *************************************************************以下部分处理页面调用逻辑**********************************************************************//
	public RestResult save(PositionDomain positionDomain, Long userId) {
		RestResult r = new RestResult();
		Position position = new Position();
		if (StringUtils.isEmpty(positionDomain.getName())) {
			return new RestResult("error", "职位名称不能为空");
		}
		if (StringUtils.isEmpty(positionDomain.getType())) {
			return new RestResult("error", "职位类型不能为空");
		}
		if (StringUtils.isEmpty(positionDomain.getDesc())) {
			return new RestResult("error", "职位描述不能为空");
		}
		long c = countByName(positionDomain.getName());
		if (c > 0) {
			return new RestResult("error", "职位名称[" + positionDomain.getName() + "]已经存在");
		}

		if (null != positionDomain.getProfessionalQualitys()) {
			if (positionDomain.getProfessionalQualitys().size() < 3
					&& positionDomain.getProfessionalQualitys().size() > 0) {
				return new RestResult("error", "职素最少必须输入3条及以上数据");
			}
		}

		if (null != positionDomain.getTechnicalAbilitys()) {
			if (positionDomain.getTechnicalAbilitys().size() < 3 && positionDomain.getTechnicalAbilitys().size() > 0) {
				return new RestResult("error", "技能最少必须输入3条及以上数据");
			}
		}

		if (null != positionDomain.getKnowledges()) {
			if (positionDomain.getKnowledges().size() < 3 && positionDomain.getKnowledges().size() > 0) {
				return new RestResult("error", "知识最少必须输入3条及以上数据");
			}
		}
		position.setDesc(positionDomain.getDesc());
		position.setName(positionDomain.getName());
		position.setPublishStatus(0);
		position.setType(positionDomain.getType());
		position.setCreatedBy(userId);
		position.setLastModifiedBy(userId);
		Set<String> content = new HashSet<>();
		List<PositionAbilityList> positionAbilityLists = getAbilityFromDomain(positionDomain, position);
		for (PositionAbilityList positionAbilityList : positionAbilityLists) {
			if (content.contains(positionAbilityList.getContent())) {
				return new RestResult("error", "职位能力分类内容['" + positionAbilityList.getContent() + "']已经存在");
			} else {
				content.add(positionAbilityList.getContent());
			}
		}
		position = save(position);
		positionAbilityListService.save(positionAbilityLists);
		r.setResult("success");
		r.setId(position.getId());
		return r;
	}

	private List<PositionAbilityList> getAbilityFromDomain(PositionDomain positionDomain, Position position) {
		List<PositionAbilityList> positionAbilityLists = new ArrayList<>();
		if (null != positionDomain.getProfessionalQualitys() && positionDomain.getProfessionalQualitys().size() > 0) {
			for (PositionAbilityListDomain d : positionDomain.getProfessionalQualitys()) {
				copyAddPositionAbilityList(position, positionAbilityLists, d,
						PositionAbilityType.PROFESSIONAL_QUALITY.getState());
			}
		}

		if (null != positionDomain.getTechnicalAbilitys() && positionDomain.getTechnicalAbilitys().size() > 0) {
			for (PositionAbilityListDomain d : positionDomain.getTechnicalAbilitys()) {
				copyAddPositionAbilityList(position, positionAbilityLists, d,
						PositionAbilityType.TECHNICAL_ABILITY.getState());
			}
		}

		if (null != positionDomain.getKnowledges() && positionDomain.getKnowledges().size() > 0) {
			for (PositionAbilityListDomain d : positionDomain.getKnowledges()) {
				copyAddPositionAbilityList(position, positionAbilityLists, d, PositionAbilityType.KNOWLEDGE.getState());
			}
		}
		return positionAbilityLists;
	}

	private void copyAddPositionAbilityList(Position position, List<PositionAbilityList> positionAbilityLists,
			PositionAbilityListDomain d, Integer ability) {
		PositionAbilityList positionAbilityList = new PositionAbilityList();
		positionAbilityList.setPosition(position);
		positionAbilityList.setClassification(ability);
		positionAbilityList.setContent(d.getContent());
		positionAbilityList.setScore(d.getScore());

		positionAbilityLists.add(positionAbilityList);
	}

	public RestResult upadate(PositionDomain positionDomain, Long userId) {
		RestResult r = new RestResult();
		if (null == positionDomain.getId() || positionDomain.getId() <= 0) {
			return new RestResult("error", "id不能为空");
		}
		Position position = findById(positionDomain.getId());
		if (null == position) {
			return new RestResult("error", "根据id[" + positionDomain.getId() + "]查找不到对应的职位信息");
		}
		if (StringUtils.isEmpty(positionDomain.getName())) {
			return new RestResult("error", "职位名称不能为空");
		}
		if (StringUtils.isEmpty(positionDomain.getType())) {
			return new RestResult("error", "职位类型不能为空");
		}
		if (StringUtils.isEmpty(positionDomain.getDesc())) {
			return new RestResult("error", "职位描述不能为空");
		}
		long c = countByNameAndIdNot(positionDomain.getName(), positionDomain.getId());
		if (c > 0) {
			return new RestResult("error", "职位名称[" + positionDomain.getName() + "]已经存在");
		}

		if (null != positionDomain.getProfessionalQualitys()) {
			if (positionDomain.getProfessionalQualitys().size() < 3
					&& positionDomain.getProfessionalQualitys().size() > 0) {
				return new RestResult("error", "职素最少必须输入3条及以上数据");
			}
		}

		if (null != positionDomain.getTechnicalAbilitys()) {
			if (positionDomain.getTechnicalAbilitys().size() < 3 && positionDomain.getTechnicalAbilitys().size() > 0) {
				return new RestResult("error", "技能最少必须输入3条及以上数据");
			}
		}

		if (null != positionDomain.getKnowledges()) {
			if (positionDomain.getKnowledges().size() < 3 && positionDomain.getKnowledges().size() > 0) {
				return new RestResult("error", "知识最少必须输入3条及以上数据");
			}
		}
		position.setDesc(positionDomain.getDesc());
		position.setName(positionDomain.getName());
		position.setPublishStatus(0);
		position.setType(positionDomain.getType());
		position.setLastModifiedBy(userId);
		Set<String> content = new HashSet<>();
		List<PositionAbilityList> positionAbilityLists = getAbilityFromDomain(positionDomain, position);
		for (PositionAbilityList positionAbilityList : positionAbilityLists) {
			if (content.contains(positionAbilityList.getContent())) {
				return new RestResult("error", "职位能力分类内容['" + positionAbilityList.getContent() + "']已经存在");
			} else {
				content.add(positionAbilityList.getContent());
			}
		}
		position = save(position);
		positionAbilityListService.deleteByPosition(position);
		positionAbilityListService.save(positionAbilityLists);
		r.setResult("success");
		r.setId(position.getId());
		return r;
	}

	public RestResult delete(Long id, Long userId) {
		RestResult r = new RestResult();
		if (null == id || id <= 0) {
			r.setResult("fail");
			r.setMsg("id不能为空");
			return r;
		}
		Position position = findById(id);
		if (null == position) {
			r.setResult("fail");
			r.setMsg("根据id不能查找到对应的数据");
			return r;
		}
		position.setDeleteFlag(DataValidity.INVALID.getState());
		position.setLastModifiedBy(userId);
		save(position);

		r.setResult("success");
		return r;
	}

	@Transactional(readOnly = true)
	public PageInfo<PositionQueryListDomain> list(Pageable pageable, String name) {
		PageInfo<PositionQueryListDomain> r = new PageInfo<>();
		Page<PositionQueryListDomain> page = null;
		if (StringUtils.isEmpty(name)) {
			page = findAllPage(pageable);
		} else {
			page = findByNamePage(pageable, "%" + name + "%");
		}

		r.setData(page.getContent());
		r.setLimit(pageable.getPageSize());
		r.setOffset(pageable.getPageNumber());
		r.setPageCount(page.getTotalPages());
		r.setTotalCount(page.getTotalElements());
		return r;
	}

	private void copyPosition(Position p, PositionDomain d) {
		d.setId(p.getId());
		d.setName(p.getName());
		d.setType(p.getType());
		d.setDesc(p.getDesc());
		d.setPublishStatus(p.getPublishStatus());

		List<PositionAbilityList> abilitylist = positionAbilityListService.findByPosition(p);
		List<PositionAbilityListDomain> positionAbilityList = new ArrayList<>();
		List<PositionAbilityListDomain> technicalAbilitys = new ArrayList<>();
		List<PositionAbilityListDomain> knowledges = new ArrayList<>();
		for (PositionAbilityList pa : abilitylist) {
			PositionAbilityListDomain pd = new PositionAbilityListDomain();
			pd.setContent(pa.getContent());
			pd.setScore(pa.getScore());
			switch (pa.getClassification()) {
			case 10:
				positionAbilityList.add(pd);
				break;
			case 20:
				technicalAbilitys.add(pd);
				break;
			case 30:
				knowledges.add(pd);
				break;
			default:
			}
		}
		d.setProfessionalQualitys(positionAbilityList);
		d.setTechnicalAbilitys(technicalAbilitys);
		d.setKnowledges(knowledges);
	}

	@Transactional(readOnly = true)
	public PositionDomain get(Long id) {
		PositionDomain d = new PositionDomain();
		if (null != id && id > 0) {
			Position p = findById(id);
			if (null != p) {
				copyPosition(p, d);
			}
		}
		return d;
	}

	public RestResult publish(Long id, Long userId) {
		RestResult r = new RestResult();
		if (null == id || id <= 0) {
			r.setResult("fail");
			r.setMsg("id不能为空");
			return r;
		}
		Position position = findById(id);
		if (null == position) {
			r.setResult("fail");
			r.setMsg("根据id不能查找到对应的数据");
			return r;
		}
		if (0 != position.getPublishStatus()) {
			r.setResult("fail");
			r.setMsg("该职位已经发布，不能重复发布");
		}
		position.setPublishStatus(1);
		position.setLastModifiedBy(userId);
		save(position);

		r.setResult("success");
		return r;
	}

	public RestResult unPublish(Long id, Long userId) {
		RestResult r = new RestResult();
		if (null == id || id <= 0) {
			r.setResult("fail");
			r.setMsg("id不能为空");
			return r;
		}
		Position position = findById(id);
		if (null == position) {
			r.setResult("fail");
			r.setMsg("根据id不能查找到对应的数据");
			return r;
		}
		if (1 != position.getPublishStatus()) {
			r.setResult("fail");
			r.setMsg("该职位还没有发布，不能取消发布");
		}
		position.setPublishStatus(0);
		position.setLastModifiedBy(userId);
		save(position);

		r.setResult("success");
		return r;
	}

	private List<PositionAbilityListDomain> stringNameToPositionAbilityListDomain(List<String> ds) {
		List<PositionAbilityListDomain> list = new ArrayList<>();
		if (null != ds && ds.size() > 0) {
			for (String name : ds) {
				PositionAbilityListDomain d = new PositionAbilityListDomain();
				d.setContent(name);
				list.add(d);
			}
		}
		return list;
	}

	@Transactional(readOnly = true)
	public List<PositionAbilityListDomain> findProfessionalQualitys(Long id) {
		if (null != id && id > 0) {
			return stringNameToPositionAbilityListDomain(
					positionAbilityListService.findAilityNameByPositionAndClassification(id,
							PositionAbilityType.PROFESSIONAL_QUALITY.getState()));
		} else {
			return stringNameToPositionAbilityListDomain(positionAbilityListService
					.findAilityNameByClassification(PositionAbilityType.PROFESSIONAL_QUALITY.getState()));
		}
	}

	@Transactional(readOnly = true)
	public List<PositionAbilityListDomain> findTechnicalAbilityss(Long id) {
		if (null != id && id > 0) {
			return stringNameToPositionAbilityListDomain(positionAbilityListService
					.findAilityNameByPositionAndClassification(id, PositionAbilityType.TECHNICAL_ABILITY.getState()));
		} else {
			return stringNameToPositionAbilityListDomain(positionAbilityListService
					.findAilityNameByClassification(PositionAbilityType.TECHNICAL_ABILITY.getState()));
		}
	}

	@Transactional(readOnly = true)
	public List<PositionAbilityListDomain> findKnowledges(Long id) {
		if (null != id && id > 0) {
			return stringNameToPositionAbilityListDomain(positionAbilityListService
					.findAilityNameByPositionAndClassification(id, PositionAbilityType.KNOWLEDGE.getState()));
		} else {
			return stringNameToPositionAbilityListDomain(positionAbilityListService
					.findAilityNameByClassification(PositionAbilityType.KNOWLEDGE.getState()));
		}
	}

	@Transactional(readOnly = true)
	public PositionDomain getByName(String name) {
		Position p = findByName(name);
		if (null == p)
			return null;
		PositionDomain d = new PositionDomain();
		copyPosition(p, d);
		return d;
	}

	public List<ProspectTypeDomain> getAllPositionAndType() {
		List<ProspectTypeDomain> ds = new ArrayList<>();
		Page<PositionQueryListDomain> page = findPublishStatusPage(
				PageUtil.createNoErrorPageRequest(1, Integer.MAX_VALUE));
		Map<String, ProspectTypeDomain> ch = new HashMap<>();
		for (PositionQueryListDomain d : page.getContent()) {
			ProspectTypeDomain pqld = ch.get(d.getType());
			if (null == pqld) {
				pqld = new ProspectTypeDomain();
				pqld.setTypeName(d.getType());

				List<IdNameDomain> ps = new ArrayList<>();
				pqld.setSubNodes(ps);

				ds.add(pqld);
				ch.put(d.getType(), pqld);
			}

			pqld.getSubNodes().add(new IdNameDomain(d.getId(), d.getName()));
		}
		return ds;
	}

	public List<IdNameDomain> getFivePosition() {
		List<IdNameDomain> ds = new ArrayList<>();
		Page<PositionQueryListDomain> page = findPublishStatusPage(PageUtil.createNoErrorPageRequest(1, 5));
		for (PositionQueryListDomain d : page.getContent()) {
			ds.add(new IdNameDomain(d.getId(), d.getName()));
		}
		return ds;
	}
}
