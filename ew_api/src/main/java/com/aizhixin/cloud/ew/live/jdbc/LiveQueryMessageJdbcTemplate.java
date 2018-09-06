package com.aizhixin.cloud.ew.live.jdbc;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.aizhixin.cloud.ew.common.jdbc.PaginationJDBCTemplate;
import com.aizhixin.cloud.ew.live.domain.LiveCommentDomain;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by DuanWei on 2017/6/6.
 */
@Repository
public class LiveQueryMessageJdbcTemplate extends
        PaginationJDBCTemplate<LiveCommentDomain> {

    public static final RowMapper<LiveCommentDomain> liveContentMapper=new RowMapper<LiveCommentDomain>() {
        @Override
        public LiveCommentDomain mapRow(ResultSet rs, int rowNum) throws SQLException {
            LiveCommentDomain  item=new LiveCommentDomain();
            item.setId(rs.getLong("id"));
            item.setUserId(rs.getLong("userId"));
            item.setVideoId(rs.getLong("videoId"));
            item.setName(rs.getString("name"));
            item.setText(rs.getString("text"));
            item.setCommentTime(rs.getTimestamp("commentTime"));
            return item;
        }
    };


}
