package com.aizhixin.cloud.dd.rollcall.v2.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.AssessDTOV2;
import com.aizhixin.cloud.dd.rollcall.dto.RevertDTO;
import com.aizhixin.cloud.dd.rollcall.entity.Assess;
import com.aizhixin.cloud.dd.rollcall.entity.Revert;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import com.aizhixin.cloud.dd.rollcall.serviceV2.AssessServiceV2;
import com.aizhixin.cloud.dd.rollcall.serviceV2.RevertService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/phone/v2")
@Api(description = "评论及回复API")
public class AssessControllerV2 {
    @Autowired
    private AssessServiceV2 assessServicer2;
    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private RevertService revertService;

    @RequestMapping(value = "/assess/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "评价信息保存", response = Void.class, notes = "评价信息保存<br>@author xiagen")
    public ResponseEntity<Map<String, Object>> postAssess(@RequestHeader("Authorization") String accessToken,
                                                          @RequestBody AssessDTOV2 assessDTOV2) {
        AccountDTO adt = ddUserService.getUserInfoWithLogin(accessToken);
        Map<String, Object> result = new HashMap<>();
        if (null == adt) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "无权限");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.UNAUTHORIZED);
        }
        if (null == assessDTOV2.getSourseId() || 0l == assessDTOV2.getSourseId()) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "排课id为空，不能评论");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
        }
        if (StringUtils.isEmpty(assessDTOV2.getModule())) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "保存模块不能为空");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
        }
        Assess as = assessServicer2.saveAssess(assessDTOV2, adt);
        if (null == as) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "保存失败");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
        } else {
            result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
            result.put(ApiReturnConstants.DATA, as.getId());
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "revert/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "回复信息保存", response = Void.class, notes = "评价信息保存<br>@author xiagen")
    public ResponseEntity<Map<String, Object>> postRevert(@RequestHeader("Authorization") String accessToken,
                                                          @RequestBody RevertDTO revertDTO) {
        AccountDTO adt = ddUserService.getUserInfoWithLogin(accessToken);
        Map<String, Object> result = new HashMap<>();
        if (null == adt) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "无权限");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.UNAUTHORIZED);
        }
        Revert rt = revertService.save(revertDTO, adt);
        if (null == rt) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "保存失败");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.EXPECTATION_FAILED);
        } else {
            result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
            result.put(ApiReturnConstants.DATA, rt.getId());
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "assess/and/revert/find", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询评论及回复", response = Void.class, notes = "查询评论及回复<br>@author xiagen")
    public ResponseEntity<Map<String, Object>> findAssessAndRevert(@RequestHeader("Authorization") String accessToken,
                                                                   @ApiParam(value = "sourseId 评论来源id") @RequestParam(value = "sourseId", required = true) Long sourseId,
                                                                   @ApiParam(value = "module 评论模块,pj：评教模块，xyq：校友圈模块，swzl：失误招领模块") @RequestParam(value = "module", required = true) String module,
                                                                   @ApiParam(value = "sort 按什么排序,1:时间排序，2:热度排序 ") @RequestParam(value = "sortName", required = false) Integer sort,
                                                                   @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                                   @ApiParam(value = "pageSize 分页大小") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        AccountDTO adt = ddUserService.getUserInfoWithLogin(accessToken);
        Map<String, Object> result = new HashMap<>();
        if (null == adt) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "无权限");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.UNAUTHORIZED);
        }
        if (null == pageNumber) {
            pageNumber = 0;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        Pageable page = null;
        if (null != sort && 0 != sort) {
            if (1 == sort) {
                page = PageUtil.createNoErrorPageRequestAndSortType(pageNumber, pageSize, "desc", "createdDate");
            } else {
                page = PageUtil.createNoErrorPageRequestAndSortType(pageNumber, pageSize, "desc", "revertTotal");
            }
        } else {
            page = PageUtil.createNoErrorPageRequestAndSortType(pageNumber, pageSize, "desc", "createdDate");
        }
        result = assessServicer2.findByAssessAndRevert(module, sourseId, page, result);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/revert/find", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询单条评论的回复", response = Void.class, notes = "查询单条评论的回复<br>@author xiagen")
    public ResponseEntity<Map<String, Object>> findAssessAndRevert(@RequestHeader("Authorization") String accessToken,
                                                                   @ApiParam(value = "assessId 评论记录id") @RequestParam(value = "assessId", required = true) Long assessId,
                                                                   @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                                   @ApiParam(value = "pageSize 分页大小") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        AccountDTO adt = ddUserService.getUserInfoWithLogin(accessToken);
        Map<String, Object> result = new HashMap<>();
        if (null == adt) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "无权限");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.UNAUTHORIZED);
        }
        if (null == pageNumber) {
            pageNumber = 0;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        result = revertService.findByRevert(assessId, pageNumber, pageSize, result);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/myRevert/find", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询自己所有回复", response = Void.class, notes = "查询自己所有回复<br>@author xiagen")
    public ResponseEntity<Map<String, Object>> findAssessAndRevert(@RequestHeader("Authorization") String accessToken,
                                                                   @ApiParam(value = "module 评论模块，pj:评教，xyq:校友圈，swzl：失物招领") @RequestParam(value = "module", required = false) String module,
                                                                   @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                                   @ApiParam(value = "pageSize 分页大小") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        AccountDTO adt = ddUserService.getUserInfoWithLogin(accessToken);
        Map<String, Object> result = new HashMap<>();
        if (null == adt) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "无权限");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.UNAUTHORIZED);
        }
        if (null == pageNumber || 2 > pageNumber) {
            pageNumber = 1;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        result = revertService.findByRevert(adt.getId(), pageNumber, pageSize, module, result);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }
}
