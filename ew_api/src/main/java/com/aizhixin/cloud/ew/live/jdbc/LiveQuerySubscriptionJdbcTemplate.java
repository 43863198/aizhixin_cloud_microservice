package com.aizhixin.cloud.ew.live.jdbc;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.aizhixin.cloud.ew.live.domain.LiveSubscriptionDomain;

@Repository
public class LiveQuerySubscriptionJdbcTemplate {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public List<LiveSubscriptionDomain> queryList() {
        String sql = "SELECT DISTINCT * FROM live_subscription WHERE TO_DAYS(publishTime) =  TO_DAYS(NOW()) AND NOW() <=publishTime AND STATUS =1 ORDER BY publishTime ASC";

        return jdbcTemplate.query(sql, new RowMapper() {

            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                LiveSubscriptionDomain item= new LiveSubscriptionDomain();
                item.setId(rs.getLong("id"));
                item.setVideoId(rs.getLong("videoId"));
                item.setUserId(rs.getLong("userId"));
                item.setTypeId(rs.getLong("typeId"));
                item.setStatus(rs.getString("status"));
                item.setPublishTime(rs.getTimestamp("publishTime"));
                item.setSubscriptionTime(rs.getTimestamp("subscriptionTime"));
                return item;
            }
        });
    }

    }

