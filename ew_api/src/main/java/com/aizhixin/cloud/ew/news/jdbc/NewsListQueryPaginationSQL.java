package com.aizhixin.cloud.ew.news.jdbc;

import java.util.ArrayList;
import java.util.List;

import com.aizhixin.cloud.ew.common.jdbc.PaginationSQL;
import com.aizhixin.cloud.ew.common.jdbc.SortDTO;


public class NewsListQueryPaginationSQL implements PaginationSQL {

	public static String FIND_SQL = " select n.ID ,n.TITLE,n.PICURL1,n.PICURL2,n.PICURL3,n.HITCOUNT,n.CREATED_DATE created_date"
			+ " from n_news n join n_organs o on n.id =o.NEWS_ID "
			+ " where n.DELETE_FLAG =0 and n.PUBLISHED=1  and o.DELETE_FLAG=0 #organId# "
			+ " UNION ALL select ID ,TITLE,PICURL1,PICURL2,PICURL3,HITCOUNT,CREATED_DATE created_date"
			+ " from n_news n1 where n1.ALL_FLAG =1 and n1.DELETE_FLAG=0 ";

	public static String FIND_COUNT_SQL = "SELECT count(1) " + " from "
			+ "(select n.ID ,n.TITLE,n.PICURL1,n.PICURL2,n.PICURL3,n.HITCOUNT,n.CREATED_DATE created_date "
			+ " from n_news n join n_organs o on n.id =o.NEWS_ID "
			+ " where n.DELETE_FLAG =0 and n.PUBLISHED=1  and o.DELETE_FLAG=0 #organId# "
			+ " UNION ALL select n1.ID ,n1.TITLE,n1.PICURL1,n1.PICURL2,n1.PICURL3,n1.HITCOUNT,n1.CREATED_DATE created_date "
			+ " from n_news n1 where n1.ALL_FLAG =1 and n1.DELETE_FLAG=0 ) A ";

	public String getFindCountSql() {

		String sql = FIND_COUNT_SQL;
		if (organId != null && !"".equals(organId)) {
			sql = sql.replace("#organId#", "and o.ORGAN_ID = '" + organId + "' ");
		} else {
			sql = sql.replace("#organId#", "");
		}
		return sql;
	}

	private Long organId;

	public String getFindSql() {

		String sql = FIND_SQL;
		if (organId != null && !"".equals(organId)) {
			sql = sql.replace("#organId#", "and o.ORGAN_ID = '" + organId + "' ");
		} else {
			sql = sql.replace("#organId#", "");
		}
		return sql;
	}

	public NewsListQueryPaginationSQL(Long organId) {
		this.setOrganId(organId);
	}

	@Override
	public List<SortDTO> sort() {
		List<SortDTO> list = new ArrayList<SortDTO>();
		SortDTO dto = new SortDTO();
		dto.setAsc(false);
		dto.setKey("created_date");
		list.add(dto);
		return list;
	}

	public Long getOrganId() {
		return organId;
	}

	public void setOrganId(Long organId) {
		this.organId = organId;
	}

}
