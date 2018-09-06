package com.aizhixin.cloud.studentpractice.task.repository;




import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aizhixin.cloud.studentpractice.task.entity.PeopleCount;
import com.aizhixin.cloud.studentpractice.task.entity.PeopleCountDetail;




public interface PeopleCountRepository extends JpaRepository<PeopleCount, Long> {
	
	PeopleCount findOneByDeleteFlagAndClassIdAndCollegeIdAndProfessionalId(int deleteFlag,Long classId,Long collegeId,Long professionalId);
	
	@Query("select pc from com.aizhixin.cloud.studentpractice.task.entity.PeopleCount pc where pc.classId in (:classIds) ")
	List<PeopleCount> findAllByClassIds(@Param("classIds") Set<Long> classIds);
}
