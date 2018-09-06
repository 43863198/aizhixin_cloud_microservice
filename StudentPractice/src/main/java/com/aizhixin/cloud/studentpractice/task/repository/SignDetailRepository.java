package com.aizhixin.cloud.studentpractice.task.repository;



import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aizhixin.cloud.studentpractice.task.entity.SignDetail;




public interface SignDetailRepository extends JpaRepository<SignDetail, Long> {
	
	@Modifying(clearAutomatically = true) 
	@Query("delete from com.aizhixin.cloud.studentpractice.task.entity.SignDetail s where s.groupId in(:groupIds)")
	public void deleteByGroupIds(@Param("groupIds") Set<Long> groupIds);
}
