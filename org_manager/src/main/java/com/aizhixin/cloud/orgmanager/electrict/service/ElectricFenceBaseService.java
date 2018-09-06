package com.aizhixin.cloud.orgmanager.electrict.service;


import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.common.loging.AspectLog;
import com.aizhixin.cloud.orgmanager.common.rest.RestUtil;
import com.aizhixin.cloud.orgmanager.company.domain.AdminUserDomain;
import com.aizhixin.cloud.orgmanager.company.entity.*;
import com.aizhixin.cloud.orgmanager.company.service.UserService;
import com.aizhixin.cloud.orgmanager.config.ZhiXinConfig;
import com.aizhixin.cloud.orgmanager.electrict.constant.UserConstants;
import com.aizhixin.cloud.orgmanager.electrict.domain.UseElectricFenceUserDaomin;
import com.aizhixin.cloud.orgmanager.electrict.domain.UserLocusLonlat;
import com.aizhixin.cloud.orgmanager.electrict.dto.*;
import com.aizhixin.cloud.orgmanager.electrict.entity.ElectricFenceBase;
import com.aizhixin.cloud.orgmanager.electrict.entity.ElectricFenceInfo;
import com.aizhixin.cloud.orgmanager.electrict.entity.ElectricFenceStatistics;
import com.aizhixin.cloud.orgmanager.electrict.repository.*;
import net.sf.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 电子围栏
 *
 * @author HUM
 */
@Service
@Transactional
public class ElectricFenceBaseService {
    final static private Logger LOG = LoggerFactory.getLogger(ElectricFenceBaseService.class);
    @Autowired
    private EntityManager em;
    @Autowired
    private ElectricFenceBaseRepository electricFenceBaseRepository;
    @Autowired
    ElectricFenceStatisticsRepository electricFenceStatisticsRepository;
    @Autowired
    private ElectricFenceInfoService electricFenceInfoService;
    @Autowired
    private UserService userService;
    @Autowired
    private RestUtil restUtil;
    @Autowired
    ZhiXinConfig zhiXinConfig;

    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 校验权限是否是管理员
     *
     * @param schoolId
     * @param userId
     * @return
     */
    public boolean checkRoleAdmin(Long schoolId, Long userId) {
        User user = userService.findById(userId);
        if (null != user) {
            PageData<AdminUserDomain> adminUserDomainPageData = userService.findSchoolAdmin(PageUtil.createNoErrorPageRequest(1, 10), schoolId, user.getLoginName());
            if (adminUserDomainPageData.getData().size() < 1) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * 电子围栏信息设置
     *
     * @param electricFenceInfoDTO
     * @return
     */
    public Map save(ElectricFenceInfoDTO electricFenceInfoDTO) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (int i = 0; i < electricFenceInfoDTO.getLltudes().size(); i++) {
            if (electricFenceInfoDTO.getLltudes().get(0).get(i).getLatitude().equals("string")
                    || electricFenceInfoDTO.getLltudes().get(0).get(i).getLongitude().equals("string")
                    || electricFenceInfoDTO.getMonitorDate().equals("[0]")) {
                result.put("message", "数据设置有误");
                result.put("error", UserConstants.DATA_ERROR);
                return result;
            }
        }
        //删掉该管理员之前的设置，保存最新数据。
        ElectricFenceInfo electricFence1 = electricFenceInfoService.findOneByOrganId(electricFenceInfoDTO.getOrganId());

        try {
            List<List<Lonlat>> ll = electricFenceInfoDTO.getLltudes();
            String json = JSONArray.fromCollection(ll).toString();
            if (electricFence1 != null) {
                electricFenceInfoService.updateElectricFenceInfo(electricFenceInfoDTO.getOrganId(), json.toString(), electricFenceInfoDTO.getMonitorDate().toString(), electricFenceInfoDTO.getNomonitorDate().toString(), electricFenceInfoDTO.getSemesterId());
            } else {
                // 电子围栏信息设置
                ElectricFenceInfo electricFenceInfo = new ElectricFenceInfo();
                electricFenceInfo.setSemesterId(electricFenceInfoDTO.getSemesterId());
                electricFenceInfo.setOrganId(electricFenceInfoDTO.getOrganId());
                electricFenceInfo.setNoticeTimeInterval((long) 1800);
                electricFenceInfo.setLltudes(json.toString());
                List<Long> moni = electricFenceInfoDTO.getMonitorDate();
                electricFenceInfo.setMonitorDate(moni.toString());
                List<Long> nomoni = electricFenceInfoDTO.getNomonitorDate();
                electricFenceInfo.setNomonitorDate(nomoni.toString());
                electricFenceInfo.setSetupOrClose(20);
                electricFenceInfoService.save(electricFenceInfo);
            }
            result.put("trueMSG", true);
        } catch (BeansException e) {
            e.printStackTrace();
            result.put("message", "保存失败" + e);
            result.put("falseMSG", false);
        }
        return result;
    }

    /**
     * 电子围栏初始化信息查询
     *
     * @param organId
     * @return
     */
    public ElectricFenceInfoDTO queryInit(Long organId) {
        ElectricFenceInfo electricFenceInfo = electricFenceInfoService.findOneByOrganId(organId);
        if (null == electricFenceInfo) {
            return null;
        } else {
            ElectricFenceInfoDTO electricFenceInfoDTO = new ElectricFenceInfoDTO();
            electricFenceInfoDTO.setSemesterId(electricFenceInfo.getSemesterId());
            electricFenceInfoDTO.setSetupOrClose(electricFenceInfo.getSetupOrClose());
            String nomonitorDate = electricFenceInfo.getNomonitorDate();
            if (!nomonitorDate.equals("[]")) {
                String nom = nomonitorDate.substring(1, nomonitorDate.length() - 1);
                String[] no = nom.split(",");
                List<Long> ll = new ArrayList<Long>();
                for (int i = 0; i <= no.length - 1; i++) {
                    if (i != 0) {
                        String aa = no[i].substring(1);
                        ll.add(Long.parseLong(aa));
                    } else {
                        ll.add(Long.parseLong(no[i]));
                    }
                }
                electricFenceInfoDTO.setNomonitorDate(ll);
            } else {
                electricFenceInfoDTO.setNomonitorDate(null);
            }
            String monitorDate = electricFenceInfo.getMonitorDate();
            if (!monitorDate.equals("[]")) {
                String mon = monitorDate.substring(1, monitorDate.length() - 1);
                String[] on = mon.split(",");
                List<Long> lm = new ArrayList<Long>();
                for (int i = 0; i <= on.length - 1; i++) {
                    if (i != 0) {
                        String bb = on[i].substring(1);
                        lm.add(Long.parseLong(bb));
                    } else {
                        lm.add(Long.parseLong(on[i]));
                    }
                }

                electricFenceInfoDTO.setMonitorDate(lm);
            } else {
                electricFenceInfoDTO.setMonitorDate(null);
            }
            try {
                String lltude = electricFenceInfo.getLltudes();
                List<List<Lonlat>> lon = JSONArray.toList(JSONArray.fromObject(lltude), Lonlat.class);
                electricFenceInfoDTO.setLltudes(lon);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return electricFenceInfoDTO;
        }
    }

    /**
     * 电子围栏学生状态信息查询
     *
     * @param pageable
     * @param organId
     * @param collegeId
     * @param professionalId
     * @param classId
     * @param name
     * @param jobNumber
     * @param time
     * @param isLeaveSchool
     * @param isActivation
     * @param isAtSchool
     * @param isOline
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> queryElectricFence(Pageable pageable, Long organId, Long collegeId, Long professionalId, Long classId, String name, String jobNumber,
                                                  Long time, String isLeaveSchool, String isActivation, String isAtSchool, String isOline, String isLogin, String accessToken) {
        Map<String, Object> result = new HashMap<>();
        PageData<ElectricFenceStatisticsDTO> datas = new PageData<>();
        PageData<ElectricFenceStatistics> p = new PageData<>();
        //当前时间
        Date now = new Date();
        //获取今天的日期
        String nowDay = sf.format(now);
        //对比的时间
        String day = sf.format(time);
        StringBuilder csql = new StringBuilder("SELECT count(1) FROM t_electric_fence_statistics s WHERE 1 = 1");
        StringBuilder sql = new StringBuilder("SELECT s.* FROM t_electric_fence_statistics s WHERE 1 = 1");
        StringBuilder countsql = new StringBuilder("SELECT count(1) FROM t_electric_fence_statistics s WHERE s.OUT_COUNT > 0");
        StringBuilder outcountsql = new StringBuilder("SELECT count(1) FROM t_electric_fence_statistics s WHERE s.SEMIH_OUT_COUNT > 0");
        Map<String, Object> condition = new HashMap<>();
        if (null != organId) {
            csql.append(" and s.ORG_ID = :organId");
            sql.append(" and s.ORG_ID = :organId");
            countsql.append(" and s.ORG_ID = :organId");
            outcountsql.append(" and s.ORG_ID = :organId");
            condition.put("organId", organId);
        }
        if (null != collegeId && collegeId > 0) {
            csql.append(" and s.COLLEGE_ID = :collegeId");
            sql.append(" and s.COLLEGE_ID = :collegeId");
            countsql.append(" and s.COLLEGE_ID = :collegeId");
            outcountsql.append(" and s.COLLEGE_ID = :collegeId");
            condition.put("collegeId", collegeId);
        }
        if (null != professionalId && professionalId > 0) {
            csql.append(" and s.PROFESSIONAL_ID = :professionalId");
            sql.append(" and s.PROFESSIONAL_ID = :professionalId");
            countsql.append(" and s.PROFESSIONAL_ID = :professionalId");
            outcountsql.append(" and s.PROFESSIONAL_ID = :professionalId");
            condition.put("professionalId", professionalId);
        }
        if (null != classId && classId > 0) {
            csql.append(" and s.CLASSES_ID = :classId");
            sql.append(" and s.CLASSES_ID = :classId");
            countsql.append(" and s.CLASSES_ID = :classId");
            outcountsql.append(" and s.CLASSES_ID = :classId");
            condition.put("classId", classId);
        }
        if (!StringUtils.isBlank(name)) {
            csql.append(" and s.USER_NAME like :name");
            sql.append(" and s.USER_NAME like :name");
            countsql.append(" and s.USER_NAME like :name");
            outcountsql.append(" and s.USER_NAME like :name");
            condition.put("name", "%" + name + "%");
        }
        if (!StringUtils.isBlank(jobNumber)) {
            csql.append(" and s.JOB_NUMBER like :jobNumber");
            sql.append(" and s.JOB_NUMBER like :jobNumber");
            countsql.append(" and s.JOB_NUMBER like :jobNumber");
            outcountsql.append(" and s.JOB_NUMBER like :jobNumber");
            condition.put("jobNumber", "%" + jobNumber + "%");
        }
        if (!StringUtils.isBlank(day)) {
            csql.append(" and DATE_FORMAT(s.CREATED_DATE,'%Y-%m-%d') = :day");
            sql.append(" and DATE_FORMAT(s.CREATED_DATE,'%Y-%m-%d') = :day");
            countsql.append(" and DATE_FORMAT(s.CREATED_DATE,'%Y-%m-%d') = :day");
            outcountsql.append(" and DATE_FORMAT(s.CREATED_DATE,'%Y-%m-%d') = :day");
            condition.put("day", day);
        }
        //未激活
        if (null != isActivation && isActivation.equals("0")) {
            csql.append(" and s.FENCE_ACTIVATION = 0");
            sql.append(" and s.FENCE_ACTIVATION = 0");
            countsql.append(" and s.FENCE_ACTIVATION = 0");
            outcountsql.append(" and s.FENCE_ACTIVATION = 0");
        }
        //激活
        if (null != isActivation && isActivation.equals("1")) {
            csql.append(" and s.FENCE_ACTIVATION IS NULL");
            sql.append(" and s.FENCE_ACTIVATION IS NULL");
            countsql.append(" and s.FENCE_ACTIVATION IS NULL");
            outcountsql.append(" and s.FENCE_ACTIVATION IS NULL");
        }
        //未曾离校
        if (null != isLeaveSchool && isLeaveSchool.equals("0")) {
            csql.append(" and s.OUT_COUNT = 0");
            sql.append(" and s.OUT_COUNT = 0");
            countsql.append(" and s.OUT_COUNT = 0");
            outcountsql.append(" and s.OUT_COUNT = 0");
        }
        //曾离校
        if (null != isLeaveSchool && isLeaveSchool.equals("1")) {
            csql.append(" and s.OUT_COUNT > 0");
            sql.append(" and s.OUT_COUNT > 0");
            countsql.append(" and s.OUT_COUNT > 0");
            outcountsql.append(" and s.OUT_COUNT > 0");
        }
        //未知是否曾离校
        if (null != isLeaveSchool && isLeaveSchool.equals("2")) {
            csql.append(" and s.OUT_COUNT IS NULL");
            sql.append(" and s.OUT_COUNT IS NULL");
            countsql.append(" and s.OUT_COUNT IS NULL");
            outcountsql.append(" and s.OUT_COUNT IS NULL");
        }
        //不在校
        if (null != isAtSchool && isAtSchool.equals("0")) {
            csql.append(" and s.SEMIH_OUT_COUNT > 0");
            sql.append(" and s.SEMIH_OUT_COUNT > 0");
            countsql.append(" and s.SEMIH_OUT_COUNT > 0");
            outcountsql.append(" and s.SEMIH_OUT_COUNT > 0");
        }
        //在校
        if (null != isAtSchool && isAtSchool.equals("1")) {
            csql.append(" and s.SEMIH_OUT_COUNT = 0");
            sql.append(" and s.SEMIH_OUT_COUNT = 0");
            countsql.append(" and s.SEMIH_OUT_COUNT = 0");
            outcountsql.append(" and s.SEMIH_OUT_COUNT = 0");
        }
        //未知在校
        if (null != isAtSchool && isAtSchool.equals("2")) {
            csql.append(" and s.SEMIH_OUT_COUNT IS NULL");
            sql.append(" and s.SEMIH_OUT_COUNT IS NULL");
            countsql.append(" and s.SEMIH_OUT_COUNT IS NULL");
            outcountsql.append(" and s.SEMIH_OUT_COUNT IS NULL");
        }
        //不在线
        if (null != isOline && isOline.equals("0")) {
            csql.append(" and (s.SEMIH_COUNT IS NULL or s.SEMIH_COUNT = 0)");
            sql.append(" and (s.SEMIH_COUNT IS NULL or s.SEMIH_COUNT = 0)");
            countsql.append(" and (s.SEMIH_COUNT IS NULL or s.SEMIH_COUNT = 0)");
            outcountsql.append(" and (s.SEMIH_COUNT IS NULL or s.SEMIH_COUNT = 0)");
        }
        //在线
        if (null != isOline && isOline.equals("1")) {
            csql.append(" and s.SEMIH_COUNT > 0");
            sql.append(" and s.SEMIH_COUNT > 0");
            countsql.append(" and s.SEMIH_COUNT > 0");
            outcountsql.append(" and s.SEMIH_COUNT > 0");
        }
        //未登录
        if (null != isLogin && isLogin.equals("0")) {
            csql.append(" and (s.CHECK_COUNT IS NULL or s.CHECK_COUNT = 0)");
            sql.append(" and (s.CHECK_COUNT IS NULL or s.CHECK_COUNT = 0)");
            countsql.append(" and (s.CHECK_COUNT IS NULL or s.CHECK_COUNT = 0)");
            outcountsql.append(" and (s.CHECK_COUNT IS NULL or s.CHECK_COUNT = 0)");
        }
        //登录
        if (null != isLogin && isLogin.equals("1")) {
            csql.append(" and s.CHECK_COUNT > 0");
            sql.append(" and s.CHECK_COUNT > 0");
            countsql.append(" and s.CHECK_COUNT > 0");
            outcountsql.append(" and s.CHECK_COUNT > 0");
        }
        sql.append(" ORDER BY s.CREATED_DATE DESC");
        Query q = em.createNativeQuery(csql.toString());
        Query sq = em.createNativeQuery(sql.toString(), ElectricFenceStatistics.class);
        Query countsq = em.createNativeQuery(countsql.toString());
        Query outcountsq = em.createNativeQuery(outcountsql.toString());
        for (Map.Entry<String, Object> e : condition.entrySet()) {
            q.setParameter(e.getKey(), e.getValue());
            sq.setParameter(e.getKey(), e.getValue());
            countsq.setParameter(e.getKey(), e.getValue());
            outcountsq.setParameter(e.getKey(), e.getValue());
        }
        Long count = Long.valueOf(String.valueOf(q.getSingleResult()));
        sq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        sq.setMaxResults(pageable.getPageSize());
        p.setData(sq.getResultList());
        p.getPage().setTotalElements(count);
        p.getPage().setPageNumber(pageable.getPageNumber());
        p.getPage().setPageSize(pageable.getPageSize());
        p.getPage().setTotalPages(PageUtil.cacalatePagesize(count, p.getPage().getPageSize()));

        List<ElectricFenceStatisticsDTO> electricFenceStatisticsDTOList = new ArrayList<>();
        for (ElectricFenceStatistics data : p.getData()) {
            ElectricFenceStatisticsDTO electricFenceStatisticsDTO = new ElectricFenceStatisticsDTO();
            electricFenceStatisticsDTO.setId(data.getUserId());
            electricFenceStatisticsDTO.setName(data.getUserName());
            electricFenceStatisticsDTO.setJobNumber(data.getJobNumber());
            electricFenceStatisticsDTO.setClassesName(data.getClassesName());
            electricFenceStatisticsDTO.setProfessionalName(data.getProfessionalName());
            electricFenceStatisticsDTO.setCollegeName(data.getCollegeName());
            if (null != data.getActivation()) {
                if (null != data.getCheckCount()) {
                    electricFenceStatisticsDTO.setCheckCount(data.getCheckCount().longValue());
                } else {
                    electricFenceStatisticsDTO.setCheckCount(0L);
                }
                electricFenceStatisticsDTO.setLeave("--");
                electricFenceStatisticsDTO.setAddress("--");
                electricFenceStatisticsDTO.setOnlinStatus("离线");
                electricFenceStatisticsDTO.setRemark("未激活");
            } else {
                if (day.equals(nowDay)) {
                    if (null != data.getCheckCount()) {
                        electricFenceStatisticsDTO.setCheckCount(data.getCheckCount().longValue());
                        if (null != data.getOutCount()) {
                            if (data.getOutCount() > 0) {
                                electricFenceStatisticsDTO.setLeave("是");
                            } else {
                                electricFenceStatisticsDTO.setLeave("否");
                            }
                        } else {
                            electricFenceStatisticsDTO.setLeave("否");
                        }
                        if (null != data.getSemihCount()) {
                            electricFenceStatisticsDTO.setOnlinStatus("在线");
                            if (null != data.getSemihOutCount()) {
                                if (data.getSemihOutCount() > 0) {
                                    electricFenceStatisticsDTO.setAddress("离校");
                                } else {
                                    electricFenceStatisticsDTO.setAddress("在校");
                                }
                            } else {
                                electricFenceStatisticsDTO.setAddress("在校");
                            }
                        } else {
                            electricFenceStatisticsDTO.setOnlinStatus("离线");
                            electricFenceStatisticsDTO.setAddress("--");
                        }
                        electricFenceStatisticsDTO.setRemark("--");
                    } else {
                        electricFenceStatisticsDTO.setCheckCount(0L);
                        electricFenceStatisticsDTO.setLeave("--");
                        electricFenceStatisticsDTO.setAddress("--");
                        electricFenceStatisticsDTO.setOnlinStatus("离线");
                        electricFenceStatisticsDTO.setRemark("未登录");
                    }
                } else {
                    if (null != data.getCheckCount()) {
                        electricFenceStatisticsDTO.setCheckCount(data.getCheckCount().longValue());
                        if (null != data.getOutCount()) {
                            if (data.getOutCount() > 0) {
                                electricFenceStatisticsDTO.setLeave("是");
                            } else {
                                electricFenceStatisticsDTO.setLeave("否");
                            }
                        } else {
                            electricFenceStatisticsDTO.setLeave("否");
                        }
                        electricFenceStatisticsDTO.setRemark("--");
                    } else {
                        electricFenceStatisticsDTO.setCheckCount(0L);
                        electricFenceStatisticsDTO.setLeave("--");
                        electricFenceStatisticsDTO.setRemark("未登录");
                    }
                    electricFenceStatisticsDTO.setAddress("--");
                    electricFenceStatisticsDTO.setOnlinStatus("离线");
                }
            }
            electricFenceStatisticsDTOList.add(electricFenceStatisticsDTO);
        }
        datas.getPage().setTotalPages(p.getPage().getTotalPages());
        datas.getPage().setPageNumber(pageable.getPageNumber());
        datas.getPage().setPageSize(pageable.getPageSize());
        datas.getPage().setTotalElements(p.getPage().getTotalElements());
        datas.setData(electricFenceStatisticsDTOList);
        result.put("pagedata", datas);
        result.put("onceLeave", (BigInteger) countsq.getSingleResult());
        if (day.equals(nowDay)) {
            result.put("nowLeave", (BigInteger) outcountsq.getSingleResult());
        } else {
            result.put("nowLeave", 0);
        }
        return result;
    }

    /**
     * 查询当天轨迹
     *
     * @param organId
     * @param time
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public UserLocusLonlat querLocus(Long organId, Long time, Long userId) {
        UserLocusLonlat userLocusLonlat = new UserLocusLonlat();
        User user = userService.findById(userId);
        if (null != user) {
            userLocusLonlat.setUserId(user.getId());
            userLocusLonlat.setName(user.getName());
            if (null != user.getCollege()) {
                userLocusLonlat.setCollegeName(user.getCollege().getName());
            }
            if (null != user.getClasses()) {
                userLocusLonlat.setClassesName(user.getClasses().getName());
            }
            if (null != user.getProfessional()) {
                userLocusLonlat.setProfessionalName(user.getProfessional().getName());
            }
            userLocusLonlat.setUserPhone(user.getPhone());
        }

        //一天开始时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        //一天结束时间
        SimpleDateFormat end = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        String start = sdf.format(time);
        String endtime = end.format(time);
        Date startDate = null;
        Date endDate = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            startDate = format.parse(start);
            endDate = format.parse(endtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<Date> endTimeList = electricFenceStatisticsRepository.findStatisticsTime(organId, userId, startDate, endDate);
        if (null != endTimeList && endTimeList.size() > 0) {
            endDate = endTimeList.get(0);
        }
        List<UseElectricFenceUserDaomin> electricFenceBaseList = electricFenceBaseRepository.findUserInfoByUserId(organId, userId, startDate, endDate);
        userLocusLonlat.setUseElectricFenceUserDaominList(electricFenceBaseList);
        return userLocusLonlat;
    }

    /**
     * 历史轨迹
     *
     * @param pageable
     * @param organId
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public PageData<UseElectricFenceUserDaomin> queryhistorylocus(Pageable pageable, Long organId, Long userId) {
        PageData<UseElectricFenceUserDaomin> p = new PageData<>();
        StringBuilder sql = new StringBuilder("SELECT s.* FROM t_electric_fence_statistics s WHERE  1 = 1");
        Map <String, Object> condition = new HashMap <>();
        if (null != organId) {
            sql.append(" and s.ORG_ID = :organId");
            condition.put("organId", organId);
        }
        if (null != userId) {
            sql.append(" and s.USER_ID = :userId");
            condition.put("userId", userId);
        }
        sql.append(" ORDER BY s.CREATED_DATE DESC");
        Query sq = em.createNativeQuery(sql.toString(), ElectricFenceStatistics.class);
        for (Map.Entry <String, Object> e : condition.entrySet()) {
            sq.setParameter(e.getKey(), e.getValue());
        }
        Long count = new Long(sq.getResultList().size());
        sq.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        sq.setMaxResults(pageable.getPageSize());
        List<ElectricFenceStatistics> result = sq.getResultList();
        if (null != result && result.size() > 0) {
            List<UseElectricFenceUserDaomin> useElectricFenceUserDaominLis = new ArrayList<>();
            for (ElectricFenceStatistics data : result) {
                UseElectricFenceUserDaomin useElectricFenceUserDaomin = new UseElectricFenceUserDaomin();
                useElectricFenceUserDaomin.setId(data.getUserId());
                useElectricFenceUserDaomin.setName(data.getUserName());
                useElectricFenceUserDaomin.setJobNumber(data.getJobNumber());
                useElectricFenceUserDaomin.setClassesName(data.getClassesName());
                useElectricFenceUserDaomin.setProfessionalName(data.getProfessionalName());
                useElectricFenceUserDaomin.setCollegeName(data.getCollegeName());
                if (null != data.getCheckCount()) {
                    useElectricFenceUserDaomin.setCheckCount(data.getCheckCount().longValue());
                    if (null != data.getOutCount()) {
                        if (data.getOutCount() > 0) {
                            useElectricFenceUserDaomin.setLeave("是");
                        } else {
                            useElectricFenceUserDaomin.setLeave("否");
                        }
                    } else {
                        useElectricFenceUserDaomin.setLeave("否");
                    }
                    useElectricFenceUserDaomin.setRemark("--");
                } else {
                    useElectricFenceUserDaomin.setCheckCount(0L);
                    useElectricFenceUserDaomin.setLeave("未知");
                    useElectricFenceUserDaomin.setRemark("当天未登录");
                }
                useElectricFenceUserDaomin.setCheckdate(data.getCreatedDate());
                //添加到list中
                useElectricFenceUserDaominLis.add(useElectricFenceUserDaomin);
            }
            p.setData(useElectricFenceUserDaominLis);
        }
        p.getPage().setTotalElements(count);
        p.getPage().setPageNumber(pageable.getPageNumber());
        p.getPage().setPageSize(pageable.getPageSize());
        p.getPage().setTotalPages(PageUtil.cacalatePagesize(count, p.getPage().getPageSize()));
        return p;
    }

    /**
     * save[GPS信息保存]
     * 创建人:  HUM
     * 创建时间: 2016年12月5日
     * @param electricFenceBaseDTO
     * @return
     * @Title: save
     * @since CodingExample　Ver(编码范例查看) 1.1
     */
    public Map saveBase(ElectricFenceBaseDTO electricFenceBaseDTO) {
        Map<String, Object> result = new HashMap<String, Object>();

        //删掉该管理员之前的设置，保存最新数据。
        ElectricFenceInfo electricFence = electricFenceInfoService.findOneByOrganId(electricFenceBaseDTO.getOrganId());
        if (null == electricFence) {
            result.put("message", "学校范围未设置");
            result.put("falseMSG", false);
            return result;
        }
//        Long id = electricFenceInfoService.findIdByOranIdAndSetupOrClose(electricFenceBaseDTO.getOrganId(),10);
        if (null != electricFence && electricFence.getSetupOrClose().intValue() == 10) {
            //如果上传的gps是在非监控日期内则不保存gps信息。
            String no = electricFence.getNomonitorDate();
            String aa = no.substring(1, no.length() - 1);//去掉[];
            List<Long> list = new ArrayList<Long>();
            if (aa.contains(",")) {
                String[] bb = aa.split(",");
                for (int i = 0; i < bb.length; i++) {
                    if (i == 0) {
                        list.add(Long.parseLong(bb[0]));
                    } else {
                        list.add(Long.parseLong(bb[i].substring(1)));
                    }
                }
            } else {
                list.add(Long.parseLong(aa));
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            long date = 0;
            try {
                date = sdf.parse(sdf.format(new Date())).getTime();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (list.contains(date)) {
                //在非监控日期内，不保存直接返回;
                result.put("message", "非监控日期数据不需要保存");
                result.put("falseMSG", false);
                return result;
            }
            String lltudes = electricFence.getLltudes();
            List data = JSONArray.toList(JSONArray.fromObject(lltudes), Lonlat.class);
            try {
                // GPS信息
                ElectricFenceBase electricFenceBase = new ElectricFenceBase();
                if (null != electricFenceBaseDTO.getUserId()) {
                    User user = userService.findById(electricFenceBaseDTO.getUserId());
                    electricFenceBase.setUser(user);
                }
                electricFenceBase.setAddress(electricFenceBaseDTO.getAddress());
                electricFenceBase.setConnectWay(electricFenceBaseDTO.getConnectWay());
                electricFenceBase.setEquipmentCode(electricFenceBaseDTO.getEquipmentCode());
                electricFenceBase.setLltude(electricFenceBaseDTO.getLltude());
                electricFenceBase.setNoticeTime(new Timestamp(new Date().getTime()));
                electricFenceBase.setOrganId(electricFenceBaseDTO.getOrganId());
                //此处注掉的代码部分的逻辑是在临时统计表t_electric_fence_statistics中做了处理
                //查询该学生上一条GPS的申报时间，判断是否离线离线多久（以秒为单位）
//                List<Date> notice = (List<Date>) electricFenceBaseRepository.findNoticeTimeByUserId(electricFenceBaseDTO.getUserId());
//                if (notice.isEmpty()) {
                    electricFenceBase.setLeaveNum((long) 0);
//                } else {
//                    String noticeTime = notice.get(0).toString();
//                    SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    Date date1 = null; //最近的离线时间
//                    try {
//                        date1 = sdff.parse(noticeTime);
//                    } catch (ParseException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                    Date date2 = new Date();
//                    long minutes = date2.getTime() - date1.getTime();
//                    long min = minutes / (1000);
//                    //大于申报时隔则离线，记录离线时长  转换为秒单位
//                    if (min > electricFence.getNoticeTimeInterval()) {
//                        electricFenceBase.setLeaveNum(min);
//                    } else {
//                        electricFenceBase.setLeaveNum((long) 0);
//                    }
//                }
                Gaom test = new Gaom();
                if (null != electricFence) {
                    for (int j = 0; j < data.size(); j++) {
                        boolean outOfRange = test.isInPolygon(electricFenceBaseDTO.getLltude(), JSONArray.fromObject(data.get(j)).toString());
                        if (outOfRange) {
                            electricFenceBase.setOutOfRange(0);
                            break;
                        } else {
                            if (j == data.size() - 1) {
                                electricFenceBase.setOutOfRange(1);
                            }
                        }
                    }
                } else {
                    electricFenceBase.setOutOfRange(0);
                }
                electricFenceBaseRepository.save(electricFenceBase);
                result.put("trueMSG", true);
            } catch (BeansException e) {
                e.printStackTrace();
                result.put("message", "保存失败" + e);
                result.put("falseMSG", false);
                return result;
            }
        } else {
            result.put("message", "学校范围未开启");
            result.put("falseMSG", false);
            return result;
        }
        return result;
    }

    /**
     * 电子围栏的关闭和开启
     *
     * @param organdId
     * @param flag
     * @return
     */
    public Map<String, Object> fenceSwitch(Long organdId, String flag) {
        return electricFenceInfoService.fenceSwitch(organdId, flag);
    }


    /**
     * 电子围栏查询
     */
    @Transactional(readOnly = true)
    public ElectricFenceInfo queryTimeInterval(Long organId) {
        return electricFenceInfoService.findOneByOrganId(organId);
    }

    /**
     * 根据userId 查询地址
     */
    public List<String> findAddressByUserId(Long userId) {
        return electricFenceBaseRepository.findAddressByUserId(userId);
    }


    /**
     * 根据userId查询上报时间
     */
    public List<Date> findNoticeTimeByUserId(Long userId) {
        return electricFenceBaseRepository.findNoticeTimeByUserId(userId);
    }


    public void timingStatistics(){
        try {
            Query query = em.createNativeQuery("{CALL elec_timer()}");
            query.executeUpdate();
        }catch (Exception e){
            LOG.info("电子围栏定时统计存储过程不存在！");
        }
    }


    public void deleteData(){
        try {
            StringBuilder bql = new StringBuilder("DELETE FROM t_electric_fence_statistics WHERE CREATED_DATE < DATE_SUB(CURDATE(), INTERVAL 7 DAY)");
            StringBuilder sql = new StringBuilder("DELETE FROM t_electric_fence_base WHERE CREATED_DATE < DATE_SUB(CURDATE(), INTERVAL 7 DAY)");
            Query bq = em.createNativeQuery(bql.toString());
            Query sq = em.createNativeQuery(sql.toString());
            bq.executeUpdate();
            sq.executeUpdate();
        }catch (Exception e){
            LOG.info("删除7天前的数据失败！");
        }
    }

}