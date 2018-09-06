package com.aizhixin.cloud.orgmanager.electrict.repository;

import com.aizhixin.cloud.orgmanager.company.domain.StudentSimpleDomain;
import com.aizhixin.cloud.orgmanager.company.entity.Classes;
import com.aizhixin.cloud.orgmanager.electrict.domain.UseElectricFenceUserDaomin;
import com.aizhixin.cloud.orgmanager.electrict.entity.ElectricFenceStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * @author Created by jianwei.wu on
 * @E-mail wujianwei@aizhixin.com
 */
public interface ElectricFenceStatisticsRepository extends PagingAndSortingRepository<ElectricFenceStatistics, Long>,
        JpaSpecificationExecutor<ElectricFenceStatistics> {

        //查询统时间
        @Query("select e.createdDate from #{#entityName} e where e.organId = :organId and e.userId = :userId and e.createdDate between :startDate and :endDate order by e.createdDate")
        List<Date> findStatisticsTime(@Param(value = "organId") Long organId,@Param(value = "userId") Long userId, @Param(value = "startDate") Date startDate, @Param(value = "endDate") Date endDate);


}
