package com.aizhixin.cloud.dd.orgStructure.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.aizhixin.cloud.dd.orgStructure.entity.Classes;

public interface ClassesRepository extends MongoRepository<Classes, String>{

	public Page<Classes> findByProfId(Pageable page,Long profId);
	
	public List<Classes> findByClassesIdIn(List<Long> classesIds);
	
	public void deleteByOrgId(Long orgId);
	
	public Classes findByClassesId(Long classesId);
}
