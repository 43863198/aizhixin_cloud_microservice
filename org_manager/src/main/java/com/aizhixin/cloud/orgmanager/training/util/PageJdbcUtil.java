
package com.aizhixin.cloud.orgmanager.training.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.PageDomain;
import com.aizhixin.cloud.orgmanager.common.core.ApiReturnConstants;
import com.aizhixin.cloud.orgmanager.training.dto.SortDTO;



@Repository
public class PageJdbcUtil {
	 private static Logger log = LoggerFactory.getLogger(PageJdbcUtil.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	//默认当前页数：第1页 
    public static final int DEFAULT_OFFSET = 1;

    public static final int MIN_OFFSET = 1;
    //默认每页记录数: 10条
    public static final int DEFAULT_LIMIT = 10;

    public static final int MAX_LIMIT = 100;
	
	public <T> Map<String, Object> getPageInfor(Integer pageSize, Integer offset,RowMapper<T> rowMapper,
			List<SortDTO> sort,String querySql,String countSql) {
		Map<String, Object> r = new HashedMap();
		
		if (pageSize == null || pageSize <= 0)
			pageSize = DEFAULT_LIMIT;
		if (offset == null || offset <= 0)
			offset = DEFAULT_OFFSET;

		Long totalCount = jdbcTemplate.queryForObject(
				countSql, Long.class);
		String orderBy = "";
		// sort不为空时按页面输入排序操作
		if (sort != null) {
			orderBy = getOrderByStatement(sort);
		}else{
			
		}
		querySql = querySql + orderBy;
		int start = (offset - 1) * pageSize;
		querySql += " limit " + start + " , " + pageSize;
		log.debug("find page = {} " + querySql);
		List<T> data = jdbcTemplate.query(querySql, rowMapper);
		log.debug("data size : {} ", data.size());

		int pageCount = totalCount == 0 ? 1 : (int) Math
				.ceil((double) totalCount / (double) pageSize);
		PageDomain p = new PageDomain();
		p.setPageNumber(offset);
		p.setPageSize(pageSize);
		p.setTotalElements(totalCount);
		p.setTotalPages(pageCount);
		r.put(ApiReturnConstants.PAGE, p);
		r.put(ApiReturnConstants.DATA, data);
		return r;
	}
	
	
	public <T> PageData<T> getPageData(Integer pageSize, Integer offset,RowMapper<T> rowMapper,
			List<SortDTO> sort,String querySql,String countSql) {
		PageData page = new PageData();
		
		if (pageSize == null || pageSize <= 0)
			pageSize = DEFAULT_LIMIT;
		if (offset == null || offset <= 0)
			offset = DEFAULT_OFFSET;

		Long totalCount = jdbcTemplate.queryForObject(
				countSql, Long.class);
		String orderBy = "";
		// sort不为空时按页面输入排序操作
		if (sort != null) {
			orderBy = getOrderByStatement(sort);
		}else{
			
		}
		querySql = querySql + orderBy;
		int start = (offset - 1) * pageSize;
		querySql += " limit " + start + " , " + pageSize;
		log.debug("find page = {} " + querySql);
		List<T> data = jdbcTemplate.query(querySql, rowMapper);
		log.debug("data size : {} ", data.size());

		int pageCount = totalCount == 0 ? 1 : (int) Math
				.ceil((double) totalCount / (double) pageSize);
		PageDomain p = new PageDomain();
		p.setPageNumber(offset);
		p.setPageSize(pageSize);
		p.setTotalElements(totalCount);
		p.setTotalPages(pageCount);
		page.setData(data);
		page.setPage(p);
		return page;
	}
	
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
			if (!StringUtils.isEmpty(s))
				s = " ORDER BY " + s;
			return s;
		}
	}
	
	public <T> List<T> getInfo(String sql, RowMapper<T> rowMapper){

		log.debug("getInfo sql = {} " + sql);
		List<T> data = jdbcTemplate.query(sql, rowMapper);
		log.debug("getInfo data size : {} ", data.size());

		return data;
	}
	
	public Long getCount(String sql){
		Long totalCount = jdbcTemplate.queryForObject(
				sql, Long.class);
		return totalCount;
	}
}
