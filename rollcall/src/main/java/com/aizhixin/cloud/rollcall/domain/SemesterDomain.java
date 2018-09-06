package com.aizhixin.cloud.rollcall.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;


@ApiModel(description="id和name信息")
@ToString
@NoArgsConstructor
public class SemesterDomain {
    @ApiModelProperty(value = "ID", position=1)
    @Getter @Setter private Long id;

    @NotNull(message ="名称不能为空")
    @ApiModelProperty(value = "名称", position=2)
    @Getter @Setter private String name;

    @ApiModelProperty(value = "学期编码")
    @Getter @Setter private String code;

    @ApiModelProperty(value = "学年ID")
    @Getter @Setter private String yearId;

    @ApiModelProperty(value = "学年名称")
    @Getter @Setter private String yearName;

    @ApiModelProperty(value = "起始时间 ")
    @NotNull(message ="起始时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Getter @Setter private Date startDate;

    @ApiModelProperty(value = "终止时间 ")
    @NotNull(message ="结束时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Getter @Setter private Date endDate;

    @ApiModelProperty(value = "周数量 ")
    @Getter @Setter private  Integer numWeek;
}
