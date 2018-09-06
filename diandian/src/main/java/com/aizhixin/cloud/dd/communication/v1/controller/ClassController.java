package com.aizhixin.cloud.dd.communication.v1.controller;

import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by DuanWei on 2017/5/27.
 */
@RestController
@RequestMapping("/api/web/v1")
@Api(value = "班级API", description = "针对班级操作API")
public class ClassController {


    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;
    @Autowired
    private DDUserService ddUserService;

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/findAllClassesByHeadTeacherId", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)

    @ApiOperation(httpMethod = "GET", value = "获取班主任所带的班级", response = Void.class, notes = "获取班主任所带的班级<br>@author 段伟")
    public ResponseEntity<?> findAllByHeadTeacherId(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List<IdNameDomain> classs = orgManagerRemoteService.getClassesByTeacher(account.getId());
        return new ResponseEntity(classs, HttpStatus.OK);
    }


    @RequestMapping(value = "/user/info/rollcall", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取用户信息（包含点名信息 仅限学生）", response = Void.class, notes = "获取用户信息（包含点名信息 仅限学生）<br>@author meihua.li")
    public ResponseEntity<?> infoWithRollCall(
            @ApiParam(value = "studentId 学生id") @RequestParam(value = "studentId", required = true) Long studentId,
            @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> studentById = orgManagerRemoteService.getStudentById(studentId);

        try {
            JSONObject userInfos = ddUserService.getUserinfoByIds(Lists.newArrayList(studentId));
            studentById.put("phone", userInfos.getString("phoneNumber"));
            studentById.put("phoneNumber", userInfos.getString("phoneNumber"));
        } catch (Exception e) {
        }

        //遗留bug 缺少一部分信息 需要用map组装
        return new ResponseEntity(studentById, HttpStatus.OK);
    }


}
