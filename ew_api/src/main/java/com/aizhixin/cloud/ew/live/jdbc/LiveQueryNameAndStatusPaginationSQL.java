package com.aizhixin.cloud.ew.live.jdbc;


import com.aizhixin.cloud.ew.common.jdbc.PaginationSQL;
import com.aizhixin.cloud.ew.common.jdbc.SortDTO;

import java.util.List;

public class LiveQueryNameAndStatusPaginationSQL implements PaginationSQL {

    public static String FIND_SQL       = "SELECT * FROM live_content l WHERE 1=1";
    private String title;
    private Integer status;
    private Integer typeId;


    public LiveQueryNameAndStatusPaginationSQL(String title, Integer typeId, Integer status) {
        this.title=title;
        this.status=status;
        this.typeId=typeId;
    }

    @Override
    public String getFindSql() {

        String sql=FIND_SQL;
        if(title!=null){
            sql+=" AND l.title Like '%"+title+"%'";
        }
        if(status!=null){
            sql+=" AND l.status="+status;
        }
        if(!(typeId==null)){
            if(typeId==0){
                sql+=" AND (NOW() >DATE_ADD(publishTime,INTERVAL videoTime HOUR_SECOND))";
            }
            if(typeId==1){
                sql+=" AND (NOW() <publishTime)";
            }
            if(typeId==2){
                sql+=" AND (NOW() <DATE_ADD(publishTime,INTERVAL videoTime HOUR_SECOND) AND NOW()>=publishTime)";
            }
            if(typeId==3){
                sql+=" AND (NOW() <DATE_ADD(publishTime,INTERVAL videoTime HOUR_SECOND) AND NOW()>=publishTime OR NOW() >DATE_ADD(publishTime,INTERVAL videoTime HOUR_SECOND))";
            }else {

            }
        }

        sql+=" ORDER BY publishTime ASC";
        return sql;

    }

    @Override
    public String getFindCountSql() {
        String sql="SELECT COUNT(0)  FROM ("+FIND_SQL;
        if(title!=null){
            sql+=" AND l.title Like '%"+title+"%'";
        }
        if(status!=null){
            sql+=" AND l.status="+status;
        }
        if(!(typeId==null)){
            if(typeId==0){
                sql+=" AND (NOW() >DATE_ADD(publishTime,INTERVAL videoTime HOUR_SECOND))";
            }
            if(typeId==1){
                sql+=" AND (NOW() <publishTime)";
            }
            if(typeId==2){
                sql+=" AND (NOW() <DATE_ADD(publishTime,INTERVAL videoTime HOUR_SECOND) AND NOW()>=publishTime)";
            }
            if(typeId==3){
                sql+=" AND (NOW() <DATE_ADD(publishTime,INTERVAL videoTime HOUR_SECOND) AND NOW()>=publishTime OR NOW() >DATE_ADD(publishTime,INTERVAL videoTime HOUR_SECOND))";
            }
        }else {

        }
        sql+=") ss";
        return sql;
    }



    @Override
    public List<SortDTO> sort() {
        return null;
    }


}
