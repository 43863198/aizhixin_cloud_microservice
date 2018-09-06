package com.aizhixin.cloud.orgmanager.training.repository;

import java.util.List;
import java.util.Set;

import com.aizhixin.cloud.orgmanager.training.entity.TrainingGroupSet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TrainingGroupSetRepository extends JpaRepository<TrainingGroupSet, Long>,
        JpaSpecificationExecutor<TrainingGroupSet> {
	
	TrainingGroupSet findOneByGroupId(Long groupId);
	
	@Query("select tgs from com.aizhixin.cloud.orgmanager.training.entity.TrainingGroupSet tgs where tgs.groupId in (:groupIds) ")
	List<TrainingGroupSet> findAllByGroupIds(@Param("groupIds") Set<Long> groupIds);
	
}
