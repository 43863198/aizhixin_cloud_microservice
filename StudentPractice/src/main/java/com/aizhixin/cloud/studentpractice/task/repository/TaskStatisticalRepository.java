package com.aizhixin.cloud.studentpractice.task.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aizhixin.cloud.studentpractice.task.entity.TaskStatistical;




public interface TaskStatisticalRepository extends JpaRepository<TaskStatistical, Long> {
	
	TaskStatistical findOneByDeleteFlagAndStudentIdAndGroupId(int deleteFlag,Long stuId,Long groupId);
	
	List<TaskStatistical> findAllByDeleteFlagAndStudentId(int deleteFlag,Long stuId);
}
