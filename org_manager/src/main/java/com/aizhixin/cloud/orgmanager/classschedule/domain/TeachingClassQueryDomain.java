package com.aizhixin.cloud.orgmanager.classschedule.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

/**
 * Created by zhen.pan on 2017/7/4.
 */
@ApiModel(description="根据学生和课程列表查询老师列表")
@Data
public class TeachingClassQueryDomain {
    @ApiModelProperty(value = "学校ID", required = true)
    private Long orgId;
    @ApiModelProperty(value = "学期ID")
    private Long semesterId;
    @ApiModelProperty(value = "学院IDD")
    private Long collegeId;
    @ApiModelProperty(value = "排除的教学班列表")
    private Set<Long> teachingClassIds;
    @ApiModelProperty(value = "课程名称")
    private String courseName;
    @ApiModelProperty(value = "教师名称")
    private String teacherName;
    @ApiModelProperty(value = "第几页")
    private Integer pageNumber;
    @ApiModelProperty(value = "每页数据的数目")
    private Integer pageSize;
}
