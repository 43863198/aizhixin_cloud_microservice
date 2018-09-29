package com.aizhixin.cloud.dd.imgManager.v1.controller;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.imgManager.domain.ImgInfoDomain;
import com.aizhixin.cloud.dd.imgManager.service.ImgManagerService;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/phone/v1")
@Api(description = "手机端广告操作")
public class ImgPhoneController {

    @Autowired
    private ImgManagerService imgManagerService;
    @Autowired
    private DDUserService ddUserService;

    @RequestMapping(value = "/img/info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "手机端广告信息", httpMethod = "GET", response = Void.class, notes = "手机端广告信息：数据缓存5分钟<br>@author xiagen")
    public ResponseEntity<Map<String, Object>> getInfo(@RequestHeader("Authorization") String accessToken) {
        Map<String, Object> result = new HashMap<>();
//        AccountDTO accountDTO = ddUserService.getUserInfoWithLogin(accessToken);
//        if (null == accountDTO) {
//            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
//            result.put(ApiReturnConstants.CAUSE, "无权限");
//            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
//        }
        //暂时屏蔽
//        List<ImgInfoDomain> imgInfoDomainList= imgManagerService.findByImgInfo(accountDTO.getOrganId());
        List<ImgInfoDomain> imgInfoDomainList = new ArrayList<>();
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, imgInfoDomainList);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
