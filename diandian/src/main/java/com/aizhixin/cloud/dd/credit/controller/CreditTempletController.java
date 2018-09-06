package com.aizhixin.cloud.dd.credit.controller;

import com.aizhixin.cloud.dd.common.core.ApiReturn;
import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.credit.domain.CreditTempletDomain;
import com.aizhixin.cloud.dd.credit.dto.CreditTempletDTO;
import com.aizhixin.cloud.dd.credit.entity.CreditTemplet;
import com.aizhixin.cloud.dd.credit.entity.CreditTempletQues;
import com.aizhixin.cloud.dd.credit.service.CreditTempletService;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hsh
 */
@RequestMapping("/api/phone/v1/credit/templet")
@RestController
@Api(value = "素质学分模板相关API", description = "素质学分模板相关API")
public class CreditTempletController {
    @Autowired
    private DDUserService ddUserService;

    @Autowired
    private CreditTempletService templetService;

//    /**
//     * 初始化模板
//     */
//    @RequestMapping(value = "/initTemplet", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(httpMethod = "GET", value = "初始化模板", response = Void.class, notes = "初始化模板<br>@author hsh")
//    public ResponseEntity<?> initTemplet(@RequestHeader("Authorization") String accessToken,
//                                         @ApiParam(value = "orgId") @RequestParam(value = "orgId") Long orgId) {
//        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
//        if (account == null) {
//            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
//        }
//        templetService.initTemplet(orgId);
//        Map<String, Object> reuslt = new HashMap<>();
//        reuslt.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
//        return new ResponseEntity(reuslt, HttpStatus.OK);
//    }

    /**
     * 获取模板列表
     */
    @RequestMapping(value = "/getTempletList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取模板列表", response = Void.class, notes = "获取模板列表<br>@author hsh")
    public ResponseEntity<?> getList(@RequestHeader("Authorization") String accessToken,
                                     @ApiParam(value = "orgId", required = true) @RequestParam(value = "orgId", required = true) Long orgId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List<CreditTemplet> result = templetService.findTempletByOrgid(orgId);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/getTempletListPage", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取模板列表", response = Void.class, notes = "获取模板列表<br>@author hsh")
    public ResponseEntity<?> findTempletPageByOrgid(@RequestHeader("Authorization") String accessToken,
                                                    @ApiParam(value = "orgId", required = true) @RequestParam(value = "orgId", required = true) Long orgId,
                                                    @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                    @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Pageable pageable = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
        PageData<CreditTemplet> result = templetService.findTempletPageByOrgid(pageable, orgId);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * 获取模板
     */
    @RequestMapping(value = "/getTemplet", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取模板", response = Void.class, notes = "获取模板<br>@author hsh")
    public ResponseEntity<?> getTemplet(@RequestHeader("Authorization") String accessToken,
                                        @ApiParam(value = "templetId", required = true) @RequestParam(value = "templetId", required = true) Long templetId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        CreditTempletDomain result = templetService.findTempletById(templetId);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * 保存模板
     */
    @RequestMapping(value = "/saveTemplet", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "保存模板", response = Void.class, notes = "保存模板<br>@author hsh")
    public ResponseEntity<?> saveTeachingTemplet(@RequestHeader("Authorization") String accessToken,
                                                 @ApiParam(value = "模板") @RequestBody CreditTempletDTO templet) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        templetService.saveTemplet(templet);
        return new ResponseEntity(ApiReturn.message(Boolean.TRUE, null, null), HttpStatus.OK);
    }

    /**
     * 删除模板
     */
    @RequestMapping(value = "/deleteTemplet", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "删除模板", response = Void.class, notes = "删除模板<br>@author hsh")
    public ResponseEntity<?> saveTeachingTemplet(@RequestHeader("Authorization") String accessToken,
                                                 @ApiParam(value = "templetId", required = true) @RequestParam(value = "templetId", required = true) Long templetId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        templetService.deleteTemplet(templetId);
        return new ResponseEntity(ApiReturn.message(Boolean.TRUE, null, null), HttpStatus.OK);
    }
}
