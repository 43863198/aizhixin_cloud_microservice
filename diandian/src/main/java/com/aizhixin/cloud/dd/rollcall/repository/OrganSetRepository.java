package com.aizhixin.cloud.dd.rollcall.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aizhixin.cloud.dd.rollcall.entity.OrganSet;

public interface OrganSetRepository extends JpaRepository <OrganSet, Long> {
    OrganSet findByOrganId(Long organID);

    void deleteByOrganId(Long organId);

}
