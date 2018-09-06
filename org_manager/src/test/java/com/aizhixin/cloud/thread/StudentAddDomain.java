package com.aizhixin.cloud.thread;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by zhen.pan on 2017/12/15.
 */
@ApiModel(description = "用户信息")
@Data
public class StudentAddDomain {
    @ApiModelProperty(value = "姓名", position = 4)
    private String name;
    @ApiModelProperty(value = "姓名电话", position = 5)
    private String phone;
    @ApiModelProperty(value = "工号", position = 7)
    private String jobNumber;
    @ApiModelProperty(value = "性别(男性male|女性female)", position = 8)
    private String sex;
    @ApiModelProperty(value = "班级ID", position = 9)
    private Long classesId;
    @ApiModelProperty(value = "学校ID", position = 15)
    private Long orgId = 318L;
    @ApiModelProperty(value = "在校10、毕业20")
    private Integer schoolStatus = 10;
    @ApiModelProperty(value = "学生学年")
    private String teachingYear;

    @ApiModelProperty(value = "操作用户ID", position = 13)
    private Long userId=123456L;
}
