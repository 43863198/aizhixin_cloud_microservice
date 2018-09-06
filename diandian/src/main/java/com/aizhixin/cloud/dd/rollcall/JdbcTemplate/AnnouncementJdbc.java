package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author xiagen
 * @date 2018/2/6 14:15
 * @param
 * @return
 */
@Repository
public class AnnouncementJdbc {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void deleteById(Long id) {
		String sql="DELETE FROM `dd_announcement_file` WHERE `announcement_id`= "+id;
		jdbcTemplate.batchUpdate(sql);
	}
	
	
	public void deleteByGroupId(String groupId) {
		String sql1="DELETE FROM `dd_announcement_group` WHERE `group_id`='"+groupId+"'";
		jdbcTemplate.batchUpdate(sql1);
	}
	
	public List<String> findGroupInfo(Long userId) {
		String sql="SELECT dag.group_id FROM  `dd_announcement_group` AS dag WHERE dag.user_id="+userId+" AND dag.delete_flag=0";
		return jdbcTemplate.queryForList(sql, String.class);
	}
	
	

}
