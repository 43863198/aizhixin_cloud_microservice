package com.aizhixin.cloud.dd.credit.service;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.constant.ReturnConstants;
import com.aizhixin.cloud.dd.credit.entity.CreditReport;
import com.aizhixin.cloud.dd.credit.entity.CreditReportRecord;
import com.aizhixin.cloud.dd.credit.entity.CreditStudent;
import com.aizhixin.cloud.dd.credit.entity.CreditTempletQues;
import com.aizhixin.cloud.dd.credit.repository.CreditReportRecordRepository;
import com.aizhixin.cloud.dd.credit.repository.CreditReportRepository;
import com.aizhixin.cloud.dd.credit.repository.CreditStudentRepository;
import com.aizhixin.cloud.dd.credit.repository.CreditTempletQuesRepository;
import com.aizhixin.cloud.dd.orgStructure.entity.Classes;
import com.aizhixin.cloud.dd.orgStructure.entity.OrgInfo;
import com.aizhixin.cloud.dd.orgStructure.entity.Prof;
import com.aizhixin.cloud.dd.orgStructure.repository.ClassesRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.OrgInfoRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.ProfRepository;
import com.aizhixin.cloud.dd.rollcall.dto.IODTO;
import com.aizhixin.cloud.dd.rollcall.utils.IOUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CreditReportService {

    @Autowired
    private CreditReportRepository reportRepository;
    @Autowired
    private CreditReportRecordRepository reportRecordRepository;
    @Autowired
    private CreditTempletQuesRepository quesRepository;
    @Autowired
    private CreditStudentRepository studentRepository;
    @Autowired
    private OrgInfoRepository orgInfoRepository;
    @Autowired
    private ProfRepository profRepository;
    @Autowired
    private ClassesRepository classesRepository;
    @Autowired
    private IOUtil ioUtil;

    public PageData<CreditReport> findByOrgid(Pageable pageable, Long orgId, String className, String teacherName, Long templetId) {
        if (StringUtils.isEmpty(className)) {
            className = "";
        }
        if (StringUtils.isEmpty(teacherName)) {
            teacherName = "";
        }
        Page<CreditReport> page = null;
        if (templetId != null && templetId > 0) {
            page = reportRepository.findByOrgIdAndClassNameAndTeacherNameAndTempletId(pageable, orgId, className, teacherName, templetId);
        } else {
            page = reportRepository.findByOrgIdAndClassNameAndTeacherName(pageable, orgId, className, teacherName);
        }
        PageData<CreditReport> pageData = new PageData<>();
        pageData.setData(page.getContent());
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageSize(page.getSize());
        pageDomain.setPageNumber(page.getNumber());
        pageDomain.setTotalElements(page.getTotalElements());
        pageDomain.setTotalPages(page.getTotalPages());
        pageData.setPage(pageDomain);
        return pageData;
    }

    public Map<String, Object> exportReport(Long reportId) {
        Map<String, Object> result = new HashMap<>();
        CreditReport report = reportRepository.findOne(reportId);
        if (report == null) {
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            result.put(ApiReturnConstants.MESSAGE, "无报表数据");
        } else {
            Sort quesSort = new Sort(new Sort.Order(Sort.Direction.ASC, "id"));
            List<CreditTempletQues> quesList = quesRepository.findByTempletIdAndDeleteFlag(report.getTempletId(), DataValidity.VALID.getState(), quesSort);
            List<CreditReport> reports = new ArrayList<>();
            reports.add(report);
            String title = getTitleWithClass(report);
            byte[] data = fillExel(reports, title, quesList);
            String url = uploadIo(data, title + ".xlsx");
            result.put("fileName", title + ".xlsx");
            result.put(ReturnConstants.RETURN_MESSAGE, url);
            result.put(ReturnConstants.RETURN_SUCCESS, Boolean.TRUE);
        }
        return result;
    }

    private byte[] fillExel(List<CreditReport> reports, String title, List<CreditTempletQues> quesList) {
        ByteArrayOutputStream os = null;
        try {
            XSSFWorkbook wb = new XSSFWorkbook();
            CellStyle numCellStyle = wb.createCellStyle();
            XSSFDataFormat df = wb.createDataFormat();
            numCellStyle.setDataFormat(df.getFormat("#,#0.0"));
            XSSFSheet sheet = wb.createSheet();

            setTitle(sheet, title, quesList.size() + 2);
            setSubTitle(sheet, quesList);
            int rowIndex = 2;
            for (CreditReport report : reports) {
                List<CreditStudent> stuList = studentRepository.findByCreditId(report.getCreditId());
                if (stuList != null && stuList.size() > 0) {
                    for (int si = 0; si < stuList.size(); si++) {
                        CreditStudent stu = stuList.get(si);
                        List<CreditReportRecord> recordList = getRecord(report.getId(), stu.getStuId());
                        if (recordList != null && recordList.size() > 0) {
                            XSSFRow row = sheet.createRow(rowIndex);
                            XSSFCell cell1 = row.createCell(0);
                            cell1.setCellValue(stu.getStuName());
                            XSSFCell cell2 = row.createCell(1);
                            cell2.setCellValue(stu.getJobNum());
                            XSSFCell cell3 = row.createCell(2);
                            cell3.setCellValue(report.getClassName());
                            for (int i = 0; i < recordList.size(); i++) {
                                CreditReportRecord r = recordList.get(i);
                                XSSFCell cell = row.createCell(i + 3, CellType.NUMERIC);
                                cell.setCellValue(r.getAvgScore());
                                cell.setCellStyle(numCellStyle);
                            }
                            rowIndex++;
                        }
                    }
                }
            }
            // 输出转输入
            os = new ByteArrayOutputStream();
            wb.write(os);
            byte[] data = os.toByteArray();
            return data;
        } catch (Exception e) {
            log.warn("Exception", e);
            return null;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                log.warn("Exception", e);
            }
        }
    }

    private void setTitle(XSSFSheet sheet, String title, Integer colCount) {
        XSSFRow titleRow = sheet.createRow(0);
        XSSFCell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(title);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, colCount));
    }

    private void setSubTitle(XSSFSheet sheet, List<CreditTempletQues> quesList) {
        XSSFRow subTitleRow = sheet.createRow(1);
        XSSFCell subTitleCell1 = subTitleRow.createCell(0);
        subTitleCell1.setCellValue("姓名");
        XSSFCell subTitleCell2 = subTitleRow.createCell(1);
        subTitleCell2.setCellValue("学号");
        XSSFCell subTitleCell3 = subTitleRow.createCell(2);
        subTitleCell3.setCellValue("班级");
        for (int i = 0; i < quesList.size(); i++) {
            XSSFCell subTitleCell = subTitleRow.createCell(i + 3);
            subTitleCell.setCellValue(quesList.get(i).getContent());
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

    private String getTitleWithClass(CreditReport report) {
        OrgInfo org = orgInfoRepository.findByOrgId(report.getOrgId());
        Classes classes = classesRepository.findByClassesId(report.getClassId());
        Prof prof = profRepository.findByProfId(classes.getProfId());
        String title = org.getName() + prof.getProfName() + classes.getClassesName() + "素质学分统计表(" + report.getTempletName() + ")";
        return title;
    }

    private String getTitle(CreditReport report) {
        OrgInfo org = orgInfoRepository.findByOrgId(report.getOrgId());
        Classes classes = classesRepository.findByClassesId(report.getClassId());
        Prof prof = profRepository.findByProfId(classes.getProfId());
        String title = org.getName() + prof.getProfName() + "素质学分统计表(" + report.getTempletName() + ")";
        return title;
    }

    private List<CreditReportRecord> getRecord(Long reportId, Long stuId) {
        Sort recordSort = new Sort(new Sort.Order(Sort.Direction.ASC, "quesId"));
        List<CreditReportRecord> recordList = reportRecordRepository.findByReportIdAndStuId(reportId, stuId, recordSort);
        return recordList;
    }

    public Map<String, Object> exportReportByTemplet(Long templetId, Long orgId, String className, String teacherName) {
        Map<String, Object> result = new HashMap<>();
        if (StringUtils.isEmpty(className)) {
            className = "";
        }
        if (StringUtils.isEmpty(teacherName)) {
            teacherName = "";
        }
        List<CreditReport> reports = reportRepository.findByOrgIdAndClassNameAndTeacherNameAndTempletId(orgId, className, teacherName, templetId);
        if (reports == null || reports.size() == 0) {
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            result.put(ApiReturnConstants.MESSAGE, "无报表数据");
        } else {
            Sort quesSort = new Sort(new Sort.Order(Sort.Direction.ASC, "id"));
            List<CreditTempletQues> quesList = quesRepository.findByTempletIdAndDeleteFlag(templetId, DataValidity.VALID.getState(), quesSort);
            String title = getTitle(reports.get(0));
            byte[] data = fillExel(reports, title, quesList);
            String url = uploadIo(data, title + ".xlsx");
            result.put("fileName", title + ".xlsx");
            result.put(ReturnConstants.RETURN_MESSAGE, url);
            result.put(ReturnConstants.RETURN_SUCCESS, Boolean.TRUE);
        }
        return result;
    }
}
