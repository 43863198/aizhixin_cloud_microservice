package com.aizhixin.cloud.dd.orgStructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.aizhixin.cloud.dd.orgStructure.entity.College;

public interface CollegeRepository extends MongoRepository<College, String>{
	public Page<College> findByOrgId(Pageable page,Long orgId);
	
	public void deleteByOrgId(Long orgId);
	
	public College findByCollegeId(Long collegeId);
}
