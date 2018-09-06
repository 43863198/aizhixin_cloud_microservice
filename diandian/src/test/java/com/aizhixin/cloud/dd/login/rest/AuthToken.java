package com.aizhixin.cloud.dd.login.rest;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LIMH
 * @date 2018/1/18
 */
@Data
@NoArgsConstructor
public class AuthToken {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private long expires_in;
    private String scope;
    private String token;

    public String getToken() {
        token = token_type + " " + access_token;
        return token;
    }
}
