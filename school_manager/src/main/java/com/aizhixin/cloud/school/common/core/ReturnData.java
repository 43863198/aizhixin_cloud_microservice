package com.aizhixin.cloud.school.common.core;

import com.aizhixin.cloud.school.common.PageDomain;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ReturnData<T> {
    private List<T>  dataList=new ArrayList<>();
    private PageDomain pageDomain;
}
