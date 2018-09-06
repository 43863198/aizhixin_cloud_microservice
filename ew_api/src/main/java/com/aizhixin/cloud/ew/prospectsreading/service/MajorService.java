package com.aizhixin.cloud.ew.prospectsreading.service;

import com.aizhixin.cloud.ew.common.PageInfo;
import com.aizhixin.cloud.ew.common.RestResult;
import com.aizhixin.cloud.ew.common.core.DataValidity;
import com.aizhixin.cloud.ew.common.core.PageUtil;
import com.aizhixin.cloud.ew.prospectsreading.domain.IdNameDomain;
import com.aizhixin.cloud.ew.prospectsreading.domain.MajorDomain;
import com.aizhixin.cloud.ew.prospectsreading.domain.MajorQueryListDomain;
import com.aizhixin.cloud.ew.prospectsreading.domain.ProspectTypeDomain;
import com.aizhixin.cloud.ew.prospectsreading.entity.Major;
import com.aizhixin.cloud.ew.prospectsreading.repository.MajorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Transactional
public class MajorService {
	@Autowired
	private MajorRepository majorRepository;

	/**
	 * 保存实体
	 * 
	 * @param entity
	 * @return
	 */
	public Major save(Major entity) {
		return majorRepository.save(entity);
	}

	/**
	 * 根据实体ID查询实体
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public Major findById(Long id) {
		return majorRepository.findByIdAndDeleteFlag(id, DataValidity.VALID.getState());
	}

	@Transactional(readOnly = true)
	public long countByNameAndDeleteFlag(String name) {
		return majorRepository.countByNameAndDeleteFlag(name, DataValidity.VALID.getState());
	}

	@Transactional(readOnly = true)
	public long countByNameAndIdNotAndDeleteFlag(String name, Long id) {
		return majorRepository.countByNameAndIdNotAndDeleteFlag(name, id, DataValidity.VALID.getState());
	}

	@Transactional(readOnly = true)
	public Page<MajorQueryListDomain> findAllPage(Pageable page) {
		return majorRepository.findAll(page, DataValidity.VALID.getState());
	}

	@Transactional(readOnly = true)
	public Page<MajorQueryListDomain> findByNamePage(Pageable page, String name) {
		return majorRepository.findByName(page, name, DataValidity.VALID.getState());
	}

	@Transactional(readOnly = true)
	public Page<MajorQueryListDomain> findByPublishStatusPage(Pageable page) {
		return majorRepository.findByPublishStatus(page, 1, DataValidity.VALID.getState());
	}

	// *************************************************************以下部分处理页面调用逻辑**********************************************************************//
	/**
	 * 保存
	 * 
	 * @param majorDomain
	 * @param userId
	 * @return
	 */
	public RestResult save(MajorDomain majorDomain, Long userId) {
		RestResult r = new RestResult();
		Major major = new Major();
		if (StringUtils.isEmpty(majorDomain.getName())) {
			return new RestResult("error", "专业名称不能为空");
		}
		if (StringUtils.isEmpty(majorDomain.getType())) {
			return new RestResult("error", "专业类型不能为空");
		}
		if (StringUtils.isEmpty(majorDomain.getDesc())) {
			return new RestResult("error", "专业描述不能为空");
		}
		long c = countByNameAndDeleteFlag(majorDomain.getName());
		if (c > 0) {// 如果通过名称查询的总条数大于0
			return new RestResult("error", "专业名称[" + majorDomain.getName() + "]已经存在");
		}

		major.setDesc(majorDomain.getDesc());
		major.setName(majorDomain.getName());
		major.setPublishStatus(0);
		major.setType(majorDomain.getType());
		major.setCreatedBy(userId);
		major.setLastModifiedBy(userId);
		major = save(major);

		r.setResult("success");
		r.setId(major.getId());
		return r;
	}

	/**
	 * 更新
	 * 
	 * @param majorDomain
	 * @param userId
	 * @return
	 */
	public RestResult upadate(MajorDomain majorDomain, Long userId) {
		RestResult r = new RestResult();
		if (null == majorDomain.getId() || majorDomain.getId() <= 0) {
			return new RestResult("error", "id不能为空");
		}
		Major major = findById(majorDomain.getId());
		if (null == major) {
			return new RestResult("error", "根据id[" + majorDomain.getId() + "]查找不到对应的专业信息");
		}
		if (StringUtils.isEmpty(majorDomain.getName())) {
			return new RestResult("error", "专业名称不能为空");
		}
		if (StringUtils.isEmpty(majorDomain.getType())) {
			return new RestResult("error", "专业类型不能为空");
		}
		if (StringUtils.isEmpty(majorDomain.getDesc())) {
			return new RestResult("error", "专业描述不能为空");
		}
		long c = countByNameAndIdNotAndDeleteFlag(majorDomain.getName(), majorDomain.getId());
		if (c > 0) {
			return new RestResult("error", "专业名称[" + majorDomain.getName() + "]已经存在");
		}

		major.setDesc(majorDomain.getDesc());
		major.setName(majorDomain.getName());
		major.setPublishStatus(0);
		major.setType(majorDomain.getType());
		major.setLastModifiedBy(userId);
		major = save(major);

		r.setResult("success");
		r.setId(major.getId());
		return r;
	}

	/**
	 * 删除
	 * 
	 * @param id
	 * @param userId
	 * @return
	 */
	public RestResult delete(Long id, Long userId) {
		RestResult r = new RestResult();
		if (null == id || id <= 0) {
			r.setResult("fail");
			r.setMsg("id不能为空");
			return r;
		}
		Major major = findById(id);
		if (null == major) {
			r.setResult("fail");
			r.setMsg("根据id不能查找到对应的数据");
			return r;
		}
		major.setDeleteFlag(DataValidity.INVALID.getState());
		major.setLastModifiedBy(userId);
		save(major);

		r.setResult("success");
		return r;
	}

	/**
	 * 分页查询
	 * 
	 * @param pageable
	 * @param name
	 * @return
	 */
	@Transactional(readOnly = true)
	public PageInfo<MajorQueryListDomain> list(Pageable pageable, String name) {
		PageInfo<MajorQueryListDomain> r = new PageInfo<>();
		Page<MajorQueryListDomain> page = null;
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

	@Transactional(readOnly = true)
	public MajorDomain get(Long id) {
		MajorDomain majorDomain = new MajorDomain();
		if (null != id && id > 0) {
			Major major = findById(id);
			if (null != major) {
				majorDomain.setId(major.getId());
				majorDomain.setName(major.getName());
				majorDomain.setType(major.getType());
				majorDomain.setDesc(major.getDesc());
				majorDomain.setPublishStatus(major.getPublishStatus());
			}
		}
		return majorDomain;
	}

	public RestResult publish(Long id, Long userId) {
		RestResult r = new RestResult();
		if (null == id || id <= 0) {
			r.setResult("fail");
			r.setMsg("id不能为空");
			return r;
		}
		Major major = findById(id);
		if (null == major) {
			r.setResult("fail");
			r.setMsg("根据id不能查找到对应的数据");
			return r;
		}
		if (0 != major.getPublishStatus()) {
			r.setResult("fail");
			r.setMsg("该专业已经发布，不能重复发布");
		}
		major.setPublishStatus(1);
		major.setLastModifiedBy(userId);
		save(major);

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
		Major major = findById(id);
		if (null == major) {
			r.setResult("fail");
			r.setMsg("根据id不能查找到对应的数据");
			return r;
		}
		if (1 != major.getPublishStatus()) {
			r.setResult("fail");
			r.setMsg("该专业还没有发布，不能取消发布");
		}
		major.setPublishStatus(0);
		major.setLastModifiedBy(userId);
		save(major);

		r.setResult("success");
		return r;
	}

	public List<ProspectTypeDomain> getAllMajorAndType() {
		List<ProspectTypeDomain> ds = new ArrayList<>();
		Page<MajorQueryListDomain> page = findByPublishStatusPage(PageUtil.createNoErrorPageRequest(1, Integer.MAX_VALUE));
		Map<String, ProspectTypeDomain> ch = new HashMap<>();
		for (MajorQueryListDomain d : page.getContent()) {
			ProspectTypeDomain ptd = ch.get(d.getType());
			if (null == ptd) {
				ptd = new ProspectTypeDomain();
				ptd.setTypeName(d.getType());

				List<IdNameDomain> ps  = new ArrayList<>();
				ptd.setSubNodes(ps);

				ds.add(ptd);
				ch.put(d.getType(), ptd);
			}

			ptd.getSubNodes().add(new IdNameDomain(d.getId(), d.getName()));
		}
		return ds;
	}

	public List<IdNameDomain> getFiveMajor() {
		List<IdNameDomain> ds = new ArrayList<>();
		Page<MajorQueryListDomain> page = findByPublishStatusPage(PageUtil.createNoErrorPageRequest(1, 5));
		for (MajorQueryListDomain d : page.getContent()) {
			ds.add(new IdNameDomain(d.getId(), d.getName()));
		}
		return ds;
	}
}
