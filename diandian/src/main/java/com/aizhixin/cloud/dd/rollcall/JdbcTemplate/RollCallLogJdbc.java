package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class RollCallLogJdbc {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getRollCallCount(String date){
        String sql = "SELECT COUNT(*) stucount, dr.SCHEDULE_ROLLCALL_ID FROM dd_rollcall dr LEFT JOIN dd_schedule_rollcall dsr ON dsr.ID=dr.SCHEDULE_ROLLCALL_ID LEFT JOIN dd_schedule ds ON ds.ID=dsr.SCHEDULE_ID WHERE ds.TEACH_DATE='"+date+"' GROUP BY dr.SCHEDULE_ROLLCALL_ID";
        return jdbcTemplate.queryForList(sql);
    }
}
