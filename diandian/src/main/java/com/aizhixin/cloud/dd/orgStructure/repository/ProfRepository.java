package com.aizhixin.cloud.dd.orgStructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.aizhixin.cloud.dd.orgStructure.entity.Prof;

public interface ProfRepository extends MongoRepository<Prof, String>{
	public Page<Prof> findByCollegeId(Pageable page,Long collegeId);
	
	public void deleteByOrgId(Long orgId);
	
	public Prof findByProfId(Long profId);
}
