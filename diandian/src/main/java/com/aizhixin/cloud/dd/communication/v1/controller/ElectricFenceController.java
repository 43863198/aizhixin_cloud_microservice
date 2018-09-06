package com.aizhixin.cloud.dd.communication.v1.controller;


import com.aizhixin.cloud.dd.common.exception.DlEduException;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.repository.PushMessageRepository;
import com.aizhixin.cloud.dd.rollcall.repository.PushOutRecodeRepository;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import com.aizhixin.cloud.dd.messege.service.PushService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.*;

/**
 * @author Created by jianwei.wu on ${date} ${time}
 * @E-mail wujianwei@aizhixin.com
 */
@RestController
@RequestMapping("/api/web/v1/electricFence")
@Api(value = "电子围栏API", description = "针对电子围栏web端在点点业务的API")
public class ElectricFenceController {
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;
    @Autowired
    private DDUserService userService;
    @Autowired
    private PushService pushService;
    @Autowired
    private PushMessageRepository pushMessageRepository;
    @Autowired
    private PushOutRecodeRepository pushOutRecodeRepository;

    /**
     * 电子围栏超出范围学生给班主任推送消息
     *
     * @throws DlEduException
     */

    @RequestMapping(value = "/assigned", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "电子围栏超出范围学生给班主任推送消息", response = Void.class, notes = "电子围栏超出范围学生给班主任推送消息<br>@author HUM")
    public Map <String, Object> assigned(
            @ApiParam(value = "学生id") @RequestParam(value = "userId", required = false) Long userId,
            @RequestHeader("Authorization") String accessToken) throws URISyntaxException, DlEduException {
        Map <String, Object> result = new HashMap <String, Object>();
//        AccountDTO account = userService.getUserInfoWithLogin(accessToken);
//        if (account == null) {
//            Map <String, Object> resBody = new HashMap <>();
//            resBody.put("message", "unvalid_token");
//            resBody.put("error", UserConstants.UNVALID_TOKEN);
//            return resBody;
//        }
////        if (!orgManagerRemoteService.checkRoleAdmin(account.getOrganId(), userId)) {
////            Map<String, Object> result = new HashMap<String, Object>();
////            result.put("success", Boolean.FALSE);
////            result.put("msg", "role faild");
////            return result;
////        }

        try {
//            String json = orgManagerRemoteService.getUserInfo(userId);// account.getOrganId()
//            JSONObject userMap = JSONObject.fromObject(json);
//            Long id = userMap.getLong("id");
//            String userName = userMap.getString("name");
//            Long classesId = userMap.getLong("classesId");
//            String jobNumber = userMap.getString("jobNumber");
//            Map <String, Object> teachers = orgManagerRemoteService.getTeachingClassByClassId(classesId);
//            List <Long> userIds = new ArrayList <Long>();
//            if (null != teachers) {
//                List <Map <String, Object>> tls = (List <Map <String, Object>>) teachers.get(ApiReturnConstants.DATA);
//                if (null != tls && tls.size() > 0) {
//                    for (Map <String, Object> tch : tls) {
//                        userIds.add(Long.valueOf(String.valueOf(tch.get("id"))));
//                    }
//                } else {
//                    result.put("success", Boolean.FALSE);
//                    result.put("msg", "没有班主任");
//                    return result;
//                }
//            } else {
//                result.put("success", Boolean.FALSE);
//                result.put("msg", "没有班主任");
//                return result;
//            }
//            List <PushMessage> messages = new ArrayList <PushMessage>();
//            PushMessage message = new PushMessage();
//            //message.setContent("学生" + user.getName() + "(" + user.getPersonId() + ")" + "已离校");
//            message.setContent("学生" + userName + "已离校");
//            message.setFunction(PushMessageConstants.FUNCTION_LEAVETEACHER_NOTICE);
//            message.setModule(PushMessageConstants.MODULE_ELECTRICFENCE);
//            message.setHaveRead(Boolean.FALSE);
//            message.setPushTime(new Date());
////            message.setStatus(PushMessageConstants.STATUS_CREATED);
//            message.setTitle("离校学生通知班主任");
//            message.setUserId(userId);
//            String businessContent = "";
//            message.setBusinessContent(businessContent);
//            messages.add(message);
////            userIds.add(Long.valueOf(String.valueOf(tls.get(0).get("id"))));
//
//            pushMessageRepository.save(message);
//            pushService.listPush(accessToken, "学生" + userName + "已离校", "离校学生通知班主任", "离校学生通知班主任", userIds);
//
//            // 保存推送记录便于教师端消息显示
//            PushOutRecode push = new PushOutRecode();
//            push.setTeacherId(userIds.get(0));
//            push.setStudentId(userId);
//            push.setName(userName);
//            push.setJobNumber(jobNumber);
//            push.setOrganId(account.getOrganId());
//            //超出范围的地址
//            List <String> add = orgManagerRemoteService.findAddressByUserId(userId);
//            //超出范围的上报时间
//            List <Date> time = orgManagerRemoteService.findNoticeTimeByUserId(userId);
//            if(add.size()>0){
//                push.setAddress(add.get(0));
//            }
//            if(time.size()>0){
//                push.setNoticeTime(time.get(0));
//            }
//            pushOutRecodeRepository.save(push);
            result.put("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("errorCode", 100001);
            return result;
        }

        return result;
    }

}
