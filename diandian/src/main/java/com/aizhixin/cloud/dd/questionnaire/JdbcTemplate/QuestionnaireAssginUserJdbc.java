package com.aizhixin.cloud.dd.questionnaire.JdbcTemplate;

import com.aizhixin.cloud.dd.questionnaire.dto.QuestionnaireCensusDTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Repository
public class QuestionnaireAssginUserJdbc {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void deleteByNameAndTeacherType(Long quesId, String name, Integer teacherType) {
        String sql = "update DD_QUESTIONNAIRE_ASSGIN_USER q set q.delete_flag=1 where q.ques_id='" + quesId + "'";
        if (!StringUtils.isEmpty(name)) {
            sql += " and (user_name like '%" + name + "%' or job_num like '%" + name + "%')";
        }
        if (teacherType != null && teacherType > 0) {
            sql += " and teacher_type='" + teacherType + "'";
        }
        jdbcTemplate.execute(sql);
    }

    public List<QuestionnaireCensusDTO> findByUserIdAndStatus(Long userId, Integer status) {
        String sql = "SELECT dqau.ID AS dqauid, dqau.weight, dqau.CREATED_DATE, dqa.ID AS dqaid, dqa.`commit_status` commitstatus, dqa.TEACHER_ID, dqa.TEACHER_NAME,dqa.COURSE_ID,dqa.COURSE_NAME,dqa.COURSE_CODE,dqa.classes_name,dqa.classes_id,dq.ID AS dqid,dq.`NAME` AS quesname,dq.END_DATE,dq.TOTAL_SCORE,dq.TOTAL_QUESTIONS,dq.ques_type FROM dd_questionnaire_assgin_user AS dqau LEFT JOIN dd_questionnaire AS dq ON dq.ID=dqau.ques_id LEFT JOIN dd_questionnaire_assgin AS dqa ON dqa.QUESTIONNAIRE_ID=dq.ID AND dqa.CREATED_BY=dqau.user_id WHERE dqau.DELETE_FLAG=0 AND dq.DELETE_FLAG=0 AND dqau.user_id='" + userId + "'";
        if (status != null && status > 0) {
            if (status == 30) {
                sql += " AND dqa.commit_status='10'";
                sql += " AND dq.END_DATE < NOW()";
            } else {
                sql += " AND dqa.commit_status='" + status + "'";
                if (status == 10) {
                    sql += " AND dq.END_DATE > NOW()";
                }
            }
        }
        sql += " ORDER BY dqau.CREATED_DATE DESC";
        RowMapper<QuestionnaireCensusDTO> rowMapper = new RowMapper<QuestionnaireCensusDTO>() {
            @Override
            public QuestionnaireCensusDTO mapRow(ResultSet rs, int arg1) throws SQLException {
                QuestionnaireCensusDTO qd = new QuestionnaireCensusDTO();
                qd.setId(rs.getLong("dqid"));
                qd.setName(rs.getString("quesname"));
                qd.setEnddate(rs.getDate("END_DATE"));
                qd.setTotalScore2(rs.getFloat("TOTAL_SCORE"));
                qd.setTotalScore(new BigDecimal(qd.getTotalScore2()).setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
                qd.setTotalQuestions(rs.getInt("TOTAL_QUESTIONS"));
                qd.setQuesType(rs.getInt("ques_type"));

                qd.setQuestionnaireAssignId(rs.getLong("dqaid"));
                qd.setTeacherId(rs.getLong("TEACHER_ID"));
                qd.setTeacherName(rs.getString("TEACHER_NAME"));
                qd.setCourseId(rs.getLong("COURSE_ID"));
                qd.setCourseName(rs.getString("COURSE_NAME"));
                qd.setCourseCode(rs.getString("COURSE_CODE"));
                qd.setClassId(rs.getLong("classes_id"));
                qd.setClassName(rs.getString("classes_name"));
                Long currTime = new Date().getTime();
                Integer commitstatus = rs.getInt("commitstatus");
                if (commitstatus != null && commitstatus == 10) {
                    if (qd.getEnddate().getTime() < currTime) {
                        qd.setQuestionnaireStatus("30");
                        qd.setIsEnd("true");
                    } else {
                        qd.setQuestionnaireStatus(rs.getString("commitstatus"));
                        qd.setIsEnd("false");
                    }
                } else {
                    qd.setQuestionnaireStatus(rs.getString("commitstatus"));
                    qd.setIsEnd("true");
                }
                qd.setQuestionnaireAssignStudentId(rs.getLong("dqauid"));
                qd.setWeight(rs.getLong("weight"));
                qd.setCreateDate(rs.getTimestamp("CREATED_DATE"));
                qd.setSystemDate(new Date());
                qd.setAdminName("管理员");
                return qd;
            }
        };
        return jdbcTemplate.query(sql, rowMapper);
    }
}
