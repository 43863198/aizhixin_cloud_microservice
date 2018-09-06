package com.aizhixin.cloud.dd.rollcall.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.aizhixin.cloud.dd.rollcall.dto.AssessAndRevertDTO;
import com.aizhixin.cloud.dd.rollcall.dto.AssessClassesAvgDTO;
import com.aizhixin.cloud.dd.rollcall.entity.Assess;

public interface AssessRepository extends PagingAndSortingRepository<Assess, Long> {

    List<Assess> findByStudentIdAndScheduleId(Long studentId, Long scheduleId);

    List<Assess> findByStudentIdAndScheduleIdIn(Long studentId, Set<Long> scheduleIds);

    List<Assess> findByClassIdAndScheduleId(Long classId, Long scheduleId);

    Page<Assess> findByScheduleIdOrderByCreatedDateDesc(Pageable pageable, Long scheduleId);

    List<Assess> findAllByScheduleId(Long scheduleId);


    Long countByScheduleId(Long schedule_id);

    @Query("select avg(a.score) from com.aizhixin.cloud.dd.rollcall.entity.Assess a where a.scheduleId = :scheduleId")
    Double avgByScheduleId(
            @Param(value = "scheduleId") Long scheduleId);

    @Query("select new com.aizhixin.cloud.dd.rollcall.dto.AssessClassesAvgDTO(a.scheduleId,a.classId,count(a.scheduleId), sum(a.score)) from com.aizhixin.cloud.dd.rollcall.entity.Assess a where a.scheduleId =:scheduleId and a.classId=:classId group by a.scheduleId,a.classId")
    AssessClassesAvgDTO findByScheduleIdAndClassId(@Param(value = "scheduleId") Long scheduleId, @Param(value = "classId") Long classId);

    Long countByStudentIdAndSemesterId(Long studentId, Long semesterId);
    
    Long countBySourseIdAndModuleAndDeleteFlag(Long sourseId,String module,Integer deleteFlag);
    
    List<Assess> findBySourseIdInAndModuleAndDeleteFlag(List<Long> sourseIds,String module,Integer deleteFlag);
    
    Long countBySourseIdAndModuleAndDeleteFlagAndCommentId(Long sourseId,String module,Integer deleteFlag,Long commentId);
    
    Assess findByIdAndDeleteFlag(Long id,Integer deleteFlag);
    
    @Query("select new com.aizhixin.cloud.dd.rollcall.dto.AssessAndRevertDTO(a.id,a.commentId,a.commentName,a.content,a.createdDate,a.revertTotal,a.anonymity,a.score) from  com.aizhixin.cloud.dd.rollcall.entity.Assess as a where a.sourseId =:sourseId and a.module=:module and a.deleteFlag=:deleteFlag ")
    Page<AssessAndRevertDTO> findBySourseIdAndModuleAndDeleteFlag(Pageable page,@Param("sourseId")Long sourseId,@Param("module")String module,@Param("deleteFlag")Integer deleteFlag);
}
