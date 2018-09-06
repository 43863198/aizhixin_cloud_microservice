package com.aizhixin.cloud.dd.rollcall.repository;

import com.aizhixin.cloud.dd.rollcall.dto.AttendanceDTO;
import com.aizhixin.cloud.dd.rollcall.dto.RollCallOfSearchDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Repository
public class AttendanceListQuery {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List <AttendanceDTO> queryScheduleAttendance(
            Set <Long> scheduleRollCallIds) {

        String sql = "SELECT   da.schedule_rollcall_id,  allcount,  IFNULL(normalCount, 0) AS normalCount"
                + " FROM  (SELECT     COUNT(*) AS allcount,    dr.schedule_rollcall_id   FROM "
                + " dd_rollcall dr   WHERE dr.schedule_rollcall_id IN ( #scheduleRollCallIds# )"
                + " GROUP BY dr.`schedule_rollcall_id`) da   LEFT JOIN     (SELECT "
                + " COUNT(*) AS normalCount,      dr.schedule_rollcall_id     FROM"
                + " dd_rollcall dr     WHERE (dr.`type` = '1' or dr.type='4')      AND dr.schedule_rollcall_id IN ( #scheduleRollCallIds# )"
                + " GROUP BY dr.`schedule_rollcall_id`) dn     ON da.schedule_rollcall_id = dn.schedule_rollcall_id "
                + " WHERE da.schedule_rollcall_id IN ( #scheduleRollCallIds# )";

        if (null != scheduleRollCallIds && scheduleRollCallIds.size() > 0) {
            StringBuilder sc = new StringBuilder();
            scheduleRollCallIds.forEach(s -> {
                sc.append(s);
                sc.append(",");
            });
            String temp = sc.toString();
            temp = temp.substring(0, temp.length() - 1);
            sql = sql.replace("#scheduleRollCallIds#", temp);
        } else {
            return null;
        }
        return jdbcTemplate.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                AttendanceDTO item = new AttendanceDTO();
                item.setScheduleRollCallId(rs.getLong("schedule_rollcall_id"));
                item.setNormalCount(rs.getInt("normalCount"));
                item.setAllCount(rs.getInt("allCount"));
                return item;
            }
        });
    }


    public List <AttendanceDTO> queryScheduleAttendanceNew(
            Set <Long> scheduleRollCallIds) {

        String sql = " SELECT   dr.`SCHEDULE_ROLLCALL_ID`,  COUNT(dr.`SCHEDULE_ROLLCALL_ID`) allcount, "
                + "   SUM(IF(dr.`TYPE`=1,1,0)) AS normalCount,  SUM(IF(dr.`TYPE`=5,1,0)) AS leaveCount, "
                + "   SUM(IF(dr.`TYPE`=3,1,0)) AS laterCount,  SUM(IF(dr.`TYPE`=4,1,0)) AS askForLeaveCount "
                + " FROM  dd_rollcall dr   LEFT JOIN dd_schedule_rollcall ds "
                + "     ON dr.`SCHEDULE_ROLLCALL_ID` = ds.`ID` WHERE dr.`SCHEDULE_ROLLCALL_ID` IN (#scheduleRollCallIds#) "
                + " GROUP BY dr.`SCHEDULE_ROLLCALL_ID` ";

        if (null != scheduleRollCallIds && scheduleRollCallIds.size() > 0) {
            StringBuilder sc = new StringBuilder();
            scheduleRollCallIds.forEach(s -> {
                sc.append(s);
                sc.append(",");
            });
            String temp = sc.toString();
            temp = temp.substring(0, temp.length() - 1);
            sql = sql.replace("#scheduleRollCallIds#", temp);
        } else {
            return null;
        }
        return jdbcTemplate.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                AttendanceDTO item = new AttendanceDTO();
                item.setScheduleRollCallId(rs.getLong("SCHEDULE_ROLLCALL_ID"));
                item.setNormalCount(rs.getInt("normalCount"));
                item.setAllCount(rs.getInt("allcount"));
                item.setAskForLeaveCount(rs.getInt("askForLeaveCount"));
                item.setLaterCount(rs.getInt("laterCount"));
                item.setLeaveCount(rs.getInt("leaveCount"));
                return item;
            }
        });
    }

    /**
     * 老师断 学生考勤 单周
     *
     * @param teacherId
     * @param weekId
     * @return
     */
    public List <RollCallOfSearchDTO> getRollCallInfoByTeacherAndWeek(
            Long teacherId, Long weekId) {

        String sql = " SELECT	ds.id AS schedule_id,	ds.COURSE_ID AS courseId,	ds.COURSE_NAME AS courseName,"
                + " 	ds.period_no ,ds.period_num,	ds.DAY_OF_WEEK AS day_of_week,	ds.WEEK_ID AS weekId,"
                + " 	min(r.class_id) AS class_id, r.class_name,	r.all_count all_count,	r.n_count n_count"
                + " FROM	dd_schedule ds LEFT JOIN dd_schedule_rollcall dsc ON dsc.SCHEDULE_ID = ds.id"
                + " LEFT JOIN (	SELECT		count(rc.id) all_count,		rc.SCHEDULE_ROLLCALL_ID,		rc.class_id,rc.class_name,"
                + " count(IF(rc.type = 1, 1, IF(rc.`TYPE` = 4, 1, NULL))) n_count	FROM		dd_rollcall rc	WHERE		rc.teacher_id = #teacher_id#"
                + " GROUP BY		rc.SCHEDULE_ROLLCALL_ID,		rc.class_id) r ON r.SCHEDULE_ROLLCALL_ID = dsc.ID"
                + " where ds.TEACHER_ID = #teacher_id# and ds.WEEK_ID = #weekId# GROUP BY	ds.SEMESTER_ID,ds.COURSE_ID,ds.TEACH_DATE,ds.PERIOD_NO,r.class_id "
                + " ORDER BY	ds.DAY_OF_WEEK ASC,	ds.PERIOD_NO ASC";


        if (teacherId != null) {
            sql = sql.replaceAll("#teacher_id#", teacherId.toString());
        }
        if (teacherId != null) {
            sql = sql.replaceAll("#weekId#", weekId.toString());
        }

        return jdbcTemplate.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                RollCallOfSearchDTO item = new RollCallOfSearchDTO();
                item.setClassId(rs.getLong("class_id"));
                item.setClassName(rs.getString("class_name"));
                item.setCourseName(rs.getString("courseName"));
                item.setDayOfWeek(rs.getString("day_of_week"));
                item.setPeriodName(rs.getString("period_no"));
                item.setScheduleId(rs.getLong("schedule_id"));
                item.setCourseId(rs.getLong("courseId"));
                item.setAllCount(rs.getLong("all_count"));
                item.setnCount(rs.getLong("n_count"));
                return item;
            }
        });
    }
}
