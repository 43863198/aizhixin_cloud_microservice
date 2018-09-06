package com.aizhixin.cloud.dd.rollcall.repository;

import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.rollcall.dto.*;
import com.aizhixin.cloud.dd.rollcall.utils.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

@Repository
public class TeachingClassAttendaceInfoQuery {
//    final static private Logger LOG = LoggerFactory.getLogger(TeachingClassAttendaceInfoQuery.class);


    @Autowired
    JdbcTemplate jdbcTemplate;

    //获取教学班应签到总数
    public Long queryTeachingClassAttendaceInfoCount(Long AttendaceId) {
        String sql = "SELECT count(0) FROM dd_rollcall ro LEFT JOIN dd_schedule_rollcall sr ON ro.SCHEDULE_ROLLCALL_ID = sr.id LEFT JOIN dd_schedule sd ON sr.SCHEDULE_ID = sd.ID WHERE sr.IS_OPEN_ROLL_CALL = b'1' AND sd.TEACHINGCLASS_ID = " + AttendaceId;
        return jdbcTemplate.queryForObject(sql, Long.class);
    }


    //获取教学班信息  type 点名结果,1:已到；2：旷课；3：迟到；4：请假；5：早退(选填，不填则为全部)
    public PageData <AttendancesInfoDTO> queryTeachingClassAttendaceInfopractical(Long organId, Long semesterId, String courseName, String teacherName, Integer offset, Integer limit) {
        String sql = "SELECT sd.semester_id as semesterId,sd.semester_name as semesterName,rc.TEACHINGCLASS_ID,sd.teachingclass_code AS courseId, sd.COURSE_NAME AS courseName, sd.`TEACHER_NAME` AS teacherName, COUNT(0) AS num,COUNT( CASE WHEN rc.type =1 OR rc.TYPE=4 THEN 1 END) AS practical FROM dd_rollcall rc LEFT JOIN dd_schedule_rollcall sr ON rc.SCHEDULE_ROLLCALL_ID = sr.id LEFT JOIN dd_schedule sd ON sr.schedule_id = sd.id WHERE 1 = 1 AND sd.id is not null";
        if (organId != null) {
            sql += " AND sd.ORGAN_ID =" + organId;
        }
        if (semesterId != null) {
            sql += " AND rc.SEMESTER_ID =" + semesterId;
        }
        if (!org.springframework.util.StringUtils.isEmpty(courseName)) {
            sql += " AND sd.COURSE_NAME ='" + courseName + "'";
        }
        if (!org.springframework.util.StringUtils.isEmpty(teacherName)) {
            sql += " AND sd.TEACHER_NAME ='" + teacherName + "'";
        }
        sql += " GROUP BY rc.TEACHINGCLASS_ID";
        PageData <AttendancesInfoDTO> page = new PageData();
        String limitSql = PaginationUtil.page(jdbcTemplate, sql, offset, limit, page);
        if (null == limitSql) {
            page.setPage(new PageDomain(0l, 0, 0, 10));
            return page;
        }
        sql = sql + limitSql;
        long start = System.currentTimeMillis();
//        LOG.info("Start: " + start + "\tLimit SQL:" + limitSql);
        page.setData(jdbcTemplate.query(sql, new RowMapper() {
            @Override
            public Object mapRow(ResultSet rs, int i) throws SQLException {
                AttendancesInfoDTO item = new AttendancesInfoDTO();
                item.setSemesterId(rs.getLong("semesterId"));
                item.setSemesterName(rs.getString("semesterName"));
                item.setClassId(rs.getLong("TEACHINGCLASS_ID"));
                item.setCode(rs.getString("courseId"));
                item.setCourseName(rs.getString("courseName"));
                item.setTeacherName(rs.getString("teacherName"));
                item.setParticipationCount(rs.getLong("num"));
                item.setPractical(rs.getLong("practical"));
                DecimalFormat df = new DecimalFormat("######0.00");
                item.setProportion(df.format(rs.getLong("practical") * 1.0 / rs.getLong("num") * 100) + "%");
                return item;
            }
        }));
        long end = System.currentTimeMillis();
//        LOG.info("End: " + end + "\tSpend time:" + (end - start));
        return page;
    }


    public PageData <AttendancesDTO> queryTeachingClassAttendaceInfo(Long semesterId, Long classId, Integer offset, Integer limit) {
        String sql = "SELECT sd.semester_id as semesterId,sd.semester_name as semesterName,rc.student_id AS studentId,rc.class_name, rc.student_name AS NAME, rc.TEACHINGCLASS_ID AS classId, COUNT(CASE rc.type WHEN 1 THEN 1 END) AS arrvied, COUNT(CASE rc.type WHEN 2 THEN 1 END) AS crunk , COUNT(CASE rc.type WHEN 3 THEN 1 END) AS late, COUNT(CASE rc.type WHEN 4 THEN 1 END) AS beg, COUNT(CASE rc.type WHEN 5 THEN 1 END) AS early FROM dd_rollcall rc LEFT JOIN dd_schedule_rollcall sr ON rc.SCHEDULE_ROLLCALL_ID = sr.id LEFT JOIN dd_schedule sd ON sr.schedule_id = sd.id  WHERE 1=1 And sd.id is not null";
        if (classId != null) {
            sql += " AND rc.TEACHINGCLASS_ID =" + classId;
        }
        if (semesterId != null) {
            sql += " And rc.SEMESTER_ID=" + semesterId;
        }
        sql += " GROUP BY studentId";

        PageData <AttendancesDTO> page = new PageData();
        String limitSql = PaginationUtil.page(jdbcTemplate, sql, offset, limit, page);
        if (null == limitSql) {
            page.setPage(new PageDomain(0l, 0, 0, 10));
            return page;
        }
        sql = sql + limitSql;
        page.setData(jdbcTemplate.query(sql, new RowMapper() {
            @Override
            public Object mapRow(ResultSet rs, int i) throws SQLException {
                AttendancesDTO item = new AttendancesDTO();
                item.setSemesterId(rs.getLong("semesterId"));
                item.setSemesterName(rs.getString("semesterName"));
                item.setArrvied(rs.getLong("arrvied"));
                item.setCrunk(rs.getLong("crunk"));
                item.setLate(rs.getLong("late"));
                item.setBeg(rs.getLong("beg"));
                item.setEarly(rs.getLong("early"));
                item.setStudentId(rs.getLong("studentId"));
                item.setStudentName(rs.getString("name"));
                item.setClassId(rs.getLong("classId"));
                item.setClassName(rs.getString("class_name"));
                return item;
            }
        }));
        return page;
    }

    public PageData <AdministrativeDTO> queryClassAdministrativeInfo(Long semesterId, Long classId, Integer offset, Integer limit) {
        String sql = "SELECT sd.TEACHINGCLASS_NAME AS className,sd.semester_id as semesterId,sd.semester_name as semesterName,rc.student_id AS studentId, rc.student_name AS NAME, rc.TEACHINGCLASS_ID AS classId, COUNT(CASE rc.type WHEN 1 THEN 1 END) AS arrvied, COUNT(CASE rc.type WHEN 2 THEN 1 END) AS crunk , COUNT(CASE rc.type WHEN 3 THEN 1 END) AS late, COUNT(CASE rc.type WHEN 4 THEN 1 END) AS beg, COUNT(CASE rc.type WHEN 5 THEN 1 END) AS early FROM dd_rollcall rc LEFT JOIN dd_schedule_rollcall sr ON rc.SCHEDULE_ROLLCALL_ID = sr.id LEFT JOIN dd_schedule sd ON sr.schedule_id = sd.id  WHERE 1=1 And sd.id is not null";
        if (classId != null) {
            sql += " And rc.CLASS_ID =" + classId;
        }
        if (semesterId != null) {
            sql += " And rc.SEMESTER_ID=" + semesterId;
        }
        sql += " GROUP BY studentId";
        PageData <AdministrativeDTO> page = new PageData();
        String limitSql = PaginationUtil.page(jdbcTemplate, sql, offset, limit, page);
        if (null == limitSql) {
            page.setPage(new PageDomain(0l, 0, 0, 10));
            return page;
        }
        sql = sql + limitSql;
        page.setData(jdbcTemplate.query(sql, new RowMapper() {
            @Override
            public Object mapRow(ResultSet rs, int i) throws SQLException {
                AdministrativeDTO item = new AdministrativeDTO();
                item.setSemesterId(rs.getLong("semesterId"));
                item.setSemesterName(rs.getString("semesterName"));
                item.setArrvied(rs.getLong("arrvied"));
                item.setCrunk(rs.getLong("crunk"));
                item.setLate(rs.getLong("late"));
                item.setBeg(rs.getLong("beg"));
                item.setEarly(rs.getLong("early"));
                item.setStudentId(rs.getLong("studentId"));
                item.setStudentName(rs.getString("name"));
                item.setClassName(rs.getString("className"));
                return item;
            }
        }));
        return page;
    }


    public List <WeekTendencyDto> queryAdministrativeWeekTendency(Long organId, Long semesterId, String classId) {
        String sql = "SELECT sd.semester_id as semesterId,sd.semester_name as semesterName,sd.WEEK_NAME WEEK, COUNT(0) num,COUNT( CASE WHEN rc.type =1 OR rc.TYPE=4 THEN 1 END) AS practical FROM dd_rollcall rc LEFT JOIN dd_schedule_rollcall sr ON rc.SCHEDULE_ROLLCALL_ID = sr.id LEFT JOIN dd_schedule sd ON sr.schedule_id = sd.id WHERE 1 = 1 And sd.id is not null";
        if (organId != null) {
            sql += " AND sd.ORGAN_ID =" + organId;
        }
        if (semesterId != null) {
            sql += " AND rc.SEMESTER_ID =" + semesterId;
        }
        if (classId != null) {
            sql += " AND rc.Class_ID IN(" + classId + ")";
        }
        sql += " GROUP BY sd.WEEK_NAME ORDER BY sd.WEEK_ID";


        return jdbcTemplate.query(sql, new RowMapper() {
            @Override
            public Object mapRow(ResultSet rs, int i) throws SQLException {
                WeekTendencyDto item = new WeekTendencyDto();
                item.setSemesterId(rs.getLong("semesterId"));
                item.setSemesterName(rs.getString("semesterName"));
                if (rs.getString("WEEK") == null) {
                    item.setWeek("");
                } else {
                    item.setWeek(rs.getString("WEEK"));
                }
                item.setParticipationCount(rs.getLong("num"));
                item.setPractical(rs.getLong("practical"));
                DecimalFormat df = new DecimalFormat("######0.00");
                item.setProportion(df.format(rs.getLong("practical") * 1.0 / rs.getLong("num") * 100) + "%");
                return item;
            }
        });

    }

    public List <WeekTendencyDto> queryAttendanceWeekTendency(Long organId, Long semesterId, Long classId, String courseName, String teacherName) {
        String sql = "SELECT sd.semester_id as semesterId,sd.semester_name as semesterName,sd.WEEK_NAME WEEK, COUNT(0) num, COUNT( CASE WHEN rc.type =1 OR rc.TYPE=4 THEN 1 END) AS practical FROM dd_rollcall rc LEFT JOIN dd_schedule_rollcall sr ON rc.SCHEDULE_ROLLCALL_ID = sr.id LEFT JOIN dd_schedule sd ON sr.schedule_id = sd.id WHERE 1 = 1 And sd.id is not null";
        if (organId != null) {
            sql += " AND sd.ORGAN_ID =" + organId;
        }
        if (semesterId != null) {
            sql += " AND rc.SEMESTER_ID =" + semesterId;
        }
        if (classId != null) {
            sql += " AND sd.TEACHINGCLASS_ID = " + classId;
        }
        if (!org.springframework.util.StringUtils.isEmpty(courseName)) {
            sql += " AND sd.COURSE_NAME ='" + courseName + "'";
        }
        if (!org.springframework.util.StringUtils.isEmpty(teacherName)) {
            sql += " AND sd.TEACHER_Name ='" + teacherName + "'";
        }
        sql += " GROUP BY sd.WEEK_NAME";


        return jdbcTemplate.query(sql, new RowMapper() {
            @Override
            public Object mapRow(ResultSet rs, int i) throws SQLException {
                WeekTendencyDto item = new WeekTendencyDto();
                item.setSemesterId(rs.getLong("semesterId"));
                item.setSemesterName(rs.getString("semesterName"));
                if (rs.getString("WEEK") == null) {
                    item.setWeek("");
                } else {
                    item.setWeek(rs.getString("WEEK"));
                }
                item.setParticipationCount(rs.getLong("num"));
                item.setPractical(rs.getLong("practical"));
                DecimalFormat df = new DecimalFormat("######0.00");
                item.setProportion(df.format(rs.getLong("practical") * 1.0 / rs.getLong("num") * 100) + "%");
                return item;
            }
        });

    }


    public PageData <AdministrativesDTO> queryListClassAdministrativepractical(Long organId, Long semesterId, String classAdministrativeId, Integer offset, Integer limit) {
        String sql = "SELECT sd.semester_id as semesterId,sd.semester_name as semesterName,sd.WEEK_NAME WEEK,rc.class_name,rc.CLASS_ID, COUNT(0) AS num, COUNT( CASE WHEN rc.type =1 OR rc.TYPE=4 THEN 1 END) AS practical FROM dd_rollcall rc RIGHT JOIN dd_schedule_rollcall sr ON rc.SCHEDULE_ROLLCALL_ID = sr.id LEFT JOIN dd_schedule sd ON sr.schedule_id = sd.id WHERE 1 = 1 And sd.id is not null AND rc.CLASS_ID IS NOT NULL";
        if (organId != null) {
            sql += " AND sd.ORGAN_ID =" + organId;
        }
        if (semesterId != null) {
            sql += " AND rc.SEMESTER_ID =" + semesterId;
        }
        if (classAdministrativeId != null) {
            sql += " AND rc.CLASS_ID in(" + classAdministrativeId + ")";
        }
        sql += " GROUP BY rc.CLASS_ID";
        PageData <AdministrativesDTO> page = new PageData();
        String limitSql = PaginationUtil.page(jdbcTemplate, sql, offset, limit, page);
        if (null == limitSql) {
            page.setPage(new PageDomain(0l, 0, 0, 10));
            return page;
        }
        sql = sql + limitSql;
        page.setData(jdbcTemplate.query(sql, new RowMapper() {
            @Override
            public Object mapRow(ResultSet rs, int i) throws SQLException {
                AdministrativesDTO item = new AdministrativesDTO();
                item.setSemesterId(rs.getLong("semesterId"));
                item.setSemesterName(rs.getString("semesterName"));
                item.setParticipationCount(rs.getLong("num"));
                item.setPractical(rs.getLong("practical"));
                DecimalFormat df = new DecimalFormat("######0.00");
                item.setProportion(df.format(rs.getLong("practical") * 1.0 / rs.getLong("num") * 100) + "%");
                item.setClassId(rs.getLong("CLASS_ID"));
                item.setClassName(rs.getString("class_name"));
                return item;
            }
        }));
        return page;

    }
}
