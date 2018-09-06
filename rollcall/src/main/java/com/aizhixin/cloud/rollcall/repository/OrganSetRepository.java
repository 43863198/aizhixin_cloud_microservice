package com.aizhixin.cloud.rollcall.repository;


import com.aizhixin.cloud.rollcall.entity.OrganSet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganSetRepository extends JpaRepository <OrganSet, Long> {
    OrganSet findByOrganId(Long organID);

    void deleteByOrganId(Long organId);

}
