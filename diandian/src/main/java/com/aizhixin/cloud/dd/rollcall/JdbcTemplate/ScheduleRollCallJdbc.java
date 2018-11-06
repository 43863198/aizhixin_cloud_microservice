package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ScheduleRollCallJdbc {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void updateScheduleVerify(Long id, String verify) {
        String sql = "UPDATE dd_schedule_rollcall SET LOCALTION='" + verify + "' WHERE ID=" + id;
        jdbcTemplate.execute(sql);
    }
}
