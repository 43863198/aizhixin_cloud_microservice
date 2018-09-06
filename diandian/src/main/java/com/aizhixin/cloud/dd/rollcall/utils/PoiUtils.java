package com.aizhixin.cloud.dd.rollcall.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;


public class PoiUtils {

    public static void exprotExcel(HSSFWorkbook wb, FileInputStream fis, List<?> data, int rowStart, int hide) throws IOException, InvalidFormatException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        HSSFSheet sheet = wb.getSheetAt(0);
        for (int rows = 0; rows < data.size(); rows++) {
            //偏移行起始位置
            HSSFRow row = sheet.createRow(rows + rowStart);
            //获取当前对象
            Object o = data.get(rows);
            //获取对象属性个数 用于列处理
            Field[] Field = o.getClass().getDeclaredFields();
            //定义对象数组
            if (hide!=0) {
                for (int cells = 0; cells < Field.length; cells++) {
                        if (cells<hide) {
                            Field field = Field[cells];
                            //循环的方法名
                            String fieldName = field.getName();
                            //根据方法名 取值
                            String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                            Class c = o.getClass();
                            Method getMethod = c.getMethod(getMethodName, new Class[]{});
                            Object value = getMethod.invoke(o, new Object[]{});
                            String textValue = value == null ? "" : value.toString();
                            if (textValue != null) {
                                Pattern p = Pattern.compile("^//d+(//.//d+)?$");
                                Matcher matcher = p.matcher(textValue);
                                if (matcher.matches()) {
                                    // 是数字当作double处理
                                    row.createCell(cells).setCellValue(Double.parseDouble(textValue));
                                } else {
                                    row.createCell(cells).setCellValue(textValue);
                                }
                            }
                    }
                }
            } else {
                for (int cells = 0; cells < Field.length; cells++) {
                    Field field = Field[cells];
                    //循环的方法名
                    String fieldName = field.getName();
                    //根据方法名 取值
                    String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    Class c = o.getClass();
                    Method getMethod = c.getMethod(getMethodName, new Class[]{});
                    Object value = getMethod.invoke(o, new Object[]{});
                    String textValue = value == null ? "" : value.toString();
                    if (textValue != null) {
                        Pattern p = Pattern.compile("^//d+(//.//d+)?$");
                        Matcher matcher = p.matcher(textValue);
                        if (matcher.matches()) {
                            // 是数字当作double处理
                            row.createCell(cells).setCellValue(Double.parseDouble(textValue));
                        } else {
                            row.createCell(cells).setCellValue(textValue);
                        }
                    }
                }
            }
        }
    }



}
