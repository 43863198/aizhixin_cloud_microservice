package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.PeriodDTO;
import com.aizhixin.cloud.dd.rollcall.dto.SemesterIdAndClassesSetDomain;
import com.aizhixin.cloud.dd.rollcall.dto.StudentScheduleDTO;
import com.aizhixin.cloud.dd.rollcall.dto.counsellorAttendance.ClassAttendanceDetailDTO;
import com.aizhixin.cloud.dd.rollcall.dto.counsellorAttendance.ClassesInfoDTO;
import com.aizhixin.cloud.dd.rollcall.dto.counsellorAttendance.CounsellorAttendanceDTO;
import com.aizhixin.cloud.dd.rollcall.dto.counsellorAttendance.StudentAttendanceDTO;
import com.aizhixin.cloud.dd.rollcall.entity.RollCall;
import com.aizhixin.cloud.dd.rollcall.repository.RollCallRepository;
import com.aizhixin.cloud.dd.rollcall.repository.ScheduleQuery;
import com.aizhixin.cloud.dd.rollcall.utils.CourseUtils;
import com.aizhixin.cloud.dd.rollcall.utils.RedisUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class CounsellorService {

    private final Logger log = LoggerFactory.getLogger(CounsellorService.class);
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;
    @Autowired
    private PeriodService periodService;
    @Autowired
    private WeekService weekService;
    @Autowired
    private SemesterService semesterService;
    @Autowired
    private ScheduleQuery scheduleQuery;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RollCallRepository rollCallRepository;


    public List queryClasses(Long teacherId) {
        List<IdNameDomain> classesByTeacher = orgManagerRemoteService.getClassesByTeacher(teacherId);
        if (classesByTeacher != null && classesByTeacher.size() > 1) {
            Collections.sort(classesByTeacher, new Comparator<IdNameDomain>() {
                @Override
                public int compare(IdNameDomain o1, IdNameDomain o2) {
                    return o1.getId() > o2.getId() ? 1 : -1;
                }
            });
        }
        return classesByTeacher;
    }

    private String getCurrentPeriodStr(List<PeriodDTO> periodDTOS, Date date) {
        List<Integer> pno = new ArrayList<>();
        Map<Integer, PeriodDTO> pmap = new HashMap<>();
        String curTime = DateFormatUtil.format(date, "HH:mm");
        for (PeriodDTO periodDTO : periodDTOS) {
            if (null != periodDTO.getNo()) {
                pno.add(periodDTO.getNo());
                pmap.put(periodDTO.getNo(), periodDTO);
            }
        }
        if (pno.size() <= 0) {
            return "第 - 节";
        }
        Collections.sort(pno);// 按照课程节的顺序进行排序

        // 首先判断是否在学校的课节的最小最大时间范围内
        PeriodDTO first = pmap.get(pno.get(0));
        PeriodDTO last = pmap.get(pno.get(pno.size() - 1));
        PeriodDTO currentPeroid = null;
        PeriodDTO neibPeroid = null;
        if (curTime.compareTo(first.getStartTime()) < 0) {// 第一节课之前(给前两节时间)
            currentPeroid = pmap.get(pno.get(0));
            if (pno.size() >= 1) {
                neibPeroid = pmap.get(pno.get(1));
            }
        } else if (curTime.compareTo(last.getEndTime()) > 0) {// 最后一节之后(给最后两节的)
            neibPeroid = pmap.get(pno.get(pno.size() - 1));
            if (pno.size() >= 1) {
                currentPeroid = pmap.get(pno.get(pno.size() - 2));
            }
        } else {// 第一节到最后一节之间
            int p = 0;
            currentPeroid = pmap.get(pno.get(0));
            while (p + 2 < pno.size()) {
                PeriodDTO next = pmap.get(pno.get(p + 2));
                if (null != next) {
                    if (curTime.compareTo(next.getStartTime()) < 0) {
                        break;
                    }
                    currentPeroid = next;
                }
                p += 2;
            }
            if (p + 1 < pno.size()) {
                neibPeroid = pmap.get(pno.get(p + 1));
            }
        }
        StringBuilder r = new StringBuilder();
        r.append("第");
        if (null != currentPeroid) {
            r.append(currentPeroid.getNo());
        }
        r.append("-");
        if (null != neibPeroid) {
            r.append(neibPeroid.getNo());
        } else {
            if (null != currentPeroid) {
                r.append(currentPeroid.getNo());
            }
        }
        r.append("节");
        return r.toString();
    }

    public Long getCurrentPeriodId(List<PeriodDTO> periodDTOS, Date date) {
        List<Integer> pno = new ArrayList<>();
        Map<Integer, PeriodDTO> pmap = new HashMap<>();
        String curTime = DateFormatUtil.format(date, "HH:mm");
        for (PeriodDTO periodDTO : periodDTOS) {
            if (null != periodDTO.getNo()) {
                pno.add(periodDTO.getNo());
                pmap.put(periodDTO.getNo(), periodDTO);
            }
        }
        if (pno.size() <= 0) {
            return null;
        }
        Collections.sort(pno);// 按照课程节的顺序进行排序

        // 首先判断是否在学校的课节的最小最大时间范围内
        PeriodDTO first = pmap.get(pno.get(0));
        PeriodDTO last = pmap.get(pno.get(pno.size() - 1));
        PeriodDTO currentPeroid = null;
        PeriodDTO neibPeroid = null;
        if (curTime.compareTo(first.getStartTime()) < 0) {// 第一节课之前(给前两节时间)
            currentPeroid = pmap.get(pno.get(0));
            if (pno.size() >= 1) {
                neibPeroid = pmap.get(pno.get(1));
            }
        } else if (curTime.compareTo(last.getEndTime()) > 0) {// 最后一节之后(给最后两节的)
            neibPeroid = pmap.get(pno.get(pno.size() - 1));
            if (pno.size() >= 1) {
                currentPeroid = pmap.get(pno.get(pno.size() - 2));
            }
        } else {// 第一节到最后一节之间
            int p = 0;
            currentPeroid = pmap.get(pno.get(0));
            while (p + 2 < pno.size()) {
                PeriodDTO next = pmap.get(pno.get(p + 2));
                if (null != next) {
                    if (curTime.compareTo(next.getStartTime()) < 0) {
                        break;
                    }
                    currentPeroid = next;
                }
                p += 2;
            }
            if (p + 1 < pno.size()) {
                neibPeroid = pmap.get(pno.get(p + 1));
            }
        }
        if (currentPeroid != null) {
            return currentPeroid.getId();
        }
        return null;
    }

    public List<PeriodDTO> getPeriodList(Long orgId) {
        List<PeriodDTO> periodDTOS = periodService.listPeriod(orgId);
        return periodDTOS;
    }

    /**
     * 及时到课率
     *
     * @param teacherId
     * @return
     */
    public Map timelyAttendance(Long teacherId, Long orgId, Long periodId) {
        if (periodId == null || periodId < 1) {
            return null;
        }
        Map<String, Object> result = new HashedMap();
        CounsellorAttendanceDTO counsellorAttendanceDTO = new CounsellorAttendanceDTO();
        // 判断当前时间是否有课
        List<PeriodDTO> periodDTOS = periodService.listPeriod(orgId);
        if (null == periodDTOS) {
            return null;
        }
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        for (PeriodDTO p : periodDTOS) {
            if (p.getId().intValue() == periodId.intValue()) {
                try {
                    date = df.parse(p.getStartTime());
                    Calendar currCalendar = Calendar.getInstance();
                    currCalendar.setTime(new Date());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.set(Calendar.YEAR, currCalendar.get(Calendar.YEAR));
                    calendar.set(Calendar.MONTH, currCalendar.get(Calendar.MONTH));
                    calendar.set(Calendar.DAY_OF_MONTH, currCalendar.get(Calendar.DAY_OF_MONTH));
                    calendar.set(Calendar.MINUTE, currCalendar.get(Calendar.MINUTE) + 5);
                } catch (ParseException e) {
                    log.warn("Exception", e);
                    date = null;
                }
                break;
            }
        }
        if (date == null) {
            return null;
        }
        // 潘震修改当前节计算
        String currentPeriodStr = getCurrentPeriodStr(periodDTOS, date);
        counsellorAttendanceDTO.setCurrentPeriod(currentPeriodStr);
        counsellorAttendanceDTO.setDayOfWeek(DateFormatUtil.getWeekOfDate(DateFormatUtil.formatShort(date)) + "");
        counsellorAttendanceDTO.setCurrentDate(DateFormatUtil.formatShort(date));

        IdNameDomain currentSemester = semesterService.getCurrentSemester(orgId);
        if (currentSemester.getId() == 0) {
            result.put(ApiReturnConstants.MESSAGE, "不在学期内。");
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            return result;
        }
        List<IdNameDomain> classesByTeacher = orgManagerRemoteService.getClassesByTeacher(teacherId);
        if (classesByTeacher != null && classesByTeacher.size() > 0) {
            Map<Long, String> classIdAndNameMap = new HashedMap();
            Set<Long> classSet = new HashSet();
            for (IdNameDomain domain : classesByTeacher) {
                classSet.add(domain.getId());
                classIdAndNameMap.put(domain.getId(), domain.getName());
            }
            String teachingclassandclasses = orgManagerRemoteService.teachingclassandclassesall(new SemesterIdAndClassesSetDomain(classSet, currentSemester.getId()));
            if (StringUtils.isBlank(teachingclassandclasses)) {
                return null;
            }
            JSONArray jsonArray = JSONArray.fromObject(teachingclassandclasses);
            if (null == jsonArray || jsonArray.length() == 0) {
                return null;
            }
            Map<Long, String> teachingClassIdAndNameMap = new HashedMap();
            Map<Long, Set> classesIdAndTeachingClassSetMap = new HashedMap();
            Set teachingClassIds = new HashSet();
            Set<String> teachingClassIdStrs = new HashSet<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                long classId = jsonObject.getLong("classesId");
                String className = jsonObject.getString("classesName");
                long teachClassId = jsonObject.getLong("teachingclassId");
                String teachingClassName = jsonObject.getString("teachingclassName");

                teachingClassIdAndNameMap.put(teachClassId, teachingClassName);

                Set set = classesIdAndTeachingClassSetMap.get(classId);
                if (null == set) {
                    set = new HashSet();
                    classesIdAndTeachingClassSetMap.put(classId, set);
                }
                set.add(teachClassId);
                teachingClassIds.add(teachClassId);
                teachingClassIdStrs.add(String.valueOf(teachClassId));
            }
            // 根据教学班 id 查询当前时间正在上的课程
            SimpleDateFormat dfdateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            List<StudentScheduleDTO> studentSignCourse = scheduleQuery.getStudentSignCourseByDate(teachingClassIds, dfdateTime.format(date), StringUtils.join(teachingClassIdStrs.toArray(), ",") + df.format(date));

            Map<Long, StudentScheduleDTO> scheduleMap = new HashedMap();
            if (studentSignCourse != null && studentSignCourse.size() > 0) {
                for (StudentScheduleDTO studentScheduleDTO : studentSignCourse) {
                    scheduleMap.put(studentScheduleDTO.getTeachingClassId(), studentScheduleDTO);
                }
            }

            List<Long> classList = new ArrayList();
            classList.addAll(classSet);
            Collections.sort(classList);

            List<ClassesInfoDTO> resultList = new ArrayList();
            for (Long classId : classList) {
                ClassesInfoDTO classesInfoDto = new ClassesInfoDTO();
                classesInfoDto.setClassId(classId);
                classesInfoDto.setClassName(classIdAndNameMap.get(classId));
                int needSchedule = 0;
                int commitStudent = 0;
                int uncommitStudent = 0;
                Set<Long> set = classesIdAndTeachingClassSetMap.get(classId);
                List<ClassAttendanceDetailDTO> classAttendanceDetailList = new ArrayList<>();
                if (set != null && set.size() > 0) {
                    for (Long teachingClassId : set) {
                        StudentScheduleDTO studentScheduleDTO = scheduleMap.get(teachingClassId);
                        if (studentScheduleDTO == null) {
                            continue;
                        }
                        ClassAttendanceDetailDTO classAttendanceDetailDTO = new ClassAttendanceDetailDTO();
                        classAttendanceDetailDTO.setTeachingClassName(studentScheduleDTO.getTeachingClassName());
                        classAttendanceDetailDTO.setTeacher(studentScheduleDTO.getTeacher());

                        long begin = System.currentTimeMillis();

                        boolean inClass = (DateFormatUtil.getNow(DateFormatUtil.FORMAT_SHORT).equals(studentScheduleDTO.getTeach_time()) && CourseUtils.classBeginTime(studentScheduleDTO.getClassBeginTime())
                                && CourseUtils.classEndTime(studentScheduleDTO.getClassEndTime()));

                        List<RollCall> rollcalls = null;
                        if (inClass) {
                            rollcalls = redisTemplate.opsForHash().values(RedisUtil.getScheduleRollCallKey(studentScheduleDTO.getScheduleRollCallId()));
                        } else {
                            rollcalls = rollCallRepository.findByScheduleRollcallId(studentScheduleDTO.getScheduleRollCallId());
                        }
                        long end = System.currentTimeMillis() - begin;
                        log.info("根据教学id查询学生考勤耗时:" + end);
                        if (rollcalls != null && rollcalls.size() > 0) {
                            List<StudentAttendanceDTO> studentAttendanceList = new ArrayList<>();
                            for (RollCall rollcall : rollcalls) {
                                if (rollcall.getClassId().longValue() == classId.longValue()) {
                                    StudentAttendanceDTO studentAttendanceDTO = new StudentAttendanceDTO();
                                    studentAttendanceDTO.setStudentName(rollcall.getStudentName());
                                    studentAttendanceDTO.setJobNumber(rollcall.getStudentNum());
                                    studentAttendanceDTO.setType(rollcall.getType());
                                    studentAttendanceDTO.setSignTime(rollcall.getSignTime() == null ? "" : df.format(rollcall.getSignTime()));
                                    studentAttendanceDTO.setDistance(rollcall.getDistance() == null ? "" : rollcall.getDistance());
                                    studentAttendanceDTO.setIsPublicLeave(rollcall.getIsPublicLeave());
                                    if (inClass) {
                                        if ("7".equals(rollcall.getType())) {
                                            uncommitStudent++;
                                        } else {
                                            commitStudent++;
                                        }
                                    } else {
                                        if (rollcall.getHaveReport()) {
                                            commitStudent++;
                                        } else {
                                            uncommitStudent++;
                                        }
                                    }
                                    studentAttendanceList.add(studentAttendanceDTO);
                                }
                            }
                            Collections.sort(studentAttendanceList);
                            classAttendanceDetailDTO.setStudentAttendance(studentAttendanceList);
                        }
                        classAttendanceDetailList.add(classAttendanceDetailDTO);
                        needSchedule++;
                    }
                }
                classesInfoDto.setCourseNum(needSchedule);
                classesInfoDto.setCommitStudent(commitStudent);
                classesInfoDto.setUncommitStudent(uncommitStudent);
                classesInfoDto.setTotalStudent(commitStudent + uncommitStudent);
                classesInfoDto.setClassAttendanceDetail(classAttendanceDetailList);
                resultList.add(classesInfoDto);
            }
            counsellorAttendanceDTO.setClassInfo(resultList);
        }
        result.put("data", counsellorAttendanceDTO);
        result.put(ApiReturnConstants.MESSAGE, "");
        result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        return result;
    }

    /**
     * 及时到课率
     *
     * @param teacherId
     * @return
     */
    public Map timelyAttendance(Long teacherId, Long orgId) {

        Map<String, Object> result = new HashedMap();
        CounsellorAttendanceDTO counsellorAttendanceDTO = new CounsellorAttendanceDTO();
        // 判断当前时间是否有课
        List<PeriodDTO> periodDTOS = periodService.listPeriod(orgId);
        if (null == periodDTOS) {
            return null;
        }
        // 潘震修改当前节计算
        String currentPeriodStr = getCurrentPeriodStr(periodDTOS, new Date());

        counsellorAttendanceDTO.setCurrentPeriod(currentPeriodStr);
        counsellorAttendanceDTO.setDayOfWeek(DateFormatUtil.getWeekOfDate(DateFormatUtil.formatShort(new Date())) + "");
        counsellorAttendanceDTO.setCurrentDate(DateFormatUtil.formatShort(new Date()));

        IdNameDomain currentSemester = semesterService.getCurrentSemester(orgId);
        if (currentSemester.getId() == 0) {
            result.put(ApiReturnConstants.MESSAGE, "不在学期内。");
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            return result;
        }

        List<IdNameDomain> classesByTeacher = orgManagerRemoteService.getClassesByTeacher(teacherId);
        if (classesByTeacher != null && classesByTeacher.size() > 0) {
            Map<Long, String> classIdAndNameMap = new HashedMap();
            Set<Long> classSet = new HashSet();
            for (IdNameDomain domain : classesByTeacher) {
                classSet.add(domain.getId());
                classIdAndNameMap.put(domain.getId(), domain.getName());

            }

            String teachingclassandclasses = orgManagerRemoteService.teachingclassandclassesall(new SemesterIdAndClassesSetDomain(classSet, currentSemester.getId()));
            if (StringUtils.isBlank(teachingclassandclasses)) {
                return null;
            }

            JSONArray jsonArray = JSONArray.fromObject(teachingclassandclasses);
            if (null == jsonArray || jsonArray.length() == 0) {
                return null;
            }

            Map<Long, String> teachingClassIdAndNameMap = new HashedMap();
            Map<Long, Set> classesIdAndTeachingClassSetMap = new HashedMap();
            Set teachingClassIds = new HashSet();
            Set<String> teachingClassIdStrs = new HashSet<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                long classId = jsonObject.getLong("classesId");
                String className = jsonObject.getString("classesName");
                long teachClassId = jsonObject.getLong("teachingclassId");
                String teachingClassName = jsonObject.getString("teachingclassName");

                teachingClassIdAndNameMap.put(teachClassId, teachingClassName);

                Set set = classesIdAndTeachingClassSetMap.get(classId);
                if (null == set) {
                    set = new HashSet();
                    classesIdAndTeachingClassSetMap.put(classId, set);
                }
                set.add(teachClassId);
                teachingClassIds.add(teachClassId);
                teachingClassIdStrs.add(String.valueOf(teachClassId));
            }

            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            // 根据教学班 id 查询当前时间正在上的课程
            List<StudentScheduleDTO> studentSignCourse = scheduleQuery.getStudentSignCourse(teachingClassIds, StringUtils.join(teachingClassIdStrs.toArray(), ","));

            Map<Long, StudentScheduleDTO> scheduleMap = new HashedMap();
            if (studentSignCourse != null && studentSignCourse.size() > 0) {
                for (StudentScheduleDTO studentScheduleDTO : studentSignCourse) {
                    scheduleMap.put(studentScheduleDTO.getTeachingClassId(), studentScheduleDTO);
                }
            }

            List<Long> classList = new ArrayList();
            classList.addAll(classSet);
            Collections.sort(classList);

            List<ClassesInfoDTO> resultList = new ArrayList();
            for (Long classId : classList) {
                ClassesInfoDTO classesInfoDto = new ClassesInfoDTO();
                classesInfoDto.setClassId(classId);
                classesInfoDto.setClassName(classIdAndNameMap.get(classId));
                int needSchedule = 0;
                int commitStudent = 0;
                int uncommitStudent = 0;
                Set<Long> set = classesIdAndTeachingClassSetMap.get(classId);
                List<ClassAttendanceDetailDTO> classAttendanceDetailList = new ArrayList<>();
                if (set != null && set.size() > 0) {
                    for (Long teachingClassId : set) {
                        StudentScheduleDTO studentScheduleDTO = scheduleMap.get(teachingClassId);
                        if (studentScheduleDTO == null) {
                            continue;
                        }

                        ClassAttendanceDetailDTO classAttendanceDetailDTO = new ClassAttendanceDetailDTO();
                        classAttendanceDetailDTO.setTeachingClassName(studentScheduleDTO.getTeachingClassName());
                        classAttendanceDetailDTO.setTeacher(studentScheduleDTO.getTeacher());

                        long begin = System.currentTimeMillis();

                        List<RollCall> rollcalls = redisTemplate.opsForHash().values(RedisUtil.getScheduleRollCallKey(studentScheduleDTO.getScheduleRollCallId()));
                        long end = System.currentTimeMillis() - begin;
                        log.info("根据教学id查询学生考勤耗时:" + end);
                        if (rollcalls != null && rollcalls.size() > 0) {
                            List<StudentAttendanceDTO> studentAttendanceList = new ArrayList<>();
                            for (RollCall rollcall : rollcalls) {
                                if (rollcall.getClassId().longValue() == classId.longValue()) {
                                    StudentAttendanceDTO studentAttendanceDTO = new StudentAttendanceDTO();
                                    studentAttendanceDTO.setStudentName(rollcall.getStudentName());
                                    studentAttendanceDTO.setJobNumber(rollcall.getStudentNum());
                                    studentAttendanceDTO.setType(rollcall.getType());
                                    studentAttendanceDTO.setSignTime(rollcall.getSignTime() == null ? "" : df.format(rollcall.getSignTime()));
                                    studentAttendanceDTO.setDistance(rollcall.getDistance() == null ? "" : rollcall.getDistance());
                                    studentAttendanceDTO.setIsPublicLeave(rollcall.getIsPublicLeave());
                                    if ("7".equals(rollcall.getType())) {
                                        uncommitStudent++;
                                    } else {
                                        commitStudent++;
                                    }
                                    studentAttendanceList.add(studentAttendanceDTO);
                                }
                            }
                            Collections.sort(studentAttendanceList);
                            classAttendanceDetailDTO.setStudentAttendance(studentAttendanceList);
                        }
                        classAttendanceDetailList.add(classAttendanceDetailDTO);
                        needSchedule++;
                    }
                }

                classesInfoDto.setCourseNum(needSchedule);
                classesInfoDto.setCommitStudent(commitStudent);
                classesInfoDto.setUncommitStudent(uncommitStudent);
                classesInfoDto.setTotalStudent(commitStudent + uncommitStudent);
                classesInfoDto.setClassAttendanceDetail(classAttendanceDetailList);
                resultList.add(classesInfoDto);
            }
            counsellorAttendanceDTO.setClassInfo(resultList);

        }

        result.put("data", counsellorAttendanceDTO);
        result.put(ApiReturnConstants.MESSAGE, "");
        result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        return result;
    }

}