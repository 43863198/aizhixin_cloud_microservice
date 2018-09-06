package com.aizhixin.cloud.orgmanager.training.repository;

import com.aizhixin.cloud.orgmanager.training.entity.Enterprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2018-03-15
 */
public interface EnterpriseRepository extends JpaRepository<Enterprise, Long>,
        JpaSpecificationExecutor<Enterprise> {
}
