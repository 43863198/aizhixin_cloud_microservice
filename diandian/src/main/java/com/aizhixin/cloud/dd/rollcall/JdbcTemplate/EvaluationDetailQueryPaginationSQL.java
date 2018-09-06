package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;

import com.aizhixin.cloud.dd.communication.jdbcTemplate.PaginationSQL;
import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class EvaluationDetailQueryPaginationSQL implements PaginationSQL {

    public static String FIND_SQL = " SELECT                              "
            + "   dsc.`COURSE_ID` AS courseId,      "
            + "   dsc.`COURSE_NAME` AS courseName,  "
            + "   dsc.`TEACHER_NAME` AS ceTeaName,  "
            + "   CONCAT(                           "
            + "     dsc.`TEACH_DATE`,               "
            + "     ' ',                            "
            + "     dsc.`START_TIME`,               "
            + "     '~',                            "
            + "     dsc.`END_TIME`                  "
            + "   ) AS CourseTime,                  "
            + "   DATE_FORMAT(                      "
            + "     ass.created_date,               "
            + "     '%Y-%m-%d %H:%i:%s'             "
            + "   ) AS evaluationTime,              "
            + "   dsc.`CLASSROOM_NAME` AS classRom, "
            + "   ass.score AS evaluationScore,     "
            + "   ass.content AS evaluationContent  "
            + " FROM                                "
            + "   dd_assess ass                     "
            + "   LEFT JOIN dd_schedule dsc         "
            + "     ON ass.`schedule_id` = dsc.`ID` "
            + " WHERE 1 = 1 ";
    String beginTime;
    String endTime;
    Long courseId;
    Long userId;

    @Override
    public String getFindCountSql() {
        String sql = "SELECT COUNT(1) FROM(" + FIND_SQL;
        if (userId != null) {
            sql += " AND ass.student_id=" + userId;
        }
        if (courseId != null) {
            sql += " AND dsc.`COURSE_ID` =" + courseId;
        }
        if (StringUtils.isNotBlank(beginTime)) {
            sql += " AND ass.created_date >='" + beginTime + "'";
        }
        if (StringUtils.isNotBlank(endTime)) {
            sql += " AND ass.created_date <='" + endTime + "'";
        }
        sql += " )ss";
        return sql;
    }

    public String getFindSql() {
        String sql = FIND_SQL;
        if (userId != null) {
            sql += " AND ass.student_id=" + userId;
        }
        if (courseId != null) {
            sql += " AND dsc.`COURSE_ID` =" + courseId;
        }
        if (StringUtils.isNotBlank(beginTime)) {
            sql += " AND ass.created_date >='" + beginTime + "'";
        }
        if (StringUtils.isNotBlank(endTime)) {
            sql += " AND ass.created_date <='" + endTime + "'";
        }
        sql += " ORDER BY STR_TO_DATE(CONCAT(dsc.TEACH_DATE,\" \",dsc.START_TIME),'%Y-%m-%d %H:%i') DESC";
        return sql;
    }

    public List<SortDTO> sort() {
        return null;
    }


    public EvaluationDetailQueryPaginationSQL(String beginTime, String endTime, Long userId, Long courseId) {
        super();
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.courseId = courseId;
        this.userId = userId;
    }
}
