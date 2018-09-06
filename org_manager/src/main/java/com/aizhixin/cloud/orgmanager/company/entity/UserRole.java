package com.aizhixin.cloud.orgmanager.company.entity;

import com.aizhixin.cloud.orgmanager.common.entity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

/**
 * 用户角色
 * Created by zhen.pan on 2017/4/17.
 */
@Entity(name = "T_USER_ROLE")
@ToString
public class UserRole extends AbstractEntity {
    /**
     * 用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    @Getter  @Setter private User user;
    /**
     * 角色
     */
    @Column(name = "ROLE_NAME")
    @Getter @Setter private String roleName;
    /**
     * 角色组
     */
    @Column(name = "ROLE_GROUP")
    @Getter @Setter private String roleGroup;
}
