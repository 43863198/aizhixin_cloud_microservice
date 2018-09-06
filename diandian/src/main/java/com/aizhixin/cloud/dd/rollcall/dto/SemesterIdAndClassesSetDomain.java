package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Set;

/**
 * Created by LIMH on 2017/10/16.
 */
@ApiModel(description = "根据行政班ID列表和学期查询教学班ID,name")
@Data
public class SemesterIdAndClassesSetDomain {
    Set classesIdSet;
    Long semesterId;


    public SemesterIdAndClassesSetDomain(Set classesIdSet, Long semesterId) {
        this.classesIdSet = classesIdSet;
        this.semesterId = semesterId;
    }
}
