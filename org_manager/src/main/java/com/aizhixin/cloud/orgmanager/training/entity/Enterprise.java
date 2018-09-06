package com.aizhixin.cloud.orgmanager.training.entity;

import com.aizhixin.cloud.orgmanager.common.entity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2018-03-15
 */
@Entity
@Table(name = "T_ENTERPRISE")
@ToString
public class Enterprise extends AbstractEntity {
    /**
     * 企业名称
     */
    @Column(name = "NAME")
    @Getter
    @Setter
    private String name;
    /**
     * 所在省份
     */
    @Column(name = "PROVINCE")
    @Getter
    @Setter
    private String province;
    /**
     * 所在市
     */
    @Column(name = "CITY")
    @Getter
    @Setter
    private String city;
    /**
     * 所在县
     */
    @Column(name = "COUNTY")
    @Getter
    @Setter
    private String county;
    /**
     * 地址
     */
    @Column(name = "ADDRESS")
    @Getter
    @Setter
    private String address;
    /**
     * 电话
     */
    @Column(name = "TELEPHONE")
    @Getter
    @Setter
    private String telephone;
    /**
     * 邮箱
     */
    @Column(name = "MAILBOX")
    @Getter
    @Setter
    private String mailbox;

    /**
     * 学校id
     */
    @Column(name = "ORG_ID")
    @Getter
    @Setter
    private Long orgId;

}
