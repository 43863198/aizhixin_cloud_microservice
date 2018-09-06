package com.aizhixin.cloud.ew.news.jdbc;

import java.util.ArrayList;
import java.util.List;

import com.aizhixin.cloud.ew.common.jdbc.PaginationSQL;
import com.aizhixin.cloud.ew.common.jdbc.SortDTO;


public class NewsQueryPaginationSQL implements PaginationSQL {

	public static String FIND_SQL = " select n.id,n.title,n.hitcount,n.published, n.created_date" + " from n_news n "
			+ " where n.delete_flag =0 ";

	public static String FIND_COUNT_SQL = "SELECT count(1) " + " from n_news n " + " where n.delete_flag =0 ";

	public String getFindCountSql() {

		String sql = FIND_COUNT_SQL;

		// title
		if ((title != null) && (title.length() > 0)) {
			sql += " and n.title like '%" + title + "%'";
		}

		// 开始日期 String startDate
		if ((startDate != null) && (startDate.length() > 0)) {
			sql += " and n.created_Date >= '" + startDate + "'";
		}

		// 结束日期 String endDate
		if ((endDate != null) && (endDate.length() > 0)) {
			sql += " and n.created_Date <= '" + endDate + "'";
		}

		// organId
		if (organId != null) {
			sql += " and n.organId = " + organId;
		}

		// published
		if (published != null) {
			sql += " and n.published = " + published;
		}

		return sql;
	}

	public String getFindSql() {

		String sql = FIND_SQL;

		// title
		if ((title != null) && (title.length() > 0)) {
			sql += " and n.title like '%" + title + "%'";
		}

		// 开始日期 String startDate
		if ((startDate != null) && (startDate.length() > 0)) {
			sql += " and n.created_Date >= '" + startDate + "'";
		}

		// 结束日期 String endDate
		if ((endDate != null) && (endDate.length() > 0)) {
			sql += " and n.created_Date <= '" + endDate + "'";
		}

		// organId
		if (organId != null) {
			sql += " and n.organId = " + organId;
		}

		// published
		if (published != null) {
			sql += " and n.published = " + published;
		}

		return sql;
	}

	private String title;

	private String startDate;

	private String endDate;

	private Long organId;

	private Integer published;

	public NewsQueryPaginationSQL(String title, String startDate, String endDate, Long organId, Integer published) {
		this.setTitle(title);
		this.setStartDate(startDate);
		this.setEndDate(endDate);
		this.setOrganId(organId);
		this.setPublished(published);
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;

	}

	private void setStartDate(String startDate) {
		this.startDate = startDate;

	}

	@Override
	public List<SortDTO> sort() {
		List<SortDTO> list = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		dto.setAsc(false);
		dto.setKey("n.created_date");
		list.add(dto);
		return list;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getPublished() {
		return published;
	}

	public void setPublished(Integer published) {
		this.published = published;
	}

	public Long getOrganId() {
		return organId;
	}

	public void setOrganId(Long organId) {
		this.organId = organId;
	}

}
