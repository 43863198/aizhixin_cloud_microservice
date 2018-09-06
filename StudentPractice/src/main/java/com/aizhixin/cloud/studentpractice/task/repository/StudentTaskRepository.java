package com.aizhixin.cloud.studentpractice.task.repository;


import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.studentpractice.task.entity.StudentTask;




public interface StudentTaskRepository extends JpaRepository<StudentTask, String> {
	
	Long countByDeleteFlagAndReviewScoreNotNullAndMentorTaskId(int deleteFlag,String mentorTaskId);
	
	Long countByDeleteFlagAndMentorTaskId(int deleteFlag,String mentorTaskId);
	
	StudentTask findOneByDeleteFlagAndId(int deleteFlag,String id);
	
	Page<StudentTask> findAllByDeleteFlagAndMentorTaskId(Pageable page,int deleteFlag,String mentorTaskId);
	
	List<StudentTask> findAllByDeleteFlagAndMentorTaskId(int deleteFlag,String mentorTaskId);
	
	List<StudentTask> findAllByMentorTaskId(String mentorTaskId);
	
	StudentTask findOneByDeleteFlagAndStudentIdAndMentorTaskId(int deleteFlag,Long studentId,String mentorTaskId);
	
	@Modifying(clearAutomatically = true) 
	@Query("update com.aizhixin.cloud.studentpractice.task.entity.StudentTask st set st.deleteFlag = 1 where st.deleteFlag = 0 and st.mentorTaskId in (:mentorTaskIds) and st.createdBy =:createdBy")
	public void logicDeleteByMentorTaskId(@Param("mentorTaskIds") String[] mentorTaskIds,@Param("createdBy") Long createdBy);
	
	@Query(value="SELECT * FROM `SP_STUDENT_TASK` st WHERE st.delete_Flag=:deleteFlag and MENTOR_ID is not null group by st.mentor_id",nativeQuery=true)
	List<StudentTask> findDistinctMentorIdByDeleteFlag(@Param("deleteFlag")int deleteFlag);
	
	@Transactional
	@Modifying(clearAutomatically = true) 
	@Query("update com.aizhixin.cloud.studentpractice.task.entity.StudentTask st set st.jobNum=:jobNum,st.classId=:classId,st.professionalId=:professionalId,collegeId=:collegeId,st.enterpriseName=:enterpriseName,st.orgId=:orgId where st.deleteFlag = 0 and st.studentId=:studentId")
	public void synStuInfor(@Param("jobNum") String jobNum,@Param("classId") Long classId,@Param("professionalId") Long professionalId,@Param("collegeId") Long collegeId,@Param("enterpriseName") String enterpriseName,@Param("orgId") Long orgId,@Param("studentId") Long studentId);
	
	@Modifying(clearAutomatically = true) 
	@Query("update com.aizhixin.cloud.studentpractice.task.entity.StudentTask st set st.groupId = :groupId where st.mentorTaskId = :mentorTaskId")
	public void initGroupIdByMentorTaskId(@Param("groupId") Long groupId,@Param("mentorTaskId") String mentorTaskId);
	
	@Transactional
	@Modifying(clearAutomatically = true) 
	@Query("update com.aizhixin.cloud.studentpractice.task.entity.StudentTask mt set mt.groupName=:groupName where mt.groupId =:groupId ")
	public void updateGroupNameByGroupId(@Param("groupName") String groupName,@Param("groupId") Long groupId);
}
