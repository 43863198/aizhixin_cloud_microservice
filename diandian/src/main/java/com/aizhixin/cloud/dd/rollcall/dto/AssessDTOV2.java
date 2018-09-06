package com.aizhixin.cloud.dd.rollcall.dto;

import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "评教信息")
@Data
public class AssessDTOV2 {
	@ApiModelProperty(value="主键",required=false)
	private Long id;
	@ApiModelProperty(value="学生id",required=false)
	private Long studentId;
	@ApiModelProperty(value="教师id",required=false)
	private Long teacherId;
	@ApiModelProperty(value="第几节课id",required=false)
	private Long scheduleId;
	@ApiModelProperty(value="课程id",required=false)
	private Long courseId;
	@ApiModelProperty(value="学期id",required=false)
	private Long semesterId;
	@ApiModelProperty(value="评教内容",required=false)
	private String content;
	@ApiModelProperty(value="评分",required=false)
	private Integer score;
	@ApiModelProperty(value="行政班id",required=false)
	private Long classesId;
	@ApiModelProperty(value="学生姓名",required=false)
	private String stuName;
	@ApiModelProperty(value="教师姓名",required=false)
	private String teacherName;
	@ApiModelProperty(value="是否匿名评价",required=false)
	private boolean anonymity;
	@ApiModelProperty(value="创建时间",required=false)
	private Date createDate;
	@ApiModelProperty(value="评论模块,pj:评教，xyq:校友圈，swzl:失物招领,dian: dian一下",required=false)
	private String module;
	@ApiModelProperty(value="评论回复数量",required=false)
	private Integer revertTotal;
	@ApiModelProperty(value="评论来源id",required=false)
    private Long sourseId;
	@ApiModelProperty(value="评论图片",required=false)
	private List<AssessFileDTO> files;
}
