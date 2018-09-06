package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import com.aizhixin.cloud.dd.communication.jdbcTemplate.PaginationSQL;
import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;

import lombok.Getter;
import lombok.Setter;

public class CourseAssessOneQueryPaginationSQL implements PaginationSQL {

    private static String FIND_SQL = "SELECT "
            + "dd.`COURSE_NAME`, "
            + "dd.`teachingclass_code`, "
            + "dd.`TEACHINGCLASS_NAME`, "
            + " dro.`class_name` ,"
            + "dd.`TEACH_DATE`, "
            + "dd.`PERIOD_NO`, "
            + "dd.`PERIOD_NUM`, "
            + "ass.`score`, "
            + "ass.`CREATED_DATE`, "
            + "ass.`content`, "
            + "dd.`SEMESTER_NAME` "
            + "FROM dd_schedule dd RIGHT JOIN dd_assess ass "
            + " ON dd.`ID` = ass.`schedule_id` LEFT JOIN dd_schedule_rollcall dr     ON dr.schedule_id = dd.id " +
            " LEFT JOIN dd_rollcall dro     ON dro.`SCHEDULE_ROLLCALL_ID` = dr.`ID`     AND ass.`student_id` = dro.`STUDENT_ID` WHERE 1=1 AND ass.`DELETE_FLAG` = 0 ";

    public static String FIND_COUNT_SQL = "SELECT "
            + "COUNT(1) "
            + "FROM dd_schedule dd RIGHT JOIN dd_assess ass "
            + "ON dd.`ID` = ass.`schedule_id` WHERE 1=1 AND ass.`DELETE_FLAG` = 0 ";
    @Getter
    @Setter
    private Long scheduleId;// 排课id

    public CourseAssessOneQueryPaginationSQL(Long scheduleId) {
        super();
        this.scheduleId = scheduleId;
    }

    @Override
    public String getFindCountSql() {
        String sql = FIND_COUNT_SQL;
        if (scheduleId != null && !"".equals(scheduleId)) {
            sql += "AND ass.`schedule_id` = '" + scheduleId + "' ";
        } else {
            sql += "";
        }
        return sql;
    }

    @Override
    public String getFindSql() {
        String sql = FIND_SQL;
        if (scheduleId != null && !"".equals(scheduleId)) {
            sql += "AND ass.`schedule_id` = '" + scheduleId + "' ";
        } else {
            sql += "";
        }
        return sql;
    }

    @Override
    public List <SortDTO> sort() {
        List <SortDTO> list = new ArrayList <SortDTO>();
        SortDTO dto = new SortDTO();
        dto.setAsc(false);
        dto.setKey("ass.`CREATED_DATE`");
        list.add(dto);
        return list;
    }

}
