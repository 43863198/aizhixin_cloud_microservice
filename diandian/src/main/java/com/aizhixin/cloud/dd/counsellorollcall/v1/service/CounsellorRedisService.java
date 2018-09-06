package com.aizhixin.cloud.dd.counsellorollcall.v1.service;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.provider.store.redis.RedisTokenStore;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.counsellorollcall.domain.*;
import com.aizhixin.cloud.dd.counsellorollcall.entity.*;
import com.aizhixin.cloud.dd.counsellorollcall.repository.StudentSubGroupRepository;
import com.aizhixin.cloud.dd.counsellorollcall.thread.UpdateRollcallMessageThread;
import com.aizhixin.cloud.dd.counsellorollcall.v2.service.CounselorRollcallStudentService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 导员点名 redis操作
 *
 * @author hsh
 */
@Service
@Transactional
public class CounsellorRedisService {

    private final Logger LOG = LoggerFactory.getLogger(CounsellorRedisService.class);

    @Autowired
    private StudentSignInService studentSignInService;

    @Autowired
    private CounsellorRollcallService counsellorRollcallService;

    @Autowired
    private RedisTokenStore redisTokenStore;

    @Autowired
    private CounselorRollcallStudentService counselorRollcallStudentService;

    @Autowired
    private StudentSubGroupRepository subGroupRepository;

    /**
     * 从缓存更新是否已读
     *
     * @param studentSignInDomains
     * @return
     */
    public List<StudentSignInDomain> updateReadFromCache(List<StudentSignInDomain> studentSignInDomains) {
        if (studentSignInDomains != null && studentSignInDomains.size() > 0) {
            for (StudentSignInDomain item : studentSignInDomains) {
                if (!item.getLookStatus()) {
                    List<RollcallReportDomain> rollcallReportDomains = redisTokenStore.readRedisData(item.getStudentId());
                    if (rollcallReportDomains != null && rollcallReportDomains.size() > 0) {
                        for (RollcallReportDomain d : rollcallReportDomains) {
                            if (d.getId().longValue() == item.getId().longValue()) {
                                item.setLookStatus(d.getHaveRead());
                                break;
                            }
                        }
                    }
                }
            }
        }
        return studentSignInDomains;
    }

    /**
     * 点名缓存到redis
     *
     * @param data
     */
    public void pushCache(Long studentId, List<RollcallReportDomain> data) {
        redisTokenStore.storeRedisData(studentId, data);
    }

    public void pushCacheV2(Long studentId, List<StuTempGroupDomainV2> data) {
        redisTokenStore.storeRedisDataV2(studentId, data);
    }

    /**
     * 更新新点名到缓存 点名开启时触发
     *
     * @param conunsellorRollcall
     * @param stus
     */
    public void putCache(CounsellorRollcall conunsellorRollcall, List<StudentSignIn> stus) {
        //c.id,n.teacherId,n.teacherName,c.studentName,c.studentNum,n.id,c.gpsLocation,c.gpsDetail,
        // c.signTime,n.openTime,c.haveReport,c.status,c.haveRead,n.status
        Map<Long, List<RollcallReportDomain>> datas = new HashMap<>();
        Map<Long, Long> rollcallSingIns = new HashMap<>();
        for (StudentSignIn item : stus) {
            RollcallReportDomain data = new RollcallReportDomain();
            data.setId(item.getId());
            data.setTeacherId(conunsellorRollcall.getTeacherId());
            data.setTeacherName(conunsellorRollcall.getTeacherName());
            data.setStudentName(item.getStudentName());
            data.setPsersonId(item.getStudentNum());
            data.setGpsLocation(item.getGpsLocation());
            data.setGpsDetail(item.getGpsDetail());
            data.setCounsellorId(conunsellorRollcall.getId());
            if (item.getSignTime() != null) {
                data.setSignTime(formatDate(item.getSignTime()));
            }
            if (conunsellorRollcall.getOpenTime() != null) {
                data.setOpenTime(formatDate(conunsellorRollcall.getOpenTime()));
            }
            data.setHaveReport(item.getHaveReport());
            data.setStatus(item.getStatus());
            data.setHaveRead(item.isHaveRead());
            data.setIsOpen(conunsellorRollcall.getStatus());

            List<RollcallReportDomain> list = datas.get(item.getStudentId());
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(data);
            datas.put(item.getStudentId(), list);
            rollcallSingIns.put(item.getId(), conunsellorRollcall.getId());
        }
        redisTokenStore.pushRedisDatas(datas);
        if (rollcallSingIns.size() > 0) {
            redisTokenStore.storeLongRedisDatas(rollcallSingIns);
        }
    }

    public List<StuTempGroupDomainV2> putCacheV2(CounsellorRollcall conunsellorRollcall, List<StudentSignIn> stus, boolean isV2, AlarmClock alarmClock) {
        //c.id,n.teacherId,n.teacherName,c.studentName,c.studentNum,n.id,c.gpsLocation,c.gpsDetail,
        // c.signTime,n.openTime,c.haveReport,c.status,c.haveRead,n.status
        Map<Long, Long> rollcallSingIns = new HashMap<>();
        List<StuTempGroupDomainV2> stuTempGroupDomainV2s = new ArrayList<>();
        for (StudentSignIn item : stus) {
            StuTempGroupDomainV2 d = counselorRollcallStudentService.refreshStuCache(conunsellorRollcall, item, isV2, alarmClock);
            if (d != null) {
                stuTempGroupDomainV2s.add(d);
            }
            rollcallSingIns.put(item.getId(), conunsellorRollcall.getId());
        }
        if (rollcallSingIns.size() > 0) {
            redisTokenStore.storeLongRedisDatas(rollcallSingIns);
        }
        return stuTempGroupDomainV2s;
    }

    public Long getGroupId(Long key) {
        return redisTokenStore.getGroupId(key);
    }

    /**
     * 关闭点名，更新缓存
     *
     * @param conunsellorRollcalls
     */
    @Transactional
    public void cleanCache(TempGroup tempGroup, List<CounsellorRollcall> conunsellorRollcalls, boolean isClean, boolean isAll, boolean closeAll) {
        if (conunsellorRollcalls != null && conunsellorRollcalls.size() > 0) {
            for (CounsellorRollcall item : conunsellorRollcalls) {
                LOG.warn(isClean + ":cleanCache:" + item.toString());
                List<StudentSignIn> stus = studentSignInService.findStudentSignInByCounsellorRollcall(item);
                if (stus != null && stus.size() > 0) {
                    for (StudentSignIn stusingin : stus) {
                        List<RollcallReportDomain> list = getRollcallByStuId(stusingin.getStudentId());
                        if (list != null && list.size() > 0) {
                            for (RollcallReportDomain d : list) {
                                if (isAll) {
                                    d.setIsOpen(false);
                                } else if (d.getCounsellorId() != null && d.getCounsellorId().longValue() == item.getId().longValue()) {
                                    d.setIsOpen(false);
                                    break;
                                }
                            }
                            redisTokenStore.storeRedisData(stusingin.getStudentId(), list);
                        }
                        //v2
                        if(closeAll){
                            List<StuTempGroupDomainV2> stuListV2 = redisTokenStore.readRedisDataV2(stusingin.getStudentId());
                            if (tempGroup != null && stuListV2 != null && stuListV2.size() > 0) {
                                for (StuTempGroupDomainV2 d : stuListV2) {
                                    if (d.getGroupId().longValue() == tempGroup.getId().longValue() && d.getReportList() != null && d.getReportList().size() > 0) {
                                        d.setIsOpen(false);
                                        d.setReportList(new ArrayList<>());
                                        break;
                                    }
                                }
                                redisTokenStore.storeRedisDataV2(stusingin.getStudentId(), stuListV2);
                                //发送消息
                                UpdateRollcallMessageThread.addCounsellersNotCount(stuListV2);
                            }
                        } else if (isClean) {
                            counselorRollcallStudentService.refreshStuCache(stusingin.getStudentId());
                        } else {
                            List<StuTempGroupDomainV2> stuListV2 = redisTokenStore.readRedisDataV2(stusingin.getStudentId());
                            if (tempGroup != null && stuListV2 != null && stuListV2.size() > 0) {
                                for (StuTempGroupDomainV2 d : stuListV2) {
                                    if (d.getGroupId().longValue() == tempGroup.getId().longValue() && d.getReportList() != null && d.getReportList().size() > 0) {
                                        d.setIsOpen(false);
                                        for (StuRollcallReportDomainV2 dd : d.getReportList()) {
                                            if (dd.getCounsellorId().longValue() == item.getId().longValue()) {
                                                dd.setIsOpen(false);
                                                break;
                                            }
                                        }
                                        break;
                                    }
                                }
                                redisTokenStore.storeRedisDataV2(stusingin.getStudentId(), stuListV2);
                                //发送消息
                                UpdateRollcallMessageThread.addCounsellersNotCount(stuListV2);
                            }
                        }
                    }
                }
            }
        }
    }

    public void delCache(TempGroup tempGroup, List<CounsellorRollcall> conunsellorRollcalls) {
        if (conunsellorRollcalls != null && conunsellorRollcalls.size() > 0) {
            for (CounsellorRollcall item : conunsellorRollcalls) {
                List<StudentSignIn> stus = studentSignInService.findStudentSignInByCounsellorRollcall(item);
                if (stus != null && stus.size() > 0) {
                    for (StudentSignIn stusingin : stus) {
                        List<RollcallReportDomain> list = getRollcallByStuId(stusingin.getStudentId());
                        if (list != null && list.size() > 0) {
                            boolean isUpdate = false;
                            for (RollcallReportDomain d : list) {
                                if (d.getCounsellorId() != null && d.getCounsellorId().longValue() == item.getId().longValue()) {
                                    d.setIsOpen(false);
                                    isUpdate = true;
                                }
                            }
                            if (isUpdate) {
                                redisTokenStore.storeRedisData(stusingin.getStudentId(), list);
                            }
                        }
                    }
                }
            }
        }
    }

    public void delCacheV2(TempGroup tempGroup) {
        List<StudentSubGroup> list = subGroupRepository.findAllByTempGroupAndDeleteFlag(tempGroup, DataValidity.VALID.getState());
        if (list != null && list.size() > 0) {
            for (StudentSubGroup stu : list) {
                List<StuTempGroupDomainV2> stuListV2 = redisTokenStore.readRedisDataV2(stu.getStudentId());
                if (stuListV2 != null && stuListV2.size() > 0) {
                    for (StuTempGroupDomainV2 d : stuListV2) {
                        if (d.getGroupId().longValue() == tempGroup.getId().longValue()) {
                            stuListV2.remove(d);
                            redisTokenStore.storeRedisDataV2(stu.getStudentId(), stuListV2);
                            UpdateRollcallMessageThread.addDel(d);
                            break;
                        }
                    }
                }
            }


        }
    }

    public void refreshStuCache(Long stuId, Long counsellorId, String status) {
        List<RollcallReportDomain> list = getRollcallByStuId(stuId);
        if (list != null && list.size() > 0) {
            for (RollcallReportDomain d : list) {
                if (d.getCounsellorId() != null && d.getCounsellorId().longValue() == counsellorId.longValue()) {
                    d.setStatus(status);
                    break;
                }
            }
        }
        redisTokenStore.storeRedisData(stuId, list);
    }

    /**
     * 删除点名组，更新缓存
     *
     * @param tempGroupIds
     */
    public void delCacheByDelTempGroup(Set<Long> tempGroupIds) {
        if (tempGroupIds != null && tempGroupIds.size() > 0) {
            for (Long id : tempGroupIds) {
                List<CounsellorRollcall> clist = counsellorRollcallService.findByTemGroupId(id);
                cleanCache(null, clist, true, false, false);
            }
        }
    }

    public void delCacheByDelTempGroupV2(List<TempGroup> groups) {
        try {
            if (groups != null && groups.size() > 0) {
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("--------------------------------------------------");
                        deltCacheV2(groups);
                    }
                };
                Thread thread = new Thread(myRunnable);
                thread.start();
            }
        } catch (Exception e) {
            LOG.debug("delCacheByDelTempGroupV2", e);
        }
    }

    public void deltCacheV2(List<TempGroup> groups) {
        if (groups != null && groups.size() > 0) {
            for (TempGroup group : groups) {
                List<CounsellorRollcall> clist = counsellorRollcallService.findByTemGroupId(group.getId());
                delCache(group, clist);
                delCacheV2(group);
            }
        }
    }

    /**
     * 更新签到状态
     *
     * @param stuId         studentSignIn
     * @param studentSignIn 签到数据
     */
    public void updateRollcallStatus(Long stuId, StudentSignIn studentSignIn) {
        //c.id,n.teacherId,n.teacherName,c.studentName,c.studentNum,n.id,c.gpsLocation,c.gpsDetail,
        // c.signTime,n.openTime,c.haveReport,c.status,c.haveRead,n.status
        List<RollcallReportDomain> list = getRollcallByStuId(stuId);
        if (list != null && list.size() > 0) {
            for (RollcallReportDomain item : list) {
                if (item.getId().longValue() == studentSignIn.getId().longValue()) {
                    item.setStatus(studentSignIn.getStatus());
                    if (studentSignIn.isHaveRead()) {
                        item.setHaveRead(studentSignIn.isHaveRead());
                    }
                    item.setHaveReport(studentSignIn.getHaveReport());
                    item.setGpsDetail(studentSignIn.getGpsDetail());
                    item.setGpsLocation(studentSignIn.getGpsLocation());
                    if (studentSignIn.getSignTime() != null) {
                        item.setSignTime(formatDate(studentSignIn.getSignTime()));
                    }
                    break;
                }
            }
            redisTokenStore.storeRedisData(stuId, list);
        }
    }

    public void updateRollcallStatusV2(Long groupId, Long stuId, StudentSignIn studentSignIn) {
        //c.id,n.teacherId,n.teacherName,c.studentName,c.studentNum,n.id,c.gpsLocation,c.gpsDetail,
        // c.signTime,n.openTime,c.haveReport,c.status,c.haveRead,n.status
        List<StuTempGroupDomainV2> list = getRollcallByStuIdV2(stuId);
        if (list != null && list.size() > 0) {
            LOG.debug("updateRollcallStatusV2:" + list.toString());
            for (StuTempGroupDomainV2 item : list) {
                if (item != null && item.getGroupId().longValue() == groupId.longValue()) {
                    List<StuRollcallReportDomainV2> reportList = item.getReportList();
                    if (reportList != null && reportList.size() > 0) {
                        for (StuRollcallReportDomainV2 srr : reportList) {
                            if (srr.getId().longValue() == studentSignIn.getId().longValue()) {
                                if (StringUtils.isNotEmpty(studentSignIn.getStatus())) {
                                    srr.setGpsDetail(studentSignIn.getGpsDetail());
                                    srr.setGpsLocation(studentSignIn.getGpsLocation());
                                    srr.setHaveReport(studentSignIn.getHaveReport());
                                    if (studentSignIn.getSignTime() != null) {
                                        srr.setSignTime(DateFormatUtil.format(studentSignIn.getSignTime(), DateFormatUtil.FORMAT_SHORT_MINUTE));
                                    }
                                    srr.setStatus(studentSignIn.getStatus());
                                } else {
                                    srr.setGpsDetail2(studentSignIn.getGpsDetail2());
                                    srr.setGpsLocation2(studentSignIn.getGpsLocation2());
                                    srr.setHaveReport2(studentSignIn.getHaveReport2());
                                    if (studentSignIn.getSignTime2() != null) {
                                        srr.setSignTime2(DateFormatUtil.format(studentSignIn.getSignTime2(), DateFormatUtil.FORMAT_SHORT_MINUTE));
                                    }
                                    srr.setStatus2(studentSignIn.getStatus2());
                                }
                                //更新消息
                                UpdateRollcallMessageThread.addCounsellerNotCount(item);
                                break;
                            }
                        }
                    }
                    break;
                }
            }
            redisTokenStore.storeRedisDataV2(stuId, list);
        }
    }

    private String formatDate(Timestamp time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义格式，不显示毫秒
        return df.format(time);
    }

    /**
     * 查询列表
     *
     * @param stuId
     * @return
     */
    public List<RollcallReportDomain> getRollcallReportsByStuId(Long stuId) {
        List<RollcallReportDomain> rollcallReportDomains = redisTokenStore.readRedisData(stuId);
        if (rollcallReportDomains != null && rollcallReportDomains.size() > 0) {
            boolean isUpdate = false;
            for (RollcallReportDomain item : rollcallReportDomains) {
                if (!item.getHaveRead()) {
                    item.setHaveRead(true);
                    isUpdate = true;
                } else {
                    break;
                }
            }
            if (isUpdate) {
                redisTokenStore.storeRedisData(stuId, rollcallReportDomains);
//                UpdateRollcallReportDomain data = new UpdateRollcallReportDomain();
//                data.setStuId(stuId);
//                data.setList(rollcallReportDomains);
//                UpdateRollcallCacheThread.add(data);
            }
        }
        return rollcallReportDomains;
    }

    public List<StuTempGroupDomainV2> getRollcallReportsByStuIdV2(Long stuId) {
        List<StuTempGroupDomainV2> rollcallReportDomains = redisTokenStore.readRedisDataV2(stuId);
        return rollcallReportDomains;
    }

    private List<RollcallReportDomain> getRollcallByStuId(Long stuId) {
        return redisTokenStore.readRedisData(stuId);
    }

    public List<StuTempGroupDomainV2> getRollcallByStuIdV2(Long stuId) {
        return redisTokenStore.readRedisDataV2(stuId);
    }

    public void putLongReidsData(Long key, Long value) {
        redisTokenStore.storeLongRedisData(key, value);
    }

    public Long readLongReidsData(Long key) {
        return redisTokenStore.readLongReidsData(key);
    }

    public void putRollcallRule(Long key, CounsellorRollcallRule data) {
        redisTokenStore.storeRule(key, data);
    }

    public CounsellorRollcallRule getRollcallRule(Long key) {
        return redisTokenStore.getRule(key);
    }
}
