package com.aizhixin.test.example;

import com.aizhixin.test.SimpleMetrics;
import com.aizhixin.test.json.JsonUtil;
import com.aizhixin.test.rest.RestUtil;
import com.aizhixin.test.task.Login;
import com.aizhixin.test.thread.BioThreadPool;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class TestSign {
    private final static RestUtil restUtil = new RestUtil();
    private final static String loginUrl = "http://dledutest.aizhixin.com/zhixin_api/oauth/token";
    private ConcurrentLinkedQueue <SimpleMetrics> cacheMetrics = new ConcurrentLinkedQueue <>();
    private CopyOnWriteArrayList <AuthToken> tokens = new CopyOnWriteArrayList <>();

    public void testLogin(BioThreadPool pool, Map <String, String> users) throws InterruptedException {
        for (Map.Entry <String, String> e : users.entrySet()) {
            while (true) {
                if (!pool.addAndExecute(new LoginThread(cacheMetrics, new Login(restUtil, loginUrl, e.getKey(), e.getValue()), tokens))) {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } else {
                    break;
                }
            }
        }
    }

    public void outMetrics(String dir, String fileName) {
        File file = new File(dir, fileName);
        List <SimpleMetrics> metrics = new ArrayList <>();
        SimpleMetrics m = cacheMetrics.poll();
        while (null != m) {
            m.executeTime();
            metrics.add(m);
            m = cacheMetrics.poll();
        }
        JsonUtil.encode(file, metrics);
    }

    public static void main(String[] args) throws InterruptedException {
        TestLogin test = new TestLogin();
        BioThreadPool pool = new BioThreadPool();
        Map <String, String> users = new HashMap <>();

        for (int i = 1001; i < 1019; i++) {
            users.put("lmcs" + i, "1234567");
        }
        test.testLogin(pool, users);
        pool.close();
//        test.outMetrics("d:/", "metrics.json");
    }
}

//class LoginThread extends Thread {
//    private Login login;
//    private ConcurrentLinkedQueue<SimpleMetrics> cacheMetrics;
//    private CopyOnWriteArrayList<AuthToken> users;
//
//    public LoginThread (ConcurrentLinkedQueue<SimpleMetrics> cacheMetrics, Login login, CopyOnWriteArrayList<AuthToken> users) {
//        this.login = login;
//        this.cacheMetrics = cacheMetrics;
//        this.users = users;
//    }
//    @Override
//    public void run() {
//        SimpleMetrics m = new SimpleMetrics("登录", System.currentTimeMillis());
//        String json = login.login();
//        m.setEndtime(System.currentTimeMillis());
//        AuthToken token = JsonUtil.decode(json, AuthToken.class);
//        cacheMetrics.offer(m);
//        users.add(token);
//    }
//}
