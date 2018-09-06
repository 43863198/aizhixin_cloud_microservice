package com.aizhixin.cloud.dd.messege.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aizhixin.cloud.dd.constant.PushMessageConstants;
import com.aizhixin.cloud.dd.messege.dto.AudienceDTO;
import com.aizhixin.cloud.dd.messege.dto.MessageDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.rollcall.dto.PageInfo;
import com.aizhixin.cloud.dd.rollcall.dto.PushMessageDTO;
import com.aizhixin.cloud.dd.rollcall.dto.PushMessageDTOV2;
import com.aizhixin.cloud.dd.rollcall.dto.PushMessageStatusDTO;
import com.aizhixin.cloud.dd.messege.entity.PushMessage;
import com.aizhixin.cloud.dd.rollcall.repository.PushMessageQuery;
import com.aizhixin.cloud.dd.rollcall.repository.PushMessageRepository;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class PushMessageService {

    @Autowired
    private PushMessageRepository pushMessageRepository;

    @Autowired
    private PushMessageQuery pushMessageQuery;

    @Autowired
    private PushService pushService;

    @Autowired
    private MessageService messageService;

    public PageInfo getMessageByModuleAndFunctionAndUserId(Pageable pageable, String module, String function, Long id) {
        PageInfo pageInfo = PageInfo.getPageInfo(pageable);
        Page<PushMessage> page = null;
        try {
            //TODO 消息 查询
            page = pushMessageRepository.findAllByModuleAndFunctionAndUserIdAndDeleteFlag(module, function, id, DataValidity.VALID.getState(), pageable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        pageInfo.setTotalCount(page.getTotalElements());
        pageInfo.setPageCount(page.getTotalPages());
        List<PushMessage> pmList = page.getContent();
        List results = new ArrayList();
        PushMessageDTO pmDto = null;
        for (PushMessage pushMessage : pmList) {
            pmDto = new PushMessageDTO();
            BeanUtils.copyProperties(pushMessage, pmDto);
            pmDto.setPushTime(DateFormatUtil.format(pushMessage.getPushTime()));
            results.add(pmDto);
        }
        pageInfo.setData(results);
        readMessage(module, function, id);
        return pageInfo;
    }

    //    @Cacheable(value = "CACHE.GETSTATUSINFO", key = "#id")
    public List<PushMessageStatusDTO> getMessageStatus(String module, String function, Long id) {
        return pushMessageQuery.queryMessageState(module, function, id);
    }

    public PushMessage createPushMessage(String businessContent, String content, String function, String module, String title, Long userId) {
        PushMessage message = new PushMessage();
        message.setBusinessContent(businessContent);
        message.setContent(content);
        message.setFunction(function);
        message.setHaveRead(Boolean.FALSE);
        message.setModule(module);
        message.setPushTime(new Date());
        message.setDeleteFlag(DataValidity.VALID.getState());
        message.setTitle(title);
        message.setUserId(userId);
        pushMessageRepository.save(message);
        return message;
    }

    public void readMessage(String module, String function, Long userId) {
        //TODO 消息 设为已读 需要更新缓存
        List<PushMessage> list = pushMessageRepository.findAllByModuleAndFunctionAndUserIdAndHaveRead(module, function, userId, Boolean.FALSE);
        for (PushMessage pushMessage : list) {
            pushMessage.setHaveRead(Boolean.TRUE);
        }
        pushMessageRepository.save(list);
    }

    public int delete(Long id) {
        return pushMessageRepository.deleteMessage(id,
                DataValidity.INVALID.getState());
    }

    public void saveListPushMessage(PushMessageDTOV2 pushMessageDTOV2) {
        List<PushMessage> pml = new ArrayList<>();
        for (Long userId : pushMessageDTOV2.getUserIds()) {
            PushMessage pm = new PushMessage(userId, pushMessageDTOV2.getContent(), pushMessageDTOV2.getTitle(), pushMessageDTOV2.getModule(), pushMessageDTOV2.getFunction(), false, new Date(), pushMessageDTOV2.getBusinessContent());
            pml.add(pm);
        }
        if (!pml.isEmpty()) {
            pushMessageRepository.save(pml);
        }
    }

    public void saveListPushMessagev2(PushMessageDTOV2 pushMessageDTOV2) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setTitle(pushMessageDTOV2.getTitle());
        messageDTO.setContent(pushMessageDTOV2.getContent());
        messageDTO.setFunction(pushMessageDTOV2.getFunction());
        List<AudienceDTO> audienceList = new ArrayList<>();

        List<PushMessage> pml = new ArrayList<>();
        for (Long userId : pushMessageDTOV2.getUserIds()) {
            PushMessage pm = new PushMessage(userId, pushMessageDTOV2.getContent(), pushMessageDTOV2.getTitle(), pushMessageDTOV2.getModule(), pushMessageDTOV2.getFunction(), false, new Date(), pushMessageDTOV2.getBusinessContent());
            pml.add(pm);

            audienceList.add(new AudienceDTO(userId, pushMessageDTOV2.getBusinessContent()));
        }
        if (!pml.isEmpty()) {
            pushMessageRepository.save(pml);
        }
        if (pushMessageDTOV2.isPush()) {
            pushService.listPush("", pushMessageDTOV2.getContent(), pushMessageDTOV2.getTitle(), pushMessageDTOV2.getTitle(), pushMessageDTOV2.getUserIds());
        }
        if (audienceList != null && audienceList.size() > 0) {
            messageDTO.setAudience(audienceList);
            messageService.push(messageDTO);
        }
    }
}
