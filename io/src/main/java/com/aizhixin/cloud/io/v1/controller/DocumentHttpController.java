package com.aizhixin.cloud.io.v1.controller;

import com.aizhixin.cloud.io.domain.LocalFileDomain;
import com.aizhixin.cloud.io.service.v2.LocalDocumentV2Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 文档对象下载管理
 * Created by zhen.pan on 2017/6/13.
 */
@Controller
@RequestMapping("/v1/doc")
@Api("Token管理API")
public class DocumentHttpController {
    private LocalDocumentV2Service localDocumentV2Service;
    @Autowired
    public DocumentHttpController(LocalDocumentV2Service localDocumentV2Service) {
        this.localDocumentV2Service = localDocumentV2Service;
    }
    @RequestMapping(value = "/download", method = RequestMethod.GET, produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(httpMethod = "GET", value = "下载文档", response = Void.class, notes = "下载文档<br>@author panzhen@aizhixin.com")
    public ResponseEntity<byte[]> download(@ApiParam(value = "key 文档对象key", required = true) @RequestParam(value = "key") String key,
                                           @ApiParam(value = "appId 应用ID", required = true) @RequestParam(value = "appId") String appId,
                                           @ApiParam(value = "token 应用token", required = true) @RequestParam(value = "token") String token) {
        return localDocumentV2Service.download(key, appId, token);
    }

    @RequestMapping(value = "/redis2db", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(httpMethod = "GET", value = "Redis数据转出到数据库", response = Void.class, notes = "Redis数据转出到数据库<br>@author panzhen@aizhixin.com")
    public int doRedis2DB(@ApiParam(value = "token 应用token", required = true) @RequestParam(value = "token") String token) {
        return localDocumentV2Service.loadFromRedisToDB();
    }

    @RequestMapping(value = "/addexistfile", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(httpMethod = "POST", value = "补录文件信息，文件必须依据存在", response = Void.class, notes = "补录文件信息，文件必须依据存在<br>@author panzhen@aizhixin.com")
    public String addExistFile(@ApiParam(value = "<b>必填:key、filePath、originalFileName</br>") @RequestBody LocalFileDomain localFileDomain) {
        return localDocumentV2Service.addExistFile(localFileDomain);
    }
}
