package com.aizhixin.cloud.dd.rollcall.repository;

import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.rollcall.dto.AttendanceAllDTO;
import com.aizhixin.cloud.dd.rollcall.dto.AttendanceCountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.AttendanceDTO;
import com.aizhixin.cloud.dd.rollcall.dto.AttendancesInfoDTO;
import com.aizhixin.cloud.dd.rollcall.utils.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class StudentAttendanceQuery {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public PageData<AttendanceAllDTO> queryScheduleAttendance(Integer offset, Integer limit, Long organId, Long userId, Long semesterId, String typeId, String courseName, Long courseId) {

        String sql = "SELECT dr.TEACHER_ID AS teacherId, ds.TEACHER_NAME AS NAME, ds.COURSE_ID as courseId,dr.TYPE AS typeId, ds.DAY_OF_WEEK AS dayWeek, ds.TEACH_DATE AS teachTime, " +
                " ds.PERIOD_NO AS periodName, ds.COURSE_NAME AS courseName, ds.ID AS scheduleId, dsr.ID AS scheduleRollCallId " +
                " FROM dd_rollcall dr LEFT JOIN dd_schedule_rollcall dsr ON dr.SCHEDULE_ROLLCALL_ID = dsr.ID " +
                " LEFT JOIN dd_schedule ds ON ds.ID = dsr.SCHEDULE_ID where 1=1 and dr.TYPE >=1 AND dr.TYPE <=5 ";

        if (organId != null) {
            sql += " AND ds.organ_Id=" + organId;
        }
        if (userId != null) {
            sql += " And dr.student_id=" + userId;
        }
        if (semesterId != null) {
            sql += " AND ds.semester_id=" + semesterId;
        }
        if (typeId != null) {
            sql += " AND dr.type=" + typeId;
        }
        if (courseName != null) {
            sql += " And ds.course_name='" + courseName + "'";
        }

        if (null != courseId) {
            sql += " And ds.course_id='" + courseId + "'";
        }
        sql += " order by ds.TEACH_DATE desc ,ds.DAY_OF_WEEK desc ,ds.PERIOD_NO desc ";

        PageData<AttendanceAllDTO> page = new PageData();
        String limitSql = PaginationUtil.page(jdbcTemplate, sql, offset, limit, page);

        if (null == limitSql) {
            page.setPage(new PageDomain(0l, 0, 0, 10));
            return page;
        }
        sql = sql + limitSql;

        page.setData(jdbcTemplate.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                AttendanceAllDTO item = new AttendanceAllDTO();
                item.setName(rs.getString("name"));
                item.setCourseName(rs.getString("courseName"));
                item.setDayWeek(rs.getLong("dayWeek"));
                item.setPeriodName(rs.getString("periodName"));
                item.setTeach_time(rs.getDate("teachTime"));
                item.setTeacherId(rs.getLong("teacherId"));
                item.setTypeId(rs.getLong("typeId"));
                item.setCourseId(rs.getString("courseId"));
                item.setScheduleId(rs.getString("scheduleId"));
                item.setScheduleRollCallId(rs.getString("scheduleRollCallId"));
                return item;
            }
        }));
        return page;
    }


    public List<AttendanceCountDTO> queryScheduleAttendanceCount(Long organId, Long userId, Long semesterId) {

        String sql = "SELECT COUNT(CASE ddd.typeId WHEN 1 THEN 1 END) AS arrived, COUNT(CASE ddd.typeId WHEN 2 THEN 1 END) AS crunk, COUNT(CASE ddd.typeId WHEN 3 THEN 1 END) AS late, COUNT(CASE ddd.typeId WHEN 4 THEN 1 END) AS beg, COUNT(CASE ddd.typeId WHEN 5 THEN 1 END) AS early , COUNT(1) AS countSum FROM (SELECT rl.type AS typeId FROM dd_rollcall rl left JOIN dd_schedule_rollcall dsr on rl.SCHEDULE_ROLLCALL_ID = dsr.id LEFT JOIN dd_schedule ds on ds.ID=dsr.SCHEDULE_ID WHERE 1 = 1";

        if (organId != null) {
            sql += " AND ds.organ_Id=" + organId;
        }
        if (userId != null) {
            sql += " And rl.student_id=" + userId;
        }
        if (semesterId != null) {
            sql += " AND ds.semester_id=" + semesterId;
        }
        sql += ") ddd ";
        return jdbcTemplate.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                AttendanceCountDTO item = new AttendanceCountDTO();
                item.setArrvied(rs.getLong("arrived"));
                item.setBeg(rs.getLong("beg"));
                item.setCrunk(rs.getLong("crunk"));
                item.setEarly(rs.getLong("early"));
                item.setLate(rs.getLong("late"));
                item.setCountSum(rs.getLong("countSum"));
                return item;
            }
        });
    }


    public List<AttendanceCountDTO> queryScheduleAttendanceCourse(Integer offset, Integer limit, Long organId, Long userId,
                                                                  Long semesterId, String typeId, String courseName, Long courseId) {

        String sql = "SELECT ds.COURSE_NAME AS courseName,ds.TEACHER_NAME AS teacherName, COUNT(CASE rl.type WHEN 1 THEN 1 END) AS arrvied, COUNT(CASE rl.type WHEN 2 THEN 1 END) AS crunk, COUNT(CASE rl.type WHEN 3 THEN 1 END) AS late , COUNT(CASE rl.type WHEN 4 THEN 1 END) AS beg, COUNT(CASE rl.type WHEN 5 THEN 1 END) AS early, COUNT(1) AS CountSum FROM dd_rollcall rl left JOIN dd_schedule_rollcall dsr on rl.SCHEDULE_ROLLCALL_ID = dsr.id LEFT JOIN dd_schedule ds on ds.ID=dsr.SCHEDULE_ID WHERE 1=1";

        if (organId != null) {
            sql += " AND ds.organ_Id=" + organId;
        }
        if (userId != null) {
            sql += " And rl.student_id=" + userId;
        }
        if (semesterId != null) {
            sql += " AND ds.semester_id=" + semesterId;
        }
        if (typeId != null) {
            sql += " And rl.type=" + typeId;
        }
        if (courseName != null) {
            sql += " And ds.COURSE_NAME='" + courseName + "'";
        }

        if (courseId != null) {
            sql += " And ds.COURSE_id='" + courseId + "'";
        }
        sql += " GROUP BY ds.COURSE_ID";

        String limitSql = PaginationUtil.page(jdbcTemplate, sql, offset, limit);
        if (null == limitSql) {
            return new ArrayList<>();
        }
        sql = sql + limitSql;

        return jdbcTemplate.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                AttendanceCountDTO item = new AttendanceCountDTO();
                item.setArrvied(rs.getLong("arrvied"));
                item.setBeg(rs.getLong("beg"));
                item.setCrunk(rs.getLong("crunk"));
                item.setEarly(rs.getLong("early"));
                item.setLate(rs.getLong("late"));
                item.setCountSum(rs.getLong("countSum"));
                item.setTeacherName(rs.getString("teacherName"));
                item.setCourseName(rs.getString("courseName"));
                return item;
            }
        });
    }
}
