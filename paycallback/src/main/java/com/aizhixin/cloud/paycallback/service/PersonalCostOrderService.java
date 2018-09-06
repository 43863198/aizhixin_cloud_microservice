/**
 * 
 */
package com.aizhixin.cloud.paycallback.service;


import com.aizhixin.cloud.paycallback.common.PageData;
import com.aizhixin.cloud.paycallback.common.core.DataValidity;
import com.aizhixin.cloud.paycallback.common.core.PageUtil;
import com.aizhixin.cloud.paycallback.common.core.PublicErrorCode;
import com.aizhixin.cloud.paycallback.common.domain.StringIdCountDomain;
import com.aizhixin.cloud.paycallback.common.exception.CommonException;
import com.aizhixin.cloud.paycallback.common.exception.NoAuthenticationException;
import com.aizhixin.cloud.paycallback.common.util.DateUtil;
import com.aizhixin.cloud.paycallback.common.util.NumberUtil;
import com.aizhixin.cloud.paycallback.core.*;
import com.aizhixin.cloud.paycallback.domain.*;
import com.aizhixin.cloud.paycallback.entity.AliCallBackLogRecord;
import com.aizhixin.cloud.paycallback.entity.PersonalCost;
import com.aizhixin.cloud.paycallback.entity.PersonalCostOrder;
import com.aizhixin.cloud.paycallback.remote.PublicMobileService;
import com.aizhixin.cloud.paycallback.repository.PersonalCostOrderRepository;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * 学生缴费相关操作业务逻辑处理
 * @author zhen.pan
 *
 */
@Component
@Transactional
public class PersonalCostOrderService {
	private final static Logger LOG = LoggerFactory.getLogger(PersonalCostOrderService.class);
	private final static String ALIPAY_FORMAT = "json";
	private final static String ALIPAY_SIGN_TYPE = "RSA2";
	private final static String OUT_PARAM = "personalCostOrderId=";
	@Autowired
	private EntityManager em;
	@Autowired
	private PersonalCostOrderRepository personalCostOrderRepository;
	@Autowired
	private OrderNumberService orderNumberService;
	@Autowired
	private PersonalCostService personalCostService;
	@Autowired
	private PublicMobileService publicMobileService;
	@Autowired
	private AliCallBackLogRecordService aliCallBackLogRecordService;
	@Value("${pay.appId}")
	private String appId;
	@Value("${pay.appPrivateKey}")
	private String appPrivateKey;
	@Value("${pay.charset}")
	private String charset;
	@Value("${pay.alipayPublicKey}")
	private String alipayPublicKey;
	@Value("${pay.timeoutExpress}")
	private String timeoutExpress;
	@Value("${pay.productCode}")
	private String productCode;
	@Value("${pay.notifyUrl}")
	private String notifyUrl;
	@Value("${pay.alipayServerUrl}")
	private String alipayServerUrl;

	private AlipayClient alipayClient;
	private AlipayTradeAppPayRequest request;

	@PostConstruct
	public void initAlipayClient() {
		alipayClient = new DefaultAlipayClient(alipayServerUrl, appId, appPrivateKey, ALIPAY_FORMAT, charset, alipayPublicKey, ALIPAY_SIGN_TYPE); //获得初始化的AlipayClient
		request = new AlipayTradeAppPayRequest();
	}

	/**
	 * 保存实体
	 * @param personalCostOrder
	 * @return
	 */
	public PersonalCostOrder save(PersonalCostOrder personalCostOrder) {
		return personalCostOrderRepository.save(personalCostOrder);
	}
	public PersonalCostOrder findById(String id) {
		return personalCostOrderRepository.findOne(id);
	}

	/**
	 * 根据个人缴费信息和订单状态查询订单详情列表
	 * @param personalCost	个人缴费信息
	 * @param orderStatus	订单状态
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<PersonCostOrderListDomain> findByPersonalCostAndOrderStatus(PersonalCost personalCost, Set<Integer> orderStatus) {
		return personalCostOrderRepository.findByPersonalCost(personalCost, orderStatus);
	}

	/**
	 * 根据订单号和阿里交易号查询订单数据
	 * @param orderNo		订单号
	 * @param aliTradeNo	阿里交易号
	 * @return				订单列表
	 */
	@Transactional(readOnly = true)
	public List<PersonalCostOrder> findByPersonalCostAndOrderStatus(String orderNo, String aliTradeNo) {
		return personalCostOrderRepository.findByOrderNoAndAliTradeNo(orderNo, aliTradeNo);
	}

	@Transactional(readOnly = true)
	public Long countByPersonalCostPaySucess(PersonalCost personalCost) {
		return personalCostOrderRepository.countByPersonalCostAndOrderStatus(personalCost, OrderState.EXCUTED.getState());
	}

	@Transactional(readOnly = true)
	public List<StringIdCountDomain> countByPaymentSubjectId(String paymentSubjectId, Integer orderStatus) {
		return personalCostOrderRepository.countByPaymentSubjectId(paymentSubjectId, orderStatus);
	}

	@Transactional(readOnly = true)
	public List<StringIdCountDomain> countByPaymentSubjectIdAndPersonalCostIds(String paymentSubjectId, Integer orderStatus, Set<String> personalCostIds) {
		return personalCostOrderRepository.countByPaymentSubjectIdAndPersonalCostIds(paymentSubjectId, orderStatus, personalCostIds);
	}

	@Transactional(readOnly = true)
	public Long countByPersonalCostIdOrders(String idNumber) {
		return personalCostOrderRepository.countByPersonalCost_IdNumberAndOrderStatus(idNumber, OrderState.EXCUTED.getState());
	}
	//*************************************************************以下部分处理页面调用逻辑**********************************************************************//
	@Transactional(readOnly = true)
	public List<PersonCostOrderListDomain> findByPersonalCostId(String personalCostId) {
		if (StringUtils.isEmpty(personalCostId)) {
			return null;
		}
		PersonalCost personalCost = personalCostService.findById(personalCostId);
		if (null == personalCost) {
			return null;
		}
		Set<Integer> orderStates = new HashSet<>();
		orderStates.add(OrderState.EXCUTED.getState());
//		orderStates.add(OrderState.COMPLETE.getState());
		return findByPersonalCostAndOrderStatus(personalCost, orderStates);
	}

	public PageData<OrderDetailDomain> queryList(String paymentSubjectId, Date start, Date end, Integer pageNumber, Integer pageSize) {
		Pageable pageable = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
		PageData<OrderDetailDomain> p = new PageData<>();
		p.getPage().setPageNumber(pageable.getPageNumber() + 1);
		p.getPage().setPageSize(pageable.getPageSize());
		if (StringUtils.isEmpty(paymentSubjectId)) {
			return p;
		}

		Map<String, Object> condition = new HashMap<>();
		condition.put("deleteFlag", DataValidity.VALID.getState());
		condition.put("paymentSubjectId", paymentSubjectId);
		Set<Integer> orderStates = new HashSet<>();
		orderStates.add(OrderState.EXCUTED.getState());
//		orderStates.add(OrderState.COMPLETE.getState());
		condition.put("orderStatus", orderStates);

		StringBuilder chql = new StringBuilder("select count(c.id) from com.aizhixin.cloud.paycallback.entity.PersonalCostOrder c where c.deleteFlag = :deleteFlag and c.paymentSubjectId = :paymentSubjectId and c.orderStatus in (:orderStatus)");
		StringBuilder hql = new StringBuilder("select new com.aizhixin.cloud.paycallback.domain.OrderDetailDomain(c.id, c.personalCost.id, c.personalCost.name, c.personalCost.idNumber, c.personalCost.admissionNoticeNumber, c.personalCost.professionalName, c.orderNo, c.payTime, c.orderAmount, c.aliTradeNo, c.personalCost.personalState) from com.aizhixin.cloud.paycallback.entity.PersonalCostOrder c where c.deleteFlag = :deleteFlag and c.paymentSubjectId = :paymentSubjectId and c.orderStatus in (:orderStatus)");
		if (null != start) {
			chql.append(" and c.payTime >= :start");
			hql.append(" and c.payTime >= :start");
			condition.put("start", start);
		}
		if (null != end) {
			chql.append(" and c.payTime <= :end");
			hql.append(" and c.payTime <= :end");
			condition.put("end", end);
		}
		hql.append(" order by c.lastModifiedDate desc");
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
		TypedQuery<OrderDetailDomain> tq = em.createQuery(hql.toString(), OrderDetailDomain.class);
		for (Map.Entry<String, Object> e : condition.entrySet()) {
			tq.setParameter(e.getKey(), e.getValue());
		}

		tq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
		tq.setMaxResults(pageable.getPageSize());
		p.setData(tq.getResultList());
		p.getPage().setTotalPages(PageUtil.cacalatePagesize(count, p.getPage().getPageSize()));
		return p;
	}



	/************************************************************************************************手机端API********************************************************************************************************/
	public OrderUrlMobileDomain createOrder(String authorization, String personCostId, Double payAmount) {
		UserInfoDomain user = publicMobileService.getUserInfo(authorization);
		if (null == user || null == user.getId()) {
			throw new NoAuthenticationException();
		}
		if(StringUtils.isEmpty(personCostId)) {
			throw new CommonException(PublicErrorCode.PARAM_EXCEPTION.getIntValue(), "个人费用ID是必须的");
		}
		if (null == payAmount || payAmount <= 0.0) {
			throw new CommonException(PublicErrorCode.PARAM_EXCEPTION.getIntValue(), "收费金额必须是大于0的数字");
		}

		PersonalCost personalCost = personalCostService.findById(personCostId);
		if (null == personalCost) {
			throw new CommonException(PublicErrorCode.PARAM_EXCEPTION.getIntValue(), "根据个人费用ID查询不到对应的收费信息");
		}
		if (PublishState.PUBLISH.getState().intValue() != personalCost.getPaymentSubject().getPublishState()) {
			throw new CommonException(PublicErrorCode.PARAM_EXCEPTION.getIntValue(), "缴费科目不是发布状态，不能进行缴费操作");
		}
		if (PaymentState.COMPLETE.getState().intValue() == personalCost.getPaymentState()) {
			throw new CommonException(PublicErrorCode.PARAM_EXCEPTION.getIntValue(), "已经完成付款");
		}
		if (null == personalCost.getPaymentSubject()) {
			throw new CommonException(PublicErrorCode.PARAM_EXCEPTION.getIntValue(), "收费科目不存在");
		}

		Date lastDate = DateUtil.nextDate(personalCost.getPaymentSubject().getLastDate());
		Date current = new Date();
		if (current.after(lastDate)) {
			throw new CommonException(PublicErrorCode.PARAM_EXCEPTION.getIntValue(), "该收费科目已经过期");
		}

		if (PaymentType.ALL.getState().intValue() == personalCost.getPaymentSubject().getPaymentType()) {
			if (payAmount.doubleValue() < personalCost.getShouldPay()) {
				throw new CommonException(PublicErrorCode.PARAM_EXCEPTION.getIntValue(), "该收费要求一次付清");
			}
		} else {
			double owedPay = 0.0;//欠款
			double hasPay = 0.0;//已付
			if (null != personalCost.getHasPay()) {
				hasPay = personalCost.getHasPay();
			}
			owedPay = personalCost.getShouldPay() - hasPay;
			if (payAmount < personalCost.getPaymentSubject().getSmallAmount()) {//小于最低付款金额
				if(InstallmentAmount.FIRST.getState().intValue() == personalCost.getPaymentSubject().getInstallmentRate()) {//首次最低限制
					if (hasPay <= 0.0) {
						if (payAmount < owedPay) {
							throw new CommonException(PublicErrorCode.PARAM_EXCEPTION.getIntValue(), "需付款金额小于最低付款金额，请一次性结清");
						}
					}
				} else {//每次最低限制
					if (payAmount < owedPay) {
						throw new CommonException(PublicErrorCode.PARAM_EXCEPTION.getIntValue(), "需付款金额小于最低付款金额，请一次性结清");
					}
				}
			}
		}
		PersonalCostOrder order = new PersonalCostOrder();
		order.setOrderStatus(OrderState.INIT.getState());
		order.setOrderType(OrderType.PAYMENT.getState());
		order.setOrderAmount(payAmount);
		order.setOrderContent(personalCost.getPayDesc());
		order.setOrderSubject(personalCost.getPaymentSubject().getName());
		order.setPaymentSubjectId(personalCost.getPaymentSubject().getId());
		order.setPersonalCost(personalCost);
		order.setOrderNo(orderNumberService.generaterOrderNumber(user.getOrgId()));//根据登录用户获取
		order = save(order);
		AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
		model.setBody(personalCost.getPayDesc());
		model.setSubject(personalCost.getPaymentSubject().getName());
		model.setOutTradeNo(order.getOrderNo());
		model.setTotalAmount(NumberUtil.doubleToString(payAmount));
		model.setTimeoutExpress(timeoutExpress);
		model.setProductCode(productCode);

		OrderUrlMobileDomain orderUrlMobileDomain = new OrderUrlMobileDomain();
		try {
			model.setPassbackParams(URLEncoder.encode(OUT_PARAM + order.getId(), charset));
			request.setBizModel(model);
			request.setNotifyUrl(notifyUrl);
			//这里和普通的接口调用不同，使用的是sdkExecute
			AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
			orderUrlMobileDomain.setPayUrl(response.getBody());//就是orderString 可以直接给客户端请求，无需再做处理。
		} catch (UnsupportedEncodingException e) {
			LOG.warn("create alipay order fail:{}", e);
		} catch (AlipayApiException e) {
			LOG.warn("create alipay order fail:{}", e);
		}
		return orderUrlMobileDomain;
	}


	@Transactional(readOnly = true)
	public OrderResultMobileDomain orderQuery(String authorization, String out_trade_no, String trade_no, String app_id, String seller_id, Double total_amount) {
		UserInfoDomain user = publicMobileService.getUserInfo(authorization);
		if (null == user || null == user.getId()) {
			throw new NoAuthenticationException();
		}
		OrderResultMobileDomain orderResultMobileDomain = new OrderResultMobileDomain ();
		if (StringUtils.isEmpty(out_trade_no) || StringUtils.isEmpty(trade_no) || null == total_amount) {
			throw new CommonException(PublicErrorCode.PARAM_EXCEPTION.getIntValue(), "订单号、支付宝交易号、交易金额是必须的");
		}
		List<PersonalCostOrder> orders = findByPersonalCostAndOrderStatus(out_trade_no, trade_no);
		if (null != orders && orders.size() > 0) {
			PersonalCostOrder order = orders.get(0);
			orderResultMobileDomain.setAliOrderNo(order.getAliTradeNo());
			orderResultMobileDomain.setOrderNo(order.getOrderNo());
			orderResultMobileDomain.setPaymentSubjectName(order.getOrderSubject());
			orderResultMobileDomain.setPayDesc(order.getOrderContent());
			orderResultMobileDomain.setPayTime(order.getPayTime());
			orderResultMobileDomain.setAmount(order.getOrderAmount());
			if (null != order.getPersonalCost()) {
				orderResultMobileDomain.setPersonalCostId(order.getPersonalCost().getId());
			}
		} else {
			throw new CommonException(PublicErrorCode.PARAM_EXCEPTION.getIntValue(), "支付结果还没有返回，请稍等一会再试！");
		}
		return orderResultMobileDomain;
	}

	public void doAliCallBackOrder(AliCallBackLogRecord record) {
		if (null != record) {
			aliCallBackLogRecordService.save(record);
			if (!StringUtils.isEmpty(record.getOutTradeNo()) && !StringUtils.isEmpty(record.getPassbackParams())) {
				try {
					String passbackParams = URLDecoder.decode(record.getPassbackParams(), charset);
					if (passbackParams.length() > OUT_PARAM.length()) {
						if ("TRADE_SUCCESS".equalsIgnoreCase(record.getTradeStatus())) {//交易付款成功
							String id = passbackParams.substring(OUT_PARAM.length());
							PersonalCostOrder personalCostOrder = findById(id);
							if (null != personalCostOrder) {
								if (personalCostOrder.getOrderNo().equals(record.getOutTradeNo())) {//验证订单号一致
									personalCostOrder.setAliTradeNo(record.getTradeNo());
								}
								if (personalCostOrder.getOrderAmount().doubleValue() != record.getReceiptAmount().doubleValue()) {//验证订单金额和收款金额一致
									LOG.warn("personal cost order id({}), order no({}), order amount ({}), receipt amount:({})", id, record.getOutTradeNo(), personalCostOrder.getOrderAmount(), record.getReceiptAmount());
									personalCostOrder.setOrderAmount(record.getReceiptAmount());
								}
								personalCostOrder.setOrderStatus(OrderState.EXCUTED.getState());//订单已经支付
								personalCostOrder.setPayTime(record.getGmtPayment());//付款时间
								personalCostOrder.setLastModifiedDate(new Date());
								save(personalCostOrder);//更新订单记录
								if (null != personalCostOrder.getPersonalCost()) {
									PersonalCost personalCost = personalCostOrder.getPersonalCost();
									personalCost.setHasPay((null == personalCost.getHasPay() ? 0.0 : personalCost.getHasPay()) + personalCostOrder.getOrderAmount());
									if (personalCost.getShouldPay() > personalCost.getHasPay()) {
										personalCost.setPaymentState(PaymentState.OWED.getState());
									} else if (personalCost.getShouldPay() <= personalCost.getHasPay()) {
										personalCost.setPaymentState(PaymentState.COMPLETE.getState());
									}
//									personalCost.setPayNumber(countByPersonalCostPaySucess(personalCost));//计算付款成功的次数
									personalCostService.save(personalCost);
								}
							}
						} else {//交易关闭(含退款)，只保存记录就行
							LOG.warn("pay state is not TRADE_SUCCESS, ({})", record.getTradeStatus());
						}
					} else {
						LOG.warn("call back params error.({})", record.getPassbackParams());
					}
				} catch (UnsupportedEncodingException e) {
					LOG.warn("URLDecoder.decode fail.String ({}), charset ({})", record.getPassbackParams(), charset);
				}
			}
		}
	}
}