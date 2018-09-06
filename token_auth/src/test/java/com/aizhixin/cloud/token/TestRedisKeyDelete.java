package com.aizhixin.cloud.token;

import java.io.IOException;
import java.util.Base64;

/**
 * Created by zhen.pan on 2017/6/8.
 */
public class TestRedisKeyDelete {
//YWl6aGl4aW5rYWlqdWFuOkFDRUY0NkU4ODY1NlNXb2tqaDZIQjU0
    public static void main(String[] args) throws IOException {
//        Jedis jedis = new Jedis("172.16.23.30");
//        jedis.select(4);
//        System.out.println("Connection to server sucessfully");
//
//        byte[] value = jedis.get(new String("azx:io:doc:7EABD0541E33405DAF3A64D885956A5E").getBytes());
//        System.out.println(value.length);
        // 获取数据并输出
//        Set<String> keys = jedis.keys("*");
//        Iterator<String> it=keys.iterator() ;
//        while(it.hasNext()){
//            String key = it.next();
//            if (key.startsWith("dledu_web_session")) {
//                System.out.println(key);
//                jedis.del(key);
//            }
//        }
//        jedis.close();
//  9451D61278574FECBFBF5B267305D80A
//        DefaultKeyGenerator d = new DefaultKeyGenerator();
//        System.out.println(d.getBase64("aizhixinkaijuan:ACEF46E88656SWokjh6HB54"));
        System.out.println(Base64.getEncoder().encodeToString("aizhixinkaijuan:ACEF46E88656SWokjh6HB54".getBytes("UTF-8")));
    }
}
