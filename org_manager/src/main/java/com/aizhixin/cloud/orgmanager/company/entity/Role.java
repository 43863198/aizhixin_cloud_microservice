package com.aizhixin.cloud.orgmanager.company.entity;

import com.aizhixin.cloud.orgmanager.common.entity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2018-03-30
 */
@Entity(name = "T_ROLE")
@ToString
public class Role extends AbstractEntity {
    /*
	 * 角色名称
	 */
    @NotNull
    @Column(name = "ROLE_NAME")
    @Getter
    @Setter
    private String roleName;
    /*
	 * 角色组
	 */
    @NotNull
    @Column(name = "ROLE_GROUP")
    @Getter
    @Setter
    private String roleGroup;

    /*
	 * 角色描述
	 */
    @Column(name = "ROLE_DESCRIBE")
    @Getter
    @Setter
    private String roleDescribe;


}
