package com.aizhixin.cloud.ew.live.jdbc;



import com.aizhixin.cloud.ew.live.domain.LiveContentDomain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class LiveQueryRecentlyNoLiveJdbcTemplate {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public List<LiveContentDomain> queryList() {
        String sql = "SELECT * FROM live_content WHERE publishTime >= NOW() AND STATUS =1 ORDER BY publishTime DESC";

        return jdbcTemplate.query(sql, new RowMapper() {

            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                LiveContentDomain item= new LiveContentDomain();
                item.setId(rs.getLong("id"));
                item.setTitle(rs.getString("title"));
                item.setName(rs.getString("name"));
                item.setPublishTime(rs.getTimestamp("publishTime"));
                return item;
            }
        });
    }

    }

