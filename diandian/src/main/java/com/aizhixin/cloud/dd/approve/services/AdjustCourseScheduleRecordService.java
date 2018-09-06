package com.aizhixin.cloud.dd.approve.services;

import com.aizhixin.cloud.dd.approve.domain.AdjustCourseScheduleRecordDomainV2;
import com.aizhixin.cloud.dd.approve.entity.AdjustCourseScheduleRecord;
import com.aizhixin.cloud.dd.approve.repository.AdjustCourseScheduleRecordRepository;
import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.rollcall.service.TempAdjustCourseMessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xiagen
 * @date 2018/3/1 14:55
 * @description  调课记录service
 */
@Service
@Transactional
public class AdjustCourseScheduleRecordService {
    @Autowired
    private AdjustCourseScheduleRecordRepository adjustCourseScheduleRecordRepository;
    @Autowired
    private TempAdjustCourseMessageService tempAdjustCourseMessageService;

    public void saveList(List<AdjustCourseScheduleRecord> adjustCourseScheduleRecordList){
        adjustCourseScheduleRecordRepository.save(adjustCourseScheduleRecordList);
    }

    /**
     * 发送调停课通知
     * @param teachingClassId
     * @param accessToken
     * @param userName
     */
    public void adjustCourseMsg(Long teachingClassId,String accessToken,String userName){
        tempAdjustCourseMessageService.sendMessage(accessToken, teachingClassId, userName);
    }


    /**
     * 调课记录列表
     * @param pageNumber
     * @param pageSize
     * @param userId
     * @param result
     * @return
     */
    public Map<String,Object> findByUserIdAdjustCourseList(Integer pageNumber,Integer pageSize,Long userId,Map<String,Object> result){
       Pageable page= PageUtil.createNoErrorPageRequest(pageNumber,pageSize);
       Page<AdjustCourseScheduleRecord> adjustCourseScheduleRecordPage=adjustCourseScheduleRecordRepository.findByUserIdAndDeleteFlagOrderByCreatedDateDesc(page,userId, DataValidity.VALID.getState());
       List<AdjustCourseScheduleRecord> adjustCourseScheduleRecordList=adjustCourseScheduleRecordPage.getContent();
       List<AdjustCourseScheduleRecordDomainV2> adjustCourseScheduleRecordDomainV2List=new ArrayList<>();
       for (AdjustCourseScheduleRecord adjustCourseScheduleRecord:adjustCourseScheduleRecordList) {
           AdjustCourseScheduleRecordDomainV2 adjustCourseScheduleRecordDomainV2=new AdjustCourseScheduleRecordDomainV2();
           BeanUtils.copyProperties(adjustCourseScheduleRecord,adjustCourseScheduleRecordDomainV2);
           adjustCourseScheduleRecordDomainV2List.add(adjustCourseScheduleRecordDomainV2);

       }
        PageDomain pageDomain=new PageDomain();
        pageDomain.setTotalPages(adjustCourseScheduleRecordPage.getTotalPages());
        pageDomain.setTotalElements(adjustCourseScheduleRecordPage.getTotalElements());
        pageDomain.setPageNumber(page.getPageNumber());
        pageDomain.setPageSize(page.getPageSize());
        result.put(ApiReturnConstants.RESULT,Boolean.TRUE);
        result.put(ApiReturnConstants.DATA,adjustCourseScheduleRecordDomainV2List);
        result.put(ApiReturnConstants.PAGE,pageDomain);
       return result;
    }

}
