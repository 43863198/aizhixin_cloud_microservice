package com.aizhixin.cloud.studentpractice.summary.repository;



import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.studentpractice.summary.entity.Summary;




public interface SummaryRepository extends JpaRepository<Summary, String> {
	
	
	@Transactional
	@Modifying(clearAutomatically = true) 
	@Query("update com.aizhixin.cloud.studentpractice.summary.entity.Summary mt set mt.groupName=:groupName where mt.groupId =:groupId ")
	public void updateGroupNameByGroupId(@Param("groupName") String groupName,@Param("groupId") Long groupId);
	
	List<Summary> findAllByGroupIdIsNotNullAndJobNumIsNullAndDeleteFlag(int deleteFlag);
}
