package com.aizhixin.test.example;

import com.aizhixin.test.SimpleMetrics;
import com.aizhixin.test.json.JsonUtil;
import com.aizhixin.test.rest.RestUtil;
import com.aizhixin.test.task.*;
import com.aizhixin.test.thread.BioThreadPool;
import org.springframework.util.StringUtils;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class TestLogin {
    private final static RestUtil restUtil = new RestUtil();
    private final static String LOGIN_URL = "http://dledu.aizhixin.com/zhixin_api/oauth/token";
    private final static String SIGN_URL = "http://gatewaytest.aizhixin.com/diandian_api/api/phone/v1/student/signIn";
    private final static String NULL_URL = "http://gatewaydev.aizhixin.com/diandian_api/api/phone/v1/student/signInNull";
    private final static String NULL_URL2 = "http://114.67.48.139";
    private final static String FREE_TEST_COURSE_URL = "http://em.aizhixin.com/em_api2/api/web/v1/course/student/freetest";
    private final static String USER_INFO_URL = "http://dledu.aizhixin.com/zhixin_api/api/account/infoplus";
    private final static String STUDENT_COURSE_TEACHER_URL = "http://gateway.aizhixin.com/org-manager/v1/teachingclassstudent/findteacherbystudentandcourses";
    private final static String CUURENT_USER_URL = "http://dledu.aizhixin.com/zhixin_api/api/currentuser";
    private ConcurrentLinkedQueue <SimpleMetrics> cacheMetrics = new ConcurrentLinkedQueue <>();
    private CopyOnWriteArrayList <AuthToken> tokens = new CopyOnWriteArrayList <>();
    private ConcurrentMap <String, UserInfo> userInfoMap = new ConcurrentHashMap <>();

    public void testLogin(BioThreadPool pool, Map <String, String> users) throws InterruptedException {
        for (Map.Entry <String, String> e : users.entrySet()) {
            while (true) {
                if (!pool.addAndExecute(new LoginThread(cacheMetrics, new Login(restUtil, LOGIN_URL, e.getKey(), e.getValue()), tokens))) {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } else {
                    break;
                }
            }
        }
    }

    public void testSign(BioThreadPool pool) throws InterruptedException {
        Random r = new Random();
        for (int i = 0; i < 20000; i++) {
            AuthToken auth = tokens.get(r.nextInt(tokens.size()));
            SignDomain d = new SignDomain(2783, "automatic", "34.22842-108.872819");
            if (!pool.addAndExecute(new SignThread(cacheMetrics, new SignName(restUtil, SIGN_URL, "Bearer " + auth.getAccess_token(), d)))) {
                TimeUnit.MILLISECONDS.sleep(1000);
            }
        }
    }

    public void testNullApi(BioThreadPool pool) throws InterruptedException {
        for (int i = 0; i < 200000; i++) {
            if (!pool.addAndExecute(new NullApi(cacheMetrics, new NullApiTask(restUtil, NULL_URL2)))) {
                TimeUnit.MILLISECONDS.sleep(1000);
            }
        }
    }

//    publicc void testKaijuanFreeCourseApi (BioThreadPool pool) throws InterruptedException {
//        for (int i = 0; i < 1000; i++) {
//            AuthToken auth = tokens.get(i);
//            if (!pool.addAndExecute(new FreeTestCourseApi(cacheMetrics, new KaijuanFreeTestCourse(restUtil, FREE_TEST_COURSE_URL, "Bearer " + auth.getAccess_token())))) {
//                TimeUnit.MILLISECONDS.sleep(1000);
//            }
//        }
//    }
//
//    public void testGetUserInfoApi (BioThreadPool pool) throws InterruptedException {
//        for (int i = 0; i < 1000; i++) {
//            AuthToken auth = tokens.get(i);
//            if (!pool.addAndExecute(new UserInfoApi(cacheMetrics, new UserInfoTask(restUtil, USER_INFO_URL, "Bearer " + auth.getAccess_token()), userInfoMap))) {
//                TimeUnit.MILLISECONDS.sleep(1000);
//            }
//        }
//    }

//    public void testGetOrgUserInfoApi (BioThreadPool pool, List<Long> userIdList) throws InterruptedException {
//        Random r = new java.util.Random();
//        int p = userIdList.size();
////        for (int i = 0; i < 20000; i++) {
////            AuthToken auth = tokens.get(i);
//            for (Long id : userIdList) {
//                if (!pool.addAndExecute(new OrgUserInfoApi(cacheMetrics, new OrgUserInfoTask(restUtil, "http://localhost:8080/v1/user/get", id)))) {
//                    TimeUnit.MILLISECONDS.sleep(1000);
//                }
//            }
////        }
//    }


//    public void testRedisCluster(BioThreadPool pool, List<Long> userIdList, JedisCluster cluster) throws InterruptedException {
//        Random r = new java.util.Random();
//        int p = userIdList.size();
//        Long id = null;
//        for (int i = 0; i < 100000; i++) {
//            id = userIdList.get(r.nextInt(p));
//            if (!pool.addAndExecute(new RedisClusterMetrics(cacheMetrics, new RedisClusterTask(cluster,  id)))) {
//                TimeUnit.MILLISECONDS.sleep(1000);
//            }
//        }
//    }
    public void testRedisSentinel(BioThreadPool pool, List<Long> userIdList, JedisSentinelPool cluster) throws InterruptedException {
        Random r = new java.util.Random();
        int p = userIdList.size();
        Long id = null;
        for (int i = 0; i < 300000; i++) {
            id = userIdList.get(r.nextInt(p));
            if (!pool.addAndExecute(new RedisClusterMetrics(cacheMetrics, new RedisClusterTask(cluster,  id)))) {
                TimeUnit.MILLISECONDS.sleep(2000);
            }
        }
    }
//
//    public void testGetStudentcourseTeacherInfoApi (BioThreadPool pool) throws InterruptedException {
//        for (int i = 0; i < 1000; i++) {
//            AuthToken auth = tokens.get(0);
//            if (!pool.addAndExecute(new StudentCourseTeacher(cacheMetrics, new StudentCourseTeacherApi(restUtil, STUDENT_COURSE_TEACHER_URL)))) {
//                TimeUnit.MILLISECONDS.sleep(1000);
//            }
//        }
//    }
//
//
//
//    public void testGetCurrentApi (BioThreadPool pool) throws InterruptedException {
//        Random r = new Random();
//        for (int i = 0; i < 1000000; i++) {
//            AuthToken auth = tokens.get(r.nextInt(tokens.size()));
//            if (!pool.addAndExecute(new CurrentUserApi(cacheMetrics, new CurrentUserTask(restUtil, CUURENT_USER_URL, "Bearer " + auth.getAccess_token())))) {
//                TimeUnit.MILLISECONDS.sleep(1000);
//            }
//        }
//    }

    public void outMetrics(String dir, String fileName) {
        File file = new File(dir, fileName);
        List<SimpleMetrics> metrics = new ArrayList<>();
        SimpleMetrics m = cacheMetrics.poll();
        while (null != m) {
            m.executeTime();
            metrics.add(m);
            m = cacheMetrics.poll();
        }
        JsonUtil.encode(file, metrics);
    }

    public JedisCluster getRedisCluster() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        // 最大连接数
        poolConfig.setMaxTotal(300);
        // 最大空闲数
        poolConfig.setMaxIdle(10);
        // 最大允许等待时间，如果超过这个时间还未获取到连接，则会报JedisException异常：
        // Could not get a resource from the pool
        poolConfig.setMaxWaitMillis(1000);
        Set<HostAndPort> nodes = new LinkedHashSet<>();
        nodes.add(new HostAndPort("172.16.40.103", 7000));
        nodes.add(new HostAndPort("172.16.40.103", 7001));
        nodes.add(new HostAndPort("172.16.40.103", 7002));
        nodes.add(new HostAndPort("172.16.40.104", 7003));
        nodes.add(new HostAndPort("172.16.40.104", 7004));
        nodes.add(new HostAndPort("172.16.40.104", 7005));
        JedisCluster cluster = new JedisCluster(nodes, poolConfig);
        return cluster;
    }

    public JedisSentinelPool getJedisSentinelPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(300);
        poolConfig.setMaxIdle(50);
        poolConfig.setMaxWaitMillis(1000);
        poolConfig.setTestOnBorrow(true);
        Set<String> sentinels = new HashSet<>();
        sentinels.add("172.16.40.102:6700");
        sentinels.add("172.16.40.102:6701");
        sentinels.add("172.16.40.103:6700");
        return new JedisSentinelPool("mymaster", sentinels, poolConfig);
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        String file = "d:/账号.csv";
        if (args.length > 0) {
            file = args[0];
        }
        TestLogin test = new TestLogin();
        BioThreadPool pool = new BioThreadPool();
//        Map<String, String> users = FileUtil.readUsernameAndPwdFromFile(file);//new HashMap<>();
//        users.put("zxdx1008", "12345678");
//        test.testLogin(pool, users);
//        pool.close();
//
//        pool = new BioThreadPool();
//        pool.close();
//
//        pool = new BioThreadPool();
//        test.testGetUserInfoApi(pool);
//        test.testKaijuanFreeCourseApi(pool);
//        test.testGetStudentcourseTeacherInfoApi(pool);
//        pool = new BioThreadPool();
//
//        test.testSign(pool);
//        test.testNullApi(pool);
//        test.testGetCurrentApi(pool);
        List<Long> userIdList = FileUtil.readUserIdFromFile("d:/id.txt");
//        test.testGetOrgUserInfoApi(pool, userIdList);
//        JedisCluster cluster = test.getRedisCluster();
        JedisSentinelPool sentinel = test.getJedisSentinelPool();
        long start = System.currentTimeMillis();
//        test.testRedisCluster(pool, userIdList, cluster);
        test.testRedisSentinel(pool, userIdList, sentinel);
        pool.close();
//        cluster.close();
        sentinel.close();
        System.out.println("---------------------------------------------" + (System.currentTimeMillis()- start));
        test.outMetrics("d:/", "metrics3.json");
    }
}

class LoginThread extends Thread {
    private Login login;
    private ConcurrentLinkedQueue <SimpleMetrics> cacheMetrics;
    private CopyOnWriteArrayList <AuthToken> users;

    public LoginThread(ConcurrentLinkedQueue <SimpleMetrics> cacheMetrics, Login login, CopyOnWriteArrayList <AuthToken> users) {
        this.login = login;
        this.cacheMetrics = cacheMetrics;
        this.users = users;
    }

    @Override
    public void run() {
        SimpleMetrics m = new SimpleMetrics("登录", System.currentTimeMillis());
        String json = login.login();
        m.setEndtime(System.currentTimeMillis());
        AuthToken token = JsonUtil.decode(json, AuthToken.class);
        cacheMetrics.offer(m);
        users.add(token);
    }
}


class SignThread extends Thread {
    private SignName signName;
    private ConcurrentLinkedQueue <SimpleMetrics> cacheMetrics;

    public SignThread(ConcurrentLinkedQueue <SimpleMetrics> cacheMetrics, SignName signName) {
        this.signName = signName;
        this.cacheMetrics = cacheMetrics;
    }

    @Override
    public void run() {
        SimpleMetrics m = new SimpleMetrics("签到", System.currentTimeMillis());
        signName.sign();
        m.setEndtime(System.currentTimeMillis());
        cacheMetrics.offer(m);
    }
}


class NullApi extends Thread {
    private NullApiTask nullApiTask;
    private ConcurrentLinkedQueue <SimpleMetrics> cacheMetrics;

    public NullApi(ConcurrentLinkedQueue <SimpleMetrics> cacheMetrics, NullApiTask nullApiTask) {
        this.cacheMetrics = cacheMetrics;
        this.nullApiTask = nullApiTask;
    }

    @Override
    public void run() {
        SimpleMetrics m = new SimpleMetrics("空接口", System.currentTimeMillis());
        nullApiTask.restInvoke();
        m.setEndtime(System.currentTimeMillis());
        cacheMetrics.offer(m);
    }
}

class FreeTestCourseApi extends Thread {
    private KaijuanFreeTestCourse kaijuanFreeTestCourse;
    private ConcurrentLinkedQueue <SimpleMetrics> cacheMetrics;

    public FreeTestCourseApi(ConcurrentLinkedQueue <SimpleMetrics> cacheMetrics, KaijuanFreeTestCourse kaijuanFreeTestCourse) {
        this.cacheMetrics = cacheMetrics;
        this.kaijuanFreeTestCourse = kaijuanFreeTestCourse;
    }

    @Override
    public void run() {
        SimpleMetrics m = new SimpleMetrics("开卷自由练习课程", System.currentTimeMillis());
        kaijuanFreeTestCourse.execute();
        m.setEndtime(System.currentTimeMillis());
        cacheMetrics.offer(m);
    }
}

class UserInfoApi extends Thread {
    private UserInfoTask userInfoTask;
    private ConcurrentLinkedQueue <SimpleMetrics> cacheMetrics;
    private ConcurrentMap <String, UserInfo> userInfoMap;

    public UserInfoApi(ConcurrentLinkedQueue <SimpleMetrics> cacheMetrics, UserInfoTask userInfoTask, ConcurrentMap <String, UserInfo> userInfoMap) {
        this.cacheMetrics = cacheMetrics;
        this.userInfoTask = userInfoTask;
        this.userInfoMap = userInfoMap;
    }

    @Override
    public void run() {
        SimpleMetrics m = new SimpleMetrics("获取用户信息", System.currentTimeMillis());
        String json = userInfoTask.execute();
        if (!StringUtils.isEmpty(json)) {
            UserInfo userInfo = JsonUtil.decode(json, UserInfo.class);
            userInfoMap.put(userInfoTask.getAuthorization(), userInfo);
        }
        m.setEndtime(System.currentTimeMillis());
        cacheMetrics.offer(m);
    }
}


class StudentCourseTeacher extends Thread {
    private StudentCourseTeacherApi studentCourseTeacherApi;
    private ConcurrentLinkedQueue <SimpleMetrics> cacheMetrics;

    public StudentCourseTeacher(ConcurrentLinkedQueue <SimpleMetrics> cacheMetrics, StudentCourseTeacherApi studentCourseTeacherApi) {
        this.cacheMetrics = cacheMetrics;
        this.studentCourseTeacherApi = studentCourseTeacherApi;
    }

    @Override
    public void run() {
        SimpleMetrics m = new SimpleMetrics("获取学生课程的老师列表信息", System.currentTimeMillis());
        studentCourseTeacherApi.execute();
        m.setEndtime(System.currentTimeMillis());
        cacheMetrics.offer(m);
    }
}


class CurrentUserApi extends Thread {
    private CurrentUserTask userInfoTask;
    private ConcurrentLinkedQueue <SimpleMetrics> cacheMetrics;

    public CurrentUserApi(ConcurrentLinkedQueue <SimpleMetrics> cacheMetrics, CurrentUserTask userInfoTask) {
        this.cacheMetrics = cacheMetrics;
        this.userInfoTask = userInfoTask;
    }

    @Override
    public void run() {
        SimpleMetrics m = new SimpleMetrics("获取用户信息", System.currentTimeMillis());
        String json = userInfoTask.execute();
        if (!StringUtils.isEmpty(json)) {
            JsonUtil.decode(json, CurrentUserDTO.class);
        }
        m.setEndtime(System.currentTimeMillis());
        cacheMetrics.offer(m);
    }
}


//class OrgUserInfoApi extends Thread {
//    private OrgUserInfoTask userInfoTask;
//    private ConcurrentLinkedQueue<SimpleMetrics> cacheMetrics;
//
//    public OrgUserInfoApi (ConcurrentLinkedQueue<SimpleMetrics> cacheMetrics, OrgUserInfoTask userInfoTask) {
//        this.cacheMetrics = cacheMetrics;
//        this.userInfoTask = userInfoTask;
//    }
//    @Override
//    public void run() {
//        SimpleMetrics m = new SimpleMetrics("获取用户信息", System.currentTimeMillis());
//        userInfoTask.execute();
//        m.setEndtime(System.currentTimeMillis());
//        cacheMetrics.offer(m);
//    }
//}

class OrgUserInfoApi extends Thread {
    private OrgUserInfoTask userInfoTask;
    private ConcurrentLinkedQueue <SimpleMetrics> cacheMetrics;

    public OrgUserInfoApi(ConcurrentLinkedQueue <SimpleMetrics> cacheMetrics, OrgUserInfoTask userInfoTask) {
        this.cacheMetrics = cacheMetrics;
        this.userInfoTask = userInfoTask;
    }

    @Override
    public void run() {
        SimpleMetrics m = new SimpleMetrics("获取用户信息", System.currentTimeMillis());
        userInfoTask.execute();
        m.setEndtime(System.currentTimeMillis());
        cacheMetrics.offer(m);
    }
}


class RedisClusterMetrics extends Thread {
    private RedisClusterTask redisClusterTask;
    private ConcurrentLinkedQueue <SimpleMetrics> cacheMetrics;

    public RedisClusterMetrics(ConcurrentLinkedQueue <SimpleMetrics> cacheMetrics, RedisClusterTask redisClusterTask) {
        this.cacheMetrics = cacheMetrics;
        this.redisClusterTask = redisClusterTask;
    }

    @Override
    public void run() {
        SimpleMetrics m = new SimpleMetrics("获取用户信息", System.currentTimeMillis());
        redisClusterTask.execute();
        m.setEndtime(System.currentTimeMillis());
        cacheMetrics.offer(m);
    }
}