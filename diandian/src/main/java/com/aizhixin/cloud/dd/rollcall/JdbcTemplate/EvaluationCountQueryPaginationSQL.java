package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;


import com.aizhixin.cloud.dd.communication.jdbcTemplate.PaginationSQL;
import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class EvaluationCountQueryPaginationSQL implements PaginationSQL {

    public static String FIND_SQL = " SELECT                              "
            + "   dsc.COURSE_ID AS courseId,        "
            + "   dsc.COURSE_NAME AS courseName,    "
            + "   dsc.TEACHER_NAME AS courseTeacher,"
            + "   COUNT(                            "
            + "     CASE                            "
            + "       da.score                      "
            + "       WHEN 1                        "
            + "       THEN 1                        "
            + "     END) AS oneStar,                "
            + "   COUNT(                            "
            + "     CASE                            "
            + "       da.score                      "
            + "       WHEN 2                        "
            + "       THEN 1                        "
            + "     END) AS twoStar,                "
            + "   COUNT(                            "
            + "     CASE                            "
            + "       da.score                      "
            + "       WHEN 3                        "
            + "       THEN 1                        "
            + "     END) AS threeStar,              "
            + "   COUNT(                            "
            + "     CASE                            "
            + "       da.score                      "
            + "       WHEN 4                        "
            + "       THEN 1                        "
            + "     END) AS fourStar,               "
            + "   COUNT(                            "
            + "     CASE                            "
            + "       da.score                      "
            + "       WHEN 5                        "
            + "       THEN 1                        "
            + "     END) AS fiveStar                "
            + " FROM                                "
            + "   dd_assess da                      "
            + "   LEFT JOIN dd_schedule dsc         "
            + "     ON dsc.id = da.`schedule_id`    "
            + " WHERE 1 = 1                         ";
    String beginTime;
    String endTime;
    Long courseId;
    Long userId;

    @Override
    public String getFindCountSql() {
        String sql = "SELECT COUNT(1) FROM(" + FIND_SQL;
        if (userId != null) {
            sql += " AND da.student_id=" + userId;
        }
        if (courseId != null) {
            sql += " AND dsc.COURSE_ID=" + courseId;
        }
        if (StringUtils.isNotBlank(beginTime)) {
            sql += " AND da.created_date >= '" + beginTime + "' ";
        }
        if (StringUtils.isNotBlank(endTime)) {
            sql += " AND da.created_date<='" + endTime + "' ";
        }
        sql += " GROUP BY da.course_id)ss";
        return sql;
    }

    @Override
    public String getFindSql() {
        String sql = FIND_SQL;
        if (userId != null) {
            sql += " AND da.student_id=" + userId;
        }
        if (courseId != null) {
            sql += " AND dsc.`COURSE_ID`=" + courseId;
        }
        if (StringUtils.isNotBlank(beginTime)) {
            sql += " AND da.created_date>='" + beginTime + "'";
        }
        if (StringUtils.isNotBlank(endTime)) {
            sql += " AND da.created_date<='" + endTime + "'";
        }
        sql += " GROUP BY da.course_id";
        return sql;
    }

    @Override
    public List<SortDTO> sort() {
        return null;
    }


    public EvaluationCountQueryPaginationSQL(String beginTime, String endTime, Long userId, Long courseId) {
        super();
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.courseId = courseId;
        this.userId = userId;
    }
}