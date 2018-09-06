package com.aizhixin.cloud.token.service;

import com.aizhixin.cloud.token.common.exception.NoAuthenticationException;
import com.aizhixin.cloud.token.config.AppStoreConfig;
import com.aizhixin.cloud.token.entity.Access;
import com.aizhixin.cloud.token.provider.DefaultKeyGenerator;
import com.aizhixin.cloud.token.provider.store.redis.RedisTokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * accessToken管理
 * Created by zhen.pan on 2017/6/7.
 */
@Component
public class AccessTokenService {
    private final static int defaultTTL = 86400;
    private RedisTokenStore redisTokenStore;
    private DefaultKeyGenerator defaultKeyGenerator;
    private TokenService tokenService;
    private AppStoreConfig appStoreConfig;

    @Autowired
    public AccessTokenService(RedisConnectionFactory redisConnectionFactory, DefaultKeyGenerator defaultKeyGenerator, TokenService tokenService, AppStoreConfig appStoreConfig) {
        redisTokenStore = new RedisTokenStore(redisConnectionFactory);
        redisTokenStore.setPrefix("azx:token");
        this.defaultKeyGenerator = defaultKeyGenerator;
        this.tokenService = tokenService;
        this.appStoreConfig = appStoreConfig;
    }

    public boolean hasAuth(Map<String, String> data, String appId, String appSecurity) {
        if (StringUtils.isEmpty(appId)) {
            return false;
        }
        String t = data.get(appId);
        if (StringUtils.isEmpty(t)) {
            return false;
        }
        if (t.equals(appSecurity)) {
            return true;
        }
        return false;
    }

    public String createToken(String appId, String appSecurity, String auth, Integer ttl) {
        //验证协议
        if (null != auth) {
            String base64 = defaultKeyGenerator.getBase64(appId + ":" + appSecurity);
            if (!auth.equals(base64)) {
                throw new NoAuthenticationException();
            }
        } else {
            throw new NoAuthenticationException();
        }
        //验证appId和appSecurity
        if (!hasAuth(appStoreConfig.getData(), appId, appSecurity)) {
            throw new NoAuthenticationException();
        }
        //生成token
        String token = tokenService.generatorToken();
        //设置有效期
        //将token和appId关系和token分别保存，是否删除同一个appId的未过期token
        if (null == ttl || ttl <= 0) {
            ttl = defaultTTL;
        }
        Access a = new Access();
        a.setAppId(appId);
        a.setToken(token);
        a.setTtl(ttl);
        redisTokenStore.storeAccessToken(a);
        return token;
    }

    public boolean validateToken(String appId, String token) {
        if (StringUtils.isEmpty(token)) {
            throw new NoAuthenticationException();
        }
        String stAppId = redisTokenStore.readAccessToken(token);
        if (StringUtils.isEmpty(stAppId)) {
            throw new NoAuthenticationException();
        }
        return stAppId.equals(appId);
    }
}
