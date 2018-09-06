package com.aizhixin.cloud.paycallback.repository;


import com.aizhixin.cloud.paycallback.domain.PaymentOrderItemListDomain;
import com.aizhixin.cloud.paycallback.domain.SimplePaymentOrderItemDomain;
import com.aizhixin.cloud.paycallback.entity.PaymentOrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface PaymentOrderItemRepository extends JpaRepository<PaymentOrderItem, String> {

    @Query("select new com.aizhixin.cloud.paycallback.domain.SimplePaymentOrderItemDomain(c.paymentOrder.orderId, c.paymentOrder.orderEndDate, c.fee) from #{#entityName} c where c.itemId = :itemId order by c.paymentOrder.orderEndDate desc")
    List<SimplePaymentOrderItemDomain> findByItemId (@Param(value = "itemId") String itemId);

    @Query(value = "SELECT p.`NAME` as name, p.ID_NUMBER as idNumber, p.PROFESSIONAL_NAME as professionalName, p.PERSONAL_STATE as personalState, o.ORDER_ID as orderNo, o.ORDER_END_DATE as payTime, i.FEE as amount FROM t_personal_cost p, t_payment_order o, t_payment_order_item i WHERE p.ID=i.ITEM_ID AND i.PAYMENT_ORDER_ID=o.ID AND p.PAYMENT_SUBJECT_ID=?1 ?#{#pageable}",
            countQuery = "SELECT count(*) FROM t_personal_cost p, t_payment_order o, t_payment_order_item i WHERE p.ID=i.ITEM_ID AND i.PAYMENT_ORDER_ID=o.ID AND p.PAYMENT_SUBJECT_ID=?1",
            nativeQuery = true)
    Page<PaymentOrderItemListDomain> findByDetails(Pageable pageable, String paymentSubjectId);
}
