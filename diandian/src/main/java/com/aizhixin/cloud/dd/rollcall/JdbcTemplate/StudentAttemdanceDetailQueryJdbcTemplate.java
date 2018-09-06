package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;

import com.aizhixin.cloud.dd.communication.jdbcTemplate.PaginationJDBCTemplate;
import com.aizhixin.cloud.dd.rollcall.dto.StudentAttendanceDetailDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class StudentAttemdanceDetailQueryJdbcTemplate extends
        PaginationJDBCTemplate<StudentAttendanceDetailDTO> {

    public static final RowMapper<StudentAttendanceDetailDTO> organMapper = new RowMapper<StudentAttendanceDetailDTO>() {
        public StudentAttendanceDetailDTO mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            StudentAttendanceDetailDTO item = new StudentAttendanceDetailDTO();
            item.setStudentId(rs.getLong("student_id"));
            item.setClassName(rs.getString("className"));
            item.setCourseName(rs.getString("courseName"));
            item.setTeachName(rs.getString("teachName"));
            item.setTeachTime(rs.getString("teachTime"));
            item.setClassRoomName(rs.getString("classRoomName"));
            item.setType(rs.getString("type"));
            item.setSignTime(rs.getString("SIGNTIME"));
            item.setGpsDetail(rs.getString("GPS_DETAIL"));
            return item;
        }
    };
}