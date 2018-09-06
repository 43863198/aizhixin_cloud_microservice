package com.aizhixin.cloud.orgmanager.common.v1.controller;

import com.aizhixin.cloud.orgmanager.common.domain.StringAndLongSetDomain;
import com.aizhixin.cloud.orgmanager.common.domain.SystemTimeDomain;
import com.aizhixin.cloud.orgmanager.common.service.SemsterAndWeekBackService;
import com.aizhixin.cloud.orgmanager.common.util.DateUtil;
import com.aizhixin.cloud.orgmanager.company.service.BaseDataCacheService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Set;


/**
 * Created by zhen.pan on 2017/7/6.
 */
@RestController
@RequestMapping("/v1/system")
@Api(description = "后台系统API")
public class BackSystemController {

    @Autowired
    private BaseDataCacheService baseDataCacheService;

    @Autowired
    private SemsterAndWeekBackService semsterAndWeekBackService;

    @RequestMapping(value = "/time", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取系统当前时间", response = Void.class, notes = "获取系统当前时间<br><br><b>@author zhen.pan</b>")
    public SystemTimeDomain get(@ApiParam(value = "dateFormat 日期格式化字符串(yyyyMMddHHmmss)，如果为空，则返回数字格式的时间值") @RequestParam(value = "dateFormat", required = false) String dateFormat) {
        SystemTimeDomain d = new SystemTimeDomain();
        if (StringUtils.isEmpty(dateFormat)) {
            d.setTime("" + System.currentTimeMillis());
        } else {
            d.setTime(DateUtil.getCurrentTime(dateFormat));
        }
        return d;
    }

    @RequestMapping(value = "/manual/cache/org", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "缓存指定的或者所有的组织结构信息", response = Void.class, notes = "缓存指定的或者所有的组织结构信息<br><br><b>@author zhen.pan</b>")
    public void cacheOrg(@ApiParam(value = "学校ID列表，如果不填写则缓存所有的学校信息") @RequestBody(required =  false) Set<Long> orgIds) {
        baseDataCacheService.cacheAllOrg(orgIds);
    }

    @RequestMapping(value = "/manual/clear/org", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "清空缓存中的所有的组织结构信息", response = Void.class, notes = "清空缓存中的所有的组织结构信息<br><br><b>@author zhen.pan</b>")
    public void clearOrg() {
        baseDataCacheService.clearAllOrg();
    }

    @RequestMapping(value = "/manual/clear/college", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "清空缓存中所有的学院信息", response = Void.class, notes = "清空缓存中所有的学院信息<br><br><b>@author zhen.pan</b>")
    public void clearCollege() {
        baseDataCacheService.clearAllCollege();
    }

    @RequestMapping(value = "/manual/clear/professional", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "清空缓存中所有的专业信息", response = Void.class, notes = "清空缓存中所有的专业信息<br><br><b>@author zhen.pan</b>")
    public void clearProfessional() {
        baseDataCacheService.clearAllProfessional();
    }

    @RequestMapping(value = "/manual/clear/classes", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "清空缓存中所有的班级信息", response = Void.class, notes = "清空缓存中所有的班级信息<br><br><b>@author zhen.pan</b>")
    public void clearClasses() {
        baseDataCacheService.clearAllClasses();
    }

    @RequestMapping(value = "/manual/clear/users", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "清空缓存中所有的用户信息", response = Void.class, notes = "清空缓存中所有的用户信息<br><br><b>@author zhen.pan</b>")
    public void clearUsers() {
        baseDataCacheService.clearAllUser();
    }

    @RequestMapping(value = "/manual/cache/users", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "手动缓存所有的学生老师信息", response = Void.class, notes = "手动缓存所有的学生老师信息<br><br><b>@author zhen.pan</b>")
    public void cacheClasses(@ApiParam(value = "学校ID列表") @RequestBody Set<Long> orgIds) {
        baseDataCacheService.cacheAllOrgUser(orgIds);
    }

    @RequestMapping(value = "/manual/int/semesters", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "手动初始化一批学校的学期和第一学周数据", response = Void.class, notes = "手动初始化一批学校的学期和第一学周数据<br><br><b>@author zhen.pan</b>")
    public StringAndLongSetDomain initSemestersAndWeeks(@ApiParam(value = "学校ID列表(不填写就是所有学校)") @RequestParam(value = "orgIds", required = false) Set<Long> orgIds,
                                                        @ApiParam(value = "学期名称", required = true) @RequestParam(value = "semsterName") String semsterName,
                                                        @ApiParam(value = "学期编码(yyyy-MM-1或者yyyy-MM-2)", required = true) @RequestParam(value = "semsterCode") String semsterCode,
                                                        @ApiParam(value = "学期开始日期(yyyy-MM-dd)", required = true) @RequestParam(value = "semsterStartDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date semsterStartDate,
                                                        @ApiParam(value = "学期结束日期(yyyy-MM-dd)", required = true) @RequestParam(value = "semsterEndDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date semsterEndDate,
                                                        @ApiParam(value = "第一周开始日期(yyyy-MM-dd)", required = true) @RequestParam(value = "firstWeekStartDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date firstWeekStartDate,
                                                        @ApiParam(value = "第一周结束日期(yyyy-MM-dd)", required = true) @RequestParam(value = "firstWeekEndDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date firstWeekEndDate
                                      ) {
        return semsterAndWeekBackService.batchAddSemsterAndWeek(orgIds, semsterName, semsterCode, semsterStartDate, semsterEndDate, firstWeekStartDate, firstWeekEndDate);
    }
}
