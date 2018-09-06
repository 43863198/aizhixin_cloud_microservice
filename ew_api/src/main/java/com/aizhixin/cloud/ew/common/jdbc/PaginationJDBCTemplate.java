package com.aizhixin.cloud.ew.common.jdbc;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.aizhixin.cloud.ew.common.PageInfo;
import com.aizhixin.cloud.ew.common.core.PageUtil;

import java.util.List;

@Repository
public class PaginationJDBCTemplate<T> {

	protected final Logger log = LoggerFactory.getLogger(PaginationJDBCTemplate.class);
	@Autowired
	protected JdbcTemplate jdbcTemplate;

	private String getOrderByStatement(List<SortDTO> sorts) {
		if (sorts == null)
			return "";

		else {
			String s = "";
			log.debug("sorts size : {} ", sorts.size());
			for (int i = 0; i < sorts.size(); i++) {
				SortDTO sdto = sorts.get(i);
				if (sdto.getAsc())
					s += sdto.getKey() + " ASC";
				else
					s += sdto.getKey() + " DESC";
				if (i < sorts.size() - 1)
					s += " , ";
			}
			if (StringUtils.isNotBlank(s))
				s = " ORDER BY " + s;
			return s;
		}
	}

	@SuppressWarnings("rawtypes")
	public PageInfo getPageInfo(Integer pageSize, Integer offset, RowMapper<T> rowMapper, List<SortDTO> sort,
			PaginationSQL paginationSQL) throws Exception {
		if (pageSize == null || pageSize <= 0)
			pageSize = PageUtil.DEFAULT_LIMIT;
		if (offset == null || offset <= 0)
			offset = PageUtil.DEFAULT_OFFSET;
		if (paginationSQL.getFindSql().toLowerCase().contains("limit")
				|| paginationSQL.getFindSql().contains("order by"))
			throw new Exception("this sql cannot contains limit or order by statement when query for pagination {"
					+ paginationSQL.getFindSql() + "}");
		Long totalCount = jdbcTemplate.queryForObject(paginationSQL.getFindCountSql(), Long.class);
		String sql = paginationSQL.getFindSql();
		String orderBy = "";
		// sort不为空时按页面输入排序操作
		if (sort != null) {
			orderBy = getOrderByStatement(sort);
		}
		// 为空时按默认排序操作
		else {
			if (paginationSQL.sort() != null)
				orderBy = getOrderByStatement(paginationSQL.sort());
		}
		sql = sql + orderBy;
		int start = (offset - 1) * pageSize;
		sql += " limit " + start + " , " + pageSize;
		log.debug("find page = {} " + sql);
		List<T> data = jdbcTemplate.query(sql, rowMapper);
		log.debug("data size : {} ", data.size());

		int pageCount = totalCount == 0 ? 1 : (int) Math.ceil((double) totalCount / (double) pageSize);
		PageInfo info = new PageInfo();
		info.setLimit(pageSize);
		info.setOffset(offset);
		info.setPageCount(pageCount);
		info.setTotalCount(totalCount);
		info.setData(data);
		return info;
	}

	public List<T> getInfo(String sql, RowMapper<T> rowMapper) throws Exception {

		log.debug("getInfo sql = {} " + sql);
		List<T> data = jdbcTemplate.query(sql, rowMapper);
		log.debug("getInfo data size : {} ", data.size());

		return data;
	}

}
