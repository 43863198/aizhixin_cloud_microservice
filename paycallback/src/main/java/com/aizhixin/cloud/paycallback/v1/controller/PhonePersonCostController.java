package com.aizhixin.cloud.paycallback.v1.controller;


import com.aizhixin.cloud.paycallback.common.PageData;
import com.aizhixin.cloud.paycallback.domain.OrderResultMobileDomain;
import com.aizhixin.cloud.paycallback.domain.OrderUrlMobileDomain;
import com.aizhixin.cloud.paycallback.domain.PersonCostMobileDetailDomain;
import com.aizhixin.cloud.paycallback.domain.PersonCostMobileListDomain;
import com.aizhixin.cloud.paycallback.service.PersonalCostOrderService;
import com.aizhixin.cloud.paycallback.service.PersonalCostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/phone/v1/personcost")
@Api(description = "手机端人员费用管理API")
public class PhonePersonCostController {

    private PersonalCostService personalCostService;
    private PersonalCostOrderService personalCostOrderService;

    @Autowired
    public PhonePersonCostController(PersonalCostService personalCostService, PersonalCostOrderService personalCostOrderService) {
        this.personalCostService = personalCostService;
        this.personalCostOrderService = personalCostOrderService;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据人员的登录信息获取个人缴费相关数据", response = Void.class, notes = "根据人员的登录信息获取个人缴费相关数据<br><br><b>@author zhen.pan</b>")
    public PageData<PersonCostMobileListDomain> list(@RequestHeader("Authorization") String authorization,
                                                     @ApiParam(value = "paymentState 缴费状态(10待缴费(含部分缴费和未缴费)，20已结清，50所有)") @RequestParam(value = "paymentState", required = false) Integer paymentState,
                                                     @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                     @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return personalCostService.phoneQueryPersonCost(authorization, paymentState, pageNumber, pageSize);
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据个人缴费信息ID查询缴费详情明细数据", response = Void.class, notes = "根据个人缴费信息ID查询缴费详情明细数据<br><br><b>@author zhen.pan</b>")
    public PersonCostMobileDetailDomain get(@RequestHeader("Authorization") String authorization,
                                            @ApiParam(value = "personCostId 个人缴费信息ID") @RequestParam(value = "personCostId", required = false) String personCostId) {
        return personalCostService.phoneQueryPersonCostDetail(authorization, personCostId);
    }

    @RequestMapping(value = "/createorder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "生成支付订单", response = Void.class, notes = "生成支付订单<br><br><b>@author zhen.pan</b>")
    public OrderUrlMobileDomain createOrder(@RequestHeader("Authorization") String authorization,
                                       @ApiParam(value = "personCostId 个人缴费信息ID", required = true) @RequestParam(value = "personCostId") String personCostId,
                                       @ApiParam(value = "payAmount 订单金额", required = true) @RequestParam(value = "payAmount") Double payAmount) {
        return personalCostOrderService.createOrder(authorization, personCostId, payAmount);
    }

    @RequestMapping(value = "/querycurrentpay", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询本次支付结果", response = Void.class, notes = "查询本次支付结果<br><br><b>@author zhen.pan</b>")
    public OrderResultMobileDomain queryCurrentPay(@RequestHeader("Authorization") String authorization,
                                                   @ApiParam(value = "out_trade_no 订单号", required = true) @RequestParam(value = "out_trade_no") String out_trade_no,
                                                   @ApiParam(value = "trade_no 支付宝交易号", required = true) @RequestParam(value = "trade_no") String trade_no,
                                                   @ApiParam(value = "app_id AppID", required = true) @RequestParam(value = "app_id") String app_id,
                                                   @ApiParam(value = "seller_id 卖家ID", required = true) @RequestParam(value = "seller_id") String seller_id,
                                                   @ApiParam(value = "total_amount 订单金额", required = true) @RequestParam(value = "total_amount") Double total_amount) {
        return personalCostOrderService.orderQuery(authorization, out_trade_no,trade_no, app_id, seller_id, total_amount);
    }
}