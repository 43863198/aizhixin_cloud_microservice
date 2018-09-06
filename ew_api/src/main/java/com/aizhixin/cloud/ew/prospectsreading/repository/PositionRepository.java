package com.aizhixin.cloud.ew.prospectsreading.repository;

import com.aizhixin.cloud.ew.prospectsreading.domain.PositionQueryListDomain;
import com.aizhixin.cloud.ew.prospectsreading.entity.Position;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Long> {

    long countByNameAndDeleteFlag(String name, Integer deleteFlag);

    long countByNameAndIdNotAndDeleteFlag(String name, Long id, Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.ew.prospectsreading.domain.PositionQueryListDomain(p.id, p.name, p.type, p.publishStatus) from #{#entityName} p where p.deleteFlag = :deleteFlag")
    Page<PositionQueryListDomain> findAll(Pageable page, @Param(value = "deleteFlag") Integer deleteFlag);


    @Query("select new com.aizhixin.cloud.ew.prospectsreading.domain.PositionQueryListDomain(p.id, p.name, p.type, p.publishStatus) from #{#entityName} p where p.deleteFlag = :deleteFlag  and name like :name")
    Page<PositionQueryListDomain> findByName(Pageable page, @Param(value = "name") String name, @Param(value = "deleteFlag") Integer deleteFlag);

    List<Position> findByName(String name);

    @Query("select new com.aizhixin.cloud.ew.prospectsreading.domain.PositionQueryListDomain(p.id, p.name, p.type, p.publishStatus) from #{#entityName} p where p.publishStatus = :publishStatus and p.deleteFlag = :deleteFlag")
    Page<PositionQueryListDomain> findByPublishStatus(Pageable page, @Param(value = "publishStatus") Integer publishStatus, @Param(value = "deleteFlag") Integer deleteFlag);

	Position findByIdAndDeleteFlag(Long id, Integer deleteFlag);
}
