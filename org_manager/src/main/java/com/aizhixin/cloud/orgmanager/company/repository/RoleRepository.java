package com.aizhixin.cloud.orgmanager.company.repository;

import com.aizhixin.cloud.orgmanager.company.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2018-03-30
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByRoleGroupAndDeleteFlag(String roleGroup,Integer deleteFlag);
}
