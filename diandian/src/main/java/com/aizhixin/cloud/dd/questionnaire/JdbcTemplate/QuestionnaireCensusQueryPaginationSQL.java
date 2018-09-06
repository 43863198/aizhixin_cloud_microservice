package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;

import com.aizhixin.cloud.dd.communication.jdbcTemplate.PaginationSQL;
import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


public class QuestionnaireCensusQueryPaginationSQL implements PaginationSQL {

    public static String FIND_SQL       = "SELECT du.ID, dq.ID as QUESTIONNAIRECENSUSDTO,   du.name,    dq.TEACHER_ID,  dq.TEACHER_NAME,    dq.COURSE_ID,   dq.COURSE_NAME, dq.COURSE_CODE,  dq.END_DATE FROM    dd_questionnaire_assgin dq LEFT JOIN dd_questionnaire du ON dq.QUESTIONNAIRE_ID = du.ID where du.ID is not null and dq.delete_flag= '0' and du.delete_flag= '0' ";

    public static String FIND_COUNT_SQL = "SELECT count(*) FROM    dd_questionnaire_assgin dq LEFT JOIN dd_questionnaire du ON dq.QUESTIONNAIRE_ID = du.ID  where du.ID is not null and dq.delete_flag= '0' and du.delete_flag= '0' ";
    @Getter@Setter
    private String       questionnaireName;
    @Getter@Setter
    private Long         organId;
    @SuppressWarnings("unchecked")
	@Override
    public String getFindCountSql() {

        String sql = FIND_COUNT_SQL;

        if (questionnaireName != null) {
            sql += " and du.name like '%" + questionnaireName + "%'";
        }
        
        sql += " and du.organ_id = " + organId;
        return sql;
    }

    @Override
    public String getFindSql() {

        String sql = FIND_SQL;
        if (questionnaireName != null) {
            sql += " and du.name like '%" + questionnaireName + "%'";
        }
       
        sql += " and du.organ_id = " + organId;
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


    public QuestionnaireCensusQueryPaginationSQL(String questionnaireName,
            Long organId) {
        super();
        this.questionnaireName = questionnaireName;
        this.organId = organId;
    }

}
