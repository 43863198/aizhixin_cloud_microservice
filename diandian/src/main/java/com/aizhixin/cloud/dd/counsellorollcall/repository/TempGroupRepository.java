package com.aizhixin.cloud.dd.counsellorollcall.repository;

import com.aizhixin.cloud.dd.counsellorollcall.domain.TempGroupDomain;
import com.aizhixin.cloud.dd.counsellorollcall.domain.TempGroupDomainV2;
import com.aizhixin.cloud.dd.counsellorollcall.entity.TempGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by LIMH on 2017/11/29.
 */
public interface TempGroupRepository extends JpaRepository<TempGroup, Long> {
    @Query("select new com.aizhixin.cloud.dd.counsellorollcall.domain.TempGroupDomain(g.id,g.name,g.subGroupNum,g.status) from #{#entityName} g where g.teacherId = :teacherId and g.deleteFlag = :deleteFlag and (g.rollcallNum is null or g.rollcallNum < 2)")
    public List<TempGroupDomain> findAllByTeacherIdAndDeleteFlag(@Param(value = "teacherId") Long teacherId, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.dd.counsellorollcall.domain.TempGroupDomain(g.id,g.name,g.subGroupNum,g.status) from #{#entityName} g where g.teacherId = :teacherId")
    public List<TempGroupDomain> findAllByTeacherId(@Param(value = "teacherId") Long teacherId);

    @Query("select new com.aizhixin.cloud.dd.counsellorollcall.domain.TempGroupDomainV2(g.id,g.name,g.subGroupNum,g.rollcallNum,g.status,g.rollcallType,g.ruleId, g.messageId, g.teacherName) from #{#entityName} g where g.teacherId = :teacherId and g.deleteFlag = :deleteFlag")
    public List<TempGroupDomainV2> findByTeacherIdAndDeleteFlag(@Param(value = "teacherId") Long teacherId, @Param(value = "deleteFlag") Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.dd.counsellorollcall.domain.TempGroupDomainV2(g.id,g.name,g.subGroupNum,g.rollcallNum,g.status,g.rollcallType,g.ruleId, g.messageId, g.teacherName) from #{#entityName} g where g.classId = :classId and g.deleteFlag = :deleteFlag")
    public List<TempGroupDomainV2> findByClassIdAndDeleteFlag(@Param(value = "classId") Long classId, @Param(value = "deleteFlag") Integer deleteFlag);

    public List<TempGroup> findAllByTeacherIdAndNameAndDeleteFlag(Long teacherId, String name, Integer deleteFlag);

    public List<TempGroup> findByRuleIdAndDeleteFlag(Long ruleId, Integer deleteFlag);

    public List<TempGroup> findByPracticeIdAndDeleteFlag(Long practiceId, Integer deleteFlag);

    @Query("select new com.aizhixin.cloud.dd.counsellorollcall.domain.TempGroupDomainV2(g.id,g.name,g.subGroupNum,g.rollcallNum,g.status,g.rollcallType,g.ruleId, g.messageId, g.teacherName) from #{#entityName} g where g.practiceId=:practiceId and g.deleteFlag = :deleteFlag")
    public List<TempGroupDomainV2> findByPracticeIdAndDeleteFlag2(@Param(value = "practiceId") Long practiceId, @Param(value = "deleteFlag") Integer deleteFlag);

    public List<TempGroup> findAllByStatusAndDeleteFlag(Boolean status, Integer deleteFlag);
}
