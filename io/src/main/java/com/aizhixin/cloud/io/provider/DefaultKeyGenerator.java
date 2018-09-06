package com.aizhixin.cloud.io.provider;

import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

/**
 * Created by zhen.pan on 2017/6/6.
 */
@Component
public class DefaultKeyGenerator {
    private final static String ENCODING = "UTF-8";
    private MessageDigest digest;
    public DefaultKeyGenerator () {
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsae) {
            throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).", nsae);
        }
    }

    public String getMD5(String value) {
        try {
            byte[] bytes = digest.digest(value.getBytes(ENCODING));
            StringBuilder t = new StringBuilder();
            for (byte b : bytes) {
                int temp = b & 255;
                if (temp < 16 & temp > 0) {
                    t.append("0");
                }
                t.append(Integer.toHexString(temp));
            }
            return t.toString();
        } catch (UnsupportedEncodingException uee) {
            throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).", uee);
        }
    }

    public String getBase64(String value) {
        try {
            return Base64.getEncoder().encodeToString(value.getBytes(ENCODING));
        } catch (UnsupportedEncodingException uee) {
            throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).", uee);
        }
    }

    public String getUUID() {
        return UUID.randomUUID().toString().replaceAll("\\-", "").toUpperCase();
    }
}
