package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;


import com.aizhixin.cloud.dd.communication.jdbcTemplate.PaginationSQL;
import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StudentAttendanceDetailQueryPaginationSQL implements PaginationSQL {

    public static String FIND_SQL = " SELECT                                        "
            + "   dro.student_id,                             "
            + "   dro.class_name AS className,                "
            + "   dsc.COURSE_NAME AS courseName,              "
            + "   dsc.TEACHER_NAME AS teachName,              "
            + "   CONCAT(                                     "
            + "     dsc.`TEACH_DATE`,                         "
            + "     \" \",                                      "
            + "     dsc.START_TIME,                           "
            + "     \"~\",                                      "
            + "     dsc.`END_TIME`                            "
            + "   ) AS teachTime,                             "
            + "   STR_TO_DATE(                                "
            + "     CONCAT(                                   "
            + "       dsc.TEACH_DATE,                         "
            + "       \" \",                                    "
            + "       dsc.START_TIME,                         "
            + "       ':00'                                   "
            + "     ),                                        "
            + "     '%Y-%m-%d %H:%i:%s'                       "
            + "   ) AS sortTime,                              "
            + "   dsc.CLASSROOM_NAME AS classRoomName,        "
            + "   IF(dro.type = 7, 2, dro.type) AS TYPE,      "
            + "   dro.`SIGN_TIME` AS SIGNTIME,                            "
            + "   dro.`GPS_DETAIL`                            "
            + " FROM                                          "
            + "   dd_rollcall dro                             "
            + "   LEFT JOIN dd_schedule_rollcall dsr          "
            + "     ON dsr.`ID` = dro.`SCHEDULE_ROLLCALL_ID`  "
            + "   LEFT JOIN dd_schedule dsc                   "
            + "     ON dsr.`schedule_id` = dsc.`id`           "
            + " WHERE dro.`type` IN ('5', '4', '3', '2', '1') ";

    public static String FIND_COUNT_SQL = " SELECT   count(*)           FROM      "
            + "   dd_rollcall dro                             "
            + "   LEFT JOIN dd_schedule_rollcall dsr          "
            + "     ON dsr.`ID` = dro.`SCHEDULE_ROLLCALL_ID`  "
            + "   LEFT JOIN dd_schedule dsc                   "
            + "     ON dsr.`schedule_id` = dsc.`id`           "
            + " WHERE dro.`type` IN ('5', '4', '3', '2', '1') ";

    @Override
    public String getFindCountSql() {

        String sql = FIND_COUNT_SQL;
        if (null != studentId) {
            sql += " and dro.student_id = " + studentId;
        }
        if (StringUtils.isNotBlank(type)) {
            sql += " and dro.type = " + type;
        }
        if (StringUtils.isNotBlank(beginTime)) {
            sql += " and dro.last_modified_date >= '" + beginTime + "' ";
        }
        if (StringUtils.isNotBlank(endTime)) {
            sql += " and dro.last_modified_date <= '" + endTime + "'";
        }
        if (courseIds.size() == 1) {
            sql += " and dro.course_id = " + courseIds.iterator().next();
        } else if (courseIds.size() > 1) {
            StringBuilder temp = new StringBuilder();
            for (Long val : courseIds) {
                temp.append(val);
                temp.append(",");
            }
            String t = temp.toString().substring(0, temp.toString().length() - 1);
            sql += " and dro.course_id in (" + t + ")";
        }
        return sql;
    }

    @Override
    public String getFindSql() {
        String sql = FIND_SQL;
        if (null != studentId) {
            sql += " and dro.student_id = " + studentId;
        }
        if (StringUtils.isNotBlank(type)) {
            sql += " and dro.type = " + type;
        }
        if (StringUtils.isNotBlank(beginTime)) {
            sql += " and dro.last_modified_date >= '" + beginTime + "' ";
        }
        if (StringUtils.isNotBlank(endTime)) {
            sql += " and dro.last_modified_date <= '" + endTime + "' ";
        }
        if (courseIds.size() == 1) {
            sql += " and dro.course_id = " + courseIds.iterator().next();
        } else if (courseIds.size() > 1) {
            StringBuilder temp = new StringBuilder();
            for (Long val : courseIds) {
                temp.append(val);
                temp.append(",");
            }
            String t = temp.toString().substring(0, temp.toString().length() - 1);
            sql += " and dro.course_id in (" + t + ")";
        }

        sql += " ORDER BY sortTime DESC ";
        return sql;
    }

    Set<Long> courseIds;
    String beginTime;
    String endTime;
    String type;
    Long studentId;

    @Override
    public List<SortDTO> sort() {

        List<SortDTO> list = new ArrayList<SortDTO>();
        SortDTO dto = new SortDTO();
        // dto.setAsc(false);
        // dto.setKey("dro.`last_modified_date`");
        // list.add(dto);
        return list;
    }

    public StudentAttendanceDetailQueryPaginationSQL(Set<Long> courseIds, String beginTime, String endTime,
                                                     String type, Long studentId) {
        super();
        this.courseIds = courseIds;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.type = type;
        this.studentId = studentId;
    }
}