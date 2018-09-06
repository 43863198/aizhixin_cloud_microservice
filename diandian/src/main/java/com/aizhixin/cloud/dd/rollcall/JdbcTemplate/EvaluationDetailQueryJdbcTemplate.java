package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;

import com.aizhixin.cloud.dd.communication.jdbcTemplate.PaginationJDBCTemplate;
import com.aizhixin.cloud.dd.rollcall.dto.EvaluationDetailDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class EvaluationDetailQueryJdbcTemplate extends PaginationJDBCTemplate<EvaluationDetailDTO> {
    public static final RowMapper<EvaluationDetailDTO> rowMapper = new RowMapper<EvaluationDetailDTO>() {
        @Override
        public EvaluationDetailDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            EvaluationDetailDTO item = new EvaluationDetailDTO();
            item.setCourseId(rs.getString("courseId"));
            item.setCourseName(rs.getString("courseName"));
            item.setCeTeaName(rs.getString("ceTeaName"));
            item.setCourseTime(rs.getString("CourseTime"));
            item.setEvaluationTime(rs.getString("evaluationTime"));
            item.setClassRom(rs.getString("classRom"));
            item.setEvaluationScore(rs.getString("evaluationScore"));
            item.setEvaluationContent(rs.getString("evaluationContent"));
            return item;
        }
    };

}