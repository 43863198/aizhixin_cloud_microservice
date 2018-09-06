package com.aizhixin.cloud.studentpractice.common.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.studentpractice.common.PageData;
import com.aizhixin.cloud.studentpractice.common.PageDomain;
import com.aizhixin.cloud.studentpractice.common.core.ApiReturnConstants;
import com.aizhixin.cloud.studentpractice.common.core.DataValidity;
import com.aizhixin.cloud.studentpractice.common.domain.PushMessageDTO;
import com.aizhixin.cloud.studentpractice.common.domain.PushMessageStatusDTO;
import com.aizhixin.cloud.studentpractice.common.entity.PushMessage;
import com.aizhixin.cloud.studentpractice.common.repository.PushMessageQuery;
import com.aizhixin.cloud.studentpractice.common.repository.PushMessageRepository;
import com.aizhixin.cloud.studentpractice.common.util.DateUtil;


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

    public Map<String, Object> getMessageByModuleAndFunctionAndUserId(Pageable pageable,
                                                           String module, String function, Long id) {
    	Map<String, Object> r = new HashedMap();
    	PageDomain pageDomain = new PageDomain();
        Page<PushMessage> page = null;
        
        try {
            page = pushMessageRepository
                    .findAllByModuleAndFunctionAndUserIdAndDeleteFlag(module,
                            function, id, DataValidity.VALID.getIntValue(),
                            pageable);
        } catch (Exception e) {
            e.printStackTrace();
        }

        pageDomain.setPageNumber(pageable.getPageNumber());
        pageDomain.setPageSize(pageable.getPageSize());
        pageDomain.setTotalElements(page.getTotalElements());
        pageDomain.setTotalPages(page.getTotalPages());
        List<PushMessage> pmList = page.getContent();
        List results = new ArrayList();
        PushMessageDTO pmDto = null;
        for (PushMessage pushMessage : pmList) {
            pmDto = new PushMessageDTO();
            BeanUtils.copyProperties(pushMessage, pmDto);
            pmDto.setPushTime(DateUtil.format(pushMessage.getPushTime()));
            results.add(pmDto);
        }

        readMessage(module, function, id);
        r.put(ApiReturnConstants.PAGE, pageDomain);
		r.put(ApiReturnConstants.DATA, results);
		return r;
    }

  
    public void createPushMessage(PushMessageDTO dto) {
    	if(null != dto && !dto.getUserIds().isEmpty()){
    		List<PushMessage> messageList = new ArrayList<PushMessage>();
	    	for(Long userId : dto.getUserIds()){
				PushMessage message = new PushMessage();
				BeanUtils.copyProperties(dto, message);
//				message.setBusinessContent(dto.getBusinessContent());
//				message.setContent(dto.getContent());
//				message.setFunction(dto.getFunction());
//				message.setModule(dto.getModule());
//				message.setTitle(dto.getTitle());
//				message.setWeekTaskId(dto.getWeekTaskId());
//				message.setMentorTaskId(dto.getMentorTaskId());
//				message.setStuTaskId(dto.getStuTaskId());
//				message.setTaskName(dto.getTaskName());
//				message.setWeekTaskName(dto.getWeekTaskName());
				if(null != dto.getStuTaskIdMap() && !dto.getStuTaskIdMap().isEmpty()){
					if(null != dto.getStuTaskIdMap().get(userId)){
						message.setStuTaskId(dto.getStuTaskIdMap().get(userId));
					}
				}
				message.setPushTime(new Date());
				message.setDeleteFlag(DataValidity.VALID.getIntValue());
				message.setHaveRead(Boolean.FALSE);
				message.setUserId(userId);
				messageList.add(message);
	    	}
	    	messageList = pushMessageRepository.save(messageList);
    	}
	}

    public void readMessage(String module, String function, Long userId) {
        List<PushMessage> list = pushMessageRepository
                .findAllByModuleAndFunctionAndUserIdAndHaveRead(module,
                        function, userId, Boolean.FALSE);
        for (PushMessage pushMessage : list) {
            pushMessage.setHaveRead(Boolean.TRUE);
        }
        pushMessageRepository.save(list);
    }

    public int delete(Long id) {
        return pushMessageRepository.deleteMessage(id,
                DataValidity.INVALID.getIntValue());
    }
    
    
    public int deleteAll(String[] mentorTaskId) {
        return pushMessageRepository.deleteAllMessage(
                DataValidity.INVALID.getIntValue(),mentorTaskId);
    }
}
