package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class RollCallStatsJdbc {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getStuStats(Long orgId, Long semesterId) {
        String sql = "SELECT STUDENT_ID, STUDENT_NUM, STUDENT_NAME, SUM(IF(TYPE=1, 1, 0)) normacount, SUM(IF(TYPE=2, 1, 0)) truancycount, SUM(IF(TYPE=3, 1, 0)) latecount, SUM(IF(TYPE=4, 1, 0)) askforleavecount, SUM(IF(TYPE=5, 1, 0)) leavecount  FROM dd_rollcall dr WHERE dr.DELETE_FLAG=0 AND org_id=" + orgId + " AND SEMESTER_ID=" + semesterId + " GROUP BY STUDENT_ID";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getStuStatsByStuId(Long orgId, Long semesterId, Long stuId) {
        String sql = "SELECT STUDENT_ID, STUDENT_NUM, STUDENT_NAME, SUM(IF(TYPE=1, 1, 0)) normacount, SUM(IF(TYPE=2, 1, 0)) truancycount, SUM(IF(TYPE=3, 1, 0)) latecount, SUM(IF(TYPE=4, 1, 0)) askforleavecount, SUM(IF(TYPE=5, 1, 0)) leavecount  FROM dd_rollcall dr WHERE dr.DELETE_FLAG=0 AND org_id=" + orgId + " AND SEMESTER_ID=" + semesterId + " AND STUDENT_ID="+stuId;
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getStuStatsByTeachingClassId(Long orgId, Long semesterId, Long teachingClassId) {
        String sql = "SELECT STUDENT_ID, STUDENT_NUM, STUDENT_NAME, SUM(IF(TYPE=1, 1, 0)) normacount, SUM(IF(TYPE=2, 1, 0)) truancycount, SUM(IF(TYPE=3, 1, 0)) latecount, SUM(IF(TYPE=4, 1, 0)) askforleavecount, SUM(IF(TYPE=5, 1, 0)) leavecount  FROM dd_rollcall dr WHERE dr.DELETE_FLAG=0 AND org_id=" + orgId + " AND SEMESTER_ID=" + semesterId + " AND TEACHINGCLASS_ID=" + teachingClassId + " GROUP BY STUDENT_ID";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getStuTeachingClassStats(Long orgId) {
        String sql = "SELECT TEACHINGCLASS_ID, STUDENT_ID, STUDENT_NUM, STUDENT_NAME, MIN(CREATED_DATE) startdate, COUNT(*) totalcount, SUM(IF(TYPE=1, 1, 0)) normacount, SUM(IF(TYPE=2, 1, 0)) truancycount, SUM(IF(TYPE=3, 1, 0)) latecount, SUM(IF(TYPE=4, 1, 0)) askforleavecount, SUM(IF(TYPE=5, 1, 0)) leavecount  FROM dd_rollcall dr WHERE dr.DELETE_FLAG=0 AND org_id=" + orgId + " GROUP BY STUDENT_ID, TEACHINGCLASS_ID";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getTeachingClassStats(Long orgId, Long semesterId) {
        String sql = "SELECT TEACHINGCLASS_ID, COUNT(*) totalcount, SUM(IF(TYPE=1, 1, 0)) normacount, SUM(IF(TYPE=2, 1, 0)) truancycount, SUM(IF(TYPE=3, 1, 0)) latecount, SUM(IF(TYPE=4, 1, 0)) askforleavecount, SUM(IF(TYPE=5, 1, 0)) leavecount FROM dd_rollcall dr WHERE dr.DELETE_FLAG=0 AND org_id=" + orgId + " AND SEMESTER_ID=" + semesterId + " GROUP BY TEACHINGCLASS_ID";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getTeachingClassStatsHistory(Long teachingClassId, Long semesterId) {
        String sql = "SELECT TEACHINGCLASS_ID, DATE_FORMAT(CREATED_DATE,'%Y-%m-%d') date, COUNT(*) totalcount, SUM(IF(TYPE=1, 1, 0)) normacount, SUM(IF(TYPE=2, 1, 0)) truancycount, SUM(IF(TYPE=3, 1, 0)) latecount, SUM(IF(TYPE=4, 1, 0)) askforleavecount, SUM(IF(TYPE=5, 1, 0)) leavecount FROM dd_rollcall dr WHERE dr.DELETE_FLAG=0 AND TEACHINGCLASS_ID=" + teachingClassId + " AND SEMESTER_ID=" + semesterId + " GROUP BY SCHEDULE_ROLLCALL_ID";
        return jdbcTemplate.queryForList(sql);
    }
}
