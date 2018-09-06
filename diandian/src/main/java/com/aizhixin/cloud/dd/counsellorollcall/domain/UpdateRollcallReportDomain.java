package com.aizhixin.cloud.dd.counsellorollcall.domain;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.util.List;
//暂无用
@ApiModel(description = "学生签到列表")
@ToString
@Data
public class UpdateRollcallReportDomain {
    private Long stuId;
    private List<RollcallReportDomain> list;
}
