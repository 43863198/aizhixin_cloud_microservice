package com.aizhixin.cloud.dd.questionnaire.JdbcTemplate;

import com.aizhixin.cloud.dd.communication.jdbcTemplate.PaginationSQL;
import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class QuestionnaireCensusDetailClassInfoQueryPaginationSQL implements PaginationSQL {

    public static String FIND_SQL       = "select dqas.classes_name as className ,CLASSES_ID as classId ,count(*) as totalCount from dd_questionnaire_assgin_students dqas LEFT JOIN dd_questionnaire_assgin dqa on dqas.QUESTIONNAIRE_ASSGIN_ID = dqa.ID where 1 ";

    public static String FIND_COUNT_SQL = "select count(*) from dd_questionnaire_assgin_students dqas LEFT JOIN dd_questionnaire_assgin dqa on dqas.QUESTIONNAIRE_ASSGIN_ID = dqa.ID where 1 ";
    @Getter@Setter
    private Long         questionnaireId;
    @Getter@Setter
    private Long         classId;
    @Getter@Setter
    private Long         organId;

    @Override
    public String getFindCountSql() {

        String sql = FIND_COUNT_SQL;
        if (questionnaireId != null) {
            sql += " and dqa.id  = " + questionnaireId;
        }
        if (classId != null) {
            sql += " and dqas.classes_id =" + classId;
        }
        sql += " GROUP BY dqas.CLASSES_ID,dqas.CLASSES_NAME ";
        sql = "select count(*) from (" + sql + ") b";
        return sql;
    }

    @Override
    public String getFindSql() {

        String sql = FIND_SQL;
        if (questionnaireId != null) {
            sql += " and dqa.id  = " + questionnaireId;
        }
        if (classId != null) {
            sql += " and dqas.classes_id =" + classId;
        }
        sql += " GROUP BY dqas.CLASSES_ID,dqas.CLASSES_NAME";
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

    public QuestionnaireCensusDetailClassInfoQueryPaginationSQL(Long questionnaireId, Long classId) {
        super();
        this.questionnaireId = questionnaireId;
        this.classId = classId;
    }

}
