package com.aizhixin.cloud.dd.rollcall.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aizhixin.cloud.dd.constant.CourseRollCallConstants;
import com.aizhixin.cloud.dd.constant.ScheduleConstants;
import com.aizhixin.cloud.dd.rollcall.dto.StudentScheduleDTO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.aizhixin.cloud.dd.rollcall.dto.AttendanceDTO;
import com.aizhixin.cloud.dd.rollcall.dto.CourseScheduleDTO;

@Repository
public class ScheduleQuery {

    private static int ktime = 5;
    private final Logger log = LoggerFactory.getLogger(ScheduleQuery.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Cacheable(value = "CACHE.TEACHINGCLASSIDS", key = "#key")
    public List<StudentScheduleDTO> getStudentSignCourse(Set<Long> teachingClassIds, String key) {

        String sql = " SELECT  * FROM  ( "
                + " SELECT dsc.id,dsc.teachingclass_id,dsc.teachingclass_name,dsr.id as scheduleRollcallId,dsc.`WEEK_NAME`,dsc.`DAY_OF_WEEK`,dsc.`COURSE_NAME`,dsc.`TEACHER_NAME`,dsc.`CLASSROOM_NAME`, "
                + "  dsc.`START_TIME`,dsc.`END_TIME`,dsc.`TEACH_DATE`,dsr.`ROLL_CALL_TYPE`,dsr.localtion,dsr.course_later_time, "
                + " DATE_SUB(STR_TO_DATE(CONCAT(DATE_FORMAT(dsc.teach_date, '%Y-%m-%d'),\" \",dsc.start_time),'%Y-%m-%d%H:%i:%s'),INTERVAL 5 MINUTE "
                + "     ) AS teachBeginBeforeTime, " + "     STR_TO_DATE(CONCAT(DATE_FORMAT(dsc.teach_date, '%Y-%m-%d'),\" \",dsc.start_time),'%Y-%m-%d%H:%i:%s') AS teachBeginTime, "
                + "     STR_TO_DATE(CONCAT(DATE_FORMAT(dsc.teach_date, '%Y-%m-%d'),\" \",dsc.end_time),'%Y-%m-%d%H:%i:%s') AS teachEndTime  "
                + "   FROM dd_schedule dsc  LEFT JOIN dd_schedule_rollcall dsr ON dsr.schedule_id = dsc.id WHERE  dsr.is_open_roll_call = true "
                + "  and dsc.`TEACHINGCLASS_ID` IN (#TEACHINGCLASS_ID#) ) dd  " + "  WHERE dd.teachBeginBeforeTime <= CURRENT_TIME()  AND dd.teachEndTime >= CURRENT_TIME()";

        sql = sql.replace("#TEACHINGCLASS_ID#", StringUtils.join(teachingClassIds.toArray(), ","));

        return jdbcTemplate.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                StudentScheduleDTO item = new StudentScheduleDTO();
                item.setId(rs.getLong("id"));
                item.setScheduleId(rs.getLong("id"));
                item.setScheduleRollCallId(rs.getLong("scheduleRollcallId"));
                item.setTeachingClassId(rs.getLong("teachingclass_id"));
                item.setTeachingClassName(rs.getString("teachingclass_name"));
                item.setWeekName(rs.getString("WEEK_NAME"));
                item.setDayOfWeek(rs.getString("DAY_OF_WEEK"));
                item.setCourseName(rs.getString("COURSE_NAME"));
                item.setTeacher(rs.getString("TEACHER_NAME"));
                item.setClassRoom(rs.getString("CLASSROOM_NAME"));
                item.setClassBeginTime(rs.getString("START_TIME"));
                item.setClassEndTime(rs.getString("END_TIME"));
                item.setRollcallType(rs.getString("ROLL_CALL_TYPE"));
                item.setTeach_time(rs.getString("TEACH_DATE"));
                item.setLocaltion(rs.getString("localtion"));
                item.setLateTime(rs.getInt("course_later_time"));
                item.setRollCall(Boolean.TRUE);
                item.setInClass(Boolean.TRUE);
                item.setHaveReport(Boolean.FALSE);
                item.setDeviation(0);
                item.setType("1");
                return item;
            }
        });
    }

    //    @Cacheable(value = "CACHE.TEACHINGCLASSIDS", key = "#key")
    public List<StudentScheduleDTO> getStudentSignCourseByDate(Set<Long> teachingClassIds, String date, String key) {

        String sql = " SELECT  * FROM  ( "
                + " SELECT dsc.id,dsc.teachingclass_id,dsc.teachingclass_name,dsr.id as scheduleRollcallId,dsc.`WEEK_NAME`,dsc.`DAY_OF_WEEK`,dsc.`COURSE_NAME`,dsc.`TEACHER_NAME`,dsc.`CLASSROOM_NAME`, "
                + "  dsc.`START_TIME`,dsc.`END_TIME`,dsc.`TEACH_DATE`,dsr.`ROLL_CALL_TYPE`,dsr.localtion,dsr.course_later_time, "
                + " DATE_SUB(STR_TO_DATE(CONCAT(DATE_FORMAT(dsc.teach_date, '%Y-%m-%d'),\" \",dsc.start_time),'%Y-%m-%d%H:%i:%s'),INTERVAL 5 MINUTE "
                + "     ) AS teachBeginBeforeTime, " + "     STR_TO_DATE(CONCAT(DATE_FORMAT(dsc.teach_date, '%Y-%m-%d'),\" \",dsc.start_time),'%Y-%m-%d%H:%i:%s') AS teachBeginTime, "
                + "     STR_TO_DATE(CONCAT(DATE_FORMAT(dsc.teach_date, '%Y-%m-%d'),\" \",dsc.end_time),'%Y-%m-%d%H:%i:%s') AS teachEndTime "
                + "   FROM dd_schedule dsc  LEFT JOIN dd_schedule_rollcall dsr ON dsr.schedule_id = dsc.id WHERE  dsr.is_open_roll_call = true "
                + "  and dsc.`TEACHINGCLASS_ID` IN (#TEACHINGCLASS_ID#) ) dd  " + "  WHERE dd.teachBeginTime <= '" + date + "'  AND dd.teachEndTime >= '" + date + "'";

        sql = sql.replace("#TEACHINGCLASS_ID#", StringUtils.join(teachingClassIds.toArray(), ","));

        return jdbcTemplate.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                StudentScheduleDTO item = new StudentScheduleDTO();
                item.setId(rs.getLong("id"));
                item.setScheduleId(rs.getLong("id"));
                item.setScheduleRollCallId(rs.getLong("scheduleRollcallId"));
                item.setTeachingClassId(rs.getLong("teachingclass_id"));
                item.setTeachingClassName(rs.getString("teachingclass_name"));
                item.setWeekName(rs.getString("WEEK_NAME"));
                item.setDayOfWeek(rs.getString("DAY_OF_WEEK"));
                item.setCourseName(rs.getString("COURSE_NAME"));
                item.setTeacher(rs.getString("TEACHER_NAME"));
                item.setClassRoom(rs.getString("CLASSROOM_NAME"));
                item.setClassBeginTime(rs.getString("START_TIME"));
                item.setClassEndTime(rs.getString("END_TIME"));
                item.setRollcallType(rs.getString("ROLL_CALL_TYPE"));
                item.setTeach_time(rs.getString("TEACH_DATE"));
                item.setLocaltion(rs.getString("localtion"));
                item.setLateTime(rs.getInt("course_later_time"));
                item.setRollCall(Boolean.TRUE);
                item.setInClass(Boolean.TRUE);
                item.setHaveReport(Boolean.FALSE);
                item.setDeviation(0);
                item.setType("1");
                return item;
            }
        });
    }

    public List<StudentScheduleDTO> getStudentSignCourseByDay(Set<Long> teachingClassIds, String date) {

        String sql = " SELECT  * FROM  ( "
                + " SELECT dsc.id,dsc.teachingclass_id,dsc.teachingclass_name,dsr.id as scheduleRollcallId,dsc.`WEEK_NAME`,dsc.`DAY_OF_WEEK`,dsc.`COURSE_NAME`,dsc.`TEACHER_NAME`,dsc.`CLASSROOM_NAME`, "
                + "  dsc.`START_TIME`,dsc.`END_TIME`,dsc.`TEACH_DATE`,dsr.`ROLL_CALL_TYPE`,dsr.localtion,dsr.course_later_time, "
                + " DATE_SUB(STR_TO_DATE(CONCAT(DATE_FORMAT(dsc.teach_date, '%Y-%m-%d'),\" \",dsc.start_time),'%Y-%m-%d%H:%i:%s'),INTERVAL 5 MINUTE "
                + "     ) AS teachBeginBeforeTime, " + "     STR_TO_DATE(CONCAT(DATE_FORMAT(dsc.teach_date, '%Y-%m-%d'),\" \",dsc.start_time),'%Y-%m-%d%H:%i:%s') AS teachBeginTime, "
                + "     STR_TO_DATE(CONCAT(DATE_FORMAT(dsc.teach_date, '%Y-%m-%d'),\" \",dsc.end_time),'%Y-%m-%d%H:%i:%s') AS teachEndTime "
                + "   FROM dd_schedule dsc  LEFT JOIN dd_schedule_rollcall dsr ON dsr.schedule_id = dsc.id WHERE  dsr.is_open_roll_call = true "
                + "  and dsc.`TEACHINGCLASS_ID` IN (#TEACHINGCLASS_ID#) ) dd  " + "  WHERE dd.TEACH_DATE = '" + date + "'";

        sql = sql.replace("#TEACHINGCLASS_ID#", StringUtils.join(teachingClassIds.toArray(), ","));

        return jdbcTemplate.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                StudentScheduleDTO item = new StudentScheduleDTO();
                item.setId(rs.getLong("id"));
                item.setScheduleId(rs.getLong("id"));
                item.setScheduleRollCallId(rs.getLong("scheduleRollcallId"));
                item.setTeachingClassId(rs.getLong("teachingclass_id"));
                item.setTeachingClassName(rs.getString("teachingclass_name"));
                item.setWeekName(rs.getString("WEEK_NAME"));
                item.setDayOfWeek(rs.getString("DAY_OF_WEEK"));
                item.setCourseName(rs.getString("COURSE_NAME"));
                item.setTeacher(rs.getString("TEACHER_NAME"));
                item.setClassRoom(rs.getString("CLASSROOM_NAME"));
                item.setClassBeginTime(rs.getString("START_TIME"));
                item.setClassEndTime(rs.getString("END_TIME"));
                item.setRollcallType(rs.getString("ROLL_CALL_TYPE"));
                item.setTeach_time(rs.getString("TEACH_DATE"));
                item.setLocaltion(rs.getString("localtion"));
                item.setLateTime(rs.getInt("course_later_time"));
                item.setRollCall(Boolean.TRUE);
                item.setInClass(Boolean.TRUE);
                item.setHaveReport(Boolean.FALSE);
                item.setDeviation(0);
                item.setType("1");
                return item;
            }
        });
    }

    public List<Map<String, Object>> queryUnOuntSchedule(String start, String end, String teachDay) {
        String sql = "SELECT dsr.ID schedulerollcallid,ds.ID scheduleid FROM dd_schedule_rollcall dsr LEFT JOIN dd_schedule ds ON ds.ID=dsr.SCHEDULE_ID WHERE dsr.DELETE_FLAG=0 AND ds.DELETE_FLAG=0 AND dsr.IS_IN_CLASSROOM=1 AND dsr.IS_OPEN_ROLL_CALL=1 AND ds.TEACH_DATE='" + teachDay + "' AND CONCAT(ds.TEACH_DATE,' ',ds.END_TIME)>='" + start + "' AND CONCAT(ds.TEACH_DATE,' ',ds.END_TIME)<='" + end + "'";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        return result;
    }

    public List<CourseScheduleDTO> queryScheduleByStartAndEndTime(String beginTime, String endTime, String teachTime, Boolean isBefore) {

        String sql
                = " SELECT * FROM ( SELECT   dsc.`ID`,  dsc.`TEACHER_ID`,  dsc.`COURSE_ID`,dco.ISOPEN,dsr.id AS scheduleRollcallId,dsr.`IS_OPEN_ROLL_CALL`, dsc.teach_date,dsc.start_time,dsr.course_later_time,"
                + " DATE_SUB(STR_TO_DATE(CONCAT(DATE_FORMAT(dsc.teach_date,'%Y-%m-%d'),\" \",dsc.start_time),'%Y-%m-%d%H:%i:%s'),INTERVAL 5 MINUTE) AS teachBeginBeforeTime, "
                + " STR_TO_DATE(CONCAT(DATE_FORMAT(dsc.teach_date, '%Y-%m-%d'),\" \",dsc.start_time),'%Y-%m-%d%H:%i:%s') AS teachBeginTime, "
                + " STR_TO_DATE(CONCAT(DATE_FORMAT(dsc.teach_date, '%Y-%m-%d'),\" \",dsc.end_time),'%Y-%m-%d%H:%i:%s') AS teachEndTime  "
                + " FROM  dd_schedule dsc   LEFT JOIN dd_courserollcall dco     ON dco.TEACHER_ID = dsc.TEACHER_ID     AND dco.COURSE_ID = dsc.COURSE_ID LEFT JOIN dd_schedule_rollcall dsr ON dsr.schedule_id = dsc.id ) dsp WHERE 1  ";

        if (StringUtils.isBlank(teachTime)) {
            sql += " and dsp.teach_date = CURDATE() ";
        } else {
            sql += " and dsp.teach_date = ' " + teachTime + "'";
        }
        if (isBefore) {
            if (beginTime != null) {
                sql += "AND dsp.teachBeginBeforeTime >= '" + beginTime + "'";
            }
            if (endTime != null) {
                sql += "AND dsp.teachBeginBeforeTime < '" + endTime + "'";
            }
        } else {
            if (beginTime != null) {
                sql += "AND dsp.teachEndTime >= '" + beginTime + "'";
            }
            if (endTime != null) {
                sql += "AND dsp.teachEndTime < '" + endTime + "'";
            }
        }
        log.info("扫描下课sql:" + sql);
        return jdbcTemplate.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                CourseScheduleDTO item = new CourseScheduleDTO();
                item.setScheduleId(rs.getLong("id"));
                item.setTeachBeginTime(rs.getString("teachBeginTime"));
                item.setTeachEndTime(rs.getString("teachEndTime"));
                item.setTeachBeginBeforeTime(rs.getString("teachBeginBeforeTime"));
                item.setBeginTime(rs.getString("start_time"));
                item.setLateTime(rs.getInt("course_later_time"));
                item.setIsOpen(rs.getString("ISOPEN"));
                item.setIsRollCall(rs.getBoolean("IS_OPEN_ROLL_CALL"));
                return item;
            }
        });
    }

    public List<Long> queryIdByTeacherIdAndCurrentTime(Long teacherId) {

        String sql = " SELECT	ddd.scheduleRollCallId FROM " + " (SELECT	dsc.id as scheduleId,	dsr.id as scheduleRollCallId,dsc.TEACHER_ID,"
                + " STR_TO_DATE(CONCAT(DATE_FORMAT(dsc.teach_date, '%Y-%m-%d'),\" \",dsc.end_time),'%Y-%m-%d%H:%i:%s') AS teachEndTime,"
                + " DATE_SUB(STR_TO_DATE(CONCAT(DATE_FORMAT(dsc.teach_date, '%Y-%m-%d'),\" \",dsc.start_time),'%Y-%m-%d%H:%i:%s'),"
                + " INTERVAL 5 MINUTE			) AS teachBeginBeforeTime"
                + " 		FROM			dd_schedule dsc		LEFT JOIN dd_schedule_rollcall dsr ON dsr.SCHEDULE_ID = dsc.id where dsc.teacher_id = #teacherId# "
                + " 	) ddd WHERE	teachEndTime > CURRENT_TIMESTAMP () AND CURRENT_TIMESTAMP > teachBeginBeforeTime ";

        if (null != teacherId) {
            sql = sql.replace("#teacherId#", teacherId.toString());
        } else {
            return null;
        }

        return jdbcTemplate.queryForList(sql, Long.class);
    }
}
