package com.aizhixin.cloud.dd.questionnaire.JdbcTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class QuestionnaireStatisticsJdbc {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int getQuestionCountByQuesId(Long id) {
        String sql = "SELECT COUNT(*) count FROM dd_questions WHERE DELETE_FLAG=0 AND QUESTIONNAIRE_ID=" + id;
        Map map = jdbcTemplate.queryForMap(sql);
        if (map != null && map.get("count") != null) {
            return Integer.parseInt(map.get("count").toString());
        }
        return 0;
    }

    public List<Map<String, Object>> getCourseByQuesId(Long id) {
        String sql = "SELECT dqa.ID, dqa.COURSE_NAME,dqa.TEACHER_NAME,dqa.TEACHING_CLASS_NAME,(SELECT COUNT(*) FROM dd_questionnaire_assgin_students dqas WHERE dqas.DELETE_FLAG=0 AND dqas.QUESTIONNAIRE_ASSGIN_ID=dqa.ID) stucount FROM dd_questionnaire_assgin dqa WHERE dqa.DELETE_FLAG=0 AND dqa.`STATUS`=10 AND dqa.COURSE_NAME IS NOT NULL AND dqa.QUESTIONNAIRE_ID=" + id;
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getCourseQuestionAvgByAssginIdAndQuesId(String assginId, Long id) {
        String sql = "SELECT dqar.QUESTIONS_ID,dq.`NO`,ROUND(AVG(dqar.SCORE),2) avgscore FROM dd_question_answer_record dqar LEFT JOIN dd_questions dq ON dq.ID=dqar.QUESTIONS_ID LEFT JOIN dd_questionnaire_assgin_students dqas ON dqas.ID=dqar.QUESTIONNAIRE_ASSGIN_STUDENTS_ID LEFT JOIN dd_questionnaire_assgin dqa ON dqa.ID=dqas.QUESTIONNAIRE_ASSGIN_ID WHERE dqar.DELETE_FLAG=0 AND dq.QUESTIONNAIRE_ID=" + id + " AND dq.DELETE_FLAG=0 AND dqas.DELETE_FLAG=0 AND dqa.DELETE_FLAG=0 AND dqa.`STATUS`='10' AND dqa.ID=" + assginId + " GROUP BY dqar.QUESTIONS_ID ORDER BY dq.`NO`";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getCourseScoreByQuesId(Long id) {
        String sql = "SELECT dqa.COURSE_NAME,(SELECT COUNT(*) FROM dd_questionnaire_assgin da WHERE da.QUESTIONNAIRE_ID=dqa.QUESTIONNAIRE_ID AND da.COLLEGE_NAME=dqa.COLLEGE_NAME AND da.DELETE_FLAG=0 AND da.`STATUS`=10 AND dqa.COURSE_NAME IS NOT NULL AND da.TEACHER_NAME=dqa.TEACHER_NAME) teachercount,ROUND(AVG(scorett.SCORE),2) avgscore,ROUND(MAX(scorett.SCORE),2) maxscore,ROUND(MIN(scorett.SCORE),2) minscore,SUM(IF (scorett.SCORE< 80,1,0)) countx80,SUM(IF (scorett.SCORE>=80 AND scorett.SCORE< 90,1,0)) count8090,SUM(IF (scorett.SCORE>=90,1,0)) count90,SUM(scorett.stu) stucount FROM dd_questionnaire_assgin dqa LEFT JOIN (SELECT dr.QUESTIONNAIRE_ASSGIN_STUDENTS_ID id,SUM(dr.SCORE) SCORE,1 stu FROM dd_question_answer_record dr GROUP BY dr.QUESTIONNAIRE_ASSGIN_STUDENTS_ID) scorett ON scorett.ID IN (SELECT dqas.ID FROM dd_questionnaire_assgin_students dqas WHERE dqas.QUESTIONNAIRE_ASSGIN_ID=dqa.ID AND dqas.DELETE_FLAG=0) WHERE dqa.QUESTIONNAIRE_ID=" + id + " AND dqa.DELETE_FLAG=0 AND dqa.`STATUS`=10 AND dqa.COURSE_NAME IS NOT NULL GROUP BY dqa.COURSE_NAME";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getCourseCommitByQuest(Long id) {
        String sql = "SELECT dqa.COURSE_NAME,COUNT(*) stucount,SUM(IF (dqas.`STATUS`=20,1,0)) commitcount FROM dd_questionnaire_assgin dqa LEFT JOIN dd_questionnaire_assgin_students dqas ON dqas.QUESTIONNAIRE_ASSGIN_ID=dqa.ID WHERE dqa.QUESTIONNAIRE_ID=" + id + " AND dqa.DELETE_FLAG=0 AND dqa.`STATUS`=10 AND dqa.COURSE_NAME IS NOT NULL GROUP BY COURSE_NAME";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getCollgeByQuesId(Long id) {
        String sql = "SELECT dqa.COLLEGE_NAME,dqa.TEACHER_NAME,(SELECT COUNT(*) FROM dd_questionnaire_assgin_students dqas WHERE dqas.QUESTIONNAIRE_ASSGIN_ID=dqa.ID AND dqas.DELETE_FLAG=0) stucount FROM dd_questionnaire_assgin dqa WHERE dqa.QUESTIONNAIRE_ID=" + id + " AND dqa.DELETE_FLAG=0 AND dqa.`STATUS`=10 AND dqa.TEACHER_NAME IS NOT NULL GROUP BY dqa.TEACHER_NAME";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getCollgeQueByTeacherName(Long queId, String teacherName) {
        String sql = "SELECT dq.`NO`,ROUND(AVG(dqar.SCORE),2) avgscore FROM dd_question_answer_record dqar LEFT JOIN dd_questions dq ON dq.ID=dqar.QUESTIONS_ID LEFT JOIN dd_questionnaire_assgin_students dqas ON dqas.ID=dqar.QUESTIONNAIRE_ASSGIN_STUDENTS_ID LEFT JOIN dd_questionnaire_assgin dqa ON dqa.ID=dqas.QUESTIONNAIRE_ASSGIN_ID WHERE dqar.DELETE_FLAG=0 AND dqa.QUESTIONNAIRE_ID=" + queId + " AND dqa.DELETE_FLAG=0 AND dqa.`STATUS`=10 AND dqa.TEACHER_NAME='" + teacherName + "' GROUP BY dqar.QUESTIONS_ID ORDER BY dq.`NO`";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getCollgeScoreByQuesId(Long id) {
        String sql = "SELECT dqa.COLLEGE_NAME,(SELECT COUNT(*) FROM dd_questionnaire_assgin da WHERE da.QUESTIONNAIRE_ID=dqa.QUESTIONNAIRE_ID AND da.COLLEGE_NAME=dqa.COLLEGE_NAME AND da.DELETE_FLAG=0 AND da.`STATUS`=10 AND da.TEACHER_NAME=dqa.TEACHER_NAME) teachercount,ROUND(AVG(scorett.SCORE),2) avgscore,ROUND(MAX(scorett.SCORE),2) maxscore,ROUND(MIN(scorett.SCORE),2) minscore,SUM(IF (scorett.SCORE< 80,1,0)) countx80,SUM(IF (scorett.SCORE>=80 AND scorett.SCORE< 90,1,0)) count8090,SUM(IF (scorett.SCORE>=90,1,0)) count90,SUM(scorett.stu) stucount FROM dd_questionnaire_assgin dqa LEFT JOIN (SELECT dr.QUESTIONNAIRE_ASSGIN_STUDENTS_ID id,SUM(dr.SCORE) SCORE,1 stu FROM dd_question_answer_record dr GROUP BY dr.QUESTIONNAIRE_ASSGIN_STUDENTS_ID) scorett ON scorett.ID IN (SELECT dqas.ID FROM dd_questionnaire_assgin_students dqas WHERE dqas.QUESTIONNAIRE_ASSGIN_ID=dqa.ID AND dqas.DELETE_FLAG=0) WHERE dqa.QUESTIONNAIRE_ID=" + id + " AND dqa.DELETE_FLAG=0 AND dqa.`STATUS`=10 GROUP BY dqa.COLLEGE_NAME";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getClassCommitByQuest(Long id) {
        String sql = "SELECT dqas.CLASSES_NAME,COUNT(*) stucount,SUM(IF (dqas.`STATUS`=20,1,0)) commitcount FROM dd_questionnaire_assgin_students dqas LEFT JOIN dd_questionnaire_assgin dqa ON dqa.ID=dqas.QUESTIONNAIRE_ASSGIN_ID WHERE dqa.QUESTIONNAIRE_ID=" + id + " AND dqa.DELETE_FLAG=0 AND dqa.`STATUS`=10 GROUP BY dqas.CLASSES_ID";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getCollgeCommitByQuest(Long id) {
        String sql = "SELECT dqa.COLLEGE_NAME,COUNT(*) stucount,SUM(IF (dqas.`STATUS`=20,1,0)) commitcount FROM dd_questionnaire_assgin dqa LEFT JOIN dd_questionnaire_assgin_students dqas ON dqas.QUESTIONNAIRE_ASSGIN_ID=dqa.ID WHERE dqa.QUESTIONNAIRE_ID=" + id + " AND dqa.DELETE_FLAG=0 AND dqa.`STATUS`=10 GROUP BY dqa.COLLEGE_NAME";
        return jdbcTemplate.queryForList(sql);
    }
}
