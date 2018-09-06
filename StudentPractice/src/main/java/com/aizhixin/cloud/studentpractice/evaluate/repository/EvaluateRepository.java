package com.aizhixin.cloud.studentpractice.evaluate.repository;



import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.studentpractice.evaluate.entity.Evaluate;




public interface EvaluateRepository extends JpaRepository<Evaluate, String> {
	
	List<Evaluate> findAllByDeleteFlagAndGroupId(int deleteFlag,Long groupId);
	
	Evaluate findOneByDeleteFlagAndEvaluateTypeAndGroupIdAndStudentId(int deleteFlag,String evaluateType,Long groupId,Long studentId);
	
	List<Evaluate> findAllByDeleteFlagAndGroupIdAndEvaluateTypeInAndStudentIdIn(int deleteFlag,Long groupId,Set<String> evaluateType,Set<Long> studentIds);
	
	@Transactional
	@Modifying(clearAutomatically = true) 
	@Query("update com.aizhixin.cloud.studentpractice.evaluate.entity.Evaluate mt set mt.groupName=:groupName where mt.groupId =:groupId ")
	public void updateGroupNameByGroupId(@Param("groupName") String groupName,@Param("groupId") Long groupId);
}
