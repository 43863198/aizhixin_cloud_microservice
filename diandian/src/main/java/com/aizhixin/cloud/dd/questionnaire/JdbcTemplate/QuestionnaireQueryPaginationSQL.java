package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;

import com.aizhixin.cloud.dd.communication.jdbcTemplate.PaginationSQL;
import com.aizhixin.cloud.dd.constant.QuestionnaireStatus;
import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class QuestionnaireQueryPaginationSQL implements PaginationSQL {

    public static String FIND_SQL       = "select dq.ID,dq.name,dq.TOTAL_QUESTIONS,dq.created_date,dq.status from dd_questionnaire dq where 1 ";

    public static String FIND_COUNT_SQL = "select count(*) from dd_questionnaire dq where 1 ";
    @Getter@Setter
    private String       questionnaireName;
    @Getter@Setter
    private String       startDate;
    @Getter@Setter
    private String       endDate;
    @Getter@Setter
    private Long         organId;

    @Override
    public String getFindCountSql() {

        String sql = FIND_COUNT_SQL;
        if (questionnaireName != null) {
            sql += " and dq.name like '%" + questionnaireName + "%'";
        }
        if (startDate != null) {
            sql += " and dq.created_date > '" + startDate + "'";
        }
        if (endDate != null) {
            sql += " and dq.created_date < '" + endDate + "'";
        }
        sql += "and dq.delete_flag = "+ QuestionnaireStatus.QUESTION_DELETEFLAG_NORMAL;
        sql += " and dq.organ_id = " + organId;
        return sql;
    }

    @Override
    public String getFindSql() {

        String sql = FIND_SQL;
        if (questionnaireName != null) {
            sql += " and dq.name like '%" + questionnaireName + "%'";
        }
        if (startDate != null) {
            sql += " and dq.created_date > '" + startDate + "'";
        }
        if (endDate != null) {
            sql += " and dq.created_date < '" + endDate + "'";
        }
        sql += "and dq.delete_flag = "+ QuestionnaireStatus.QUESTION_DELETEFLAG_NORMAL;
        sql += " and dq.organ_id = " + organId;
        return sql;
    }

    @Override
    public List<SortDTO> sort() {
        List<SortDTO> list = new ArrayList<SortDTO>();
        SortDTO dto = new SortDTO();
        dto.setAsc(false);
        dto.setKey("dq.last_modified_date");
        list.add(dto);

        return list;
    }


    public QuestionnaireQueryPaginationSQL(String questionnaireName, String startDate, String endDate, Long organId) {
        super();
        this.questionnaireName = questionnaireName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.organId = organId;
    }

}
