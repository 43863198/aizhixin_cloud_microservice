package com.aizhixin.cloud.data.syn.manager;

import com.aizhixin.cloud.data.common.manager.FileOperator;
import com.aizhixin.cloud.data.common.manager.JsonUtil;
import com.aizhixin.cloud.data.syn.dto.BaseDTO;
import com.aizhixin.cloud.data.syn.dto.TeacherDTO;
import com.aizhixin.cloud.data.syn.repository.DatabaseJdbcDataRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("teacherManager")
public class TeacherManager implements BaseDataManager {
	private final static String KEY = "ZGH";// 数据库字段名称，老师工号
	private final static String NAME = "XM";// 数据库字段名称，老师姓名
	private final static String COLLEGE_NAME = "BM";// 数据库字段名称，院系所名称
	private final static String FILE_NAME = "teacher.json";// 输出文件名称
	@Autowired
	private DatabaseJdbcDataRepository repository;
	@Autowired
	private FileOperator fileOperator;

	@Override
	public List<BaseDTO> findAll() {
		List<Map<String, Object>> ds = repository.findTeacher();
		List<BaseDTO> list = new ArrayList<>();
		for (Map<String, Object> m : ds) {
			TeacherDTO d = new TeacherDTO();
			if (StringUtils.isEmpty(m.get(KEY))) {
				log.warn("Teacher code is null");
				continue;
			}

			d.setKey(m.get(KEY).toString());

			if (!StringUtils.isEmpty(m.get(NAME))) {
				d.setName(m.get(NAME).toString());
			} else {
				log.warn("Teacher name is null");
			}

			if (!StringUtils.isEmpty(m.get(COLLEGE_NAME))) {
				d.setCollegeName(m.get(COLLEGE_NAME).toString());
			} else {
				if (!StringUtils.isEmpty(m.get(KEY))) {
					log.warn("Teacher college name is null.  Teacher code : " + m.get(KEY).toString());
				}
			}
			list.add(d);
		}
		return list;
	}

	@Override
	public void writeDataToDBFile(List<BaseDTO> allNowLines) {
		fileOperator.outDBDir(allNowLines, FILE_NAME, "teacher");
	}

	@Override
	public List<BaseDTO> readYesterdayData() {
		File f = fileOperator.getYesterdayDBFile(FILE_NAME);
		List<BaseDTO> rs = new ArrayList<>();
		if (f.exists() && f.isFile()) {
			List<TeacherDTO> list = JsonUtil.decode(f, new TypeReference<List<TeacherDTO>>() {
			});
			rs.addAll(list);
		} else {
			log.warn("Read teacher yesterday file ({}) not exists.", f.toString());
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
	public List<TeacherDTO> getCurrentAllNewData() {
		File f = fileOperator.getCurrentdayDBFile(FILE_NAME);
		List<TeacherDTO> rs = new ArrayList<>();
		if (f.exists() && f.isFile()) {
			List<TeacherDTO> list = JsonUtil.decode(f, new TypeReference<List<TeacherDTO>>() {
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
	public List<TeacherDTO> getCurrentUpdateData() {
		List<TeacherDTO> rs = new ArrayList<>();
		File addFile = fileOperator.getCurrentdayAddFile(FILE_NAME);
		if (addFile.exists() && addFile.isFile()) {
			List<TeacherDTO> list = JsonUtil.decode(addFile, new TypeReference<List<TeacherDTO>>() {
			});
			rs.addAll(list);
		}
		File updateFile = fileOperator.getCurrentdayUpdateFile(FILE_NAME);
		if (updateFile.exists() && updateFile.isFile()) {
			List<TeacherDTO> list = JsonUtil.decode(updateFile, new TypeReference<List<TeacherDTO>>() {
			});
			rs.addAll(list);
		}
		return rs;
	}
}
