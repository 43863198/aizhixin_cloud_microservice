package com.aizhixin.cloud.dd.communication.utils;

import com.aizhixin.cloud.dd.common.utils.ConfigCache;
import com.aizhixin.cloud.dd.common.utils.http.HttpResponse;
import com.aizhixin.cloud.dd.common.utils.http.OauthGet;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class HttpSimpleUtils {
    @Autowired
    private ConfigCache configCache;

    /**
     * 如果Map存在id键名,该方法用于拼接
     *
     * @param map
     * @return
     */
    public String CompositionIds(List<Map<String, Object>> map) {
        if (null == map) {
            return null;
        }
        String ids = "";
        for (Map<String, Object> data : map) {
            if (StringUtils.isNotBlank(ids)) {
                ids += "," + data.get("id");
            } else {
                ids += data.get("id");
            }
        }
        if (ids.length() > 0) {
            return ids.substring(1);
        }
        return ids;
    }

    public void putUserMsg(List<Map<String, Object>> dataout, List<Map<String, Object>> tls, String role, String own, StringBuilder sb, Set<String> ids) {
        String tem = null;
        for (Map<String, Object> tch : tls) {
            Map<String, Object> s = new HashedMap();
            tem = null == tch.get("id") ? null : tch.get("id").toString();
            s.put("id", tem);
            if (null != own && own.equals(tem)) {
                continue;
            }
            if (null != ids && ids.size() > 0) {
                if (null != tem && ids.contains(tem)) {
                    continue;
                }
            }
            if (!org.springframework.util.StringUtils.isEmpty(tch.get("id"))) {
                sb.append(",").append(tch.get("id"));
            }
            s.put("name", null == tch.get("name") ? "" : tch.get("name"));
            s.put("phone", "");
//                s.put("phone", org.springframework.util.StringUtils.isEmpty(tch.get("phone"))  ? "" : tch.get("phone"));
            s.put("stuId", org.springframework.util.StringUtils.isEmpty(tch.get("jobNumber")) ? "" : tch.get("jobNumber"));
            tem = null == tch.get("sex") ? null : (String) tch.get("sex");
            if (!org.springframework.util.StringUtils.isEmpty(tem)) {
                if ("男".equals(tem)) {
                    s.put("sex", "male");
                } else if ("女".equals(tem)) {
                    s.put("sex", "female");
                } else {
                    s.put("sex", "");
                }
            } else {
                s.put("sex", "");
            }
            s.put("role", role);
            dataout.add(s);
        }
    }

    public void pushAvater(Map<String, String> avartMap, List<Map<String, Object>> cdata) {
        for (Map<String, Object> s : cdata) {
            String id = (String) s.get("id");
            if (null != id) {
                s.put("avatar", null == avartMap.get(id + "avatar") ? "null" : avartMap.get(id + "avatar"));
//                if (!org.springframework.util.StringUtils.isEmpty(avartMap.get("phone"))) {
                String phone = avartMap.get(id + "phone");
                s.put("phone", org.springframework.util.StringUtils.isEmpty(phone) ? "" : ("null".equals(phone) ? "" : phone));
//                }
            } else {
                s.put("avatar", "null");
            }
        }
    }

    public void fromZhixinAvater(StringBuilder sb, List<Map<String, Object>> outdata) {
        if (sb.length() > 0) {
            //请求知新网
            try {
                Map<String, String> listMap = HttpGet(sb.substring(1));
                pushAvater(listMap, outdata);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void fromZhixinAvater(String id, Map<String, Object> out) {
        if (id.length() > 0) {
            //请求知新网
            try {
                Map<String, String> listMap = HttpGet(id);
                String phone = listMap.get(id + "phone");
                if (org.springframework.util.StringUtils.isEmpty(phone)) {
                    out.put("phoneNumber", phone);
                } else {
                    out.put("phoneNumber", "");
                }
                out.put("success", Boolean.TRUE);
            } catch (Exception e) {
                e.printStackTrace();
                out.put("success", Boolean.FALSE);
            }
        }
    }

    /**
     * 将List集合转化字符串 以,分割,该方法用于拼接
     */
    public String ArrayToStringIds(List<Long> classIds) {
        if (null == classIds) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (Object obj : classIds) {
            sb.append(obj);
            sb.append(",");
        }
        return sb.toString().substring(0, sb.length() - 1);
    }


    /**
     * @param ids
     * @return 存储用户头像和手机号码的Map
     * @throws IOException
     */
    public Map<String, String> HttpGet(String ids) throws IOException {
        if ("".equals(ids)) {
            return null;
        }
        OauthGet get = new OauthGet();
        HttpResponse response = null;
        response = get.get(configCache.getConfigValueByParm("user.service.host"), configCache.getConfigValueByParm("user.service.avatarList") + "?ids=" + ids, null);
        String reg = ".*error.*";
        if (response.getResponseBody().matches(reg)) {
            response = null;
        }
        HashMap<String, String> listMap = new HashMap();
        if (null != response) {
            String s = response.getResponseBody();
            JSONObject user = JSONObject.fromString(s);
            Iterator<String> iterator = user.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = user.getString(key);
                JSONObject user_json = JSONObject.fromString(value);
                String avatar = user_json.getString("avatar");
                String phone = user_json.getString("phoneNumber");
                listMap.put(key + "avatar", avatar);
                listMap.put(key + "phone", phone);
            }
        }
        return listMap;
    }
}
