package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * Created by Administrator on 2017/6/15.
 */

@Data
public class AssessClassesAvgDTO {
    private Long scheduleId;
    private Long classId;
    private Long count;
    private Long sumScore;

    public AssessClassesAvgDTO() {
    }

    public AssessClassesAvgDTO(Long scheduleId, Long classId, Long count, Long sumScore) {
        this.scheduleId = scheduleId;
        this.classId = classId;
        this.count = count;
        this.sumScore = sumScore;
    }
}
