package com.aizhixin.cloud.orgmanager.common.util;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by zhen.pan on 2017/4/17.
 */
@Component
@Data
public class RoleConfig {
    /**
     * 班主任
     */
    public final static String CLASSES_MASTER = "ROLE_CLASSROOMTEACHE";

    /**
     * 角色组2B(学校)
     */
    @Value("${sys.role.group2b}")
    private String roleGroup2B;
    /**
     * 角色组2C（普通自己注册的散户）
     */
    @Value("${sys.role.group2c}")
    private String roleGroup2C;
    /**
     *  角色组2M（知新树管理人员）
     */
    @Value("${sys.role.group2m}")
    private String roleGroup2M;
    /**
     *  角色组2COM（企业端）
     */
    @Value("${sys.role.group2com}")
    private String roleGroup2Com;
    /**
     * 角色组2M的超级管理员角色
     */
    @Value("${sys.role.groupm.admin}")
    private String roleSupperAdmin2M;
    /**
     * 角色组2B的老师角色
     */
    @Value("${sys.role.groupb.teacher}")
    private String roleTeacher2B;
    /**
     * 角色组2B的学生角色
     */
    @Value("${sys.role.groupb.student}")
    private String roleStudent2B;
    /**
     * 角色组2B的学校管理员角色
     */
    @Value("${sys.role.groupb.orgadmin}")
    private String roleSchoolAdmin2B;
    /**
     * 角色组2B的学院管理员角色
     */
    @Value("${sys.role.groupb.collegeadmin}")
    private String roleCollegeAdmin2B;
    /**
     * 角色组2B的班级管理员角色
     */
    @Value("${sys.role.groupb.classesadmin}")
    private String roleClassesAdmin2B;
    /**
     * 角色组2COM的老师角色(企业导师)
     */
    @Value("${sys.role.groupcom.teacher}")
    private String roleTeacher2Com;
}