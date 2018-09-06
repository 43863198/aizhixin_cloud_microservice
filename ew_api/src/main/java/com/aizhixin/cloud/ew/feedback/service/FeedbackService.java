package com.aizhixin.cloud.ew.feedback.service;

import java.text.ParseException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.ew.feedback.domain.FeedbackDomain;
import com.aizhixin.cloud.ew.feedback.domain.FeedbackListDomain;
import com.aizhixin.cloud.ew.feedback.entity.Feedback;
import com.aizhixin.cloud.ew.feedback.repository.FeedbackRepository;
import com.aizhixin.cloud.ew.common.PageData;
import com.aizhixin.cloud.ew.common.PageDomain;
import com.aizhixin.cloud.ew.common.dto.AccountDTO;
import com.aizhixin.cloud.ew.common.RestResult;
import com.aizhixin.cloud.ew.common.util.DateFormatUtil;
import com.aizhixin.cloud.ew.common.util.TransferUtil;

@Service
@Transactional
public class FeedbackService {
	@Autowired
	private FeedbackRepository feedbackRepository;

	/**
	 * 保存实体
	 * 
	 * @param entity
	 * @return
	 */
	public Feedback save(Feedback entity) {
		return feedbackRepository.save(entity);
	}

	/**
	 * 根据id查询实体
	 * 
	 * @param id
	 * @return
	 */
	public Feedback findById(Long id) {
		return feedbackRepository.findOne(id);
	}

	/**
	 * 分页查询所有
	 * 
	 * @param pageable
	 * @return
	 */
	public Page<FeedbackListDomain> findAllPage(Pageable pageable) {
		return feedbackRepository.findAllPage(pageable);
	}
	public Page<FeedbackListDomain> findAllPageByDate(Pageable pageable, Date startDate, Date endDate) {
		return feedbackRepository.findAllPageByDate(pageable, startDate, endDate);
	}
    
	// ***************************************以下部分处理页面调用逻辑***********************************************//

	/**
	 * 新增问题反馈
	 * 
	 * @param feedbackDomain
	 * @return
	 */
	public RestResult save(FeedbackDomain feedbackDomain, AccountDTO account) {
		RestResult result = new RestResult();
		if (StringUtils.isEmpty(feedbackDomain.getDescription())) {
			return new RestResult("error", "问题描述不能为空");
		}
		Feedback feedback = new Feedback();
		feedback.setName(account.getName());
		feedback.setPhone(account.getPhoneNumber());
		feedback.setPhoneDeviceInfo(feedbackDomain.getPhoneDeviceInfo());
		feedback.setSchool(account.getOrganName());
		feedback.setClasses(account.getClassName());
		feedback.setDescription(feedbackDomain.getDescription());
		feedback.setPictureUrls(TransferUtil.arrayToString(feedbackDomain.getPictureUrls()));
		feedback.setCreatedBy(account.getId());
		feedback.setLastModifiedBy(account.getId());
		feedback = save(feedback);
		result.setResult("success");
		result.setId(feedback.getId());
		return result;
	}

	/**
	 * 根据id查询问题反馈详情
	 * 
	 * @param id
	 * @return
	 */
	public FeedbackListDomain get(Long id) {
		FeedbackListDomain feedbackListDomain = new FeedbackListDomain();
		if (null != id && id > 0) {
			Feedback feedback = findById(id);
			if (feedback != null) {
				feedbackListDomain.setId(feedback.getId());
				feedbackListDomain.setName(feedback.getName());
				feedbackListDomain.setPhone(feedback.getPhone());
				feedbackListDomain.setPhoneDeviceInfo(feedback.getPhoneDeviceInfo());
				feedbackListDomain.setSchool(feedback.getSchool());
				feedbackListDomain.setClasses(feedback.getClasses());
				feedbackListDomain.setDescription(feedback.getDescription());
				if (feedback.getPictureUrls() != null && feedback.getPictureUrls().length() > 0) {
					feedbackListDomain.setPictureUrls(TransferUtil.stringToList(feedback.getPictureUrls()));
				}
				feedbackListDomain.setCreatedDate(feedback.getCreatedDate());
			}
		}
		return feedbackListDomain;
	}

	/**
	 * 分页查询问题反馈列表
	 * 
	 * @param pageable
	 * @return
	 * @throws ParseException 
	 */
	public PageData<FeedbackListDomain> findAll(Pageable pageable, Date startDate, Date endDate) throws ParseException {
		Page<FeedbackListDomain> page = null;
		if (startDate != null && endDate != null) {
			// 转换Date为String类型
			String s = DateFormatUtil.format(startDate, "yyyy-MM-dd");
			String e = DateFormatUtil.format(endDate, "yyyy-MM-dd");
			// 再次转换String为Date类型
			Date start = DateFormatUtil.parse(s + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
			Date end = DateFormatUtil.parse(e + " 23:23:59", "yyyy-MM-dd HH:mm:ss");
			page = findAllPageByDate(pageable, start, end);
		}else {
			page = findAllPage(pageable);
		}
		PageData<FeedbackListDomain> pageData = new PageData<>();
		pageData.setData(page.getContent());
		pageData.setPage(new PageDomain(page.getTotalElements(), page.getTotalPages(), pageable.getPageNumber(),
				pageable.getPageSize()));
		return pageData;
	}
}
