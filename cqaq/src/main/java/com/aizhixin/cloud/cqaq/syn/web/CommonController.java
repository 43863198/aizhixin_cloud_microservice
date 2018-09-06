package com.aizhixin.cloud.cqaq.syn.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.cqaq.common.service.ExcelExportService;
import com.aizhixin.cloud.cqaq.common.tools.CompressedFileUtil;
import com.aizhixin.cloud.cqaq.syn.service.SynAllDataService;
import com.aizhixin.cloud.cqaq.syn.service.SynUpdateSqlService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/manual")
@Api(description = "手动测试数据同步")
public class CommonController {

	@Autowired
	private SynAllDataService synAllDataService;
	@Autowired
	private ExcelExportService excelExportService;
	@Autowired
	private CompressedFileUtil compressedFileUtil;
	@Autowired
	private SynUpdateSqlService synUpdateSqlService;

	@GetMapping(value = "/synalldata")
	@ApiOperation(httpMethod = "GET", value = "手动触发所有基础数据同步", notes = "手动触发所有基础数据同步<br><br><b>@author zhen.pan</b>")
	public void synAllData() {
		synAllDataService.syn();
	}

	@GetMapping(value = "/outexcel")
	@ApiOperation(httpMethod = "GET", value = "手动触发Excel文件的生成", notes = "手动触发Excel文件的生成<br><br><b>@author zhen.pan</b>")
	public void outExcel() {
		excelExportService.outAllNewDataToExcel();
		excelExportService.outAllUpdateDataToExcel();
	}

	@GetMapping(value = "/zip")
	@ApiOperation(httpMethod = "GET", value = "手动触发压缩文件的生成", notes = "手动触发压缩文件的生成<br><br><b>@author bly</b>")
	public void zip() {
		compressedFileUtil.zip();
	}

	@GetMapping(value = "/updateSql_xq1")
	@ApiOperation(httpMethod = "GET", value = "手动修改sql查询条件（学年、第一学期）", notes = "手动修改sql查询条件（学年、第一学期）<br><br><b>@author bly</b>")
	public void update1() {
		synUpdateSqlService.syn_xq1();
	}

	@GetMapping(value = "/updateSql_xq2")
	@ApiOperation(httpMethod = "GET", value = "手动修改sql查询条件（第二学期）", notes = "手动修改sql查询条件（第二学期）<br><br><b>@author bly</b>")
	public void update2() {
		synUpdateSqlService.syn_xq2();
	}

}
