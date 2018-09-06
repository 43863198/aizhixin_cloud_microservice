package com.aizhixin.cloud.rollcall.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@ApiModel(description="课程节信息")
@NoArgsConstructor
@ToString
public class PeriodDomain {
    @ApiModelProperty(value = "ID", position=1)
    @Getter @Setter private Long id;

    @ApiModelProperty(value = "起始时间(格式为:HH:mm)")
    @Getter @Setter private String startTime;

    @ApiModelProperty(value = "终止时间(格式为:HH:mm)")
    @Getter @Setter private String endTime;

    @ApiModelProperty(value = "第几节")
    @Getter @Setter private  Integer no;

    @ApiModelProperty(value = "创建日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Getter @Setter private Date createdDate;

    @ApiModelProperty(value = "操作用户id")
    @Getter @Setter private Long userId;

    @ApiModelProperty(value = "课程节名称")
    @Getter @Setter private String name;

    @ApiModelProperty(value = "学校ID", position=15)
    @Getter @Setter private Long orgId;
}
