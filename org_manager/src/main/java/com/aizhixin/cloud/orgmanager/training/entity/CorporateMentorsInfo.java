package com.aizhixin.cloud.orgmanager.training.entity;


import com.aizhixin.cloud.orgmanager.common.entity.AbstractEntity;
import lombok.*;

import javax.persistence.*;

/**
 * 企业导师信息表
 *
 * @author wu
 */
@Entity
@Table(name = "T_CORPORATE_MENTORS_INFO")
@ToString
public class CorporateMentorsInfo extends AbstractEntity {
    /**
     * 账号id
     */
    @Column(name = "ACCOUNT_ID")
    @Getter
    @Setter
    private Long accountId;
    /**
     * 登录账号
     */
    @Column(name = "LOGIN_NAME")
    @Getter
    @Setter
    private String loginName;
    /**
     * 公司地址
     */
    @Column(name = "COMPANY_ADDRESS")
    @Getter
    @Setter
    private String companyAddress;
    /**
     * 工号
     */
    @Column(name = "JOB_NUMBER")
    @Getter
    @Setter
    private String jobNumber;
    /**
     * 姓名
     */
    @JoinColumn(name = "NAME")
    @Getter
    @Setter
    private String name;
    /**
     * 学校id
     */
    @Column(name = "ORG_ID")
    @Getter
    @Setter
    private Long orgId;
    /**
     * 企业id
     */
    @Column(name = "ENTERPRISE_ID")
    @Getter
    @Setter
    private Long enterpriseId;
    /**
     * 企业名称
     */
    @Column(name = "ENTERPRISE_NAME")
    @Getter
    @Setter
    private String enterpriseName;
    /**
     * 部门
     */
    @Column(name = "DEPARTMENT")
    @Getter
    @Setter
    private String department;

    /**
     * 职务
     */
    @Column(name = "POSITION")
    @Getter
    @Setter
    private String position;

    /**
     * 邮箱
     */
    @Column(name = "MAILBOX")
    @Getter
    @Setter
    private String mailbox;
    /**
     * 手机号
     */
    @Column(name = "PHONE")
    @Getter
    @Setter
    private  String phone;
    /**
     * 省
     */
    @Column(name = "PROVINCE")
    @Getter
    @Setter
    private  String province;
    /**
     * 市
     */
    @Column(name = "CITY")
    @Getter
    @Setter
    private  String city;

    public CorporateMentorsInfo(){}

    public CorporateMentorsInfo(Long id, Long accountId, String loginName, String jobNumber, String name, String enterpriseName, String department,
                                   String position, String mailbox, String phone, String province, String city){
        this.id = id;
        this.accountId = accountId;
        this.loginName = loginName;
        this.jobNumber = jobNumber;
        this.name = name;
        this.enterpriseName = enterpriseName;
        this.department = department;
        this.position = position;
        this.mailbox = mailbox;
        this.phone = phone;
        this.province = province;
        this.city = city;
    }
}
