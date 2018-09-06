package com.aizhixin.cloud.dd.questionnaire.JdbcTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.dd.questionnaire.dto.QuestionnaireAssginDTOV2;
import com.aizhixin.cloud.dd.questionnaire.entity.QuestionnaireAssgin;

@Repository
public class QuestionnaireAssginJdbc {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public Long insertQuestionnaireAssgin(final QuestionnaireAssgin qa) {
        final String sql = "insert into `dd_questionnaire_assgin` ( `QUESTIONNAIRE_ID`, `TEACHING_CLASS_ID`, `TEACHING_CLASS_NAME`, `COLLEGE_ID`, `COLLEGE_NAME`, `TEACHER_ID`, `TEACHER_NAME`, `COURSE_ID`, `COURSE_NAME`, `COURSE_CODE`, `SEMESTER_ID`, `STATUS`, `CREATED_BY`, `LAST_MODIFIED_BY`, `DELETE_FLAG`, `teaching_class_code`, `class_type`, `classes_id`, `classes_name`, `classes_code`, `prof_id`, `prof_name`) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, qa.getQuestionnaire().getId());
                if (null == qa.getTeachingClassId()) {
                    ps.setNull(2, Types.BIGINT);
                } else {
                    ps.setLong(2, qa.getTeachingClassId());
                }

                ps.setString(3, qa.getTeachingClassName());
                if (null == qa.getCollegeId()) {
                    ps.setNull(4, Types.BIGINT);
                } else {
                    ps.setLong(4, qa.getCollegeId());
                }

                ps.setString(5, qa.getCollegeName());
                if (null == qa.getTeacherId()) {
                    ps.setNull(6, Types.BIGINT);
                } else {
                    ps.setLong(6, qa.getTeacherId());
                }
                ps.setString(7, qa.getTeacherName());
                if (null == qa.getCourseId()) {
                    ps.setNull(8, Types.BIGINT);
                } else {
                    ps.setLong(8, qa.getCourseId());
                }
                ps.setString(9, qa.getCourseName());
                ps.setString(10, qa.getCourseCode());
                ps.setLong(11, qa.getSemesterId());
                ps.setString(12, qa.getStatus());
                ps.setLong(13, qa.getCreatedBy());
                ps.setLong(14, qa.getLastModifiedBy());
                ps.setInt(15, qa.getDeleteFlag());
                ps.setString(16, qa.getTeachingClassCode());
                ps.setInt(17, qa.getClassType());
                if (null == qa.getClassesId()) {
                    ps.setNull(18, Types.BIGINT);
                } else {
                    ps.setLong(18, qa.getClassesId());
                }
                ps.setString(19, qa.getClassesName());
                ps.setString(20, qa.getClassesCode());
                if (null == qa.getProfId()) {
                    ps.setNull(21, Types.BIGINT);
                } else {
                    ps.setLong(21, qa.getProfId());
                }
                ps.setString(22, qa.getProfName());
                // ps.setString(7, customer.getRegTime());
                return ps;
            }
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public List<Long> findQuestionnaireAssginStudent(Long questionnaireAssginId) {
        String sql = "SELECT dqas.`STUDENT_ID` FROM `dd_questionnaire_assgin_students` AS dqas WHERE dqas.`QUESTIONNAIRE_ASSGIN_ID`= " + questionnaireAssginId;
        RowMapper<Long> rowMapper = new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet arg0, int arg1) throws SQLException {

                return arg0.getLong("student_id");
            }
        };
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<QuestionnaireAssginDTOV2> findByQuestionnaireAssginInfo(Long questionnaireId, Integer classType, String name, String teacherName, Integer pageNumber, Integer pageSize) {
        String sql = "SELECT * FROM `dd_questionnaire_assgin` AS dqa where dqa.`QUESTIONNAIRE_ID`=" + questionnaireId + " and dqa.`STATUS`='10' AND dqa.`DELETE_FLAG`=0 ";
        if (null != classType && 0 != classType) {
            sql += " and dqa.`class_type`=" + classType;
        }
        if (!StringUtils.isEmpty(name)) {
            sql += " and (dqa.`TEACHING_CLASS_NAME` LIKE '%" + name + "%' OR dqa.`classes_name` LIKE '%" + name + "%')";
        }
        if (!StringUtils.isEmpty(teacherName)) {
            sql += " and dqa.`TEACHER_NAME` LIKE '%" + teacherName + "%' ";
        }
        sql += " ORDER BY dqa.`ID` LIMIT " + pageNumber + "," + pageSize;
        RowMapper<QuestionnaireAssginDTOV2> rowMapper = new RowMapper<QuestionnaireAssginDTOV2>() {
            @Override
            public QuestionnaireAssginDTOV2 mapRow(ResultSet arg0, int arg1) throws SQLException {

                QuestionnaireAssginDTOV2 qd = new QuestionnaireAssginDTOV2();
                qd.setId(arg0.getLong("id"));
                qd.setNo(pageNumber + arg1 + 1);
                qd.setClassType(arg0.getInt("class_type"));
                if (arg0.getInt("class_type") == 10) {
                    qd.setCode(arg0.getString("teaching_class_code"));
                    qd.setName(arg0.getString("TEACHING_CLASS_NAME"));
                } else if (arg0.getInt("class_type") == 20) {
                    qd.setCode(arg0.getString("classes_code"));
                    qd.setName(arg0.getString("classes_name"));
                }
                qd.setTeacherName(arg0.getString("TEACHER_NAME"));
                qd.setAssginDate(arg0.getTimestamp("CREATED_DATE"));
                return qd;
            }
        };
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Integer countByQuestionnaireAssginInfo(Long questionnaireId, Integer classType, String name, String teacherName) {
        String sql = "SELECT count(*) FROM `dd_questionnaire_assgin` AS dqa where dqa.`QUESTIONNAIRE_ID`=" + questionnaireId + " and dqa.`STATUS`='10' AND dqa.`DELETE_FLAG`=0 ";
        if (null != classType && 0 != classType) {
            sql += " and dqa.`class_type`=" + classType;
        }
        if (!StringUtils.isEmpty(name)) {
            sql += " and (dqa.`TEACHING_CLASS_NAME` LIKE '%" + name + "%' OR dqa.`classes_name` LIKE '%" + name + "%')";
        }
        if (!StringUtils.isEmpty(teacherName)) {
            sql += " and dqa.`TEACHER_NAME` LIKE '%" + teacherName + "%' ";
        }
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public void deleteByNameAndTeacherType(Long quesId, String name, Integer teacherType) {
        String sql = "update DD_QUESTIONNAIRE_ASSGIN q set q.delete_flag=1 where q.QUESTIONNAIRE_ID='" + quesId + "' and q.CREATED_BY in (select user_id from DD_QUESTIONNAIRE_ASSGIN_USER dqau where dqau.ques_id=" + quesId;
        if (!org.apache.commons.lang.StringUtils.isEmpty(name)) {
            sql += " and (user_name like '%" + name + "%' or job_num like '%" + name + "%')";
        }
        if (teacherType != null && teacherType > 0) {
            sql += " and teacher_type='" + teacherType + "'";
        }
        sql += ")";
        jdbcTemplate.execute(sql);
    }
}
