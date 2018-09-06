package com.aizhixin.cloud.studentpractice.task.repository;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aizhixin.cloud.studentpractice.task.entity.File;
import com.aizhixin.cloud.studentpractice.task.entity.TaskFile;




public interface FileRepository extends JpaRepository<File, String> {
	
	List<File> findAllByDeleteFlagAndSourceId(int deleteFlag,String sourceId);
	
	@Modifying(clearAutomatically = true) 
	@Query("update com.aizhixin.cloud.studentpractice.task.entity.File f set f.deleteFlag = 1 where f.deleteFlag = 0 and f.sourceId in (:sourceIds) and f.createdBy =:createdBy ")
	public void logicDeleteBySourceId(@Param("sourceIds") String[] sourceIds,@Param("createdBy") Long createdBy);
	
	@Modifying(clearAutomatically = true) 
	@Query("delete from com.aizhixin.cloud.studentpractice.task.entity.File f where f.sourceId =:sourceId")
	public void deleteBySourceId(@Param("sourceId") String sourceId);
	
	
	@Query("select f from com.aizhixin.cloud.studentpractice.task.entity.File f where f.deleteFlag =0 and f.sourceId in (:sourceIds) ")
	List<File> findAllBySourceIds(@Param("sourceIds") Set<String> sourceIds);
}
