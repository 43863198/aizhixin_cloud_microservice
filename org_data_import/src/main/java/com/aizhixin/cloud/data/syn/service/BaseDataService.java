package com.aizhixin.cloud.data.syn.service;

import com.aizhixin.cloud.data.syn.dto.BaseDTO;
import com.aizhixin.cloud.data.syn.manager.BaseDataManager;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseDataService {

	public void compactBaseLineData(BaseDataManager baseDataManager) {
		// 读当前数据库的数据,作为相对版本的数据
		List<BaseDTO> dataList = baseDataManager.findAll();
		// 输出当天的数据(覆盖式的)
		if (!dataList.isEmpty()) {
			baseDataManager.writeDataToDBFile(dataList);
		}
		// 读昨天的数据，作为基线版本
		List<BaseDTO> baseList = baseDataManager.readYesterdayData();
		// 比较
		List<BaseDTO> addList = new ArrayList<>();// 新增的数据
		List<BaseDTO> updateList = new ArrayList<>();// 修改后的数据
		List<BaseDTO> delList = new ArrayList<>();// 删除的数据

		if (baseList.isEmpty()) {// 基线版本数据不存在，全部添加
			addList.addAll(dataList);
		} else {
			DataCompactBase.compact(baseList, dataList, addList, updateList, delList);
		}
		// 输出新增、修改、删除的数据
		baseDataManager.writeDataToOut(addList, updateList, delList);
	}
}
