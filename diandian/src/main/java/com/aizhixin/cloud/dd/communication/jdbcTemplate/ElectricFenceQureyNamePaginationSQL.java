package com.aizhixin.cloud.dd.communication.jdbcTemplate;


import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;

import java.util.List;


public class ElectricFenceQureyNamePaginationSQL implements PaginationSQL {


    public static String FIND_SQL       = " SELECT du.ORGAN_ID, du.id, du.name,du.person_id,cl.id AS classid, ma.id AS majorid, co.id AS collegeid, bb.lltude, bb.lltudes, bb.noticeTimeInterval, bb.noticeTime, bb.address FROM dd_user du LEFT JOIN dd_user_class uc ON uc.user_id = du.id LEFT JOIN dd_class cl ON uc.class_id = cl.id LEFT JOIN dd_major ma ON cl.major_id = ma.id LEFT JOIN dd_college co ON ma.college_id = co.id RIGHT JOIN (SELECT * FROM (SELECT us.ORGAN_ID, us.status, us.role_id, us.id AS id, el.address AS address, el.noticeTime AS noticeTime, el.lltude AS lltude, ei.lltudes AS lltudes, ei.noticeTimeInterval AS noticeTimeInterval FROM (SELECT * FROM dd_electricfencebase WHERE id IN (SELECT MAX(id) FROM dd_electricfencebase el GROUP BY el.user_Id)) el LEFT JOIN dd_user us ON el.user_Id = us.id LEFT JOIN dd_electricfenceinfo ei ON us.ORGAN_ID = ei.organ_Id WHERE 1 AND us.ROLE_ID = 2 AND us.status = 'created' AND el.noticetime IS NOT NULL) test) bb ON du.id = bb.id";

    private String       name;
    private Long         skTeacherId;
    private Long         collegeId;
    private Long         organId;

    @Override
    public String getFindCountSql() {


        String sql = "SELECT COUNT(1) FROM ( SELECT * FROM ( "+FIND_SQL+") ss";
        sql += " where ss.ORGAN_ID = " + organId;
        if (name != null) {
            sql += " and (ss.NAME like '%" + name + "%' or ss.PERSON_ID like '%" + name + "%' )";
        }
        if (collegeId != null) {
            sql += "  and ss.collegeid =" + collegeId;
        }
        sql+=") ss";
        return sql;
    }

    @Override
    public String getFindSql() {
        String sql = FIND_SQL;
        sql += " and du.organ_id = " + organId;
        if (name != null) {
            sql += " where (du.name like '%" + name + "%' or du.person_id like '%" + name + "%') ";
        }
        if (collegeId != null) {
            sql += " and co.id =" + collegeId;
        }
        if (skTeacherId != null) {
            sql += " and cl.head_teacher_id =" + skTeacherId;
        }
        sql += " ORDER BY bb.noticeTime DESC ";
        return sql;
    }


    @Override
    public List<SortDTO> sort() {

        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public ElectricFenceQureyNamePaginationSQL(String name, Long skTeacherId, Long collegeId, Long organId) {
        super();
        this.name = name;
        this.skTeacherId = skTeacherId;
        this.collegeId = collegeId;
        this.organId = organId;
    }

}
