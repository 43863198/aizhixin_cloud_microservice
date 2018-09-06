package com.aizhixin.cloud.ew.live.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.ew.common.PageInfo;
import com.aizhixin.cloud.ew.common.dto.AccountDTO;
import com.aizhixin.cloud.ew.common.service.AuthUtilService;
import com.aizhixin.cloud.ew.live.domain.LiveCommentDomain;
import com.aizhixin.cloud.ew.live.domain.LiveContentDomain;
import com.aizhixin.cloud.ew.live.domain.LiveSubscriptionDomain;
import com.aizhixin.cloud.ew.live.entity.LiveComment;
import com.aizhixin.cloud.ew.live.entity.LiveContent;
import com.aizhixin.cloud.ew.live.service.LiveService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Created by DuanWei on 2017/6/5.
 */
@RestController
@RequestMapping("/api/phone/v1/Live")
@Api(description = "视频点播相关的所有API")
public class Live_Controller {

    @Autowired
    private AuthUtilService authUtilService;

    @Autowired
    private LiveService liveService;


    @RequestMapping(value = "/saveLive", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "保存点播内容", response = Void.class, notes = "WEB端保存点播视频信息")
    public ResponseEntity<Map<String, Object>> saveLive(
            @ApiParam(value = "保存点播信息的结构体") @RequestBody LiveContentDomain liveContentDomain,
            @ApiParam(value = "状态") @RequestParam(value = "status", required = false) Integer status,
            @ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) throws ParseException {
        AccountDTO account = authUtilService.getByToken(token);
        if (account == null) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put("message", "unvalid_token");
            return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
        }
        if (status == null || status <= 0) {
            status = 0;
        }
        return new ResponseEntity<Map<String, Object>>(liveService.saveLiveContent(liveContentDomain, account, String.valueOf(status)), HttpStatus.OK);
    }


    @RequestMapping(value = "/updateLive", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "更新点播内容", response = Void.class, notes = "WEB端更新点播视频信息")
    public ResponseEntity<Map<String, Object>> updateLive(
            @ApiParam(value = "保存点播信息的结构体") @RequestBody LiveContentDomain liveContentDomain,
            @ApiParam(value = "状态") @RequestParam(value = "status", required = false) Integer status,
            @ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) {
        AccountDTO account = authUtilService.getByToken(token);
        if (account == null) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put("message", "unvalid_token");
            return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
        }
        if (status == null || status <= 0) {
            status = 0;
        }
        Map<String, Object> result = liveService.updateLiveContent(liveContentDomain, account, String.valueOf(status));
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/openLive", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "发布点播内容", response = Void.class, notes = "根据ID发布点播内容")
    public ResponseEntity<Map<String, Object>> openLive(
            @ApiParam(value = "视频ID") @RequestParam Long liveId,
            @ApiParam(value = "点播开始时间(yyyy-MM-dd hh:mm:ss)") @RequestParam(value = "date") String date
    ) {

        return new ResponseEntity<Map<String, Object>>(liveService.openLive(liveId, date), HttpStatus.OK);
    }


    @RequestMapping(value = "/delLive", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "删除点播内容", response = Void.class, notes = "根据ID删除WEB端保存点播视频信息")
    public ResponseEntity<Map<String, Object>> delLive(
            @ApiParam(value = "视频ID", required = true) @RequestParam Long liveId) {
        return new ResponseEntity<Map<String, Object>>(liveService.delLiveContent(liveId), HttpStatus.OK);
    }


    @SuppressWarnings("rawtypes")
	@RequestMapping(value = "/queryLiveId", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据ID查询点播内容", response = Void.class, notes = "根据ID查询点播内容")
    public ResponseEntity queryLiveId(
            @ApiParam(value = "视频ID") @RequestParam Long liveId) {
        return new ResponseEntity<>(liveService.queryLiveId(liveId), HttpStatus.OK);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/queryLiveList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取已发布的直播列表", response = Void.class, notes = "获取已发布的直播列表")
    public ResponseEntity queryLiveList(
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @ApiParam(value = "0 已经结束的(回播) 1 未开始的 (预播) 2 正在直播 3 正直播+已结束(组合)") @RequestParam(value = "status", required = false) Integer status,
            @ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) throws Exception {
        AccountDTO account = authUtilService.getByToken(token);
        if (account == null) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put("message", "unvalid_token");
            return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
        }
        PageInfo<LiveContentDomain> PageInfo = liveService.queryLiveList(pageNumber, pageSize, status, account.getId());
        List<LiveContentDomain> data = PageInfo.getData();

        if (data != null && data.size() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Calendar c = Calendar.getInstance();
            Calendar v = Calendar.getInstance();
            for (LiveContentDomain d : data) {
                Date videoTime = sdf.parse(d.getVideoTime());
                v.setTime(videoTime);
                int hour = v.get(Calendar.HOUR_OF_DAY);
                int minute = v.get(Calendar.MINUTE);
                int second = v.get(Calendar.SECOND);
                Date publishTime = d.getPublishTime();
                if (publishTime != null) {
                    //开始时间+视频时间
                    c.setTime(publishTime);
                    c.add(Calendar.HOUR, hour);
                    c.add(Calendar.MINUTE, minute);
                    c.add(Calendar.SECOND, second);
                    Date lastTime = c.getTime();
                    //当前时间
                    Date date = new Date();
                    //已结束
                    if (date.compareTo(lastTime) > 0) {
                        d.setLiveStatus("0");
                    }
                    //未开播
                    if (date.compareTo(publishTime) < 0) {
                        d.setLiveStatus("1");
                    }
                    //直播中
                    if (date.compareTo(lastTime) < 0 && date.compareTo(publishTime) > 0) {
                        d.setLiveStatus("2");
                    }
                }
            }
        }

        return new ResponseEntity<>(PageInfo, HttpStatus.OK);
    }


    @SuppressWarnings("rawtypes")
	@RequestMapping(value = "/queryNoLiveList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取未发布的直播列表", response = Void.class, notes = "获取未发布的直播列表")
    public ResponseEntity queryNoLiveList(
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) {
        AccountDTO account = authUtilService.getByToken(token);
        if (account == null) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put("message", "unvalid_token");
            return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
        }
        Page<LiveContent> liveContents = liveService.queryNoLiveList(pageNumber, pageSize);
        return new ResponseEntity<>(liveContents, HttpStatus.OK);
    }


    @SuppressWarnings("rawtypes")
	@RequestMapping(value = "/recentlyNoLive", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "最近即将预告的直播", response = Void.class, notes = "最近即将预告的直播")
    public ResponseEntity recentlyNoLive(
            @ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) throws Exception {
        AccountDTO account = authUtilService.getByToken(token);
        if (account == null) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put("message", "unvalid_token");
            return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
        }
        List<LiveContentDomain> liveContentDomains = liveService.recentlyNoLive();
        Map<String, Object> Body = new HashMap<>();
        Body.put("count", liveContentDomains.size());
        Body.put("data", liveContentDomains);
        return new ResponseEntity<>(Body, HttpStatus.OK);
    }


    @SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/queryNameAndStatus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据名称和状态查询直播列表", response = Void.class, notes = "根据名称和状态查询直播列表")
    public ResponseEntity queryNameAndStatus(
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @ApiParam(value = "名称") @RequestParam(value = "title", required = false) String title,
            @ApiParam(value = "0 已经结束的(回播) 1 未开始的 (预播) 2 正在直播 3 正直播+已结束(组合)") @RequestParam(value = "typeId", required = false) Integer typeId,
            @ApiParam(value = "状态ID") @RequestParam(value = "status", required = true) Integer status,
            @ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) throws
            Exception {
        AccountDTO account = authUtilService.getByToken(token);
        if (account == null) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put("message", "unvalid_token");
            return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
        }

        PageInfo<LiveContentDomain> PageInfo = liveService.queryNameAndStatus(pageNumber, pageSize, title, typeId, status);
        List<LiveContentDomain> data = PageInfo.getData();
        if (data != null && data.size() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Calendar c = Calendar.getInstance();
            Calendar v = Calendar.getInstance();
            for (LiveContentDomain d : data) {
                Date videoTime = sdf.parse(d.getVideoTime());
                v.setTime(videoTime);
                int hour = v.get(Calendar.HOUR_OF_DAY);
                int minute = v.get(Calendar.MINUTE);
                int second = v.get(Calendar.SECOND);
                Date publishTime = d.getPublishTime();
                if (publishTime != null) {
                    //开始时间+视频时间
                    c.setTime(publishTime);
                    c.add(Calendar.HOUR, hour);
                    c.add(Calendar.MINUTE, minute);
                    c.add(Calendar.SECOND, second);
                    Date lastTime = c.getTime();
                    //当前时间
                    Date date = new Date();
                    //已结束
                    if (date.compareTo(lastTime) > 0) {
                        d.setLiveStatus("0");
                    }
                    //未开播
                    if (date.compareTo(publishTime) < 0) {
                        d.setLiveStatus("1");
                    }
                    //直播中
                    if (date.compareTo(lastTime) < 0 && date.compareTo(publishTime) > 0) {
                        d.setLiveStatus("2");
                    }
                }
            }
        }
        return new ResponseEntity<>(PageInfo, HttpStatus.OK);
    }


    @RequestMapping(value = "/onlineNumber", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "在线访问计算", response = Void.class, notes = "计算当前直播在线人数")
    public ResponseEntity<Map<String, Object>> onlineNumber(
            @ApiParam(value = "视频ID") @RequestParam Long liveId) {
        int i = liveService.saveOnlineNumber(liveId);
        Map<String, Object> result = new HashMap<>();
        if (i > 0) {
            result.put("result", "success");
        }
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/saveLiveSubscription", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "保存订阅内容", response = Void.class, notes = "WEB端保存订阅信息")
    public ResponseEntity<Map<String, Object>> saveLiveSubscription(
            @ApiParam(value = "保存订阅信息的结构体") @RequestBody LiveSubscriptionDomain liveSubscriptionDomain,
            @ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) throws ParseException {
        AccountDTO account = authUtilService.getByToken(token);
        if (account == null) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put("message", "unvalid_token");
            return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<Map<String, Object>>(liveService.saveLiveSubscription(liveSubscriptionDomain, account), HttpStatus.OK);
    }


    @RequestMapping(value = "/cancelLiveSubscription", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "取消订阅内容", response = Void.class, notes = "WEB端取消订阅内容")
    public ResponseEntity<Map<String, Object>> cancelLiveSubscription(
            @ApiParam(value = "保存订阅信息的结构体 userId 发送用户Id") @RequestBody LiveSubscriptionDomain liveSubscriptionDomain,
            @ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) throws ParseException {
        AccountDTO account = authUtilService.getByToken(token);
        if (account == null) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put("message", "unvalid_token");
            return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<Map<String, Object>>(liveService.cancelLiveSubscription(liveSubscriptionDomain, account.getId()), HttpStatus.OK);
    }


    @RequestMapping(value = "/saveMessageComment", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "保存评论内容", response = Void.class, notes = "WEB端保存保存评论信息")
    public ResponseEntity<Map<String, Object>> saveMessageComment(
            @ApiParam(value = "保存评论信息的结构体") @RequestBody LiveCommentDomain liveCommentDomain,
            @ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) throws ParseException {
        AccountDTO account = authUtilService.getByToken(token);
        if (account == null) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put("message", "unvalid_token");
            return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<Map<String, Object>>(liveService.saveMessageComment(liveCommentDomain, account), HttpStatus.OK);
    }

    @SuppressWarnings("rawtypes")
	@RequestMapping(value = "/queryMessageCommentList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查看评论列表", response = Void.class, notes = "查看评论列表")
    public ResponseEntity cancelLiveSubscription(
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @ApiParam(value = "视频ID") @RequestParam Long liveId,
            @ApiParam(value = "token 用户登录认证token") @RequestHeader("Authorization") String token) throws Exception {

        AccountDTO account = authUtilService.getByToken(token);
        if (account == null) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put("message", "unvalid_token");
            return new ResponseEntity<Map<String, Object>>(resBody, HttpStatus.UNAUTHORIZED);
        }
        PageInfo<LiveComment> PageInfo = liveService.queryMessageCommentList(pageNumber, pageSize, liveId);
        return new ResponseEntity<>(PageInfo, HttpStatus.OK);
    }


}
