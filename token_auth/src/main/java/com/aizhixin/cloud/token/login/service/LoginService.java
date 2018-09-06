package com.aizhixin.cloud.token.login.service;

import com.aizhixin.cloud.token.common.core.ErrorCode;
import com.aizhixin.cloud.token.common.exception.CommonException;
import com.aizhixin.cloud.token.common.exception.NoAuthenticationException;
import com.aizhixin.cloud.token.login.common.PostData;
import com.aizhixin.cloud.token.login.common.StatusCodeEnum;
import com.aizhixin.cloud.token.login.util.RestUtil;
import com.aizhixin.cloud.token.config.ScanCodeConfig;
import com.aizhixin.cloud.token.login.common.ResultClass;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;


/**
 * Created by wu on 2017/7/20.
 */
@Component
public class LoginService {
    final static private Logger LOG = LoggerFactory.getLogger(LoginService.class);
    @Autowired
    private ScanCodeConfig scanCodeConfig;
    @Autowired
    private RestUtil restUtil;

    public ResultClass checkCodeService(String socketId, Long timeStamp, String identification, String accessToken) throws  NoAuthenticationException {
        String id = null;
        Integer code = null;
        ResultClass result = new ResultClass();
        String url = scanCodeConfig.getZhixinhost();
        url = url + scanCodeConfig.getInfoplus();
        String sockethosturl = scanCodeConfig.getSockethost();
        sockethosturl = sockethosturl + scanCodeConfig.getSocketapi();
        if (null != scanCodeConfig.getIdentification()) {
            try {
                String userJson = restUtil.get(url, accessToken);
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> user = mapper.readValue(userJson, Map.class);
                id = user.get("id").toString();
            } catch (Exception e) {
                result.setResultCode(StatusCodeEnum.UNAUTHORIZED.getState());
                result.setSuccess(false);
                result.setMessage(StatusCodeEnum.UNAUTHORIZED.getStateDesc());
                return result;
//              throw new NoAuthenticationException();
            }
            if (!StringUtils.isBlank(id)) {
                PostData postdata = new PostData();
                postdata.setId(socketId);
                postdata.setAction("ready");
                postdata.setData("success");
                try {
                    String respone = restUtil.postData(sockethosturl, postdata);
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> user = mapper.readValue(respone, Map.class);
                    code = Integer.valueOf(user.get("code").toString());
                    if (code.equals(StatusCodeEnum.POST_SUCCES.getState())) {
                        result.setResultCode(StatusCodeEnum.SCAN_SUCCESS.getState());
                        result.setSuccess(true);
                        result.setMessage(StatusCodeEnum.SCAN_SUCCESS.getStateDesc());
                    } else {
                        result.setResultCode(StatusCodeEnum.POST_FAIL.getState());
                        result.setSuccess(false);
                        result.setMessage(StatusCodeEnum.POST_FAIL.getStateDesc());
                    }
                } catch (Exception e) {
                    result.setResultCode(StatusCodeEnum.POST_FAIL.getState());
                    result.setSuccess(false);
                    result.setMessage(StatusCodeEnum.POST_FAIL.getStateDesc());
                    return  result;
//                  throw new CommonException(StatusCodeEnum.POST_FAIL.getState(), StatusCodeEnum.POST_FAIL.getStateDesc());
                }
            } else {
                result.setResultCode(StatusCodeEnum.UNAUTHORIZED.getState());
                result.setSuccess(false);
                result.setMessage(StatusCodeEnum.UNAUTHORIZED.getStateDesc());
            }
        } else {
            result.setResultCode(StatusCodeEnum.UNKNOWN_SCAN.getState());
            result.setSuccess(false);
            result.setMessage(StatusCodeEnum.UNKNOWN_SCAN.getStateDesc());
        }
        return result;
    }

    public ResultClass confirmLoginService(String socketId, Long timeStamp, String identification, String accessToken) {
        ResultClass result = new ResultClass();
        String url = scanCodeConfig.getSockethost();
        url = url + scanCodeConfig.getSocketapi();
        Integer code = null;
        PostData postdata = new PostData();
        postdata.setId(socketId);
        postdata.setAction("success");
        postdata.setData(accessToken);
        try {
            String respone = restUtil.postData(url, postdata);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> user = mapper.readValue(respone, Map.class);
            code = Integer.valueOf(user.get("code").toString());
        } catch (Exception e) {
            result.setResultCode(StatusCodeEnum.POST_FAIL.getState());
            result.setSuccess(false);
            result.setMessage(StatusCodeEnum.POST_FAIL.getStateDesc());
            return result;
//          throw new CommonException(StatusCodeEnum.POST_FAIL.getState(), StatusCodeEnum.POST_FAIL.getStateDesc());
        }
        if (code.equals(StatusCodeEnum.POST_SUCCES.getState())) {
            result.setResultCode(StatusCodeEnum.LOGIN_SUCCESS.getState());
            result.setSuccess(true);
            result.setMessage(StatusCodeEnum.LOGIN_SUCCESS.getStateDesc());
        }else{
            result.setResultCode(StatusCodeEnum.LOGIN_FAIL.getState());
            result.setSuccess(false);
            result.setMessage(StatusCodeEnum.LOGIN_FAIL.getStateDesc());
        }
        return result;
    }

}
