/**
 * 
 */
package com.aizhixin.cloud.paycallback.service;


import com.aizhixin.cloud.paycallback.common.PageData;
import com.aizhixin.cloud.paycallback.common.core.DataValidity;
import com.aizhixin.cloud.paycallback.common.core.PageUtil;
import com.aizhixin.cloud.paycallback.common.exception.NoAuthenticationException;
import com.aizhixin.cloud.paycallback.common.util.DateUtil;
import com.aizhixin.cloud.paycallback.common.util.NumberUtil;
import com.aizhixin.cloud.paycallback.core.*;
import com.aizhixin.cloud.paycallback.domain.*;
import com.aizhixin.cloud.paycallback.domain.third.StudentPaySubjectDomain;
import com.aizhixin.cloud.paycallback.entity.PaymentSubject;
import com.aizhixin.cloud.paycallback.entity.PersonalCost;
import com.aizhixin.cloud.paycallback.remote.PublicMobileService;
import com.aizhixin.cloud.paycallback.repository.PersonalCostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;

/**
 * 学生缴费相关操作业务逻辑处理
 * @author zhen.pan
 *
 */
@Component
@Transactional
public class PersonalCostService {
	@Autowired
	private EntityManager em;
	@Autowired
	private PersonalCostRepository personalCostRepository;
	@Autowired
	private PersonalCostOrderService personalCostOrderService;
	@Autowired
	private PublicMobileService publicMobileService;
	@Autowired
	private PaymentOrderService paymentOrderService;

	/**
	 * 保存实体
	 * @param personalCost
	 * @return
	 */
	public PersonalCost save(PersonalCost personalCost) {
		return personalCostRepository.save(personalCost);
	}

	/**
	 * 批量保存实体
	 * @param personalCosts
	 * @return
	 */
	public List<PersonalCost> save(List<PersonalCost> personalCosts) {
		return personalCostRepository.save(personalCosts);
	}

	/**
	 * 批量假删除
	 * @param paymentSubject
	 */
	public void deleteByPaymentSubjectId(PaymentSubject paymentSubject) {
		personalCostRepository.udpateByPaymentSubjectId(paymentSubject, DataValidity.INVALID.getState());
	}

	/**
	 * 缴费科目的总人数
	 * @param paymentSubject
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countAllPerson(PaymentSubject paymentSubject) {
		return personalCostRepository.countByPaymentSubjectAndDeleteFlag(paymentSubject, DataValidity.VALID.getState());
	}

	/**
	 * 根据付款状态统计缴费科目的人数
	 * @param paymentSubject
	 * @param paymentState
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countPaymentStatePerson(PaymentSubject paymentSubject, Integer paymentState) {
		return personalCostRepository.countByPaymentSubjectAndDeleteFlagAndPaymentState(paymentSubject, DataValidity.VALID.getState(), paymentState);
	}

	/**
	 * 统计应付总金额
	 * @param paymentSubject
	 * @return
	 */
	@Transactional(readOnly = true)
	public Double sumShouldPayAll(PaymentSubject paymentSubject) {
		return personalCostRepository.sumByPaymentSubjectAndDeleteFlag(paymentSubject, DataValidity.VALID.getState());
	}

	/**
	 * 根据付款状态统计应付总金额
	 * @param paymentSubject
	 * @param paymentState
	 * @return
	 */
	@Transactional(readOnly = true)
	public Double sumShouldPayAllPaymentState(PaymentSubject paymentSubject, Integer paymentState) {
		return personalCostRepository.sumShoudPayByPaymentSubjectAndDeleteFlagAndPaymentState(paymentSubject, DataValidity.VALID.getState(), paymentState);
	}

	/**
	 * 根据付款状态统计已付总金额
	 * @param paymentSubject
	 * @param paymentState
	 * @return
	 */
	@Transactional(readOnly = true)
	public Double sumHasPayAllPaymentState(PaymentSubject paymentSubject, Integer paymentState) {
		return personalCostRepository.sumHasPayByPaymentSubjectAndDeleteFlagAndPaymentState(paymentSubject, DataValidity.VALID.getState(), paymentState);
	}

	/**
	 * 根据实体ID查询实体
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public PersonalCost findById(String id) {
		return personalCostRepository.findOne(id);
	}

	/**
	 * 查询身份证对应的缴费记录信息
	 * @param pageable
	 * @param idNumber
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<PersonCostMobileListDomain> findByIdNumber(Pageable pageable , String idNumber) {
		return personalCostRepository.findMobileSimpleList(pageable, idNumber, PublishState.PUBLISH.getState(), DataValidity.VALID.getState());
	}

	/**
	 * 查询身份证和付款状态对应的缴费记录信息
	 * 发布状态及以后状态
	 * @param pageable
	 * @param idNumber
	 * @param paymentStates
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<PersonCostMobileListDomain> findByIdNumberAndPaymentStates(Pageable pageable , String idNumber, Set<Integer> paymentStates) {
		return personalCostRepository.findMobileSimpleListByPaymentState(pageable, idNumber, PublishState.PUBLISH.getState(), paymentStates,DataValidity.VALID.getState());
	}

//	public List<PersonalCost> findByPaymentSubject(PaymentSubject paymentSubject) {
//		return personalCostRepository.findByDeleteFlagAndPaymentSubject(DataValidity.VALID.getState(), paymentSubject);
//	}

	/**
	 * 按照身份证号码，
	 * @param idNumber
	 * @return
	 */
	@Transactional(readOnly = true)
 	public List<PersonalCost> findByIdNumber(String idNumber) {
		return personalCostRepository.findByIdNumberAndDeleteFlag(idNumber, DataValidity.VALID.getState());
	}

	/**
	 * 查询一批身份证在当前收费人员中是否存在
	 * @param paymentSubject
	 * @param idNumbers
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countByPaymentSubjectAndIdNumberIn (PaymentSubject paymentSubject, Set<String> idNumbers) {
 		return personalCostRepository.countByPaymentSubjectAndDeleteFlagAndIdNumberIn(paymentSubject, DataValidity.VALID.getState(), idNumbers);
	}


	/**
	 * 查询当前时间有缴费项的学生信息
	 * 对应的缴费科目必须是已发布状态，没有过期，没有被删除的
	 * @param idNumber		身份证号
	 * @param date			日期
	 * @param publishState	发布状态
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<FeeUserInfoDomain> findHasPayUserInfo (String idNumber, Date date, Integer publishState) {
		return personalCostRepository.findPersonalInfo(idNumber, date, publishState, DataValidity.VALID.getState());
	}

	@Transactional(readOnly = true)
	Page<StudentPaySubjectDomain> findPersonalCostSubjectList(Pageable pageable, String idNumber, Integer publishState){
		return personalCostRepository.findPersonalCostSubjectList(pageable, idNumber, publishState, DataValidity.VALID.getState());
	}

	@Transactional(readOnly = true)
	Page<StudentPaySubjectDomain> findPersonalCostSubjectListPaymentStateLessEq(Pageable pageable, String idNumber, Integer publishState, Integer paymentState){
		return personalCostRepository.findPersonalCostSubjectListPaymentStateLessEq(pageable, idNumber, publishState, paymentState, DataValidity.VALID.getState());
	}

	@Transactional(readOnly = true)
	Page<StudentPaySubjectDomain> findPersonalCostSubjectListPaymentStateGe(Pageable pageable, String idNumber, Integer publishState, Integer paymentState){
		return personalCostRepository.findPersonalCostSubjectListPaymentStateGe(pageable, idNumber, publishState, paymentState, DataValidity.VALID.getState());
	}

	@Transactional(readOnly = true)
	List<PersonalCost> findByIdNumberAndPaymentSubjectIdIn( String idNumber, Set<String> personCostIdSet){
		return personalCostRepository.findByIdNumberAndPaymentSubject_idInAndDeleteFlag(idNumber, personCostIdSet, DataValidity.VALID.getState());
	}

//	@Transactional(readOnly = true)
//	List<PaymentOrderItemListDomain> findByPaymentSubjectId(String paymentSubjectId){
//		return personalCostRepository.findAllByPaymentSubject_Id(paymentSubjectId);
//	}
//
//	@Transactional(readOnly = true)
//	List<PaymentOrderItemListDomain> findByPaymentSubjectId(String paymentSubjectId, String name){
//		return personalCostRepository.findAllByPaymentSubject_IdAndName(paymentSubjectId, name);
//	}

	@Transactional(readOnly = true)
	List<PersonalCost> findByIdNumberDesc(String idNumber) {
		return personalCostRepository.findByIdNumberAndDeleteFlagOrderByLastModifiedDateDesc(idNumber, DataValidity.VALID.getState());
	}
	//*************************************************************以下部分处理页面调用逻辑**********************************************************************//	
	/**
	 * 导入缴费科目对应的人员缴费信息
	 * @param paymentSubject	缴费科目实体对象
	 * @param personCostExcelDomainList		学生录取预置数据
	 * @return						缴费科目实体对象
	 */
	public void save(PaymentSubject paymentSubject, List<PersonCostExcelDomain> personCostExcelDomainList) {
		List<PersonalCost> list = new ArrayList<>();
		Set<String> professionalNameSet = new HashSet<>();
		for (PersonCostExcelDomain d : personCostExcelDomainList) {
			PersonalCost p = new PersonalCost(d);
			p.setPaymentSubject(paymentSubject);
			p.setPaymentState(PaymentState.NOPAY.getState());
			professionalNameSet.add(p.getProfessionalName());
			list.add(p);
		}
		if (list.size() > 0) {
			save(list);
		}
	}

	/**
	 * 列表查询
	 * @param paymentSubjectId	收费科目ID
	 * @param name			姓名或者身份证号
	 * @param pageNumber	第几页
	 * @param pageSize		每页的数据条数
	 * @return				查询结果
	 */
	@Transactional(readOnly = true)
	public PageData<PersonCostQueryListDomain> query(String paymentSubjectId, String name, Integer paymentState, String professionalName, Integer pageNumber, Integer pageSize) {
		Pageable pageable = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
		PageData<PersonCostQueryListDomain> p = new PageData<>();
		p.getPage().setPageNumber(pageable.getPageNumber() + 1);
		p.getPage().setPageSize(pageable.getPageSize());
		if (StringUtils.isEmpty(paymentSubjectId)) {
			return p;
		}
		Page<PersonCostQueryListDomain> pageData = null;

		Map<String, Object> condition = new HashMap<>();
		condition.put("deleteFlag", DataValidity.VALID.getState());
		condition.put("paymentSubjectId", paymentSubjectId);

		StringBuilder chql = new StringBuilder("select count(c.id) from com.aizhixin.cloud.paycallback.entity.PersonalCost c where c.deleteFlag = :deleteFlag and c.paymentSubject.id = :paymentSubjectId");
		StringBuilder hql = new StringBuilder("select new com.aizhixin.cloud.paycallback.domain.PersonCostQueryListDomain(c.id, c.name, c.idNumber, c.admissionNoticeNumber, c.professionalName, c.shouldPay, c.hasPay, c.paymentState, c.payDesc, c.personalState, c.payNumber) from com.aizhixin.cloud.paycallback.entity.PersonalCost c where c.deleteFlag = :deleteFlag and c.paymentSubject.id = :paymentSubjectId");
		if (!StringUtils.isEmpty(name)) {
			chql.append(" and (c.name like :name or c.idNumber like :name)");
			hql.append(" and (c.name like :name or c.idNumber like :name)");
			condition.put("name", "%" + name + "%");
		}
		if (null != paymentState) {
			chql.append(" and c.paymentState = :paymentState");
			hql.append(" and c.paymentState = :paymentState");
			condition.put("paymentState", paymentState);
		}
		if (!StringUtils.isEmpty(professionalName)) {
			chql.append(" and c.professionalName like :professionalName");
			hql.append(" and c.professionalName like :professionalName");
			condition.put("professionalName", "%" + professionalName + "%");
		}
		hql.append(" order by c.createdDate desc");
		Query q = em.createQuery(chql.toString());
		for (Map.Entry<String, Object> e : condition.entrySet()) {
			q.setParameter(e.getKey(), e.getValue());
		}
		Long count = (Long) q.getSingleResult();
		p.getPage().setTotalElements(count);
		if (count <= 0) {
			p.getPage().setTotalPages(1);
			return p;
		}
		TypedQuery<PersonCostQueryListDomain> tq = em.createQuery(hql.toString(), PersonCostQueryListDomain.class);
		for (Map.Entry<String, Object> e : condition.entrySet()) {
			tq.setParameter(e.getKey(), e.getValue());
		}
		tq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
		tq.setMaxResults(pageable.getPageSize());
		p.setData(tq.getResultList());
		p.getPage().setTotalPages(PageUtil.cacalatePagesize(count, p.getPage().getPageSize()));

		Set<String> personCostIdSet = new HashSet<>();
        List<PersonCostQueryListDomain> rs = p.getData();
        for (PersonCostQueryListDomain pcq : rs) {
            if (null == pcq.getPersonalState()) {
                pcq.setPersonalState(PersonalCostState.NORMAL.getState());
            }
            if (null == pcq.getPayNumber()) {
                pcq.setPayNumber(0L);
            }
            personCostIdSet.add(pcq.getId());
        }

//		Map<String, Long> orderCountMap = new HashMap<>();
//        List<StringIdCountDomain> clist = null;
//        if ( count <= p.getPage().getPageSize()) {
//            clist = personalCostOrderService.countByPaymentSubjectId(paymentSubjectId, OrderState.EXCUTED.getState());
//        } else {
//            clist = personalCostOrderService.countByPaymentSubjectIdAndPersonalCostIds(paymentSubjectId, OrderState.EXCUTED.getState(), personCostIdSet);
//        }
//        Map<String, Long> personalCountMap = new HashMap<>();
//        for (StringIdCountDomain c : clist) {
//            orderCountMap.put(c.getId(), c.getCount());
//            personalCountMap.put(c.getId(), c.getCount());
//        }
//        for (PersonCostQueryListDomain pcq : rs) {
//            Long pc = personalCountMap.get(pcq.getId());
//            if (null != pc) {
//                pcq.setPayNumber(pc);
//            }
//        }
		return p;
	}

	@Transactional(readOnly = true)
	public PaymentSubjectCalculateDomain calculatePaymentSubject(PaymentSubject paymentSubject) {
		PaymentSubjectCalculateDomain paymentSubjectCalculateDomain = new PaymentSubjectCalculateDomain ();
		paymentSubjectCalculateDomain.setName(paymentSubject.getName());
		paymentSubjectCalculateDomain.setTotalPersons(countAllPerson(paymentSubject));//总人数
		paymentSubjectCalculateDomain.setNoPayPersons(countPaymentStatePerson(paymentSubject, PaymentState.NOPAY.getState()));//未付款人数
		paymentSubjectCalculateDomain.setOwedPersons(countPaymentStatePerson(paymentSubject, PaymentState.OWED.getState()));//欠款总人数
		paymentSubjectCalculateDomain.setCompletePersions(countPaymentStatePerson(paymentSubject, PaymentState.COMPLETE.getState()));//总结清人数

		paymentSubjectCalculateDomain.setTotalShouldPay(sumShouldPayAll(paymentSubject));//总应付金额
		paymentSubjectCalculateDomain.setNoPayShouldPay(sumShouldPayAllPaymentState(paymentSubject, PaymentState.NOPAY.getState()));//未付款总已付金额
		paymentSubjectCalculateDomain.setOwedHasPay(sumHasPayAllPaymentState(paymentSubject, PaymentState.OWED.getState()));//欠款人总已付金额
		paymentSubjectCalculateDomain.setCompleteHasPay(sumHasPayAllPaymentState(paymentSubject, PaymentState.COMPLETE.getState()));//结清人总已付金额
		if (null == paymentSubjectCalculateDomain.getNoPayShouldPay()) {
			paymentSubjectCalculateDomain.setNoPayShouldPay(0.00);
		}
		if (null == paymentSubjectCalculateDomain.getOwedHasPay()) {
			paymentSubjectCalculateDomain.setOwedHasPay(0.00);
		}
		if (null == paymentSubjectCalculateDomain.getCompleteHasPay()) {
			paymentSubjectCalculateDomain.setCompleteHasPay(0.00);
		}
		return paymentSubjectCalculateDomain;
	}


	/**
	 * 学生放弃本校继续学习的计划，标签缴费为放弃状态
	 * @param idNumber	身份证号码
	 * @param userId	操作人
	 */
	public void cansel(String idNumber, Long userId) {
		List<PersonalCost> personalCostList = findByIdNumber(idNumber);
		if (null != personalCostList && !personalCostList.isEmpty()) {
			Date current = new Date ();
			for (PersonalCost p : personalCostList) {
				p.setPersonalState(PersonalCostState.CANCEL.getState());
				p.setLastModifiedBy(userId);
				p.setLastModifiedDate(current);
			}
			save(personalCostList);
		}
	}

	/**
	 * 统计身份证对应的支付成功的订单数量
	 * @param idNumber
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long countOrders(String idNumber) {
		return personalCostOrderService.countByPersonalCostIdOrders(idNumber);
	}

/************************************************************************************************手机端API********************************************************************************************************/
	/**
	 * 手机个人费用支付列表及查询接口
	 * @param authorization		token
	 * @param paymentState		支付状态
	 * @param pageNumber		第几页
	 * @param pageSize			每页几条
	 * @return					费用列表
	 */
	@Transactional(readOnly = true)
	public PageData<PersonCostMobileListDomain> phoneQueryPersonCost(String authorization, Integer paymentState, Integer pageNumber, Integer pageSize) {
		Pageable pageable = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
		PageData<PersonCostMobileListDomain> p = new PageData<>();
		p.getPage().setPageNumber(pageable.getPageNumber() + 1);
		p.getPage().setPageSize(pageable.getPageSize());
		Page<PersonCostMobileListDomain> page = null;
		String idNumber = null;// = "000111200112120222";//根据当前登录用户获取身份证号
		UserInfoDomain user = publicMobileService.getUserInfo(authorization);
		if (null != user) {
			idNumber = user.getIdNumber();
		}
		if (null == user || null == user.getId()) {
			throw new NoAuthenticationException();
		}
		if (StringUtils.isEmpty(idNumber)) {
			return p;
		}
		if (null == paymentState || paymentState.intValue() == 50) {//所有
			page =  findByIdNumber (pageable, idNumber);
		} else {
			Set<Integer>  paymentStates = new HashSet<>();
			if (paymentState.intValue() == 10 ) {
				paymentStates.add(PaymentState.NOPAY.getState());
				paymentStates.add(PaymentState.OWED.getState());
			} else {
				paymentStates.add(PaymentState.COMPLETE.getState());
			}
			page =  findByIdNumberAndPaymentStates (pageable, idNumber, paymentStates);
		}
		if (null != page) {
			p.getPage().setTotalElements(page.getTotalElements());
			p.getPage().setTotalPages(page.getTotalPages());
			p.setData(page.getContent());
		}
		return p;
	}

	/**
	 * 查询某个缴费科目的详情
	 * @param authorization		token
	 * @param personCostId		个人费用ID
	 * @return					费用详情
	 */
	@Transactional(readOnly = true)
	public PersonCostMobileDetailDomain phoneQueryPersonCostDetail(String authorization, String personCostId) {
		PersonCostMobileDetailDomain personCostMobileDetailDomain = new PersonCostMobileDetailDomain ();
//		String idNumber = "000111200112120222";//根据当前登录用户获取身份证号或手机号码
		String phone = null;// "136****2289";
		UserInfoDomain user = publicMobileService.getUserInfo(authorization);
		if (null != user) {
//			idNumber = user.getIdNumber();
			phone = user.getPhone();
			if (!StringUtils.isEmpty(phone)) {
				if (phone.length() >= 7) {
					phone = phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
				}
			}
		}
		if (null == user || null == user.getId()) {
			throw new NoAuthenticationException();
		}
		if (StringUtils.isEmpty(personCostId)) {
			return personCostMobileDetailDomain;
		}
		PersonalCost personalCost = findById(personCostId);
		if (null != personalCost) {
			personCostMobileDetailDomain.setPersonalCostId(personCostId);
			personCostMobileDetailDomain.setBuyerName(personalCost.getName());
			personCostMobileDetailDomain.setBuyerPhone(phone);
			personCostMobileDetailDomain.setShouldPay(NumberUtil.doubleToString(personalCost.getShouldPay()));
			personCostMobileDetailDomain.setHasPay(NumberUtil.doubleToString(personalCost.getHasPay()));
			personCostMobileDetailDomain.setPayDesc(personalCost.getPayDesc());
			personCostMobileDetailDomain.setPaymentState(personalCost.getPaymentState());
			if (!StringUtils.isEmpty(personalCost.getIdNumber())) {
				if (personalCost.getIdNumber().length() > 4) {
					personCostMobileDetailDomain.setIdNumber(personalCost.getIdNumber().substring(0, 4) + "****" + personalCost.getIdNumber().substring(personalCost.getIdNumber().length() - 4));
				} else {
					personCostMobileDetailDomain.setIdNumber(personalCost.getIdNumber());
				}
			}

			if (null != personalCost.getPaymentSubject()) {
				personCostMobileDetailDomain.setPublishState(personalCost.getPaymentSubject().getPublishState());
				if (PublishState.PUBLISH.getState().intValue() == personCostMobileDetailDomain.getPublishState()) {
					Date lastDate = DateUtil.nextDate(personalCost.getPaymentSubject().getLastDate());
					Date current = new Date();
					if (current.after(lastDate)) {
						personCostMobileDetailDomain.setPublishState(PublishState.EXPRIRED.getState());
					}
				}
				personCostMobileDetailDomain.setPaymentSubjectName(personalCost.getPaymentSubject().getName());
				personCostMobileDetailDomain.setPublishDate(personalCost.getPaymentSubject().getPublishTime());
				personCostMobileDetailDomain.setLastDate(personalCost.getPaymentSubject().getLastDate());
				personCostMobileDetailDomain.setPaymentType(personalCost.getPaymentSubject().getPaymentType());
				if (PaymentType.ALL.getState().intValue() == personalCost.getPaymentSubject().getPaymentType()) {
					personCostMobileDetailDomain.setSmallPay(personCostMobileDetailDomain.getShouldPay());
				} else {
					personCostMobileDetailDomain.setSmallPay(NumberUtil.doubleToString(personalCost.getPaymentSubject().getSmallAmount()));
				}
				personCostMobileDetailDomain.setInstallmentRate(personalCost.getPaymentSubject().getInstallmentRate());
			}
			if (null == personCostMobileDetailDomain.getHasPay()) {
				personCostMobileDetailDomain.setHasPay("0.0");
			}
			personCostMobileDetailDomain.setOwedPay(NumberUtil.subDouble(personalCost.getShouldPay(), personalCost.getHasPay()));
		}
		Set<Integer> orderStatus = new HashSet<>();
		orderStatus.add(OrderState.EXCUTED.getState());
//		orderStatus.add(OrderState.COMPLETE.getState());
		personCostMobileDetailDomain.setOrderList(personalCostOrderService.findByPersonalCostAndOrderStatus(personalCost, orderStatus));
		return personCostMobileDetailDomain;
	}

	/**
	 * 查询当前有缴费项目的学生信息
	 * @param orgId		学校ID	暂时没有使用
	 * @param idNumber	身份证号码
	 * @param name		姓名
	 * @return
	 */
	@Transactional(readOnly = true)
	public FeeUserInfoDomain findUserInfoHasPay(Long orgId, String idNumber, String name) {
		List<FeeUserInfoDomain> users = findHasPayUserInfo(idNumber, new Date (), PublishState.PUBLISH.getState());
		if (null != users && users.size() > 0) {
			FeeUserInfoDomain feeUserInfoDomain = users.get(0);
			if (feeUserInfoDomain.getName().equals(name)) {
				return feeUserInfoDomain;
			}
		}
		return null;
	}

	@Transactional(readOnly = true)
	public Long countByIdNumberPaymentOrder(String idNumber) {
		long c =  paymentOrderService.countByUserNo(idNumber);
		if (c > 0) {//
			List<PersonalCost> list = findByIdNumberDesc(idNumber);
			if (null != list && list.size() > 0) {
				PersonalCost personalCost = list.get(0);//最新更新的数据，最近的缴费科目
				PaymentSubject paymentSubject = personalCost.getPaymentSubject();
				if (null != paymentSubject) {
					if (PaymentType.INSTALLMENT.getState().intValue() == paymentSubject.getPaymentType()) {//如果是分期
						if (null != paymentSubject.getSmallAmount() && paymentSubject.getSmallAmount() > 0) {//有设置最低缴费金额
							if (paymentSubject.getSmallAmount() > personalCost.getHasPay()) {
								c = 0;
							}
						}
					}
				}
			}
		}
		return c;
	}

	@Transactional(readOnly = true)
	public PayResultDomain calIdNumberPaymentOrder(String idNumber) {
		PayResultDomain payResultDomain = new PayResultDomain();
		long c =  paymentOrderService.countByUserNo(idNumber);
		payResultDomain.setPayCount(c);
		if (c > 0) {//
			List<PersonalCost> list = findByIdNumberDesc(idNumber);
			if (null != list && list.size() > 0) {
				PersonalCost personalCost = list.get(0);//最新更新的数据，最近的缴费科目
				PaymentSubject paymentSubject = personalCost.getPaymentSubject();
				if (null != paymentSubject) {
					if (PaymentType.INSTALLMENT.getState().intValue() == paymentSubject.getPaymentType()) {//如果是分期
						payResultDomain.setSmallPay(paymentSubject.getSmallAmount());
					}
				}
				payResultDomain.setHasPay(personalCost.getHasPay());
			}
		}
		return payResultDomain;
	}
}