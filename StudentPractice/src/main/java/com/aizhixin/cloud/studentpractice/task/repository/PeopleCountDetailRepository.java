package com.aizhixin.cloud.studentpractice.task.repository;




import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.studentpractice.task.entity.PeopleCountDetail;




public interface PeopleCountDetailRepository extends JpaRepository<PeopleCountDetail, Long> {
	
	@Query("select pcd from com.aizhixin.cloud.studentpractice.task.entity.PeopleCountDetail pcd where pcd.studentId in (:studentIds) ")
	List<PeopleCountDetail> findAllByStuIds(@Param("studentIds") Set<Long> studentIds);
	
	@Query("select pcd from com.aizhixin.cloud.studentpractice.task.entity.PeopleCountDetail pcd where pcd.studentId in (:studentIds) and pcd.groupId =:groupId ")
	List<PeopleCountDetail> findAllByStuIdsAndGroupId(@Param("studentIds") Set<Long> studentIds,@Param("groupId") Long groupId);
	
	List<PeopleCountDetail> findAllByDeleteFlagAndWhetherPractice(int deleteFlag ,String whetherPractice);
	
	PeopleCountDetail findOneByStudentIdAndGroupId(Long studentId,Long groupId);
	
	List<PeopleCountDetail> findAllByDeleteFlagAndGroupId(int deleteFlag ,Long groupId);
	
	@Query("select pcd from com.aizhixin.cloud.studentpractice.task.entity.PeopleCountDetail pcd where pcd.groupId in (:groupIds) ")
	List<PeopleCountDetail> findAllByGroupIds(@Param("groupIds") Set<Long> groupIds);
	
	@Transactional
	@Modifying(clearAutomatically = true) 
	@Query("update com.aizhixin.cloud.studentpractice.task.entity.PeopleCountDetail mt set mt.groupName=:groupName where mt.groupId =:groupId ")
	public void updateGroupNameByGroupId(@Param("groupName") String groupName,@Param("groupId") Long groupId);
}
