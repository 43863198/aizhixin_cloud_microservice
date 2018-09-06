package com.aizhixin.cloud.thread;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by zhen.pan on 2017/12/15.
 */
@ApiModel(description="用户信息")
@Data
public class TeacherAddDomain {
    @ApiModelProperty(value = "姓名", position=4)
    private String name;
    @ApiModelProperty(value = "工号", position=7)
    private String jobNumber;
    @ApiModelProperty(value = "学院ID", position=13)
    private Long collegeId;
    @ApiModelProperty(value = "操作用户ID", position=13)
    private Long userId = 123456L;
}
