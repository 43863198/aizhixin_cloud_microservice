/**
 * 
 */
package com.aizhixin.cloud.paycallback.service;


import com.aizhixin.cloud.paycallback.common.PageData;
import com.aizhixin.cloud.paycallback.common.util.DateUtil;
import com.aizhixin.cloud.paycallback.common.util.NumberUtil;
import com.aizhixin.cloud.paycallback.domain.PaymentOrderItemListDomain;
import com.aizhixin.cloud.paycallback.domain.SimplePaymentOrderItemDomain;
import com.aizhixin.cloud.paycallback.entity.PaymentOrderItem;
import com.aizhixin.cloud.paycallback.repository.PaymentOrderItemRepository;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.*;

/**
 * 支付宝学校缴费大厅缴费订单明细相关处理
 * @author zhen.pan
 *
 */
@Component
@Transactional
public class PaymentOrderItemService {
	private final static Logger LOG = LoggerFactory.getLogger(PaymentOrderItemService.class);

	@Autowired
	private EntityManager em;

	@Autowired
	private PaymentOrderItemRepository paymentOrderItemRepository;

	/**
	 * 保存实体
	 * @param paymentOrderItem
	 * @return
	 */
	public PaymentOrderItem save(PaymentOrderItem paymentOrderItem) {
		return paymentOrderItemRepository.save(paymentOrderItem);
	}


	/**
	 * 保存实体
	 * @param paymentOrderItemList
	 * @return
	 */
	public List<PaymentOrderItem> save(List<PaymentOrderItem> paymentOrderItemList) {
		return paymentOrderItemRepository.save(paymentOrderItemList);
	}

	/**
	 * 查询人员费用明细
	 * @param personalCostId 	人员费用明细
	 * @return		明细结果
	 */
	@Transactional (readOnly = true)
	public List<SimplePaymentOrderItemDomain> findByItemId(String personalCostId) {
		return paymentOrderItemRepository.findByItemId(personalCostId);
	}



	//*************************************************************以下部分处理页面调用逻辑**********************************************************************//
	@Transactional (readOnly = true)
	public PageData<PaymentOrderItemListDomain> findByPaymentSubjectAndQueryParam(String paymentSubjectId, String name, Date start, Date end, Integer pageNumber, Integer pageSize) {
		PageData<PaymentOrderItemListDomain> page = new PageData<>();
		if (null == pageNumber) {
			pageNumber = 1;
		}
		if (null == pageSize) {
			pageSize = 20;
		}
		page.getPage().setPageNumber(pageNumber);
		page.getPage().setPageSize(pageSize);
		StringBuilder sqlCount = new StringBuilder("SELECT count(*) ");
		sqlCount.append("FROM t_personal_cost p, t_payment_order o, t_payment_order_item i ");
		sqlCount.append("WHERE p.ID=i.ITEM_ID AND i.PAYMENT_ORDER_ID=o.ID ");
		sqlCount.append("AND p.PAYMENT_SUBJECT_ID=?1 ");
		Map<Integer, String> params = new HashMap<>();
		params.put(1, paymentSubjectId);

		apendSql(sqlCount, params, name, start, end);

		StringBuilder sql = new StringBuilder("SELECT p.`NAME` as name, p.ID_NUMBER as idNumber, p.PROFESSIONAL_NAME as professionalName, p.PERSONAL_STATE as personalState, o.ORDER_ID as orderNo, o.ORDER_END_DATE as payTime, i.FEE as amount ");
		sql.append("FROM t_personal_cost p, t_payment_order o, t_payment_order_item i ");
		sql.append("WHERE p.ID=i.ITEM_ID AND i.PAYMENT_ORDER_ID=o.ID ");
		sql.append("AND p.PAYMENT_SUBJECT_ID = ?1 ");
		apendSql(sql, params, name, start, end);

		sql.append(" ORDER BY o.CREATED_DATE DESC");
		Query query = em.createNativeQuery(sqlCount.toString());

		setQueryParams(query, params);
		BigInteger c = (BigInteger)query.getSingleResult();
		page.getPage().setTotalElements(c.longValue());

		if (c.longValue() > 0) {
			query = em.createNativeQuery(sql.toString());
			query.setFirstResult((pageNumber - 1) * pageSize);
			query.setMaxResults(pageSize);
			setQueryParams(query, params);
			List<PaymentOrderItemListDomain> list = query.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(PaymentOrderItemListDomain.class)).list();
			if (null != list && !list.isEmpty()) {
				for (PaymentOrderItemListDomain p : list) {
					if (null != p.getAmount()) {
						p.setAmount(NumberUtil.doubleToString(new Double(p.getAmount())/100));
					}
				}
			}
			page.setData(list);

		}
		page.getPage().setTotalPages((int)(c.longValue() / pageSize + 1));
		return page;
	}

	private void apendSql(StringBuilder sql, Map<Integer, String> params, String name, Date start, Date end) {
		int pp = 2;
		if (!StringUtils.isEmpty(name)) {
			sql.append("AND (p.`NAME` LIKE ?").append( pp).append(" OR o.ORDER_ID LIKE ?").append(pp).append(") ");
			params.put(pp, "%" + name + "%");
			pp++;
		}
		if (null != start) {
			sql.append("AND o.ORDER_END_DATE >= ?").append(pp).append(" ");
			params.put(pp, DateUtil.format(start, "yyyy-MM-dd") + " 00:00:00");
			pp++;
		}

		if (null != end) {
			sql.append("AND o.ORDER_END_DATE <= ?").append(pp).append(" ");
			params.put(pp, DateUtil.format(end, "yyyy-MM-dd") + " 23:59:59");
			pp++;
		}
	}

	private void setQueryParams(Query query, Map<Integer, String> params) {
		if (!params.isEmpty()) {
			Iterator<Map.Entry<Integer, String>> it = params.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Integer, String> e = it.next();
				query.setParameter(e.getKey(), e.getValue());
			}
		}
	}
}