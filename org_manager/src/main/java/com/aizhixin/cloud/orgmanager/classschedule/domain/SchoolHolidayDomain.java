package com.aizhixin.cloud.orgmanager.classschedule.domain;


import com.aizhixin.cloud.orgmanager.common.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@ApiModel(description="学校假日信息")
@ToString
@NoArgsConstructor
public class SchoolHolidayDomain {
    @ApiModelProperty(value = "ID")
    @Getter @Setter private Long id;
    @ApiModelProperty(value = "学校")
    @Getter @Setter private Long orgId;
    @ApiModelProperty(value = "学期")
    @Getter @Setter private Long semesterId;
    @ApiModelProperty(value = "学期")
    @Getter @Setter private String semesterName;
    @ApiModelProperty(value = "假日说明")
    @Getter @Setter private String name;
    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Getter @Setter private Date startDate = new Date();
    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Getter @Setter private Date endDate = new Date();
    @ApiModelProperty(value = "操作人")
    @Getter @Setter private Long userId;

    public SchoolHolidayDomain (Long id, String name, String startDate, String endDate, Long semesterId, String semesterName) {
        this.id = id;
        this.name = name;
        if (null != startDate) {
            this.startDate = DateUtil.parse(startDate);
        }
        if (null != endDate) {
            this.endDate = DateUtil.parse(endDate);
        }
        this.semesterId = semesterId;
        this.semesterName = semesterName;
    }
}
