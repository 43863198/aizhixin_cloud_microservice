package com.aizhixin.cloud.dd.homepage.v1.controller;

import com.aizhixin.cloud.dd.homepage.domain.HomePageDomain;
import com.aizhixin.cloud.dd.homepage.service.HomePageService;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.utils.HomePageUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LIMH
 * @date 2018/1/9
 */
@RestController
@RequestMapping("/api/web/v1/homepage")
public class HomePageController {

    @Autowired
    private HomePageService homePageService;

    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;

    @RequestMapping(value = "/listRole", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "角色", response = Void.class, notes = "角色<br>@author meihua.li")
    public ResponseEntity<?> listRole() {
        List list = new ArrayList();
        list.add(HomePageUtil.TEACHER);
        list.add(HomePageUtil.STUDENT);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "/listType", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "类型", response = Void.class, notes = "类型 <br>@author meihua.li")
    public ResponseEntity<?> listType() {
        List list = new ArrayList();
        list.add(HomePageUtil.TYPE_BANNER);
        list.add(HomePageUtil.TYPE_MENU);
        list.add(HomePageUtil.TYPE_AD);
        list.add(HomePageUtil.TYPE_ALERT);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "/query", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "", response = Void.class, notes = "<br>@author meihua.li")
    public ResponseEntity<?> query(@ApiParam(value = "teacher/student") @RequestParam(value = "role") String role,
                                   @ApiParam(value = "menu|banner|ad|alert") @RequestParam(value = "type") String type,
                                   @ApiParam(value = "orgId", required = false) @RequestParam(value = "orgId", required = false) Long orgId) {
        return new ResponseEntity<>(homePageService.query(role, type, "V2", orgId), HttpStatus.OK);
    }

    @RequestMapping(value = "/queryById", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "", response = Void.class, notes = "<br>@author meihua.li")
    public ResponseEntity<?> queryById(@ApiParam(value = "id") @RequestParam(value = "id") Long id) {
        return new ResponseEntity<>(homePageService.queryById(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "", response = Void.class, notes = "<br>@author meihua.li")
    public ResponseEntity<?> add(@RequestBody HomePageDomain homePageDomain) {
        return new ResponseEntity<>(homePageService.add(homePageDomain), HttpStatus.OK);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "", response = Void.class, notes = "<br>@author meihua.li")
    public ResponseEntity<?> update(@RequestBody HomePageDomain homePageDomain) {
        return new ResponseEntity<>(homePageService.update(homePageDomain), HttpStatus.OK);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "", response = Void.class, notes = "<br>@author meihua.li")
    public ResponseEntity<?> delete(@RequestParam(value = "homePageId") Long homePageId) {
        return new ResponseEntity<>(homePageService.delete(homePageId), HttpStatus.OK);
    }

    /**
     * 查询所有学校
     */
    @RequestMapping(value = "/listAllOrgInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询所有学校", response = Void.class, notes = "查询所有学校<br>@author meihua")
    public ResponseEntity<?> listOrgInfo(@RequestHeader(value = "Authorization", required = false) String accessToken) {

        return new ResponseEntity<Object>(orgManagerRemoteService.findAllOrg(), HttpStatus.OK);
    }

    @RequestMapping(value = "/sortOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "", response = Void.class, notes = "<br>@author meihua.li")
    public ResponseEntity<?> sortOrder(@ApiParam(value = "只需传递 id,order") @RequestBody List<Long> homePages) {
        return new ResponseEntity<>(homePageService.sort(homePages), HttpStatus.OK);
    }
}
