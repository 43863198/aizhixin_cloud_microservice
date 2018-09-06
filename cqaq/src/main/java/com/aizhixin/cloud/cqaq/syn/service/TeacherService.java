package com.aizhixin.cloud.cqaq.syn.service;

import com.aizhixin.cloud.cqaq.syn.manager.BaseDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("teacherService")
public class TeacherService extends BaseDataService {

	@Autowired
	@Qualifier("teacherManager")
	private BaseDataManager baseDataManager;

	/**
	 * 比较基线版本数据
	 */
	public void compactBaseLineData() {
		compactBaseLineData(baseDataManager);
	}
}