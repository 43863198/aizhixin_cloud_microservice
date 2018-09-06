package com.aizhixin.cloud.dd.rollcall.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.aizhixin.cloud.dd.common.utils.ConfigCache;

@Service
public class SmsService {
   @Autowired
   ConfigCache configCache;
   
   public void sendSms(String phone,String context) {
	 
	 String sendUrl=configCache.getConfigValueByParm("user.service.host")+configCache.getConfigValueByParm("user.service.sms");
	 MultiValueMap<String, String> requestEntity =new LinkedMultiValueMap<>();
	 requestEntity.add("phone", phone);
	 requestEntity.add("msg", context);
	 RestTemplate rest=new RestTemplate();
	 rest.postForObject(sendUrl, requestEntity, String.class);
   }
   
}
