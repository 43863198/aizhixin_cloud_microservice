package com.aizhixin.cloud.dd.feedback.service;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.feedback.domain.FeedbackRecordAnswerDomain;
import com.aizhixin.cloud.dd.feedback.domain.FeedbackRecordDomain;
import com.aizhixin.cloud.dd.feedback.domain.FeedbackTempletOptionsDomain;
import com.aizhixin.cloud.dd.feedback.dto.FeedbackRecordAnswerDTO;
import com.aizhixin.cloud.dd.feedback.dto.FeedbackRecordDTO;
import com.aizhixin.cloud.dd.feedback.dto.FeedbackTempletOptionsDTO;
import com.aizhixin.cloud.dd.feedback.entity.FeedbackRecord;
import com.aizhixin.cloud.dd.feedback.entity.FeedbackRecordAnswer;
import com.aizhixin.cloud.dd.feedback.entity.FeedbackTemplet;
import com.aizhixin.cloud.dd.feedback.entity.FeedbackTempletQues;
import com.aizhixin.cloud.dd.feedback.repository.*;
import com.aizhixin.cloud.dd.feedback.utils.FeedbackQuesGroup;
import com.aizhixin.cloud.dd.feedback.utils.FeedbackQuesType;
import com.aizhixin.cloud.dd.feedback.utils.FeedbackTempletType;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.IODTO;
import com.aizhixin.cloud.dd.rollcall.utils.IOUtil;
import com.alibaba.fastjson.JSON;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

@Service
@Transactional
public class RecordService {

    @Autowired
    private RecordRespository recordRespository;

    @Autowired
    private RecordAnswerRespository recordAnswerRespository;

    @Autowired
    private TempletOptionsRespository optionsRespository;

    @Autowired
    private TempletQuesRespository templetQuesRespository;

    @Autowired
    private TempletRespository templetRespository;

    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;

    @Autowired
    private IOUtil ioUtil;

    private String steeringPath = "/templates/feedback/steeringTemplate.xlsx";
    private String teachingPath = "/templates/feedback/teachingTemplate.xlsx";

    /**
     * 导出Excel
     *
     * @param pageable
     * @param orgId
     * @param type
     * @param filename
     * @return
     */
    public String exportList(Pageable pageable, Long orgId, String userName, String teacherName, String courseName, Integer type, String filename) {
        if (userName == null) {
            userName = "";
        }
        if (teacherName == null) {
            teacherName = "";
        }
        if (courseName == null) {
            courseName = "";
        }
        Page<FeedbackRecord> page = recordRespository.findList(pageable, orgId, userName, teacherName, courseName, type, DataValidity.VALID.getState());
        List<FeedbackRecord> list = page.getContent();
        if (list != null && list.size() > 0) {
            byte[] data = fillExel(orgId, type, list);
            String url = uploadIo(data, filename);
            return url;
        }
        return null;
    }

    private byte[] fillExel(Long orgId, Integer type, List<FeedbackRecord> list) {
        ByteArrayOutputStream os = null;
        try {
            String tempPath = steeringPath;
            if (type == FeedbackTempletType.TEACHING.getType()) {
                tempPath = teachingPath;
            }
            InputStream resourceAsStream = this.getClass().getResourceAsStream(tempPath);
            XSSFWorkbook wb = new XSSFWorkbook(resourceAsStream);

            XSSFCellStyle dateStyle = wb.createCellStyle();
            XSSFDataFormat format = wb.createDataFormat();
            dateStyle.setDataFormat(format.getFormat("yyyy-MM-dd HH:mm"));
            Map<String, Integer> colMap = new HashMap<>();
            for (FeedbackRecord item : list) {
                XSSFSheet sheet = null;
                if (item.getTemplet().getQuesType() == FeedbackQuesType.DAFEN.getType()) {
                    sheet = wb.getSheet("打分题");
                } else if (item.getTemplet().getQuesType() == FeedbackQuesType.XUANXIANG.getType()) {
                    sheet = wb.getSheet("选择题");
                } else {
                    sheet = wb.getSheet("简答题");
                }
                int rownum = sheet.getLastRowNum() + 1;
                XSSFRow row = sheet.createRow(rownum);
                //序号
                XSSFCell cell0 = row.createCell(0);
                cell0.setCellValue(rownum);
                //选课编号
                XSSFCell cell1 = row.createCell(1);
                cell1.setCellValue(item.getTeachingClassCode());
                //教学班名称
                XSSFCell cell2 = row.createCell(2);
                cell2.setCellValue(item.getTeachingClassName());
                //课程名称
                XSSFCell cell3 = row.createCell(3);
                cell3.setCellValue(item.getCourseName());
                //授课教师
                XSSFCell cell4 = row.createCell(4);
                cell4.setCellValue(item.getTeachingClassTeacher());
                int quesStartIndex = 0;
                if (type == FeedbackTempletType.TEACHING.getType()) {
                    //反馈时间
                    XSSFCell cell5 = row.createCell(5);
                    cell5.setCellStyle(dateStyle);
                    cell5.setCellValue(item.getCreatedDate());
                    //学生姓名
                    XSSFCell cell6 = row.createCell(6);
                    cell6.setCellValue(item.getUserName());
                    //学生学号
                    XSSFCell cell7 = row.createCell(7);
                    cell7.setCellValue(item.getJobNum());

                    sheet.setColumnWidth(5, 4200);
                    quesStartIndex = 8;
                } else {
                    //教师教学得分
                    XSSFCell cell5 = row.createCell(5);
                    cell5.setCellValue(item.getTeachingScore());
                    //班级学风得分
                    XSSFCell cell6 = row.createCell(6);
                    cell6.setCellValue(item.getStudyStyleScore());
                    //反馈时间
                    XSSFCell cell7 = row.createCell(7);
                    cell7.setCellStyle(dateStyle);
                    cell7.setCellValue(item.getCreatedDate());
                    //督导姓名
                    XSSFCell cell8 = row.createCell(8);
                    cell8.setCellValue(item.getUserName());

                    XSSFCell cell9 = row.createCell(9);
                    cell9.setCellValue(item.getClassNames());

                    sheet.setColumnWidth(7, 4200);
                    quesStartIndex = 10;
                }
                FeedbackRecordDomain d = queryFeedbackRecord(item.getId());
                int colNum = 0;
                if (d.getQuesList() != null && d.getQuesList().size() > 0) {
                    fillQuesExcel(row, quesStartIndex, d.getQuesList());
                    colNum += d.getQuesList().size();
                }
                if (d.getTeacherQuesList() != null && d.getTeacherQuesList().size() > 0) {
                    fillQuesExcel(row, quesStartIndex, d.getTeacherQuesList());
                    colNum += d.getTeacherQuesList().size();
                }
                if (d.getStyleQuesList() != null && d.getStyleQuesList().size() > 0) {
                    fillQuesExcel(row, quesStartIndex, d.getStyleQuesList());
                    colNum += d.getStyleQuesList().size();
                }
                Integer maxCol = colMap.get(sheet.getSheetName());
                if (maxCol == null) {
                    maxCol = 0;
                }
                if (colNum > maxCol) {
                    colMap.put(sheet.getSheetName(), colNum);
                }
            }
            Integer sheet1ColNum = colMap.get("打分题");
            if (sheet1ColNum != null && sheet1ColNum > 0) {
                int startIndex = 0;
                if (type == FeedbackTempletType.TEACHING.getType()) {
                    startIndex = 8;
                } else {
                    startIndex = 10;
                }
                XSSFSheet sheet1 = wb.getSheet("打分题");
                XSSFRow row = sheet1.getRow(0);
                for (int i = 0; i < sheet1ColNum; i++) {
                    XSSFCell cell1 = row.createCell(startIndex);
                    cell1.setCellValue("打分题" + (i + 1) + "题干");
                    XSSFCell cell2 = row.createCell(startIndex + 1);
                    cell2.setCellValue("打分题" + (i + 1) + "答案");
                    startIndex += 2;
                }
            }

            sheet1ColNum = colMap.get("选择题");
            if (sheet1ColNum != null && sheet1ColNum > 0) {
                int startIndex = 0;
                if (type == FeedbackTempletType.TEACHING.getType()) {
                    startIndex = 8;
                } else {
                    startIndex = 10;
                }
                XSSFSheet sheet1 = wb.getSheet("选择题");
                XSSFRow row = sheet1.getRow(0);
                for (int i = 0; i < sheet1ColNum; i++) {
                    XSSFCell cell1 = row.createCell(startIndex);
                    cell1.setCellValue("选择题" + (i + 1) + "题干");
                    XSSFCell cell2 = row.createCell(startIndex + 1);
                    cell2.setCellValue("选择题" + (i + 1) + "答案");
                    startIndex += 2;
                }
            }

            sheet1ColNum = colMap.get("简答题");
            if (sheet1ColNum != null && sheet1ColNum > 0) {
                int startIndex = 0;
                if (type == FeedbackTempletType.TEACHING.getType()) {
                    startIndex = 8;
                } else {
                    startIndex = 10;
                }
                XSSFSheet sheet1 = wb.getSheet("简答题");
                XSSFRow row = sheet1.getRow(0);
                for (int i = 0; i < sheet1ColNum; i++) {
                    XSSFCell cell1 = row.createCell(startIndex);
                    cell1.setCellValue("简答题" + (i + 1) + "题干");
                    XSSFCell cell2 = row.createCell(startIndex + 1);
                    cell2.setCellValue("简答题" + (i + 1) + "答案");
                    startIndex += 2;
                }
            }

            // 输出转输入
            os = new ByteArrayOutputStream();
            wb.write(os);
            byte[] data = os.toByteArray();
            return data;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void fillQuesExcel(XSSFRow row, int index, List<FeedbackRecordAnswerDomain> list) {
        for (FeedbackRecordAnswerDomain item : list) {
            XSSFCell cell1 = row.createCell(index);
            cell1.setCellValue(item.getContent());
            index++;
            XSSFCell cell2 = row.createCell(index);
            cell2.setCellValue(item.getAnswer());
            index++;
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

    /**
     * @param pageable
     * @param jobNum
     * @param type
     * @return
     */
    public PageData<FeedbackRecordDomain> queryListByJobNum(Pageable pageable, Long orgId, String jobNum, Integer type) {
        Page<FeedbackRecord> page = recordRespository.findByOrgIdAndJobNumAndTypeAndDeleteFlagOrderByCreatedDateDesc(pageable, orgId, jobNum, type, DataValidity.VALID.getState());
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageSize(page.getSize());
        pageDomain.setPageNumber(page.getNumber());
        pageDomain.setTotalElements(page.getTotalElements());
        pageDomain.setTotalPages(page.getTotalPages());
        List<FeedbackRecordDomain> list = typeListFeedbackRecordDomain(page.getContent());
        PageData<FeedbackRecordDomain> pageData = new PageData<>();
        pageData.setData(list);
        pageData.setPage(pageDomain);
        return pageData;
    }

    /**
     * @param pageable
     * @param orgId
     * @param userName
     * @param teacherName
     * @param courseName
     * @param type
     * @return
     */
    public PageData<FeedbackRecordDomain> queryList(Pageable pageable, Long orgId, String userName, String teacherName, String courseName, Integer type) {
        if (userName == null) {
            userName = "";
        }
        if (teacherName == null) {
            teacherName = "";
        }
        if (courseName == null) {
            courseName = "";
        }
        Page<FeedbackRecord> page = recordRespository.findList(pageable, orgId, type, userName, teacherName, courseName, DataValidity.VALID.getState());
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageSize(page.getSize());
        pageDomain.setPageNumber(pageable.getPageNumber() + 1);
        pageDomain.setTotalElements(page.getTotalElements());
        pageDomain.setTotalPages(page.getTotalPages());
        List<FeedbackRecordDomain> list = typeListFeedbackRecordDomain(page.getContent());
        PageData<FeedbackRecordDomain> pageData = new PageData<>();
        pageData.setData(list);
        pageData.setPage(pageDomain);
        return pageData;
    }

    /**
     * @param id
     * @return
     */
    public FeedbackRecordDomain queryFeedbackRecord(Long id) {
        FeedbackRecord record = recordRespository.findOne(id);
        if (record != null) {
            FeedbackRecordDomain domain = typeFeedbackRecordDomain(record);
            FeedbackTemplet templet = templetRespository.findOne(record.getTemplet().getId());
            if (templet.getType() == FeedbackTempletType.TEACHING.getType()) {
                domain.setQuesList(findRecordAnswer(record.getId(), FeedbackQuesGroup.FEEDBACK.getType()));
            } else {
                domain.setTeacherQuesList(findRecordAnswer(record.getId(), FeedbackQuesGroup.TEACHER.getType()));
                domain.setStyleQuesList(findRecordAnswer(record.getId(), FeedbackQuesGroup.STYLE.getType()));
            }
            return domain;
        }
        return null;
    }

    /**
     * @param feedback
     * @param account
     */
    public void saveRecord(FeedbackRecordDTO feedback, AccountDTO account) {
        FeedbackRecord record = typeFeedbackRecord(feedback, account.getOrganId());
        record = recordRespository.save(record);

        List<FeedbackRecordAnswer> savelist = new ArrayList<>();
        typeListFeedbackRecordAnswer(savelist, feedback.getQuesList(), record);
        typeListFeedbackRecordAnswer(savelist, feedback.getTeacherQuesList(), record);
        typeListFeedbackRecordAnswer(savelist, feedback.getStyleQuesList(), record);
        if (savelist.size() > 0) {
            recordAnswerRespository.save(savelist);
        }
    }

    private void typeListFeedbackRecordAnswer(List<FeedbackRecordAnswer> savelist, List<FeedbackRecordAnswerDTO> list, FeedbackRecord record) {
        if (list != null && list.size() > 0) {
            for (FeedbackRecordAnswerDTO item : list) {
                FeedbackRecordAnswer d = new FeedbackRecordAnswer();
                d.setRecord(record);
                FeedbackTempletQues templetQues = templetQuesRespository.findOne(item.getQuesId());
                d.setTempletQues(templetQues);
                d.setAnswer(item.getAnswer());
                savelist.add(d);
            }
        }
    }


    private FeedbackRecord typeFeedbackRecord(FeedbackRecordDTO item, Long orgId) {
        FeedbackRecord record = new FeedbackRecord();
        if (item != null) {
            record.setId(item.getId());
            record.setOrgId(orgId);
            FeedbackTemplet t = templetRespository.findOne(item.getTempletId());
            record.setTemplet(t);
            record.setType(item.getType());
            record.setTeachingClassId(item.getTeachingClassId());
            record.setTeachingClassCode(item.getTeachingClassCode());
            record.setTeachingClassName(item.getTeachingClassName());
            record.setTeachingClassTeacher(item.getTeachingClassTeacher());
            String teacherIds = item.getTeacherIds();
            if (teacherIds != null && !teacherIds.equals("")) {
                Set<Long> set = new HashSet();
                String[] strs = teacherIds.split(" ");
                for (String str : strs) {
                    if (str != null && !str.equals("")) {
                        set.add(Long.parseLong(str));
                    }
                }
                if (set.size() > 0) {
                    String json = orgManagerRemoteService.findUserByIds(set);
                    if (!StringUtils.isEmpty(json)) {
                        List<Map> mapList = JSON.parseArray(json, Map.class);
                        String jobNums = "";
                        if (mapList != null && mapList.size() > 0) {
                            for (Map m : mapList) {
                                if (!jobNums.equals("")) {
                                    jobNums += " ";
                                }
                                jobNums += m.get("code");
                            }
                        }
                        record.setTeacherJobNum(jobNums);
                    }

                }

            }

            record.setCourseId(item.getCourseId());
            record.setCourseName(item.getCourseName());
            record.setClassNames(item.getClassNames());
            record.setJobNum(item.getJobNum());
            record.setUserName(item.getUserName());
            record.setUserAvatar(item.getUserAvatar());
            if (item.getTeachingScore2() != null && item.getTeachingScore2() > 0) {
                record.setTeachingScore(item.getTeachingScore2());
            } else if (item.getTeachingScore() != null) {
                record.setTeachingScore(Float.parseFloat(item.getTeachingScore().toString()));
            } else {
                record.setTeachingScore(0f);
            }
            if (item.getStudyStyleScore2() != null && item.getStudyStyleScore2() > 0) {
                record.setStudyStyleScore(item.getStudyStyleScore2());
            } else if (item.getStudyStyleScore() != null) {
                record.setStudyStyleScore(Float.parseFloat(item.getStudyStyleScore().toString()));
            } else {
                record.setStudyStyleScore(0f);
            }
        }
        return record;
    }


    private List<FeedbackRecordAnswerDomain> findRecordAnswer(Long recordId, Integer group) {
        List<FeedbackRecordAnswer> list = recordAnswerRespository.findByRecordIdAndGroup(recordId, group);
        List<FeedbackRecordAnswerDomain> domains = typeListFeedbackRecordAnswerDomain(list);
        return domains;
    }

    private List<FeedbackRecordAnswerDomain> typeListFeedbackRecordAnswerDomain(List<FeedbackRecordAnswer> list) {
        if (list != null && list.size() > 0) {
            List<FeedbackRecordAnswerDomain> result = new ArrayList<>();
            for (FeedbackRecordAnswer item : list) {
                FeedbackRecordAnswerDomain d = typeFeedbackRecordAnswerDomain(item);
                result.add(d);
            }
            return result;
        }
        return null;
    }

    private boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    private FeedbackRecordAnswerDomain typeFeedbackRecordAnswerDomain(FeedbackRecordAnswer item) {
        if (item != null) {
            FeedbackRecordAnswerDomain d = new FeedbackRecordAnswerDomain();
            d.setSubject(item.getTempletQues().getSubject());
            d.setContent(item.getTempletQues().getContent());
            d.setScore2(item.getTempletQues().getScore());
            d.setScore(new BigDecimal(item.getTempletQues().getScore()).setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
            d.setAnswer2(item.getAnswer());
            if (isInteger(item.getAnswer())) {
                d.setAnswer(new BigDecimal(item.getAnswer()).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() + "");
            } else {
                d.setAnswer(item.getAnswer());
            }
            if (item.getTempletQues().getTemplet().getQuesType() == FeedbackQuesType.XUANXIANG.getType()) {
                List<FeedbackTempletOptionsDTO> optionsDTOS = optionsRespository.findByQuesId(item.getTempletQues().getId());
                d.setOptionList(typeListFeedbackTempletOptionsDomain(optionsDTOS));
            }
            return d;
        }
        return null;
    }

    private List<FeedbackTempletOptionsDomain> typeListFeedbackTempletOptionsDomain(List<FeedbackTempletOptionsDTO> list) {
        if (list != null && list.size() > 0) {
            List<FeedbackTempletOptionsDomain> result = new ArrayList<>();
            for (FeedbackTempletOptionsDTO item : list) {
                FeedbackTempletOptionsDomain d = typeFeedbackTempletOptionsDomain(item);
                result.add(d);
            }
            return result;
        }
        return null;
    }

    private FeedbackTempletOptionsDomain typeFeedbackTempletOptionsDomain(FeedbackTempletOptionsDTO item) {
        if (item != null) {
            FeedbackTempletOptionsDomain d = new FeedbackTempletOptionsDomain();
            d.setId(item.getId());
            d.setContent(item.getContent());
            d.setOption(item.getOption());
            return d;
        }
        return null;
    }

    private List<FeedbackRecordDomain> typeListFeedbackRecordDomain(List<FeedbackRecord> list) {
        if (list != null && list.size() > 0) {
            List<FeedbackRecordDomain> result = new ArrayList<>();
            for (FeedbackRecord item : list) {
                FeedbackRecordDomain d = typeFeedbackRecordDomain(item);
                result.add(d);
            }
            return result;
        } else {
            return null;
        }
    }

    private FeedbackRecordDomain typeFeedbackRecordDomain(FeedbackRecord item) {
        if (item != null) {
            FeedbackRecordDomain d = new FeedbackRecordDomain();
            d.setId(item.getId());
            d.setTeachingClassId(item.getTeachingClassId());
            d.setTeachingClassCode(item.getTeachingClassCode());
            d.setTeachingClassName(item.getTeachingClassName());
            d.setTeachingClassTeacher(item.getTeachingClassTeacher());
            d.setCourseId(item.getCourseId());
            d.setCourseName(item.getCourseName());
            d.setClassNames(item.getClassNames());
            d.setJobNum(item.getJobNum());
            d.setUserName(item.getUserName());
            d.setUserAvatar(item.getUserAvatar());
            d.setTeachingScore2(item.getTeachingScore());
            if (d.getTeachingScore2() != null) {
                d.setTeachingScore(new BigDecimal(item.getTeachingScore()).setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
            } else {
                d.setTeachingScore(0);
            }
            d.setStudyStyleScore2(item.getStudyStyleScore());
            if (d.getStudyStyleScore2() != null) {
                d.setStudyStyleScore(new BigDecimal(item.getStudyStyleScore()).setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
            } else {
                d.setStudyStyleScore(0);
            }
            d.setCreateDate(item.getCreatedDate());
            d.setQuesType(item.getTemplet().getQuesType());
            return d;
        } else {
            return null;
        }
    }
}
