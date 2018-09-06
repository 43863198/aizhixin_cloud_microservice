package com.aizhixin.cloud.cqaq.syn.service;

import com.aizhixin.cloud.cqaq.syn.manager.BaseDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("collegeService")
public class CollegeService extends BaseDataService {

	@Autowired
	@Qualifier("collegeManager")
	private BaseDataManager baseDataManager;

	/**
	 * 比较基线版本数据
	 */
	public void compactBaseLineData() {
		compactBaseLineData(baseDataManager);
	}
}