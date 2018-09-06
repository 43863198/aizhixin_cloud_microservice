package com.aizhixin.cloud.ew.article.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.aizhixin.cloud.ew.article.domain.ArticleListDomain;
import com.aizhixin.cloud.ew.common.jdbc.PaginationJDBCTemplate;

@Repository
public class ArticleListQueryJdbcTemplete extends PaginationJDBCTemplate<ArticleListDomain> {

	public static final RowMapper<ArticleListDomain> beanMapper = new RowMapper<ArticleListDomain>() {
		public ArticleListDomain mapRow(ResultSet rs, int rowNum) throws SQLException {
			ArticleListDomain item = new ArticleListDomain();
			item.setId(rs.getLong("id"));
			item.setTitle(rs.getString("title"));
			item.setPicUrl(rs.getString("picurl"));
			item.setHitCount(rs.getLong("hitcount"));
			item.setPraiseCount(rs.getLong("praiseCount"));
			item.setLinkUrl(rs.getString("linkUrl"));
			return item;
		}
	};
}
