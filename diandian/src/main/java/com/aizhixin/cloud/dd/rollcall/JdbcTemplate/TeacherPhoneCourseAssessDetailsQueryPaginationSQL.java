package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import com.aizhixin.cloud.dd.communication.jdbcTemplate.PaginationSQL;
import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;

import lombok.Getter;
import lombok.Setter;

public class TeacherPhoneCourseAssessDetailsQueryPaginationSQL implements PaginationSQL {

	private static String FIND_SQL = "SELECT "
			+ "dd.`COURSE_NAME`, "
			+ "dd.`teachingclass_code`, "
			+ "dd.`WEEK_NAME`, "
			+ "dd.`PERIOD_NO`, "
			+ "dd.`PERIOD_NUM`, "
			+ "ass.`content`, "
			+ "ass.`score`, "
			+ "ass.`CREATED_DATE` "
			+ "FROM dd_schedule dd RIGHT JOIN dd_assess ass "
			+ "ON dd.`ID` = ass.`schedule_id` WHERE 1 = 1  #teachingClassId# "
			+ "AND ass.`content` IS NOT NULL AND ass.`content` != '' AND ass.`DELETE_FLAG` = 0 ";

	public static String FIND_COUNT_SQL = "SELECT COUNT(1) "
			+ "FROM dd_schedule dd RIGHT JOIN dd_assess ass "
			+ "ON dd.`ID` = ass.`schedule_id` WHERE 1 = 1  #teachingClassId# "
			+ "AND ass.`content` IS NOT NULL AND ass.`content` != '' AND ass.`DELETE_FLAG` = 0 ";;
	@Getter
	@Setter
	private Long teachingClassId;// 教师id

	public TeacherPhoneCourseAssessDetailsQueryPaginationSQL(Long teachingClassId) {
		super();
		this.teachingClassId = teachingClassId;
	}

	@Override
	public String getFindCountSql() {
		String sql = FIND_COUNT_SQL;
		if (teachingClassId != null && !"".equals(teachingClassId)) {
			sql = sql.replace("#teachingClassId#", "AND dd.`TEACHINGCLASS_ID` = '" + teachingClassId + "' ");
		} else {
			sql = sql.replace("#teachingClassId#", "");
		}
		return sql;
	}

	@Override
	public String getFindSql() {
		String sql = FIND_SQL;
		if (teachingClassId != null && !"".equals(teachingClassId)) {
			sql = sql.replace("#teachingClassId#", "AND dd.`TEACHINGCLASS_ID` = '" + teachingClassId + "' ");
		} else {
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
