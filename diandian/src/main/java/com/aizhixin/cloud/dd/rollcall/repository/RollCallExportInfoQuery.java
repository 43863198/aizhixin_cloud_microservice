package com.aizhixin.cloud.dd.rollcall.repository;

import com.aizhixin.cloud.dd.rollcall.dto.CourseScheduleDTO;
import com.aizhixin.cloud.dd.rollcall.dto.RollCallExport;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Repository
public class RollCallExportInfoQuery {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<RollCallExport> queryRollCall(
            Long teacherId, String beginTime, String endTime, Long courseId,
            boolean isHeadTeacher, String studentIds) {

        String sql = " SELECT                                  "
                + "   dr.STUDENT_ID AS studentId,           "
                + "   dr.student_num,                       "
                + "   dr.STUDENT_NAME AS studentName,       "
                + "   dr.class_id,                          "
                + "   dr.class_name AS className,           "
                + "   ds.course_id,                         "
                + "   ds.course_id,                         "
                + "   ds.COURSE_NAME AS courseName,         "
                + "   ds.`SEMESTER_NAME`  AS seName,        "
                + "   SUM(                                  "
                + "     CASE                                "
                + "       WHEN dr.type = 1                  "
                + "       THEN 1                            "
                + "       ELSE 0                            "
                + "     END) AS normal,                     "
                + "   SUM(                                  "
                + "     CASE                                "
                + "       WHEN dr.type = 4                  "
                + "       THEN 1                            "
                + "       ELSE 0                            "
                + "     END) AS ASKFORLEAVE,                "
                + "   SUM(                                  "
                + "     CASE                                "
                + "       WHEN dr.type = 3                  "
                + "       THEN 1                            "
                + "       ELSE 0                            "
                + "     END) AS late,                       "
                + "   SUM(                                  "
                + "     CASE                                "
                + "       WHEN dr.type = 2                  "
                + "       THEN 1                            "
                + "       ELSE 0                            "
                + "     END) AS truancy,                    "
                + "   SUM(                                  "
                + "     CASE                                "
                + "       WHEN dr.type = 5                  "
                + "       THEN 1                            "
                + "       ELSE 0                            "
                + "     END) AS typeLeave,                  "
                + "   dr.`type`                             "
                + " FROM                                    "
                + "   dd_rollcall dr                        "
                + "   LEFT JOIN dd_schedule_rollcall dsr    "
                + "     ON dsr.ID = dr.SCHEDULE_ROLLCALL_ID "
                + "   LEFT JOIN dd_schedule ds      "
                + "     ON ds.id = dsr.SCHEDULE_ID  "
                + "  WHERE  #teacherId#  #courseId# #beginTime# #endTime#  "
                + " GROUP BY dr.course_id,          "
                + "   dr.class_id,                  "
                + "   dr.student_id                 ";

        if (!isHeadTeacher) {
            if (teacherId != null) {
                sql = sql.replace("#teacherId#", " dr.`teacher_id` =" + teacherId.toString());
            } else {
                sql = sql.replace("#teacherId#", " ");
            }
        } else {
            if (teacherId != null) {
                sql = sql.replace("#teacherId#", " dr.student_id in (" + studentIds + ")");
            } else {
                sql = sql.replace("#teacherId#", " ");
            }
        }
        if (StringUtils.isNotBlank(beginTime)) {
            sql = sql.replace("#beginTime#", " AND dsr.CREATED_DATE >= '" + beginTime + "'");
        } else {
            sql = sql.replace("#beginTime#", " ");
        }
        if (StringUtils.isNotBlank(endTime)) {
            sql = sql.replace("#endTime#", " and dsr.CREATED_DATE <= '" + endTime + "'");
        } else {
            sql = sql.replace("#endTime#", " ");
        }
        if (courseId != null) {
            sql = sql.replace("#courseId#", " and dr.`course_id` =" + courseId);
        } else {
            sql = sql.replace("#courseId#", " ");
        }

        return jdbcTemplate.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                RollCallExport item = new RollCallExport();
                item.setPersonId(rs.getString("student_num"));
                item.setStudentName(rs.getString("studentName"));
                item.setClassName(rs.getString("className"));
                item.setCourseName(rs.getString("courseName"));
                item.setSemeterName(rs.getString("seName"));
                item.setNormal(rs.getInt("normal"));
                item.setTruancy(rs.getInt("truancy"));
                item.setLate(rs.getInt("late"));
                item.setAskforlevae(rs.getInt("askforleave"));
                item.setLeave(rs.getInt("typeLeave"));
                item.setStudentId(rs.getLong("studentId"));
                return item;
            }
        });
    }
}
