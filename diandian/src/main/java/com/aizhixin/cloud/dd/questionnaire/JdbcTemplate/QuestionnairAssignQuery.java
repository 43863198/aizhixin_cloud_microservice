package com.aizhixin.cloud.dd.questionnaire.JdbcTemplate;

import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.questionnaire.dto.PartStatisticsDTO;
import com.aizhixin.cloud.dd.questionnaire.dto.QuestionnaireCensusDTO;
import com.aizhixin.cloud.dd.questionnaire.dto.QuestionnareAssignQueryDTO;
import com.aizhixin.cloud.dd.rollcall.utils.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

@Repository
public class QuestionnairAssignQuery {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public PageData<QuestionnareAssignQueryDTO> queryAssign(Integer offset, Integer limit, String courseName, String teacherName, Long questionnaireId, Long assginId, Long collegeId) {

        String sql = "SELECT  dqa.id, dqa.teaching_class_id,dqa.teaching_class_code,dqa.teaching_class_name, dqa.course_name,  dqa.TEACHER_NAME,  dqa.created_date,  COUNT(dqas.id) AS totalCount," +
                " SUM(IFNULL(dqas.SCORE, 0)) AS totalScore,  " +
                " COUNT(CASE IFNULL(dqas.`SCORE`, 0) != 0 WHEN 1  THEN 0 END  ) AS assesCount,  dq.STATUS,dqa.status as assAndCanStuatus,dq.quantification" +
                " FROM  dd_questionnaire dq  LEFT JOIN dd_questionnaire_assgin dqa " +
                " ON dq.id = dqa.QUESTIONNAIRE_ID   LEFT JOIN dd_questionnaire_assgin_students dqas   " +
                " ON dqa.id = dqas.`QUESTIONNAIRE_ASSGIN_ID`  " +
                " WHERE 1=1   AND  dq.delete_flag = 0 AND dq.status = '20' ";
        if (questionnaireId != null) {
            sql += " AND dqa.QUESTIONNAIRE_ID =" + questionnaireId;
        }

        if (assginId != null) {
            sql += " AND dqa.id =" + assginId;
        }

        if (courseName != null) {
            sql += " AND dqa.COURSE_NAME like '%" + courseName + "%'";
        }
        if (teacherName != null) {
            sql += " and dqa.TEACHER_NAME like '%" + teacherName + "%'";
        }
        if (collegeId != null && !collegeId.equals("")) {
            sql += " AND dqa.COLLEGE_ID = " + collegeId;
        }
        sql += "  group by dqa.`TEACHING_CLASS_ID`  ";

        sql += "  ORDER BY dqa.created_date DESC ";


        PageData<QuestionnareAssignQueryDTO> page = new PageData();
        String limitSql = PaginationUtil.page(jdbcTemplate, sql, offset, limit, page);
        if (null == limitSql) {
            page.getPage().setTotalElements(0L);
            page.getPage().setPageSize(10);
            page.getPage().setPageNumber(1);
            page.getPage().setTotalPages(0);
            return page;
        }
        sql = sql + limitSql;

        page.setData(jdbcTemplate.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                QuestionnareAssignQueryDTO item = new QuestionnareAssignQueryDTO();
                item.setId(rs.getLong("id"));
                item.setQuantification(rs.getBoolean("quantification"));
                item.setTeachingClassCode(rs.getString("teaching_class_code"));
                item.setTeachingClassName(rs.getString("teaching_class_name"));
                item.setTeachingClassId(rs.getLong("teaching_class_id"));
                item.setCourseName(rs.getString("course_name"));
                item.setTeacherName(rs.getString("TEACHER_NAME"));
                int assesCount = rs.getInt("assesCount");
                long totalScore = rs.getLong("totalScore");
                item.setAssessCount(assesCount);
                item.setTotalScore(totalScore);
                item.setAssAndCanStuatus(rs.getString("assAndCanStuatus"));
                float temp = (float) totalScore
                        / (assesCount == 0 ? 1 : assesCount);
                NumberFormat nt = NumberFormat.getNumberInstance();
                nt.setMinimumFractionDigits(2);

                item.setAvageScore(nt.format(temp));
                item.setTotalCount(rs.getInt("totalCount"));
                item.setAssginDate(DateFormatUtil.formatShort(rs.getDate("created_date")));
                item.setStatus(rs.getString("status"));
                return item;
            }
        }));
        return page;
    }


    public List<QuestionnaireCensusDTO> getQuestionnaireInfo(Long studentId, Integer type, Integer status) {

        String sql = "SELECT   dqas.ID as dqasId,dqa.Id as dqaId,dq.id as dqId ,dqa.COURSE_ID, dqa.TEACHER_ID,   " +
                "dqa.COURSE_NAME,    dqa.COURSE_CODE,    dqa.TEACHER_NAME,  dq. NAME,   dq.END_DATE,dqas.created_date,  " +
                " dqas.created_by , dqa.created_by as adm, dqa.delete_flag,dqa.status FROM    dd_questionnaire_assgin_students dqas " +
                " LEFT JOIN dd_questionnaire_assgin dqa ON dqas.QUESTIONNAIRE_ASSGIN_ID = dqa.ID " +
                "LEFT JOIN dd_questionnaire dq ON dqa.QUESTIONNAIRE_ID = dq.ID where dqa.delete_flag='0' ";

        sql += " and dqas.STUDENT_ID =" + studentId;
        if (null != status && status > 0) {
            sql += " and dqas.`status` =" + status;
        }
        if (type != null && type > 0) {
            sql += " and dq.ques_type='" + type + "'";
        }
        sql += " ORDER  by dqas.last_modified_date desc";


        return jdbcTemplate.query(sql, new RowMapper<QuestionnaireCensusDTO>() {
            @Override
            public QuestionnaireCensusDTO mapRow(ResultSet rs, int i) throws SQLException {
                QuestionnaireCensusDTO questionnaireCensusDTO = new QuestionnaireCensusDTO();
                //分配学生ID
                questionnaireCensusDTO.setId(rs.getLong("dqId"));
                questionnaireCensusDTO.setQuestionnaireAssignId(rs.getLong("dqaID"));
                questionnaireCensusDTO.setQuestionnaireAssignStudentId(rs.getLong("dqasId"));
                questionnaireCensusDTO.setName(rs.getString("name"));
                questionnaireCensusDTO.setTeacherId(rs.getLong("TEACHER_ID"));
                questionnaireCensusDTO.setTeacherName(rs.getString("TEACHER_NAME"));
                questionnaireCensusDTO.setCreateDate(rs.getTimestamp("created_date"));
                questionnaireCensusDTO.setCourseId(rs.getLong("COURSE_ID"));
                questionnaireCensusDTO.setCourseName(rs.getString("COURSE_NAME"));
                questionnaireCensusDTO.setCourseCode(rs.getString("COURSE_CODE"));
                questionnaireCensusDTO.setEnddate(rs.getDate("END_DATE"));
                int status = rs.getInt("status");
                questionnaireCensusDTO.setQuestionnaireStatus(status == 20 ? "remove" : String.valueOf(status));
                questionnaireCensusDTO.setSystemDate(new Date());
                questionnaireCensusDTO.setAdminName("管理员");
                return questionnaireCensusDTO;
            }
        });
    }


    public List<PartStatisticsDTO> partStatistics(Long questionnaireAssginId) {

        String sql = "SELECT   dq.id,dq.name,dq.no,dqar.score , COUNT(1) AS countNum        FROM  dd_question_answer_record dqar"
                + " LEFT JOIN dd_questions dq ON dq.id = dqar.questions_id   LEFT JOIN dd_questionnaire_assgin_students dqas ON dqas.id = dqar.questionnaire_assgin_students_id "
                + " LEFT JOIN dd_questionnaire_assgin dqa ON dqa.id=dqas.questionnaire_assgin_id   where  dqa.id = " + questionnaireAssginId + "   GROUP BY dq.id,dqar.score";
        return jdbcTemplate.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                PartStatisticsDTO item = new PartStatisticsDTO();
                item.setQuestionId(rs.getLong("id"));
                item.setNo(rs.getInt("no"));
                item.setQuestionName(rs.getString("name"));
                item.setScore(rs.getInt("score"));
                item.setCount(rs.getInt("countNum"));
                return item;
            }
        });
    }
}
