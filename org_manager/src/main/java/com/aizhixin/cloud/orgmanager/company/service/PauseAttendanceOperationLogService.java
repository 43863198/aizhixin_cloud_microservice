package com.aizhixin.cloud.orgmanager.company.service;

import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.company.dto.StudentRollcallSetLogDTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-10-18
 */
@Service
public class PauseAttendanceOperationLogService {
    @Autowired
    private EntityManager em;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Async
    public void initLogStatus() {
        String sql = "UPDATE t_student_rollcall_set SET is_last=0;";
        jdbcTemplate.execute(sql);
        sql = "SELECT MAX(ID) ID, STUDENT_ID FROM t_student_rollcall_set GROUP BY STUDENT_ID";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        if (list != null && list.size() > 0) {
            String ids = "";
            for (Map<String, Object> item : list) {
                if (StringUtils.isNotEmpty(ids)) {
                    ids += ",";
                }
                ids += item.get("ID");
            }
            sql = "UPDATE t_student_rollcall_set SET is_last=1 WHERE ID IN (" + ids + ");";
            jdbcTemplate.execute(sql);
        }
    }

    public void setLogStatus(Long stuId, Integer status) {
        String sql = "UPDATE t_student_rollcall_set SET is_last=" + status + " WHERE STUDENT_ID=" + stuId + ";";
        jdbcTemplate.execute(sql);
    }

    public PageData<StudentRollcallSetLogDTO> getPauseAttendanceLogBywhere(Long orgId, Long collegeId, Integer opt, String criteria, String startTime, String endTime, Pageable pageable) {
        PageData<StudentRollcallSetLogDTO> p = new PageData<>();
        Long count = 0L;
        Map<String, Object> condition = new HashMap<>();
        StringBuilder cql = new StringBuilder("SELECT count(1) FROM t_student_rollcall_set srs LEFT JOIN t_user u ON srs.STUDENT_ID = u.ID WHERE 1=1");
        StringBuilder sql = new StringBuilder("SELECT srs.CREATED_DATE, srs.OPERATOR, srs.OPT, srs.MSG, srs.STU_JOB_NUMBER, srs.STU_NAME, srs.STU_CLASSES_NAME, srs.STU_CLASSES_YEAR, srs.STU_PROFESSIONAL_NAME, srs.STU_COLLEGE_NAME, srs.STUDENT_ID, srs.is_last FROM " +
                "t_student_rollcall_set srs LEFT JOIN t_user u ON srs.STUDENT_ID = u.ID WHERE 1=1");
        if (null != orgId) {
            cql.append(" AND srs.ORG_ID = :orgId");
            sql.append(" AND srs.ORG_ID = :orgId");
            condition.put("orgId", orgId);
        }
        if (null != collegeId) {
            cql.append(" AND u.COLLEGE_ID = :collegeId");
            sql.append(" AND u.COLLEGE_ID = :collegeId");
            condition.put("collegeId", collegeId);
        }
        if (null != opt) {
            cql.append(" AND srs.OPT = :opt");
            sql.append(" AND srs.OPT = :opt");
            condition.put("opt", opt);
        }
        if (null != criteria && !StringUtils.isBlank(criteria)) {
            cql.append(" AND (srs.STU_JOB_NUMBER like :criteria or srs.STU_NAME like :criteria)");
            sql.append(" AND (srs.STU_JOB_NUMBER like :criteria or srs.STU_NAME like :criteria)");
            condition.put("criteria", "%" + criteria + "%");
        }
        if (null != startTime && null == endTime) {
            cql.append(" AND DATE_FORMAT(srs.CREATED_DATE,'%Y-%m-%d') >= :startTime");
            sql.append(" AND DATE_FORMAT(srs.CREATED_DATE,'%Y-%m-%d') >= :startTime");
            condition.put("startTime", startTime);
        }
        if (null != endTime && null == startTime) {
            cql.append(" AND DATE_FORMAT(srs.CREATED_DATE,'%Y-%m-%d') <= :endTime");
            sql.append(" AND DATE_FORMAT(srs.CREATED_DATE,'%Y-%m-%d') <= :endTime");
            condition.put("endTime", endTime);
        }
        if (null != startTime && null != endTime) {
            cql.append(" AND DATE_FORMAT(srs.CREATED_DATE,'%Y-%m-%d') BETWEEN :startTime  AND :endTime");
            sql.append(" AND DATE_FORMAT(srs.CREATED_DATE,'%Y-%m-%d') BETWEEN :startTime  AND :endTime");
            condition.put("startTime", startTime);
            condition.put("endTime", endTime);
        }
        if (null != startTime && null != endTime && startTime.equals(endTime)) {
            cql.append(" AND DATE_FORMAT(srs.CREATED_DATE,'%Y-%m-%d') = :startTime");
            sql.append(" AND DATE_FORMAT(srs.CREATED_DATE,'%Y-%m-%d') = :startTime");
            condition.put("startTime", startTime);
        }
        sql.append(" ORDER BY srs.CREATED_DATE DESC");
        try {
            Query cq = em.createNativeQuery(cql.toString());
            Query sq = em.createNativeQuery(sql.toString());
            for (Map.Entry<String, Object> e : condition.entrySet()) {
                cq.setParameter(e.getKey(), e.getValue());
                sq.setParameter(e.getKey(), e.getValue());
            }
            count = Long.valueOf(String.valueOf(cq.getSingleResult()));
            List<StudentRollcallSetLogDTO> attendanceRecordDTOList = new ArrayList<>();
            if (count.intValue() > 0) {
                sq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
                sq.setMaxResults(pageable.getPageSize());
                List<Object> res = sq.getResultList();
                for (Object d : res) {
                    StudentRollcallSetLogDTO studentRollcallSetLogDTO = new StudentRollcallSetLogDTO();
                    Object[] data = (Object[]) d;
                    if (null != data[0]) {
                        studentRollcallSetLogDTO.setOperationTime(new SimpleDateFormat("yyyy-M-dd HH:mm:ss").parse(String.valueOf(data[0])));
                    }
                    if (null != data[1]) {
                        studentRollcallSetLogDTO.setOperator(String.valueOf(data[1]));
                    }
                    if (null != data[2]) {
                        if (Integer.valueOf(String.valueOf(data[2])).intValue() == 10) {
                            studentRollcallSetLogDTO.setOptContent("暂停考勤");
                        }
                        if (Integer.valueOf(String.valueOf(data[2])).intValue() == 20) {
                            studentRollcallSetLogDTO.setOptContent("恢复考勤");
                        }
                    }
                    if (null != data[3]) {
                        studentRollcallSetLogDTO.setMsg(String.valueOf(data[3]));
                    }
                    if (null != data[4]) {
                        studentRollcallSetLogDTO.setStuJobNumber(String.valueOf(data[4]));
                    }
                    if (null != data[5]) {
                        studentRollcallSetLogDTO.setStuName(String.valueOf(data[5]));
                    }
                    if (null != data[6]) {
                        studentRollcallSetLogDTO.setStuClassesName(String.valueOf(data[6]));
                    }
                    if (null != data[7]) {
                        studentRollcallSetLogDTO.setStuClassesYear(String.valueOf(data[7]));
                    }
                    if (null != data[8]) {
                        studentRollcallSetLogDTO.setStuProfessionalName(String.valueOf(data[8]));
                    }
                    if (null != data[9]) {
                        studentRollcallSetLogDTO.setStuCollegeName(String.valueOf(data[9]));
                    }
                    if (null != data[10]) {
                        try {
                            studentRollcallSetLogDTO.setStuId(Long.parseLong(String.valueOf(data[10])));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (null != data[11]) {
                        studentRollcallSetLogDTO.setIsLast((Boolean) data[11]);
                    }
                    attendanceRecordDTOList.add(studentRollcallSetLogDTO);
                }
            }
            p.setData(attendanceRecordDTOList);
            p.getPage().setTotalElements(count);
            p.getPage().setPageNumber(pageable.getPageNumber() + 1);
            p.getPage().setPageSize(pageable.getPageSize());
            p.getPage().setTotalPages(PageUtil.cacalatePagesize(count, p.getPage().getPageSize()));
        } catch (Exception e) {
            return p;
        }
        return p;
    }


}
