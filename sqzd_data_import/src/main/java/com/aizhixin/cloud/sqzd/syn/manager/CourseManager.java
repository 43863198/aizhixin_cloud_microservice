package com.aizhixin.cloud.sqzd.syn.manager;



import com.aizhixin.cloud.sqzd.common.manager.FileOperator;
import com.aizhixin.cloud.sqzd.common.manager.JsonUtil;
import com.aizhixin.cloud.sqzd.syn.dto.BaseDTO;
import com.aizhixin.cloud.sqzd.syn.dto.CourseDTO;
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
@Component("courseManager")
public class CourseManager implements BaseDataManager {
    private final static String KEY = "user_kcid";//数据库字段名称，课程编码
    private final static String NAME = "ZWMC";//数据库字段名称，课程中文名称
	private final static String FILE_NAME = "course.json";//输出文件名称
    @Autowired
    private ChongqingJdbcRepository repository;
    @Autowired
    private FileOperator fileOperator;

    @Override
    public List<BaseDTO> findAll() {
        List<Map<String, Object>> ds = repository.findCourse();
        List<BaseDTO> list = new ArrayList<>();
        for (Map<String, Object> m : ds) {
            CourseDTO d = new CourseDTO();
            if (StringUtils.isEmpty(m.get(KEY))) {
                log.warn("Course code is null");
                continue;
            }

            d.setKey(m.get(KEY).toString());

            if (!StringUtils.isEmpty(m.get(NAME))) {
                d.setName(m.get(NAME).toString());
            } else {
                log.warn("Course name is null");
            }
            list.add(d);
        }
        return list;
    }

    @Override
    public void writeDataToDBFile(List<BaseDTO> allNowLines) {
        fileOperator.outDBDir(allNowLines, FILE_NAME, "course");
    }

    @Override
    public List<BaseDTO> readYesterdayData() {
        File f = fileOperator.getYesterdayDBFile(FILE_NAME);
        List<BaseDTO> rs = new ArrayList<>();
        if (f.exists() && f.isFile()) {
            List<CourseDTO> list =  JsonUtil.decode(f, new TypeReference<List<CourseDTO>>() {});
            rs.addAll(list);
        } else {
            log.warn("Read course yesterday file ({}) not exists.", f.toString());
        }
        return rs;
    }

    @Override
    public void writeDataToOut(List<BaseDTO> addList, List<BaseDTO> upadateList, List<BaseDTO> delList) {
        fileOperator.outOutCompactDir(addList, upadateList, delList, FILE_NAME);
    }

    /**
     * 读当天的所有最新数据
     * @return  所有最新数据
     */
    public List<CourseDTO> getCurrentAllNewData() {
        File f = fileOperator.getCurrentdayDBFile(FILE_NAME);
        List<CourseDTO> rs = new ArrayList<>();
        if (f.exists() && f.isFile()) {
            List<CourseDTO> list =  JsonUtil.decode(f, new TypeReference<List<CourseDTO>>() {});
            rs.addAll(list);
        }
        return rs;
    }

    /**
     * 读当天的添加和修改的数据(删除数据需要单独处理)
     * @return  当天的添加和修改的数据
     */
    public List<CourseDTO> getCurrentUpdateData() {
        List<CourseDTO> rs = new ArrayList<>();
        File addFile = fileOperator.getCurrentdayAddFile(FILE_NAME);
        if (addFile.exists() && addFile.isFile()) {
            List<CourseDTO> list =  JsonUtil.decode(addFile, new TypeReference<List<CourseDTO>>() {});
            rs.addAll(list);
        }
        File updateFile = fileOperator.getCurrentdayUpdateFile(FILE_NAME);
        if (updateFile.exists() && updateFile.isFile()) {
            List<CourseDTO> list =  JsonUtil.decode(updateFile, new TypeReference<List<CourseDTO>>() {});
            rs.addAll(list);
        }
        return rs;
    }
}
