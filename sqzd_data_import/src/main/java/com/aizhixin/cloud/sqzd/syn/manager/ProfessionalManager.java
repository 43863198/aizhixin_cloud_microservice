package com.aizhixin.cloud.sqzd.syn.manager;


import com.aizhixin.cloud.sqzd.common.manager.FileOperator;
import com.aizhixin.cloud.sqzd.common.manager.JsonUtil;
import com.aizhixin.cloud.sqzd.syn.dto.BaseDTO;
import com.aizhixin.cloud.sqzd.syn.dto.ProfessionalDTO;
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
@Component("professionalManager")
public class ProfessionalManager implements BaseDataManager {
	private final static String KEY = "DM";// 数据库字段名称，专业编码
	private final static String NAME = "ZWMC";// 数据库字段名称，专业中文名称
	private final static String COLLEGE_KEY = "XY_ID";// 数据库字段名称，院系所编码
	private final static String FILE_NAME = "professional.json";// 输出文件名称
	@Autowired
	private ChongqingJdbcRepository repository;
	@Autowired
	private FileOperator fileOperator;

	@Override
	public List<BaseDTO> findAll() {
		List<Map<String, Object>> ds = repository.findProfessional();
		List<BaseDTO> list = new ArrayList<>();
		for (Map<String, Object> m : ds) {
			ProfessionalDTO d = new ProfessionalDTO();
			if (StringUtils.isEmpty(m.get(KEY))) {
				log.warn("Professional code is null");
				continue;
			}

			d.setKey(m.get(KEY).toString());

			if (!StringUtils.isEmpty(m.get(NAME))) {
				d.setName(m.get(NAME).toString());
			} else {
				log.warn("Professional name is null");
			}

			if (!StringUtils.isEmpty(m.get(COLLEGE_KEY))) {
				d.setCollegeKey(m.get(COLLEGE_KEY).toString());
			} else {
				log.warn("Professional college key is null");
			}
			list.add(d);
		}
		return list;
	}

	@Override
	public void writeDataToDBFile(List<BaseDTO> allNowLines) {
		fileOperator.outDBDir(allNowLines, FILE_NAME, "professional");
	}

	@Override
	public List<BaseDTO> readYesterdayData() {
		File f = fileOperator.getYesterdayDBFile(FILE_NAME);
		List<BaseDTO> rs = new ArrayList<>();
		if (f.exists() && f.isFile()) {
			List<ProfessionalDTO> list = JsonUtil.decode(f, new TypeReference<List<ProfessionalDTO>>() {
			});
			rs.addAll(list);
		} else {
			log.warn("Read professional yesterday file ({}) not exists.", f.toString());
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
	public List<ProfessionalDTO> getCurrentAllNewData() {
		File f = fileOperator.getCurrentdayDBFile(FILE_NAME);
		List<ProfessionalDTO> rs = new ArrayList<>();
		if (f.exists() && f.isFile()) {
			List<ProfessionalDTO> list = JsonUtil.decode(f, new TypeReference<List<ProfessionalDTO>>() {
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
	public List<ProfessionalDTO> getCurrentUpdateData() {
		List<ProfessionalDTO> rs = new ArrayList<>();
		File addFile = fileOperator.getCurrentdayAddFile(FILE_NAME);
		if (addFile.exists() && addFile.isFile()) {
			List<ProfessionalDTO> list = JsonUtil.decode(addFile, new TypeReference<List<ProfessionalDTO>>() {
			});
			rs.addAll(list);
		}
		File updateFile = fileOperator.getCurrentdayUpdateFile(FILE_NAME);
		if (updateFile.exists() && updateFile.isFile()) {
			List<ProfessionalDTO> list = JsonUtil.decode(updateFile, new TypeReference<List<ProfessionalDTO>>() {
			});
			rs.addAll(list);
		}
		return rs;
	}
}
