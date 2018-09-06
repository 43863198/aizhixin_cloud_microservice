package com.aizhixin.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Demo1 {

    private static void checkSignAndDecrypt() throws AlipayApiException {
        // 参数构建
        String biz_content = "<XML><AppId><![CDATA[2013082200024893]]></AppId><FromUserId><![CDATA[2088102122485786]]></FromUserId><CreateTime>1377228401913</CreateTime><MsgType><![CDATA[click]]></MsgType><EventType><![CDATA[event]]></EventType><ActionParam><![CDATA[authentication]]></ActionParam><AgreementId><![CDATA[201308220000000994]]></AgreementId><AccountNo><![CDATA[null]]></AccountNo><UserInfo><![CDATA[{\"logon_id\":\"15858179811\",\"user_name\":\"许旦辉\"}]]></UserInfo></XML>";
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDIgHnOn7LLILlKETd6BFRJ0GqgS2Y3mn1wMQmyh9zEyWlz5p1zrahRahbXAfCfSqshSNfqOmAQzSHRVjCqjsAw1jyqrXaPdKBmr90DIpIxmIyKXv4GGAkPyJ/6FTFY99uhpiq0qadD/uSzQsefWo0aTvP/65zi3eof7TcZ32oWpwIDAQAB";
        String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMiAec6fsssguUoRN3oEVEnQaqBLZjeafXAxCbKH3MTJaXPmnXOtqFFqFtcB8J9KqyFI1+o6YBDNIdFWMKqOwDDWPKqtdo90oGav3QMikjGYjIpe/gYYCQ/In/oVMVj326GmKrSpp0P+5LNCx59ajRpO8//rnOLd6h/tNxnfahanAgMBAAECgYEAusouMFfJGsIWvLEDbPIhkE7RNxpnVP/hQqb8sM0v2EkHrAk5wG4VNBvQwWe2QsAuY6jYNgdCPgTNL5fLaOnqkyy8IobrddtT/t3vDX96NNjHP4xfhnMbpGjkKZuljWKduK2FAh83eegrSH48TuWS87LjeZNHhr5x4C0KHeBTYekCQQD5cyrFuKua6GNG0dTj5gA67R9jcmtcDWgSsuIXS0lzUeGxZC4y/y/76l6S7jBYuGkz/x2mJaZ/b3MxxcGQ01YNAkEAzcRGLTXgTMg33UOR13oqXiV9cQbraHR/aPmS8kZxkJNYows3K3umNVjLhFGusstmLIY2pIpPNUOho1YYatPGgwJBANq8vnj64p/Hv6ZOQZxGB1WksK2Hm9TwfJ5I9jDu982Ds6DV9B0L4IvKjHvTGdnye234+4rB4SpGFIFEo+PXLdECQBiOPMW2cT8YgboxDx2E4bt8g9zSM5Oym2Xeqs+o4nKbcu96LipNRkeFgjwXN1708QuNNMYsD0nO+WIxqxZMkZsCQHtS+Jj/LCnQZgLKxXZAllxqSTlBln2YnBgk6HqHLp8Eknx2rUXhoxE1vD9tNmom6PiaZlQyukrQkp5GOMWDMkU=";
        Map<String, String> params = new HashMap<String, String>();
        params.put("biz_content", AlipaySignature.rsaEncrypt(biz_content, publicKey, "UTF-8"));
        params.put("charset", "UTF-8");
        params.put("service", "alipay.mobile.public.message.notify");
        params.put("sign_type", "RSA");
        params.put("sign", AlipaySignature.rsaSign(params, privateKey, "UTF-8"));

        // 验签&解密
        String resultContent = AlipaySignature.checkSignAndDecrypt(params, publicKey, privateKey,
                true, true);

        System.out.println(resultContent);
    }


    private static void encryptAndSign() throws AlipayApiException {
        // 参数构建
        String bizContent = "<XML><ToUserId><![CDATA[2088102122494786]]></ToUserId><AppId><![CDATA[2013111100036093]]></AppId><AgreementId><![CDATA[20131111000001895078]]></AgreementId>"
                + "<CreateTime>12334349884</CreateTime>"
                + "<MsgType><![CDATA[image-text]]></MsgType>"
                + "<ArticleCount>1</ArticleCount>"
                + "<Articles>"
                + "<Item>"
                + "<Title><![CDATA[[回复测试加密解密]]></Title>"
                + "<Desc><![CDATA[测试加密解密]]></Desc>"
                + "<Url><![CDATA[http://m.taobao.com]]></Url>"
                + "<ActionName><![CDATA[立即前往]]></ActionName>"
                + "</Item>"
                + "</Articles>" + "<Push><![CDATA[false]]></Push>" + "</XML>";
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDIgHnOn7LLILlKETd6BFRJ0GqgS2Y3mn1wMQmyh9zEyWlz5p1zrahRahbXAfCfSqshSNfqOmAQzSHRVjCqjsAw1jyqrXaPdKBmr90DIpIxmIyKXv4GGAkPyJ/6FTFY99uhpiq0qadD/uSzQsefWo0aTvP/65zi3eof7TcZ32oWpwIDAQAB";
        String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMiAec6fsssguUoRN3oEVEnQaqBLZjeafXAxCbKH3MTJaXPmnXOtqFFqFtcB8J9KqyFI1+o6YBDNIdFWMKqOwDDWPKqtdo90oGav3QMikjGYjIpe/gYYCQ/In/oVMVj326GmKrSpp0P+5LNCx59ajRpO8//rnOLd6h/tNxnfahanAgMBAAECgYEAusouMFfJGsIWvLEDbPIhkE7RNxpnVP/hQqb8sM0v2EkHrAk5wG4VNBvQwWe2QsAuY6jYNgdCPgTNL5fLaOnqkyy8IobrddtT/t3vDX96NNjHP4xfhnMbpGjkKZuljWKduK2FAh83eegrSH48TuWS87LjeZNHhr5x4C0KHeBTYekCQQD5cyrFuKua6GNG0dTj5gA67R9jcmtcDWgSsuIXS0lzUeGxZC4y/y/76l6S7jBYuGkz/x2mJaZ/b3MxxcGQ01YNAkEAzcRGLTXgTMg33UOR13oqXiV9cQbraHR/aPmS8kZxkJNYows3K3umNVjLhFGusstmLIY2pIpPNUOho1YYatPGgwJBANq8vnj64p/Hv6ZOQZxGB1WksK2Hm9TwfJ5I9jDu982Ds6DV9B0L4IvKjHvTGdnye234+4rB4SpGFIFEo+PXLdECQBiOPMW2cT8YgboxDx2E4bt8g9zSM5Oym2Xeqs+o4nKbcu96LipNRkeFgjwXN1708QuNNMYsD0nO+WIxqxZMkZsCQHtS+Jj/LCnQZgLKxXZAllxqSTlBln2YnBgk6HqHLp8Eknx2rUXhoxE1vD9tNmom6PiaZlQyukrQkp5GOMWDMkU=";
        String responseContent = AlipaySignature.encryptAndSign(bizContent, publicKey, privateKey,
                "UTF-8", true, true);
        System.out.println(responseContent);

    }

    public void appPay() {

        String APP_ID = "2017121800944206";
        String APP_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCt19qoiQHx6fevZsVpb+2n1e9TqdhdfYcHHeF6PHdl+l8/C/Lkdt6ertzZhCdhh8nN2pO0zIZlLmxTUZHs0pA1j525ow6WIpP3TUqKYBh1MXLOB0bExd20taBZKamm6hFCeJQ3gra5MPV4A2u5j8yJ69jScn9KkKZrUFwQGsjy65jHPt7WYWQoDF6nREPQ6NdxDg6EyMcMCP/dOGMJsh1tS/Gnb10N3Y+IqmHSYCWXBvhb9m/6fKyNhHZ07zDju8x3C0KeGOQ5Fn3jgQUjqexH8FUB3IZesH9vFSgONFN7nbadREVVKzDVNVTsv9QblwhRKLcAj131nmCqelEfSiVZAgMBAAECggEAeVUXktuVFPAUZEKZdB6fw61oTX9UuOO803XCgpsjRnS593nyfAyPEje/gr1e8w5gBiIRR3YcMxB0oK5k5+KKridfkUpCCv11QnyakvvA9kKd+zhO51WmiNLfaHvQoePqSfd4k0nTGGwYVzwj+q5ntrd6bF9ngnZ1AJ9ksgTPzeMC6+Rvwsvlz4CcGU3keBn37+vhBGAeOWdF9ZWIK28I5/RCyYqqkSinOmK+S5T/caTjWfNk2kjlE6+RJhbiVUoLDRtxGVSStQl3Ugqvkw63XgeDfNsjAEv30gSKrV6LuPowsNILQiKy3zg+rLiW9JZ3hA8toZT/FvB33J/UD+2DgQKBgQDuDz9Co3CZYQVV2Zyr7QII1ofmUGAYH0KlVFEtm1xG43SJNXJd+Dpvw2BilqrWnPj+pFobl6wt697ARutWaO9qCL67voWA/7P7RBk5zhv2bRQzVE1qBy2oIxToyQgfbb/X5RzL4acXVe3k48wDVF8XO2iSLdV1EE0WLVtlzRR76QKBgQC68bct4Wuox9p7Jo2b5cS5BfLeRq51wFs8XoQxWHe9umuMnMiu5UNwtGpCInzcGD9BMxZOTG3ONUTFiJNXC4zSDolADbj8eGt39rKV5HiSqPAPAVU9x6W4GzwRE0bN2AEWI1r961HhTnVH8h8ylez+z5BOAsVHTq8dm6nzB3kn8QKBgQDkyw6XcxEiuBYynkRvEAgmilhuR80zIcghsVmbpXcYQj6cKBvUqF8xTurxlbB3NsIvqbFYV3sJX2nkDcTcdVmz5Ne7BK62fgpycM51udsyCT+i3WDRiDifIahU2fDOk+IPEomMgUDfspxCoZRPluUVJOmppBdEvPf5Wno+6szHEQKBgQCNfJ3YErEJt+Cqrj4lDJx8MH1lL3eg1Sn4IczJEBzoMqxtasgUXp5RMaruTO3VImDVdtXeAshkdjqLileBXNOCs8+68+fa5UTpMYRuH9IFpeiyYp7iODU7hxlKDx3acy43VCDs5uo8rMXk4uIrxf7tx6h+8mffFTVCImtY7YIL8QKBgDGMcXXFuTAF/PLEv9w2kH7vSsFLKcHrhlp+ljU5k2WemRj8f7Te+Ms/P4u8wXAWuYkJu5CfosTKTTliR47njFb9QL46vkvfu7WYE5aASeFtLFgC/JqLgpY3LNvf43mtm26bp5XK0YswCtiJ2/WE2/PjulqpX0tfunMjJEqf6UdR";
        String CHARSET = "";
        String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnlwgtNDjPuTKOL0KZ3ekYGzzh6ZWSVkmz+n+iHWFODG3qUcFkX5ca1o4IJGrRS3UgBBOUisfVjRYIV1qvbg832iPKbC7VQt5dWyAP7+IB0gstnoErZxYisLgkvfsvgwrX6Zm665ImD34ScUqaqGSDXur0jipceI5aQbYhqGLbxmxwYu+1D0KdlOmPirUcTgNC/nYnOKnRj1evxlO2TRFWEf53IzEXhHKivOOE63qM/xZIc23J6xYYTOPKVAp169AcOGOakgrZ40umw0A8CgrTeCxfioLSnYWjSkYJN6/fsMOW+vear/MVJe7kWPLwyvZm3C8d/JGaLlFLv5RNHFq+QIDAQAB";
        String outtradeno = "201712220000401";
        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, "RSA2");
//实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
//SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody("我是测试数据");
        model.setSubject("App支付测试Java");
        model.setOutTradeNo(outtradeno);
        model.setTimeoutExpress("20m");
        model.setTotalAmount("0.01");
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);
        request.setNotifyUrl("http://www.dlztc.com/calllback");
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            System.out.println(response.getBody());//就是orderString 可以直接给客户端请求，无需再做处理。
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }



    public void appPayDev() {

        String APP_ID = "2016082700320155";
        String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCa4S0SpDSk4CscDbFbtQUvzwVlKf8tNh4lR1adSv6SD4UCjiiX7+t7NdXrf3Jfgf7lNQqmo9ijHsRgi6U6Ohtjb71XjlAioDkj+6MILGTJfUQKdKws8B3my1kQEosLIHNTfuCPsRTIpzZ67pXoRjPLs4CQz/4kf4Tl7VP0dcLkb3288j0SjTv+WCoqwG6HwUHe2+YAufyOLlrM6Y+AiIET6sX59CAmtPBDsk3cn6A8CQFA92UqlEA7aH84NXORgl6cPHGd/KYgJf1/u3OTa+Cf+ElODY+jrcQ4qyWAWixBk6zIgxv6OP0MaQ/Ot86kwOfqa5SmaQC/vFMls7KDNsIVAgMBAAECggEAFMBo9p9zwkea3xZiliQJ/t7hHS3kV91xapNFYqvishVMQAxmGf12crrC39viqjNTwMg7lhpL84cyzrDHQab5dnhJSUrzJ8CKB2026ehw1KJpFHGPhDHj/8FtPH8PjD0b1qsgUYBrhXsKDKyc/1Mwq3Fwn3aO2bsQRnJnFlEU/OBRnW9JaYHLBwKzhtba48DSjV10YEsyMJ58+k7phPqZ4ogwgydHZxDN4v5k2ZkSdn2k2amVsh2fDFSuODxvEyXhw5GIa2PvDr7KdZR8gILS7sh1nQTKWMG9MCHtBQJFvlGelZXxoKXAOS8HLTbuSqX5ApthX79HqvTutlQzzjjPQQKBgQDgdtvXGuAk2b/2bw+IqQ8ka4w/Kp3Q3i6Krz9/CupSZPnW16te8w7/XwukAhAX84CZVXqT+7WZd+RnAa6F5UDAfMQ0U3mKWRjsIsBFwx5rqbe2Ym4UT8KztRzzS/g/fHngQsnrzM7ovHbYu+KQ1zd1coe+OXadQ57HztFD718EEQKBgQCwo52tObuX69cDUm844eHFzvh9Pbw9iPi9hYru2SVJCNl2xt0hsOMbbz2ClXBsZVVvX9Pykq7r+fRDN6Ufa+o/AopYJN09OfNoDlNd8l+5MYK2Yv8JlSl8CXwlO6mXn5ksyEufLZQJFwfOwQYtDTr5lFym179bderThEeQEBuRxQKBgDaZEq5GzkCIaqYBq7CdFp7QPiPbNNnqQT3glLxNJmP2RcMZYIjO7FX3g3hYPBvnUd96KD+4mRqETB02DoJpGg/4CS2FJGofc+10InqVlF+xv4rwdEAiioR1yF3xm6etmmOPO1hM0ANSXEIpqVl7z/SUcCo4Bf8IxKVrTAGPRvhRAoGAK7CshXYsMk2BJy8yl203fMfOqpukfcvtSmNHlABduozXzQsvEvA/nD+NhUkVP5po4V1gfTVO0stGYYHX88erbt5f/aFQn54/2FMCx8/1YUcfv6EI2APu+OEXtdXmArzVFECOg9awMPdjVP5lWqtVFPpfWF0w1Zx68spbMQTq7MkCgYEAupILj/k1RN1ffj6i2rDVuSdMKFcC3EygL3sP9xgsawz6hjeHVkha+FdkzCkkxsf/ZR7gXRsxssGVflxB/y3WX8xFUy+v9eqEI5oqeb/dbsxWKe6R8gEbGLtY6CLpq7Yt8GyqmaPzni3XoqC7zP5xhiLa8fhUWu8J6z8aUD+u6w0=";
        String CHARSET = "UTF-8";
        String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAz58xBmYbiwo3wBk8AC/garRYKMv4gseiGoKgQ+E614ATXmRCNURbqXC+x0zuMinL4SBj90sOPMP1IWA+mGY1SFTLrev0VzZOx5+mqgk3213avpgSEemto5flq79N7WFNPfHogJVr/F715e1YY3+/0gEm7wyBQTVIg82cyeXttKdL2il/iBgwFDN6/OUQtSCG0jWDTAyv3umpVafadWT0mYM4CIE9v1bbGBrH+FuegOwM7X9Ir0wALyJKiX3PjpRtHR0ufOPc/EEXdKVjDQ+tKBHbIjJYnNKLxmh5fcQ9CEcGbvZeX0I4+pxmGtzyZc8PbGxFa+NnvgqpHib560P2RwIDAQAB";
        String outtradeno = "T201712220000404";
        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipaydev.com/gateway.do", APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, "RSA2");
//实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
//SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody("我是测试数据");
        model.setSubject("App支付测试Java");
        model.setOutTradeNo(outtradeno);
        model.setTimeoutExpress("30m");
        model.setTotalAmount("120");
        model.setProductCode("QUICK_MSECURITY_PAY");
//        model.setPassbackParams(URLEncoder.encode("回调参数", "UTF-8"));
        request.setBizModel(model);
        request.setNotifyUrl("http://pay.dlztc.com/alipay/callback");
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            System.out.println(response.getBody());//就是orderString 可以直接给客户端请求，无需再做处理。
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }

    public void queryTrade() {
        String APP_ID = "2016082700320155";
        String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCa4S0SpDSk4CscDbFbtQUvzwVlKf8tNh4lR1adSv6SD4UCjiiX7+t7NdXrf3Jfgf7lNQqmo9ijHsRgi6U6Ohtjb71XjlAioDkj+6MILGTJfUQKdKws8B3my1kQEosLIHNTfuCPsRTIpzZ67pXoRjPLs4CQz/4kf4Tl7VP0dcLkb3288j0SjTv+WCoqwG6HwUHe2+YAufyOLlrM6Y+AiIET6sX59CAmtPBDsk3cn6A8CQFA92UqlEA7aH84NXORgl6cPHGd/KYgJf1/u3OTa+Cf+ElODY+jrcQ4qyWAWixBk6zIgxv6OP0MaQ/Ot86kwOfqa5SmaQC/vFMls7KDNsIVAgMBAAECggEAFMBo9p9zwkea3xZiliQJ/t7hHS3kV91xapNFYqvishVMQAxmGf12crrC39viqjNTwMg7lhpL84cyzrDHQab5dnhJSUrzJ8CKB2026ehw1KJpFHGPhDHj/8FtPH8PjD0b1qsgUYBrhXsKDKyc/1Mwq3Fwn3aO2bsQRnJnFlEU/OBRnW9JaYHLBwKzhtba48DSjV10YEsyMJ58+k7phPqZ4ogwgydHZxDN4v5k2ZkSdn2k2amVsh2fDFSuODxvEyXhw5GIa2PvDr7KdZR8gILS7sh1nQTKWMG9MCHtBQJFvlGelZXxoKXAOS8HLTbuSqX5ApthX79HqvTutlQzzjjPQQKBgQDgdtvXGuAk2b/2bw+IqQ8ka4w/Kp3Q3i6Krz9/CupSZPnW16te8w7/XwukAhAX84CZVXqT+7WZd+RnAa6F5UDAfMQ0U3mKWRjsIsBFwx5rqbe2Ym4UT8KztRzzS/g/fHngQsnrzM7ovHbYu+KQ1zd1coe+OXadQ57HztFD718EEQKBgQCwo52tObuX69cDUm844eHFzvh9Pbw9iPi9hYru2SVJCNl2xt0hsOMbbz2ClXBsZVVvX9Pykq7r+fRDN6Ufa+o/AopYJN09OfNoDlNd8l+5MYK2Yv8JlSl8CXwlO6mXn5ksyEufLZQJFwfOwQYtDTr5lFym179bderThEeQEBuRxQKBgDaZEq5GzkCIaqYBq7CdFp7QPiPbNNnqQT3glLxNJmP2RcMZYIjO7FX3g3hYPBvnUd96KD+4mRqETB02DoJpGg/4CS2FJGofc+10InqVlF+xv4rwdEAiioR1yF3xm6etmmOPO1hM0ANSXEIpqVl7z/SUcCo4Bf8IxKVrTAGPRvhRAoGAK7CshXYsMk2BJy8yl203fMfOqpukfcvtSmNHlABduozXzQsvEvA/nD+NhUkVP5po4V1gfTVO0stGYYHX88erbt5f/aFQn54/2FMCx8/1YUcfv6EI2APu+OEXtdXmArzVFECOg9awMPdjVP5lWqtVFPpfWF0w1Zx68spbMQTq7MkCgYEAupILj/k1RN1ffj6i2rDVuSdMKFcC3EygL3sP9xgsawz6hjeHVkha+FdkzCkkxsf/ZR7gXRsxssGVflxB/y3WX8xFUy+v9eqEI5oqeb/dbsxWKe6R8gEbGLtY6CLpq7Yt8GyqmaPzni3XoqC7zP5xhiLa8fhUWu8J6z8aUD+u6w0=";
        String CHARSET = "UTF-8";
        String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAz58xBmYbiwo3wBk8AC/garRYKMv4gseiGoKgQ+E614ATXmRCNURbqXC+x0zuMinL4SBj90sOPMP1IWA+mGY1SFTLrev0VzZOx5+mqgk3213avpgSEemto5flq79N7WFNPfHogJVr/F715e1YY3+/0gEm7wyBQTVIg82cyeXttKdL2il/iBgwFDN6/OUQtSCG0jWDTAyv3umpVafadWT0mYM4CIE9v1bbGBrH+FuegOwM7X9Ir0wALyJKiX3PjpRtHR0ufOPc/EEXdKVjDQ+tKBHbIjJYnNKLxmh5fcQ9CEcGbvZeX0I4+pxmGtzyZc8PbGxFa+NnvgqpHib560P2RwIDAQAB";
        String outtradeno = "T201712220000402";
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipaydev.com/gateway.do", APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, "RSA2"); //获得初始化的AlipayClient
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();//创建API对应的request类
        request.setBizContent("{" +
                "   \"out_trade_no\":\"T201712220000402\"," +
                "   \"trade_no\":\"2017122521001004680200220425\"" +
                "  }");//设置业务参数
        try {
            AlipayTradeQueryResponse response = alipayClient.execute(request);//通过alipayClient调用API，获得对应的response类
            System.out.print(response.getBody());
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }

    public void tuikuan() {
        String APP_ID = "2016082700320155";
        String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCa4S0SpDSk4CscDbFbtQUvzwVlKf8tNh4lR1adSv6SD4UCjiiX7+t7NdXrf3Jfgf7lNQqmo9ijHsRgi6U6Ohtjb71XjlAioDkj+6MILGTJfUQKdKws8B3my1kQEosLIHNTfuCPsRTIpzZ67pXoRjPLs4CQz/4kf4Tl7VP0dcLkb3288j0SjTv+WCoqwG6HwUHe2+YAufyOLlrM6Y+AiIET6sX59CAmtPBDsk3cn6A8CQFA92UqlEA7aH84NXORgl6cPHGd/KYgJf1/u3OTa+Cf+ElODY+jrcQ4qyWAWixBk6zIgxv6OP0MaQ/Ot86kwOfqa5SmaQC/vFMls7KDNsIVAgMBAAECggEAFMBo9p9zwkea3xZiliQJ/t7hHS3kV91xapNFYqvishVMQAxmGf12crrC39viqjNTwMg7lhpL84cyzrDHQab5dnhJSUrzJ8CKB2026ehw1KJpFHGPhDHj/8FtPH8PjD0b1qsgUYBrhXsKDKyc/1Mwq3Fwn3aO2bsQRnJnFlEU/OBRnW9JaYHLBwKzhtba48DSjV10YEsyMJ58+k7phPqZ4ogwgydHZxDN4v5k2ZkSdn2k2amVsh2fDFSuODxvEyXhw5GIa2PvDr7KdZR8gILS7sh1nQTKWMG9MCHtBQJFvlGelZXxoKXAOS8HLTbuSqX5ApthX79HqvTutlQzzjjPQQKBgQDgdtvXGuAk2b/2bw+IqQ8ka4w/Kp3Q3i6Krz9/CupSZPnW16te8w7/XwukAhAX84CZVXqT+7WZd+RnAa6F5UDAfMQ0U3mKWRjsIsBFwx5rqbe2Ym4UT8KztRzzS/g/fHngQsnrzM7ovHbYu+KQ1zd1coe+OXadQ57HztFD718EEQKBgQCwo52tObuX69cDUm844eHFzvh9Pbw9iPi9hYru2SVJCNl2xt0hsOMbbz2ClXBsZVVvX9Pykq7r+fRDN6Ufa+o/AopYJN09OfNoDlNd8l+5MYK2Yv8JlSl8CXwlO6mXn5ksyEufLZQJFwfOwQYtDTr5lFym179bderThEeQEBuRxQKBgDaZEq5GzkCIaqYBq7CdFp7QPiPbNNnqQT3glLxNJmP2RcMZYIjO7FX3g3hYPBvnUd96KD+4mRqETB02DoJpGg/4CS2FJGofc+10InqVlF+xv4rwdEAiioR1yF3xm6etmmOPO1hM0ANSXEIpqVl7z/SUcCo4Bf8IxKVrTAGPRvhRAoGAK7CshXYsMk2BJy8yl203fMfOqpukfcvtSmNHlABduozXzQsvEvA/nD+NhUkVP5po4V1gfTVO0stGYYHX88erbt5f/aFQn54/2FMCx8/1YUcfv6EI2APu+OEXtdXmArzVFECOg9awMPdjVP5lWqtVFPpfWF0w1Zx68spbMQTq7MkCgYEAupILj/k1RN1ffj6i2rDVuSdMKFcC3EygL3sP9xgsawz6hjeHVkha+FdkzCkkxsf/ZR7gXRsxssGVflxB/y3WX8xFUy+v9eqEI5oqeb/dbsxWKe6R8gEbGLtY6CLpq7Yt8GyqmaPzni3XoqC7zP5xhiLa8fhUWu8J6z8aUD+u6w0=";
        String CHARSET = "UTF-8";
        String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAz58xBmYbiwo3wBk8AC/garRYKMv4gseiGoKgQ+E614ATXmRCNURbqXC+x0zuMinL4SBj90sOPMP1IWA+mGY1SFTLrev0VzZOx5+mqgk3213avpgSEemto5flq79N7WFNPfHogJVr/F715e1YY3+/0gEm7wyBQTVIg82cyeXttKdL2il/iBgwFDN6/OUQtSCG0jWDTAyv3umpVafadWT0mYM4CIE9v1bbGBrH+FuegOwM7X9Ir0wALyJKiX3PjpRtHR0ufOPc/EEXdKVjDQ+tKBHbIjJYnNKLxmh5fcQ9CEcGbvZeX0I4+pxmGtzyZc8PbGxFa+NnvgqpHib560P2RwIDAQAB";

        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipaydev.com/gateway.do", APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, "RSA2"); //获得初始化的AlipayClient
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();//创建API对应的request类
        request.setBizContent("{" +
                "    \"out_trade_no\":\"5120180100000098\"," +
                "    \"trade_no\":\"2018011221001004680200227566\"," +
//                "    \"out_request_no\":\"1000001\"," +//部分退款流水好
                "    \"refund_amount\":\"200\"" +
                "  }");//设置业务参数
        try {
            AlipayTradeRefundResponse response = alipayClient.execute(request);//通过alipayClient调用API，获得对应的response类
            System.out.print(response.getBody());
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }


    public void duizhangdan() {
//        String APP_ID = "2016082700320155";
//        String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCa4S0SpDSk4CscDbFbtQUvzwVlKf8tNh4lR1adSv6SD4UCjiiX7+t7NdXrf3Jfgf7lNQqmo9ijHsRgi6U6Ohtjb71XjlAioDkj+6MILGTJfUQKdKws8B3my1kQEosLIHNTfuCPsRTIpzZ67pXoRjPLs4CQz/4kf4Tl7VP0dcLkb3288j0SjTv+WCoqwG6HwUHe2+YAufyOLlrM6Y+AiIET6sX59CAmtPBDsk3cn6A8CQFA92UqlEA7aH84NXORgl6cPHGd/KYgJf1/u3OTa+Cf+ElODY+jrcQ4qyWAWixBk6zIgxv6OP0MaQ/Ot86kwOfqa5SmaQC/vFMls7KDNsIVAgMBAAECggEAFMBo9p9zwkea3xZiliQJ/t7hHS3kV91xapNFYqvishVMQAxmGf12crrC39viqjNTwMg7lhpL84cyzrDHQab5dnhJSUrzJ8CKB2026ehw1KJpFHGPhDHj/8FtPH8PjD0b1qsgUYBrhXsKDKyc/1Mwq3Fwn3aO2bsQRnJnFlEU/OBRnW9JaYHLBwKzhtba48DSjV10YEsyMJ58+k7phPqZ4ogwgydHZxDN4v5k2ZkSdn2k2amVsh2fDFSuODxvEyXhw5GIa2PvDr7KdZR8gILS7sh1nQTKWMG9MCHtBQJFvlGelZXxoKXAOS8HLTbuSqX5ApthX79HqvTutlQzzjjPQQKBgQDgdtvXGuAk2b/2bw+IqQ8ka4w/Kp3Q3i6Krz9/CupSZPnW16te8w7/XwukAhAX84CZVXqT+7WZd+RnAa6F5UDAfMQ0U3mKWRjsIsBFwx5rqbe2Ym4UT8KztRzzS/g/fHngQsnrzM7ovHbYu+KQ1zd1coe+OXadQ57HztFD718EEQKBgQCwo52tObuX69cDUm844eHFzvh9Pbw9iPi9hYru2SVJCNl2xt0hsOMbbz2ClXBsZVVvX9Pykq7r+fRDN6Ufa+o/AopYJN09OfNoDlNd8l+5MYK2Yv8JlSl8CXwlO6mXn5ksyEufLZQJFwfOwQYtDTr5lFym179bderThEeQEBuRxQKBgDaZEq5GzkCIaqYBq7CdFp7QPiPbNNnqQT3glLxNJmP2RcMZYIjO7FX3g3hYPBvnUd96KD+4mRqETB02DoJpGg/4CS2FJGofc+10InqVlF+xv4rwdEAiioR1yF3xm6etmmOPO1hM0ANSXEIpqVl7z/SUcCo4Bf8IxKVrTAGPRvhRAoGAK7CshXYsMk2BJy8yl203fMfOqpukfcvtSmNHlABduozXzQsvEvA/nD+NhUkVP5po4V1gfTVO0stGYYHX88erbt5f/aFQn54/2FMCx8/1YUcfv6EI2APu+OEXtdXmArzVFECOg9awMPdjVP5lWqtVFPpfWF0w1Zx68spbMQTq7MkCgYEAupILj/k1RN1ffj6i2rDVuSdMKFcC3EygL3sP9xgsawz6hjeHVkha+FdkzCkkxsf/ZR7gXRsxssGVflxB/y3WX8xFUy+v9eqEI5oqeb/dbsxWKe6R8gEbGLtY6CLpq7Yt8GyqmaPzni3XoqC7zP5xhiLa8fhUWu8J6z8aUD+u6w0=";
//        String CHARSET = "UTF-8";
//        String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAz58xBmYbiwo3wBk8AC/garRYKMv4gseiGoKgQ+E614ATXmRCNURbqXC+x0zuMinL4SBj90sOPMP1IWA+mGY1SFTLrev0VzZOx5+mqgk3213avpgSEemto5flq79N7WFNPfHogJVr/F715e1YY3+/0gEm7wyBQTVIg82cyeXttKdL2il/iBgwFDN6/OUQtSCG0jWDTAyv3umpVafadWT0mYM4CIE9v1bbGBrH+FuegOwM7X9Ir0wALyJKiX3PjpRtHR0ufOPc/EEXdKVjDQ+tKBHbIjJYnNKLxmh5fcQ9CEcGbvZeX0I4+pxmGtzyZc8PbGxFa+NnvgqpHib560P2RwIDAQAB";

        String APP_ID = "2017121800944206";
        String APP_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCt19qoiQHx6fevZsVpb+2n1e9TqdhdfYcHHeF6PHdl+l8/C/Lkdt6ertzZhCdhh8nN2pO0zIZlLmxTUZHs0pA1j525ow6WIpP3TUqKYBh1MXLOB0bExd20taBZKamm6hFCeJQ3gra5MPV4A2u5j8yJ69jScn9KkKZrUFwQGsjy65jHPt7WYWQoDF6nREPQ6NdxDg6EyMcMCP/dOGMJsh1tS/Gnb10N3Y+IqmHSYCWXBvhb9m/6fKyNhHZ07zDju8x3C0KeGOQ5Fn3jgQUjqexH8FUB3IZesH9vFSgONFN7nbadREVVKzDVNVTsv9QblwhRKLcAj131nmCqelEfSiVZAgMBAAECggEAeVUXktuVFPAUZEKZdB6fw61oTX9UuOO803XCgpsjRnS593nyfAyPEje/gr1e8w5gBiIRR3YcMxB0oK5k5+KKridfkUpCCv11QnyakvvA9kKd+zhO51WmiNLfaHvQoePqSfd4k0nTGGwYVzwj+q5ntrd6bF9ngnZ1AJ9ksgTPzeMC6+Rvwsvlz4CcGU3keBn37+vhBGAeOWdF9ZWIK28I5/RCyYqqkSinOmK+S5T/caTjWfNk2kjlE6+RJhbiVUoLDRtxGVSStQl3Ugqvkw63XgeDfNsjAEv30gSKrV6LuPowsNILQiKy3zg+rLiW9JZ3hA8toZT/FvB33J/UD+2DgQKBgQDuDz9Co3CZYQVV2Zyr7QII1ofmUGAYH0KlVFEtm1xG43SJNXJd+Dpvw2BilqrWnPj+pFobl6wt697ARutWaO9qCL67voWA/7P7RBk5zhv2bRQzVE1qBy2oIxToyQgfbb/X5RzL4acXVe3k48wDVF8XO2iSLdV1EE0WLVtlzRR76QKBgQC68bct4Wuox9p7Jo2b5cS5BfLeRq51wFs8XoQxWHe9umuMnMiu5UNwtGpCInzcGD9BMxZOTG3ONUTFiJNXC4zSDolADbj8eGt39rKV5HiSqPAPAVU9x6W4GzwRE0bN2AEWI1r961HhTnVH8h8ylez+z5BOAsVHTq8dm6nzB3kn8QKBgQDkyw6XcxEiuBYynkRvEAgmilhuR80zIcghsVmbpXcYQj6cKBvUqF8xTurxlbB3NsIvqbFYV3sJX2nkDcTcdVmz5Ne7BK62fgpycM51udsyCT+i3WDRiDifIahU2fDOk+IPEomMgUDfspxCoZRPluUVJOmppBdEvPf5Wno+6szHEQKBgQCNfJ3YErEJt+Cqrj4lDJx8MH1lL3eg1Sn4IczJEBzoMqxtasgUXp5RMaruTO3VImDVdtXeAshkdjqLileBXNOCs8+68+fa5UTpMYRuH9IFpeiyYp7iODU7hxlKDx3acy43VCDs5uo8rMXk4uIrxf7tx6h+8mffFTVCImtY7YIL8QKBgDGMcXXFuTAF/PLEv9w2kH7vSsFLKcHrhlp+ljU5k2WemRj8f7Te+Ms/P4u8wXAWuYkJu5CfosTKTTliR47njFb9QL46vkvfu7WYE5aASeFtLFgC/JqLgpY3LNvf43mtm26bp5XK0YswCtiJ2/WE2/PjulqpX0tfunMjJEqf6UdR";
        String CHARSET = "UTF-8";
        String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnlwgtNDjPuTKOL0KZ3ekYGzzh6ZWSVkmz+n+iHWFODG3qUcFkX5ca1o4IJGrRS3UgBBOUisfVjRYIV1qvbg832iPKbC7VQt5dWyAP7+IB0gstnoErZxYisLgkvfsvgwrX6Zm665ImD34ScUqaqGSDXur0jipceI5aQbYhqGLbxmxwYu+1D0KdlOmPirUcTgNC/nYnOKnRj1evxlO2TRFWEf53IzEXhHKivOOE63qM/xZIc23J6xYYTOPKVAp169AcOGOakgrZ40umw0A8CgrTeCxfioLSnYWjSkYJN6/fsMOW+vear/MVJe7kWPLwyvZm3C8d/JGaLlFLv5RNHFq+QIDAQAB";

        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, "RSA2"); //获得初始化的AlipayClient
        AlipayDataDataserviceBillDownloadurlQueryRequest request = new AlipayDataDataserviceBillDownloadurlQueryRequest();//创建API对应的request类
        request.setBizContent("{" +
                "    \"bill_type\":\"trade\"," +
                "    \"bill_date\":\"2017-12-25\"" +
                "  }");//设置业务参数
        try {
            AlipayDataDataserviceBillDownloadurlQueryResponse response = alipayClient.execute(request);//通过alipayClient调用API，获得对应的response类
            if ("10000".equals(response.getCode())) {
                //将接口返回的对账单下载地址传入urlStr
                String urlStr = response.getBillDownloadUrl();//"http://dwbillcenter.alipay.com/downloadBillFile.resource?bizType=X&userId=X&fileType=X&bizDates=X&downloadFileName=X&fileId=X";
//指定希望保存的文件路径
                String filePath = "d:/test0002.zip";
                URL url = null;
                HttpURLConnection httpUrlConnection = null;
                InputStream fis = null;
                FileOutputStream fos = null;
                try {
                    url = new URL(urlStr);
                    httpUrlConnection = (HttpURLConnection) url.openConnection();
                    httpUrlConnection.setConnectTimeout(5 * 1000);
                    httpUrlConnection.setDoInput(true);
                    httpUrlConnection.setDoOutput(true);
                    httpUrlConnection.setUseCaches(false);
                    httpUrlConnection.setRequestMethod("GET");
                    httpUrlConnection.setRequestProperty("Charsert", "UTF-8");
                    httpUrlConnection.connect();
                    fis = httpUrlConnection.getInputStream();
                    byte[] temp = new byte[1024];
                    int b;
                    fos = new FileOutputStream(new File(filePath));
                    while ((b = fis.read(temp)) != -1) {
                        fos.write(temp, 0, b);
                        fos.flush();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if(fis!=null) fis.close();
                        if(fos!=null) fos.close();
                        if(httpUrlConnection!=null) httpUrlConnection.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
//            System.out.println(response.getBillDownloadUrl());
//            System.out.println(response.getBody());
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }

    public void getAuthToken() {
        String APP_ID = "2016082700320155";
        String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCa4S0SpDSk4CscDbFbtQUvzwVlKf8tNh4lR1adSv6SD4UCjiiX7+t7NdXrf3Jfgf7lNQqmo9ijHsRgi6U6Ohtjb71XjlAioDkj+6MILGTJfUQKdKws8B3my1kQEosLIHNTfuCPsRTIpzZ67pXoRjPLs4CQz/4kf4Tl7VP0dcLkb3288j0SjTv+WCoqwG6HwUHe2+YAufyOLlrM6Y+AiIET6sX59CAmtPBDsk3cn6A8CQFA92UqlEA7aH84NXORgl6cPHGd/KYgJf1/u3OTa+Cf+ElODY+jrcQ4qyWAWixBk6zIgxv6OP0MaQ/Ot86kwOfqa5SmaQC/vFMls7KDNsIVAgMBAAECggEAFMBo9p9zwkea3xZiliQJ/t7hHS3kV91xapNFYqvishVMQAxmGf12crrC39viqjNTwMg7lhpL84cyzrDHQab5dnhJSUrzJ8CKB2026ehw1KJpFHGPhDHj/8FtPH8PjD0b1qsgUYBrhXsKDKyc/1Mwq3Fwn3aO2bsQRnJnFlEU/OBRnW9JaYHLBwKzhtba48DSjV10YEsyMJ58+k7phPqZ4ogwgydHZxDN4v5k2ZkSdn2k2amVsh2fDFSuODxvEyXhw5GIa2PvDr7KdZR8gILS7sh1nQTKWMG9MCHtBQJFvlGelZXxoKXAOS8HLTbuSqX5ApthX79HqvTutlQzzjjPQQKBgQDgdtvXGuAk2b/2bw+IqQ8ka4w/Kp3Q3i6Krz9/CupSZPnW16te8w7/XwukAhAX84CZVXqT+7WZd+RnAa6F5UDAfMQ0U3mKWRjsIsBFwx5rqbe2Ym4UT8KztRzzS/g/fHngQsnrzM7ovHbYu+KQ1zd1coe+OXadQ57HztFD718EEQKBgQCwo52tObuX69cDUm844eHFzvh9Pbw9iPi9hYru2SVJCNl2xt0hsOMbbz2ClXBsZVVvX9Pykq7r+fRDN6Ufa+o/AopYJN09OfNoDlNd8l+5MYK2Yv8JlSl8CXwlO6mXn5ksyEufLZQJFwfOwQYtDTr5lFym179bderThEeQEBuRxQKBgDaZEq5GzkCIaqYBq7CdFp7QPiPbNNnqQT3glLxNJmP2RcMZYIjO7FX3g3hYPBvnUd96KD+4mRqETB02DoJpGg/4CS2FJGofc+10InqVlF+xv4rwdEAiioR1yF3xm6etmmOPO1hM0ANSXEIpqVl7z/SUcCo4Bf8IxKVrTAGPRvhRAoGAK7CshXYsMk2BJy8yl203fMfOqpukfcvtSmNHlABduozXzQsvEvA/nD+NhUkVP5po4V1gfTVO0stGYYHX88erbt5f/aFQn54/2FMCx8/1YUcfv6EI2APu+OEXtdXmArzVFECOg9awMPdjVP5lWqtVFPpfWF0w1Zx68spbMQTq7MkCgYEAupILj/k1RN1ffj6i2rDVuSdMKFcC3EygL3sP9xgsawz6hjeHVkha+FdkzCkkxsf/ZR7gXRsxssGVflxB/y3WX8xFUy+v9eqEI5oqeb/dbsxWKe6R8gEbGLtY6CLpq7Yt8GyqmaPzni3XoqC7zP5xhiLa8fhUWu8J6z8aUD+u6w0=";
        String CHARSET = "UTF-8";
        String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAz58xBmYbiwo3wBk8AC/garRYKMv4gseiGoKgQ+E614ATXmRCNURbqXC+x0zuMinL4SBj90sOPMP1IWA+mGY1SFTLrev0VzZOx5+mqgk3213avpgSEemto5flq79N7WFNPfHogJVr/F715e1YY3+/0gEm7wyBQTVIg82cyeXttKdL2il/iBgwFDN6/OUQtSCG0jWDTAyv3umpVafadWT0mYM4CIE9v1bbGBrH+FuegOwM7X9Ir0wALyJKiX3PjpRtHR0ufOPc/EEXdKVjDQ+tKBHbIjJYnNKLxmh5fcQ9CEcGbvZeX0I4+pxmGtzyZc8PbGxFa+NnvgqpHib560P2RwIDAQAB";

        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, "RSA2"); //获得初始化的AlipayClient
        AlipayOpenAuthTokenAppRequest request = new AlipayOpenAuthTokenAppRequest();
        request.setBizContent("{" +
                "\"grant_type\":\"authorization_code\"," +//authorization_code或者refresh_token
                "\"code\":\"1cc19911172e4f8aaa509c8fb5d12F56\"" +
//                "\"refresh_token\":\"201509BBdcba1e3347de4e75ba3fed2c9abebE36\"" +
                "  }");
        try {
            AlipayOpenAuthTokenAppResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                System.out.println(response.getBody());
                System.out.println("调用成功");
            } else {
                System.out.println(response.getBody());
                System.out.println("调用失败");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) throws Exception {
//        Demo1.checkSignAndDecrypt();
//
//        Demo1.encryptAndSign();

        Demo1 d = new Demo1();
//        d.appPay();
//        d.appPayDev();
//        d.queryTrade();
        d.tuikuan();
//        d.duizhangdan();
//        d.getAuthToken();
    }
}
