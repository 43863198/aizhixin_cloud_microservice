package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by Administrator on 2017/6/15.
 */
@Data
@ApiModel(description = "教师端 我的评分")
@EqualsAndHashCode(callSuper = false)
public class AssessPhoneTeacherWeekScheduleDTO {
    private Long periodId;
}
