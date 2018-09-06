package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * Created by DuanWei on 2017/6/30.
 */
@ApiModel(description = "教学班学周趋势")
@Data
public class WeekTendencyDto {
    private String week;
    private Long participationCount;
    private Long practical;
    private String proportion;
    private Long semesterId;
    private String semesterName;
}
