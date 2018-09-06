package com.aizhixin.cloud.orgmanager.training.controller;

import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.company.domain.excel.LineCodeNameBaseDomain;
import com.aizhixin.cloud.orgmanager.training.dto.CorporateMentorsInfoByEnterpriseDTO;
import com.aizhixin.cloud.orgmanager.training.dto.CorporateMentorsInfoDTO;
import com.aizhixin.cloud.orgmanager.training.dto.CorporateMentorsInfoPageByEnterpriseDTO;
import com.aizhixin.cloud.orgmanager.training.dto.StudentDTO;
import com.aizhixin.cloud.orgmanager.training.dto.TrainingGroupInfoDTO;
import com.aizhixin.cloud.orgmanager.training.entity.CorporateMentorsInfo;
import com.aizhixin.cloud.orgmanager.training.entity.TrainingGroup;
import com.aizhixin.cloud.orgmanager.training.service.EnterpriseService;
import com.aizhixin.cloud.orgmanager.training.service.MentorsTrainingService;
import com.aizhixin.cloud.orgmanager.training.service.TrainingGroupService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Created by jianwei.wu
 * @E-mail wujianwei@aizhixin.com
 */
@RestController
@RequestMapping("/v1/mentorstraining")
@Api(value = "企业导师API", description = "针对企业实训中企业导师API")
public class MentorsTrainingController {
    @Autowired
    private MentorsTrainingService mentorsTrainingService;
    @Autowired
    private TrainingGroupService trainingGroupService;

    /**
     * 企业导师信息保存
     * @param corporateMentorsInfoDTO
     * @return
     */
    @PostMapping(value = "/corporatementorscreat",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "企业导师信息的创建", response = Void.class, notes = "企业导师信息的创建  <br>@author HUM")
    public Map<String,Object> create(
            @ApiParam(value = "corporateMentorsInfo 企业导师信息") @RequestBody CorporateMentorsInfoDTO corporateMentorsInfoDTO) {
        return mentorsTrainingService.save(corporateMentorsInfoDTO);
    }

    /**
     * 查询企业导师列表
     * @param name
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/querycorporatementors",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询企业导师列表的查询", response = Void.class, notes = "查询企业导师列表的查询  <br>@author HUM")
    public PageData<CorporateMentorsInfo> queryCorporateMentorsList(
            @ApiParam(value = "name 企业导师姓名") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "orgId 学校id") @RequestParam(value = "orgId", required = false) Long orgId,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize){
        return mentorsTrainingService.queryCorporateMentorsPage(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), name, orgId);
    }

    /**
     * 查询企业导师信息
     * @param id
     * @return
     */
    @GetMapping(value = "/query/{id}",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "企业导师查询信息", response = Void.class, notes = "企业导师查询信息  <br>@author HUM")
    public CorporateMentorsInfo queryCorporateMentorsInfoById(@ApiParam(value = "id", required = true) @PathVariable Long id) {
        return mentorsTrainingService.queryCorporateMentorsInfoById(id);
    }

    /**
     * 企业导师信息删除
     * @param id
     * @return
     */
    @DeleteMapping(value = "/delete",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "企业导师信息删除", response = Void.class, notes = "企业导师信息删除  <br>@author HUM")
    public Map<String,Object> deleteCorporateMentorsInfoById(
        @ApiParam(value = "id") @RequestParam(value = "id", required = true) Long id,
        @ApiParam(value = "accessToken Authorization") @RequestParam(value = "accessToken", required = true) String  accessToken
        ) {
        return  mentorsTrainingService.deleteCorporateMentorsInfoById(id, accessToken);
    }

    /**
     * 更新企业导师信息
     * @param corporateMentorsInfoDTO
     * @return
     */
    @PutMapping(value = "/update",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "企业导师信息更新", response = Void.class, notes = "企业导师信息更新  <br>@author HUM")
    public Map<String,Object> updateCorporateMentorsInfoById(
            @ApiParam(value = "corporateMentorsInfo 企业导师信息") @RequestBody CorporateMentorsInfoDTO corporateMentorsInfoDTO) {
            return mentorsTrainingService.updateCorporateMentorsInfo(corporateMentorsInfoDTO);
    }

    /**
     * 企业导师查询信息
     * @param id
     * @return
     */
    @GetMapping(value = "/queryinfo/{id}",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "企业导师查询信息", response = Void.class, notes = "企业导师查询信息  <br>@author HUM")
    public CorporateMentorsInfoByEnterpriseDTO queryById(@ApiParam(value = "id", required = true) @PathVariable Long id) {
    	 return trainingGroupService.queryGroupInfoById(id);
    }
    
    @GetMapping(value = "/getgroupinfo",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取实践计划企业信息", response = Void.class, notes = "获取实践计划企业信息  <br>@author HUM")
    public CorporateMentorsInfoByEnterpriseDTO getGroupById(@ApiParam(value = "groupId 实践计划id") @RequestParam(value = "groupId", required = true)  Long groupId) {
    	 return trainingGroupService.getGroupById(groupId);
    }

    /**
     * 企业导师查询信息
     * @param accountId
     * @return
     */@GetMapping(value = "/queryinfobyaccountid/{accountId}",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "企业导师查询信息", response = Void.class, notes = "企业导师查询信息  <br>@author HUM")
    public List<CorporateMentorsInfo> queryByAccountId(@ApiParam(value = "accountId", required = true) @PathVariable Long accountId) {
        return mentorsTrainingService.queryByAccountId(accountId) ;
    }


    /**
     * 企业导师查询信息（开卷）
     * @param accoutId
     * @return
     */
    @GetMapping(value = "/queryinfo",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "企业导师查询信息", response = Void.class, notes = "企业导师查询信息  <br>@author HUM")
    public CorporateMentorsInfoPageByEnterpriseDTO queryInfoByAccoutId(
    		@ApiParam(value = "accountId 企业导师的账号id") @RequestParam(value = "accountId", required = true)  Long accoutId,
            @ApiParam(value = "name 学生姓名") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize
            ) {
        return mentorsTrainingService.queryPageByAccountIdOrName(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), accoutId, name);
    }
    
    @GetMapping(value = "/querystubygroupId",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询实践参加计划学生信息", response = Void.class, notes = "查询实践参加计划学生信息 <br>@author zhengning")
    public PageData<StudentDTO> queryInfoByGroupId(
    		@ApiParam(value = "groupId 实践参与计划id") @RequestParam(value = "groupId", required = true)  Long groupId,
            @ApiParam(value = "name 学生姓名/学号") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize
            ) {
        return mentorsTrainingService.findByGroupIdAndName(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), groupId, name);
    }
    
    /**
     * 根据企业导师账号id查询实训小组（开卷）
     * @param accoutId
     * @return
     */
    @GetMapping(value = "/grouplistbyaccountid",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据企业导师账号id查询实训小组", response = Void.class, notes = "根据企业导师账号id查询实训小组  <br>@jianwei.wu")
    public List<TrainingGroupInfoDTO> queryGroupListByAccoutId(
            @ApiParam(value = "accountId 企业导师的账号id" ,required = true) @RequestParam(value = "accountId")  Long accoutId) {
        return mentorsTrainingService.queryGroupListByAccoutId(accoutId);
    }


    /**
     * 企业导师信息模板下载
     * @return
     * @throws IOException
     */
    @GetMapping(value = "/template",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "企业老师信息Excel导入模版下载API", response = Void.class, notes = "企业老师信息Excel导入模版下载API<br><br><b>@author jianwei.wu</b>")
    public ResponseEntity<byte[]> exportCollegeTemplate() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        FileCopyUtils.copy(this.getClass().getResourceAsStream("/templates/CorporateMentorTemplate.xlsx"), output);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).header("Content-Type", "application/force-download").header("Content-Disposition", "attachment; filename=teacherTemplate.xlsx").body(output.toByteArray());
    }

    /**
     * 根据模板导入企业导师信息
     * @param orgId
     * @param file
     * @return
     */
    @PostMapping(value = "/import", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "根据Excel模板批量导入企业老师信息", response = Void.class, notes = "根据Excel模板批量导入企业老师信息<br><br><b>@author jianwei.wu</b>")
    public Map<String,Object> imporTeacher(
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "Excel数据文件", required = true) @RequestParam(value = "file") MultipartFile file) {
        List<LineCodeNameBaseDomain>  errorInfos = mentorsTrainingService.importCorporateMentorData(orgId, file);
        Map<String,Object> result = new HashMap<>();
        if(errorInfos.size()>0){
            result.put("success",false);
            result.put("errorInfo",errorInfos);
        }else {
            result.put("success",true);
            result.put("errorInfo","");
        }
        return result;
    }


}
