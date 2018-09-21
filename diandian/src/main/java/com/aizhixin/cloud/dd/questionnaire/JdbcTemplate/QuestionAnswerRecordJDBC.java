package com.aizhixin.cloud.dd.questionnaire.JdbcTemplate;

import com.aizhixin.cloud.dd.questionnaire.entity.QuestionAnswerRecord;
import com.aizhixin.cloud.dd.questionnaire.entity.QuestionnaireAssginStudents;
import com.aizhixin.cloud.dd.questionnaire.entity.Questions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class QuestionAnswerRecordJDBC {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<QuestionAnswerRecord> findByQuestionnaireAssginStudentsId(Long id){
        String sql = "SELECT * FROM dd_question_answer_record WHERE QUESTIONNAIRE_ASSGIN_STUDENTS_ID='" + id + "'";
        RowMapper<QuestionAnswerRecord> rowMapper = new RowMapper<QuestionAnswerRecord>() {
            @Override
            public QuestionAnswerRecord mapRow(ResultSet rs, int arg1) throws SQLException {
                QuestionAnswerRecord qd = new QuestionAnswerRecord();
                qd.setId(rs.getLong("ID"));
                QuestionnaireAssginStudents qas = new QuestionnaireAssginStudents();
                qas.setId(rs.getLong("QUESTIONNAIRE_ASSGIN_STUDENTS_ID"));
                qd.setQuestionnaireAssginStudents(qas);
                Questions q = new Questions();
                q.setId(rs.getLong("QUESTIONS_ID"));
                qd.setQuestions(q);
                qd.setScore(rs.getFloat("SCORE"));
                qd.setAnswer(rs.getString("answer"));
                qd.setWeightScore(rs.getFloat("weight_score"));
                return qd;
            }
        };
        return jdbcTemplate.query(sql, rowMapper);
    }
}
