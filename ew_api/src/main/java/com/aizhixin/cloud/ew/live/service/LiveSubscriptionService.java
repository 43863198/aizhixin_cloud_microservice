package com.aizhixin.cloud.ew.live.service;

import com.aizhixin.cloud.ew.Main;
import com.aizhixin.cloud.ew.common.util.HttpResponse;
import com.aizhixin.cloud.ew.common.util.OauthPostJson;
import com.aizhixin.cloud.ew.common.util.SendHttp;
import com.aizhixin.cloud.ew.live.domain.LiveSubscriptionDomain;
import com.aizhixin.cloud.ew.live.domain.PushMessageDto;
import com.aizhixin.cloud.ew.live.entity.LiveContent;
import com.aizhixin.cloud.ew.live.jdbc.LiveQuerySubscriptionJdbcTemplate;
import com.aizhixin.cloud.ew.live.repository.LiveRepository;
import com.aizhixin.cloud.ew.live.repository.LiveSubscriptionRepository;
import net.sf.json.JSONObject;
import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by DuanWei on 2017/6/15.
 */
@Component
@Transactional
public class LiveSubscriptionService {
    private final static Logger log = LoggerFactory.getLogger(Main.class);
    String Username = "sjlb11211076";
    String Password = "1234567";
//    private static String local = "http://127.0.0.1:8001/oauth/token";
//    private static String dev = "http://dledudev.aizhixin.com/zhixin_api/oauth/token";
    private static String test = "http://dledutest.aizhixin.com/zhixin_api/oauth/token";
    String accessToken = "";
    @Autowired
    LiveQuerySubscriptionJdbcTemplate liveQuerySubscriptionJdbcTemplate;
    private static AtomicInteger ERROR_NUM = new AtomicInteger(0);
    @Autowired
    LiveSubscriptionRepository liveSubscriptionRepository;

    @Autowired
    LiveRepository liveRepository;


    @SuppressWarnings("rawtypes")
	public void executePerTenMinutes() {
        //得到当天当前未开始的订阅列表
        List<LiveSubscriptionDomain> liveSubscriptionDomains = liveQuerySubscriptionJdbcTemplate.queryList();
        List<PushMessageDto> pushMessageDtoList = new ArrayList<>();
        for (LiveSubscriptionDomain liveSubscriptionDomain : liveSubscriptionDomains) {
            //获取到视频ID和开播时间  得到剩余分钟数
            Date publishTime = liveSubscriptionDomain.getPublishTime();
            long time1 = publishTime.getTime();
            Date date = new Date();
            long time2 = date.getTime();
            long time3 = (time1 - time2) / 1000 / 60;
            //离直播只有20分钟的数据
            if (time3 <= 20) {
                //消息对象
                PushMessageDto push = new PushMessageDto();
                //直播信息 得到名称
                Long videoId = liveSubscriptionDomain.getVideoId();
                LiveContent livecontent = liveRepository.findOne(videoId);
                String title = livecontent.getTitle();
                //当前视频所有用户id
                List<Long> allUserId = liveSubscriptionRepository.findAllByVideoId(videoId);
                push.setTitle(title);
                push.setTime(time3);
                push.setUserId(allUserId);
                pushMessageDtoList.add(push);

            }
        }
        try {
            Map authResponseMap = SendHttp.sendAuthRequest(Username, Password, test);
            accessToken = "Bearer " + authResponseMap.get("access_token");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //循环队列
        for (PushMessageDto pushMessageDto : pushMessageDtoList) {
            Thread thread = new Thread() {
                public void run() {
                    Long time = pushMessageDto.getTime();
                    try {
                        System.out.println("当前剩余时间:" + time + "分钟");
                        Thread.sleep((time - 1) * 60 * 1000);
                        log.debug("任务执行");
                        listPush(pushMessageDto);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (HttpException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
    }


    public void listPush(PushMessageDto pushMessageDto) throws HttpException {
        OauthPostJson post = new OauthPostJson();
        HttpResponse response = null;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("text", "直播时间" + new Date());
        params.put("title", "您预约的" + pushMessageDto.getTitle() + "已开播，快去观看!");
        params.put("ticker", "您有一条新消息");
        params.put("userIds", pushMessageDto.getUserId());
        String paramsStr = JSONObject.fromMap(params).toString();
        try {
            response = post.post("http://dledutest.aizhixin.com/zhixin_api/api/web/v1/push/listpush", paramsStr, accessToken);
        } catch (IOException e) {
            ERROR_NUM.incrementAndGet();
            log.error("调用推送消息接口异常,消息：" + paramsStr);
            e.printStackTrace();
        }
        if (response.getStatusCode() != HttpStatus.OK.value()) {
            log.error("推送消息失败,返回值：" + response.getStatusCode() + ",消息：" + paramsStr);
        } else {
            log.debug("推送消息:" + paramsStr);
        }
    }


}
