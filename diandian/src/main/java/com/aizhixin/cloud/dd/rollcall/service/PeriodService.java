package com.aizhixin.cloud.dd.rollcall.service;

import java.util.*;

import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.rollcall.dto.DianDianDaySchoolTimeTableDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.PeriodDTO;
import org.springframework.util.StringUtils;

@Component
@Transactional
@Slf4j
public class PeriodService {
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;

    public List<PeriodDTO> listPeriod(Long orgId) {

        if (null == orgId) {
            return null;
        }
        Map<String, Object> periodResult = orgManagerRemoteService.listPeriod(orgId, 1, Integer.MAX_VALUE);
        List<Map<String, Object>> mapPeriod = (List<Map<String, Object>>) periodResult.get(ApiReturnConstants.DATA);
        List listPeriod = new ArrayList();
        for (Map<String, Object> map : mapPeriod) {
            PeriodDTO dto = new PeriodDTO();
            dto.setId(Long.parseLong(String.valueOf(map.get("id"))));
            dto.setOrgId(orgId);
            dto.setStartTime((String) map.get("startTime"));
            dto.setEndTime((String) map.get("endTime"));
            dto.setNo((Integer) map.get("no"));
            dto.setName("第" + map.get("no") + "节课");

            //无用字段，防止ios崩溃
            //******
            dto.setCreatedDate(new Date());
            dto.setUserId(orgId);
            //******
            listPeriod.add(dto);
        }
        return listPeriod;
    }

    public List<PeriodDTO> findAllByOrganIdAndStatus(Long studentId, Long orgId, Date teachDay) {
        List<DianDianDaySchoolTimeTableDomain> scheduleList = orgManagerRemoteService
                .getStudentDaySchoolTimeTable(studentId, DateFormatUtil.formatShort(teachDay));
        List<PeriodDTO> list = new ArrayList();
        List<PeriodDTO> periods = listPeriod(orgId);
        Set<Integer> periodNos = new HashSet<>();
        for (DianDianDaySchoolTimeTableDomain d : scheduleList) {
            int num = d.getPeriodNum();
            int tmep = 0;
            for (int i = 0; i < num; i++) {
                tmep = i;
                if ((d.getPeriodNo() + i) > periods.size()) {
                    tmep = periods.size() - d.getPeriodNo();
                }
                PeriodDTO periodDTO = periods.get(d.getPeriodNo() - 1 + tmep);
                if (periodNos.contains(d.getPeriodNo())) {
                    continue;
                }
                periodNos.add(d.getPeriodNo());
                periodDTO.setTeachingClassId(d.getTeachingClassId());
                periodDTO.setClassroom(d.getClassroom());
                String t = d.getTeachers();
                String teacherName = "";
                if (!StringUtils.isEmpty(t)) {
                    try {
                        String[] ts = d.getTeachers().split(";");
                        if (ts != null) {
                            for (String tt : ts) {
                                if (!StringUtils.isEmpty(tt)) {
                                    String[] names = tt.split(",");
                                    if (names != null && names.length == 2) {
                                        if (!StringUtils.isEmpty(teacherName)) {
                                            teacherName += ",";
                                        }
                                        teacherName += names[1];
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.warn("Exception", e);
                    }
                    periodDTO.setTeacherName(d.getTeachers().split(",")[1]);
                }
                periodDTO.setTeacherName(teacherName);
                periodDTO.setCourseName(d.getCourseName());
                list.add(periodDTO);
            }
        }
        Collections.sort(list, new Comparator<PeriodDTO>() {
            @Override
            public int compare(PeriodDTO o1, PeriodDTO o2) {
                return o1.getNo().intValue() - o2.getNo().intValue();
            }
        });
        return list;
    }

    public List<PeriodDTO> findAllByOrganIdAndStatusV2(Long studentId, Long orgId, Date teachDay) {
        List<DianDianDaySchoolTimeTableDomain> scheduleList = orgManagerRemoteService
                .getStudentDaySchoolTimeTable(studentId, DateFormatUtil.formatShort(teachDay));
        List<PeriodDTO> list = new ArrayList();
        List<PeriodDTO> periods = listPeriod(orgId);
        Set<Integer> periodNos = new HashSet<>();
        for (DianDianDaySchoolTimeTableDomain d : scheduleList) {
            int num = d.getPeriodNum();
            int tmep = 0;
            for (int i = 0; i < num; i++) {
                tmep = i;
                if ((d.getPeriodNo() + i) > periods.size()) {
                    tmep = periods.size() - d.getPeriodNo();
                }
                if (periodNos.contains(d.getPeriodNo())) {
                    continue;
                }
                periodNos.add(d.getPeriodNo());

                PeriodDTO periodDTO = periods.get(d.getPeriodNo() - 1 + tmep);
                periodDTO.setTeachingClassId(d.getTeachingClassId());
                periodDTO.setClassroom(d.getClassroom());
                if (d.getPeriodNum() > 1) {
                    periodDTO.setEndTime(d.getPeriodEndtime());
                    periodDTO.setName("第" + d.getPeriodNo() + "-" + (d.getPeriodNo() + d.getPeriodNum() - 1) + "节课");
                }
                String t = d.getTeachers();
                String teacherName = "";
                if (!StringUtils.isEmpty(t)) {
                    try {
                        String[] ts = d.getTeachers().split(";");
                        if (ts != null) {
                            for (String tt : ts) {
                                if (!StringUtils.isEmpty(tt)) {
                                    String[] names = tt.split(",");
                                    if (names != null && names.length == 2) {
                                        if (!StringUtils.isEmpty(teacherName)) {
                                            teacherName += ",";
                                        }
                                        teacherName += names[1];
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.warn("Exception", e);
                    }
                    periodDTO.setTeacherName(d.getTeachers().split(",")[1]);
                }
                periodDTO.setTeacherName(teacherName);
                periodDTO.setCourseName(d.getCourseName());
                list.add(periodDTO);
            }
        }
        Collections.sort(list, new Comparator<PeriodDTO>() {
            @Override
            public int compare(PeriodDTO o1, PeriodDTO o2) {
                return o1.getNo().intValue() - o2.getNo().intValue();
            }
        });
        return list;
    }
}
