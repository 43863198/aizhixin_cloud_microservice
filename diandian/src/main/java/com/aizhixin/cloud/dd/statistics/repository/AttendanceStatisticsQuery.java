package com.aizhixin.cloud.dd.statistics.repository;

import com.aizhixin.cloud.dd.rollcall.dto.AttendanceDTO;
import com.aizhixin.cloud.dd.rollcall.dto.RollCallOfSearchDTO;
import com.aizhixin.cloud.dd.rollcall.dto.TeachingClassesDTO;
import com.aizhixin.cloud.dd.statistics.dto.AssessStatisticsDTO;
import com.aizhixin.cloud.dd.statistics.dto.RollCallStatisticsDTO;
import com.aizhixin.cloud.dd.statistics.dto.TeachingClassStatisticsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Set;

@Repository
public class AttendanceStatisticsQuery {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List <TeachingClassStatisticsDTO> queryScheduleAttendance(Long organId,
                                                                     String date) {

        String sql = " SELECT   dr.id as sccheduleRollCallId, ds.`COURSE_NAME`,  ds.`TEACHINGCLASS_NAME`,  ds.`TEACHINGCLASS_ID`, ds.TEACHER_ID, ds.`TEACHER_NAME`,ds.start_time,ds.end_time,  "
                + "   SUM(IF(dro.type = 1, 1,IF(dro.type = 4, 1,IF(dro.type = 3, 1,0)))) as signCount,  COUNT(dro.id) as total                                 "
                + " FROM  dd_schedule ds   LEFT JOIN dd_schedule_rollcall dr     ON ds.`ID` = dr.`SCHEDULE_ID`      "
                + "   LEFT JOIN dd_rollcall dro     ON dro.`SCHEDULE_ROLLCALL_ID` = dr.`ID`                         "
                + " WHERE ds.ORGAN_ID =" + organId + " AND ds.TEACH_DATE = '" + date + "' GROUP BY ds.`ID` order by ds.start_time,ds.`TEACHER_ID` ;                                                      ";

        return jdbcTemplate.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                TeachingClassStatisticsDTO item = new TeachingClassStatisticsDTO();
                item.setCourseName(rs.getString("COURSE_NAME"));
                item.setTeachingClassesId(rs.getLong("TEACHINGCLASS_ID"));
                item.setScheduleRollCallId(rs.getLong("sccheduleRollCallId"));
                item.setTeacherId(rs.getLong("TEACHER_ID"));
                item.setTeacherName(rs.getString("TEACHER_NAME"));
                item.setSignCount(rs.getString("signCount"));
                item.setAllStudent(rs.getString("total"));
                item.setBeginTime(rs.getString("start_time"));
                item.setEndTime(rs.getString("end_time"));
                return item;
            }
        });
    }

    public List <TeachingClassStatisticsDTO> queryScheduleAttendanceIds(Long organId, String date) {

        String sql = " SELECT * FROM  (SELECT  dsc.organ_id, dsc.COURSE_NAME, dsc.TEACHER_NAME,dsc.TEACHER_ID, dsc.TEACHINGCLASS_ID,dsc.start_time,dsc.end_time,"
                + " STR_TO_DATE(CONCAT(DATE_FORMAT(dsc.teach_date, '%Y-%m-%d'),\" \",dsc.start_time),'%Y-%m-%d%H:%i:%s') AS teachBeginTime, "
                + " STR_TO_DATE(CONCAT(DATE_FORMAT(dsc.teach_date, '%Y-%m-%d'),\" \",dsc.end_time),'%Y-%m-%d%H:%i:%s') AS teachEndTime, "
                + " dr.`ID` AS scheduleRolllCallId  "
                + " FROM  dd_schedule dsc   LEFT JOIN dd_schedule_rollcall dr     ON dsc.`ID` = dr.`SCHEDULE_ID`  "
                + " WHERE dsc.`ORGAN_ID` = " + organId + " AND dsc.`TEACH_DATE` = '" + date + "' AND dr.id IS NOT NULL) db "
                + " WHERE db.teachBeginTime < CURRENT_TIME() AND db.teachEndTime >CURRENT_TIME();                                                   ";
        return jdbcTemplate.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                TeachingClassStatisticsDTO item = new TeachingClassStatisticsDTO();
                item.setCourseName(rs.getString("COURSE_NAME"));
                item.setTeachingClassesId(rs.getLong("TEACHINGCLASS_ID"));
                item.setTeacherName(rs.getString("TEACHER_NAME"));
                item.setTeacherId(rs.getLong("TEACHER_ID"));
                item.setScheduleRollCallId(rs.getLong("scheduleRolllCallId"));
                item.setBeginTime(rs.getString("start_time"));
                item.setEndTime(rs.getString("end_time"));
                return item;
            }
        });
    }


    public List <RollCallStatisticsDTO> queryRollCallAttendance(Long organId, String date) {

        String sql = " SELECT   ds.`TEACHER_ID`,  ds.`TEACHER_NAME`,  ds.`ORGAN_ID`, "
                + "   SUM(IF(dr.type = 1, 1,IF(dr.type = 4, 1,IF(dr.type = 3, 1,0)))) AS signCount,  SUM(IF(dr.id IS NULL, 0, 1)) total  "
                + " FROM  dd_schedule ds   LEFT JOIN dd_schedule_rollcall dsr     ON ds.`ID` = dsr.`SCHEDULE_ID` "
                + "   LEFT JOIN dd_rollcall dr     ON dr.`SCHEDULE_ROLLCALL_ID` = dsr.`ID`  "
                + " WHERE ds.`TEACH_DATE` = '" + date + "'   AND ds.organ_id = " + organId + " "
                + " GROUP BY ds.`TEACHER_ID` ";
        NumberFormat nt = NumberFormat.getPercentInstance();
        nt.setMinimumFractionDigits(1);
        return jdbcTemplate.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                RollCallStatisticsDTO item = new RollCallStatisticsDTO();
                item.setTeacherId(rs.getLong("TEACHER_ID"));
                item.setTeacherName(rs.getString("TEACHER_NAME"));
                int signCount = rs.getInt("signCount");
                int total = rs.getInt("total");
                item.setSignCount(signCount);
                item.setTotalCount(total);
                String temp = nt.format((float) signCount / (total == 0 ? 1 : total));
                item.setRollCallRate(temp.substring(0, temp.indexOf("%")));
                return item;
            }
        });
    }

    public List <AssessStatisticsDTO> queryAssessAttendance(Long organId, String date) {

        String sql = " SELECT   ds.`TEACHER_ID`,  ds.`TEACHER_NAME`,  COUNT(ds.id) totalAssess,SUM(da.score) totalScore,  SUM(IF(da.score > 2, da.score, 0)) goodScore, SUM(IF(da.score > 2, 1, 0)) goodCount          "
                + " FROM  dd_schedule ds   RIGHT JOIN dd_assess da     ON ds.`ID` = da.`schedule_id`  "
                + " WHERE ds.`TEACHER_ID` IS NOT NULL   AND ds.`TEACH_DATE` = '" + date + "'            "
                + "   AND ds.organ_id = " + organId + " GROUP BY ds.TEACHER_ID                                    ";

        NumberFormat nt = NumberFormat.getPercentInstance();
        nt.setMinimumFractionDigits(2);
        return jdbcTemplate.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                AssessStatisticsDTO item = new AssessStatisticsDTO();
                item.setTeacherId(rs.getLong("TEACHER_ID"));
                item.setTeacherName(rs.getString("TEACHER_NAME"));
                int total = rs.getInt("totalAssess");
                int assess = rs.getInt("goodCount");
                item.setAssessCount(total);
                item.setGoodCount(assess);
                item.setTotalScore(rs.getLong("totalScore"));
                item.setGoodScore(rs.getLong("goodScore"));
                String temp = nt.format((float) assess / (total == 0 ? 1 : total));
                item.setAssesRate(temp.substring(0, temp.indexOf("%")));

                return item;
            }
        });
    }
}
