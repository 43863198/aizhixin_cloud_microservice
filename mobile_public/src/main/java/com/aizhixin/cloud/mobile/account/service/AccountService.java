package com.aizhixin.cloud.mobile.account.service;

import com.aizhixin.cloud.mobile.account.core.DeviceType;
import com.aizhixin.cloud.mobile.account.core.RoleGroup;
import com.aizhixin.cloud.mobile.account.domain.*;
import com.aizhixin.cloud.mobile.account.remote.OrgRemoteService;
import com.aizhixin.cloud.mobile.common.exception.CommonException;
import com.aizhixin.cloud.mobile.common.exception.NoAuthenticationException;
import com.aizhixin.cloud.mobile.common.exception.PublicErrorCode;
import com.aizhixin.cloud.mobile.common.service.AESNewCryptor;
import com.aizhixin.cloud.mobile.common.util.JsonUtil;
import com.aizhixin.cloud.mobile.common.util.RestUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户相关业务接口
 */
@Component
public class AccountService {
    private final static Logger LOG = LoggerFactory.getLogger(AccountService.class);
    private final static String DEFAUT_LOGIN_AUTH = "Basic " + Base64.getEncoder().encodeToString("dleduApp:mySecretOAuthSecret".getBytes());//Basic ZGxlZHVBcHA6bXlTZWNyZXRPQXV0aFNlY3JldA==
    @Value("${zhixin.api.url}")
    private String zhixinUrl;
    @Value("${dd_api.url}")
    private String ddUrl;
//    @Value("${security.key}")
//    private String encryptKey;
    @Autowired
    private RestUtil restUtil;
    @Autowired
    private OrgRemoteService orgRemoteService;
    @Autowired
    private AESNewCryptor aesNewCryptor;

    /**
     * OAuth2认证接口
     * @param username
     * @param pwd
     * @return
     */
    private OAuthToken oauth2 (String username, String pwd) {
        OAuthToken token = null;
        try {
            String planPwd = aesNewCryptor.decrypt(pwd);//解密密码
            LOG.info("OAUTH2:URL:({})\tHeader:({})\tUsername:({})\tpwd:({})\tmw:({})" , zhixinUrl + "/oauth/token" , DEFAUT_LOGIN_AUTH , username, planPwd, pwd);
            String tokenJson = restUtil.oauthLogin(DEFAUT_LOGIN_AUTH, zhixinUrl + "/oauth/token", username, planPwd);//oauth2认证
            if (!StringUtils.isEmpty(tokenJson)) {
                token = JsonUtil.decode(tokenJson, OAuthToken.class);
            }
        } catch (Exception e) {
            if (e instanceof HttpClientErrorException) {
                throw new CommonException(PublicErrorCode.AUTH_EXCEPTION.getIntValue(), "用户名密码错误");
            } else if (e instanceof ResourceAccessException) {
                LOG.warn("OAuth2 Authentication fail:{}", e);
                throw new CommonException(PublicErrorCode.AUTH_EXCEPTION.getIntValue(), "服务异常");
            } else {
                LOG.warn("OAuth2 Authentication fail:{}", e);
                throw new CommonException(PublicErrorCode.AUTH_EXCEPTION.getIntValue(), "未知异常");
            }
        }
        return token;
    }

    private Map<String, Object> getAntiCheatingAndAvatar(Long orgId, Long studentId) {
        Map<String, Object> r = new HashMap<>();
        try {
            String cheatingJson = restUtil.get(ddUrl + "/api/web/v1/user/avatarAndCheating?orgId=" + orgId + "&userId=" + studentId, null);
            ObjectMapper jsonObject = new ObjectMapper();
            JsonNode node = jsonObject.readTree(cheatingJson);
            JsonNode cheating = node.get("antiCheating");
            if (null != cheating) {
                r.put("cheating", cheating.booleanValue());
            }
            JsonNode avatar = node.get("antiCheating");
            if (null != avatar) {
                r.put("avatar", cheating.textValue());
            }
        } catch (Exception e) {
//            e.printStackTrace();
            LOG.warn("获取防止欺骗和用户头像:{}", e);
            throw new CommonException(PublicErrorCode.AUTH_EXCEPTION.getIntValue(), "服务异常");
        }
        return r;
    }

    /**
     * 调用知新的上传设备号接口
     * @param token
     * @param diviceTokenType
     * @param deviceToken
     */
    private void postDevicetoken(OAuthToken token, Integer diviceTokenType, String deviceToken) {
        StringBuilder dsb = new StringBuilder(zhixinUrl);
        dsb.append("/api/web/v1/users/postdevicetoken?");
        if (DeviceType.IOS_TEACHER.getState().intValue() == diviceTokenType) {
            dsb.append("iosTeacher=");
        } else if (DeviceType.IOS_STUDENT.getState().intValue() == diviceTokenType) {
            dsb.append("ios=");
        } else if (DeviceType.ANDROID_TEACHER.getState().intValue() == diviceTokenType) {
            dsb.append("androidTeacher=");
        } else if (DeviceType.ANDROID_STUDENT.getState().intValue() == diviceTokenType) {
            dsb.append("android=");
        } else {
            return;
        }
        dsb.append(deviceToken);
        try {
            restUtil.post(dsb.toString(), "Bearer " + token.getAccess_token());
        } catch (Exception e) {
            LOG.warn("Post device token exception:{}", e);
//            e.printStackTrace();
        }
    }

    /**
     * 调用知新的获取用户信息接口
     * @param token
     * @return
     */
    private UserBaseInfoDomain getCurrentUser(String token) {
        StringBuilder dsb = new StringBuilder(zhixinUrl);
        dsb.append("/api/currentuser");
        UserBaseInfoDomain userinfo = null;
        try {
            String json = restUtil.get(dsb.toString(), token);
            if (!StringUtils.isEmpty(json)) {
                userinfo = JsonUtil.decode(json, UserBaseInfoDomain.class);
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == HttpStatus.UNAUTHORIZED.value()) {
                throw new NoAuthenticationException();
            }
            throw new CommonException(PublicErrorCode.QUERY_EXCEPTION.getIntValue(), "服务端异常");
        } catch (Exception e) {
            throw new CommonException(PublicErrorCode.QUERY_EXCEPTION.getIntValue(), "服务端异常");
        }
        return userinfo;
    }

    /**
     * 知新用户信息
     * @param info
     * @param zxUserInfo
     */
    private void copyZXUserInfo(MobileUserInfo info, UserBaseInfoDomain zxUserInfo) {
        info.setId(zxUserInfo.getId());
        info.setName(zxUserInfo.getUserName());
        info.setLogin(zxUserInfo.getLogin());
        info.setAvatar(zxUserInfo.getAvatar());
        info.setPhoneNumber(zxUserInfo.getPhoneNumber());
        info.setEmail(zxUserInfo.getEmail());
        info.setGroupType(zxUserInfo.getRoleGroup());
        if (null != zxUserInfo.getRoles() && zxUserInfo.getRoles().size() > 0) {
            info.addRole(zxUserInfo.getRoles().get(0));
        }
    }

    /**
     * org用户信息
     * @param info
     * @param ud
     */
    private void copyOrgUserInfo(MobileUserInfo info, UserDomain ud) {
        info.setPersonId(ud.getJobNumber());
        info.setClassesId(ud.getClassesId());
        info.setClassesName(ud.getClassesName());
        info.setTeachingYear(ud.getTeachingYear());
        info.setProfessionalId(ud.getProfessionalId());
        info.setProfessionalName(ud.getProfessionalName());
        info.setCollegeId(ud.getCollegeId());
        info.setCollegeCode(ud.getCollegeCode());
        info.setClassesName(ud.getCollegeName());
        info.setOrganId(ud.getOrgId());
        info.setOrganName(ud.getOrgName());
        info.setOrganCode(ud.getOrgCode());
        info.setIdNumber(ud.getIdNumber());
        info.setStudentSource(ud.getStudentSource());
        info.setRole(ud.createRoleString());
    }

    /**
     * 企业用户信息
     * @param info
     * @param comJson
     */
    private void copyComJsonUserInfo(MobileUserInfo info, String comJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        if (!StringUtils.isEmpty(comJson)) {
            try {
                JsonNode root = objectMapper.readTree(comJson);
                JsonNode node = root.get(0);
                JsonNode t = node.get("enterpriseName");
                if (null != t) {
                    info.setEnterpriseName(t.textValue());
                }
                t = node.get("name");
                if (null != t) {
                    info.setName(t.textValue());
                }
                t = node.get("phone");
                if (null != t) {
                    info.setPhoneNumber(t.textValue());
                }
                t = node.get("mailbox");
                if (null != t) {
                    info.setEmail(t.textValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取用户信息
     * @param token
     * @return
     */
    private MobileUserInfo getUserInfo(OAuthToken token) {
        MobileUserInfo info = new MobileUserInfo();
        //获取知新用户信息，
        try {
            UserBaseInfoDomain zxUserInfo = getCurrentUser(token.getToken_type() + " " + token.getAccess_token());
            copyZXUserInfo(info, zxUserInfo);
            //获取org用户信息
            if (RoleGroup.B.getStateDesc().equals(info.getGroupType())) {
                UserDomain ud = orgRemoteService.getUserInfo(info.getId());
                copyOrgUserInfo(info, ud);
            } else if (RoleGroup.COM.getStateDesc().equals(info.getGroupType())) {
                String comJson = orgRemoteService.getEntorstrainingUserInfoByAccountId(info.getId());
                copyComJsonUserInfo(info, comJson);
            }
        } catch (Exception e) {
            LOG.warn("Get User Info fail:{}", e);
            throw new CommonException(PublicErrorCode.QUERY_EXCEPTION.getIntValue(), "服务器异常");
        }
        return info;
    }


    /**
     * 知新用户信息
     * @param info
     * @param zxUserInfo
     */
    private void copyZXUserInfoToAll(UserInfoDomain info, UserBaseInfoDomain zxUserInfo) {
        info.setId(zxUserInfo.getId());
        info.setAccountId(zxUserInfo.getId());
        info.setName(zxUserInfo.getUserName());
        info.setLogin(zxUserInfo.getLogin());
        info.setAvatar(zxUserInfo.getAvatar());
        info.setPhone(zxUserInfo.getPhoneNumber());
        info.setMail(zxUserInfo.getEmail());
        info.setPhoneActivated(zxUserInfo.getPhoneActivated());
        info.setMailActivated(zxUserInfo.getMailActivated());
        info.setUserGroup(zxUserInfo.getRoleGroup());
        if (null != zxUserInfo.getRoles() && zxUserInfo.getRoles().size() > 0) {
            info.addRole(zxUserInfo.getRoles().get(0));
        }
    }

    /**
     * org用户信息
     * @param info
     * @param ud
     */
    private void copyOrgUserInfoToAll(UserInfoDomain info, UserDomain ud) {
        info.setWorkNo(ud.getJobNumber());
        info.setGender(ud.getSex());
        info.setClassId(ud.getClassesId());
        info.setClassName(ud.getClassesName());
        info.setTeachingYear(ud.getTeachingYear());
        info.setMajorId(ud.getProfessionalId());
        info.setMajorName(ud.getProfessionalName());
        info.setCollegeId(ud.getCollegeId());
        info.setCollegeCode(ud.getCollegeCode());
        info.setCollegeName(ud.getCollegeName());
        info.setOrgId(ud.getOrgId());
        info.setIdNumber(ud.getIdNumber());
        info.setStudentSource(ud.getStudentSource());

        if (null != ud.getRoles()) {
            for (String role : ud.getRoles()) {
                info.addRole(role);
            }
        }
    }

    /**
     * org用户信息
     * @param info
     * @param org
     */
    private void copyOrgUserInfoToAll(UserInfoDomain info, OrgDomain org) {
        info.setOrgCode(org.getCode());
        info.setOrgDomainName(org.getDomainName());
        info.setOrgName(org.getName());
        info.setOrgLogo(org.getLogo());
    }

    /**
     * 登录认证、获取用户信息、上传设备token三合一接口
     * @param username          用户账号
     * @param pwd               密码
     * @param authorization     认证字符串
     * @param clientSecret      客户端密码
     * @param clientId          客户端ID
     * @param deviceTokenType   设备类型
     * @param deviceToken       设备码值
     * @return                  OAuth2的token相关信息及用户信息
     */
    public OAuth2TokenUserInfo loginMutilOperate (String username, String pwd, String authorization, String clientSecret, String clientId, Integer deviceTokenType, String deviceToken) {
        OAuth2TokenUserInfo tokenUserInfo = new OAuth2TokenUserInfo();
        //参数验证
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(pwd) || StringUtils.isEmpty(authorization) || StringUtils.isEmpty(clientSecret) || StringUtils.isEmpty(clientId)) {
            throw new CommonException(PublicErrorCode.AUTH_EXCEPTION.getIntValue(), "缺少必须输入字段");
        }
        if (!DEFAUT_LOGIN_AUTH.equals(authorization)) {
            throw new NoAuthenticationException();
        }
        //OAuth2认证登录
        OAuthToken token = oauth2 (username, pwd);
        if (null == token) {
            throw new CommonException(PublicErrorCode.AUTH_EXCEPTION.getIntValue(), "账号或密码错误");
        }
        tokenUserInfo.setAuthToken(token);

        //获取用户信息
        MobileUserInfo info = getUserInfo(token);

        //获取点点相关信息（防欺骗和头像）
        Map<String, Object> r = getAntiCheatingAndAvatar(info.getOrganId(), info.getId());
        if (null != r.get("cheating")) {
            info.setAntiCheating((Boolean) r.get("cheating"));
        } else {
            info.setAntiCheating(false);
        }
        if (null != r.get("avatar")) {
            info.setAvatar((String) r.get("avatar"));
        }
        info.setCurrentTime(new Date());//服务器当前时间

        tokenUserInfo.setUserInfo(info);

        //上传设备token
        if (null != deviceTokenType && !StringUtils.isEmpty(deviceToken)) {
            postDevicetoken(token, deviceTokenType, deviceToken);
        }
        return tokenUserInfo;
    }

    /**
     * 获取当前Token的用户信息
     * 用于替换知新的userinfo和userinfonew接口，用户信息从缓存获取并且，并且将能最新信息同步更新到缓存中
     * 不支持企业用户信息的获取
     * @param authorization     用户的access_token
     * @return                  用户信息
     */
    public UserInfoDomain getCurrentUserInfo (String authorization) {
        UserInfoDomain userInfoDomain = new UserInfoDomain();
        //获取知新用户信息，
        UserBaseInfoDomain zxUserInfo = getCurrentUser(authorization);
        if (null != zxUserInfo) {
            copyZXUserInfoToAll(userInfoDomain, zxUserInfo);
        } else {
            return userInfoDomain;
        }
        //获取org用户信息
        if (RoleGroup.B.getStateDesc().equals(userInfoDomain.getUserGroup())) {
            UserDomain ud = orgRemoteService.getUserInfo(userInfoDomain.getId());
            if (null != ud) {
                copyOrgUserInfoToAll(userInfoDomain, ud);
            }
        }
        if (null != userInfoDomain.getOrgId()) {
            OrgDomain org = orgRemoteService.getOrgInfo(userInfoDomain.getOrgId());
            if (null != org) {
                copyOrgUserInfoToAll(userInfoDomain, org);
            }
        }
        return userInfoDomain;
    }

    public String decoderString(String encodingString) {
        return aesNewCryptor.decrypt(encodingString);
    }

    public String encoderString(String planString) {
        return aesNewCryptor.encrypt(planString);
    }
}
