package com.aizhixin.cloud.dd.approve.services;

import com.aizhixin.cloud.dd.approve.core.ApproveState;
import com.aizhixin.cloud.dd.approve.domain.CourseApproveDomain;
import com.aizhixin.cloud.dd.approve.domain.ApproveStateDomain;
import com.aizhixin.cloud.dd.approve.domain.CourseApproveDomainV2;
import com.aizhixin.cloud.dd.approve.entity.ApproveImg;
import com.aizhixin.cloud.dd.approve.entity.CourseApprove;
import com.aizhixin.cloud.dd.approve.repository.CourseApproveRepository;
import com.aizhixin.cloud.dd.approve.util.DateUtil;
import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.PageDomain;
import com.aizhixin.cloud.dd.common.domain.UserDomain;
import com.aizhixin.cloud.dd.messege.dto.AudienceDTO;
import com.aizhixin.cloud.dd.messege.dto.MessageDTO;
import com.aizhixin.cloud.dd.messege.service.MessageService;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import com.aizhixin.cloud.dd.messege.service.PushMessageService;
import com.aizhixin.cloud.dd.rollcall.service.SmsService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author xiagen
 * @date 2018/2/26 14:19
 */
@Service
@Transactional
public class CourseApproveService {

    @Autowired
    private CourseApproveRepository courseApproveRepository;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;
    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private PushMessageService pushMessageService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private MessageService messageService;

    /**
     * @param courseApproveDomain, userId, name
     * @return com.aizhixin.cloud.dd.approve.entity.CourseApprove
     * @description 调停课审批保存
     */
    public CourseApprove save(CourseApproveDomain courseApproveDomain, AccountDTO accountDTO) {
        CourseApprove courseApprove = new CourseApprove();
        BeanUtils.copyProperties(courseApproveDomain, courseApprove);
        courseApprove.setId(null);
        courseApprove.setApproveNum(DateUtil.getApproveNum());
        List<ApproveImg> approveImgList = new ArrayList<>();
        for (String imgUrl : courseApproveDomain.getApproveImgLsit()) {
            imgUrl = imgUrl.replace("http://7xpscc.com1.z0.glb.clouddn.com", "https://s1.aizhixin.com");
            ApproveImg approveImg = new ApproveImg();
            approveImg.setImgUrl(imgUrl);
            approveImg.setCourseApprove(courseApprove);
            approveImg.setCreatedBy(accountDTO.getId());
            approveImg.setLastModifiedBy(accountDTO.getId());
            approveImgList.add(approveImg);
        }
        courseApprove.setApproveImgList(approveImgList);
        courseApprove.setCreatedBy(accountDTO.getId());
        courseApprove.setLastModifiedBy(accountDTO.getId());
        courseApprove = courseApproveRepository.save(courseApprove);
        pushMessageService.createPushMessage(null, "有新的申请正在等待你审批", "approve_receive", "approve_receive", "调停课审批", courseApprove.getApproveUserId());
        UserDomain userDomain = orgManagerRemoteService.getUser(courseApprove.getApproveUserId());
        if (null != userDomain) {
            if (!StringUtils.isEmpty(userDomain.getPhone())) {
                smsService.sendSms(userDomain.getPhone(), "[知新教师]" + accountDTO.getName() + "发起了调停课审批申请，等待您的审批");
            }
        }

        //----新消息服务----start
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setTitle("调停课审批");
        messageDTO.setContent("有新的申请正在等待你审批");
        messageDTO.setFunction("approve_receive");
        List<AudienceDTO> audienceList = new ArrayList<>();
        audienceList.add(new AudienceDTO(courseApprove.getApproveUserId(), typeCourseApproveDomainV2(courseApprove, accountDTO)));
        messageDTO.setAudience(audienceList);
        messageService.push(messageDTO);
        //----新消息服务----end

        return courseApprove;
    }

    /**
     * @param approveStateDomain, accountDTO
     * @return com.aizhixin.cloud.dd.approve.entity.CourseApprove
     * @description 审批撤销
     */
    public void updateApproveState(ApproveStateDomain approveStateDomain, AccountDTO accountDTO) {
        CourseApprove courseApprove = findByOne(approveStateDomain.getCourseApproveId());
        courseApprove.setApproveOpinion(approveStateDomain.getApproveOpinion());
        courseApprove.setApproveState(approveStateDomain.getApproveState());
        courseApprove.setApproveDate(new Date());
       if(!approveStateDomain.getApproveImgResultList().isEmpty()){
           List<String> list=new ArrayList<>();
           for (String s:approveStateDomain.getApproveImgResultList()){
               list.add(s.replaceAll("http://7xpscc.com1.z0.glb.clouddn.com","https://s1.aizhixin.com"));
           }
           String json=JSON.toJSONString(list);
           courseApprove.setApproveImgResultList(json);
       }
       courseApproveRepository.save(courseApprove);
       if (approveStateDomain.getApproveState()==ApproveState.APPROVE_PASS||approveStateDomain.getApproveState()==ApproveState.APPROVE_REFUSED){
           pushMessageService.createPushMessage(null,accountDTO.getName()+"审批了你的申请","approve_send","approve_send","调停课审批",courseApprove.getCreatedBy());

           //----新消息服务----start
           MessageDTO messageDTO = new MessageDTO();
           messageDTO.setTitle("调停课审批");
           messageDTO.setContent(accountDTO.getName()+"审批了你的申请");
           messageDTO.setFunction("approve_send");
           List<AudienceDTO> audienceList = new ArrayList<>();
           audienceList.add(new AudienceDTO(courseApprove.getCreatedBy(), typeCourseApproveDomainV2(courseApprove, accountDTO)));
           messageDTO.setAudience(audienceList);
           messageService.push(messageDTO);
           //----新消息服务----end
       }
    }

    private CourseApproveDomainV2 typeCourseApproveDomainV2(CourseApprove courseApprove, AccountDTO accountDTO){
        CourseApproveDomainV2 courseApproveDomainV2 = new CourseApproveDomainV2();
        BeanUtils.copyProperties(courseApprove, courseApproveDomainV2);
        courseApproveDomainV2.setApplyUserId(courseApprove.getCreatedBy());
        courseApproveDomainV2.setApplyUserName(accountDTO.getName());
        courseApproveDomainV2.setApplyUserAvtar(accountDTO.getAvatar());
        if (!StringUtils.isEmpty(courseApprove.getApproveImgResultList())) {
            List<String> approveImgResultList = JSON.parseArray(courseApprove.getApproveImgResultList(), String.class);
            courseApproveDomainV2.setApproveImgResultList(approveImgResultList);
        }
        List<String> imgList = new ArrayList<>();
        for (ApproveImg approveImg : courseApprove.getApproveImgList()) {
            imgList.add(approveImg.getImgUrl());
        }
        courseApproveDomainV2.setApproveImgLsit(imgList);
        return courseApproveDomainV2;
    }

    public CourseApprove findByOne(Long id) {
        CourseApprove courseApprove = courseApproveRepository.findByIdAndDeleteFlag(id, DataValidity.VALID.getState());
        return courseApprove;
    }

    /**
     * @param pageNumber, pageSize, result, accountDTO, approveState
     * @return java.util.Map<java.lang.String       ,       java.lang.Object>
     * @description 发送的申请审批
     */
    public Map<String, Object> findMySendApprove(Integer pageNumber, Integer pageSize, Map<String, Object> result, AccountDTO accountDTO, Integer approveState) {
        Pageable page = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
        Page<CourseApprove> courseApprovePage = null;
        if (null == approveState) {
            courseApprovePage = courseApproveRepository.findByCreatedByAndDeleteFlagOrderByCreatedDateDesc(page, accountDTO.getId(), DataValidity.VALID.getState());
        } else {
            courseApprovePage = courseApproveRepository.findByCreatedByAndApproveStateAndDeleteFlagOrderByCreatedDateDesc(page, accountDTO.getId(), approveState, DataValidity.VALID.getState());
        }
        List<CourseApprove> courseApproveList = courseApprovePage.getContent();
        List<CourseApproveDomainV2> courseApproveDomainV2List = new ArrayList<>();
        Set<Long> approverUserIds = new HashSet<>();
        UserDomain userDomain = orgManagerRemoteService.getUser(accountDTO.getId());
        for (CourseApprove courseApprove : courseApproveList) {
            CourseApproveDomainV2 courseApproveDomainV2 = new CourseApproveDomainV2();
            BeanUtils.copyProperties(courseApprove, courseApproveDomainV2);
            courseApproveDomainV2.setApplyUserId(courseApprove.getCreatedBy());
            if (null != userDomain) {
                courseApproveDomainV2.setApplyUserName(userDomain.getName());
            }
            courseApproveDomainV2.setApplyUserAvtar(accountDTO.getAvatar());
            if (!StringUtils.isEmpty(courseApprove.getApproveImgResultList())) {
                List<String> approveImgResultList = JSON.parseArray(courseApprove.getApproveImgResultList(), String.class);
                courseApproveDomainV2.setApproveImgResultList(approveImgResultList);
            }
            List<String> imgList = new ArrayList<>();
            for (ApproveImg approveImg : courseApprove.getApproveImgList()) {
                imgList.add(approveImg.getImgUrl());
            }
            courseApproveDomainV2.setApproveImgLsit(imgList);
            courseApproveDomainV2List.add(courseApproveDomainV2);
            approverUserIds.add(courseApprove.getApproveUserId());
        }
        Map<Long, String> mapdata = null;
        if (!approverUserIds.isEmpty()) {
            String json = orgManagerRemoteService.findUserByIds(approverUserIds);
            if (!StringUtils.isEmpty(json)) {
                List<Map> mapList = JSON.parseArray(json, Map.class);
                if (null != mapList && 0 < mapList.size()) {
                    for (Map map : mapList) {
                        if (null == mapdata) {
                            mapdata = new HashMap<>();
                        }
                        if (null != map.get("id") && null != map.get("name")) {
                            mapdata.put(Long.valueOf(map.get("id").toString()), map.get("name").toString());
                        }
                    }
                }
            }
        }
        if (null != mapdata) {
            for (CourseApproveDomainV2 courseApproveDomainV2 : courseApproveDomainV2List) {
                courseApproveDomainV2.setApproveUserName(mapdata.get(courseApproveDomainV2.getApproveUserId()));
            }
        }
        PageDomain pageDomain = new PageDomain();
        pageDomain.setTotalPages(courseApprovePage.getTotalPages());
        pageDomain.setTotalElements(courseApprovePage.getTotalElements());
        pageDomain.setPageNumber(page.getPageNumber());
        pageDomain.setPageSize(page.getPageSize());
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, courseApproveDomainV2List);
        result.put(ApiReturnConstants.PAGE, pageDomain);
        return result;
    }


    /**
     * @param result, accountDTO, pageNumber, pageSize, isApprove
     * @return java.util.Map<java.lang.String ,   java.lang.Object>
     * @description 审批人接收列表
     */
    public Map<String, Object> findReceiveApprove(Map<String, Object> result, AccountDTO accountDTO, Integer pageNumber, Integer pageSize, boolean isApprove) {
        Pageable page = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
        Page<CourseApprove> courseApprovePage = null;
        if (isApprove) {
            courseApprovePage = courseApproveRepository.findByApproveUserIdAndApproveStateIsNotAndDeleteFlagOrderByCreatedDateDesc(page, accountDTO.getId(), ApproveState.APPROVE_MID, DataValidity.VALID.getState());
        } else {
            courseApprovePage = courseApproveRepository.findByApproveUserIdAndApproveStateAndDeleteFlagOrderByCreatedDateDesc(page, accountDTO.getId(), ApproveState.APPROVE_MID, DataValidity.VALID.getState());
        }
        UserDomain userDomain = orgManagerRemoteService.getUser(accountDTO.getId());
        List<CourseApprove> courseApproveList = courseApprovePage.getContent();
        List<CourseApproveDomainV2> courseApproveDomainV2List = new ArrayList<>();
        Set<Long> applyUserIds = new HashSet<>();
        List<Long> applyUserIdList = new ArrayList<>();
        for (CourseApprove courseApprove : courseApproveList) {
            CourseApproveDomainV2 courseApproveDomainV2 = new CourseApproveDomainV2();
            BeanUtils.copyProperties(courseApprove, courseApproveDomainV2);
            if (null != userDomain) {
                courseApproveDomainV2.setApproveUserName(userDomain.getName());
            }
            if (!StringUtils.isEmpty(courseApprove.getApproveImgResultList())) {
                List<String> approveImgResultList = JSON.parseArray(courseApprove.getApproveImgResultList(), String.class);
                courseApproveDomainV2.setApproveImgResultList(approveImgResultList);
            }
            List<String> imgList = new ArrayList<>();
            for (ApproveImg approveImg : courseApprove.getApproveImgList()) {
                imgList.add(approveImg.getImgUrl());
            }
            courseApproveDomainV2.setApproveImgLsit(imgList);
            courseApproveDomainV2.setApplyUserId(courseApprove.getCreatedBy());
            courseApproveDomainV2List.add(courseApproveDomainV2);
            applyUserIds.add(courseApprove.getCreatedBy());
            applyUserIdList.add(courseApprove.getCreatedBy());
        }
        Map<Long, String> userNameMap = null;
        Map<Long, AccountDTO> userAvtarMap = new HashMap<>();
        if (!applyUserIds.isEmpty()) {
            String json = orgManagerRemoteService.findUserByIds(applyUserIds);
            if (!StringUtils.isEmpty(json)) {
                List<Map> mapList = JSON.parseArray(json, Map.class);
                if (null != mapList && 0 < mapList.size()) {
                    for (Map map : mapList) {
                        if (userNameMap == null) {
                            userNameMap = new HashMap<>();
                        }
                        if (null != map.get("id") && null != map.get("name")) {
                            userNameMap.put(Long.valueOf(map.get("id").toString()), map.get("name").toString());
                        }
                    }
                }
            }
            Map<Long, AccountDTO> accountDTOMap = ddUserService.getUserinfoByIdsV2(applyUserIdList);
            if (null != accountDTOMap) {
                userAvtarMap.putAll(accountDTOMap);
            }
        }
        if (null != userNameMap) {
            for (CourseApproveDomainV2 courseApproveDomainV2 : courseApproveDomainV2List) {
                AccountDTO account = userAvtarMap.get(courseApproveDomainV2.getApplyUserId());
                if (account != null) {
                    courseApproveDomainV2.setApplyUserAvtar(account.getAvatar());
                }
                courseApproveDomainV2.setApplyUserName(userNameMap.get(courseApproveDomainV2.getApplyUserId()));

            }
        }
        PageDomain pageDomain = new PageDomain();
        pageDomain.setTotalPages(courseApprovePage.getTotalPages());
        pageDomain.setTotalElements(courseApprovePage.getTotalElements());
        pageDomain.setPageNumber(page.getPageNumber());
        pageDomain.setPageSize(page.getPageSize());
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, courseApproveDomainV2List);
        result.put(ApiReturnConstants.PAGE, pageDomain);
        return result;
    }

    /**
     * @param id
     * @return com.aizhixin.cloud.dd.approve.domain.CourseApproveDomainV2
     * @description 审批详情信息查询
     */
    public CourseApproveDomainV2 findByCourseApproveInfo(Long id) {
        CourseApproveDomainV2 courseApproveDomainV2 = new CourseApproveDomainV2();
        CourseApprove courseApprove = courseApproveRepository.findByIdAndDeleteFlag(id, DataValidity.VALID.getState());
        if (null != courseApprove) {
            BeanUtils.copyProperties(courseApprove, courseApproveDomainV2);
            UserInfo userInfo = userInfoRepository.findByUserId(courseApprove.getCreatedBy());
            UserInfo userInfov = userInfoRepository.findByUserId(courseApprove.getApproveUserId());
            if (null != userInfo) {
                courseApproveDomainV2.setApplyUserName(userInfo.getName());
                courseApproveDomainV2.setApplyUserAvtar(userInfo.getAvatar());
                courseApproveDomainV2.setApplyUserId(userInfo.getUserId());
            }
            if (!StringUtils.isEmpty(courseApprove.getApproveImgResultList())) {
                List<String> approveImgResultList = JSON.parseArray(courseApprove.getApproveImgResultList(), String.class);
                courseApproveDomainV2.setApproveImgResultList(approveImgResultList);
            }
            if (null != userInfov) {
                courseApproveDomainV2.setApproveUserName(userInfov.getName());
            }
            List<String> imgList = new ArrayList<>();
            for (ApproveImg approveImg : courseApprove.getApproveImgList()) {
                imgList.add(approveImg.getImgUrl());
            }
            courseApproveDomainV2.setApproveImgLsit(imgList);
        }
        return courseApproveDomainV2;
    }


}
