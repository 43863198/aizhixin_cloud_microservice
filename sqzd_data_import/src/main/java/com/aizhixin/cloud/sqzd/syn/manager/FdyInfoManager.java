package com.aizhixin.cloud.sqzd.syn.manager;


import com.aizhixin.cloud.sqzd.common.manager.FileOperator;
import com.aizhixin.cloud.sqzd.common.manager.JsonUtil;
import com.aizhixin.cloud.sqzd.syn.dto.BaseDTO;
import com.aizhixin.cloud.sqzd.syn.dto.FdyDTO;
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
@Component("fdyInfoManager")
public class FdyInfoManager implements BaseDataManager {
    private final static String KEY = "BJDM";// 数据库字段名称，班级编码
    private final static String fdyname = "xm"; //数据库字段，辅导员名字
    private final static String fdynum = "gh";//数据库字段，辅导员电话
    private final static String FILE_NAME = "fdyinfo.json";//生成的json文件

    @Autowired
    private ChongqingJdbcRepository repository;

    @Autowired
    private FileOperator fileOperator;

    @Override
    public List<BaseDTO> findAll() {
        List<Map<String, Object>> fdyInfo = repository.findFdyInfo();
        List<BaseDTO> list = new ArrayList<>();

        for(Map<String, Object> m : fdyInfo){
            FdyDTO fdyDTO = new FdyDTO();
            if (!StringUtils.isEmpty(m.get(KEY))) {
                fdyDTO.setKey(m.get(KEY).toString());
            }else{
                log.warn("fdy key is null");
            }

            if(!StringUtils.isEmpty(m.get(fdyname))) {
                fdyDTO.setFdyname(m.get(fdyname).toString());
            }else{
                log.warn("fdy name is null");
            }

            if(!StringUtils.isEmpty(m.get(fdynum))) {
                fdyDTO.setFdynum(m.get(fdynum).toString());
            }else{
                log.warn("fdy num is null");
            }
            list.add(fdyDTO);
            }
        return list;
    }

    @Override
    public void writeDataToDBFile(List<BaseDTO> allNowLines) {
        fileOperator.outDBDir(allNowLines,FILE_NAME,"fdyinfo");
    }

    @Override
    public List<BaseDTO> readYesterdayData() {
        File f = fileOperator.getYesterdayDBFile(FILE_NAME);
        List<BaseDTO> rs = new ArrayList<>();
        if (f.exists() && f.isFile()) {
            List<FdyDTO> list = JsonUtil.decode(f, new TypeReference<List<FdyDTO>>() {
            });
            rs.addAll(list);
        } else {
            log.warn("Read fdy info yesterday file ({}) not exists.", f.toString());
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
    public List<FdyDTO> getCurrentAllNewData() {
        File f = fileOperator.getCurrentdayDBFile(FILE_NAME);
        List<FdyDTO> rs = new ArrayList<>();
        if (f.exists() && f.isFile()) {
            List<FdyDTO> list = JsonUtil.decode(f, new TypeReference<List<FdyDTO>>() {
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
    public List<FdyDTO> getCurrentUpdateData() {
        List<FdyDTO> rs = new ArrayList<>();
        File addFile = fileOperator.getCurrentdayAddFile(FILE_NAME);
        if (addFile.exists() && addFile.isFile()) {
            List<FdyDTO> list = JsonUtil.decode(addFile, new TypeReference<List<FdyDTO>>() {
            });
            rs.addAll(list);
        }
        File updateFile = fileOperator.getCurrentdayUpdateFile(FILE_NAME);
        if (updateFile.exists() && updateFile.isFile()) {
            List<FdyDTO> list = JsonUtil.decode(updateFile, new TypeReference<List<FdyDTO>>() {
            });
            rs.addAll(list);
        }
        return rs;
    }
}
