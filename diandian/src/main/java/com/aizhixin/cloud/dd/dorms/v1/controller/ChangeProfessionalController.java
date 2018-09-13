package com.aizhixin.cloud.dd.dorms.v1.controller;

import com.aizhixin.cloud.dd.common.core.ApiReturn;
import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.dorms.domain.NewStudentDomain;
import com.aizhixin.cloud.dd.dorms.service.ChangeProfessionalService;
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

@RestController
@RequestMapping("/api/phone/v1/profession")
@Api(description = "转专业API")
public class ChangeProfessionalController {

    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private ChangeProfessionalService changeProfessionalService;

    @RequestMapping(value = "/teacher/getStudentList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询学生列表", response = Void.class, notes = "查询学生列表<br>@author hsh")
    public ResponseEntity<?> getStudentList(@RequestHeader("Authorization") String accessToken,
                                            @ApiParam(value = "name") @RequestParam(value = "name", required = false) String name,
                                            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Pageable pageable = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
        PageData<NewStudentDomain> result = changeProfessionalService.getStudentList(pageable, account.getOrganId(), name);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/getProfessionList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询专业", response = Void.class, notes = "查询专业<br>@author hsh")
    public ResponseEntity<?> getProfessionList(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List<IdNameDomain> result = changeProfessionalService.getProfessionList(account.getOrganId(), account.getId());
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/changeProf", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "调换专业", response = Void.class, notes = "调换专业<br>@author hsh")
    public ResponseEntity<?> changeProf(@RequestHeader("Authorization") String accessToken,
                                        @ApiParam(value = "stuId") @RequestParam(value = "stuId", required = false) Long stuId,
                                        @ApiParam(value = "profId") @RequestParam(value = "profId", required = false) Long profId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();
        try {
            changeProfessionalService.changeProf(stuId, profId);
            result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        } catch (Exception e) {
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

}
