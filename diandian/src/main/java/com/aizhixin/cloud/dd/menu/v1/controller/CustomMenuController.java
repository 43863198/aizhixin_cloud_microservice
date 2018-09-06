package com.aizhixin.cloud.dd.menu.v1.controller;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.menu.entity.CustomMenu;
import com.aizhixin.cloud.dd.menu.service.CustomMenuService;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api/web/v1/menus/custommenu")
@RestController
@Api(value = "自定义菜单相关API", description = "自定义菜单相关API")
public class CustomMenuController {

    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private CustomMenuService menuService;

    /**
     * 获取自定义菜单
     */
    @RequestMapping(value = "/getCustomMenu", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取自定义菜单", response = Void.class, notes = "获取自定义菜单<br>@author hsh")
    public ResponseEntity<?> getCustomMenu(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(menuService.getByUserId(account.getId()), HttpStatus.OK);
    }

    /**
     * 保存自定义菜单
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "保存自定义菜单", response = Void.class, notes = "保存自定义菜单<br>@author hsh")
    public ResponseEntity<?> saveCustomMenu(@RequestHeader("Authorization") String accessToken,
                                            @ApiParam(value = "菜单", required = true) @RequestParam(value = "menus", required = true) String menus) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        CustomMenu menu = new CustomMenu();
        menu.setUserId(account.getId());
        menu.setMenus(menus);
        
        Map<String, Object> result = new HashMap<>();
        try {
            menuService.save(menu);
            result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        } catch (Exception ex) {
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

}
