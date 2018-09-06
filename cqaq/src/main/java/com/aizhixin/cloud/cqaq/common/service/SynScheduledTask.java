package com.aizhixin.cloud.cqaq.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.aizhixin.cloud.cqaq.common.tools.CompressedFileUtil;
import com.aizhixin.cloud.cqaq.syn.service.SynAllDataService;
import com.aizhixin.cloud.cqaq.syn.service.SynUpdateSqlService;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据同步任务调度
 */
@Component
@Slf4j
public class SynScheduledTask {
	@Autowired
	private ExcelExportService excelExportService;
	@Autowired
	private SynAllDataService synAllDataService;
	@Autowired
	private CompressedFileUtil compressedFileUtil;
	@Autowired
	private SynUpdateSqlService synUpdateSqlService;

	/**
	 * 数据同步SQL学年、第一学期
	 */
	@Scheduled(cron = "0 0 0 1 8 ?")
	public void updateSql1() {
		log.info("Start update syn_xq1.");
		synUpdateSqlService.syn_xq1();
		log.info("End update syn_xq1.");
	}

	/**
	 * 数据同步SQL第二学期
	 */
	@Scheduled(cron = "0 0 0 15 2 ?")
	public void updateSql2() {
		log.info("Start update syn_xq2.");
		synUpdateSqlService.syn_xq2();
		log.info("End update syn_xq2.");
	}

	/**
	 * 数据同步比较任务
	 */
	@Scheduled(cron = "0 15 22 * * ?")
	public void executeSynDataTask() {
		log.info("Start syn data task.");
		synAllDataService.syn();
		log.info("End syn data task.");
	}

	/**
	 * Excel生成任务
	 */
	@Scheduled(cron = "0 25 22 * * ?")
	public void executeExcelCreateTask() {
		log.info("Start create excel task.");
		excelExportService.outAllNewDataToExcel();
		excelExportService.outAllUpdateDataToExcel();
		log.info("End create excel task.");
	}

	/**
	 * 生成压缩文件
	 */
	@Scheduled(cron = "0 35 22 * * ?")
	public void zip() {
		log.info("Start create zip task.");
		compressedFileUtil.zip();
		log.info("End create zip task.");
	}
}
