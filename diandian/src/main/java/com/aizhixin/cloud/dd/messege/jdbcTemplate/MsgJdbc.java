package com.aizhixin.cloud.dd.messege.jdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.aizhixin.cloud.dd.messege.domain.PushMsgDomain;

@Component
public class MsgJdbc {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<PushMsgDomain> findByMsgInfo(Long userId) {
        String sql = "SELECT * FROM(SELECT dpm.module,dpm.module_name,dpm.jump_type,dpm.jump_url,dpm.icon,pm.`function`,DATE_FORMAT(MAX(pm.push_time),'%Y-%m-%d %H:%i:%S') AS last_push_time,COUNT(IF(pm.`have_read`=0,1,NULL)) AS not_read FROM `dd_push_message` AS pm LEFT JOIN `dd_msg_module` AS dpm ON pm.`function`=dpm.`function` WHERE pm.`user_id`=" + userId + "  AND pm.`DELETE_FLAG`=0 AND dpm.`DELETE_FLAG`=0 AND dpm.id IS NOT NULL GROUP BY dpm.`function`) AS msg ORDER BY msg.last_push_time DESC";

        System.out.println("模块统计:" + sql);
        RowMapper<PushMsgDomain> rowMapper = new RowMapper<PushMsgDomain>() {
            @Override
            public PushMsgDomain mapRow(ResultSet rs, int rowNum) throws SQLException {

                PushMsgDomain psd = new PushMsgDomain();
                psd.setModule(rs.getString("module"));
                psd.setModuleName(rs.getString("module_name"));
                psd.setFunction(rs.getString("function"));
                psd.setIcon(rs.getString("icon"));
                psd.setJumpType(rs.getString("jump_type"));
                psd.setJumpUrl(rs.getString("jump_url"));
                psd.setLastPushTime(rs.getString("last_push_time"));
                psd.setNotRead(rs.getLong("not_read"));
                return psd;
            }
        };
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<PushMsgDomain> findByMsgInfoV2(Long userId) {
        String sql = "SELECT dpm.module,dpm.module_name,dpm.jump_type,dpm.jump_url,dpm.icon,ppmm.`function`,DATE_FORMAT(ppmm.push_time,'%Y-%m-%d %H:%i:%S') AS last_push_time,ppmm.`content` FROM `dd_push_message` AS ppmm INNER JOIN `dd_msg_module` AS dpm ON ppmm.`function`=dpm.`function` WHERE ppmm.`push_time` IN(SELECT MAX(pm.`push_time`) FROM `dd_push_message` AS pm WHERE pm.`user_id`=" + userId + "  AND pm.`DELETE_FLAG`=0 GROUP BY pm.`function`) AND ppmm.`user_id`=" + userId + " GROUP BY ppmm.`function` ORDER BY ppmm.`push_time` DESC";
        System.out.println("模块统计:" + sql);
        RowMapper<PushMsgDomain> rowMapper = new RowMapper<PushMsgDomain>() {
            @Override
            public PushMsgDomain mapRow(ResultSet rs, int rowNum) throws SQLException {

                PushMsgDomain psd = new PushMsgDomain();
                psd.setModule(rs.getString("module"));
                psd.setModuleName(rs.getString("module_name"));
                psd.setFunction(rs.getString("function"));
                psd.setIcon(rs.getString("icon"));
                psd.setJumpType(rs.getString("jump_type"));
                psd.setJumpUrl(rs.getString("jump_url"));
                psd.setLastPushTime(rs.getString("last_push_time"));
                psd.setNewInfo(rs.getString("content"));
                psd.setNotRead(0L);
                return psd;
            }
        };
        return jdbcTemplate.query(sql, rowMapper);
    }


    public Map<String, Long> totalFunctionNotRead(Long userId) {
        String sql = "SELECT dpm.`function`,COUNT(dpm.`function`) as not_read  FROM `dd_push_message` AS dpm WHERE dpm.`user_id`=" + userId + " AND dpm.`have_read`=0 GROUP BY dpm.`function`";
        System.out.println("模块未读统计:" + sql);
        Map<String, Long> map = new HashMap<>();
        RowMapper<PushMsgDomain> rowMapper = new RowMapper<PushMsgDomain>() {
            @Override
            public PushMsgDomain mapRow(ResultSet rs, int rowNum) throws SQLException {

                PushMsgDomain psd = new PushMsgDomain();
                psd.setFunction(rs.getString("function"));
                psd.setNotRead(rs.getLong("not_read"));
                return psd;
            }
        };
        List<PushMsgDomain> pdl = jdbcTemplate.query(sql, rowMapper);
        if (null != pdl && 0 < pdl.size()) {
            for (PushMsgDomain pushMsgDomain : pdl) {
                map.put(pushMsgDomain.getFunction(), pushMsgDomain.getNotRead());
            }
        }
        return map;
    }


    public List<Long> findRevertMsg() {
        String sql = "SELECT dpm.`user_id` FROM `dd_push_message` AS dpm WHERE dpm.`module`='revert' AND dpm.`user_id` IS NOT NULL GROUP BY dpm.`user_id`";
        return jdbcTemplate.queryForList(sql, Long.class);
    }

}
