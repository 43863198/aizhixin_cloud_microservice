package com.aizhixin.cloud.studentpractice.common.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.aizhixin.cloud.studentpractice.common.core.DataValidity;
import com.aizhixin.cloud.studentpractice.common.domain.PushMessageStatusDTO;


@Repository
public class PushMessageQuery {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public List<PushMessageStatusDTO> queryMessageState(String module,
			String function, Long userId) {

		String sql = "select * from ( SELECT "
				+ " date_format(max(pm.push_time),'%Y-%m-%d %H:%i:%S') last_push_time, "
				+ " count(1) push_count, "
				+ " count(IF(pm.have_read = 0,1,null)) not_read, "
				+ " module,`function` "
				+ " FROM "
				+ " dd_push_message pm "
				+ " WHERE "
				+ " #where_space# group by pm.module,pm.function ) t where t.module is not null  ";

		String wheresql = " 1 = 1 ";
		if (StringUtils.isNotBlank(module)) {
			wheresql += " and pm.module = '" + module + "' ";
		}
		if (StringUtils.isNotBlank(function)) {
			wheresql += " and pm.function = '" + function + "' ";
		}
		if (userId != null && userId > 0) {
			wheresql += " and pm.user_id = " + userId + " ";
			wheresql += " and pm.delete_flag = '"
					+ DataValidity.VALID.getIntValue() + "'";
		}

		sql = sql.replace("#where_space#", wheresql);

		return jdbcTemplate.query(sql, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				PushMessageStatusDTO item = new PushMessageStatusDTO();
				item.setFunction(rs.getString("function"));
				item.setModule(rs.getString("module"));
				item.setLastPushTime(rs.getString("last_push_time"));
				item.setNotRead(rs.getInt("not_read"));
				item.setPushCount(rs.getInt("push_count"));
				return item;
			}
		});
	}
}
