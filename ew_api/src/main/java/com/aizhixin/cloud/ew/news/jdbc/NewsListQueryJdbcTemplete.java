package com.aizhixin.cloud.ew.news.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.aizhixin.cloud.ew.common.jdbc.PaginationJDBCTemplate;
import com.aizhixin.cloud.ew.news.domain.NewsDomain;

@Repository
public class NewsListQueryJdbcTemplete extends PaginationJDBCTemplate<NewsDomain> {

	public static final RowMapper<NewsDomain> beanMapper = new RowMapper<NewsDomain>() {
		public NewsDomain mapRow(ResultSet rs, int rowNum) throws SQLException {
			NewsDomain item = new NewsDomain();
			item.setId(rs.getLong("id"));
			item.setTitle(rs.getString("title"));
			item.setPicUrl(rs.getString("picurl1"));
			item.setPicUr2(rs.getString("picurl2"));
			item.setPicUr3(rs.getString("picurl3"));
			item.setHitCount(rs.getLong("hitcount"));
			item.setPublishDate(rs.getString("created_date"));
			return item;
		}

	};
}
