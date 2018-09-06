package com.aizhixin.cloud.dd.statistics.dto;

import lombok.Data;

/**
 * Created by LIMH on 2017/8/22.
 */
@Data
public class RollCallStatisticsDTO {
    Long teacherId;
    String teacherName;
    int signCount;
    int totalCount;
    String rollCallRate;
}
