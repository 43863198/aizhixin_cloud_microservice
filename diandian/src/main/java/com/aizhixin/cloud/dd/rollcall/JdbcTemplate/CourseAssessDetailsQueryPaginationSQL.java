package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import com.aizhixin.cloud.dd.communication.jdbcTemplate.PaginationSQL;
import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;

import lombok.Getter;
import lombok.Setter;

public class CourseAssessDetailsQueryPaginationSQL implements PaginationSQL {

	private static String FIND_SQL = "SELECT " 
			+ "ass.`schedule_id`, "
			+ "dd.`SEMESTER_NAME`, "
			+ "dd.`teachingclass_code`, "
			+ "dd.`TEACHER_NAME`, " 
			+ "dd.`COURSE_NAME`, " 
			+ "dd.`TEACH_DATE`, " 
			+ "dd.`PERIOD_NO`, "
			+ "dd.`TEACHER_NAME`, "
			+ "dd.`CLASSROOM_NAME`, " 
			+ "COUNT(1) AS assessNum, "
			+ "SUM(CASE WHEN ass.`score` = 5 THEN 1 ELSE 0 END) AS fiveStar, "
			+ "SUM(CASE WHEN ass.`score` = 4 THEN 1 ELSE 0 END) AS fourStar, "
			+ "SUM(CASE WHEN ass.`score` = 3 THEN 1 ELSE 0 END) AS threeStar, "
			+ "SUM(CASE WHEN ass.`score` = 2 THEN 1 ELSE 0 END) AS towStar, "
			+ "SUM(CASE WHEN ass.`score` = 1 THEN 1 ELSE 0 END) AS oneStar "
			+ "FROM dd_assess ass LEFT JOIN dd_schedule dd "
			+ "ON ass.`schedule_id`= dd.`ID` "
			+ "WHERE 1 = 1 AND ass.`DELETE_FLAG` = 0 #teachingClassId# GROUP BY ass.`schedule_id` ";

	public static String FIND_COUNT_SQL = "SELECT "
			+ "COUNT(1) "
			+ "FROM (SELECT COUNT(1) FROM dd_assess ass LEFT JOIN dd_schedule dd "
			+ "ON ass.`schedule_id`= dd.`ID` "
			+ "WHERE 1 = 1 AND ass.`DELETE_FLAG` = 0 #teachingClassId# GROUP BY ass.`schedule_id`) AS num ";
	@Getter
	@Setter
	private Long teachingClassId;// 教学班id

	public CourseAssessDetailsQueryPaginationSQL(Long teachingClassId) {
		super();
		this.teachingClassId = teachingClassId;
	}

	@Override
	public String getFindCountSql() {
		String sql = FIND_COUNT_SQL;
		if (teachingClassId != null && !"".equals(teachingClassId)) {
			sql = sql.replace("#teachingClassId#", " AND dd.`TEACHINGCLASS_ID` = '" + teachingClassId + "'");
		}else {
			sql = sql.replace("#teachingClassId#", "");
		}
		return sql;
	}

	@Override
	public String getFindSql() {
		String sql = FIND_SQL;
		if (teachingClassId != null && !"".equals(teachingClassId)) {
			sql = sql.replace("#teachingClassId#", " AND dd.`TEACHINGCLASS_ID` = '" + teachingClassId + "'");
		}else {
			sql = sql.replace("#teachingClassId#", "");
		}
		return sql;
	}

	@Override
	public List<SortDTO> sort() {
		List<SortDTO> list = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		dto.setAsc(false);
		dto.setKey("ass.`CREATED_DATE`");
		list.add(dto);
		return list;
	}

}
