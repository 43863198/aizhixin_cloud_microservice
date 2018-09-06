package com.aizhixin.cloud.dd.rollcall.v1.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.aizhixin.cloud.dd.rollcall.serviceV2.StuTeachClassService;

/**
 * Created by zhen.pan on 2017/5/8.
 */
@RestController
@RequestMapping("/api/web/v1/test")
@Api(description = "测试API")
public class TestController {


    @Autowired
    private StuTeachClassService stuTeachClassService;

    @RequestMapping(value = "/test", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "将当天学生对应的教学班id字符串写入redis", response = Void.class, notes = "将当天学生对应的教学班id字符串写入redis<br><br><b>@author zhengning</b>")
    public ResponseEntity <String> test() {
        
    	stuTeachClassService.saveStuTeachClassIds();
        return new ResponseEntity <>("", HttpStatus.OK);
    }

}
