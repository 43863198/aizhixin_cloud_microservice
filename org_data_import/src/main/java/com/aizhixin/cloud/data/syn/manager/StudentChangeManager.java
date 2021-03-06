package com.aizhixin.cloud.data.syn.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.data.common.manager.FileOperator;
import com.aizhixin.cloud.data.common.manager.JsonUtil;
import com.aizhixin.cloud.data.syn.dto.BaseDTO;
import com.aizhixin.cloud.data.syn.dto.StudentChangeDTO;
import com.aizhixin.cloud.data.syn.repository.DatabaseJdbcDataRepository;
import com.fasterxml.jackson.core.type.TypeReference;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("studentChangeManager")
public class StudentChangeManager implements BaseDataManager {
	private final static String KEY = "XH";// 数据库字段名称，学生学号
	private final static String LBMC = "YDLB";// 数据库字段名称，异动类别
	private final static String YY = "YDYY";// 数据库字段名称，异动原因
	private final static String FILE_NAME = "studentChange.json";// 输出文件名称
	@Autowired
	private DatabaseJdbcDataRepository repository;
	@Autowired
	private FileOperator fileOperator;

	@Override
	public List<BaseDTO> findAll() {
		List<Map<String, Object>> sc = repository.findStudentChange();
		List<BaseDTO> list = new ArrayList<>();
		for (Map<String, Object> m : sc) {
			StudentChangeDTO d = new StudentChangeDTO();
			if (StringUtils.isEmpty(m.get(KEY))) {
				log.warn("Student xh is null");
				continue;
			}

			d.setKey(m.get(KEY).toString());

			if (!StringUtils.isEmpty(m.get(LBMC))) {
				d.setLbmc(m.get(LBMC).toString());
			} else {
				log.warn("Student lb is null");
			}

			if (!StringUtils.isEmpty(m.get(YY))) {
				d.setYy(m.get(YY).toString());
			}

			list.add(d);
		}
		return list;
	}

	@Override
	public void writeDataToDBFile(List<BaseDTO> allNowLines) {
		fileOperator.outDBDir(allNowLines, FILE_NAME, "studentChange");
	}

	@Override
	public List<BaseDTO> readYesterdayData() {
		File f = fileOperator.getYesterdayDBFile(FILE_NAME);
		List<BaseDTO> rs = new ArrayList<>();
		if (f.exists() && f.isFile()) {
			List<StudentChangeDTO> list = JsonUtil.decode(f, new TypeReference<List<StudentChangeDTO>>() {
			});
			rs.addAll(list);
		} else {
			log.warn("Read studentChange yesterday file ({}) not exists.", f.toString());
		}
		return rs;
	}

	@Override
	public void writeDataToOut(List<BaseDTO> addList, List<BaseDTO> upadateList, List<BaseDTO> delList) {
		fileOperator.outOutCompactDir(addList, upadateList, delList, FILE_NAME);
	}

	/**
	 * 读当天的所有最新数据
	 * 
	 * @return 所有最新数据
	 */
	public List<StudentChangeDTO> getCurrentAllNewData() {
		File f = fileOperator.getCurrentdayDBFile(FILE_NAME);
		List<StudentChangeDTO> rs = new ArrayList<>();
		if (f.exists() && f.isFile()) {
			List<StudentChangeDTO> list = JsonUtil.decode(f, new TypeReference<List<StudentChangeDTO>>() {
			});
			rs.addAll(list);
		}
		return rs;
	}

	/**
	 * 读当天的添加和修改的数据(删除数据需要单独处理)
	 * 
	 * @return 当天的添加和修改的数据
	 */
	public List<StudentChangeDTO> getCurrentUpdateData() {
		List<StudentChangeDTO> rs = new ArrayList<>();
		File addFile = fileOperator.getCurrentdayAddFile(FILE_NAME);
		if (addFile.exists() && addFile.isFile()) {
			List<StudentChangeDTO> list = JsonUtil.decode(addFile, new TypeReference<List<StudentChangeDTO>>() {
			});
			rs.addAll(list);
		}
		File updateFile = fileOperator.getCurrentdayUpdateFile(FILE_NAME);
		if (updateFile.exists() && updateFile.isFile()) {
			List<StudentChangeDTO> list = JsonUtil.decode(updateFile, new TypeReference<List<StudentChangeDTO>>() {
			});
			rs.addAll(list);
		}
		return rs;
	}

}
