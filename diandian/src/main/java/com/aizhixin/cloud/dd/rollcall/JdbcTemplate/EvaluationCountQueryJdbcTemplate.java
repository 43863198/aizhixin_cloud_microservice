package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;

import com.aizhixin.cloud.dd.communication.jdbcTemplate.PaginationJDBCTemplate;
import com.aizhixin.cloud.dd.rollcall.dto.EvaluationCountDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class EvaluationCountQueryJdbcTemplate extends PaginationJDBCTemplate<EvaluationCountDTO> {
    public static final RowMapper<EvaluationCountDTO> rowMapper = new RowMapper<EvaluationCountDTO>() {
        @Override
        public EvaluationCountDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            EvaluationCountDTO item = new EvaluationCountDTO();
            item.setCourseId(rs.getString("courseId"));
            item.setCourseName(rs.getString("courseName"));
            item.setCourseTeacher(rs.getString("courseTeacher"));
            item.setOneStar(rs.getLong("oneStar"));
            item.setTwoStar(rs.getLong("twoStar"));
            item.setThreeStar(rs.getLong("threeStar"));
            item.setFourStar(rs.getLong("fourStar"));
            item.setFiveStar(rs.getLong("fiveStar"));
            return item;
        }
    };

}
