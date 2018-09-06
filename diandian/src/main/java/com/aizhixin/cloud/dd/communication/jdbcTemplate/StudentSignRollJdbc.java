package com.aizhixin.cloud.dd.communication.jdbcTemplate;

import com.aizhixin.cloud.dd.counsellorollcall.entity.StudentSignIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
@Transactional
public class StudentSignRollJdbc {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void updateStudentSignInfo(List<StudentSignIn> studentSignIns) {
        String sql = "UPDATE `dd_studentsignin` SET `GPS_DETAIL`=?,`GPS_LOCATION`=?,`GPS_TYPE`=?,`DEVICE_TOKEN`=?,`STATUS`=?,`SIGN_TIME`=?,`HAVE_REPORT`=?, `HAVE_READ`=? WHERE `ID`=?";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                StudentSignIn studentSignIn = studentSignIns.get(i);
                ps.setString(1, studentSignIn.getGpsDetail());
                ps.setString(2, studentSignIn.getGpsLocation());
                ps.setString(3, studentSignIn.getGpsType());
                ps.setString(4, studentSignIn.getDeviceToken());
                ps.setString(5, studentSignIn.getStatus());
                ps.setString(6, studentSignIn.getSignTime().toString());
                ps.setInt(7, studentSignIn.getHaveReport());
                ps.setBoolean(8, studentSignIn.isHaveRead());
                ps.setLong(9, studentSignIn.getId());
            }

            @Override
            public int getBatchSize() {
                return studentSignIns.size();
            }
        });
    }

    public void updateStudentSignInfoV2(List<StudentSignIn> studentSignIns) {
        String sql = "UPDATE `dd_studentsignin` SET `GPS_DETAIL2`=?,`GPS_LOCATION2`=?,`GPS_TYPE2`=?,`DEVICE_TOKEN2`=?,`STATUS2`=?,`SIGN_TIME2`=?,`HAVE_REPORT2`=?, `HAVE_READ2`=? WHERE `ID`=?";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                StudentSignIn studentSignIn = studentSignIns.get(i);
                ps.setString(1, studentSignIn.getGpsDetail2());
                ps.setString(2, studentSignIn.getGpsLocation2());
                ps.setString(3, studentSignIn.getGpsType2());
                ps.setString(4, studentSignIn.getDeviceToken2());
                ps.setString(5, studentSignIn.getStatus2());
                ps.setString(6, studentSignIn.getSignTime2().toString());
                ps.setInt(7, studentSignIn.getHaveReport2());
                ps.setBoolean(8, studentSignIn.getHaveRead2());
                ps.setLong(9, studentSignIn.getId());
            }

            @Override
            public int getBatchSize() {
                return studentSignIns.size();
            }
        });
    }
}
