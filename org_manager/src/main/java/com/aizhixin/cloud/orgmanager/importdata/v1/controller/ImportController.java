package com.aizhixin.cloud.orgmanager.importdata.v1.controller;

import com.aizhixin.cloud.orgmanager.common.core.ErrorCode;
import com.aizhixin.cloud.orgmanager.common.exception.CommonException;
import com.aizhixin.cloud.orgmanager.company.service.ClassesTeacherService;
import com.aizhixin.cloud.orgmanager.importdata.domain.ImportBaseData;
import com.aizhixin.cloud.orgmanager.importdata.domain.ImportCourseData;
import com.aizhixin.cloud.orgmanager.importdata.service.BaseDataService;
import com.aizhixin.cloud.orgmanager.importdata.service.CourseDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 导入数据
 *
 * @author hsh
 */
@RestController
@RequestMapping("/v1/import")
@Api(description = "数据导入API")
public class ImportController {

    @Autowired
    private BaseDataService baseDataService;
    @Autowired
    private CourseDataService courseDataService;
    @Autowired
    private ClassesTeacherService classesTeacherService;

    @RequestMapping(value = "/basetemplate", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "学校信息模版下载API", response = Void.class, notes = "学校信息模版下载API<br><br><b>@author hsh</b>")
    public ResponseEntity<byte[]> getBaseTemplate() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        FileCopyUtils.copy(this.getClass().getResourceAsStream("/templates/BaseDataTemplate.xlsx"), output);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).header("Content-Type", "application/force-download").header("Content-Disposition", "attachment; filename=BaseDataTemplate.xlsx").body(output.toByteArray());
    }

    @RequestMapping(value = "/basedata", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "根据Excel模板批量导入学校信息", response = Void.class, notes = "根据Excel模板批量导入学校信息<br><br><b>@author hsh</b>")
    public ResponseEntity<?> importBaseData(
            @ApiParam(value = "orgId 组织ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "Excel数据文件", required = true) @RequestParam(value = "file") MultipartFile file,
            @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        String msg = baseDataService.importData(orgId, file, userId);
        Map<String, String> map = new HashMap<>();
        map.put("msg", msg);
        if (StringUtils.isEmpty(msg)) {
            map.put("result", "success");
            try {
                classesTeacherService.initOrgId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            map.put("result", "fail");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/basedatamsg", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查看学校信息Excel导入进度及结果", response = Void.class, notes = "查看学校信息Excel导入进度及结果<br><br><b>@author hsh</b>")
    public ImportBaseData getBaseDataMsg(
            @ApiParam(value = "orgId 组织ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        return baseDataService.getImportMsg(orgId, userId);
    }

    @RequestMapping(value = "/coursetemplate", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "开课信息模版下载API", response = Void.class, notes = "开课信息模版下载API<br><br><b>@author hsh</b>")
    public ResponseEntity<byte[]> getCourseTemplate() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        FileCopyUtils.copy(this.getClass().getResourceAsStream("/templates/CourseDataTemplate.xlsx"), output);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).header("Content-Type", "application/force-download").header("Content-Disposition", "attachment; filename=CourseDataTemplate.xlsx").body(output.toByteArray());
    }

    @RequestMapping(value = "/coursedata", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "根据Excel模板批量导入开课信息", response = Void.class, notes = "根据Excel模板批量导入开课信息<br><br><b>@author hsh</b>")
    public ResponseEntity<?> importCourseData(
            @ApiParam(value = "orgId 组织ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "Excel数据文件", required = true) @RequestParam(value = "file") MultipartFile file,
            @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        String msg = courseDataService.importData(orgId, file, userId);
        Map<String, String> map = new HashMap<>();
        map.put("msg", msg);
        if (StringUtils.isEmpty(msg)) {
            map.put("result", "success");
        } else {
            map.put("result", "fail");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/coursedatamsg", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查看开课信息Excel导入进度及结果", response = Void.class, notes = "查看开课信息Excel导入进度及结果<br><br><b>@author hsh</b>")
    public ImportCourseData getCourseDataMsg(
            @ApiParam(value = "orgId 组织ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "接口调用用户ID", required = true) @RequestParam("userId") Long userId) {
        return courseDataService.getImportMsg(orgId, userId);
    }
}
