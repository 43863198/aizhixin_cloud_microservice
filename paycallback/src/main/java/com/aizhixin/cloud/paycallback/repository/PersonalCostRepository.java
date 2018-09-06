package com.aizhixin.cloud.paycallback.repository;


import com.aizhixin.cloud.paycallback.domain.FeeUserInfoDomain;
import com.aizhixin.cloud.paycallback.domain.PersonCostMobileListDomain;
import com.aizhixin.cloud.paycallback.domain.third.StudentPaySubjectDomain;
import com.aizhixin.cloud.paycallback.entity.PaymentSubject;
import com.aizhixin.cloud.paycallback.entity.PersonalCost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;


public interface PersonalCostRepository extends JpaRepository<PersonalCost, String> {

    @Modifying
    @Query("update  #{#entityName} t set t.deleteFlag = :deleteFlag where t.paymentSubject = :paymentSubject")
    void udpateByPaymentSubjectId(@Param(value = "paymentSubject") PaymentSubject paymentSubject, @Param(value = "deleteFlag") Integer deleteFlag);

    Long countByPaymentSubjectAndDeleteFlag(PaymentSubject paymentSubject, Integer deleteFlag);

    Long countByPaymentSubjectAndDeleteFlagAndPaymentState(PaymentSubject paymentSubject, Integer deleteFlag, Integer paymentState);

    @Query("select sum(t.shouldPay) from  #{#entityName} t where t.deleteFlag = :deleteFlag and t.paymentSubject = :paymentSubject")
    Double sumByPaymentSubjectAndDeleteFlag(@Param(value = "paymentSubject") PaymentSubject paymentSubject, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select sum(t.hasPay) from  #{#entityName} t where t.deleteFlag = :deleteFlag and t.paymentSubject = :paymentSubject and t.paymentState = :paymentState")
    Double sumHasPayByPaymentSubjectAndDeleteFlagAndPaymentState(@Param(value = "paymentSubject") PaymentSubject paymentSubject, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "paymentState") Integer paymentState);

    @Query("select sum(t.shouldPay) from  #{#entityName} t where t.deleteFlag = :deleteFlag and t.paymentSubject = :paymentSubject and t.paymentState = :paymentState")
    Double sumShoudPayByPaymentSubjectAndDeleteFlagAndPaymentState(@Param(value = "paymentSubject") PaymentSubject paymentSubject, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "paymentState") Integer paymentState);

    @Query ("select  new com.aizhixin.cloud.paycallback.domain.PersonCostMobileListDomain(t.id, t.paymentSubject.name, t.paymentSubject.lastDate, t.paymentSubject.paymentType, t.paymentSubject.publishState, t.shouldPay, t.hasPay, t.paymentState) from #{#entityName} t where t.idNumber=:idNumber and t.deleteFlag = :deleteFlag and t.paymentSubject.deleteFlag = :deleteFlag and  t.paymentSubject.publishState >= :publishState order by t.createdDate desc")
    Page<PersonCostMobileListDomain> findMobileSimpleList(Pageable pageable, @Param(value = "idNumber") String idNumber, @Param(value = "publishState") Integer publishState, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query ("select  new com.aizhixin.cloud.paycallback.domain.PersonCostMobileListDomain(t.id, t.paymentSubject.name, t.paymentSubject.lastDate, t.paymentSubject.paymentType, t.paymentSubject.publishState, t.shouldPay, t.hasPay, t.paymentState) from #{#entityName} t where t.deleteFlag = :deleteFlag and t.paymentSubject.deleteFlag = :deleteFlag and  t.paymentSubject.publishState >= :publishState and t.paymentState in (:paymentStates)  and t.idNumber=:idNumber order by t.createdDate desc")
    Page<PersonCostMobileListDomain> findMobileSimpleListByPaymentState(Pageable pageable, @Param(value = "idNumber") String idNumber, @Param(value = "publishState") Integer publishState, @Param(value = "paymentStates") Set<Integer> paymentStates, @Param(value = "deleteFlag") Integer deleteFlag);

//    List<PersonalCost> findByDeleteFlagAndPaymentSubject(Integer deleteFlag, PaymentSubject paymentSubject);
    List<PersonalCost> findByIdNumberAndDeleteFlag(String idNumber, Integer deleteFlag);

    Long countByPaymentSubjectAndDeleteFlagAndIdNumberIn(PaymentSubject paymentSubject, Integer deleteFlag, Set<String> idNumbers);

    @Query ("select  new com.aizhixin.cloud.paycallback.domain.FeeUserInfoDomain(t.name, t.idNumber, t.professionalName) from #{#entityName} t where t.idNumber=:idNumber and  t.deleteFlag = :deleteFlag and t.paymentSubject.deleteFlag = :deleteFlag and  t.paymentSubject.publishState = :publishState and t.paymentSubject.lastDate >= :current")
    List<FeeUserInfoDomain> findPersonalInfo(@Param(value = "idNumber") String idNumber, @Param(value = "current") Date current,  @Param(value = "publishState") Integer publishState, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query ("select  new com.aizhixin.cloud.paycallback.domain.third.StudentPaySubjectDomain(t.id, t.paymentSubject.name, t.paymentSubject.publishTime, t.paymentSubject.paymentType, t.shouldPay, t.hasPay) from #{#entityName} t where t.idNumber=:idNumber and t.deleteFlag = :deleteFlag and t.paymentSubject.deleteFlag = :deleteFlag and  t.paymentSubject.publishState = :publishState order by t.paymentSubject.publishTime desc")
    Page<StudentPaySubjectDomain> findPersonalCostSubjectList(Pageable pageable, @Param(value = "idNumber") String idNumber, @Param(value = "publishState") Integer publishState, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query ("select  new com.aizhixin.cloud.paycallback.domain.third.StudentPaySubjectDomain(t.id, t.paymentSubject.name, t.paymentSubject.publishTime, t.paymentSubject.paymentType, t.shouldPay, t.hasPay) from #{#entityName} t where t.idNumber=:idNumber and t.deleteFlag = :deleteFlag and t.paymentSubject.deleteFlag = :deleteFlag and  t.paymentSubject.publishState = :publishState and t.paymentState <= :paymentState order by t.paymentSubject.publishTime desc")
    Page<StudentPaySubjectDomain> findPersonalCostSubjectListPaymentStateLessEq(Pageable pageable, @Param(value = "idNumber") String idNumber, @Param(value = "publishState") Integer publishState, @Param(value = "paymentState") Integer paymentState, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query ("select  new com.aizhixin.cloud.paycallback.domain.third.StudentPaySubjectDomain(t.id, t.paymentSubject.name, t.paymentSubject.publishTime, t.paymentSubject.paymentType, t.shouldPay, t.hasPay) from #{#entityName} t where t.idNumber=:idNumber and t.deleteFlag = :deleteFlag and t.paymentSubject.deleteFlag = :deleteFlag and  t.paymentSubject.publishState = :publishState and t.paymentState > :paymentState order by t.paymentSubject.publishTime desc")
    Page<StudentPaySubjectDomain> findPersonalCostSubjectListPaymentStateGe(Pageable pageable, @Param(value = "idNumber") String idNumber, @Param(value = "publishState") Integer publishState, @Param(value = "paymentState") Integer paymentState, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select t from #{#entityName} t where t.idNumber=:idNumber and t.deleteFlag = :deleteFlag and t.paymentSubject.deleteFlag = :deleteFlag and  t.id in (:personCostIdSet)")
    List<PersonalCost> findByIdNumberAndPaymentSubject_idInAndDeleteFlag(@Param(value = "idNumber") String idNumber, @Param(value = "personCostIdSet") Set<String> personCostIdSet, @Param(value = "deleteFlag") Integer deleteFlag);

//    @Query("select new com.aizhixin.cloud.paycallback.domain.PaymentOrderItemListDomain(t.id, t.name, t.idNumber, t.professionalName, c.personalState) from #{#entityName} t where t.paymentSubject.id=:paymentSubjectId")
//    List<PaymentOrderItemListDomain> findAllByPaymentSubject_Id(@Param(value = "paymentSubjectId") String paymentSubjectId);
//
//    @Query("select new com.aizhixin.cloud.paycallback.domain.PaymentOrderItemListDomain(t.id, t.name, t.idNumber, t.professionalName, c.personalState) from #{#entityName} t where t.paymentSubject.id=:paymentSubjectId and t.name like :name")
//    List<PaymentOrderItemListDomain> findAllByPaymentSubject_IdAndName(@Param(value = "paymentSubjectId") String paymentSubjectId, @Param(value = "name") String name);

    List<PersonalCost> findByIdNumberAndDeleteFlagOrderByLastModifiedDateDesc(String idNumber, Integer deleteFlag);
}
