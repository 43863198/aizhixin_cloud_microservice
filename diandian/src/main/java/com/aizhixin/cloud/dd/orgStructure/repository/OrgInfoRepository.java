package com.aizhixin.cloud.dd.orgStructure.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.aizhixin.cloud.dd.orgStructure.entity.OrgInfo;

public interface OrgInfoRepository extends MongoRepository<OrgInfo, String>{
	public void deleteByOrgId(Long orgId);
	public OrgInfo findByOrgId(Long orgId);
}
