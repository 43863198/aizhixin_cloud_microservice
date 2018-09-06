package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.dd.rollcall.dto.RevertDTO;
@Repository
public class RevertJdbc {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public List<RevertDTO> findByRevert(Long userId,String module,Integer pageNumber,Integer pageSize) {
		String sql="SELECT dr.*,da.`anonymity`,da.`module`,da.`schedule_id`,da.`comment_id`,da.`comment_name`,da.`sourse_id` FROM `dd_revert` AS dr LEFT JOIN `dd_assess` AS da ON dr.`assess_id`=da.`id` WHERE";
		if(!StringUtils.isEmpty(module)){
			sql+=" da.`module`='"+module+"' AND";
		}
		sql+="  (dr.`to_user_id`="+userId+" or (da.`comment_id`="+userId+" and dr.`to_user_id` is null)) AND da.`DELETE_FLAG`=0 AND dr.`delete_flag`=0 ORDER BY dr.`created_date` DESC limit "+pageNumber+","+pageSize;
		RowMapper<RevertDTO> rowMapper=new RowMapper<RevertDTO>() {
			@Override
			public RevertDTO mapRow(ResultSet arg0, int arg1) throws SQLException {

				RevertDTO rd=new RevertDTO();
				rd.setAnonymity(arg0.getBoolean("anonymity"));
				rd.setAssessId(arg0.getLong("assess_id"));
				rd.setContent(arg0.getString("content"));
				rd.setCreatedDate(arg0.getTimestamp("created_date"));
				rd.setFromUserId(arg0.getLong("from_user_id"));
				if(arg0.getLong("from_user_id")==arg0.getLong("comment_id")&&arg0.getBoolean("anonymity")){
					rd.setFromUserName("匿名");
				}else{
					rd.setFromUserName(arg0.getString("from_user_name"));
				}
				rd.setModule(arg0.getString("module"));
				rd.setId(arg0.getLong("id"));
				if(0l==arg0.getLong("to_user_id")){
					rd.setToUserId(arg0.getLong("comment_id"));
					if(arg0.getBoolean("anonymity")){
						rd.setToUserName("匿名");
					}else{
						rd.setToUserName(arg0.getString("comment_name"));
					}
				}else{
					rd.setToUserId(arg0.getLong("to_user_id"));
					if(arg0.getLong("to_user_id")==arg0.getLong("comment_id")&&arg0.getBoolean("anonymity")){
						rd.setToUserName("匿名");
					}else{
						rd.setToUserName(arg0.getString("to_user_name"));
					}
					
				}
				rd.setScheduleId(arg0.getLong("schedule_id"));
				rd.setModule(arg0.getString("module"));
				rd.setSourceId(arg0.getLong("sourse_id"));
				return rd;
			}
		};
		return jdbcTemplate.query(sql, rowMapper);
	}
	
	public Integer countRevert(Long userId,String module) {
		String sql="SELECT count(*) FROM `dd_revert` AS dr LEFT JOIN `dd_assess` AS da ON dr.`assess_id`=da.`id` WHERE ";
		if(!StringUtils.isEmpty(module)){
			sql+="da.`module`='"+module+"' AND";
		}
		sql+="  (dr.`to_user_id`="+userId+" or (da.`comment_id`="+userId+" and dr.`to_user_id` is null))  AND da.`DELETE_FLAG`=0 AND dr.`delete_flag`=0 ORDER BY dr.`created_date` DESC ";
		return jdbcTemplate.queryForObject(sql, Integer.class);
		
	}
	
	public RevertDTO findFirstByRevert(Long userId) {
		String sql="SELECT dr.*,da.`anonymity`,da.`module`,da.`schedule_id`,da.`comment_id`,da.`comment_name` FROM `dd_revert` AS dr LEFT JOIN `dd_assess` AS da ON dr.`assess_id`=da.`id` WHERE";
		sql+="  (dr.`to_user_id`="+userId+" or (da.`comment_id`="+userId+" and dr.`to_user_id` is null)) AND da.`DELETE_FLAG`=0 AND dr.`delete_flag`=0 ORDER BY dr.`created_date` DESC limit 1";
		RowMapper<RevertDTO> rowMapper=new RowMapper<RevertDTO>() {
			@Override
			public RevertDTO mapRow(ResultSet arg0, int arg1) throws SQLException {

				RevertDTO rd=new RevertDTO();
				rd.setAnonymity(arg0.getBoolean("anonymity"));
				rd.setAssessId(arg0.getLong("assess_id"));
				rd.setContent(arg0.getString("content"));
				rd.setCreatedDate(arg0.getTimestamp("created_date"));
				rd.setFromUserId(arg0.getLong("from_user_id"));
				if(arg0.getLong("from_user_id")==arg0.getLong("comment_id")&&arg0.getBoolean("anonymity")){
					rd.setFromUserName("匿名");
				}else{
					rd.setFromUserName(arg0.getString("from_user_name"));
				}
				rd.setModule(arg0.getString("module"));
				rd.setId(arg0.getLong("id"));
				if(0l==arg0.getLong("to_user_id")){
					rd.setToUserId(arg0.getLong("comment_id"));
					if(arg0.getBoolean("anonymity")){
						rd.setToUserName("匿名");
					}else{
						rd.setToUserName(arg0.getString("comment_name"));
					}
				}else{
					rd.setToUserId(arg0.getLong("to_user_id"));
					if(arg0.getLong("to_user_id")==arg0.getLong("comment_id")&&arg0.getBoolean("anonymity")){
						rd.setToUserName("匿名");
					}else{
						rd.setToUserName(arg0.getString("to_user_name"));
					}
					
				}
				rd.setScheduleId(arg0.getLong("schedule_id"));
				rd.setModule(arg0.getString("module"));
				return rd;
			}
		};
		List<RevertDTO> rl=jdbcTemplate.query(sql, rowMapper);
		if(null!=rl&&0<rl.size()) {
		   return rl.get(0);	
		}
		return null;
	}
	
}
