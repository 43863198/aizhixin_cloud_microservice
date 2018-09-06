package com.aizhixin.cloud.studentpractice.score.repository;


import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.studentpractice.score.entity.Score;




public interface ScoreRepository extends JpaRepository<Score, String> {
	
//	@Query("select tf from com.aizhixin.cloud.studentpractice.task.entity.TaskFile tf where tf.sourceId in (:sourceIds) ")
//	List<TaskFile> findAllBySourceIds(@Param("sourceIds") ArrayList<String> sourceIds);
	
	@Transactional
	void deleteByGroupId(Long groupId);
	
	@Transactional
	@Modifying(clearAutomatically = true) 
	@Query("update com.aizhixin.cloud.studentpractice.score.entity.Score mt set mt.groupName=:groupName where mt.groupId =:groupId ")
	public void updateGroupNameByGroupId(@Param("groupName") String groupName,@Param("groupId") Long groupId);
}
