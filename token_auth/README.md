# token_auth
platform token auth
token认证管理服务

## 编译运行需求：
> oracle sdk 1.8xx及以上，tomcat8.0xx及以上，maven 3.2xx及以上，mysql5.6及以上

---

1\. 运行应用程序时需要在添加program arguments运行参数(为区分生产和本地开发环境)

```
--spring.profiles.active=local
-Dspring.profiles.active=local(JUnit test VM arguments:)
```
