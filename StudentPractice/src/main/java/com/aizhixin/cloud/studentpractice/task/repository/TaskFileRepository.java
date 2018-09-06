package com.aizhixin.cloud.studentpractice.task.repository;


import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aizhixin.cloud.studentpractice.task.entity.MentorTask;
import com.aizhixin.cloud.studentpractice.task.entity.TaskFile;




public interface TaskFileRepository extends JpaRepository<TaskFile, String> {
	
	@Query("select tf from com.aizhixin.cloud.studentpractice.task.entity.TaskFile tf where tf.sourceId in (:sourceIds) ")
	List<TaskFile> findAllBySourceIds(@Param("sourceIds") ArrayList<String> sourceIds);
}
