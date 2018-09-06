package com.aizhixin.cloud.ew.announcement.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.ew.announcement.domain.announcementListDomain;
import com.aizhixin.cloud.ew.announcement.domain.announcementsDomain;
import com.aizhixin.cloud.ew.announcement.entity.announcementList;
import com.aizhixin.cloud.ew.announcement.repository.announcementListRepository;
import com.aizhixin.cloud.ew.common.PageData;
import com.aizhixin.cloud.ew.common.PageDomain;
import com.aizhixin.cloud.ew.common.core.DataValidity;
import com.aizhixin.cloud.ew.common.core.ErrorCode;
import com.aizhixin.cloud.ew.common.dto.AccountDTO;
import com.aizhixin.cloud.ew.common.exception.ExceptionMessage;
import com.aizhixin.cloud.ew.common.util.DateFormatUtil;


@Component
@Transactional
public class announcementListService {
	@Autowired
	private announcementListRepository repository;

	/**
	 * 保存实体
	 * 
	 * @param entity
	 * @return
	 */
	public announcementList save(announcementList entity) {
		return repository.save(entity);
	}

	/**
	 * 根据id、deleteFlag查询实体
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public announcementList findByIdAndDeleteFlag(Long id) {
		return repository.findByIdAndDeleteFlag(id, DataValidity.VALID.getState());
	}

	/**
	 * 根据title、deleteFlag查询实体数量
	 * 
	 * @param title
	 * @return
	 */
	@Transactional(readOnly = true)
	public long countByTitleAndDeleteFlag(String title) {
		return repository.countByTitleAndDeleteFlag(title, DataValidity.VALID.getState());
	}

	/**
	 * 根据deleteFlag、organId分页查询所有
	 * 
	 * @param page
	 * @param organId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<announcementsDomain> findAllPage(Pageable page, Long organId) {
		return repository.findAll(page, DataValidity.VALID.getState(), organId);
	}

	/**
	 * 根据title、deleteFlag、organId分页查询
	 * 
	 * @param page
	 * @param title
	 * @param organId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<announcementsDomain> findByTitlePage(Pageable page, String title, Long organId) {
		return repository.findByTitle(page, DataValidity.VALID.getState(), title, organId);
	}

	/**
	 * 根据id、title、deleteFlag查询实体数量
	 * 
	 * @param id
	 * @param title
	 * @return
	 */
	@Transactional(readOnly = true)
	public long countByIdNotAndTitleAndDeleteFlag(Long id, String title) {
		return repository.countByIdNotAndTitleAndDeleteFlag(id, title, DataValidity.VALID.getState());
	}

	/**
	 * 根据publishStatus、deleteFlag分页查询
	 * 
	 * @param page
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<announcementsDomain> findByPublishStatusPage(Pageable page, Long organId) {
		return repository.findByPublishStatus(page, DataValidity.VALID.getState(), 1, "10", organId);
	}

	/**
	 * 根据organId、publishStatus、deleteFlag分页查询所有
	 * 
	 * @param page
	 * @param organId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<announcementsDomain> findAllPageByOrganIdAndPublishStatus(Pageable page, Long organId) {
		return repository.findAllByOrganIdAndPublishStatus(page, DataValidity.VALID.getState(), organId, 1);
	}

	/**
	 * 根据organId、publishStatus、deleteFlag、type分页查询所有
	 * 
	 * @param page
	 * @param organId
	 * @param type
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<announcementsDomain> findAllPageByOrganIdAndPublishStatusAndType(Pageable page, Long organId,
			String type) {
		return repository.findAllByOrganIdAndPublishStatusAndType(page, DataValidity.VALID.getState(), organId, 1,
				type);
	}

	// *************************************************************以下部分处理页面调用逻辑**********************************************************************//
	/**
	 * 新增公告
	 * 
	 * @param listDomain
	 * @param account
	 * @return
	 */
	public ResponseEntity<ExceptionMessage> save(announcementListDomain listDomain, AccountDTO account) {
		ExceptionMessage e = new ExceptionMessage();
		announcementList list = new announcementList();
		if (StringUtils.isEmpty(listDomain.getTitle())) {
			e = new ExceptionMessage(ErrorCode.ID_IS_REQUIRED, "公告标题不能为空");
			return new ResponseEntity<>(e, HttpStatus.EXPECTATION_FAILED);
		}
//		if (StringUtils.isEmpty(listDomain.getPicUrl())) {
//			e = new ExceptionMessage(ErrorCode.ID_IS_REQUIRED, "公告图片不能为空");
//			return new ResponseEntity<>(e, HttpStatus.EXPECTATION_FAILED);
//		}
		long c = countByTitleAndDeleteFlag(listDomain.getTitle());
		if (c > 0) {
			e = new ExceptionMessage(ErrorCode.FIELD_IS_REPLICATION, "公告标题[" + listDomain.getTitle() + "]已经存在");
			return new ResponseEntity<>(e, HttpStatus.EXPECTATION_FAILED);
		}
		list.setTitle(listDomain.getTitle());
		list.setType(listDomain.getType());
		list.setContent(listDomain.getContent());
		list.setPicUrl(listDomain.getPicUrl());
		list.setOrganId(account.getOrganId());
		list.setOrganName(account.getOrganName());
		list.setPublishStatus(0);
		list.setDeleteFlag(DataValidity.VALID.getState());
		list.setCreatedBy(account.getId());
		list.setLastModifiedBy(account.getId());
		list = save(list);
		e.setCause("success");
		return new ResponseEntity<>(e, HttpStatus.OK);
	}

	/**
	 * 修改公告
	 * 
	 * @param listDomain
	 * @param account
	 * @return
	 */
	public ResponseEntity<ExceptionMessage> update(announcementListDomain listDomain, AccountDTO account) {
		ExceptionMessage e = new ExceptionMessage();
		if (null == listDomain.getId() || listDomain.getId() <= 0) {
			e = new ExceptionMessage(ErrorCode.ID_IS_REQUIRED, "id不能为空");
			return new ResponseEntity<>(e, HttpStatus.EXPECTATION_FAILED);
		}
		announcementList list = findByIdAndDeleteFlag(listDomain.getId());
		if (null == list) {
			e = new ExceptionMessage(ErrorCode.ID_NOT_FOUND_OBJECT, "根据id[" + listDomain.getId() + "]查找不到对应的公告信息");
			return new ResponseEntity<>(e, HttpStatus.EXPECTATION_FAILED);
		}
		if (StringUtils.isEmpty(listDomain.getTitle())) {
			e = new ExceptionMessage(ErrorCode.ID_IS_REQUIRED, "公告标题不能为空");
			return new ResponseEntity<>(e, HttpStatus.EXPECTATION_FAILED);
		}
//		if (StringUtils.isEmpty(listDomain.getPicUrl())) {
//			e = new ExceptionMessage(ErrorCode.ID_IS_REQUIRED, "公告图片不能为空");
//			return new ResponseEntity<>(e, HttpStatus.EXPECTATION_FAILED);
//		}
		long c = countByIdNotAndTitleAndDeleteFlag(listDomain.getId(), listDomain.getTitle());
		if (c > 0) {
			e = new ExceptionMessage(ErrorCode.FIELD_IS_REPLICATION, "公告标题[" + listDomain.getTitle() + "]已经存在");
			return new ResponseEntity<>(e, HttpStatus.EXPECTATION_FAILED);
		}
		list.setTitle(listDomain.getTitle());
		list.setType(listDomain.getType());
		list.setContent(listDomain.getContent());
		list.setPicUrl(listDomain.getPicUrl());
		list.setOrganId(account.getOrganId());
		list.setOrganName(account.getOrganName());
		list.setPublishStatus(0);
		list.setDeleteFlag(DataValidity.VALID.getState());
		list.setCreatedBy(account.getId());
		list.setLastModifiedBy(account.getId());
		list.setLastModifiedDate(new Date());
		list = save(list);
		e.setCause("success");
		return new ResponseEntity<>(e, HttpStatus.OK);
	}

	/**
	 * 删除公告
	 * 
	 * @param id
	 * @param userId
	 * @return
	 */
	public ResponseEntity<ExceptionMessage> delete(Long id, Long userId) {
		ExceptionMessage e = new ExceptionMessage();
		if (null == id || id <= 0) {
			e = new ExceptionMessage(ErrorCode.ID_IS_REQUIRED, "id不能为空");
			return new ResponseEntity<>(e, HttpStatus.EXPECTATION_FAILED);
		}
		announcementList list = findByIdAndDeleteFlag(id);
		if (null == list) {
			e = new ExceptionMessage(ErrorCode.ID_NOT_FOUND_OBJECT, "根据id[" + id + "]查找不到对应的公告信息");
			return new ResponseEntity<>(e, HttpStatus.EXPECTATION_FAILED);
		}
		list.setDeleteFlag(DataValidity.INVALID.getState());
		list.setLastModifiedBy(userId);
		save(list);
		e.setCause("success");
		return new ResponseEntity<>(e, HttpStatus.OK);
	}

	/**
	 * 批量删除公告
	 * 
	 * @param ids
	 * @param userId
	 * @return
	 */
	public ResponseEntity<ExceptionMessage> deletes(Long[] ids, Long userId) {
		ExceptionMessage e = new ExceptionMessage();
		if (null == ids || ids.length <= 0) {
			e = new ExceptionMessage(ErrorCode.ID_IS_REQUIRED, "id不能为空");
			return new ResponseEntity<>(e, HttpStatus.EXPECTATION_FAILED);
		}
		for (int i = 0; i < ids.length; i++) {
			announcementList list = findByIdAndDeleteFlag(ids[i]);
			if (null == list) {
				e = new ExceptionMessage(ErrorCode.ID_NOT_FOUND_OBJECT, "根据id[" + ids[i] + "]查找不到对应的公告信息");
				return new ResponseEntity<>(e, HttpStatus.EXPECTATION_FAILED);
			}
			list.setDeleteFlag(DataValidity.INVALID.getState());
			list.setLastModifiedBy(userId);
			save(list);
			e.setCause("success");
		}
		return new ResponseEntity<>(e, HttpStatus.OK);
	}

	/**
	 * 发布公告
	 * 
	 * @param id
	 * @param userId
	 * @return
	 */
	public ResponseEntity<ExceptionMessage> publish(Long id, Long userId) {
		ExceptionMessage e = new ExceptionMessage();
		if (null == id || id <= 0) {
			e = new ExceptionMessage(ErrorCode.ID_IS_REQUIRED, "id不能为空");
			return new ResponseEntity<>(e, HttpStatus.EXPECTATION_FAILED);
		}
		announcementList list = findByIdAndDeleteFlag(id);
		if (null == list) {
			e = new ExceptionMessage(ErrorCode.ID_NOT_FOUND_OBJECT, "根据id[" + id + "]查找不到对应的公告信息");
			return new ResponseEntity<>(e, HttpStatus.EXPECTATION_FAILED);
		}
		if (0 != list.getPublishStatus()) {
			e = new ExceptionMessage(ErrorCode.PARAMS_CONFLICT, "该公告已经发布，不能重复发布");
			return new ResponseEntity<>(e, HttpStatus.EXPECTATION_FAILED);
		}
		list.setPublishStatus(1);
		list.setLastModifiedBy(userId);
		list.setPublishDate(DateFormatUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		save(list);
		e.setCause("success");
		return new ResponseEntity<>(e, HttpStatus.OK);
	}

	/**
	 * 批量发布公告
	 * 
	 * @param ids
	 * @param userId
	 * @return
	 */
	public ResponseEntity<ExceptionMessage> publishs(Long[] ids, Long userId) {
		ExceptionMessage e = new ExceptionMessage();
		if (null == ids || ids.length <= 0) {
			e = new ExceptionMessage(ErrorCode.ID_IS_REQUIRED, "id不能为空");
			return new ResponseEntity<>(e, HttpStatus.EXPECTATION_FAILED);
		}
		for (int i = 0; i < ids.length; i++) {
			announcementList list = findByIdAndDeleteFlag(ids[i]);
			if (null == list) {
				e = new ExceptionMessage(ErrorCode.ID_NOT_FOUND_OBJECT, "根据id[" + ids[i] + "]查找不到对应的公告信息");
				return new ResponseEntity<>(e, HttpStatus.EXPECTATION_FAILED);
			}
			if (0 != list.getPublishStatus()) {
				e = new ExceptionMessage(ErrorCode.PARAMS_CONFLICT, "id为[" + ids[i] + "]的公告已经发布，不能重复发布");
				return new ResponseEntity<>(e, HttpStatus.EXPECTATION_FAILED);
			}
			list.setPublishStatus(1);
			list.setLastModifiedBy(userId);
			list.setPublishDate(DateFormatUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
			save(list);
			e.setCause("success");
		}
		return new ResponseEntity<>(e, HttpStatus.OK);
	}

	/**
	 * 取消发布公告
	 * 
	 * @param id
	 * @param userId
	 * @return
	 */
	public ResponseEntity<ExceptionMessage> unPublish(Long id, Long userId) {
		ExceptionMessage e = new ExceptionMessage();
		if (null == id || id <= 0) {
			e = new ExceptionMessage(ErrorCode.ID_IS_REQUIRED, "id不能为空");
			return new ResponseEntity<>(e, HttpStatus.EXPECTATION_FAILED);
		}
		announcementList list = findByIdAndDeleteFlag(id);
		if (null == list) {
			e = new ExceptionMessage(ErrorCode.ID_NOT_FOUND_OBJECT, "根据id[" + id + "]查找不到对应的公告信息");
			return new ResponseEntity<>(e, HttpStatus.EXPECTATION_FAILED);
		}
		if (1 != list.getPublishStatus()) {
			e = new ExceptionMessage(ErrorCode.PARAMS_CONFLICT, "id为[" + id + "]的公告还没有发布，不能取消发布");
			return new ResponseEntity<>(e, HttpStatus.EXPECTATION_FAILED);
		}
		list.setPublishStatus(0);
		list.setLastModifiedBy(userId);
		save(list);
		e.setCause("success");
		return new ResponseEntity<>(e, HttpStatus.OK);
	}

	/**
	 * 公告详情
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public announcementListDomain get(Long id) {
		announcementListDomain domain = new announcementListDomain();
		if (null != id && id > 0) {
			announcementList list = findByIdAndDeleteFlag(id);
			if (null != list) {
				domain.setId(list.getId());
				domain.setTitle(list.getTitle());
				domain.setContent(list.getContent());
				domain.setPicUrl(list.getPicUrl());
				domain.setPublishDate(list.getPublishDate());
				domain.setPublishStatus(list.getPublishStatus());
				domain.setType(list.getType());
			}
		}
		return domain;
	}

	/**
	 * 分页查询公告列表
	 * 
	 * @param pageable
	 * @param title
	 * @return
	 */
	@Transactional(readOnly = true)
	public PageData<announcementsDomain> list(Pageable pageable, String title, Long organId) {
		Page<announcementsDomain> page = null;
		if (StringUtils.isEmpty(title)) {
			page = findAllPage(pageable, organId);
		} else {
			page = findByTitlePage(pageable, "%" + title + "%", organId);
		}
		PageData<announcementsDomain> pageData = new PageData<>();
		pageData.setData(page.getContent());
		pageData.setPage(new PageDomain(page.getTotalElements(), page.getTotalPages(), pageable.getPageNumber(),
				pageable.getPageSize()));
		return pageData;
	}

	/**
	 * 分页查询公告列表BypublishStatus
	 * 
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	public PageData<announcementsDomain> listByPublishStatus(Pageable pageable, Long organId) {
		Page<announcementsDomain> page = null;
		if (organId != null && organId > 0) {
			page = findByPublishStatusPage(pageable, organId);
		}
		PageData<announcementsDomain> pageData = new PageData<>();
		pageData.setData(page.getContent());
		pageData.setPage(new PageDomain(page.getTotalElements(), page.getTotalPages(), pageable.getPageNumber(),
				pageable.getPageSize()));
		return pageData;
	}

	/**
	 * 分页查询公告列表ByOrganIdAndPublishStatus
	 * 
	 * @param pageable
	 * @param organId
	 * @param type
	 * @return
	 */
	@Transactional(readOnly = true)
	public PageData<announcementsDomain> listByOrganIdAndPublishStatusAndType(Pageable pageable, Long organId,
			String type) {
		Page<announcementsDomain> page = null;
		if (organId != null && organId > 0) {
			if (StringUtils.isEmpty(type)) {
				page = findAllPageByOrganIdAndPublishStatus(pageable, organId);
			} else {
				page = findAllPageByOrganIdAndPublishStatusAndType(pageable, organId, type);
			}
		}
		PageData<announcementsDomain> pageData = new PageData<>();
		pageData.setData(page.getContent());
		pageData.setPage(new PageDomain(page.getTotalElements(), page.getTotalPages(), pageable.getPageNumber(),
				pageable.getPageSize()));
		return pageData;
	}
}
