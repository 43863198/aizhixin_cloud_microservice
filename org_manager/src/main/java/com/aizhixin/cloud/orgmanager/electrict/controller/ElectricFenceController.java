package com.aizhixin.cloud.orgmanager.electrict.controller;

import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.electrict.domain.UseElectricFenceUserDaomin;
import com.aizhixin.cloud.orgmanager.electrict.domain.UserLocusLonlat;
import com.aizhixin.cloud.orgmanager.electrict.entity.ElectricFenceInfo;
import com.aizhixin.cloud.orgmanager.electrict.exception.DlEduException;
import com.aizhixin.cloud.orgmanager.electrict.service.*;
import com.aizhixin.cloud.orgmanager.electrict.dto.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 类名称：ElectricFenceResource 类描述：TODO 创建人：HUM 创建时间：2016年12月5日 上午10:16:55
 */
@RestController
@RequestMapping("/v1/electricFence")
@Api(value = "电子围栏API", description = "针对电子围栏API")
public class ElectricFenceController {
    @Autowired
    private ElectricFenceBaseService electricFenceBaseService;

    private RestTemplate restTemplate;

    /**
     * 电子围栏的设置
     * @param electricFenceInfoDTO
     * @return
     */
    @PostMapping(value = "/electricFenceCreate",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "电子围栏的设置", response = Void.class, notes = "电子围栏信息设置  <br>@author HUM")
    public Map create(
            @ApiParam(value = "electricFenceBase 电子围栏信息设置") @RequestBody ElectricFenceInfoDTO electricFenceInfoDTO) {
        return electricFenceBaseService.save(electricFenceInfoDTO);
    }

    /**
     * 电子围栏信息查看
     * @param organId
     * @return
     * @throws URISyntaxException
     * @throws DlEduException
     */
    @GetMapping(value = "/queryInit", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查看电子围栏信息", response = Void.class, notes = "查看电子围栏是否设置<br>@author HUM")
    public ResponseEntity<?> queryElectricFenceInit(
            @ApiParam(value = "学校id organId") @RequestParam(value = "organId", required = false) Long organId)
            throws URISyntaxException, DlEduException {
        ElectricFenceInfoDTO electricFenceInfoDTO = electricFenceBaseService.queryInit(organId);
        if(null == electricFenceInfoDTO){
            return new ResponseEntity<>("null", HttpStatus.OK);
        }
        return new ResponseEntity<>(electricFenceInfoDTO, HttpStatus.OK);
    }
    /**
     * 电子围栏学生状态信息查询
     *
     * @throws DlEduException
     */
    @GetMapping(value = "/queryelectricfence", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "电子围栏学生状态信息查询", response = Void.class, notes = "电子围栏信息查询<br>@author HUM")
    public Map<String,Object> queryElectricFence(
            @ApiParam(value = "organId 学校id") @RequestParam(value = "organId", required = false) Long organId,
            @ApiParam(value = "学院id") @RequestParam(value = "collegeId", required = false) Long collegeId,
            @ApiParam(value = "专业id") @RequestParam(value = "professionalId", required = false) Long professionalId,
            @ApiParam(value = "班级id") @RequestParam(value = "classId", required = false) Long classId,
            @ApiParam(value = "name 姓名") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "jobNumber 学号") @RequestParam(value = "jobNumber", required = false) String jobNumber,
            @ApiParam(value = "time 时间") @RequestParam(value = "time", required = true) Long time,
            @ApiParam(value = "是否曾离校（1：是；:0：否；2：未知）") @RequestParam(value = "isLeaveSchool", required = false) String isLeaveSchool,
            @ApiParam(value = "是否激活（1：是；:0：否）") @RequestParam(value = "isActivation", required = false) String isActivation,
            @ApiParam(value = "当前位置（1：在校；:0：离校）") @RequestParam(value = "isAtSchool", required = false) String isAtSchool,
            @ApiParam(value = "在线状态（1：在线；:0 离线 ）") @RequestParam(value = "isOline", required = false) String isOline,
            @ApiParam(value = "是否登录（1：登录；:0 未登录 ）") @RequestParam(value = "isLogin", required = false) String isLogin,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @ApiParam(value = "accessToken   Authorization") @RequestParam(value = "accessToken", required = true) String accessToken
    ) throws URISyntaxException, DlEduException {
        return  electricFenceBaseService.queryElectricFence(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), organId, collegeId, professionalId, classId, name, jobNumber, time, isLeaveSchool, isActivation, isAtSchool, isOline,isLogin, accessToken);
    }

    /**
     * 当天轨迹
     * @param organId
     * @param time
     * @param userId
     * @return
     * @throws URISyntaxException
     * @throws DlEduException
     */
    @GetMapping(value = "/querlocus", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "当天轨迹", response = Void.class, notes = "电子围栏信息查询<br>@author HUM")
    public UserLocusLonlat querLocus(
            @ApiParam(value = "organId 学校id") @RequestParam(value = "organId", required = true) Long organId,
            @ApiParam(value = "time 时间") @RequestParam(value = "time", required = true) Long time,
            @ApiParam(value = "userId 学生id") @RequestParam(value = "userId", required = true) Long userId
    ) throws URISyntaxException, DlEduException {
        return  electricFenceBaseService.querLocus(organId, time, userId);
    }

    /**
     * 历史轨迹
     *
     * @throws DlEduException
     */
    @GetMapping(value = "/queryHistory",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "历史轨迹", response = Void.class, notes = "电子围栏查询学生历史轨迹，指定日期<br>@author 段伟")
    public PageData<UseElectricFenceUserDaomin> queryhistorylocus(
            @ApiParam(value = "学生Id") @RequestParam(value = "userId", required = false) Long userId,
            @ApiParam(value = "学校id") @RequestParam(value = "organId", required = false) Long organId,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize
    ) throws URISyntaxException, DlEduException {
        return  electricFenceBaseService.queryhistorylocus(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), organId, userId);
    }

    /**
     * GPS信息保存
     * @param electricFenceBaseDTO
     * @return
     */
    @RequestMapping(value = "/saveGPS", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "GPS信息保存", response = Void.class, notes = "GPS信息保存  <br>@author HUM")
    public Map<String, Object> saveBase(
            @ApiParam(value = "electricFenceBase GPS信息") @RequestBody ElectricFenceBaseDTO electricFenceBaseDTO) {
        return electricFenceBaseService.saveBase(electricFenceBaseDTO);
    }

    /**
     * 电子围栏申报时隔查看
     * 2017-01-05
     *
     * @throws DlEduException
     */
    @RequestMapping(value = "/queryTimeInterval", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "电子围栏申报时隔查看", response = Void.class, notes = "电子围栏申报时隔查看<br>@author HUM")
    public Map<String, Object> queryTimeInterval(
            @ApiParam(value = "organId 学校id") @RequestParam(value = "organId", required = false) Long organId) {
        ElectricFenceInfo electricFenceInfo = electricFenceBaseService.queryTimeInterval(organId);
        Map<String, Object> result = new HashMap<>();
        if (electricFenceInfo == null) {
            result.put("message", "GPS时隔未设置");
            return  result;
        }
        result.put("timeInterval", electricFenceInfo.getNoticeTimeInterval());
        return result;
    }

    /**
     * 电子围栏的开启和关闭
     * @param organId
     * @param flag
     * @return
     */
    @PostMapping(value = "/fenceswitch", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "电子围栏的开启和关闭", response = Void.class, notes = "电子围栏的开启和关闭<br>@author HUM")
    public Map<String,Object> fenceSwitch(
            @ApiParam(value = "organId 学校id") @RequestParam(value = "organId", required = false) Long organId,
            @ApiParam(value = "flag 电子围栏开关（1:打开，0:关闭）") @RequestParam(value = "flag", required = true) String flag) {
        return electricFenceBaseService.fenceSwitch(organId,flag);
    }

    /****
     * 校验权限是否是管理员
     * @param organId
     * @param userId
     * @return
     */
    @RequestMapping(value = "/checkadmin", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "校验权限是否是管理员", response = Void.class, notes = "电子围栏申报时隔查看<br>@author HUM")
    public boolean checkRoleAdmin(
            @ApiParam(value = "organId 学校id") @RequestParam(value = "organId", required = false) Long organId,
            @ApiParam(value = "userId 用户id") @RequestParam(value = "userId", required = false) Long userId) {
        return electricFenceBaseService.checkRoleAdmin(organId, userId);
    }

    /**
     * 根据userId 查询地址
     * @param userId
     * @return
     */
    @RequestMapping(value = "/findaddressbyuserid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据userId 查询地址", response = Void.class, notes = "根据userId 查询地址<br>@author HUM")
    public List<String> findAddressByUserId(
            @ApiParam(value = "userId 用户id") @RequestParam(value = "userId", required = false) Long userId) {
        return electricFenceBaseService.findAddressByUserId(userId);
    }

    /**
     * 根据userId查询上报时间
     * @param userId
     * @return
     */
    @RequestMapping(value = "/findnoticetimetyuserid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据userId查询上报时间", response = Void.class, notes = "根据userId查询上报时间<br>@author HUM")
    public  List<Date> findNoticeTimeByUserId(
            @ApiParam(value = "userId 用户id") @RequestParam(value = "userId", required = false) Long userId) {
        return electricFenceBaseService.findNoticeTimeByUserId(userId);
    }

//    @PostConstruct
//    public void init() {
////        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
////        requestFactory.setReadTimeout(30000);
////        requestFactory.setConnectTimeout(10000);
//
//        // 添加转换器
//        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
//        messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
//        messageConverters.add(new FormHttpMessageConverter());
////        messageConverters.add(new MappingJackson2XmlHttpMessageConverter());
//        messageConverters.add(new MappingJackson2HttpMessageConverter());
//
//        restTemplate = new RestTemplate(messageConverters);
////        restTemplate.setRequestFactory(requestFactory);
//        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
//    }

}
