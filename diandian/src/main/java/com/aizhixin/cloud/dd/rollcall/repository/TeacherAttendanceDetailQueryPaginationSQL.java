package com.aizhixin.cloud.dd.rollcall.repository;

import com.aizhixin.cloud.dd.rollcall.dto.AttendanceDetailDTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class TeacherAttendanceDetailQueryPaginationSQL {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<AttendanceDetailDTO> query(String teacherId, String courseId) {

        String sql = "  SELECT   * FROM                                 "
                + "   (SELECT                                        "
                + "     ds.`id` AS scheduleId,                       "
                + "     ds.`teacher_id`,                             "
                + "     ds.`course_id`,ds.`PERIOD_ID`,                              "
                + "     dr.`student_id`,                             "
                + "     dr.`STUDENT_NAME` AS studentName,            "
                + "     dr.`class_name` AS className,                "
                + "     dr.student_num as person_id,                              "
                + "     dr.`type`,                                   "
                + "    CONCAT(ds.teach_date, ' ', ds.START_TIME) AS sortTime "
                + "   FROM                                           "
                + "     dd_schedule ds                               "
                + "     LEFT JOIN dd_schedule_rollcall dsr           "
                + "       ON dsr.`SCHEDULE_ID` = ds.`ID`             "
                + "     LEFT JOIN dd_rollcall dr   ON dr.`SCHEDULE_ROLLCALL_ID` = dsr.`ID`                  "
                + "   WHERE ds.`teacher_id` = #teacherId#            "
                + "     AND ds.`course_id` = #courseId#              "
                + "     ) das "
                + " WHERE das.sortTime < CURRENT_TIMESTAMP()          "
                + " ORDER BY das.scheduleId,                          "
                + "   das.period_id                                   ";

        if (StringUtils.isNotBlank(teacherId)) {
            sql = sql.replaceAll("#teacherId#", teacherId);
        } else {
            sql = sql.replaceAll("#teacherId#", "0");

        }

        if (StringUtils.isNotBlank(courseId)) {
            sql = sql.replaceAll("#courseId#", courseId);
        } else {
            sql = sql.replaceAll("#courseId#", "0");
        }

        return jdbcTemplate.query(sql, new RowMapper() {

            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                AttendanceDetailDTO item = new AttendanceDetailDTO();
                item.setScheduleId(rs.getLong("scheduleId"));
                item.setTeacherId(rs.getLong("teacher_id"));
                item.setCourseId(rs.getLong("course_id"));
                item.setStudentName(rs.getString("studentName"));
                item.setPersonId(rs.getString("person_id"));
                item.setStudentId(rs.getLong("student_id"));
                item.setType(rs.getString("type"));
                item.setClassName(rs.getString("className"));
                return item;
            }
        });
    }

    public List<Long> queryAllSortScheduleId(String teacherId, String courseId) {

        String sql = " SELECT   ddd.scheduleId    FROM    (SELECT      "
                + "     ds.`id` AS scheduleId,                        "
                + "     CONCAT(                                       "
                + "       ds.teach_date,                            "
                + "       ' ',                                      "
                + "       ds.START_TIME                         "
                + "     ) AS sortTime                                 "
                + "   FROM                                            "
                + "     dd_schedule ds                                "
                + "     LEFT JOIN  dd_schedule_rollcall dsr                         "
                + "       ON dsr.`SCHEDULE_ID` = ds.`ID`                  "
                + "     LEFT JOIN dd_rollcall dr                      "
                + "       ON dr.`SCHEDULE_ROLLCALL_ID` = dsr.`ID`    WHERE ds.`teacher_id` = #teacherId# "
                + "     AND ds.`course_id` = #courseId#   GROUP BY ds.`id`) ddd  WHERE ddd.sortTime < CURRENT_TIMESTAMP() ORDER BY ddd.sortTime asc ";

        if (StringUtils.isNotBlank(teacherId)) {
            sql = sql.replaceAll("#teacherId#", teacherId);
        } else {
            sql = sql.replaceAll("#teacherId#", "0");

        }

        if (StringUtils.isNotBlank(courseId)) {
            sql = sql.replaceAll("#courseId#", courseId);
        } else {
            sql = sql.replaceAll("#courseId#", "0");
        }
        return jdbcTemplate.queryForList(sql, Long.class);
    }
}
