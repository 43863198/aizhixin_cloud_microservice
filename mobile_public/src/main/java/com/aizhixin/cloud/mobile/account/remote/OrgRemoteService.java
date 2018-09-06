package com.aizhixin.cloud.mobile.account.remote;

import com.aizhixin.cloud.mobile.account.domain.OrgDomain;
import com.aizhixin.cloud.mobile.account.domain.UserDomain;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "org-manager")
public interface OrgRemoteService {
    /**
     * 老师、学生用户信息查询
     * @param id    用户ID
     * @return      用户的详细信息
     */
    @RequestMapping(value = "/v1/user/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    UserDomain getUserInfo(@PathVariable("id") Long id);

    /**
     * 企业导师信息查询
     * @param accountId 账号ID
     * @return          企业导师信息
     */
    @RequestMapping(value = "/v1/mentorstraining/queryinfobyaccountid/{accountId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    String getEntorstrainingUserInfoByAccountId(@PathVariable("accountId") Long accountId);

    /**
     * 学校信息查询
     * @param id    学校ID
     * @return      学校信息
     */
    @RequestMapping(value = "/v1/org/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    OrgDomain getOrgInfo(@PathVariable("id") Long id);
}
