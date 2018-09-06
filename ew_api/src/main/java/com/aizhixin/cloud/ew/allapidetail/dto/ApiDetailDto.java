package com.aizhixin.cloud.ew.allapidetail.dto;

import java.util.List;

import io.swagger.annotations.ApiModel;
import lombok.Data;
@ApiModel(description = "API详情")
@Data
public class ApiDetailDto {
	private String proName; // 项目功能模块名称
	private String depend; // api相关依赖
	private List<String> apiName; // api名称
	private List<String> apiHttpMeth; // api请求方法
	private List<List<Object>> apiParam; 
	private List<List<Object>> pathVariable; 
	private List<List<Object>> requestHeader; 
	private List<List<Object>> requestParam; 
}
