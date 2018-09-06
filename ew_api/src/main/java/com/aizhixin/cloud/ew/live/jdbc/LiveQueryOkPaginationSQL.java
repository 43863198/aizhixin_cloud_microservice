package com.aizhixin.cloud.ew.live.jdbc;


import com.aizhixin.cloud.ew.common.jdbc.PaginationSQL;
import com.aizhixin.cloud.ew.common.jdbc.SortDTO;

import java.util.List;

public class LiveQueryOkPaginationSQL implements PaginationSQL {
    //0 已经结束的(回播) 1 未开始的 (预播) 2 正在直播 3 正直播+已结束(组合)
    public static String FIND_SQL = "SELECT ll.*, ss.status AS Subscriptionstatus FROM(SELECT  * FROM live_content l WHERE 1 = 1";

    public Integer status;
    public Long userId;

    public LiveQueryOkPaginationSQL(Integer status, Long userId) {
        this.status = status;
        this.userId = userId;
    }

    @Override
    public String getFindSql() {
        String sql = FIND_SQL;
        if (!(status == null)) {
            if (status == 0) {
                sql += " AND (NOW() >DATE_ADD(publishTime,INTERVAL videoTime HOUR_SECOND))";
            }
            if (status == 1) {
                sql += " AND (NOW() <publishTime)";
            }
            if (status == 2) {
                sql += " AND (NOW() <DATE_ADD(publishTime,INTERVAL videoTime HOUR_SECOND) AND NOW()>=publishTime)";
            }
            if (status == 3) {
                sql += " AND (NOW() <DATE_ADD(publishTime,INTERVAL videoTime HOUR_SECOND) AND NOW()>=publishTime OR NOW() >DATE_ADD(publishTime,INTERVAL videoTime HOUR_SECOND))";
            }
        }
        sql += ") ll LEFT JOIN live_subscription ss ON ll.id = ss.videoId";
        if (userId != null) {
            sql += " AND ss.userId =" + userId;
        }
        sql += " ORDER BY publishTime DESC";
        return sql;
    }

    @Override
    public String getFindCountSql() {
        String sql = "SELECT COUNT(0)  FROM (SELECT  * FROM live_content l WHERE 1 = 1";
        if (!(status == null)) {
            if (status == 0) {
                sql += " AND (NOW() >DATE_ADD(publishTime,INTERVAL videoTime HOUR_SECOND))";
            }
            if (status == 1) {
                sql += " AND (NOW() <publishTime)";
            }
            if (status == 2) {
                sql += " AND (NOW() <DATE_ADD(publishTime,INTERVAL videoTime HOUR_SECOND) AND NOW()>=publishTime)";
            }
            if (status == 3) {
                sql += " AND (NOW() <DATE_ADD(publishTime,INTERVAL videoTime HOUR_SECOND) AND NOW()>=publishTime OR NOW() >DATE_ADD(publishTime,INTERVAL videoTime HOUR_SECOND))";
            }
        }
        sql += ") ll LEFT JOIN live_subscription ss ON ll.id = ss.videoId";
        if (userId != null) {
            sql += " AND ss.userId =" + userId;
        }
        return sql;
    }


    @Override
    public List<SortDTO> sort() {
        return null;
    }


}
