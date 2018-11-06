package com.aizhixin.cloud.dd.dorms.jdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.dorms.domain.StuStatsDomain;
import com.aizhixin.cloud.dd.dorms.entity.BedStu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.dd.dorms.domain.AssginDomain;
import com.aizhixin.cloud.dd.dorms.domain.RoomInfoDomain;
import com.aizhixin.cloud.dd.dorms.domain.RoomChooseInfo;


@Repository
public class RoomAssginJdbc {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<IdNameDomain> findProfByTeacherId(Long orgId, Long teacherId) {
        String sql = "SELECT dra.prof_id, dra.prof_name FROM dd_room_assgin dra WHERE dra.org_id=" + orgId + " AND dra.DELETE_FLAG=0 AND dra.counselor_ids LIKE '%" + teacherId + "%' GROUP BY dra.prof_id";
        RowMapper<IdNameDomain> rowMapper = new RowMapper<IdNameDomain>() {
            @Override
            public IdNameDomain mapRow(ResultSet rs, int rowNum) throws SQLException {
                IdNameDomain ad = new IdNameDomain();
                ad.setId(rs.getLong("prof_id"));
                ad.setName(rs.getString("prof_name"));
                return ad;
            }
        };
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<AssginDomain> findRoomAssginGroupById(Long orgId) {
        String sql = "SELECT dra.college_id,dra.college_name FROM `dd_room_assgin` AS dra WHERE dra.DELETE_FLAG=0 AND dra.org_id="
                + orgId + " GROUP BY dra.college_id";
        RowMapper<AssginDomain> rowMapper = new RowMapper<AssginDomain>() {
            @Override
            public AssginDomain mapRow(ResultSet rs, int rowNum) throws SQLException {

                AssginDomain ad = new AssginDomain();
                ad.setCollegeId(rs.getLong("college_id"));
                ad.setCollegeName(rs.getString("college_name"));
                return ad;
            }
        };
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<RoomInfoDomain> findRoomInfo(Long orgId, List<Long> floorIds, List<String> unitNo, List<String> floorNo,
                                             Boolean full, Boolean open, Boolean isAssignment, Long profId, String no, String grade, Integer pageNumber, Integer pageSize) {
        String sql = "SELECT dr.`id`,df.`name`,dr.`unit_no`,dr.`floor_no`,dr.`no`,dr.`beds`,dr.`em_beds`,dr.`open`,draa.`id` as rid, GROUP_CONCAT(draa.prof_id) profId, GROUP_CONCAT(draa.prof_name)  profName, draa.counselor_ids, draa.counselor_names, dr.grade  FROM `dd_floor` AS df LEFT JOIN `dd_room` AS dr ON df.`id`=dr.`floor_id`";
        // 专业
        //			if (null != profId) {
        sql += " LEFT JOIN `dd_room_assgin` AS draa ON draa.`room_id`=dr.`id` AND draa.DELETE_FLAG=0";
        //		}
        sql += " WHERE df.`DELETE_FLAG`=0 AND dr.`DELETE_FLAG`=0 AND df.`org_id`=" + orgId;

        // 楼栋信息
        if (null != floorIds && 0 < floorIds.size()) {
            sql += " AND df.`id` IN(";
            for (int i = 0; i < floorIds.size(); i++) {
                if (i != floorIds.size() - 1) {
                    sql += floorIds.get(i) + ",";
                } else {
                    sql += floorIds.get(i) + ")";
                }
            }
        }
        // 单元
        if (null != unitNo && 0 < unitNo.size()) {
            sql += " AND dr.`unit_no`  IN(";
            for (int i = 0; i < unitNo.size(); i++) {
                if (i != unitNo.size() - 1) {
                    sql += "'" + unitNo.get(i) + "',";
                } else {
                    sql += "'" + unitNo.get(i) + "')";
                }
            }
        }
        // 楼层
        if (null != floorNo && 0 < floorNo.size()) {
            sql += " AND dr.`floor_no`  IN(";
            for (int i = 0; i < floorNo.size(); i++) {
                if (i != floorNo.size() - 1) {
                    sql += "'" + floorNo.get(i) + "',";
                } else {
                    sql += "'" + floorNo.get(i) + "')";
                }
            }
        }
        // 是否满员
        if (null != full) {
            if (full) {
                sql += "  AND dr.`em_beds`=0";
            } else {
                sql += "  AND dr.`em_beds`>0";
            }
        }
        // 是否开放
        if (null != open) {
            if (open) {
                sql += " AND dr.`open`=1";
            } else {
                sql += " AND dr.`open`=0";
            }
        }
        // 年级
        if (!StringUtils.isEmpty(grade)) {
            sql += " AND dr.`grade`='" + grade + "'";
        }
        // 是否分配
        if (null != isAssignment) {
            if (isAssignment) {
                sql += " AND draa.id > 0 ";
            } else {
                sql += " AND draa.id IS NULL ";
            }
        }
        // 专业
        if (null != profId) {
            sql += " AND draa.`prof_id`=" + profId + " AND draa.`DELETE_FLAG`=0";
        }
        // 宿舍号
        if (!StringUtils.isEmpty(no)) {
            sql += " AND dr.`no` LIKE '%" + no + "%' ";
        }
        sql += " AND dr.`id` IS NOT NULL  GROUP BY dr.`id` order by dr.`CREATED_DATE` desc,dr.`id` DESC   LIMIT " + pageNumber + "," + pageSize;
        RowMapper<RoomInfoDomain> rowMapper = new RowMapper<RoomInfoDomain>() {
            @Override
            public RoomInfoDomain mapRow(ResultSet rs, int rowNum) throws SQLException {

                RoomInfoDomain rd = new RoomInfoDomain();
                rd.setFloorName(rs.getString("name"));
                rd.setUnitNo(rs.getString("unit_no"));
                rd.setFloorNo(rs.getString("floor_no"));
                rd.setNo(rs.getString("no"));
                rd.setBeds(rs.getInt("beds"));
                rd.setEmBeds(rs.getInt("em_beds"));
                rd.setOpen(rs.getBoolean("open"));
                rd.setRoomId(rs.getLong("id"));
                rd.setProfNames(rs.getString("profName"));
                rd.setCounselorNames(rs.getString("counselor_names"));
                rd.setGrade(rs.getString("grade"));
                if (rs.getLong("rid") == 0) {
                    rd.setAssgin(false);
                } else {
                    rd.setAssgin(true);
                }
                return rd;
            }
        };
        System.out.println(sql);
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Integer countRoomInfo(Long orgId, List<Long> floorIds, List<String> unitNo, List<String> floorNo,
                                 Boolean full, Boolean open, Boolean isAssignment, Long profId, String no, String grade) {
        String sql = "SELECT count(DISTINCT dr.`id`) FROM `dd_floor` AS df LEFT JOIN `dd_room` AS dr ON df.`id`=dr.`floor_id`";
        // 专业
        //		if (null != profId) {
        sql += " LEFT JOIN `dd_room_assgin` AS draa ON draa.`room_id`=dr.`id` AND draa.DELETE_FLAG=0";
        //		}
        sql += " WHERE df.`DELETE_FLAG`=0 AND dr.`DELETE_FLAG`=0 AND df.`org_id`=" + orgId;

        // 楼栋信息
        if (null != floorIds && 0 < floorIds.size()) {
            sql += " AND df.`id` IN(";
            for (int i = 0; i < floorIds.size(); i++) {
                if (i != floorIds.size() - 1) {
                    sql += floorIds.get(i) + ",";
                } else {
                    sql += floorIds.get(i) + ")";
                }
            }
        }
        // 单元
        if (null != unitNo && 0 < unitNo.size()) {
            sql += " AND dr.`unit_no`  IN(";
            for (int i = 0; i < unitNo.size(); i++) {
                if (i != unitNo.size() - 1) {
                    sql += "'" + unitNo.get(i) + "',";
                } else {
                    sql += "'" + unitNo.get(i) + "')";
                }
            }
        }
        // 楼层
        if (null != floorNo && 0 < floorNo.size()) {
            sql += " AND dr.`floor_no`  IN(";
            for (int i = 0; i < floorNo.size(); i++) {
                if (i != floorNo.size() - 1) {
                    sql += "'" + floorNo.get(i) + "',";
                } else {
                    sql += "'" + floorNo.get(i) + "')";
                }
            }
        }
        // 是否满员
        if (null != full) {
            if (full) {
                sql += "  AND dr.`em_beds`=0";
            } else {
                sql += "  AND dr.`em_beds`>0";
            }
        }
        // 是否开放
        if (null != open) {
            if (open) {
                sql += " AND dr.`open`=1";
            } else {
                sql += " AND dr.`open`=0";
            }
        }
        // 年级
        if (!StringUtils.isEmpty(grade)) {
            sql += " AND dr.`grade`='" + grade + "'";
        }
        //是否分配
        if (null != isAssignment) {
            if (isAssignment) {
                sql += " AND draa.id > 0 ";
            } else {
                sql += " AND draa.id IS NULL ";
            }
        }
        // 专业
        if (null != profId) {
            sql += " AND draa.`prof_id`=" + profId + " AND draa.`DELETE_FLAG`=0";
        }
        // 宿舍号
        if (!StringUtils.isEmpty(no)) {
            sql += " AND dr.`no` LIKE '%" + no + "%' ";
        }
        sql += " AND dr.`id` IS NOT NULL ";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public Integer countRoomInfoByCounselor(Long orgId, Long userId, String no) {
        String sql = "SELECT count(DISTINCT dr.`id`) FROM `dd_floor` AS df LEFT JOIN `dd_room` AS dr ON df.`id`=dr.`floor_id`";
        // 专业
        //		if (null != profId) {
        sql += " LEFT JOIN `dd_room_assgin` AS draa ON draa.`room_id`=dr.`id` AND draa.DELETE_FLAG=0";
        //		}
        sql += " WHERE df.`DELETE_FLAG`=0 AND dr.`DELETE_FLAG`=0 AND df.`org_id`=" + orgId;

        // 是否开放
        sql += " AND dr.`open`=1";
        // 辅导员id
        if (null != userId) {
            sql += " AND draa.`counselor_ids` like '%" + userId + "%' AND draa.`DELETE_FLAG`=0";
        }
        // 宿舍号
        if (!StringUtils.isEmpty(no)) {
            sql += " AND dr.`no` LIKE '%" + no + "%' ";
        }
        sql += " AND dr.`id` IS NOT NULL ";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public List<RoomInfoDomain> findRoomInfoByCounselor(Long orgId, Long userId, String no, Integer pageNumber, Integer pageSize) {
        String sql = "SELECT dr.`id`,df.`name`,dr.`unit_no`,dr.`floor_no`,dr.`no`,dr.`beds`,dr.`em_beds`,dr.`open`,draa.`id` as rid, GROUP_CONCAT(draa.prof_id) profId, GROUP_CONCAT(draa.prof_name)  profName, draa.counselor_ids, draa.counselor_names, draa.sex_type  FROM `dd_floor` AS df LEFT JOIN `dd_room` AS dr ON df.`id`=dr.`floor_id`";
        // 专业
        //			if (null != profId) {
        sql += " LEFT JOIN `dd_room_assgin` AS draa ON draa.`room_id`=dr.`id` AND draa.DELETE_FLAG=0";
        //		}
        sql += " WHERE df.`DELETE_FLAG`=0 AND dr.`DELETE_FLAG`=0 AND df.`org_id`=" + orgId;

        // 是否开放
        sql += " AND dr.`open`=1";
        // 辅导员id
        if (null != userId) {
            sql += " AND draa.`counselor_ids` like '%" + userId + "%' AND draa.`DELETE_FLAG`=0";
        }
        // 宿舍号
        if (!StringUtils.isEmpty(no)) {
            sql += " AND dr.`no` LIKE '%" + no + "%' ";
        }
        sql += " AND dr.`id` IS NOT NULL  GROUP BY dr.`id` order by dr.`CREATED_DATE` desc,dr.`id` DESC   LIMIT " + pageNumber + "," + pageSize;
        RowMapper<RoomInfoDomain> rowMapper = new RowMapper<RoomInfoDomain>() {
            @Override
            public RoomInfoDomain mapRow(ResultSet rs, int rowNum) throws SQLException {

                RoomInfoDomain rd = new RoomInfoDomain();
                rd.setFloorName(rs.getString("name"));
                rd.setUnitNo(rs.getString("unit_no"));
                rd.setFloorNo(rs.getString("floor_no"));
                rd.setNo(rs.getString("no"));
                rd.setBeds(rs.getInt("beds"));
                rd.setEmBeds(rs.getInt("em_beds"));
                rd.setOpen(rs.getBoolean("open"));
                rd.setRoomId(rs.getLong("id"));
                rd.setProfNames(rs.getString("profName"));
                rd.setCounselorNames(rs.getString("counselor_names"));
                Integer sexType = rs.getInt("sex_type");
                if (sexType != null) {
                    if (sexType == 10) {
                        rd.setGender("男");
                    } else {
                        rd.setGender("女");
                    }
                }
                if (rs.getLong("rid") == 0) {
                    rd.setAssgin(false);
                } else {
                    rd.setAssgin(true);
                }
                return rd;
            }
        };
        System.out.println(sql);
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<RoomChooseInfo> findByChooseInfo(List<Long> roomIds) {
        String sql = "SELECT dr.*,df.`name`,df.`floor_type` FROM `dd_room` AS dr LEFT JOIN `dd_floor` AS df ON dr.`floor_id` = df.`id` WHERE dr.`open`=1 AND dr.`DELETE_FLAG`=0  AND df.`DELETE_FLAG`=0 AND dr.`em_beds`!=0 AND dr.`id` IN(";
        for (int i = 0; i < roomIds.size(); i++) {
            if (i != roomIds.size() - 1) {
                sql += roomIds.get(i) + ",";
            } else {
                sql += roomIds.get(i) + ")";
            }
        }
        sql += " order by dr.`CREATED_DATE` desc ";
        RowMapper<RoomChooseInfo> rowMapper = new RowMapper<RoomChooseInfo>() {
            @Override
            public RoomChooseInfo mapRow(ResultSet rs, int rowNum) throws SQLException {

                RoomChooseInfo rsd = new RoomChooseInfo();
                rsd.setFloorName(rs.getString("name"));
                rsd.setFloorNo(rs.getString("floor_no"));
                rsd.setNo(rs.getString("no"));
                rsd.setRoomId(rs.getLong("id"));
                rsd.setUnitNo(rs.getString("unit_no"));
                rsd.setRoomType(rs.getInt("floor_type"));
                rsd.setEmBeds(rs.getInt("em_beds"));
                return rsd;
            }
        };
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<Long> findProfInfo(List<Long> profIds) {
        String sql = "SELECT dr.id FROM `dd_room_assgin` AS dra LEFT JOIN `dd_room` AS dr ON dra.`room_id`=dr.`id` WHERE dra.`prof_id` in(";
        for (int i = 0; i < profIds.size(); i++) {
            if (i != profIds.size() - 1) {
                sql += profIds.get(i) + ",";
            } else {
                sql += profIds.get(i) + ") ";
            }
        }
        sql += "AND dr.`id` IS NOT NULL AND dr.`DELETE_FLAG`=0";
        return jdbcTemplate.queryForList(sql, Long.class);
    }

    public List<Map<String, Object>> findProfInfoByOrgIdAndProfessionalId(Long orgId, Long professionalId) {
        String sql = "SELECT dra.prof_id profId, dra.prof_name profName FROM dd_room_assgin dra WHERE dra.DELETE_FLAG=0 and dra.org_id='" + orgId + "'";
        if (professionalId != null && professionalId.longValue() > 0) {
            sql += " and dra.prof_id=" + professionalId;
        }
        sql += " GROUP BY dra.prof_id";
        return jdbcTemplate.queryForList(sql);
    }

    public Integer countBedByProfId(Long profId) {
        String sql = "SELECT count(*) FROM dd_bed bed WHERE bed.DELETE_FLAG=0 and bed.room_id IN (SELECT dra.room_id FROM dd_room_assgin dra WHERE dra.prof_id='" + profId + "' and dra.DELETE_FLAG=0);";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public Integer countStuBedByProfId(Long profId) {
        String sql = "SELECT count(*) FROM dd_bed_stu dbs WHERE dbs.DELETE_FLAG=0 and dbs.room_id IN (SELECT dra.room_id FROM dd_room_assgin dra WHERE dra.prof_id='" + profId + "' and dra.DELETE_FLAG=0);";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public List<BedStu> findBedStuByOrgIdAndNoName(Long orgId) {
        String sql = "SELECT * FROM dd_bed_stu dbs WHERE dbs.DELETE_FLAG=0 AND dbs.stu_name IS NULL AND dbs.room_id IN (SELECT dra.room_id FROM dd_room_assgin dra WHERE dra.DELETE_FLAG=0";
        if (orgId != null && orgId > 0) {
            sql += " AND dra.org_id='" + orgId + "'";
        }
        sql += " )";
        RowMapper<BedStu> rowMapper = new RowMapper<BedStu>() {
            @Override
            public BedStu mapRow(ResultSet rs, int rowNum) throws SQLException {
                BedStu rsd = new BedStu();
                rsd.setId(rs.getLong("id"));
                rsd.setStuId(rs.getLong("stu_id"));
                rsd.setBedId(rs.getLong("bed_id"));
                rsd.setCreatedBy(rs.getLong("CREATED_BY"));
                rsd.setCreatedDate(rs.getDate("CREATED_DATE"));
                rsd.setLastModifiedBy(rs.getLong("LAST_MODIFIED_BY"));
                rsd.setLastModifiedDate(rs.getDate("LAST_MODIFIED_DATE"));
                rsd.setDeleteFlag(rs.getInt("DELETE_FLAG"));
                rsd.setRoomId(rs.getLong("room_id"));
                return rsd;
            }
        };
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<BedStu> findBedStuByOrgIdAndNoPhone(Long orgId) {
        String sql = "SELECT * FROM dd_bed_stu dbs WHERE dbs.DELETE_FLAG=0 AND dbs.phone IS NULL AND dbs.room_id IN (SELECT dra.room_id FROM dd_room_assgin dra WHERE dra.DELETE_FLAG=0";
        if (orgId != null && orgId > 0) {
            sql += " AND dra.org_id='" + orgId + "'";
        }
        sql += " )";
        RowMapper<BedStu> rowMapper = new RowMapper<BedStu>() {
            @Override
            public BedStu mapRow(ResultSet rs, int rowNum) throws SQLException {
                BedStu rsd = new BedStu();
                rsd.setId(rs.getLong("id"));
                rsd.setStuId(rs.getLong("stu_id"));
                rsd.setBedId(rs.getLong("bed_id"));
                rsd.setCreatedBy(rs.getLong("CREATED_BY"));
                rsd.setCreatedDate(rs.getDate("CREATED_DATE"));
                rsd.setLastModifiedBy(rs.getLong("LAST_MODIFIED_BY"));
                rsd.setLastModifiedDate(rs.getDate("LAST_MODIFIED_DATE"));
                rsd.setDeleteFlag(rs.getInt("DELETE_FLAG"));
                rsd.setRoomId(rs.getLong("room_id"));
                return rsd;
            }
        };
        return jdbcTemplate.query(sql, rowMapper);
    }

    /**
     * @param orgId
     * @param profId
     * @param gender
     * @param name
     * @return
     */
    public Long countBedStuByProfIdAndSexTypeAndName(Long orgId, Long profId, String gender, String name) {
        String sql = "SELECT count(*) FROM dd_bed_stu dbs LEFT JOIN dd_bed dbd ON dbd.id=dbs.bed_id LEFT JOIN dd_room dr ON dr.id=dbs.room_id WHERE dbs.DELETE_FLAG=0 and dbs.room_id in (select room_id FROM dd_room_assgin dra WHERE dra.DELETE_FLAG=0 and dra.org_id='" + orgId + "'";
        sql += ")";
        if (!StringUtils.isEmpty(name)) {
            name = "%" + name + "%";
            sql += " and (dbs.stu_name LIKE '" + name + "' or dbs.id_number LIKE '" + name + "')";
        }
        if (!StringUtils.isEmpty(gender)) {
            sql += " and dbs.gender = '" + gender + "'";
        }
        if (profId != null && profId > 0) {
            sql += " and dbs.prof_id=" + profId;
        }
        return jdbcTemplate.queryForObject(sql, Long.class);
    }


    /**
     * @param page
     * @param orgId
     * @param profId
     * @param gender
     * @param name
     * @return
     */
    public List<StuStatsDomain> findBedStuByProfIdAndSexTypeAndName(Pageable page, Long orgId, Long profId, String gender, String name) {
        String sql = "SELECT dbs.id, dbs.stu_id, dbs.bed_id, dbs.created_date, dbd.`name` bed_name, dr.no room_no,dbs.room_id, dbs.stu_name, dbs.gender, dbs.id_number, dbs.phone, dbs.prof_id, dbs.prof_name FROM dd_bed_stu dbs LEFT JOIN dd_bed dbd ON dbd.id=dbs.bed_id LEFT JOIN dd_room dr ON dr.id=dbs.room_id WHERE dbs.DELETE_FLAG=0 and dbs.room_id in (select room_id FROM dd_room_assgin dra WHERE dra.DELETE_FLAG=0 and dra.org_id='" + orgId + "'";
        sql += ")";
        if (!StringUtils.isEmpty(name)) {
            name = "%" + name + "%";
            sql += " and (dbs.stu_name LIKE '" + name + "' or dbs.id_number LIKE '" + name + "')";
        }
        if (!StringUtils.isEmpty(gender)) {
            sql += " and dbs.gender = '" + gender + "'";
        }
        if (profId != null && profId > 0) {
            sql += " and dbs.prof_id=" + profId;
        }
        sql += " order by dbs.created_date desc limit " + (page.getPageSize() * page.getPageNumber()) + "," + page.getPageSize();
        RowMapper<StuStatsDomain> rowMapper = new RowMapper<StuStatsDomain>() {
            @Override
            public StuStatsDomain mapRow(ResultSet rs, int rowNum) throws SQLException {
                StuStatsDomain rsd = new StuStatsDomain();
                rsd.setId(rs.getLong("id"));
                rsd.setStuId(rs.getLong("stu_id"));
                rsd.setBedId(rs.getLong("bed_id"));
                rsd.setBedName(rs.getString("bed_name"));
                rsd.setCreatedDate(rs.getDate("created_date"));
                rsd.setRoomId(rs.getLong("room_id"));
                rsd.setRoomNo(rs.getString("room_no"));
                rsd.setStuName(rs.getString("stu_name"));
                rsd.setGender(rs.getString("gender"));
                rsd.setPhone(rs.getString("phone"));
                rsd.setProfId(rs.getLong("prof_id"));
                rsd.setProfName(rs.getString("prof_name"));
                return rsd;
            }
        };
        return jdbcTemplate.query(sql, rowMapper);
    }
}
