package com.aizhixin.cloud.ew.live.jdbc;

import com.aizhixin.cloud.ew.live.domain.LiveContentDomain;
import com.aizhixin.cloud.ew.common.jdbc.PaginationJDBCTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by DuanWei on 2017/6/6.
 */
@Repository
public class LiveQueryOkJdbcTemplate extends
        PaginationJDBCTemplate<LiveContentDomain> {

    public static final RowMapper<LiveContentDomain> liveContentMapper=new RowMapper<LiveContentDomain>() {
        @Override
        public LiveContentDomain mapRow(ResultSet rs, int rowNum) throws SQLException {
            LiveContentDomain  item=new LiveContentDomain();
            item.setId(rs.getLong("id"));
            item.setTitle(rs.getString("title"));
            item.setName(rs.getString("name"));
            item.setCoverPic(rs.getString("coverPic"));
            item.setChildPic(rs.getString("childPic"));
            item.setData(rs.getString("data"));
            item.setStatus(rs.getString("status"));
            item.setLiveStatus(rs.getString("LiveStatus"));
            item.setPublishTime(rs.getTimestamp("publishTime"));
            item.setUserId(rs.getLong("userId"));
            item.setTypeId(rs.getLong("typeId"));
            item.setOnlineNumber(rs.getLong("onlineNumber"));
            item.setVideoTime(rs.getString("videoTime"));
            if(rs.getString("Subscriptionstatus")==null){
                item.setSubscriptionStatus("0");
            }else{
                item.setSubscriptionStatus(rs.getString("Subscriptionstatus"));
            }
            return item;
        }
    };


}
