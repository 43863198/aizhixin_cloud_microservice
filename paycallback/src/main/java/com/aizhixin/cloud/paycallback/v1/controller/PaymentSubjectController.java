package com.aizhixin.cloud.paycallback.v1.controller;


import com.aizhixin.cloud.paycallback.common.PageData;
import com.aizhixin.cloud.paycallback.domain.IdDomain;
import com.aizhixin.cloud.paycallback.domain.PaymentSubjectQueryListDomain;
import com.aizhixin.cloud.paycallback.domain.PaymentSubjectCalculateDomain;
import com.aizhixin.cloud.paycallback.domain.PaymentSubjectDomain;
import com.aizhixin.cloud.paycallback.entity.PaymentSubject;
import com.aizhixin.cloud.paycallback.service.PaymentSubjectService;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;

@RestController
@RequestMapping("/v1/paymentsubject")
@Api(description = "缴费科目管理API")
public class PaymentSubjectController {
    private PaymentSubjectService paymentSubjectService;
    @Autowired
    public PaymentSubjectController (PaymentSubjectService paymentSubjectService) {
        this.paymentSubjectService = paymentSubjectService;
    }

    /**
     * 上传保存缴费科目信息
     * @param file  缴费科目信息
     * @return                      成功后的实体ID
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "保存缴费科目信息", response = Void.class, notes = "保存缴费科目信息<br><br><b>@author zhen.pan</b>")
    public IdDomain add(
                    @ApiParam(value = "Excel数据文件", required = true) @RequestParam(value = "file") MultipartFile file,
                    @ApiParam(value = "fileName excel文件名称", required = true) @RequestParam(value = "fileName") String fileName,
                    @ApiParam(value = "paymentType 缴费方式(10全款，20分期)", required = true) @RequestParam(value = "paymentType") Integer paymentType,
                    @ApiParam(value = "smallAmount 分期的最低额度") @RequestParam(value = "smallAmount", required = false) Double smallAmount,
                    @ApiParam(value = "installmentRate 分期频次(10首次，20每次)") @RequestParam(value = "installmentRate", required = false) Integer installmentRate,
                    @ApiParam(value = "lastDate 截止日期", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8") @RequestParam(value = "lastDate") Date lastDate,
                    @ApiParam(value = "orgId 组织ID", required = true) @RequestParam(value = "orgId") Long orgId,
                    @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        PaymentSubject paymentSubject = paymentSubjectService.save(file, fileName, paymentType, smallAmount, installmentRate, lastDate, orgId, userId);
        if (null != paymentSubject) {
            IdDomain r = new IdDomain ();
            r.setId(paymentSubject.getId());
            return r;
        }
        return null;
    }

    /**
     * 查询缴费科目信息
     * @param orgId         学校ID
     * @param name          缴费科目名称
     * @param pageNumber    第几页
     * @param pageSize      每页的数据条数
     * @return              查询结果
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据查询条件分页查询指定查询条件的缴费科目信息", response = Void.class, notes = "根据查询条件分页查询指定查询条件的缴费科目信息<br><br><b>@author zhen.pan</b>")
    public PageData<PaymentSubjectQueryListDomain> list(
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "name 缴费科目名称") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return paymentSubjectService.query(orgId, name, pageNumber, pageSize);
    }

    /**
     * 查询缴费科目详情信息
     * @param id    缴费科目ID
     * @return      缴费科目详情信息
     */
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取缴费科目信息", response = Void.class, notes = "获取缴费科目信息<br><br><b>@author zhen.pan</b>")
    public PaymentSubjectDomain get(@ApiParam(value = "ID", required = true) @PathVariable String id) {
        return paymentSubjectService.get(id);
    }

    /**
     * 缴费科目信息的修改
     * @param id  缴费科目ID信息
     * @return                      成功后的实体ID
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "修改缴费科目信息，含重新导入Excel、修改支付方式、修改截止日期", response = Void.class, notes = "修改缴费科目信息，含重新导入Excel、修改支付方式、修改截止日期<br><br><b>@author zhen.pan</b>")
    public IdDomain update(
            @ApiParam(value = "Excel数据文件", required = true) @RequestParam(value = "file") MultipartFile file,
            @ApiParam(value = "fileName excel文件名称", required = true) @RequestParam(value = "fileName") String fileName,
            @ApiParam(value = "ID 缴费科目ID", required = true) @RequestParam(value = "id") String id,
            @ApiParam(value = "paymentType 缴费方式(10全款，20分期)", required = true) @RequestParam(value = "paymentType") Integer paymentType,
            @ApiParam(value = "smallAmount 分期的最低额度") @RequestParam(value = "smallAmount", required = false) Double smallAmount,
            @ApiParam(value = "installmentRate 分期频次(10首次，20每次)") @RequestParam(value = "installmentRate", required = false) Integer installmentRate,
            @ApiParam(value = "lastDate 截止日期", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8") @RequestParam(value = "lastDate") Date lastDate,
            @ApiParam(value = "orgId 组织ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        PaymentSubject paymentSubject = paymentSubjectService.update(id, file, fileName, paymentType, smallAmount, installmentRate, lastDate, orgId, userId);
        if (null != paymentSubject) {
            IdDomain r = new IdDomain ();
            r.setId(paymentSubject.getId());
            return r;
        }
        return null;
    }

    /**
     * 新生录取预置模板下载API
     * @return  excel模板
     * @throws IOException 可能异常
     */
    @RequestMapping(value = "/newstudentcosttemplate", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "新生录取预置模板Excel导入模版下载API", response = Void.class, notes = "新生录取预置模板Excel导入模版下载API<br><br><b>@author panzhen</b>")
    public ResponseEntity<byte[]> exportNewStudentTemplate() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        FileCopyUtils.copy(this.getClass().getResourceAsStream("/excel/newStudentCost.xlsx"), output);
        String filename = URLEncoder.encode("缴费单导入模板.xlsx", "UTF-8");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).header("Content-Type", "application/force-download").header("Content-Disposition", "attachment; filename=" + filename).body(output.toByteArray());
    }

    @RequestMapping(value = "/publish/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "发布缴费科目，使其生效", response = Void.class, notes = "发布缴费科目，使其生效<br><br><b>@author zhen.pan</b>")
    public void publish(
            @ApiParam(value = "ID", required = true) @PathVariable String id,
            @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        paymentSubjectService.publish(id, userId);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "删除缴费科目信息", response = Void.class, notes = "删除缴费科目信息<br><br><b>@author zhen.pan</b>")
    public void delete(
            @ApiParam(value = "ID", required = true) @PathVariable String id,
            @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        paymentSubjectService.delete(id, userId);
    }

    @RequestMapping(value = "/updatepaymenttype", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "修改缴费科目的支付方式", response = Void.class, notes = "修改缴费科目的支付方式<br><br><b>@author zhen.pan</b>")
    public IdDomain update(
            @ApiParam(value = "ID", required = true) @RequestParam("id") String id,
            @ApiParam(value = "paymentType 缴费方式(10全款，20分期)", required = true) @RequestParam(value = "paymentType") Integer paymentType,
            @ApiParam(value = "smallAmount 分期的最低额度") @RequestParam(value = "smallAmount", required = false) Double smallAmount,
            @ApiParam(value = "installmentRate 分期频次(10首次，20每次)") @RequestParam(value = "installmentRate", required = false) Integer installmentRate,
            @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        PaymentSubject paymentSubject = paymentSubjectService.update(id, paymentType, smallAmount, installmentRate, userId);
        if (null != paymentSubject) {
            IdDomain r = new IdDomain ();
            r.setId(paymentSubject.getId());
            return r;
        }
        return null;
    }

    @RequestMapping(value = "/updatelastdate", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "修改缴费科目信息的截止日期", response = Void.class, notes = "修改缴费科目信息的截止日期<br><br><b>@author zhen.pan</b>")
    public IdDomain update(
            @ApiParam(value = "ID", required = true) @RequestParam("id") String id,
            @ApiParam(value = "lastDate 截止日期", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8") @RequestParam(value = "lastDate") Date lastDate,
            @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        PaymentSubject paymentSubject = paymentSubjectService.update(id, lastDate, userId);
        if (null != paymentSubject) {
            IdDomain r = new IdDomain ();
            r.setId(paymentSubject.getId());
            return r;
        }
        return null;
    }

    @RequestMapping(value = "/cal", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "统计缴费科目的缴费信息", response = Void.class, notes = "统计缴费科目的缴费信息<br><br><b>@author zhen.pan</b>")
    public PaymentSubjectCalculateDomain cal(@ApiParam(value = "ID", required = true) @RequestParam(value = "id")  String id) {
        return paymentSubjectService.calcalatePaymentSubject(id);
    }

    @RequestMapping(value = "/exprise", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "手工触发缴费科目的过期", response = Void.class, notes = "手工触发缴费科目的过期<br><br><b>@author zhen.pan</b>")
    public void exprise() {
        paymentSubjectService.checkLastDateTask();
    }


    @RequestMapping(value = "/cansel/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "取消发布状态缴费科目", response = Void.class, notes = "取消发布状态缴费科目<br><br><b>@author zhen.pan</b>")
    public void cansel(
            @ApiParam(value = "ID", required = true) @PathVariable String id,
            @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        paymentSubjectService.cansel(id, userId);
    }


    @RequestMapping(value = "/addpersonalcost", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "给缴费科目新增缴费人员名单(不限状态)", response = Void.class, notes = "给缴费科目新增缴费人员名单(不限状态)<br><br><b>@author zhen.pan</b>")
    public IdDomain addPersonalCost(
            @ApiParam(value = "Excel数据文件", required = true) @RequestParam(value = "file") MultipartFile file,
            @ApiParam(value = "paymentSubjectId 缴费科目的ID", required = true) @RequestParam(value = "paymentSubjectId") String paymentSubjectId,
            @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        PaymentSubject paymentSubject = paymentSubjectService.addPersonalCost(file, paymentSubjectId, userId);
        if (null != paymentSubject) {
            IdDomain r = new IdDomain ();
            r.setId(paymentSubject.getId());
            return r;
        }
        return null;
    }
}