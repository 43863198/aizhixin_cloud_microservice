package com.aizhixin.cloud.dd.communication.jdbcTemplate;

import com.aizhixin.cloud.dd.constant.RollCallConstants;


import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class RollCallQueryPaginationSQL implements PaginationSQL {

    public static String FIND_SQL       = "SELECT   rc.id AS rollCallId,ds.schedule_id AS scheduleId,   ds.id as student_schedule_id,u.id AS userId,sc.teacher_id,sc.classroomrollcall,"
                                                + " class.id AS classId,sc.course_id,rc.signtime,  rc.distance,   u.`name` AS userName,   IFNULL(rc.type,#type#) as type,  class.`name` AS className "
                                                + " FROM dd_schedule sc LEFT JOIN dd_studentschedule ds on sc.id = ds.schedule_id"
                                                + " LEFT JOIN `dd_class` AS class ON class.id = ds.class_id"
                                                + " LEFT JOIN `dd_user` AS u ON u.id = ds.student_id"
                                                + " LEFT JOIN dd_rollcall rc ON rc.student_schedule_id = ds.id"
                                                + " where 1 = 1";
    public static String FIND_COUNT_SQL = "SELECT count(ds.id)"
                                                + " FROM dd_schedule sc LEFT JOIN dd_studentschedule ds on sc.id = ds.schedule_id"
                                                + " LEFT JOIN `dd_class` AS class ON class.id = ds.class_id"
                                                + " LEFT JOIN `dd_user` AS u ON u.id = ds.student_id"
                                                + " LEFT JOIN dd_rollcall rc ON rc.student_schedule_id = ds.id"
                                                + " where 1 = 1 ";

    private Long         teacher_id;

    private Long         schedule_id;

    private String       type;

    private String       name;

    private boolean      isSchoolTime   = false;

    private String       rollCallType   = "";

    public Long getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(Long teacher_id) {
        this.teacher_id = teacher_id;
    }

    public Long getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(Long schedule_id) {
        this.schedule_id = schedule_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getFindCountSql() {

        String sql = FIND_COUNT_SQL;
        if (null != this.teacher_id && this.teacher_id.longValue() > 0L) {
            sql += " and sc.teacher_id = " + teacher_id;
        }

        if (null != this.schedule_id && this.schedule_id.longValue() > 0L) {
            sql += " and sc.id = " + schedule_id;
        }
        if (StringUtils.isNotBlank(this.name)) {
            sql += " and (u.name like '%" + name + "%' or u.person_id like '%" + name + "%') ";
        }
        if (StringUtils.isNotBlank(this.type)) {
            sql += " and rc.type = '" + this.type + "'";
        }
        return sql;
    }

    @Override
    public String getFindSql() {

        String sql = FIND_SQL;

        if (isSchoolTime) {
            sql = sql.replaceAll("#type#", RollCallConstants.TYPE_UNCOMMITTED);
        } else {
            sql = sql.replaceAll("#type#", RollCallConstants.TYPE_TRUANCY);
        }

        if (null != this.teacher_id && this.teacher_id.longValue() > 0L) {
            sql += " and sc.teacher_id = " + teacher_id;
        }

        if (null != this.schedule_id && this.schedule_id.longValue() > 0L) {
            sql += " and sc.id = " + schedule_id;
        }
        if (StringUtils.isNotBlank(this.name)) {
            sql += " and (u.name like '%" + name + "%' or u.person_id like '%" + name + "%') ";
        }

        if (StringUtils.isNotBlank(this.type)) {
            sql += " and rc.type = '" + this.type + "'";
        }
        if (StringUtils.isNotBlank(rollCallType)) {
            sql += " and sc.roll_call_type = '" + this.rollCallType + "'";
        }
        return sql;
    }

    @Override
    public List<SortDTO> sort() {

        List<SortDTO> list = new ArrayList<SortDTO>();
        SortDTO dto = new SortDTO();
        dto.setAsc(true);
        dto.setKey("class.id");
        list.add(dto);
        dto = new SortDTO();
        dto.setAsc(true);
        dto.setKey("u.id");
        list.add(dto);

        return list;
    }

    public RollCallQueryPaginationSQL() {}

    public RollCallQueryPaginationSQL(Long teacher_id, Long schedule_id, String type, String name,
            boolean isSchoolTime, String rollCallType) {
        this.teacher_id = teacher_id;
        this.schedule_id = schedule_id;
        this.type = type;
        this.name = name;
        this.isSchoolTime = isSchoolTime;
        this.rollCallType = rollCallType;
    }
}
