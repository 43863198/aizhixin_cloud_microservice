package com.aizhixin.cloud.io.v1.controller;

import com.aizhixin.cloud.io.domain.LocalFileDomain;
import com.aizhixin.cloud.io.service.v2.LocalDocumentV2Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文档对象管理
 * Created by zhen.pan on 2017/6/10.
 */
@RestController
@RequestMapping("/v1/doc")
@Api("Token管理API")
public class LocalDocumentController {
    private LocalDocumentV2Service localDocumentV2Service;

    @Autowired
    public LocalDocumentController (LocalDocumentV2Service localDocumentService) {
        this.localDocumentV2Service = localDocumentService;
    }
    @RequestMapping(value = "/upload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "上传一个本地文档", response = Void.class, notes = "上传一个本地文档<br>@author panzhen@aizhixin.com")
    public LocalFileDomain upload(@ApiParam(value = "file HTTP上传的文档对象文件", required = true) @RequestParam(value = "file") MultipartFile file,
                                  @ApiParam(value = "bucket 应用存储桶，不填写则使用缺省值") @RequestParam(value = "bucket", required = false) String bucket,
                                  @ApiParam(value = "ttl 文档有效时长（单位秒），缺省不过期") @RequestParam(value = "ttl", required = false) Long ttl,
                                  @ApiParam(value = "filename 文件名称，如果不传，取实际文件名称") @RequestParam(value = "filename", required = false) String filename,
                                  @ApiParam(value = "appId 应用ID", required = true) @RequestParam(value = "appId") String appId,
                                  @ApiParam(value = "token 应用token", required = true) @RequestParam(value = "token") String token) {
        return localDocumentV2Service.uploadDocument(file, filename, bucket, ttl, appId, token);
    }

    
    @RequestMapping(value = "/info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取文档的基本信息", response = Void.class, notes = "获取文档的基本信息<br>@author panzhen@aizhixin.com")
    public LocalFileDomain info(@ApiParam(value = "key 文档对象key", required = true) @RequestParam(value = "key") String key,
                                  @ApiParam(value = "appId 应用ID", required = true) @RequestParam(value = "appId") String appId,
                                  @ApiParam(value = "token 应用token", required = true) @RequestParam(value = "token") String token) {
        return localDocumentV2Service.getDocumentInfo(key, appId, token);
    }
}
