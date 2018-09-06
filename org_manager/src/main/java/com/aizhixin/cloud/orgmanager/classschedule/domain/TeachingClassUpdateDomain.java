package com.aizhixin.cloud.orgmanager.classschedule.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by zhen.pan on 2017/4/25.
 */
@ApiModel(description="教学班修改对象")
@Data
public class TeachingClassUpdateDomain {
    @ApiModelProperty(value = "ID", required = true, allowableValues = "range[1,infinity]", position=1)
    @Getter @Setter private Long id;
    @NotNull(message ="名称不能为空")
    @ApiModelProperty(value = "名称", required = true, position=2)
    @Size(min = 1, max = 140)
    @Getter @Setter private String name;

    @ApiModelProperty(value = "教学班编码(选课课号)", position=3)
    @Getter @Setter private String code;
    @ApiModelProperty(value = "学期ID", required = true, allowableValues = "range[1,infinity]", position=4)
    @Getter @Setter private Long semesterId;

    @ApiModelProperty(value = "课程ID", required = true, allowableValues = "range[1,infinity]", position=5)
    @Getter @Setter private Long courseId;
    @ApiModelProperty(value = "操作用户ID", required = true)
    @Getter @Setter private Long userId;
}
