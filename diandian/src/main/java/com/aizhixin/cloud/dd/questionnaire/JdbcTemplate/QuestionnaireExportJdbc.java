package com.aizhixin.cloud.dd.questionnaire.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aizhixin.cloud.dd.questionnaire.entity.Questions;
import com.aizhixin.cloud.dd.questionnaire.entity.QuestionsChoice;
import com.aizhixin.cloud.dd.rollcall.dto.StudentInfoDTOV2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.aizhixin.cloud.dd.questionnaire.domain.QuestionDomain;
import com.aizhixin.cloud.dd.questionnaire.dto.QuestionnaireStudentCommentDTO;

@Repository
public class QuestionnaireExportJdbc {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    //获取问卷平均分
    public Double avgScoreStu(Long questionnaireId) {
        String sql = "SELECT ROUND(AVG(s.sc),2) AS avg_score FROM (SELECT dqas.`SCORE` AS sc FROM `dd_questionnaire_assgin` AS dqa LEFT JOIN  `dd_questionnaire_assgin_students` AS dqas ON dqa.`ID`=dqas.QUESTIONNAIRE_ASSGIN_ID  WHERE dqas.`DELETE_FLAG`=0 AND dqa.`DELETE_FLAG`=0 AND dqa.`STATUS`='10' AND dqa.`QUESTIONNAIRE_ID`=" + questionnaireId + " AND dqas.`STATUS`=20 ) AS s";
        return jdbcTemplate.queryForObject(sql, Double.class);
    }

    public Double avgScoreUser(Long questionnaireId) {
        String sql = "SELECT ROUND(AVG(s.sc),2) AS avg_score FROM (SELECT dqau.`SCORE` AS sc FROM `dd_questionnaire_assgin` AS dqau WHERE dqau.`DELETE_FLAG`=0 AND dqau.`QUESTIONNAIRE_ID`=" + questionnaireId + " AND dqau.`commit_status`=20) AS s";
        return jdbcTemplate.queryForObject(sql, Double.class);
    }

    public Double avgScorePeer(Long questionnaireId) {
        String sql = "SELECT ROUND(AVG(s.sc),2) AS avg_score FROM (SELECT dqau.`weight_score` AS sc FROM `dd_questionnaire_assgin` AS dqau WHERE dqau.`DELETE_FLAG`=0 AND dqau.`QUESTIONNAIRE_ID`=" + questionnaireId + " AND dqau.`commit_status`=20) AS s";
        return jdbcTemplate.queryForObject(sql, Double.class);
    }

    //统计问卷调查总人数
    public Integer countTotalNumStu(Long questionnaireId) {
        String sql = "SELECT COUNT(dqas.`STUDENT_ID`) FROM `dd_questionnaire` AS dq LEFT JOIN `dd_questionnaire_assgin` AS dqa ON dq.`ID`=dqa.`QUESTIONNAIRE_ID` LEFT JOIN `dd_questionnaire_assgin_students` AS dqas ON dqa.`ID`=dqas.`QUESTIONNAIRE_ASSGIN_ID` WHERE dq.`ID`= " + questionnaireId + " AND dq.`DELETE_FLAG`=0 AND dqa.`DELETE_FLAG`=0 AND dqas.`DELETE_FLAG`=0 AND dqa.`STATUS`='10'";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public Integer countTotalNumUser(Long questionnaireId) {
        String sql = "SELECT COUNT(dqa.`ID`) FROM `dd_questionnaire_assgin` AS dqa WHERE dqa.QUESTIONNAIRE_ID=" + questionnaireId + " AND dqa.`DELETE_FLAG`=0";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    //获取问卷提交人数
    public Integer countCommitTotalStu(Long questionnaireId) {
        String sql = "SELECT COUNT(dqas.`STUDENT_ID`) FROM `dd_questionnaire_assgin` AS dqa LEFT JOIN  `dd_questionnaire_assgin_students` AS dqas ON dqa.`ID`=dqas.QUESTIONNAIRE_ASSGIN_ID  WHERE dqas.`DELETE_FLAG`=0 AND dqa.`DELETE_FLAG`=0 AND dqa.`STATUS`='10' AND dqas.`STATUS`=20 AND dqa.`QUESTIONNAIRE_ID`=" + questionnaireId;
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public Integer countCommitTotalUser(Long questionnaireId) {
        String sql = "SELECT COUNT(dqa.`ID`) FROM `dd_questionnaire_assgin` AS dqa WHERE dqa.`DELETE_FLAG`=0 and dqa.QUESTIONNAIRE_ID=" + questionnaireId + " and dqa.commit_status=20";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    //获取问卷试题平均分
    public Map<Long, String> countQuestionAvgScoreStu(Long questionnaireId) {
        String sql = "SELECT dqar.`QUESTIONS_ID` AS qid,ROUND(AVG(dqar.`SCORE`),2) AS avg_score FROM `dd_questions` AS dqs LEFT JOIN `dd_question_answer_record` AS dqar ON dqs.`ID`=dqar.`QUESTIONS_ID` WHERE dqs.`QUESTIONNAIRE_ID`=" + questionnaireId + " AND dqar.`QUESTIONNAIRE_ASSGIN_STUDENTS_ID` IN(SELECT  dqas.`id` FROM  `dd_questionnaire_assgin` AS dqa  LEFT JOIN `dd_questionnaire_assgin_students` AS dqas ON dqa.`ID`=dqas.`QUESTIONNAIRE_ASSGIN_ID`  WHERE dqa.`QUESTIONNAIRE_ID`=" + questionnaireId + " AND dqa.`STATUS`='10' AND dqas.`STATUS`=20 AND dqas.`STUDENT_ID` IS NOT NULL GROUP BY dqas.`STUDENT_ID`) GROUP BY dqar.`QUESTIONS_ID`";
        RowMapper<QuestionDomain> rowMapper = new RowMapper<QuestionDomain>() {
            @Override
            public QuestionDomain mapRow(ResultSet arg0, int arg1) throws SQLException {
                QuestionDomain qd = new QuestionDomain();
                qd.setQuestionId(arg0.getLong("qid"));
                qd.setAvgScore(arg0.getDouble("avg_score") + "");
                return qd;
            }
        };
        System.out.println("------------->>>  " + sql);
        List<QuestionDomain> data = jdbcTemplate.query(sql, rowMapper);
        Map<Long, String> map = new HashMap<>();
        if (null != data && 0 < data.size()) {
            for (QuestionDomain questionDomain : data) {
                map.put(questionDomain.getQuestionId(), questionDomain.getAvgScore());
            }
        }
        return map;
    }

    public Map<Long, String> countQuestionAvgScoreUser(Long questionnaireId) {
        String sql = "SELECT dqar.`QUESTIONS_ID` AS qid,ROUND(AVG(dqar.`SCORE`),2) AS avg_score FROM `dd_questions` AS dqs LEFT JOIN `dd_question_answer_record` AS dqar ON dqs.`ID`=dqar.`QUESTIONS_ID` WHERE dqs.`QUESTIONNAIRE_ID`=" + questionnaireId + " AND dqar.`QUESTIONNAIRE_ASSGIN_STUDENTS_ID` IN (SELECT dqa.id FROM `dd_questionnaire_assgin` AS dqa WHERE dqa.`QUESTIONNAIRE_ID`=" + questionnaireId + " AND dqa.`STATUS`='10' AND dqa.commit_status=20 AND dqa.DELETE_FLAG=0) GROUP BY dqar.`QUESTIONS_ID`";
        RowMapper<QuestionDomain> rowMapper = new RowMapper<QuestionDomain>() {
            @Override
            public QuestionDomain mapRow(ResultSet arg0, int arg1) throws SQLException {
                QuestionDomain qd = new QuestionDomain();
                qd.setQuestionId(arg0.getLong("qid"));
                qd.setAvgScore(arg0.getDouble("avg_score") + "");
                return qd;
            }
        };
        System.out.println("------------->>>  " + sql);
        List<QuestionDomain> data = jdbcTemplate.query(sql, rowMapper);
        Map<Long, String> map = new HashMap<>();
        if (null != data && 0 < data.size()) {
            for (QuestionDomain questionDomain : data) {
                map.put(questionDomain.getQuestionId(), questionDomain.getAvgScore());
            }
        }
        return map;
    }

    public Map<Long, String> countQuestionAvgScorePeer(Long questionnaireId) {
        String sql = "SELECT dqar.`QUESTIONS_ID` AS qid,ROUND(AVG(dqar.`weight_score`),2) AS avg_score FROM `dd_questions` AS dqs LEFT JOIN `dd_question_answer_record` AS dqar ON dqs.`ID`=dqar.`QUESTIONS_ID` WHERE dqs.`QUESTIONNAIRE_ID`=" + questionnaireId + " AND dqar.`QUESTIONNAIRE_ASSGIN_STUDENTS_ID` IN (SELECT dqa.id FROM `dd_questionnaire_assgin` AS dqa WHERE dqa.`QUESTIONNAIRE_ID`=" + questionnaireId + " AND dqa.`STATUS`='10' AND dqa.commit_status=20 AND dqa.DELETE_FLAG=0) GROUP BY dqar.`QUESTIONS_ID`";
        RowMapper<QuestionDomain> rowMapper = new RowMapper<QuestionDomain>() {
            @Override
            public QuestionDomain mapRow(ResultSet arg0, int arg1) throws SQLException {
                QuestionDomain qd = new QuestionDomain();
                qd.setQuestionId(arg0.getLong("qid"));
                qd.setAvgScore(arg0.getDouble("avg_score") + "");
                return qd;
            }
        };
        System.out.println("------------->>>  " + sql);
        List<QuestionDomain> data = jdbcTemplate.query(sql, rowMapper);
        Map<Long, String> map = new HashMap<>();
        if (null != data && 0 < data.size()) {
            for (QuestionDomain questionDomain : data) {
                map.put(questionDomain.getQuestionId(), questionDomain.getAvgScore());
            }
        }
        return map;
    }

    //获取选择题每个选项占比
    public Double getChoiceZbStu(Long questionId, String choice, Long questionnaireId) {
        String sql = "SELECT COUNT(*)  FROM `dd_question_answer_record` AS dqar WHERE dqar.`QUESTIONS_ID`=" + questionId + " AND dqar.`QUESTIONNAIRE_ASSGIN_STUDENTS_ID` IN (SELECT  dqas.`id` FROM  `dd_questionnaire_assgin` AS dqa  LEFT JOIN `dd_questionnaire_assgin_students` AS dqas ON dqa.`ID`=dqas.`QUESTIONNAIRE_ASSGIN_ID`  WHERE dqa.`QUESTIONNAIRE_ID`=" + questionnaireId + " AND dqa.`STATUS`='10' AND dqas.`STATUS`=20 AND dqas.`STUDENT_ID` IS NOT NULL GROUP BY dqas.`STUDENT_ID`)";
        String sql1 = "SELECT COUNT(*)  FROM `dd_question_answer_record` AS dqar WHERE dqar.`QUESTIONS_ID`=" + questionId + " AND dqar.`answer` LIKE '%" + choice + "%'  AND dqar.`QUESTIONNAIRE_ASSGIN_STUDENTS_ID` IN(SELECT  dqas.`id` FROM  `dd_questionnaire_assgin` AS dqa  LEFT JOIN `dd_questionnaire_assgin_students` AS dqas ON dqa.`ID`=dqas.`QUESTIONNAIRE_ASSGIN_ID`  WHERE dqa.`QUESTIONNAIRE_ID`=" + questionnaireId + " AND dqa.`STATUS`='10' AND dqas.`STATUS`=20 AND dqas.`STUDENT_ID` IS NOT NULL GROUP BY dqas.`STUDENT_ID`)";
        Double a = jdbcTemplate.queryForObject(sql, Double.class);
        Double b = jdbcTemplate.queryForObject(sql1, Double.class);
        if (a == 0) {
            return 0.00;
        }
        return b / a;
    }

    public Map<String, Object> getQuestionChoiceZbStu(Questions questions) {
        String choiceStr = "";
        for (QuestionsChoice questionsChoice : questions.getQuestionsChoice()) {
            if (StringUtils.isNotEmpty(choiceStr)) {
                choiceStr += ",";
            }
            choiceStr += " SUM(if(dqar.`answer` LIKE '%" + questionsChoice.getChoice() + "%', 1, 0)) " + questionsChoice.getChoice();
        }
        String sql = "SELECT COUNT(*) total, " + choiceStr + "  FROM `dd_question_answer_record` AS dqar WHERE dqar.`QUESTIONS_ID`=" + questions.getId() + " AND dqar.`QUESTIONNAIRE_ASSGIN_STUDENTS_ID` IN (SELECT  dqas.`id` FROM  `dd_questionnaire_assgin` AS dqa  LEFT JOIN `dd_questionnaire_assgin_students` AS dqas ON dqa.`ID`=dqas.`QUESTIONNAIRE_ASSGIN_ID`  WHERE dqa.`QUESTIONNAIRE_ID`=" + questions.getQuestionnaire().getId() + " AND dqa.`STATUS`='10' AND dqas.`STATUS`=20 AND dqas.`STUDENT_ID` IS NOT NULL GROUP BY dqas.`STUDENT_ID`)";
        Map<String, Object> a = jdbcTemplate.queryForMap(sql);
        return a;
    }

    public Map<String, Object> getQuestionChoiceZbUser(Questions questions) {
        String choiceStr = "";
        for (QuestionsChoice questionsChoice : questions.getQuestionsChoice()) {
            if (StringUtils.isNotEmpty(choiceStr)) {
                choiceStr += ",";
            }
            choiceStr += " SUM(if(dqar.`answer` LIKE '%" + questionsChoice.getChoice() + "%', 1, 0)) " + questionsChoice.getChoice();
        }
        String sql = "SELECT COUNT(*) total, " + choiceStr + " FROM `dd_question_answer_record` AS dqar WHERE dqar.`QUESTIONS_ID`=" + questions.getId() + " AND dqar.`QUESTIONNAIRE_ASSGIN_STUDENTS_ID` IN (SELECT dqa.id FROM `dd_questionnaire_assgin` AS dqa WHERE dqa.`QUESTIONNAIRE_ID`=" + questions.getQuestionnaire().getId() + " AND dqa.`STATUS`='10' AND dqa.`commit_status`=20 AND dqa.DELETE_FLAG=0)";
        Map<String, Object> a = jdbcTemplate.queryForMap(sql);
        return a;
    }

    public Double getChoiceZbUser(Long questionId, String choice, Long questionnaireId) {
        String sql = "SELECT COUNT(*) FROM `dd_question_answer_record` AS dqar WHERE dqar.`QUESTIONS_ID`=" + questionId + " AND dqar.`QUESTIONNAIRE_ASSGIN_STUDENTS_ID` IN (SELECT dqa.id FROM `dd_questionnaire_assgin` AS dqa WHERE dqa.`QUESTIONNAIRE_ID`=" + questionnaireId + " AND dqa.`STATUS`='10' AND dqa.`commit_status`=20 AND dqa.DELETE_FLAG=0)";
        String sql1 = "SELECT COUNT(*) FROM `dd_question_answer_record` AS dqar WHERE dqar.`QUESTIONS_ID`=" + questionId + " AND dqar.`answer` LIKE '%" + choice + "%' AND dqar.`QUESTIONNAIRE_ASSGIN_STUDENTS_ID` IN (SELECT dqa.id FROM `dd_questionnaire_assgin` AS dqa WHERE dqa.`QUESTIONNAIRE_ID`=" + questionnaireId + " AND dqa.`STATUS`='10' AND dqa.`commit_status`=20 AND dqa.DELETE_FLAG=0)";
        Double a = jdbcTemplate.queryForObject(sql, Double.class);
        Double b = jdbcTemplate.queryForObject(sql1, Double.class);
        if (a == 0) {
            return 0.00;
        }
        return b / a;
    }

    //获取未提交人数
    public List<Long> findByNoCommitPelpe(Long questionnaireId, Integer pageNumber, Integer pageSize) {
        String sql = "SELECT  DISTINCT dqas.`STUDENT_ID` FROM  `dd_questionnaire_assgin` AS dqa  LEFT JOIN `dd_questionnaire_assgin_students` AS dqas ON dqa.`ID`=dqas.`QUESTIONNAIRE_ASSGIN_ID`  WHERE dqa.`QUESTIONNAIRE_ID`=" + questionnaireId + " AND dqa.`STATUS`='10' AND dqas.`STATUS`=10 AND dqas.`STUDENT_ID` IS NOT NULL  LIMIT " + pageNumber + "," + pageSize;
        return jdbcTemplate.queryForList(sql, Long.class);
    }

    public List<StudentInfoDTOV2> findByNoCommitUserComment(Long questionnaireId, Integer pageNumber, Integer pageSize) {
        String sql = "SELECT  dqas.`user_id`,dqas.user_name,dqas.teacher_type,dqas.college_name, dqa.`TEACHING_CLASS_NAME`,dqa.`classes_name`  FROM  `dd_questionnaire_assgin` AS dqa  LEFT JOIN `dd_questionnaire_assgin_user` AS dqas ON dqas.user_id = dqa.CREATED_BY  WHERE dqas.ques_id=" + questionnaireId + " and dqa.`QUESTIONNAIRE_ID`=" + questionnaireId + " AND dqa.`STATUS`='10' AND dqa.commit_status=10 AND dqas.`DELETE_FLAG`=0 LIMIT " + pageNumber + "," + pageSize;
        RowMapper<StudentInfoDTOV2> rowMapper = new RowMapper<StudentInfoDTOV2>() {
            @Override
            public StudentInfoDTOV2 mapRow(ResultSet arg0, int arg1) throws SQLException {
                StudentInfoDTOV2 rscd = new StudentInfoDTOV2();
                rscd.setStuId(arg0.getLong("user_id"));
                rscd.setStuName(arg0.getString("user_name"));
                rscd.setCollegeName(arg0.getString("college_name"));
                rscd.setClassesName(arg0.getString("classes_name"));
                return rscd;
            }
        };
        return jdbcTemplate.query(sql, rowMapper);
    }


    //获取未提交总数
    public Integer countByNoCommitPelpe(Long questionnaireId) {
        String sql = "SELECT count(DISTINCT dqas.`STUDENT_ID`) FROM  `dd_questionnaire_assgin` AS dqa  LEFT JOIN `dd_questionnaire_assgin_students` AS dqas ON dqa.`ID`=dqas.`QUESTIONNAIRE_ASSGIN_ID`  WHERE dqa.`QUESTIONNAIRE_ID`=" + questionnaireId + " AND dqa.`STATUS`='10' AND dqas.`STATUS`=10 AND dqas.`STUDENT_ID` IS NOT NULL ";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public Integer countByNoCommitUser(Long questionnaireId) {
        String sql = "SELECT  count(dqa.id) FROM  `dd_questionnaire_assgin` AS dqa  LEFT JOIN `dd_questionnaire_assgin_user` AS dqas ON dqas.user_id = dqa.CREATED_BY  WHERE dqas.ques_id=" + questionnaireId + " and dqa.`QUESTIONNAIRE_ID`=" + questionnaireId + " AND dqa.`STATUS`='10' AND dqa.commit_status=10 AND dqas.`DELETE_FLAG`=0 ";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    //获取提交评语
    public List<QuestionnaireStudentCommentDTO> findByCommitPelpeComment(Long questionnaireId, Integer pageNumber, Integer pageSize) {
        String sql = "SELECT  dqas.`STUDENT_ID`,dqas.`comment`,dqa.`class_type`,dqa.`TEACHING_CLASS_NAME`,dqa.`classes_name`  FROM  `dd_questionnaire_assgin` AS dqa  LEFT JOIN `dd_questionnaire_assgin_students` AS dqas ON dqa.`ID`=dqas.`QUESTIONNAIRE_ASSGIN_ID`  WHERE dqa.`QUESTIONNAIRE_ID`=" + questionnaireId + " AND dqa.`STATUS`='10' AND dqas.`STATUS`=20 AND dqas.`STUDENT_ID` IS NOT NULL AND dqas.`comment` IS NOT NULL  LIMIT " + pageNumber + "," + pageSize;
        RowMapper<QuestionnaireStudentCommentDTO> rowMapper = new RowMapper<QuestionnaireStudentCommentDTO>() {
            @Override
            public QuestionnaireStudentCommentDTO mapRow(ResultSet arg0, int arg1) throws SQLException {

                QuestionnaireStudentCommentDTO rscd = new QuestionnaireStudentCommentDTO();
                rscd.setStuId(arg0.getLong("STUDENT_ID"));
                rscd.setComment(arg0.getString("comment"));
                rscd.setClassType(arg0.getInt("class_type"));
                rscd.setTeachingClassName(arg0.getString("TEACHING_CLASS_NAME"));
                rscd.setClassesName(arg0.getString("classes_name"));
                return rscd;
            }
        };
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<QuestionnaireStudentCommentDTO> findByCommitUserComment(Long questionnaireId, Integer pageNumber, Integer pageSize) {
        String sql = "SELECT  dqas.`user_id`,dqas.user_name,dqas.teacher_type,dqas.college_name, dqa.`comment`,dqa.`class_type`,dqa.`TEACHING_CLASS_NAME`,dqa.`classes_name`  FROM  `dd_questionnaire_assgin` AS dqa  LEFT JOIN `dd_questionnaire_assgin_user` AS dqas ON dqas.user_id = dqa.CREATED_BY  WHERE dqas.ques_id=" + questionnaireId + " and dqa.`QUESTIONNAIRE_ID`=" + questionnaireId + " AND dqa.`STATUS`='10' AND dqa.commit_status=20 AND dqas.`DELETE_FLAG`=0 AND dqa.`comment` IS NOT NULL  LIMIT " + pageNumber + "," + pageSize;
        RowMapper<QuestionnaireStudentCommentDTO> rowMapper = new RowMapper<QuestionnaireStudentCommentDTO>() {
            @Override
            public QuestionnaireStudentCommentDTO mapRow(ResultSet arg0, int arg1) throws SQLException {

                QuestionnaireStudentCommentDTO rscd = new QuestionnaireStudentCommentDTO();
                rscd.setStuId(arg0.getLong("user_id"));
                rscd.setStuName(arg0.getString("user_name"));
                rscd.setCollegeName(arg0.getString("college_name"));
                rscd.setComment(arg0.getString("comment"));
                rscd.setClassType(arg0.getInt("class_type"));
                rscd.setTeachingClassName(arg0.getString("TEACHING_CLASS_NAME"));
                rscd.setClassesName(arg0.getString("classes_name"));
                return rscd;
            }
        };
        return jdbcTemplate.query(sql, rowMapper);
    }

    //获取提交评语总数
    public Integer countByCommitPelpeComment(Long questionnaireId) {
        String sql = "SELECT count(dqas.`STUDENT_ID`)  FROM  `dd_questionnaire_assgin` AS dqa  LEFT JOIN `dd_questionnaire_assgin_students` AS dqas ON dqa.`ID`=dqas.`QUESTIONNAIRE_ASSGIN_ID`  WHERE dqa.`QUESTIONNAIRE_ID`=" + questionnaireId + " AND dqa.`STATUS`='10' AND dqas.`STATUS`=20 AND dqas.`STUDENT_ID` IS NOT NULL AND dqas.`comment` IS NOT NULL";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public Integer countByCommitUserComment(Long questionnaireId) {
        String sql = "SELECT  count(dqa.id) FROM  `dd_questionnaire_assgin` AS dqa  LEFT JOIN `dd_questionnaire_assgin_user` AS dqas ON dqas.user_id = dqa.CREATED_BY  WHERE dqas.ques_id=" + questionnaireId + " and dqa.`QUESTIONNAIRE_ID`=" + questionnaireId + " AND dqa.`STATUS`='10' AND dqa.commit_status=20 AND dqas.`DELETE_FLAG`=0 AND dqa.`comment` IS NOT NULL";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    /**********************************************************************************按班统计************************************************************************************************/
    //统计班级平均分
    public Double countByClassAvgScore(Long questionnaireAssginId) {
        String sql = "SELECT ROUND(AVG(s.sc),2) FROM (SELECT dqas.`SCORE` AS sc FROM `dd_questionnaire_assgin_students` AS dqas  WHERE dqas.`QUESTIONNAIRE_ASSGIN_ID`=" + questionnaireAssginId + " AND dqas.`STATUS`=20  GROUP BY dqas.`STUDENT_ID`) AS s";
        return jdbcTemplate.queryForObject(sql, Double.class);
    }

    //班调查人数
    public Integer countByClassTotalNum(Long questionnaireAssginId) {
        String sql = "SELECT COUNT(DISTINCT dqas.`STUDENT_ID`) FROM `dd_questionnaire_assgin_students` AS dqas WHERE dqas.`QUESTIONNAIRE_ASSGIN_ID`=" + questionnaireAssginId;
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    //班提交人数
    public Integer countByClassCommitPeple(Long questionnaireAssginId) {
        String sql = "SELECT COUNT(DISTINCT dqas.`STUDENT_ID`) FROM `dd_questionnaire_assgin_students` AS dqas  WHERE dqas.`QUESTIONNAIRE_ASSGIN_ID`=" + questionnaireAssginId + " AND dqas.`STATUS`=20";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    //班未提交人信息查询
    public List<Long> findByClassNoCommitPepleInfo(Long questionnaireAssginId, Integer pageNumber, Integer pageSize) {
        String sql = "SELECT DISTINCT dqas.`STUDENT_ID` FROM `dd_questionnaire_assgin_students` AS dqas  WHERE dqas.`QUESTIONNAIRE_ASSGIN_ID`=" + questionnaireAssginId + " AND dqas.`STATUS`=10  LIMIT " + pageNumber + "," + pageSize;
        return jdbcTemplate.queryForList(sql, Long.class);
    }

    //班未提交人总数
    public Integer countByClassNoCommitPepleInfo(Long questionnaireAssginId) {
        String sql = "SELECT count(DISTINCT dqas.`STUDENT_ID`)  FROM `dd_questionnaire_assgin_students` AS dqas WHERE dqas.`QUESTIONNAIRE_ASSGIN_ID`=" + questionnaireAssginId + " AND dqas.`STATUS`=10 ";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    //获取班问卷试题平均分
    public Map<Long, String> countClassQuestionAvgScore(Long questionnaireId, Long questionnaireAssginId) {
        String sql = "SELECT dqar.`QUESTIONS_ID` AS qid,ROUND(AVG(dqar.`SCORE`),2) AS avg_score FROM `dd_questions` AS dqs LEFT JOIN `dd_question_answer_record` AS dqar ON dqs.`ID`=dqar.`QUESTIONS_ID` LEFT JOIN `dd_questionnaire_assgin_students` AS dqas ON dqas.`ID`=dqar.`QUESTIONNAIRE_ASSGIN_STUDENTS_ID` WHERE dqs.`QUESTIONNAIRE_ID`=" + questionnaireId + " AND dqas.`QUESTIONNAIRE_ASSGIN_ID`=" + questionnaireAssginId + " GROUP BY dqar.`QUESTIONS_ID` ";
        RowMapper<QuestionDomain> rowMapper = new RowMapper<QuestionDomain>() {
            @Override
            public QuestionDomain mapRow(ResultSet arg0, int arg1) throws SQLException {

                QuestionDomain qd = new QuestionDomain();
                qd.setQuestionId(arg0.getLong("qid"));
                qd.setAvgScore(arg0.getDouble("avg_score") + "");
                return qd;
            }
        };
        List<QuestionDomain> data = jdbcTemplate.query(sql, rowMapper);
        Map<Long, String> map = new HashMap<>();
        if (null != data && 0 < data.size()) {
            for (QuestionDomain questionDomain : data) {
                map.put(questionDomain.getQuestionId(), questionDomain.getAvgScore());
            }
        }
        return map;
    }

    //按班获取选择题每个选项占比
    public Double getClassChoiceZb(Long questionId, String choice, Long questionnaireAssginId) {
        String sql = "SELECT COUNT(*)  FROM `dd_question_answer_record` AS dqar LEFT JOIN `dd_questionnaire_assgin_students` AS dqas ON dqas.`ID`=dqar.`QUESTIONNAIRE_ASSGIN_STUDENTS_ID` WHERE dqar.`QUESTIONS_ID`=" + questionId + " AND dqas.`QUESTIONNAIRE_ASSGIN_ID`=" + questionnaireAssginId;
        String sql1 = "SELECT COUNT(*)  FROM `dd_question_answer_record` AS dqar LEFT JOIN `dd_questionnaire_assgin_students` AS dqas ON dqas.`ID`=dqar.`QUESTIONNAIRE_ASSGIN_STUDENTS_ID`  WHERE dqar.`QUESTIONS_ID`=" + questionId + " AND dqar.`answer` LIKE '%" + choice + "%'  AND dqas.`QUESTIONNAIRE_ASSGIN_ID`=" + questionnaireAssginId;
        Double a = jdbcTemplate.queryForObject(sql, Double.class);
        Double b = jdbcTemplate.queryForObject(sql1, Double.class);
        if (a == 0) {
            return 0.00;
        }
        return b / a;
    }

    //获取按班提交评语
    public List<QuestionnaireStudentCommentDTO> findByClassCommitPelpeComment(Long questionnaireAssginId, Integer pageNumber, Integer pageSize) {
        String sql = "SELECT  dqas.`STUDENT_ID`,dqas.`comment`  FROM  `dd_questionnaire_assgin_students` AS dqas   WHERE dqas.`QUESTIONNAIRE_ASSGIN_ID`=" + questionnaireAssginId + " AND dqas.`STATUS`=20 AND dqas.`STUDENT_ID` IS NOT NULL AND dqas.`comment` IS NOT NULL  LIMIT " + pageNumber + "," + pageSize;
        RowMapper<QuestionnaireStudentCommentDTO> rowMapper = new RowMapper<QuestionnaireStudentCommentDTO>() {
            @Override
            public QuestionnaireStudentCommentDTO mapRow(ResultSet arg0, int arg1) throws SQLException {

                QuestionnaireStudentCommentDTO rscd = new QuestionnaireStudentCommentDTO();
                rscd.setStuId(arg0.getLong("STUDENT_ID"));
                rscd.setComment(arg0.getString("comment"));
                return rscd;
            }
        };
        return jdbcTemplate.query(sql, rowMapper);
    }

    //获取按班提交评语总数
    public Integer countByClassCommitPelpeComment(Long questionnaireAssginId) {
        String sql = "SELECT count(dqas.`STUDENT_ID`)  FROM  `dd_questionnaire_assgin_students` AS dqas   WHERE dqas.`QUESTIONNAIRE_ASSGIN_ID`=" + questionnaireAssginId + " AND dqas.`STATUS`=20 AND dqas.`STUDENT_ID` IS NOT NULL AND dqas.`comment` IS NOT NULL";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }


}
