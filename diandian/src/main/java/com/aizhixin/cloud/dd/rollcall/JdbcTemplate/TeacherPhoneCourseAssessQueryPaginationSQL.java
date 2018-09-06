package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import com.aizhixin.cloud.dd.communication.jdbcTemplate.PaginationSQL;
import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;

import lombok.Getter;
import lombok.Setter;

public class TeacherPhoneCourseAssessQueryPaginationSQL implements PaginationSQL {

	private static String FIND_SQL = "SELECT " 
			+ "dd.`TEACHINGCLASS_ID`, " 
			+ "dd.`teachingclass_code`, " 
			+ "dd.`COURSE_NAME`, "
			+ "SUM(CASE WHEN ass.`score` = 5 AND ass.`DELETE_FLAG`= 0 THEN 1 ELSE 0 END) AS score5, "
			+ "SUM(CASE WHEN ass.`score` = 4 AND ass.`DELETE_FLAG`= 0 THEN 1 ELSE 0 END) AS score4, "
			+ "SUM(CASE WHEN ass.`score` = 3 AND ass.`DELETE_FLAG`= 0 THEN 1 ELSE 0 END) AS score3, "
			+ "SUM(CASE WHEN ass.`score` = 2 AND ass.`DELETE_FLAG`= 0 THEN 1 ELSE 0 END) AS score2, "
			+ "SUM(CASE WHEN ass.`score` = 1 AND ass.`DELETE_FLAG`= 0 THEN 1 ELSE 0 END) AS score1, "
			+ "ROUND((SUM(ass.`score`)/COUNT(ass.`schedule_id`)),2) AS averageScore, "
			+ "COUNT(ass.`schedule_id`)  AS allAssessNum "
			+ "FROM dd_assess ass RIGHT JOIN dd_schedule dd ON ass.`schedule_id` = dd.`ID` "
			+ "WHERE 1=1 #teacherId# GROUP BY dd.`TEACHINGCLASS_ID`";

	public static String FIND_COUNT_SQL = "SELECT COUNT(1) FROM "
			+ "(SELECT COUNT(ass.`schedule_id`) FROM dd_assess ass RIGHT JOIN dd_schedule dd "
			+ "ON ass.`schedule_id` = dd.`ID` "
			+ "WHERE 1=1 #teacherId# GROUP BY dd.`TEACHINGCLASS_ID`) AS num";
	@Getter
	@Setter
	private Long teacherId;// 教师id

	public TeacherPhoneCourseAssessQueryPaginationSQL(Long teacherId) {
		super();
		this.teacherId = teacherId;
	}

	@Override
	public String getFindCountSql() {
		String sql = FIND_COUNT_SQL;
		if (teacherId != null && !"".equals(teacherId)) {
			sql = sql.replace("#teacherId#", "AND dd.`TEACHER_ID` = '" + teacherId + "' ");
		} else {
			sql = sql.replace("#teacherId#", "");
		}
		return sql;
	}

	@Override
	public String getFindSql() {
		String sql = FIND_SQL;
		if (teacherId != null && !"".equals(teacherId)) {
			sql = sql.replace("#teacherId#", "AND dd.`TEACHER_ID` = '" + teacherId + "' ");
		} else {
			sql = sql.replace("#teacherId#", "");
		}
		return sql;
	}

	@Override
	public List<SortDTO> sort() {
		List<SortDTO> list = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		dto.setAsc(false);
		dto.setKey("dd.`teachingclass_code`");
		list.add(dto);
		return list;
	}

}
