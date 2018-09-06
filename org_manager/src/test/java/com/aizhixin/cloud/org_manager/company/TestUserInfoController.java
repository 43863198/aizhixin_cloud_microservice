package com.aizhixin.cloud.org_manager.company;

import com.aizhixin.cloud.orgmanager.common.rest.RestUtil;
import com.aizhixin.cloud.rest.RestBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * 测试样例
 * 班级API测试
 * Created by zhen.pan on 2017/4/11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class TestUserInfoController extends RestBase {
    @Before
    public void init() {
        super.init("bearer bb0a0b2a-2ae8-4726-b774-063fd61f6912");
    }

    @Test
    public void get() {
        get("http://dledu.aizhixintest.com/zhixin_api/api/web/v1/users/userinfo");
    }
    @Test
    public void getAvatar() {
        get("http://dledu.aizhixin.com/zhixin_api/api/account/userAvatarlist?ids=46625");
    }
    @Test
    public void getUser() {
        get("http://dledudev.aizhixin.com/zhixin_api/api/web/v1/users/checkuserisexist?account=kjkf201697001");
    }

    @Test
    public void getAccount() {
        get("http://dledutest.aizhixin.com/zhixin_api/api/account");
    }

    @Test
    public void getUserInfo() {
        get("http://dledu.dlztc.com/zhixin_api/api/account/infoplus");
    }
    @Test
    public void testLogin() {
        login("http://localhost:8001/account/token", "kjkf101", "123456");
    }

    @Test
    public void testAddAccount() {
        RestUtil restUtil = new RestUtil();
        Long accountId = null;
        try {
            String json = restUtil.post("http://dledudev.aizhixin.com/zhixin_api/api/account/add" + "?loginName=qlgl000000002&name=测试用户&userType=COM", null);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(json);
            if (null != rootNode) {
                JsonNode idNode = rootNode.path("id");
                if (null != idNode) {
                    accountId = idNode.longValue();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("---------------------------" + accountId);
    }


    @Test
    public void testPolyvVideoMsg() {
        RestUtil restUtil = new RestUtil();
        try {
            long t = System.currentTimeMillis();
            String sign = DigestUtils.sha1Hex("ptime=" + t + "&vid=e1510bdd3a2f800a754444cc6e0fb195_eE8vnZopmkt").toUpperCase();
            String json = restUtil.post("http://api.polyv.net/v2/video/e1510bdd3a/get-video-msg" + "?vid=e1510bdd3a2f800a754444cc6e0fb195_e&ptime=" + t + "&sign=" + sign, null);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(json);
            if (null != rootNode) {
                System.out.println(rootNode.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLogin2 () {
//        login("http://172.16.23.43:8001/zhixin_api/oauth/token", "qygl000009", "123456");
//        login("http://dledu.aizhixin.com/zhixin_api/oauth/token", "", "123456");
//        RestUtil restUtil = new RestUtil();
//        restUtil.oauthLogin("http://172.16.23.43:8001/zhixin_api/oauth/token", "qygl000009", "123456");
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        List<MediaType> mt = new ArrayList<>();
        mt.add(MediaType.APPLICATION_JSON_UTF8);
        headers.set(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("dleduApp:mySecretOAuthSecret".getBytes()));
        headers.setAccept(mt);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("scope", "read write");
        map.add("client_secret", "mySecretOAuthSecret");
        map.add("client_id", "dleduApp");
        map.add("username", "17393123052");
        map.add("password", "112434wwyy");
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        try {
            ResponseEntity<String> response = rest.exchange("http://dledu.aizhixin.com/zhixin_api/oauth/token", HttpMethod.POST,  entity,  String.class);
            System.out.println(response.getBody());
        } catch (RestClientException e) {
            if (e instanceof  HttpClientErrorException) {
                HttpClientErrorException ee = (HttpClientErrorException)e;
                System.out.println(ee.getResponseBodyAsString());
            } else if (e instanceof ResourceAccessException) {
                System.out.println("服务器异常");
            } else if (e instanceof HttpServerErrorException) {
                HttpServerErrorException se = (HttpServerErrorException)e;
                System.out.println("-----------------" + se.getResponseBodyAsString());
            }
            e.printStackTrace();
        }
    }
    @Test
    public void getCurrentUserInfo() {
        get("http://dledu.aizhixintest.com/zhixin_api/api/currentuser");
    }

    @Test
    public void testSendNoLoginSendMsgCode() {
            put("http://dledu.aizhixin.com/zhixin_api/api/web/v1/users/nologinsendbindingphonecode?id=421368&phone=18710763531");
    }

    @Test
    public void testSendNoLoginSendMsg() {
        post("http://dledu.aizhixin.com/zhixin_api/api/web/v1/msg/send?msg=abcdefg&phone=18681861821");
    }

    @Test
    public void postSignIn() {
//        postBody("", "");
        String android="234567",ios= null,androidTeacher= null, iosTeacher=null;
        if (!((android != null && ios == null && androidTeacher == null && iosTeacher == null)
                || (android == null && ios != null && androidTeacher == null && iosTeacher == null)
                || (android == null && ios == null && androidTeacher != null && iosTeacher == null) || (android == null
                && ios == null && androidTeacher == null && iosTeacher != null))) {
            System.out.println("-------------------------");
        }
        System.out.println("========================");
    }

    @Test
    public void deleteUser() {
        String r = "444860,444894,444916,444920,444922,444924,444926,444928,444930,444932,444942,444944,444940,444950,444952,444966,444968,445276,445278,445280,445090,445294,445376,445466,445610,445300,445320,445372,445416,445318,445324,445316,445296,445348,445298,445284,445468,445472,445330,445386,445336,445606,445344,445560,445312,445396,445282,445342,445400,445340,445450,445398,445350,445352,445370,445478,445354,445358,445356,445408,445404,445890,445360,445338,445334,445498,445644,445440,445460,445410,445412,445406,445514,445434,445436,445574,445548,445694,445888,445308,445438,445310,445326,445306,445288,445286,445328,445322,445332,445302,445314,445290,445292,445402,445476,445426,445484,445458,445470,445590,445508,445512,445454,445432,445626,445594,445510,445598,445494,445940,445820,445456,445592,445668,445672,445674,445686,445492,445490,445850,445822,445428,445752,445500,445502,445628,445858,445430,445452,445564,445482,445496,445602,445506,445624,445596,445562,445802,445622,445474,445604,445700,445654,445646,445648,445882,445884,445758,445384,445346,445364,445446,445488,445380,445570,445382,445444,445448,445608,445486,445378,445392,445390,445414,445422,445388,445362,445394,445424,445788,445836,445848,445886,445892,445578,445580,445944,445946,445518,445520,445522,445524,445526,445528,445536,445538,445540,445542,445544,445826,445688,445690,445692,445696,445698,445724,445726,445728,445868,445870,445914,445916,445918,445920,445922,445924,445926,445938,445766,445768,445770,445576,446012,446014,445750,445778,445780,445782,445784,445786,445816,445818,445678,445712,445714,445716,445760,445762,445764,445796,445798,445800,445824,446006,445652,445550,445552,445554,445556,445558,445650,445656,445658,445660,445662,445708,445710,445812,445814,446010,445546,445582,445584,445586,445630,445632,445634,445636,445638,445640,445642,445804,445832,445834,445840,445842,445844,445902,445304,445904,445730,445732,445734,445736,445754,445810,445806,445374,445566,445702,445420,445368,445756,445418,445568,445676,445366,445670,445588,445464,445504,445442,445462,445704,445706,445480,446030,445618,445838,445534,445998,446008,445932,445880,445532,445744,446002,445108,445928,446032,445124,445680,445852,445830,445530,445160,445828,445878,446004,445620,445954,445996,445270,445994,445088,445110,445106,445894,445664,446040,445934,445896,445740,445188,445174,445190,445218,445128,445150,445948,445746,445256,445240,445254,445194,445200,445242,445244,445246,445248,445794,445854,445792,445100,445102,445942,445930,445906,445094,445092,445096,445114,445116,445126,445098,445774,445272,445856,445738,445172,445164,445162,445204,445186,445140,445230,445166,445178,445210,445176,445228,445168,445614,445980,445790,446044,445986,445952,446046,445950,445874,445718,445772,445776,445180,445182,445156,445214,445154,445184,445212,445742,445264,445808,445266,445274,445262,445148,445136,445260,445250,445146,445192,445158,445258,445216,445236,445134,445846,445220,445226,445720,445268,445872,445132,445612,445130,445198,445912,446026,445908,445910,445966,445968,445572,446048,445864,445970,445900,445956,445978,445976,446038,446036,445866,445600,445862,445170,445122,445898,445682,445936,445616,445138,445722,445112,445876,445516,445118,445120,445860,445196,445224,445208,445222,445232,445202,445206,445684,445144,445152,445142,445252,445234,445238,445972,445974,445960,445748,445958,445666,445962,445104,446052,446054,446170,446098,446194,446060,446106,446090,446140,446134,446196,446096,446022,446184,446042,446034,445964,446020,446028,446186,446088,446086,446180,446130,446132,446162,446074,446114,446182,446152,446092,446084,446078,446164,446198,446104,446166,446094,446100,446082,445982,445984,446072,446000,446158,446156,446062,446018,446066,446016,446144,445990,445988,445992,446108,446112,446110,446160,446150,446148,446142,446146,446154,446070,446064,446068,446192,446172,446058,446080,446174,446176,446102,446124,446128,446138,446136,446190,446050,446056,446178,446122,446126,446024,446168,446120,446076";
        String[] us = r.split(",");
        for (String uid : us) {
            delete("http://gateway.aizhixin.com/org-manager/v1/students/delete/" + uid + "?userId=123");
        }
    }

}
