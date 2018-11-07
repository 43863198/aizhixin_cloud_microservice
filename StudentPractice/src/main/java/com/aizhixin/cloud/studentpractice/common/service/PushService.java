package com.aizhixin.cloud.studentpractice.common.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.aizhixin.cloud.studentpractice.common.domain.MessageDTOV2;
import com.aizhixin.cloud.studentpractice.common.domain.PushMessageDTO;
import com.aizhixin.cloud.studentpractice.common.util.http.HttpResponse;
import com.aizhixin.cloud.studentpractice.common.util.http.OauthPostJson;
import com.alibaba.fastjson.JSONObject;


@Component
@Scope(value = "singleton")
public class PushService {

	private final Logger log = LoggerFactory.getLogger(PushService.class);

	@Autowired
	private AuthUtilService authUtilService;
	
	@Autowired
	private PushMessageService pushMessageService;
	
	@Value("${dl.dd.back.host}")
	private String ddHost;
	
	@Value("${dl.dd.back.savemsg}")
	private String savemsgUrl;
	
	
	public PushService() {
		log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>启动推送消息线程。");
		Thread pushThread = new Thread(run);
		pushThread.start();
	}

	public void addPushList(PushMessageDTO dto) {

		try {
			pushQueue.put(dto);
		} catch (InterruptedException e) {
			log.error("添加消息到推送队列异常。");
			e.printStackTrace();
		}
	}

	private final static int PUSH_QUEUE_SIZE = 10000;
	private final static int PUSH_QUEUE_OVER_SIZE = 1000;
	private static AtomicInteger ERROR_NUM = new AtomicInteger(0);

	private static BlockingQueue<PushMessageDTO> pushQueue = new LinkedBlockingQueue<PushMessageDTO>(
			PUSH_QUEUE_SIZE);

	Runnable run = new Runnable() {
		@Override
		public void run() {
			while (true) {
				try {
					log.debug("获取需要推送的消息数据...");
					PushMessageDTO message = pushQueue.take();
					if (message == null) {
						sleep(500);
						continue;
					}
					listPush(message);
					sleep(50);
					log.debug("推送消息成功,现推送队列消息数量为:" + pushQueue.size());
				} catch (Exception e) {
					ERROR_NUM.incrementAndGet();
					log.error("推送消息异常。");
					e.printStackTrace();
				}
				// 防止推送消息异常缓慢，导致队列增大，从而引起调用服务阻塞。
				if (ERROR_NUM.get() > 20
						&& pushQueue.size() > PUSH_QUEUE_OVER_SIZE) {
					log.error("无法正常推送消息，已删除消息条数:" + pushQueue.size()
							+ ",请检查消息推送服务。");
					pushQueue.clear();
					ERROR_NUM.set(0);
				}
			}
		}
		
		public void listPush(PushMessageDTO dto){
			
		    authUtilService.saveMsgInfor(dto);
			MessageDTOV2 messageDTO = new MessageDTOV2();
			messageDTO.setAudience(dto.getUserIds());
			messageDTO.setContent(dto.getContent());
			messageDTO.setFunction(dto.getFunction());
			messageDTO.setTitle(dto.getTitle());
			messageDTO.setData(dto);
			authUtilService.pushMsg(messageDTO);
			pushMessageService.createPushMessage(dto);
		}
		

		public void sleep(int seconds) {
			try {
				Thread.sleep(seconds);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	};

}
