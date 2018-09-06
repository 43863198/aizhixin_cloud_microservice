package com.aizhixin.cloud.io.service;

import com.aizhixin.cloud.io.auth.TokenAuth;
import com.aizhixin.cloud.io.common.rest.RestUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class PolyvVedioService {
    private Logger LOG = LoggerFactory.getLogger(PolyvVedioService.class);
    private String polyvUserId = "e1510bdd3a";
    private String polyvSecretkey = "E8vnZopmkt";

    private String polyvMsgUrl = "http://api.polyv.net/v2/video/e1510bdd3a/get-video-msg";
    @Autowired
    private TokenAuth tokenAuth;
    @Autowired
    private RestUtil restUtil;

    private void validationAuthToken(String appId, String token) {
        tokenAuth.authTokean(appId, token);
    }
    public ResponseEntity<String> getPolyvVideoMsgByVid(String vid, String appId, String token) {
        validationAuthToken(appId, token);
        try {
            long t = System.currentTimeMillis();
            String sign = DigestUtils.sha1Hex("ptime=" + t + "&vid=" + vid + polyvSecretkey).toUpperCase();
            String url = "http://api.polyv.net/v2/video/" + polyvUserId + "/get-video-msg" + "?vid=" + vid + "&ptime=" + t + "&sign=" + sign;
            LOG.debug(url);
            String json = restUtil.post(url, null);
            LOG.debug(json);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(json);
        } catch (Exception e) {
            LOG.warn("Invoke polyv get vidoe msg error:{}", e);
            return ResponseEntity.badRequest().body("");
        }
    }
}
