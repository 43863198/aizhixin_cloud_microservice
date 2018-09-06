package com.aizhixin.cloud.orgmanager.company.repository;


import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
import com.aizhixin.cloud.orgmanager.company.domain.StudentRollcallSetLogDomain;
import com.aizhixin.cloud.orgmanager.company.entity.Classes;
import com.aizhixin.cloud.orgmanager.company.entity.StudentRollcallSet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;


public interface StudentRollcallSetRepository extends JpaRepository<StudentRollcallSet, Long> {
}