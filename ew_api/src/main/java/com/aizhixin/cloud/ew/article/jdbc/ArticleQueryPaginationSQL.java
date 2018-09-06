package com.aizhixin.cloud.ew.article.jdbc;

import java.util.ArrayList;
import java.util.List;

import com.aizhixin.cloud.ew.common.jdbc.PaginationSQL;
import com.aizhixin.cloud.ew.common.jdbc.SortDTO;

public class ArticleQueryPaginationSQL implements PaginationSQL {

	public static String FIND_SQL = " select DISTINCT "
//			+ "a.content,a.picurl,
			+ " a.id,a.title,a.linkUrl,a.classification_id,c.name,a.hitcount, "
			+ " a.praiseCount,a.commentCount,a.OPEN_COMMENT openComment,a.published,a.CREATED_DATE createDate "
			+ " from am_article a join am_article_lable al on a.id=al.ARTICLE_ID "
			+ " join am_classification c on a.CLASSIFICATION_ID=c.id "
			+ " where a.DELETE_FLAG=0 and al.DELETE_FLAG=0 ";

	public static String FIND_COUNT_SQL = "SELECT DISTINCT count(DISTINCT a.id ) "
			+ " from am_article a join am_article_lable al on a.id=al.ARTICLE_ID "
			+ " join am_classification c on a.CLASSIFICATION_ID=c.id "
			+ " where a.DELETE_FLAG=0 and al.DELETE_FLAG=0 ";

	public String getFindCountSql() {

		String sql = FIND_COUNT_SQL;

		// title
		if ((title != null) && (title.length() > 0)) {
			sql += " and a.title like '%" + title + "%'";
		}

		// 开始日期 String startDate
		if ((startDate != null) && (startDate.length() > 0)) {
			sql += " and a.created_Date >= '" + startDate + "'";
		}

		// 结束日期 String endDate
		if ((endDate != null) && (endDate.length() > 0)) {
			sql += " and a.created_Date <= '" + endDate + "'";
		}
		// classificationId
		if (classificationId != null) {
			sql += " and a.classification_id = " + classificationId;
		}
		// lableId
		if (lableId != null) {
			sql += " and al.lable_id = " + lableId;
		}
		// published
		if (published != null) {
			sql += " and a.published = " + published;
		}

		return sql;
	}

	public String getFindSql() {

		String sql = FIND_SQL;

		// title
		if ((title != null) && (title.length() > 0)) {
			sql += " and a.title like '%" + title + "%'";
		}

		// 开始日期 String startDate
		if ((startDate != null) && (startDate.length() > 0)) {
			sql += " and a.created_Date >= '" + startDate + "'";
		}

		// 结束日期 String endDate
		if ((endDate != null) && (endDate.length() > 0)) {
			sql += " and a.created_Date <= '" + endDate + "'";
		}
		// classificationId
		if (classificationId != null) {
			sql += " and a.classification_id = " + classificationId;
		}
		// lableId
		if (lableId != null) {
			sql += " and al.lable_id = " + lableId;
		}
		// published
		if (published != null) {
			sql += " and a.published = " + published;
		}
		return sql;
	}

	private String title;

	private String startDate;

	private String endDate;

	private Long classificationId;

	private Long lableId;

	private Integer published;

	public ArticleQueryPaginationSQL(String title, String startDate, String endDate, Long classificationId,
			Long lableId, Integer published) {
		this.setTitle(title);
		this.setStartDate(startDate);
		this.setEndDate(endDate);
		this.setClassificationId(classificationId);
		this.setLableId(lableId);
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
		dto.setKey("a.last_modified_date");
		list.add(dto);
		return list;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getClassificationId() {
		return classificationId;
	}

	public void setClassificationId(Long classificationId) {
		this.classificationId = classificationId;
	}

	public Long getLableId() {
		return lableId;
	}

	public void setLableId(Long lableId) {
		this.lableId = lableId;
	}

	public Integer getPublished() {
		return published;
	}

	public void setPublished(Integer published) {
		this.published = published;
	}

}
