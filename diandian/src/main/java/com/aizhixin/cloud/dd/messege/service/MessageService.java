package com.aizhixin.cloud.dd.messege.service;

import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.utils.ConfigCache;
import com.aizhixin.cloud.dd.messege.domain.MessageResultDomain;
import com.aizhixin.cloud.dd.messege.dto.AudienceDTO;
import com.aizhixin.cloud.dd.messege.dto.MessageDTO;
import com.aizhixin.cloud.dd.messege.dto.MessageDTOV2;
import com.aizhixin.cloud.dd.messege.entity.MsgModule;
import com.aizhixin.cloud.dd.messege.repository.MsgModuleRepository;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {
    private final Logger log = LoggerFactory.getLogger(MessageService.class);
    @Autowired
    private MsgModuleRepository moduleRepository;
    @Autowired
    private ConfigCache configCache;

    public void pushList(List<MessageDTO> list) {
        if (list != null && list.size() > 0) {
            for (MessageDTO item : list) {
                push(item);
            }
        }
    }

    public MessageResultDomain push(String title, String content, String function, List<AudienceDTO> audience) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setTitle(title);
        messageDTO.setContent(content);
        messageDTO.setType(10);
        messageDTO.setFunction(function);
        messageDTO.setAudience(audience);
        return push(messageDTO);
    }

    public MessageResultDomain push(MessageDTO messageDTO) {
        List<MsgModule> modules = moduleRepository.findByFunctionAndDeleteFlag(messageDTO.getFunction(), DataValidity.VALID.getState());
        MsgModule module = null;
        if (modules != null && modules.size() > 0) {
            module = modules.get(0);
        }

        Map<String, Object> extras = new HashMap<>();
        if (module != null) {
            extras.put("module", module.getModule());
            extras.put("moduleName", module.getModuleName());
            extras.put("icon", module.getIcon());
            extras.put("url", module.getJumpUrl());
            extras.put("type", module.getJumpType());
            extras.put("function", module.getFunction());
        }

        Map<String, Object> message = new HashMap<>();
        message.put("title", messageDTO.getTitle());
        message.put("content", messageDTO.getContent());
        message.put("type", messageDTO.getType());
        message.put("isDel", messageDTO.getIsDel());
        message.put("isCount", messageDTO.getIsCount());
        message.put("extras", extras);

        Map<String, Object> params = new HashMap<>();
        params.put("audience", messageDTO.getAudience());
        params.put("message", message);
        MessageResultDomain result = post(params);
        return result;
    }

    public MessageResultDomain pushMessage(MessageDTOV2 messageDTO) {
        MessageDTO dto = new MessageDTO();
        dto.setTitle(messageDTO.getTitle());
        dto.setContent(messageDTO.getContent());
        dto.setType(messageDTO.getType());
        dto.setFunction(messageDTO.getFunction());
        dto.setIsCount(messageDTO.getIsCount());
        dto.setIsDel(messageDTO.getIsDel());
        List<AudienceDTO> list = new ArrayList<>();
        for (Long item : messageDTO.getAudience()) {
            AudienceDTO ad = new AudienceDTO();
            ad.setUserId(item);
            ad.setData(messageDTO.getData());
            list.add(ad);
        }
        dto.setAudience(list);
        MessageResultDomain result = push(dto);
        return result;
    }

    private MessageResultDomain post(Map<String, Object> params) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl = configCache.getConfigValueByParm("message.service") + "/api/messages";
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(params);
            String result = restTemplate.postForObject(fooResourceUrl, request, String.class);
            if (StringUtils.isNotEmpty(result)) {
                MessageResultDomain domain = JSON.parseObject(result, MessageResultDomain.class);
                if (domain != null && domain.getSuccess()) {
                    System.out.println(domain.getData());
                    return domain;
                }
            }
        } catch (Exception e) {
            log.warn("MessageException", e);
        }
        return null;
    }
}
