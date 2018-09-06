package com.aizhixin.cloud.dd.homepage.repository;

import com.aizhixin.cloud.dd.homepage.entity.HomePage;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


public interface HomePageRepository extends JpaRepository<HomePage, Long>, JpaSpecificationExecutor<HomePage> {

    List<HomePage> findAllByRoleAndVersionAndType(String role, String version, String type, Sort sort);

    List<HomePage> findAllByRoleAndTypeAndVersionAndDeleteFlag(String role, String type, String version, Integer deleteFlag, Sort sort);

    List<HomePage> findAllByRoleAndTypeAndVersionAndDeleteFlagAndOrgs(String role, String type, String version, Integer deleteFlag,  String orgs, Sort sort);

    List<HomePage> findAllByRoleAndTypeAndVersionAndOnOffAndDeleteFlag(String role, String type, String version, String onOff, Integer deleteFlag, Sort sort);

}