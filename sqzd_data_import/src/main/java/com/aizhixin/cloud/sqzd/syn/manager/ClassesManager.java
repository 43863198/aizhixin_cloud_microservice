package com.aizhixin.cloud.sqzd.syn.manager;


import com.aizhixin.cloud.sqzd.common.manager.FileOperator;
import com.aizhixin.cloud.sqzd.common.manager.JsonUtil;
import com.aizhixin.cloud.sqzd.syn.dto.BaseDTO;
import com.aizhixin.cloud.sqzd.syn.dto.ClassesDTO;
import com.aizhixin.cloud.sqzd.syn.repository.ChongqingJdbcRepository;
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
@Component("classesManager")
public class ClassesManager implements BaseDataManager {
	private final static String KEY = "BJDM";// 数据库字段名称，班级编码
	private final static String NAME = "BJMC";// 数据库字段名称，班级中文名称
	private final static String PROFESSIONAL_KEY = "ZY_ID";// 数据库字段名称，专业编码
	private final static String NJ = "NJ";// 数据库字段名称，年级
	private final static String FILE_NAME = "classes.json";// 输出文件名称
	@Autowired
	private ChongqingJdbcRepository repository;
	@Autowired
	private FileOperator fileOperator;

	@Override
	public List<BaseDTO> findAll() {
		/*
		 * SELECT BJDM , BJMC, ZY_ID, NJ FROM T_XJ_ClassInfo WHERE NJ in
		 * ('2015', '2016', '2017') 替换字符串，替换为当前年份，前1年，前2年 获取当前年份，前1年，前2年
		 * 的字符串，然后替换
		 */
		// SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd
		// HH:mm:ss");
		// Calendar c = Calendar.getInstance();
		// 过去一年
		// c.setTime(new Date());
		// c.add(Calendar.YEAR, -1);
		// Date y = c.getTime();
		// c.add(Calendar.YEAR, -2);
		// Date y1 = c.getTime();
		// String year = format.format(y);
		// String year1 = format.format(y1);
		// System.out.println(year+year1);
		List<Map<String, Object>> ds = repository.findClasses();
		List<BaseDTO> list = new ArrayList<>();
		for (Map<String, Object> m : ds) {
			ClassesDTO d = new ClassesDTO();
			if (StringUtils.isEmpty(m.get(KEY))) {
				log.warn("Classes code is null");
				continue;
			}

			d.setKey(m.get(KEY).toString());

			if (!StringUtils.isEmpty(m.get(NAME))) {
				d.setName(m.get(NAME).toString());
			} else {
				log.warn("Classes name is null");
			}

			if (!StringUtils.isEmpty(m.get(PROFESSIONAL_KEY))) {
				d.setProfessionalKey(m.get(PROFESSIONAL_KEY).toString());
			} else {
				log.warn("Classes professional key is null");
			}
			if (!StringUtils.isEmpty(m.get(NJ))) {
				d.setNj(m.get(NJ).toString());
			}
			list.add(d);
		}
		return list;
	}

	@Override
	public void writeDataToDBFile(List<BaseDTO> allNowLines) {
		fileOperator.outDBDir(allNowLines, FILE_NAME, "classes");
	}

	@Override
	public List<BaseDTO> readYesterdayData() {
		File f = fileOperator.getYesterdayDBFile(FILE_NAME);
		List<BaseDTO> rs = new ArrayList<>();
		if (f.exists() && f.isFile()) {
			List<ClassesDTO> list = JsonUtil.decode(f, new TypeReference<List<ClassesDTO>>() {
			});
			rs.addAll(list);
		} else {
			log.warn("Read classes yesterday file ({}) not exists.", f.toString());
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
	public List<ClassesDTO> getCurrentAllNewData() {
		File f = fileOperator.getCurrentdayDBFile(FILE_NAME);
		List<ClassesDTO> rs = new ArrayList<>();
		if (f.exists() && f.isFile()) {
			List<ClassesDTO> list = JsonUtil.decode(f, new TypeReference<List<ClassesDTO>>() {
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
	public List<ClassesDTO> getCurrentUpdateData() {
		List<ClassesDTO> rs = new ArrayList<>();
		File addFile = fileOperator.getCurrentdayAddFile(FILE_NAME);
		if (addFile.exists() && addFile.isFile()) {
			List<ClassesDTO> list = JsonUtil.decode(addFile, new TypeReference<List<ClassesDTO>>() {
			});
			rs.addAll(list);
		}
		File updateFile = fileOperator.getCurrentdayUpdateFile(FILE_NAME);
		if (updateFile.exists() && updateFile.isFile()) {
			List<ClassesDTO> list = JsonUtil.decode(updateFile, new TypeReference<List<ClassesDTO>>() {
			});
			rs.addAll(list);
		}
		return rs;
	}
}
