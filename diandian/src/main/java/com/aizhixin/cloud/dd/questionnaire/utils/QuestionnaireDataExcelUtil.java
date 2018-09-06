package com.aizhixin.cloud.dd.questionnaire.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.aizhixin.cloud.dd.questionnaire.dto.QuestionnaireDataDTO;
import com.aizhixin.cloud.dd.questionnaire.dto.QuestionnaireQuestionDataDTO;
import com.aizhixin.cloud.dd.questionnaire.dto.QuestionnaireStudentCommentDTO;
import com.aizhixin.cloud.dd.rollcall.dto.IODTO;
import com.aizhixin.cloud.dd.rollcall.dto.StudentInfoDTOV2;
import com.aizhixin.cloud.dd.rollcall.utils.IOUtil;
import org.apache.poi.hssf.usermodel.*;

import org.apache.poi.ss.util.CellRangeAddress;

public class QuestionnaireDataExcelUtil {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    @SuppressWarnings("deprecation")
    public static String exportQuestionnaireData(QuestionnaireDataDTO qdd, List<StudentInfoDTOV2> stuInfo, List<QuestionnaireStudentCommentDTO> comments, IOUtil ioUtil) {
        ByteArrayOutputStream ba = new ByteArrayOutputStream(1000);
        String[] s = new String[]{"问卷名称", "截止时间", "调查份数", "已提交份数", "已提交占比", "未提交份数", "未提交占比", "问卷总分", "问卷平均分"};
        if (qdd.getQuesType() != null && qdd.getQuesType().intValue() == QuestionnaireType.PEER.getType()) {
            s = new String[]{"被评人", "截止时间", "调查份数", "已提交份数", "已提交占比", "未提交份数", "未提交占比", "问卷总分", "问卷得分"};
        }
        String[] s1 = new String[]{"题号", "题目类型", "题目内容", "题目分数", "题目选项", "选项分数", "选项调查占比", "平均分"};
        HSSFWorkbook workbook = new HSSFWorkbook();
        workbook.createInformationProperties();
        workbook.getDocumentSummaryInformation().setCompany("北京知新树科技有限公司");
        HSSFSheet sheet1 = workbook.createSheet("问卷统计");
        sheet1.setColumnWidth(0, (int) ((50 + 0.72) * 64));
        sheet1.setColumnWidth(1, (int) ((50 + 0.72) * 64));
        sheet1.setColumnWidth(2, (int) ((50 + 0.72) * 256));
        sheet1.setColumnWidth(3, (int) ((50 + 0.72) * 64));
        sheet1.setColumnWidth(4, (int) ((50 + 0.72) * 64));
        sheet1.setColumnWidth(5, (int) ((50 + 0.72) * 64));
        sheet1.setColumnWidth(6, (int) ((50 + 0.72) * 64));
        sheet1.setColumnWidth(7, (int) ((50 + 0.72) * 64));
        HSSFCellStyle style1 = workbook.createCellStyle();
        style1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        style1.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        style1.setBorderBottom(HSSFCellStyle.BORDER_THIN);// 下边框
        style1.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
        style1.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
        style1.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
        HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        style2.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);// 下边框
        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
        HSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);// 下边框
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
        for (int i = 0; i < s.length; i++) {
            CellRangeAddress cad = new CellRangeAddress(i, i, 1, 7);
            sheet1.addMergedRegion(cad);
            HSSFRow hr = sheet1.createRow(i);
            HSSFCell hc = hr.createCell(0);
            hc.setCellValue(s[i]);
            hc.setCellStyle(style1);
            HSSFCell hc1 = hr.createCell(1);
            HSSFCell hc2 = hr.createCell(2);
            hc2.setCellStyle(style);
            HSSFCell hc3 = hr.createCell(3);
            hc3.setCellStyle(style);
            HSSFCell hc4 = hr.createCell(4);
            hc4.setCellStyle(style);
            HSSFCell hc5 = hr.createCell(5);
            hc5.setCellStyle(style);
            HSSFCell hc6 = hr.createCell(6);
            hc6.setCellStyle(style);
            HSSFCell hc7 = hr.createCell(7);
            hc7.setCellStyle(style);
            hc1.setCellStyle(style);
            switch (i) {
                case 0:
                    hc1.setCellValue(qdd.getQuestionnaireName());
                    break;
                case 1:
                    hc1.setCellValue(sdf.format(qdd.getEndDate()));
                    break;
                case 2:
                    hc1.setCellValue(qdd.getTotalPeple());
                    break;
                case 3:
                    hc1.setCellValue(qdd.getCommitNum());
                    break;
                case 4:
                    hc1.setCellValue(qdd.getCommitZb());
                    break;
                case 5:
                    hc1.setCellValue(qdd.getNoCommitNum());
                    break;
                case 6:
                    hc1.setCellValue(qdd.getNoCommitZb());
                    break;
                case 7:
                    hc1.setCellValue(qdd.getQuestionnaireTotalScore());
                    break;
                case 8:
                    if (qdd.getAvgScore() != null) {
                        if (qdd.getQuesType() != null && qdd.getQuesType().intValue() == QuestionnaireType.PEER.getType()) {
                            float score = Float.parseFloat(qdd.getAvgScore());
                            score = score * qdd.getCommitNum();
                            hc1.setCellValue(score);
                        } else {
                            hc1.setCellValue(qdd.getAvgScore());
                        }
                    } else {
                        hc1.setCellValue("0");
                    }

                    break;
            }
        }
        HSSFRow hr = sheet1.createRow(s.length + 2);
        for (int i = 0; i < s1.length; i++) {
            HSSFCell h = hr.createCell(i);
            h.setCellStyle(style);
            h.setCellValue(s1[i]);
        }
        if (null != qdd.getQuestionnaireQuestionDataDTOs() && 0 < qdd.getQuestionnaireQuestionDataDTOs().size()) {
            List<QuestionnaireQuestionDataDTO> qqdl = qdd.getQuestionnaireQuestionDataDTOs();
            int ss = s.length + 3;
            for (int i = 0; i < qqdl.size(); i++) {
                if (qqdl.get(i).getQuestionType() != 30) {
                    int a = qqdl.get(i).getChoices().size(); // a=2
                    if (a == 0) {
                        HSSFRow r = sheet1.createRow(ss);
                        HSSFCell hc = r.createCell(0);
                        hc.setCellValue(qqdl.get(i).getNo());
                        hc.setCellStyle(style);
                        HSSFCell hc1 = r.createCell(1);
                        hc1.setCellStyle(style);
                        hc1.setCellValue("非选择题");
                        HSSFCell hc2 = r.createCell(2);
                        hc2.setCellStyle(style2);
                        hc2.setCellValue(qqdl.get(i).getContent());
                        HSSFCell hc3 = r.createCell(3);
                        hc3.setCellStyle(style);
                        hc3.setCellValue(qqdl.get(i).getScore());
                        HSSFCell hc4 = r.createCell(4);
                        hc4.setCellValue("-");
                        hc4.setCellStyle(style);
                        HSSFCell hc5 = r.createCell(5);
                        hc5.setCellValue("-");
                        hc5.setCellStyle(style);
                        HSSFCell hc6 = r.createCell(6);
                        hc6.setCellValue("-");
                        hc6.setCellStyle(style);
                        HSSFCell hc7 = r.createCell(7);
                        hc7.setCellValue(qqdl.get(i).getAvgScore());
                        hc7.setCellStyle(style);
                        ss = ss + 1;
                        continue;
                    }
                    for (int j = 0; j < 4; j++) {
                        int lastRow = ss + a - 1;
                        if (ss != lastRow) {
                            CellRangeAddress cad = new CellRangeAddress(ss, ss + a - 1, j, j);
                            sheet1.addMergedRegion(cad);
                        }
                    }
                    int lastRow = ss + a - 1;
                    if (ss != lastRow) {
                        CellRangeAddress cad1 = new CellRangeAddress(ss, ss + a - 1, 7, 7);
                        sheet1.addMergedRegion(cad1);
                    }
                    for (int j = 0; j < a; j++) {
                        HSSFRow hr1 = sheet1.createRow(ss + j);
                        HSSFCell hc7 = hr1.createCell(7);
                        hc7.setCellStyle(style);
                        HSSFCell hc = hr1.createCell(0);
                        HSSFCell hc1 = hr1.createCell(1);
                        hc1.setCellStyle(style);
                        hc.setCellStyle(style);
                        HSSFCell hc2 = hr1.createCell(2);
                        hc2.setCellStyle(style2);
                        HSSFCell hc3 = hr1.createCell(3);
                        hc3.setCellStyle(style);
                        if (j == 0) {
                            hc.setCellValue(new HSSFRichTextString(qqdl.get(i).getNo() + ""));
                            if (qqdl.get(i).getQuestionType() == 10) {
                                hc1.setCellValue(new HSSFRichTextString("单选"));
                            } else if (qqdl.get(i).getQuestionType() == 20) {
                                hc1.setCellValue(new HSSFRichTextString("多选"));
                            }
                            hc2.setCellValue(new HSSFRichTextString(qqdl.get(i).getContent()));
                            hc3.setCellValue(new HSSFRichTextString(qqdl.get(i).getScore()));
                            hc7.setCellValue(new HSSFRichTextString(qqdl.get(i).getAvgScore()));
                        }
                        HSSFCell hfc4 = hr1.createCell(4);
                        hfc4.setCellValue(new HSSFRichTextString(qqdl.get(i).getChoices().get(j).getChoice()));
                        hfc4.setCellStyle(style);
                        HSSFCell hfc5 = hr1.createCell(5);
                        hfc5.setCellValue(new HSSFRichTextString(qqdl.get(i).getChoices().get(j).getScore()));
                        hfc5.setCellStyle(style);
                        HSSFCell hfc6 = hr1.createCell(6);
                        hfc6.setCellValue(new HSSFRichTextString(qqdl.get(i).getChoices().get(j).getChoiceZb()));
                        hfc6.setCellStyle(style);
                    }
                    ss = ss + a;
                } else {
                    HSSFRow r = sheet1.createRow(ss);
                    HSSFCell hc = r.createCell(0);
                    hc.setCellValue(qqdl.get(i).getNo());
                    hc.setCellStyle(style);
                    HSSFCell hc1 = r.createCell(1);
                    hc1.setCellStyle(style);
                    hc1.setCellValue("非选择题");
                    HSSFCell hc2 = r.createCell(2);
                    hc2.setCellStyle(style2);
                    hc2.setCellValue(qqdl.get(i).getContent());
                    HSSFCell hc3 = r.createCell(3);
                    hc3.setCellStyle(style);
                    hc3.setCellValue(qqdl.get(i).getScore());
                    HSSFCell hc4 = r.createCell(4);
                    hc4.setCellValue("-");
                    hc4.setCellStyle(style);
                    HSSFCell hc5 = r.createCell(5);
                    hc5.setCellValue("-");
                    hc5.setCellStyle(style);
                    HSSFCell hc6 = r.createCell(6);
                    hc6.setCellValue("-");
                    hc6.setCellStyle(style);
                    HSSFCell hc7 = r.createCell(7);
                    hc7.setCellValue(qqdl.get(i).getAvgScore());
                    hc7.setCellStyle(style);
                    ss = ss + 1;
                }
            }
        }
        HSSFSheet sheet2 = workbook.createSheet("未提交人名单");
        sheet2.setColumnWidth(1, (int) ((50 + 0.72) * 128));
        sheet2.setColumnWidth(2, (int) ((50 + 0.72) * 128));
        sheet2.setColumnWidth(3, (int) ((50 + 0.72) * 128));
        String[] s2 = new String[]{"学生姓名", "班级名称", "所属专业", "所属院系", "班主任"};
        HSSFCellStyle hfs = workbook.createCellStyle();
        hfs.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        hfs.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        hfs.setBorderBottom(HSSFCellStyle.BORDER_THIN);// 下边框
        hfs.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
        hfs.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
        hfs.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
        HSSFRow row = sheet2.createRow(0);
        for (int k = 0; k < s2.length; k++) {
            HSSFCell hf = row.createCell(k);
            hf.setCellValue(s2[k]);
            hf.setCellStyle(hfs);
        }
        if (null != stuInfo && 0 < stuInfo.size()) {
            for (int k = 1; k < stuInfo.size() + 1; k++) {
                HSSFRow rowf = sheet2.createRow(k);
                HSSFCell hf0 = rowf.createCell(0);
                hf0.setCellValue(stuInfo.get(k - 1).getStuName());
                hf0.setCellStyle(style);
                HSSFCell hf1 = rowf.createCell(1);
                hf1.setCellValue(stuInfo.get(k - 1).getClassesName());
                hf1.setCellStyle(style);
                HSSFCell hf2 = rowf.createCell(2);
                hf2.setCellValue(stuInfo.get(k - 1).getProfName());
                hf2.setCellStyle(style);
                HSSFCell hf3 = rowf.createCell(3);
                hf3.setCellValue(stuInfo.get(k - 1).getCollegeName());
                hf3.setCellStyle(style);
                HSSFCell hf4 = rowf.createCell(4);
                hf4.setCellValue(stuInfo.get(k - 1).getTeacherName());
                hf4.setCellStyle(style);
            }
        }
        HSSFSheet sheet3 = workbook.createSheet("问卷评语");
        HSSFRow row3 = sheet3.createRow(0);
        String[] s3 = new String[]{"学生姓名", "班级类型", "班级名称", "所属专业", "所属院系", "评语"};
        sheet3.setColumnWidth(2, (int) ((50 + 0.72) * 128));
        sheet3.setColumnWidth(3, (int) ((50 + 0.72) * 128));
        sheet3.setColumnWidth(4, (int) ((50 + 0.72) * 128));
        sheet3.setColumnWidth(5, (int) ((50 + 0.72) * 128));
        for (int k = 0; k < s3.length; k++) {
            HSSFCell hf = row3.createCell(k);
            hf.setCellValue(s3[k]);
            hf.setCellStyle(hfs);
        }
        if (null != comments && 0 < comments.size()) {
            for (int k = 1; k < comments.size() + 1; k++) {
                HSSFRow rowf = sheet3.createRow(k);
                HSSFCell hf0 = rowf.createCell(0);
                hf0.setCellValue(comments.get(k - 1).getStuName());
                hf0.setCellStyle(style);
                HSSFCell hf1 = rowf.createCell(1);
                if (comments.get(k - 1).getClassType() == 10) {
                    hf1.setCellValue("班课");
                    HSSFCell hf2 = rowf.createCell(2);
                    hf2.setCellValue(comments.get(k - 1).getTeachingClassName());
                    hf2.setCellStyle(style);
                } else if (comments.get(k - 1).getClassType() == 20) {
                    hf1.setCellValue("行政班");
                    HSSFCell hf2 = rowf.createCell(2);
                    hf2.setCellValue(comments.get(k - 1).getClassesName());
                    hf2.setCellStyle(style);
                }
                hf1.setCellStyle(style);
                HSSFCell hf3 = rowf.createCell(3);
                hf3.setCellValue(comments.get(k - 1).getProfName());
                hf3.setCellStyle(style);
                HSSFCell hf4 = rowf.createCell(4);
                hf4.setCellValue(comments.get(k - 1).getCollegeName());
                hf4.setCellStyle(style);
                HSSFCell hf5 = rowf.createCell(5);
                hf5.setCellValue(comments.get(k - 1).getComment());
                hf5.setCellStyle(style);
            }
        }
        IODTO io = null;
        try {
            workbook.write(ba);
            io = ioUtil.upload(qdd.getQuestionnaireName() + ".xls", ba.toByteArray());
        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            if (ba != null) {
                try {
                    ba.close();
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }
        return io.getFileUrl();
    }
}
