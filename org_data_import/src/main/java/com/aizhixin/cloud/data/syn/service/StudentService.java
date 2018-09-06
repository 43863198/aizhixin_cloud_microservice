package com.aizhixin.cloud.data.syn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aizhixin.cloud.data.syn.manager.BaseDataManager;

@Component("studentService")
public class StudentService extends BaseDataService {

	@Autowired
	@Qualifier("studentManager")
	private BaseDataManager baseDataManager;

	/**
	 * 比较基线版本数据
	 */
	public void compactBaseLineData() {
		compactBaseLineData(baseDataManager);
	}
}