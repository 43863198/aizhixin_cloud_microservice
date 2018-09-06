package com.aizhixin.cloud.ew.article.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.ew.article.domain.ClassificationAndLableDomain;
import com.aizhixin.cloud.ew.article.domain.ClassificationDomain;
import com.aizhixin.cloud.ew.article.domain.LableDomain;
import com.aizhixin.cloud.ew.article.domain.NewLableDomain;
import com.aizhixin.cloud.ew.article.entity.ArticleLable;
import com.aizhixin.cloud.ew.article.entity.Classification;
import com.aizhixin.cloud.ew.article.entity.Lables;
import com.aizhixin.cloud.ew.article.repository.ArticleLablesRepository;
import com.aizhixin.cloud.ew.article.repository.ClassificationsRepository;
import com.aizhixin.cloud.ew.article.repository.LablesRepository;
import com.aizhixin.cloud.ew.common.dto.AccountDTO;

@Component
@Transactional
public class ClassificationAndLabelsService {

	@Autowired
	private ClassificationsRepository classificationRepository;

	@Autowired
	private LablesRepository lablessRepository;

	@Autowired
	private ArticleLablesRepository articleLablesRepository;

	// 新增分类
	public Map<String, Object> addClassification(String name, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		if (StringUtils.isEmpty(name)) {
			result.put("error", "分类名称不能为空");
			return result;
		}
		Classification classification = new Classification();
		classification.setName(name);
		classification.setCreatedBy(account.getId());
		classification.setCreatedDate(new Date());
		classificationRepository.save(classification);
		result.put("classificationId", classification.getId());
		return result;
	}

	// 修改分类
	public Map<String, Object> modifyClassification(ClassificationDomain classificationDomain, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		if (null == classificationDomain.getClassificationId() || classificationDomain.getClassificationId() <= 0) {
			result.put("error", "分类id不能为空");
			return result;
		}
		if (StringUtils.isEmpty(classificationDomain.getClassificationName())) {
			result.put("error", "分类名称不能为空");
			return result;
		}
		Classification classification = classificationRepository.findOne(classificationDomain.getClassificationId());
		if (null == classification) {
			result.put("error", "根据id[" + classificationDomain.getClassificationId() + "]查找不到对应的分类");
			return result;
		}
		classification.setName(classificationDomain.getClassificationName());
		classification.setLastModifiedBy(account.getId());
		classification.setLastModifiedDate(new Date());
		classificationRepository.save(classification);
		result.put("classificationId", classification.getId());
		return result;
	}

	// 新增标签
	public Map<String, Object> addLable(NewLableDomain labledomain, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		if (StringUtils.isEmpty(labledomain.getLableName())) {
			result.put("error", "标签名称不能为空");
			return result;
		}
		if (null == labledomain.getClassificationId() || labledomain.getClassificationId() <= 0) {
			result.put("error", "分类id不能为空");
			return result;
		}
		Lables lable = new Lables();
		lable.setClassificationId(labledomain.getClassificationId());
		lable.setName(labledomain.getLableName());
		lable.setCreatedBy(account.getId());
		lable.setCreatedDate(new Date());
		lablessRepository.save(lable);
		result.put("id", lable.getId());
		return result;
	}

	// 修改标签
	public Map<String, Object> lableModify(NewLableDomain labledomain, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		if (null == labledomain.getLableId() || labledomain.getLableId() <= 0) {
			result.put("error", "标签id不能为空");
			return result;
		}
		if (StringUtils.isEmpty(labledomain.getLableName())) {
			result.put("error", "标签名称不能为空");
			return result;
		}
		if (null == labledomain.getClassificationId() || labledomain.getClassificationId() <= 0) {
			result.put("error", "分类id不能为空");
			return result;
		}
		Classification classification = classificationRepository.findOne(labledomain.getClassificationId());
		if (null == classification) {
			result.put("error", "根据id[" + labledomain.getClassificationId() + "]查找不到对应的分类");
			return result;
		}
		Lables lable = lablessRepository.findOne(labledomain.getLableId());
		if (null == lable) {
			result.put("error", "根据id[" + labledomain.getLableId() + "]查找不到对应的标签");
			return result;
		}
		lable.setClassificationId(labledomain.getClassificationId());
		lable.setName(labledomain.getLableName());
		lable.setLastModifiedBy(account.getId());
		lable.setLastModifiedDate(new Date());
		lablessRepository.save(lable);
		result.put("id", lable.getId());
		return result;
	}

	/**
	 * 删除标签
	 * 
	 * @param labledId
	 * @return
	 */
	public Map<String, Object> deleteLable(Long labledId) {
		Map<String, Object> result = new HashMap<>();
		if (null == labledId || labledId <= 0) {
			result.put("error", "标签id不能为空");
			return result;
		}
		Lables lable = lablessRepository.findOne(labledId);
		if (null == lable) {
			result.put("error", "根据id[" + labledId + "]查找不到对应的标签");
			return result;
		}
		List<ArticleLable> articleLables = articleLablesRepository.findByLable(lable);
		if (articleLables.size() > 0) {
			result.put("error", "有文章在用id[" + labledId + "]这个标签");
			return result;
		}
		lablessRepository.delete(lable);
		result.put("result", "success");
		return result;
	}

	// 删除分类
	public Map<String, Object> deleteClassification(Long classificationId) {
		Map<String, Object> result = new HashMap<>();
		if (null == classificationId || classificationId <= 0) {
			result.put("error", "分类id不能为空");
			return result;
		}
		Classification classification = classificationRepository.findOne(classificationId);
		if (null == classification) {
			result.put("error", "根据id[" + classificationId + "]查找不到对应的分类");
			return result;
		}
		List<Lables> lables = lablessRepository.findByClassificationId(classificationId);
		if (lables.size() > 0) {
			result.put("error", "该分类id[" + classificationId + "]下有标签");
			return result;
		}
		classificationRepository.delete(classificationId);
		result.put("result", "success");
		return result;
	}

	/**
	 * 查询分类和标签
	 * 
	 * @return
	 */
	public Map<String, Object> getClassificationAndLable() {
		Map<String, Object> result = new HashMap<>();
		List<ClassificationAndLableDomain> classificationAndLableDomains = new ArrayList<>();
		List<Classification> classifications = classificationRepository.findAll();
		if (null != classifications) {
			for (int i = 0; i < classifications.size(); i++) {
				Classification classification = classifications.get(i);
				ClassificationAndLableDomain classificationAndLableDomain = new ClassificationAndLableDomain();
				classificationAndLableDomain.setClassificationId(classification.getId());
				classificationAndLableDomain.setClassificationName(classification.getName());
				List<Lables> lables = lablessRepository.findByClassificationId(classification.getId());
				List<LableDomain> lableDomains = new ArrayList<>();
				if (null != lables) {
					for (int j = 0; j < lables.size(); j++) {
						Lables lable = lables.get(j);
						LableDomain lableDomain = new LableDomain();
						lableDomain.setLableId(lable.getId());
						lableDomain.setLableName(lable.getName());
						lableDomains.add(lableDomain);
					}
				}
				classificationAndLableDomain.setLableDomains(lableDomains);
				classificationAndLableDomains.add(classificationAndLableDomain);
			}
			result.put("classificationAndLableDomains", classificationAndLableDomains);
		}
		return result;
	}
}
