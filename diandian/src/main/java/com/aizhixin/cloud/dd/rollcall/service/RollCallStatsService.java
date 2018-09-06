package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.common.provider.store.redis.RedisTokenStore;
import com.aizhixin.cloud.dd.homepage.service.HomePagePhoneService;
import com.aizhixin.cloud.dd.orgStructure.entity.OrgInfo;
import com.aizhixin.cloud.dd.orgStructure.entity.TeachingClass;
import com.aizhixin.cloud.dd.orgStructure.entity.TeachingClassStudent;
import com.aizhixin.cloud.dd.orgStructure.repository.OrgInfoRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.TeachingClassRepository;
import com.aizhixin.cloud.dd.orgStructure.repository.TeachingClassStudentRepository;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.JdbcTemplate.RollCallStatsJdbc;
import com.aizhixin.cloud.dd.rollcall.entity.OrganSet;
import com.aizhixin.cloud.dd.rollcall.entity.RollCall;
import com.aizhixin.cloud.dd.rollcall.repository.OrganSetRepository;
import com.aizhixin.cloud.dd.rollcall.repository.RollCallRepository;
import com.aizhixin.cloud.dd.rollcall.utils.RollCallUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RollCallStatsService {
    private final Logger log = LoggerFactory.getLogger(RollCallStatsService.class);
    @Autowired
    private OrgInfoRepository orgInfoRepository;
    @Autowired
    private TeachingClassRepository teachingClassRepository;
    @Autowired
    private TeachingClassStudentRepository studentRepository;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteClient;
    @Autowired
    private RollCallStatsJdbc rollCallStatsJdbc;
    @Autowired
    private RollCallRepository rollCallRepository;
    @Autowired
    private RedisTokenStore redisTokenStore;
    @Autowired
    private HomePagePhoneService homePagePhoneService;
    @Autowired
    private OrganSetRepository organSetRepository;

    public Map<String, Object> getStuStatsAll(Long stuId) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> data = redisTokenStore.getStudentRollCallStatsAll(stuId);
        result.put("data", data);
        return result;
    }

    public Map<String, Object> getStuTeachingClassStats(Long stuId, Long teachingClassId) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> data = redisTokenStore.getStudentRollCallTeachingClassStats(teachingClassId.toString() + stuId.toString());
        result.put("data", data);
        return result;
    }

    public Map<String, Object> getTeachingClassStats(Long teachingClassId) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> statsData = redisTokenStore.getRollCallTeachingClassStats(teachingClassId.toString());
        result.put("stats", statsData);
        List<Map<String, Object>> data = redisTokenStore.getRollCallTeachingClassStatsHistory(teachingClassId.toString());
        result.put("data", data);
        return result;
    }

    public void initStatsData() {
        List<OrgInfo> orgInfoList = orgInfoRepository.findAll();
        if (orgInfoList != null && orgInfoList.size() > 0) {
            log.info("开始初始化考勤统计");
            for (OrgInfo org : orgInfoList) {
                initStatsDataByOrg(org.getOrgId());
            }
            log.info("完成初始化考勤统计");
        }
    }

    public void initStuTotalRollCallStatsByTeachingClass(Long orgId, Set<Long> teachingClassIds) {
        if (teachingClassIds != null && teachingClassIds.size() > 0) {
            Map<String, Object> semesterMap = orgManagerRemoteClient.getorgsemester(orgId, null);
            if (semesterMap != null && semesterMap.get("id") != null) {
                Long semesterId = Long.parseLong(semesterMap.get("id").toString());
                List<TeachingClassStudent> students = studentRepository.findByTeachingClassIdIn(teachingClassIds);
                if (students != null && students.size() > 0) {
                    for (TeachingClassStudent student : students) {
                        statsStuAllByStuId(orgId, semesterId, student.getStuId());
                    }
                }
            }
        }


        List<OrgInfo> orgInfoList = orgInfoRepository.findAll();
        if (orgInfoList != null && orgInfoList.size() > 0) {
            log.info("开始初始化考勤统计");
            for (OrgInfo org : orgInfoList) {
                initStatsDataByOrg(org.getOrgId());
            }
            log.info("完成初始化考勤统计");
        }
    }

    @Async("threadPool1")
    public void initStatsDataByOrg(Long orgId) {
        log.info("初始化考勤统计:" + orgId);
        Map<String, Object> semesterMap = orgManagerRemoteClient.getorgsemester(orgId, null);
        if (semesterMap != null && semesterMap.get("id") != null) {
            Long semesterId = Long.parseLong(semesterMap.get("id").toString());
            if (semesterId != null) {
                statsStuAll(orgId, semesterId);
                statsStuTeachingClass(orgId);
                statsTeachingClass(orgId, semesterId);
                statsTeachingClassHistory(orgId, semesterId);
            }
        }
        log.info("完成考勤统计:" + orgId);
    }

    public void initStatsDateByRollCallIds(Set<Long> rollCallIds) {
        List<RollCall> rollCallList = rollCallRepository.findByIdIn(rollCallIds);
        if (rollCallList != null && rollCallList.size() > 0) {
            RollCall rollCall = rollCallList.get(0);
            statsStuByTeachingClass(rollCall.getOrgId(), rollCall.getSemesterId(), rollCall.getTeachingClassId());
            statsStuAllByRollCallList(rollCallList);
        }
    }

    /**
     * 学生学期累计 统计全部学生
     *
     * @param orgId
     * @param semesterId
     */
    private void statsStuAll(Long orgId, Long semesterId) {
        try {
            List<Map<String, Object>> list = rollCallStatsJdbc.getStuStats(orgId, semesterId);
            if (list != null && list.size() > 0) {
                for (Map<String, Object> item : list) {
                    redisTokenStore.setStudentRollCallStatsAll(Long.parseLong(item.get("STUDENT_ID").toString()), item);
                }
            }
        } catch (Exception e) {
            log.warn("statsStuAllException", e);
        }
    }

    /**
     * 学生学期累计 统计单个学生
     *
     * @param orgId
     * @param semesterId
     * @param stuId
     */
    @Async("threadPool1")
    public void statsStuAllByStuId(Long orgId, Long semesterId, Long stuId) {
        try {
            List<Map<String, Object>> list = rollCallStatsJdbc.getStuStatsByStuId(orgId, semesterId, stuId);
            if (list != null && list.size() > 0) {
                for (Map<String, Object> item : list) {
                    redisTokenStore.setStudentRollCallStatsAll(Long.parseLong(item.get("STUDENT_ID").toString()), item);
                }
            }
        } catch (Exception e) {
            log.warn("statsStuAllByStuId", e);
        }
    }

    public void statsStuAllByScheduleRollCallId(Long scheduleRollCallId) {
        List<RollCall> rollCallList = rollCallRepository.findByScheduleRollcallId(scheduleRollCallId);
        statsStuAllByRollCallList(rollCallList);
    }

    private void statsStuAllByRollCallList(List<RollCall> rollCallList) {
        try {
            if (rollCallList != null && rollCallList.size() > 0) {
                for (RollCall rollCall : rollCallList) {
                    statsStuAllByStuId(rollCall.getOrgId(), rollCall.getSemesterId(), rollCall.getStudentId());
                }
            }
        } catch (Exception e) {
            log.warn("statsStuAllByRollCallListException", e);
        }
    }

    @Async
    public void statsStuByTeachingClass(Long orgId, Long semesterId, Long teachingClassId) {
        try {
            List<Map<String, Object>> list = rollCallStatsJdbc.getStuStatsByTeachingClassId(orgId, semesterId, teachingClassId);
            if (list != null && list.size() > 0) {
                for (Map<String, Object> item : list) {
                    redisTokenStore.setStudentRollCallStatsAll(Long.parseLong(item.get("STUDENT_ID").toString()), item);
                }
            }
        } catch (Exception e) {
            log.warn("statsStuByTeachingClassException", e);
        }
    }

    /**
     * 统计学生的教学班出勤率
     *
     * @param orgId
     */
    private void statsStuTeachingClass(Long orgId) {
        try {
            Integer type = getStatsType(orgId);
            List<Map<String, Object>> list = rollCallStatsJdbc.getStuTeachingClassStats(orgId);
            if (list != null && list.size() > 0) {
                for (Map<String, Object> item : list) {
                    calculateRate(item, type);
                    redisTokenStore.setStudentRollCallTeachingClassStats(item.get("TEACHINGCLASS_ID").toString() + item.get("STUDENT_ID"), item);
                }
            }
        } catch (Exception e) {
            log.warn("statsStuTeachingClassException", e);
        }
    }

    /**
     * 统计教学班出勤率
     *
     * @param orgId
     * @param semesterId
     */
    private void statsTeachingClass(Long orgId, Long semesterId) {
        try {
            Integer type = getStatsType(orgId);
            List<Map<String, Object>> list = rollCallStatsJdbc.getTeachingClassStats(orgId, semesterId);
            if (list != null && list.size() > 0) {
                for (Map<String, Object> item : list) {
                    calculateRate(item, type);
                    redisTokenStore.setRollCallTeachingClassStats(item.get("TEACHINGCLASS_ID").toString(), item);
                }
            }
        } catch (Exception e) {
            log.warn("statsTeachingClassException", e);
        }
    }

    /**
     * 统计教学班出勤率历史
     *
     * @param orgId
     * @param semesterId
     */
    private void statsTeachingClassHistory(Long orgId, Long semesterId) {
        try {
            Integer type = getStatsType(orgId);
            List<TeachingClass> teachingClassList = teachingClassRepository.findByOrgIdAndSemesterId(orgId, semesterId);
            if (teachingClassList != null && teachingClassList.size() > 0) {
                for (TeachingClass teachingClass : teachingClassList) {
                    List<Map<String, Object>> list = rollCallStatsJdbc.getTeachingClassStatsHistory(teachingClass.getTeachingClassId(), semesterId);
                    if (list != null && list.size() > 0) {
                        for (Map<String, Object> item : list) {
                            calculateRate(item, type);
                        }
                        redisTokenStore.setRollCallTeachingClassStatsHistory(teachingClass.getTeachingClassId().toString(), list);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("statsTeachingClassHistoryException", e);
        }
    }

    private void calculateRate(Map<String, Object> data, Integer type) {
        String result = RollCallUtils.AttendanceAccount(Integer.parseInt(data.get("totalcount").toString()), Integer.parseInt(data.get("normacount").toString()), Integer.parseInt(data.get("latecount").toString()), Integer.parseInt(data.get("askforleavecount").toString()), Integer.parseInt(data.get("truancycount").toString()), type);
        if (!StringUtils.isEmpty(result)) {
            result = result.replace("%", "");
        }
        data.put("attendance", result);
    }

    private Integer getStatsType(Long orgId) {
        OrganSet organSet = organSetRepository.findByOrganId(orgId);
        if (organSet != null && organSet.getArithmetic() != null) {
            return organSet.getArithmetic();
        }
        return 10;
    }
}
