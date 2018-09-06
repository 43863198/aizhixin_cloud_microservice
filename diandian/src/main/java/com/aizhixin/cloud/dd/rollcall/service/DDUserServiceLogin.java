package com.aizhixin.cloud.dd.rollcall.service;

import com.aizhixin.cloud.dd.common.utils.ConfigCache;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.common.utils.http.HttpResponse;
import com.aizhixin.cloud.dd.common.utils.http.OauthGet;
import com.aizhixin.cloud.dd.constant.RoleConstants;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.entity.OrganSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by LIMH on 2017/10/14.
 */
@Service
public class DDUserServiceLogin {

    private final Logger log = LoggerFactory.getLogger(DDUserServiceLogin.class);

    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;

    @Autowired
    private ConfigCache configCache;

    @Autowired
    private UserService userService;

    @Autowired
    private OrganSetService organSetService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Cacheable(value = "CACHE.AUTHORIZATION")
    public AccountDTO getUserInfoWithLogin(String authorization) {
        return getAccountDTO(authorization);
    }

    @CachePut(value = "CACHE.AUTHORIZATION")
    public AccountDTO getUserInfoWithLoginUpdate(String authorization) {
        return getAccountDTO(authorization);
    }

    public AccountDTO getAccountDTO(String authorization) {
        AccountDTO account = null;
        try {
            JSONObject obj = getZXUserByAuthorizationCurrentUser(authorization);
            account = new AccountDTO();
            if (obj.has("id")) {
                account.setId(obj.getLong("id"));
            } else {
                log.warn("token Unauthorized");
                return null;
            }

            if (obj.has("userName")) {
                account.setName(obj.getString("userName"));
            }
            if (obj.has("login")) {
                account.setLogin(obj.getString("login"));
            }
            if (obj.has("phoneNumber")) {
                account.setPhoneNumber(obj.getString("phoneNumber"));
            }
            if (obj.has("email")) {
                account.setEmail(obj.getString("email"));
            }
            if (obj.has("roleGroup")) {
                account.setGroupType(obj.getString("roleGroup")); // 用户属于B or C
            }
            if (obj.has("avatar")) {
                account.setAvatar(obj.getString("avatar"));
            }
            try {
                if (obj.has("roles")) {
                    Object tem = obj.get("roles");
                    if (tem != null && !"null".equals(String.valueOf(tem))) {
                        JSONArray roles = obj.getJSONArray("roles");
                        account.setRole(String.valueOf(roles.get(0)));
                    }
                }
            } catch (Exception e) {
                log.warn("获取用户角色异常", e.getMessage());
            }
            fillingAccount(account);

        } catch (Exception e) {
            account = null;
            e.printStackTrace();
            log.warn("token认证异常,token:" + authorization, e);
        }
        if (redisTemplate.opsForValue().setIfAbsent(authorization, account)) {
            redisTemplate.expire(authorization, 1, TimeUnit.HOURS);
        }

        TokenUtil.accessToken = authorization;

        return account;
    }

    // 从orgManager中查询基础信息
    public void fillingAccount(AccountDTO accountDto) {
        if (accountDto == null) {
            return;
        }

        // 企业用户
        if (RoleConstants.GROUP_ENTERPRISE.equals(accountDto.getGroupType())) {
            fillingEn(accountDto);
        } else {
            String userInfo = orgManagerRemoteService.getUserInfo(accountDto.getId());
            if (userInfo == null) {
                return;
            }
            JSONObject json = JSONObject.fromObject(userInfo);
            log.debug("获取用户信息:" + json.toString());

            String str = json.getString("teachingYear");
            log.debug("获取teachingYear------------------>" + str);
            System.out.println("获取teachingYear------------------>" + str);
            accountDto.setTeachingYear((StringUtils.isBlank(str) || "null".equals(str)) ? "" : str);

            accountDto.setOrganId("null".equals(json.getString("orgId")) ? 0L : json.getLong("orgId"));
            accountDto.setPersonId(json.getString("jobNumber"));
            accountDto.setName(json.getString("name"));
            accountDto.setSex("null".equals(json.getString("sex")) ? null : json.getString("sex"));
            accountDto.setProfessionalId("null".equals(json.getString("professionalId")) ? 0L : json.getLong("professionalId"));
            accountDto.setProfessionalName(json.getString("professionalName"));
            accountDto.setCollegeName(json.getString("collegeName"));
            accountDto.setCollegeId("null".equals(json.getString("collegeId")) ? 0L : json.getLong("collegeId"));
            accountDto.setIdNumber("null".equals(json.getString("idNumber")) ? null : json.getString("idNumber"));
            List roleList = new ArrayList();
            try {
                if (StringUtils.isBlank(accountDto.getRole())) {
                    StringBuffer roles = new StringBuffer();
                    if (json.has("roles")) {
                        JSONArray roleJson = json.getJSONArray("roles");
                        if (null != roleJson && roleJson.length() > 0) {
                            for (int i = 0, len = roleJson.length(); i < len; i++) {
                                roles.append(roleJson.getString(i));
                                if (i != len - 1) {
                                    roles.append(",");
                                }
                            }
                        }
                        // if (userService.isHeaderTeacher(accountDto.getId())) {
                        // roles.append(",");
                        // roles.append(RoleConstants.ROLE_CLASSROOMTEACHE);
                        // }
                    }
                    accountDto.setRole(roles.toString());
                }
            } catch (Exception e) {
                log.warn("", e);
            }
        }

        try {
            OrganSet organSet = organSetService.findByOrganId(accountDto.getOrganId());
            accountDto.setAntiCheating(organSet == null ? Boolean.TRUE : organSet.getAnti_cheating());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private JSONObject getZXUserByAuthorizationCurrentUser(String authorization) {
        OauthGet get = new OauthGet();
        HttpResponse response = null;
        try {
            response = get.get(configCache.getConfigValueByParm("user.service.host") + "/api/currentuser", "", authorization);
            System.out.println(configCache.getConfigValueByParm("user.service.host") + "/api/currentuser");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String s = response.getResponseBody();
        JSONObject obj = JSONObject.fromString(s);
        return obj;
    }

    public void fillingEn(AccountDTO account) {
        String json = orgManagerRemoteService.getEntorstrainingUserInfoByAccountId(account.getId());
        if (StringUtils.isNotBlank(json) && !"[]".equals(json)) {
            JSONArray jsonArray = JSONArray.fromObject(json);
            if (jsonArray.length() > 0) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                if (jsonObject.has("enterpriseName")) {
                    account.setEnterpriseName(jsonObject.getString("enterpriseName"));
                    account.setName(jsonObject.getString("name"));
                    account.setPhoneNumber(jsonObject.getString("phone"));
                    account.setEmail(jsonObject.getString("mailbox"));
                }
            }
        }
    }
}
