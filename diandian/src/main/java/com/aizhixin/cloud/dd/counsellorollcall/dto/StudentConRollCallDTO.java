package com.aizhixin.cloud.dd.counsellorollcall.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author LIMH
 */
@Data
@ApiModel(description = "学生信息")
public class StudentConRollCallDTO {
    @ApiModelProperty(value = "学生ID")
    private Long id;
    @ApiModelProperty(value = "学生名称")
    private String name;
    @ApiModelProperty(value = "学生学号")
    private String job;
    @ApiModelProperty(value = "班级ID")
    private Long cid;
    @ApiModelProperty(value = "班级名称")
    private String cname;
    @ApiModelProperty(value = "选择")
    private Boolean isChecked;

    public StudentConRollCallDTO(Long id, String name, String job, Long cid, String cname, Boolean isChecked) {
        this.id = id;
        this.name = name;
        this.job = job;
        this.cid = cid;
        this.cname = cname;
        this.isChecked = isChecked;
    }
}
