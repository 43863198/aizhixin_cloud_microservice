package com.aizhixin.cloud.dd.questionnaire.JdbcTemplate;

import com.aizhixin.cloud.dd.communication.jdbcTemplate.PaginationJDBCTemplate;
import com.aizhixin.cloud.dd.questionnaire.dto.QuestionnaireDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class QuestionnaireQueryJdbcTemplate extends
        PaginationJDBCTemplate<QuestionnaireDTO> {

    public static final RowMapper<QuestionnaireDTO> questionnaireMapper = new RowMapper<QuestionnaireDTO>() {
        public QuestionnaireDTO mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            QuestionnaireDTO questionnaireDTO = new QuestionnaireDTO();
            questionnaireDTO.setId(rs.getLong("id"));
            questionnaireDTO.setName(rs.getString("name"));
            questionnaireDTO.setTotalQuestions(rs.getInt("TOTAL_QUESTIONS"));
            questionnaireDTO.setCreateDate(rs.getTimestamp("created_date"));
//		    questionnaireDTO.setStatus(rs.getString("status"));
            return questionnaireDTO;
        }
    };
}
