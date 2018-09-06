package com.aizhixin.cloud.dd.communication.jdbcTemplate;


import com.aizhixin.cloud.dd.communication.dto.ElectricFenceQueryNotActiveUserDto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class ElectricFenceQueryNotActiveUserJdbcTemplate extends PaginationJDBCTemplate<ElectricFenceQueryNotActiveUserDto> {
    public static final RowMapper<ElectricFenceQueryNotActiveUserDto> electricFenceQueryNotActiveUserDtoMapper = new RowMapper<ElectricFenceQueryNotActiveUserDto>() {
        @SuppressWarnings("deprecation")
        public ElectricFenceQueryNotActiveUserDto mapRow(ResultSet rs, int rowNum)
            throws SQLException {
            ElectricFenceQueryNotActiveUserDto electricFenceQueryNotActiveUser = new ElectricFenceQueryNotActiveUserDto();
            electricFenceQueryNotActiveUser.setId(rs.getLong("id"));
            electricFenceQueryNotActiveUser.setName(rs.getString("name"));
            electricFenceQueryNotActiveUser.setRole_id(rs.getLong("roleId"));
            electricFenceQueryNotActiveUser.setOrganId(rs.getLong("organId"));
            electricFenceQueryNotActiveUser.setCollegeId(rs.getLong("collegeId"));
            electricFenceQueryNotActiveUser.setClassName(rs.getString("ClassName"));
            electricFenceQueryNotActiveUser.setMajorId(rs.getLong("majorId"));
            electricFenceQueryNotActiveUser.setClassId(rs.getLong("classId"));
            return electricFenceQueryNotActiveUser;

        }
    };


}
