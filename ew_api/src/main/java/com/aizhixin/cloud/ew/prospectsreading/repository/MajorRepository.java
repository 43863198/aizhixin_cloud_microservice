package com.aizhixin.cloud.ew.prospectsreading.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aizhixin.cloud.ew.prospectsreading.domain.MajorQueryListDomain;
import com.aizhixin.cloud.ew.prospectsreading.entity.Major;

public interface MajorRepository extends JpaRepository<Major, Long> {
	
	long countByNameAndDeleteFlag(String name, Integer deleteFlag);
	// 查询不包含目前填写的name 的总数
    long countByNameAndIdNotAndDeleteFlag(String name, Long id, Integer deleteFlag);
    
    @Query("select new com.aizhixin.cloud.ew.prospectsreading.domain.MajorQueryListDomain(m.id, m.name, m.type, m.publishStatus) from #{#entityName} m where m.deleteFlag = :deleteFlag")
    Page<MajorQueryListDomain> findAll(Pageable page, @Param(value = "deleteFlag") Integer deleteFlag);
    
    @Query("select new com.aizhixin.cloud.ew.prospectsreading.domain.MajorQueryListDomain(m.id, m.name, m.type, m.publishStatus) from #{#entityName} m where m.deleteFlag = :deleteFlag  and m.name like :name")
    Page<MajorQueryListDomain> findByName(Pageable page, @Param(value = "name") String name, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.ew.prospectsreading.domain.MajorQueryListDomain(m.id, m.name, m.type, m.publishStatus) from #{#entityName} m where m.publishStatus = :publishStatus and m.deleteFlag = :deleteFlag")
    Page<MajorQueryListDomain> findByPublishStatus(Pageable page, @Param(value = "publishStatus") Integer publishStatus, @Param(value = "deleteFlag") Integer deleteFlag);
	Major findByIdAndDeleteFlag(Long id, Integer deleteFlag);
}
