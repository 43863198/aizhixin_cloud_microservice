package com.aizhixin.cloud.dd.rollcall.utils;

import java.util.ArrayList;
import java.util.List;

import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.aizhixin.cloud.dd.rollcall.dto.PageInfo;
import com.aizhixin.cloud.dd.rollcall.dto.SortDTO;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Utility class for handling pagination.
 * <p>
 * <p>
 * Pagination uses the same principles as the <a href="https://developer.github.com/v3/#pagination">Github API</api>,
 * and follow <a href="http://tools.ietf.org/html/rfc5988">RFC 5988 (Link header)</a>.
 * </p>
 */
public class PaginationUtil {

    public static final int DEFAULT_OFFSET = 1;

    public static final int MIN_OFFSET = 1;

    public static final int DEFAULT_LIMIT = 20;

    public static final int MAX_LIMIT = 100;

    public static Pageable generatePageRequestBySort(Integer offset, Integer limit) {
        return generatePageRequestBySort(offset, limit, new Sort(Direction.DESC, "lastModifiedDate"));
    }

    public static Pageable generatePageRequestBySort(Integer offset, Integer limit, Sort sort) {
        if (offset == null || offset < MIN_OFFSET) {
            offset = DEFAULT_OFFSET;
        }
        if (limit == null || limit > MAX_LIMIT) {
            limit = DEFAULT_LIMIT;
        }
        return new PageRequest(offset - 1, limit, sort);
    }

    public static Pageable generatePageRequest(Integer offset, Integer limit) {
        if (offset == null || offset < MIN_OFFSET) {
            offset = DEFAULT_OFFSET;
        }
        if (limit == null || limit > MAX_LIMIT) {
            limit = DEFAULT_LIMIT;
        }
        return new PageRequest(offset - 1, limit);
    }

    public static Pageable generatePageRequest(Integer offset, Integer limit, String sort) {
        return generatePageRequest(offset, limit, SortUtil.convert2SortDTO(sort));
    }

    public static Pageable generatePageRequest(Integer offset, Integer limit, List<SortDTO> sorts) {
        if (offset == null || offset < MIN_OFFSET) {
            offset = DEFAULT_OFFSET;
        }
        if (limit == null || limit > MAX_LIMIT) {
            limit = DEFAULT_LIMIT;
        }
        List<org.springframework.data.domain.Sort.Order> orders = new ArrayList<org.springframework.data.domain.Sort.Order>();
        if (sorts != null) {
            for (int i = 0; i < sorts.size(); i++) {
                if (sorts.get(i).getAsc())
                    orders.add(new org.springframework.data.domain.Sort.Order(Direction.ASC, sorts.get(i).getKey()));
                else
                    orders.add(new org.springframework.data.domain.Sort.Order(Direction.DESC, sorts.get(i).getKey()));
            }
        }
        if (!orders.isEmpty()) {
            Sort sort = new Sort(orders);
            return new PageRequest(offset - 1, limit, sort);
        } else
            return new PageRequest(offset - 1, limit);
    }

    public static Pageable generatePageRequest(Integer offset, Integer limit, String[] asc, String[] desc) {
        if (offset == null || offset < MIN_OFFSET) {
            offset = DEFAULT_OFFSET;
        }
        if (limit == null || limit > MAX_LIMIT) {
            limit = DEFAULT_LIMIT;
        }
        List<org.springframework.data.domain.Sort.Order> orders = new ArrayList<org.springframework.data.domain.Sort.Order>();
        if (asc != null) {
            for (String s : asc) {
                orders.add(new org.springframework.data.domain.Sort.Order(Direction.ASC, s));
            }
        }
        if (desc != null) {
            for (String s : desc) {
                orders.add(new org.springframework.data.domain.Sort.Order(Direction.DESC, s));
            }
        }
        if (!orders.isEmpty()) {
            Sort sort = new Sort(orders);
            return new PageRequest(offset - 1, limit, sort);
        } else
            return new PageRequest(offset - 1, limit);
    }


    @SuppressWarnings("rawtypes")
    public static PageInfo generatePagination(List data, Integer offset, Integer limit, Integer totelPages, Long totelElements) {
        if (offset == null || offset < MIN_OFFSET) {
            offset = DEFAULT_OFFSET;
        }
        if (limit == null || limit > MAX_LIMIT) {
            limit = DEFAULT_LIMIT;
        }

        PageInfo pageInfo = new PageInfo();
        pageInfo.setData(data);
        pageInfo.setLimit(limit);
        pageInfo.setOffset(offset);
        pageInfo.setPageCount(totelPages);
        pageInfo.setTotalCount(totelElements);
        return pageInfo;
    }

    public static String page(JdbcTemplate jdbcTemplate, String sql, Integer offset, Integer limit) {
        if (null == offset) {
            return "";
        }
        if (offset == 0) {
            offset = 1;
        }
        if (null == limit) {
            limit = 20;
        }
        String limitSql = " limit ";
        sql = "select count(*) from (" + sql + ") sc";
        Long totalCount = jdbcTemplate.queryForObject(sql, Long.class);
        int currentIndex = (offset - 1) * limit;
        if (currentIndex >= totalCount.intValue()) {
            return null;
        } else {
            int temp = totalCount.intValue() - currentIndex;
            limitSql = limitSql + (offset - 1) * limit + " , " + (temp < limit.intValue() ? temp : limit.intValue());
        }
        return limitSql;
    }

    public static String page(JdbcTemplate jdbcTemplate, String sql, Integer offset, Integer limit, PageData pageData) {

        if(null==pageData){
            return "";
        }

        if (null == offset) {
            offset = 1;
        }
        if (offset.intValue() == 0) {
            offset = 1;
        }
        if (null == limit) {
            limit = 20;
        }

        String limitSql = " limit ";
        sql = "select count(*) from (" + sql + ") sc";
        Long totalCount = jdbcTemplate.queryForObject(sql, Long.class);
        int currentIndex = (offset - 1) * limit;
        if (currentIndex >= totalCount.intValue()) {
            return null;
        } else {
            int temp = totalCount.intValue() - currentIndex;
            limitSql = limitSql + (offset - 1) * limit + " , " + (temp < limit.intValue() ? temp : limit.intValue());
        }

        pageData.setData(null);
        pageData.setPage(new PageDomain(totalCount, ((totalCount % limit == 0) ? (Integer.valueOf(totalCount / limit + "")) : (Integer.valueOf((totalCount / limit + 1) + ""))), offset, limit));
        return limitSql;
    }
}
