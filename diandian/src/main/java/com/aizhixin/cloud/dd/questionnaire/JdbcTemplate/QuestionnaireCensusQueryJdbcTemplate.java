package com.aizhixin.cloud.dd.questionnaire.JdbcTemplate;

import com.aizhixin.cloud.dd.communication.jdbcTemplate.PaginationJDBCTemplate;
import com.aizhixin.cloud.dd.questionnaire.dto.QuestionnaireCensusDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

@Repository
public class QuestionnaireCensusQueryJdbcTemplate extends
        PaginationJDBCTemplate<QuestionnaireCensusDTO> {

	public static final RowMapper<QuestionnaireCensusDTO> questionnaireMapper = new RowMapper<QuestionnaireCensusDTO>() {
		public QuestionnaireCensusDTO mapRow(ResultSet rs, int rowNum)
				throws SQLException {
		    QuestionnaireCensusDTO questionnaireCensusDTO = new QuestionnaireCensusDTO();
		    questionnaireCensusDTO.setId(rs.getLong("id"));
		    questionnaireCensusDTO.setQuestionnaireAssignId(rs.getLong("QUESTIONNAIRECENSUSDTO"));
		    questionnaireCensusDTO.setName(rs.getString("name"));
		    questionnaireCensusDTO.setTeacherId(rs.getLong("TEACHER_ID"));
		    questionnaireCensusDTO.setTeacherName(rs.getString("TEACHER_NAME"));
		    questionnaireCensusDTO.setCourseId(rs.getLong("COURSE_ID"));
		    questionnaireCensusDTO.setCourseName(rs.getString("COURSE_NAME"));
		    questionnaireCensusDTO.setCourseCode(rs.getString("COURSE_CODE"));
		    questionnaireCensusDTO.setEnddate(rs.getDate("END_DATE"));
		    questionnaireCensusDTO.setSystemDate(new Date());
		    return questionnaireCensusDTO;
		}
	};
	
	public static final RowMapper<QuestionnaireCensusDTO> questionnairePhoneMapper = new RowMapper<QuestionnaireCensusDTO>() {
        public QuestionnaireCensusDTO mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            QuestionnaireCensusDTO questionnaireCensusDTO = new QuestionnaireCensusDTO();
            //分配学生ID
            questionnaireCensusDTO.setId(rs.getLong("dqId"));
            questionnaireCensusDTO.setQuestionnaireAssignId(rs.getLong("dqaID"));
            questionnaireCensusDTO.setQuestionnaireAssignStudentId(rs.getLong("dqasId"));
            questionnaireCensusDTO.setName(rs.getString("name"));
            questionnaireCensusDTO.setTeacherId(rs.getLong("TEACHER_ID"));
            questionnaireCensusDTO.setTeacherName(rs.getString("TEACHER_NAME"));
            questionnaireCensusDTO.setCreateDate(rs.getTimestamp("created_date"));
            questionnaireCensusDTO.setCourseId(rs.getLong("COURSE_ID"));
            questionnaireCensusDTO.setCourseName(rs.getString("COURSE_NAME"));
            questionnaireCensusDTO.setCourseCode(rs.getString("COURSE_CODE"));
            questionnaireCensusDTO.setEnddate(rs.getDate("END_DATE"));
            questionnaireCensusDTO.setQuestionnaireStatus(rs.getString("dqa.delete_flag"));
            questionnaireCensusDTO.setSystemDate(new Date());
            questionnaireCensusDTO.setAdminName("管理员");
            return questionnaireCensusDTO;
        }
    };
}
