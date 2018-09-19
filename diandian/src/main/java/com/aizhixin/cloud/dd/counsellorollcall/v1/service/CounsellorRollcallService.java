package com.aizhixin.cloud.dd.counsellorollcall.v1.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import com.aizhixin.cloud.dd.common.core.ApiReturn;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.provider.store.redis.RedisTokenStore;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.constant.LeaveConstants;
import com.aizhixin.cloud.dd.counsellorollcall.dto.CounRollcallStatisticsDTO;
import com.aizhixin.cloud.dd.counsellorollcall.entity.AlarmClock;
import com.aizhixin.cloud.dd.counsellorollcall.repository.AlarmClockRepository;
import com.aizhixin.cloud.dd.counsellorollcall.repository.CounsellorRollcallQuery;
import com.aizhixin.cloud.dd.counsellorollcall.repository.TempGroupRepository;
import com.aizhixin.cloud.dd.counsellorollcall.utils.CounsellorStatus;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.entity.Leave;
import com.aizhixin.cloud.dd.rollcall.repository.LeaveRepository;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.counsellorollcall.domain.CouRollCallDomain;
import com.aizhixin.cloud.dd.counsellorollcall.entity.CounsellorRollcall;
import com.aizhixin.cloud.dd.counsellorollcall.entity.StudentSubGroup;
import com.aizhixin.cloud.dd.counsellorollcall.entity.TempGroup;
import com.aizhixin.cloud.dd.counsellorollcall.repository.CounsellorRollcallRepository;

/**
 * Created by LIMH on 2017/11/30.
 */
@Service
@Transactional
public class CounsellorRollcallService {

    private final Logger LOG = LoggerFactory.getLogger(CounsellorRollcallService.class);

    @Autowired
    private CounsellorRollcallRepository counsellorRollcallRepository;
    @Lazy
    @Autowired
    private StudentSubGroupService studentSubGroupService;
    @Lazy
    @Autowired
    private TempGroupService tempGroupService;
    @Autowired
    private TempGroupRepository tempGroupRepository;
    @Lazy
    @Autowired
    private StudentSignInService studentSignInService;
    @Lazy
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;
    @Autowired
    private CounsellorRollcallQuery counsellorRollcallQuery;
    @Lazy
    @Autowired
    private CounsellorRedisService counsellorRedisService;
    @Autowired
    private RedisTokenStore redisTokenStore;
    @Autowired
    private LeaveRepository leaveRepository;
    @Autowired
    private AlarmClockRepository alarmClockRepository;

    /**
     * 添加点名记录
     *
     * @param tempGroup
     */
    public void openConunsellorRollcall(String accessToken, TempGroup tempGroup) {
        if (null == tempGroup) {
            throw new NullPointerException();
        }
        List<StudentSubGroup> studentSubGroups = studentSubGroupService.findAllByTempGroupAndDeleteFlag(tempGroup, DataValidity.VALID.getState());
        if (studentSubGroups == null || studentSubGroups.isEmpty()) {
            return;
        }
        Set<Long> studentIds = new HashSet<>();
        for (StudentSubGroup studentSubGroup : studentSubGroups) {
            studentIds.add(studentSubGroup.getStudentId());
        }

        CounsellorRollcall conunsellorRollcall = new CounsellorRollcall(tempGroup, tempGroup.getTeacherId(), tempGroup.getTeacherName(), new Timestamp(System.currentTimeMillis()), Boolean.TRUE);
        conunsellorRollcall = counsellorRollcallRepository.save(conunsellorRollcall);

        List<Leave> leaves = null;
        if (tempGroup.getRollcallNum() != null && tempGroup.getRollcallNum() > 1) {
            AlarmClock alarmClock = alarmClockRepository.findByTempGroupAndDeleteFlag(tempGroup, DataValidity.VALID.getState());
            if (alarmClock != null) {
                leaves = leaveRepository.findByStatusAndDeleteFlagAndStudentIdIn2(LeaveConstants.STATUS_PASS, DataValidity.VALID.getState(), DateFormatUtil.parse2(DateFormatUtil.formatShort(new Date()) + " " + alarmClock.getClockTime(), DateFormatUtil.FORMAT_MINUTE), DateFormatUtil.parse2(DateFormatUtil.formatShort(new Date()) + " " + alarmClock.getEndTime(), DateFormatUtil.FORMAT_MINUTE), studentIds);
            }
        } else {
            leaves = leaveRepository.findByStatusAndDeleteFlagAndStudentIdIn(LeaveConstants.STATUS_PASS, DataValidity.VALID.getState(), conunsellorRollcall.getOpenTime(), studentIds);
        }
        Map<Long, Boolean> leaveMap = null;
        if (leaves != null && leaves.size() > 0) {
            leaveMap = new HashMap<>();
            for (Leave leave : leaves) {
                leaveMap.put(leave.getStudentId(), Boolean.TRUE);
            }
        }

        redisTokenStore.storeGroupId(conunsellorRollcall.getId(), tempGroup.getId());
        studentSignInService.save(accessToken, conunsellorRollcall, studentIds, tempGroup.getOrgId(), tempGroup.getRollcallNum(), leaveMap);
    }

    /**
     * 关闭点名
     *
     * @param tempGroup
     */
    public void closeConunsellorRollcall(TempGroup tempGroup, boolean isClean, boolean isAll) {
        LOG.warn("closeConunsellorRollcall:" + tempGroup.toString());
        if (null == tempGroup) {
            throw new NullPointerException();
        }
        List<CounsellorRollcall> conunsellorRollcalls = counsellorRollcallRepository.findAllByTempGroupAndStatusAndDeleteFlag(tempGroup, Boolean.TRUE, DataValidity.VALID.getState());
        if (conunsellorRollcalls == null || conunsellorRollcalls.isEmpty()) {
            return;
        }
        for (CounsellorRollcall conunsellorRollcall : conunsellorRollcalls) {
            conunsellorRollcall.setStatus(Boolean.FALSE);
        }
        counsellorRollcallRepository.save(conunsellorRollcalls);
        counsellorRedisService.cleanCache(tempGroup, conunsellorRollcalls, isClean, isAll, false);
    }

    @Async("threadPool1")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void closeConunsellorRollcallV2(TempGroup tempGroup, boolean isClean, boolean isAll, boolean closeAll) {
        LOG.warn("start closeConunsellorRollcall:" + tempGroup.toString());
        if (null == tempGroup) {
            throw new NullPointerException();
        }
        List<CounsellorRollcall> conunsellorRollcalls = counsellorRollcallRepository.findAllByTempGroupAndStatusAndDeleteFlag(tempGroup, Boolean.TRUE, DataValidity.VALID.getState());
        if (conunsellorRollcalls != null && conunsellorRollcalls.size() > 0) {
            for (CounsellorRollcall conunsellorRollcall : conunsellorRollcalls) {
                conunsellorRollcall.setStatus(Boolean.FALSE);
            }
            counsellorRollcallRepository.save(conunsellorRollcalls);
            counsellorRedisService.cleanCache(tempGroup, conunsellorRollcalls, isClean, isAll, closeAll);
        }
        tempGroup.setStatus(Boolean.FALSE);
        tempGroupRepository.save(tempGroup);
        LOG.warn("end closeConunsellorRollcall:" + tempGroup.getId());
    }

    /**
     * 关闭点名
     *
     * @param tempGroup
     */
    public void closeConunsellorRollcallAsyn(TempGroup tempGroup, boolean isClean, boolean isAll) {
        LOG.warn("closeConunsellorRollcall:" + tempGroup.toString());
        if (null == tempGroup) {
            throw new NullPointerException();
        }
        List<CounsellorRollcall> conunsellorRollcalls = counsellorRollcallRepository.findAllByTempGroupAndStatusAndDeleteFlag(tempGroup, Boolean.TRUE, DataValidity.VALID.getState());
        if (conunsellorRollcalls == null || conunsellorRollcalls.isEmpty()) {
            return;
        }
        for (CounsellorRollcall conunsellorRollcall : conunsellorRollcalls) {
            conunsellorRollcall.setStatus(Boolean.FALSE);
        }
        counsellorRollcallRepository.save(conunsellorRollcalls);
        try {
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    System.out.println("--------------------------------------------------");
                    counsellorRedisService.cleanCache(tempGroup, conunsellorRollcalls, isClean, isAll, false);
                }
            };
            Thread thread = new Thread(myRunnable);
            thread.start();
        } catch (Exception e) {
            LOG.debug("closeConunsellorRollcallAsyn-1", e);
        }

    }

    public List<CounsellorRollcall> findByTemGroupId(Long tempGroupId) {
        TempGroup tempGroup = tempGroupService.findOne(tempGroupId);
        if (null == tempGroup) {
            return null;
        }
        return counsellorRollcallRepository.findByTempGroup(tempGroup);
    }

    /**
     * 查询导员点名列表
     *
     * @param tempGroupId
     * @return
     */
    public List<CouRollCallDomain> findConRollCallList(Long tempGroupId) {
        TempGroup tempGroup = tempGroupService.findOne(tempGroupId);
        if (null == tempGroup) {
            throw new NullPointerException();
        }
        return studentSignInService.findConRollCallList(tempGroup);
    }

    public CounsellorRollcall findOne(Long id) {
        return counsellorRollcallRepository.findOne(id);
    }

    /**
     * 查询id
     *
     * @param teacherIds
     * @param startDate
     * @param endDate
     * @return
     */
    public List<Long> getRollcalleverIdByTeacherId(List<Long> teacherIds, Date startDate, Date endDate) {
        if (teacherIds.size() > 0) {
            return counsellorRollcallRepository.findByTeacherIdIn(teacherIds, startDate, endDate);
        } else {
            return null;
        }

    }

    /**
     * web端导员点名列表
     *
     * @param pageNum
     * @param pageSize
     * @param nj
     * @param orgId
     * @param tcollegeId
     * @param status
     * @param startDate
     * @param endDate
     * @return
     */
    public PageData<CounRollcallStatisticsDTO> getCounRollcallStatistics(Integer pageNum, Integer pageSize, String nj, Long orgId, Long tcollegeId, String status, String startDate,
                                                                         String endDate) {
        PageData<CounRollcallStatisticsDTO> listCounRollcall = new PageData<>();
        listCounRollcall.getPage().setPageNumber(pageNum);
        listCounRollcall.getPage().setPageSize(pageSize);

        List<Long> classTeacherIds = null;
        Long count = 0L;
        try {
            if (null != orgId) {
                classTeacherIds = orgManagerRemoteService.getClassTeacherIds(orgId, tcollegeId, nj);
            }
            if (null != classTeacherIds && classTeacherIds.size() > 0) {
                List<Long> ids = getRollcalleverIdByTeacherId(classTeacherIds, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDate + " 00:00:00"),
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate + " 23:59:59"));
                List<Long> isHaveIds = getIsHaveTeacherId(classTeacherIds, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDate + " 00:00:00"),
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate + " 23:59:59"));
                if (null != ids && ids.size() > 0) {
                    // 已提交
                    if (CounsellorStatus.OpenCounsellorRollcall.getType().equals(status)) {
                        Set<Long> counIds = new HashSet(ids);
                        listCounRollcall = counsellorRollcallQuery.listCounRollcall(pageNum, pageSize, counIds);
                        List<CounRollcallStatisticsDTO> data = listCounRollcall.getData();
                        if (data != null && !data.isEmpty()) {
                            for (CounRollcallStatisticsDTO item : data) {
                                getTeacherInfo(item);
                            }
                        }
                    } else {
                        if (null != isHaveIds && isHaveIds.size() > 0) {
                            classTeacherIds.removeAll(isHaveIds);
                        }
                        counsellorNoData(pageNum, pageSize, listCounRollcall, classTeacherIds);
                    }
                } else if (CounsellorStatus.UnOpenCounsellorRollcall.getType().equals(status)) {
                    counsellorNoData(pageNum, pageSize, listCounRollcall, classTeacherIds);
                }
            }
        } catch (Exception e) {
        }
        return listCounRollcall;
    }

    /**
     * 进行数据分页处理
     *
     * @param pageNum
     * @param pageSize
     * @param listCounRollcall
     * @param classTeacherIds
     */
    public void counsellorNoData(Integer pageNum, Integer pageSize, PageData<CounRollcallStatisticsDTO> listCounRollcall, List<Long> classTeacherIds) {
        Long count = new Long(classTeacherIds.size());
        if (pageNum.intValue() != 0) {
            pageNum--;
        }
        int maxsize = pageSize;
        int cursor = 0;
        List<Long> dd = new ArrayList<>();
        for (int i = pageNum * pageSize; i < classTeacherIds.size(); i++) {
            dd.add(classTeacherIds.get(i));
            cursor++;
            if (cursor >= maxsize) {
                break;
            }
        }
        List list = new ArrayList<>();
        for (Long id : dd) {
            CounRollcallStatisticsDTO item = new CounRollcallStatisticsDTO();
            item.setTId(id);
            getTeacherInfo(item);
            list.add(item);
        }
        listCounRollcall.setData(list);
        listCounRollcall.getPage().setTotalElements(count);
        listCounRollcall.getPage().setTotalPages(PageUtil.cacalatePagesize(count, pageSize));
    }

    /**
     * 获取教师数据
     *
     * @param item
     */
    public void getTeacherInfo(CounRollcallStatisticsDTO item) {
        String teacherDomain = orgManagerRemoteService.findByTeacherId(item.getTId());
        if (null != teacherDomain) {
            JSONObject td = JSONObject.fromObject(teacherDomain);
            item.setTName(td.getString("name"));
            item.setJobNumber(td.getString("jobNumber"));
            item.setTCollegeName(td.getString("collegeName"));
        }
    }

    /**
     * 导员点名--点名组详情
     *
     * @param rid
     * @param status
     * @param haveRead
     * @return
     */
    public Map<String, Object> getClassdetailst(Long rid, Integer status, Boolean haveRead) {
        CounsellorRollcall counsellorRollcall = findOne(rid);
        if (counsellorRollcall == null) {
            return null;
        }
        return studentSignInService.findAllByCounsellorRollcallAndStatusAndHaveRead(counsellorRollcall, status, haveRead);
    }

    public List<Long> getIsHaveTeacherId(List<Long> teacherIds, Date startDate, Date endDate) {
        if (teacherIds.size() > 0) {
            return counsellorRollcallRepository.getIsHaveTeacherId(teacherIds, startDate, endDate);
        } else {
            return null;
        }

    }

    /**
     * 删除
     *
     * @param counsellorIds
     * @return
     */
    public Map<String, Object> deleteCounsellor(Set<Long> counsellorIds) {
        try {
            List<CounsellorRollcall> list = new ArrayList<>();
            for (Long counsellorId : counsellorIds) {
                CounsellorRollcall counsellorRollcall = findOne(counsellorId);
                counsellorRollcall.setDeleteFlag(DataValidity.INVALID.getState());
                list.add(counsellorRollcall);
            }
            if (list.size() > 0) {
                counsellorRollcallRepository.save(list);
                counsellorRedisService.cleanCache(null, list, true, false, false);
            }
        } catch (Exception e) {
            return ApiReturn.message(Boolean.FALSE, e.getMessage(), null);
        }
        return ApiReturn.message(Boolean.TRUE, null, null);
    }
}
