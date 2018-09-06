package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;

import com.aizhixin.cloud.dd.communication.jdbcTemplate.PaginationSQL;
import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class QuestionnaireCensusDetailScoreQueryPaginationSQL implements PaginationSQL {

    public static String FIND_SQL       = "SELECT count(*) as subAllPeoCount,sum(IFNULL(score,0)) as totalScore from dd_questionnaire_assgin_students dqas LEFT JOIN dd_questionnaire_assgin dqa on dqas.QUESTIONNAIRE_ASSGIN_ID = dqa.ID where 1 ";

    public static String FIND_COUNT_SQL = "SELECT count(*) as submitAllPeoCount,sum(IFNULL(score,0)) as totalScore from dd_questionnaire_assgin_students dqas LEFT JOIN dd_questionnaire_assgin dqa on dqas.QUESTIONNAIRE_ASSGIN_ID = dqa.ID where 1 ";
    @Getter@Setter
    private Long         questionnaireAssginId;
    @Getter@Setter
    private Long         classId;
    @Getter@Setter
    private int          status;

    @Override
    public String getFindCountSql() {

    	
        String sql = FIND_COUNT_SQL;
        if (questionnaireAssginId != null) {
            sql += " and dqa.id  = " + questionnaireAssginId;
        } 
        if (classId != null) {
            sql += " and dqas.classes_id =" + classId;
        }
        sql += " and dqas.`STATUS` =  " + status;
        sql = " select count(*) from ("+sql+") b";
        return sql;
    }

    @Override
    public String getFindSql() {

        String sql = FIND_SQL;
        if (questionnaireAssginId != null) {
            sql += " and dqa.id  = " + questionnaireAssginId;
        } 
        if (classId != null) {
            sql += " and dqas.classes_id =" + classId;
        }
        sql += " and dqas.`STATUS` =  " + status;
        return sql;
    }

    @Override
    public List<SortDTO> sort() {

        List<SortDTO> list = new ArrayList<SortDTO>();
        SortDTO dto = new SortDTO();
        dto.setAsc(true);
        dto.setKey("dqas.last_modified_date");
        list.add(dto);

        return list;
    }
    public QuestionnaireCensusDetailScoreQueryPaginationSQL(Long questionnaireAssginId, Long classId, int status) {
        super();
        this.questionnaireAssginId = questionnaireAssginId;
        this.classId = classId;
        this.status = status;
    }

}
