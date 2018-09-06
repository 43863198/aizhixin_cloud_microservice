/**
 * 
 */
package com.aizhixin.cloud.ew.common.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.aizhixin.cloud.ew.common.dto.AccountDTO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author zhen.pan
 *
 */
@Component
public class AuthUtilService {

	@Value("${dl.dledu.back.userInfoUrl}")
	private String userInfoUrl;

	@Value("${dl.dledu.back.userInfo}")
	private String userInfo;

	@Value("${dl.dledu.back.host}")
	private String dleduBackHost;

	@Value("${dl.dledu.back.userAvatarUrl}")
	private String userAvatarUrl;

	@Value("${dl.org.back.host}")
	private String orgBackHost;

	@Value("${dl.org.back.getuserInfo}")
	private String getuserinfo;

	@Value("${dl.org.back.getuserInfoByAccountId}")
	private String getuserInfoByAccountId;

	@Cacheable(value = "authorCache")
	public AccountDTO getByToken(String token) {
		OAuth2Get get = new OAuth2Get();
		AccountDTO dto = null;
		try {
			HttpContent httpContent = get.get(dleduBackHost + userInfo, token);
			if (httpContent.getStatusCode() == 200) {
				dto = new AccountDTO();
				String Str = httpContent.getResponseBody();
				JSONObject jsobj1 = new JSONObject(Str);
				dto.setId(jsobj1.getLong("id"));
				dto.setName(jsobj1.get("name").toString());
				if (jsobj1.has("orgId")) {
					dto.setOrganId(jsobj1.getLong("orgId"));
				}
				if (jsobj1.has("phone")) {
					dto.setPhoneNumber(jsobj1.get("phone").toString());
				}
				if (jsobj1.has("className")) {
					dto.setClassName(jsobj1.get("className").toString());
				}
				if (jsobj1.has("orgName")) {
					dto.setOrganName(jsobj1.get("orgName").toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}

	// 2017-6-29 修改取用户信息用知新网
	@Cacheable(value = "authorCache")
	public AccountDTO getByTokenForLF(String token) {
		OAuth2Get get = new OAuth2Get();
		AccountDTO account = null;
		HttpContent httpcontent;
		try {
			httpcontent = get.get(dleduBackHost + userInfo, token);
			if (httpcontent.getStatusCode() == 200) {
				account = new AccountDTO();
				String Str = httpcontent.getResponseBody();
				JSONObject jsobj1 = new JSONObject(Str);
				Long userId = jsobj1.getLong("id");
				String name = null;
				String gender = null;
				String organName = null;
				String avatar = null;
				name = jsobj1.get("name").toString();
				gender = jsobj1.get("gender").toString();
				if (jsobj1.has("orgName")) {
					organName = jsobj1.get("orgName").toString();
				}
				if (jsobj1.has("avatar")) {
					avatar = jsobj1.get("avatar").toString();
				}
				RestTemplate rest = new RestTemplate();
				String str = rest.getForObject(orgBackHost + getuserinfo + userId, String.class);
				JSONObject jsobj = new JSONObject(str);
				account.setId(userId);
				account.setName(name);
				account.setGender(gender);
				account.setAvatar(avatar);
				account.setOrganName(organName);
				if (jsobj.has("name")) {
					account.setLogin(jsobj.get("name").toString());
				}
				if (jsobj.has("phone")) {
					account.setPhoneNumber(jsobj.get("phone").toString());
				}
				if (jsobj.has("email")) {
					account.setEmail(jsobj.get("email").toString());
				}
				if (jsobj.has("orgId")) {
					account.setOrganId(jsobj.getLong("orgId"));
				}
				if (jsobj.has("collegeName")) {
					account.setCollegeName(jsobj.get("collegeName").toString());
				}
				if (jsobj.has("jobNumber")) {
					account.setPersonId(jsobj.get("jobNumber").toString());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return account;
	}

	@Cacheable(value = "authorCache")
	@SuppressWarnings("unchecked")
	public AccountDTO getByTokenForOrg(String token) {
		OAuth2Get get = new OAuth2Get();
		AccountDTO account = null;
		HttpContent httpcontent;
		try {
			httpcontent = get.get(dleduBackHost + userInfo, token);
			if (httpcontent.getStatusCode() == 200) {
				account = new AccountDTO();
				String Str = httpcontent.getResponseBody();
				JSONObject jsobj1 = new JSONObject(Str);
				Long userId = jsobj1.getLong("id");
				RestTemplate rest = new RestTemplate();
				String str = rest.getForObject(orgBackHost + getuserInfoByAccountId + userId, String.class);
				JSONArray ja = new JSONArray(str);
				Iterator<JSONObject> it = ja.iterator();
				while (it.hasNext()) {
					JSONObject jsobj = it.next();
					account.setId(userId);
					if (jsobj.has("loginName")) {
						account.setLogin(jsobj.get("loginName").toString());
					}
					if (jsobj.has("orgId")) {
						account.setOrganId(jsobj.getLong("orgId"));
					}
					if (jsobj.has("phone")) {
						account.setPhoneNumber(jsobj.get("phone").toString());
					}
					if (jsobj.has("mailbox")) {
						account.setEmail(jsobj.get("mailbox").toString());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return account;
	}

	public AccountDTO getavatarUserInfo(Long id) {
		RestTemplate rest = new RestTemplate();
		AccountDTO dto = new AccountDTO();
		try {
			String str = rest.getForObject(dleduBackHost + userAvatarUrl + "?ids=" + id, String.class);
			JSONObject jsonObject = new JSONObject(str);
			Iterator<?> iterator = jsonObject.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				String value = jsonObject.get(key).toString();
				value = value.replaceAll("null", "\"\"");
				JSONObject jsonUser = new JSONObject(value);
				dto.setAvatar(jsonUser.get("avatar").toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}


	public List<AccountDTO> getavatarUserInfo(Set<Long> ids) {
		List<AccountDTO> rs = new ArrayList<>();
		if (null == ids || ids.size() <= 0) {
			return rs;
		}
		
		RestTemplate rest = new RestTemplate();
		StringBuilder sb = new StringBuilder ();
		for(Long id : ids) {
			sb.append(",").append(id);
		}
		try {
			String str = rest.getForObject(dleduBackHost + userAvatarUrl + "?ids=" + sb.substring(1), String.class);
			JSONObject jsonObject = new JSONObject(str);
			Iterator<?> iterator = jsonObject.keys();
			while (iterator.hasNext()) {
				AccountDTO dto = new AccountDTO();
				String key = (String) iterator.next();
				String value = jsonObject.get(key).toString();
				value = value.replaceAll("null", "\"\"");
				JSONObject jsonUser = new JSONObject(value);
				dto.setAvatar(jsonUser.get("avatar").toString());
				dto.setId(jsonUser.getLong("id"));
				rs.add(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

}
