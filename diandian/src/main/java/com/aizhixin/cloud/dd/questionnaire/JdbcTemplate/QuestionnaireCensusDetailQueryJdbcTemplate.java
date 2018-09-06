package com.aizhixin.cloud.dd.questionnaire.JdbcTemplate;

import com.aizhixin.cloud.dd.communication.jdbcTemplate.PaginationJDBCTemplate;
import com.aizhixin.cloud.dd.questionnaire.dto.QuestionnaireCensusDetailDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class QuestionnaireCensusDetailQueryJdbcTemplate extends
		PaginationJDBCTemplate<QuestionnaireCensusDetailDTO> {

	public static final RowMapper<QuestionnaireCensusDetailDTO> classAndTotallPeoNum = new RowMapper<QuestionnaireCensusDetailDTO>() {
		public QuestionnaireCensusDetailDTO mapRow(ResultSet rs, int rowNum)
				throws SQLException {
		    QuestionnaireCensusDetailDTO questionnaireCensusDetailDTO = new QuestionnaireCensusDetailDTO();
		    questionnaireCensusDetailDTO.setClassName(rs.getString("className"));
		    questionnaireCensusDetailDTO.setClassId(rs.getLong("classId"));
		    questionnaireCensusDetailDTO.setTotalCount(rs.getInt("totalCount"));
			return questionnaireCensusDetailDTO;
		}
	};
	
	   public static final RowMapper<QuestionnaireCensusDetailDTO> commitCountAndTotalScore = new RowMapper<QuestionnaireCensusDetailDTO>() {
	        public QuestionnaireCensusDetailDTO mapRow(ResultSet rs, int rowNum)
	                throws SQLException {
	            QuestionnaireCensusDetailDTO questionnaireCensusDetailDTO = new QuestionnaireCensusDetailDTO();
	            questionnaireCensusDetailDTO.setCommitCount(rs.getInt("subAllPeoCount"));
	            questionnaireCensusDetailDTO.setTotalScore(rs.getInt("totalScore"));
	            return questionnaireCensusDetailDTO;
	        }
	    };
}
