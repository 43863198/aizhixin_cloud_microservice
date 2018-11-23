package com.aizhixin.cloud.dd.statistics.service;

import com.aizhixin.cloud.dd.common.utils.ConfigCache;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.common.utils.http.HttpResponse;
import com.aizhixin.cloud.dd.common.utils.http.OauthGet;
import com.aizhixin.cloud.dd.constant.RollCallConstants;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.AttendanceDTO;
import com.aizhixin.cloud.dd.rollcall.entity.RollCall;
import com.aizhixin.cloud.dd.rollcall.service.RollCallService;
import com.aizhixin.cloud.dd.statistics.controller.WebController;
import com.aizhixin.cloud.dd.statistics.dto.*;
import com.aizhixin.cloud.dd.statistics.repository.AttendanceStatisticsQuery;
import com.google.common.collect.Lists;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;

/**
 * Created by LIMH on 2017/8/21.
 */
@Service
@Transactional
public class StatisticsService {

    private final Logger log = LoggerFactory.getLogger(StatisticsService.class);

    @Autowired
    private AttendanceStatisticsQuery attendanceStatisticsQuery;

    @Lazy
    @Autowired
    private RollCallService rollCallService;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;
    @Autowired
    private ConfigCache configCache;

    public List <CollegeStatisticsDTO> getOrganAttendanceStatistics(Long organId) {

        List <CollegeStatisticsDTO> resultList = new ArrayList <>();
        Map <Long, CollegeStatisticsDTO> tempMap = new HashedMap();
        Map <Long, TeachingClassStatisticsDTO> scheduleRol = new HashedMap();
        NumberFormat nt = NumberFormat.getPercentInstance();
        nt.setMinimumFractionDigits(1);
        

        Set teachingClassesIds = new HashSet <>();
        Set <String> teacherIds = new HashSet();


        List <TeachingClassStatisticsDTO> list = attendanceStatisticsQuery.queryScheduleAttendance(organId, DateFormatUtil.formatShort(new Date()));
        if (null == list) {
            list = new ArrayList <>();
        }

        for (TeachingClassStatisticsDTO dto : list) {
            teachingClassesIds.add(dto.getTeachingClassesId());
            teacherIds.add(dto.getTeacherId() + "");
            scheduleRol.put(dto.getScheduleRollCallId(), dto);
            int total = Integer.valueOf(dto.getAllStudent());
            float temp = (float) Integer.valueOf(dto.getSignCount()) / (total == 0 ? 1 : total);
            String formart = nt.format(temp);
            dto.setClassRate(formart.substring(0, formart.indexOf("%")));
        }
        List <TeachingClassStatisticsDTO> tcsList = attendanceStatisticsQuery.queryScheduleAttendanceIds(organId, DateFormatUtil.formatShort(new Date()));
        if (tcsList != null & tcsList.size() > 0) {
            for (TeachingClassStatisticsDTO tc : tcsList) {
                TeachingClassStatisticsDTO teachingClassStatisticsDTO = scheduleRol.get(tc.getScheduleRollCallId());
                if (null != teachingClassStatisticsDTO) {
                    tc = teachingClassStatisticsDTO;
                }
                teachingClassesIds.add(tc.getTeachingClassesId());
                teacherIds.add(tc.getTeacherId() + "");

                List <RollCall> rollCalls = rollCallService.listRollCallBySRCIdInRedis(tc.getScheduleRollCallId());
                if (rollCalls != null && rollCalls.size() > 0) {
                    int reportCoutn = 0;
                    for (RollCall rollCall : rollCalls) {
                        if (RollCallConstants.TYPE_NORMA.equals(rollCall.getType()) || RollCallConstants.TYPE_ASK_FOR_LEAVE.equals(rollCall.getType()) || RollCallConstants.TYPE_LATE.equals(rollCall.getType())) {
                            reportCoutn++;
                        }

                    }
                    int total = rollCalls.size();
                    tc.setSignCount(reportCoutn + "");
                    tc.setAllStudent(total + "");
                    float temp = (float) reportCoutn / (total == 0 ? 1 : total);
                    String formart = nt.format(temp);
                    tc.setClassRate(formart.substring(0, formart.indexOf("%")));
                }
            }
//            list.addAll(tcsList);
        }


        //获取头像
        OauthGet get = new OauthGet();
        HashMap <String, String> listMap = null;
        HttpResponse response = null;
        String ids = String.join(",", teacherIds);
        try {
            response = get.get(configCache.getConfigValueByParm("user.service.host"),
                    configCache.getConfigValueByParm("user.service.avatarList") + "?ids=" + ids, null);
            String reg = ".*error.*";
            if (response.getResponseBody().matches(reg)) {
                response = null;
            }

            if (null != response) {
                listMap = new HashMap();
                String s = response.getResponseBody();
                JSONObject user = JSONObject.fromString(s);
                Iterator <String> iterator = user.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = user.getString(key);
                    JSONObject user_json = JSONObject.fromString(value);
                    String avatar = user_json.getString("avatar");
                    if (avatar.equals("null")) {
                        listMap.put(key, null);
                    } else {
                        listMap.put(key, avatar);
                    }
                }
            }

        } catch (IOException e) {
            log.warn("Exception", e);
            log.warn("", e);
        }

        try {
            if (teachingClassesIds.size() > 0) {
                //根据教学班查询对应的行政班
                List <ClassesCollegeDTO> ccList = orgManagerRemoteService.listClassCollege(teachingClassesIds);
                Map <Long, List> teachingClassesClassMap = new HashedMap();
                Map <Long, String> teachingClassesCollegeMap = new HashedMap();
                Map <Long, Long> teachingClassesCollegeIdMap = new HashedMap();
                if (null != ccList && ccList.size() > 0) {
                    for (ClassesCollegeDTO dto : ccList) {
                        if (!teachingClassesClassMap.containsKey(dto.getTeachingClassId())) {
                            teachingClassesClassMap.put(dto.getTeachingClassId(), Lists.newArrayList(dto.getClassName()));
                        } else {
                            teachingClassesClassMap.get(dto.getTeachingClassId()).add(dto.getClassName());
                        }

                        teachingClassesCollegeMap.put(dto.getTeachingClassId(), dto.getCollegeName());
                        teachingClassesCollegeIdMap.put(dto.getTeachingClassId(), dto.getCollegeId());
                    }

                    for (TeachingClassStatisticsDTO dto : list) {
                        Long collegeId = teachingClassesCollegeIdMap.get(dto.getTeachingClassesId());
                        if (null == collegeId) {
                            continue;
                        }
                        if (!tempMap.containsKey(collegeId)) {
                            CollegeStatisticsDTO csDto = new CollegeStatisticsDTO();
                            csDto.setCollegeName(teachingClassesCollegeMap.get(dto.getTeachingClassesId()));
                            csDto.setClassInfo(new ArrayList <>());
                            tempMap.put(collegeId, csDto);
                            resultList.add(csDto);
                        }
                        if (null != listMap) {
                            dto.setTeacherAvatar(listMap.get(dto.getTeacherId() + ""));
                        }
                        dto.setClasses(teachingClassesClassMap.get(dto.getTeachingClassesId()));
                        tempMap.get(collegeId).getClassInfo().add(dto);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("仪表盘", e);
        }


//        for (CollegeStatisticsDTO collegeStatisticsDTO : resultList) {
//            List <TeachingClassStatisticsDTO> classInfo = collegeStatisticsDTO.getClassInfo();
//            if (null != classInfo) {
//                Collections.sort(classInfo, new Comparator <TeachingClassStatisticsDTO>() {
//                    @Override
//                    public int compare(TeachingClassStatisticsDTO o1, TeachingClassStatisticsDTO o2) {
//                        return Double.valueOf(o1.getClassRate()).doubleValue() > Double.valueOf(o2.getClassRate()).doubleValue() ? -1 : 1;
//                    }
//                });
//            }
//        }

        return resultList;
    }

    public List <TeacherAttendanceDTO> getTeacherAttendanceStatistics(Long organId) {
        List <RollCallStatisticsDTO> rcs = attendanceStatisticsQuery.queryRollCallAttendance(organId, DateFormatUtil.formatShort(new Date()));
        if (null == rcs) {
            return null;
        }
        NumberFormat nt = NumberFormat.getPercentInstance();
        nt.setMinimumFractionDigits(1);
        //正在上课的
        Map <Long, RollCallStatisticsDTO> tempMap = new HashedMap();
        List <TeachingClassStatisticsDTO> tcsList = attendanceStatisticsQuery.queryScheduleAttendanceIds(organId, DateFormatUtil.formatShort(new Date()));
        if (tcsList != null & tcsList.size() > 0) {
            for (TeachingClassStatisticsDTO tc : tcsList) {
                RollCallStatisticsDTO dto = new RollCallStatisticsDTO();
                List <RollCall> rollCalls = rollCallService.listRollCallBySRCIdInRedis(tc.getScheduleRollCallId());
                if (null != rollCalls) {
                    int signCount = 0;
                    for (RollCall rollCall : rollCalls) {
                        if (RollCallConstants.TYPE_NORMA.equals(rollCall.getType()) || RollCallConstants.TYPE_ASK_FOR_LEAVE.equals(rollCall.getType()) || RollCallConstants.TYPE_LATE.equals(rollCall.getType())) {
                            signCount++;
                        }
                    }
                    int total = rollCalls.size();
                    dto.setSignCount(signCount);
                    dto.setTotalCount(total);
                    dto.setTeacherName(tc.getTeacherName());
                    dto.setTeacherId(tc.getTeacherId());
                    String temp = nt.format((float) signCount / (total == 0 ? 1 : total));
                    dto.setRollCallRate(temp.substring(0, temp.indexOf("%")));
                }
                tempMap.put(tc.getTeacherId(), dto);
            }
        }

        //整合到课率

        for (RollCallStatisticsDTO rc : rcs) {
            if (tempMap.containsKey(rc.getTeacherId())) {
                RollCallStatisticsDTO dto = tempMap.get(rc.getTeacherId());
                if ("0.0".equals(rc.getRollCallRate())) {
                    rc.setRollCallRate(Double.valueOf(dto.getRollCallRate()).doubleValue() + "");
                } else {
                    rc.setRollCallRate(((Double.valueOf(rc.getRollCallRate()).doubleValue() + Double.valueOf(dto.getRollCallRate()).doubleValue()) / 2) + "");
                }
                rc.setSignCount(dto.getSignCount() + rc.getSignCount());
                rc.setTotalCount(dto.getTotalCount() + rc.getTotalCount());
            }
        }

        List <AssessStatisticsDTO> ass = attendanceStatisticsQuery.queryAssessAttendance(organId, DateFormatUtil.formatShort(new Date()));
        Map <Long, String> assessMap = new HashedMap();
        if (null != ass && ass.size() > 0) {
            for (AssessStatisticsDTO dto : ass) {
                assessMap.put(dto.getTeacherId(), dto.getAssesRate());
            }
        }

        List list = new ArrayList();
        for (RollCallStatisticsDTO rc : rcs) {
            TeacherAttendanceDTO tDto = new TeacherAttendanceDTO();
            tDto.setTeacherId(rc.getTeacherId());
            tDto.setTeacherName(rc.getTeacherName());
            tDto.setClassRate(rc.getRollCallRate());
            tDto.setAssessRate(assessMap.get(rc.getTeacherId()) == null ? "0" : assessMap.get(rc.getTeacherId()));
            try {
                tDto.setAvgRate((Double.valueOf(rc.getRollCallRate().substring(0, rc.getRollCallRate().indexOf("."))) + Double.valueOf(tDto.getAssessRate())) / 2);
            } catch (Exception e) {
            }
            list.add(tDto);
        }

        Collections.sort(list, new Comparator <TeacherAttendanceDTO>() {
            @Override
            public int compare(TeacherAttendanceDTO o1, TeacherAttendanceDTO o2) {
                return o1.getAvgRate() - o2.getAvgRate() > 0 ? -1 : 1;
            }
        });
        return list;
    }
}
