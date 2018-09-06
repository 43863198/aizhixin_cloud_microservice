package com.aizhixin.cloud.orgmanager.training.repository;

import com.aizhixin.cloud.orgmanager.training.entity.CorporateMentorsInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Created by jianwei.wu
 * @E-mail wujianwei@aizhixin.com
 */
public interface MentorsTrainingRepository extends JpaRepository<CorporateMentorsInfo, Long>,
        JpaSpecificationExecutor<CorporateMentorsInfo> {


        //返回最大的id
        @Query("select id from #{#entityName} order by id desc")
        List<Long> findIds();

        //根据name查询所有企业导师信息
        @Query("select new com.aizhixin.cloud.orgmanager.training.entity.CorporateMentorsInfo(c.id, c.accountId, c.loginName, c.jobNumber, c.name, c.enterpriseName, c.department, c.position, c.mailbox, c.phone, c.province, c.city) from #{#entityName} c where c.deleteFlag = :deleteFlag and c.orgId = :orgId and c.name like %:name% order by c.lastModifiedDate desc")
        Page<CorporateMentorsInfo> findCorporateMentorsInfoByPageName(Pageable pageable, @Param(value = "name") String name, @Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag);
        //查询所有企业导师信息
        @Query("select new com.aizhixin.cloud.orgmanager.training.entity.CorporateMentorsInfo(c.id, c.accountId, c.loginName, c.jobNumber, c.name, c.enterpriseName, c.department, c.position, c.mailbox, c.phone, c.province, c.city) from #{#entityName} c where c.deleteFlag = :deleteFlag and c.orgId = :orgId order by c.lastModifiedDate desc")
        Page<CorporateMentorsInfo> findCorporateMentorsInfoByPage(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag);


        //根据账号id(accountId)查询企业导师信息
        List<CorporateMentorsInfo> findByAccountIdAndDeleteFlag(Long accountId,Integer deleteFlag);

        //根据手机号码查询企业导师信息
        List<CorporateMentorsInfo> findByPhoneAndDeleteFlag(String phone,Integer deleteFlag);

        //根据手机号码查询企业导师信息
        @Query("select new com.aizhixin.cloud.orgmanager.training.entity.CorporateMentorsInfo(c.id, c.accountId, c.loginName, c.jobNumber, c.name, c.enterpriseName, c.department, c.position, c.mailbox, c.phone, c.province, c.city) from #{#entityName} c where c.deleteFlag = :deleteFlag and c.phone in :phoneList order by c.lastModifiedDate desc")
        List<CorporateMentorsInfo> findByPhoneListAndDeleteFlag(@Param(value = "phoneList")List<String> phoneList,@Param(value = "deleteFlag") Integer deleteFlag);

        //根据邮箱查询企业导师信息
        List<CorporateMentorsInfo> findByMailboxAndDeleteFlag(String mailbox,Integer deleteFlag);

        //根据手机号码查询企业导师信息
        @Query("select new com.aizhixin.cloud.orgmanager.training.entity.CorporateMentorsInfo(c.id, c.accountId, c.loginName, c.jobNumber, c.name, c.enterpriseName, c.department, c.position, c.mailbox, c.phone, c.province, c.city) from #{#entityName} c where c.deleteFlag = :deleteFlag and c.mailbox in :mailboxList order by c.lastModifiedDate desc")
        List<CorporateMentorsInfo> findByMailboxListAndDeleteFlag(@Param(value = "mailboxList")List<String> mailboxList,@Param(value = "deleteFlag") Integer deleteFlag);

        Long countByOrgId(Long orgId);

}
