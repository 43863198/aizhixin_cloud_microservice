package com.aizhixin.cloud.studentpractice.task.repository;



import java.util.List;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.studentpractice.task.entity.WeekTaskTeam;




public interface WeekTaskTeamRepository extends JpaRepository<WeekTaskTeam, String> {
	
	@Transactional
	@Modifying(clearAutomatically = true) 
	@Query("update com.aizhixin.cloud.studentpractice.task.entity.WeekTaskTeam wtt set wtt.deleteFlag = 1 where wtt.deleteFlag = 0 and wtt.weekTaskId = :weekTaskId")
	public void logicDeleteByWeekTaskId(@Param("weekTaskId") String weekTaskId);
	
	List<WeekTaskTeam> findAllByDeleteFlagAndWeekTaskId(int deleteFlag,String weekTaskId);
	
	Long countByDeleteFlagAndWeekTaskIdAndGroupId(int deleteFlag,String weekTaskId,Long groupId);
}
