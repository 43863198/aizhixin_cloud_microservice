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

@ApiModel(description="学校整体调课信息")
@ToString
@NoArgsConstructor
public class SchoolUnifyAdjustCourseDomain {
    @ApiModelProperty(value = "ID")
    @Getter @Setter private Long id;
    @ApiModelProperty(value = "学校")
    @Getter @Setter private Long orgId;
    @ApiModelProperty(value = "学期")
    @Getter @Setter private Long semesterId;
    @ApiModelProperty(value = "学期")
    @Getter @Setter private String semesterName;
    @ApiModelProperty(value = "整体调课说明")
    @Getter @Setter private String name;
    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Getter @Setter private Date srcDate = new Date();
    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Getter @Setter private Date destDate = new Date();
    @ApiModelProperty(value = "操作人")
    @Getter @Setter private Long userId;

    public SchoolUnifyAdjustCourseDomain (Long id, String name, String srcDate, String destDate, Long semesterId, String semesterName) {
        this.id = id;
        this.name = name;
        if (null != srcDate)
            this.srcDate = DateUtil.parse(srcDate);
        if (null != destDate) {
            this.destDate = DateUtil.parse(destDate);
        }
        this.semesterId = semesterId;
        this.semesterName = semesterName;
    }
}
