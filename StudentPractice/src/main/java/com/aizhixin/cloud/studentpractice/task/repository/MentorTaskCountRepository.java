package com.aizhixin.cloud.studentpractice.task.repository;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aizhixin.cloud.studentpractice.task.entity.File;
import com.aizhixin.cloud.studentpractice.task.entity.MentorTaskCount;
import com.aizhixin.cloud.studentpractice.task.entity.TaskFile;




public interface MentorTaskCountRepository extends JpaRepository<MentorTaskCount, String> {
	
	
	@Query("select m from com.aizhixin.cloud.studentpractice.task.entity.MentorTaskCount m where m.deleteFlag =0 and m.mentorTaskId in (:mentorTaskIds) ")
	List<MentorTaskCount> findAllByMentorTaskIds(@Param("mentorTaskIds") Set<String> mentorTaskIds);
}
