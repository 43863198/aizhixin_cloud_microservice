package com.aizhixin.cloud.dd.rollcall.v1.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.constant.UpgradeConstants;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import com.aizhixin.cloud.dd.rollcall.service.UpgradeService;
import com.aizhixin.cloud.dd.questionnaire.serviceV2.QuestionnaireServiceV2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/web/v1")
@Api(value = "升级API", description = "APP升级API")
public class UpgradeController {

    private final Logger log = LoggerFactory.getLogger(UpgradeController.class);
    @Autowired
    private UpgradeService upgradeService;
    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private QuestionnaireServiceV2 qs;

    /**
     * @param version
     * @param versionDescrip
     * @param type
     * @param role
     * @param isRequired
     * @param file
     * @param accessToken
     * @return
     */
    @RequestMapping(value = "/upgrade/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "上传apk", response = Void.class, notes = "创建老师<br><br><b>@author 杨立强</b>")
    public ResponseEntity<?> create(
            @ApiParam(value = "version 版本号") @RequestParam(value = "version", required = true) String version,
            @ApiParam(value = "versionDescrip 版本描述") @RequestParam(value = "versionDescrip", required = true) String versionDescrip,
            @ApiParam(value = "type ios/android") @RequestParam(value = "type", required = true) String type,
            @ApiParam(value = "role teacher/student") @RequestParam(value = "role", required = true) String role,
            @ApiParam(value = "isRequired yes/no") @RequestParam(value = "isRequired", required = true) String isRequired,
            @ApiParam(value = "file 升级包") @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        if (!UpgradeConstants.UPGRADE_ANDROID.equals(type) && !UpgradeConstants.UPGRADE_IOS.equals(type)) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put(ApiReturnConstants.MESSAGE, "type is error");
            return new ResponseEntity<Object>(resBody, HttpStatus.BAD_REQUEST);
        }
        if (!UpgradeConstants.UPGRADE_ROLE_STUDENT.equals(role) && !UpgradeConstants.UPGRADE_ROLE_TEACHER.equals(role)) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put(ApiReturnConstants.MESSAGE, "role is error");
            return new ResponseEntity<Object>(resBody, HttpStatus.BAD_REQUEST);
        }
        Object result = upgradeService.upgradeApk(version, versionDescrip, type, role, file, isRequired);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    /**
     * 获取最新版本信息
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/upgrade/getInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取最新版本信息", response = Void.class, notes = "获取最新版本信息<br>@author 李美华")
    public ResponseEntity<?> getTeachInfo(
            @ApiParam(value = "ios/android") @RequestParam(value = "type", required = false) String type,
            @ApiParam(value = "teacher/student") @RequestParam(value = "role", required = false) String role,
            @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        if (!UpgradeConstants.UPGRADE_ANDROID.equals(type) && !UpgradeConstants.UPGRADE_IOS.equals(type)) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put(ApiReturnConstants.MESSAGE, "type is error");
            return new ResponseEntity<Object>(resBody, HttpStatus.BAD_REQUEST);
        }
        if (!UpgradeConstants.UPGRADE_ROLE_STUDENT.equals(role) && !UpgradeConstants.UPGRADE_ROLE_TEACHER.equals(role)) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put(ApiReturnConstants.MESSAGE, "role is error");
            return new ResponseEntity<Object>(resBody, HttpStatus.BAD_REQUEST);
        }
        Map<String, Object> result = (Map<String, Object>) upgradeService.getLatestVersionInfo(type, role);
        Map<String, Object> map = qs.isCommit(account, role);
        result.putAll(map);
        result.put("isQuestionnaire", Boolean.FALSE);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }
}
