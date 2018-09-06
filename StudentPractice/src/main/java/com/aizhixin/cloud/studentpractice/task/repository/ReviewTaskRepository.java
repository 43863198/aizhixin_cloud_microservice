package com.aizhixin.cloud.studentpractice.task.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aizhixin.cloud.studentpractice.task.entity.ReviewTask;




public interface ReviewTaskRepository extends JpaRepository<ReviewTask, String> {
	
	ReviewTask findOneByDeleteFlagAndReviewScoreIsNotNullAndStudentTaskId(int deleteFlag,String stuTaskId);
	
	List<ReviewTask> findAllByDeleteFlagAndStudentTaskIdOrderByCreatedDateDesc(int deleteFlag,String stuTaskId);
	
	Long countByDeleteFlagAndReviewScoreIsNullAndStudentTaskId(int deleteFlag,String stuTaskId);
}
