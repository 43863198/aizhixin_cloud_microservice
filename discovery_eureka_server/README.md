# aizhixin cloud Discovery Server

--
#### 系统环境要求：
    spring cloud 采用Camden版本
    spring boot 采用1.4.5版本
    Java SDK java8及以上
    Discovery Server待Netflix的Eureka
#### 部署方式：
    嵌入tomcat的方式
#### 编译、打包及运行
    mvn compile
    mvn package
    java -jar discovery_eureka_server-0.0.1-SNAPSHOT.jar --spring.profiles.active=xa08 --server.port=1111
#### 配置文件含义
    eureka:
      server:
        renewal-percent-threshold: 0.8
        eviction-interval-timer-in-ms: 120000
      instance:
        prefer-ip-address: true
        instance-id: ${spring.cloud.client.ipAddress}:${spring.application.name}:${server.port}
      client:
        service-url:
          defaultZone: http://172.16.23.108:${server.port}/eureka/
          
    Eureka缺省采用高可用，需要配置两个节点，其中defaultZone的值，表示另一个Eureka的访问地址
    
#### 服务的注册端（客户端）的配置
    上一段的配置是服务中心的配置，服务中心配置完成并启动以后，客户端需要注册到服务中心才能被其它服务发现或者调用其它服务。
    客户端需要做一点简单的配置来将自己注册到服务中心，因为所以客户端的这部分的配置完全一样，这儿就统一说明一下：
    其中defaultZone部分的值表示多个高可用的服务中心的地址
    eureka:
      instance:
        prefer-ip-address: true
        instance-id: ${spring.cloud.client.ipAddress}:${spring.application.name}:${server.port}
      client:
        service-url:
          defaultZone: http://172.16.23.107:1111/eureka/,http://172.16.23.108:1111/eureka/