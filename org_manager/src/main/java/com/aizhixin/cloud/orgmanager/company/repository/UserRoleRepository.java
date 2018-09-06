package com.aizhixin.cloud.orgmanager.company.repository;


import com.aizhixin.cloud.orgmanager.company.domain.SemesterDomain;
import com.aizhixin.cloud.orgmanager.company.domain.TeacherDomain;
import com.aizhixin.cloud.orgmanager.company.entity.User;
import com.aizhixin.cloud.orgmanager.company.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

	@Query("select u.roleName from #{#entityName} u where u.user.id = :userId and u.deleteFlag = :deleteFlag")
	List<String> findByUser(@Param(value = "userId") Long userId, @Param(value = "deleteFlag") Integer deleteFlag);

	@Modifying
	@Query("delete from com.aizhixin.cloud.orgmanager.company.entity.UserRole r where r.user = :user")
	void deleteByUser(@Param(value = "user") User user);

	@Modifying
	@Query("delete from com.aizhixin.cloud.orgmanager.company.entity.UserRole r where r.user.id = :userId")
	void deleteByUserId(@Param(value = "userId") Long userId);

	@Modifying
	@Query("delete from com.aizhixin.cloud.orgmanager.company.entity.UserRole r where r.user.id = :userId and r.roleName = :roleName")
	void deleteByUserIdAndRoleName(@Param(value = "userId") Long userId, @Param(value = "roleName") String roleName);



}
