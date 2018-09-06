package com.aizhixin.cloud.paycallback.v1.controller;

import com.aizhixin.cloud.paycallback.domain.third.InputDomain;
import com.aizhixin.cloud.paycallback.domain.third.ReturenDomain;
import com.aizhixin.cloud.paycallback.service.ThirdPayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 支付宝缴费大厅调用接口
 */
@RestController
@RequestMapping("/open/api/v1/alipay")
@Api(description = "支付宝缴费大厅调用接口")
public class AlipayPaymentController {

    @Autowired
    private ThirdPayService thirdPayService;

    @RequestMapping(value = "/student/info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "缴费学生个人信息查询", response = Void.class, notes = "缴费学生个人信息查询<br><br><b>@author zhen.pan</b>")
    public ReturenDomain findPayStudentInfo(@ApiParam(value = "缴费学生查询条件", required = true) @RequestBody InputDomain inputDomain) {
        return thirdPayService.findPayStudentInfo(inputDomain);
    }

    @RequestMapping(value = "/pay/subject", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "缴费学生缴费科目信息查询", response = Void.class, notes = "缴费学生缴费科目信息查询<br><br><b>@author zhen.pan</b>")
    public ReturenDomain findPaySubject(@ApiParam(value = "缴费科目查询条件", required = true) @RequestBody InputDomain inputDomain) {
        return thirdPayService.findPaySubject(inputDomain);
    }

    @RequestMapping(value = "/pay/feed", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "支付宝缴费大厅缴费结果反馈", response = Void.class, notes = "支付宝缴费大厅缴费结果反馈<br><br><b>@author zhen.pan</b>")
    public ReturenDomain doFeedBackOrder(@ApiParam(value = "缴费科目实际缴费反馈结果", required = true) @RequestBody InputDomain inputDomain) {
        return thirdPayService.doFeedBackOrder(inputDomain);
    }
}
