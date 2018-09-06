package com.aizhixin.cloud.paycallback.repository;


import com.aizhixin.cloud.paycallback.common.domain.StringIdCountDomain;
import com.aizhixin.cloud.paycallback.domain.PersonCostOrderListDomain;
import com.aizhixin.cloud.paycallback.entity.PersonalCost;
import com.aizhixin.cloud.paycallback.entity.PersonalCostOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;


public interface PersonalCostOrderRepository extends JpaRepository<PersonalCostOrder, String> {

    @Query("select new com.aizhixin.cloud.paycallback.domain.PersonCostOrderListDomain(orderNo, payTime, orderAmount, aliTradeNo) from #{#entityName} c where c.personalCost = :personalCost and c.orderStatus in (:orderStatus)  order by c.createdDate desc")
    List<PersonCostOrderListDomain> findByPersonalCost(@Param(value = "personalCost") PersonalCost personalCost, @Param(value = "orderStatus") Set<Integer> orderStatus);

    List<PersonalCostOrder> findByOrderNoAndAliTradeNo(String orderNo, String aliTradeNo);

    Long countByPersonalCostAndOrderStatus(PersonalCost personalCost, Integer orderStatus);

    @Query("select new com.aizhixin.cloud.paycallback.common.domain.StringIdCountDomain(c.personalCost.id, count(c.id)) from #{#entityName} c where c.paymentSubjectId = :paymentSubjectId and c.orderStatus = :orderStatus  group by c.personalCost.id")
    List<StringIdCountDomain> countByPaymentSubjectId(@Param(value = "paymentSubjectId") String paymentSubjectId, @Param(value = "orderStatus") Integer orderStatus);


    @Query("select new com.aizhixin.cloud.paycallback.common.domain.StringIdCountDomain(c.personalCost.id, count(c.id)) from #{#entityName} c where c.paymentSubjectId = :paymentSubjectId and c.orderStatus = :orderStatus and c.personalCost.id in (:personalCostIds)  group by c.personalCost.id")
    List<StringIdCountDomain> countByPaymentSubjectIdAndPersonalCostIds(@Param(value = "paymentSubjectId") String paymentSubjectId, @Param(value = "orderStatus") Integer orderStatus, @Param(value = "personalCostIds") Set<String> personalCostIds);

    Long countByPersonalCost_IdNumberAndOrderStatus(String idNumber, Integer orderStatus);
}
