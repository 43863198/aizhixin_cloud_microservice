package com.aizhixin.cloud.dd.counsellorollcall.repository;

import com.aizhixin.cloud.dd.counsellorollcall.entity.StudentSubGroup;
import com.aizhixin.cloud.dd.counsellorollcall.entity.TempGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

/**
 * Created by LIMH on 2017/11/29.
 */
public interface StudentSubGroupRepository extends JpaRepository<StudentSubGroup, Long> {

    List<StudentSubGroup> findAllByTempGroupAndDeleteFlag(TempGroup tempGroup, Integer deleteFlag);

    @Query("select g.studentId from #{#entityName} g where g.tempGroup = :tempGroup and g.studentId  in :studentIds and g.deleteFlag = :deleteFlag")
    List<Long> findAllByTempGroupAndStudentId(@Param(value = "tempGroup") TempGroup tempGroup, @Param(value = "studentIds") Set studentIds,
        @Param(value = "deleteFlag") Integer deleteFlag);
}
