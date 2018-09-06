package com.aizhixin.cloud.pay;

import com.aizhixin.cloud.paycallback.common.service.AESCryptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;

public class AliyPaymentParamTest {
    public static void main(String[] args) throws IOException {
        AESCryptor aesCryptor = new AESCryptor();
        ObjectMapper mapper = new ObjectMapper();
        aesCryptor.setKey("F78726AD90A8FE8E945791AB86BB2AEF");
        String json = "{\"user_no\":\"000411200112110222\",\"status\":1,\"page_no\":1,\"page_size\":15}";
        String mw = aesCryptor.encrypt(json);
        System.out.println(mw);
        StringBuilder sb = new StringBuilder();
        sb.append("ZxCjPay");
        sb.append(mw);
        sb.append("*tp6QrZbyYUUERSC");
        System.out.println(DigestUtils.md5Hex(sb.toString()));

        System.out.println(aesCryptor.decrypt("uXBIQL7r0CrEMivFFodPXfx6IjlGIJmi0ZgdJCVixHk/UgW/rvQkhA29TPG8rQm31uy/aR/MHNACAd/iXlql/NaqsMnkocXQJsTiVUkXVyI="));
//        PaymentOrderDomain order = new PaymentOrderDomain();
//        order.setUser_no("000411200112110222");
//        order.setPay_type("AL");
//        order.setInfo("success");
//        order.setOrder_id("T000012342ADSDAW01");
//        order.setOrder_end_date("2018-03-12 17:39:23");
//        order.setTotal_fee("1000");
//        List<PaymentOrderItemListDomain> data = new ArrayList<>();
//
//        PaymentOrderItemListDomain item = new PaymentOrderItemListDomain();
//        item.setItem_id("9958cec6-b3a6-4136-95d6-f4e7dec144bd");
//        item.setFee("1000");
//        item.setName("电信学院2018年第二学期学杂费");
//        data.add(item);
//
//        order.setData(data);
//
//        json = mapper.writeValueAsString(order);
//        System.out.println("3入参明文:" + json);
//
//        mw = aesCryptor.encrypt(json);
//        sb = new StringBuilder();
//        sb.append("ZxCjPay");
//        sb.append(mw);
//        sb.append("*tp6QrZbyYUUERSC");
//        System.out.println(mw);
//        System.out.println(DigestUtils.md5Hex(sb.toString()));
//
////        mw = aesCryptor.decrypt("EoBHRk5I0VaQdNJgNF+F6tUJKMnEDjwG+0QVRPvmQLt2ONiOweDakvSfnKU6a3ug/bgpU4eGqTkBYBGqvgXiPYjmjI0OGEDBM+9++Dt+DamJ6+PR73aCBLGvZtfxTS6vEakK6LV3fDam8DW3UNdw3A==");
////        System.out.println(mw);
//
//        json = "{\"user_no\":\"000411200112110222\",\"pay_type\":\"AL\",\"order_id\":\"T2000012342ADSDAW02\",\"order_start_date\":\"2018-03-14 14:25:23\",\"order_end_date\":\"2018-03-14 14:25:23\",\"total_fee\":\"1000\",\"info\":\"success\",\"data\":[{\"item_id\":\"892fea76-8c11-4a73-b37e-a57e252b378c\",\"name\":\"" + gbEncoding("农学系农学专业2018年第二学期学杂费") + "\",\"fee\":\"1000\"}]}";
//        mw = aesCryptor.encrypt(json);
//        System.out.println(mw);
//        sb = new StringBuilder();
//        sb.append("ZxCjPay");
//        sb.append(mw);
//        sb.append("*tp6QrZbyYUUERSC");
//        System.out.println(DigestUtils.md5Hex(sb.toString()));
//        System.out.println(gbEncoding("张三"));
//        String json = aesCryptor.decrypt("G81fG3EkEm+lLWxdi3rSS80ODKh8hnrt8VkIw7m27GKkdUyHmohnEw33ze0uqmQd");
//        System.out.println(json);
//        String user_no = null;
//        String key = null;
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode root = mapper.readTree(json);
//        if (root.has("user_no")) {
//            user_no = root.get("user_no").asText();
//        }
//        if (root.has("key")) {
//            key = root.get("key").asText();
//        }
//        System.out.println("user_no:" + user_no + "\tkey:" + key);
//
//        StringBuilder sb = new StringBuilder();
//        sb.append("0").append("Nbf7EAPVe+Sivs0VoQ8H4rUYXDqBFY1+TWUVipfNFzBkUhgoCLuEq80JuTl90dQuyDNRGi8SwUKGz5W31nHEjNOVjad0ang5eNSHGqLV8MHl2o0PKEQoNmigrjUBZ0SX").append("weiweixiao");
//        System.out.println(DigestUtils.md5Hex(sb.toString()));
//
//        sb = new StringBuilder();
//        sb.append("partner1").append("G81fG3EkEm+lLWxdi3rSS80ODKh8hnrt8VkIw7m27GKkdUyHmohnEw33ze0uqmQd").append("weiweixiao");
//        System.out.println(DigestUtils.md5Hex(sb.toString()));
//
//        FeeUserInfoDomain d = new FeeUserInfoDomain ("小鲁", "20170805352", "第五专业");
//        d.setId_card("444");
//        json = mapper.writeValueAsString(d);
//        System.out.println(json);
//        System.out.println(aesCryptor.encrypt(json));
    }

    public static String gbEncoding(final String gbString) {   //gbString = "测试"
        char[] utfBytes = gbString.toCharArray();   //utfBytes = [测, 试]
        String unicodeBytes = "";
        for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
            String hexB = Integer.toHexString(utfBytes[byteIndex]);   //转换为16进制整型字符串
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        System.out.println("unicodeBytes is: " + unicodeBytes);
        return unicodeBytes;
    }
}
