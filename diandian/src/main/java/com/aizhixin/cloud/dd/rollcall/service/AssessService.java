package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.ModuleConstants;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.CountDomain;
import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.constant.AssessConstants;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.*;
import com.aizhixin.cloud.dd.rollcall.entity.Assess;
import com.aizhixin.cloud.dd.rollcall.entity.Schedule;
import com.aizhixin.cloud.dd.rollcall.repository.AssessRepository;
import com.aizhixin.cloud.dd.rollcall.repository.ScheduleRepository;
import com.aizhixin.cloud.dd.rollcall.repository.TeacherAssessQuery;
import com.aizhixin.cloud.dd.rollcall.utils.CourseUtils;
import com.google.common.collect.Sets;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class AssessService {

    @Autowired
    private AssessRepository assessRepository;

    @Autowired
    private ScheduleRollCallService scheduleRollCallService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;
    @Autowired
    private TeacherAssessQuery teacherAssessQuery;

    public List<AssessDetailStudentDTO> queryDetail(Long teacherId, Long courseId, Date teachTime, Long scheduleId) {
        SimpleDateFormat edf = new SimpleDateFormat("yyyy-MM-dd");
        List<AssessDetailStudentDTO> list = teacherAssessQuery.queryDetail(teacherId, courseId, edf.format(teachTime), scheduleId);
        if (list != null && list.size() > 0) {
            Set<Long> studentIds = new HashSet<>();
            for (AssessDetailStudentDTO assessDetailStudentDTO : list) {
                studentIds.add(assessDetailStudentDTO.getStudentId());
            }

            Map<Long, String> map = new HashMap();
            if (studentIds.size() > 0) {
                String userByIds = orgManagerRemoteService.getStudentByIds(studentIds);
                if (StringUtils.isNotBlank(userByIds)) {
                    JSONArray jsonArray = JSONArray.fromObject(userByIds);
                    for (int i = 0, jsonLength = jsonArray.length(); i < jsonLength; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (null != jsonObject) {
                            map.put(jsonObject.getLong("id"), jsonObject.getString("classesName"));
                        }
                    }
                }
            }

            for (AssessDetailStudentDTO assessDetailStudentDTO : list) {
                assessDetailStudentDTO.setClassName(map.get(assessDetailStudentDTO.getStudentId()));
            }
        }
        return list;
    }

    public ResponseEntity<?> save(AssessDTO assessDao) {

        Map<String, String> res = new HashMap<String, String>();
        Assess assess = new Assess();

        if (null == assessDao.getScheduleId()
                || null == assessDao.getStudentId()) {
            res.put(ApiReturnConstants.CODE, AssessConstants.EMPTY_SCHEDULE_ID);
            res.put(ApiReturnConstants.MESSAGE,
                    "Schedule Id and studentId is required");
            return new ResponseEntity<Map<String, String>>(res, HttpStatus.OK);
        } else {
            Schedule schedule = scheduleService.findOne(assessDao.getScheduleId());

            if (null == schedule.getScheduleRollCall()) {
                res.put("errCode", AssessConstants.EMPTY_SCHEDULE);
                res.put("errMsg",
                        "It`s not found schedule by id:"
                                + assessDao.getScheduleId());
                return new ResponseEntity<Map<String, String>>(res,
                        HttpStatus.OK);
            }
            assess.setSemesterId(schedule.getSemesterId());
            assess.setCourseId(schedule.getCourseId());
            List<Assess> datas = assessRepository
                    .findByStudentIdAndScheduleId(
                            assessDao.getStudentId(),
                            schedule.getId());
            if (datas.size() > 0) {
                assess = datas.get(datas.size() - 1);
            }
            if (null == assess.getId() || assess.getId() <= 0) {
                assess.setScheduleId(schedule.getId());
                //评论来源id
                assess.setSourseId(schedule.getId());
                assess.setAnonymity(false);
                assess.setModule(ModuleConstants.eval);
                assess.setSemesterId(schedule.getSemesterId());
                assess.setTeacherId(schedule.getTeacherId());
                assess.setCourseId(schedule.getCourseId());
                assess.setStudentId(assessDao.getStudentId());
                //添加评论者id
                assess.setCommentId(schedule.getCourseId());

                // 该处存放行政班ID
                Long classId = assessDao.getClassesId();
                if (null == classId || 0 == classId.longValue()) {
                    Map<String, Object> studentMap = orgManagerRemoteService.getStudentById(assessDao.getStudentId());
                    Map<String, Object> teacherMap = orgManagerRemoteService.getStudentById(assessDao.getTeacherId());
                    classId = ((Integer) studentMap.get("id")).longValue();
                    if(null!=studentMap.get("name")){
                        assess.setCommentName(studentMap.get("name").toString());
                        assess.setStuName(studentMap.get("name").toString());
                    }
                    if(null!=teacherMap.get("name")){
                        assess.setTeacherName(teacherMap.get("name").toString());
                    }

                }
                assess.setClassId(classId);
            }
        }
        if (null == assessDao.getScore() || 0 > assessDao.getScore()
                || 5 < assessDao.getScore()) {
            res.put("errCode", AssessConstants.SCORE_ERR);
            res.put("errMsg", "score is >= 0 and <= 5");
            return new ResponseEntity<Map<String, String>>(res, HttpStatus.OK);
        }

        assess.setContent(assessDao.getContent());
        assess.setScore(assessDao.getScore());
        assess.setRevertTotal(0);
        assessRepository.save(assess);
        res.put("errCode", "0");
        res.put("errMsg", "OK");
        res.put("id", assess.getId().toString());
        return new ResponseEntity<Map<String, String>>(res, HttpStatus.OK);
    }

    /**
     * 获取特定学生某堂课的评价信息
     *
     * @param scheduleId
     * @param studentId
     * @return
     */
    @Transactional(readOnly = true)
    public AssessPageInfo<List<Map<String, String>>> getMyAssess(
            Long scheduleId, Long studentId) {
        //查询评教信息

        List<Assess> datas = assessRepository
                .findByStudentIdAndScheduleId(
                        studentId,
                        scheduleId);
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (null != datas) {
            for (Assess a : datas) {
                Map<String, String> d = new HashMap<String, String>();
                d.put("score", a.getScore().toString());
                d.put("content", a.getContent());
                d.put("createDate",
                        null != a.getCreatedDate() ? DateFormatUtil.format(
                                a.getCreatedDate(), DateFormatUtil.FORMAT_LONG)
                                : "");
                list.add(d);
            }
        }
        AssessPageInfo<List<Map<String, String>>> info = new AssessPageInfo<List<Map<String, String>>>();
        info.setData(list);
        return info;
    }

    /**
     * 获取学生未评教课程
     *
     * @param limit
     * @param offset
     * @param studentId
     * @return
     */
    public PageInfo getNotAssessList(Integer limit, Integer offset,
                                     Long studentId) {
        PageInfo page = null;
        // notAssessWithStudentQueryJdbcTemplate.getPageInfo(
        // limit, offset,
        // NotAssessWithStudentQueryJdbcTemplate.beanMapper, null,
        // new NotAssessWithStudentQueryPaginationSQL(studentId));
        return page;
    }

    @Transactional(readOnly = true)
    public AssessPageInfo<Map<String, String>> getAssess(Integer pageSize,
                                                         Integer offset, Long scheduleId) {
        if (offset == null) {
            offset = 1;
        }
        if (pageSize == null) {
            pageSize = 20;
        }
        Page<Assess> page = assessRepository.findByScheduleIdOrderByCreatedDateDesc(PageUtil.createNoErrorPageRequest(offset, pageSize), scheduleId);
        Double avgScore = assessRepository.avgByScheduleId(scheduleId);

        AssessPageInfo<Map<String, String>> info = new AssessPageInfo<Map<String, String>>();
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (null != page) {
            for (Assess a : page.getContent()) {
                Map<String, String> d = new HashMap<String, String>();
                d.put("score", a.getScore().toString());
                d.put("content", a.getContent());
                d.put("createDate",
                        null != a.getCreatedDate() ? DateFormatUtil.format(
                                a.getCreatedDate(), DateFormatUtil.FORMAT_LONG)
                                : "");
                list.add(d);
            }
            info.setLimit(page.getSize());
            info.setPageCount(page.getTotalPages());
            info.setTotalCount(page.getTotalElements());
            info.setOffset(offset);
            info.setData(list);
            info.setScour(avgScore);
        }

        return info;
    }

    public Object getAssessByClass(Long scheduleId, Long classId) {
        Map<String, String> res = new HashMap<String, String>();
        if (scheduleId == null) {
            res.put("errCode", AssessConstants.EMPTY_SCHEDULE_ID);
            res.put("errMsg", "Schedule Id is required");
            return res;
        }
        if (classId == null) {
            res.put("errCode", AssessConstants.EMPTY_SCHEDULE_ID);
            res.put("errMsg", "Class Id is required");
            return res;
        }
        List<Assess> assessList = assessRepository.findByClassIdAndScheduleId(classId, scheduleId);
        List list = new ArrayList();
        if (null != assessList && assessList.size() > 0) {
            for (Assess as : assessList) {
                AssessInfoWithTeacherDTO item = new AssessInfoWithTeacherDTO();
                item.setContent(as.getContent());
                item.setScore(as.getScore());
                item.setAssessTime(DateFormatUtil.format(as.getLastModifiedDate()));
                list.add(item);
            }
        }
//        else {
//            AssessInfoWithTeacherDTO item = new AssessInfoWithTeacherDTO();
//            item.setContent("");
//            item.setScore(0);
//            list.add(item);
//        }

        Long count = 0L;
        List<CountDomain> classCount = orgManagerRemoteService.countbyclassesids(Sets.newHashSet(classId));
        if (null != classCount && classCount.size() > 0) {
            count = classCount.get(0).getCount();
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("data", list);
        result.put("classStudentCount", count);
        return result;
    }

    public Object getAssessInfoByTeacher(Long teacherId) {
        PageInfo page = null;
        // assessOfTeacherListQueryJdbcTemplate.getPageInfo(
        // Integer.MAX_VALUE, 1,
        // AssessOfTeacherListQueryJdbcTemplate.allWeekMapper, null,
        // new AssessOfTeacherListQueryPaginationSQL(null, teacherId));

        Map<Long, AssessOfScheduleDTO> t = new HashMap<Long, AssessOfScheduleDTO>();
        List<AssessOfScheduleDTO> resList = new ArrayList<AssessOfScheduleDTO>();
        for (Object obj : page.getData()) {
            AssessOfSearchDTO dto = null;
            if (obj instanceof AssessOfSearchDTO) {
                dto = (AssessOfSearchDTO) obj;
            } else {
                continue;
            }
            Long courseId = dto.getCourseId();
            AssessOfScheduleDTO p = null;
            if (t.get(courseId) == null) {
                AssessOfScheduleDTO ts = new AssessOfScheduleDTO();
                t.put(courseId, ts);
                ts.setClassInfo(new ArrayList<AssessOfClassDTO>());
                ts.setCourseName(dto.getCourseName());
                resList.add(ts);
            }
            p = t.get(courseId);
            p.setAvgScore(((p.getAvgScore() == null ? 0.0 : p.getAvgScore())
                    * (p.getCount() == null ? 0.0 : p.getCount()) + dto
                    .getScore() * dto.getCount())
                    / ((p.getCount() == null ? 0 : p.getCount()) + dto
                    .getCount()));
            p.setCount((p.getCount() == null ? 0 : p.getCount())
                    + dto.getCount());
            if (p.getAvgScore().isNaN()) {
                p.setAvgScore(0.0);
            }
            AssessOfClassDTO tc = new AssessOfClassDTO();
            tc.setAvgScore(dto.getScore());
            if (tc.getAvgScore().isNaN()) {
                tc.setAvgScore(0.0);
            }
            tc.setClassId(dto.getClassId());
            tc.setClassName(dto.getClassName());
            tc.setCount(dto.getCount());
            p.getClassInfo().add(tc);
        }
        return resList;
    }

    public Object getAssessInfoByTeacher(Long weekId, Long teacherId) {
        List<AssessOfScheduleDTO> resultList = new
                ArrayList<AssessOfScheduleDTO>();
        List<Schedule> schedules = null;
        Sort sort = new Sort(Sort.Direction.ASC, "dayOfWeek", "periodNo");
        if (null == weekId) {
            schedules = scheduleRepository.findAllByTeacherId(teacherId, sort);
        } else {
            schedules = scheduleRepository.findAllByTeacherIdAndWeekId(
                    teacherId, weekId, sort);
        }


        if (null != schedules && schedules.size() > 0) {

            Map<Long, AssessOfScheduleDTO> cacheScheduleId = new HashMap<Long,
                    AssessOfScheduleDTO>();
            Map<String, AssessOfClassDTO> cache2 = new HashMap<String,
                    AssessOfClassDTO>();

            for (Schedule a : schedules) {
                if (null == a.getScheduleRollCall())
                    continue;
                AssessOfScheduleDTO as = new AssessOfScheduleDTO();
                as.setScheduleId(a.getId());
                as.setDayOfWeek(a.getDayOfWeek() + "");
                as.setCourseName(a.getCourseName());
                as.setPeriodName(CourseUtils.getWhichLesson(a.getPeriodNo(), a.getPeriodNum()));

                List<IdNameDomain> classsList = orgManagerRemoteService.listClass(a.getTeachingclassId());
                List aocdList = new ArrayList<AssessOfClassDTO>();
                int totalCount = 0;
                double totalAvage = 0.0;

                if (null != classsList && classsList.size() > 0) {
                    for (IdNameDomain dom : classsList) {
                        AssessOfClassDTO assessOfClassDTO = new AssessOfClassDTO();
                        assessOfClassDTO.setClassId(dom.getId());
                        assessOfClassDTO.setClassName(dom.getName());

                        AssessClassesAvgDTO acaDto = assessRepository.findByScheduleIdAndClassId(as.getScheduleId(), dom.getId());
                        int classCount = null == acaDto ? 0 : (null == acaDto.getCount() ? 0 : acaDto.getCount().intValue());
                        double classAvage = 0.0 + (null == acaDto ? 0 : (null == acaDto.getSumScore() ? 0 : (acaDto.getSumScore() / acaDto.getCount())));
                        assessOfClassDTO.setCount(classCount);
                        assessOfClassDTO.setAvgScore(classAvage);
                        totalCount = +classCount;
                        totalAvage += classAvage;
                        aocdList.add(assessOfClassDTO);
                    }
                }
                as.setCount(totalCount);
                as.setAvgScore(totalAvage);
                as.setClassInfo(aocdList);
                resultList.add(as);
                cacheScheduleId.put(as.getScheduleId(), as);
            }

            for (AssessOfScheduleDTO a : resultList) {
                if (null == a.getCount()) {
                    a.setCount(0);
                }
                if (null == a.getAvgScore()) {
                    a.setAvgScore(0.0);
                }
                if (0 != a.getCount()) {
                    a.setAvgScore(a.getAvgScore() / a.getCount());
                }
            }

            return resultList;
        }
        return null;
    }

    public Long getWaitAssessCount(Long studentId, Long semesterId) {
        return assessRepository.countByStudentIdAndSemesterId(studentId, semesterId);
    }

}