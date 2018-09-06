/**
 * 
 */
package com.aizhixin.cloud.paycallback.service;


import com.aizhixin.cloud.paycallback.entity.PaymentOrder;
import com.aizhixin.cloud.paycallback.repository.PaymentOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 支付宝学校缴费大厅缴费订单相关处理
 * @author zhen.pan
 *
 */
@Component
@Transactional
public class PaymentOrderService {
	private final static Logger LOG = LoggerFactory.getLogger(PaymentOrderService.class);

	@Autowired
	private PaymentOrderRepository paymentOrderRepository;

	/**
	 * 保存实体
	 * @param paymentOrder
	 * @return
	 */
	public PaymentOrder save(PaymentOrder paymentOrder) {
		return paymentOrderRepository.save(paymentOrder);
	}

	public long countByUserNo(String userNo) {
		return paymentOrderRepository.countByUserNo(userNo);
	}
}