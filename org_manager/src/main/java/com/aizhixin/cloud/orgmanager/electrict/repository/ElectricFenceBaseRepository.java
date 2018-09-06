package com.aizhixin.cloud.orgmanager.electrict.repository;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.aizhixin.cloud.orgmanager.electrict.domain.UseElectricFenceUserDaomin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aizhixin.cloud.orgmanager.electrict.entity.ElectricFenceBase;
public interface ElectricFenceBaseRepository extends JpaRepository<ElectricFenceBase, Long>,
		JpaSpecificationExecutor<ElectricFenceBase> {

	@Query("SELECT e.address FROM #{#entityName} e where e.user.id = :userId and e.outOfRange = 1 order by e.noticeTime desc")
	List<String>  findAddressByUserId(@Param(value="userId")Long userId);

	@Query("select e.noticeTime from #{#entityName} e where e.user.id = :userId and e.outOfRange = 1 order by e.noticeTime desc ")
	List<Date>  findNoticeTimeByUserId(@Param(value="userId")Long userId);

	//当天轨迹
	@Query("select new com.aizhixin.cloud.orgmanager.electrict.domain.UseElectricFenceUserDaomin(e.noticeTime, e.address, e.outOfRange, e.lltude) from #{#entityName} e where e.organId = :organId and e.user.id = :userId and e.noticeTime between :startDate and :endDate order by e.noticeTime")
	List<UseElectricFenceUserDaomin> findUserInfoByUserId(@Param(value = "organId") Long organId,@Param(value = "userId") Long userId, @Param(value = "startDate") Date startDate, @Param(value = "endDate") Date endDate);

}