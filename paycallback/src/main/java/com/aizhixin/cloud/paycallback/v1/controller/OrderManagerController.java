package com.aizhixin.cloud.paycallback.v1.controller;


import com.aizhixin.cloud.paycallback.common.PageData;
import com.aizhixin.cloud.paycallback.domain.OrderDetailDomain;
import com.aizhixin.cloud.paycallback.domain.PersonCostOrderListDomain;
import com.aizhixin.cloud.paycallback.service.OrderNumberService;
import com.aizhixin.cloud.paycallback.service.PersonalCostOrderService;
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
@RequestMapping("/v1/order")
@Api(description = "订单管理API")
public class OrderManagerController {
    @Autowired
    private OrderNumberService orderNumberService;
    @Autowired
    private PersonalCostOrderService personalCostOrderService;
    @Autowired
    public OrderManagerController() {
    }

    @RequestMapping(value = "/simplelist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询人员缴费的订单明细信息", response = Void.class, notes = "查询人员缴费的订单明细信息<br><br><b>@author zhen.pan</b>")
    public List<PersonCostOrderListDomain> simpleList(
            @ApiParam(value = "人员费用ID", required = true) @RequestParam(value = "personCostId") String personCostId) {
        return personalCostOrderService.findByPersonalCostId(personCostId);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询缴费科目下的所有人员缴费的订单明细信息", response = Void.class, notes = "查询缴费科目下的所有人员缴费的订单明细信息<br><br><b>@author zhen.pan</b>")
    public PageData<OrderDetailDomain> list(
            @ApiParam(value = "缴费科目ID", required = true) @RequestParam(value = "paymentSubjectId") String paymentSubjectId,
//            @ApiParam(value = "name 缴费科目名称") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "start 起始日期") @DateTimeFormat(pattern = "yyyy-MM-dd") @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8") @RequestParam(value = "start", required = false) Date start,
            @ApiParam(value = "end 结束日期") @DateTimeFormat(pattern = "yyyy-MM-dd") @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8") @RequestParam(value = "lastDate", required = false) Date end,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return personalCostOrderService.queryList(paymentSubjectId, start, end, pageNumber, pageSize);
    }
    @RequestMapping(value = "/initRedisIncrement", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "重置订单自增序列", response = Void.class, notes = "重置订单自增序列<br><br><b>@author 王俊</b>")
    public void initRedisIncrement() {
        orderNumberService.initRedisIncrement();
    }
    @RequestMapping(value = "/generaterOrderNumber", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取订单号", response = Void.class, notes = "获取订单号<br><br><b>@author 王俊</b>")
    public String generaterOrderNumber(@ApiParam(value = "组织id", required = true) @RequestParam(value = "orgId") Long orgId) {
        return orderNumberService.generaterOrderNumber(orgId);
    }

}