package com.aizhixin.cloud.orgmanager.company.repository;


import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.company.entity.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;


public interface OrganizationRepository extends JpaRepository<Organization, Long>, JpaSpecificationExecutor<Organization> {
	Long countByCodeAndDeleteFlag(String code, Integer deleteFlag);

	Long countByCodeAndIdNotAndDeleteFlag(String code, Long id, Integer deleteFlag);
	
	Long countByNameAndDeleteFlag(String name, Integer deleteFlag);
	
	Long countByNameAndIdNotAndDeleteFlag(String name, Long id, Integer deleteFlag);
	
	Long countByDomainNameAndDeleteFlag(String domainName, Integer deleteFlag);

	Long countByDomainNameAndIdNotAndDeleteFlag(String domainName, Long id, Integer deleteFlag);
	
	List<Organization> findByCodeAndDeleteFlag(String code, Integer deleteFlag);
	
	List<Organization> findByNameAndDeleteFlag(String name, Integer deleteFlag);
	
	List<Organization> findByDomainNameAndDeleteFlag(String domainName, Integer deleteFlag);
	@Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(o.id, o.name) from #{#entityName} o where o.deleteFlag = :deleteFlag")
	Page<IdNameDomain> findIdName(Pageable pageable, @Param(value = "deleteFlag") Integer deleteFlag);

	@Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(o.id, o.name) from #{#entityName} o where o.id in (:ids)")
	List<IdNameDomain> findIdNameByIdIn(@Param(value = "ids") Set<Long> ids);

	List<Organization> findByIdIn(Set<Long> ids);

	List<Organization> findByDeleteFlag(Integer deleteFlag);
}
