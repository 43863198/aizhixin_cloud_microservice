package com.aizhixin.cloud.orgmanager.classschedule.repository;


import com.aizhixin.cloud.orgmanager.classschedule.domain.SchoolUnifyAdjustCourseDomain;
import com.aizhixin.cloud.orgmanager.classschedule.entity.SchoolUnifyAdjustCourseSchedule;
import com.aizhixin.cloud.orgmanager.company.entity.Semester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface SchoolUnifyAdjustCourseRepository extends JpaRepository<SchoolUnifyAdjustCourseSchedule, Long> {

    @Query("select new com.aizhixin.cloud.orgmanager.classschedule.domain.SchoolUnifyAdjustCourseDomain(c.id, c.name, c.srcDate, c.destDate, c.semester.id, c.semester.name) from #{#entityName} c where c.orgId = :orgId and c.deleteFlag = :deleteFlag")
    Page<SchoolUnifyAdjustCourseDomain> findByOrgId(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag);

    List<SchoolUnifyAdjustCourseSchedule> findBySemesterAndDestDateAndDeleteFlag(Semester semester, String date, Integer deleteFlag);

    long countByOrgIdAndSrcDateAndDeleteFlag(Long orgId, String srcDate, Integer deleteFlag);

    long countByOrgIdAndDestDateAndDeleteFlag(Long orgId, String destDate, Integer deleteFlag);
}