package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * Created by Administrator on 2017/6/17.
 */
@ApiModel(description = "个人中心 我的考勤")
public class RollCallOfClassDTO {
    private Long classId;

    private Long allCount;

    private String className;

    private Long nCount;

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getAllCount() {
        return allCount;
    }

    public void setAllCount(Long allCount) {
        this.allCount = allCount;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Long getnCount() {
        return nCount;
    }

    public void setnCount(Long nCount) {
        this.nCount = nCount;
    }

}
