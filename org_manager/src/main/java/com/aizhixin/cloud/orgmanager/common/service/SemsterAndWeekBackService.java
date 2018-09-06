package com.aizhixin.cloud.orgmanager.common.service;

import com.aizhixin.cloud.orgmanager.common.core.ErrorCode;
import com.aizhixin.cloud.orgmanager.common.domain.StringAndLongSetDomain;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.common.util.DateUtil;
import com.aizhixin.cloud.orgmanager.company.entity.Organization;
import com.aizhixin.cloud.orgmanager.company.entity.Semester;
import com.aizhixin.cloud.orgmanager.company.entity.Week;
import com.aizhixin.cloud.orgmanager.company.service.OrganizationService;
import com.aizhixin.cloud.orgmanager.company.service.SemesterService;
import com.aizhixin.cloud.orgmanager.company.service.WeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 学期和学周后台手动操作
 */
@Component
public class SemsterAndWeekBackService {

    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private SemesterService semesterService;
    @Autowired
    private WeekService weekService;

    @Transactional
    private void saveAllSemsterAndWeek(List<Semester> semesterList, List<Week> weekList) {
        semesterService.save(semesterList);
        weekService.save(weekList);
    }

    /**
     * 给传入的学校新建学期和一个学周，学校如果不传入，所有学校进行操作
     * @param orgIds            学校
     * @param semsterName       学期名称
     * @param semsterCode       学期编码
     * @param semsterStartDate  学期开始日期
     * @param semsterEndDate    学期结束日期
     * @param firstWeekStartDate    第一周的开始日期
     * @param firstWeekEndDate      第一周的结束日期
     * @return 失败学校ID列表
     */
    public StringAndLongSetDomain batchAddSemsterAndWeek(Set<Long> orgIds, String semsterName, String semsterCode, Date semsterStartDate, Date semsterEndDate, Date firstWeekStartDate, Date firstWeekEndDate) {
        Set<Long> preparOrgIds = new HashSet<>();//准备处理的学校
        //需要处理学校的确定
        if (null == orgIds || orgIds.isEmpty()) {//没有传入，获取所有学校
            List<Organization> orgs = organizationService.findAll();
            for (Organization o : orgs) {
                preparOrgIds.add(o.getId());
            }
        } else {
            preparOrgIds.addAll(orgIds);
        }
        if (semsterEndDate.before(semsterStartDate)) {
            throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "学期结束日期不能早于开始日期");
        }
        if (firstWeekEndDate.before(firstWeekStartDate)) {
            throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "学周结束日期不能早于开始日期");
        }
        if (firstWeekStartDate.before(semsterStartDate) || firstWeekEndDate.after(semsterEndDate)) {
            throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "学周日期不能在学期数据范围之外");
        }
        Date sunday = DateUtil.getSunday(firstWeekStartDate);

        if (firstWeekEndDate.after(sunday)) {
            throw new CommonException(ErrorCode.FIELD_IS_REPLICATION, "学周开始日期和结束日期不在同一学周");
        }

        Set<Long> failOrgs = new HashSet<>();//学期冲突的学校(时间和编码的检查)
        List<Semester> semesterList = new ArrayList<>();
        List<Week> weekList = new ArrayList<>();

        //逐个学校验证学期时间和编码是否冲突，将不冲突的所有数据集中起来，然后保存即可
        for (Long orgId : preparOrgIds) {
            long cs = semesterService.countBetweenStartDate(orgId, semsterStartDate);
            long ce = semesterService.countBetweenStartDate(orgId, semsterStartDate);
            long c = semesterService.countByCode(orgId, semsterCode);

            if (cs > 0 || ce > 0 || c > 0) {
                failOrgs.add(orgId);
            } else {
                Semester semester = new Semester();
                semester.setName(semsterName);
                semester.setCode(semsterCode);
                semester.setStartDate(semsterStartDate);
                semester.setEndDate(semsterEndDate);
                semester.setOrgId(orgId);
                semester.setNumWeek(1);

                Week week = new Week();
                week.setSemester(semester);
                week.setOrgId(orgId);
                week.setNo(1);
                week.setStartDate(firstWeekStartDate);
                week.setEndDate(firstWeekEndDate);

                weekList.add(week);
                semesterList.add(semester);
            }
        }
        if (!weekList.isEmpty() && !semesterList.isEmpty()) {
            saveAllSemsterAndWeek(semesterList, weekList);
        }
        if (!failOrgs.isEmpty()) {
            return new StringAndLongSetDomain("学期时间或编码冲突的学校", failOrgs);
        } else {
            return  null;
        }
    }
}
