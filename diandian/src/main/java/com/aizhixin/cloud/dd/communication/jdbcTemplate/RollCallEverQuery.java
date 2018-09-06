package com.aizhixin.cloud.dd.communication.jdbcTemplate;

import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.communication.dto.RollCallEverDTO;
import com.aizhixin.cloud.dd.communication.dto.RollCallReportDTO;
import com.aizhixin.cloud.dd.communication.dto.RollCallReportStudentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Repository
public class RollCallEverQuery {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<RollCallEverDTO> queryEver(Long teacherId, Long rollCallEverId) {

        String sql = " SELECT   dre.id ,dre.open_time,  dre.status,  COUNT(*) totalCount, "
                + " SUM(CASE WHEN drr.have_report =TRUE THEN 1 ELSE 0 END) AS commitCount "
                + " FROM  dd_rollcallever dre   LEFT JOIN dd_rollcallreport drr "
                + " ON dre.id = drr.rollcallever_id where 1=1 #teacherId# #rollCallEverId# GROUP BY dre.id ORDER BY dre.open_time DESC ";
        if (null != teacherId) {
            sql = sql.replaceAll("#teacherId#", " and dre.teacher_id = " + teacherId);
        } else {
            sql = sql.replaceAll("#teacherId#", "");
        }

        if (null != rollCallEverId) {
            sql = sql.replaceAll("#rollCallEverId#", " and dre.id = " + rollCallEverId);
        } else {
            sql = sql.replaceAll("#rollCallEverId#", "");
        }
        return jdbcTemplate.query(sql, new RowMapper() {

            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                RollCallEverDTO item = new RollCallEverDTO();
                item.setRollCallEverId(rs.getLong("id"));
                item.setCommitCount(rs.getInt("commitCount"));
                item.setTotalCount(rs.getInt("totalCount"));
                item.setStatus(rs.getBoolean("status"));
                try {
                    item.setOpenTime(DateFormatUtil.format(rs.getTimestamp("open_time"), DateFormatUtil.FORMAT_LONG));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return item;
            }
        });
    }

    public List<RollCallReportDTO> queryEverDetail(Long rollCallEverId, Boolean haveReport) {

        String sql = "SELECT   drc.sign_time,drc.gps_detail,gps_location,drc.student_id ,drc.have_report "
                + " FROM  dd_rollcallreport drc  "
                + " where 1 = 1  #rollCallEverId# #haveReport# ORDER BY drc.`id`,drc.`student_id` ASC ";

        if (null != rollCallEverId) {
            sql = sql.replaceAll("#rollCallEverId#", " and drc.rollcallever_id = " + rollCallEverId);
        } else {
            sql = sql.replaceAll("#rollCallEverId#", "");
        }

        if (null != haveReport) {
            sql = sql.replaceAll("#haveReport#", " and drc.have_report = " + haveReport);
        } else {
            sql = sql.replaceAll("#haveReport#", "");
        }

        return jdbcTemplate.query(sql, new RowMapper() {

            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                RollCallReportDTO item = new RollCallReportDTO();
                item.setSignTime(DateFormatUtil.format(rs.getTimestamp("sign_time"), DateFormatUtil.FORMAT_LONG));
                item.setGpsDetail(rs.getString("gps_detail"));
                item.setGpsLocation(rs.getString("gps_location"));
//                item.setStudentName(rs.getString("studentName"));
                item.setStudentId(rs.getLong("student_id"));
//                item.setClassName(rs.getString("className"));
                item.setHaveReport(rs.getBoolean("have_report"));
//                item.setClassId(rs.getLong("classId"));
                return item;
            }
        });
    }

    public List<RollCallReportDTO> queryEverDetails(Long rollCallEverId, Boolean haveReport, Boolean leaveStatus) {

        String sql = "SELECT drc.id,drc.sign_time,drc.gps_detail,drc.gps_location,drc.student_id ,drc.have_report,drc.leave_status,drc.LOOK_STATUS FROM  dd_rollcallreport drc WHERE 1 = 1";

        if (null != rollCallEverId) {
            sql += " and drc.rollcallever_id = " + rollCallEverId;
        }
        if (null != haveReport) {
            sql += " and drc.have_report =" + haveReport + " AND drc.leave_status = false ";
        }
        if (null != leaveStatus) {
            sql += "  AND drc.leave_status = " + leaveStatus;
        }
        sql += " ORDER BY drc.id,drc.student_id ASC ";


        return jdbcTemplate.query(sql, new RowMapper() {

            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                RollCallReportDTO item = new RollCallReportDTO();
                item.setId(rs.getLong("id"));
                item.setSignTime(DateFormatUtil.format(rs.getTimestamp("sign_time"), DateFormatUtil.FORMAT_LONG));
                item.setGpsDetail(rs.getString("gps_detail"));
                item.setGpsLocation(rs.getString("gps_location"));
                item.setStudentId(rs.getLong("student_id"));
                item.setHaveReport(rs.getBoolean("have_report"));
                item.setLeaveStatus(rs.getBoolean("leave_status"));
                item.setLookStatus(rs.getInt("LOOK_STATUS"));
                return item;
            }
        });
    }


    public List<RollCallReportStudentDTO> queryEverStudentDetail(Long studentId) {

        String sql = "SELECT   drc.id,drc.sign_time,drc.gps_detail,gps_location,drc.have_report,drc.rollcallever_id,dro.open_time,dro.teacher_id,dro.status "
                + " FROM  dd_rollcallreport drc   LEFT JOIN dd_rollcallever dro     ON dro.id = drc.rollcallever_id "
                + " WHERE 1 = 1   #studentId# ORDER BY dro.open_time DESC ";
        if (null != studentId) {
            sql = sql.replaceAll("#studentId#", " AND drc.student_id = " + studentId);
        } else {
            sql = sql.replaceAll("#studentId#", "");
        }

        return jdbcTemplate.query(sql, new RowMapper() {

            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                RollCallReportStudentDTO item = new RollCallReportStudentDTO();
                item.setId(rs.getLong("id"));
                item.setTeacherId(rs.getLong("teacher_id"));
                item.setSignTime(DateFormatUtil.format(rs.getTimestamp("sign_time"), DateFormatUtil.FORMAT_LONG));
                item.setGpsDetail(rs.getString("gps_detail"));
                item.setGpsLocation(rs.getString("gps_location"));
                item.setHaveReport(rs.getBoolean("have_report"));
                item.setRollCallEverId(rs.getLong("rollcallever_id"));
                item.setOpenTime(DateFormatUtil.format(rs.getTimestamp("open_time"), DateFormatUtil.FORMAT_LONG));
                item.setStatus(rs.getBoolean("status"));
                return item;
            }
        });
    }
}
