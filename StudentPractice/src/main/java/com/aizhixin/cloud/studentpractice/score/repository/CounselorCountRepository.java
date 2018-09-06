package com.aizhixin.cloud.studentpractice.score.repository;



import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.studentpractice.score.entity.CounselorCount;




public interface CounselorCountRepository extends JpaRepository<CounselorCount, Long> {
	
	CounselorCount findOneByGroupIdAndCounselorId(Long groupId,Long counselorId);
	
	@Transactional
	@Modifying(clearAutomatically = true) 
	@Query("update com.aizhixin.cloud.studentpractice.score.entity.CounselorCount mt set mt.groupName=:groupName where mt.groupId =:groupId ")
	public void updateGroupNameByGroupId(@Param("groupName") String groupName,@Param("groupId") Long groupId);
	
}
