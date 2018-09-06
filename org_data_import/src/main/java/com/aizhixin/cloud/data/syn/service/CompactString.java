package com.aizhixin.cloud.data.syn.service;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CompactString {

	public void compact(List<String> allNowLines, Map<String, String> cacheNowMap, List<String> yestodayLines,
			Map<String, String> cacheYestodayMap, List<String> addList, List<String> updateList, List<String> delList) {
		if (yestodayLines.size() > 0) {
			String line = null;
			for (Map.Entry<String, String> e : cacheYestodayMap.entrySet()) {
				line = cacheNowMap.get(e.getKey());
				if (null == line) {// 上次有，这次没有
					delList.add(e.getValue());
				} else {// 上次有，本次有
					if (!line.equals(e.getValue())) {// 上次和本次不一样
						updateList.add(line);
					} // else 没有变化的
				}
			}
			for (Map.Entry<String, String> e : cacheNowMap.entrySet()) {
				line = cacheYestodayMap.get(e.getKey());
				if (null == line) {// 上次没有,这次有
					addList.add(e.getValue());
				}
			}
		} else {
			addList.addAll(allNowLines);// 全部是新增的
		}
	}
}
