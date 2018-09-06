package com.aizhixin.cloud.paycallback.service;


import com.aizhixin.cloud.paycallback.common.core.ErrorCode;
import com.aizhixin.cloud.paycallback.common.exception.CommonException;
import com.aizhixin.cloud.paycallback.common.util.ExcelUtil;
import com.aizhixin.cloud.paycallback.domain.PersonCostExcelDomain;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Component
public class ExcelHelper {

    /**
     * 获取字符串类型的Cell的值
     * @param row
     * @param i
     * @return
     */
    private String getCellStringValue(Row row, int i) {
        if (null != row.getCell(i)) {
            String t = row.getCell(i).getStringCellValue();
            if (null != t) {
                t = t.trim();
            }
            return t;
        }
        return null;
    }

    /**
     * 将Cell类型设置为字符串类型
     * @param row
     * @param n
     */
    private void setCellStringType(Row row, int n) {
        for (int i = 0; i < n; i++) {
            if (null != row.getCell(i)) {
                row.getCell(i).setCellType(CellType.STRING);
            }
        }
    }

    /**
     * 从上次Excel文件中读取学生录取预置表
     * @param file  excel文件
     * @return      读取内容
     */
    public List<PersonCostExcelDomain> readStudentCostFromInputStream(MultipartFile file) {
        List<PersonCostExcelDomain> r = new ArrayList<>();
        ExcelUtil util = new ExcelUtil(file);
        Sheet sheet = util.getSheet("学生录取信息预置表");
        if (null == sheet) {//如果没有学院标签，读取第1个标签的内容
            sheet = util.getSheet(0);
        }
        Set<String> idNumberSet = new HashSet<>();
        boolean hasError = false;
        StringBuilder error = new StringBuilder();
        if (null != sheet) {
            Iterator<Row> rows = sheet.rowIterator();
            int line = 1;
            while (rows.hasNext()) {
                Row row = rows.next();
                if (1 == line) {
                    line++;
                    continue;//跳过第一行
                }
                StringBuilder m = new StringBuilder();
                setCellStringType(row, 13);
                PersonCostExcelDomain c = new PersonCostExcelDomain();
                String temp = getCellStringValue(row, 0);
                if (StringUtils.isEmpty(temp)) {
                    m.append(line).append("行姓名缺失");
                    hasError = true;
                }
                c.setName(temp);
                temp = getCellStringValue(row, 1);
                if (StringUtils.isEmpty(temp)) {
                    m.append(line).append("行性别缺失");
                    hasError = true;
                }
                c.setSex(temp);
                temp = getCellStringValue(row, 2);
                if (StringUtils.isEmpty(temp) || temp.length() < 15) {
                    m.append(line).append("行身份证号码缺失");
                    hasError = true;
                } else {
                    if(idNumberSet.contains(temp)) {
                        m.append(line).append("行身份证号码重复");
                        hasError = true;
                    } else {
                        idNumberSet.add(temp);
                    }
                }
                c.setIdNumber(temp);
                temp = getCellStringValue(row, 3);
                c.setAdmissionNoticeNumber(temp);
                temp = getCellStringValue(row, 4);
                c.setStudentSource(temp);
                temp = getCellStringValue(row, 5);
                c.setStudentType(temp);
                temp = getCellStringValue(row, 6);
                c.setEduLevel(temp);
                temp = getCellStringValue(row, 7);
                if (StringUtils.isEmpty(temp)) {
                    m.append(line).append("行录取专业缺失");
                    hasError = true;
                }
                c.setProfessionalName(temp);
                temp = getCellStringValue(row, 8);
                if (StringUtils.isEmpty(temp)) {
                    m.append(line).append("行系别缺失");
                    hasError = true;
                }
                c.setCollegeName(temp);
                temp = getCellStringValue(row, 9);
                c.setGrade(temp);
                temp = getCellStringValue(row, 10);
                c.setSchoolLocal(temp);

                temp = getCellStringValue(row, 11);
                if (!StringUtils.isEmpty(temp)) {
                    c.setShouldPay(new Double(temp));
                } else {
                    if (StringUtils.isEmpty(temp)) {
                        m.append(line).append("行应缴费缺失");
                    }
                }
                if (m.length() > 0) {
                    c.setMsg("第" + line + "行:" + m.toString());
                    error.append(c.getMsg()).append(";\n");
                }
                temp = getCellStringValue(row, 12);
                c.setPayDesc(temp);
//                temp = getCellStringValue(row, 12);
//                if (!StringUtils.isEmpty(temp)) {
//                    c.setSmallAmount(new Double(temp));
//                }
//                temp = getCellStringValue(row, 13);
//                c.setInstallmentRate(temp);
                r.add(c);
                line++;
            }
        }
        if (r.size() <= 0) {
            throw new CommonException(ErrorCode.PARAMS_CONFLICT, "没有读取到有效数据");
        }
        if (hasError) {
            throw new CommonException(ErrorCode.PARAMS_CONFLICT, error.toString());
        }
        return r;
    }
}
