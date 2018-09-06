package com.aizhixin.cloud.studentpractice.task.repository;



import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.studentpractice.task.entity.MentorTask;




public interface MentorTaskRepository extends JpaRepository<MentorTask, String> {
	
	MentorTask findOneByDeleteFlagAndId(int deleteFlag,String id);
	
	@Modifying(clearAutomatically = true) 
	@Query("update com.aizhixin.cloud.studentpractice.task.entity.MentorTask mt set mt.deleteFlag = 1 where mt.deleteFlag = 0 and mt.id in (:ids) and mt.createdBy =:createdBy")
	public void logicDeleteByIds(@Param("ids") String[] ids,@Param("createdBy") Long createdBy);
	
	@Query("select mt from com.aizhixin.cloud.studentpractice.task.entity.MentorTask mt where mt.id in (:ids) ")
	List<MentorTask> findAllByIds(@Param("ids") String[] ids);
	
	Long countByDeleteFlagAndWeekTaskId(int deleteFlag,String weekTaskId);
	
	List<MentorTask> findAllByDeleteFlagAndGroupIdIsNotNull(int deleteFlag);
	
	@Modifying(clearAutomatically = true) 
	@Query("update com.aizhixin.cloud.studentpractice.task.entity.MentorTask mt set mt.beginDate =:beginDate, mt.deadLine =:deadLine where mt.deleteFlag = 0 and mt.id =:id ")
	public void updateTimeById(@Param("beginDate") Date beginDate,@Param("deadLine") Date deadLine,@Param("id") String id);
	
	@Transactional
	@Modifying(clearAutomatically = true) 
	@Query("update com.aizhixin.cloud.studentpractice.task.entity.MentorTask mt set mt.groupName=:groupName where mt.groupId =:groupId ")
	public void updateGroupNameByGroupId(@Param("groupName") String groupName,@Param("groupId") Long groupId);
	
}
