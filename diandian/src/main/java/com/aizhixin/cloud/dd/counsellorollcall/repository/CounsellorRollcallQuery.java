package com.aizhixin.cloud.dd.counsellorollcall.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.counsellorollcall.domain.*;
import com.aizhixin.cloud.dd.counsellorollcall.dto.CounRollcallStatisticsDTO;
import com.aizhixin.cloud.dd.counsellorollcall.utils.CounsellorRollCallEnum;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.rollcall.utils.PaginationUtil;

@Repository
public class CounsellorRollcallQuery {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<DailyStatisticsStuDomain> findDailyStatisticsStu(Long stuId, Long groupId, String date) {
        String sql = "SELECT ds.* FROM dd_studentsignin ds WHERE ds.DELETE_FLAG=0 AND ds.STUDENT_ID=" + stuId + " AND ds.COUNSERLLORROLLCALL_ID IN (SELECT dc.id FROM dd_counsellorrollcall dc WHERE dc.DELETE_FLAG=0 AND dc.TEMPGROUP_ID=" + groupId + ") AND DATE_FORMAT(ds.CREATED_DATE,'%Y-%m-%d')='" + date + "'";
        RowMapper<DailyStatisticsStuDomain> rowMapper = new RowMapper<DailyStatisticsStuDomain>() {
            @Override
            public DailyStatisticsStuDomain mapRow(ResultSet rs, int arg1) throws SQLException {
                DailyStatisticsStuDomain d = new DailyStatisticsStuDomain();
                d.setId(rs.getLong("ID"));
                d.setStudentId(rs.getLong("STUDENT_ID"));
                d.setStudentName(rs.getString("STUDENT_NAME"));
                d.setClassName(rs.getString("CLASS_NAME"));

                d.setStatus(rs.getString("STATUS"));
                d.setGpsLocation(rs.getString("GPS_LOCATION"));
                d.setGpsDetail(rs.getString("GPS_DETAIL"));
                d.setSignTime(DateFormatUtil.format(rs.getTimestamp("SIGN_TIME"), DateFormatUtil.FORMAT_SHORT_MINUTE));
                d.setHaveReport(rs.getInt("HAVE_REPORT"));

                d.setStatus2(rs.getString("STATUS2"));
                d.setGpsLocation2(rs.getString("GPS_LOCATION2"));
                d.setGpsDetail2(rs.getString("GPS_DETAIL2"));
                d.setSignTime2(DateFormatUtil.format(rs.getTimestamp("SIGN_TIME2"), DateFormatUtil.FORMAT_SHORT_MINUTE));
                d.setHaveReport2(rs.getInt("HAVE_REPORT2"));
                return d;
            }
        };
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<DailyStatisticsStuDomain> findDailyStatistics(Long groupId, String date, Integer status) {
        String sql = "SELECT ds.* FROM dd_studentsignin ds WHERE ds.DELETE_FLAG=0 AND ds.COUNSERLLORROLLCALL_ID IN (SELECT dc.id FROM dd_counsellorrollcall dc WHERE dc.DELETE_FLAG=0 AND dc.TEMPGROUP_ID=" + groupId + ") AND DATE_FORMAT(ds.CREATED_DATE,'%Y-%m-%d')='" + date + "'";
        //10:迟到 20:开始缺卡 30:结束缺卡 40:请假 50:已到 60:两次都缺卡
        switch (status.intValue()) {
            case 10:
                sql += " AND ds.`STATUS`='" + CounsellorRollCallEnum.Late.getType() + "'";
                break;
            case 20:
                sql += " AND ds.`STATUS`='" + CounsellorRollCallEnum.UnCommit.getType() + "'";
                break;
            case 30:
                sql += " AND ds.`STATUS2`='" + CounsellorRollCallEnum.UnCommit.getType() + "'";
                break;
            case 40:
                sql += " AND (ds.`STATUS`='" + CounsellorRollCallEnum.AskForLeave.getType() + "' OR ds.`STATUS2`='" + CounsellorRollCallEnum.AskForLeave.getType() + "')";
                break;
            case 50:
                sql += " AND ds.`STATUS`='" + CounsellorRollCallEnum.HavaTo.getType() + "' AND ds.`STATUS2`='" + CounsellorRollCallEnum.HavaTo.getType() + "'";
                break;
            case 60:
                sql += " AND ds.`STATUS`='" + CounsellorRollCallEnum.UnCommit.getType() + "' AND ds.`STATUS2`='" + CounsellorRollCallEnum.UnCommit.getType() + "'";
                break;
        }
        RowMapper<DailyStatisticsStuDomain> rowMapper = new RowMapper<DailyStatisticsStuDomain>() {
            @Override
            public DailyStatisticsStuDomain mapRow(ResultSet rs, int arg1) throws SQLException {
                DailyStatisticsStuDomain d = new DailyStatisticsStuDomain();
                d.setId(rs.getLong("ID"));
                d.setStudentId(rs.getLong("STUDENT_ID"));
                d.setStudentName(rs.getString("STUDENT_NAME"));
                d.setClassName(rs.getString("CLASS_NAME"));

                d.setStatus(rs.getString("STATUS"));
                d.setGpsLocation(rs.getString("GPS_LOCATION"));
                d.setGpsDetail(rs.getString("GPS_DETAIL"));
                d.setSignTime(DateFormatUtil.format(rs.getTimestamp("SIGN_TIME"), DateFormatUtil.FORMAT_SHORT_MINUTE));
                d.setHaveReport(rs.getInt("HAVE_REPORT"));

                d.setStatus2(rs.getString("STATUS2"));
                d.setGpsLocation2(rs.getString("GPS_LOCATION2"));
                d.setGpsDetail2(rs.getString("GPS_DETAIL2"));
                d.setSignTime2(DateFormatUtil.format(rs.getTimestamp("SIGN_TIME2"), DateFormatUtil.FORMAT_SHORT_MINUTE));
                d.setHaveReport2(rs.getInt("HAVE_REPORT2"));
                return d;
            }
        };
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<MonthlyStatsRecordDomain> findMonthlyStatisticsStuRecord(Long groupId, Long stuId, String date, Integer status) {
        String sql = "SELECT ds.STUDENT_ID, ds.STUDENT_NAME, ds.CLASS_NAME, ds.SIGN_TIME, ds.CREATED_DATE FROM dd_studentsignin ds WHERE ds.STUDENT_ID='" + stuId + "' AND ds.DELETE_FLAG=0 AND ds.COUNSERLLORROLLCALL_ID IN (SELECT dc.id FROM dd_counsellorrollcall dc WHERE dc.DELETE_FLAG=0 AND dc.TEMPGROUP_ID=" + groupId + ") AND DATE_FORMAT(ds.CREATED_DATE,'%Y-%m')='" + date + "'";
        //10:迟到 20:开始缺卡 30:结束缺卡 40:请假
        switch (status.intValue()) {
            case 10:
                sql += " AND ds.`STATUS`='" + CounsellorRollCallEnum.Late.getType() + "'";
                break;
            case 20:
                sql += " AND ds.`STATUS`='" + CounsellorRollCallEnum.UnCommit.getType() + "'";
                break;
            case 30:
                sql += " AND ds.`STATUS2`='" + CounsellorRollCallEnum.UnCommit.getType() + "'";
                break;
            case 40:
                sql += " AND (ds.`STATUS`='" + CounsellorRollCallEnum.AskForLeave.getType() + "' OR ds.`STATUS2`='" + CounsellorRollCallEnum.AskForLeave.getType() + "')";
                break;
        }

        RowMapper<MonthlyStatsRecordDomain> rowMapper = new RowMapper<MonthlyStatsRecordDomain>() {
            @Override
            public MonthlyStatsRecordDomain mapRow(ResultSet rs, int arg1) throws SQLException {
                MonthlyStatsRecordDomain d = new MonthlyStatsRecordDomain();
                d.setStuId(rs.getLong("STUDENT_ID"));
                d.setGroupId(groupId);
                if (status == 10) {
                    d.setDate(DateFormatUtil.formatShort(rs.getTimestamp("SIGN_TIME")));
                    d.setLabel(DateFormatUtil.formatDateWithWeek(rs.getTimestamp("SIGN_TIME")));
                } else {
                    d.setDate(DateFormatUtil.formatShort(rs.getTimestamp("CREATED_DATE")));
                    d.setLabel(DateFormatUtil.formatDateWithWeek(rs.getTimestamp("CREATED_DATE")));
                }
                return d;
            }
        };
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<MonthlyStatisticsStuDomain> findMonthlyStatistics(Long groupId, String date, Integer status) {
        String sql = "SELECT ds.STUDENT_ID, ds.STUDENT_NAME, ds.CLASS_NAME, COUNT(*) scount FROM dd_studentsignin ds WHERE ds.DELETE_FLAG=0 AND ds.COUNSERLLORROLLCALL_ID IN (SELECT dc.id FROM dd_counsellorrollcall dc WHERE dc.DELETE_FLAG=0 AND dc.TEMPGROUP_ID=" + groupId + ") AND DATE_FORMAT(ds.CREATED_DATE,'%Y-%m')='" + date + "'";
        //10:迟到 20:开始缺卡 30:结束缺卡 40:请假
        switch (status.intValue()) {
            case 10:
                sql += " AND ds.`STATUS`='" + CounsellorRollCallEnum.Late.getType() + "'";
                break;
            case 20:
                sql += " AND ds.`STATUS`='" + CounsellorRollCallEnum.UnCommit.getType() + "'";
                break;
            case 30:
                sql += " AND ds.`STATUS2`='" + CounsellorRollCallEnum.UnCommit.getType() + "'";
                break;
            case 40:
                sql += " AND (ds.`STATUS`='" + CounsellorRollCallEnum.AskForLeave.getType() + "' OR ds.`STATUS2`='" + CounsellorRollCallEnum.AskForLeave.getType() + "')";
                break;
        }
        sql += " GROUP BY ds.STUDENT_ID";
        RowMapper<MonthlyStatisticsStuDomain> rowMapper = new RowMapper<MonthlyStatisticsStuDomain>() {
            @Override
            public MonthlyStatisticsStuDomain mapRow(ResultSet rs, int arg1) throws SQLException {
                MonthlyStatisticsStuDomain d = new MonthlyStatisticsStuDomain();
                d.setStudentId(rs.getLong("STUDENT_ID"));
                d.setStudentName(rs.getString("STUDENT_NAME"));
                d.setClassName(rs.getString("CLASS_NAME"));
                d.setCount(rs.getInt("scount"));
                return d;
            }
        };
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<StuTempGroupDomainV2> findGroupByStudentId(Long stuId) {
        String sql = "SELECT dtg.id, dtg.message_id, dtg.`NAME`, dtg.rollcall_num, dtg.rollcall_type, dtg.TEACHER_NAME, dac.CLOCK_MODE, dac.start_end_time, dac.CLOCK_TIME, dac.late_time, dac.second_time, dac.end_time, (select open_time from dd_counsellorrollcall dd where dd.tempgroup_id=dtg.id and dd.DELETE_FLAG=0 order by id desc limit 1) openTime FROM dd_tempgroup dtg LEFT JOIN dd_alarmclock dac ON dac.TEMPGROUP_ID=dtg.id WHERE dtg.DELETE_FLAG=0 AND dtg.id IN (SELECT dsg.TEMPGROUP_ID FROM dd_studentsubgroup dsg WHERE dsg.STUDENT_ID=" + stuId + " AND dsg.DELETE_FLAG=0)";
        RowMapper<StuTempGroupDomainV2> rowMapper = new RowMapper<StuTempGroupDomainV2>() {
            @Override
            public StuTempGroupDomainV2 mapRow(ResultSet rs, int arg1) throws SQLException {
                StuTempGroupDomainV2 qd = new StuTempGroupDomainV2();
                qd.setGroupId(rs.getLong("id"));
                qd.setMessageId(rs.getString("message_id"));
                qd.setGroupName(rs.getString("name"));
                qd.setRollcallNum(rs.getInt("rollcall_num"));
                qd.setRollcallType(rs.getInt("rollcall_type"));
                qd.setTeacherName(rs.getString("TEACHER_NAME"));
                qd.setAlarmModel(rs.getString("CLOCK_MODE"));
                qd.setOpenTime(rs.getTimestamp("openTime"));
                if (qd.getRollcallNum() > 1) {
                    qd.setAlarmTime(rs.getString("start_end_time"));
                    qd.setFirstTime(rs.getString("CLOCK_TIME"));
                    qd.setLateTime(rs.getString("late_time"));
                    qd.setSecondTime(rs.getString("second_time"));
                    qd.setEndTime(rs.getString("end_time"));
                } else {
                    qd.setAlarmTime(rs.getString("CLOCK_TIME"));
                    qd.setFirstTime(rs.getString("CLOCK_TIME"));
                }
                return qd;
            }
        };
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<StuTempGroupDomainV2> findGroupById(Long groupId) {
        String sql = "SELECT dtg.id, dtg.`NAME`, dtg.rollcall_num, dtg.rollcall_type, dtg.TEACHER_NAME, dac.CLOCK_MODE, dac.start_end_time, dac.CLOCK_TIME, dac.late_time, dac.second_time, dac.end_time FROM dd_tempgroup dtg LEFT JOIN dd_alarmclock dac ON dac.TEMPGROUP_ID=dtg.id WHERE dtg.DELETE_FLAG=0 AND dtg.id =" + groupId;
        RowMapper<StuTempGroupDomainV2> rowMapper = new RowMapper<StuTempGroupDomainV2>() {
            @Override
            public StuTempGroupDomainV2 mapRow(ResultSet rs, int arg1) throws SQLException {
                StuTempGroupDomainV2 qd = new StuTempGroupDomainV2();
                qd.setGroupId(rs.getLong("id"));
                qd.setGroupName(rs.getString("name"));
                qd.setRollcallNum(rs.getInt("rollcall_num"));
                qd.setRollcallType(rs.getInt("rollcall_type"));
                qd.setTeacherName(rs.getString("TEACHER_NAME"));
                qd.setAlarmModel(rs.getString("CLOCK_MODE"));
                if (qd.getRollcallNum() > 1) {
                    qd.setAlarmTime(rs.getString("start_end_time"));
                    qd.setFirstTime(rs.getString("CLOCK_TIME"));
                    qd.setLateTime(rs.getString("late_time"));
                    qd.setSecondTime(rs.getString("second_time"));
                    qd.setEndTime(rs.getString("end_time"));
                } else {
                    qd.setAlarmTime(rs.getString("CLOCK_TIME"));
                    qd.setFirstTime(rs.getString("CLOCK_TIME"));
                }
                return qd;
            }
        };
        return jdbcTemplate.query(sql, rowMapper);
    }

    public PageData<CounRollcallStatisticsDTO> listCounRollcall(Integer offset, Integer limit, Set<Long> counIds) {

        StringBuffer sql = new StringBuffer();
        // UnCommit(10, "未提交"), HavaTo(20, "已到"), Exception(30, "异常"), AskForLeave(40, "请假");
        sql.append(" SELECT   dc.`id`,  dc.`TEACHER_ID`,  dte.`NAME`,  dc.`OPEN_TIME`,dte.name as groupName,dst.teaching_year, ");
        sql.append(" COUNT(dst.`id`) as total,SUM(IF(dst.`STATUS` = 10, 1, 0)) as uncomit, ");
        sql.append(" SUM(IF(dst.`STATUS` = 20, 1, 0)) as haveTo ,SUM(IF(dst.`STATUS` = 30, 1, 0)) as nonArrival , ");
        sql.append(" SUM(IF(dst.`STATUS` = 40, 1, 0)) as askForLeave ");
        sql.append(" FROM  dd_counsellorrollcall dc ");
        sql.append(" LEFT JOIN dd_studentsignin dst ON dc.`ID` = dst.`COUNSERLLORROLLCALL_ID`  ");
        sql.append(" LEFT JOIN dd_tempgroup dte ON dte.`id` = dc.`TEMPGROUP_ID`  ");
        sql.append(" where dc.id in (#counIds#)");
        sql.append(" GROUP BY dc.id ORDER BY dc.`OPEN_TIME` DESC ");

        sql = new StringBuffer(sql.toString().replace("#counIds#", StringUtils.join(counIds.toArray(), ",")));

        PageData<CounRollcallStatisticsDTO> page = new PageData();
        String limitSql = PaginationUtil.page(jdbcTemplate, sql.toString(), offset, limit, page);
        if (null == limitSql) {
            page.getPage().setTotalElements(0L);
            page.getPage().setPageSize(10);
            page.getPage().setPageNumber(1);
            page.getPage().setTotalPages(0);
            return page;
        }
        sql.append(limitSql);

        page.setData(jdbcTemplate.query(sql.toString(), new RowMapper() {
            @Override
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                CounRollcallStatisticsDTO item = new CounRollcallStatisticsDTO();
                item.setRid(rs.getLong("id"));
                item.setUncommitted(rs.getInt("uncomit"));
                item.setHaveTo(rs.getInt("haveTo"));
                item.setTotal(rs.getInt("total"));
                item.setNonArrival(rs.getInt("nonArrival"));
                item.setLeave(rs.getInt("askForLeave"));
                item.setTId(rs.getLong("TEACHER_ID"));
                item.setInitiatingTime(rs.getTimestamp("OPEN_TIME"));
                item.setGroupName(rs.getString("groupName"));
                String teachingyear = rs.getString("teaching_year");
                item.setGrade("null".equals(teachingyear) ? null : teachingyear);
                return item;
            }
        }));
        return page;
    }
}
