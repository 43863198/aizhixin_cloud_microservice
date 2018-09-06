package com.aizhixin.cloud.orgmanager.classschedule.repository;


import com.aizhixin.cloud.orgmanager.classschedule.domain.SchoolHolidayDomain;
import com.aizhixin.cloud.orgmanager.classschedule.entity.SchoolHoliday;
import com.aizhixin.cloud.orgmanager.company.entity.Semester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface SchoolHolidayRepository extends JpaRepository<SchoolHoliday, Long> {

    @Query("select new com.aizhixin.cloud.orgmanager.classschedule.domain.SchoolHolidayDomain(c.id, c.name, c.startDate, c.endDate, c.semester.id, c.semester.name) from #{#entityName} c where c.orgId = :orgId and c.deleteFlag = :deleteFlag")
    Page<SchoolHolidayDomain> findByOrgId(Pageable pageable, @Param(value = "orgId") Long orgId, @Param(value = "deleteFlag") Integer deleteFlag);

    List<SchoolHoliday> findBySemesterAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndDeleteFlag(Semester semester, String date1, String date2, Integer deleteFlag);
}