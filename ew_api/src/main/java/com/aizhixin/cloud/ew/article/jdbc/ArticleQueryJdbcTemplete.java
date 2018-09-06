package com.aizhixin.cloud.ew.article.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.aizhixin.cloud.ew.article.domain.ArticleDomain;
import com.aizhixin.cloud.ew.common.jdbc.PaginationJDBCTemplate;

@Repository
public class ArticleQueryJdbcTemplete extends PaginationJDBCTemplate<ArticleDomain> {

	public static final RowMapper<ArticleDomain> beanMapper = new RowMapper<ArticleDomain>() {
		public ArticleDomain mapRow(ResultSet rs, int rowNum) throws SQLException {
			ArticleDomain item = new ArticleDomain();
			item.setId(rs.getLong("id"));
			item.setClassificationId(rs.getLong("classification_id"));
			item.setClassificationName(rs.getString("name"));
			item.setTitle(rs.getString("title"));
//			item.setPicUrl(rs.getString("picurl"));
//			item.setContent(rs.getString("content"));
			item.setHitCount(rs.getLong("hitcount"));
			item.setCommentCount(rs.getLong("commentCount"));
			item.setPraiseCount(rs.getLong("praiseCount"));
			item.setLinkUrl(rs.getString("linkUrl"));
			item.setPublished(rs.getInt("published"));
			item.setOpenComment(rs.getBoolean("openComment"));
			item.setPublishDate(rs.getDate("createDate").toString().substring(0, 10));
			return item;
		}
	};
}
