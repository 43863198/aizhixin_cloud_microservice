package com.aizhixin.cloud.paycallback.v1.controller;


import com.aizhixin.cloud.paycallback.common.PageData;
import com.aizhixin.cloud.paycallback.domain.PayResultDomain;
import com.aizhixin.cloud.paycallback.domain.PersonCostQueryListDomain;
import com.aizhixin.cloud.paycallback.service.PersonalCostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/personcost")
@Api(description = "人员费用管理API")
public class PersonCostController {
    private PersonalCostService personalCostService;
    @Autowired
    public PersonCostController(PersonalCostService personalCostService) {
        this.personalCostService = personalCostService;
    }
    /**
     * 查询缴费科目对应的人员缴费信息
     * @param paymentSubjectId  收费科目ID
     * @param name          姓名/身份证号
     * @param pageNumber    第几页
     * @param pageSize      每页的数据条数
     * @return              查询结果
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据查询条件分页查询指定查询条件的缴费科目对应的人员缴费信息", response = Void.class, notes = "根据查询条件分页查询指定查询条件的缴费科目对应的人员缴费信息<br><br><b>@author zhen.pan</b>")
    public PageData<PersonCostQueryListDomain> list(
            @ApiParam(value = "paymentSubjectId 收费科目ID", required = true) @RequestParam(value = "paymentSubjectId") String paymentSubjectId,
            @ApiParam(value = "name 姓名/身份证号") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "paymentState 缴费状态") @RequestParam(value = "paymentState", required = false) Integer paymentState,
            @ApiParam(value = "professionalName 专业名称") @RequestParam(value = "professionalName", required = false) String professionalName,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return personalCostService.query(paymentSubjectId, name, paymentState, professionalName, pageNumber, pageSize);
    }

    @RequestMapping(value = "/cansel/{idNumber}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "修改缴费人员状态为自愿放弃", response = Void.class, notes = "修改缴费人员状态为自愿放弃<br><br><b>@author zhen.pan</b>")
    public void cansel(
            @ApiParam(value = "idNumber 身份证号码", required = true) @PathVariable String idNumber,
            @ApiParam(value = "userId 接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        personalCostService.cansel(idNumber, userId);
    }

    @Deprecated
    @RequestMapping(value = "/count/{idNumber}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "统计对应身份证号码的缴费支付成功的次数，在允许分期的情况下，如果已经缴费金额小于分期的最低缴费金额，缴费次数按0计算", response = Void.class, notes = "统计对应身份证号码的缴费支付成功的次数，<br/>在允许分期的情况下，如果已经缴费金额小于分期的最低缴费金额，缴费次数按0计算<br><br><b>@author zhen.pan</b>")
    public Long countOrders(
            @ApiParam(value = "idNumber 身份证号码", required = true) @PathVariable String idNumber) {
        return personalCostService.countByIdNumberPaymentOrder(idNumber);
    }


    @RequestMapping(value = "/cal/{idNumber}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "统计个人最近一次缴费对应的缴费情况", response = Void.class, notes = "统计个人最近一次缴费对应的缴费情况，用于替换接口：/v1/personcost/count/{idNumber}<br><br><b>@author zhen.pan</b>")
    public PayResultDomain calPayCountLast( @ApiParam(value = "idNumber 身份证号码", required = true) @PathVariable String idNumber) {
        return personalCostService.calIdNumberPaymentOrder(idNumber);
    }
}