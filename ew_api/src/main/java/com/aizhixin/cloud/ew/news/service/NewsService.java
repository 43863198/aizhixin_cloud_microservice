package com.aizhixin.cloud.ew.news.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.ew.common.core.DataValidity;
import com.aizhixin.cloud.ew.common.core.PublishStatus;
import com.aizhixin.cloud.ew.common.dto.AccountDTO;
import com.aizhixin.cloud.ew.news.domain.NewsDomain;
import com.aizhixin.cloud.ew.news.entity.News;
import com.aizhixin.cloud.ew.news.entity.Organs;
import com.aizhixin.cloud.ew.news.repository.NewsRepository;
import com.aizhixin.cloud.ew.news.repository.OrgansRepository;

/**
 * 新闻管理操作后台API
 * 
 * @author Rigel.ma
 *
 */
@Component
@Transactional
public class NewsService {

	@Autowired
	private NewsRepository newsRepository;

	@Autowired
	private OrgansRepository organsRepository;

	/**
	 * 新增新闻信息
	 * 
	 * @param newsDomain
	 * @param account
	 * @return
	 */
	public Map<String, Object> addNews(NewsDomain newsDomain, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		if (StringUtils.isEmpty(newsDomain.getTitle())) {
			result.put("error", "新闻标题不能为空");
			return result;
		}
		if (StringUtils.isEmpty(newsDomain.getTitle())) {
			result.put("error", "新闻标题不能为空");
			return result;
		}
		if (StringUtils.isEmpty(newsDomain.getPublishDate())) {
			result.put("error", "新闻发布时间不能为空");
			return result;
		}
		if (StringUtils.isEmpty(newsDomain.getOrganIDs())) {
			result.put("error", "选择的学校id不能为空");
			return result;
		}
		News news = new News();
		news.setTitle(newsDomain.getTitle());
		news.setContent(newsDomain.getContent());
		news.setPicUrl1(newsDomain.getPicUrl());
		news.setPicUrl2(newsDomain.getPicUr2());
		news.setPicUrl3(newsDomain.getPicUr3());
		news.setHitCount(0l);
		news.setPublished(newsDomain.getPublished());
		news.setAllFlag(newsDomain.getAllFlag());
		news.setCreatedBy(account.getId());
		news.setCreatedDate(newsDomain.getPublishDate());
		news.setOrgan(account.getOrganName());
		news.setOrganId(account.getOrganId());
		news.setOrganIds(newsDomain.getOrganIDs());
		newsRepository.save(news);
		Long newsId = news.getId();
		result.put("newsId", newsId);
		// 发布学校和新闻的关系
		if (newsDomain.getOrganIDs().length() > 0) {
			String[] organIds = newsDomain.getOrganIDs().split(",");
			for (int i = 0; i < organIds.length; i++) {
				String organId = organIds[i];
				Organs organs = new Organs();
				organs.setNewsId(newsId);
				organs.setOrganId(Long.parseLong(organId));
				organs.setCreatedBy(account.getId());
				organs.setCreatedDate(newsDomain.getPublishDate());
				organsRepository.save(organs);
			}
		}
		return result;
	}

	/**
	 * 修改新闻信息
	 * 
	 * @param newsDomain
	 * @param account
	 * @return
	 */
	public Map<String, Object> updateNews(NewsDomain newsDomain, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		if (null == newsDomain.getId() || newsDomain.getId() <= 0) {
			result.put("error", "id不能为空");
			return result;
		}
		News news = newsRepository.findByIdAndDeleteFlag(newsDomain.getId(), DataValidity.VALID.getState());
		if (null == news) {
			result.put("error", "根据id[" + newsDomain.getId() + "]查找不到对应的新闻");
			return result;
		}
		if (StringUtils.isEmpty(newsDomain.getTitle())) {
			result.put("error", "新闻标题不能为空");
			return result;
		}
		if (StringUtils.isEmpty(newsDomain.getTitle())) {
			result.put("error", "新闻标题不能为空");
			return result;
		}
		if (StringUtils.isEmpty(newsDomain.getPublishDate())) {
			result.put("error", "新闻发布时间不能为空");
			return result;
		}
		if (StringUtils.isEmpty(newsDomain.getOrganIDs())) {
			result.put("error", "选择的学校id不能为空");
			return result;
		}
		news.setTitle(newsDomain.getTitle());
		news.setContent(newsDomain.getContent());
		news.setPicUrl1(newsDomain.getPicUrl());
		news.setPicUrl2(newsDomain.getPicUr2());
		news.setPicUrl3(newsDomain.getPicUr3());
		news.setPublished(newsDomain.getPublished());
		news.setAllFlag(newsDomain.getAllFlag());
		news.setLastModifiedBy(account.getId());
		news.setLastModifiedDate(new Date());
		news.setOrganIds(newsDomain.getOrganIDs());
		newsRepository.save(news);
		// 之前发布的学校标记为已删除
		List<Organs> organss = organsRepository.findByNewsIdAndDeleteFlag(news.getId(), DataValidity.VALID.getState());
		if (null == organss || organss.size() <= 0) {
			result.put("error", "该新闻id[" + news.getId() + "]的学校和新闻关系表无数据");
			return result;
		}
		for (int j = 0; j < organss.size(); j++) {
			Organs organ = organss.get(j);
			organ.setDeleteFlag(DataValidity.INVALID.getState());
			organsRepository.save(organ);
		}
		// 发布学校和新闻的关系
		if (newsDomain.getOrganIDs().length() > 0) {
			String[] organIds = newsDomain.getOrganIDs().split(",");
			for (int i = 0; i < organIds.length; i++) {
				String organId = organIds[i];
				Organs organs = new Organs();
				organs.setNewsId(news.getId());
				organs.setOrganId(Long.parseLong(organId));
				organs.setCreatedBy(account.getId());
				organs.setCreatedDate(newsDomain.getPublishDate());
				organsRepository.save(organs);
			}
		}
		result.put("newsId", news.getId());
		return result;
	}

	/**
	 * 新闻详情
	 * 
	 * @param newsId
	 * @return
	 */
	public Map<String, Object> newsDetail(Long newsId) {
		Map<String, Object> result = new HashMap<>();
		News news = newsRepository.findByIdAndDeleteFlag(newsId, DataValidity.VALID.getState());
		if (null == news) {
			result.put("error", "根据id[" + newsId + "]查找不到对应的新闻");
			return result;
		}
		news.setHitCount(news.getHitCount() + 1);
		newsRepository.save(news);
		result.put("news", news);
		return result;
	}

	/**
	 * 删除新闻
	 * 
	 * @param newsId
	 * @param account
	 * @return
	 */
	public Map<String, Object> deleteNews(Long newsId, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		News news = newsRepository.findOne(newsId);
		if (null == news) {
			result.put("error", "根据id[" + newsId + "]查找不到对应的新闻");
			return result;
		}
		news.setDeleteFlag(DataValidity.INVALID.getState());
		news.setLastModifiedBy(account.getId());
		news.setLastModifiedDate(new Date());
		newsRepository.save(news);
		result.put("result", "success");
		return result;
	}

	/**
	 * 批量删除新闻
	 * 
	 * @param newsIds
	 * @param account
	 * @return
	 */
	public Map<String, Object> deleteNewss(Long[] newsIds, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		if (null == newsIds || newsIds.length <= 0) {
			result.put("error", "新闻id不能为空");
			return result;
		}
		for (int i = 0; i < newsIds.length; i++) {
			News news = newsRepository.findOne(newsIds[i]);
			if (null == news) {
				result.put("error", "根据id[" + newsIds[i] + "]查找不到对应的新闻");
				return result;
			}
			news.setDeleteFlag(DataValidity.INVALID.getState());
			news.setLastModifiedBy(account.getId());
			news.setLastModifiedDate(new Date());
			newsRepository.save(news);
		}
		result.put("result", "success");
		return result;
	}

	/**
	 * 新闻发布
	 * 
	 * @param newsId
	 * @param account
	 * @return
	 */
	public Map<String, Object> Publish(Long newsId, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		News news = newsRepository.findByIdAndDeleteFlag(newsId, DataValidity.VALID.getState());
		if (null == news) {
			result.put("error", "根据id[" + newsId + "]查找不到对应的新闻");
			return result;
		}
		if (PublishStatus.UNPUBLISHED.getState() != news.getPublished()) {
			result.put("error", "该新闻已经发布，不能重复发布");
			return result;
		}
		news.setPublished(PublishStatus.PUBLISHED.getState());
		news.setLastModifiedBy(account.getId());
		news.setLastModifiedDate(new Date());
		newsRepository.save(news);
		result.put("result", "success");
		return result;
	}

	/**
	 * 批量新闻发布
	 * 
	 * @param newsIds
	 * @param account
	 * @return
	 */
	public Map<String, Object> Publishes(Long[] newsIds, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		if (null == newsIds || newsIds.length <= 0) {
			result.put("error", "新闻id不能为空");
			return result;
		}
		for (int i = 0; i < newsIds.length; i++) {
			News news = newsRepository.findByIdAndDeleteFlag(newsIds[i], DataValidity.VALID.getState());
			if (null == news) {
				result.put("error", "根据id[" + newsIds[i] + "]查找不到对应的新闻");
				return result;
			}
			if (PublishStatus.UNPUBLISHED.getState() != news.getPublished()) {
				result.put("error", "该新闻已经发布，不能重复发布");
				return result;
			}
			news.setPublished(PublishStatus.PUBLISHED.getState());
			news.setLastModifiedBy(account.getId());
			news.setLastModifiedDate(new Date());
			newsRepository.save(news);
		}
		result.put("result", "success");
		return result;
	}

	/**
	 * 取消新闻发布
	 * 
	 * @param newsId
	 * @param account
	 * @return
	 */
	public Map<String, Object> noPublish(Long newsId, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		News news = newsRepository.findByIdAndDeleteFlag(newsId, DataValidity.VALID.getState());
		if (null == news) {
			result.put("error", "根据id[" + newsId + "]查找不到对应的新闻");
			return result;
		}
		if (PublishStatus.PUBLISHED.getState() != news.getPublished()) {
			result.put("error", "该新闻还未发布，不能取消发布");
			return result;
		}
		news.setPublished(PublishStatus.UNPUBLISHED.getState());
		news.setLastModifiedBy(account.getId());
		news.setLastModifiedDate(new Date());
		newsRepository.save(news);
		result.put("result", "success");
		return result;
	}

}
