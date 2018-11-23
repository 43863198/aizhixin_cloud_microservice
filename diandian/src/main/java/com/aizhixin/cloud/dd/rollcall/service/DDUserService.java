package com.aizhixin.cloud.dd.rollcall.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.aizhixin.cloud.dd.common.provider.store.redis.RedisTokenStore;
import com.aizhixin.cloud.dd.orgStructure.domain.UserInfoDomain;
import com.aizhixin.cloud.dd.orgStructure.entity.UserInfo;
import com.aizhixin.cloud.dd.orgStructure.repository.UserInfoRepository;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.aizhixin.cloud.dd.common.utils.ConfigCache;
import com.aizhixin.cloud.dd.common.utils.http.HttpResponse;
import com.aizhixin.cloud.dd.common.utils.http.OauthGet;
import com.aizhixin.cloud.dd.constant.RoleConstants;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.IODTO;
import com.aizhixin.cloud.dd.rollcall.entity.OrganSet;
import com.aizhixin.cloud.dd.rollcall.utils.IOUtil;
import com.aizhixin.cloud.dd.rollcall.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonParseException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
//@Transactional
public class DDUserService {

    private final Logger log = LoggerFactory.getLogger(DDUserService.class);

    private static String APPLICATION_JSON = "application/json;charset=utf-8";

    private static String APPLICATION_FORMURL = "application/x-www-form-urlencoded";
    private static String SECRET = "dleduApp" + ':' + "mySecretOAuthSecret";

    private HttpClient client = new HttpClient();

    @Autowired
    private IOUtil ioUtil = new IOUtil();

    @Autowired
    private ConfigCache configCache;


    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;

    @Autowired
    private OrganSetService organSetService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private RedisTokenStore redisTokenStore;

    @Autowired
    DDUserServiceLogin ddUserServiceLogin;

    /**
     * 根据authorization 获取用户信息
     *
     * @param authorization
     * @return
     */
    public AccountDTO getUserInfoWithLogin(String authorization) {
        AccountDTO accountDTO = ddUserServiceLogin.getUserInfoWithLogin(authorization);
        if (null == accountDTO) {
            log.info("缓存失效...，从二级缓存获取...");
            accountDTO = (AccountDTO) redisTemplate.opsForValue().get(authorization);
            if (null == accountDTO) {
                log.info("缓存失效...，从三级级缓存获取...");
                accountDTO = ddUserServiceLogin.getUserInfoWithLoginUpdate(authorization);
            }
        }
        return accountDTO;
    }


    /**
     * 根据authorization 获取用户信息
     *
     * @param authorization
     * @return
     */
    @Cacheable(value = "CACHE.AUTHORIZATION")//(获取照片不添加缓存)
    public AccountDTO getUserInfoWithLoginBak(String authorization) {


        AccountDTO account = null;
        try {
            JSONObject obj = getZXUserByAuthorizationNew(authorization);
            account = new AccountDTO();
            if (obj.has("account_id")) {
                account.setId(obj.getLong("account_id"));
            } else {
                log.warn("token Unauthorized");
                return null;
            }

            if (obj.has("name")) {
                account.setName(obj.getString("name"));
            }
            if (obj.has("login")) {
                account.setLogin(obj.getString("login"));

            }
            if (obj.has("phone")) {
                account.setPhoneNumber(obj.getString("phone"));

            }
            if (obj.has("mail")) {
                account.setEmail(obj.getString("mail"));
            }

            if (obj.has("orgId")) {
                account.setOrganId(obj.getLong("orgId"));
            }
            if (obj.has("orgName")) {
                account.setOrganName(obj.getString("orgName"));
            }
            if (obj.has("roleGroup")) {
                account.setGroupType(obj.getString("roleGroup")); // 用户属于B or C
            }

            if (obj.has("classId")) {
                account.setClassesId(obj.getLong("classId"));
            }
            if (obj.has("className")) {
                account.setClassesName(obj.getString("className"));
            }

            if (obj.has("collegeId")) {
                account.setCollegeId(obj.getLong("collegeId"));
            }

            if (obj.has("collegeName")) {
                account.setCollegeName(obj.getString("collegeName"));
            }

            String role = "";
            if (obj.has("roleNames")) {
                JSONArray roles = obj.getJSONArray("roleNames");
                if (null != roles && roles.length() > 0) {
                    role = String.valueOf(roles.get(0));
                    account.setRole(role);
                }
            }

            ddUserServiceLogin.fillingAccount(account);

            if (role.equals(RoleConstants.ROLE_ENTERPRISE_TEACHER)) {
                fillingEn(account);
            } else if (role.equals(RoleConstants.ROLE_TEACHER) && userService.isHeaderTeacher(account.getId())) {
                role = RoleConstants.ROLE_CLASSROOMTEACHE;
                account.setRole(role);
            }

            if (obj.has("orgName")) {
                account.setShortName(obj.getString("orgName"));
            }
            if (obj.has("workNo")) {
                account.setPersonId(obj.getString("workNo"));
            }
            if (obj.has("userGroup")) {
                account.setGroupType(obj.getString("userGroup")); // 用户属于B or C
            }
            if (obj.has("avatar")) {
                account.setAvatar(obj.getString("avatar"));
            }


            try {
                OrganSet organSet = organSetService.findByOrganId(account.getOrganId());
                account.setAntiCheating(organSet == null ? Boolean.TRUE : organSet.getAnti_cheating());
            } catch (Exception e) {
                log.warn("Exception", e);
            }
        } catch (Exception e) {
            account = null;
            log.warn("Exception", e);
            log.warn("token认证异常,token:" + authorization, e);
        }

        return account;
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

    private JSONObject getZXUserByAuthorization(String authorization) {
        OauthGet get = new OauthGet();
        HttpResponse response = null;
        try {
            response = get.get(configCache.getConfigValueByParm("user.service.host") + configCache.getConfigValueByParm("user.service.getInfo"), "", authorization);
            System.out.println(configCache.getConfigValueByParm("user.service.host") + configCache.getConfigValueByParm("user.service.getInfo"));
        } catch (IOException e) {
            log.warn("Exception", e);
        }
        String s = response.getResponseBody();
        JSONObject obj = JSONObject.fromString(s);
        return obj;
    }

    private JSONObject getZXUserByAuthorizationNew(String authorization) {
        OauthGet get = new OauthGet();
        HttpResponse response = null;
        try {
            response = get.get(configCache.getConfigValueByParm("user.service.host") + configCache.getConfigValueByParm("user.service.getInfoNew"), "", authorization);
            System.out.println(configCache.getConfigValueByParm("user.service.host") + configCache.getConfigValueByParm("user.service.getInfoNew"));
        } catch (IOException e) {
            log.warn("Exception", e);
        }
        String s = response.getResponseBody();
        JSONObject obj = JSONObject.fromString(s);
        return obj;
    }


    public boolean updateAvatar(Long userId, String accessToken, String fileName, byte[] avatarData) {
        if (accessToken == null) {
            return false;
        }
        String apiURL = configCache.getConfigValueByParm("user.service.host") + configCache.getConfigValueByParm("user.service.avatar");
        // 存储文件
        IODTO ioDTO = ioUtil.upload(fileName, avatarData);
        // 获取文件MD5标示
        String fileId = ioDTO.getFileMD5code();
        final PutMethod put = new PutMethod(apiURL + "?fileId=" + fileId);
        put.setRequestHeader("Authorization", accessToken);
        try {
            client.executeMethod(put);
            switch (put.getStatusCode()) {
                case 200:
                case 201:
                    log.info("update avatar success.");
                    redisTemplate.opsForValue().set("avatarFile:" + userId, ioDTO.getFileUrl(), 1, TimeUnit.HOURS);
                    //update cache
                    UserInfo userInfo = userInfoRepository.findByUserId(userId);
                    if (userInfo != null) {
                        userInfo.setAvatar(ioDTO.getFileUrl());
                        userInfoRepository.save(userInfo);
                    }
                    UserInfoDomain d = redisTokenStore.getUserInfoDomain(userId);
                    if (d != null) {
                        d.setAvatar(ioDTO.getFileUrl());
                        redisTokenStore.setUserInfoDomain(d);
                    }
                    return true;
                default:
                    log.warn("Invalid response code (" + put.getStatusCode()
                            + ") from User service!");
                    break;
            }
        } catch (Exception e) {
            log.warn("Exception", e);
        } finally {
            put.releaseConnection();
        }
        return false;
    }


    public JSONObject getUserinfoByIds(List<Long> ids) {
        OauthGet get = new OauthGet();
        HttpResponse response = null;
        String idsStr = "";
        for (Long l : ids) {
            if (StringUtils.isNotBlank(idsStr)) {
                idsStr += "," + l;
            } else {
                idsStr += l;
            }
        }
        try {
            response = get.get(configCache.getConfigValueByParm("user.service.host"),
                    configCache.getConfigValueByParm("user.service.avatarList") + "?ids=" + idsStr, null);
            String reg = ".*error.*";
            if (response.getResponseBody().matches(reg)) {
                response = null;
            }
        } catch (IOException e) {
        }
        String phone = null;
        HashMap listMap = null;
        JSONObject result = null;
        if (null != response) {
            listMap = new HashMap();
            String s = response.getResponseBody();
            JSONObject user = JSONObject.fromString(s);
            Iterator<String> iterator = user.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = user.getString(key);
                JSONObject user_json = JSONObject.fromString(value);
                result = user_json;
                break;
            }
        }

        return result;
    }


    public Map<Long, AccountDTO> getUserinfoByIdsV2(List<Long> ids) {
        OauthGet get = new OauthGet();
        HttpResponse response = null;
        String idsStr = "";
        for (Long l : ids) {
            if (StringUtils.isNotBlank(idsStr)) {
                idsStr += "," + l;
            } else {
                idsStr += l;
            }
        }
        try {
            response = get.get(configCache.getConfigValueByParm("user.service.host"),
                    configCache.getConfigValueByParm("user.service.avatarList") + "?ids=" + idsStr, null);
            String reg = ".*error.*";
            if (response.getResponseBody().matches(reg)) {
                response = null;
            }
        } catch (IOException e) {
        }
        HashMap<Long, AccountDTO> listMap = null;
        JSONObject result = null;
        if (null != response) {
            listMap = new HashMap();
            String s = response.getResponseBody();
            JSONObject user = JSONObject.fromString(s);
            Iterator<String> iterator = user.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = user.getString(key);
                Map<String, Object> data = null;
                AccountDTO adt = new AccountDTO();
                try {
                    data = JsonUtil.Json2Object(value);
                    if (null != data.get("id") && null != data) {
                        adt.setId(Long.valueOf(data.get("id").toString()));
                    }

                    if (null != data.get("phoneNumber")) {
                        adt.setPhoneNumber(data.get("phoneNumber").toString());
                    }
                    if (null != data.get("avatar")) {
                        adt.setAvatar(data.get("avatar").toString());
                    }
                    if (null != data.get("userName")) {
                        adt.setName(data.get("userName").toString());
                    }
                } catch (Exception e) {
                    log.warn("Exception", e);
                }
                listMap.put(Long.valueOf(key), adt);
            }
        }

        return listMap;
    }
}
