package com.aizhixin.cloud.ew.live.jdbc;


import com.aizhixin.cloud.ew.common.jdbc.PaginationSQL;
import com.aizhixin.cloud.ew.common.jdbc.SortDTO;

import java.util.List;

public class LiveQueryMessagePaginationSQL implements PaginationSQL {

    public static String FIND_SQL = "SELECT * FROM live_comment WHERE 1=1";

    private Long liveId;

    public LiveQueryMessagePaginationSQL(Long liveId) {
        this.liveId = liveId;
    }


    @Override
    public String getFindSql() {
        String sql = FIND_SQL;
        if (liveId != null) {
            sql += " AND videoId =" + liveId;
        }
        sql += " ORDER BY CREATED_DATE DESC";
        return sql;
    }

    @Override
    public String getFindCountSql() {
        String sql = "SELECT COUNT(0)  FROM (" + FIND_SQL;
        if (liveId != null) {
            sql += " AND videoId =" + liveId;
        }
        sql += ")ss";
        return sql;
    }


    @Override
    public List<SortDTO> sort() {
        return null;
    }


}
