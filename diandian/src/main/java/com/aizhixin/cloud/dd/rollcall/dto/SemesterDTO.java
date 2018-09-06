package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * Created by LIMH on 2017/8/1.
 */
@ApiModel(description = "学期信息")
@Data
public class SemesterDTO {
    Long id;
    String name;
    String startDate;
    String endDate;
    String numWeek;
}
