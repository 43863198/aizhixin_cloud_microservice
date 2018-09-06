package com.aizhixin.cloud.dd.communication.repository;

import com.aizhixin.cloud.dd.communication.entity.RollCallReport;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface RollCallReportRepository extends JpaRepository<RollCallReport, Long>,
        JpaSpecificationExecutor<RollCallReport> {

    @Modifying
    @Query("update com.aizhixin.cloud.dd.communication.entity.RollCallReport rc set rc.leaveStatus =true where rc.id = :reportId")
    int findreportIdModifyLeave(@Param("reportId") Long reportId);
    
    @Query(value ="SELECT rc FROM #{#entityName} rc WHERE rc.studentId = :studentId AND rc.lookStatus = 0 AND rc.leaveStatus IS NOT NULL")
	List<RollCallReport> findByStudentId(@Param("studentId") Long studentId);

    List<RollCallReport> findAllByRollCallEverIdAndClassId(Long rollCallEverId,Long classId);
}
