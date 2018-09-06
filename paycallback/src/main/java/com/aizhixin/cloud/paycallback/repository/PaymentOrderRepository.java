package com.aizhixin.cloud.paycallback.repository;


import com.aizhixin.cloud.paycallback.entity.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, String> {
    long countByUserNo(String userNo);
}
