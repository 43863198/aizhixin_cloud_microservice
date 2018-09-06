/**
 * 
 */
package com.aizhixin.cloud.paycallback.service;


import com.aizhixin.cloud.paycallback.common.PageData;
import com.aizhixin.cloud.paycallback.common.core.DataValidity;
import com.aizhixin.cloud.paycallback.common.core.ErrorCode;
import com.aizhixin.cloud.paycallback.common.core.PageUtil;
import com.aizhixin.cloud.paycallback.common.exception.CommonException;
import com.aizhixin.cloud.paycallback.core.InstallmentAmount;
import com.aizhixin.cloud.paycallback.core.PaymentType;
import com.aizhixin.cloud.paycallback.core.PublishState;
import com.aizhixin.cloud.paycallback.domain.PaymentSubjectCalculateDomain;
import com.aizhixin.cloud.paycallback.domain.PaymentSubjectDomain;
import com.aizhixin.cloud.paycallback.domain.PaymentSubjectQueryListDomain;
import com.aizhixin.cloud.paycallback.domain.PersonCostExcelDomain;
import com.aizhixin.cloud.paycallback.entity.PaymentSubject;
import com.aizhixin.cloud.paycallback.repository.PaymentSubjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 缴费科目相关操作业务逻辑处理
 * @author zhen.pan
 *
 */
@Component
@Transactional
public class PaymentSubjectService {
	private final static Logger LOG = LoggerFactory.getLogger(PaymentSubjectService.class);
	@Autowired
	private PaymentSubjectRepository paymentSubjectRepository;
	@Autowired
	private PersonalCostService personalCostService;
	@Autowired
	private ExcelHelper excelHelper;

	/**
	 * 保存实体
	 * @param paymentSubject
	 * @return
	 */
	public PaymentSubject save(PaymentSubject paymentSubject) {
		return paymentSubjectRepository.save(paymentSubject);
	}

	public List<PaymentSubject>  save(List<PaymentSubject> paymentSubjects) {
		return paymentSubjectRepository.save(paymentSubjects);
	}

	/**
	 * 根据实体ID查询实体
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public PaymentSubject findById(String id) {
		return paymentSubjectRepository.findOne(id);
	}

	/**
	 * 分页查询特定学校的缴费科目
	 * @param pageable
	 * @param orgId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<PaymentSubjectQueryListDomain> findByOrgId(Pageable pageable, Long orgId) {
		return paymentSubjectRepository.findByOrgId(pageable, orgId, DataValidity.VALID.getState());
	}
	/**
	 * 分页查查询特定学校、特定名称的缴费科目
	 * @param pageable
	 * @param name
	 * @param orgId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<PaymentSubjectQueryListDomain> findByOrgIdAndName(Pageable pageable, String name, Long orgId) {
		return paymentSubjectRepository.findByOrgIdAndName(pageable, orgId, name, DataValidity.VALID.getState());
	}

	@Transactional(readOnly = true)
	public List<PaymentSubject> findByPublishStateAndLastDateLessThan(Integer publishState, Date date) {
		return paymentSubjectRepository.findByDeleteFlagAndPublishStateAndLastDateLessThan(DataValidity.VALID.getState(), publishState, date);
	}
	//*************************************************************以下部分处理页面调用逻辑**********************************************************************//	
	/**
	 * 缴费科目新增
	 * @param file	缴费科目视图对象
	 * @return		缴费科目实体对象
	 */
	public PaymentSubject save(MultipartFile file, String fileName, Integer paymentType, Double smallAmount, Integer installmentRate, Date lastDate, Long orgId, Long userId) {
		List<PersonCostExcelDomain> list = null;
		PaymentSubject paymentSubject = new PaymentSubject();
		if (null == orgId || orgId <= 0) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "学校是必须的");
		}
		if (null == userId || userId <= 0) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "操作人信息是必须的");
		}
		if (null != file) {
			if (StringUtils.isEmpty(fileName)) {
				fileName = file.getOriginalFilename();
			}
			int p = fileName.lastIndexOf(".");
			if (p > 0) {
				fileName = fileName.substring(0, p);
			}
			paymentSubject.setName(fileName);
			list = excelHelper.readStudentCostFromInputStream(file);
		} else {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "缴费单的excel文件是必须的");
		}
		if (null == paymentType) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "缴费方式是必须的");
		}
		if (PaymentType.ALL.getState().intValue() != paymentType && PaymentType.INSTALLMENT.getState().intValue() != paymentType) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "缴费方式只能是[全款缴费]或者[分期缴费]");
		}
		paymentSubject.setPaymentType(paymentType);
		if (PaymentType.INSTALLMENT.getState().intValue() == paymentType) {
			if (null == smallAmount || smallAmount.doubleValue() <= 0) {
				throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "[分期缴费]方式必须设置[最低支付限额]");
			}
			if (null == installmentRate) {
				throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "[分期缴费]方式的[额度频次]是必须的");
			}
			if (InstallmentAmount.EVERY.getState().intValue() != installmentRate && InstallmentAmount.FIRST.getState().intValue() != installmentRate) {
				throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "[分期缴费]方式的[额度频次]只能是[首次缴费]或者[每次缴费]");
			}
			paymentSubject.setSmallAmount(smallAmount);
			paymentSubject.setInstallmentRate(installmentRate);
		}
		if (null == lastDate) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "[截止日期]是必须的");
		}
		if (StringUtils.isEmpty(paymentSubject.getName())) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "名称是必须的");
		}
		paymentSubject.setPublishState(PublishState.INIT.getState());
		paymentSubject.setLastDate(lastDate);
		paymentSubject.setLastModifiedBy(userId);
		paymentSubject.setCreatedBy(userId);
		paymentSubject.setOrgId(orgId);
		paymentSubject = save(paymentSubject);
		if (null != list && list.size() > 0) {
			personalCostService.save(paymentSubject, list);
		}
		return paymentSubject;
	}

	/**
	 * 列表查询
	 * @param orgId			学校ID
	 * @param name			缴费科目名称
	 * @param pageNumber	第几页
	 * @param pageSize		每页的数据条数
	 * @return				查询结果
	 */
	@Transactional(readOnly = true)
	public PageData<PaymentSubjectQueryListDomain> query(Long orgId, String name, Integer pageNumber, Integer pageSize) {
		Pageable pageable = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
		PageData<PaymentSubjectQueryListDomain> p = new PageData<>();
		Page<PaymentSubjectQueryListDomain> pageData;
		if (StringUtils.isEmpty(name)) {
			pageData = findByOrgId(pageable, orgId);
		} else {
			pageData = findByOrgIdAndName(pageable, "%" + name + "%", orgId);
		}
		if (null != pageData) {
			p.setData(pageData.getContent());
			p.getPage().setPageNumber(pageable.getPageNumber() + 1);
			p.getPage().setPageSize(pageable.getPageSize());
			p.getPage().setTotalElements(pageData.getTotalElements());
			p.getPage().setTotalPages(pageData.getTotalPages());
		}
		return p;
	}

	/**
	 * 获取特定ID的缴费科目详细信息
	 * @param id	缴费科目ID
	 * @return		缴费科目实体对象
	 */
	@Transactional(readOnly = true)
	public PaymentSubjectDomain get(String id) {
		if (null != id && id.length() > 5) {
			PaymentSubject paymentSubject = findById(id);
			if (null != paymentSubject) {
				return new PaymentSubjectDomain(paymentSubject);
			}
		}
		return null;
	}

	/**
	 * 缴费科目修改
	 * @param id	缴费科目id
	 * @return						缴费科目实体对象
	 */
	public PaymentSubject update(String id, MultipartFile file, String fileName, Integer paymentType, Double smallAmount, Integer installmentRate, Date lastDate, Long orgId, Long userId) {
		PaymentSubject paymentSubject = null;
		List<PersonCostExcelDomain> list = null;
		if (null != id && id.length() > 5) {
			paymentSubject = findById(id);
		}
		if (null == paymentSubject) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据ID[" + id + "]没有查找到对应的缴费科目信息");
		}
		if (PublishState.PUBLISH.getState().intValue() == paymentSubject.getPublishState()) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "已经生效的缴费科目不能修改");
		}
		if (null != file) {
			if (StringUtils.isEmpty(fileName)) {
				fileName = file.getOriginalFilename();
			}
			int p = fileName.lastIndexOf(".");
			if (p > 0) {
				fileName = fileName.substring(0, p);
			}
			paymentSubject.setName(fileName);
//			paymentSubject.setName(file.getOriginalFilename());
			personalCostService.deleteByPaymentSubjectId(paymentSubject);
			list = excelHelper.readStudentCostFromInputStream(file);
		}
		if (null == paymentType) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "缴费方式是必须的");
		}
		if (null != paymentType) {
			if (PaymentType.ALL.getState().intValue() != paymentType && PaymentType.INSTALLMENT.getState().intValue() != paymentType) {
				throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "缴费方式只能是[全款缴费]或者[分期缴费]");
			}
			paymentSubject.setPaymentType(paymentType);
			if (PaymentType.INSTALLMENT.getState().intValue() == paymentType) {
				if (null == smallAmount || smallAmount.doubleValue() <= 0) {
					throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "[分期缴费]方式必须设置[最低支付限额]");
				}
				if (null == installmentRate) {
					throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "[分期缴费]方式的[额度频次]是必须的");
				}
				if (InstallmentAmount.EVERY.getState().intValue() != installmentRate && InstallmentAmount.FIRST.getState().intValue() != installmentRate) {
					throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "[分期缴费]方式的[额度频次]只能是[首次缴费]或者[每次缴费]");
				}
				paymentSubject.setInstallmentRate(installmentRate);
			}
		}

		if (null != lastDate) {
			paymentSubject.setLastDate(lastDate);
		}
		paymentSubject.setLastModifiedBy(userId);
		paymentSubject.setLastModifiedDate(new Date());
		paymentSubject = save(paymentSubject);
		if (null != list && list.size() > 0) {
			personalCostService.save(paymentSubject, list);
		}
		return paymentSubject;
	}

	/**
	 * 发布缴费科目，使其立即生效
	 * @param id		缴费科目ID
	 * @param userId	操作人
	 */
	public void publish(String id, Long userId) {
		PaymentSubject paymentSubject = null;
		if (null != id && id.length() > 5) {
			paymentSubject = findById(id);
		}
		if (null == paymentSubject) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据ID[" + id + "]没有查找到对应的缴费科目信息");
		}
		if (PublishState.PUBLISH.getState().intValue() == paymentSubject.getPublishState()) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "已经生效的缴费科目不能在发布");
		}
		paymentSubject.setPublishState(PublishState.PUBLISH.getState());
		paymentSubject.setPublishTime(new Date());
		paymentSubject.setLastModifiedDate(new Date());
		paymentSubject.setLastModifiedBy(userId);
		save(paymentSubject);
	}

	/**
	 * 删除缴费科目
	 * @param id		缴费科目ID
	 * @param userId	操作人
	 */
	public void delete(String id, Long userId) {
		PaymentSubject paymentSubject = null;
		if (null != id && id.length() > 5) {
			paymentSubject = findById(id);
		}
		if (null == paymentSubject) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据ID[" + id + "]没有查找到对应的缴费科目信息");
		}
		if (PublishState.PUBLISH.getState().intValue() == paymentSubject.getPublishState()) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "已经生效的缴费科目不能删除");
		}
		paymentSubject.setDeleteFlag(DataValidity.INVALID.getState());
		paymentSubject.setLastModifiedDate(new Date());
		paymentSubject.setLastModifiedBy(userId);
		save(paymentSubject);
		personalCostService.deleteByPaymentSubjectId(paymentSubject);
	}

	/**
	 * 修改缴费方式
	 * @param id				缴费科目ID
	 * @param paymentType		付款方式
	 * @param smallAmount		最低金额
	 * @param installmentRate	最低金额频次
	 * @param userId			操作人
	 * @return					缴费科目实体
	 */
	public PaymentSubject update(String id, Integer paymentType, Double smallAmount, Integer installmentRate, Long userId) {
		PaymentSubject paymentSubject = null;
		if (null != id && id.length() > 5) {
			paymentSubject = findById(id);
		}
		if (null == paymentSubject) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据ID[" + id + "]没有查找到对应的缴费科目信息");
		}
		if (null == paymentType) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "缴费方式是必须的");
		}
		if (PaymentType.ALL.getState().intValue() != paymentType && PaymentType.INSTALLMENT.getState().intValue() != paymentType) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "缴费方式只能是[全款缴费]或者[分期缴费]");
		}
		paymentSubject.setPaymentType(paymentType);
		if (PaymentType.INSTALLMENT.getState().intValue() == paymentType) {
			if (null == smallAmount || smallAmount.doubleValue() <= 0) {
				throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "[分期缴费]方式必须设置[最低支付限额]");
			}
			paymentSubject.setSmallAmount(smallAmount);
			if (null == installmentRate) {
				throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "[分期缴费]方式的[额度频次]是必须的");
			}
			if (InstallmentAmount.EVERY.getState().intValue() != installmentRate && InstallmentAmount.FIRST.getState().intValue() != installmentRate) {
				throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "[分期缴费]方式的[额度频次]只能是[首次缴费]或者[每次缴费]");
			}
			paymentSubject.setInstallmentRate(installmentRate);
		}

		paymentSubject.setLastModifiedBy(userId);
		paymentSubject.setLastModifiedBy(userId);
		paymentSubject.setLastModifiedDate(new Date());
		paymentSubject = save(paymentSubject);
		return paymentSubject;
	}

	/**
	 * 修改缴费科目的截止日期
	 * @param id			缴费科目ID
	 * @param lastDate		截止日期
	 * @param userId		操作人
	 * @return				缴费科目实体
	 */
	public PaymentSubject update(String id, Date lastDate, Long userId) {
		PaymentSubject paymentSubject = null;
		if (null != id && id.length() > 5) {
			paymentSubject = findById(id);
		}
		if (null == paymentSubject) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据ID[" + id + "]没有查找到对应的缴费科目信息");
		}

		if (null != lastDate) {
			paymentSubject.setLastDate(lastDate);
		} else {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "[截止日期]是必须的");
		}
		paymentSubject.setLastModifiedDate(new Date());
		paymentSubject.setLastModifiedBy(userId);
		paymentSubject =  save(paymentSubject);
		return paymentSubject;
	}

	/**
	 * 统计缴费情况
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public PaymentSubjectCalculateDomain calcalatePaymentSubject(String id) {
		PaymentSubject paymentSubject = null;
		if (null != id && id.length() > 5) {
			paymentSubject = findById(id);
		}
		if (null != paymentSubject) {
			return personalCostService.calculatePaymentSubject(paymentSubject);
		}
		return new PaymentSubjectCalculateDomain ();
	}

	/**
	 * 定时处理过期的缴费数据信息
	 */
	public void checkLastDateTask() {
		LOG.info("开始处理支付过期的缴费科目");
		List<PaymentSubject> expriseDatas = findByPublishStateAndLastDateLessThan(PublishState.PUBLISH.getState(), new Date());
		if (null != expriseDatas && expriseDatas.size() > 0) {
			LOG.info("查询到过期的缴费科目数据{}条", expriseDatas.size());
			for (PaymentSubject paymentSubject : expriseDatas) {
				paymentSubject.setPublishState(PublishState.EXPRIRED.getState());
			}
			save(expriseDatas);
		}
		LOG.info("完成支付过期的缴费科目的处理");
	}


	/**
	 * 取消发布状态缴费科目
	 * @param id		缴费科目ID
	 * @param userId	操作人
	 */
	public void cansel(String id, Long userId) {
		PaymentSubject paymentSubject = null;
		if (null != id && id.length() > 5) {
			paymentSubject = findById(id);
		}
		if (null == paymentSubject) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据ID[" + id + "]没有查找到对应的缴费科目信息");
		}
		if (PublishState.PUBLISH.getState().intValue() != paymentSubject.getPublishState()) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "只能取消已经发布的缴费科目");
		}
		paymentSubject.setPublishState(PublishState.CANCEL.getState());
		paymentSubject.setLastModifiedDate(new Date());
		paymentSubject.setLastModifiedBy(userId);
		save(paymentSubject);
	}


	public PaymentSubject addPersonalCost(MultipartFile file, String paymentSubjectId, Long userId) {
		List<PersonCostExcelDomain> list = null;
		PaymentSubject paymentSubject = null;
		if (StringUtils.isEmpty(paymentSubjectId)) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "缴费科目ID是必须的");
		}
		paymentSubject = findById(paymentSubjectId);
		if (null == paymentSubject) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "根据缴费科目ID没有查找到对应的信息");
		}
		if (null == userId || userId <= 0) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "操作人信息是必须的");
		}
		if (PublishState.CANCEL.getState().intValue() == paymentSubject.getPublishState().intValue() || new Date().after(paymentSubject.getLastDate())) {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "已经取消或者过期的的缴费科目不能再添加缴费人员");
		}
		if (null != file) {
			list = excelHelper.readStudentCostFromInputStream(file);
		} else {
			throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "缴费单的excel文件是必须的");
		}
		if (null != list && list.size() > 0) {
			Set<String> idNumbers = new HashSet<>();
			for (PersonCostExcelDomain d : list) {
				idNumbers.add(d.getIdNumber());
			}
			long c = personalCostService.countByPaymentSubjectAndIdNumberIn(paymentSubject, idNumbers);
			if (c > 0) {
				throw new CommonException(ErrorCode.ID_NOT_FOUND_OBJECT, "新添加的部分身份证号码已经存在");
			}
			personalCostService.save(paymentSubject, list);
		}
		return paymentSubject;
	}
}
