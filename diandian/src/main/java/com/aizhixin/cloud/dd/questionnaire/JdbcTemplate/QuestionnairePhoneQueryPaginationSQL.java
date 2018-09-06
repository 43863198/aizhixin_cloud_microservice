package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;

import com.aizhixin.cloud.dd.communication.jdbcTemplate.PaginationSQL;
import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class QuestionnairePhoneQueryPaginationSQL implements PaginationSQL {

    public static String FIND_SQL       = "SELECT   dqas.ID as dqasId,dqa.Id as dqaId,dq.id as dqId ,dqa.COURSE_ID, dqa.TEACHER_ID,   dqa.COURSE_NAME,    dqa.COURSE_CODE,    dqa.TEACHER_NAME,  dq. NAME,   dqa.END_DATE,dqas.created_date,   dqas.created_by , dqa.created_by as adm, dqa.delete_flag FROM    dd_questionnaire_assgin_students dqas "
                                                + " LEFT JOIN dd_questionnaire_assgin dqa ON dqas.QUESTIONNAIRE_ASSGIN_ID = dqa.ID LEFT JOIN dd_questionnaire dq ON dqa.QUESTIONNAIRE_ID = dq.ID where 1 ";
    public static String FIND_COUNT_SQL = " SELECT count(*) FROM    dd_questionnaire_assgin_students dqas "
                                                + " LEFT JOIN dd_questionnaire_assgin dqa ON dqas.QUESTIONNAIRE_ASSGIN_ID = dqa.ID LEFT JOIN dd_questionnaire dq ON dqa.QUESTIONNAIRE_ID = dq.ID where 1 ";
    @Getter@Setter
    private Long         studentId;
    @Getter@Setter
    private Long         status;

    @Override
    public String getFindCountSql() {

        String sql = FIND_COUNT_SQL;
        sql += " and dqas.STUDENT_ID =" + studentId;
        sql += " and dqas.`status` =" + status;
        return sql;
    }

    @Override
    public String getFindSql() {

        String sql = FIND_SQL;
        sql += " and dqas.STUDENT_ID =" + studentId;
        sql += " and dqas.`status` =" + status;
        sql +=" and dqa.delete_flag='0'";
        return sql;
    }

    @Override
    public List<SortDTO> sort() {

        List<SortDTO> list = new ArrayList<SortDTO>();
        SortDTO dto = new SortDTO();
        dto.setAsc(false);
        dto.setKey("dqas.last_modified_date");
        list.add(dto);

        return list;
    }

    public QuestionnairePhoneQueryPaginationSQL(Long studentId, Long status) {
        super();
        this.studentId = studentId;
        this.status = status;
    }

}
