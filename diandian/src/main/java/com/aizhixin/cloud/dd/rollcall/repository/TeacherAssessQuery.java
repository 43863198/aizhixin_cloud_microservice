package com.aizhixin.cloud.dd.rollcall.repository;

import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.rollcall.dto.AssessDetailStudentDTO;
import com.aizhixin.cloud.dd.rollcall.dto.AssessGatherDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
public class TeacherAssessQuery {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<AssessGatherDTO> queryGather(Long teacherId, Long courseId, String beginTime, String endTime) {

        String sql = " SELECT das.schedule_id,  dsc.COURSE_NAME AS courseName,  dsc.TEACH_DATE AS teachTime,  dsc.PERIOD_NO AS periodName,  dsc.CLASSROOM_NAME AS classRoomName,   "
                + " SUM(das.`score`) AS totalScore, COUNT(1) AS assessNum,STR_TO_DATE(CONCAT( dsc.TEACH_DATE,\" \",dsc.START_TIME,':00'),'%Y-%m-%d %H:%i:%s') AS sortTime, SUM(CASE WHEN das.score = 5 THEN 1 ELSE 0 END) AS fiveStar, SUM(CASE WHEN das.score= "
                + " 4 THEN 1 ELSE 0 END) AS fourStar, SUM(CASE WHEN das.score = 3 THEN 1 ELSE 0 END) AS threeStar , SUM(CASE WHEN das.score = 2 THEN 1 ELSE 0   "
                + " END) AS towStar, SUM(CASE WHEN das.score = 1 THEN 1 ELSE 0 END) AS oneStar FROM dd_assess das   "
                + "  LEFT JOIN dd_schedule dsc ON dsc.id = das.`schedule_id`  "
                + "  WHERE das.`teacher_id` = #teacherId#  and das.`course_id` = #courseId#  and das.`created_date` > '#beginTime#'  "
                + " and das.`created_date` < '#endTime#'  GROUP BY das.`schedule_id`, dsc.PERIOD_ID ORDER BY sortTime DESC";

        if (null != teacherId) {
            sql = sql.replaceAll("#teacherId#", String.valueOf(teacherId));
        } else {
            sql = sql.replaceAll("#teacherId#", "0");
        }

        if (null != courseId) {
            sql = sql.replaceAll("#courseId#", String.valueOf(courseId));
        } else {
            sql = sql.replaceAll("#courseId#", "0");
        }

        if (StringUtils.isNotBlank(beginTime)) {
            sql = sql.replaceAll("#beginTime#", beginTime);
        } else {
            sql = sql.replaceAll("#beginTime#", "0");
        }

        if (StringUtils.isNotBlank(endTime)) {
            sql = sql.replaceAll("#endTime#", endTime);
        } else {
            sql = sql.replaceAll("#endTime#", "0");
        }

        return jdbcTemplate.query(sql, new RowMapper() {

            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                AssessGatherDTO item = new AssessGatherDTO();
                item.setScheduleId(rs.getLong("schedule_id"));
                item.setCourseName(rs.getString("courseName"));
                item.setTeachTime(rs.getString("teachTime"));
                item.setPeriod(rs.getString("periodName"));
                item.setClassRoomName(rs.getString("classRoomName"));
                item.setTotalScore(rs.getString("totalScore"));
                item.setAssessNum(rs.getInt("assessNum"));
                item.setFiveStar(rs.getInt("fiveStar"));
                item.setFourStar(rs.getInt("fourStar"));
                item.setThreeStar(rs.getInt("threeStar"));
                item.setTowStar(rs.getInt("towStar"));
                item.setOneStar(rs.getInt("oneStar"));
                String averageScore = "0";
                try {
                    float temp = (float) Integer.valueOf(item.getTotalScore())
                            / (item.getAssessNum() == 0 ? 1 : item.getAssessNum());
                    averageScore = String.valueOf(temp);
                } catch (NumberFormatException e) {
                    log.warn("Exception", e);
                }
                item.setAverageScore(averageScore);
                return item;
            }
        });
    }

    public List<AssessDetailStudentDTO> queryDetail(Long teacherId, Long courseId, String teachTime, Long scheduleId) {

        String sql =
                "   SELECT   das.student_id,dsc.TEACH_DATE AS teachTime,  dsc.PERIOD_NO AS periodName, "
                        + "   das.score,  das.last_modified_date,  "
                        + "    das.content,  dsc.CLASSROOM_NAME AS classRoomName  "
                        + "   FROM   dd_assess das  "
                        + "   LEFT JOIN dd_schedule dsc     ON dsc.`ID` = das.`schedule_id` "
                        + " WHERE dsc.`teacher_id` = #teacherId# #scheduleId# AND dsc.`course_id` = #courseId#     and dsc.TEACH_DATE = '#teachTime#' "
                        + " ORDER BY dsc.TEACH_DATE,dsc.`START_TIME` DESC ";

        if (null != teacherId) {
            sql = sql.replaceAll("#teacherId#", String.valueOf(teacherId));
        } else {
            sql = sql.replaceAll("#teacherId#", "0");
        }

        if (null != courseId) {
            sql = sql.replaceAll("#courseId#", String.valueOf(courseId));
        } else {
            sql = sql.replaceAll("#courseId#", "0");
        }

        if (StringUtils.isNotBlank(teachTime)) {
            sql = sql.replaceAll("#teachTime#", teachTime);
        } else {
            sql = sql.replaceAll("#teachTime#", "0");
        }

        if (null != scheduleId) {
            sql = sql.replaceAll("#scheduleId#", "AND das.`schedule_id` = " + scheduleId);
        } else {
            sql = sql.replaceAll("#scheduleId#", "");
        }
        return jdbcTemplate.query(sql, new RowMapper() {

            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                AssessDetailStudentDTO item = new AssessDetailStudentDTO();
                item.setStudentId(rs.getLong("student_id"));
                item.setTeachTime(rs.getString("teachTime"));
                item.setPeriodName(rs.getString("periodName"));
                item.setScore(rs.getInt("score"));
                try {
                    item.setAssessDate(DateFormatUtil.format(rs.getTimestamp("last_modified_date"),
                            DateFormatUtil.FORMAT_LONG));
                } catch (Exception e) {
                    log.warn("Exception", e);
                }
                item.setContent(rs.getString("content"));
                item.setClassRoomName(rs.getString("classRoomName"));
                return item;
            }
        });
    }
}
