//package com.aizhixin.cloud.dd.rollcall.JdbcTemplate;
//
//import java.io.IOException;
//import java.util.List;
//
//import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.DataAccessException;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//import org.springframework.stereotype.Repository;
//
//import com.aizhixin.cloud.dd.common.exception.DlEduException;
//import com.aizhixin.cloud.dd.rollcall.dto.PageInfo;
//import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;
//import com.aizhixin.cloud.dd.rollcall.utils.PaginationUtil;
//import com.fasterxml.jackson.core.JsonParseException;
//import com.fasterxml.jackson.databind.JsonMappingException;
//
//@Repository
//public class PaginationJDBCTemplate<T> {
//
//    protected final Logger log = LoggerFactory.getLogger(PaginationJDBCTemplate.class);
//    @Autowired
//    protected JdbcTemplate jdbcTemplate;
//
//
//
//    @SuppressWarnings("rawtypes")
//    public PageInfo getPageInfo(Integer pageSize, Integer offset, RowMapper<T> rowMapper, List<SortDTO> sort,
//                                PaginationSQL paginationSQL, List<Long> teancherID, List<Long> courseID ) throws DlEduException, DataAccessException, JsonParseException, JsonMappingException, IOException {
//        if (pageSize == null || pageSize <= 0)
//            pageSize = PaginationUtil.DEFAULT_LIMIT;
//        if (offset == null || offset <= 0)
//            offset = PaginationUtil.DEFAULT_OFFSET;
//        if (paginationSQL.getFindSql().toLowerCase().contains("limit")
//            || paginationSQL.getFindSql().contains("order by"))
//            throw new DlEduException("this sql cannot contains limit or order by statement when query for pagination {"
//                + paginationSQL.getFindSql() + "}");
//        NamedParameterJdbcTemplate  jdbc=new NamedParameterJdbcTemplate(jdbcTemplate);
//        String sqlCount=paginationSQL.getFindCountSql();
//        String sql = paginationSQL.getFindSql();
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//
//        if (teancherID != null&&!teancherID.isEmpty()) {
//
//        	sqlCount += " and dq.TEACHER_ID in(:teacherId)";
//        	sql += " and dq.TEACHER_ID in(:teacherId)";
//        	 parameters.addValue("teacherId", teancherID);
//        }
//        if (courseID != null&& !courseID.isEmpty()) {
//
//        	sqlCount += " and dq.COURSE_ID in(:courseId)";
//        	sql += " and dq.COURSE_ID in(:courseId)";
//        	parameters.addValue("courseId", courseID);
//        }
//
//
//        Long totalCount = jdbc.queryForObject(sqlCount, parameters,  Long.class);
//       System.out.println(totalCount);
//        String orderBy = "";
//        // sort不为空时按页面输入排序操作
//        if (sort != null) {
//            orderBy = getOrderByStatement(sort);
//        }
//        // 为空时按默认排序操作
//        else {
//            if (paginationSQL.sort() != null)
//                orderBy = getOrderByStatement(paginationSQL.sort());
//        }
//        sql = sql + orderBy;
//        int start = (offset - 1) * pageSize;
//        sql += " limit " + start + " , " + pageSize;
//        log.debug("find page = {} " + sql);
//        System.out.println(sql);
//        List<T> data = jdbc.query(sql, parameters, rowMapper);
//        log.debug("data size : {} ", data.size());
//
//        int pageCount = totalCount == 0 ? 1 : (int) Math.ceil((double) totalCount / (double) pageSize);
//        PageInfo info = new PageInfo();
//        info.setLimit(pageSize);
//        info.setOffset(offset);
//        info.setPageCount(pageCount);
//        info.setTotalCount(totalCount);
//        info.setData(data);
//        return info;
//    }
//    private String getOrderByStatement(List<SortDTO> sorts) {
//        if (sorts == null)
//            return "";
//
//        else {
//            String s = "";
//            log.debug("sorts size : {} ", sorts.size());
//            for (int i = 0; i < sorts.size(); i++) {
//                SortDTO sdto = sorts.get(i);
//                if (sdto.getAsc())
//                    s += sdto.getKey() + " ASC";
//                else
//                    s += sdto.getKey() + " DESC";
//                if (i < sorts.size() - 1)
//                    s += " , ";
//            }
//            if (StringUtils.isNotBlank(s))
//                s = " ORDER BY " + s;
//            return s;
//        }
//    }
//
//}