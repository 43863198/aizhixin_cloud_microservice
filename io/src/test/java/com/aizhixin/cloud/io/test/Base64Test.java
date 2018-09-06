package com.aizhixin.cloud.io.test;

import java.util.Base64;

/**
 * Created by zhen.pan on 2017/6/14.
 */
public class Base64Test {
    //YWl6aGl4aW5rYWlqdWFuOkFDRUY0NkU4ODY1NlNXb2tqaDZIQjU0
    public static void main(String[] args) {
        System.out.println(Base64.getEncoder().encodeToString("aizhixinkaijuan:ACEF46E88656SWokjh6HB54".getBytes()));
    }
}
