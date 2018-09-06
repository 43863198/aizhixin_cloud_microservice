package com.aizhixin.cloud.orgmanager.company.core;

import lombok.Getter;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-09-11
 */
public enum UserRoleEnum {
    ROLE_ORG_ADMIN("超级管理员"),
    ROLE_ORG_MANAGER("校级超级管理员"),
    ROLE_COLLEGE_ADMIN("院级超级管理员"),
    ROLE_ORG_EDUCATIONALMANAGER("校级教务管理"),
    ROLE_ORG_DATAVIEW("校级数据查看"),
    ROLE_COLLEG_EDUCATIONALMANAGER("院级教务管理"),
    ROLE_COLLEG_DATAVIEW("院级数据查看"),
    ROLE_FINANCE_ADMIN("财务管理员"),
    ROLE_DORM_ADMIN("宿舍管理员"),
    ROLE_ENROL_ADMIN("招生管理员");

    private String value ;

    private UserRoleEnum(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
