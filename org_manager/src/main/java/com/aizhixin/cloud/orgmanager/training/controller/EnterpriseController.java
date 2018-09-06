package com.aizhixin.cloud.orgmanager.training.controller;

import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.training.entity.Enterprise;
import com.aizhixin.cloud.orgmanager.training.service.EnterpriseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2018-03-15
 */
@RestController
@RequestMapping("/v1/enterprise")
@Api(value = "针对实训企业API", description = "针对实训企业API")
public class EnterpriseController {
    @Autowired
    private EnterpriseService enterpriseService;
    /**
     * 获取企业信息列表
     * @param name
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取企业信息列表", response = Void.class, notes = "获取企业信息列表<br><br><b>@author jianwei.wu</b>")
    public ResponseEntity<Map<String,Object>>getEnterprieList(
            @ApiParam(value = "orgId 学校id", required = true) @RequestParam(value = "orgId", required = true) Long orgId,
            @ApiParam(value = "name 企业名称",required = false) @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize
    ) {
        return new ResponseEntity<>(enterpriseService.getList(orgId, name, PageUtil.createNoErrorPageRequest(pageNumber,pageSize)), HttpStatus.OK);
    }
    /**
     * 保存企业信息
     * @param enterprise
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "保存企业信息", response = Void.class, notes = "保存企业信息<br><br><b>@author jianwei.wu</b>")
    public ResponseEntity<Map<String,Object>>saveEnterprie(
            @ApiParam(value = "<b>必填:<br />name:企业名称") @Valid @RequestBody Enterprise enterprise) {
        return new ResponseEntity<>(enterpriseService.save(enterprise), HttpStatus.OK);
    }

    /**
     *更新企业信息
     * @param enterprise
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "更新企业信息", response = Void.class, notes = "更新企业信息<br><br><b>@author jianwei.wu</b>")
    public ResponseEntity<Map<String,Object>>updateEnterprie(
            @ApiParam(value = "<b>必填:<br/>id:企业id") @Valid @RequestBody Enterprise enterprise) {
        return new ResponseEntity<>(enterpriseService.update(enterprise), HttpStatus.OK);
    }

    /**
     * 删除企业信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "删除企业信息", response = Void.class, notes = "删除企业信息<br><br><b>@author jianwei.wu</b>")
    public ResponseEntity<Map<String,Object>>deleteEnterprie(
            @ApiParam(value = "id 企业id", required = true) @RequestParam(value = "id", required = true) Long id) {
        return new ResponseEntity<>(enterpriseService.delete(id), HttpStatus.OK);
    }





}
