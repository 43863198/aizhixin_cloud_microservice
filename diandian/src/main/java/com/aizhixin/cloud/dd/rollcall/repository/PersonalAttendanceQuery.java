package com.aizhixin.cloud.dd.rollcall.repository;

import com.aizhixin.cloud.dd.rollcall.dto.PersonalAttendanceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class PersonalAttendanceQuery {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<PersonalAttendanceDTO> query(Long studentId, Long semesterId) {

        String sql = "SELECT dr.`student_id` AS studentId, SUM(CASE WHEN dr.type = 1 THEN 1 ELSE 0 END) AS NORMAL, SUM(CASE WHEN dr.type = 4 THEN 1 ELSE 0 END) AS ASKFORLEAVE, SUM(CASE WHEN dr.type = 3 THEN 1 ELSE 0 END) AS late, SUM(CASE WHEN dr.type = 2 THEN 1 ELSE 0 END) AS truancy, SUM(CASE WHEN dr.type = 5 THEN 1 ELSE 0 END) AS typeLeave, dr.`type` FROM dd_rollcall dr WHERE 1=1 ";
        if (null != studentId) {
            sql += "  and dr.student_id= " + studentId;
        }
        if (null != semesterId) {
            sql += "  and dr.semester_id= " + semesterId;
        }
        return jdbcTemplate.query(sql, new RowMapper() {

            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                PersonalAttendanceDTO item = new PersonalAttendanceDTO();
                item.setNormal(rs.getInt("normal"));
                item.setTruancy(rs.getInt("truancy"));
                item.setLate(rs.getInt("late"));
                item.setAskforlevae(rs.getInt("askforleave"));
                item.setLeave(rs.getInt("typeLeave"));
                item.setStudentId(rs.getLong("studentId"));
                return item;
            }
        });
    }

    public List<PersonalAttendanceDTO> queryAssess(Long teacherId, Long semesterId) {

        String sql = "SELECT SUM(score) as score ,COUNT(*) as total FROM dd_assess da  WHERE 1=1  ";
        if (null != teacherId) {
            sql += "  and da.teacher_id= " + teacherId;
        }
        if (null != semesterId) {
            sql += "  and da.semester_id= " + semesterId;
        }
        return jdbcTemplate.query(sql, new RowMapper() {

            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                PersonalAttendanceDTO item = new PersonalAttendanceDTO();
                item.setAssessTotalCount(rs.getInt("total"));
                item.setAssessTotalScore(rs.getDouble("score"));
                return item;
            }
        });
    }

    public List<PersonalAttendanceDTO> queryForTeacher(Long teacherId, Long semesterId) {

        String sql = "SELECT dr.`teacher_id` AS teacherId, SUM(CASE WHEN dr.type = 1 THEN 1 ELSE 0 END) AS NORMAL, SUM(CASE WHEN dr.type = 4 THEN 1 ELSE 0 END) AS ASKFORLEAVE, SUM(CASE WHEN dr.type = 3 THEN 1 ELSE 0 END) AS late, SUM(CASE WHEN dr.type = 2 THEN 1 ELSE 0 END) AS truancy, SUM(CASE WHEN dr.type = 5 THEN 1 ELSE 0 END) AS typeLeave, dr.`type` FROM dd_rollcall dr WHERE 1=1 ";
        if (null != teacherId) {
            sql += "  and dr.teacher_id= " + teacherId;
        }
        if (null != semesterId) {
            sql += "  and dr.semester_id= " + semesterId;
        }
        return jdbcTemplate.query(sql, new RowMapper() {

            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                PersonalAttendanceDTO item = new PersonalAttendanceDTO();
                item.setNormal(rs.getInt("normal"));
                item.setTruancy(rs.getInt("truancy"));
                item.setLate(rs.getInt("late"));
                item.setAskforlevae(rs.getInt("askforleave"));
                item.setLeave(rs.getInt("typeLeave"));
                item.setTeacherId(rs.getLong("teacherId"));
                return item;
            }
        });
    }
}
