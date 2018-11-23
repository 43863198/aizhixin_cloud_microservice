package com.aizhixin.cloud.dd.counsellorollcall.thread;

import com.aizhixin.cloud.dd.constant.PushMessageConstants;
import com.aizhixin.cloud.dd.counsellorollcall.domain.StuTempGroupDomainV2;
import com.aizhixin.cloud.dd.messege.dto.AudienceDTO;
import com.aizhixin.cloud.dd.messege.dto.MessageDTO;
import com.aizhixin.cloud.dd.messege.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.xpath.operations.Bool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class UpdateRollcallMessageThread extends Thread {
    private static LinkedBlockingQueue<MessageDTO> concurrentLinkedQueue = new LinkedBlockingQueue<>();

    private Logger log = LoggerFactory.getLogger(UpdateRollcallMessageThread.class);

    @Autowired
    private MessageService messageService;

    public static void addCounseller(StuTempGroupDomainV2 data) {
        add(data, true);
    }

    public static void addCounsellerNotCount(StuTempGroupDomainV2 data) {
        add(data, false);
    }

    public static void addCounsellersNotCount(List<StuTempGroupDomainV2> data) {
        addList(data, false);
    }

    private static void add(StuTempGroupDomainV2 data, Boolean isCount) {
        try {
            if (data != null) {
                MessageDTO messageDTO = new MessageDTO();
                messageDTO.setIsCount(isCount);
                if (data.getGroupId() != null) {
                    messageDTO.setTitle("辅导员点名通知");
                    messageDTO.setContent("您有新的签到提醒！");
                    messageDTO.setFunction(PushMessageConstants.FUNCITON_STUDENT_NOTICE);
                    List<AudienceDTO> audienceList = new ArrayList<>();
                    audienceList.add(new AudienceDTO(data.getStudentId(), data.getMessageId(), data));
                    messageDTO.setAudience(audienceList);
                } else {
                    messageDTO.setIsDel(true);
                    messageDTO.setFunction(PushMessageConstants.FUNCITON_STUDENT_NOTICE);
                    List<AudienceDTO> audienceList = new ArrayList<>();
                    audienceList.add(new AudienceDTO(data.getStudentId(), data.getMessageId(), data));
                    messageDTO.setAudience(audienceList);
                }
                concurrentLinkedQueue.add(messageDTO);
            }
        } catch (Exception e) {
//            log.warn("Exception", e);
        }
    }

    private static void addList(List<StuTempGroupDomainV2> datas, Boolean isCount) {
        try {
            if (datas != null && datas.size() > 0) {
                int totalCount = datas.size();
                int psize = 300;
                int pcount = totalCount / psize;
                if (totalCount % psize > 0) {
                    pcount++;
                }
                int index = 0;
                for (int i = 0; i < pcount; i++) {
                    index = (i + 1) * psize;
                    if (index > totalCount) {
                        index = totalCount;
                    }
                    MessageDTO messageDTO = new MessageDTO();
                    messageDTO.setIsCount(isCount);
                    StuTempGroupDomainV2 df = datas.get(0);
                    if (df.getGroupId() != null) {
                        messageDTO.setTitle("辅导员点名通知");
                        messageDTO.setContent("您有新的签到提醒！");
                    } else {
                        messageDTO.setIsDel(true);
                    }
                    messageDTO.setFunction(PushMessageConstants.FUNCITON_STUDENT_NOTICE);
                    List<AudienceDTO> audienceList = new ArrayList<>();
                    for (int j = i * psize; j < index; j++) {
                        StuTempGroupDomainV2 data = datas.get(j);
                        audienceList.add(new AudienceDTO(data.getStudentId(), data.getMessageId(), data));
                    }
                    messageDTO.setAudience(audienceList);
                    concurrentLinkedQueue.add(messageDTO);
                }
            }
        } catch (Exception e) {
//            log.warn("Exception", e);
        }
    }

    public static void addDel(StuTempGroupDomainV2 data) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setIsDel(true);
        messageDTO.setFunction(PushMessageConstants.FUNCITON_STUDENT_NOTICE);
        List<AudienceDTO> audienceList = new ArrayList<>();
        audienceList.add(new AudienceDTO(data.getStudentId(), data.getMessageId(), data));
        messageDTO.setAudience(audienceList);
        concurrentLinkedQueue.add(messageDTO);
    }

    @Override
    public void run() {
        for (; ; ) {
            if (concurrentLinkedQueue.size() > 0) {
                try {
                    int size = concurrentLinkedQueue.size();
                    log.info("更新点名消息队列有数据:" + size);
                    for (int i = 0; i < size; i++) {
                        MessageDTO data = concurrentLinkedQueue.poll();
                        if (data != null) {
                            messageService.push(data);
                        }
                    }
                    log.info("更新点名消息完成:" + size);
                } catch (Exception e) {
                    log.info("UpdateRollcallCacheThreadException", e);
                }
            } else {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    log.warn("Exception", e);
                }
            }
        }
    }
}
