package com.aizhixin.cloud.dd.communication.jdbcTemplate;


import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;

import java.util.List;


public class ElectricFenceQueryOutOfRangeDayPaginationSQL implements PaginationSQL {

    public static String FIND_SQL = "SELECT cc.*,co.`name` as cname ,ma.`name` as mname,cl.`name` as clname,co.id as oid,ma.id as mid,cl.id as lid FROM(	select us.ORGAN_ID,	us. NAME AS NAME,us.id AS id,el.address AS address,el.noticeTime AS noticeTime,el.lltude AS lltude,ei.lltudes AS lltudes,ei.noticeTimeInterval AS noticeTimeInterval,el.outOfRange from (select * from dd_electricfencebase where id in(select MAX(id) from dd_electricfencebase el  GROUP BY el.user_Id ))el LEFT JOIN dd_user us ON el.user_id=us.id LEFT JOIN dd_electricfenceinfo ei ON us.ORGAN_ID=ei.organ_Id WHERE 1 AND us.ROLE_ID = 2 AND lltude IS NOT NULL";


    @Override
    public String getFindCountSql() {
        String sql = "select count(1) from ( ";
        sql += FIND_SQL;
        if (dateTime != null) {
            sql += " AND DATE_FORMAT(noticeTime, '%Y-%m-%d') = '" + dateTime + "'";
        } else {
            sql += " AND DATE_FORMAT(noticeTime, '%Y-%m-%d') = DATE_FORMAT(NOW(), '%Y-%m-%d')";
        }
        sql += " ) cc LEFT JOIN dd_user_class uc on uc.user_id = cc.id LEFT JOIN dd_class cl on uc.class_id = cl.id LEFT JOIN dd_major ma on cl.major_id = ma.id LEFT JOIN dd_college co on ma.college_id = co.id  where 1 ";
        sql += " AND  cc.organ_id=" + organId;
        if (collegeId != null) {
            sql += " AND co.id = " + collegeId;
        }
        if (majorId != null) {
            sql += " AND ma.id = " + majorId;
        }
        if (classId != null) {
            sql += " AND cl.id = " + classId;
        }
        if (skTeacherId != null) {
            sql += " AND cl.head_teacher_id = " + skTeacherId;
        }

        sql += ")mm ";
        return sql;
    }

    @Override
    public String getFindSql() {
        String sql = FIND_SQL;
        if (dateTime != null) {
            sql += " AND DATE_FORMAT(noticeTime, '%Y-%m-%d') = '" + dateTime + "'";
        } else {
            sql += " AND DATE_FORMAT(noticeTime, '%Y-%m-%d') = DATE_FORMAT(NOW(), '%Y-%m-%d')";
        }
        sql += " ) cc LEFT JOIN dd_user_class uc on uc.user_id = cc.id LEFT JOIN dd_class cl on uc.class_id = cl.id LEFT JOIN dd_major ma on cl.major_id = ma.id LEFT JOIN dd_college co on ma.college_id = co.id  where 1 ";
        sql += " AND  cc.organ_id=" + organId;
        if (collegeId != null) {
            sql += " AND co.id = " + collegeId;
        }
        if (majorId != null) {
            sql += " AND ma.id = " + majorId;
        }
        if (classId != null) {
            sql += " AND cl.id = " + classId;
        }
        if (skTeacherId != null) {
            sql += " AND cl.head_teacher_id = " + skTeacherId;
        }
        sql += " ORDER BY noticeTime DESC";
        return sql;
    }

    @Override
    public List<SortDTO> sort() {

        return null;
    }


    private Long collegeId;
    private Long majorId;
    private Long classId;
    private Long skTeacherId;
    private String dateTime;
    private Long organId;


    public ElectricFenceQueryOutOfRangeDayPaginationSQL(Long collegeId, Long majorId, Long classId, Long skTeacherId, String dateTime, Long organId) {
        super();
        this.classId = classId;
        this.collegeId = collegeId;
        this.majorId = majorId;
        this.skTeacherId = skTeacherId;
        this.dateTime = dateTime;
        this.organId = organId;
    }

    public Long getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Long collegeId) {
        this.collegeId = collegeId;
    }

    public Long getMajorId() {
        return majorId;
    }

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getSkTeacherId() {
        return skTeacherId;
    }

    public void setSkTeacherId(Long skTeacherId) {
        this.skTeacherId = skTeacherId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }


}
