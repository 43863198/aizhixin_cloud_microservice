package com.aizhixin.cloud.dd.homepage.v1.controller;

import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.homepage.dto.HomePageMenuDTO;
import com.aizhixin.cloud.dd.homepage.service.HomePagePhoneService;
import com.aizhixin.cloud.dd.homepage.service.HomePageService;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.PageInfo;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import com.aizhixin.cloud.dd.rollcall.utils.HomePageUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/phone/v1/homepage")
public class HomePagePhoneController {
    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private HomePageService homePageService;
    @Autowired
    private HomePagePhoneService homePagePhoneService;

    /**
     * 教师获取首页数据
     */
    @RequestMapping(value = "/teacher/getHomeData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "教师获取首页数据", response = Void.class, notes = "教师获取首页数据<br>@author hsh")
    public ResponseEntity<?> getTeacherHomeData(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();
        //按钮
        List<HomePageMenuDTO> menuList = homePageService.list(HomePageUtil.TEACHER, HomePageUtil.TEACHER, HomePageUtil.TYPE_MENU, account.getOrganId() + HomePageUtil.TEACHER + HomePageUtil.TEACHER + "_" + HomePageUtil.TYPE_MENU + "V2", "V2",
                account.getId(), account.getOrganId());
        result.put("menu", menuList);
        //今日课程
        List todayCourse = homePagePhoneService.getTodayCourse(account);
        result.put("todayCourse", todayCourse);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * 教师获取首页菜单
     */
    @RequestMapping(value = "/teacher/getMenu", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "教师获取首页菜单", response = Void.class, notes = "教师获取首页菜单<br>@author hsh")
    public ResponseEntity<?> getMenu(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List<HomePageMenuDTO> menuList = homePageService.list(HomePageUtil.TEACHER, HomePageUtil.TEACHER, HomePageUtil.TYPE_MENU, account.getOrganId() + HomePageUtil.TEACHER + HomePageUtil.TEACHER + "_" + HomePageUtil.TYPE_MENU + "V2", "V2",
                account.getId(), account.getOrganId());
        return new ResponseEntity(menuList, HttpStatus.OK);
    }

    /**
     * 教师获取今日课程
     */
    @RequestMapping(value = "/teacher/getTodayCourse", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "教师获取今日课程", response = Void.class, notes = "教师获取今日课程<br>@author hsh")
    public ResponseEntity<?> getTodayCourse(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List courseList = homePagePhoneService.getTodayCourse(account);
        return new ResponseEntity(courseList, HttpStatus.OK);
    }

    /**
     * 教师获取今日考勤累计
     */
    @RequestMapping(value = "/teacher/getTodayRollCall", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "教师获取今日考勤累计", response = Void.class, notes = "教师获取今日考勤累计<br>@author hsh")
    public ResponseEntity<?> getTodayRollCall(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(homePagePhoneService.getTodayRollCall(account), HttpStatus.OK);
    }

    /**
     * 辅导员获取首页数据
     */
    @RequestMapping(value = "/counselor/getHomeData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "辅导员获取首页数据", response = Void.class, notes = "辅导员获取首页数据<br>@author hsh")
    public ResponseEntity<?> getCounselorHomeData(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();
        //今日课程考勤累计 行政班
        result.put("todayRollCall", homePagePhoneService.getCounselorTodayRollCall(account));

        //按钮
        List<HomePageMenuDTO> menuList = homePageService.list(HomePageUtil.TEACHER, HomePageUtil.CLASSROOMTEACHER, HomePageUtil.TYPE_MENU, account.getOrganId() + HomePageUtil.TEACHER + HomePageUtil.CLASSROOMTEACHER + "_" + HomePageUtil.TYPE_MENU + "V2", "V2",
                account.getId(), account.getOrganId());
        result.put("menu", menuList);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * 辅导员取今日考勤累计
     */
    @RequestMapping(value = "/counselor/getTodayRollCall", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "辅导员取今日考勤累计", response = Void.class, notes = "辅导员取今日考勤累计<br>@author hsh")
    public ResponseEntity<?> getCounselorTodayRollCall(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(homePagePhoneService.getCounselorTodayRollCall(account), HttpStatus.OK);
    }

    /**
     * 学生获取今日排课
     */
    @RequestMapping(value = "/student/getTodaySchedule", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生获取今日排课", response = Void.class, notes = "学生获取今日排课<br>@author hsh")
    public ResponseEntity<?> getTodaySchedule(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(homePagePhoneService.getTodaySchedule(account), HttpStatus.OK);
    }

}
