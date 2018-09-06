package com.aizhixin.cloud.dd.questionnaire.serviceV2;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.questionnaire.JdbcTemplate.QuestionnaireStatisticsJdbc;
import com.aizhixin.cloud.dd.questionnaire.entity.Questionnaire;
import com.aizhixin.cloud.dd.questionnaire.entity.QuestionnaireAssgin;
import com.aizhixin.cloud.dd.questionnaire.repository.QuestionnaireAssginRepository;
import com.aizhixin.cloud.dd.questionnaire.repository.QuestionnaireRepository;
import com.aizhixin.cloud.dd.questionnaire.thread.QuestionnaireStatisticsThread;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.IODTO;
import com.aizhixin.cloud.dd.rollcall.utils.IOUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class QuestionnaireStatisticsService {
    @Autowired
    private QuestionnaireRepository questionnaireRepository;
    @Autowired
    private QuestionnaireStatisticsJdbc statisticsJdbc;
    @Autowired
    private IOUtil ioUtil;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;
    @Autowired
    private QuestionnaireAssginRepository questionnaireAssginRepository;
    @Autowired
    private RedisTemplate redisTemplate;

    public Map<String, String> getResult(Long quesId, Long userId) {
        if (quesId == null) {
            Map<String, String> redisData = new HashMap<>();
            redisData.put(ApiReturnConstants.RESULT, "30");
            redisData.put(ApiReturnConstants.DATA, "问卷ID不能为空!");
            return redisData;
        }
        String key = "exportSQS_" + quesId + "_" + userId;
        Map<String, String> redisData = (Map<String, String>) redisTemplate.opsForValue().get(key);
        return redisData;
    }

    /**
     * 学生评教统计Excel
     *
     * @param quesId
     * @return
     */
    public Map<String, String> exportStuQuestionnaireStatistics(Long quesId, Long userId, String fileName) {
        if (quesId == null) {
            Map<String, String> redisData = new HashMap<>();
            redisData.put(ApiReturnConstants.RESULT, "30");
            redisData.put(ApiReturnConstants.DATA, "问卷ID不能为空!");
            return redisData;
        }
        String key = "exportSQS_" + quesId + "_" + userId;
        Map<String, String> redisData = (Map<String, String>) redisTemplate.opsForValue().get(key);
        if (redisData != null && redisData.get(ApiReturnConstants.RESULT).equals("10")) {
            return redisData;
        }

        Questionnaire questionnaire = questionnaireRepository.findOne(quesId);
        if (questionnaire != null) {
            redisData = new HashMap<>();
            redisData.put(ApiReturnConstants.RESULT, "10");
            redisData.put(ApiReturnConstants.DATA, "进行中");
            redisTemplate.opsForValue().set(key, redisData, 1, TimeUnit.DAYS);
            QuestionnaireStatisticsThread thread = new QuestionnaireStatisticsThread(this, key, questionnaire);
            thread.run();
            return redisData;
        } else {
            redisData = new HashMap<>();
            redisData.put(ApiReturnConstants.RESULT, "30");
            redisData.put(ApiReturnConstants.DATA, "问卷信息不存在!");
            return redisData;
        }
    }

    public void generateStuExcel(String key, Questionnaire questionnaire) {
        try {
            //初始化Excel
            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFCellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            //1.按课程统计
            generationSheet1(wb, questionnaire);
            //2.各课程汇总统计
            generationSheet2(wb, cellStyle, questionnaire);
            //3.按课程统计参评率
            generationSheet3(wb, cellStyle, questionnaire);
            //4.按院系统计的明细表总表
            generationSheet4(wb, cellStyle, questionnaire);
            //5.各院系汇总统计
            generationSheet5(wb, cellStyle, questionnaire);
            //6.按行政班统计参评率
            generationSheet6(wb, cellStyle, questionnaire);
            //7.按学院统计参评率
            generationSheet7(wb, cellStyle, questionnaire);
            ByteArrayOutputStream os = null;
            try {
                os = new ByteArrayOutputStream();
                wb.write(os);
                byte[] data = os.toByteArray();
                String url = uploadIo(data, "学生评教统计.xlsx");
                Map<String, String> redisData = new HashMap<>();
                redisData.put(ApiReturnConstants.RESULT, "20");
                redisData.put(ApiReturnConstants.DATA, url);
                redisTemplate.opsForValue().set(key, redisData, 1, TimeUnit.DAYS);
            } catch (Exception ex) {
                ex.printStackTrace();
                Map<String, String> redisData = new HashMap<>();
                redisData.put(ApiReturnConstants.RESULT, "30");
                redisData.put(ApiReturnConstants.DATA, "导出错误");
                redisTemplate.opsForValue().set(key, redisData, 1, TimeUnit.DAYS);
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Map<String, String> redisData = new HashMap<>();
            redisData.put(ApiReturnConstants.RESULT, "30");
            redisData.put(ApiReturnConstants.DATA, "生成Excel错误");
            redisTemplate.opsForValue().set(key, redisData, 1, TimeUnit.DAYS);
        }
    }

    private void generationSheet1(XSSFWorkbook wb, Questionnaire questionnaire) {
        List<Map<String, Object>> list = statisticsJdbc.getCourseByQuesId(questionnaire.getId());
        if (list == null || list.size() < 1) {
            return;
        }
        XSSFSheet sheet = wb.createSheet("按课程统计");
        XSSFRow titleRow = sheet.createRow(0);
        XSSFCell c0 = titleRow.createCell(0);
        c0.setCellValue("序号");
        XSSFCell c1 = titleRow.createCell(1);
        c1.setCellValue("课程名称");
        XSSFCell c2 = titleRow.createCell(2);
        c2.setCellValue("授课教师");
        XSSFCell c3 = titleRow.createCell(3);
        c3.setCellValue("班课名称");
        XSSFCell c4 = titleRow.createCell(4);
        c4.setCellValue("评教人数");
        int questionsCount = statisticsJdbc.getQuestionCountByQuesId(questionnaire.getId());
        for (int i = 0; i < questionsCount; i++) {
            XSSFCell c = titleRow.createCell(5 + i);
            c.setCellValue((i + 1) + "题");
        }
        XSSFCell ct = titleRow.createCell(5 + questionsCount);
        ct.setCellValue("平均分合计");
        int size = list.size();
        for (int i = 0; i < size; i++) {
            Map<String, Object> item = list.get(i);
            XSSFRow row = sheet.createRow(i + 1);
            XSSFCell rc0 = row.createCell(0);
            rc0.setCellValue(i + 1);
            XSSFCell rc1 = row.createCell(1);
            if (item.get("COURSE_NAME") != null) {
                rc1.setCellValue(item.get("COURSE_NAME").toString());
            } else {
                rc1.setCellValue("--");
            }
            XSSFCell rc2 = row.createCell(2);
            if (item.get("TEACHER_NAME") != null) {
                rc2.setCellValue(item.get("TEACHER_NAME").toString());
            } else {
                rc2.setCellValue("--");
            }
            XSSFCell rc3 = row.createCell(3);
            if (item.get("TEACHING_CLASS_NAME") != null) {
                rc3.setCellValue(item.get("TEACHING_CLASS_NAME").toString());
            } else {
                rc3.setCellValue("--");
            }
            XSSFCell rc4 = row.createCell(4);
            if (item.get("stucount") != null) {
                rc4.setCellValue(Integer.parseInt(item.get("stucount").toString()));
            } else {
                rc4.setCellValue(0);
            }
            List<Map<String, Object>> avgList = statisticsJdbc.getCourseQuestionAvgByAssginIdAndQuesId(item.get("ID").toString(), questionnaire.getId());
            int avgCount = avgList.size();
            BigDecimal totoAvg = new BigDecimal(0);
            for (int j = 0; j < avgCount; j++) {
                Map<String, Object> avgItem = avgList.get(j);
                XSSFCell rc = row.createCell(5 + j);
                if (avgItem.get("avgscore") != null) {
                    BigDecimal avg = new BigDecimal(avgItem.get("avgscore").toString());
                    rc.setCellValue(avg.doubleValue());
                    totoAvg = totoAvg.add(avg);
                } else {
                    rc.setCellValue(0);
                }
            }
            XSSFCell ctavg = row.createCell(5 + questionsCount);
            ctavg.setCellValue(totoAvg.doubleValue());
        }
    }

    private void generationSheet2(XSSFWorkbook wb, XSSFCellStyle cellStyle, Questionnaire questionnaire) {
        List<Map<String, Object>> list = statisticsJdbc.getCourseScoreByQuesId(questionnaire.getId());
        if (list == null || list.size() < 1) {
            return;
        }
        XSSFSheet sheet = wb.createSheet("各课程汇总统计");
        XSSFRow titleRow = sheet.createRow(0);
        XSSFCell titleRowCell0 = titleRow.createCell(0);
        titleRowCell0.setCellValue(questionnaire.getName() + "评教中各门课程教师得分情况统计");
        titleRowCell0.setCellStyle(cellStyle);

        XSSFRow subTitleRow = sheet.createRow(1);
        XSSFCell stc0 = subTitleRow.createCell(0);
        stc0.setCellValue("课程名称");
        XSSFCell stc1 = subTitleRow.createCell(1);
        stc1.setCellValue("被评教师人数");
        XSSFCell stc2 = subTitleRow.createCell(2);
        stc2.setCellValue("平均分");
        XSSFCell stc3 = subTitleRow.createCell(3);
        stc3.setCellValue("最高分");
        XSSFCell stc4 = subTitleRow.createCell(4);
        stc4.setCellValue("最低分");
        XSSFCell stc5 = subTitleRow.createCell(5);
        stc5.setCellValue("分数<80（人数）");
        XSSFCell stc6 = subTitleRow.createCell(6);
        stc6.setCellValue("分数<80（占比）");
        XSSFCell stc7 = subTitleRow.createCell(7);
        stc7.setCellValue("80≤分数<90（人数）");
        XSSFCell stc8 = subTitleRow.createCell(8);
        stc8.setCellValue("80≤分数<90（占比）");
        XSSFCell stc9 = subTitleRow.createCell(9);
        stc9.setCellValue("分数≥90（人数）");
        XSSFCell stc10 = subTitleRow.createCell(10);
        stc10.setCellValue("分数≥90（占比）");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));

        int count = list.size();
        BigDecimal totalAvg = new BigDecimal(0);
        BigDecimal maxScore = new BigDecimal(0);
        BigDecimal minScore = new BigDecimal(999999999);
        BigDecimal totalStu = new BigDecimal(0);
        BigDecimal totalx80 = new BigDecimal(0);
        BigDecimal total8090 = new BigDecimal(0);
        BigDecimal total90 = new BigDecimal(0);
        for (int i = 0; i < count; i++) {
            Map<String, Object> item = list.get(i);
            XSSFRow row = sheet.createRow(2 + i);
            BigDecimal stucount = new BigDecimal(0);
            if (item.get("stucount") != null) {
                stucount = new BigDecimal(item.get("stucount").toString());
                totalStu = totalStu.add(stucount);
            }
            XSSFCell cell0 = row.createCell(0);
            if (item.get("COURSE_NAME") != null) {
                cell0.setCellValue(item.get("COURSE_NAME").toString());
            } else {
                cell0.setCellValue("--");
            }
            XSSFCell cell1 = row.createCell(1);
            if (item.get("teachercount") != null) {
                cell1.setCellValue(new BigDecimal(item.get("teachercount").toString()).intValue());
            } else {
                cell1.setCellValue(0);
            }
            XSSFCell cell2 = row.createCell(2);
            if (item.get("avgscore") != null) {
                BigDecimal c = new BigDecimal(item.get("avgscore").toString());
                cell2.setCellValue(c.doubleValue());
                totalAvg = totalAvg.add(c);
            } else {
                cell2.setCellValue(0);
            }
            XSSFCell cell3 = row.createCell(3);
            if (item.get("maxscore") != null) {
                BigDecimal c = new BigDecimal(item.get("maxscore").toString());
                cell3.setCellValue(c.doubleValue());
                if (c.doubleValue() > maxScore.doubleValue()) {
                    maxScore = c;
                }
            } else {
                cell3.setCellValue(0);
            }
            XSSFCell cell4 = row.createCell(4);
            if (item.get("minscore") != null) {
                BigDecimal c = new BigDecimal(item.get("minscore").toString());
                cell4.setCellValue(c.doubleValue());
                if (c.doubleValue() < minScore.doubleValue()) {
                    minScore = c;
                }
            } else {
                cell4.setCellValue(0);
                minScore = new BigDecimal(0);
            }
            XSSFCell cell5 = row.createCell(5);
            XSSFCell cell6 = row.createCell(6);
            if (item.get("countx80") != null) {
                BigDecimal c = new BigDecimal(item.get("countx80").toString());
                cell5.setCellValue(c.intValue());
                totalx80 = totalx80.add(c);
                if (stucount.intValue() > 0 && c.doubleValue() > 0) {
                    cell6.setCellValue(c.divide(stucount, 2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } else {
                    cell6.setCellValue(0);
                }
            } else {
                cell5.setCellValue(0);
                cell6.setCellValue(0);
            }
            XSSFCell cell7 = row.createCell(7);
            XSSFCell cell8 = row.createCell(8);
            if (item.get("count8090") != null) {
                BigDecimal c = new BigDecimal(item.get("count8090").toString());
                cell7.setCellValue(c.intValue());
                total8090 = total8090.add(c);
                if (stucount.intValue() > 0 && c.doubleValue() > 0) {
                    cell8.setCellValue(c.divide(stucount, 2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } else {
                    cell8.setCellValue(0);
                }
            } else {
                cell7.setCellValue(0);
                cell8.setCellValue(0);
            }
            XSSFCell cell9 = row.createCell(9);
            XSSFCell cell10 = row.createCell(10);
            if (item.get("count90") != null) {
                BigDecimal c = new BigDecimal(item.get("count90").toString());
                cell9.setCellValue(c.intValue());
                total90 = total90.add(c);
                if (stucount.intValue() > 0 && c.doubleValue() > 0) {
                    cell10.setCellValue(c.divide(stucount, 2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } else {
                    cell10.setCellValue(0);
                }
            } else {
                cell9.setCellValue(0);
                cell10.setCellValue(0);
            }
        }
        XSSFRow footRow = sheet.createRow(2 + count);
        XSSFCell frc0 = footRow.createCell(0);
        frc0.setCellValue("全课程总计");
        BigDecimal rowCount = new BigDecimal(count);
        XSSFCell frc1 = footRow.createCell(2);
        if (totalAvg.doubleValue() > 0 && rowCount.intValue() > 0) {
            frc1.setCellValue(totalAvg.divide(rowCount, 2, BigDecimal.ROUND_HALF_UP).doubleValue());
        } else {
            frc1.setCellValue(0);
        }
        XSSFCell frc2 = footRow.createCell(3);
        frc2.setCellValue(maxScore.doubleValue());
        XSSFCell frc3 = footRow.createCell(4);
        frc3.setCellValue(minScore.doubleValue());
        XSSFCell frc4 = footRow.createCell(5);
        frc4.setCellValue(totalx80.intValue());
        XSSFCell frc5 = footRow.createCell(6);
        if (totalStu.intValue() > 0 && totalx80.intValue() > 0) {
            frc5.setCellValue(totalx80.divide(totalStu, 2, BigDecimal.ROUND_HALF_UP).doubleValue());
        } else {
            frc5.setCellValue(0);
        }
        XSSFCell frc6 = footRow.createCell(7);
        frc6.setCellValue(total8090.intValue());
        XSSFCell frc7 = footRow.createCell(8);
        if (totalStu.intValue() > 0 && total8090.intValue() > 0) {
            frc7.setCellValue(total8090.divide(totalStu, 2, BigDecimal.ROUND_HALF_UP).doubleValue());
        } else {
            frc7.setCellValue(0);
        }
        XSSFCell frc8 = footRow.createCell(9);
        frc8.setCellValue(total90.intValue());
        XSSFCell frc9 = footRow.createCell(10);
        if (totalStu.intValue() > 0 && total90.intValue() > 0) {
            frc9.setCellValue(total90.divide(totalStu, 2, BigDecimal.ROUND_HALF_UP).doubleValue());
        } else {
            frc9.setCellValue(0);
        }
    }

    private void generationSheet3(XSSFWorkbook wb, XSSFCellStyle cellStyle, Questionnaire questionnaire) {
        List<Map<String, Object>> list = statisticsJdbc.getCourseCommitByQuest(questionnaire.getId());
        if (list == null || list.size() < 1) {
            return;
        }
        XSSFSheet sheet = wb.createSheet("按课程统计参评率");
        XSSFRow titleRow = sheet.createRow(0);
        XSSFCell titleRowCell0 = titleRow.createCell(0);
        titleRowCell0.setCellValue("按课程统计参评率");
        titleRowCell0.setCellStyle(cellStyle);

        XSSFRow subTitleRow = sheet.createRow(1);
        XSSFCell stc0 = subTitleRow.createCell(0);
        stc0.setCellValue("课程名称");
        XSSFCell stc1 = subTitleRow.createCell(1);
        stc1.setCellValue("需评价人数");
        XSSFCell stc2 = subTitleRow.createCell(2);
        stc2.setCellValue("已评价过人数");
        XSSFCell stc3 = subTitleRow.createCell(3);
        stc3.setCellValue("未评价人数");
        XSSFCell stc4 = subTitleRow.createCell(4);
        stc4.setCellValue("参评率");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

        BigDecimal totalStu = new BigDecimal(0);
        BigDecimal totalCommit = new BigDecimal(0);
        int count = list.size();
        for (int i = 0; i < count; i++) {
            Map<String, Object> item = list.get(i);
            XSSFRow row = sheet.createRow(2 + i);
            BigDecimal stuCount = new BigDecimal(0);
            BigDecimal commitCount = new BigDecimal(0);
            XSSFCell cell0 = row.createCell(0);
            if (item.get("COURSE_NAME") != null) {
                cell0.setCellValue(item.get("COURSE_NAME").toString());
            } else {
                cell0.setCellValue("--");
            }
            XSSFCell cell1 = row.createCell(1);
            if (item.get("stucount") != null) {
                stuCount = new BigDecimal(item.get("stucount").toString());
                totalStu = totalStu.add(stuCount);
                cell1.setCellValue(stuCount.intValue());
            } else {
                cell1.setCellValue(0);
            }
            XSSFCell cell2 = row.createCell(2);
            if (item.get("commitcount") != null) {
                commitCount = new BigDecimal(item.get("commitcount").toString());
                totalCommit = totalCommit.add(commitCount);
                cell2.setCellValue(commitCount.intValue());
            } else {
                cell2.setCellValue(0);
            }
            XSSFCell cell3 = row.createCell(3);
            cell3.setCellValue(stuCount.subtract(commitCount).intValue());
            XSSFCell cell4 = row.createCell(4);
            if (commitCount.intValue() > 0 && stuCount.intValue() > 0) {
                cell4.setCellValue(commitCount.divide(stuCount, 2, BigDecimal.ROUND_HALF_UP).doubleValue());
            } else {
                cell4.setCellValue(0);
            }
        }
        XSSFRow footRow = sheet.createRow(2 + count);
        XSSFCell frc0 = footRow.createCell(0);
        frc0.setCellValue("总计");
        XSSFCell frc1 = footRow.createCell(1);
        frc1.setCellValue(totalStu.intValue());
        XSSFCell frc2 = footRow.createCell(2);
        frc2.setCellValue(totalCommit.intValue());
        XSSFCell frc3 = footRow.createCell(3);
        frc3.setCellValue(totalStu.subtract(totalCommit).intValue());
        XSSFCell frc4 = footRow.createCell(4);
        if (totalCommit.intValue() > 0 && totalStu.intValue() > 0) {
            frc4.setCellValue(totalCommit.divide(totalStu, 2, BigDecimal.ROUND_HALF_UP).doubleValue());
        } else {
            frc4.setCellValue(0);
        }
    }

    private void generationSheet4(XSSFWorkbook wb, XSSFCellStyle cellStyle, Questionnaire questionnaire) {
        List<Map<String, Object>> list = statisticsJdbc.getCollgeByQuesId(questionnaire.getId());
        if (list == null || list.size() < 1) {
            return;
        }
        XSSFSheet sheet = wb.createSheet("各院系统计明细表");
        XSSFRow titleRow = sheet.createRow(0);
        XSSFCell titleRowCell0 = titleRow.createCell(0);
        titleRowCell0.setCellValue(questionnaire.getName() + "学生评教结果统计表");
        titleRowCell0.setCellStyle(cellStyle);

        XSSFRow subTitleRow = sheet.createRow(1);
        XSSFCell stc0 = subTitleRow.createCell(0);
        stc0.setCellValue("序号");
        XSSFCell stc1 = subTitleRow.createCell(1);
        stc1.setCellValue("教师所\n在系部");
        XSSFCell stc2 = subTitleRow.createCell(2);
        stc2.setCellValue("教师姓名");
        XSSFCell stc3 = subTitleRow.createCell(3);
        stc3.setCellValue("样本量");
        int questionsCount = statisticsJdbc.getQuestionCountByQuesId(questionnaire.getId());
        for (int i = 0; i < questionsCount; i++) {
            XSSFCell c = subTitleRow.createCell(4 + i);
            c.setCellValue((i + 1) + "题");
        }
        XSSFCell ct1 = subTitleRow.createCell(4 + questionsCount);
        ct1.setCellValue("总平均分");
        XSSFCell ct2 = subTitleRow.createCell(5 + questionsCount);
        ct2.setCellValue("排名");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5 + questionsCount));

        int size = list.size();
        for (int i = 0; i < size; i++) {
            Map<String, Object> item = list.get(i);
            List<Map<String, Object>> tlist = statisticsJdbc.getCollgeQueByTeacherName(questionnaire.getId(), item.get("TEACHER_NAME").toString());
            BigDecimal totalAvg = new BigDecimal(0);
            for (Map<String, Object> titem : tlist) {
                BigDecimal avgscore = new BigDecimal(titem.get("avgscore").toString());
                totalAvg = totalAvg.add(avgscore);
            }
            item.put("ques", tlist);
            item.put("totalAvg", totalAvg.doubleValue());
        }
        Collections.sort(list, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                double oo1 = Double.parseDouble(o1.get("totalAvg").toString());
                double oo2 = Double.parseDouble(o2.get("totalAvg").toString());
                if (oo1 < oo2) {
                    return 1;
                } else if (oo1 == oo2) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });
        for (int i = 0; i < size; i++) {
            Map<String, Object> item = list.get(i);
            XSSFRow row = sheet.createRow(2 + i);
            XSSFCell c0 = row.createCell(0);
            c0.setCellValue(i + 1);
            XSSFCell c1 = row.createCell(1);
            c1.setCellValue(item.get("COLLEGE_NAME").toString());
            XSSFCell c2 = row.createCell(2);
            c2.setCellValue(item.get("TEACHER_NAME").toString());
            XSSFCell c3 = row.createCell(3);
            BigDecimal stucount = new BigDecimal(item.get("stucount").toString());
            c3.setCellValue(stucount.intValue());
            List<Map<String, Object>> tlist = (List<Map<String, Object>>) item.get("ques");
            int count = tlist.size();
            for (int index = 0; index < count; index++) {
                Map<String, Object> titem = tlist.get(index);
                XSSFCell cc = row.createCell(4 + index);
                if (StringUtils.isNotEmpty(titem.get("avgscore").toString())) {
                    BigDecimal avgscore = new BigDecimal(titem.get("avgscore").toString());
                    cc.setCellValue(avgscore.doubleValue());
                } else {
                    cc.setCellValue(0);
                }
            }
            XSSFCell cc1 = row.createCell(4 + questionsCount);
            BigDecimal totalAvg = new BigDecimal(item.get("totalAvg").toString());
            cc1.setCellValue(totalAvg.doubleValue());
            XSSFCell cc2 = row.createCell(5 + questionsCount);
            cc2.setCellValue(i + 1);
        }
    }

    private void generationSheet5(XSSFWorkbook wb, XSSFCellStyle cellStyle, Questionnaire questionnaire) {
        XSSFSheet sheet = wb.createSheet("各院系汇总统计");
        XSSFRow titleRow = sheet.createRow(0);
        XSSFCell titleRowCell0 = titleRow.createCell(0);
        titleRowCell0.setCellValue(questionnaire.getName() + "各教学单位教师得分情况统计表");
        titleRowCell0.setCellStyle(cellStyle);

        XSSFRow subTitleRow = sheet.createRow(1);
        XSSFCell stc0 = subTitleRow.createCell(0);
        stc0.setCellValue("院系名称");
        XSSFCell stc1 = subTitleRow.createCell(1);
        stc1.setCellValue("被评教师人数");
        XSSFCell stc2 = subTitleRow.createCell(2);
        stc2.setCellValue("平均分");
        XSSFCell stc3 = subTitleRow.createCell(3);
        stc3.setCellValue("最高分");
        XSSFCell stc4 = subTitleRow.createCell(4);
        stc4.setCellValue("最低分");
        XSSFCell stc5 = subTitleRow.createCell(5);
        stc5.setCellValue("分数<80（人数）");
        XSSFCell stc6 = subTitleRow.createCell(6);
        stc6.setCellValue("分数<80（占比）");
        XSSFCell stc7 = subTitleRow.createCell(7);
        stc7.setCellValue("80≤分数<90（人数）");
        XSSFCell stc8 = subTitleRow.createCell(8);
        stc8.setCellValue("80≤分数<90（占比）");
        XSSFCell stc9 = subTitleRow.createCell(9);
        stc9.setCellValue("分数≥90（人数）");
        XSSFCell stc10 = subTitleRow.createCell(10);
        stc10.setCellValue("分数≥90（占比）");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));

        List<Map<String, Object>> list = statisticsJdbc.getCollgeScoreByQuesId(questionnaire.getId());
        int count = list.size();
        BigDecimal totalT = new BigDecimal(0);
        BigDecimal totalAvg = new BigDecimal(0);
        BigDecimal maxScore = new BigDecimal(0);
        BigDecimal minScore = new BigDecimal(999999999);
        BigDecimal totalStu = new BigDecimal(0);
        BigDecimal totalx80 = new BigDecimal(0);
        BigDecimal total8090 = new BigDecimal(0);
        BigDecimal total90 = new BigDecimal(0);
        for (int i = 0; i < count; i++) {
            Map<String, Object> item = list.get(i);
            XSSFRow row = sheet.createRow(2 + i);
            BigDecimal stucount = new BigDecimal(0);
            if (item.get("stucount") != null) {
                stucount = new BigDecimal(item.get("stucount").toString());
                totalStu = totalStu.add(stucount);
            }
            XSSFCell cell0 = row.createCell(0);
            if (item.get("COLLEGE_NAME") != null) {
                cell0.setCellValue(item.get("COLLEGE_NAME").toString());
            } else {
                cell0.setCellValue("--");
            }
            XSSFCell cell1 = row.createCell(1);
            if (item.get("teachercount") != null) {
                BigDecimal t = new BigDecimal(item.get("teachercount").toString());
                totalT = totalT.add(t);
                cell1.setCellValue(t.intValue());
            } else {
                cell1.setCellValue(0);
            }
            XSSFCell cell2 = row.createCell(2);
            if (item.get("avgscore") != null) {
                BigDecimal c = new BigDecimal(item.get("avgscore").toString());
                cell2.setCellValue(c.doubleValue());
                totalAvg = totalAvg.add(c);
            } else {
                cell2.setCellValue(0);
            }
            XSSFCell cell3 = row.createCell(3);
            if (item.get("maxscore") != null) {
                BigDecimal c = new BigDecimal(item.get("maxscore").toString());
                cell3.setCellValue(c.doubleValue());
                if (c.doubleValue() > maxScore.doubleValue()) {
                    maxScore = c;
                }
            } else {
                cell3.setCellValue(0);
            }
            XSSFCell cell4 = row.createCell(4);
            if (item.get("minscore") != null) {
                BigDecimal c = new BigDecimal(item.get("minscore").toString());
                cell4.setCellValue(c.doubleValue());
                if (c.doubleValue() < minScore.doubleValue()) {
                    minScore = c;
                }
            } else {
                cell4.setCellValue(0);
                minScore = new BigDecimal(0);
            }
            XSSFCell cell5 = row.createCell(5);
            XSSFCell cell6 = row.createCell(6);
            if (item.get("countx80") != null) {
                BigDecimal c = new BigDecimal(item.get("countx80").toString());
                cell5.setCellValue(c.intValue());
                totalx80 = totalx80.add(c);

                if (stucount.intValue() > 0 && c.doubleValue() > 0) {
                    cell6.setCellValue(c.divide(stucount, 2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } else {
                    cell6.setCellValue(0);
                }
            } else {
                cell5.setCellValue(0);
                cell6.setCellValue(0);
            }
            XSSFCell cell7 = row.createCell(7);
            XSSFCell cell8 = row.createCell(8);
            if (item.get("count8090") != null) {
                BigDecimal c = new BigDecimal(item.get("count8090").toString());
                cell7.setCellValue(c.intValue());
                total8090 = total8090.add(c);
                if (c.doubleValue() > 0) {
                    cell8.setCellValue(c.divide(stucount, 2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } else {
                    cell8.setCellValue(0);
                }
            } else {
                cell7.setCellValue(0);
                cell8.setCellValue(0);
            }
            XSSFCell cell9 = row.createCell(9);
            XSSFCell cell10 = row.createCell(10);
            if (item.get("count90") != null) {
                BigDecimal c = new BigDecimal(item.get("count90").toString());
                cell9.setCellValue(c.intValue());
                total90 = total90.add(c);
                if (c.doubleValue() > 0) {
                    cell10.setCellValue(c.divide(stucount, 2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } else {
                    cell10.setCellValue(0);
                }
            } else {
                cell9.setCellValue(0);
                cell10.setCellValue(0);
            }
        }
        XSSFRow footRow = sheet.createRow(2 + count);
        XSSFCell frc0 = footRow.createCell(0);
        frc0.setCellValue("全院总计");
        BigDecimal rowCount = new BigDecimal(count);
        XSSFCell frc01 = footRow.createCell(1);
        frc01.setCellValue(totalT.intValue());
        XSSFCell frc1 = footRow.createCell(2);
        if (totalAvg.doubleValue() > 0 && rowCount.intValue() > 0) {
            frc1.setCellValue(totalAvg.divide(rowCount, 2, BigDecimal.ROUND_HALF_UP).doubleValue());
        } else {
            frc1.setCellValue(0);
        }
        XSSFCell frc2 = footRow.createCell(3);
        frc2.setCellValue(maxScore.doubleValue());
        XSSFCell frc3 = footRow.createCell(4);
        frc3.setCellValue(minScore.doubleValue());
        XSSFCell frc4 = footRow.createCell(5);
        frc4.setCellValue(totalx80.intValue());
        XSSFCell frc5 = footRow.createCell(6);
        if (totalStu.intValue() > 0 && totalx80.intValue() > 0) {
            frc5.setCellValue(totalx80.divide(totalStu, 2, BigDecimal.ROUND_HALF_UP).doubleValue());
        } else {
            frc5.setCellValue(0);
        }
        XSSFCell frc6 = footRow.createCell(7);
        frc6.setCellValue(total8090.intValue());
        XSSFCell frc7 = footRow.createCell(8);
        if (totalStu.intValue() > 0 && total8090.intValue() > 0) {
            frc7.setCellValue(total8090.divide(totalStu, 2, BigDecimal.ROUND_HALF_UP).doubleValue());
        } else {
            frc7.setCellValue(0);
        }
        XSSFCell frc8 = footRow.createCell(9);
        frc8.setCellValue(total90.intValue());
        XSSFCell frc9 = footRow.createCell(10);
        if (totalStu.intValue() > 0 && total90.intValue() > 0) {
            frc9.setCellValue(total90.divide(totalStu, 2, BigDecimal.ROUND_HALF_UP).doubleValue());
        } else {
            frc9.setCellValue(0);
        }
    }

    private void generationSheet6(XSSFWorkbook wb, XSSFCellStyle cellStyle, Questionnaire questionnaire) {
        XSSFSheet sheet = wb.createSheet("按行政班统计参评率");
        XSSFRow titleRow = sheet.createRow(0);
        XSSFCell titleRowCell0 = titleRow.createCell(0);
        titleRowCell0.setCellValue("按行政班统计参评率");
        titleRowCell0.setCellStyle(cellStyle);

        XSSFRow subTitleRow = sheet.createRow(1);
        XSSFCell stc0 = subTitleRow.createCell(0);
        stc0.setCellValue("行政班");
        XSSFCell stc1 = subTitleRow.createCell(1);
        stc1.setCellValue("需评价人数");
        XSSFCell stc2 = subTitleRow.createCell(2);
        stc2.setCellValue("已评价过人数");
        XSSFCell stc3 = subTitleRow.createCell(3);
        stc3.setCellValue("未评价人数");
        XSSFCell stc4 = subTitleRow.createCell(4);
        stc4.setCellValue("参评率");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
        List<Map<String, Object>> list = statisticsJdbc.getClassCommitByQuest(questionnaire.getId());
        BigDecimal totalStu = new BigDecimal(0);
        BigDecimal totalCommit = new BigDecimal(0);
        int count = list.size();
        for (int i = 0; i < count; i++) {
            Map<String, Object> item = list.get(i);
            XSSFRow row = sheet.createRow(2 + i);
            BigDecimal stuCount = new BigDecimal(0);
            BigDecimal commitCount = new BigDecimal(0);
            XSSFCell cell0 = row.createCell(0);
            if (item.get("CLASSES_NAME") != null) {
                cell0.setCellValue(item.get("CLASSES_NAME").toString());
            } else {
                cell0.setCellValue("--");
            }
            XSSFCell cell1 = row.createCell(1);
            if (item.get("stucount") != null) {
                stuCount = new BigDecimal(item.get("stucount").toString());
                totalStu = totalStu.add(stuCount);
                cell1.setCellValue(stuCount.intValue());
            } else {
                cell1.setCellValue(0);
            }
            XSSFCell cell2 = row.createCell(2);
            if (item.get("commitcount") != null) {
                commitCount = new BigDecimal(item.get("commitcount").toString());
                totalCommit = totalCommit.add(commitCount);
                cell2.setCellValue(commitCount.intValue());
            } else {
                cell2.setCellValue(0);
            }
            XSSFCell cell3 = row.createCell(3);
            cell3.setCellValue(stuCount.subtract(commitCount).intValue());
            XSSFCell cell4 = row.createCell(4);
            if (commitCount.intValue() > 0 && stuCount.intValue() > 0) {
                cell4.setCellValue(commitCount.divide(stuCount, 2, BigDecimal.ROUND_HALF_UP).doubleValue());
            } else {
                cell4.setCellValue(0);
            }
        }
        XSSFRow footRow = sheet.createRow(2 + count);
        XSSFCell frc0 = footRow.createCell(0);
        frc0.setCellValue("总计");
        XSSFCell frc1 = footRow.createCell(1);
        frc1.setCellValue(totalStu.intValue());
        XSSFCell frc2 = footRow.createCell(2);
        frc2.setCellValue(totalCommit.intValue());
        XSSFCell frc3 = footRow.createCell(3);
        frc3.setCellValue(totalStu.subtract(totalCommit).intValue());
        XSSFCell frc4 = footRow.createCell(4);
        if (totalCommit.intValue() > 0 && totalStu.intValue() > 0) {
            frc4.setCellValue(totalCommit.divide(totalStu, 2, BigDecimal.ROUND_HALF_UP).doubleValue());
        } else {
            frc4.setCellValue(0);
        }
    }

    private void generationSheet7(XSSFWorkbook wb, XSSFCellStyle cellStyle, Questionnaire questionnaire) {
        XSSFSheet sheet = wb.createSheet("按学院统计参评率");
        XSSFRow titleRow = sheet.createRow(0);
        XSSFCell titleRowCell0 = titleRow.createCell(0);
        titleRowCell0.setCellValue("按学院统计参评率");
        titleRowCell0.setCellStyle(cellStyle);

        XSSFRow subTitleRow = sheet.createRow(1);
        XSSFCell stc0 = subTitleRow.createCell(0);
        stc0.setCellValue("学院");
        XSSFCell stc1 = subTitleRow.createCell(1);
        stc1.setCellValue("需评价人数");
        XSSFCell stc2 = subTitleRow.createCell(2);
        stc2.setCellValue("已评价过人数");
        XSSFCell stc3 = subTitleRow.createCell(3);
        stc3.setCellValue("未评价人数");
        XSSFCell stc4 = subTitleRow.createCell(4);
        stc4.setCellValue("参评率");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
        List<Map<String, Object>> list = statisticsJdbc.getCollgeCommitByQuest(questionnaire.getId());
        BigDecimal totalStu = new BigDecimal(0);
        BigDecimal totalCommit = new BigDecimal(0);
        int count = list.size();
        for (int i = 0; i < count; i++) {
            Map<String, Object> item = list.get(i);
            XSSFRow row = sheet.createRow(2 + i);
            BigDecimal stuCount = new BigDecimal(0);
            BigDecimal commitCount = new BigDecimal(0);
            XSSFCell cell0 = row.createCell(0);
            if (item.get("COLLEGE_NAME") != null) {
                cell0.setCellValue(item.get("COLLEGE_NAME").toString());
            } else {
                cell0.setCellValue("--");
            }
            XSSFCell cell1 = row.createCell(1);
            if (item.get("stucount") != null) {
                stuCount = new BigDecimal(item.get("stucount").toString());
                totalStu = totalStu.add(stuCount);
                cell1.setCellValue(stuCount.intValue());
            } else {
                cell1.setCellValue(0);
            }
            XSSFCell cell2 = row.createCell(2);
            if (item.get("commitcount") != null) {
                commitCount = new BigDecimal(item.get("commitcount").toString());
                totalCommit = totalCommit.add(commitCount);
                cell2.setCellValue(commitCount.intValue());
            } else {
                cell2.setCellValue(0);
            }
            XSSFCell cell3 = row.createCell(3);
            cell3.setCellValue(stuCount.subtract(commitCount).intValue());
            XSSFCell cell4 = row.createCell(4);
            if (commitCount.intValue() > 0 && stuCount.intValue() > 0) {
                cell4.setCellValue(commitCount.divide(stuCount, 2, BigDecimal.ROUND_HALF_UP).doubleValue());
            } else {
                cell4.setCellValue(0);
            }
        }
        XSSFRow footRow = sheet.createRow(2 + count);
        XSSFCell frc0 = footRow.createCell(0);
        frc0.setCellValue("总计");
        XSSFCell frc1 = footRow.createCell(1);
        frc1.setCellValue(totalStu.intValue());
        XSSFCell frc2 = footRow.createCell(2);
        frc2.setCellValue(totalCommit.intValue());
        XSSFCell frc3 = footRow.createCell(3);
        frc3.setCellValue(totalStu.subtract(totalCommit).intValue());
        XSSFCell frc4 = footRow.createCell(4);
        if (totalCommit.intValue() > 0 && totalStu.intValue() > 0) {
            frc4.setCellValue(totalCommit.divide(totalStu, 2, BigDecimal.ROUND_HALF_UP).doubleValue());
        } else {
            frc4.setCellValue(0);
        }
    }

    private String uploadIo(byte[] data, String fileName) {
        try {
            IODTO ioDTO = ioUtil.upload(fileName, data);
            String url = ioDTO.getFileUrl();
            return url;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void fixCollgeName() {
        try {
            List<QuestionnaireAssgin> list = questionnaireAssginRepository.findByCollegeNull();
            if (list != null && list.size() > 0) {
                for (QuestionnaireAssgin item : list) {
                    if (item.getTeachingClassId() != null) {
                        Map<String, Object> ct = null;
                        try {
                            ct = orgManagerRemoteService.getTeachingclassTeachers(item.getTeachingClassId());
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
                        if (ct != null && ct.get("data") != null) {
                            List<Map> ts = (List<Map>) ct.get("data");
                            if (ts != null && ts.size() > 0) {
                                Map tt = ts.get(0);
                                if (tt != null && tt.get("id") != null) {
                                    Map<String, Object> user = null;
                                    try {
                                        user = orgManagerRemoteService.getUserInfo1(Long.parseLong(tt.get("id").toString()));
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                    if (user != null && user.get("collegeId") != null) {
                                        item.setCollegeId(Long.parseLong(user.get("collegeId").toString()));
                                        if (user.get("collegeName") != null) {
                                            item.setCollegeName(user.get("collegeName").toString());
                                        }
                                        questionnaireAssginRepository.save(item);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
