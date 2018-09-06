package com.aizhixin.cloud.paycallback.repository;


import com.aizhixin.cloud.paycallback.domain.PaymentSubjectQueryListDomain;
import com.aizhixin.cloud.paycallback.entity.PaymentSubject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;


public interface PaymentSubjectRepository extends JpaRepository<PaymentSubject, String> {

    @Query("select new com.aizhixin.cloud.paycallback.domain.PaymentSubjectQueryListDomain(id, name, publishState, lastDate, paymentType) from #{#entityName} c where c.orgId = :orgId and c.deleteFlag = :deleteFlag order by  c.createdDate desc")
    Page<PaymentSubjectQueryListDomain> findByOrgId(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.paycallback.domain.PaymentSubjectQueryListDomain(id, name, publishState, lastDate, paymentType) from #{#entityName} c where c.orgId = :orgId and c.name like :name and c.deleteFlag = :deleteFlag  order by  c.createdDate desc")
    Page<PaymentSubjectQueryListDomain> findByOrgIdAndName(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "name") String name, @Param(value = "deleteFlag") Integer deleteFlag);

    List<PaymentSubject> findByDeleteFlagAndPublishStateAndLastDateLessThan(Integer deleteFlag, Integer publishState, Date lastDate);
}
