package com.aizhixin.cloud.dd.common.domain;

import com.aizhixin.cloud.dd.rollcall.dto.AttendanceRecordDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页对象
 * Created by zhen.pan on 2017/6/29.
 */
@ApiModel(description = "分页数据对象")
@ToString
public class PageData<T> {
    @ApiModelProperty(value = "分页数据")
    @Getter
    @Setter
    private PageDomain page = new PageDomain();
    @ApiModelProperty(value = "数据内容")
    @Getter
    @Setter
    private List<T> data = new ArrayList<>();

    public static PageData getPageData(Page page) {
        if (null == page) {
            throw new NullPointerException();
        }
        PageData pageData = new PageData();
        pageData.setData(page.getContent());
        pageData.getPage().setPageNumber(page.getNumber());
        pageData.getPage().setPageSize(page.getSize());
        pageData.getPage().setTotalElements(page.getTotalElements());
        pageData.getPage().setTotalPages(page.getTotalPages());
        return pageData;
    }

    public static PageData getPageData(Pageable pageable) {
        if (null == pageable) {
            throw new NullPointerException();
        }
        PageData pageData = new PageData();
        pageData.getPage().setPageNumber(pageable.getPageNumber() + 1);
        pageData.getPage().setPageSize(pageable.getPageSize());
        return pageData;
    }

    public static void setPageData(PageData pageData, Long totalElements, Integer totalPages) {
        if (null == pageData) {
            throw new NullPointerException();
        }
        pageData.getPage().setTotalElements(totalElements);
        pageData.getPage().setTotalPages(totalPages);
    }


}