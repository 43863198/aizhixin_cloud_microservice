package com.aizhixin.cloud.dd.communication.jdbcTemplate;


import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;

import java.util.List;


public class ElectricFenceQueryHistroyPaginnationSQL implements PaginationSQL {

    public static String FIND_SQL = "SELECT cc.*,co.name AS cname,ma.name AS mname,cl.name AS clname,co.id AS oid,ma.id AS MID,cl.id AS lid FROM (SELECT us.ORGAN_ID,us.NAME AS NAME,us.id AS id,el.address AS address,el.noticeTime AS noticeTime,el.lltude AS lltude,ei.lltudes AS lltudes, ei.noticeTimeInterval AS noticeTimeInterval, el.outOfRange FROM (SELECT  * from  dd_electricfencebase ) el  LEFT JOIN dd_user us  ON el.user_id = us.id LEFT JOIN dd_electricfenceinfo ei ON us.ORGAN_ID = ei.organ_Id  WHERE 1 AND us.ROLE_ID = 2 AND lltude IS NOT NULL";


    private String dateTime;
    private Long collegeId;
    private Long skTeacherId;
    private Long userId;
    private Long organId;

    @Override
    public String getFindCountSql() {
        String sql = "select count(1) from ( ";
        sql += FIND_SQL;
        if (dateTime != null) {
            sql += " and date_format(noticeTime,'%Y-%m-%d')= '" + dateTime + "'";
        } else {
            sql += " AND TO_DAYS(noticeTime) = TO_DAYS(NOW())";
        }
        sql += " ) cc	LEFT JOIN dd_user_class uc on uc.user_id = cc.id LEFT JOIN dd_class cl on uc.class_id = cl.id LEFT JOIN dd_major ma on cl.major_id = ma.id LEFT JOIN dd_college co on ma.college_id = co.id  where 1 ";
        sql += " and  cc.organ_id=" + organId;
        if (userId != null) {
            sql += " AND cc.id =" + userId;
        }
        if (collegeId != null) {
            sql += " AND co.id=" + collegeId;
        }
        if (skTeacherId != null) {
            sql += " AND cl.head_teacher_id=" + skTeacherId;
        }
        sql += " ORDER BY cc.noticeTime DESC ) mm";
        return sql;
    }

    @Override
    public String getFindSql() {
        String sql = FIND_SQL;
        if (dateTime != null) {
            sql += " and date_format(noticeTime,'%Y-%m-%d')= '" + dateTime + "'";
        } else {
            sql += " AND TO_DAYS(noticeTime) = TO_DAYS(NOW())";
        }
        sql += " ) cc	LEFT JOIN dd_user_class uc on uc.user_id = cc.id LEFT JOIN dd_class cl on uc.class_id = cl.id LEFT JOIN dd_major ma on cl.major_id = ma.id LEFT JOIN dd_college co on ma.college_id = co.id  where 1 ";
        sql += " and  cc.organ_id=" + organId;
        if (userId != null) {
            sql += " AND cc.id =" + userId;
        }
        if (collegeId != null) {
            sql += " AND co.id=" + collegeId;
        }
        if (skTeacherId != null) {
            sql += " AND cl.head_teacher_id=" + skTeacherId;
        }
        sql += " ORDER BY cc.noticeTime DESC";
        return sql;
    }


    @Override
    public List<SortDTO> sort() {

        return null;
    }

    public ElectricFenceQueryHistroyPaginnationSQL(Long userId, String dateTime, Long collegeId, Long skTeacherId, Long organId) {
        super();
        this.dateTime = dateTime;
        this.organId = organId;
        this.userId = userId;
        this.collegeId = collegeId;
        this.skTeacherId = skTeacherId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }
}
