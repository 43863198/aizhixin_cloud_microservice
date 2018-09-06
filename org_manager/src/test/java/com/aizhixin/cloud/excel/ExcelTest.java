package com.aizhixin.cloud.excel;

import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ExcelTest {

    private void setCellStringType(Row row, int n) {
        for (int i = 0; i < n; i++) {
            if (null != row.getCell(i)) {
                row.getCell(i).setCellType(CellType.STRING);
            }
        }
    }
    private String getCellStringValue(Row row, int i) {
        if (null != row.getCell(i)) {
            Cell cell  = row.getCell(i);
//            System.out.println(cell.toString());
//                cell.get
            String t = cell.getStringCellValue();
            if (null != t) {
                t = t.trim();
            }
            return t;
        }
        return null;
    }

    public SXSSFWorkbook getWorkbook(File file) throws Exception {
        return new SXSSFWorkbook(new XSSFWorkbook(file));
    }

    public SXSSFSheet getSheet(SXSSFWorkbook wb, String sheetName) {
        return wb.getSheet(sheetName);
    }

    public void readSheet(SXSSFSheet sheet) {
//        sheet.getR
//        Iterator<SXSSFRow> rows = sheet.rowIterator();
//        int line = 1;
//        while (rows.hasNext()) {
//            Row row = rows.next();
//            if (1 == line) {
//                line++;
//                continue;//跳过第一行
//            }
////            System.out.print(row.toString());
////            setCellStringType(row, 9);
////            System.out.println(row.getFirstCellNum());
//            for (int i = 0; i < 9; i++) {
//                System.out.print(getCellStringValue(row, i));
//                System.out.print("\t");
//            }
//            System.out.println("\n");
//        }
    }

    public void read (String excelFile) throws Exception {
        File file = new File(excelFile);
        if (!file.exists() || !file.isFile()) {
            System.out.println("File:(" + excelFile + ") is not exists");
            return;
        }
        SXSSFWorkbook wb = getWorkbook(file);
        SXSSFSheet sheet = getSheet(wb, "课程表");
        readSheet(sheet);
    }

    public static void main(String[] args) throws Exception {
//        ExcelTest t = new ExcelTest();
//        t.read("d:/2017-08-25必修课课程表.xlsx");

        // create a new file input stream with the input file specified
        // at the command line
        FileInputStream fin = new FileInputStream(args[0]);
        // create a new org.apache.poi.poifs.filesystem.Filesystem
        POIFSFileSystem poifs = new POIFSFileSystem(fin);
        // get the Workbook (excel part) stream in a InputStream
        InputStream din = poifs.createDocumentInputStream("Workbook");
        // construct out HSSFRequest object
        HSSFRequest req = new HSSFRequest();
        // lazy listen for ALL records with the listener shown above
//        req.addListenerForAllRecords(new EventExample());
        // create our event factory
        HSSFEventFactory factory = new HSSFEventFactory();
        // process our events based on the document input stream
        factory.processEvents(req, din);
        // once all the events are processed close our file input stream
        fin.close();
        // and our document input stream (don't want to leak these!)
        din.close();
        System.out.println("done.");
    }
}
