package com.aizhixin.cloud.studentpractice.task.repository;



import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aizhixin.cloud.studentpractice.task.entity.Task;
import com.aizhixin.cloud.studentpractice.task.entity.WeekTask;




public interface WeekTaskRepository extends JpaRepository<WeekTask, String> {
	
	@Query("select t from com.aizhixin.cloud.studentpractice.task.entity.WeekTask t where t.id in (:ids) ")
	List<WeekTask> findAllByIds(@Param("ids") ArrayList<String> ids);
}
