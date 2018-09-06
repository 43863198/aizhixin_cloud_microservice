package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by Administrator on 2017/6/15.
 */
@Data
@ApiModel(description = "辅导员查下看班级考勤")
@EqualsAndHashCode(callSuper = false)
public class CounselorClassesAddendanceDTO {
    Long classId;
    String className;
    Integer normalCount;
    Integer totalCount;
    String classRate;
    Long semesterId;

}
