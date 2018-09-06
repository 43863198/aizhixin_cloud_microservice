package com.aizhixin.cloud.studentpractice.task.repository;


import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aizhixin.cloud.studentpractice.task.entity.File;
import com.aizhixin.cloud.studentpractice.task.entity.MentorTask;
import com.aizhixin.cloud.studentpractice.task.entity.Task;




public interface TaskRepository extends JpaRepository<Task, String> {
	
//	List<Task> findAllByDeleteFlagAndSourceId(int deleteFlag,String sourceId);
//	
//	@Modifying(clearAutomatically = true) 
//	@Query("update com.aizhixin.cloud.studentpractice.task.entity.File f set f.deleteFlag = 1 where f.deleteFlag = 0 and f.sourceId in (:sourceIds) and f.createdBy =:createdBy ")
//	public void logicDeleteBySourceId(@Param("sourceIds") String[] sourceIds,@Param("createdBy") Long createdBy);
//	
//	@Modifying(clearAutomatically = true) 
//	@Query("delete from com.aizhixin.cloud.studentpractice.task.entity.File f where f.sourceId =:sourceId")
//	public void deleteBySourceId(@Param("sourceId") String sourceId);
	
	@Query("select t from com.aizhixin.cloud.studentpractice.task.entity.Task t where deleteFlag = 0 and t.id in (:ids) ")
	List<Task> findAllByIds(@Param("ids") ArrayList<String> ids);
	
	
	@Query("select t from com.aizhixin.cloud.studentpractice.task.entity.Task t where deleteFlag = 0 and t.weekTaskId in (:ids) order by t.createdDate asc ")
	List<Task> findAllByWeekTaskIds(@Param("ids") ArrayList<String> weekTaskIds);
}
