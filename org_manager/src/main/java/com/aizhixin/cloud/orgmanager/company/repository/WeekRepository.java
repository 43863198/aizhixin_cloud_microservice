package com.aizhixin.cloud.orgmanager.company.repository;


import com.aizhixin.cloud.orgmanager.company.domain.WeekDomain;
import com.aizhixin.cloud.orgmanager.company.entity.Semester;
import com.aizhixin.cloud.orgmanager.company.entity.Week;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;



public interface WeekRepository extends JpaRepository<Week, Long> {
	
	Long countBySemesterAndDeleteFlag(Semester semester, Integer deleteFlag);

	Long countBySemesterAndNameAndDeleteFlag(Semester semester, String name, Integer deleteFlag);

	Long countBySemesterAndNameAndIdNotAndDeleteFlag(Semester semester, String name, Long id, Integer deleteFlag);

//	@Query("select new com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain(w.id, w.name) from com.aizhixin.cloud.orgmanager.company.entity.Week w where w.semester = :semester and w.deleteFlag = :deleteFlag")
//	Page<IdNameDomain> findIdName(Pageable pageable, @Param(value = "semester") Semester semester, @Param(value = "deleteFlag") Integer deleteFlag);
	
	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.WeekDomain(w.id, w.name, w.startDate, w.endDate, w.no, w.createdDate) from #{#entityName} w where w.semester = :semester and w.deleteFlag = :deleteFlag")
	Page<WeekDomain> findBySemester(Pageable pageable, @Param(value = "semester") Semester semester, @Param(value = "deleteFlag") Integer deleteFlag);
	
//	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.WeekDomain(w.id, w.name, w.startDate, w.endDate, w.no, w.createdDate) from com.aizhixin.cloud.orgmanager.company.entity.Week w where w.semester = :semester and w.deleteFlag = :deleteFlag and w.name like %:name%")
//	Page<WeekDomain> findBySemesterAndName(Pageable pageable, @Param(value = "semester") Semester semester, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "name") String name);

	Long countBySemesterAndNoAndDeleteFlag(Semester semester, Integer no,Integer state);

	Long countBySemesterAndNoAndIdNotAndDeleteFlag(Semester semester,Integer no, Long id, Integer state);

	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.WeekDomain(w.id,w.no) from #{#entityName} w where w.semester = :semester and w.deleteFlag = :deleteFlag")
	Page<WeekDomain> findIdNo(Pageable pageable, @Param(value = "semester") Semester semester, @Param(value = "deleteFlag") Integer deleteFlag);

	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.WeekDomain(w.id, w.name, w.startDate, w.endDate, w.no, w.createdDate) from #{#entityName} w where w.semester = :semester and w.deleteFlag = :deleteFlag and w.no = :no")
	Page<WeekDomain> findBySemesterAndNo(Pageable pageable, @Param(value = "semester") Semester semester, @Param(value = "deleteFlag") Integer deleteFlag,@Param(value = "no") Integer no);

	@Modifying(clearAutomatically = true) 
	@Query("update #{#entityName} set deleteFlag =:newDeleteFlag where deleteFlag =:deleteFlag and semester = :semester")
	void deleteAllSemesterWeek(@Param(value = "newDeleteFlag") Integer newDeleteFlag,@Param(value = "deleteFlag") Integer deleteFlag,@Param("semester") Semester semester);
	
	@Query("select new com.aizhixin.cloud.orgmanager.company.domain.WeekDomain(w.id, w.name, w.startDate, w.endDate, w.no, w.createdDate,w.semester.id) from #{#entityName} w where w.orgId = :orgId and w.deleteFlag = :deleteFlag and w.startDate <= :startDate and w.endDate >= :endDate ")
	List<WeekDomain> findByOrgIdAndDate(@Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag, @Param(value = "startDate") Date startDate,@Param(value = "endDate") Date endDate);

	List<Week> findBySemesterAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndDeleteFlag(Semester semester, Date startDate, Date endDate, Integer deleteFlag);

	List<Week> findByIdIn(Set<Long> ids);

	List<Week> findBySemesterAndDeleteFlag(Semester semester, Integer deleteFlag);

	List<Week> findBySemesterAndNoAndDeleteFlag (Semester semester, Integer no, Integer deleteFlag);

	@Query("select max(w.no) from #{#entityName} w where w.semester = :semester and w.deleteFlag = :deleteFlag")
	Integer findMaxWeekNoBySemester(@Param(value = "semester") Semester semester, @Param(value = "deleteFlag") Integer deleteFlag);

	@Query("select c from #{#entityName} c where c.no in (select max(w.no) from #{#entityName} w where w.semester = :semester and w.deleteFlag = :deleteFlag) and c.semester = :semester and c.deleteFlag = :deleteFlag")
	List<Week> findMaxWeekBySemester(@Param(value = "semester") Semester semester, @Param(value = "deleteFlag") Integer deleteFlag);

	@Query("select c from #{#entityName} c where c.no in (select min(w.no) from #{#entityName} w where w.semester = :semester and w.deleteFlag = :deleteFlag) and c.semester = :semester and c.deleteFlag = :deleteFlag")
	List<Week> findMinWeekBySemester(@Param(value = "semester") Semester semester, @Param(value = "deleteFlag") Integer deleteFlag);
}
