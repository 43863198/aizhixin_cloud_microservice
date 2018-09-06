package com.aizhixin.cloud.dd.communication.jdbcTemplate;



import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 查询指定某天的GPS信息
 *
 * @author www.0001.GA
 */
public class ElectricFenceQueryLeaveDayPaginationSQL implements PaginationSQL {

    /**
     *
     * */


    public static String FIND_SQL = "SELECT us.id AS userId,us.name AS userName,co.id AS collegeId,mr.id AS majorId,og.id AS orgranId,uscs.id AS userClassId,cs.head_teacher_id AS teacherId,eb.noticeTime,eb.address, eb.lltude,ei.noticeTimeInterval FROM (SELECT * FROM dd_electricfencebase WHERE id IN (SELECT MAX(ebase.id) FROM dd_electricfencebase ebase GROUP BY ebase.user_Id)) eb LEFT JOIN dd_user AS us ON us.id = eb.user_Id LEFT JOIN dd_user_class AS uscs ON uscs.user_id=us.id LEFT JOIN dd_class AS cs ON uscs.class_id = cs.id LEFT JOIN dd_organ AS og ON us.ORGAN_ID= og.id LEFT JOIN dd_major AS mr ON cs.major_id=mr.id LEFT JOIN dd_college co ON mr.college_id=co.id LEFT JOIN dd_electricfenceinfo AS ei ON us.ORGAN_ID = ei.organ_Id WHERE us.status = 'created' AND us.role_id = 2";


    private Long skTeacherId;
    private Long collegeId;

    private Long majorId;

    private Long classId;
    private String starttime;
    private String endtime;
    private Long organId;
    private String dateTime;

    @Override
    public String getFindCountSql() {
        String sql = " SELECT COUNT(1) FROM ( SELECT * FROM (" + FIND_SQL + ") ss where 1 ";
        sql += " and ss.orgranId = " + organId;
        if (collegeId != null) {
            sql += " and ss.collegeId = " + collegeId;
        }
        if (majorId != null) {
            sql += " and ss.majorId = " + majorId;
        }
        if (classId != null) {
            sql += " and ss.userClassId = " + classId;
        }
        if (skTeacherId != null) {
            sql += " and ss.teacherId = " + skTeacherId;
        }
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (dateTime != null) {
            sql += " AND DATE_FORMAT(noticeTime, '%Y-%m-%d') = '" + dateTime + "'";
            sql += " AND UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(noticeTime) > noticeTimeInterval";
        } else {
            sql += " AND DATE_FORMAT(noticeTime, '%Y-%m-%d') = DATE_FORMAT(NOW(), '%Y-%m-%d')";
            sql += " AND UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(noticeTime) > noticeTimeInterval";
        }
        sql += ") ss";
        return sql;
    }

    @Override
    public String getFindSql() {
        String sql = " select * from ( " + FIND_SQL + ") ss where 1 ";
        sql += " and ss.orgranId = " + organId;
        if (collegeId != null) {
            sql += " and ss.collegeId = " + collegeId;
        }
        if (majorId != null) {
            sql += " and ss.majorId = " + majorId;
        }
        if (classId != null) {
            sql += " and ss.userClassId = " + classId;
        }
        if (skTeacherId != null) {
            sql += " and ss.teacherId = " + skTeacherId;
        }
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (dateTime != null) {
            sql += " AND DATE_FORMAT(noticeTime, '%Y-%m-%d') = '" + dateTime + "'";
            sql += " AND UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(noticeTime) > noticeTimeInterval";
        } else {
            sql += " AND DATE_FORMAT(noticeTime, '%Y-%m-%d') = DATE_FORMAT(NOW(), '%Y-%m-%d')";
            sql += " AND UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(noticeTime) > noticeTimeInterval";
        }
        sql += "  ORDER BY noticeTime DESC";
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

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }


    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Long getSkTeacherId() {
        return skTeacherId;
    }

    public void setSkTeacherId(Long skTeacherId) {
        this.skTeacherId = skTeacherId;
    }


    public ElectricFenceQueryLeaveDayPaginationSQL(Long skTeacherId,
                                                   Long collegeId, Long majorId, Long classId, String starttime,
                                                   String endtime, Long organId) {
        super();
        this.skTeacherId = skTeacherId;
        this.collegeId = collegeId;
        this.majorId = majorId;
        this.classId = classId;
        this.starttime = starttime;
        this.endtime = endtime;
        this.organId = organId;
    }

    public ElectricFenceQueryLeaveDayPaginationSQL(Long skTeacherId, Long collegeId, Long majorId, Long classId, String dateTime, Long organId) {
        super();
        this.skTeacherId = skTeacherId;
        this.collegeId = collegeId;
        this.majorId = majorId;
        this.classId = classId;
        this.organId = organId;
        this.dateTime = dateTime;

    }


}
