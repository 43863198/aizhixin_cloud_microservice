package com.aizhixin.cloud.dd.rollcall.repository;

import com.aizhixin.cloud.dd.rollcall.entity.ModifyAttendanceLog;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-10-16
 */
public interface ModifyAttendanceLogRepository extends JpaRepository<ModifyAttendanceLog, Long> {
    List<ModifyAttendanceLog> findAllByRollcallIdOrderByOperatingDate(Long rollcallId);

}
