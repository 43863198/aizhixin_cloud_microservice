package com.aizhixin.cloud.dd.statistics.dto;

import lombok.Data;

/**
 * Created by LIMH on 2017/8/22.
 */
@Data
public class AssessStatisticsDTO {
    Long teacherId;
    String teacherName;
    Long totalScore;
    Long goodScore;
    Integer goodCount;
    Integer assessCount;

    String assesRate;
}
