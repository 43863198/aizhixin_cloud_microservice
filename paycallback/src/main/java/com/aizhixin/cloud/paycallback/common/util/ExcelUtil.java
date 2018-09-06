package com.aizhixin.cloud.paycallback.common.util;

import com.aizhixin.cloud.paycallback.common.core.ErrorCode;
import com.aizhixin.cloud.paycallback.common.exception.CommonException;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * Created by zhen.pan on 2017/7/14.
 */
public class ExcelUtil {
    private Workbook wb;
    @Getter @Setter private boolean isE2007 = false; // 判断是否是excel2007格式
    public ExcelUtil(MultipartFile file) {
        String path = file.getOriginalFilename();// 返回原始文件名
        String extName = null;
        int p = path.lastIndexOf(".");
        if (p >= 0) {
            extName = path.substring(p + 1);
        }
        isE2007 = "xlsx".equalsIgnoreCase(extName) ? true : false;
        InputStream input;
        try {
            input = file.getInputStream();
            if (isE2007) {
                wb = new XSSFWorkbook(input);
            } else {
                wb = new HSSFWorkbook(input);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommonException(ErrorCode.SYSTEM_EXCEPTION_CODE, e.getMessage());
        }
    }

    public Sheet getSheet(String sheetName) {
        return wb.getSheet(sheetName);
    }

    public Sheet getSheet(int sheetIndex) {
        return wb.getSheetAt(sheetIndex);
    }
}
