package com.aizhixin.cloud.dd.communication.jdbcTemplate;

import com.aizhixin.cloud.dd.common.exception.DlEduException;
import com.aizhixin.cloud.dd.communication.dto.*;
import com.aizhixin.cloud.dd.communication.utils.Gaom;
import com.aizhixin.cloud.dd.rollcall.dto.PageInfo;
import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;
import com.aizhixin.cloud.dd.rollcall.utils.PaginationUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class PaginationJDBCTemplate<T> {

    protected final Logger log = LoggerFactory.getLogger(PaginationJDBCTemplate.class);
    @Autowired
    protected JdbcTemplate jdbcTemplate;


    @SuppressWarnings("rawtypes")
    public PageInfo getPageInfo(Integer pageSize, Integer offset, RowMapper<T> rowMapper, List<SortDTO> sort,
                                PaginationSQL paginationSQL, List<Long> teancherID, List<Long> courseID) throws DlEduException, DataAccessException, JsonParseException, JsonMappingException, IOException {
        if (pageSize == null || pageSize <= 0)
            pageSize = PaginationUtil.DEFAULT_LIMIT;
        if (offset == null || offset <= 0)
            offset = PaginationUtil.DEFAULT_OFFSET;
        if (paginationSQL.getFindSql().toLowerCase().contains("limit")
                || paginationSQL.getFindSql().contains("order by"))
            throw new DlEduException("this sql cannot contains limit or order by statement when query for pagination {"
                    + paginationSQL.getFindSql() + "}");
        NamedParameterJdbcTemplate jdbc = new NamedParameterJdbcTemplate(jdbcTemplate);
        String sqlCount = paginationSQL.getFindCountSql();
        String sql = paginationSQL.getFindSql();
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if (teancherID != null && !teancherID.isEmpty()) {

            sqlCount += " and dq.TEACHER_ID in(:teacherId)";
            sql += " and dq.TEACHER_ID in(:teacherId)";
            parameters.addValue("teacherId", teancherID);
        }
        if (courseID != null && !courseID.isEmpty()) {

            sqlCount += " and dq.COURSE_ID in(:courseId)";
            sql += " and dq.COURSE_ID in(:courseId)";
            parameters.addValue("courseId", courseID);
        }


        Long totalCount = jdbc.queryForObject(sqlCount, parameters, Long.class);
        System.out.println(totalCount);
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
        log.info("find page = {} " + sql);
        System.out.println(sql);
        List<T> data = jdbc.query(sql, parameters, rowMapper);
        log.info("data size : {} ", data.size());

        int pageCount = totalCount == 0 ? 1 : (int) Math.ceil((double) totalCount / (double) pageSize);
        PageInfo info = new PageInfo();
        info.setLimit(pageSize);
        info.setOffset(offset);
        info.setPageCount(pageCount);
        info.setTotalCount(totalCount);
        info.setData(data);
        return info;
    }

    private String getOrderByStatement(List<SortDTO> sorts) {
        if (sorts == null)
            return "";

        else {
            String s = "";
            log.info("sorts size : {} ", sorts.size());
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


    public PageInfo getPageInfo(Integer pageSize, Integer offset, RowMapper<T> rowMapper, List<SortDTO> sort,
                                PaginationSQL paginationSQL) throws DlEduException {
        if (pageSize == null || pageSize <= 0)
            pageSize = PaginationUtil.DEFAULT_LIMIT;
        if (offset == null || offset <= 0)
            offset = PaginationUtil.DEFAULT_OFFSET;
        if (paginationSQL.getFindSql().toLowerCase().contains("limit")
                || paginationSQL.getFindSql().contains("order by"))
            throw new DlEduException("this sql cannot contains limit or order by statement when query for pagination {"
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
        log.info("find page = {} " + sql);
        List<T> data = jdbcTemplate.query(sql, rowMapper);
        log.info("data size : {} ", data.size());

        int pageCount = totalCount == 0 ? 1 : (int) Math.ceil((double) totalCount / (double) pageSize);
        PageInfo info = new PageInfo();
        info.setLimit(pageSize);
        info.setOffset(offset);
        info.setPageCount(pageCount);
        info.setTotalCount(totalCount);
        info.setData(data);
        return info;
    }


    //
    @SuppressWarnings("rawtypes")
    public PageInfo getOnLineUserInfo(Integer pageSize, Integer offset, RowMapper<T> rowMapper, List<SortDTO> sort,
                                      PaginationSQL paginationSQL) throws DlEduException {
        if (pageSize == null || pageSize <= 0)
            pageSize = PaginationUtil.DEFAULT_LIMIT;
        if (offset == null || offset <= 0)
            offset = PaginationUtil.DEFAULT_OFFSET;
        if (paginationSQL.getFindSql().toLowerCase().contains("limit")
                || paginationSQL.getFindSql().contains("order by"))
            throw new DlEduException("this sql cannot contains limit or order by statement when query for pagination {"
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
        log.info("find page = {} " + sql);
        List<T> data = jdbcTemplate.query(sql, rowMapper);
        log.info("data size : {} ", data.size());

        int pageCount = totalCount == 0 ? 1 : (int) Math.ceil((double) totalCount / (double) pageSize);
        PageInfo info = new PageInfo();
        info.setLimit(pageSize);
        info.setOffset(offset);
        info.setPageCount(pageCount);
        info.setTotalCount(totalCount);
        info.setData(data);
        return info;
    }

    @SuppressWarnings("rawtypes")
    public PageInfo getOutOfRangeDayInfo(Integer pageSize, Integer offset, RowMapper<T> rowMapper, List<SortDTO> sort,
                                         PaginationSQL paginationSQL) throws DlEduException {
        if (pageSize == null || pageSize <= 0)
            pageSize = PaginationUtil.DEFAULT_LIMIT;
        if (offset == null || offset <= 0)
            offset = PaginationUtil.DEFAULT_OFFSET;
        if (paginationSQL.getFindSql().toLowerCase().contains("limit")
                || paginationSQL.getFindSql().contains("order by"))
            throw new DlEduException("this sql cannot contains limit or order by statement when query for pagination {"
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
        log.info("find page = {} " + sql);
        List<T> data = jdbcTemplate.query(sql, rowMapper);
        log.info("data size : {} ", data.size());

        int pageCount = totalCount == 0 ? 1 : (int) Math.ceil((double) totalCount / (double) pageSize);
        PageInfo info = new PageInfo();
        info.setLimit(pageSize);
        info.setOffset(offset);
        info.setPageCount(pageCount);
        info.setTotalCount(totalCount);
        info.setData(data);
        return info;
    }

    @SuppressWarnings("rawtypes")
    public PageInfo WeekLeaveOneDayInfo(Integer pageSize, Integer offset, RowMapper<T> rowMapper, List<SortDTO> sort,
                                        PaginationSQL paginationSQL) throws DlEduException {
        if (pageSize == null || pageSize <= 0)
            pageSize = PaginationUtil.DEFAULT_LIMIT;
        if (offset == null || offset <= 0)
            offset = PaginationUtil.DEFAULT_OFFSET;
        if (paginationSQL.getFindSql().toLowerCase().contains("limit")
                || paginationSQL.getFindSql().contains("order by"))
            throw new DlEduException("this sql cannot contains limit or order by statement when query for pagination {"
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
        log.info("find page = {} " + sql);
        List<T> data = jdbcTemplate.query(sql, rowMapper);
        log.info("data size : {} ", data.size());

        int pageCount = totalCount == 0 ? 1 : (int) Math.ceil((double) totalCount / (double) pageSize);
        PageInfo info = new PageInfo();
        info.setLimit(pageSize);
        info.setOffset(offset);
        info.setPageCount(pageCount);
        info.setTotalCount(totalCount);
        info.setData(data);
        return info;
    }


    @SuppressWarnings("rawtypes")
    public PageInfo getNotActiveUser(Integer pageSize, Integer offset, RowMapper<T> rowMapper, Long collegeId,
                                     Long majorId, Long classId, Long long1, List<SortDTO> sort, PaginationSQL paginationSQL)
            throws DlEduException {
        if (pageSize == null || pageSize <= 0)
            pageSize = PaginationUtil.DEFAULT_LIMIT;
        if (offset == null || offset <= 0)
            offset = PaginationUtil.DEFAULT_OFFSET;
        if (paginationSQL.getFindSql().toLowerCase().contains("limit")
                || paginationSQL.getFindSql().contains("order by"))
            throw new DlEduException("this sql cannot contains limit or order by statement when query for pagination {"
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
        log.info("find page = {} " + sql);
        List<T> data = jdbcTemplate.query(sql, rowMapper);
        log.info("data size : {} ", data.size());

        int pageCount = totalCount == 0 ? 1 : (int) Math.ceil((double) totalCount / (double) pageSize);
        PageInfo info = new PageInfo();
        info.setLimit(pageSize);
        info.setOffset(offset);
        info.setPageCount(pageCount);
        info.setTotalCount(totalCount);
        info.setData(data);
        return info;
    }

    @SuppressWarnings("rawtypes")
    public PageInfo getHistoryPageInfo(Integer pageSize, Integer offset, RowMapper<T> rowMapper, List<SortDTO> sort,
                                       PaginationSQL paginationSQL) throws DlEduException {
        if (pageSize == null || pageSize <= 0)
            pageSize = PaginationUtil.DEFAULT_LIMIT;
        if (offset == null || offset <= 0)
            offset = PaginationUtil.DEFAULT_OFFSET;
        if (paginationSQL.getFindSql().toLowerCase().contains("limit")
                || paginationSQL.getFindSql().contains("order by"))
            throw new DlEduException("this sql cannot contains limit or order by statement when query for pagination {"
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
        log.info("find page = {} " + sql);
        List<T> data = jdbcTemplate.query(sql, rowMapper);
        log.info("data size : {} ", data.size());

        int pageCount = totalCount == 0 ? 1 : (int) Math.ceil((double) totalCount / (double) pageSize);
        PageInfo info = new PageInfo();
        info.setLimit(pageSize);
        info.setOffset(offset);
        info.setPageCount(pageCount);
        info.setTotalCount(totalCount);
        info.setData(data);
        return info;
    }

    @SuppressWarnings("rawtypes")
    public PageInfo getPageInfoss(Integer pageSize, Integer offset, RowMapper<T> rowMapper, List<SortDTO> sort,
                                  PaginationSQL paginationSQL) throws DlEduException {
        if (pageSize == null || pageSize <= 0)
            pageSize = PaginationUtil.DEFAULT_LIMIT;
        if (offset == null || offset <= 0)
            offset = PaginationUtil.DEFAULT_OFFSET;
        if (paginationSQL.getFindSql().toLowerCase().contains("limit"))
            throw new DlEduException("this sql cannot contains limit or order by statement when query for pagination {"
                    + paginationSQL.getFindSql() + "}");
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
        log.info("find page = {} " + sql);
        List<T> data = jdbcTemplate.query(sql, rowMapper);

        System.out.println("TTTTTTTTTTT  " + data);

        List<Object> da = new ArrayList<Object>();
        for (int i = 0; i < data.size(); i++) {
            ElectricFenceQueryOutOfRangeDTO oo = (ElectricFenceQueryOutOfRangeDTO) data.get(i);
            String id = Long.toString(oo.getId());
            String name = oo.getName();
            String noticeTime = oo.getNoticeTime().toString();
            long time = oo.getNoticeTimeInterval();
            String lltude = oo.getLltude();
            String lltudes = oo.getLltudes();
            List lltudeData = JSONArray.toList(JSONArray.fromObject(lltudes), Lonlat.class);
            System.out.println("第一次打印截取之前时间 notice  " + noticeTime + "  name   " + name);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date1 = sdf.parse(noticeTime);
                Date date2 = new Date();
                long minutes = date2.getTime() - date1.getTime();
                long min = minutes / (1000);
                System.out.println("相隔" + min + "秒");
                // 在线时在判断超出范围
                if (min <= time) {
                    for (int j = 0; j < lltudeData.size(); j++) {
                        Gaom test = new Gaom();
                        boolean boo = test.isInPolygon(lltude, JSONArray.fromObject(lltudeData.get(j)).toString());
                        if (!boo) {
                            da.add(data.get(i));
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("data size : {} ", data.size());
        PageInfo info = new PageInfo();
        info.setTotalCount(((long) da.size()));
        info.setData(da);
        return info;

    }

    @SuppressWarnings("rawtypes")
    public PageInfo getLeaveConnectPageInfo(Integer pageSize, Integer offset, RowMapper<T> rowMapper,
                                            List<SortDTO> sort, PaginationSQL paginationSQL) throws DlEduException {
        if (pageSize == null || pageSize <= 0)
            pageSize = PaginationUtil.DEFAULT_LIMIT;
        if (offset == null || offset <= 0)
            offset = PaginationUtil.DEFAULT_OFFSET;
        if (paginationSQL.getFindSql().toLowerCase().contains("limit"))
            throw new DlEduException("this sql cannot contains limit or order by statement when query for pagination {"
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
        log.info("find page = {} " + sql);
        List<ElectricFenceLeaveConnectDTO> data = (List<ElectricFenceLeaveConnectDTO>) jdbcTemplate.query(sql,
                rowMapper);

        Map<String, String> map = new HashMap<String, String>();
        List<ElectricFenceLeaveConnectDTO> data1 = new ArrayList<ElectricFenceLeaveConnectDTO>();
        log.info("data size : {} ", data.size());
        String leaveNum = null;
        // 处理结果集合，计算目前离线的时长
        for (int i = 0; i < data.size(); i++) {
            ElectricFenceLeaveConnectDTO oo = (ElectricFenceLeaveConnectDTO) data.get(i);
            String id = Long.toString(oo.getId());
            String name = oo.getName();

            long time = oo.getNoticeTimeInterval();
            log.info("第一次打印截取之前时间 notice  " + oo.getNoticeTime() + "  name   " + name);

            if (oo.getNoticeTime() == null) {
                map.put(id, null);
            } else {

                try {
                    String noticeTime = oo.getNoticeTime().toString();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date1 = sdf.parse(noticeTime);

                    Date date2 = new Date();
                    long minutes = date2.getTime() - date1.getTime();

                    long min = minutes / (1000);
                    log.info("相隔" + min + "秒");
                    // 人数
                    if (min > time) {
                        long minn = minutes / (1000 * 60);
                        if (minn < 60) {
                            leaveNum = minn + "分钟";
                        } else {
                            Long dd = minn / 60;// 取整数部分为小时
                            Long mm = minn % 60;// 取余数部分为分钟
                            leaveNum = dd + "小时  " + mm + "分钟";
                        }
                        map.put(id, leaveNum);
                    }

                } catch (ParseException e) {

                    e.printStackTrace();
                }
            }
        }
        for (int i = 0; i < data.size(); i++) {
            ElectricFenceLeaveConnectDTO nn = (ElectricFenceLeaveConnectDTO) data.get(i);
            String id = nn.getId().toString();
            String name = nn.getName();
            ElectricFenceLeaveConnectDTO ele = new ElectricFenceLeaveConnectDTO();
            ele.setId(Long.parseLong(id));
            ele.setLeaveNum(map.get(id));
            ele.setName(name);
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if (nn.getNoticeTime() == null) {

            } else {
                String noticeTime = nn.getNoticeTime().toString();
                ele.setNoticeTime(null);
                Date sd = null;
                try {
                    sd = sdf1.parse(noticeTime);
                    ele.setNoticeTime(sd);

                } catch (ParseException e) {

                    e.printStackTrace();
                }
            }

            if (map.containsKey(id)) {
                data1.add(ele);
            }

        }

        int pageCount = totalCount == 0 ? 1 : (int) Math.ceil((double) totalCount / (double) pageSize);
        PageInfo info = new PageInfo();
        info.setLimit(pageSize);
        info.setOffset(offset);
        info.setPageCount(pageCount);
        info.setTotalCount((long) data1.size());
        info.setData(data1);
        return info;
    }

    @SuppressWarnings("rawtypes")
    public PageInfo getLeaveWeekPageInfo(Integer pageSize, Integer offset, RowMapper<T> rowMapper, List<SortDTO> sort,
                                         PaginationSQL paginationSQL) throws DlEduException {
        if (pageSize == null || pageSize <= 0)
            pageSize = PaginationUtil.DEFAULT_LIMIT;
        if (offset == null || offset <= 0)
            offset = PaginationUtil.DEFAULT_OFFSET;
        if (paginationSQL.getFindSql().toLowerCase().contains("limit"))
            throw new DlEduException("this sql cannot contains limit or order by statement when query for pagination {"
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
        log.info("find page = {} " + sql);
        List<T> data = jdbcTemplate.query(sql, rowMapper);

        int pageCount = totalCount == 0 ? 1 : (int) Math.ceil((double) totalCount / (double) pageSize);
        PageInfo info = new PageInfo();
        info.setLimit(pageSize);
        info.setOffset(offset);
        info.setPageCount(pageCount);
        info.setTotalCount((long) data.size());
        info.setData(data);
        return info;
    }

    @SuppressWarnings("rawtypes")
    public PageInfo getLeaveSchoolPageInfo(Integer pageSize, Integer offset, RowMapper<T> rowMapper, List<SortDTO> sort,
                                           PaginationSQL paginationSQL) throws DlEduException {
        if (pageSize == null || pageSize <= 0)
            pageSize = PaginationUtil.DEFAULT_LIMIT;
        if (offset == null || offset <= 0)
            offset = PaginationUtil.DEFAULT_OFFSET;
        if (paginationSQL.getFindSql().toLowerCase().contains("limit"))
            throw new DlEduException("this sql cannot contains limit or order by statement when query for pagination {"
                    + paginationSQL.getFindSql() + "}");
        log.info("=============", paginationSQL.getFindCountSql());
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
        log.info("find page = {} " + sql);
        List<T> data = jdbcTemplate.query(sql, rowMapper);
        List<ElectricFenceLeaveSchoolDTO> data1 = new ArrayList<ElectricFenceLeaveSchoolDTO>();
        long sum = 0;
        int c = 0;
        // 查看目前离校人数：
        for (int i = 0; i < data.size(); i++) {
            ElectricFenceLeaveSchoolDTO oo = (ElectricFenceLeaveSchoolDTO) data.get(i);
            String id = Long.toString(oo.getId());
            String noticeTime = oo.getNoticeTime().toString();
            long time = oo.getNoticeTimeInterval();
            String lltude = oo.getLltude();
            String lltudes = oo.getLltudes();
            List DataList = JSONArray.toList(JSONArray.fromObject(lltudes), Lonlat.class);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date1 = sdf.parse(noticeTime);
                Date date2 = new Date();
                long minutes = date2.getTime() - date1.getTime();
                long min = minutes / (1000);
                // 计算在线人数
                if (min <= time) {
                    sum = sum + 1;
                }
                for (int j = 0; j < DataList.size(); j++) {
                    Gaom test = new Gaom();
                    boolean boo = test.isInPolygon(lltude, JSONArray.fromObject(DataList.get(j)).toString());
                    if (!boo) {
                        c = c + 1;
                        break;
                    }
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
        log.info("YYYYYYY 总人数   " + totalCount + "  在线人数:  " + sum);

        long ll = totalCount - sum;
        ElectricFenceLeaveSchoolDTO el = new ElectricFenceLeaveSchoolDTO();
        el.setOutOfRange((long) c);
        el.setLeavenum(ll);
        el.setOnLinenum(sum);
        data1.add(el);

        int pageCount = totalCount == 0 ? 1 : (int) Math.ceil((double) totalCount / (double) pageSize);
        PageInfo info = new PageInfo();
        info.setLimit(pageSize);
        info.setOffset(offset);
        info.setPageCount(pageCount);
        info.setTotalCount(totalCount);
        info.setData(data1);
        return info;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public PageInfo getPushPageInfo(Integer pageSize, Integer offset, RowMapper<T> rowMapper, List<SortDTO> sort,
                                    PaginationSQL paginationSQL) throws DlEduException {
        if (pageSize == null || pageSize <= 0)
            pageSize = PaginationUtil.DEFAULT_LIMIT;
        if (offset == null || offset <= 0)
            offset = PaginationUtil.DEFAULT_OFFSET;
        if (paginationSQL.getFindSql().toLowerCase().contains("limit"))
            throw new DlEduException("this sql cannot contains limit or order by statement when query for pagination {"
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
        log.info("find page = {} " + sql);
        List<T> data = jdbcTemplate.query(sql, rowMapper);
        log.info("data size : {} ", data.size());

        int pageCount = totalCount == 0 ? 1 : (int) Math.ceil((double) totalCount / (double) pageSize);
        PageInfo info = new PageInfo();
        info.setLimit(pageSize);
        info.setOffset(offset);
        info.setPageCount(pageCount);
        info.setTotalCount(totalCount);
        info.setData(data);
        return info;
    }

    @SuppressWarnings("rawtypes")
    public PageInfo getPageElectricFenceInfo(Integer pageSize, Integer offset, RowMapper<T> electricfencequerymapper,
                                             List<SortDTO> sort, PaginationSQL paginationSQL) throws DlEduException {
        if (pageSize == null || pageSize <= 0)
            pageSize = PaginationUtil.DEFAULT_LIMIT;
        if (offset == null || offset <= 0)
            offset = PaginationUtil.DEFAULT_OFFSET;
        if (paginationSQL.getFindSql().toLowerCase().contains("limit"))
            throw new DlEduException("this sql cannot contains limit or order by statement when query for pagination {"
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
        log.info("find page = {} " + sql);
        List<T> data = jdbcTemplate.query(sql, electricfencequerymapper);
        int pageCount = totalCount == 0 ? 1 : (int) Math.ceil((double) totalCount / (double) pageSize);
        PageInfo info = new PageInfo();
        info.setLimit(pageSize);
        info.setOffset(offset);
        info.setPageCount(pageCount);
        info.setTotalCount(totalCount);
        info.setData(data);
        return info;
    }

    @SuppressWarnings("rawtypes")
    public ElectricFenceQueryDTO getPageElectricInfo(Integer pageSize, Integer offset,
                                                     RowMapper<T> electricfencequerymapper, List<SortDTO> sort, PaginationSQL paginationSQL)
            throws DlEduException {
        if (pageSize == null || pageSize <= 0)
            pageSize = PaginationUtil.DEFAULT_LIMIT;
        if (offset == null || offset <= 0)
            offset = PaginationUtil.DEFAULT_OFFSET;
        if (paginationSQL.getFindSql().toLowerCase().contains("limit"))
            throw new DlEduException("this sql cannot contains limit or order by statement when query for pagination {"
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
        log.info("find page = {} " + sql);
        List<T> data = jdbcTemplate.query(sql, electricfencequerymapper);

        ElectricFenceQueryDTO tt = (ElectricFenceQueryDTO) data.get(0);
        if (tt.getNoticeTime() == null) {
            tt.setStatus("离线");
        } else {
            String noticeTime = tt.getNoticeTime().toString();
            String timeInterval = Long.toString(tt.getNoticeTimeInterval());
            String lltude = tt.getLltude();
            String lltudes = tt.getLltudes();
            long time = Long.parseLong(timeInterval);
            List DataList = JSONArray.toList(JSONArray.fromObject(lltudes), Lonlat.class);

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                Date date1 = sdf.parse(noticeTime);

                Date date2 = new Date();
                long minutes = date2.getTime() - date1.getTime();
                long min = minutes / (1000);
                System.out.println("相隔" + min + "秒");
                // 在线人数
                if (min <= time) {
                    try {
                        for (int j = 0; j < DataList.size(); j++) {
                            Gaom test = new Gaom();
                            boolean boo = test.isInPolygon(lltude, JSONArray.fromObject(DataList.get(j)).toString());
                            if (!boo) {
                                tt.setStatus("超出范围");
                                break;
                            } else {
                                tt.setStatus("在线");
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.getStackTrace();
                    }

                } else {
                    tt.setStatus("离线");
                }
            } catch (ParseException e) {

                e.printStackTrace();
            }
        }
        return tt;
    }


    @SuppressWarnings("rawtypes")
    public PageInfo getStudentRollCall(Integer pageSize, Integer offset, RowMapper<Map<String, String>> rowMapper,
                                       List<SortDTO> sort, PaginationSQL paginationSQL) throws DlEduException {
        if (pageSize == null || pageSize <= 0)
            pageSize = PaginationUtil.DEFAULT_LIMIT;
        if (offset == null || offset <= 0)
            offset = PaginationUtil.DEFAULT_OFFSET;

        if (paginationSQL.getFindSql().toLowerCase().contains("limit")
                || paginationSQL.getFindSql().contains("order by"))
            throw new DlEduException("this sql cannot contains limit or order by statement when query for pagination {" + paginationSQL.getFindSql() + "}");
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
        log.info("find page = {} " + sql);
        List<Map<String, String>> data = jdbcTemplate.query(sql, rowMapper);
        log.info("data size : {} ", data.size());

        int pageCount = totalCount == 0 ? 1 : (int) Math.ceil((double) totalCount / (double) pageSize);
        PageInfo info = new PageInfo();
        info.setLimit(pageSize);
        info.setOffset(offset);
        info.setPageCount(pageCount);
        info.setTotalCount(totalCount);
        info.setData(data);
        return info;
    }

    public PageInfo EvaluationDetail(Integer pageSize, Integer offset, RowMapper<T> rowMapper, List<SortDTO> sort, PaginationSQL paginationSQL) throws DlEduException {
        if (pageSize == null || pageSize <= 0)
            pageSize = PaginationUtil.DEFAULT_LIMIT;
        if (offset == null || offset <= 0)
            offset = PaginationUtil.DEFAULT_OFFSET;
        if (paginationSQL.getFindSql().toLowerCase().contains("limit")
                || paginationSQL.getFindSql().contains("order by"))
            throw new DlEduException("this sql cannot contains limit or order by statement when query for pagination {"
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
        log.info("find page = {} " + sql);
        List<T> data = jdbcTemplate.query(sql, rowMapper);
        log.info("data size : {} ", data.size());

        int pageCount = totalCount == 0 ? 1 : (int) Math.ceil((double) totalCount / (double) pageSize);
        PageInfo info = new PageInfo();
        info.setLimit(pageSize);
        info.setOffset(offset);
        info.setPageCount(pageCount);
        info.setTotalCount(totalCount);
        info.setData(data);
        return info;
    }

    @SuppressWarnings("rawtypes")
    public PageInfo getEvaluationCount(Integer pageSize, Integer offset, RowMapper<T> rowMapper, List<SortDTO> sort,
                                       PaginationSQL paginationSQL) throws DlEduException {
        if (pageSize == null || pageSize <= 0)
            pageSize = PaginationUtil.DEFAULT_LIMIT;
        if (offset == null || offset <= 0)
            offset = PaginationUtil.DEFAULT_OFFSET;
        if (paginationSQL.getFindSql().toLowerCase().contains("limit")
                || paginationSQL.getFindSql().contains("order by"))
            throw new DlEduException("this sql cannot contains limit or order by statement when query for pagination {"
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
        log.info("find page = {} " + sql);
        List<T> data = jdbcTemplate.query(sql, rowMapper);
        log.info("data size : {} ", data.size());

        int pageCount = totalCount == 0 ? 1 : (int) Math.ceil((double) totalCount / (double) pageSize);
        PageInfo info = new PageInfo();
        info.setLimit(pageSize);
        info.setOffset(offset);
        info.setPageCount(pageCount);
        info.setTotalCount(totalCount);
        info.setData(data);
        return info;
    }

}