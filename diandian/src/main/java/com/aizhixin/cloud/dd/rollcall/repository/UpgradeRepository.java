package com.aizhixin.cloud.dd.rollcall.repository;

import com.aizhixin.cloud.dd.rollcall.entity.Upgrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UpgradeRepository extends
        JpaRepository<Upgrade, Long> {

    @Query("select MAX(buildNumber) from Upgrade")
    Integer findMaxBuildNum();

    @Query("select MAX(buildNumber) from Upgrade up where  up.type= :type and up.role=:role")
    Integer findMaxBuildNumAnd(@Param("type") String type,
                               @Param("role") String role);

    Upgrade findOneByTypeAndRoleAndBuildNumberAndDeleteFlag(String type,
                                                            String role, int buildNumber, Integer deleteFlag);
}
