package com.aizhixin.cloud.ew.news.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.aizhixin.cloud.ew.common.jdbc.PaginationJDBCTemplate;
import com.aizhixin.cloud.ew.news.domain.NewsDomain;

@Repository
public class NewsQueryJdbcTemplete extends PaginationJDBCTemplate<NewsDomain> {

	public static final RowMapper<NewsDomain> beanMapper = new RowMapper<NewsDomain>() {
		public NewsDomain mapRow(ResultSet rs, int rowNum) throws SQLException {
			NewsDomain item = new NewsDomain();
			item.setId(rs.getLong("id"));
			item.setTitle(rs.getString("title"));
			item.setHitCount(rs.getLong("hitcount"));
			item.setPublished(rs.getInt("published"));
			item.setPublishDate(rs.getString("created_date"));
			return item;
		}

	};
}
