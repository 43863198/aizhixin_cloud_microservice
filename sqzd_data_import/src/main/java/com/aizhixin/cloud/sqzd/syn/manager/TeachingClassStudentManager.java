package com.aizhixin.cloud.sqzd.syn.manager;//package com.aizhixin.cloud.cqaq.syn.manager;
//
//
//import com.aizhixin.cloud.cqaq.syn.dto.BaseDTO;
//import com.aizhixin.cloud.cqaq.syn.dto.KeyNameDTO;
//import com.aizhixin.cloud.cqaq.syn.repository.ChongqingJdbcRepository;
//import com.aizhixin.cloud.cqaq.common.manager.FileOperator;
//import com.aizhixin.cloud.cqaq.common.manager.JsonUtil;
//import com.fasterxml.jackson.core.type.TypeReference;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import java.io.File;
//import java.util.*;
//
//@Slf4j
//@Component
//public class TeachingClassStudentManager {
//    private final static String KEY = "KC_ID";//数据库字段名称，教学班编码
//    private final static String STUENT_KEY = "XS_ID";//数据库字段名称，学生学号
//    private final static String FILE_NAME = "teachingclass_student.json";//输出文件名称
//    @Autowired
//    private ChongqingJdbcRepository repository;
//    @Autowired
//    private FileOperator fileOperator;
//
//    public List<KeyNameDTO> findAll() {
//        List<Map<String, Object>> ds = repository.findTeachingClassStudent();
//        List<KeyNameDTO> list = new ArrayList<>();
//        for (Map<String, Object> m : ds) {
//            KeyNameDTO d = new KeyNameDTO();
//            if (StringUtils.isEmpty(m.get(KEY))) {
//                log.warn("TeachingClass student  teachingClass key is null");
//                continue;
//            }
//
//            d.setKey(m.get(KEY).toString());
//
//            if (!StringUtils.isEmpty(m.get(STUENT_KEY))) {
//                d.setName(m.get(STUENT_KEY).toString());
//            } else {
//                log.warn("TeachingClass student student key is null");
//                continue;
//            }
//            list.add(d);
//        }
//        return list;
//    }
//
//    public void writeDataToDBFile(List<BaseDTO> allNowLines) {
//        fileOperator.outDBDir(allNowLines, FILE_NAME, "teachingClassStudent");
//    }
//
//    public List<KeyNameDTO> readYesterdayData() {
//        File f = fileOperator.getYesterdayDBFile(FILE_NAME);
//        if (f.exists() && f.isFile()) {
//            return  JsonUtil.decode(f, new TypeReference<List<KeyNameDTO>>() {});
//        } else {
//            log.warn("Read TeachingClass student yesterday file ({}) not exists.", f.toString());
//            return new ArrayList<>();
//        }
//    }
//
//    public void writeDataToOut(Map<String, Set<String>> addMap, Map<String, Set<String>> delMap) {
//        fileOperator.outOutCompactDir(addMap, delMap, FILE_NAME);
//    }
//
//    public void parserData(List<KeyNameDTO> inDatas, List<BaseDTO> outList, Map<String, Set<String>> classStudents) {
//        if (null != outList) {
//            outList.addAll(inDatas);
//        }
//
//        for (KeyNameDTO d : inDatas) {
//            Set<String> classesSet = classStudents.get(d.getKey());
//            if (null == classesSet) {
//                classesSet = new HashSet<>();
//                classStudents.put(d.getKey(), classesSet);
//            }
//            classesSet.add(d.getName());
//        }
//    }
//
//    public void validateRs(Map<String, String> teachingClassAndRsMap, Map<String, Set<String>> classStudents) {
//        if (teachingClassAndRsMap.size() != classStudents.size()) {
//            log.warn("Teachingclass size:{}, student teachingclass size: {} not eq.", teachingClassAndRsMap.size(), classStudents.size());
//        }
//
//        for (Map.Entry<String, String> e : teachingClassAndRsMap.entrySet()) {
//            Set<String> students = classStudents.get(e.getKey());
//            if (null == students || students.isEmpty()) {
//                log.warn("Teachingclass:({}) RS ({}) have not any student", e.getKey(), e.getValue());
//                continue;
//            }
//
//            int rs = 0;
//            if (null != e.getValue()) {
//                rs = Integer.parseInt(e.getValue());
//            }
//            if (rs != students.size()) {
//                log.warn("Teachingclass ({}) students have count:({}), but teachingclass student have count:({})", e.getKey(),  e.getValue(), students.size());
//            }
//        }
//    }
//}
