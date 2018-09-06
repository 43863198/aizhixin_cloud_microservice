package com.aizhixin.cloud.ew.lostAndFound.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.ew.common.PageDomain;
import com.aizhixin.cloud.ew.common.core.ApiReturnConstants;
import com.aizhixin.cloud.ew.common.core.DataValidity;
import com.aizhixin.cloud.ew.common.dto.AccountDTO;
import com.aizhixin.cloud.ew.common.service.AuthUtilService;
import com.aizhixin.cloud.ew.lostAndFound.domain.LFDomain;
import com.aizhixin.cloud.ew.lostAndFound.domain.LFListDomain;
import com.aizhixin.cloud.ew.lostAndFound.entity.LostAndFound;
import com.aizhixin.cloud.ew.lostAndFound.entity.Praise;
import com.aizhixin.cloud.ew.lostAndFound.entity.Type;
import com.aizhixin.cloud.ew.lostAndFound.repository.LFPraiseRepository;
import com.aizhixin.cloud.ew.lostAndFound.repository.LFRepository;
import com.aizhixin.cloud.ew.lostAndFound.repository.TypeRepository;

/**
 * 失物招领操作后台API
 * 
 * @author Rigel.ma
 *
 */
@Component
@Transactional
public class LostAndFoundManagementService {

	@Autowired
	private LFRepository lfRepository;

	@Autowired
	private TypeRepository typeRepository;

	@Autowired
	private LFPraiseRepository praiseRepository;

	@Autowired
	private AuthUtilService authUtilService;

	/**
	 * 新增失物招领
	 * 
	 * @param lfDomain
	 * @param account
	 * @return
	 */
	public Map<String, Object> addLF(LFDomain lfDomain, AccountDTO account) {

		Map<String, Object> result = new HashMap<>();
		LostAndFound lostAndFound = new LostAndFound();
		if (lfDomain.getInfoType() < 0 || "".equals(lfDomain.getInfoType())) {
			result.put("error", "信息类型Id不能为空");
			return result;
		}
		if (lfDomain.getType().getId() <= 0 || "".equals(lfDomain.getType().getId())
				|| lfDomain.getType().getId() > 8) {
			result.put("error", "物品类型Id不能为空且在1~8之间");
			return result;
		}
		if (StringUtils.isEmpty(lfDomain.getAddress())) {
			result.put("error", "称呼不能为空");
			return result;
		}
		if (StringUtils.isEmpty(lfDomain.getContactWay())) {
			result.put("error", "联系方式不能为空");
			return result;
		}
		if (StringUtils.isEmpty(lfDomain.getContactNumber())) {
			result.put("error", "联系号码不能为空");
			return result;
		}
		if (StringUtils.isEmpty(lfDomain.getContent())) {
			result.put("error", "失物描述不能为空");
			return result;
		}
		lostAndFound.setInfoType(lfDomain.getInfoType());
		lostAndFound.setTypeId(lfDomain.getType().getId());
		lostAndFound.setPicUrl1(lfDomain.getPicUrl1());
		lostAndFound.setPicUrl2(lfDomain.getPicUrl2());
		lostAndFound.setPicUrl3(lfDomain.getPicUrl3());
		lostAndFound.setAddress(lfDomain.getAddress());
		lostAndFound.setContactWay(lfDomain.getContactWay());
		lostAndFound.setContactNumber(lfDomain.getContactNumber());
		lostAndFound.setOrganId(account.getOrganId());
		lostAndFound.setCreatedBy(account.getId());
		lostAndFound.setContent(lfDomain.getContent());
		lostAndFound.setUserName(account.getName());
		lostAndFound.setPraiseCount(0);
		lostAndFound.setCollege(account.getCollegeName());
		lostAndFound.setMemo(account.getGender());
		lostAndFound.setLastModifiedBy(account.getId());
		lostAndFound.setDeleteFlag(DataValidity.VALID.getState());
		lostAndFound.setFinishFlag(0);
		lostAndFound.setOrgan(account.getOrganName());
		lfRepository.save(lostAndFound);
		result.put("id", lostAndFound.getId());
		return result;
	}

	/**
	 * 删除失物招领
	 * 
	 * @param lostAndFoundId
	 * @param account
	 * @return
	 */
	public Map<String, Object> deleteLF(Long lostAndFoundId, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		LostAndFound lostAndFound = lfRepository.findByIdAndDeleteFlag(lostAndFoundId, DataValidity.VALID.getState());
		if (null == lostAndFound) {
			result.put("error", "根据id不能查找到对应的数据");
			return result;
		}
		lostAndFound.setDeleteFlag(DataValidity.INVALID.getState());
		lostAndFound.setLastModifiedDate(new Date());
		lostAndFound.setLastModifiedBy(account.getId());
		lfRepository.save(lostAndFound);
		result.put("id", lostAndFound.getId());
		result.put("result", "success");
		return result;
	}

	/**
	 * 结束失物招领
	 * 
	 * @param lostAndFoundId
	 * @param account
	 * @return
	 */
	public Map<String, Object> finishLF(Long lostAndFoundId, AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		LostAndFound lostAndFound = lfRepository.findByIdAndDeleteFlagAndFinishFlag(lostAndFoundId,
				DataValidity.VALID.getState(), 0);
		if (null == lostAndFound) {
			result.put("error", "根据id不能查找到对应的数据");
			return result;
		}
		lostAndFound.setFinishFlag(1);// 1为已完成
		lostAndFound.setLastModifiedDate(new Date());
		lostAndFound.setLastModifiedBy(account.getId());
		result.put("id", lostAndFound.getId());
		result.put("result", "success");
		return result;
	}

	/**
	 * 失物的类型
	 * 
	 * @return
	 */
	public Map<String, Object> typeList() {
		Map<String, Object> result = new HashMap<>();
		List<Type> types = typeRepository.findAll();
		if (types.size() > 0) {
			result.put("types", types);
		}
		return result;
	}

	/**
	 * 增加表扬或心痛
	 * 
	 * @param account
	 * @param lostAndFoundId
	 * @return
	 */
	public Map<String, Object> addPraise(AccountDTO account, Long lostAndFoundId) {
		Map<String, Object> result = new HashMap<>();
		LostAndFound lostAndFound = lfRepository.findByIdAndDeleteFlag(lostAndFoundId, DataValidity.VALID.getState());
		if (null == lostAndFound) {
			result.put("error", "根据id不能查找到对应的数据");
			return result;
		}
		Praise praise = praiseRepository.findByCreatedByAndLfId(account.getId(), lostAndFoundId);
		if (null == praise) {
			praise = new Praise();
			praise.setCreatedBy(account.getId());
			praise.setCreatedDate(new Date());
			praise.setLfId(lostAndFoundId);
			praise.setStatus(1);
		} else if (praise.getStatus() == 1) {
			result.put("error", "已增加表扬或心痛");
			return result;
		} else {
			praise.setLastModifiedDate(new Date());
			praise.setStatus(1);
		}
		praiseRepository.save(praise);
		lostAndFound.setPraiseCount(lostAndFound.getPraiseCount() + 1);
		lfRepository.save(lostAndFound);
		result.put("id", lostAndFound.getId());
		result.put("result", "success");
		return result;
	}

	/**
	 * 取消表扬或心痛
	 * 
	 * @param account
	 * @param lostAndFoundId
	 * @return
	 */
	public Map<String, Object> cancelPraise(AccountDTO account, Long lostAndFoundId) {
		Map<String, Object> result = new HashMap<>();
		LostAndFound lostAndFound = lfRepository.findByIdAndDeleteFlag(lostAndFoundId, DataValidity.VALID.getState());
		if (null == lostAndFound) {
			result.put("error", "根据id不能查找到对应的数据");
			return result;
		}
		Praise praise = praiseRepository.findByCreatedByAndLfId(account.getId(), lostAndFoundId);
		if (null == praise) {
			praise = new Praise();
			praise.setCreatedBy(account.getId());
			praise.setCreatedDate(new Date());
			praise.setLfId(lostAndFoundId);
			praise.setStatus(0);
		} else if (praise.getStatus() == 0) {
			result.put("error", "已取消表扬或心痛");
			return result;
		} else {
			praise.setLastModifiedDate(new Date());
			praise.setStatus(0);
		}
		praiseRepository.save(praise);
		lostAndFound.setPraiseCount(lostAndFound.getPraiseCount() - 1);
		lfRepository.save(lostAndFound);
		result.put("id", lostAndFound.getId());
		result.put("result", "success");
		return result;
	}

	/**
	 * 失物招领列表
	 * 
	 * @param result
	 * @param pageable
	 * @param account
	 * @return
	 */
	public Map<String, Object> getList(Map<String, Object> result, Pageable pageable, AccountDTO account) {
		Page<LostAndFound> page = lfRepository.findByOrganIdAndDeleteFlag(pageable, account.getOrganId(),
				DataValidity.VALID.getState());
		PageDomain p = new PageDomain();
		p.setPageNumber(pageable.getPageNumber() + 1);
		p.setPageSize(pageable.getPageSize());
		p.setTotalElements(page.getTotalElements());
		p.setTotalPages(page.getTotalPages());
		result.put(ApiReturnConstants.PAGE, p);
		// 取到的数据加工
		Set<Long> typeIds = new HashSet<>();
		Set<Long> userIds = new HashSet<>();
		Set<Long> lostIds = new HashSet<>();
		List<LFListDomain> lFListDomains = new ArrayList<>();
		Map<Long, List<LFListDomain>> typeMap = new HashMap<>();
		Map<Long, List<LFListDomain>> userMap = new HashMap<>();
		for (int i = 0; i < page.getContent().size(); i++) {
			LostAndFound lostAndFound = page.getContent().get(i);
			LFListDomain lFListDomain = new LFListDomain();
			lFListDomain.setId(lostAndFound.getId());
			lFListDomain.setAddress(lostAndFound.getAddress());
			typeIds.add(lostAndFound.getTypeId());
			List<LFListDomain> lfListDomainList = typeMap.get(lostAndFound.getTypeId());
			if (null == lfListDomainList) {
				lfListDomainList = new ArrayList<>();
				typeMap.put(lostAndFound.getTypeId(), lfListDomainList);
			}
			lfListDomainList.add(lFListDomain);
			lFListDomain.setCollege(lostAndFound.getCollege());
			lFListDomain.setContactWay(lostAndFound.getContactWay());
			lFListDomain.setContactNumber(lostAndFound.getContactNumber());
			lFListDomain.setContent(lostAndFound.getContent());
			lFListDomain.setPraiseCount(lostAndFound.getPraiseCount());
			lFListDomain.setFinishFlag(lostAndFound.getFinishFlag());
			lFListDomain.setInfoType(lostAndFound.getInfoType() == 0 ? "丢失" : "捡到");
			String url1 = lostAndFound.getPicUrl1();
			String url2 = lostAndFound.getPicUrl2();
			String url3 = lostAndFound.getPicUrl3();
			// 3张图片放到数组里
			List<String> urls = new ArrayList<>();
			if (StringUtils.isNotEmpty(url1)) {
				urls.add(url1);
			}
			if (StringUtils.isNotEmpty(url2)) {
				urls.add(url2);
			}
			if (StringUtils.isNotEmpty(url3)) {
				urls.add(url3);
			}
			lFListDomain.setPicUrls(urls);
			String valTime = timeDifference(lostAndFound.getCreatedDate());
			lFListDomain.setPublishTime(valTime);
			userIds.add(lostAndFound.getCreatedBy());
			List<LFListDomain> lfListDomainList2 = userMap.get(lostAndFound.getCreatedBy());
			if (null == lfListDomainList2) {
				lfListDomainList2 = new ArrayList<>();
				userMap.put(lostAndFound.getCreatedBy(), lfListDomainList2);
			}
			lfListDomainList2.add(lFListDomain);

			lFListDomain.setGender(lostAndFound.getMemo());
			lostIds.add(lostAndFound.getId());
			lFListDomains.add(lFListDomain);
		}
		if (typeIds.size() > 0) {
			List<Type> types = typeRepository.findByIdIn(typeIds);
			for (Type t : types) {
				List<LFListDomain> lfListDomainList = typeMap.get(t.getId());
				if (null != lfListDomainList) {
					for (LFListDomain d : lfListDomainList) {
						d.setType(t.getType());
					}
				}
			}
		}
		if (lostIds.size() > 0) {
			List<Praise> praises = praiseRepository.findByCreatedByAndLfIdIn(account.getId(), lostIds);
			Map<Long, Praise> praiseMap = new HashMap<>();
			for (Praise prs : praises) {
				praiseMap.put(prs.getLfId(), prs);
			}
			for (LFListDomain lFListDomain : lFListDomains) {
				Praise praise = praiseMap.get(lFListDomain.getId());
				if (null != praise) {
					lFListDomain.setStatus(praise.getStatus());
				} else {
					lFListDomain.setStatus(0);
				}
			}
		}
		if (userIds.size() > 0) {
			List<AccountDTO> useList = authUtilService.getavatarUserInfo(userIds);
			for (AccountDTO a : useList) {
				List<LFListDomain> lfListDomainList = userMap.get(a.getId());
				if (null != lfListDomainList) {
					for (LFListDomain d : lfListDomainList) {
						d.setAvatar(a.getAvatar());
					}
				}
			}
		}
		result.put(ApiReturnConstants.DATA, lFListDomains);
		return result;
	}

	/**
	 * 失物招領列表(所有)
	 * 
	 * @param result
	 * @param pageable
	 * @param account
	 * @return
	 */
	public Map<String, Object> getLists(Map<String, Object> result, Pageable pageable, AccountDTO account) {
		Page<LostAndFound> page = lfRepository.findByDeleteFlag(pageable, DataValidity.VALID.getState());
		PageDomain p = new PageDomain();
		p.setPageNumber(pageable.getPageNumber() + 1);
		p.setPageSize(pageable.getPageSize());
		p.setTotalElements(page.getTotalElements());
		p.setTotalPages(page.getTotalPages());
		result.put(ApiReturnConstants.PAGE, p);
		// 取到的数据加工
		Set<Long> typeIds = new HashSet<>();
		Set<Long> userIds = new HashSet<>();
		Set<Long> lostIds = new HashSet<>();
		List<LFListDomain> lFListDomains = new ArrayList<>();
		Map<Long, List<LFListDomain>> typeMap = new HashMap<>();
		Map<Long, List<LFListDomain>> userMap = new HashMap<>();
		for (int i = 0; i < page.getContent().size(); i++) {
			LostAndFound lostAndFound = page.getContent().get(i);
			LFListDomain lFListDomain = new LFListDomain();
			lFListDomain.setAddress(lostAndFound.getAddress());
			lFListDomain.setId(lostAndFound.getId());
			typeIds.add(lostAndFound.getTypeId());
			List<LFListDomain> lfListDomainList = typeMap.get(lostAndFound.getTypeId());
			if (null == lfListDomainList) {
				lfListDomainList = new ArrayList<>();
				typeMap.put(lostAndFound.getTypeId(), lfListDomainList);
			}
			lfListDomainList.add(lFListDomain);
			lFListDomain.setCollege(lostAndFound.getCollege());
			lFListDomain.setContactWay(lostAndFound.getContactWay());
			lFListDomain.setContactNumber(lostAndFound.getContactNumber());
			lFListDomain.setContent(lostAndFound.getContent());
			lFListDomain.setPraiseCount(lostAndFound.getPraiseCount());
			lFListDomain.setFinishFlag(lostAndFound.getFinishFlag());
			lFListDomain.setInfoType(lostAndFound.getInfoType() == 0 ? "丢失" : "捡到");
			String url1 = lostAndFound.getPicUrl1();
			String url2 = lostAndFound.getPicUrl2();
			String url3 = lostAndFound.getPicUrl3();
			// 3张图片放到数组里
			List<String> urls = new ArrayList<>();
			if (StringUtils.isNotEmpty(url1)) {
				urls.add(url1);
			}
			if (StringUtils.isNotEmpty(url2)) {
				urls.add(url2);
			}
			if (StringUtils.isNotEmpty(url3)) {
				urls.add(url3);
			}
			lFListDomain.setPicUrls(urls);
			String valTime = timeDifference(lostAndFound.getCreatedDate());
			lFListDomain.setPublishTime(valTime);
			lFListDomain.setGender(lostAndFound.getMemo());
			if (null != lostAndFound.getCreatedBy())
				userIds.add(lostAndFound.getCreatedBy());
			List<LFListDomain> lfListDomainList2 = userMap.get(lostAndFound.getCreatedBy());
			if (null == lfListDomainList2) {
				lfListDomainList2 = new ArrayList<>();
				userMap.put(lostAndFound.getCreatedBy(), lfListDomainList2);
			}
			lfListDomainList2.add(lFListDomain);
			lostIds.add(lostAndFound.getId());
			lFListDomains.add(lFListDomain);
		}
		if (typeIds.size() > 0) {
			List<Type> types = typeRepository.findByIdIn(typeIds);
			for (Type t : types) {
				List<LFListDomain> lfListDomainList = typeMap.get(t.getId());
				if (null != lfListDomainList) {
					for (LFListDomain d : lfListDomainList) {
						d.setType(t.getType());
					}
				}
			}
		}
		if (lostIds.size() > 0) {
			List<Praise> praises = praiseRepository.findByCreatedByAndLfIdIn(account.getId(), lostIds);
			Map<Long, Praise> praiseMap = new HashMap<>();
			for (Praise prs : praises) {
				praiseMap.put(prs.getLfId(), prs);
			}
			for (LFListDomain lFListDomain : lFListDomains) {
				Praise praise = praiseMap.get(lFListDomain.getId());
				if (null != praise) {
					lFListDomain.setStatus(praise.getStatus());
				} else {
					lFListDomain.setStatus(0);
				}
			}
		}
		if (userIds.size() > 0) {
			List<AccountDTO> useList = authUtilService.getavatarUserInfo(userIds);
			for (AccountDTO a : useList) {
				List<LFListDomain> lfListDomainList = userMap.get(a.getId());
				if (null != lfListDomainList) {
					for (LFListDomain d : lfListDomainList) {
						d.setAvatar(a.getAvatar());
					}
				}
			}
		}
		result.put(ApiReturnConstants.DATA, lFListDomains);
		return result;
	}

	/**
	 * 失物招领列表(我的发布)
	 * 
	 * @param account
	 * @return
	 */
	public Map<String, Object> getMyList(AccountDTO account) {
		Map<String, Object> result = new HashMap<>();
		List<LostAndFound> lostAndFounds = lfRepository.findByCreatedByAndDeleteFlag(account.getId(),
				DataValidity.VALID.getState());
		Set<Long> typeIds = new HashSet<>();
		Set<Long> lostIds = new HashSet<>();
		Map<Long, List<LFListDomain>> typeMap = new HashMap<>();
		Integer lfsize = lostAndFounds.size();
		if (lfsize > 0) {
			List<LFListDomain> lFListDomains = new ArrayList<>();
			for (int i = lfsize - 1; i >= 0; i--) {
				LostAndFound lostAndFound = lostAndFounds.get(i);
				LFListDomain lFListDomain = new LFListDomain();
				lFListDomain.setAddress(lostAndFound.getAddress());
				lFListDomain.setId(lostAndFound.getId());
				typeIds.add(lostAndFound.getTypeId());
				List<LFListDomain> listDomains = typeMap.get(lostAndFound.getTypeId());
				if (listDomains == null) {
					listDomains = new ArrayList<>();
					typeMap.put(lostAndFound.getTypeId(), listDomains);
				}
				listDomains.add(lFListDomain);
				lFListDomain.setCollege(lostAndFound.getCollege());
				lFListDomain.setContactWay(lostAndFound.getContactWay());
				lFListDomain.setContactNumber(lostAndFound.getContactNumber());
				lFListDomain.setContent(lostAndFound.getContent());
				lFListDomain.setPraiseCount(lostAndFound.getPraiseCount());
				lFListDomain.setFinishFlag(lostAndFound.getFinishFlag());
				lFListDomain.setInfoType(lostAndFound.getInfoType() == 0 ? "丢失" : "捡到");
				String url1 = lostAndFound.getPicUrl1();
				String url2 = lostAndFound.getPicUrl2();
				String url3 = lostAndFound.getPicUrl3();
				// 3张图片放到数组里
				List<String> urls = new ArrayList<>();
				if (StringUtils.isNotEmpty(url1)) {
					urls.add(url1);
				}
				if (StringUtils.isNotEmpty(url2)) {
					urls.add(url2);
				}
				if (StringUtils.isNotEmpty(url3)) {
					urls.add(url3);
				}
				lFListDomain.setPicUrls(urls);
				String valTime = timeDifference(lostAndFound.getCreatedDate());
				lFListDomain.setPublishTime(valTime);
				lFListDomain.setGender(lostAndFound.getMemo());
				lFListDomain.setAvatar(account.getAvatar());
				lostIds.add(lostAndFound.getId());
				lFListDomains.add(lFListDomain);
			}
			if (typeIds.size() > 0) {
				List<Type> types = typeRepository.findByIdIn(typeIds);
				for (Type t : types) {
					List<LFListDomain> lfListDomainList = typeMap.get(t.getId());
					if (null != lfListDomainList) {
						for (LFListDomain d : lfListDomainList) {
							d.setType(t.getType());
						}
					}
				}
			}
			if (lostIds.size() > 0) {
				List<Praise> praises = praiseRepository.findByCreatedByAndLfIdIn(account.getId(), lostIds);
				Map<Long, Praise> praiseMap = new HashMap<>();
				for (Praise prs : praises) {
					praiseMap.put(prs.getLfId(), prs);
				}
				for (LFListDomain lFListDomain : lFListDomains) {
					Praise praise = praiseMap.get(lFListDomain.getId());
					if (null != praise) {
						lFListDomain.setStatus(praise.getStatus());
					} else {
						lFListDomain.setStatus(0);
					}
				}
			}
			result.put("data", lFListDomains);
		}
		return result;
	}

	// 算发布时间和现在的时差
	public String timeDifference(Date createdDate) {
		Date nowTime = new Date();
		Long r = (nowTime.getTime() - createdDate.getTime()) / 1000;
		String valTime = null;
		if (r > 0) {
			Long diffMinutes = r / 60;
			Long diffHours = r / (60 * 60);
			Long diffDays = r / (24 * 60 * 60);
			Long diffWeeks = r / (24 * 60 * 60 * 7);
			Long diffMonths = r / (24 * 60 * 60 * 30);
			Long diffYears = r / (24 * 60 * 60 * 30 * 12);
			if (diffYears >= 1) {
				valTime = diffYears + "年前";
			} else if (diffMonths >= 1) {
				valTime = diffMonths + "月前";
			} else if (diffWeeks >= 1) {
				valTime = diffWeeks + "周前";
			} else if (diffDays >= 1) {
				valTime = diffDays + "天前";
			} else if (diffHours >= 1) {
				valTime = diffHours + "小时前";
			} else if (diffMinutes > 1) {
				valTime = diffMinutes + "分钟前";
			} else {
				valTime = "1分钟前";
			}
		} else {
			valTime = "1分钟前";
		}
		return valTime;
	}
}
