package com.aizhixin.cloud.dd.rollcall.repository;

import com.aizhixin.cloud.dd.rollcall.dto.AttendanceDTO;
import com.aizhixin.cloud.dd.rollcall.dto.CounselorClassesAddendanceDTO;
import com.aizhixin.cloud.dd.rollcall.dto.CounselorStudentAddendanceDTO;
import com.aizhixin.cloud.dd.rollcall.dto.RollCallOfSearchDTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Set;

@Repository
public class CounselorStudentAttendanceQuery {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<CounselorStudentAddendanceDTO> queryStudentsAttendance(Long classId, Long semesterId, String sort) {
        String sql = " SELECT   dr.`STUDENT_ID`,  dr.`STUDENT_NUM`,  dr.`STUDENT_NAME`," +
                " SUM(    IF(dr.`TYPE` = 1, 1, IF(dr.`TYPE` = 4, 1, 0))  ) AS normal," +
                " SUM(IF(dr.`TYPE` = 2,1,IF(dr.`TYPE` = 3, 1, IF(dr.`TYPE` = 5, 1, 0)))) AS exception" +
                " FROM  dd_rollcall dr WHERE dr.delete_flag=0";

        if (classId != null && classId > 0) {
            sql += " and dr.`CLASS_ID` ='" + classId + "'";
        }
        if (semesterId != null && semesterId > 0) {
            sql += " and dr.semester_id='" + semesterId + "'";
        }
        sql += " GROUP BY dr.STUDENT_ID";
        if (!StringUtils.isEmpty(sort)) {
            if (sort.equals("normal")) {
                sql += " order by normal desc";
            } else if (sort.equals("exception")) {
                sql += " order by exception desc";
            } else {
                sql += " order by dr.student_num asc";
            }
        } else {
            sql += " order by dr.student_num asc";
        }
        return jdbcTemplate.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                CounselorStudentAddendanceDTO item = new CounselorStudentAddendanceDTO();
                item.setStudentId(rs.getLong("STUDENT_ID"));
                item.setStudentNum(rs.getString("STUDENT_NUM"));
                item.setStudentName(rs.getString("STUDENT_NAME"));
                item.setNormalCount(rs.getInt("normal"));
                item.setExceptionCount(rs.getInt("exception"));
                return item;
            }
        });
    }


    public List<CounselorClassesAddendanceDTO> queryClassesAttendance(
            Set<Long> classIds, Long semesterId) {

        String sql = " SELECT   dr.`CLASS_ID`,dr.`class_name`,dr.semester_id," +
                " SUM(    IF(dr.`TYPE` = 1, 1, IF(dr.`TYPE` = 4, 1, 0))  ) AS normal," +
                " COUNT(1) AS total" +
                " FROM  dd_rollcall dr WHERE dr.`CLASS_ID` IN (#classIds#) #semesterId# " +
                " GROUP BY dr.`CLASS_ID` order by dr.class_id asc";

        if (classIds != null) {
            sql = sql.replaceAll("#classIds#", StringUtils.join(classIds.toArray(), ","));
        }
        if (null != semesterId) {
            sql = sql.replace("#semesterId#", "and dr.semester_id = " + semesterId + "");
        } else {
            sql = sql.replace("#semesterId#", "");
        }
        NumberFormat nt = NumberFormat.getPercentInstance();
        nt.setMinimumFractionDigits(2);

        return jdbcTemplate.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                CounselorClassesAddendanceDTO item = new CounselorClassesAddendanceDTO();
                item.setClassId(rs.getLong("CLASS_ID"));
                item.setClassName(rs.getString("class_name"));
                item.setNormalCount(rs.getInt("normal"));
                item.setTotalCount(rs.getInt("total"));
                item.setSemesterId(rs.getLong("semester_id"));
                int normalCount = item.getNormalCount();
                int totalCount = item.getTotalCount();
                float temp = (float) normalCount / (totalCount == 0 ? 1 : totalCount);
                item.setClassRate(nt.format(temp));
                return item;
            }
        });
    }

    //StudentAttendanceQuery   queryScheduleAttendance
}
