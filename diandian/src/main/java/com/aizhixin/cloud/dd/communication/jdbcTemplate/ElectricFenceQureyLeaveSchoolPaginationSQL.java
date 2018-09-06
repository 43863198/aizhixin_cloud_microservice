package com.aizhixin.cloud.dd.communication.jdbcTemplate;


import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;

import java.util.List;


public class ElectricFenceQureyLeaveSchoolPaginationSQL implements PaginationSQL {

    /**
     *
     * */
    public static String FIND_SQL   ="SELECT cc.*, co.`name` AS cname, ma.`name` AS mname, cl.`name` AS clname, co.id AS oid, ma.id AS MID, cl.id AS lid,cl.head_teacher_id FROM (SELECT us.ORGAN_ID, us.NAME AS NAME, us.id AS id, el.address AS address, el.noticeTime AS noticeTime, el.lltude AS lltude, ei.lltudes AS lltudes, ei.noticeTimeInterval AS noticeTimeInterval FROM (SELECT * FROM dd_electricfencebase WHERE id IN (SELECT MAX(id) FROM dd_electricfencebase el GROUP BY el.user_Id)) el LEFT JOIN dd_user us ON el.user_id = us.id LEFT JOIN dd_electricfenceinfo ei ON us.ORGAN_ID = ei.organ_Id WHERE 1 AND us.ROLE_ID = 2 AND lltude IS NOT NULL) cc LEFT JOIN dd_user_class uc ON uc.user_id = cc.id LEFT JOIN dd_class cl ON uc.class_id = cl.id LEFT JOIN dd_major ma ON cl.major_id = ma.id LEFT JOIN dd_college co ON ma.college_id = co.id WHERE 1";

    private Long         skTeacherId;
    private Long         collegeId;
    private Long         organId;

    @Override
    public String getFindCountSql() {

        String sql = "SELECT COUNT(1) FROM ( SELECT * FROM ( "+FIND_SQL+")ss WHERE 1";
        sql += " AND  ss.organ_id= " + organId;
        if (collegeId != null) {
            sql += " AND ss.oid =" + collegeId;
        }
        if (skTeacherId != null) {
            sql += " AND ss.head_teacher_id=" + skTeacherId;
        }
        sql+=" AND TO_DAYS(noticeTime) = TO_DAYS(NOW()) ";
        sql += ") ss";
        return sql;
    }

    @Override
    public String getFindSql() {
        String sql = FIND_SQL;
        sql += " AND  co.organ_id= " + organId;
        if (collegeId != null) {
            sql += " AND co.id=" + collegeId;
        }
        if (skTeacherId != null) {
            sql += " AND cl.head_teacher_id=" + skTeacherId;
        }
        sql+=" AND TO_DAYS(noticeTime) = TO_DAYS(NOW())";
        return sql;
    }

    @Override
    public List<SortDTO> sort() {

        return null;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public Long getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Long collegeId) {
        this.collegeId = collegeId;
    }

    public Long getSkTeacherId() {
        return skTeacherId;
    }

    public void setSkTeacherId(Long skTeacherId) {
        this.skTeacherId = skTeacherId;
    }

    public ElectricFenceQureyLeaveSchoolPaginationSQL() {
        super();

    }

    public ElectricFenceQureyLeaveSchoolPaginationSQL(Long skTeacherId, Long collegeId, Long organId) {
        super();
        this.skTeacherId = skTeacherId;
        this.collegeId = collegeId;
        this.organId = organId;
    }

}
