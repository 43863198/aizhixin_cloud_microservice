# org
platform org manager
组织机构

## 编译运行需求：
> oracle sdk 1.8xx及以上，tomcat8.0xx及以上，maven 3.2xx及以上，mysql5.6及以上

---

1\. create database and create user

```
CREATE DATABASE IF NOT EXISTS org_api DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
create user  'org_api'   IDENTIFIED BY 'dinglicom';
grant ALL privileges on  org_api.* to 'org_api';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'192.168.1.100' IDENTIFIED BY '' WITH GRANT OPTION;
flush  privileges;
```

2\. 运行应用程序时需要在添加program arguments运行参数(为区分生产和本地开发环境)

```
--spring.profiles.active=local
-Dspring.profiles.active=local(JUnit test VM arguments:)
```

3\. 打war包时可以忽略测试

```
JVM arguments: -Dmaven.test.skip=true
```

4\. mysqldump 数据库备份：

```
mysqldump.exe --default-character-set=utf8 -u em_api2  -p org_api >> d:/org.sql
```

5\. ERMaster 插件地址：

```
eclipse下ER图查询编辑插件ERMaster <http://sourceforge.net/projects/ermaster>
```

6\. eclipse lombok 需要运行相应的jar文件，对eclipse处理以后可以不需要书写getter和setter方法

```
java -jar lombok-xxx.jar
```

7\. 根据访问日志统计每分钟访问次数

    awk -F: '{count[$2":"substr($3,1,2)]++} END {for(minute in count) print minute, count[minute]} /dinglicom/gateway_access_log/org_api_access_log.2017-09-16.log | sort > /tmp/count2.log
    
8\. 统计请求时间大于500毫秒的日志记录

    awk '($NF > 500){print $0}' /dinglicom/gateway_access_log/org_api_access_log.2017-09-16.log

