package com.aizhixin.cloud.dd.questionnaire.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.dd.questionnaire.domain.QuestionnaireAssginStudentsDomain;

@Repository
public class QuestionnaireAssginStudentsJdbc {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public void insertQuestionnaireAssgin(List<QuestionnaireAssginStudentsDomain> qal) {
        String sql = "insert into `dd_questionnaire_assgin_students` (`QUESTIONNAIRE_ASSGIN_ID`, `STUDENT_ID`, `STUDENT_NAME`, `CLASSES_ID`, `CLASSES_NAME`,  `STATUS`) values(?,?,?,?,?,?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement arg0, int arg1) throws SQLException {
                arg0.setLong(1, qal.get(arg1).getQuestionnaireAssginId());
                arg0.setLong(2, qal.get(arg1).getStuId());
                arg0.setString(3, qal.get(arg1).getStuName());
                arg0.setLong(4, qal.get(arg1).getClassesId());
                arg0.setString(5, qal.get(arg1).getClassesName());
                arg0.setInt(6, qal.get(arg1).getStatus());
            }

            @Override
            public int getBatchSize() {

                return qal.size();
            }
        });
    }

    public Map<String, Object> findAlertByNoCommitStudent(Long userId, Long orgId, String date, String role) {
        try {
            String sql = "";
            if (role.equals("student")) {
                sql = "SELECT dh.ICON_URL iconUrl, dh.TARGET_URL targetUrl, dh.TITLE title, dh.TARGET_TYPE targetType, dh.ONOFF, dh.ORGS FROM dd_homepage dh WHERE (dh.is_questionnaire=0 OR (SELECT count(*) FROM `dd_questionnaire_assgin_students` AS dqas LEFT JOIN `dd_questionnaire_assgin` AS dqa ON dqas.`QUESTIONNAIRE_ASSGIN_ID`=dqa.`ID` LEFT JOIN `dd_questionnaire` AS dq ON dq.`ID`=dqa.`QUESTIONNAIRE_ID` WHERE dq.`STATUS`=20 AND dqa.`STATUS`='10' AND dqas.`STATUS`=10 AND dqas.`STUDENT_ID`=" + userId + " AND dq.`END_DATE`> '" + date + "')> 0) AND dh.ROLE='student' AND TYPE='alert' AND dh.version='V2' AND dh.DELETE_FLAG=0 AND (dh.ONOFF='on' OR (dh.ONOFF='many' AND ORGS like '%" + orgId + "%')) order by created_date desc limit 1";
            } else {
                sql = "SELECT dh.ICON_URL iconUrl, dh.TARGET_URL targetUrl, dh.TITLE title, dh.TARGET_TYPE targetType, dh.ONOFF, dh.ORGS FROM dd_homepage dh WHERE dh.ROLE='teacher' AND TYPE='alert' AND dh.version='V2' AND dh.DELETE_FLAG=0 AND (dh.ONOFF='on' OR dh.ONOFF='many') AND ORGS='" + orgId + "' order by created_date desc limit 1";
            }
            return jdbcTemplate.queryForMap(sql);
        } catch (Exception ex) {
            return null;
        }
    }
}
