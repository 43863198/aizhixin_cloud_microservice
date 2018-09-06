/**
 *
 */
package com.aizhixin.cloud.paycallback.common.core;

import org.springframework.data.domain.PageRequest;

/**
 * 分页相关辅助工具类
 *
 * @author zhen.pan
 */
public class PageUtil {

    /**
     * 初始化分页为一个不引起后台报错的合适的数值
     *
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public static PageRequest createNoErrorPageRequest(final Integer pageNumber, final Integer pageSize) {
        int pn = 0;
        int ps = 20;
        if (null != pageNumber && pageNumber - 1 >= 0) {
            pn = pageNumber - 1;
        }
        if (null != pageSize && pageSize > 0) {
            ps = pageSize;
        }
        return new PageRequest(pn, ps);
    }

    /**
     * 计算总页数
     *
     * @param count
     * @param pagesize
     * @return
     */
    public static int cacalatePagesize(long count, int pagesize) {
        return (int) (count / pagesize + (count % pagesize == 0 ? 0 : 1));
    }
}
