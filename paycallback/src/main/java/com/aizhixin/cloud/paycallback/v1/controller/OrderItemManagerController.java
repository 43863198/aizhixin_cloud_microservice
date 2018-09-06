package com.aizhixin.cloud.paycallback.v1.controller;


import com.aizhixin.cloud.paycallback.common.PageData;
import com.aizhixin.cloud.paycallback.domain.PaymentOrderItemListDomain;
import com.aizhixin.cloud.paycallback.domain.SimplePaymentOrderItemDomain;
import com.aizhixin.cloud.paycallback.service.PaymentOrderItemService;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/v1/orderitem")
@Api(description = "缴费大厅订单管理API")
public class OrderItemManagerController {
    @Autowired
    private PaymentOrderItemService paymentOrderItemService;


    @RequestMapping(value = "/simplelist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询人员缴费的订单明细信息", response = Void.class, notes = "查询人员缴费的订单明细信息<br><br><b>@author zhen.pan</b>")
    public List<SimplePaymentOrderItemDomain> simpleList(
            @ApiParam(value = "人员费用ID", required = true) @RequestParam(value = "personCostId") String personCostId) {
        return paymentOrderItemService.findByItemId(personCostId);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询缴费科目下的所有人员缴费的订单明细信息", response = Void.class, notes = "查询缴费科目下的所有人员缴费的订单明细信息<br><br><b>@author zhen.pan</b>")
    public PageData<PaymentOrderItemListDomain> list(
            @ApiParam(value = "缴费科目ID", required = true) @RequestParam(value = "paymentSubjectId") String paymentSubjectId,
            @ApiParam(value = "name 缴费科目名称") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "start 起始日期") @DateTimeFormat(pattern = "yyyy-MM-dd") @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8") @RequestParam(value = "start", required = false) Date start,
            @ApiParam(value = "end 结束日期") @DateTimeFormat(pattern = "yyyy-MM-dd") @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8") @RequestParam(value = "end", required = false) Date end,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return paymentOrderItemService.findByPaymentSubjectAndQueryParam(paymentSubjectId, name, start, end, pageNumber, pageSize);
    }

}