package com.aizhixin.cloud.dd.communication.jdbcTemplate;


import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;

import java.util.List;


public class electricFenceQueryNotActiveUserPaginationSQL implements PaginationSQL {


    private Long collegeId;
    private Long classId;
    private Long majorId;
    private Long organId;
    private Long skTeacherId;


    public static String FIND_SQL = "SELECT us.id AS id, us.name AS NAME, us.role_id AS roleId, us.college_id AS collegeId, us.ORGAN_ID AS organId, mr.id AS majorId, cs.id AS classId, cs.name AS ClassName FROM (SELECT * FROM dd_user us) us LEFT JOIN dd_user_class uscs ON us.id = uscs.user_id LEFT JOIN dd_class cs ON cs.id = uscs.class_id LEFT JOIN dd_college co ON co.id = us.college_id LEFT JOIN dd_major mr ON cs.major_id =mr.id WHERE us.role_id=2";

    @Override
    public String getFindCountSql() {
        String sql = "select count(1) from ( ";
        sql += FIND_SQL;
        sql += " AND us.ORGAN_ID =" + organId;
        if (collegeId != null) {
            sql += " AND co.id=" + collegeId;
        }
        if (majorId != null) {
            sql += " AND mr.id=" + majorId;
        }
        if (classId != null) {
            sql += " AND cs.id=" + classId;
        }
        if(skTeacherId!=null){
            sql +="  AND cs.head_teacher_id ="+skTeacherId;
        }
        sql += ")mm ";
        return sql;
    }

    @Override
    public String getFindSql() {
        String sql = FIND_SQL;
        sql += " AND us.ORGAN_ID =" + organId;
        if (collegeId != null) {
            sql += " AND co.id=" + collegeId;
        }
        if (majorId != null) {
            sql += " AND mr.id=" + majorId;
        }
        if (classId != null) {
            sql += " AND cs.id=" + classId;
        }
        if(skTeacherId!=null){
            sql +="  AND cs.head_teacher_id ="+skTeacherId;
        }
        return sql;
    }

    @Override
    public List<SortDTO> sort() {

        return null;
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

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
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

    public electricFenceQueryNotActiveUserPaginationSQL(Long collegeId, Long classId, Long majorId,Long skTeacherId,Long organId) {
        super();
        this.collegeId = collegeId;
        this.classId = classId;
        this.majorId = majorId;
        this.organId = organId;
        this.skTeacherId=skTeacherId;
    }


}
